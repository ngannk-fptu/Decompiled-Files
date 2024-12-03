/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1GraphicString;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DERGraphicString
extends ASN1GraphicString {
    public DERGraphicString(byte[] octets) {
        this(octets, true);
    }

    DERGraphicString(byte[] contents, boolean clone) {
        super(contents, clone);
    }
}

