/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  org.springframework.core.env.MutablePropertySources
 *  org.springframework.core.env.PropertySource
 *  org.springframework.core.env.PropertySource$StubPropertySource
 *  org.springframework.core.env.StandardEnvironment
 *  org.springframework.jndi.JndiLocatorDelegate
 *  org.springframework.jndi.JndiPropertySource
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 */
package org.springframework.web.context.support;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.jndi.JndiPropertySource;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ConfigurableWebEnvironment;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class StandardServletEnvironment
extends StandardEnvironment
implements ConfigurableWebEnvironment {
    public static final String SERVLET_CONTEXT_PROPERTY_SOURCE_NAME = "servletContextInitParams";
    public static final String SERVLET_CONFIG_PROPERTY_SOURCE_NAME = "servletConfigInitParams";
    public static final String JNDI_PROPERTY_SOURCE_NAME = "jndiProperties";
    private static final boolean jndiPresent = ClassUtils.isPresent((String)"javax.naming.InitialContext", (ClassLoader)StandardServletEnvironment.class.getClassLoader());

    public StandardServletEnvironment() {
    }

    protected StandardServletEnvironment(MutablePropertySources propertySources) {
        super(propertySources);
    }

    protected void customizePropertySources(MutablePropertySources propertySources) {
        propertySources.addLast((PropertySource)new PropertySource.StubPropertySource(SERVLET_CONFIG_PROPERTY_SOURCE_NAME));
        propertySources.addLast((PropertySource)new PropertySource.StubPropertySource(SERVLET_CONTEXT_PROPERTY_SOURCE_NAME));
        if (jndiPresent && JndiLocatorDelegate.isDefaultJndiEnvironmentAvailable()) {
            propertySources.addLast((PropertySource)new JndiPropertySource(JNDI_PROPERTY_SOURCE_NAME));
        }
        super.customizePropertySources(propertySources);
    }

    @Override
    public void initPropertySources(@Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig) {
        WebApplicationContextUtils.initServletPropertySources(this.getPropertySources(), servletContext, servletConfig);
    }
}

