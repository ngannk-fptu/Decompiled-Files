/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.attachments;

public class OctetStream {
    private byte[] bytes = null;

    public OctetStream() {
    }

    public OctetStream(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}

