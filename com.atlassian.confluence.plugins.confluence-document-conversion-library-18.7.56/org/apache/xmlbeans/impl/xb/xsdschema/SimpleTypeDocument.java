/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelSimpleType;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface SimpleTypeDocument
extends XmlObject {
    public static final DocumentFactory<SimpleTypeDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "simpletypedef7doctype");
    public static final SchemaType type = Factory.getType();

    public TopLevelSimpleType getSimpleType();

    public void setSimpleType(TopLevelSimpleType var1);

    public TopLevelSimpleType addNewSimpleType();
}

