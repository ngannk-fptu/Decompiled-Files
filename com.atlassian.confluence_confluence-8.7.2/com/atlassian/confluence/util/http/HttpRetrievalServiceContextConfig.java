/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.util.http;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.http.HttpRetrievalService;
import com.atlassian.confluence.util.http.httpclient.HttpClientHttpRetrievalService;
import com.atlassian.plugin.spring.AvailableToPlugins;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Deprecated(forRemoval=true)
public class HttpRetrievalServiceContextConfig {
    @Resource
    private BandanaManager bandanaManager;
    @Resource
    private SettingsManager settingsManager;

    @Bean
    @AvailableToPlugins
    HttpRetrievalService httpRetrievalService() {
        HttpClientHttpRetrievalService bean = new HttpClientHttpRetrievalService();
        bean.setSettingsManager(this.settingsManager);
        bean.setBandanaManager(this.bandanaManager);
        return bean;
    }
}

