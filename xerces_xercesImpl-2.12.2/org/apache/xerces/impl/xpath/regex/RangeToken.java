/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xpath.regex;

import java.io.Serializable;
import org.apache.xerces.impl.xpath.regex.Token;

final class RangeToken
extends Token
implements Serializable {
    private static final long serialVersionUID = -553983121197679934L;
    int[] ranges;
    boolean sorted;
    boolean compacted;
    RangeToken icaseCache = null;
    int[] map = null;
    int nonMapIndex;
    private static final int MAPSIZE = 256;

    RangeToken(int n) {
        super(n);
        this.setSorted(false);
    }

    @Override
    protected void addRange(int n, int n2) {
        int n3;
        int n4;
        this.icaseCache = null;
        if (n <= n2) {
            n4 = n;
            n3 = n2;
        } else {
            n4 = n2;
            n3 = n;
        }
        int n5 = 0;
        if (this.ranges == null) {
            this.ranges = new int[2];
            this.ranges[0] = n4;
            this.ranges[1] = n3;
            this.setSorted(true);
        } else {
            n5 = this.ranges.length;
            if (this.ranges[n5 - 1] + 1 == n4) {
                this.ranges[n5 - 1] = n3;
                return;
            }
            int[] nArray = new int[n5 + 2];
            System.arraycopy(this.ranges, 0, nArray, 0, n5);
            this.ranges = nArray;
            if (this.ranges[n5 - 1] >= n4) {
                this.setSorted(false);
            }
            this.ranges[n5++] = n4;
            this.ranges[n5] = n3;
            if (!this.sorted) {
                this.sortRanges();
            }
        }
    }

    private final boolean isSorted() {
        return this.sorted;
    }

    private final void setSorted(boolean bl) {
        this.sorted = bl;
        if (!bl) {
            this.compacted = false;
        }
    }

    private final boolean isCompacted() {
        return this.compacted;
    }

    private final void setCompacted() {
        this.compacted = true;
    }

    @Override
    protected void sortRanges() {
        if (this.isSorted()) {
            return;
        }
        if (this.ranges == null) {
            return;
        }
        for (int i = this.ranges.length - 4; i >= 0; i -= 2) {
            for (int j = 0; j <= i; j += 2) {
                if (this.ranges[j] <= this.ranges[j + 2] && (this.ranges[j] != this.ranges[j + 2] || this.ranges[j + 1] <= this.ranges[j + 3])) continue;
                int n = this.ranges[j + 2];
                this.ranges[j + 2] = this.ranges[j];
                this.ranges[j] = n;
                n = this.ranges[j + 3];
                this.ranges[j + 3] = this.ranges[j + 1];
                this.ranges[j + 1] = n;
            }
        }
        this.setSorted(true);
    }

    @Override
    protected void compactRanges() {
        boolean bl = false;
        if (this.ranges == null || this.ranges.length <= 2) {
            return;
        }
        if (this.isCompacted()) {
            return;
        }
        int n = 0;
        int n2 = 0;
        while (n2 < this.ranges.length) {
            if (n != n2) {
                this.ranges[n] = this.ranges[n2++];
                this.ranges[n + 1] = this.ranges[n2++];
            } else {
                n2 += 2;
            }
            int n3 = this.ranges[n + 1];
            while (n2 < this.ranges.length && n3 + 1 >= this.ranges[n2]) {
                if (n3 + 1 == this.ranges[n2]) {
                    if (bl) {
                        System.err.println("Token#compactRanges(): Compaction: [" + this.ranges[n] + ", " + this.ranges[n + 1] + "], [" + this.ranges[n2] + ", " + this.ranges[n2 + 1] + "] -> [" + this.ranges[n] + ", " + this.ranges[n2 + 1] + "]");
                    }
                    this.ranges[n + 1] = this.ranges[n2 + 1];
                    n3 = this.ranges[n + 1];
                    n2 += 2;
                    continue;
                }
                if (n3 >= this.ranges[n2 + 1]) {
                    if (bl) {
                        System.err.println("Token#compactRanges(): Compaction: [" + this.ranges[n] + ", " + this.ranges[n + 1] + "], [" + this.ranges[n2] + ", " + this.ranges[n2 + 1] + "] -> [" + this.ranges[n] + ", " + this.ranges[n + 1] + "]");
                    }
                    n2 += 2;
                    continue;
                }
                if (n3 < this.ranges[n2 + 1]) {
                    if (bl) {
                        System.err.println("Token#compactRanges(): Compaction: [" + this.ranges[n] + ", " + this.ranges[n + 1] + "], [" + this.ranges[n2] + ", " + this.ranges[n2 + 1] + "] -> [" + this.ranges[n] + ", " + this.ranges[n2 + 1] + "]");
                    }
                    this.ranges[n + 1] = this.ranges[n2 + 1];
                    n3 = this.ranges[n + 1];
                    n2 += 2;
                    continue;
                }
                throw new RuntimeException("Token#compactRanges(): Internel Error: [" + this.ranges[n] + "," + this.ranges[n + 1] + "] [" + this.ranges[n2] + "," + this.ranges[n2 + 1] + "]");
            }
            n += 2;
        }
        if (n != this.ranges.length) {
            int[] nArray = new int[n];
            System.arraycopy(this.ranges, 0, nArray, 0, n);
            this.ranges = nArray;
        }
        this.setCompacted();
    }

    @Override
    protected void mergeRanges(Token token) {
        RangeToken rangeToken = (RangeToken)token;
        this.sortRanges();
        rangeToken.sortRanges();
        if (rangeToken.ranges == null) {
            return;
        }
        this.icaseCache = null;
        this.setSorted(true);
        if (this.ranges == null) {
            this.ranges = new int[rangeToken.ranges.length];
            System.arraycopy(rangeToken.ranges, 0, this.ranges, 0, rangeToken.ranges.length);
            return;
        }
        int[] nArray = new int[this.ranges.length + rangeToken.ranges.length];
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        while (n < this.ranges.length || n2 < rangeToken.ranges.length) {
            if (n >= this.ranges.length) {
                nArray[n3++] = rangeToken.ranges[n2++];
                nArray[n3++] = rangeToken.ranges[n2++];
                continue;
            }
            if (n2 >= rangeToken.ranges.length) {
                nArray[n3++] = this.ranges[n++];
                nArray[n3++] = this.ranges[n++];
                continue;
            }
            if (rangeToken.ranges[n2] < this.ranges[n] || rangeToken.ranges[n2] == this.ranges[n] && rangeToken.ranges[n2 + 1] < this.ranges[n + 1]) {
                nArray[n3++] = rangeToken.ranges[n2++];
                nArray[n3++] = rangeToken.ranges[n2++];
                continue;
            }
            nArray[n3++] = this.ranges[n++];
            nArray[n3++] = this.ranges[n++];
        }
        this.ranges = nArray;
    }

    @Override
    protected void subtractRanges(Token token) {
        if (token.type == 5) {
            this.intersectRanges(token);
            return;
        }
        RangeToken rangeToken = (RangeToken)token;
        if (rangeToken.ranges == null || this.ranges == null) {
            return;
        }
        this.icaseCache = null;
        this.sortRanges();
        this.compactRanges();
        rangeToken.sortRanges();
        rangeToken.compactRanges();
        int[] nArray = new int[this.ranges.length + rangeToken.ranges.length];
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        while (n2 < this.ranges.length && n3 < rangeToken.ranges.length) {
            int n4 = this.ranges[n2];
            int n5 = this.ranges[n2 + 1];
            int n6 = rangeToken.ranges[n3];
            int n7 = rangeToken.ranges[n3 + 1];
            if (n5 < n6) {
                nArray[n++] = this.ranges[n2++];
                nArray[n++] = this.ranges[n2++];
                continue;
            }
            if (n5 >= n6 && n4 <= n7) {
                if (n6 <= n4 && n5 <= n7) {
                    n2 += 2;
                    continue;
                }
                if (n6 <= n4) {
                    this.ranges[n2] = n7 + 1;
                    n3 += 2;
                    continue;
                }
                if (n5 <= n7) {
                    nArray[n++] = n4;
                    nArray[n++] = n6 - 1;
                    n2 += 2;
                    continue;
                }
                nArray[n++] = n4;
                nArray[n++] = n6 - 1;
                this.ranges[n2] = n7 + 1;
                n3 += 2;
                continue;
            }
            if (n7 < n4) {
                n3 += 2;
                continue;
            }
            throw new RuntimeException("Token#subtractRanges(): Internal Error: [" + this.ranges[n2] + "," + this.ranges[n2 + 1] + "] - [" + rangeToken.ranges[n3] + "," + rangeToken.ranges[n3 + 1] + "]");
        }
        while (n2 < this.ranges.length) {
            nArray[n++] = this.ranges[n2++];
            nArray[n++] = this.ranges[n2++];
        }
        this.ranges = new int[n];
        System.arraycopy(nArray, 0, this.ranges, 0, n);
    }

    @Override
    protected void intersectRanges(Token token) {
        RangeToken rangeToken = (RangeToken)token;
        if (rangeToken.ranges == null || this.ranges == null) {
            return;
        }
        this.icaseCache = null;
        this.sortRanges();
        this.compactRanges();
        rangeToken.sortRanges();
        rangeToken.compactRanges();
        int[] nArray = new int[this.ranges.length + rangeToken.ranges.length];
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        while (n2 < this.ranges.length && n3 < rangeToken.ranges.length) {
            int n4 = this.ranges[n2];
            int n5 = this.ranges[n2 + 1];
            int n6 = rangeToken.ranges[n3];
            int n7 = rangeToken.ranges[n3 + 1];
            if (n5 < n6) {
                n2 += 2;
                continue;
            }
            if (n5 >= n6 && n4 <= n7) {
                if (n6 <= n4 && n5 <= n7) {
                    nArray[n++] = n4;
                    nArray[n++] = n5;
                    n2 += 2;
                    continue;
                }
                if (n6 <= n4) {
                    nArray[n++] = n4;
                    nArray[n++] = n7;
                    this.ranges[n2] = n7 + 1;
                    n3 += 2;
                    continue;
                }
                if (n5 <= n7) {
                    nArray[n++] = n6;
                    nArray[n++] = n5;
                    n2 += 2;
                    continue;
                }
                nArray[n++] = n6;
                nArray[n++] = n7;
                this.ranges[n2] = n7 + 1;
                continue;
            }
            if (n7 < n4) {
                n3 += 2;
                continue;
            }
            throw new RuntimeException("Token#intersectRanges(): Internal Error: [" + this.ranges[n2] + "," + this.ranges[n2 + 1] + "] & [" + rangeToken.ranges[n3] + "," + rangeToken.ranges[n3 + 1] + "]");
        }
        while (n2 < this.ranges.length) {
            nArray[n++] = this.ranges[n2++];
            nArray[n++] = this.ranges[n2++];
        }
        this.ranges = new int[n];
        System.arraycopy(nArray, 0, this.ranges, 0, n);
    }

    static Token complementRanges(Token token) {
        int n;
        if (token.type != 4 && token.type != 5) {
            throw new IllegalArgumentException("Token#complementRanges(): must be RANGE: " + token.type);
        }
        RangeToken rangeToken = (RangeToken)token;
        rangeToken.sortRanges();
        rangeToken.compactRanges();
        int n2 = rangeToken.ranges.length + 2;
        if (rangeToken.ranges[0] == 0) {
            n2 -= 2;
        }
        if ((n = rangeToken.ranges[rangeToken.ranges.length - 1]) == 0x10FFFF) {
            n2 -= 2;
        }
        RangeToken rangeToken2 = Token.createRange();
        rangeToken2.ranges = new int[n2];
        int n3 = 0;
        if (rangeToken.ranges[0] > 0) {
            rangeToken2.ranges[n3++] = 0;
            rangeToken2.ranges[n3++] = rangeToken.ranges[0] - 1;
        }
        for (int i = 1; i < rangeToken.ranges.length - 2; i += 2) {
            rangeToken2.ranges[n3++] = rangeToken.ranges[i] + 1;
            rangeToken2.ranges[n3++] = rangeToken.ranges[i + 1] - 1;
        }
        if (n != 0x10FFFF) {
            rangeToken2.ranges[n3++] = n + 1;
            rangeToken2.ranges[n3] = 0x10FFFF;
        }
        rangeToken2.setCompacted();
        return rangeToken2;
    }

    synchronized RangeToken getCaseInsensitiveToken() {
        int n;
        int n2;
        if (this.icaseCache != null) {
            return this.icaseCache;
        }
        RangeToken rangeToken = this.type == 4 ? Token.createRange() : Token.createNRange();
        for (int i = 0; i < this.ranges.length; i += 2) {
            for (n2 = this.ranges[i]; n2 <= this.ranges[i + 1]; ++n2) {
                if (n2 > 65535) {
                    rangeToken.addRange(n2, n2);
                    continue;
                }
                n = Character.toUpperCase((char)n2);
                rangeToken.addRange(n, n);
            }
        }
        RangeToken rangeToken2 = this.type == 4 ? Token.createRange() : Token.createNRange();
        for (n2 = 0; n2 < rangeToken.ranges.length; n2 += 2) {
            for (n = rangeToken.ranges[n2]; n <= rangeToken.ranges[n2 + 1]; ++n) {
                if (n > 65535) {
                    rangeToken2.addRange(n, n);
                    continue;
                }
                char c = Character.toLowerCase((char)n);
                rangeToken2.addRange(c, c);
            }
        }
        rangeToken2.mergeRanges(rangeToken);
        rangeToken2.mergeRanges(this);
        rangeToken2.compactRanges();
        this.icaseCache = rangeToken2;
        return rangeToken2;
    }

    void dumpRanges() {
        System.err.print("RANGE: ");
        if (this.ranges == null) {
            System.err.println(" NULL");
            return;
        }
        for (int i = 0; i < this.ranges.length; i += 2) {
            System.err.print("[" + this.ranges[i] + "," + this.ranges[i + 1] + "] ");
        }
        System.err.println("");
    }

    @Override
    boolean match(int n) {
        boolean bl;
        if (this.map == null) {
            this.createMap();
        }
        if (this.type == 4) {
            if (n < 256) {
                return (this.map[n / 32] & 1 << (n & 0x1F)) != 0;
            }
            bl = false;
            for (int i = this.nonMapIndex; i < this.ranges.length; i += 2) {
                if (this.ranges[i] > n || n > this.ranges[i + 1]) continue;
                return true;
            }
        } else {
            if (n < 256) {
                return (this.map[n / 32] & 1 << (n & 0x1F)) == 0;
            }
            bl = true;
            for (int i = this.nonMapIndex; i < this.ranges.length; i += 2) {
                if (this.ranges[i] > n || n > this.ranges[i + 1]) continue;
                return false;
            }
        }
        return bl;
    }

    private void createMap() {
        int n;
        int n2 = 8;
        int[] nArray = new int[n2];
        int n3 = this.ranges.length;
        for (n = 0; n < n2; ++n) {
            nArray[n] = 0;
        }
        for (n = 0; n < this.ranges.length; n += 2) {
            int n4 = this.ranges[n];
            int n5 = this.ranges[n + 1];
            if (n4 < 256) {
                for (int i = n4; i <= n5 && i < 256; ++i) {
                    int n6 = i / 32;
                    nArray[n6] = nArray[n6] | 1 << (i & 0x1F);
                }
            } else {
                n3 = n;
                break;
            }
            if (n5 < 256) continue;
            n3 = n;
            break;
        }
        this.map = nArray;
        this.nonMapIndex = n3;
    }

    @Override
    public String toString(int n) {
        String string;
        if (this.type == 4) {
            if (this == Token.token_dot) {
                string = ".";
            } else if (this == Token.token_0to9) {
                string = "\\d";
            } else if (this == Token.token_wordchars) {
                string = "\\w";
            } else if (this == Token.token_spaces) {
                string = "\\s";
            } else {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append('[');
                for (int i = 0; i < this.ranges.length; i += 2) {
                    if ((n & 0x400) != 0 && i > 0) {
                        stringBuffer.append(',');
                    }
                    if (this.ranges[i] == this.ranges[i + 1]) {
                        stringBuffer.append(RangeToken.escapeCharInCharClass(this.ranges[i]));
                        continue;
                    }
                    stringBuffer.append(RangeToken.escapeCharInCharClass(this.ranges[i]));
                    stringBuffer.append('-');
                    stringBuffer.append(RangeToken.escapeCharInCharClass(this.ranges[i + 1]));
                }
                stringBuffer.append(']');
                string = stringBuffer.toString();
            }
        } else if (this == Token.token_not_0to9) {
            string = "\\D";
        } else if (this == Token.token_not_wordchars) {
            string = "\\W";
        } else if (this == Token.token_not_spaces) {
            string = "\\S";
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("[^");
            for (int i = 0; i < this.ranges.length; i += 2) {
                if ((n & 0x400) != 0 && i > 0) {
                    stringBuffer.append(',');
                }
                if (this.ranges[i] == this.ranges[i + 1]) {
                    stringBuffer.append(RangeToken.escapeCharInCharClass(this.ranges[i]));
                    continue;
                }
                stringBuffer.append(RangeToken.escapeCharInCharClass(this.ranges[i]));
                stringBuffer.append('-');
                stringBuffer.append(RangeToken.escapeCharInCharClass(this.ranges[i + 1]));
            }
            stringBuffer.append(']');
            string = stringBuffer.toString();
        }
        return string;
    }

    private static String escapeCharInCharClass(int n) {
        String string;
        switch (n) {
            case 44: 
            case 45: 
            case 91: 
            case 92: 
            case 93: 
            case 94: {
                string = "\\" + (char)n;
                break;
            }
            case 12: {
                string = "\\f";
                break;
            }
            case 10: {
                string = "\\n";
                break;
            }
            case 13: {
                string = "\\r";
                break;
            }
            case 9: {
                string = "\\t";
                break;
            }
            case 27: {
                string = "\\e";
                break;
            }
            default: {
                if (n < 32) {
                    String string2 = "0" + Integer.toHexString(n);
                    string = "\\x" + string2.substring(string2.length() - 2, string2.length());
                    break;
                }
                if (n >= 65536) {
                    String string3 = "0" + Integer.toHexString(n);
                    string = "\\v" + string3.substring(string3.length() - 6, string3.length());
                    break;
                }
                string = "" + (char)n;
            }
        }
        return string;
    }
}

