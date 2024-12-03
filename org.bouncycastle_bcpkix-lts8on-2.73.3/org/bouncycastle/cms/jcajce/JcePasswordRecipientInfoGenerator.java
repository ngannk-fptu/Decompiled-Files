/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cms.jcajce;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.PasswordRecipientInfoGenerator;
import org.bouncycastle.cms.jcajce.DefaultJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.EnvelopedDataHelper;
import org.bouncycastle.cms.jcajce.NamedJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.ProviderJcaJceExtHelper;
import org.bouncycastle.operator.GenericKey;

public class JcePasswordRecipientInfoGenerator
extends PasswordRecipientInfoGenerator {
    private EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());

    public JcePasswordRecipientInfoGenerator(ASN1ObjectIdentifier kekAlgorithm, char[] password) {
        super(kekAlgorithm, password);
    }

    public JcePasswordRecipientInfoGenerator setProvider(Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }

    public JcePasswordRecipientInfoGenerator setProvider(String providerName) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(providerName));
        return this;
    }

    @Override
    protected byte[] calculateDerivedKey(int schemeID, AlgorithmIdentifier derivationAlgorithm, int keySize) throws CMSException {
        return this.helper.calculateDerivedKey(schemeID, this.password, derivationAlgorithm, keySize);
    }

    @Override
    public byte[] generateEncryptedBytes(AlgorithmIdentifier keyEncryptionAlgorithm, byte[] derivedKey, GenericKey contentEncryptionKey) throws CMSException {
        Key contentEncryptionKeySpec = this.helper.getJceKey(contentEncryptionKey);
        Cipher keyEncryptionCipher = this.helper.createRFC3211Wrapper(keyEncryptionAlgorithm.getAlgorithm());
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(ASN1OctetString.getInstance((Object)keyEncryptionAlgorithm.getParameters()).getOctets());
            keyEncryptionCipher.init(3, (Key)new SecretKeySpec(derivedKey, keyEncryptionCipher.getAlgorithm()), ivSpec);
            return keyEncryptionCipher.wrap(contentEncryptionKeySpec);
        }
        catch (GeneralSecurityException e) {
            throw new CMSException("cannot process content encryption key: " + e.getMessage(), e);
        }
    }
}

