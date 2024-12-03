/*
 * Decompiled with CFR 0.152.
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

    @Override
    protected boolean shouldGenerateId() {
        return true;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String order;
        String fileEncoding;
        String propertiesRef;
        String location = element.getAttribute("location");
        if (StringUtils.hasLength(location)) {
            location = parserContext.getReaderContext().getEnvironment().resolvePlaceholders(location);
            String[] locations = StringUtils.commaDelimitedListToStringArray(location);
            builder.addPropertyValue("locations", locations);
        }
        if (StringUtils.hasLength(propertiesRef = element.getAttribute("properties-ref"))) {
            builder.addPropertyReference("properties", propertiesRef);
        }
        if (StringUtils.hasLength(fileEncoding = element.getAttribute("file-encoding"))) {
            builder.addPropertyValue("fileEncoding", fileEncoding);
        }
        if (StringUtils.hasLength(order = element.getAttribute("order"))) {
            builder.addPropertyValue("order", Integer.valueOf(order));
        }
        builder.addPropertyValue("ignoreResourceNotFound", Boolean.valueOf(element.getAttribute("ignore-resource-not-found")));
        builder.addPropertyValue("localOverride", Boolean.valueOf(element.getAttribute("local-override")));
        builder.setRole(2);
    }
}

