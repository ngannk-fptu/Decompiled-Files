/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.pkcs.PKCS12PBEParams
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.BlockCipher
 *  org.bouncycastle.crypto.BufferedBlockCipher
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.ExtendedDigest
 *  org.bouncycastle.crypto.digests.SHA1Digest
 *  org.bouncycastle.crypto.generators.PKCS12ParametersGenerator
 *  org.bouncycastle.crypto.io.CipherOutputStream
 *  org.bouncycastle.crypto.paddings.BlockCipherPadding
 *  org.bouncycastle.crypto.paddings.PKCS7Padding
 *  org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
 */
package org.bouncycastle.pkcs.bc;

import java.io.OutputStream;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.bc.PKCS12PBEUtils;

public class BcPKCS12PBEOutputEncryptorBuilder {
    private ExtendedDigest digest;
    private BufferedBlockCipher engine;
    private ASN1ObjectIdentifier algorithm;
    private SecureRandom random;
    private int iterationCount = 1024;

    public BcPKCS12PBEOutputEncryptorBuilder(ASN1ObjectIdentifier algorithm, BlockCipher engine) {
        this(algorithm, engine, (ExtendedDigest)new SHA1Digest());
    }

    public BcPKCS12PBEOutputEncryptorBuilder(ASN1ObjectIdentifier algorithm, BlockCipher engine, ExtendedDigest pbeDigest) {
        this.algorithm = algorithm;
        this.engine = new PaddedBufferedBlockCipher(engine, (BlockCipherPadding)new PKCS7Padding());
        this.digest = pbeDigest;
    }

    public BcPKCS12PBEOutputEncryptorBuilder setIterationCount(int iterationCount) {
        this.iterationCount = iterationCount;
        return this;
    }

    public OutputEncryptor build(final char[] password) {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        byte[] salt = new byte[20];
        this.random.nextBytes(salt);
        final PKCS12PBEParams pbeParams = new PKCS12PBEParams(salt, this.iterationCount);
        CipherParameters params = PKCS12PBEUtils.createCipherParameters(this.algorithm, this.digest, this.engine.getBlockSize(), pbeParams, password);
        this.engine.init(true, params);
        return new OutputEncryptor(){

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return new AlgorithmIdentifier(BcPKCS12PBEOutputEncryptorBuilder.this.algorithm, (ASN1Encodable)pbeParams);
            }

            @Override
            public OutputStream getOutputStream(OutputStream out) {
                return new CipherOutputStream(out, BcPKCS12PBEOutputEncryptorBuilder.this.engine);
            }

            @Override
            public GenericKey getKey() {
                return new GenericKey(new AlgorithmIdentifier(BcPKCS12PBEOutputEncryptorBuilder.this.algorithm, (ASN1Encodable)pbeParams), PKCS12ParametersGenerator.PKCS12PasswordToBytes((char[])password));
            }
        };
    }
}

