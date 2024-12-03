/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanDefinitionStoreException
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.xml.DomUtils
 */
package org.eclipse.gemini.blueprint.blueprint.config.internal;

import java.util.Map;
import java.util.Set;
import org.eclipse.gemini.blueprint.blueprint.config.internal.BlueprintDefaultsDefinition;
import org.eclipse.gemini.blueprint.blueprint.config.internal.BlueprintParser;
import org.eclipse.gemini.blueprint.blueprint.config.internal.ParsingUtils;
import org.eclipse.gemini.blueprint.config.internal.OsgiDefaultsDefinition;
import org.eclipse.gemini.blueprint.config.internal.ServiceBeanDefinitionParser;
import org.eclipse.gemini.blueprint.config.internal.util.AttributeCallback;
import org.eclipse.gemini.blueprint.config.internal.util.ParserUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BlueprintServiceDefinitionParser
extends ServiceBeanDefinitionParser {
    private static final String INTERFACE = "interface";
    private static final String INTERFACES = "interfaces";
    private static final String AUTOEXPORT = "auto-export";
    private static final String DISABLED = "disabled";
    private static final String LAZY_LISTENERS = "lazyListeners";
    private static final String CACHE_TARGET = "cacheTarget";

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        if (element.hasAttribute(AUTOEXPORT) && !DISABLED.equals(element.getAttribute(AUTOEXPORT).trim())) {
            if (element.hasAttribute(INTERFACE)) {
                parserContext.getReaderContext().error("either 'auto-export' or 'interface' attribute has be specified but not both", (Object)element);
            }
            if (DomUtils.getChildElementByTagName((Element)element, (String)INTERFACES) != null) {
                parserContext.getReaderContext().error("either 'auto-export' attribute or <intefaces> sub-element has be specified but not both", (Object)element);
            }
        }
        builder.addPropertyValue(CACHE_TARGET, (Object)true);
        super.doParse(element, parserContext, builder);
    }

    @Override
    protected void parseAttributes(Element element, BeanDefinitionBuilder builder, AttributeCallback[] callbacks, OsgiDefaultsDefinition defaults) {
        BlueprintServiceAttributeCallback blueprintCallback = new BlueprintServiceAttributeCallback();
        super.parseAttributes(element, builder, ParserUtils.mergeCallbacks(new AttributeCallback[]{blueprintCallback}, callbacks), defaults);
    }

    @Override
    protected Map<?, ?> parsePropertyMapElement(ParserContext context, Element beanDef, BeanDefinition beanDefinition) {
        return BlueprintParser.parsePropertyMapElement(context, beanDef, beanDefinition);
    }

    @Override
    protected Set<?> parsePropertySetElement(ParserContext context, Element beanDef, BeanDefinition beanDefinition) {
        return BlueprintParser.parsePropertySetElement(context, beanDef, beanDefinition);
    }

    @Override
    protected Object parsePropertySubElement(ParserContext context, Element beanDef, BeanDefinition beanDefinition) {
        return BlueprintParser.parsePropertySubElement(context, beanDef, beanDefinition);
    }

    @Override
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
        String id = ParsingUtils.resolveId(element, definition, parserContext, this.shouldGenerateId(), this.shouldGenerateIdAsFallback());
        this.validateServiceReferences(element, id, parserContext);
        return id;
    }

    @Override
    protected OsgiDefaultsDefinition resolveDefaults(Document document, ParserContext parserContext) {
        return new BlueprintDefaultsDefinition(document, parserContext);
    }

    @Override
    protected void postProcessListenerDefinition(BeanDefinition wrapperDef) {
        wrapperDef.getPropertyValues().addPropertyValue("blueprintCompliant", (Object)true);
    }

    @Override
    protected void applyDefaults(ParserContext parserContext, OsgiDefaultsDefinition defaults, BeanDefinitionBuilder builder) {
        BlueprintDefaultsDefinition defs;
        super.applyDefaults(parserContext, defaults, builder);
        if (defaults instanceof BlueprintDefaultsDefinition && (defs = (BlueprintDefaultsDefinition)defaults).getDefaultInitialization()) {
            builder.addPropertyValue(LAZY_LISTENERS, (Object)defs.getDefaultInitialization());
        }
    }

    private static class BlueprintServiceAttributeCallback
    implements AttributeCallback {
        private static final String ACTIVATION = "activation";

        private BlueprintServiceAttributeCallback() {
        }

        @Override
        public boolean process(Element parent, Attr attribute, BeanDefinitionBuilder builder) {
            String name = attribute.getLocalName();
            String value = attribute.getValue();
            if (ACTIVATION.equals(name)) {
                builder.addPropertyValue(BlueprintServiceDefinitionParser.LAZY_LISTENERS, (Object)value.startsWith("l"));
                return false;
            }
            return true;
        }
    }
}

