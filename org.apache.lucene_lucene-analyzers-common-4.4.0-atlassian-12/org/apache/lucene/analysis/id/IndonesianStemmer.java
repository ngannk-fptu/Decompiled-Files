/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.id;

import org.apache.lucene.analysis.util.StemmerUtil;

public class IndonesianStemmer {
    private int numSyllables;
    private int flags;
    private static final int REMOVED_KE = 1;
    private static final int REMOVED_PENG = 2;
    private static final int REMOVED_DI = 4;
    private static final int REMOVED_MENG = 8;
    private static final int REMOVED_TER = 16;
    private static final int REMOVED_BER = 32;
    private static final int REMOVED_PE = 64;

    public int stem(char[] text, int length, boolean stemDerivational) {
        this.flags = 0;
        this.numSyllables = 0;
        for (int i = 0; i < length; ++i) {
            if (!this.isVowel(text[i])) continue;
            ++this.numSyllables;
        }
        if (this.numSyllables > 2) {
            length = this.removeParticle(text, length);
        }
        if (this.numSyllables > 2) {
            length = this.removePossessivePronoun(text, length);
        }
        if (stemDerivational) {
            length = this.stemDerivational(text, length);
        }
        return length;
    }

    private int stemDerivational(char[] text, int length) {
        int oldLength = length;
        if (this.numSyllables > 2) {
            length = this.removeFirstOrderPrefix(text, length);
        }
        if (oldLength != length) {
            oldLength = length;
            if (this.numSyllables > 2) {
                length = this.removeSuffix(text, length);
            }
            if (oldLength != length && this.numSyllables > 2) {
                length = this.removeSecondOrderPrefix(text, length);
            }
        } else {
            if (this.numSyllables > 2) {
                length = this.removeSecondOrderPrefix(text, length);
            }
            if (this.numSyllables > 2) {
                length = this.removeSuffix(text, length);
            }
        }
        return length;
    }

    private boolean isVowel(char ch) {
        switch (ch) {
            case 'a': 
            case 'e': 
            case 'i': 
            case 'o': 
            case 'u': {
                return true;
            }
        }
        return false;
    }

    private int removeParticle(char[] text, int length) {
        if (StemmerUtil.endsWith(text, length, "kah") || StemmerUtil.endsWith(text, length, "lah") || StemmerUtil.endsWith(text, length, "pun")) {
            --this.numSyllables;
            return length - 3;
        }
        return length;
    }

    private int removePossessivePronoun(char[] text, int length) {
        if (StemmerUtil.endsWith(text, length, "ku") || StemmerUtil.endsWith(text, length, "mu")) {
            --this.numSyllables;
            return length - 2;
        }
        if (StemmerUtil.endsWith(text, length, "nya")) {
            --this.numSyllables;
            return length - 3;
        }
        return length;
    }

    private int removeFirstOrderPrefix(char[] text, int length) {
        if (StemmerUtil.startsWith(text, length, "meng")) {
            this.flags |= 8;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 4);
        }
        if (StemmerUtil.startsWith(text, length, "meny") && length > 4 && this.isVowel(text[4])) {
            this.flags |= 8;
            text[3] = 115;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (StemmerUtil.startsWith(text, length, "men")) {
            this.flags |= 8;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (StemmerUtil.startsWith(text, length, "mem")) {
            this.flags |= 8;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (StemmerUtil.startsWith(text, length, "me")) {
            this.flags |= 8;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 2);
        }
        if (StemmerUtil.startsWith(text, length, "peng")) {
            this.flags |= 2;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 4);
        }
        if (StemmerUtil.startsWith(text, length, "peny") && length > 4 && this.isVowel(text[4])) {
            this.flags |= 2;
            text[3] = 115;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (StemmerUtil.startsWith(text, length, "peny")) {
            this.flags |= 2;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 4);
        }
        if (StemmerUtil.startsWith(text, length, "pen") && length > 3 && this.isVowel(text[3])) {
            this.flags |= 2;
            text[2] = 116;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 2);
        }
        if (StemmerUtil.startsWith(text, length, "pen")) {
            this.flags |= 2;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (StemmerUtil.startsWith(text, length, "pem")) {
            this.flags |= 2;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (StemmerUtil.startsWith(text, length, "di")) {
            this.flags |= 4;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 2);
        }
        if (StemmerUtil.startsWith(text, length, "ter")) {
            this.flags |= 0x10;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (StemmerUtil.startsWith(text, length, "ke")) {
            this.flags |= 1;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 2);
        }
        return length;
    }

    private int removeSecondOrderPrefix(char[] text, int length) {
        if (StemmerUtil.startsWith(text, length, "ber")) {
            this.flags |= 0x20;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (length == 7 && StemmerUtil.startsWith(text, length, "belajar")) {
            this.flags |= 0x20;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (StemmerUtil.startsWith(text, length, "be") && length > 4 && !this.isVowel(text[2]) && text[3] == 'e' && text[4] == 'r') {
            this.flags |= 0x20;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 2);
        }
        if (StemmerUtil.startsWith(text, length, "per")) {
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (length == 7 && StemmerUtil.startsWith(text, length, "pelajar")) {
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (StemmerUtil.startsWith(text, length, "pe")) {
            this.flags |= 0x40;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 2);
        }
        return length;
    }

    private int removeSuffix(char[] text, int length) {
        if (StemmerUtil.endsWith(text, length, "kan") && (this.flags & 1) == 0 && (this.flags & 2) == 0 && (this.flags & 0x40) == 0) {
            --this.numSyllables;
            return length - 3;
        }
        if (StemmerUtil.endsWith(text, length, "an") && (this.flags & 4) == 0 && (this.flags & 8) == 0 && (this.flags & 0x10) == 0) {
            --this.numSyllables;
            return length - 2;
        }
        if (StemmerUtil.endsWith(text, length, "i") && !StemmerUtil.endsWith(text, length, "si") && (this.flags & 0x20) == 0 && (this.flags & 1) == 0 && (this.flags & 2) == 0) {
            --this.numSyllables;
            return length - 1;
        }
        return length;
    }
}

