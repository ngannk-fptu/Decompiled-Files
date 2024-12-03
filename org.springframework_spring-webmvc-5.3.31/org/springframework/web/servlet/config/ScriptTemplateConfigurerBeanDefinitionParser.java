/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.StringUtils
 *  org.springframework.util.xml.DomUtils
 */
package org.springframework.web.servlet.config;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class ScriptTemplateConfigurerBeanDefinitionParser
extends AbstractSimpleBeanDefinitionParser {
    public static final String BEAN_NAME = "mvcScriptTemplateConfigurer";

    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
        return BEAN_NAME;
    }

    protected String getBeanClassName(Element element) {
        return "org.springframework.web.servlet.view.script.ScriptTemplateConfigurer";
    }

    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        List childElements = DomUtils.getChildElementsByTagName((Element)element, (String)"script");
        if (!childElements.isEmpty()) {
            ArrayList<String> locations = new ArrayList<String>(childElements.size());
            for (Element childElement : childElements) {
                locations.add(childElement.getAttribute("location"));
            }
            builder.addPropertyValue("scripts", (Object)StringUtils.toStringArray(locations));
        }
        builder.addPropertyValue("engineName", (Object)element.getAttribute("engine-name"));
        if (element.hasAttribute("render-object")) {
            builder.addPropertyValue("renderObject", (Object)element.getAttribute("render-object"));
        }
        if (element.hasAttribute("render-function")) {
            builder.addPropertyValue("renderFunction", (Object)element.getAttribute("render-function"));
        }
        if (element.hasAttribute("content-type")) {
            builder.addPropertyValue("contentType", (Object)element.getAttribute("content-type"));
        }
        if (element.hasAttribute("charset")) {
            builder.addPropertyValue("charset", (Object)Charset.forName(element.getAttribute("charset")));
        }
        if (element.hasAttribute("resource-loader-path")) {
            builder.addPropertyValue("resourceLoaderPath", (Object)element.getAttribute("resource-loader-path"));
        }
        if (element.hasAttribute("shared-engine")) {
            builder.addPropertyValue("sharedEngine", (Object)element.getAttribute("shared-engine"));
        }
    }

    protected boolean isEligibleAttribute(String name) {
        return name.equals("engine-name") || name.equals("scripts") || name.equals("render-object") || name.equals("render-function") || name.equals("content-type") || name.equals("charset") || name.equals("resource-loader-path");
    }
}

