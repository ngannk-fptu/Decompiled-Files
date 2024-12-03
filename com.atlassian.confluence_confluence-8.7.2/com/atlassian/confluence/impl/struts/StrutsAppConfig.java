/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.config.ConfigurationManager
 *  com.opensymphony.xwork2.config.ContainerProvider
 *  javax.annotation.Nonnull
 *  javax.annotation.Resource
 *  javax.servlet.ServletContext
 *  org.apache.struts2.dispatcher.Dispatcher
 *  org.apache.struts2.views.velocity.VelocityManager
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Lazy
 *  org.springframework.web.context.ServletContextAware
 */
package com.atlassian.confluence.impl.struts;

import com.atlassian.confluence.impl.struts.ConfluenceStrutsConfigurationProvider;
import com.atlassian.confluence.impl.struts.ConfluenceStrutsDispatcher;
import com.atlassian.confluence.setup.BootstrapManager;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.ContainerProvider;
import java.util.Collections;
import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.views.velocity.VelocityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.context.ServletContextAware;

@Configuration
public class StrutsAppConfig
implements ServletContextAware {
    private ServletContext servletContext;
    @Resource
    private BootstrapManager bootstrapManager;

    @Bean
    Dispatcher strutsDispatcher() {
        ConfluenceStrutsDispatcher dispatcher = new ConfluenceStrutsDispatcher(this.servletContext, Collections.emptyMap());
        dispatcher.init();
        dispatcher.getConfigurationManager().addContainerProvider((ContainerProvider)new ConfluenceStrutsConfigurationProvider(this.bootstrapManager));
        return dispatcher;
    }

    @Bean
    @Lazy
    VelocityManager velocityManager() {
        VelocityManager velocityManager = (VelocityManager)this.strutsDispatcher().getContainer().getInstance(VelocityManager.class);
        velocityManager.init(this.servletContext);
        return velocityManager;
    }

    @Bean
    ConfigurationManager strutsConfigurationManager() {
        return this.strutsDispatcher().getConfigurationManager();
    }

    public void setServletContext(@Nonnull ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}

