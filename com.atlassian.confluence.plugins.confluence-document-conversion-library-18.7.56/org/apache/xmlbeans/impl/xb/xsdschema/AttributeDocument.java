/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelAttribute;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface AttributeDocument
extends XmlObject {
    public static final DocumentFactory<AttributeDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "attributeedb9doctype");
    public static final SchemaType type = Factory.getType();

    public TopLevelAttribute getAttribute();

    public void setAttribute(TopLevelAttribute var1);

    public TopLevelAttribute addNewAttribute();
}

