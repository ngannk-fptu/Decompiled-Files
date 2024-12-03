/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.BitmapSspRange;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfOctetString;

public class SspRange
extends ASN1Object
implements ASN1Choice {
    public static final int opaque = 0;
    public static final int all = 1;
    public static final int bitmapSspRange = 2;
    private final int choice;
    private final ASN1Encodable sspRange;

    public static SspRange opaque(SequenceOfOctetString bytes) {
        return new SspRange(0, (ASN1Encodable)bytes);
    }

    public static SspRange all() {
        return new SspRange(1, (ASN1Encodable)DERNull.INSTANCE);
    }

    public static SspRange bitmapSspRange(BitmapSspRange ext) {
        return new SspRange(2, (ASN1Encodable)ext);
    }

    public SspRange(int choice, ASN1Encodable value) {
        switch (choice) {
            case 0: 
            case 1: 
            case 2: {
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + choice);
            }
        }
        this.choice = choice;
        this.sspRange = value;
    }

    private SspRange(ASN1TaggedObject ato) {
        this(ato.getTagNo(), (ASN1Encodable)ato.getExplicitBaseObject());
    }

    public static SspRange getInstance(Object src) {
        if (src instanceof SspRange) {
            return (SspRange)((Object)src);
        }
        if (src != null) {
            return new SspRange(ASN1TaggedObject.getInstance((Object)src, (int)128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getSspRange() {
        return this.sspRange;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.sspRange);
    }
}

