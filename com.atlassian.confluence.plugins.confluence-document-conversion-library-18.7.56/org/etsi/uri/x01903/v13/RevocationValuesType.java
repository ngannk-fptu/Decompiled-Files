/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.etsi.uri.x01903.v13.OtherCertStatusValuesType
 */
package org.etsi.uri.x01903.v13;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.CRLValuesType;
import org.etsi.uri.x01903.v13.OCSPValuesType;
import org.etsi.uri.x01903.v13.OtherCertStatusValuesType;

public interface RevocationValuesType
extends XmlObject {
    public static final DocumentFactory<RevocationValuesType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "revocationvaluestype9a6etype");
    public static final SchemaType type = Factory.getType();

    public CRLValuesType getCRLValues();

    public boolean isSetCRLValues();

    public void setCRLValues(CRLValuesType var1);

    public CRLValuesType addNewCRLValues();

    public void unsetCRLValues();

    public OCSPValuesType getOCSPValues();

    public boolean isSetOCSPValues();

    public void setOCSPValues(OCSPValuesType var1);

    public OCSPValuesType addNewOCSPValues();

    public void unsetOCSPValues();

    public OtherCertStatusValuesType getOtherValues();

    public boolean isSetOtherValues();

    public void setOtherValues(OtherCertStatusValuesType var1);

    public OtherCertStatusValuesType addNewOtherValues();

    public void unsetOtherValues();

    public String getId();

    public XmlID xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlID var1);

    public void unsetId();
}

