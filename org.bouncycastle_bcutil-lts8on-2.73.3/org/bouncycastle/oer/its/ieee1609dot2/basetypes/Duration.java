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
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT16;

public class Duration
extends ASN1Object
implements ASN1Choice {
    public static final int microseconds = 0;
    public static final int milliseconds = 1;
    public static final int seconds = 2;
    public static final int minutes = 3;
    public static final int hours = 4;
    public static final int sixtyHours = 5;
    public static final int years = 6;
    private final int choice;
    private final UINT16 duration;

    public Duration(int tag, UINT16 value) {
        this.choice = tag;
        this.duration = value;
    }

    private Duration(ASN1TaggedObject taggedObject) {
        this.choice = taggedObject.getTagNo();
        switch (this.choice) {
            case 0: 
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: {
                try {
                    this.duration = UINT16.getInstance(taggedObject.getExplicitBaseObject());
                    break;
                }
                catch (Exception ioex) {
                    throw new IllegalStateException(ioex.getMessage(), ioex);
                }
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + this.choice);
            }
        }
    }

    public static Duration getInstance(Object o) {
        if (o instanceof Duration) {
            return (Duration)((Object)o);
        }
        if (o != null) {
            return new Duration(ASN1TaggedObject.getInstance((Object)o, (int)128));
        }
        return null;
    }

    public static Duration years(UINT16 value) {
        return new Duration(6, value);
    }

    public static Duration sixtyHours(UINT16 value) {
        return new Duration(5, value);
    }

    public static Duration hours(UINT16 value) {
        return new Duration(4, value);
    }

    public static Duration minutes(UINT16 value) {
        return new Duration(3, value);
    }

    public static Duration seconds(UINT16 value) {
        return new Duration(2, value);
    }

    public static Duration milliseconds(UINT16 value) {
        return new Duration(1, value);
    }

    public static Duration microseconds(UINT16 value) {
        return new Duration(0, value);
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, (ASN1Encodable)this.duration);
    }

    public int getChoice() {
        return this.choice;
    }

    public UINT16 getDuration() {
        return this.duration;
    }

    public String toString() {
        switch (this.choice) {
            case 0: {
                return this.duration.value + "uS";
            }
            case 1: {
                return this.duration.value + "mS";
            }
            case 2: {
                return this.duration.value + " seconds";
            }
            case 3: {
                return this.duration.value + " minute";
            }
            case 4: {
                return this.duration.value + " hours";
            }
            case 5: {
                return this.duration.value + " sixty hours";
            }
            case 6: {
                return this.duration.value + " years";
            }
        }
        return this.duration.value + " unknown choice";
    }
}

