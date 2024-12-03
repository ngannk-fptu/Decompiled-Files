/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.TypedStringValue
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.StringUtils
 */
package org.springframework.scripting.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.scripting.config.LangNamespaceUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

class ScriptingDefaultsParser
implements BeanDefinitionParser {
    private static final String REFRESH_CHECK_DELAY_ATTRIBUTE = "refresh-check-delay";
    private static final String PROXY_TARGET_CLASS_ATTRIBUTE = "proxy-target-class";

    ScriptingDefaultsParser() {
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String proxyTargetClass;
        BeanDefinition bd = LangNamespaceUtils.registerScriptFactoryPostProcessorIfNecessary(parserContext.getRegistry());
        String refreshCheckDelay = element.getAttribute(REFRESH_CHECK_DELAY_ATTRIBUTE);
        if (StringUtils.hasText((String)refreshCheckDelay)) {
            bd.getPropertyValues().add("defaultRefreshCheckDelay", (Object)Long.valueOf(refreshCheckDelay));
        }
        if (StringUtils.hasText((String)(proxyTargetClass = element.getAttribute(PROXY_TARGET_CLASS_ATTRIBUTE)))) {
            bd.getPropertyValues().add("defaultProxyTargetClass", (Object)new TypedStringValue(proxyTargetClass, Boolean.class));
        }
        return null;
    }
}

