/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

public class ValidUTF8Sequence {
    int lowChar;
    int highChar;
    int numBytes;
    char[] validBytes = new char[8];

    public ValidUTF8Sequence(int lowChar, int highChar, int numBytes, char[] validBytes) {
        this.lowChar = lowChar;
        this.highChar = highChar;
        this.numBytes = numBytes;
        this.validBytes = validBytes;
    }
}

