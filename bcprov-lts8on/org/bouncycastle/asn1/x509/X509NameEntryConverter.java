/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1PrintableString;
import org.bouncycastle.util.encoders.Hex;

public abstract class X509NameEntryConverter {
    protected ASN1Primitive convertHexEncoded(String str, int off) throws IOException {
        return ASN1Primitive.fromByteArray(Hex.decodeStrict(str, off, str.length() - off));
    }

    protected boolean canBePrintable(String str) {
        return ASN1PrintableString.isPrintableString(str);
    }

    public abstract ASN1Primitive getConvertedValue(ASN1ObjectIdentifier var1, String var2);
}

