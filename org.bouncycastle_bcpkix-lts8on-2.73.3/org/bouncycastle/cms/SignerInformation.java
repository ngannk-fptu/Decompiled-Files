/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.cms.Attribute
 *  org.bouncycastle.asn1.cms.AttributeTable
 *  org.bouncycastle.asn1.cms.CMSAlgorithmProtection
 *  org.bouncycastle.asn1.cms.CMSAttributes
 *  org.bouncycastle.asn1.cms.IssuerAndSerialNumber
 *  org.bouncycastle.asn1.cms.SignerIdentifier
 *  org.bouncycastle.asn1.cms.SignerInfo
 *  org.bouncycastle.asn1.cms.Time
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.DigestInfo
 *  org.bouncycastle.util.Arrays
 *  org.bouncycastle.util.io.TeeOutputStream
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAlgorithmProtection;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.cms.Time;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedHelper;
import org.bouncycastle.cms.CMSSignerDigestMismatchException;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.cms.CMSVerifierCertificateNotValidException;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.RawContentVerifier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.TeeOutputStream;

public class SignerInformation {
    private final SignerId sid;
    private final CMSProcessable content;
    private final byte[] signature;
    private final ASN1ObjectIdentifier contentType;
    private final boolean isCounterSignature;
    private AttributeTable signedAttributeValues;
    private AttributeTable unsignedAttributeValues;
    private byte[] resultDigest;
    protected final SignerInfo info;
    protected final AlgorithmIdentifier digestAlgorithm;
    protected final AlgorithmIdentifier encryptionAlgorithm;
    protected final ASN1Set signedAttributeSet;
    protected final ASN1Set unsignedAttributeSet;

    SignerInformation(SignerInfo info, ASN1ObjectIdentifier contentType, CMSProcessable content, byte[] resultDigest) {
        this.info = info;
        this.contentType = contentType;
        this.isCounterSignature = contentType == null;
        SignerIdentifier s = info.getSID();
        if (s.isTagged()) {
            ASN1OctetString octs = ASN1OctetString.getInstance((Object)s.getId());
            this.sid = new SignerId(octs.getOctets());
        } else {
            IssuerAndSerialNumber iAnds = IssuerAndSerialNumber.getInstance((Object)s.getId());
            this.sid = new SignerId(iAnds.getName(), iAnds.getSerialNumber().getValue());
        }
        this.digestAlgorithm = info.getDigestAlgorithm();
        this.signedAttributeSet = info.getAuthenticatedAttributes();
        this.unsignedAttributeSet = info.getUnauthenticatedAttributes();
        this.encryptionAlgorithm = info.getDigestEncryptionAlgorithm();
        this.signature = info.getEncryptedDigest().getOctets();
        this.content = content;
        this.resultDigest = resultDigest;
    }

    protected SignerInformation(SignerInformation baseInfo) {
        this(baseInfo, baseInfo.info);
    }

    protected SignerInformation(SignerInformation baseInfo, SignerInfo info) {
        this.info = info;
        this.contentType = baseInfo.contentType;
        this.isCounterSignature = baseInfo.isCounterSignature();
        this.sid = baseInfo.getSID();
        this.digestAlgorithm = info.getDigestAlgorithm();
        this.signedAttributeSet = info.getAuthenticatedAttributes();
        this.unsignedAttributeSet = info.getUnauthenticatedAttributes();
        this.encryptionAlgorithm = info.getDigestEncryptionAlgorithm();
        this.signature = info.getEncryptedDigest().getOctets();
        this.content = baseInfo.content;
        this.resultDigest = baseInfo.resultDigest;
        this.signedAttributeValues = this.getSignedAttributes();
        this.unsignedAttributeValues = this.getUnsignedAttributes();
    }

    public boolean isCounterSignature() {
        return this.isCounterSignature;
    }

    public ASN1ObjectIdentifier getContentType() {
        return this.contentType;
    }

    private byte[] encodeObj(ASN1Encodable obj) throws IOException {
        if (obj != null) {
            return obj.toASN1Primitive().getEncoded();
        }
        return null;
    }

    public SignerId getSID() {
        return this.sid;
    }

    public int getVersion() {
        return this.info.getVersion().intValueExact();
    }

    public AlgorithmIdentifier getDigestAlgorithmID() {
        return this.digestAlgorithm;
    }

    public String getDigestAlgOID() {
        return this.digestAlgorithm.getAlgorithm().getId();
    }

    public byte[] getDigestAlgParams() {
        try {
            return this.encodeObj(this.digestAlgorithm.getParameters());
        }
        catch (Exception e) {
            throw new RuntimeException("exception getting digest parameters " + e);
        }
    }

    public byte[] getContentDigest() {
        if (this.resultDigest == null) {
            throw new IllegalStateException("method can only be called after verify.");
        }
        return Arrays.clone((byte[])this.resultDigest);
    }

    public String getEncryptionAlgOID() {
        return this.encryptionAlgorithm.getAlgorithm().getId();
    }

    public byte[] getEncryptionAlgParams() {
        try {
            return this.encodeObj(this.encryptionAlgorithm.getParameters());
        }
        catch (Exception e) {
            throw new RuntimeException("exception getting encryption parameters " + e);
        }
    }

    public AttributeTable getSignedAttributes() {
        if (this.signedAttributeSet != null && this.signedAttributeValues == null) {
            this.signedAttributeValues = new AttributeTable(this.signedAttributeSet);
        }
        return this.signedAttributeValues;
    }

    public AttributeTable getUnsignedAttributes() {
        if (this.unsignedAttributeSet != null && this.unsignedAttributeValues == null) {
            this.unsignedAttributeValues = new AttributeTable(this.unsignedAttributeSet);
        }
        return this.unsignedAttributeValues;
    }

    public byte[] getSignature() {
        return Arrays.clone((byte[])this.signature);
    }

    public SignerInformationStore getCounterSignatures() {
        AttributeTable unsignedAttributeTable = this.getUnsignedAttributes();
        if (unsignedAttributeTable == null) {
            return new SignerInformationStore(new ArrayList<SignerInformation>(0));
        }
        ArrayList<SignerInformation> counterSignatures = new ArrayList<SignerInformation>();
        ASN1EncodableVector allCSAttrs = unsignedAttributeTable.getAll(CMSAttributes.counterSignature);
        for (int i = 0; i < allCSAttrs.size(); ++i) {
            Attribute counterSignatureAttribute = (Attribute)allCSAttrs.get(i);
            ASN1Set values = counterSignatureAttribute.getAttrValues();
            if (values.size() < 1) {
                // empty if block
            }
            Enumeration en = values.getObjects();
            while (en.hasMoreElements()) {
                SignerInfo si = SignerInfo.getInstance(en.nextElement());
                counterSignatures.add(new SignerInformation(si, null, new CMSProcessableByteArray(this.getSignature()), null));
            }
        }
        return new SignerInformationStore(counterSignatures);
    }

    public byte[] getEncodedSignedAttributes() throws IOException {
        if (this.signedAttributeSet != null) {
            return this.signedAttributeSet.getEncoded("DER");
        }
        return null;
    }

    private boolean doVerify(SignerInformationVerifier verifier) throws CMSException {
        ContentVerifier contentVerifier;
        String encName = CMSSignedHelper.INSTANCE.getEncryptionAlgName(this.getEncryptionAlgOID());
        try {
            contentVerifier = verifier.getContentVerifier(this.encryptionAlgorithm, this.info.getDigestAlgorithm());
        }
        catch (OperatorCreationException e) {
            throw new CMSException("can't create content verifier: " + e.getMessage(), e);
        }
        try {
            OutputStream sigOut = contentVerifier.getOutputStream();
            if (this.resultDigest == null) {
                DigestCalculator calc = verifier.getDigestCalculator(this.getDigestAlgorithmID());
                if (this.content != null) {
                    OutputStream digOut = calc.getOutputStream();
                    if (this.signedAttributeSet == null) {
                        if (contentVerifier instanceof RawContentVerifier) {
                            this.content.write(digOut);
                        } else {
                            TeeOutputStream cOut = new TeeOutputStream(digOut, sigOut);
                            this.content.write((OutputStream)cOut);
                            cOut.close();
                        }
                    } else {
                        this.content.write(digOut);
                        sigOut.write(this.getEncodedSignedAttributes());
                    }
                    digOut.close();
                } else if (this.signedAttributeSet != null) {
                    sigOut.write(this.getEncodedSignedAttributes());
                } else {
                    throw new CMSException("data not encapsulated in signature - use detached constructor.");
                }
                this.resultDigest = calc.getDigest();
            } else if (this.signedAttributeSet == null) {
                if (this.content != null) {
                    this.content.write(sigOut);
                }
            } else {
                sigOut.write(this.getEncodedSignedAttributes());
            }
            sigOut.close();
        }
        catch (IOException e) {
            throw new CMSException("can't process mime object to create signature.", e);
        }
        catch (OperatorCreationException e) {
            throw new CMSException("can't create digest calculator: " + e.getMessage(), e);
        }
        this.verifyContentTypeAttributeValue();
        AttributeTable signedAttrTable = this.getSignedAttributes();
        this.verifyAlgorithmIdentifierProtectionAttribute(signedAttrTable);
        this.verifyMessageDigestAttribute();
        this.verifyCounterSignatureAttribute(signedAttrTable);
        try {
            if (this.signedAttributeSet == null && this.resultDigest != null && contentVerifier instanceof RawContentVerifier) {
                RawContentVerifier rawVerifier = (RawContentVerifier)((Object)contentVerifier);
                if (encName.equals("RSA")) {
                    DigestInfo digInfo = new DigestInfo(new AlgorithmIdentifier(this.digestAlgorithm.getAlgorithm(), (ASN1Encodable)DERNull.INSTANCE), this.resultDigest);
                    return rawVerifier.verify(digInfo.getEncoded("DER"), this.getSignature());
                }
                return rawVerifier.verify(this.resultDigest, this.getSignature());
            }
            return contentVerifier.verify(this.getSignature());
        }
        catch (IOException e) {
            throw new CMSException("can't process mime object to create signature.", e);
        }
    }

    private void verifyContentTypeAttributeValue() throws CMSException {
        ASN1Primitive validContentType = this.getSingleValuedSignedAttribute(CMSAttributes.contentType, "content-type");
        if (validContentType == null) {
            if (!this.isCounterSignature && this.signedAttributeSet != null) {
                throw new CMSException("The content-type attribute type MUST be present whenever signed attributes are present in signed-data");
            }
        } else {
            if (this.isCounterSignature) {
                throw new CMSException("[For counter signatures,] the signedAttributes field MUST NOT contain a content-type attribute");
            }
            if (!(validContentType instanceof ASN1ObjectIdentifier)) {
                throw new CMSException("content-type attribute value not of ASN.1 type 'OBJECT IDENTIFIER'");
            }
            ASN1ObjectIdentifier signedContentType = (ASN1ObjectIdentifier)validContentType;
            if (!signedContentType.equals((ASN1Primitive)this.contentType)) {
                throw new CMSException("content-type attribute value does not match eContentType");
            }
        }
    }

    private void verifyMessageDigestAttribute() throws CMSException {
        ASN1Primitive validMessageDigest = this.getSingleValuedSignedAttribute(CMSAttributes.messageDigest, "message-digest");
        if (validMessageDigest == null) {
            if (this.signedAttributeSet != null) {
                throw new CMSException("the message-digest signed attribute type MUST be present when there are any signed attributes present");
            }
        } else {
            if (!(validMessageDigest instanceof ASN1OctetString)) {
                throw new CMSException("message-digest attribute value not of ASN.1 type 'OCTET STRING'");
            }
            ASN1OctetString signedMessageDigest = (ASN1OctetString)validMessageDigest;
            if (!Arrays.constantTimeAreEqual((byte[])this.resultDigest, (byte[])signedMessageDigest.getOctets())) {
                throw new CMSSignerDigestMismatchException("message-digest attribute value does not match calculated value");
            }
        }
    }

    private void verifyAlgorithmIdentifierProtectionAttribute(AttributeTable signedAttrTable) throws CMSException {
        AttributeTable unsignedAttrTable = this.getUnsignedAttributes();
        if (unsignedAttrTable != null && unsignedAttrTable.getAll(CMSAttributes.cmsAlgorithmProtect).size() > 0) {
            throw new CMSException("A cmsAlgorithmProtect attribute MUST be a signed attribute");
        }
        if (signedAttrTable != null) {
            ASN1EncodableVector protectionAttributes = signedAttrTable.getAll(CMSAttributes.cmsAlgorithmProtect);
            if (protectionAttributes.size() > 1) {
                throw new CMSException("Only one instance of a cmsAlgorithmProtect attribute can be present");
            }
            if (protectionAttributes.size() > 0) {
                Attribute attr = Attribute.getInstance((Object)protectionAttributes.get(0));
                if (attr.getAttrValues().size() != 1) {
                    throw new CMSException("A cmsAlgorithmProtect attribute MUST contain exactly one value");
                }
                CMSAlgorithmProtection algorithmProtection = CMSAlgorithmProtection.getInstance((Object)attr.getAttributeValues()[0]);
                if (!CMSUtils.isEquivalent(algorithmProtection.getDigestAlgorithm(), this.info.getDigestAlgorithm())) {
                    throw new CMSException("CMS Algorithm Identifier Protection check failed for digestAlgorithm");
                }
                if (!CMSUtils.isEquivalent(algorithmProtection.getSignatureAlgorithm(), this.info.getDigestEncryptionAlgorithm())) {
                    throw new CMSException("CMS Algorithm Identifier Protection check failed for signatureAlgorithm");
                }
            }
        }
    }

    private void verifyCounterSignatureAttribute(AttributeTable signedAttrTable) throws CMSException {
        if (signedAttrTable != null && signedAttrTable.getAll(CMSAttributes.counterSignature).size() > 0) {
            throw new CMSException("A countersignature attribute MUST NOT be a signed attribute");
        }
        AttributeTable unsignedAttrTable = this.getUnsignedAttributes();
        if (unsignedAttrTable != null) {
            ASN1EncodableVector csAttrs = unsignedAttrTable.getAll(CMSAttributes.counterSignature);
            for (int i = 0; i < csAttrs.size(); ++i) {
                Attribute csAttr = Attribute.getInstance((Object)csAttrs.get(i));
                if (csAttr.getAttrValues().size() >= 1) continue;
                throw new CMSException("A countersignature attribute MUST contain at least one AttributeValue");
            }
        }
    }

    public boolean verify(SignerInformationVerifier verifier) throws CMSException {
        X509CertificateHolder dcv;
        Time signingTime = this.getSigningTime();
        if (verifier.hasAssociatedCertificate() && signingTime != null && !(dcv = verifier.getAssociatedCertificate()).isValidOn(signingTime.getDate())) {
            throw new CMSVerifierCertificateNotValidException("verifier not valid at signingTime");
        }
        return this.doVerify(verifier);
    }

    public SignerInfo toASN1Structure() {
        return this.info;
    }

    private ASN1Primitive getSingleValuedSignedAttribute(ASN1ObjectIdentifier attrOID, String printableName) throws CMSException {
        AttributeTable unsignedAttrTable = this.getUnsignedAttributes();
        if (unsignedAttrTable != null && unsignedAttrTable.getAll(attrOID).size() > 0) {
            throw new CMSException("The " + printableName + " attribute MUST NOT be an unsigned attribute");
        }
        AttributeTable signedAttrTable = this.getSignedAttributes();
        if (signedAttrTable == null) {
            return null;
        }
        ASN1EncodableVector v = signedAttrTable.getAll(attrOID);
        switch (v.size()) {
            case 0: {
                return null;
            }
            case 1: {
                Attribute t = (Attribute)v.get(0);
                ASN1Set attrValues = t.getAttrValues();
                if (attrValues.size() != 1) {
                    throw new CMSException("A " + printableName + " attribute MUST have a single attribute value");
                }
                return attrValues.getObjectAt(0).toASN1Primitive();
            }
        }
        throw new CMSException("The SignedAttributes in a signerInfo MUST NOT include multiple instances of the " + printableName + " attribute");
    }

    private Time getSigningTime() throws CMSException {
        ASN1Primitive validSigningTime = this.getSingleValuedSignedAttribute(CMSAttributes.signingTime, "signing-time");
        if (validSigningTime == null) {
            return null;
        }
        try {
            return Time.getInstance((Object)validSigningTime);
        }
        catch (IllegalArgumentException e) {
            throw new CMSException("signing-time attribute value not a valid 'Time' structure");
        }
    }

    public static SignerInformation replaceUnsignedAttributes(SignerInformation signerInformation, AttributeTable unsignedAttributes) {
        SignerInfo sInfo = signerInformation.info;
        DERSet unsignedAttr = null;
        if (unsignedAttributes != null) {
            unsignedAttr = new DERSet(unsignedAttributes.toASN1EncodableVector());
        }
        return new SignerInformation(new SignerInfo(sInfo.getSID(), sInfo.getDigestAlgorithm(), sInfo.getAuthenticatedAttributes(), sInfo.getDigestEncryptionAlgorithm(), sInfo.getEncryptedDigest(), (ASN1Set)unsignedAttr), signerInformation.contentType, signerInformation.content, null);
    }

    public static SignerInformation addCounterSigners(SignerInformation signerInformation, SignerInformationStore counterSigners) {
        SignerInfo sInfo = signerInformation.info;
        AttributeTable unsignedAttr = signerInformation.getUnsignedAttributes();
        ASN1EncodableVector v = unsignedAttr != null ? unsignedAttr.toASN1EncodableVector() : new ASN1EncodableVector();
        ASN1EncodableVector sigs = new ASN1EncodableVector();
        Iterator<SignerInformation> it = counterSigners.getSigners().iterator();
        while (it.hasNext()) {
            sigs.add((ASN1Encodable)it.next().toASN1Structure());
        }
        v.add((ASN1Encodable)new Attribute(CMSAttributes.counterSignature, (ASN1Set)new DERSet(sigs)));
        return new SignerInformation(new SignerInfo(sInfo.getSID(), sInfo.getDigestAlgorithm(), sInfo.getAuthenticatedAttributes(), sInfo.getDigestEncryptionAlgorithm(), sInfo.getEncryptedDigest(), (ASN1Set)new DERSet(v)), signerInformation.contentType, signerInformation.content, null);
    }
}

