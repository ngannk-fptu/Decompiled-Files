/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.filter.authentication.confluence;

import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugins.authentication.impl.web.filter.authentication.confluence.ConfluenceActionResolver;
import com.atlassian.plugins.authentication.impl.web.filter.authentication.confluence.StaticConfluenceActionResolver;
import com.atlassian.plugins.authentication.impl.web.filter.authentication.confluence.Struts2ActionResolver;
import com.atlassian.plugins.authentication.impl.web.filter.authentication.confluence.WebworkActionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ConfluenceComponent
public class ConfluenceActionResolverFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfluenceActionResolverFactory.class);

    public ConfluenceActionResolver createActionResolver() {
        if (this.isStruts2AvailableOnTheClasspath()) {
            return new Struts2ActionResolver();
        }
        LOGGER.debug("Struts 2 is not available on the classpath. Assuming Webwork is used");
        return new WebworkActionResolver();
    }

    public ConfluenceActionResolver createStaticActionResolver(String loginUrl, String signUpUrl) {
        return new StaticConfluenceActionResolver(loginUrl, signUpUrl);
    }

    private boolean isStruts2AvailableOnTheClasspath() {
        return Struts2ActionResolver.REQUIRED_STRUTS2_CLASSES.stream().allMatch(requiredClassName -> {
            try {
                Class.forName(requiredClassName);
                return true;
            }
            catch (ClassNotFoundException e) {
                LOGGER.debug("'{}' is not available on the classpath", requiredClassName);
                return false;
            }
            catch (Exception e) {
                LOGGER.error("Failed to load '{}'", requiredClassName, (Object)e);
                return false;
            }
        });
    }
}

