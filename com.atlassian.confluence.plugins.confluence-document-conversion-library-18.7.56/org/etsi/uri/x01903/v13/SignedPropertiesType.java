/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.SignedDataObjectPropertiesType;
import org.etsi.uri.x01903.v13.SignedSignaturePropertiesType;

public interface SignedPropertiesType
extends XmlObject {
    public static final DocumentFactory<SignedPropertiesType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "signedpropertiestype163dtype");
    public static final SchemaType type = Factory.getType();

    public SignedSignaturePropertiesType getSignedSignatureProperties();

    public boolean isSetSignedSignatureProperties();

    public void setSignedSignatureProperties(SignedSignaturePropertiesType var1);

    public SignedSignaturePropertiesType addNewSignedSignatureProperties();

    public void unsetSignedSignatureProperties();

    public SignedDataObjectPropertiesType getSignedDataObjectProperties();

    public boolean isSetSignedDataObjectProperties();

    public void setSignedDataObjectProperties(SignedDataObjectPropertiesType var1);

    public SignedDataObjectPropertiesType addNewSignedDataObjectProperties();

    public void unsetSignedDataObjectProperties();

    public String getId();

    public XmlID xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlID var1);

    public void unsetId();
}

