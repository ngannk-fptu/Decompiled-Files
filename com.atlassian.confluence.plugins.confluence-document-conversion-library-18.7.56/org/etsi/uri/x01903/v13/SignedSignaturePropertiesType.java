/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.etsi.uri.x01903.v13.SignatureProductionPlaceType
 */
package org.etsi.uri.x01903.v13;

import java.util.Calendar;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlDateTime;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.CertIDListType;
import org.etsi.uri.x01903.v13.SignaturePolicyIdentifierType;
import org.etsi.uri.x01903.v13.SignatureProductionPlaceType;
import org.etsi.uri.x01903.v13.SignerRoleType;

public interface SignedSignaturePropertiesType
extends XmlObject {
    public static final DocumentFactory<SignedSignaturePropertiesType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "signedsignaturepropertiestype06abtype");
    public static final SchemaType type = Factory.getType();

    public Calendar getSigningTime();

    public XmlDateTime xgetSigningTime();

    public boolean isSetSigningTime();

    public void setSigningTime(Calendar var1);

    public void xsetSigningTime(XmlDateTime var1);

    public void unsetSigningTime();

    public CertIDListType getSigningCertificate();

    public boolean isSetSigningCertificate();

    public void setSigningCertificate(CertIDListType var1);

    public CertIDListType addNewSigningCertificate();

    public void unsetSigningCertificate();

    public SignaturePolicyIdentifierType getSignaturePolicyIdentifier();

    public boolean isSetSignaturePolicyIdentifier();

    public void setSignaturePolicyIdentifier(SignaturePolicyIdentifierType var1);

    public SignaturePolicyIdentifierType addNewSignaturePolicyIdentifier();

    public void unsetSignaturePolicyIdentifier();

    public SignatureProductionPlaceType getSignatureProductionPlace();

    public boolean isSetSignatureProductionPlace();

    public void setSignatureProductionPlace(SignatureProductionPlaceType var1);

    public SignatureProductionPlaceType addNewSignatureProductionPlace();

    public void unsetSignatureProductionPlace();

    public SignerRoleType getSignerRole();

    public boolean isSetSignerRole();

    public void setSignerRole(SignerRoleType var1);

    public SignerRoleType addNewSignerRole();

    public void unsetSignerRole();

    public String getId();

    public XmlID xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlID var1);

    public void unsetId();
}

