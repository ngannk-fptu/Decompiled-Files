/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 *  org.bouncycastle.oer.Element
 *  org.bouncycastle.oer.OEREncoder
 *  org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate
 *  org.bouncycastle.oer.its.ieee1609dot2.VerificationKeyIndicator
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey
 *  org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.its.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Provider;
import java.security.Signature;
import java.security.interfaces.ECPublicKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.ITSPublicVerificationKey;
import org.bouncycastle.its.jcajce.JcaITSPublicVerificationKey;
import org.bouncycastle.its.operator.ITSContentVerifierProvider;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.VerificationKeyIndicator;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Arrays;

public class JcaITSContentVerifierProvider
implements ITSContentVerifierProvider {
    private final ITSCertificate issuer;
    private final byte[] parentData;
    private final JcaJceHelper helper;
    private AlgorithmIdentifier digestAlgo;
    private ECPublicKey pubParams;
    private int sigChoice;

    private JcaITSContentVerifierProvider(ITSCertificate issuer, JcaJceHelper helper) {
        this.issuer = issuer;
        this.helper = helper;
        try {
            this.parentData = issuer.getEncoded();
        }
        catch (IOException e) {
            throw new IllegalStateException("unable to extract parent data: " + e.getMessage());
        }
        ToBeSignedCertificate toBeSignedCertificate = issuer.toASN1Structure().getToBeSigned();
        VerificationKeyIndicator vki = toBeSignedCertificate.getVerifyKeyIndicator();
        if (!(vki.getVerificationKeyIndicator() instanceof PublicVerificationKey)) {
            throw new IllegalArgumentException("not public verification key");
        }
        PublicVerificationKey pvi = PublicVerificationKey.getInstance((Object)vki.getVerificationKeyIndicator());
        this.initForPvi(pvi, helper);
    }

    private JcaITSContentVerifierProvider(ITSPublicVerificationKey pvi, JcaJceHelper helper) {
        this.issuer = null;
        this.parentData = null;
        this.helper = helper;
        this.initForPvi(pvi.toASN1Structure(), helper);
    }

    private void initForPvi(PublicVerificationKey pvi, JcaJceHelper helper) {
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
                throw new IllegalArgumentException("unknown key type");
            }
        }
        this.pubParams = (ECPublicKey)new JcaITSPublicVerificationKey(pvi, helper).getKey();
    }

    @Override
    public boolean hasAssociatedCertificate() {
        return this.issuer != null;
    }

    @Override
    public ITSCertificate getAssociatedCertificate() {
        return this.issuer;
    }

    @Override
    public ContentVerifier get(int verifierAlgorithmIdentifier) throws OperatorCreationException {
        DigestCalculatorProvider digestCalculatorProvider;
        if (this.sigChoice != verifierAlgorithmIdentifier) {
            throw new OperatorCreationException("wrong verifier for algorithm: " + verifierAlgorithmIdentifier);
        }
        try {
            JcaDigestCalculatorProviderBuilder bld = new JcaDigestCalculatorProviderBuilder().setHelper(this.helper);
            digestCalculatorProvider = bld.build();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
        final DigestCalculator calculator = digestCalculatorProvider.get(this.digestAlgo);
        try {
            Signature signature;
            byte[] parentTBSDigest;
            final OutputStream os = calculator.getOutputStream();
            if (this.parentData != null) {
                os.write(this.parentData, 0, this.parentData.length);
            }
            final byte[] parentDigest = calculator.getDigest();
            if (this.issuer != null && this.issuer.getIssuer().isSelf()) {
                byte[] enc = OEREncoder.toByteArray((ASN1Encodable)this.issuer.toASN1Structure().getToBeSigned(), (Element)IEEE1609dot2.ToBeSignedCertificate.build());
                os.write(enc, 0, enc.length);
                parentTBSDigest = calculator.getDigest();
            } else {
                parentTBSDigest = null;
            }
            switch (this.sigChoice) {
                case 0: 
                case 1: {
                    signature = this.helper.createSignature("SHA256withECDSA");
                    break;
                }
                case 2: {
                    signature = this.helper.createSignature("SHA384withECDSA");
                    break;
                }
                default: {
                    throw new IllegalArgumentException("choice " + this.sigChoice + " not supported");
                }
            }
            return new ContentVerifier(){

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
                    byte[] clientCertDigest = calculator.getDigest();
                    try {
                        signature.initVerify(JcaITSContentVerifierProvider.this.pubParams);
                        signature.update(clientCertDigest);
                        if (parentTBSDigest != null && Arrays.areEqual((byte[])clientCertDigest, (byte[])parentTBSDigest)) {
                            byte[] empty = calculator.getDigest();
                            signature.update(empty);
                        } else {
                            signature.update(parentDigest);
                        }
                        return signature.verify(expected);
                    }
                    catch (Exception ex) {
                        throw new RuntimeException(ex.getMessage(), ex);
                    }
                }
            };
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }

    public static class Builder {
        private JcaJceHelper helper = new DefaultJcaJceHelper();

        public Builder setProvider(Provider provider) {
            this.helper = new ProviderJcaJceHelper(provider);
            return this;
        }

        public Builder setProvider(String providerName) {
            this.helper = new NamedJcaJceHelper(providerName);
            return this;
        }

        public JcaITSContentVerifierProvider build(ITSCertificate issuer) {
            return new JcaITSContentVerifierProvider(issuer, this.helper);
        }

        public JcaITSContentVerifierProvider build(ITSPublicVerificationKey issuer) {
            return new JcaITSContentVerifierProvider(issuer, this.helper);
        }
    }
}

