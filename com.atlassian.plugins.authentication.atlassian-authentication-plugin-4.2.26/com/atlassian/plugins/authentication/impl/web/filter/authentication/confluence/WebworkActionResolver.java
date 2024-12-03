/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.webwork.dispatcher.ServletDispatcher
 *  com.opensymphony.xwork.config.ConfigurationManager
 *  com.opensymphony.xwork.config.RuntimeConfiguration
 *  com.opensymphony.xwork.config.entities.ActionConfig
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.filter.authentication.confluence;

import com.atlassian.plugins.authentication.impl.web.filter.authentication.confluence.ConfluenceActionResolver;
import com.opensymphony.webwork.dispatcher.ServletDispatcher;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.RuntimeConfiguration;
import com.opensymphony.xwork.config.entities.ActionConfig;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebworkActionResolver
implements ConfluenceActionResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebworkActionResolver.class);
    private static final StatelessWebworkServletDispatcher STATELESS_SERVLET_DISPATCHER = new StatelessWebworkServletDispatcher();

    @Override
    public Optional<String> getActionConfigClassName(HttpServletRequest httpRequest) {
        String nameSpace = STATELESS_SERVLET_DISPATCHER.getNameSpace(httpRequest);
        String actionName = STATELESS_SERVLET_DISPATCHER.getActionName(httpRequest);
        return this.loadRuntimeConfiguration().map(config -> config.getActionConfig(nameSpace, actionName)).map(ActionConfig::getClassName);
    }

    private Optional<RuntimeConfiguration> loadRuntimeConfiguration() {
        try {
            return Optional.ofNullable(ConfigurationManager.getConfiguration().getRuntimeConfiguration());
        }
        catch (Exception e) {
            LOGGER.debug("Failed to load Webwork Action Configuration", (Throwable)e);
            return Optional.empty();
        }
    }

    private static class StatelessWebworkServletDispatcher
    extends ServletDispatcher {
        private StatelessWebworkServletDispatcher() {
        }

        public String getActionName(HttpServletRequest request) {
            return super.getActionName(request.getRequestURI());
        }

        public String getNameSpace(HttpServletRequest request) {
            return super.getNameSpace(request);
        }
    }
}

