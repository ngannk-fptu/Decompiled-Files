/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.NotSupportedException
 *  javax.resource.ResourceException
 *  javax.resource.spi.ActivationSpec
 *  javax.resource.spi.BootstrapContext
 *  javax.resource.spi.ResourceAdapter
 *  javax.resource.spi.ResourceAdapterInternalException
 *  javax.resource.spi.endpoint.MessageEndpointFactory
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.xml.XmlBeanDefinitionReader
 *  org.springframework.context.ConfigurableApplicationContext
 *  org.springframework.core.env.ConfigurableEnvironment
 *  org.springframework.core.env.StandardEnvironment
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.jca.context;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.jca.context.ResourceAdapterApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class SpringContextResourceAdapter
implements ResourceAdapter {
    public static final String CONFIG_LOCATION_DELIMITERS = ",; \t\n";
    public static final String DEFAULT_CONTEXT_CONFIG_LOCATION = "META-INF/applicationContext.xml";
    protected final Log logger = LogFactory.getLog(this.getClass());
    private String contextConfigLocation = "META-INF/applicationContext.xml";
    @Nullable
    private ConfigurableApplicationContext applicationContext;

    public void setContextConfigLocation(String contextConfigLocation) {
        this.contextConfigLocation = contextConfigLocation;
    }

    protected String getContextConfigLocation() {
        return this.contextConfigLocation;
    }

    protected ConfigurableEnvironment createEnvironment() {
        return new StandardEnvironment();
    }

    public void start(BootstrapContext bootstrapContext) throws ResourceAdapterInternalException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Starting SpringContextResourceAdapter with BootstrapContext: " + bootstrapContext));
        }
        this.applicationContext = this.createApplicationContext(bootstrapContext);
    }

    protected ConfigurableApplicationContext createApplicationContext(BootstrapContext bootstrapContext) {
        ResourceAdapterApplicationContext applicationContext = new ResourceAdapterApplicationContext(bootstrapContext);
        applicationContext.setClassLoader(this.getClass().getClassLoader());
        String[] configLocations = StringUtils.tokenizeToStringArray((String)this.getContextConfigLocation(), (String)CONFIG_LOCATION_DELIMITERS);
        this.loadBeanDefinitions((BeanDefinitionRegistry)applicationContext, configLocations);
        applicationContext.refresh();
        return applicationContext;
    }

    protected void loadBeanDefinitions(BeanDefinitionRegistry registry, String[] configLocations) {
        new XmlBeanDefinitionReader(registry).loadBeanDefinitions(configLocations);
    }

    public void stop() {
        this.logger.debug((Object)"Stopping SpringContextResourceAdapter");
        if (this.applicationContext != null) {
            this.applicationContext.close();
        }
    }

    public void endpointActivation(MessageEndpointFactory messageEndpointFactory, ActivationSpec activationSpec) throws ResourceException {
        throw new NotSupportedException("SpringContextResourceAdapter does not support message endpoints");
    }

    public void endpointDeactivation(MessageEndpointFactory messageEndpointFactory, ActivationSpec activationSpec) {
    }

    @Nullable
    public XAResource[] getXAResources(ActivationSpec[] activationSpecs) throws ResourceException {
        return null;
    }

    public boolean equals(@Nullable Object other) {
        return this == other || other instanceof SpringContextResourceAdapter && ObjectUtils.nullSafeEquals((Object)this.getContextConfigLocation(), (Object)((SpringContextResourceAdapter)other).getContextConfigLocation());
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode((Object)this.getContextConfigLocation());
    }
}

