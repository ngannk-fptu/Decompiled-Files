/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import javax.xml.namespace.QName;

public interface JaxbXmlDocumentStructure {
    public void startElement(QName var1);

    public void endElement(QName var1);

    public boolean canHandleAttributes();

    public void handleAttribute(QName var1, String var2);

    public Type getEntityType(QName var1, boolean var2);

    public Type getIndividualType();

    public Collection<QName> getExpectedAttributes();

    public Map<String, QName> getExpectedAttributesMap();

    public Collection<QName> getExpectedElements();

    public Map<String, QName> getExpectedElementsMap();

    public boolean isArrayCollection();

    public boolean isSameArrayCollection();

    public boolean hasSubElements();
}

