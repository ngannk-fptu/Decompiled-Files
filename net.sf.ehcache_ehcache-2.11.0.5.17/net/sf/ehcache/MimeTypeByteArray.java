/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache;

import java.io.Serializable;

public class MimeTypeByteArray
implements Serializable {
    private String mimeType;
    private byte[] value;

    public MimeTypeByteArray() {
    }

    public MimeTypeByteArray(String mimeType, byte[] value) {
        this.mimeType = mimeType;
        this.value = value;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public byte[] getValue() {
        return this.value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }
}

