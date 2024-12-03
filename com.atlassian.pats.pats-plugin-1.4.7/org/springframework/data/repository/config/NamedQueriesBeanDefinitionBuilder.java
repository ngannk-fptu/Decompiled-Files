/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.PropertiesFactoryBean
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.repository.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.data.repository.core.support.PropertiesBasedNamedQueries;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class NamedQueriesBeanDefinitionBuilder {
    private final String defaultLocation;
    private String locations;

    public NamedQueriesBeanDefinitionBuilder(String defaultLocation) {
        Assert.hasText((String)defaultLocation, (String)"DefaultLocation must not be null nor empty!");
        this.defaultLocation = defaultLocation;
    }

    public void setLocations(String locations) {
        Assert.hasText((String)locations, (String)"Locations must not be null nor empty!");
        this.locations = locations;
    }

    public BeanDefinition build(@Nullable Object source) {
        BeanDefinitionBuilder properties = BeanDefinitionBuilder.rootBeanDefinition(PropertiesFactoryBean.class);
        String locationsToUse = StringUtils.hasText((String)this.locations) ? this.locations : this.defaultLocation;
        properties.addPropertyValue("locations", (Object)locationsToUse);
        if (!StringUtils.hasText((String)this.locations)) {
            properties.addPropertyValue("ignoreResourceNotFound", (Object)true);
        }
        AbstractBeanDefinition propertiesDefinition = properties.getBeanDefinition();
        propertiesDefinition.setSource(source);
        BeanDefinitionBuilder namedQueries = BeanDefinitionBuilder.rootBeanDefinition(PropertiesBasedNamedQueries.class);
        namedQueries.addConstructorArgValue((Object)propertiesDefinition);
        AbstractBeanDefinition namedQueriesDefinition = namedQueries.getBeanDefinition();
        namedQueriesDefinition.setSource(source);
        return namedQueriesDefinition;
    }
}

