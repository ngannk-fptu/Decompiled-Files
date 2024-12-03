/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.RuntimeBeanReference
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.core.Conventions
 *  org.springframework.util.xml.DomUtils
 */
package org.eclipse.gemini.blueprint.config.internal.util;

import java.util.Map;
import java.util.Set;
import org.eclipse.gemini.blueprint.config.internal.adapter.OsgiServiceRegistrationListenerAdapter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.Conventions;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class ServiceParsingUtils {
    private static final String REF = "ref";
    private static final String TARGET_BEAN_NAME_PROP = "targetBeanName";
    private static final String TARGET_PROP = "target";
    private static final String INTERFACE = "interface";
    private static final String INTERFACES_PROP = "interfaces";
    private static final String INTERFACES_ID = "interfaces";
    private static final String PROPS_ID = "service-properties";

    public static BeanDefinition parseListener(ParserContext context, Element element, BeanDefinitionBuilder builder) {
        NodeList nl = element.getChildNodes();
        Object target = null;
        String targetName = null;
        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (!(node instanceof Element)) continue;
            Element nestedDefinition = (Element)node;
            if (element.hasAttribute(REF)) {
                context.getReaderContext().error("nested bean declaration is not allowed if 'ref' attribute has been specified", (Object)nestedDefinition);
            }
            if (!((target = context.getDelegate().parsePropertySubElement(nestedDefinition, (BeanDefinition)builder.getBeanDefinition())) instanceof RuntimeBeanReference)) continue;
            targetName = ((RuntimeBeanReference)target).getBeanName();
        }
        BeanDefinitionBuilder localBuilder = BeanDefinitionBuilder.rootBeanDefinition(OsgiServiceRegistrationListenerAdapter.class);
        localBuilder.setRole(2);
        NamedNodeMap attrs = element.getAttributes();
        for (int x = 0; x < attrs.getLength(); ++x) {
            Attr attribute = (Attr)attrs.item(x);
            String name = attribute.getLocalName();
            if (REF.equals(name)) {
                targetName = attribute.getValue();
                continue;
            }
            localBuilder.addPropertyValue(Conventions.attributeNameToPropertyName((String)name), (Object)attribute.getValue());
        }
        if (targetName != null) {
            localBuilder.addPropertyValue(TARGET_BEAN_NAME_PROP, targetName);
        } else {
            localBuilder.addPropertyValue(TARGET_PROP, target);
        }
        return localBuilder.getBeanDefinition();
    }

    public static boolean parseInterfaces(Element parent, Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String name = element.getLocalName();
        if ("interfaces".equals(name)) {
            if (parent.hasAttribute(INTERFACE)) {
                parserContext.getReaderContext().error("either 'interface' attribute or <intefaces> sub-element has be specified", (Object)parent);
            }
            Set interfaces = parserContext.getDelegate().parseSetElement(element, (BeanDefinition)builder.getBeanDefinition());
            builder.addPropertyValue("interfaces", (Object)interfaces);
            return true;
        }
        return false;
    }

    public static boolean parseServiceProperties(Element parent, Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String name = element.getLocalName();
        if (PROPS_ID.equals(name)) {
            if (DomUtils.getChildElementsByTagName((Element)element, (String)"entry").size() > 0) {
                Map props = parserContext.getDelegate().parseMapElement(element, (BeanDefinition)builder.getRawBeanDefinition());
                builder.addPropertyValue(Conventions.attributeNameToPropertyName((String)PROPS_ID), (Object)props);
            } else {
                parserContext.getReaderContext().error("Invalid service property type", (Object)element);
            }
            return true;
        }
        return false;
    }
}

