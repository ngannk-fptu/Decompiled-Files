/*
 * Decompiled with CFR 0.152.
 */
package org.w3.x2000.x09.xmldsig;

import java.math.BigInteger;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface X509IssuerSerialType
extends XmlObject {
    public static final DocumentFactory<X509IssuerSerialType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "x509issuerserialtype7eb2type");
    public static final SchemaType type = Factory.getType();

    public String getX509IssuerName();

    public XmlString xgetX509IssuerName();

    public void setX509IssuerName(String var1);

    public void xsetX509IssuerName(XmlString var1);

    public BigInteger getX509SerialNumber();

    public XmlInteger xgetX509SerialNumber();

    public void setX509SerialNumber(BigInteger var1);

    public void xsetX509SerialNumber(XmlInteger var1);
}

