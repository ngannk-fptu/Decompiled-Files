/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanDefinitionStoreException
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinitionHolder
 *  org.springframework.beans.factory.parsing.BeanComponentDefinition
 *  org.springframework.beans.factory.parsing.ComponentDefinition
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionReaderUtils
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.xml.NamespaceHandler
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.blueprint.config.internal;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ParsingUtils {
    public static final String BLUEPRINT_GENERATED_NAME_PREFIX = ".";
    public static final String ID_ATTRIBUTE = "id";
    private static final String[] RESERVED_NAMES = new String[]{"blueprintContainer", "blueprintBundle", "blueprintBundleContext", "blueprintConverter"};
    public static final String BLUEPRINT_MARKER_NAME = "org.eclipse.gemini.blueprint.blueprint.config.internal.marker";

    public static BeanDefinitionHolder decorateAndRegister(Element ele, BeanDefinitionHolder bdHolder, ParserContext parserContext) {
        if (bdHolder != null) {
            bdHolder = ParsingUtils.decorateBeanDefinitionIfRequired(ele, bdHolder, parserContext);
        }
        return ParsingUtils.register(ele, bdHolder, parserContext);
    }

    public static BeanDefinitionHolder register(Element ele, BeanDefinitionHolder bdHolder, ParserContext parserContext) {
        if (bdHolder != null) {
            String name = bdHolder.getBeanName();
            ParsingUtils.checkReservedName(name, ele, parserContext);
            ParsingUtils.checkUniqueName(name, parserContext.getRegistry());
            try {
                BeanDefinition beanDefinition = bdHolder.getBeanDefinition();
                if (beanDefinition instanceof AbstractBeanDefinition) {
                    AbstractBeanDefinition abd = (AbstractBeanDefinition)beanDefinition;
                    abd.setLenientConstructorResolution(false);
                    abd.setNonPublicAccessAllowed(false);
                }
                BeanDefinitionReaderUtils.registerBeanDefinition((BeanDefinitionHolder)bdHolder, (BeanDefinitionRegistry)parserContext.getRegistry());
            }
            catch (BeanDefinitionStoreException ex) {
                parserContext.getReaderContext().error("Failed to register bean definition with name '" + bdHolder.getBeanName() + "'", (Object)ele, (Throwable)ex);
            }
            parserContext.registerComponent((ComponentDefinition)new BeanComponentDefinition(bdHolder));
        }
        return bdHolder;
    }

    private static void checkUniqueName(String beanName, BeanDefinitionRegistry registry) {
        if (registry.containsBeanDefinition(beanName)) {
            throw new BeanDefinitionStoreException(beanName, "Duplicate definitions named [" + beanName + "] detected.");
        }
    }

    public static BeanDefinitionHolder decorateBeanDefinitionIfRequired(Element ele, BeanDefinitionHolder originalDefinition, ParserContext parserContext) {
        BeanDefinitionHolder finalDefinition = originalDefinition;
        NamedNodeMap attributes = ele.getAttributes();
        for (int i = 0; i < attributes.getLength(); ++i) {
            Node node = attributes.item(i);
            finalDefinition = ParsingUtils.decorateIfRequired(node, finalDefinition, parserContext);
        }
        NodeList children = ele.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node node = children.item(i);
            if (node.getNodeType() != 1) continue;
            finalDefinition = ParsingUtils.decorateIfRequired(node, finalDefinition, parserContext);
        }
        return finalDefinition;
    }

    public static BeanDefinitionHolder decorateIfRequired(Node node, BeanDefinitionHolder originalDef, ParserContext parserContext) {
        String namespaceUri = node.getNamespaceURI();
        if (!parserContext.getDelegate().isDefaultNamespace(namespaceUri) && !ParsingUtils.isRFC124Namespace(namespaceUri)) {
            NamespaceHandler handler = parserContext.getReaderContext().getNamespaceHandlerResolver().resolve(namespaceUri);
            if (handler != null) {
                return handler.decorate(node, originalDef, new ParserContext(parserContext.getReaderContext(), parserContext.getDelegate()));
            }
            if (namespaceUri.startsWith("http://www.springframework.org/")) {
                parserContext.getReaderContext().error("Unable to locate Spring NamespaceHandler for XML schema namespace [" + namespaceUri + "]", (Object)node);
            }
        }
        return originalDef;
    }

    public static boolean isRFC124Namespace(Node node) {
        return "http://www.osgi.org/xmlns/blueprint/v1.0.0".equals(node.getNamespaceURI());
    }

    public static boolean isRFC124Namespace(String namespaceURI) {
        return "http://www.osgi.org/xmlns/blueprint/v1.0.0".equals(namespaceURI);
    }

    public static String generateBlueprintBeanName(BeanDefinition definition, BeanDefinitionRegistry registry, boolean isInnerBean) throws BeanDefinitionStoreException {
        String initialName;
        String generatedName = initialName = BLUEPRINT_GENERATED_NAME_PREFIX + BeanDefinitionReaderUtils.generateBeanName((BeanDefinition)definition, (BeanDefinitionRegistry)registry, (boolean)isInnerBean);
        int counter = 0;
        while (registry.containsBeanDefinition(generatedName)) {
            generatedName = initialName + "#" + counter;
            ++counter;
        }
        return generatedName;
    }

    public static String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext, boolean shouldGenerateId, boolean shouldGenerateIdAsFallback) throws BeanDefinitionStoreException {
        if (shouldGenerateId) {
            return ParsingUtils.generateBlueprintBeanName((BeanDefinition)definition, parserContext.getRegistry(), false);
        }
        String id = element.getAttribute(ID_ATTRIBUTE);
        if (!StringUtils.hasText((String)id) && shouldGenerateIdAsFallback) {
            id = ParsingUtils.generateBlueprintBeanName((BeanDefinition)definition, parserContext.getRegistry(), false);
        }
        return id;
    }

    public static boolean isReservedName(String name, Element element, ParserContext parserContext) {
        for (String reservedName : RESERVED_NAMES) {
            if (!reservedName.equals(name)) continue;
            return true;
        }
        return false;
    }

    public static void checkReservedName(String name, Element element, ParserContext parserContext) {
        if (ParsingUtils.isReservedName(name, element, parserContext)) {
            parserContext.getReaderContext().error("Blueprint reserved name '" + name + "' cannot be used", (Object)element, null, null);
        }
    }
}

