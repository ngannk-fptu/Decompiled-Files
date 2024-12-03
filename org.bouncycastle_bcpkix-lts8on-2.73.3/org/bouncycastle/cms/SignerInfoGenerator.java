/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.cms.AttributeTable
 *  org.bouncycastle.asn1.cms.SignerIdentifier
 *  org.bouncycastle.asn1.cms.SignerInfo
 *  org.bouncycastle.asn1.edec.EdECObjectIdentifiers
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Arrays
 *  org.bouncycastle.util.io.TeeOutputStream
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignatureEncryptionAlgorithmFinder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.TeeOutputStream;

public class SignerInfoGenerator {
    private final SignerIdentifier signerIdentifier;
    private final CMSAttributeTableGenerator sAttrGen;
    private final CMSAttributeTableGenerator unsAttrGen;
    private final ContentSigner signer;
    private final DigestCalculator digester;
    private final AlgorithmIdentifier digestAlgorithm;
    private final CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder;
    private byte[] calculatedDigest = null;
    private X509CertificateHolder certHolder;

    SignerInfoGenerator(SignerIdentifier signerIdentifier, ContentSigner signer, AlgorithmIdentifier digesterAlgorithm, CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder) {
        this.signerIdentifier = signerIdentifier;
        this.signer = signer;
        this.digestAlgorithm = digesterAlgorithm;
        this.digester = null;
        this.sAttrGen = null;
        this.unsAttrGen = null;
        this.sigEncAlgFinder = sigEncAlgFinder;
    }

    SignerInfoGenerator(SignerIdentifier signerIdentifier, ContentSigner signer, DigestCalculator digester, CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder, CMSAttributeTableGenerator sAttrGen, CMSAttributeTableGenerator unsAttrGen) {
        this.signerIdentifier = signerIdentifier;
        this.signer = signer;
        this.digestAlgorithm = digester.getAlgorithmIdentifier();
        this.digester = digester;
        this.sAttrGen = sAttrGen;
        this.unsAttrGen = unsAttrGen;
        this.sigEncAlgFinder = sigEncAlgFinder;
    }

    public SignerInfoGenerator(SignerInfoGenerator original, CMSAttributeTableGenerator sAttrGen, CMSAttributeTableGenerator unsAttrGen) {
        this.signerIdentifier = original.signerIdentifier;
        this.signer = original.signer;
        this.digestAlgorithm = original.digestAlgorithm;
        this.digester = original.digester;
        this.sigEncAlgFinder = original.sigEncAlgFinder;
        this.sAttrGen = sAttrGen;
        this.unsAttrGen = unsAttrGen;
    }

    public SignerIdentifier getSID() {
        return this.signerIdentifier;
    }

    public int getGeneratedVersion() {
        return this.signerIdentifier.isTagged() ? 3 : 1;
    }

    public boolean hasAssociatedCertificate() {
        return this.certHolder != null;
    }

    public X509CertificateHolder getAssociatedCertificate() {
        return this.certHolder;
    }

    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestAlgorithm;
    }

    public OutputStream getCalculatingOutputStream() {
        if (this.digester != null) {
            if (this.sAttrGen == null) {
                return new TeeOutputStream(this.digester.getOutputStream(), this.signer.getOutputStream());
            }
            return this.digester.getOutputStream();
        }
        return this.signer.getOutputStream();
    }

    public SignerInfo generate(ASN1ObjectIdentifier contentType) throws CMSException {
        try {
            ASN1Set signedAttr = null;
            AlgorithmIdentifier digestEncryptionAlgorithm = this.sigEncAlgFinder.findEncryptionAlgorithm(this.signer.getAlgorithmIdentifier());
            AlgorithmIdentifier digestAlg = null;
            if (this.sAttrGen != null) {
                digestAlg = this.digester.getAlgorithmIdentifier();
                this.calculatedDigest = this.digester.getDigest();
                Map parameters = this.getBaseParameters(contentType, this.digester.getAlgorithmIdentifier(), digestEncryptionAlgorithm, this.calculatedDigest);
                AttributeTable signed = this.sAttrGen.getAttributes(Collections.unmodifiableMap(parameters));
                signedAttr = this.getAttributeSet(signed);
                OutputStream sOut = this.signer.getOutputStream();
                sOut.write(signedAttr.getEncoded("DER"));
                sOut.close();
            } else {
                digestAlg = this.digestAlgorithm;
                this.calculatedDigest = (byte[])(this.digester != null ? this.digester.getDigest() : null);
            }
            byte[] sigBytes = this.signer.getSignature();
            ASN1Set unsignedAttr = null;
            if (this.unsAttrGen != null) {
                Map parameters = this.getBaseParameters(contentType, digestAlg, digestEncryptionAlgorithm, this.calculatedDigest);
                parameters.put("encryptedDigest", Arrays.clone((byte[])sigBytes));
                AttributeTable unsigned = this.unsAttrGen.getAttributes(Collections.unmodifiableMap(parameters));
                unsignedAttr = this.getAttributeSet(unsigned);
            }
            if (this.sAttrGen == null && EdECObjectIdentifiers.id_Ed448.equals((ASN1Primitive)digestEncryptionAlgorithm.getAlgorithm())) {
                digestAlg = new AlgorithmIdentifier(NISTObjectIdentifiers.id_shake256);
            }
            return new SignerInfo(this.signerIdentifier, digestAlg, signedAttr, digestEncryptionAlgorithm, (ASN1OctetString)new DEROctetString(sigBytes), unsignedAttr);
        }
        catch (IOException e) {
            throw new CMSException("encoding error.", e);
        }
    }

    void setAssociatedCertificate(X509CertificateHolder certHolder) {
        this.certHolder = certHolder;
    }

    private ASN1Set getAttributeSet(AttributeTable attr) {
        if (attr != null) {
            return new DERSet(attr.toASN1EncodableVector());
        }
        return null;
    }

    private Map getBaseParameters(ASN1ObjectIdentifier contentType, AlgorithmIdentifier digAlgId, AlgorithmIdentifier sigAlgId, byte[] hash) {
        HashMap<String, Object> param = new HashMap<String, Object>();
        if (contentType != null) {
            param.put("contentType", contentType);
        }
        param.put("digestAlgID", digAlgId);
        param.put("signatureAlgID", sigAlgId);
        param.put("digest", Arrays.clone((byte[])hash));
        return param;
    }

    public byte[] getCalculatedDigest() {
        if (this.calculatedDigest != null) {
            return Arrays.clone((byte[])this.calculatedDigest);
        }
        return null;
    }

    public CMSAttributeTableGenerator getSignedAttributeTableGenerator() {
        return this.sAttrGen;
    }

    public CMSAttributeTableGenerator getUnsignedAttributeTableGenerator() {
        return this.unsAttrGen;
    }
}

