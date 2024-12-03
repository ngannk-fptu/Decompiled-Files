/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 */
package com.sun.jersey.json.impl;

import com.sun.jersey.json.impl.JSONHelper;
import com.sun.jersey.json.impl.JaxbXmlDocumentStructure;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

public abstract class DefaultJaxbXmlDocumentStructure
implements JaxbXmlDocumentStructure {
    public static JaxbXmlDocumentStructure getXmlDocumentStructure(JAXBContext jaxbContext, Class<?> expectedType, boolean isReader) throws IllegalStateException {
        ReflectiveOperationException throwable = null;
        try {
            return JSONHelper.getJaxbProvider(jaxbContext).getDocumentStructureClass().getConstructor(JAXBContext.class, Class.class, Boolean.TYPE).newInstance(jaxbContext, expectedType, isReader);
        }
        catch (InvocationTargetException e) {
            throwable = e;
        }
        catch (NoSuchMethodException e) {
            throwable = e;
        }
        catch (InstantiationException e) {
            throwable = e;
        }
        catch (IllegalAccessException e) {
            throwable = e;
        }
        throw new IllegalStateException("Cannot create a JaxbXmlDocumentStructure instance.", throwable);
    }

    protected DefaultJaxbXmlDocumentStructure(JAXBContext jaxbContext, Class<?> expectedType, boolean isReader) {
    }

    @Override
    public Collection<QName> getExpectedElements() {
        return Collections.emptyList();
    }

    @Override
    public Collection<QName> getExpectedAttributes() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, QName> getExpectedElementsMap() {
        return this.qnameCollectionToMap(this.getExpectedElements(), true);
    }

    @Override
    public Map<String, QName> getExpectedAttributesMap() {
        return this.qnameCollectionToMap(this.getExpectedAttributes(), false);
    }

    @Override
    public void startElement(QName name) {
    }

    @Override
    public void endElement(QName name) {
    }

    @Override
    public boolean canHandleAttributes() {
        return true;
    }

    @Override
    public Type getEntityType(QName entity, boolean isAttribute) {
        return null;
    }

    @Override
    public Type getIndividualType() {
        return null;
    }

    @Override
    public void handleAttribute(QName attributeName, String value) {
    }

    @Override
    public boolean isArrayCollection() {
        return false;
    }

    @Override
    public boolean isSameArrayCollection() {
        return true;
    }

    protected Map<String, QName> qnameCollectionToMap(Collection<QName> collection, boolean elementCollection) {
        HashMap<String, QName> map = new HashMap<String, QName>();
        for (QName qname : collection) {
            String namespaceUri = qname.getNamespaceURI();
            if (elementCollection && "\u0000".equals(namespaceUri)) {
                map.put("$", null);
                continue;
            }
            map.put(qname.getLocalPart(), qname);
        }
        return map;
    }
}

