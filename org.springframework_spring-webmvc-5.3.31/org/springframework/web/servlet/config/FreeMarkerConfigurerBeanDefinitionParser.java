/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.StringUtils
 *  org.springframework.util.xml.DomUtils
 */
package org.springframework.web.servlet.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class FreeMarkerConfigurerBeanDefinitionParser
extends AbstractSingleBeanDefinitionParser {
    public static final String BEAN_NAME = "mvcFreeMarkerConfigurer";

    protected String getBeanClassName(Element element) {
        return "org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer";
    }

    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
        return BEAN_NAME;
    }

    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        List childElements = DomUtils.getChildElementsByTagName((Element)element, (String)"template-loader-path");
        if (!childElements.isEmpty()) {
            ArrayList<String> locations = new ArrayList<String>(childElements.size());
            for (Element childElement : childElements) {
                locations.add(childElement.getAttribute("location"));
            }
            builder.addPropertyValue("templateLoaderPaths", (Object)StringUtils.toStringArray(locations));
        }
    }
}

