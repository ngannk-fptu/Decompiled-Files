/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.DERBitString
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.pkcs.Attribute
 *  org.bouncycastle.asn1.pkcs.CertificationRequest
 *  org.bouncycastle.asn1.pkcs.CertificationRequestInfo
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 */
package org.bouncycastle.pkcs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

public class PKCS10CertificationRequestBuilder {
    private SubjectPublicKeyInfo publicKeyInfo;
    private X500Name subject;
    private List attributes = new ArrayList();
    private boolean leaveOffEmpty = false;

    public PKCS10CertificationRequestBuilder(PKCS10CertificationRequestBuilder original) {
        this.publicKeyInfo = original.publicKeyInfo;
        this.subject = original.subject;
        this.leaveOffEmpty = original.leaveOffEmpty;
        this.attributes = new ArrayList(original.attributes);
    }

    public PKCS10CertificationRequestBuilder(X500Name subject, SubjectPublicKeyInfo publicKeyInfo) {
        this.subject = subject;
        this.publicKeyInfo = publicKeyInfo;
    }

    public PKCS10CertificationRequestBuilder setAttribute(ASN1ObjectIdentifier attrType, ASN1Encodable attrValue) {
        Iterator it = this.attributes.iterator();
        while (it.hasNext()) {
            if (!((Attribute)it.next()).getAttrType().equals((ASN1Primitive)attrType)) continue;
            throw new IllegalStateException("Attribute " + attrType.toString() + " is already set");
        }
        this.addAttribute(attrType, attrValue);
        return this;
    }

    public PKCS10CertificationRequestBuilder setAttribute(ASN1ObjectIdentifier attrType, ASN1Encodable[] attrValue) {
        Iterator it = this.attributes.iterator();
        while (it.hasNext()) {
            if (!((Attribute)it.next()).getAttrType().equals((ASN1Primitive)attrType)) continue;
            throw new IllegalStateException("Attribute " + attrType.toString() + " is already set");
        }
        this.addAttribute(attrType, attrValue);
        return this;
    }

    public PKCS10CertificationRequestBuilder addAttribute(ASN1ObjectIdentifier attrType, ASN1Encodable attrValue) {
        this.attributes.add(new Attribute(attrType, (ASN1Set)new DERSet(attrValue)));
        return this;
    }

    public PKCS10CertificationRequestBuilder addAttribute(ASN1ObjectIdentifier attrType, ASN1Encodable[] attrValues) {
        this.attributes.add(new Attribute(attrType, (ASN1Set)new DERSet(attrValues)));
        return this;
    }

    public PKCS10CertificationRequestBuilder setLeaveOffEmptyAttributes(boolean leaveOffEmpty) {
        this.leaveOffEmpty = leaveOffEmpty;
        return this;
    }

    public PKCS10CertificationRequest build(ContentSigner signer) {
        CertificationRequestInfo info;
        if (this.attributes.isEmpty()) {
            info = this.leaveOffEmpty ? new CertificationRequestInfo(this.subject, this.publicKeyInfo, null) : new CertificationRequestInfo(this.subject, this.publicKeyInfo, (ASN1Set)new DERSet());
        } else {
            ASN1EncodableVector v = new ASN1EncodableVector();
            Iterator it = this.attributes.iterator();
            while (it.hasNext()) {
                v.add((ASN1Encodable)Attribute.getInstance(it.next()));
            }
            info = new CertificationRequestInfo(this.subject, this.publicKeyInfo, (ASN1Set)new DERSet(v));
        }
        try {
            OutputStream sOut = signer.getOutputStream();
            sOut.write(info.getEncoded("DER"));
            sOut.close();
            return new PKCS10CertificationRequest(new CertificationRequest(info, signer.getAlgorithmIdentifier(), new DERBitString(signer.getSignature())));
        }
        catch (IOException e) {
            throw new IllegalStateException("cannot produce certification request signature");
        }
    }
}

