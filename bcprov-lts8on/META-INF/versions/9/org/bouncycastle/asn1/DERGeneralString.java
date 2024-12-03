/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1GeneralString;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DERGeneralString
extends ASN1GeneralString {
    public DERGeneralString(String string) {
        super(string);
    }

    DERGeneralString(byte[] contents, boolean clone) {
        super(contents, clone);
    }
}

