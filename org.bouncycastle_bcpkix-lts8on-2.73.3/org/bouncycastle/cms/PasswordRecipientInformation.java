/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.cms.PasswordRecipientInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Integers
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cms.PasswordRecipientInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.AuthAttributesProvider;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSecureReadable;
import org.bouncycastle.cms.PasswordRecipient;
import org.bouncycastle.cms.PasswordRecipientId;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.util.Integers;

public class PasswordRecipientInformation
extends RecipientInformation {
    static Map KEYSIZES = new HashMap();
    static Map BLOCKSIZES = new HashMap();
    private PasswordRecipientInfo info;

    PasswordRecipientInformation(PasswordRecipientInfo info, AlgorithmIdentifier messageAlgorithm, CMSSecureReadable secureReadable, AuthAttributesProvider additionalData) {
        super(info.getKeyEncryptionAlgorithm(), messageAlgorithm, secureReadable, additionalData);
        this.info = info;
        this.rid = new PasswordRecipientId();
    }

    public String getKeyDerivationAlgOID() {
        if (this.info.getKeyDerivationAlgorithm() != null) {
            return this.info.getKeyDerivationAlgorithm().getAlgorithm().getId();
        }
        return null;
    }

    public byte[] getKeyDerivationAlgParams() {
        try {
            ASN1Encodable params;
            if (this.info.getKeyDerivationAlgorithm() != null && (params = this.info.getKeyDerivationAlgorithm().getParameters()) != null) {
                return params.toASN1Primitive().getEncoded();
            }
            return null;
        }
        catch (Exception e) {
            throw new RuntimeException("exception getting encryption parameters " + e);
        }
    }

    public AlgorithmIdentifier getKeyDerivationAlgorithm() {
        return this.info.getKeyDerivationAlgorithm();
    }

    @Override
    protected RecipientOperator getRecipientOperator(Recipient recipient) throws CMSException, IOException {
        PasswordRecipient pbeRecipient = (PasswordRecipient)recipient;
        AlgorithmIdentifier kekAlg = AlgorithmIdentifier.getInstance((Object)this.info.getKeyEncryptionAlgorithm());
        AlgorithmIdentifier kekAlgParams = AlgorithmIdentifier.getInstance((Object)kekAlg.getParameters());
        int keySize = (Integer)KEYSIZES.get(kekAlgParams.getAlgorithm());
        byte[] derivedKey = pbeRecipient.calculateDerivedKey(pbeRecipient.getPasswordConversionScheme(), this.getKeyDerivationAlgorithm(), keySize);
        return pbeRecipient.getRecipientOperator(kekAlgParams, this.messageAlgorithm, derivedKey, this.info.getEncryptedKey().getOctets());
    }

    static {
        BLOCKSIZES.put(CMSAlgorithm.DES_EDE3_CBC, Integers.valueOf((int)8));
        BLOCKSIZES.put(CMSAlgorithm.AES128_CBC, Integers.valueOf((int)16));
        BLOCKSIZES.put(CMSAlgorithm.AES192_CBC, Integers.valueOf((int)16));
        BLOCKSIZES.put(CMSAlgorithm.AES256_CBC, Integers.valueOf((int)16));
        KEYSIZES.put(CMSAlgorithm.DES_EDE3_CBC, Integers.valueOf((int)192));
        KEYSIZES.put(CMSAlgorithm.AES128_CBC, Integers.valueOf((int)128));
        KEYSIZES.put(CMSAlgorithm.AES192_CBC, Integers.valueOf((int)192));
        KEYSIZES.put(CMSAlgorithm.AES256_CBC, Integers.valueOf((int)256));
    }
}

