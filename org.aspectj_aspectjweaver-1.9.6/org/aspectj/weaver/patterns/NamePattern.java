/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.patterns.PatternNode;
import org.aspectj.weaver.patterns.PatternNodeVisitor;

public class NamePattern
extends PatternNode {
    char[] pattern;
    int starCount = 0;
    private int hashcode = -1;
    public static final NamePattern ELLIPSIS = new NamePattern("");
    public static final NamePattern ANY = new NamePattern("*");

    public NamePattern(String name) {
        this(name.toCharArray());
    }

    public NamePattern(char[] pattern) {
        this.pattern = pattern;
        int len = pattern.length;
        for (int i = 0; i < len; ++i) {
            if (pattern[i] != '*') continue;
            ++this.starCount;
        }
        this.hashcode = new String(pattern).hashCode();
    }

    public boolean matches(char[] a2) {
        char[] a1 = this.pattern;
        int len1 = a1.length;
        int len2 = a2.length;
        if (this.starCount == 0) {
            if (len1 != len2) {
                return false;
            }
            for (int i = 0; i < len1; ++i) {
                if (a1[i] == a2[i]) continue;
                return false;
            }
            return true;
        }
        if (this.starCount == 1) {
            if (len1 == 1) {
                return true;
            }
            if (len1 > len2 + 1) {
                return false;
            }
            int i2 = 0;
            for (int i1 = 0; i1 < len1; ++i1) {
                char c1 = a1[i1];
                if (c1 == '*') {
                    i2 = len2 - (len1 - (i1 + 1));
                    continue;
                }
                if (c1 == a2[i2++]) continue;
                return false;
            }
            return true;
        }
        boolean b = NamePattern.outOfStar(a1, a2, 0, 0, len1 - this.starCount, len2, this.starCount);
        return b;
    }

    private static boolean outOfStar(char[] pattern, char[] target, int pi, int ti, int pLeft, int tLeft, int starsLeft) {
        if (pLeft > tLeft) {
            return false;
        }
        while (tLeft != 0) {
            if (pLeft == 0) {
                return starsLeft > 0;
            }
            if (pattern[pi] == '*') {
                return NamePattern.inStar(pattern, target, pi + 1, ti, pLeft, tLeft, starsLeft - 1);
            }
            if (target[ti] != pattern[pi]) {
                return false;
            }
            ++pi;
            ++ti;
            --pLeft;
            --tLeft;
        }
        return true;
    }

    private static boolean inStar(char[] pattern, char[] target, int pi, int ti, int pLeft, int tLeft, int starsLeft) {
        char patternChar = pattern[pi];
        while (patternChar == '*') {
            --starsLeft;
            patternChar = pattern[++pi];
        }
        while (pLeft <= tLeft) {
            if (target[ti] == patternChar && NamePattern.outOfStar(pattern, target, pi + 1, ti + 1, pLeft - 1, tLeft - 1, starsLeft)) {
                return true;
            }
            ++ti;
            --tLeft;
        }
        return false;
    }

    public boolean matches(String other) {
        if (this.starCount == 1 && this.pattern.length == 1) {
            return true;
        }
        return this.matches(other.toCharArray());
    }

    public String toString() {
        return new String(this.pattern);
    }

    public boolean equals(Object other) {
        if (other instanceof NamePattern) {
            NamePattern otherPat = (NamePattern)other;
            if (otherPat.starCount != this.starCount) {
                return false;
            }
            if (otherPat.pattern.length != this.pattern.length) {
                return false;
            }
            for (int i = 0; i < this.pattern.length; ++i) {
                if (this.pattern[i] == otherPat.pattern[i]) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.hashcode;
    }

    @Override
    public void write(CompressingDataOutputStream out) throws IOException {
        out.writeUTF(new String(this.pattern));
    }

    public static NamePattern read(VersionedDataInputStream in) throws IOException {
        String s = in.readUTF();
        if (s.length() == 0) {
            return ELLIPSIS;
        }
        return new NamePattern(s);
    }

    public String maybeGetSimpleName() {
        if (this.starCount == 0 && this.pattern.length > 0) {
            return new String(this.pattern);
        }
        return null;
    }

    public boolean isAny() {
        return this.starCount == 1 && this.pattern.length == 1;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

