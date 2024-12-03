/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.cms.IssuerAndSerialNumber
 *  org.bouncycastle.asn1.cms.KEMRecipientInfo
 *  org.bouncycastle.asn1.cms.RecipientIdentifier
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KEMRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.AuthAttributesProvider;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSecureReadable;
import org.bouncycastle.cms.KEMRecipient;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientOperator;

public class KEMRecipientInformation
extends RecipientInformation {
    private KEMRecipientInfo info;

    KEMRecipientInformation(KEMRecipientInfo info, AlgorithmIdentifier messageAlgorithm, CMSSecureReadable secureReadable, AuthAttributesProvider additionalData) {
        super(info.getKem(), messageAlgorithm, secureReadable, additionalData);
        this.info = info;
        RecipientIdentifier r = info.getRecipientIdentifier();
        if (r.isTagged()) {
            ASN1OctetString octs = ASN1OctetString.getInstance((Object)r.getId());
            this.rid = new KeyTransRecipientId(octs.getOctets());
        } else {
            IssuerAndSerialNumber iAnds = IssuerAndSerialNumber.getInstance((Object)r.getId());
            this.rid = new KeyTransRecipientId(iAnds.getName(), iAnds.getSerialNumber().getValue());
        }
    }

    @Override
    protected RecipientOperator getRecipientOperator(Recipient recipient) throws CMSException {
        return ((KEMRecipient)recipient).getRecipientOperator(new AlgorithmIdentifier(this.keyEncAlg.getAlgorithm(), (ASN1Encodable)this.info), this.messageAlgorithm, this.info.getEncryptedKey().getOctets());
    }
}

