/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.util.Assert
 */
package org.springframework.web.client.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

public class RestGatewaySupport {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private RestTemplate restTemplate;

    public RestGatewaySupport() {
        this.restTemplate = new RestTemplate();
    }

    public RestGatewaySupport(ClientHttpRequestFactory requestFactory) {
        Assert.notNull((Object)requestFactory, (String)"'requestFactory' must not be null");
        this.restTemplate = new RestTemplate(requestFactory);
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        Assert.notNull((Object)restTemplate, (String)"'restTemplate' must not be null");
        this.restTemplate = restTemplate;
    }

    public RestTemplate getRestTemplate() {
        return this.restTemplate;
    }
}

