/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1BitString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.misc.MiscObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.jcajce.CompositePublicKey
 *  org.bouncycastle.jcajce.io.OutputStreamFactory
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 *  org.bouncycastle.util.io.TeeOutputStream
 */
package org.bouncycastle.operator.jcajce;

import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jcajce.CompositePublicKey;
import org.bouncycastle.jcajce.io.OutputStreamFactory;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.RawContentVerifier;
import org.bouncycastle.operator.RuntimeOperatorException;
import org.bouncycastle.operator.jcajce.OperatorHelper;
import org.bouncycastle.util.io.TeeOutputStream;

public class JcaContentVerifierProviderBuilder {
    private OperatorHelper helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());

    public JcaContentVerifierProviderBuilder setProvider(Provider provider) {
        this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }

    public JcaContentVerifierProviderBuilder setProvider(String providerName) {
        this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(providerName));
        return this;
    }

    public ContentVerifierProvider build(X509CertificateHolder certHolder) throws OperatorCreationException, CertificateException {
        return this.build(this.helper.convertCertificate(certHolder));
    }

    public ContentVerifierProvider build(final X509Certificate certificate) throws OperatorCreationException {
        JcaX509CertificateHolder certHolder;
        try {
            certHolder = new JcaX509CertificateHolder(certificate);
        }
        catch (CertificateEncodingException e) {
            throw new OperatorCreationException("cannot process certificate: " + e.getMessage(), e);
        }
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
                Signature sig;
                if (algorithm.getAlgorithm().equals((ASN1Primitive)MiscObjectIdentifiers.id_alg_composite)) {
                    return JcaContentVerifierProviderBuilder.this.createCompositeVerifier(algorithm, certificate.getPublicKey());
                }
                try {
                    sig = JcaContentVerifierProviderBuilder.this.helper.createSignature(algorithm);
                    sig.initVerify(certificate.getPublicKey());
                }
                catch (GeneralSecurityException e) {
                    throw new OperatorCreationException("exception on setup: " + e, e);
                }
                Signature rawSig = JcaContentVerifierProviderBuilder.this.createRawSig(algorithm, certificate.getPublicKey());
                if (rawSig != null) {
                    return new RawSigVerifier(algorithm, sig, rawSig);
                }
                return new SigVerifier(algorithm, sig);
            }
        };
    }

    public ContentVerifierProvider build(final PublicKey publicKey) throws OperatorCreationException {
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
                if (algorithm.getAlgorithm().equals((ASN1Primitive)MiscObjectIdentifiers.id_alg_composite)) {
                    return JcaContentVerifierProviderBuilder.this.createCompositeVerifier(algorithm, publicKey);
                }
                if (publicKey instanceof CompositePublicKey) {
                    List keys = ((CompositePublicKey)publicKey).getPublicKeys();
                    for (int i = 0; i != keys.size(); ++i) {
                        try {
                            Signature sig = JcaContentVerifierProviderBuilder.this.createSignature(algorithm, (PublicKey)keys.get(i));
                            Signature rawSig = JcaContentVerifierProviderBuilder.this.createRawSig(algorithm, (PublicKey)keys.get(i));
                            if (rawSig != null) {
                                return new RawSigVerifier(algorithm, sig, rawSig);
                            }
                            return new SigVerifier(algorithm, sig);
                        }
                        catch (OperatorCreationException operatorCreationException) {
                            continue;
                        }
                    }
                    throw new OperatorCreationException("no matching algorithm found for key");
                }
                Signature sig = JcaContentVerifierProviderBuilder.this.createSignature(algorithm, publicKey);
                Signature rawSig = JcaContentVerifierProviderBuilder.this.createRawSig(algorithm, publicKey);
                if (rawSig != null) {
                    return new RawSigVerifier(algorithm, sig, rawSig);
                }
                return new SigVerifier(algorithm, sig);
            }
        };
    }

    public ContentVerifierProvider build(SubjectPublicKeyInfo publicKey) throws OperatorCreationException {
        return this.build(this.helper.convertPublicKey(publicKey));
    }

    private ContentVerifier createCompositeVerifier(AlgorithmIdentifier compAlgId, PublicKey publicKey) throws OperatorCreationException {
        if (publicKey instanceof CompositePublicKey) {
            List pubKeys = ((CompositePublicKey)publicKey).getPublicKeys();
            ASN1Sequence keySeq = ASN1Sequence.getInstance((Object)compAlgId.getParameters());
            Signature[] sigs = new Signature[keySeq.size()];
            for (int i = 0; i != keySeq.size(); ++i) {
                AlgorithmIdentifier sigAlg = AlgorithmIdentifier.getInstance((Object)keySeq.getObjectAt(i));
                sigs[i] = pubKeys.get(i) != null ? this.createSignature(sigAlg, (PublicKey)pubKeys.get(i)) : null;
            }
            return new CompositeVerifier(sigs);
        }
        ASN1Sequence keySeq = ASN1Sequence.getInstance((Object)compAlgId.getParameters());
        Signature[] sigs = new Signature[keySeq.size()];
        for (int i = 0; i != keySeq.size(); ++i) {
            AlgorithmIdentifier sigAlg = AlgorithmIdentifier.getInstance((Object)keySeq.getObjectAt(i));
            try {
                sigs[i] = this.createSignature(sigAlg, publicKey);
                continue;
            }
            catch (Exception e) {
                sigs[i] = null;
            }
        }
        return new CompositeVerifier(sigs);
    }

    private Signature createSignature(AlgorithmIdentifier algorithm, PublicKey publicKey) throws OperatorCreationException {
        try {
            Signature sig = this.helper.createSignature(algorithm);
            sig.initVerify(publicKey);
            return sig;
        }
        catch (GeneralSecurityException e) {
            throw new OperatorCreationException("exception on setup: " + e, e);
        }
    }

    private Signature createRawSig(AlgorithmIdentifier algorithm, PublicKey publicKey) {
        Signature rawSig;
        try {
            rawSig = this.helper.createRawSignature(algorithm);
            if (rawSig != null) {
                rawSig.initVerify(publicKey);
            }
        }
        catch (Exception e) {
            rawSig = null;
        }
        return rawSig;
    }

    private static class CompositeVerifier
    implements ContentVerifier {
        private Signature[] sigs;
        private OutputStream stream;

        public CompositeVerifier(Signature[] sigs) throws OperatorCreationException {
            int start;
            this.sigs = sigs;
            for (start = 0; start < sigs.length && sigs[start] == null; ++start) {
            }
            if (start == sigs.length) {
                throw new OperatorCreationException("no matching signature found in composite");
            }
            this.stream = OutputStreamFactory.createStream((Signature)sigs[start]);
            for (int i = start + 1; i != sigs.length; ++i) {
                if (sigs[i] == null) continue;
                this.stream = new TeeOutputStream(this.stream, OutputStreamFactory.createStream((Signature)sigs[i]));
            }
        }

        @Override
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return new AlgorithmIdentifier(MiscObjectIdentifiers.id_alg_composite);
        }

        @Override
        public OutputStream getOutputStream() {
            return this.stream;
        }

        @Override
        public boolean verify(byte[] expected) {
            try {
                ASN1Sequence sigSeq = ASN1Sequence.getInstance((Object)expected);
                boolean failed = false;
                for (int i = 0; i != sigSeq.size(); ++i) {
                    if (this.sigs[i] == null || this.sigs[i].verify(ASN1BitString.getInstance((Object)sigSeq.getObjectAt(i)).getBytes())) continue;
                    failed = true;
                }
                return !failed;
            }
            catch (SignatureException e) {
                throw new RuntimeOperatorException("exception obtaining signature: " + e.getMessage(), e);
            }
        }
    }

    private static class RawSigVerifier
    extends SigVerifier
    implements RawContentVerifier {
        private Signature rawSignature;

        RawSigVerifier(AlgorithmIdentifier algorithm, Signature standardSig, Signature rawSignature) {
            super(algorithm, standardSig);
            this.rawSignature = rawSignature;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean verify(byte[] expected) {
            try {
                boolean bl = super.verify(expected);
                return bl;
            }
            finally {
                try {
                    this.rawSignature.verify(expected);
                }
                catch (Exception exception) {}
            }
        }

        @Override
        public boolean verify(byte[] digest, byte[] expected) {
            try {
                this.rawSignature.update(digest);
                boolean bl = this.rawSignature.verify(expected);
                return bl;
            }
            catch (SignatureException e) {
                throw new RuntimeOperatorException("exception obtaining raw signature: " + e.getMessage(), e);
            }
            finally {
                try {
                    this.rawSignature.verify(expected);
                }
                catch (Exception exception) {}
            }
        }
    }

    private static class SigVerifier
    implements ContentVerifier {
        private final AlgorithmIdentifier algorithm;
        private final Signature signature;
        protected final OutputStream stream;

        SigVerifier(AlgorithmIdentifier algorithm, Signature signature) {
            this.algorithm = algorithm;
            this.signature = signature;
            this.stream = OutputStreamFactory.createStream((Signature)signature);
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
            try {
                return this.signature.verify(expected);
            }
            catch (SignatureException e) {
                throw new RuntimeOperatorException("exception obtaining signature: " + e.getMessage(), e);
            }
        }
    }
}

