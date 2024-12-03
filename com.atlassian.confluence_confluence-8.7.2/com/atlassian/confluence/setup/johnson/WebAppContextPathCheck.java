/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.johnson.JohnsonEventContainer
 *  com.atlassian.johnson.event.RequestEventCheck
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.johnson;

import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.impl.setup.BootstrapConfigurer;
import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.johnson.event.RequestEventCheck;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebAppContextPathCheck
implements RequestEventCheck {
    private static final Logger log = LoggerFactory.getLogger(WebAppContextPathCheck.class);

    public void check(JohnsonEventContainer johnsonEventContainer, HttpServletRequest request) {
        BootstrapConfigurer bootstrap = BootstrapConfigurer.getBootstrapConfigurer();
        if (bootstrap == null || !bootstrap.isBootstrapped()) {
            log.error("Bootstrap did not initialize.");
            return;
        }
        if (!bootstrap.isWebAppContextPathSet() && request.getContextPath() != null || bootstrap.isWebAppContextPathSet() && !bootstrap.getWebAppContextPath().equals(request.getContextPath())) {
            try {
                bootstrap.setWebAppContextPath(request.getContextPath());
            }
            catch (ConfigurationException e) {
                log.error("", (Throwable)e);
            }
        }
    }
}

