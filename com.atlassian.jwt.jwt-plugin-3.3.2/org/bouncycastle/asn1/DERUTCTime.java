/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.util.Date;
import org.bouncycastle.asn1.ASN1UTCTime;

public class DERUTCTime
extends ASN1UTCTime {
    DERUTCTime(byte[] byArray) {
        super(byArray);
    }

    public DERUTCTime(Date date) {
        super(date);
    }

    public DERUTCTime(String string) {
        super(string);
    }
}

