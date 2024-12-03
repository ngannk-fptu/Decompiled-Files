/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.extendedProperties;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTDigSigBlob
extends XmlObject {
    public static final DocumentFactory<CTDigSigBlob> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdigsigblob73c9type");
    public static final SchemaType type = Factory.getType();

    public byte[] getBlob();

    public XmlBase64Binary xgetBlob();

    public void setBlob(byte[] var1);

    public void xsetBlob(XmlBase64Binary var1);
}

