/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.cms.IssuerAndSerialNumber
 *  org.bouncycastle.asn1.cms.KeyAgreeRecipientIdentifier
 *  org.bouncycastle.asn1.cms.KeyAgreeRecipientInfo
 *  org.bouncycastle.asn1.cms.OriginatorIdentifierOrKey
 *  org.bouncycastle.asn1.cms.OriginatorPublicKey
 *  org.bouncycastle.asn1.cms.RecipientEncryptedKey
 *  org.bouncycastle.asn1.cms.RecipientKeyIdentifier
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.SubjectKeyIdentifier
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.util.List;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientIdentifier;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientInfo;
import org.bouncycastle.asn1.cms.OriginatorIdentifierOrKey;
import org.bouncycastle.asn1.cms.OriginatorPublicKey;
import org.bouncycastle.asn1.cms.RecipientEncryptedKey;
import org.bouncycastle.asn1.cms.RecipientKeyIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cms.AuthAttributesProvider;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSecureReadable;
import org.bouncycastle.cms.KeyAgreeRecipient;
import org.bouncycastle.cms.KeyAgreeRecipientId;
import org.bouncycastle.cms.OriginatorId;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.util.Arrays;

public class KeyAgreeRecipientInformation
extends RecipientInformation {
    private KeyAgreeRecipientInfo info;
    private ASN1OctetString encryptedKey;

    static void readRecipientInfo(List infos, KeyAgreeRecipientInfo info, AlgorithmIdentifier messageAlgorithm, CMSSecureReadable secureReadable, AuthAttributesProvider additionalData) {
        ASN1Sequence s = info.getRecipientEncryptedKeys();
        for (int i = 0; i < s.size(); ++i) {
            KeyAgreeRecipientId rid;
            RecipientEncryptedKey id = RecipientEncryptedKey.getInstance((Object)s.getObjectAt(i));
            KeyAgreeRecipientIdentifier karid = id.getIdentifier();
            IssuerAndSerialNumber iAndSN = karid.getIssuerAndSerialNumber();
            if (iAndSN != null) {
                rid = new KeyAgreeRecipientId(iAndSN.getName(), iAndSN.getSerialNumber().getValue());
            } else {
                RecipientKeyIdentifier rKeyID = karid.getRKeyID();
                rid = new KeyAgreeRecipientId(rKeyID.getSubjectKeyIdentifier().getOctets());
            }
            infos.add(new KeyAgreeRecipientInformation(info, rid, id.getEncryptedKey(), messageAlgorithm, secureReadable, additionalData));
        }
    }

    KeyAgreeRecipientInformation(KeyAgreeRecipientInfo info, RecipientId rid, ASN1OctetString encryptedKey, AlgorithmIdentifier messageAlgorithm, CMSSecureReadable secureReadable, AuthAttributesProvider additionalData) {
        super(info.getKeyEncryptionAlgorithm(), messageAlgorithm, secureReadable, additionalData);
        this.info = info;
        this.rid = rid;
        this.encryptedKey = encryptedKey;
    }

    public OriginatorIdentifierOrKey getOriginator() {
        return this.info.getOriginator();
    }

    public byte[] getUserKeyingMaterial() {
        ASN1OctetString ukm = this.info.getUserKeyingMaterial();
        if (ukm != null) {
            return Arrays.clone((byte[])ukm.getOctets());
        }
        return null;
    }

    private SubjectPublicKeyInfo getSenderPublicKeyInfo(AlgorithmIdentifier recKeyAlgId, OriginatorIdentifierOrKey originator) throws CMSException, IOException {
        OriginatorId origID;
        OriginatorPublicKey opk = originator.getOriginatorKey();
        if (opk != null) {
            return this.getPublicKeyInfoFromOriginatorPublicKey(recKeyAlgId, opk);
        }
        IssuerAndSerialNumber iAndSN = originator.getIssuerAndSerialNumber();
        if (iAndSN != null) {
            origID = new OriginatorId(iAndSN.getName(), iAndSN.getSerialNumber().getValue());
        } else {
            SubjectKeyIdentifier ski = originator.getSubjectKeyIdentifier();
            origID = new OriginatorId(ski.getKeyIdentifier());
        }
        return this.getPublicKeyInfoFromOriginatorId(origID);
    }

    private SubjectPublicKeyInfo getPublicKeyInfoFromOriginatorPublicKey(AlgorithmIdentifier recKeyAlgId, OriginatorPublicKey originatorPublicKey) {
        return new SubjectPublicKeyInfo(recKeyAlgId, originatorPublicKey.getPublicKeyData());
    }

    private SubjectPublicKeyInfo getPublicKeyInfoFromOriginatorId(OriginatorId origID) throws CMSException {
        throw new CMSException("No support for 'originator' as IssuerAndSerialNumber or SubjectKeyIdentifier");
    }

    @Override
    protected RecipientOperator getRecipientOperator(Recipient recipient) throws CMSException, IOException {
        KeyAgreeRecipient agreeRecipient = (KeyAgreeRecipient)recipient;
        AlgorithmIdentifier recKeyAlgId = agreeRecipient.getPrivateKeyAlgorithmIdentifier();
        return ((KeyAgreeRecipient)recipient).getRecipientOperator(this.keyEncAlg, this.messageAlgorithm, this.getSenderPublicKeyInfo(recKeyAlgId, this.info.getOriginator()), this.info.getUserKeyingMaterial(), this.encryptedKey.getOctets());
    }
}

