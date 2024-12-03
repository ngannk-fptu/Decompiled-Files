/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.pkcs.PKCS12PBEParams
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.BufferedBlockCipher
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.ExtendedDigest
 *  org.bouncycastle.crypto.digests.SHA1Digest
 *  org.bouncycastle.crypto.generators.PKCS12ParametersGenerator
 *  org.bouncycastle.crypto.io.CipherInputStream
 *  org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
 */
package org.bouncycastle.pkcs.bc;

import java.io.InputStream;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.bc.PKCS12PBEUtils;

public class BcPKCS12PBEInputDecryptorProviderBuilder {
    private ExtendedDigest digest;

    public BcPKCS12PBEInputDecryptorProviderBuilder() {
        this((ExtendedDigest)new SHA1Digest());
    }

    public BcPKCS12PBEInputDecryptorProviderBuilder(ExtendedDigest digest) {
        this.digest = digest;
    }

    public InputDecryptorProvider build(final char[] password) {
        return new InputDecryptorProvider(){

            @Override
            public InputDecryptor get(final AlgorithmIdentifier algorithmIdentifier) {
                final PaddedBufferedBlockCipher engine = PKCS12PBEUtils.getEngine(algorithmIdentifier.getAlgorithm());
                PKCS12PBEParams pbeParams = PKCS12PBEParams.getInstance((Object)algorithmIdentifier.getParameters());
                CipherParameters params = PKCS12PBEUtils.createCipherParameters(algorithmIdentifier.getAlgorithm(), BcPKCS12PBEInputDecryptorProviderBuilder.this.digest, engine.getBlockSize(), pbeParams, password);
                engine.init(false, params);
                return new InputDecryptor(){

                    @Override
                    public AlgorithmIdentifier getAlgorithmIdentifier() {
                        return algorithmIdentifier;
                    }

                    @Override
                    public InputStream getInputStream(InputStream input) {
                        return new CipherInputStream(input, (BufferedBlockCipher)engine);
                    }

                    public GenericKey getKey() {
                        return new GenericKey(algorithmIdentifier, PKCS12ParametersGenerator.PKCS12PasswordToBytes((char[])password));
                    }
                };
            }
        };
    }
}

