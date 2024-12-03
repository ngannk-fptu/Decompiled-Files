/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinitionHolder
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.BeanDefinitionParserDelegate
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.xml.DomUtils
 */
package org.eclipse.gemini.blueprint.blueprint.config;

import org.eclipse.gemini.blueprint.blueprint.config.TypeConverterBeanDefinitionParser;
import org.eclipse.gemini.blueprint.blueprint.config.internal.BlueprintCollectionBeanDefinitionParser;
import org.eclipse.gemini.blueprint.blueprint.config.internal.BlueprintParser;
import org.eclipse.gemini.blueprint.blueprint.config.internal.BlueprintReferenceBeanDefinitionParser;
import org.eclipse.gemini.blueprint.blueprint.config.internal.BlueprintServiceDefinitionParser;
import org.eclipse.gemini.blueprint.blueprint.config.internal.ParsingUtils;
import org.eclipse.gemini.blueprint.service.importer.support.CollectionType;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class BlueprintBeanDefinitionParser
implements BeanDefinitionParser {
    static final String BLUEPRINT = "blueprint";
    private static final String DESCRIPTION = "description";
    private static final String BEAN = "bean";
    static final String REFERENCE = "reference";
    static final String SERVICE = "service";
    static final String REFERENCE_LIST = "reference-list";
    static final String REFERENCE_SET = "reference-set";

    BlueprintBeanDefinitionParser() {
    }

    public BeanDefinition parse(Element componentsRootElement, ParserContext parserContext) {
        BeanDefinitionParserDelegate delegate = parserContext.getDelegate();
        delegate.initDefaults(componentsRootElement);
        NodeList nl = componentsRootElement.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (!(node instanceof Element)) continue;
            Element ele = (Element)node;
            String namespaceUri = ele.getNamespaceURI();
            if (delegate.isDefaultNamespace(namespaceUri)) {
                BeanDefinitionHolder holder = delegate.parseBeanDefinitionElement(ele);
                ParsingUtils.decorateAndRegister(ele, holder, parserContext);
                continue;
            }
            if ("http://www.osgi.org/xmlns/blueprint/v1.0.0".equals(namespaceUri)) {
                this.parseTopLevelElement(ele, parserContext);
                continue;
            }
            delegate.parseCustomElement(ele);
        }
        return null;
    }

    protected void parseTopLevelElement(Element ele, ParserContext parserContext) {
        if (!DomUtils.nodeNameEquals((Node)ele, (String)DESCRIPTION)) {
            if (DomUtils.nodeNameEquals((Node)ele, (String)BEAN)) {
                this.parseComponentElement(ele, parserContext);
            } else if (DomUtils.nodeNameEquals((Node)ele, (String)REFERENCE)) {
                this.parseReferenceElement(ele, parserContext);
            } else if (DomUtils.nodeNameEquals((Node)ele, (String)SERVICE)) {
                this.parseServiceElement(ele, parserContext);
            } else if (DomUtils.nodeNameEquals((Node)ele, (String)REFERENCE_LIST)) {
                this.parseListElement(ele, parserContext);
            } else if (DomUtils.nodeNameEquals((Node)ele, (String)REFERENCE_SET)) {
                this.parseSetElement(ele, parserContext);
            } else if (DomUtils.nodeNameEquals((Node)ele, (String)"type-converters")) {
                this.parseConvertersElement(ele, parserContext);
            } else {
                throw new IllegalArgumentException("Unknown element " + ele);
            }
        }
    }

    protected void parseComponentElement(Element ele, ParserContext parserContext) {
        BeanDefinitionHolder holder = new BlueprintParser().parseAsHolder(ele, parserContext);
        ParsingUtils.decorateAndRegister(ele, holder, parserContext);
    }

    protected void parseConvertersElement(Element ele, ParserContext parserContext) {
        TypeConverterBeanDefinitionParser parser = new TypeConverterBeanDefinitionParser();
        parser.parse(ele, parserContext);
    }

    private void parseReferenceElement(Element ele, ParserContext parserContext) {
        BlueprintReferenceBeanDefinitionParser parser = new BlueprintReferenceBeanDefinitionParser();
        parser.parse(ele, parserContext);
    }

    private void parseServiceElement(Element ele, ParserContext parserContext) {
        BlueprintServiceDefinitionParser parser = new BlueprintServiceDefinitionParser();
        parser.parse(ele, parserContext);
    }

    private void parseListElement(Element ele, ParserContext parserContext) {
        BlueprintCollectionBeanDefinitionParser parser = new BlueprintCollectionBeanDefinitionParser(){

            @Override
            protected CollectionType collectionType() {
                return CollectionType.LIST;
            }
        };
        parser.parse(ele, parserContext);
    }

    private void parseSetElement(Element ele, ParserContext parserContext) {
        BlueprintCollectionBeanDefinitionParser parser = new BlueprintCollectionBeanDefinitionParser(){

            @Override
            protected CollectionType collectionType() {
                return CollectionType.SET;
            }
        };
        parser.parse(ele, parserContext);
    }
}

