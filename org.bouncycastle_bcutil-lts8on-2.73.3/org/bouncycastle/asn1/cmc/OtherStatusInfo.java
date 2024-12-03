/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.asn1.cmc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cmc.CMCFailInfo;
import org.bouncycastle.asn1.cmc.ExtendedFailInfo;
import org.bouncycastle.asn1.cmc.PendInfo;

public class OtherStatusInfo
extends ASN1Object
implements ASN1Choice {
    private final CMCFailInfo failInfo;
    private final PendInfo pendInfo;
    private final ExtendedFailInfo extendedFailInfo;

    public static OtherStatusInfo getInstance(Object obj) {
        if (obj instanceof OtherStatusInfo) {
            return (OtherStatusInfo)((Object)obj);
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive asn1Value = ((ASN1Encodable)obj).toASN1Primitive();
            if (asn1Value instanceof ASN1Integer) {
                return new OtherStatusInfo(CMCFailInfo.getInstance(asn1Value));
            }
            if (asn1Value instanceof ASN1Sequence) {
                if (((ASN1Sequence)asn1Value).getObjectAt(0) instanceof ASN1ObjectIdentifier) {
                    return new OtherStatusInfo(ExtendedFailInfo.getInstance(asn1Value));
                }
                return new OtherStatusInfo(PendInfo.getInstance(asn1Value));
            }
        } else if (obj instanceof byte[]) {
            try {
                return OtherStatusInfo.getInstance(ASN1Primitive.fromByteArray((byte[])((byte[])obj)));
            }
            catch (IOException e) {
                throw new IllegalArgumentException("parsing error: " + e.getMessage());
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance(): " + obj.getClass().getName());
    }

    OtherStatusInfo(CMCFailInfo failInfo) {
        this(failInfo, null, null);
    }

    OtherStatusInfo(PendInfo pendInfo) {
        this(null, pendInfo, null);
    }

    OtherStatusInfo(ExtendedFailInfo extendedFailInfo) {
        this(null, null, extendedFailInfo);
    }

    private OtherStatusInfo(CMCFailInfo failInfo, PendInfo pendInfo, ExtendedFailInfo extendedFailInfo) {
        this.failInfo = failInfo;
        this.pendInfo = pendInfo;
        this.extendedFailInfo = extendedFailInfo;
    }

    public boolean isPendingInfo() {
        return this.pendInfo != null;
    }

    public boolean isFailInfo() {
        return this.failInfo != null;
    }

    public boolean isExtendedFailInfo() {
        return this.extendedFailInfo != null;
    }

    public ASN1Primitive toASN1Primitive() {
        if (this.pendInfo != null) {
            return this.pendInfo.toASN1Primitive();
        }
        if (this.failInfo != null) {
            return this.failInfo.toASN1Primitive();
        }
        return this.extendedFailInfo.toASN1Primitive();
    }
}

