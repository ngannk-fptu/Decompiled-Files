/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.misc;

import org.bouncycastle.asn1.DERIA5String;

public class VerisignCzagExtension
extends DERIA5String {
    public VerisignCzagExtension(DERIA5String dERIA5String) {
        super(dERIA5String.getString());
    }

    public String toString() {
        return "VerisignCzagExtension: " + this.getString();
    }
}

