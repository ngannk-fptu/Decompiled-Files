/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cms.jcajce;

import java.security.Key;
import java.security.Provider;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KEKRecipient;
import org.bouncycastle.cms.jcajce.DefaultJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.EnvelopedDataHelper;
import org.bouncycastle.cms.jcajce.NamedJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.ProviderJcaJceExtHelper;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;

public abstract class JceKEKRecipient
implements KEKRecipient {
    private SecretKey recipientKey;
    protected EnvelopedDataHelper helper;
    protected EnvelopedDataHelper contentHelper;
    protected boolean validateKeySize;

    public JceKEKRecipient(SecretKey recipientKey) {
        this.contentHelper = this.helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
        this.validateKeySize = false;
        this.recipientKey = recipientKey;
    }

    public JceKEKRecipient setProvider(Provider provider) {
        this.contentHelper = this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }

    public JceKEKRecipient setProvider(String providerName) {
        this.contentHelper = this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(providerName));
        return this;
    }

    public JceKEKRecipient setContentProvider(Provider provider) {
        this.contentHelper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }

    public JceKEKRecipient setContentProvider(String providerName) {
        this.contentHelper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(providerName));
        return this;
    }

    public JceKEKRecipient setKeySizeValidation(boolean doValidate) {
        this.validateKeySize = doValidate;
        return this;
    }

    protected Key extractSecretKey(AlgorithmIdentifier keyEncryptionAlgorithm, AlgorithmIdentifier encryptedKeyAlgorithm, byte[] encryptedContentEncryptionKey) throws CMSException {
        SymmetricKeyUnwrapper unwrapper = this.helper.createSymmetricUnwrapper(keyEncryptionAlgorithm, this.recipientKey);
        try {
            Key key = this.helper.getJceKey(encryptedKeyAlgorithm.getAlgorithm(), unwrapper.generateUnwrappedKey(encryptedKeyAlgorithm, encryptedContentEncryptionKey));
            if (this.validateKeySize) {
                this.helper.keySizeCheck(encryptedKeyAlgorithm, key);
            }
            return key;
        }
        catch (OperatorException e) {
            throw new CMSException("exception unwrapping key: " + e.getMessage(), e);
        }
    }
}

