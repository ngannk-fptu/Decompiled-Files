/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1BitString
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1GeneralizedTime
 *  org.bouncycastle.asn1.DERBitString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.cmp.CMPCertificate
 *  org.bouncycastle.asn1.cmp.InfoTypeAndValue
 *  org.bouncycastle.asn1.cmp.PKIBody
 *  org.bouncycastle.asn1.cmp.PKIFreeText
 *  org.bouncycastle.asn1.cmp.PKIHeader
 *  org.bouncycastle.asn1.cmp.PKIHeaderBuilder
 *  org.bouncycastle.asn1.cmp.PKIMessage
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.GeneralName
 */
package org.bouncycastle.cert.cmp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.InfoTypeAndValue;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIFreeText;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIHeaderBuilder;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.cmp.CMPException;
import org.bouncycastle.cert.cmp.CertificateConfirmationContent;
import org.bouncycastle.cert.cmp.ProtectedPKIMessage;
import org.bouncycastle.cert.crmf.CertificateRepMessage;
import org.bouncycastle.cert.crmf.CertificateReqMessages;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.MacCalculator;

public class ProtectedPKIMessageBuilder {
    private PKIHeaderBuilder hdrBuilder;
    private PKIBody body;
    private List generalInfos = new ArrayList();
    private List extraCerts = new ArrayList();

    public ProtectedPKIMessageBuilder(GeneralName sender, GeneralName recipient) {
        this(2, sender, recipient);
    }

    public ProtectedPKIMessageBuilder(int pvno, GeneralName sender, GeneralName recipient) {
        this.hdrBuilder = new PKIHeaderBuilder(pvno, sender, recipient);
    }

    public ProtectedPKIMessageBuilder setTransactionID(byte[] tid) {
        this.hdrBuilder.setTransactionID(tid);
        return this;
    }

    public ProtectedPKIMessageBuilder setFreeText(PKIFreeText freeText) {
        this.hdrBuilder.setFreeText(freeText);
        return this;
    }

    public ProtectedPKIMessageBuilder addGeneralInfo(InfoTypeAndValue genInfo) {
        this.generalInfos.add(genInfo);
        return this;
    }

    public ProtectedPKIMessageBuilder setMessageTime(Date time) {
        this.hdrBuilder.setMessageTime(new ASN1GeneralizedTime(time));
        return this;
    }

    public ProtectedPKIMessageBuilder setRecipKID(byte[] kid) {
        this.hdrBuilder.setRecipKID(kid);
        return this;
    }

    public ProtectedPKIMessageBuilder setRecipNonce(byte[] nonce) {
        this.hdrBuilder.setRecipNonce(nonce);
        return this;
    }

    public ProtectedPKIMessageBuilder setSenderKID(byte[] kid) {
        this.hdrBuilder.setSenderKID(kid);
        return this;
    }

    public ProtectedPKIMessageBuilder setSenderNonce(byte[] nonce) {
        this.hdrBuilder.setSenderNonce(nonce);
        return this;
    }

    public ProtectedPKIMessageBuilder setBody(PKIBody body) {
        this.body = body;
        return this;
    }

    public ProtectedPKIMessageBuilder setBody(int bodyType, CertificateReqMessages certificateReqMessages) {
        if (!CertificateReqMessages.isCertificateRequestMessages(bodyType)) {
            throw new IllegalArgumentException("body type " + bodyType + " does not match CMP type CertReqMessages");
        }
        this.body = new PKIBody(bodyType, (ASN1Encodable)certificateReqMessages.toASN1Structure());
        return this;
    }

    public ProtectedPKIMessageBuilder setBody(int bodyType, CertificateRepMessage certificateRepMessage) {
        if (!CertificateRepMessage.isCertificateRepMessage(bodyType)) {
            throw new IllegalArgumentException("body type " + bodyType + " does not match CMP type CertRepMessage");
        }
        this.body = new PKIBody(bodyType, certificateRepMessage.toASN1Structure());
        return this;
    }

    public ProtectedPKIMessageBuilder setBody(int bodyType, CertificateConfirmationContent certificateConfirmationContent) {
        if (!CertificateConfirmationContent.isCertificateConfirmationContent(bodyType)) {
            throw new IllegalArgumentException("body type " + bodyType + " does not match CMP type CertConfirmContent");
        }
        this.body = new PKIBody(bodyType, (ASN1Encodable)certificateConfirmationContent.toASN1Structure());
        return this;
    }

    public ProtectedPKIMessageBuilder addCMPCertificate(X509CertificateHolder extraCert) {
        this.extraCerts.add(extraCert);
        return this;
    }

    public ProtectedPKIMessage build(MacCalculator macCalculator) throws CMPException {
        if (null == this.body) {
            throw new IllegalStateException("body must be set before building");
        }
        this.finaliseHeader(macCalculator.getAlgorithmIdentifier());
        PKIHeader header = this.hdrBuilder.build();
        try {
            DERBitString protection = new DERBitString(this.calculateMac(macCalculator, header, this.body));
            return this.finaliseMessage(header, protection);
        }
        catch (IOException e) {
            throw new CMPException("unable to encode MAC input: " + e.getMessage(), e);
        }
    }

    public ProtectedPKIMessage build(ContentSigner signer) throws CMPException {
        if (null == this.body) {
            throw new IllegalStateException("body must be set before building");
        }
        this.finaliseHeader(signer.getAlgorithmIdentifier());
        PKIHeader header = this.hdrBuilder.build();
        try {
            DERBitString protection = new DERBitString(this.calculateSignature(signer, header, this.body));
            return this.finaliseMessage(header, protection);
        }
        catch (IOException e) {
            throw new CMPException("unable to encode signature input: " + e.getMessage(), e);
        }
    }

    private void finaliseHeader(AlgorithmIdentifier algorithmIdentifier) {
        this.hdrBuilder.setProtectionAlg(algorithmIdentifier);
        if (!this.generalInfos.isEmpty()) {
            InfoTypeAndValue[] genInfos = new InfoTypeAndValue[this.generalInfos.size()];
            this.hdrBuilder.setGeneralInfo(this.generalInfos.toArray(genInfos));
        }
    }

    private ProtectedPKIMessage finaliseMessage(PKIHeader header, DERBitString protection) {
        if (!this.extraCerts.isEmpty()) {
            CMPCertificate[] cmpCerts = new CMPCertificate[this.extraCerts.size()];
            for (int i = 0; i != cmpCerts.length; ++i) {
                cmpCerts[i] = new CMPCertificate(((X509CertificateHolder)this.extraCerts.get(i)).toASN1Structure());
            }
            return new ProtectedPKIMessage(new PKIMessage(header, this.body, (ASN1BitString)protection, cmpCerts));
        }
        return new ProtectedPKIMessage(new PKIMessage(header, this.body, (ASN1BitString)protection));
    }

    private byte[] calculateSignature(ContentSigner signer, PKIHeader header, PKIBody body) throws IOException {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add((ASN1Encodable)header);
        v.add((ASN1Encodable)body);
        OutputStream sOut = signer.getOutputStream();
        sOut.write(new DERSequence(v).getEncoded("DER"));
        sOut.close();
        return signer.getSignature();
    }

    private byte[] calculateMac(MacCalculator macCalculator, PKIHeader header, PKIBody body) throws IOException {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add((ASN1Encodable)header);
        v.add((ASN1Encodable)body);
        OutputStream sOut = macCalculator.getOutputStream();
        sOut.write(new DERSequence(v).getEncoded("DER"));
        sOut.close();
        return macCalculator.getMac();
    }
}

