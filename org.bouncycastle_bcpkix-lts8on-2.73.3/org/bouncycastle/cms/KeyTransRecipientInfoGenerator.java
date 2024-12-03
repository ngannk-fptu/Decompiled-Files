/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.cms.IssuerAndSerialNumber
 *  org.bouncycastle.asn1.cms.KeyTransRecipientInfo
 *  org.bouncycastle.asn1.cms.RecipientIdentifier
 *  org.bouncycastle.asn1.cms.RecipientInfo
 */
package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.operator.AsymmetricKeyWrapper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;

public abstract class KeyTransRecipientInfoGenerator
implements RecipientInfoGenerator {
    protected final AsymmetricKeyWrapper wrapper;
    private IssuerAndSerialNumber issuerAndSerial;
    private byte[] subjectKeyIdentifier;

    protected KeyTransRecipientInfoGenerator(IssuerAndSerialNumber issuerAndSerial, AsymmetricKeyWrapper wrapper) {
        this.issuerAndSerial = issuerAndSerial;
        this.wrapper = wrapper;
    }

    protected KeyTransRecipientInfoGenerator(byte[] subjectKeyIdentifier, AsymmetricKeyWrapper wrapper) {
        this.subjectKeyIdentifier = subjectKeyIdentifier;
        this.wrapper = wrapper;
    }

    @Override
    public final RecipientInfo generate(GenericKey contentEncryptionKey) throws CMSException {
        byte[] encryptedKeyBytes;
        try {
            encryptedKeyBytes = this.wrapper.generateWrappedKey(contentEncryptionKey);
        }
        catch (OperatorException e) {
            throw new CMSException("exception wrapping content key: " + e.getMessage(), e);
        }
        RecipientIdentifier recipId = this.issuerAndSerial != null ? new RecipientIdentifier(this.issuerAndSerial) : new RecipientIdentifier((ASN1OctetString)new DEROctetString(this.subjectKeyIdentifier));
        return new RecipientInfo(new KeyTransRecipientInfo(recipId, this.wrapper.getAlgorithmIdentifier(), (ASN1OctetString)new DEROctetString(encryptedKeyBytes)));
    }
}

