/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1NumericString;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DERNumericString
extends ASN1NumericString {
    public DERNumericString(String string) {
        this(string, false);
    }

    public DERNumericString(String string, boolean validate) {
        super(string, validate);
    }

    DERNumericString(byte[] contents, boolean clone) {
        super(contents, clone);
    }
}

