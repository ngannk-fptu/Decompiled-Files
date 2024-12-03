/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.base64;

class Base64Constants {
    static final byte[] S_BASE64CHAR;
    static final byte S_BASE64PAD = 61;
    static final byte[] S_DECODETABLE;

    Base64Constants() {
    }

    static {
        int i;
        S_BASE64CHAR = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
        S_DECODETABLE = new byte[128];
        for (i = 0; i < S_DECODETABLE.length; ++i) {
            Base64Constants.S_DECODETABLE[i] = 127;
        }
        for (i = 0; i < S_BASE64CHAR.length; ++i) {
            Base64Constants.S_DECODETABLE[Base64Constants.S_BASE64CHAR[i]] = (byte)i;
        }
    }
}

