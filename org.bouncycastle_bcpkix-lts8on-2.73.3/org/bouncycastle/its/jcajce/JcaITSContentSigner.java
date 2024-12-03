/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PrivateKeyInfo
 *  org.bouncycastle.asn1.sec.SECObjectIdentifiers
 *  org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.its.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Signature;
import java.security.interfaces.ECPrivateKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.operator.ITSContentSigner;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Arrays;

public class JcaITSContentSigner
implements ITSContentSigner {
    private final ECPrivateKey privateKey;
    private final ITSCertificate signerCert;
    private final AlgorithmIdentifier digestAlgo;
    private final DigestCalculator digest;
    private final byte[] parentData;
    private final ASN1ObjectIdentifier curveID;
    private final byte[] parentDigest;
    private final String signer;
    private final JcaJceHelper helper;

    private JcaITSContentSigner(ECPrivateKey privateKey, ITSCertificate signerCert, JcaJceHelper helper) {
        DigestCalculatorProvider digestCalculatorProvider;
        this.privateKey = privateKey;
        this.signerCert = signerCert;
        this.helper = helper;
        PrivateKeyInfo pkInfo = PrivateKeyInfo.getInstance((Object)privateKey.getEncoded());
        this.curveID = ASN1ObjectIdentifier.getInstance((Object)pkInfo.getPrivateKeyAlgorithm().getParameters());
        if (this.curveID.equals((ASN1Primitive)SECObjectIdentifiers.secp256r1)) {
            this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
            this.signer = "SHA256withECDSA";
        } else if (this.curveID.equals((ASN1Primitive)TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
            this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
            this.signer = "SHA256withECDSA";
        } else if (this.curveID.equals((ASN1Primitive)TeleTrusTObjectIdentifiers.brainpoolP384r1)) {
            this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384);
            this.signer = "SHA384withECDSA";
        } else {
            throw new IllegalArgumentException("unknown key type");
        }
        try {
            JcaDigestCalculatorProviderBuilder bld = new JcaDigestCalculatorProviderBuilder().setHelper(helper);
            digestCalculatorProvider = bld.build();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
        try {
            this.digest = digestCalculatorProvider.get(this.digestAlgo);
        }
        catch (OperatorCreationException e) {
            throw new IllegalStateException("cannot recognise digest type: " + this.digestAlgo.getAlgorithm(), e);
        }
        if (signerCert != null) {
            try {
                this.parentData = signerCert.getEncoded();
                OutputStream os = this.digest.getOutputStream();
                os.write(this.parentData, 0, this.parentData.length);
                os.close();
                this.parentDigest = this.digest.getDigest();
            }
            catch (IOException e) {
                throw new IllegalStateException("signer certificate encoding failed: " + e.getMessage());
            }
        } else {
            this.parentData = null;
            this.parentDigest = this.digest.getDigest();
        }
    }

    @Override
    public OutputStream getOutputStream() {
        return this.digest.getOutputStream();
    }

    @Override
    public byte[] getSignature() {
        byte[] clientCertDigest = this.digest.getDigest();
        try {
            Signature signature = this.helper.createSignature(this.signer);
            signature.initSign(this.privateKey);
            signature.update(clientCertDigest, 0, clientCertDigest.length);
            signature.update(this.parentDigest, 0, this.parentDigest.length);
            return signature.sign();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public ITSCertificate getAssociatedCertificate() {
        return this.signerCert;
    }

    @Override
    public byte[] getAssociatedCertificateDigest() {
        return Arrays.clone((byte[])this.parentDigest);
    }

    @Override
    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestAlgo;
    }

    @Override
    public ASN1ObjectIdentifier getCurveID() {
        return this.curveID;
    }

    @Override
    public boolean isForSelfSigning() {
        return this.parentData == null;
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

        public JcaITSContentSigner build(PrivateKey privateKey) {
            return new JcaITSContentSigner((ECPrivateKey)privateKey, null, this.helper);
        }

        public JcaITSContentSigner build(PrivateKey privateKey, ITSCertificate signerCert) {
            return new JcaITSContentSigner((ECPrivateKey)privateKey, signerCert, this.helper);
        }
    }
}

