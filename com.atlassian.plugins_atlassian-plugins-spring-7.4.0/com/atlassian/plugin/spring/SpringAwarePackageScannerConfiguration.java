/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.container.impl.DefaultPackageScannerConfiguration
 *  javax.annotation.Nonnull
 *  javax.servlet.ServletContext
 *  org.springframework.web.context.ServletContextAware
 */
package com.atlassian.plugin.spring;

import com.atlassian.plugin.osgi.container.impl.DefaultPackageScannerConfiguration;
import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import org.springframework.web.context.ServletContextAware;

public class SpringAwarePackageScannerConfiguration
extends DefaultPackageScannerConfiguration
implements ServletContextAware {
    public SpringAwarePackageScannerConfiguration() {
    }

    public SpringAwarePackageScannerConfiguration(String hostVersion) {
        super(hostVersion);
    }

    public void setServletContext(@Nonnull ServletContext servletContext) {
        super.setServletContext(servletContext);
    }
}

