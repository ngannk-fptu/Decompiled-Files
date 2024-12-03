/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBElement
 *  org.eclipse.persistence.descriptors.ClassDescriptor
 *  org.eclipse.persistence.internal.oxm.MappingNodeValue
 *  org.eclipse.persistence.internal.oxm.TreeObjectBuilder
 *  org.eclipse.persistence.internal.oxm.XPathFragment
 *  org.eclipse.persistence.internal.oxm.XPathNode
 *  org.eclipse.persistence.jaxb.JAXBContext
 *  org.eclipse.persistence.jaxb.JAXBHelper
 *  org.eclipse.persistence.mappings.DatabaseMapping
 *  org.eclipse.persistence.mappings.converters.Converter
 *  org.eclipse.persistence.mappings.converters.TypeConversionConverter
 *  org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping
 *  org.eclipse.persistence.oxm.XMLContext
 *  org.eclipse.persistence.oxm.XMLDescriptor
 *  org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping
 *  org.eclipse.persistence.sessions.DatabaseSession
 */
package com.sun.jersey.json.impl;

import com.sun.jersey.json.impl.DefaultJaxbXmlDocumentStructure;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.oxm.MappingNodeValue;
import org.eclipse.persistence.internal.oxm.TreeObjectBuilder;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBHelper;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.sessions.DatabaseSession;

public class MoxyXmlStructure
extends DefaultJaxbXmlDocumentStructure {
    private Stack<XPathNodeWrapper> xPathNodes = new Stack();
    private XPathNodeWrapper lastAccessedNode = null;
    private final Class<?> expectedType;
    private final javax.xml.bind.JAXBContext jaxbContext;
    private boolean firstDocumentElement = true;
    private final boolean isReader;

    public MoxyXmlStructure(javax.xml.bind.JAXBContext jaxbContext, Class<?> expectedType, boolean isReader) {
        super(jaxbContext, expectedType, isReader);
        this.jaxbContext = jaxbContext;
        this.expectedType = expectedType;
        this.isReader = isReader;
    }

    private XPathNodeWrapper getRootNodeWrapperForElement(QName elementName, boolean isRoot) {
        ClassDescriptor descriptor;
        if (this.jaxbContext == null) {
            return null;
        }
        JAXBContext moxyJaxbContext = JAXBHelper.getJAXBContext((javax.xml.bind.JAXBContext)this.jaxbContext);
        XMLContext xmlContext = moxyJaxbContext.getXMLContext();
        DatabaseSession session = xmlContext.getSession(0);
        Class expectedType = this.expectedType;
        if (!isRoot) {
            HashMap typeToSchemaType = moxyJaxbContext.getTypeToSchemaType();
            for (Map.Entry entry : typeToSchemaType.entrySet()) {
                if (!((QName)entry.getValue()).getLocalPart().equals(elementName.getLocalPart())) continue;
                expectedType = (Class)entry.getKey();
                break;
            }
        }
        if (JAXBElement.class.isAssignableFrom(expectedType)) {
            Map descriptors = session.getDescriptors();
            for (Map.Entry descriptor2 : descriptors.entrySet()) {
                QName defaultRootElementType = ((XMLDescriptor)descriptor2.getValue()).getDefaultRootElementType();
                if (defaultRootElementType == null || !defaultRootElementType.getLocalPart().contains(elementName.getLocalPart()) || (defaultRootElementType.getNamespaceURI() != null || elementName.getNamespaceURI() != null) && (defaultRootElementType.getNamespaceURI() == null || !defaultRootElementType.getNamespaceURI().equals(elementName.getNamespaceURI()))) continue;
                expectedType = (Class)descriptor2.getKey();
            }
        }
        if ((descriptor = session.getDescriptor(expectedType)) != null) {
            TreeObjectBuilder objectBuilder = (TreeObjectBuilder)descriptor.getObjectBuilder();
            return new XPathNodeWrapper(objectBuilder.getRootXPathNode(), null, null, descriptor, new QName(expectedType.getSimpleName()));
        }
        return null;
    }

    @Override
    public Collection<QName> getExpectedElements() {
        Map nonAttributeChildrenMap;
        LinkedList<QName> elements = new LinkedList<QName>();
        XPathNodeWrapper currentNodeWrapper = this.getCurrentNodeWrapper();
        Map map = nonAttributeChildrenMap = currentNodeWrapper == null ? null : currentNodeWrapper.xPathNode.getNonAttributeChildrenMap();
        if (nonAttributeChildrenMap != null) {
            for (Map.Entry entry : nonAttributeChildrenMap.entrySet()) {
                elements.add(new QName(((XPathFragment)entry.getKey()).getNamespaceURI(), ((XPathFragment)entry.getKey()).getLocalName()));
            }
        }
        return elements;
    }

    private XPathNodeWrapper getCurrentNodeWrapper() {
        XPathNodeWrapper nodeWrapper;
        XPathNodeWrapper xPathNodeWrapper = nodeWrapper = this.xPathNodes.isEmpty() ? null : this.xPathNodes.peek();
        if (nodeWrapper != null) {
            return nodeWrapper;
        }
        return null;
    }

    @Override
    public Collection<QName> getExpectedAttributes() {
        Map attributeChildrenMap;
        LinkedList<QName> attributes = new LinkedList<QName>();
        XPathNodeWrapper currentNodeWrapper = this.getCurrentNodeWrapper();
        Map map = attributeChildrenMap = currentNodeWrapper == null ? null : currentNodeWrapper.xPathNode.getAttributeChildrenMap();
        if (attributeChildrenMap != null) {
            for (Map.Entry entry : attributeChildrenMap.entrySet()) {
                attributes.add(new QName(((XPathFragment)entry.getKey()).getNamespaceURI(), ((XPathFragment)entry.getKey()).getLocalName()));
            }
        }
        return attributes;
    }

    @Override
    public void startElement(QName name) {
        Map nonAttributeChildrenMap;
        if (name == null || this.firstDocumentElement) {
            this.firstDocumentElement = false;
            if (name != null) {
                this.xPathNodes.push(this.getRootNodeWrapperForElement(name, true));
            }
            return;
        }
        XPathNode childNode = null;
        XPathNodeWrapper newNodeWrapper = null;
        XPathNodeWrapper currentNodeWrapper = this.getCurrentNodeWrapper();
        XPathNodeWrapper actualNodeWrapper = currentNodeWrapper.currentType == null ? currentNodeWrapper : currentNodeWrapper.currentType;
        Map map = nonAttributeChildrenMap = actualNodeWrapper == null ? null : actualNodeWrapper.xPathNode.getNonAttributeChildrenMap();
        if (nonAttributeChildrenMap != null) {
            MappingNodeValue nodeValue;
            for (Map.Entry child : nonAttributeChildrenMap.entrySet()) {
                if (!name.getLocalPart().equalsIgnoreCase(((XPathFragment)child.getKey()).getLocalName())) continue;
                childNode = (XPathNode)child.getValue();
                break;
            }
            if (childNode != null && (nodeValue = (MappingNodeValue)childNode.getNodeValue()) != null) {
                ClassDescriptor descriptor = nodeValue.getMapping().getReferenceDescriptor();
                if (descriptor == null && !this.isReader) {
                    descriptor = nodeValue.getMapping().getDescriptor();
                }
                if (descriptor != null) {
                    TreeObjectBuilder objectBuilder = (TreeObjectBuilder)descriptor.getObjectBuilder();
                    XPathNodeWrapper nodeWrapper = actualNodeWrapper;
                    newNodeWrapper = new XPathNodeWrapper(objectBuilder.getRootXPathNode(), nodeWrapper, nodeValue, descriptor, name);
                    this.xPathNodes.push(newNodeWrapper);
                }
            }
        }
        this.lastAccessedNode = newNodeWrapper == null ? new XPathNodeWrapper(name) : newNodeWrapper;
    }

    @Override
    public void endElement(QName name) {
        XPathNodeWrapper xPathNodeWrapper = this.getCurrentNodeWrapper();
        if (xPathNodeWrapper != null && xPathNodeWrapper.name.equals(name)) {
            this.xPathNodes.pop();
        }
        this.lastAccessedNode = this.getCurrentNodeWrapper();
    }

    @Override
    public Map<String, QName> getExpectedElementsMap() {
        return this.getCurrentNodeWrapper() == null ? Collections.emptyMap() : this.getCurrentNodeWrapper().getExpectedElementsMap();
    }

    @Override
    public Map<String, QName> getExpectedAttributesMap() {
        return this.getCurrentNodeWrapper() == null ? Collections.emptyMap() : this.getCurrentNodeWrapper().getExpectedAttributesMap();
    }

    @Override
    public Type getEntityType(QName entity, boolean isAttribute) {
        return this.getType(entity, isAttribute, false);
    }

    @Override
    public Type getIndividualType() {
        return this.getContainerType(true);
    }

    private Type getType(QName entity, boolean isAttribute, boolean isIndividual) {
        ClassDescriptor classDescriptor;
        XPathNodeWrapper currentNodeWrapper = this.getCurrentNodeWrapper();
        ClassDescriptor classDescriptor2 = classDescriptor = currentNodeWrapper == null ? null : currentNodeWrapper.getClassDescriptor();
        if (classDescriptor != null) {
            if (currentNodeWrapper.name.equals(entity)) {
                Type containerType = this.getContainerType(isIndividual);
                return containerType != null ? containerType : classDescriptor.getJavaClass();
            }
            EntityType entityType = currentNodeWrapper.getEntitiesTypesMap(isAttribute).get(entity.getLocalPart());
            return entityType == null ? null : entityType.type;
        }
        return null;
    }

    private Type getContainerType(boolean isIndividual) {
        XPathNodeWrapper currentNodeWrapper = this.getCurrentNodeWrapper();
        if (currentNodeWrapper.nodeValue != null && currentNodeWrapper.nodeValue.isContainerValue()) {
            DatabaseMapping mapping = currentNodeWrapper.nodeValue.getMapping();
            Converter valueConverter = null;
            if (mapping != null) {
                if (isIndividual) {
                    if (mapping instanceof AbstractCompositeDirectCollectionMapping) {
                        valueConverter = ((AbstractCompositeDirectCollectionMapping)mapping).getValueConverter();
                    } else if (mapping instanceof XMLCompositeCollectionMapping) {
                        valueConverter = ((XMLCompositeCollectionMapping)mapping).getConverter();
                    }
                }
                if (valueConverter instanceof TypeConversionConverter) {
                    return ((TypeConversionConverter)valueConverter).getObjectClass();
                }
                if (mapping.getContainerPolicy() != null) {
                    return mapping.getContainerPolicy().getContainerClass();
                }
            }
        }
        return null;
    }

    @Override
    public boolean isArrayCollection() {
        XPathNodeWrapper currentNodeWrapper = this.getCurrentNodeWrapper();
        if (currentNodeWrapper != null && this.lastAccessedNode != null && this.lastAccessedNode.name == currentNodeWrapper.name) {
            MappingNodeValue nodeValue = currentNodeWrapper.getNodeValue();
            return nodeValue != null && nodeValue.isContainerValue();
        }
        return false;
    }

    @Override
    public boolean isSameArrayCollection() {
        XPathNodeWrapper beforeLast;
        XPathNodeWrapper last;
        int size = this.xPathNodes.size();
        return size >= 2 && (last = this.xPathNodes.peek()).isInSameArrayAs(beforeLast = (XPathNodeWrapper)this.xPathNodes.get(size - 2));
    }

    @Override
    public void handleAttribute(QName attributeName, String value) {
        String localPart = attributeName.getLocalPart();
        if ("@type".equals(localPart) || "type".equals(localPart)) {
            this.getCurrentNodeWrapper().currentType = this.getRootNodeWrapperForElement(new QName(value), false);
        }
    }

    @Override
    public boolean hasSubElements() {
        Collection<QName> expectedElements = this.getExpectedElements();
        return expectedElements != null && !expectedElements.isEmpty();
    }

    private final class XPathNodeWrapper {
        private Map<String, EntityType> elementTypeMap = new HashMap<String, EntityType>();
        private Map<String, EntityType> attributeTypeMap = new HashMap<String, EntityType>();
        private Map<String, QName> qNamesOfExpElems = new HashMap<String, QName>();
        private Map<String, QName> qNamesOfExpAttrs = new HashMap<String, QName>();
        private final XPathNode xPathNode;
        private final XPathNodeWrapper parent;
        private final ClassDescriptor classDescriptor;
        private final QName name;
        private final MappingNodeValue nodeValue;
        public XPathNodeWrapper currentType;

        public XPathNodeWrapper(QName name) {
            this(null, null, null, null, name);
        }

        public XPathNodeWrapper(XPathNode xPathNode, XPathNodeWrapper parent, MappingNodeValue nodeValue, ClassDescriptor classDescriptor, QName name) {
            this.xPathNode = xPathNode;
            this.parent = parent;
            this.nodeValue = nodeValue;
            this.classDescriptor = classDescriptor;
            this.name = name;
        }

        public Map<String, QName> getExpectedElementsMap() {
            if (this.qNamesOfExpElems.isEmpty()) {
                this.qNamesOfExpElems = MoxyXmlStructure.this.qnameCollectionToMap(MoxyXmlStructure.this.getExpectedElements(), true);
            }
            return this.qNamesOfExpElems;
        }

        public Map<String, QName> getExpectedAttributesMap() {
            if (this.qNamesOfExpElems.isEmpty()) {
                this.qNamesOfExpAttrs = MoxyXmlStructure.this.qnameCollectionToMap(MoxyXmlStructure.this.getExpectedAttributes(), false);
            }
            return this.qNamesOfExpAttrs;
        }

        public Map<String, EntityType> getEntitiesTypesMap(boolean isAttribute) {
            Map<String, EntityType> entitiesTypes;
            Map<String, EntityType> map = entitiesTypes = isAttribute ? this.attributeTypeMap : this.elementTypeMap;
            if (entitiesTypes.isEmpty()) {
                Map nodeMap;
                Map map2 = nodeMap = isAttribute ? this.xPathNode.getAttributeChildrenMap() : this.xPathNode.getNonAttributeChildrenMap();
                if (nodeMap != null) {
                    for (Map.Entry entry : nodeMap.entrySet()) {
                        entitiesTypes.put(((XPathFragment)entry.getKey()).getLocalName(), new EntityType(((XPathFragment)entry.getKey()).getXMLField().getType()));
                    }
                }
            }
            return entitiesTypes;
        }

        public MappingNodeValue getNodeValue() {
            return this.nodeValue;
        }

        public ClassDescriptor getClassDescriptor() {
            return this.classDescriptor;
        }

        public boolean isInSameArrayAs(XPathNodeWrapper wrapper) {
            return wrapper != null && this.classDescriptor == wrapper.classDescriptor && this.parent == wrapper.parent;
        }
    }

    private final class EntityType {
        private final Type type;

        private EntityType(Type type) {
            this.type = type;
        }
    }
}

