/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.xml.DomUtils
 */
package org.eclipse.gemini.blueprint.config.internal;

import java.util.Comparator;
import java.util.Locale;
import org.eclipse.gemini.blueprint.config.internal.AbstractReferenceDefinitionParser;
import org.eclipse.gemini.blueprint.config.internal.OsgiDefaultsDefinition;
import org.eclipse.gemini.blueprint.config.internal.util.AttributeCallback;
import org.eclipse.gemini.blueprint.config.internal.util.ParserUtils;
import org.eclipse.gemini.blueprint.service.importer.support.CollectionType;
import org.eclipse.gemini.blueprint.service.importer.support.MemberType;
import org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceCollectionProxyFactoryBean;
import org.eclipse.gemini.blueprint.service.importer.support.internal.util.ServiceReferenceComparator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class CollectionBeanDefinitionParser
extends AbstractReferenceDefinitionParser {
    private static final String NESTED_COMPARATOR = "comparator";
    private static final String INLINE_COMPARATOR_REF = "comparator-ref";
    private static final String COLLECTION_TYPE_PROP = "collectionType";
    private static final String COMPARATOR_PROPERTY = "comparator";
    private static final String SERVICE_ORDER = "service";
    private static final String SERVICE_REFERENCE_ORDER = "service-reference";
    private static final String MEMBER_TYPE = "member-type";
    private static final String MEMBER_TYPE_PROPERTY = "memberType";
    private static final Comparator SERVICE_REFERENCE_COMPARATOR = new ServiceReferenceComparator();
    private static final String NATURAL = "natural";
    private static final String BASIS = "basis";

    @Override
    protected Class getBeanClass(Element element) {
        return OsgiServiceCollectionProxyFactoryBean.class;
    }

    @Override
    protected void parseAttributes(Element element, BeanDefinitionBuilder builder, AttributeCallback[] callbacks, OsgiDefaultsDefinition defaults) {
        CollectionAttributeCallback greedyProxyingCallback = new CollectionAttributeCallback();
        super.parseAttributes(element, builder, ParserUtils.mergeCallbacks(callbacks, new AttributeCallback[]{greedyProxyingCallback}), defaults);
    }

    @Override
    protected void parseNestedElements(Element element, ParserContext context, BeanDefinitionBuilder builder) {
        super.parseNestedElements(element, context, builder);
        this.parseComparator(element, context, builder);
    }

    protected void parseComparator(Element element, ParserContext context, BeanDefinitionBuilder builder) {
        boolean hasComparatorRef = element.hasAttribute(INLINE_COMPARATOR_REF);
        Element comparatorElement = DomUtils.getChildElementByTagName((Element)element, (String)"comparator");
        Object nestedComparator = null;
        if (comparatorElement != null) {
            if (hasComparatorRef) {
                context.getReaderContext().error("nested comparator declaration is not allowed if comparator-ref attribute has been specified", (Object)comparatorElement);
            }
            NodeList nl = comparatorElement.getChildNodes();
            for (int i = 0; i < nl.getLength(); ++i) {
                Node nd = nl.item(i);
                if (!(nd instanceof Element)) continue;
                Element beanDef = (Element)nd;
                String name = beanDef.getLocalName();
                nestedComparator = NATURAL.equals(name) ? this.parseNaturalComparator(beanDef) : this.parsePropertySubElement(context, beanDef, (BeanDefinition)builder.getBeanDefinition());
            }
            if (nestedComparator != null) {
                builder.addPropertyValue("comparator", nestedComparator);
            }
        }
        if (comparatorElement != null || hasComparatorRef) {
            if (CollectionType.LIST.equals((Object)this.collectionType())) {
                builder.addPropertyValue(COLLECTION_TYPE_PROP, (Object)CollectionType.SORTED_LIST);
            }
            if (CollectionType.SET.equals((Object)this.collectionType())) {
                builder.addPropertyValue(COLLECTION_TYPE_PROP, (Object)CollectionType.SORTED_SET);
            }
        } else {
            builder.addPropertyValue(COLLECTION_TYPE_PROP, (Object)this.collectionType());
        }
    }

    protected Comparator parseNaturalComparator(Element element) {
        Comparator comparator = null;
        NamedNodeMap attributes = element.getAttributes();
        for (int x = 0; x < attributes.getLength(); ++x) {
            Attr attribute = (Attr)attributes.item(x);
            String name = attribute.getLocalName();
            String value = attribute.getValue();
            if (!BASIS.equals(name)) continue;
            if (SERVICE_REFERENCE_ORDER.equals(value)) {
                return SERVICE_REFERENCE_COMPARATOR;
            }
            if (!SERVICE_ORDER.equals(value)) continue;
            return null;
        }
        return comparator;
    }

    protected abstract CollectionType collectionType();

    static class CollectionAttributeCallback
    implements AttributeCallback {
        CollectionAttributeCallback() {
        }

        @Override
        public boolean process(Element parent, Attr attribute, BeanDefinitionBuilder builder) {
            String name = attribute.getLocalName();
            if (CollectionBeanDefinitionParser.MEMBER_TYPE.equals(name)) {
                builder.addPropertyValue(CollectionBeanDefinitionParser.MEMBER_TYPE_PROPERTY, (Object)MemberType.valueOf(attribute.getValue().toUpperCase(Locale.ENGLISH).replace('-', '_')));
                return false;
            }
            return true;
        }
    }
}

