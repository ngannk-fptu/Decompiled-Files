/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.DERBitString;

public class ReasonFlags
extends DERBitString {
    public static final int unused = 128;
    public static final int keyCompromise = 64;
    public static final int cACompromise = 32;
    public static final int affiliationChanged = 16;
    public static final int superseded = 8;
    public static final int cessationOfOperation = 4;
    public static final int certificateHold = 2;
    public static final int privilegeWithdrawn = 1;
    public static final int aACompromise = 32768;

    public ReasonFlags(int reasons) {
        super(ReasonFlags.getBytes(reasons), ReasonFlags.getPadBits(reasons));
    }

    public ReasonFlags(ASN1BitString reasons) {
        super(reasons.getBytes(), reasons.getPadBits());
    }
}

