/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Null
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.crmf.AttributeTypeAndValue
 *  org.bouncycastle.asn1.crmf.CertReqMsg
 *  org.bouncycastle.asn1.crmf.CertRequest
 *  org.bouncycastle.asn1.crmf.CertTemplate
 *  org.bouncycastle.asn1.crmf.CertTemplateBuilder
 *  org.bouncycastle.asn1.crmf.OptionalValidity
 *  org.bouncycastle.asn1.crmf.PKMACValue
 *  org.bouncycastle.asn1.crmf.POPOPrivKey
 *  org.bouncycastle.asn1.crmf.ProofOfPossession
 *  org.bouncycastle.asn1.crmf.SubsequentMessage
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.ExtensionsGenerator
 *  org.bouncycastle.asn1.x509.GeneralName
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.asn1.x509.Time
 */
package org.bouncycastle.cert.crmf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.AttributeTypeAndValue;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.asn1.crmf.CertTemplate;
import org.bouncycastle.asn1.crmf.CertTemplateBuilder;
import org.bouncycastle.asn1.crmf.OptionalValidity;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.crmf.POPOPrivKey;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.asn1.crmf.SubsequentMessage;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.CRMFUtil;
import org.bouncycastle.cert.crmf.CertificateRequestMessage;
import org.bouncycastle.cert.crmf.Control;
import org.bouncycastle.cert.crmf.PKMACBuilder;
import org.bouncycastle.cert.crmf.ProofOfPossessionSigningKeyBuilder;
import org.bouncycastle.operator.ContentSigner;

public class CertificateRequestMessageBuilder {
    private final BigInteger certReqId;
    private ExtensionsGenerator extGenerator;
    private CertTemplateBuilder templateBuilder;
    private List controls;
    private ContentSigner popSigner;
    private PKMACBuilder pkmacBuilder;
    private char[] password;
    private GeneralName sender;
    private int popoType = 2;
    private POPOPrivKey popoPrivKey;
    private ASN1Null popRaVerified;
    private PKMACValue agreeMAC;
    private AttributeTypeAndValue[] regInfo;

    public CertificateRequestMessageBuilder(BigInteger certReqId) {
        this.certReqId = certReqId;
        this.extGenerator = new ExtensionsGenerator();
        this.templateBuilder = new CertTemplateBuilder();
        this.controls = new ArrayList();
        this.regInfo = null;
    }

    public CertificateRequestMessageBuilder setRegInfo(AttributeTypeAndValue[] regInfo) {
        this.regInfo = regInfo;
        return this;
    }

    public CertificateRequestMessageBuilder setPublicKey(SubjectPublicKeyInfo publicKey) {
        if (publicKey != null) {
            this.templateBuilder.setPublicKey(publicKey);
        }
        return this;
    }

    public CertificateRequestMessageBuilder setIssuer(X500Name issuer) {
        if (issuer != null) {
            this.templateBuilder.setIssuer(issuer);
        }
        return this;
    }

    public CertificateRequestMessageBuilder setSubject(X500Name subject) {
        if (subject != null) {
            this.templateBuilder.setSubject(subject);
        }
        return this;
    }

    public CertificateRequestMessageBuilder setSerialNumber(BigInteger serialNumber) {
        if (serialNumber != null) {
            this.templateBuilder.setSerialNumber(new ASN1Integer(serialNumber));
        }
        return this;
    }

    public CertificateRequestMessageBuilder setSerialNumber(ASN1Integer serialNumber) {
        if (serialNumber != null) {
            this.templateBuilder.setSerialNumber(serialNumber);
        }
        return this;
    }

    public CertificateRequestMessageBuilder setValidity(Date notBeforeDate, Date notAfterDate) {
        this.templateBuilder.setValidity(new OptionalValidity(this.createTime(notBeforeDate), this.createTime(notAfterDate)));
        return this;
    }

    private Time createTime(Date date) {
        if (date != null) {
            return new Time(date);
        }
        return null;
    }

    public CertificateRequestMessageBuilder addExtension(ASN1ObjectIdentifier oid, boolean critical, ASN1Encodable value) throws CertIOException {
        CRMFUtil.addExtension(this.extGenerator, oid, critical, value);
        return this;
    }

    public CertificateRequestMessageBuilder addExtension(ASN1ObjectIdentifier oid, boolean critical, byte[] value) {
        this.extGenerator.addExtension(oid, critical, value);
        return this;
    }

    public CertificateRequestMessageBuilder addControl(Control control) {
        this.controls.add(control);
        return this;
    }

    public CertificateRequestMessageBuilder setProofOfPossessionSigningKeySigner(ContentSigner popSigner) {
        if (this.popoPrivKey != null || this.popRaVerified != null || this.agreeMAC != null) {
            throw new IllegalStateException("only one proof of possession allowed");
        }
        this.popSigner = popSigner;
        return this;
    }

    public CertificateRequestMessageBuilder setProofOfPossessionSubsequentMessage(SubsequentMessage msg) {
        if (this.popSigner != null || this.popRaVerified != null || this.agreeMAC != null) {
            throw new IllegalStateException("only one proof of possession allowed");
        }
        this.popoType = 2;
        this.popoPrivKey = new POPOPrivKey(msg);
        return this;
    }

    public CertificateRequestMessageBuilder setProofOfPossessionSubsequentMessage(int type, SubsequentMessage msg) {
        if (this.popSigner != null || this.popRaVerified != null || this.agreeMAC != null) {
            throw new IllegalStateException("only one proof of possession allowed");
        }
        if (type != 2 && type != 3) {
            throw new IllegalArgumentException("type must be ProofOfPossession.TYPE_KEY_ENCIPHERMENT or ProofOfPossession.TYPE_KEY_AGREEMENT");
        }
        this.popoType = type;
        this.popoPrivKey = new POPOPrivKey(msg);
        return this;
    }

    public CertificateRequestMessageBuilder setProofOfPossessionAgreeMAC(PKMACValue macValue) {
        if (this.popSigner != null || this.popRaVerified != null || this.popoPrivKey != null) {
            throw new IllegalStateException("only one proof of possession allowed");
        }
        this.agreeMAC = macValue;
        return this;
    }

    public CertificateRequestMessageBuilder setProofOfPossessionRaVerified() {
        if (this.popSigner != null || this.popoPrivKey != null) {
            throw new IllegalStateException("only one proof of possession allowed");
        }
        this.popRaVerified = DERNull.INSTANCE;
        return this;
    }

    public CertificateRequestMessageBuilder setAuthInfoPKMAC(PKMACBuilder pkmacBuilder, char[] password) {
        this.pkmacBuilder = pkmacBuilder;
        this.password = password;
        return this;
    }

    public CertificateRequestMessageBuilder setAuthInfoSender(X500Name sender) {
        return this.setAuthInfoSender(new GeneralName(sender));
    }

    public CertificateRequestMessageBuilder setAuthInfoSender(GeneralName sender) {
        this.sender = sender;
        return this;
    }

    public CertificateRequestMessage build() throws CRMFException {
        ProofOfPossession proofOfPossession;
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)new ASN1Integer(this.certReqId));
        if (!this.extGenerator.isEmpty()) {
            this.templateBuilder.setExtensions(this.extGenerator.generate());
        }
        v.add((ASN1Encodable)this.templateBuilder.build());
        if (!this.controls.isEmpty()) {
            ASN1EncodableVector controlV = new ASN1EncodableVector();
            for (Control control : this.controls) {
                controlV.add((ASN1Encodable)new AttributeTypeAndValue(control.getType(), control.getValue()));
            }
            v.add((ASN1Encodable)new DERSequence(controlV));
        }
        CertRequest request = CertRequest.getInstance((Object)new DERSequence(v));
        if (this.popSigner != null) {
            ProofOfPossessionSigningKeyBuilder builder;
            CertTemplate template = request.getCertTemplate();
            if (template.getSubject() == null || template.getPublicKey() == null) {
                SubjectPublicKeyInfo pubKeyInfo = request.getCertTemplate().getPublicKey();
                builder = new ProofOfPossessionSigningKeyBuilder(pubKeyInfo);
                if (this.sender != null) {
                    builder.setSender(this.sender);
                } else {
                    builder.setPublicKeyMac(this.pkmacBuilder, this.password);
                }
            } else {
                builder = new ProofOfPossessionSigningKeyBuilder(request);
            }
            proofOfPossession = new ProofOfPossession(builder.build(this.popSigner));
        } else {
            proofOfPossession = this.popoPrivKey != null ? new ProofOfPossession(this.popoType, this.popoPrivKey) : (this.agreeMAC != null ? new ProofOfPossession(3, new POPOPrivKey(this.agreeMAC)) : (this.popRaVerified != null ? new ProofOfPossession() : new ProofOfPossession()));
        }
        CertReqMsg certReqMsg = new CertReqMsg(request, proofOfPossession, this.regInfo);
        return new CertificateRequestMessage(certReqMsg);
    }
}

