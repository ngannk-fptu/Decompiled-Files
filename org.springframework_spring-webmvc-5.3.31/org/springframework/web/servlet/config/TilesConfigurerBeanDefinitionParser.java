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

public class TilesConfigurerBeanDefinitionParser
extends AbstractSingleBeanDefinitionParser {
    public static final String BEAN_NAME = "mvcTilesConfigurer";

    protected String getBeanClassName(Element element) {
        return "org.springframework.web.servlet.view.tiles3.TilesConfigurer";
    }

    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
        return BEAN_NAME;
    }

    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        List childElements = DomUtils.getChildElementsByTagName((Element)element, (String)"definitions");
        if (!childElements.isEmpty()) {
            ArrayList<String> locations = new ArrayList<String>(childElements.size());
            for (Element childElement : childElements) {
                locations.add(childElement.getAttribute("location"));
            }
            builder.addPropertyValue("definitions", (Object)StringUtils.toStringArray(locations));
        }
        if (element.hasAttribute("check-refresh")) {
            builder.addPropertyValue("checkRefresh", (Object)element.getAttribute("check-refresh"));
        }
        if (element.hasAttribute("validate-definitions")) {
            builder.addPropertyValue("validateDefinitions", (Object)element.getAttribute("validate-definitions"));
        }
        if (element.hasAttribute("definitions-factory")) {
            builder.addPropertyValue("definitionsFactoryClass", (Object)element.getAttribute("definitions-factory"));
        }
        if (element.hasAttribute("preparer-factory")) {
            builder.addPropertyValue("preparerFactoryClass", (Object)element.getAttribute("preparer-factory"));
        }
    }
}

