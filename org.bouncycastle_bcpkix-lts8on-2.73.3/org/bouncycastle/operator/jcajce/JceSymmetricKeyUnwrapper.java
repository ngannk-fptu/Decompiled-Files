/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 */
package org.bouncycastle.operator.jcajce;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceGenericKey;
import org.bouncycastle.operator.jcajce.OperatorHelper;

public class JceSymmetricKeyUnwrapper
extends SymmetricKeyUnwrapper {
    private OperatorHelper helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
    private SecretKey secretKey;

    public JceSymmetricKeyUnwrapper(AlgorithmIdentifier algorithmIdentifier, SecretKey secretKey) {
        super(algorithmIdentifier);
        this.secretKey = secretKey;
    }

    public JceSymmetricKeyUnwrapper setProvider(Provider provider) {
        this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }

    public JceSymmetricKeyUnwrapper setProvider(String providerName) {
        this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(providerName));
        return this;
    }

    @Override
    public GenericKey generateUnwrappedKey(AlgorithmIdentifier encryptedKeyAlgorithm, byte[] encryptedKey) throws OperatorException {
        try {
            Cipher keyCipher = this.helper.createSymmetricWrapper(this.getAlgorithmIdentifier().getAlgorithm());
            keyCipher.init(4, this.secretKey);
            return new JceGenericKey(encryptedKeyAlgorithm, keyCipher.unwrap(encryptedKey, this.helper.getKeyAlgorithmName(encryptedKeyAlgorithm.getAlgorithm()), 3));
        }
        catch (InvalidKeyException e) {
            throw new OperatorException("key invalid in message.", e);
        }
        catch (NoSuchAlgorithmException e) {
            throw new OperatorException("can't find algorithm.", e);
        }
    }
}

