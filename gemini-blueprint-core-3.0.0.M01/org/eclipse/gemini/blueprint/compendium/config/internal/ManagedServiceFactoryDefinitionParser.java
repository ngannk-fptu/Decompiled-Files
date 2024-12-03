/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.ManagedList
 *  org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 */
package org.eclipse.gemini.blueprint.compendium.config.internal;

import org.eclipse.gemini.blueprint.compendium.internal.cm.ManagedServiceFactoryFactoryBean;
import org.eclipse.gemini.blueprint.config.internal.util.AttributeCallback;
import org.eclipse.gemini.blueprint.config.internal.util.ParserUtils;
import org.eclipse.gemini.blueprint.config.internal.util.ServiceAttributeCallback;
import org.eclipse.gemini.blueprint.config.internal.util.ServiceParsingUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ManagedServiceFactoryDefinitionParser
extends AbstractSimpleBeanDefinitionParser {
    private static final String TEMPLATE_PROP = "templateDefinition";
    private static final String LISTENER = "registration-listener";
    private static final String LISTENERS_PROP = "listeners";
    private static final String LOCAL_OVERRIDE = "local-override";
    private static final String LOCAL_OVERRIDE_PROP = "localOverride";

    protected Class<?> getBeanClass(Element element) {
        return ManagedServiceFactoryFactoryBean.class;
    }

    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }

    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        ParserUtils.parseCustomAttributes(element, builder, new AttributeCallback[]{new ServiceAttributeCallback()});
        NodeList children = element.getChildNodes();
        ManagedList listeners = new ManagedList(children.getLength());
        BeanDefinition nestedDefinition = null;
        for (int i = 0; i < children.getLength(); ++i) {
            Node nd = children.item(i);
            if (nd instanceof Element) {
                Element nestedElement = (Element)nd;
                String name = nestedElement.getLocalName();
                if (!ServiceParsingUtils.parseInterfaces(element, nestedElement, parserContext, builder) && !ServiceParsingUtils.parseServiceProperties(element, nestedElement, parserContext, builder)) {
                    if (LISTENER.equals(name)) {
                        listeners.add((Object)ServiceParsingUtils.parseListener(parserContext, nestedElement, builder));
                    } else {
                        String ns = nestedElement.getNamespaceURI();
                        nestedDefinition = ns == null && name.equals("bean") || ns.equals("http://www.springframework.org/schema/beans") ? parserContext.getDelegate().parseBeanDefinitionElement(nestedElement).getBeanDefinition() : parserContext.getDelegate().parseCustomElement(nestedElement);
                    }
                }
            }
            builder.addPropertyValue(TEMPLATE_PROP, (Object)new BeanDefinition[]{nestedDefinition});
            builder.addPropertyValue(LISTENERS_PROP, (Object)listeners);
        }
    }
}

