/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface AllDocument
extends XmlObject {
    public static final DocumentFactory<AllDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "all7214doctype");
    public static final SchemaType type = Factory.getType();

    public All getAll();

    public void setAll(All var1);

    public All addNewAll();
}

