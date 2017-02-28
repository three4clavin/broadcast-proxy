package com.three4clavin.proxy.broadcast.resources.proxy;

import com.three4clavin.proxy.broadcast.configuration.BroadcastFailureMode;
import com.three4clavin.proxy.broadcast.configuration.BroadcastProxyConfiguration;
import com.three4clavin.proxy.broadcast.exception.BadRequestException;
import com.three4clavin.proxy.broadcast.exception.ProxyException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/broadcast")
public class BroadcastProxyController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BroadcastProxyController.class);

    private final BroadcastProxyConfiguration configuration;

    private final CloseableHttpClient client = HttpClients.createDefault();

    @Inject
    public BroadcastProxyController(BroadcastProxyConfiguration configuration) {
        this.configuration = configuration;
    }

    @RequestMapping(method=RequestMethod.POST)
    public @ResponseBody String doBroadcastPost(@RequestBody String body) {
        StringEntity entity;
        try {
            entity = new StringEntity(body);
        } catch (UnsupportedEncodingException uee) {
            throw new BadRequestException("Request body has unsupported encoding");
        }

        List<String> statusFailures = new ArrayList<String>();
        for (String endUrl : configuration.getBroadcastUrls()) {
            HttpPost httpPost = new HttpPost(endUrl);
            httpPost.setEntity(entity);
            CloseableHttpResponse response = null;
            try {
                response = client.execute(httpPost);
                int status = response.getStatusLine().getStatusCode();
                if ((status >= 200) && (status < 300)) {
                    LOGGER.debug("Successful broadcast to {}", endUrl);
                } else {
                    statusFailures.add("Bad response from broadcast endpoint " + endUrl + ".  Code " + status);
                }
            } catch (IOException ioe) {
                statusFailures.add("Exception trying to contact broadcast endpoint " + ioe);
            }
        }

        if (statusFailures.size() > 0) {
            if (configuration.getFailureMode().equals(BroadcastFailureMode.IGNORE)) {
                return "OK (" + statusFailures.size() + " broadcast failures ignored)";
            }

            for (String message : statusFailures) {
                LOGGER.warn(message);
            }

            if (configuration.getFailureMode().equals(BroadcastFailureMode.LOG)) {
                return "OK (" + statusFailures.size() + " broadcast failures ignored)";
            }

            if (configuration.getFailureMode().equals(BroadcastFailureMode.FAIL_ONE) ||
                    (statusFailures.size() >= configuration.getBroadcastUrls().length)) {
                throw new ProxyException("Broadcast failure: " + statusFailures.size() + " of " + configuration.getBroadcastUrls().length + " endpoints were unsuccessful.");
            }
        }

        return "OK";
    }
}
