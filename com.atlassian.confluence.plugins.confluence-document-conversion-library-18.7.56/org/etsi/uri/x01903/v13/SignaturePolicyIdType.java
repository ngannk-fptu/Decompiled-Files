/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3.x2000.x09.xmldsig.TransformsType
 */
package org.etsi.uri.x01903.v13;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.DigestAlgAndValueType;
import org.etsi.uri.x01903.v13.ObjectIdentifierType;
import org.etsi.uri.x01903.v13.SigPolicyQualifiersListType;
import org.w3.x2000.x09.xmldsig.TransformsType;

public interface SignaturePolicyIdType
extends XmlObject {
    public static final DocumentFactory<SignaturePolicyIdType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "signaturepolicyidtype0ca1type");
    public static final SchemaType type = Factory.getType();

    public ObjectIdentifierType getSigPolicyId();

    public void setSigPolicyId(ObjectIdentifierType var1);

    public ObjectIdentifierType addNewSigPolicyId();

    public TransformsType getTransforms();

    public boolean isSetTransforms();

    public void setTransforms(TransformsType var1);

    public TransformsType addNewTransforms();

    public void unsetTransforms();

    public DigestAlgAndValueType getSigPolicyHash();

    public void setSigPolicyHash(DigestAlgAndValueType var1);

    public DigestAlgAndValueType addNewSigPolicyHash();

    public SigPolicyQualifiersListType getSigPolicyQualifiers();

    public boolean isSetSigPolicyQualifiers();

    public void setSigPolicyQualifiers(SigPolicyQualifiersListType var1);

    public SigPolicyQualifiersListType addNewSigPolicyQualifiers();

    public void unsetSigPolicyQualifiers();
}

