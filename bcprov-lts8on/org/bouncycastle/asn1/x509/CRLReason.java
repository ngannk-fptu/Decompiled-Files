/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.math.BigInteger;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Integers;

public class CRLReason
extends ASN1Object {
    public static final int unspecified = 0;
    public static final int keyCompromise = 1;
    public static final int cACompromise = 2;
    public static final int affiliationChanged = 3;
    public static final int superseded = 4;
    public static final int cessationOfOperation = 5;
    public static final int certificateHold = 6;
    public static final int removeFromCRL = 8;
    public static final int privilegeWithdrawn = 9;
    public static final int aACompromise = 10;
    private static final String[] reasonString = new String[]{"unspecified", "keyCompromise", "cACompromise", "affiliationChanged", "superseded", "cessationOfOperation", "certificateHold", "unknown", "removeFromCRL", "privilegeWithdrawn", "aACompromise"};
    private static final Hashtable table = new Hashtable();
    private ASN1Enumerated value;

    public static CRLReason getInstance(Object o) {
        if (o instanceof CRLReason) {
            return (CRLReason)o;
        }
        if (o != null) {
            return CRLReason.lookup(ASN1Enumerated.getInstance(o).intValueExact());
        }
        return null;
    }

    private CRLReason(int reason) {
        if (reason < 0) {
            throw new IllegalArgumentException("Invalid CRL reason : not in (0..MAX)");
        }
        this.value = new ASN1Enumerated(reason);
    }

    public String toString() {
        int reason = this.getValue().intValue();
        String str = reason < 0 || reason > 10 ? "invalid" : reasonString[reason];
        return "CRLReason: " + str;
    }

    public BigInteger getValue() {
        return this.value.getValue();
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.value;
    }

    public static CRLReason lookup(int value) {
        Integer idx = Integers.valueOf(value);
        if (!table.containsKey(idx)) {
            table.put(idx, new CRLReason(value));
        }
        return (CRLReason)table.get(idx);
    }
}

