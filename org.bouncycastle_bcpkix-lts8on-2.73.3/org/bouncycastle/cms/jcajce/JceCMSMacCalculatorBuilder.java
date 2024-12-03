/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.jcajce.io.MacOutputStream
 */
package org.bouncycastle.cms.jcajce;

import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.jcajce.DefaultJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.EnvelopedDataHelper;
import org.bouncycastle.cms.jcajce.NamedJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.ProviderJcaJceExtHelper;
import org.bouncycastle.jcajce.io.MacOutputStream;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.jcajce.JceGenericKey;

public class JceCMSMacCalculatorBuilder {
    private final ASN1ObjectIdentifier macOID;
    private final int keySize;
    private EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    private AlgorithmParameters algorithmParameters;
    private SecureRandom random;

    public JceCMSMacCalculatorBuilder(ASN1ObjectIdentifier macOID) {
        this(macOID, -1);
    }

    public JceCMSMacCalculatorBuilder(ASN1ObjectIdentifier macOID, int keySize) {
        this.macOID = macOID;
        this.keySize = keySize;
    }

    public JceCMSMacCalculatorBuilder setProvider(Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }

    public JceCMSMacCalculatorBuilder setProvider(String providerName) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(providerName));
        return this;
    }

    public JceCMSMacCalculatorBuilder setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public JceCMSMacCalculatorBuilder setAlgorithmParameters(AlgorithmParameters algorithmParameters) {
        this.algorithmParameters = algorithmParameters;
        return this;
    }

    public MacCalculator build() throws CMSException {
        return new CMSMacCalculator(this.macOID, this.keySize, this.algorithmParameters, this.random);
    }

    private class CMSMacCalculator
    implements MacCalculator {
        private SecretKey encKey;
        private AlgorithmIdentifier algorithmIdentifier;
        private Mac mac;

        CMSMacCalculator(ASN1ObjectIdentifier macOID, int keySize, AlgorithmParameters params, SecureRandom random) throws CMSException {
            KeyGenerator keyGen = JceCMSMacCalculatorBuilder.this.helper.createKeyGenerator(macOID);
            if (random == null) {
                random = new SecureRandom();
            }
            if (keySize < 0) {
                keyGen.init(random);
            } else {
                keyGen.init(keySize, random);
            }
            this.encKey = keyGen.generateKey();
            if (params == null) {
                params = JceCMSMacCalculatorBuilder.this.helper.generateParameters(macOID, this.encKey, random);
            }
            this.algorithmIdentifier = JceCMSMacCalculatorBuilder.this.helper.getAlgorithmIdentifier(macOID, params);
            this.mac = JceCMSMacCalculatorBuilder.this.helper.createContentMac(this.encKey, this.algorithmIdentifier);
        }

        @Override
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithmIdentifier;
        }

        @Override
        public OutputStream getOutputStream() {
            return new MacOutputStream(this.mac);
        }

        @Override
        public byte[] getMac() {
            return this.mac.doFinal();
        }

        @Override
        public GenericKey getKey() {
            return new JceGenericKey(this.algorithmIdentifier, this.encKey);
        }
    }
}

