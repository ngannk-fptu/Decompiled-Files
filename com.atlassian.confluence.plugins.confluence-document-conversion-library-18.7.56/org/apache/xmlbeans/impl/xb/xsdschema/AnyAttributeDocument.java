/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Wildcard;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface AnyAttributeDocument
extends XmlObject {
    public static final DocumentFactory<AnyAttributeDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "anyattribute23b3doctype");
    public static final SchemaType type = Factory.getType();

    public Wildcard getAnyAttribute();

    public void setAnyAttribute(Wildcard var1);

    public Wildcard addNewAnyAttribute();
}

