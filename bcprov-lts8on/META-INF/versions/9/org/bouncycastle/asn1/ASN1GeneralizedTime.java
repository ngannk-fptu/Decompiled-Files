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
import java.util.TimeZone;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.LocaleUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ASN1GeneralizedTime
extends ASN1Primitive {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1GeneralizedTime.class, 24){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString octetString) {
            return ASN1GeneralizedTime.createPrimitive(octetString.getOctets());
        }
    };
    final byte[] contents;

    public static ASN1GeneralizedTime getInstance(Object obj) {
        ASN1Primitive primitive;
        if (obj == null || obj instanceof ASN1GeneralizedTime) {
            return (ASN1GeneralizedTime)obj;
        }
        if (obj instanceof ASN1Encodable && (primitive = ((ASN1Encodable)obj).toASN1Primitive()) instanceof ASN1GeneralizedTime) {
            return (ASN1GeneralizedTime)primitive;
        }
        if (obj instanceof byte[]) {
            try {
                return (ASN1GeneralizedTime)TYPE.fromByteArray((byte[])obj);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1GeneralizedTime getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        return (ASN1GeneralizedTime)TYPE.getContextInstance(taggedObject, explicit);
    }

    public ASN1GeneralizedTime(String time) {
        this.contents = Strings.toByteArray(time);
        try {
            this.getDate();
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("invalid date string: " + e.getMessage());
        }
    }

    public ASN1GeneralizedTime(Date time) {
        SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMddHHmmss'Z'", LocaleUtil.EN_Locale);
        dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.contents = Strings.toByteArray(dateF.format(time));
    }

    public ASN1GeneralizedTime(Date time, Locale locale) {
        SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMddHHmmss'Z'", locale);
        dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.contents = Strings.toByteArray(dateF.format(time));
    }

    ASN1GeneralizedTime(byte[] bytes) {
        if (bytes.length < 4) {
            throw new IllegalArgumentException("GeneralizedTime string too short");
        }
        this.contents = bytes;
        if (!(this.isDigit(0) && this.isDigit(1) && this.isDigit(2) && this.isDigit(3))) {
            throw new IllegalArgumentException("illegal characters in GeneralizedTime string");
        }
    }

    public String getTimeString() {
        return Strings.fromByteArray(this.contents);
    }

    public String getTime() {
        String stime = Strings.fromByteArray(this.contents);
        if (stime.charAt(stime.length() - 1) == 'Z') {
            return stime.substring(0, stime.length() - 1) + "GMT+00:00";
        }
        int signPos = stime.length() - 6;
        char sign = stime.charAt(signPos);
        if ((sign == '-' || sign == '+') && stime.indexOf("GMT") == signPos - 3) {
            return stime;
        }
        signPos = stime.length() - 5;
        sign = stime.charAt(signPos);
        if (sign == '-' || sign == '+') {
            return stime.substring(0, signPos) + "GMT" + stime.substring(signPos, signPos + 3) + ":" + stime.substring(signPos + 3);
        }
        signPos = stime.length() - 3;
        sign = stime.charAt(signPos);
        if (sign == '-' || sign == '+') {
            return stime.substring(0, signPos) + "GMT" + stime.substring(signPos) + ":00";
        }
        return stime + this.calculateGMTOffset(stime);
    }

    private String calculateGMTOffset(String stime) {
        String sign = "+";
        TimeZone timeZone = TimeZone.getDefault();
        int offset = timeZone.getRawOffset();
        if (offset < 0) {
            sign = "-";
            offset = -offset;
        }
        int hours = offset / 3600000;
        int minutes = (offset - hours * 60 * 60 * 1000) / 60000;
        try {
            if (timeZone.useDaylightTime()) {
                SimpleDateFormat dateF;
                if (this.hasFractionalSeconds()) {
                    stime = this.pruneFractionalSeconds(stime);
                }
                if (timeZone.inDaylightTime((dateF = this.calculateGMTDateFormat()).parse(stime + "GMT" + sign + this.convert(hours) + ":" + this.convert(minutes)))) {
                    hours += sign.equals("+") ? 1 : -1;
                }
            }
        }
        catch (ParseException parseException) {
            // empty catch block
        }
        return "GMT" + sign + this.convert(hours) + ":" + this.convert(minutes);
    }

    private SimpleDateFormat calculateGMTDateFormat() {
        SimpleDateFormat dateF = this.hasFractionalSeconds() ? new SimpleDateFormat("yyyyMMddHHmmss.SSSz") : (this.hasSeconds() ? new SimpleDateFormat("yyyyMMddHHmmssz") : (this.hasMinutes() ? new SimpleDateFormat("yyyyMMddHHmmz") : new SimpleDateFormat("yyyyMMddHHz")));
        dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
        return dateF;
    }

    private String pruneFractionalSeconds(String origTime) {
        char ch;
        int index;
        Object frac = ((String)origTime).substring(14);
        for (index = 1; index < ((String)frac).length() && '0' <= (ch = ((String)frac).charAt(index)) && ch <= '9'; ++index) {
        }
        if (index - 1 > 3) {
            frac = ((String)frac).substring(0, 4) + ((String)frac).substring(index);
            origTime = ((String)origTime).substring(0, 14) + (String)frac;
        } else if (index - 1 == 1) {
            frac = ((String)frac).substring(0, index) + "00" + ((String)frac).substring(index);
            origTime = ((String)origTime).substring(0, 14) + (String)frac;
        } else if (index - 1 == 2) {
            frac = ((String)frac).substring(0, index) + "0" + ((String)frac).substring(index);
            origTime = ((String)origTime).substring(0, 14) + (String)frac;
        }
        return origTime;
    }

    private String convert(int time) {
        if (time < 10) {
            return "0" + time;
        }
        return Integer.toString(time);
    }

    public Date getDate() throws ParseException {
        SimpleDateFormat dateF;
        String stime;
        String d = stime = Strings.fromByteArray(this.contents);
        if (stime.endsWith("Z")) {
            dateF = this.hasFractionalSeconds() ? new SimpleDateFormat("yyyyMMddHHmmss.SSS'Z'", LocaleUtil.EN_Locale) : (this.hasSeconds() ? new SimpleDateFormat("yyyyMMddHHmmss'Z'", LocaleUtil.EN_Locale) : (this.hasMinutes() ? new SimpleDateFormat("yyyyMMddHHmm'Z'", LocaleUtil.EN_Locale) : new SimpleDateFormat("yyyyMMddHH'Z'", LocaleUtil.EN_Locale)));
            dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
        } else if (stime.indexOf(45) > 0 || stime.indexOf(43) > 0) {
            d = this.getTime();
            dateF = this.calculateGMTDateFormat();
        } else {
            dateF = this.hasFractionalSeconds() ? new SimpleDateFormat("yyyyMMddHHmmss.SSS") : (this.hasSeconds() ? new SimpleDateFormat("yyyyMMddHHmmss") : (this.hasMinutes() ? new SimpleDateFormat("yyyyMMddHHmm") : new SimpleDateFormat("yyyyMMddHH")));
            dateF.setTimeZone(new SimpleTimeZone(0, TimeZone.getDefault().getID()));
        }
        if (this.hasFractionalSeconds()) {
            d = this.pruneFractionalSeconds(d);
        }
        return dateF.parse(d);
    }

    protected boolean hasFractionalSeconds() {
        for (int i = 0; i != this.contents.length; ++i) {
            if (this.contents[i] != 46 || i != 14) continue;
            return true;
        }
        return false;
    }

    protected boolean hasSeconds() {
        return this.isDigit(12) && this.isDigit(13);
    }

    protected boolean hasMinutes() {
        return this.isDigit(10) && this.isDigit(11);
    }

    private boolean isDigit(int pos) {
        return this.contents.length > pos && this.contents[pos] >= 48 && this.contents[pos] <= 57;
    }

    @Override
    final boolean encodeConstructed() {
        return false;
    }

    @Override
    int encodedLength(boolean withTag) {
        return ASN1OutputStream.getLengthOfEncodingDL(withTag, this.contents.length);
    }

    @Override
    void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        out.writeEncodingDL(withTag, 24, this.contents);
    }

    @Override
    ASN1Primitive toDERObject() {
        return new DERGeneralizedTime(this.contents);
    }

    @Override
    boolean asn1Equals(ASN1Primitive o) {
        if (!(o instanceof ASN1GeneralizedTime)) {
            return false;
        }
        return Arrays.areEqual(this.contents, ((ASN1GeneralizedTime)o).contents);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    static ASN1GeneralizedTime createPrimitive(byte[] contents) {
        return new ASN1GeneralizedTime(contents);
    }
}

