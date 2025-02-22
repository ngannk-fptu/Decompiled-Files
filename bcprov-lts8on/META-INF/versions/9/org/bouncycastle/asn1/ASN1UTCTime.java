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
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.LocaleUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ASN1UTCTime
extends ASN1Primitive {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1UTCTime.class, 23){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString octetString) {
            return ASN1UTCTime.createPrimitive(octetString.getOctets());
        }
    };
    final byte[] contents;

    public static ASN1UTCTime getInstance(Object obj) {
        ASN1Primitive primitive;
        if (obj == null || obj instanceof ASN1UTCTime) {
            return (ASN1UTCTime)obj;
        }
        if (obj instanceof ASN1Encodable && (primitive = ((ASN1Encodable)obj).toASN1Primitive()) instanceof ASN1UTCTime) {
            return (ASN1UTCTime)primitive;
        }
        if (obj instanceof byte[]) {
            try {
                return (ASN1UTCTime)TYPE.fromByteArray((byte[])obj);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1UTCTime getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        return (ASN1UTCTime)TYPE.getContextInstance(taggedObject, explicit);
    }

    public ASN1UTCTime(String time) {
        this.contents = Strings.toByteArray(time);
        try {
            this.getDate();
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("invalid date string: " + e.getMessage());
        }
    }

    public ASN1UTCTime(Date time) {
        SimpleDateFormat dateF = new SimpleDateFormat("yyMMddHHmmss'Z'", LocaleUtil.EN_Locale);
        dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.contents = Strings.toByteArray(dateF.format(time));
    }

    public ASN1UTCTime(Date time, Locale locale) {
        SimpleDateFormat dateF = new SimpleDateFormat("yyMMddHHmmss'Z'", locale);
        dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.contents = Strings.toByteArray(dateF.format(time));
    }

    ASN1UTCTime(byte[] contents) {
        if (contents.length < 2) {
            throw new IllegalArgumentException("UTCTime string too short");
        }
        this.contents = contents;
        if (!this.isDigit(0) || !this.isDigit(1)) {
            throw new IllegalArgumentException("illegal characters in UTCTime string");
        }
    }

    public Date getDate() throws ParseException {
        SimpleDateFormat dateF = new SimpleDateFormat("yyMMddHHmmssz", LocaleUtil.EN_Locale);
        return dateF.parse(this.getTime());
    }

    public Date getAdjustedDate() throws ParseException {
        SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMddHHmmssz", LocaleUtil.EN_Locale);
        dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
        return dateF.parse(this.getAdjustedTime());
    }

    public String getTime() {
        String stime = Strings.fromByteArray(this.contents);
        if (stime.indexOf(45) < 0 && stime.indexOf(43) < 0) {
            if (stime.length() == 11) {
                return stime.substring(0, 10) + "00GMT+00:00";
            }
            return stime.substring(0, 12) + "GMT+00:00";
        }
        int index = stime.indexOf(45);
        if (index < 0) {
            index = stime.indexOf(43);
        }
        Object d = stime;
        if (index == stime.length() - 3) {
            d = (String)d + "00";
        }
        if (index == 10) {
            return ((String)d).substring(0, 10) + "00GMT" + ((String)d).substring(10, 13) + ":" + ((String)d).substring(13, 15);
        }
        return ((String)d).substring(0, 12) + "GMT" + ((String)d).substring(12, 15) + ":" + ((String)d).substring(15, 17);
    }

    public String getAdjustedTime() {
        String d = this.getTime();
        if (d.charAt(0) < '5') {
            return "20" + d;
        }
        return "19" + d;
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
        out.writeEncodingDL(withTag, 23, this.contents);
    }

    @Override
    boolean asn1Equals(ASN1Primitive o) {
        if (!(o instanceof ASN1UTCTime)) {
            return false;
        }
        return Arrays.areEqual(this.contents, ((ASN1UTCTime)o).contents);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    public String toString() {
        return Strings.fromByteArray(this.contents);
    }

    static ASN1UTCTime createPrimitive(byte[] contents) {
        return new ASN1UTCTime(contents);
    }
}

