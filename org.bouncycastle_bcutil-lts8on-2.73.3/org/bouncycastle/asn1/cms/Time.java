/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1GeneralizedTime
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.ASN1UTCTime
 *  org.bouncycastle.asn1.DERGeneralizedTime
 *  org.bouncycastle.asn1.DERUTCTime
 *  org.bouncycastle.asn1.LocaleUtil
 */
package org.bouncycastle.asn1.cms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.DERUTCTime;
import org.bouncycastle.asn1.LocaleUtil;

public class Time
extends ASN1Object
implements ASN1Choice {
    ASN1Primitive time;

    public static Time getInstance(ASN1TaggedObject obj, boolean explicit) {
        if (!explicit) {
            throw new IllegalArgumentException("choice item must be explicitly tagged");
        }
        return Time.getInstance(obj.getExplicitBaseObject());
    }

    private Time(ASN1Primitive time) {
        if (!(time instanceof ASN1UTCTime) && !(time instanceof ASN1GeneralizedTime)) {
            throw new IllegalArgumentException("unknown object passed to Time");
        }
        this.time = time;
    }

    public Time(Date time) {
        SimpleTimeZone tz = new SimpleTimeZone(0, "Z");
        SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMddHHmmss", LocaleUtil.EN_Locale);
        dateF.setTimeZone(tz);
        String d = dateF.format(time) + "Z";
        int year = Integer.parseInt(d.substring(0, 4));
        this.time = year < 1950 || year > 2049 ? new DERGeneralizedTime(d) : new DERUTCTime(d.substring(2));
    }

    public Time(Date time, Locale locale) {
        SimpleTimeZone tz = new SimpleTimeZone(0, "Z");
        SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMddHHmmss", locale);
        dateF.setTimeZone(tz);
        String d = dateF.format(time) + "Z";
        int year = Integer.parseInt(d.substring(0, 4));
        this.time = year < 1950 || year > 2049 ? new DERGeneralizedTime(d) : new DERUTCTime(d.substring(2));
    }

    public static Time getInstance(Object obj) {
        if (obj == null || obj instanceof Time) {
            return (Time)((Object)obj);
        }
        if (obj instanceof ASN1UTCTime) {
            return new Time((ASN1Primitive)((ASN1UTCTime)obj));
        }
        if (obj instanceof ASN1GeneralizedTime) {
            return new Time((ASN1Primitive)((ASN1GeneralizedTime)obj));
        }
        throw new IllegalArgumentException("unknown object in factory: " + obj.getClass().getName());
    }

    public String getTime() {
        if (this.time instanceof ASN1UTCTime) {
            return ((ASN1UTCTime)this.time).getAdjustedTime();
        }
        return ((ASN1GeneralizedTime)this.time).getTime();
    }

    public Date getDate() {
        try {
            if (this.time instanceof ASN1UTCTime) {
                return ((ASN1UTCTime)this.time).getAdjustedDate();
            }
            return ((ASN1GeneralizedTime)this.time).getDate();
        }
        catch (ParseException e) {
            throw new IllegalStateException("invalid date string: " + e.getMessage());
        }
    }

    public ASN1Primitive toASN1Primitive() {
        return this.time;
    }
}

