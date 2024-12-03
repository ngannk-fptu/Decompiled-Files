/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.NumFacet;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface MaxLengthDocument
extends XmlObject {
    public static final DocumentFactory<MaxLengthDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "maxlengthf8abdoctype");
    public static final SchemaType type = Factory.getType();

    public NumFacet getMaxLength();

    public void setMaxLength(NumFacet var1);

    public NumFacet addNewMaxLength();
}

