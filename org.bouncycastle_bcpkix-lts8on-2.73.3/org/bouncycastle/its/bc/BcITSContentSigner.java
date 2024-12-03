/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.sec.SECObjectIdentifiers
 *  org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.DSA
 *  org.bouncycastle.crypto.Digest
 *  org.bouncycastle.crypto.io.DigestOutputStream
 *  org.bouncycastle.crypto.params.ECNamedDomainParameters
 *  org.bouncycastle.crypto.params.ECPrivateKeyParameters
 *  org.bouncycastle.crypto.signers.DSADigestSigner
 *  org.bouncycastle.crypto.signers.ECDSASigner
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.its.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.io.DigestOutputStream;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.DSADigestSigner;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.operator.ITSContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDefaultDigestProvider;
import org.bouncycastle.util.Arrays;

public class BcITSContentSigner
implements ITSContentSigner {
    private final ECPrivateKeyParameters privKey;
    private final ITSCertificate signerCert;
    private final AlgorithmIdentifier digestAlgo;
    private final Digest digest;
    private final byte[] parentData;
    private final ASN1ObjectIdentifier curveID;
    private final byte[] parentDigest;

    public BcITSContentSigner(ECPrivateKeyParameters privKey) {
        this(privKey, null);
    }

    public BcITSContentSigner(ECPrivateKeyParameters privKey, ITSCertificate signerCert) {
        this.privKey = privKey;
        this.curveID = ((ECNamedDomainParameters)privKey.getParameters()).getName();
        this.signerCert = signerCert;
        if (this.curveID.equals((ASN1Primitive)SECObjectIdentifiers.secp256r1)) {
            this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
        } else if (this.curveID.equals((ASN1Primitive)TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
            this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
        } else if (this.curveID.equals((ASN1Primitive)TeleTrusTObjectIdentifiers.brainpoolP384r1)) {
            this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384);
        } else {
            throw new IllegalArgumentException("unknown key type");
        }
        try {
            this.digest = BcDefaultDigestProvider.INSTANCE.get(this.digestAlgo);
        }
        catch (OperatorCreationException e) {
            throw new IllegalStateException("cannot recognise digest type: " + this.digestAlgo.getAlgorithm());
        }
        if (signerCert != null) {
            try {
                this.parentData = signerCert.getEncoded();
                this.parentDigest = new byte[this.digest.getDigestSize()];
                this.digest.update(this.parentData, 0, this.parentData.length);
                this.digest.doFinal(this.parentDigest, 0);
            }
            catch (IOException e) {
                throw new IllegalStateException("signer certificate encoding failed: " + e.getMessage());
            }
        } else {
            this.parentData = null;
            this.parentDigest = new byte[this.digest.getDigestSize()];
            this.digest.doFinal(this.parentDigest, 0);
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
    public OutputStream getOutputStream() {
        return new DigestOutputStream(this.digest);
    }

    @Override
    public boolean isForSelfSigning() {
        return this.parentData == null;
    }

    @Override
    public ASN1ObjectIdentifier getCurveID() {
        return this.curveID;
    }

    @Override
    public byte[] getSignature() {
        byte[] clientCertDigest = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(clientCertDigest, 0);
        DSADigestSigner signer = new DSADigestSigner((DSA)new ECDSASigner(), this.digest);
        signer.init(true, (CipherParameters)this.privKey);
        signer.update(clientCertDigest, 0, clientCertDigest.length);
        signer.update(this.parentDigest, 0, this.parentDigest.length);
        return signer.generateSignature();
    }
}

