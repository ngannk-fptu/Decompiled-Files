/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x500.style;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.asn1.x500.style.BCStyle;

public class BCStrictStyle
extends BCStyle {
    public static final X500NameStyle INSTANCE = new BCStrictStyle();

    @Override
    public boolean areEqual(X500Name name1, X500Name name2) {
        RDN[] rdns2;
        RDN[] rdns1 = name1.getRDNs();
        if (rdns1.length != (rdns2 = name2.getRDNs()).length) {
            return false;
        }
        for (int i = 0; i != rdns1.length; ++i) {
            if (this.rdnAreEqual(rdns1[i], rdns2[i])) continue;
            return false;
        }
        return true;
    }
}

