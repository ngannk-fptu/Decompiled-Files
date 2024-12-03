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
package com.atlassian.confluence.impl.message;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.util.message.BandanaMessageManager;
import com.atlassian.confluence.util.message.DefaultMessageManager;
import com.atlassian.confluence.util.message.RequestMessageManager;
import com.atlassian.confluence.util.message.SessionMessageManager;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.plugin.spring.AvailableToPlugins;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageManagerContextConfig {
    @Resource
    BandanaManager bandanaManager;
    @Resource
    HttpContext httpContext;

    @Bean
    @AvailableToPlugins
    BandanaMessageManager bandanaMessageManager() {
        BandanaMessageManager bandanaMessageManager = new BandanaMessageManager();
        bandanaMessageManager.setBandanaManager(this.bandanaManager);
        return bandanaMessageManager;
    }

    @Bean
    SessionMessageManager sessionMessageManager() {
        return new SessionMessageManager(this.httpContext);
    }

    @Bean
    RequestMessageManager requestMessageManager() {
        return new RequestMessageManager(this.httpContext);
    }

    @Bean
    DefaultMessageManager messageManager() {
        return new DefaultMessageManager(List.of(this.bandanaMessageManager(), this.sessionMessageManager(), this.requestMessageManager()));
    }
}

