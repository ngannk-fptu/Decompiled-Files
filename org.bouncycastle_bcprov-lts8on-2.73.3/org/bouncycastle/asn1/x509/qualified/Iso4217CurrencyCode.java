/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509.qualified;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1PrintableString;
import org.bouncycastle.asn1.DERPrintableString;

public class Iso4217CurrencyCode
extends ASN1Object
implements ASN1Choice {
    final int ALPHABETIC_MAXSIZE = 3;
    final int NUMERIC_MINSIZE = 1;
    final int NUMERIC_MAXSIZE = 999;
    ASN1Encodable obj;
    int numeric;

    public static Iso4217CurrencyCode getInstance(Object obj) {
        if (obj == null || obj instanceof Iso4217CurrencyCode) {
            return (Iso4217CurrencyCode)obj;
        }
        if (obj instanceof ASN1Integer) {
            ASN1Integer numericobj = ASN1Integer.getInstance(obj);
            int numeric = numericobj.intValueExact();
            return new Iso4217CurrencyCode(numeric);
        }
        if (obj instanceof ASN1PrintableString) {
            ASN1PrintableString alphabetic = ASN1PrintableString.getInstance(obj);
            return new Iso4217CurrencyCode(alphabetic.getString());
        }
        throw new IllegalArgumentException("unknown object in getInstance");
    }

    public Iso4217CurrencyCode(int numeric) {
        if (numeric > 999 || numeric < 1) {
            throw new IllegalArgumentException("wrong size in numeric code : not in (1..999)");
        }
        this.obj = new ASN1Integer(numeric);
    }

    public Iso4217CurrencyCode(String alphabetic) {
        if (alphabetic.length() > 3) {
            throw new IllegalArgumentException("wrong size in alphabetic code : max size is 3");
        }
        this.obj = new DERPrintableString(alphabetic);
    }

    public boolean isAlphabetic() {
        return this.obj instanceof ASN1PrintableString;
    }

    public String getAlphabetic() {
        return ((ASN1PrintableString)this.obj).getString();
    }

    public int getNumeric() {
        return ((ASN1Integer)this.obj).intValueExact();
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.obj.toASN1Primitive();
    }
}

