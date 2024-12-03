/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scripting.config;

import java.util.List;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.lang.Nullable;
import org.springframework.scripting.config.LangNamespaceUtils;
import org.springframework.scripting.support.ScriptFactoryPostProcessor;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

class ScriptBeanDefinitionParser
extends AbstractBeanDefinitionParser {
    private static final String ENGINE_ATTRIBUTE = "engine";
    private static final String SCRIPT_SOURCE_ATTRIBUTE = "script-source";
    private static final String INLINE_SCRIPT_ELEMENT = "inline-script";
    private static final String SCOPE_ATTRIBUTE = "scope";
    private static final String AUTOWIRE_ATTRIBUTE = "autowire";
    private static final String DEPENDS_ON_ATTRIBUTE = "depends-on";
    private static final String INIT_METHOD_ATTRIBUTE = "init-method";
    private static final String DESTROY_METHOD_ATTRIBUTE = "destroy-method";
    private static final String SCRIPT_INTERFACES_ATTRIBUTE = "script-interfaces";
    private static final String REFRESH_CHECK_DELAY_ATTRIBUTE = "refresh-check-delay";
    private static final String PROXY_TARGET_CLASS_ATTRIBUTE = "proxy-target-class";
    private static final String CUSTOMIZER_REF_ATTRIBUTE = "customizer-ref";
    private final String scriptFactoryClassName;

    public ScriptBeanDefinitionParser(String scriptFactoryClassName) {
        this.scriptFactoryClassName = scriptFactoryClassName;
    }

    @Override
    @Nullable
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        String proxyTargetClass;
        String engine = element.getAttribute(ENGINE_ATTRIBUTE);
        String value = this.resolveScriptSource(element, parserContext.getReaderContext());
        if (value == null) {
            return null;
        }
        LangNamespaceUtils.registerScriptFactoryPostProcessorIfNecessary(parserContext.getRegistry());
        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClassName(this.scriptFactoryClassName);
        bd.setSource(parserContext.extractSource(element));
        bd.setAttribute(ScriptFactoryPostProcessor.LANGUAGE_ATTRIBUTE, element.getLocalName());
        String scope = element.getAttribute(SCOPE_ATTRIBUTE);
        if (StringUtils.hasLength(scope)) {
            bd.setScope(scope);
        }
        String autowire = element.getAttribute(AUTOWIRE_ATTRIBUTE);
        int autowireMode = parserContext.getDelegate().getAutowireMode(autowire);
        if (autowireMode == 4) {
            autowireMode = 2;
        } else if (autowireMode == 3) {
            autowireMode = 0;
        }
        bd.setAutowireMode(autowireMode);
        String dependsOn = element.getAttribute(DEPENDS_ON_ATTRIBUTE);
        if (StringUtils.hasLength(dependsOn)) {
            bd.setDependsOn(StringUtils.tokenizeToStringArray(dependsOn, ",; "));
        }
        BeanDefinitionDefaults beanDefinitionDefaults = parserContext.getDelegate().getBeanDefinitionDefaults();
        String initMethod = element.getAttribute(INIT_METHOD_ATTRIBUTE);
        if (StringUtils.hasLength(initMethod)) {
            bd.setInitMethodName(initMethod);
        } else if (beanDefinitionDefaults.getInitMethodName() != null) {
            bd.setInitMethodName(beanDefinitionDefaults.getInitMethodName());
        }
        if (element.hasAttribute(DESTROY_METHOD_ATTRIBUTE)) {
            String destroyMethod = element.getAttribute(DESTROY_METHOD_ATTRIBUTE);
            bd.setDestroyMethodName(destroyMethod);
        } else if (beanDefinitionDefaults.getDestroyMethodName() != null) {
            bd.setDestroyMethodName(beanDefinitionDefaults.getDestroyMethodName());
        }
        String refreshCheckDelay = element.getAttribute(REFRESH_CHECK_DELAY_ATTRIBUTE);
        if (StringUtils.hasText(refreshCheckDelay)) {
            bd.setAttribute(ScriptFactoryPostProcessor.REFRESH_CHECK_DELAY_ATTRIBUTE, Long.valueOf(refreshCheckDelay));
        }
        if (StringUtils.hasText(proxyTargetClass = element.getAttribute(PROXY_TARGET_CLASS_ATTRIBUTE))) {
            bd.setAttribute(ScriptFactoryPostProcessor.PROXY_TARGET_CLASS_ATTRIBUTE, Boolean.valueOf(proxyTargetClass));
        }
        ConstructorArgumentValues cav = bd.getConstructorArgumentValues();
        int constructorArgNum = 0;
        if (StringUtils.hasLength(engine)) {
            cav.addIndexedArgumentValue(constructorArgNum++, engine);
        }
        cav.addIndexedArgumentValue(constructorArgNum++, value);
        if (element.hasAttribute(SCRIPT_INTERFACES_ATTRIBUTE)) {
            cav.addIndexedArgumentValue(constructorArgNum++, element.getAttribute(SCRIPT_INTERFACES_ATTRIBUTE), "java.lang.Class[]");
        }
        if (element.hasAttribute(CUSTOMIZER_REF_ATTRIBUTE)) {
            String customizerBeanName = element.getAttribute(CUSTOMIZER_REF_ATTRIBUTE);
            if (!StringUtils.hasText(customizerBeanName)) {
                parserContext.getReaderContext().error("Attribute 'customizer-ref' has empty value", element);
            } else {
                cav.addIndexedArgumentValue(constructorArgNum++, new RuntimeBeanReference(customizerBeanName));
            }
        }
        parserContext.getDelegate().parsePropertyElements(element, bd);
        return bd;
    }

    @Nullable
    private String resolveScriptSource(Element element, XmlReaderContext readerContext) {
        boolean hasScriptSource = element.hasAttribute(SCRIPT_SOURCE_ATTRIBUTE);
        List<Element> elements = DomUtils.getChildElementsByTagName(element, INLINE_SCRIPT_ELEMENT);
        if (hasScriptSource && !elements.isEmpty()) {
            readerContext.error("Only one of 'script-source' and 'inline-script' should be specified.", element);
            return null;
        }
        if (hasScriptSource) {
            return element.getAttribute(SCRIPT_SOURCE_ATTRIBUTE);
        }
        if (!elements.isEmpty()) {
            Element inlineElement = elements.get(0);
            return "inline:" + DomUtils.getTextValue(inlineElement);
        }
        readerContext.error("Must specify either 'script-source' or 'inline-script'.", element);
        return null;
    }

    @Override
    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }
}

