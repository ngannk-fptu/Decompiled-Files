/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.asm;

import com.google.inject.internal.asm.$ClassWriter;
import com.google.inject.internal.asm.$Item;
import com.google.inject.internal.asm.$Label;
import com.google.inject.internal.asm.$Type;

final class $Frame {
    static final int[] a;
    $Label b;
    int[] c;
    int[] d;
    private int[] e;
    private int[] f;
    private int g;
    private int h;
    private int[] i;

    $Frame() {
    }

    private int a(int n) {
        if (this.e == null || n >= this.e.length) {
            return 0x2000000 | n;
        }
        int n2 = this.e[n];
        if (n2 == 0) {
            n2 = this.e[n] = 0x2000000 | n;
        }
        return n2;
    }

    private void a(int n, int n2) {
        int n3;
        if (this.e == null) {
            this.e = new int[10];
        }
        if (n >= (n3 = this.e.length)) {
            int[] nArray = new int[Math.max(n + 1, 2 * n3)];
            System.arraycopy(this.e, 0, nArray, 0, n3);
            this.e = nArray;
        }
        this.e[n] = n2;
    }

    private void b(int n) {
        int n2;
        if (this.f == null) {
            this.f = new int[10];
        }
        if (this.g >= (n2 = this.f.length)) {
            int[] nArray = new int[Math.max(this.g + 1, 2 * n2)];
            System.arraycopy(this.f, 0, nArray, 0, n2);
            this.f = nArray;
        }
        this.f[this.g++] = n;
        int n3 = this.b.f + this.g;
        if (n3 > this.b.g) {
            this.b.g = n3;
        }
    }

    private void a($ClassWriter $ClassWriter, String string) {
        int n = $Frame.b($ClassWriter, string);
        if (n != 0) {
            this.b(n);
            if (n == 0x1000004 || n == 0x1000003) {
                this.b(0x1000000);
            }
        }
    }

    private static int b($ClassWriter $ClassWriter, String string) {
        int n;
        int n2 = string.charAt(0) == '(' ? string.indexOf(41) + 1 : 0;
        switch (string.charAt(n2)) {
            case 'V': {
                return 0;
            }
            case 'B': 
            case 'C': 
            case 'I': 
            case 'S': 
            case 'Z': {
                return 0x1000001;
            }
            case 'F': {
                return 0x1000002;
            }
            case 'J': {
                return 0x1000004;
            }
            case 'D': {
                return 0x1000003;
            }
            case 'L': {
                String string2 = string.substring(n2 + 1, string.length() - 1);
                return 0x1700000 | $ClassWriter.c(string2);
            }
        }
        int n3 = n2 + 1;
        while (string.charAt(n3) == '[') {
            ++n3;
        }
        switch (string.charAt(n3)) {
            case 'Z': {
                n = 0x1000009;
                break;
            }
            case 'C': {
                n = 0x100000B;
                break;
            }
            case 'B': {
                n = 0x100000A;
                break;
            }
            case 'S': {
                n = 0x100000C;
                break;
            }
            case 'I': {
                n = 0x1000001;
                break;
            }
            case 'F': {
                n = 0x1000002;
                break;
            }
            case 'J': {
                n = 0x1000004;
                break;
            }
            case 'D': {
                n = 0x1000003;
                break;
            }
            default: {
                String string3 = string.substring(n3 + 1, string.length() - 1);
                n = 0x1700000 | $ClassWriter.c(string3);
            }
        }
        return n3 - n2 << 28 | n;
    }

    private int a() {
        if (this.g > 0) {
            return this.f[--this.g];
        }
        return 0x3000000 | -(--this.b.f);
    }

    private void c(int n) {
        if (this.g >= n) {
            this.g -= n;
        } else {
            this.b.f -= n - this.g;
            this.g = 0;
        }
    }

    private void a(String string) {
        char c = string.charAt(0);
        if (c == '(') {
            this.c(($Type.getArgumentsAndReturnSizes(string) >> 2) - 1);
        } else if (c == 'J' || c == 'D') {
            this.c(2);
        } else {
            this.c(1);
        }
    }

    private void d(int n) {
        int n2;
        if (this.i == null) {
            this.i = new int[2];
        }
        if (this.h >= (n2 = this.i.length)) {
            int[] nArray = new int[Math.max(this.h + 1, 2 * n2)];
            System.arraycopy(this.i, 0, nArray, 0, n2);
            this.i = nArray;
        }
        this.i[this.h++] = n;
    }

    private int a($ClassWriter $ClassWriter, int n) {
        int n2;
        if (n == 0x1000006) {
            n2 = 0x1700000 | $ClassWriter.c($ClassWriter.I);
        } else if ((n & 0xFFF00000) == 0x1800000) {
            String string = $ClassWriter.H[n & 0xFFFFF].g;
            n2 = 0x1700000 | $ClassWriter.c(string);
        } else {
            return n;
        }
        for (int i = 0; i < this.h; ++i) {
            int n3 = this.i[i];
            int n4 = n3 & 0xF0000000;
            int n5 = n3 & 0xF000000;
            if (n5 == 0x2000000) {
                n3 = n4 + this.c[n3 & 0x7FFFFF];
            } else if (n5 == 0x3000000) {
                n3 = n4 + this.d[this.d.length - (n3 & 0x7FFFFF)];
            }
            if (n != n3) continue;
            return n2;
        }
        return n;
    }

    void a($ClassWriter $ClassWriter, int n, $Type[] $TypeArray, int n2) {
        this.c = new int[n2];
        this.d = new int[0];
        int n3 = 0;
        if ((n & 8) == 0) {
            this.c[n3++] = (n & 0x80000) == 0 ? 0x1700000 | $ClassWriter.c($ClassWriter.I) : 0x1000006;
        }
        for (int i = 0; i < $TypeArray.length; ++i) {
            int n4 = $Frame.b($ClassWriter, $TypeArray[i].getDescriptor());
            this.c[n3++] = n4;
            if (n4 != 0x1000004 && n4 != 0x1000003) continue;
            this.c[n3++] = 0x1000000;
        }
        while (n3 < n2) {
            this.c[n3++] = 0x1000000;
        }
    }

    void a(int n, int n2, $ClassWriter $ClassWriter, $Item $Item) {
        block0 : switch (n) {
            case 0: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 145: 
            case 146: 
            case 147: 
            case 167: 
            case 177: {
                break;
            }
            case 1: {
                this.b(0x1000005);
                break;
            }
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 16: 
            case 17: 
            case 21: {
                this.b(0x1000001);
                break;
            }
            case 9: 
            case 10: 
            case 22: {
                this.b(0x1000004);
                this.b(0x1000000);
                break;
            }
            case 11: 
            case 12: 
            case 13: 
            case 23: {
                this.b(0x1000002);
                break;
            }
            case 14: 
            case 15: 
            case 24: {
                this.b(0x1000003);
                this.b(0x1000000);
                break;
            }
            case 18: {
                switch ($Item.b) {
                    case 3: {
                        this.b(0x1000001);
                        break block0;
                    }
                    case 5: {
                        this.b(0x1000004);
                        this.b(0x1000000);
                        break block0;
                    }
                    case 4: {
                        this.b(0x1000002);
                        break block0;
                    }
                    case 6: {
                        this.b(0x1000003);
                        this.b(0x1000000);
                        break block0;
                    }
                    case 7: {
                        this.b(0x1700000 | $ClassWriter.c("java/lang/Class"));
                        break block0;
                    }
                    case 8: {
                        this.b(0x1700000 | $ClassWriter.c("java/lang/String"));
                        break block0;
                    }
                    case 16: {
                        this.b(0x1700000 | $ClassWriter.c("java/lang/invoke/MethodType"));
                        break block0;
                    }
                }
                this.b(0x1700000 | $ClassWriter.c("java/lang/invoke/MethodHandle"));
                break;
            }
            case 25: {
                this.b(this.a(n2));
                break;
            }
            case 46: 
            case 51: 
            case 52: 
            case 53: {
                this.c(2);
                this.b(0x1000001);
                break;
            }
            case 47: 
            case 143: {
                this.c(2);
                this.b(0x1000004);
                this.b(0x1000000);
                break;
            }
            case 48: {
                this.c(2);
                this.b(0x1000002);
                break;
            }
            case 49: 
            case 138: {
                this.c(2);
                this.b(0x1000003);
                this.b(0x1000000);
                break;
            }
            case 50: {
                this.c(1);
                int n3 = this.a();
                this.b(-268435456 + n3);
                break;
            }
            case 54: 
            case 56: 
            case 58: {
                int n4 = this.a();
                this.a(n2, n4);
                if (n2 <= 0) break;
                int n5 = this.a(n2 - 1);
                if (n5 == 0x1000004 || n5 == 0x1000003) {
                    this.a(n2 - 1, 0x1000000);
                    break;
                }
                if ((n5 & 0xF000000) == 0x1000000) break;
                this.a(n2 - 1, n5 | 0x800000);
                break;
            }
            case 55: 
            case 57: {
                this.c(1);
                int n6 = this.a();
                this.a(n2, n6);
                this.a(n2 + 1, 0x1000000);
                if (n2 <= 0) break;
                int n7 = this.a(n2 - 1);
                if (n7 == 0x1000004 || n7 == 0x1000003) {
                    this.a(n2 - 1, 0x1000000);
                    break;
                }
                if ((n7 & 0xF000000) == 0x1000000) break;
                this.a(n2 - 1, n7 | 0x800000);
                break;
            }
            case 79: 
            case 81: 
            case 83: 
            case 84: 
            case 85: 
            case 86: {
                this.c(3);
                break;
            }
            case 80: 
            case 82: {
                this.c(4);
                break;
            }
            case 87: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 170: 
            case 171: 
            case 172: 
            case 174: 
            case 176: 
            case 191: 
            case 194: 
            case 195: 
            case 198: 
            case 199: {
                this.c(1);
                break;
            }
            case 88: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: 
            case 173: 
            case 175: {
                this.c(2);
                break;
            }
            case 89: {
                int n8 = this.a();
                this.b(n8);
                this.b(n8);
                break;
            }
            case 90: {
                int n9 = this.a();
                int n10 = this.a();
                this.b(n9);
                this.b(n10);
                this.b(n9);
                break;
            }
            case 91: {
                int n11 = this.a();
                int n12 = this.a();
                int n13 = this.a();
                this.b(n11);
                this.b(n13);
                this.b(n12);
                this.b(n11);
                break;
            }
            case 92: {
                int n14 = this.a();
                int n15 = this.a();
                this.b(n15);
                this.b(n14);
                this.b(n15);
                this.b(n14);
                break;
            }
            case 93: {
                int n16 = this.a();
                int n17 = this.a();
                int n18 = this.a();
                this.b(n17);
                this.b(n16);
                this.b(n18);
                this.b(n17);
                this.b(n16);
                break;
            }
            case 94: {
                int n19 = this.a();
                int n20 = this.a();
                int n21 = this.a();
                int n22 = this.a();
                this.b(n20);
                this.b(n19);
                this.b(n22);
                this.b(n21);
                this.b(n20);
                this.b(n19);
                break;
            }
            case 95: {
                int n23 = this.a();
                int n24 = this.a();
                this.b(n23);
                this.b(n24);
                break;
            }
            case 96: 
            case 100: 
            case 104: 
            case 108: 
            case 112: 
            case 120: 
            case 122: 
            case 124: 
            case 126: 
            case 128: 
            case 130: 
            case 136: 
            case 142: 
            case 149: 
            case 150: {
                this.c(2);
                this.b(0x1000001);
                break;
            }
            case 97: 
            case 101: 
            case 105: 
            case 109: 
            case 113: 
            case 127: 
            case 129: 
            case 131: {
                this.c(4);
                this.b(0x1000004);
                this.b(0x1000000);
                break;
            }
            case 98: 
            case 102: 
            case 106: 
            case 110: 
            case 114: 
            case 137: 
            case 144: {
                this.c(2);
                this.b(0x1000002);
                break;
            }
            case 99: 
            case 103: 
            case 107: 
            case 111: 
            case 115: {
                this.c(4);
                this.b(0x1000003);
                this.b(0x1000000);
                break;
            }
            case 121: 
            case 123: 
            case 125: {
                this.c(3);
                this.b(0x1000004);
                this.b(0x1000000);
                break;
            }
            case 132: {
                this.a(n2, 0x1000001);
                break;
            }
            case 133: 
            case 140: {
                this.c(1);
                this.b(0x1000004);
                this.b(0x1000000);
                break;
            }
            case 134: {
                this.c(1);
                this.b(0x1000002);
                break;
            }
            case 135: 
            case 141: {
                this.c(1);
                this.b(0x1000003);
                this.b(0x1000000);
                break;
            }
            case 139: 
            case 190: 
            case 193: {
                this.c(1);
                this.b(0x1000001);
                break;
            }
            case 148: 
            case 151: 
            case 152: {
                this.c(4);
                this.b(0x1000001);
                break;
            }
            case 168: 
            case 169: {
                throw new RuntimeException("JSR/RET are not supported with computeFrames option");
            }
            case 178: {
                this.a($ClassWriter, $Item.i);
                break;
            }
            case 179: {
                this.a($Item.i);
                break;
            }
            case 180: {
                this.c(1);
                this.a($ClassWriter, $Item.i);
                break;
            }
            case 181: {
                this.a($Item.i);
                this.a();
                break;
            }
            case 182: 
            case 183: 
            case 184: 
            case 185: {
                this.a($Item.i);
                if (n != 184) {
                    int n25 = this.a();
                    if (n == 183 && $Item.h.charAt(0) == '<') {
                        this.d(n25);
                    }
                }
                this.a($ClassWriter, $Item.i);
                break;
            }
            case 186: {
                this.a($Item.h);
                this.a($ClassWriter, $Item.h);
                break;
            }
            case 187: {
                this.b(0x1800000 | $ClassWriter.a($Item.g, n2));
                break;
            }
            case 188: {
                this.a();
                switch (n2) {
                    case 4: {
                        this.b(0x11000009);
                        break block0;
                    }
                    case 5: {
                        this.b(0x1100000B);
                        break block0;
                    }
                    case 8: {
                        this.b(0x1100000A);
                        break block0;
                    }
                    case 9: {
                        this.b(0x1100000C);
                        break block0;
                    }
                    case 10: {
                        this.b(0x11000001);
                        break block0;
                    }
                    case 6: {
                        this.b(0x11000002);
                        break block0;
                    }
                    case 7: {
                        this.b(0x11000003);
                        break block0;
                    }
                }
                this.b(0x11000004);
                break;
            }
            case 189: {
                String string = $Item.g;
                this.a();
                if (string.charAt(0) == '[') {
                    this.a($ClassWriter, '[' + string);
                    break;
                }
                this.b(0x11700000 | $ClassWriter.c(string));
                break;
            }
            case 192: {
                String string = $Item.g;
                this.a();
                if (string.charAt(0) == '[') {
                    this.a($ClassWriter, string);
                    break;
                }
                this.b(0x1700000 | $ClassWriter.c(string));
                break;
            }
            default: {
                this.c(n2);
                this.a($ClassWriter, $Item.g);
            }
        }
    }

    boolean a($ClassWriter $ClassWriter, $Frame $Frame, int n) {
        int n2;
        int n3;
        int n4;
        int n5;
        int n6;
        boolean bl = false;
        int n7 = this.c.length;
        int n8 = this.d.length;
        if ($Frame.c == null) {
            $Frame.c = new int[n7];
            bl = true;
        }
        for (n6 = 0; n6 < n7; ++n6) {
            if (this.e != null && n6 < this.e.length) {
                n5 = this.e[n6];
                if (n5 == 0) {
                    n4 = this.c[n6];
                } else {
                    n3 = n5 & 0xF0000000;
                    n2 = n5 & 0xF000000;
                    if (n2 == 0x1000000) {
                        n4 = n5;
                    } else {
                        n4 = n2 == 0x2000000 ? n3 + this.c[n5 & 0x7FFFFF] : n3 + this.d[n8 - (n5 & 0x7FFFFF)];
                        if ((n5 & 0x800000) != 0 && (n4 == 0x1000004 || n4 == 0x1000003)) {
                            n4 = 0x1000000;
                        }
                    }
                }
            } else {
                n4 = this.c[n6];
            }
            if (this.i != null) {
                n4 = this.a($ClassWriter, n4);
            }
            bl |= $Frame.a($ClassWriter, n4, $Frame.c, n6);
        }
        if (n > 0) {
            for (n6 = 0; n6 < n7; ++n6) {
                n4 = this.c[n6];
                bl |= $Frame.a($ClassWriter, n4, $Frame.c, n6);
            }
            if ($Frame.d == null) {
                $Frame.d = new int[1];
                bl = true;
            }
            return bl |= $Frame.a($ClassWriter, n, $Frame.d, 0);
        }
        int n9 = this.d.length + this.b.f;
        if ($Frame.d == null) {
            $Frame.d = new int[n9 + this.g];
            bl = true;
        }
        for (n6 = 0; n6 < n9; ++n6) {
            n4 = this.d[n6];
            if (this.i != null) {
                n4 = this.a($ClassWriter, n4);
            }
            bl |= $Frame.a($ClassWriter, n4, $Frame.d, n6);
        }
        for (n6 = 0; n6 < this.g; ++n6) {
            n5 = this.f[n6];
            n3 = n5 & 0xF0000000;
            n2 = n5 & 0xF000000;
            if (n2 == 0x1000000) {
                n4 = n5;
            } else {
                n4 = n2 == 0x2000000 ? n3 + this.c[n5 & 0x7FFFFF] : n3 + this.d[n8 - (n5 & 0x7FFFFF)];
                if ((n5 & 0x800000) != 0 && (n4 == 0x1000004 || n4 == 0x1000003)) {
                    n4 = 0x1000000;
                }
            }
            if (this.i != null) {
                n4 = this.a($ClassWriter, n4);
            }
            bl |= $Frame.a($ClassWriter, n4, $Frame.d, n9 + n6);
        }
        return bl;
    }

    private static boolean a($ClassWriter $ClassWriter, int n, int[] nArray, int n2) {
        int n3;
        int n4 = nArray[n2];
        if (n4 == n) {
            return false;
        }
        if ((n & 0xFFFFFFF) == 0x1000005) {
            if (n4 == 0x1000005) {
                return false;
            }
            n = 0x1000005;
        }
        if (n4 == 0) {
            nArray[n2] = n;
            return true;
        }
        if ((n4 & 0xFF00000) == 0x1700000 || (n4 & 0xF0000000) != 0) {
            if (n == 0x1000005) {
                return false;
            }
            n3 = (n & 0xFFF00000) == (n4 & 0xFFF00000) ? ((n4 & 0xFF00000) == 0x1700000 ? n & 0xF0000000 | 0x1700000 | $ClassWriter.a(n & 0xFFFFF, n4 & 0xFFFFF) : 0x1700000 | $ClassWriter.c("java/lang/Object")) : ((n & 0xFF00000) == 0x1700000 || (n & 0xF0000000) != 0 ? 0x1700000 | $ClassWriter.c("java/lang/Object") : 0x1000000);
        } else {
            n3 = n4 == 0x1000005 ? ((n & 0xFF00000) == 0x1700000 || (n & 0xF0000000) != 0 ? n : 0x1000000) : 0x1000000;
        }
        if (n4 != n3) {
            nArray[n2] = n3;
            return true;
        }
        return false;
    }

    static {
        int[] nArray = new int[202];
        String string = "EFFFFFFFFGGFFFGGFFFEEFGFGFEEEEEEEEEEEEEEEEEEEEDEDEDDDDDCDCDEEEEEEEEEEEEEEEEEEEEBABABBBBDCFFFGGGEDCDCDCDCDCDCDCDCDCDCEEEEDDDDDDDCDCDCEFEFDDEEFFDEDEEEBDDBBDDDDDDCCCCCCCCEFEDDDCDCDEEEEEEEEEEFEEEEEEDDEEDDEE";
        for (int i = 0; i < nArray.length; ++i) {
            nArray[i] = string.charAt(i) - 69;
        }
        a = nArray;
    }
}

