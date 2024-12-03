/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 */
package org.bouncycastle.operator.jcajce;

import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.ProviderException;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.AsymmetricKeyUnwrapper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JceGenericKey;
import org.bouncycastle.operator.jcajce.OperatorHelper;

public class JceAsymmetricKeyUnwrapper
extends AsymmetricKeyUnwrapper {
    private OperatorHelper helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
    private Map extraMappings = new HashMap();
    private PrivateKey privKey;
    private boolean unwrappedKeyMustBeEncodable;

    public JceAsymmetricKeyUnwrapper(AlgorithmIdentifier algorithmIdentifier, PrivateKey privKey) {
        super(algorithmIdentifier);
        this.privKey = privKey;
    }

    public JceAsymmetricKeyUnwrapper setProvider(Provider provider) {
        this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }

    public JceAsymmetricKeyUnwrapper setProvider(String providerName) {
        this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(providerName));
        return this;
    }

    public JceAsymmetricKeyUnwrapper setMustProduceEncodableUnwrappedKey(boolean unwrappedKeyMustBeEncodable) {
        this.unwrappedKeyMustBeEncodable = unwrappedKeyMustBeEncodable;
        return this;
    }

    public JceAsymmetricKeyUnwrapper setAlgorithmMapping(ASN1ObjectIdentifier algorithm, String algorithmName) {
        this.extraMappings.put(algorithm, algorithmName);
        return this;
    }

    @Override
    public GenericKey generateUnwrappedKey(AlgorithmIdentifier encryptedKeyAlgorithm, byte[] encryptedKey) throws OperatorException {
        try {
            AlgorithmParameters algParams;
            Cipher keyCipher;
            Key sKey;
            block18: {
                sKey = null;
                keyCipher = this.helper.createAsymmetricWrapper(this.getAlgorithmIdentifier().getAlgorithm(), this.extraMappings);
                algParams = this.helper.createAlgorithmParameters(this.getAlgorithmIdentifier());
                try {
                    if (algParams != null && !this.getAlgorithmIdentifier().getAlgorithm().equals((ASN1Primitive)OIWObjectIdentifiers.elGamalAlgorithm)) {
                        keyCipher.init(4, (Key)this.privKey, algParams);
                    } else {
                        keyCipher.init(4, this.privKey);
                    }
                    sKey = keyCipher.unwrap(encryptedKey, this.helper.getKeyAlgorithmName(encryptedKeyAlgorithm.getAlgorithm()), 3);
                    if (!this.unwrappedKeyMustBeEncodable) break block18;
                    try {
                        byte[] keyBytes = sKey.getEncoded();
                        if (keyBytes == null || keyBytes.length == 0) {
                            sKey = null;
                        }
                    }
                    catch (Exception e) {
                        sKey = null;
                    }
                }
                catch (GeneralSecurityException generalSecurityException) {
                }
                catch (IllegalStateException illegalStateException) {
                }
                catch (UnsupportedOperationException unsupportedOperationException) {
                }
                catch (ProviderException providerException) {
                    // empty catch block
                }
            }
            if (sKey == null) {
                if (algParams != null) {
                    keyCipher.init(2, (Key)this.privKey, algParams);
                } else {
                    keyCipher.init(2, this.privKey);
                }
                sKey = new SecretKeySpec(keyCipher.doFinal(encryptedKey), encryptedKeyAlgorithm.getAlgorithm().getId());
            }
            return new JceGenericKey(encryptedKeyAlgorithm, sKey);
        }
        catch (InvalidKeyException e) {
            throw new OperatorException("key invalid: " + e.getMessage(), e);
        }
        catch (IllegalBlockSizeException e) {
            throw new OperatorException("illegal blocksize: " + e.getMessage(), e);
        }
        catch (BadPaddingException e) {
            throw new OperatorException("bad padding: " + e.getMessage(), e);
        }
        catch (InvalidAlgorithmParameterException e) {
            throw new OperatorException("invalid algorithm parameters: " + e.getMessage(), e);
        }
    }
}

