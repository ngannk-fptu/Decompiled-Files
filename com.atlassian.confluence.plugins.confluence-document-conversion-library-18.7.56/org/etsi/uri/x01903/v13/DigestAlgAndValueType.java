/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.w3.x2000.x09.xmldsig.DigestMethodType;
import org.w3.x2000.x09.xmldsig.DigestValueType;

public interface DigestAlgAndValueType
extends XmlObject {
    public static final DocumentFactory<DigestAlgAndValueType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "digestalgandvaluetype234etype");
    public static final SchemaType type = Factory.getType();

    public DigestMethodType getDigestMethod();

    public void setDigestMethod(DigestMethodType var1);

    public DigestMethodType addNewDigestMethod();

    public byte[] getDigestValue();

    public DigestValueType xgetDigestValue();

    public void setDigestValue(byte[] var1);

    public void xsetDigestValue(DigestValueType var1);
}

