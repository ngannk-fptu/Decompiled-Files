/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v14;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.CertificateValuesType;
import org.etsi.uri.x01903.v13.RevocationValuesType;

public interface ValidationDataType
extends XmlObject {
    public static final DocumentFactory<ValidationDataType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "validationdatatype2c11type");
    public static final SchemaType type = Factory.getType();

    public CertificateValuesType getCertificateValues();

    public boolean isSetCertificateValues();

    public void setCertificateValues(CertificateValuesType var1);

    public CertificateValuesType addNewCertificateValues();

    public void unsetCertificateValues();

    public RevocationValuesType getRevocationValues();

    public boolean isSetRevocationValues();

    public void setRevocationValues(RevocationValuesType var1);

    public RevocationValuesType addNewRevocationValues();

    public void unsetRevocationValues();

    public String getId();

    public XmlID xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlID var1);

    public void unsetId();

    public String getURI();

    public XmlAnyURI xgetURI();

    public boolean isSetURI();

    public void setURI(String var1);

    public void xsetURI(XmlAnyURI var1);

    public void unsetURI();
}

