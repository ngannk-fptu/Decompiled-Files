/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.DSA
 *  org.bouncycastle.crypto.Digest
 *  org.bouncycastle.crypto.ExtendedDigest
 *  org.bouncycastle.crypto.params.ECPublicKeyParameters
 *  org.bouncycastle.crypto.signers.DSADigestSigner
 *  org.bouncycastle.crypto.signers.ECDSASigner
 *  org.bouncycastle.oer.Element
 *  org.bouncycastle.oer.OEREncoder
 *  org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate
 *  org.bouncycastle.oer.its.ieee1609dot2.VerificationKeyIndicator
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey
 *  org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.its.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.DSADigestSigner;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.bc.BcITSPublicVerificationKey;
import org.bouncycastle.its.operator.ITSContentVerifierProvider;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.VerificationKeyIndicator;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDefaultDigestProvider;
import org.bouncycastle.util.Arrays;

public class BcITSContentVerifierProvider
implements ITSContentVerifierProvider {
    private final ITSCertificate issuer;
    private final byte[] parentData;
    private final AlgorithmIdentifier digestAlgo;
    private final ECPublicKeyParameters pubParams;
    private final int sigChoice;

    public BcITSContentVerifierProvider(ITSCertificate issuer) throws IOException {
        PublicVerificationKey pvi;
        this.issuer = issuer;
        this.parentData = issuer.getEncoded();
        ToBeSignedCertificate toBeSignedCertificate = issuer.toASN1Structure().getToBeSigned();
        VerificationKeyIndicator vki = toBeSignedCertificate.getVerifyKeyIndicator();
        if (vki.getVerificationKeyIndicator() instanceof PublicVerificationKey) {
            pvi = PublicVerificationKey.getInstance((Object)vki.getVerificationKeyIndicator());
            this.sigChoice = pvi.getChoice();
            switch (pvi.getChoice()) {
                case 0: {
                    this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
                    break;
                }
                case 1: {
                    this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
                    break;
                }
                case 2: {
                    this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384);
                    break;
                }
                default: {
                    throw new IllegalStateException("unknown key type");
                }
            }
        } else {
            throw new IllegalStateException("not public verification key");
        }
        this.pubParams = (ECPublicKeyParameters)new BcITSPublicVerificationKey(pvi).getKey();
    }

    @Override
    public ITSCertificate getAssociatedCertificate() {
        return this.issuer;
    }

    @Override
    public boolean hasAssociatedCertificate() {
        return this.issuer != null;
    }

    @Override
    public ContentVerifier get(int verifierAlgorithmIdentifier) throws OperatorCreationException {
        byte[] parentTBSDigest;
        if (this.sigChoice != verifierAlgorithmIdentifier) {
            throw new OperatorCreationException("wrong verifier for algorithm: " + verifierAlgorithmIdentifier);
        }
        ExtendedDigest digest = BcDefaultDigestProvider.INSTANCE.get(this.digestAlgo);
        byte[] parentDigest = new byte[digest.getDigestSize()];
        digest.update(this.parentData, 0, this.parentData.length);
        digest.doFinal(parentDigest, 0);
        byte[] byArray = parentTBSDigest = this.issuer.getIssuer().isSelf() ? new byte[digest.getDigestSize()] : null;
        if (parentTBSDigest != null) {
            byte[] enc = OEREncoder.toByteArray((ASN1Encodable)this.issuer.toASN1Structure().getToBeSigned(), (Element)IEEE1609dot2.ToBeSignedCertificate.build());
            digest.update(enc, 0, enc.length);
            digest.doFinal(parentTBSDigest, 0);
        }
        final OutputStream os = new OutputStream((Digest)digest){
            final /* synthetic */ Digest val$digest;
            {
                this.val$digest = digest;
            }

            @Override
            public void write(int b) throws IOException {
                this.val$digest.update((byte)b);
            }

            @Override
            public void write(byte[] b) throws IOException {
                this.val$digest.update(b, 0, b.length);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                this.val$digest.update(b, off, len);
            }
        };
        return new ContentVerifier((Digest)digest, parentTBSDigest, parentDigest){
            final DSADigestSigner signer;
            final /* synthetic */ Digest val$digest;
            final /* synthetic */ byte[] val$parentTBSDigest;
            final /* synthetic */ byte[] val$parentDigest;
            {
                this.val$digest = digest;
                this.val$parentTBSDigest = byArray;
                this.val$parentDigest = byArray2;
                this.signer = new DSADigestSigner((DSA)new ECDSASigner(), (Digest)BcDefaultDigestProvider.INSTANCE.get(BcITSContentVerifierProvider.this.digestAlgo));
            }

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return null;
            }

            @Override
            public OutputStream getOutputStream() {
                return os;
            }

            @Override
            public boolean verify(byte[] expected) {
                byte[] clientCertDigest = new byte[this.val$digest.getDigestSize()];
                this.val$digest.doFinal(clientCertDigest, 0);
                this.signer.init(false, (CipherParameters)BcITSContentVerifierProvider.this.pubParams);
                this.signer.update(clientCertDigest, 0, clientCertDigest.length);
                if (this.val$parentTBSDigest != null && Arrays.areEqual((byte[])clientCertDigest, (byte[])this.val$parentTBSDigest)) {
                    byte[] empty = new byte[this.val$digest.getDigestSize()];
                    this.val$digest.doFinal(empty, 0);
                    this.signer.update(empty, 0, empty.length);
                } else {
                    this.signer.update(this.val$parentDigest, 0, this.val$parentDigest.length);
                }
                return this.signer.verifySignature(expected);
            }
        };
    }
}

