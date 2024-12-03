/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanDefinitionStoreException
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.blueprint.config.internal;

import java.util.Set;
import org.eclipse.gemini.blueprint.blueprint.config.internal.BlueprintDefaultsDefinition;
import org.eclipse.gemini.blueprint.blueprint.config.internal.BlueprintParser;
import org.eclipse.gemini.blueprint.blueprint.config.internal.BlueprintReferenceAttributeCallback;
import org.eclipse.gemini.blueprint.config.internal.CollectionBeanDefinitionParser;
import org.eclipse.gemini.blueprint.config.internal.OsgiDefaultsDefinition;
import org.eclipse.gemini.blueprint.config.internal.util.AttributeCallback;
import org.eclipse.gemini.blueprint.config.internal.util.ParserUtils;
import org.eclipse.gemini.blueprint.service.importer.support.CollectionType;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class BlueprintCollectionBeanDefinitionParser
extends CollectionBeanDefinitionParser {
    private static final String REFERENCE_LISTENER = "reference-listener";

    @Override
    protected OsgiDefaultsDefinition resolveDefaults(Document document, ParserContext parserContext) {
        return new BlueprintDefaultsDefinition(document, parserContext);
    }

    @Override
    protected void parseAttributes(Element element, BeanDefinitionBuilder builder, AttributeCallback[] callbacks, OsgiDefaultsDefinition defaults) {
        BlueprintReferenceAttributeCallback blueprintCallback = new BlueprintReferenceAttributeCallback();
        super.parseAttributes(element, builder, ParserUtils.mergeCallbacks(new AttributeCallback[]{blueprintCallback}, callbacks), defaults);
    }

    @Override
    protected Set parsePropertySetElement(ParserContext context, Element beanDef, BeanDefinition beanDefinition) {
        return BlueprintParser.parsePropertySetElement(context, beanDef, beanDefinition);
    }

    @Override
    protected Object parsePropertySubElement(ParserContext context, Element beanDef, BeanDefinition beanDefinition) {
        return BlueprintParser.parsePropertySubElement(context, beanDef, beanDefinition);
    }

    @Override
    protected void doParse(Element element, ParserContext context, BeanDefinitionBuilder builder) {
        super.doParse(element, context, builder);
        builder.addPropertyValue("useBlueprintExceptions", (Object)true);
        builder.addPropertyValue("blueprintCompliant", (Object)true);
    }

    @Override
    protected String getListenerElementName() {
        return REFERENCE_LISTENER;
    }

    @Override
    protected CollectionType collectionType() {
        return null;
    }

    @Override
    protected String generateBeanName(String id, BeanDefinition def, ParserContext parserContext) {
        return super.generateBeanName("." + id, def, parserContext);
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
            builder.setLazyInit(defs.getDefaultInitialization());
        }
    }

    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
        String id = element.getAttribute("id");
        if (!StringUtils.hasText((String)id)) {
            id = this.generateBeanName("", (BeanDefinition)definition, parserContext);
        }
        return id;
    }
}

