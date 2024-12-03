/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.cryptopro.GOST28147Parameters
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.jcajce.io.CipherInputStream
 *  org.bouncycastle.jcajce.spec.GOST28147ParameterSpec
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.operator.jcajce;

import java.io.InputStream;
import java.security.Key;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cryptopro.GOST28147Parameters;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;

public class JceInputDecryptorProviderBuilder {
    private JcaJceHelper helper = new DefaultJcaJceHelper();

    public JceInputDecryptorProviderBuilder setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public JceInputDecryptorProviderBuilder setProvider(String providerName) {
        this.helper = new NamedJcaJceHelper(providerName);
        return this;
    }

    public InputDecryptorProvider build(byte[] keyBytes) {
        final byte[] encKeyBytes = Arrays.clone((byte[])keyBytes);
        return new InputDecryptorProvider(){
            private Cipher cipher;
            private AlgorithmIdentifier encryptionAlg;

            @Override
            public InputDecryptor get(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
                this.encryptionAlg = algorithmIdentifier;
                ASN1ObjectIdentifier algorithm = algorithmIdentifier.getAlgorithm();
                try {
                    this.cipher = JceInputDecryptorProviderBuilder.this.helper.createCipher(algorithm.getId());
                    SecretKeySpec key = new SecretKeySpec(encKeyBytes, algorithm.getId());
                    ASN1Encodable encParams = algorithmIdentifier.getParameters();
                    if (encParams instanceof ASN1OctetString) {
                        this.cipher.init(2, (Key)key, new IvParameterSpec(ASN1OctetString.getInstance((Object)encParams).getOctets()));
                    } else {
                        GOST28147Parameters gParams = GOST28147Parameters.getInstance((Object)encParams);
                        this.cipher.init(2, (Key)key, (AlgorithmParameterSpec)new GOST28147ParameterSpec(gParams.getEncryptionParamSet(), gParams.getIV()));
                    }
                }
                catch (Exception e) {
                    throw new OperatorCreationException("unable to create InputDecryptor: " + e.getMessage(), e);
                }
                return new InputDecryptor(){

                    @Override
                    public AlgorithmIdentifier getAlgorithmIdentifier() {
                        return encryptionAlg;
                    }

                    @Override
                    public InputStream getInputStream(InputStream input) {
                        return new CipherInputStream(input, cipher);
                    }
                };
            }
        };
    }
}

