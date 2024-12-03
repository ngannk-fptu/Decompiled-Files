/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.util;

import java.io.Serializable;

public final class Data
implements Serializable {
    private byte[] bytes;

    public Data() {
    }

    public Data(byte[] bytes) {
        this.bytes = bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return this.bytes;
    }
}

