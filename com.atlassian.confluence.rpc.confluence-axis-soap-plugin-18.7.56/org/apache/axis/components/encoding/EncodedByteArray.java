/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.components.encoding;

import java.io.UnsupportedEncodingException;

class EncodedByteArray {
    private byte[] array = null;
    private int pointer;
    private final double PADDING = 1.5;

    public EncodedByteArray(byte[] bytes, int startPos, int length) {
        this.array = new byte[(int)((double)bytes.length * 1.5)];
        System.arraycopy(bytes, startPos, this.array, 0, length);
        this.pointer = length;
    }

    public EncodedByteArray(int size) {
        this.array = new byte[size];
    }

    public void append(int aByte) {
        if (this.pointer + 1 >= this.array.length) {
            byte[] newArray = new byte[(int)((double)this.array.length * 1.5)];
            System.arraycopy(this.array, 0, newArray, 0, this.pointer);
            this.array = newArray;
        }
        this.array[this.pointer] = (byte)aByte;
        ++this.pointer;
    }

    public void append(byte[] byteArray) {
        if (this.pointer + byteArray.length >= this.array.length) {
            byte[] newArray = new byte[(int)((double)this.array.length * 1.5) + byteArray.length];
            System.arraycopy(this.array, 0, newArray, 0, this.pointer);
            this.array = newArray;
        }
        System.arraycopy(byteArray, 0, this.array, this.pointer, byteArray.length);
        this.pointer += byteArray.length;
    }

    public void append(byte[] byteArray, int pos, int length) {
        if (this.pointer + length >= this.array.length) {
            byte[] newArray = new byte[(int)((double)this.array.length * 1.5) + byteArray.length];
            System.arraycopy(this.array, 0, newArray, 0, this.pointer);
            this.array = newArray;
        }
        System.arraycopy(byteArray, pos, this.array, this.pointer, length);
        this.pointer += length;
    }

    public String toString() {
        return new String(this.array, 0, this.pointer);
    }

    public String toString(String charsetName) throws UnsupportedEncodingException {
        return new String(this.array, 0, this.pointer, charsetName);
    }
}

