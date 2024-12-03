/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.PropertiesFactoryBean
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.repository.config;

import javax.annotation.Nonnull;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.repository.core.support.PropertiesBasedNamedQueries;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class NamedQueriesBeanDefinitionParser
implements BeanDefinitionParser {
    private static final String ATTRIBUTE = "named-queries-location";
    private final String defaultLocation;

    public NamedQueriesBeanDefinitionParser(String defaultLocation) {
        Assert.hasText((String)defaultLocation, (String)"DefaultLocation must not be null nor empty!");
        this.defaultLocation = defaultLocation;
    }

    @Nonnull
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder properties = BeanDefinitionBuilder.rootBeanDefinition(PropertiesFactoryBean.class);
        properties.addPropertyValue("locations", (Object)this.getDefaultedLocation(element));
        if (this.isDefaultLocation(element)) {
            properties.addPropertyValue("ignoreResourceNotFound", (Object)true);
        }
        AbstractBeanDefinition propertiesDefinition = properties.getBeanDefinition();
        propertiesDefinition.setSource(parserContext.extractSource((Object)element));
        BeanDefinitionBuilder namedQueries = BeanDefinitionBuilder.rootBeanDefinition(PropertiesBasedNamedQueries.class);
        namedQueries.addConstructorArgValue((Object)propertiesDefinition);
        AbstractBeanDefinition namedQueriesDefinition = namedQueries.getBeanDefinition();
        namedQueriesDefinition.setSource(parserContext.extractSource((Object)element));
        return namedQueriesDefinition;
    }

    private boolean isDefaultLocation(Element element) {
        return !StringUtils.hasText((String)element.getAttribute(ATTRIBUTE));
    }

    private String getDefaultedLocation(Element element) {
        String locations = element.getAttribute(ATTRIBUTE);
        return StringUtils.hasText((String)locations) ? locations : this.defaultLocation;
    }
}

