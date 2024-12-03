/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCS12PBEParams
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.jcajce.PKCS12Key
 *  org.bouncycastle.jcajce.io.MacOutputStream
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 */
package org.bouncycastle.pkcs.jcajce;

import java.io.OutputStream;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.PKCS12Key;
import org.bouncycastle.jcajce.io.MacOutputStream;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder;

public class JcePKCS12MacCalculatorBuilder
implements PKCS12MacCalculatorBuilder {
    private JcaJceHelper helper = new DefaultJcaJceHelper();
    private ASN1ObjectIdentifier algorithm;
    private SecureRandom random;
    private int saltLength;
    private int iterationCount = 1024;

    public JcePKCS12MacCalculatorBuilder() {
        this(OIWObjectIdentifiers.idSHA1);
    }

    public JcePKCS12MacCalculatorBuilder(ASN1ObjectIdentifier hashAlgorithm) {
        this.algorithm = hashAlgorithm;
    }

    public JcePKCS12MacCalculatorBuilder setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public JcePKCS12MacCalculatorBuilder setProvider(String providerName) {
        this.helper = new NamedJcaJceHelper(providerName);
        return this;
    }

    public JcePKCS12MacCalculatorBuilder setIterationCount(int iterationCount) {
        this.iterationCount = iterationCount;
        return this;
    }

    @Override
    public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
        return new AlgorithmIdentifier(this.algorithm, (ASN1Encodable)DERNull.INSTANCE);
    }

    @Override
    public MacCalculator build(char[] password) throws OperatorCreationException {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        try {
            final Mac mac = this.helper.createMac(this.algorithm.getId());
            this.saltLength = mac.getMacLength();
            final byte[] salt = new byte[this.saltLength];
            this.random.nextBytes(salt);
            PBEParameterSpec defParams = new PBEParameterSpec(salt, this.iterationCount);
            PKCS12Key key = new PKCS12Key(password);
            mac.init((Key)key, defParams);
            return new MacCalculator((SecretKey)key){
                final /* synthetic */ SecretKey val$key;
                {
                    this.val$key = secretKey;
                }

                @Override
                public AlgorithmIdentifier getAlgorithmIdentifier() {
                    return new AlgorithmIdentifier(JcePKCS12MacCalculatorBuilder.this.algorithm, (ASN1Encodable)new PKCS12PBEParams(salt, JcePKCS12MacCalculatorBuilder.this.iterationCount));
                }

                @Override
                public OutputStream getOutputStream() {
                    return new MacOutputStream(mac);
                }

                @Override
                public byte[] getMac() {
                    return mac.doFinal();
                }

                @Override
                public GenericKey getKey() {
                    return new GenericKey(this.getAlgorithmIdentifier(), this.val$key.getEncoded());
                }
            };
        }
        catch (Exception e) {
            throw new OperatorCreationException("unable to create MAC calculator: " + e.getMessage(), e);
        }
    }
}

