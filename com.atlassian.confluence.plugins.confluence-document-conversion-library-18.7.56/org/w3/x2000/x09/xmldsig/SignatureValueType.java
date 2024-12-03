/*
 * Decompiled with CFR 0.152.
 */
package org.w3.x2000.x09.xmldsig;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface SignatureValueType
extends XmlBase64Binary {
    public static final DocumentFactory<SignatureValueType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "signaturevaluetype58cctype");
    public static final SchemaType type = Factory.getType();

    public String getId();

    public XmlID xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlID var1);

    public void unsetId();
}

