/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1BMPString;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DERBMPString
extends ASN1BMPString {
    public DERBMPString(String string) {
        super(string);
    }

    DERBMPString(byte[] contents) {
        super(contents);
    }

    DERBMPString(char[] string) {
        super(string);
    }
}

