/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1VideotexString;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DERVideotexString
extends ASN1VideotexString {
    public DERVideotexString(byte[] octets) {
        this(octets, true);
    }

    DERVideotexString(byte[] contents, boolean clone) {
        super(contents, clone);
    }
}

