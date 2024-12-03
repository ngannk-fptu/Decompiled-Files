/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.X509NameEntryConverter;

public class X509DefaultEntryConverter
extends X509NameEntryConverter {
    @Override
    public ASN1Primitive getConvertedValue(ASN1ObjectIdentifier oid, String value) {
        if (value.length() != 0 && value.charAt(0) == '#') {
            try {
                return this.convertHexEncoded(value, 1);
            }
            catch (IOException e) {
                throw new RuntimeException("can't recode value for oid " + oid.getId());
            }
        }
        if (value.length() != 0 && value.charAt(0) == '\\') {
            value = value.substring(1);
        }
        if (oid.equals(BCStyle.EmailAddress) || oid.equals(BCStyle.DC)) {
            return new DERIA5String(value);
        }
        if (oid.equals(BCStyle.DATE_OF_BIRTH)) {
            return new DERGeneralizedTime(value);
        }
        if (oid.equals(BCStyle.C) || oid.equals(BCStyle.SERIALNUMBER) || oid.equals(BCStyle.DN_QUALIFIER) || oid.equals(BCStyle.TELEPHONE_NUMBER)) {
            return new DERPrintableString(value);
        }
        return new DERUTF8String(value);
    }
}

