/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.cms.KEKIdentifier
 *  org.bouncycastle.asn1.cms.KEKRecipientInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cms;

import java.io.IOException;
import org.bouncycastle.asn1.cms.KEKIdentifier;
import org.bouncycastle.asn1.cms.KEKRecipientInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.AuthAttributesProvider;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSecureReadable;
import org.bouncycastle.cms.KEKRecipient;
import org.bouncycastle.cms.KEKRecipientId;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientOperator;

public class KEKRecipientInformation
extends RecipientInformation {
    private KEKRecipientInfo info;

    KEKRecipientInformation(KEKRecipientInfo info, AlgorithmIdentifier messageAlgorithm, CMSSecureReadable secureReadable, AuthAttributesProvider additionalData) {
        super(info.getKeyEncryptionAlgorithm(), messageAlgorithm, secureReadable, additionalData);
        this.info = info;
        KEKIdentifier kekId = info.getKekid();
        this.rid = new KEKRecipientId(kekId.getKeyIdentifier().getOctets());
    }

    @Override
    protected RecipientOperator getRecipientOperator(Recipient recipient) throws CMSException, IOException {
        return ((KEKRecipient)recipient).getRecipientOperator(this.keyEncAlg, this.messageAlgorithm, this.info.getEncryptedKey().getOctets());
    }
}

