/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.asn1.smime;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.RecipientKeyIdentifier;
import org.bouncycastle.asn1.smime.SMIMEAttributes;

public class SMIMEEncryptionKeyPreferenceAttribute
extends Attribute {
    public SMIMEEncryptionKeyPreferenceAttribute(IssuerAndSerialNumber issAndSer) {
        super(SMIMEAttributes.encrypKeyPref, (ASN1Set)new DERSet((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)issAndSer)));
    }

    public SMIMEEncryptionKeyPreferenceAttribute(RecipientKeyIdentifier rKeyId) {
        super(SMIMEAttributes.encrypKeyPref, (ASN1Set)new DERSet((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)rKeyId)));
    }

    public SMIMEEncryptionKeyPreferenceAttribute(ASN1OctetString sKeyId) {
        super(SMIMEAttributes.encrypKeyPref, (ASN1Set)new DERSet((ASN1Encodable)new DERTaggedObject(false, 2, (ASN1Encodable)sKeyId)));
    }
}

