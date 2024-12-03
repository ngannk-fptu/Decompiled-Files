/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PBKDF2Params
 *  org.bouncycastle.asn1.pkcs.PBMAC1Params
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.jcajce.io.MacOutputStream
 *  org.bouncycastle.jcajce.spec.PBKDF2KeySpec
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 *  org.bouncycastle.util.BigIntegers
 */
package org.bouncycastle.pkcs.jcajce;

import java.io.OutputStream;
import java.math.BigInteger;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PBMAC1Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.io.MacOutputStream;
import org.bouncycastle.jcajce.spec.PBKDF2KeySpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.DefaultMacAlgorithmIdentifierFinder;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacAlgorithmIdentifierFinder;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.BigIntegers;

public class JcePBMac1CalculatorBuilder {
    public static final AlgorithmIdentifier PRF_SHA224 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA224, (ASN1Encodable)DERNull.INSTANCE);
    public static final AlgorithmIdentifier PRF_SHA256 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, (ASN1Encodable)DERNull.INSTANCE);
    public static final AlgorithmIdentifier PRF_SHA384 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA384, (ASN1Encodable)DERNull.INSTANCE);
    public static final AlgorithmIdentifier PRF_SHA512 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, (ASN1Encodable)DERNull.INSTANCE);
    public static final AlgorithmIdentifier PRF_SHA3_224 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_224);
    public static final AlgorithmIdentifier PRF_SHA3_256 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_256);
    public static final AlgorithmIdentifier PRF_SHA3_384 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_384);
    public static final AlgorithmIdentifier PRF_SHA3_512 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_512);
    private static final DefaultMacAlgorithmIdentifierFinder defaultFinder = new DefaultMacAlgorithmIdentifierFinder();
    private JcaJceHelper helper = new DefaultJcaJceHelper();
    private AlgorithmIdentifier macAlgorithm;
    private SecureRandom random;
    private int saltLength = -1;
    private int iterationCount = 8192;
    private int keySize;
    private PBKDF2Params pbeParams = null;
    private AlgorithmIdentifier prf = PRF_SHA256;
    private byte[] salt = null;

    public JcePBMac1CalculatorBuilder(String macAlgorithm, int keySize) {
        this(macAlgorithm, keySize, defaultFinder);
    }

    public JcePBMac1CalculatorBuilder(String macAlgorithm, int keySize, MacAlgorithmIdentifierFinder algIdFinder) {
        this.macAlgorithm = algIdFinder.find(macAlgorithm);
        this.keySize = keySize;
    }

    public JcePBMac1CalculatorBuilder(PBMAC1Params pbeMacParams) {
        this.macAlgorithm = pbeMacParams.getMessageAuthScheme();
        this.pbeParams = PBKDF2Params.getInstance((Object)pbeMacParams.getKeyDerivationFunc().getParameters());
    }

    public JcePBMac1CalculatorBuilder setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public JcePBMac1CalculatorBuilder setProvider(String providerName) {
        this.helper = new NamedJcaJceHelper(providerName);
        return this;
    }

    JcePBMac1CalculatorBuilder setHelper(JcaJceHelper helper) {
        this.helper = helper;
        return this;
    }

    public JcePBMac1CalculatorBuilder setIterationCount(int iterationCount) {
        this.iterationCount = iterationCount;
        return this;
    }

    public JcePBMac1CalculatorBuilder setSaltLength(int saltLength) {
        this.saltLength = saltLength;
        return this;
    }

    public JcePBMac1CalculatorBuilder setSalt(byte[] salt) {
        this.salt = salt;
        return this;
    }

    public JcePBMac1CalculatorBuilder setRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public JcePBMac1CalculatorBuilder setPrf(AlgorithmIdentifier prf) {
        this.prf = prf;
        return this;
    }

    public MacCalculator build(char[] password) throws OperatorCreationException {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        try {
            final Mac mac = this.helper.createMac(this.macAlgorithm.getAlgorithm().getId());
            if (this.pbeParams == null) {
                if (this.salt == null) {
                    if (this.saltLength < 0) {
                        this.saltLength = mac.getMacLength();
                    }
                    this.salt = new byte[this.saltLength];
                    this.random.nextBytes(this.salt);
                }
            } else {
                this.salt = this.pbeParams.getSalt();
                this.iterationCount = BigIntegers.intValueExact((BigInteger)this.pbeParams.getIterationCount());
                this.keySize = BigIntegers.intValueExact((BigInteger)this.pbeParams.getKeyLength()) * 8;
            }
            SecretKeyFactory secFact = this.helper.createSecretKeyFactory("PBKDF2");
            final SecretKey key = secFact.generateSecret((KeySpec)new PBKDF2KeySpec(password, this.salt, this.iterationCount, this.keySize, this.prf));
            mac.init(key);
            return new MacCalculator(){

                @Override
                public AlgorithmIdentifier getAlgorithmIdentifier() {
                    return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBMAC1, (ASN1Encodable)new PBMAC1Params(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, (ASN1Encodable)new PBKDF2Params(JcePBMac1CalculatorBuilder.this.salt, JcePBMac1CalculatorBuilder.this.iterationCount, (JcePBMac1CalculatorBuilder.this.keySize + 7) / 8, JcePBMac1CalculatorBuilder.this.prf)), JcePBMac1CalculatorBuilder.this.macAlgorithm));
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
                    return new GenericKey(this.getAlgorithmIdentifier(), key.getEncoded());
                }
            };
        }
        catch (Exception e) {
            throw new OperatorCreationException("unable to create MAC calculator: " + e.getMessage(), e);
        }
    }
}

