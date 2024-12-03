/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Facet;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface MinInclusiveDocument
extends XmlObject {
    public static final DocumentFactory<MinInclusiveDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "mininclusive8b49doctype");
    public static final SchemaType type = Factory.getType();

    public Facet getMinInclusive();

    public void setMinInclusive(Facet var1);

    public Facet addNewMinInclusive();
}

