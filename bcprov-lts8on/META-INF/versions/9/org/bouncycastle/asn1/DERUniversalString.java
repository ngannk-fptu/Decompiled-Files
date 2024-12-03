/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1UniversalString;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DERUniversalString
extends ASN1UniversalString {
    public DERUniversalString(byte[] string) {
        this(string, true);
    }

    DERUniversalString(byte[] contents, boolean clone) {
        super(contents, clone);
    }
}

