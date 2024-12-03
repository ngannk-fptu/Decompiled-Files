/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1UTF8String;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DERUTF8String
extends ASN1UTF8String {
    public DERUTF8String(String string) {
        super(string);
    }

    DERUTF8String(byte[] contents, boolean clone) {
        super(contents, clone);
    }
}

