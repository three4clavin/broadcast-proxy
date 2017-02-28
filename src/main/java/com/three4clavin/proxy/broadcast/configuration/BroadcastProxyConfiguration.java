package com.three4clavin.proxy.broadcast.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BroadcastProxyConfiguration {
    @Value("${failure_mode}")
    private BroadcastFailureMode failureMode;

    @Value("${broadcast_urls}")
    private String[] broadcastUrls;

    public String[] getBroadcastUrls() {
        return broadcastUrls;
    }
    public BroadcastFailureMode getFailureMode() {
        return failureMode;
    }
}
