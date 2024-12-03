/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.util.Date;
import org.bouncycastle.asn1.ASN1UTCTime;

public class DERUTCTime
extends ASN1UTCTime {
    DERUTCTime(byte[] bytes) {
        super(bytes);
    }

    public DERUTCTime(Date time) {
        super(time);
    }

    public DERUTCTime(String time) {
        super(time);
    }
}

