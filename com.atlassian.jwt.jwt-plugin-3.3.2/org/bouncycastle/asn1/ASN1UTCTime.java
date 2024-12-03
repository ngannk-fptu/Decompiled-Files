/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DateUtil;
import org.bouncycastle.asn1.StreamUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class ASN1UTCTime
extends ASN1Primitive {
    private byte[] time;

    public static ASN1UTCTime getInstance(Object object) {
        if (object == null || object instanceof ASN1UTCTime) {
            return (ASN1UTCTime)object;
        }
        if (object instanceof byte[]) {
            try {
                return (ASN1UTCTime)ASN1UTCTime.fromByteArray((byte[])object);
            }
            catch (Exception exception) {
                throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static ASN1UTCTime getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        ASN1Primitive aSN1Primitive = aSN1TaggedObject.getObject();
        if (bl || aSN1Primitive instanceof ASN1UTCTime) {
            return ASN1UTCTime.getInstance(aSN1Primitive);
        }
        return new ASN1UTCTime(ASN1OctetString.getInstance(aSN1Primitive).getOctets());
    }

    public ASN1UTCTime(String string) {
        this.time = Strings.toByteArray(string);
        try {
            this.getDate();
        }
        catch (ParseException parseException) {
            throw new IllegalArgumentException("invalid date string: " + parseException.getMessage());
        }
    }

    public ASN1UTCTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss'Z'", DateUtil.EN_Locale);
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.time = Strings.toByteArray(simpleDateFormat.format(date));
    }

    public ASN1UTCTime(Date date, Locale locale) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss'Z'", locale);
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.time = Strings.toByteArray(simpleDateFormat.format(date));
    }

    ASN1UTCTime(byte[] byArray) {
        if (byArray.length < 2) {
            throw new IllegalArgumentException("UTCTime string too short");
        }
        this.time = byArray;
        if (!this.isDigit(0) || !this.isDigit(1)) {
            throw new IllegalArgumentException("illegal characters in UTCTime string");
        }
    }

    public Date getDate() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmssz");
        return DateUtil.epochAdjust(simpleDateFormat.parse(this.getTime()));
    }

    public Date getAdjustedDate() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssz");
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        return DateUtil.epochAdjust(simpleDateFormat.parse(this.getAdjustedTime()));
    }

    public String getTime() {
        String string = Strings.fromByteArray(this.time);
        if (string.indexOf(45) < 0 && string.indexOf(43) < 0) {
            if (string.length() == 11) {
                return string.substring(0, 10) + "00GMT+00:00";
            }
            return string.substring(0, 12) + "GMT+00:00";
        }
        int n = string.indexOf(45);
        if (n < 0) {
            n = string.indexOf(43);
        }
        Object object = string;
        if (n == string.length() - 3) {
            object = (String)object + "00";
        }
        if (n == 10) {
            return ((String)object).substring(0, 10) + "00GMT" + ((String)object).substring(10, 13) + ":" + ((String)object).substring(13, 15);
        }
        return ((String)object).substring(0, 12) + "GMT" + ((String)object).substring(12, 15) + ":" + ((String)object).substring(15, 17);
    }

    public String getAdjustedTime() {
        String string = this.getTime();
        if (string.charAt(0) < '5') {
            return "20" + string;
        }
        return "19" + string;
    }

    private boolean isDigit(int n) {
        return this.time.length > n && this.time[n] >= 48 && this.time[n] <= 57;
    }

    @Override
    boolean isConstructed() {
        return false;
    }

    @Override
    int encodedLength() {
        int n = this.time.length;
        return 1 + StreamUtil.calculateBodyLength(n) + n;
    }

    @Override
    void encode(ASN1OutputStream aSN1OutputStream, boolean bl) throws IOException {
        aSN1OutputStream.writeEncoded(bl, 23, this.time);
    }

    @Override
    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1UTCTime)) {
            return false;
        }
        return Arrays.areEqual(this.time, ((ASN1UTCTime)aSN1Primitive).time);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.time);
    }

    public String toString() {
        return Strings.fromByteArray(this.time);
    }
}

