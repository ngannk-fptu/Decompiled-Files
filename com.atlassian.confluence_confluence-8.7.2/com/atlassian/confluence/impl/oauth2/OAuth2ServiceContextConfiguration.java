/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.lib.flow.FlowRequestService
 *  com.atlassian.oauth2.client.api.storage.config.ClientConfigStorageService
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenStorageService
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.impl.oauth2;

import com.atlassian.confluence.impl.oauth2.DefaultOAuth2Service;
import com.atlassian.confluence.impl.osgi.OsgiServiceRegistry;
import com.atlassian.confluence.oauth2.OAuth2Service;
import com.atlassian.oauth2.client.api.lib.flow.FlowRequestService;
import com.atlassian.oauth2.client.api.storage.config.ClientConfigStorageService;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenStorageService;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuth2ServiceContextConfiguration {
    @Resource
    private OsgiServiceRegistry osgiServiceRegistry;

    @Bean(value={"oAuth2Service"})
    public OAuth2Service oAuth2Service() {
        return new DefaultOAuth2Service(this.osgiServiceRegistry.getService(ClientConfigStorageService.class), this.osgiServiceRegistry.getService(ClientTokenStorageService.class), this.osgiServiceRegistry.getService(FlowRequestService.class));
    }
}

