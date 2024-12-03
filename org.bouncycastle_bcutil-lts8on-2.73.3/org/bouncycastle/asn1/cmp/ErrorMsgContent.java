/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.cmp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.PKIFreeText;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;

public class ErrorMsgContent
extends ASN1Object {
    private final PKIStatusInfo pkiStatusInfo;
    private ASN1Integer errorCode;
    private PKIFreeText errorDetails;

    private ErrorMsgContent(ASN1Sequence seq) {
        Enumeration en = seq.getObjects();
        this.pkiStatusInfo = PKIStatusInfo.getInstance(en.nextElement());
        while (en.hasMoreElements()) {
            Object o = en.nextElement();
            if (o instanceof ASN1Integer) {
                this.errorCode = ASN1Integer.getInstance(o);
                continue;
            }
            this.errorDetails = PKIFreeText.getInstance(o);
        }
    }

    public ErrorMsgContent(PKIStatusInfo pkiStatusInfo) {
        this(pkiStatusInfo, null, null);
    }

    public ErrorMsgContent(PKIStatusInfo pkiStatusInfo, ASN1Integer errorCode, PKIFreeText errorDetails) {
        if (pkiStatusInfo == null) {
            throw new IllegalArgumentException("'pkiStatusInfo' cannot be null");
        }
        this.pkiStatusInfo = pkiStatusInfo;
        this.errorCode = errorCode;
        this.errorDetails = errorDetails;
    }

    public static ErrorMsgContent getInstance(Object o) {
        if (o instanceof ErrorMsgContent) {
            return (ErrorMsgContent)((Object)o);
        }
        if (o != null) {
            return new ErrorMsgContent(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public PKIStatusInfo getPKIStatusInfo() {
        return this.pkiStatusInfo;
    }

    public ASN1Integer getErrorCode() {
        return this.errorCode;
    }

    public PKIFreeText getErrorDetails() {
        return this.errorDetails;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.pkiStatusInfo);
        this.addOptional(v, (ASN1Encodable)this.errorCode);
        this.addOptional(v, (ASN1Encodable)this.errorDetails);
        return new DERSequence(v);
    }

    private void addOptional(ASN1EncodableVector v, ASN1Encodable obj) {
        if (obj != null) {
            v.add(obj);
        }
    }
}

