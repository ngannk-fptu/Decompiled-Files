/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Facet;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface MaxExclusiveDocument
extends XmlObject {
    public static final DocumentFactory<MaxExclusiveDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "maxexclusive6d69doctype");
    public static final SchemaType type = Factory.getType();

    public Facet getMaxExclusive();

    public void setMaxExclusive(Facet var1);

    public Facet addNewMaxExclusive();
}

