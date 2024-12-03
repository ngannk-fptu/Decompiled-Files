/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.core.io.Resource
 *  org.springframework.util.Assert
 */
package org.apache.commons.configuration2.spring;

import java.util.Properties;
import java.util.stream.Stream;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationConverter;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

public class ConfigurationPropertiesFactoryBean
implements InitializingBean,
FactoryBean<Properties> {
    private CompositeConfiguration compositeConfiguration;
    private Configuration[] configurations;
    private Resource[] locations;
    private boolean throwExceptionOnMissing = true;

    public ConfigurationPropertiesFactoryBean() {
    }

    public ConfigurationPropertiesFactoryBean(Configuration configuration) {
        Assert.notNull((Object)configuration, (String)"configuration");
        this.compositeConfiguration = new CompositeConfiguration(configuration);
    }

    public Properties getObject() throws Exception {
        return this.compositeConfiguration != null ? ConfigurationConverter.getProperties(this.compositeConfiguration) : null;
    }

    public Class<?> getObjectType() {
        return Properties.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void afterPropertiesSet() throws Exception {
        if (this.compositeConfiguration == null && ArrayUtils.isEmpty((Object[])this.configurations) && ArrayUtils.isEmpty((Object[])this.locations)) {
            throw new IllegalArgumentException("no configuration object or location specified");
        }
        if (this.compositeConfiguration == null) {
            this.compositeConfiguration = new CompositeConfiguration();
        }
        this.compositeConfiguration.setThrowExceptionOnMissing(this.throwExceptionOnMissing);
        if (this.configurations != null) {
            Stream.of(this.configurations).forEach(this.compositeConfiguration::addConfiguration);
        }
        if (this.locations != null) {
            for (Resource location : this.locations) {
                this.compositeConfiguration.addConfiguration(new Configurations().properties(location.getURL()));
            }
        }
    }

    public Configuration[] getConfigurations() {
        return ConfigurationPropertiesFactoryBean.defensiveCopy(this.configurations);
    }

    public void setConfigurations(Configuration ... configurations) {
        this.configurations = ConfigurationPropertiesFactoryBean.defensiveCopy(configurations);
    }

    public Resource[] getLocations() {
        return ConfigurationPropertiesFactoryBean.defensiveCopy(this.locations);
    }

    public void setLocations(Resource ... locations) {
        this.locations = ConfigurationPropertiesFactoryBean.defensiveCopy(locations);
    }

    public boolean isThrowExceptionOnMissing() {
        return this.throwExceptionOnMissing;
    }

    public void setThrowExceptionOnMissing(boolean throwExceptionOnMissing) {
        this.throwExceptionOnMissing = throwExceptionOnMissing;
    }

    public CompositeConfiguration getConfiguration() {
        return this.compositeConfiguration;
    }

    private static <T> T[] defensiveCopy(T[] src) {
        return src != null ? (Object[])src.clone() : null;
    }
}

