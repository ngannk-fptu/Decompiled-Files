/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.tsp.Accuracy
 */
package org.bouncycastle.tsp;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.tsp.Accuracy;

public class GenTimeAccuracy {
    private Accuracy accuracy;

    public GenTimeAccuracy(Accuracy accuracy) {
        this.accuracy = accuracy;
    }

    public int getSeconds() {
        return this.getTimeComponent(this.accuracy.getSeconds());
    }

    public int getMillis() {
        return this.getTimeComponent(this.accuracy.getMillis());
    }

    public int getMicros() {
        return this.getTimeComponent(this.accuracy.getMicros());
    }

    private int getTimeComponent(ASN1Integer time) {
        if (time != null) {
            return time.intValueExact();
        }
        return 0;
    }

    public String toString() {
        return this.getSeconds() + "." + this.format(this.getMillis()) + this.format(this.getMicros());
    }

    private String format(int v) {
        if (v < 10) {
            return "00" + v;
        }
        if (v < 100) {
            return "0" + v;
        }
        return Integer.toString(v);
    }
}

