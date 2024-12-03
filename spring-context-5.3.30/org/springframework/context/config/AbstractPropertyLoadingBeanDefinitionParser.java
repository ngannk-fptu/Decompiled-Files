/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.StringUtils
 */
package org.springframework.context.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

abstract class AbstractPropertyLoadingBeanDefinitionParser
extends AbstractSingleBeanDefinitionParser {
    AbstractPropertyLoadingBeanDefinitionParser() {
    }

    protected boolean shouldGenerateId() {
        return true;
    }

    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String order;
        String fileEncoding;
        String propertiesRef;
        String location = element.getAttribute("location");
        if (StringUtils.hasLength((String)location)) {
            location = parserContext.getReaderContext().getEnvironment().resolvePlaceholders(location);
            String[] locations = StringUtils.commaDelimitedListToStringArray((String)location);
            builder.addPropertyValue("locations", (Object)locations);
        }
        if (StringUtils.hasLength((String)(propertiesRef = element.getAttribute("properties-ref")))) {
            builder.addPropertyReference("properties", propertiesRef);
        }
        if (StringUtils.hasLength((String)(fileEncoding = element.getAttribute("file-encoding")))) {
            builder.addPropertyValue("fileEncoding", (Object)fileEncoding);
        }
        if (StringUtils.hasLength((String)(order = element.getAttribute("order")))) {
            builder.addPropertyValue("order", (Object)Integer.valueOf(order));
        }
        builder.addPropertyValue("ignoreResourceNotFound", (Object)Boolean.valueOf(element.getAttribute("ignore-resource-not-found")));
        builder.addPropertyValue("localOverride", (Object)Boolean.valueOf(element.getAttribute("local-override")));
        builder.setRole(2);
    }
}

