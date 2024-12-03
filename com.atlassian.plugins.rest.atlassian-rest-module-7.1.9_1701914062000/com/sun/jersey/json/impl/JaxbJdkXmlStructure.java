/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 */
package com.sun.jersey.json.impl;

import com.sun.jersey.json.impl.DefaultJaxbXmlDocumentStructure;
import com.sun.jersey.json.impl.JSONHelper;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

public class JaxbJdkXmlStructure
extends DefaultJaxbXmlDocumentStructure {
    private static final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
    private Map<String, QName> qNamesOfExpElems = new HashMap<String, QName>();
    private Map<String, QName> qNamesOfExpAttrs = new HashMap<String, QName>();
    private LinkedList<NodeWrapper> processedNodes = new LinkedList();
    private final boolean isReader;

    public JaxbJdkXmlStructure(JAXBContext jaxbContext, Class<?> expectedType, boolean isReader) {
        super(jaxbContext, expectedType, isReader);
        this.isReader = isReader;
    }

    private Collection<QName> getExpectedEntities(String methodName) {
        try {
            Class<?> aClass = systemClassLoader.loadClass("com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext");
            Object getInstance = aClass.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            Object getCurrentExpectedElements = aClass.getMethod(methodName, new Class[0]).invoke(getInstance, new Object[0]);
            return (Collection)getCurrentExpectedElements;
        }
        catch (NullPointerException nullPointerException) {
        }
        catch (Exception exception) {
            // empty catch block
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<QName> getExpectedElements() {
        return this.getExpectedEntities("getCurrentExpectedElements");
    }

    @Override
    public Collection<QName> getExpectedAttributes() {
        if (this.canHandleAttributes()) {
            return this.getExpectedEntities("getCurrentExpectedAttributes");
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
        Object rawType;
        NodeWrapper peek = this.processedNodes.getLast();
        try {
            Class<?> runtimeReferencePropertyInfo = systemClassLoader.loadClass("com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo");
            rawType = runtimeReferencePropertyInfo.getMethod("getRawType", new Class[0]).invoke(peek.runtimePropertyInfo, new Object[0]);
        }
        catch (Exception e) {
            rawType = null;
        }
        return peek.runtimePropertyInfo == null ? null : (Type)rawType;
    }

    @Override
    public Type getIndividualType() {
        NodeWrapper peek = this.processedNodes.getLast();
        Object individualType = null;
        try {
            Class<?> runtimeReferencePropertyInfo = systemClassLoader.loadClass("com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo");
            Boolean isCollection = (Boolean)runtimeReferencePropertyInfo.getMethod("isCollection", new Class[0]).invoke(peek.runtimePropertyInfo, new Object[0]);
            if (isCollection.booleanValue()) {
                individualType = runtimeReferencePropertyInfo.getMethod("getIndividualType", new Class[0]).invoke(peek.runtimePropertyInfo, new Object[0]);
            }
        }
        catch (Exception e) {
            individualType = null;
        }
        return peek.runtimePropertyInfo == null ? null : (Type)individualType;
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

    private Object getCurrentElementRuntimePropertyInfo() {
        try {
            Class<?> aClass = systemClassLoader.loadClass("com.sun.xml.internal.bind.v2.runtime.XMLSerializer");
            Object xs = aClass.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            Method getCurrentProperty = aClass.getMethod("getCurrentProperty", new Class[0]);
            Object cp = xs == null ? null : getCurrentProperty.invoke(xs, new Object[0]);
            Class<?> bClass = systemClassLoader.loadClass("com.sun.xml.internal.bind.v2.runtime.property.Property");
            Method getInfo = bClass.getMethod("getInfo", new Class[0]);
            return cp == null ? null : getInfo.invoke(cp, new Object[0]);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isArrayCollection() {
        Object runtimePropertyInfo;
        Object object = runtimePropertyInfo = this.isReader ? null : this.getCurrentElementRuntimePropertyInfo();
        if (runtimePropertyInfo == null && !this.processedNodes.isEmpty()) {
            NodeWrapper peek = this.processedNodes.getLast();
            runtimePropertyInfo = peek.runtimePropertyInfo;
        }
        boolean isCollection = false;
        try {
            Class<?> runtimeReferencePropertyInfo = systemClassLoader.loadClass("com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo");
            isCollection = (Boolean)runtimeReferencePropertyInfo.getMethod("isCollection", new Class[0]).invoke(runtimePropertyInfo, new Object[0]);
        }
        catch (Exception e) {
            isCollection = false;
        }
        return runtimePropertyInfo != null && isCollection && !this.isWildcardElement(runtimePropertyInfo);
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
        return !this.processedNodes.isEmpty() && this.processedNodes.getLast() != this.getCurrentElementRuntimePropertyInfo();
    }

    @Override
    public void endElement(QName name) {
        if (!this.isReader) {
            this.processedNodes.removeLast();
        }
    }

    private boolean isWildcardElement(Object ri) {
        try {
            Class<?> runtimeReferencePropertyInfo = systemClassLoader.loadClass("com.sun.xml.internal.bind.v2.model.runtime.RuntimeReferencePropertyInfo");
            return runtimeReferencePropertyInfo.getMethod("getWildcard", new Class[0]).invoke(ri, new Object[0]) != null;
        }
        catch (Exception e) {
            return false;
        }
    }

    private static class NodeWrapper {
        private final NodeWrapper parent;
        private final Object runtimePropertyInfo;

        private NodeWrapper(NodeWrapper parent, Object runtimePropertyInfo) {
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

