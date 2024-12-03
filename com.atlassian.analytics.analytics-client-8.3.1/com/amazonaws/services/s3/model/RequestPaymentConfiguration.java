/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import java.io.Serializable;

public class RequestPaymentConfiguration
implements Serializable {
    private Payer payer;

    public RequestPaymentConfiguration(Payer payer) {
        this.payer = payer;
    }

    public Payer getPayer() {
        return this.payer;
    }

    public void setPayer(Payer payer) {
        this.payer = payer;
    }

    public static enum Payer {
        Requester,
        BucketOwner;

    }
}

