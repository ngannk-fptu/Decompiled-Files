/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1T61String;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DERT61String
extends ASN1T61String {
    public DERT61String(String string) {
        super(string);
    }

    public DERT61String(byte[] string) {
        this(string, true);
    }

    DERT61String(byte[] contents, boolean clone) {
        super(contents, clone);
    }
}

