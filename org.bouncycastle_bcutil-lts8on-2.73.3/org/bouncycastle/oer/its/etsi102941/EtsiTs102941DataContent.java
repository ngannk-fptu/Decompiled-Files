/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.etsi102941.AuthorizationValidationRequest;
import org.bouncycastle.oer.its.etsi102941.AuthorizationValidationResponse;
import org.bouncycastle.oer.its.etsi102941.CaCertificateRequest;
import org.bouncycastle.oer.its.etsi102941.InnerAtRequest;
import org.bouncycastle.oer.its.etsi102941.InnerAtResponse;
import org.bouncycastle.oer.its.etsi102941.InnerEcRequestSignedForPop;
import org.bouncycastle.oer.its.etsi102941.InnerEcResponse;
import org.bouncycastle.oer.its.etsi102941.ToBeSignedRcaCtl;
import org.bouncycastle.oer.its.etsi102941.ToBeSignedTlmCtl;

public class EtsiTs102941DataContent
extends ASN1Object
implements ASN1Choice {
    public static final int enrolmentRequest = 0;
    public static final int enrolmentResponse = 1;
    public static final int authorizationRequest = 2;
    public static final int authorizationResponse = 3;
    public static final int certificateRevocationList = 4;
    public static final int certificateTrustListTlm = 5;
    public static final int certificateTrustListRca = 6;
    public static final int authorizationValidationRequest = 7;
    public static final int authorizationValidationResponse = 8;
    public static final int caCertificateRequest = 9;
    public static final int linkCertificateTlm = 10;
    public static final int singleSignedLinkCertificateRca = 11;
    public static final int doubleSignedlinkCertificateRca = 12;
    private final int choice;
    private final ASN1Encodable etsiTs102941DataContent;

    public EtsiTs102941DataContent(int choice, ASN1Encodable etsiTs102941DataContent) {
        this.choice = choice;
        this.etsiTs102941DataContent = etsiTs102941DataContent;
    }

    private EtsiTs102941DataContent(ASN1TaggedObject asn1TaggedObject) {
        this.choice = asn1TaggedObject.getTagNo();
        switch (this.choice) {
            case 0: {
                this.etsiTs102941DataContent = InnerEcRequestSignedForPop.getInstance(asn1TaggedObject.getExplicitBaseObject());
                return;
            }
            case 1: {
                this.etsiTs102941DataContent = InnerEcResponse.getInstance(asn1TaggedObject.getExplicitBaseObject());
                return;
            }
            case 2: {
                this.etsiTs102941DataContent = InnerAtRequest.getInstance(asn1TaggedObject.getExplicitBaseObject());
                return;
            }
            case 3: {
                this.etsiTs102941DataContent = InnerAtResponse.getInstance(asn1TaggedObject.getExplicitBaseObject());
                return;
            }
            case 5: {
                this.etsiTs102941DataContent = ToBeSignedTlmCtl.getInstance(asn1TaggedObject.getExplicitBaseObject());
                return;
            }
            case 6: {
                this.etsiTs102941DataContent = ToBeSignedRcaCtl.getInstance(asn1TaggedObject.getExplicitBaseObject());
                return;
            }
            case 7: {
                this.etsiTs102941DataContent = AuthorizationValidationRequest.getInstance(asn1TaggedObject.getExplicitBaseObject());
                return;
            }
            case 8: {
                this.etsiTs102941DataContent = AuthorizationValidationResponse.getInstance(asn1TaggedObject.getExplicitBaseObject());
                return;
            }
            case 9: {
                this.etsiTs102941DataContent = CaCertificateRequest.getInstance(asn1TaggedObject.getExplicitBaseObject());
                return;
            }
        }
        throw new IllegalArgumentException("choice not implemented " + this.choice);
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getEtsiTs102941DataContent() {
        return this.etsiTs102941DataContent;
    }

    public static EtsiTs102941DataContent getInstance(Object o) {
        if (o instanceof EtsiTs102941DataContent) {
            return (EtsiTs102941DataContent)((Object)o);
        }
        if (o != null) {
            return new EtsiTs102941DataContent(ASN1TaggedObject.getInstance((Object)o, (int)128));
        }
        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.etsiTs102941DataContent);
    }
}

