/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.bind.v2.model.runtime.RuntimePropertyInfo
 *  com.sun.xml.bind.v2.model.runtime.RuntimeReferencePropertyInfo
 *  com.sun.xml.bind.v2.runtime.XMLSerializer
 *  com.sun.xml.bind.v2.runtime.property.Property
 *  com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallingContext
 *  javax.xml.bind.JAXBContext
 */
package com.sun.jersey.json.impl;

import com.sun.jersey.json.impl.DefaultJaxbXmlDocumentStructure;
import com.sun.jersey.json.impl.ImplMessages;
import com.sun.jersey.json.impl.JSONHelper;
import com.sun.xml.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.bind.v2.model.runtime.RuntimeReferencePropertyInfo;
import com.sun.xml.bind.v2.runtime.XMLSerializer;
import com.sun.xml.bind.v2.runtime.property.Property;
import com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

public class JaxbRiXmlStructure
extends DefaultJaxbXmlDocumentStructure {
    private Map<String, QName> qNamesOfExpElems = new HashMap<String, QName>();
    private Map<String, QName> qNamesOfExpAttrs = new HashMap<String, QName>();
    private LinkedList<NodeWrapper> processedNodes = new LinkedList();
    private final boolean isReader;

    public JaxbRiXmlStructure(JAXBContext jaxbContext, Class<?> expectedType, boolean isReader) {
        super(jaxbContext, expectedType, isReader);
        this.isReader = isReader;
    }

    @Override
    public Collection<QName> getExpectedElements() {
        try {
            return UnmarshallingContext.getInstance().getCurrentExpectedElements();
        }
        catch (NullPointerException nullPointerException) {
            return Collections.emptyList();
        }
    }

    @Override
    public Collection<QName> getExpectedAttributes() {
        if (JSONHelper.isNaturalNotationEnabled()) {
            try {
                return UnmarshallingContext.getInstance().getCurrentExpectedAttributes();
            }
            catch (NullPointerException nullPointerException) {
            }
            catch (NoSuchMethodError nsme) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ImplMessages.ERROR_JAXB_RI_2_1_12_MISSING(), nsme);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public Map<String, QName> getExpectedElementsMap() {
        Collection<QName> expectedElements = this.getExpectedElements();
        if (!expectedElements.isEmpty()) {
            this.qNamesOfExpElems = this.qnameCollectionToMap(expectedElements, true);
        }
        return this.qNamesOfExpElems;
    }

    @Override
    public Map<String, QName> getExpectedAttributesMap() {
        Collection<QName> expectedAttributes = this.getExpectedAttributes();
        if (!expectedAttributes.isEmpty()) {
            this.qNamesOfExpAttrs = this.qnameCollectionToMap(expectedAttributes, false);
        }
        return this.qNamesOfExpAttrs;
    }

    @Override
    public boolean canHandleAttributes() {
        return JSONHelper.isNaturalNotationEnabled();
    }

    @Override
    public Type getEntityType(QName entity, boolean isAttribute) {
        NodeWrapper peek = this.processedNodes.getLast();
        return peek.runtimePropertyInfo == null ? null : peek.runtimePropertyInfo.getRawType();
    }

    @Override
    public Type getIndividualType() {
        NodeWrapper peek = this.processedNodes.getLast();
        return peek.runtimePropertyInfo == null ? null : (peek.runtimePropertyInfo.isCollection() ? peek.runtimePropertyInfo.getIndividualType() : null);
    }

    @Override
    public void startElement(QName name) {
        if (!this.isReader) {
            this.processedNodes.add(new NodeWrapper(this.processedNodes.isEmpty() ? null : this.processedNodes.getLast(), this.getCurrentElementRuntimePropertyInfo()));
        }
    }

    @Override
    public void handleAttribute(QName attributeName, String value) {
        this.startElement(attributeName);
    }

    private RuntimePropertyInfo getCurrentElementRuntimePropertyInfo() {
        XMLSerializer xs = XMLSerializer.getInstance();
        Property cp = xs == null ? null : xs.getCurrentProperty();
        return cp == null ? null : cp.getInfo();
    }

    @Override
    public boolean isArrayCollection() {
        RuntimePropertyInfo runtimePropertyInfo;
        RuntimePropertyInfo runtimePropertyInfo2 = runtimePropertyInfo = this.isReader ? null : this.getCurrentElementRuntimePropertyInfo();
        if (runtimePropertyInfo == null && !this.processedNodes.isEmpty()) {
            NodeWrapper peek = this.processedNodes.getLast();
            runtimePropertyInfo = peek.runtimePropertyInfo;
        }
        return runtimePropertyInfo != null && runtimePropertyInfo.isCollection() && !this.isWildcardElement(runtimePropertyInfo);
    }

    @Override
    public boolean isSameArrayCollection() {
        NodeWrapper beforeLast;
        NodeWrapper last;
        int size = this.processedNodes.size();
        return size >= 2 && (last = this.processedNodes.getLast()).equals(beforeLast = this.processedNodes.get(size - 2));
    }

    @Override
    public boolean hasSubElements() {
        if (this.isReader) {
            return !this.getExpectedElements().isEmpty();
        }
        RuntimePropertyInfo rpi = this.getCurrentElementRuntimePropertyInfo();
        return !this.processedNodes.isEmpty() && (rpi == null || rpi.elementOnlyContent());
    }

    @Override
    public void endElement(QName name) {
        if (!this.isReader) {
            this.processedNodes.removeLast();
        }
    }

    private boolean isWildcardElement(RuntimePropertyInfo ri) {
        return ri instanceof RuntimeReferencePropertyInfo && ((RuntimeReferencePropertyInfo)ri).getWildcard() != null;
    }

    private static class NodeWrapper {
        private final NodeWrapper parent;
        private final RuntimePropertyInfo runtimePropertyInfo;

        private NodeWrapper(NodeWrapper parent, RuntimePropertyInfo runtimePropertyInfo) {
            this.parent = parent;
            this.runtimePropertyInfo = runtimePropertyInfo;
        }

        public int hashCode() {
            int hash = 13;
            hash += this.parent == null ? 0 : this.parent.hashCode();
            return hash += this.runtimePropertyInfo == null ? 0 : this.runtimePropertyInfo.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof NodeWrapper)) {
                return false;
            }
            NodeWrapper other = (NodeWrapper)obj;
            return this.runtimePropertyInfo == other.runtimePropertyInfo && this.parent == other.parent;
        }
    }
}

