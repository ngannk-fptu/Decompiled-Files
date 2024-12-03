/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.Signer
 *  org.bouncycastle.crypto.params.AsymmetricKeyParameter
 */
package org.bouncycastle.operator.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDefaultDigestProvider;
import org.bouncycastle.operator.bc.BcDigestProvider;
import org.bouncycastle.operator.bc.BcSignerOutputStream;

public abstract class BcContentVerifierProviderBuilder {
    protected BcDigestProvider digestProvider = BcDefaultDigestProvider.INSTANCE;

    public ContentVerifierProvider build(final X509CertificateHolder certHolder) throws OperatorCreationException {
        return new ContentVerifierProvider(){

            @Override
            public boolean hasAssociatedCertificate() {
                return true;
            }

            @Override
            public X509CertificateHolder getAssociatedCertificate() {
                return certHolder;
            }

            @Override
            public ContentVerifier get(AlgorithmIdentifier algorithm) throws OperatorCreationException {
                try {
                    AsymmetricKeyParameter publicKey = BcContentVerifierProviderBuilder.this.extractKeyParameters(certHolder.getSubjectPublicKeyInfo());
                    BcSignerOutputStream stream = BcContentVerifierProviderBuilder.this.createSignatureStream(algorithm, publicKey);
                    return new SigVerifier(algorithm, stream);
                }
                catch (IOException e) {
                    throw new OperatorCreationException("exception on setup: " + e, e);
                }
            }
        };
    }

    public ContentVerifierProvider build(final AsymmetricKeyParameter publicKey) throws OperatorCreationException {
        return new ContentVerifierProvider(){

            @Override
            public boolean hasAssociatedCertificate() {
                return false;
            }

            @Override
            public X509CertificateHolder getAssociatedCertificate() {
                return null;
            }

            @Override
            public ContentVerifier get(AlgorithmIdentifier algorithm) throws OperatorCreationException {
                BcSignerOutputStream stream = BcContentVerifierProviderBuilder.this.createSignatureStream(algorithm, publicKey);
                return new SigVerifier(algorithm, stream);
            }
        };
    }

    private BcSignerOutputStream createSignatureStream(AlgorithmIdentifier algorithm, AsymmetricKeyParameter publicKey) throws OperatorCreationException {
        Signer sig = this.createSigner(algorithm);
        sig.init(false, (CipherParameters)publicKey);
        return new BcSignerOutputStream(sig);
    }

    protected abstract AsymmetricKeyParameter extractKeyParameters(SubjectPublicKeyInfo var1) throws IOException;

    protected abstract Signer createSigner(AlgorithmIdentifier var1) throws OperatorCreationException;

    private static class SigVerifier
    implements ContentVerifier {
        private BcSignerOutputStream stream;
        private AlgorithmIdentifier algorithm;

        SigVerifier(AlgorithmIdentifier algorithm, BcSignerOutputStream stream) {
            this.algorithm = algorithm;
            this.stream = stream;
        }

        @Override
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithm;
        }

        @Override
        public OutputStream getOutputStream() {
            if (this.stream == null) {
                throw new IllegalStateException("verifier not initialised");
            }
            return this.stream;
        }

        @Override
        public boolean verify(byte[] expected) {
            return this.stream.verify(expected);
        }
    }
}

