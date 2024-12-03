/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1PrintableString;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DERPrintableString
extends ASN1PrintableString {
    public DERPrintableString(String string) {
        this(string, false);
    }

    public DERPrintableString(String string, boolean validate) {
        super(string, validate);
    }

    DERPrintableString(byte[] contents, boolean clone) {
        super(contents, clone);
    }
}

