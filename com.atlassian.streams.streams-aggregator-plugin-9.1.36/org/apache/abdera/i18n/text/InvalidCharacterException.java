/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.text;

public class InvalidCharacterException
extends RuntimeException {
    private static final long serialVersionUID = -7150645484748059676L;
    private int input;

    public InvalidCharacterException(int input) {
        this.input = input;
    }

    public String getMessage() {
        return "Invalid Character 0x" + Integer.toHexString(this.input) + "(" + (char)this.input + ")";
    }
}

