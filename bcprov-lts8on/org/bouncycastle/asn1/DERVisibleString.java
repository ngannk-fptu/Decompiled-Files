/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1VisibleString;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DERVisibleString
extends ASN1VisibleString {
    public DERVisibleString(String string) {
        super(string);
    }

    DERVisibleString(byte[] contents, boolean clone) {
        super(contents, clone);
    }
}

