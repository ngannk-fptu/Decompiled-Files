/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Facet;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface MaxInclusiveDocument
extends XmlObject {
    public static final DocumentFactory<MaxInclusiveDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "maxinclusive93dbdoctype");
    public static final SchemaType type = Factory.getType();

    public Facet getMaxInclusive();

    public void setMaxInclusive(Facet var1);

    public Facet addNewMaxInclusive();
}

