/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.asm;

import com.google.inject.internal.asm.$ByteVector;

public class $TypePath {
    public static final int ARRAY_ELEMENT = 0;
    public static final int INNER_TYPE = 1;
    public static final int WILDCARD_BOUND = 2;
    public static final int TYPE_ARGUMENT = 3;
    byte[] a;
    int b;

    $TypePath(byte[] byArray, int n) {
        this.a = byArray;
        this.b = n;
    }

    public int getLength() {
        return this.a[this.b];
    }

    public int getStep(int n) {
        return this.a[this.b + 2 * n + 1];
    }

    public int getStepArgument(int n) {
        return this.a[this.b + 2 * n + 2];
    }

    public static $TypePath fromString(String string) {
        if (string == null || string.length() == 0) {
            return null;
        }
        int n = string.length();
        $ByteVector $ByteVector = new $ByteVector(n);
        $ByteVector.putByte(0);
        int n2 = 0;
        while (n2 < n) {
            char c;
            if ((c = string.charAt(n2++)) == '[') {
                $ByteVector.a(0, 0);
                continue;
            }
            if (c == '.') {
                $ByteVector.a(1, 0);
                continue;
            }
            if (c == '*') {
                $ByteVector.a(2, 0);
                continue;
            }
            if (c < '0' || c > '9') continue;
            int n3 = c - 48;
            while (n2 < n && (c = string.charAt(n2)) >= '0' && c <= '9') {
                n3 = n3 * 10 + c - 48;
                ++n2;
            }
            $ByteVector.a(3, n3);
        }
        $ByteVector.a[0] = (byte)($ByteVector.b / 2);
        return new $TypePath($ByteVector.a, 0);
    }

    public String toString() {
        int n = this.getLength();
        StringBuffer stringBuffer = new StringBuffer(n * 2);
        block6: for (int i = 0; i < n; ++i) {
            switch (this.getStep(i)) {
                case 0: {
                    stringBuffer.append('[');
                    continue block6;
                }
                case 1: {
                    stringBuffer.append('.');
                    continue block6;
                }
                case 2: {
                    stringBuffer.append('*');
                    continue block6;
                }
                case 3: {
                    stringBuffer.append(this.getStepArgument(i));
                    continue block6;
                }
                default: {
                    stringBuffer.append('_');
                }
            }
        }
        return stringBuffer.toString();
    }
}

