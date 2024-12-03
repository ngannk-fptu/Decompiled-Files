/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1GeneralizedTime
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 */
package org.bouncycastle.asn1.dvcs;

import java.util.Date;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.cms.ContentInfo;

public class DVCSTime
extends ASN1Object
implements ASN1Choice {
    private final ASN1GeneralizedTime genTime;
    private final ContentInfo timeStampToken;

    public DVCSTime(Date time) {
        this(new ASN1GeneralizedTime(time));
    }

    public DVCSTime(ASN1GeneralizedTime genTime) {
        this.genTime = genTime;
        this.timeStampToken = null;
    }

    public DVCSTime(ContentInfo timeStampToken) {
        this.genTime = null;
        this.timeStampToken = timeStampToken;
    }

    public static DVCSTime getInstance(Object obj) {
        if (obj instanceof DVCSTime) {
            return (DVCSTime)((Object)obj);
        }
        if (obj instanceof ASN1GeneralizedTime) {
            return new DVCSTime(ASN1GeneralizedTime.getInstance((Object)obj));
        }
        if (obj != null) {
            return new DVCSTime(ContentInfo.getInstance(obj));
        }
        return null;
    }

    public static DVCSTime getInstance(ASN1TaggedObject obj, boolean explicit) {
        if (!explicit) {
            throw new IllegalArgumentException("choice item must be explicitly tagged");
        }
        return DVCSTime.getInstance(ASN1TaggedObject.getInstance((Object)obj, (int)128).getExplicitBaseObject());
    }

    public ASN1GeneralizedTime getGenTime() {
        return this.genTime;
    }

    public ContentInfo getTimeStampToken() {
        return this.timeStampToken;
    }

    public ASN1Primitive toASN1Primitive() {
        if (this.genTime != null) {
            return this.genTime;
        }
        return this.timeStampToken.toASN1Primitive();
    }

    public String toString() {
        if (this.genTime != null) {
            return this.genTime.toString();
        }
        return this.timeStampToken.toString();
    }
}

