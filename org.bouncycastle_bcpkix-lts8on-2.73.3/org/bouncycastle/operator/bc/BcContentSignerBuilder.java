/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.CryptoException
 *  org.bouncycastle.crypto.Signer
 *  org.bouncycastle.crypto.params.AsymmetricKeyParameter
 *  org.bouncycastle.crypto.params.ParametersWithRandom
 */
package org.bouncycastle.operator.bc;

import java.io.OutputStream;
import java.security.SecureRandom;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.RuntimeOperatorException;
import org.bouncycastle.operator.bc.BcDefaultDigestProvider;
import org.bouncycastle.operator.bc.BcDigestProvider;
import org.bouncycastle.operator.bc.BcSignerOutputStream;

public abstract class BcContentSignerBuilder {
    private SecureRandom random;
    private AlgorithmIdentifier sigAlgId;
    private AlgorithmIdentifier digAlgId;
    protected BcDigestProvider digestProvider;

    public BcContentSignerBuilder(AlgorithmIdentifier sigAlgId, AlgorithmIdentifier digAlgId) {
        this.sigAlgId = sigAlgId;
        this.digAlgId = digAlgId;
        this.digestProvider = BcDefaultDigestProvider.INSTANCE;
    }

    public BcContentSignerBuilder setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public ContentSigner build(AsymmetricKeyParameter privateKey) throws OperatorCreationException {
        final Signer sig = this.createSigner(this.sigAlgId, this.digAlgId);
        if (this.random != null) {
            sig.init(true, (CipherParameters)new ParametersWithRandom((CipherParameters)privateKey, this.random));
        } else {
            sig.init(true, (CipherParameters)privateKey);
        }
        return new ContentSigner(){
            private BcSignerOutputStream stream;
            {
                this.stream = new BcSignerOutputStream(sig);
            }

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return BcContentSignerBuilder.this.sigAlgId;
            }

            @Override
            public OutputStream getOutputStream() {
                return this.stream;
            }

            @Override
            public byte[] getSignature() {
                try {
                    return this.stream.getSignature();
                }
                catch (CryptoException e) {
                    throw new RuntimeOperatorException("exception obtaining signature: " + e.getMessage(), e);
                }
            }
        };
    }

    protected abstract Signer createSigner(AlgorithmIdentifier var1, AlgorithmIdentifier var2) throws OperatorCreationException;
}

