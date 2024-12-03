/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.DigestAlgAndValueType;
import org.w3.x2000.x09.xmldsig.X509IssuerSerialType;

public interface CertIDType
extends XmlObject {
    public static final DocumentFactory<CertIDType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "certidtypee64dtype");
    public static final SchemaType type = Factory.getType();

    public DigestAlgAndValueType getCertDigest();

    public void setCertDigest(DigestAlgAndValueType var1);

    public DigestAlgAndValueType addNewCertDigest();

    public X509IssuerSerialType getIssuerSerial();

    public void setIssuerSerial(X509IssuerSerialType var1);

    public X509IssuerSerialType addNewIssuerSerial();

    public String getURI();

    public XmlAnyURI xgetURI();

    public boolean isSetURI();

    public void setURI(String var1);

    public void xsetURI(XmlAnyURI var1);

    public void unsetURI();
}

