/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUBinary;
import com.ibm.icu.impl.Trie2;
import com.ibm.icu.impl.Trie2_16;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.ICUUncheckedIOException;
import com.ibm.icu.util.ULocale;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Locale;

public final class UCaseProps {
    private static final byte[] flagsOffset = new byte[]{0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8};
    public static final int MAX_STRING_LENGTH = 31;
    public static final int LOC_ROOT = 1;
    static final int LOC_TURKISH = 2;
    static final int LOC_LITHUANIAN = 3;
    static final int LOC_GREEK = 4;
    public static final int LOC_DUTCH = 5;
    static final int LOC_ARMENIAN = 6;
    private static final String iDot = "i\u0307";
    private static final String jDot = "j\u0307";
    private static final String iOgonekDot = "\u012f\u0307";
    private static final String iDotGrave = "i\u0307\u0300";
    private static final String iDotAcute = "i\u0307\u0301";
    private static final String iDotTilde = "i\u0307\u0303";
    static final int FOLD_CASE_OPTIONS_MASK = 7;
    public static final StringBuilder dummyStringBuilder = new StringBuilder();
    private int[] indexes;
    private String exceptions;
    private char[] unfold;
    private Trie2_16 trie;
    private static final String DATA_NAME = "ucase";
    private static final String DATA_TYPE = "icu";
    private static final String DATA_FILE_NAME = "ucase.icu";
    private static final int FMT = 1665225541;
    private static final int IX_TRIE_SIZE = 2;
    private static final int IX_EXC_LENGTH = 3;
    private static final int IX_UNFOLD_LENGTH = 4;
    private static final int IX_TOP = 16;
    public static final int TYPE_MASK = 3;
    public static final int NONE = 0;
    public static final int LOWER = 1;
    public static final int UPPER = 2;
    public static final int TITLE = 3;
    static final int IGNORABLE = 4;
    private static final int EXCEPTION = 8;
    private static final int SENSITIVE = 16;
    private static final int DOT_MASK = 96;
    private static final int SOFT_DOTTED = 32;
    private static final int ABOVE = 64;
    private static final int OTHER_ACCENT = 96;
    private static final int DELTA_SHIFT = 7;
    private static final int EXC_SHIFT = 4;
    private static final int EXC_LOWER = 0;
    private static final int EXC_FOLD = 1;
    private static final int EXC_UPPER = 2;
    private static final int EXC_TITLE = 3;
    private static final int EXC_DELTA = 4;
    private static final int EXC_CLOSURE = 6;
    private static final int EXC_FULL_MAPPINGS = 7;
    private static final int EXC_DOUBLE_SLOTS = 256;
    private static final int EXC_NO_SIMPLE_CASE_FOLDING = 512;
    private static final int EXC_DELTA_IS_NEGATIVE = 1024;
    private static final int EXC_SENSITIVE = 2048;
    private static final int EXC_DOT_SHIFT = 7;
    private static final int EXC_CONDITIONAL_SPECIAL = 16384;
    private static final int EXC_CONDITIONAL_FOLD = 32768;
    private static final int FULL_LOWER = 15;
    private static final int CLOSURE_MAX_LENGTH = 15;
    private static final int UNFOLD_ROWS = 0;
    private static final int UNFOLD_ROW_WIDTH = 1;
    private static final int UNFOLD_STRING_WIDTH = 2;
    public static final UCaseProps INSTANCE;

    private UCaseProps() throws IOException {
        ByteBuffer bytes = ICUBinary.getRequiredData(DATA_FILE_NAME);
        this.readData(bytes);
    }

    private final void readData(ByteBuffer bytes) throws IOException {
        ICUBinary.readHeader(bytes, 1665225541, new IsAcceptable());
        int count = bytes.getInt();
        if (count < 16) {
            throw new IOException("indexes[0] too small in ucase.icu");
        }
        this.indexes = new int[count];
        this.indexes[0] = count;
        for (int i = 1; i < count; ++i) {
            this.indexes[i] = bytes.getInt();
        }
        this.trie = Trie2_16.createFromSerialized(bytes);
        int expectedTrieLength = this.indexes[2];
        int trieLength = this.trie.getSerializedLength();
        if (trieLength > expectedTrieLength) {
            throw new IOException("ucase.icu: not enough bytes for the trie");
        }
        ICUBinary.skipBytes(bytes, expectedTrieLength - trieLength);
        count = this.indexes[3];
        if (count > 0) {
            this.exceptions = ICUBinary.getString(bytes, count, 0);
        }
        if ((count = this.indexes[4]) > 0) {
            this.unfold = ICUBinary.getChars(bytes, count, 0);
        }
    }

    public final void addPropertyStarts(UnicodeSet set) {
        for (Trie2.Range range : this.trie) {
            if (range.leadSurrogate) break;
            set.add(range.startCodePoint);
        }
    }

    private static final int getExceptionsOffset(int props) {
        return props >> 4;
    }

    static final boolean propsHasException(int props) {
        return (props & 8) != 0;
    }

    private static final boolean hasSlot(int flags, int index) {
        return (flags & 1 << index) != 0;
    }

    private static final byte slotOffset(int flags, int index) {
        return flagsOffset[flags & (1 << index) - 1];
    }

    private final long getSlotValueAndOffset(int excWord, int index, int excOffset) {
        long value;
        if ((excWord & 0x100) == 0) {
            value = this.exceptions.charAt(excOffset += UCaseProps.slotOffset(excWord, index));
        } else {
            excOffset += 2 * UCaseProps.slotOffset(excWord, index);
            value = this.exceptions.charAt(excOffset++);
            value = value << 16 | (long)this.exceptions.charAt(excOffset);
        }
        return value | (long)excOffset << 32;
    }

    private final int getSlotValue(int excWord, int index, int excOffset) {
        int value;
        if ((excWord & 0x100) == 0) {
            value = this.exceptions.charAt(excOffset += UCaseProps.slotOffset(excWord, index));
        } else {
            excOffset += 2 * UCaseProps.slotOffset(excWord, index);
            value = this.exceptions.charAt(excOffset++);
            value = value << 16 | this.exceptions.charAt(excOffset);
        }
        return value;
    }

    public final int tolower(int c) {
        int props = this.trie.get(c);
        if (!UCaseProps.propsHasException(props)) {
            if (UCaseProps.isUpperOrTitleFromProps(props)) {
                c += UCaseProps.getDelta(props);
            }
        } else {
            char excWord;
            int excOffset = UCaseProps.getExceptionsOffset(props);
            if (UCaseProps.hasSlot(excWord = this.exceptions.charAt(excOffset++), 4) && UCaseProps.isUpperOrTitleFromProps(props)) {
                int delta = this.getSlotValue(excWord, 4, excOffset);
                return (excWord & 0x400) == 0 ? c + delta : c - delta;
            }
            if (UCaseProps.hasSlot(excWord, 0)) {
                c = this.getSlotValue(excWord, 0, excOffset);
            }
        }
        return c;
    }

    public final int toupper(int c) {
        int props = this.trie.get(c);
        if (!UCaseProps.propsHasException(props)) {
            if (UCaseProps.getTypeFromProps(props) == 1) {
                c += UCaseProps.getDelta(props);
            }
        } else {
            char excWord;
            int excOffset = UCaseProps.getExceptionsOffset(props);
            if (UCaseProps.hasSlot(excWord = this.exceptions.charAt(excOffset++), 4) && UCaseProps.getTypeFromProps(props) == 1) {
                int delta = this.getSlotValue(excWord, 4, excOffset);
                return (excWord & 0x400) == 0 ? c + delta : c - delta;
            }
            if (UCaseProps.hasSlot(excWord, 2)) {
                c = this.getSlotValue(excWord, 2, excOffset);
            }
        }
        return c;
    }

    public final int totitle(int c) {
        int props = this.trie.get(c);
        if (!UCaseProps.propsHasException(props)) {
            if (UCaseProps.getTypeFromProps(props) == 1) {
                c += UCaseProps.getDelta(props);
            }
        } else {
            int index;
            char excWord;
            int excOffset = UCaseProps.getExceptionsOffset(props);
            if (UCaseProps.hasSlot(excWord = this.exceptions.charAt(excOffset++), 4) && UCaseProps.getTypeFromProps(props) == 1) {
                int delta = this.getSlotValue(excWord, 4, excOffset);
                return (excWord & 0x400) == 0 ? c + delta : c - delta;
            }
            if (UCaseProps.hasSlot(excWord, 3)) {
                index = 3;
            } else if (UCaseProps.hasSlot(excWord, 2)) {
                index = 2;
            } else {
                return c;
            }
            c = this.getSlotValue(excWord, index, excOffset);
        }
        return c;
    }

    public final void addCaseClosure(int c, UnicodeSet set) {
        int props = this.trie.get(c);
        if (!UCaseProps.propsHasException(props)) {
            int delta;
            if (UCaseProps.getTypeFromProps(props) != 0 && (delta = UCaseProps.getDelta(props)) != 0) {
                set.add(c + delta);
            }
        } else {
            int closureOffset;
            int closureLength;
            long value;
            int excOffset = UCaseProps.getExceptionsOffset(props);
            char excWord = this.exceptions.charAt(excOffset++);
            int excOffset0 = excOffset;
            if ((excWord & 0x8000) != 0) {
                if (c == 73) {
                    set.add(105);
                    return;
                }
                if (c == 304) {
                    set.add(iDot);
                    return;
                }
            } else {
                if (c == 105) {
                    set.add(73);
                    return;
                }
                if (c == 305) {
                    return;
                }
            }
            for (int index = 0; index <= 3; ++index) {
                if (!UCaseProps.hasSlot(excWord, index)) continue;
                excOffset = excOffset0;
                int mapping = this.getSlotValue(excWord, index, excOffset);
                set.add(mapping);
            }
            if (UCaseProps.hasSlot(excWord, 4)) {
                excOffset = excOffset0;
                int delta = this.getSlotValue(excWord, 4, excOffset);
                set.add((excWord & 0x400) == 0 ? c + delta : c - delta);
            }
            if (UCaseProps.hasSlot(excWord, 6)) {
                excOffset = excOffset0;
                value = this.getSlotValueAndOffset(excWord, 6, excOffset);
                closureLength = (int)value & 0xF;
                closureOffset = (int)(value >> 32) + 1;
            } else {
                closureLength = 0;
                closureOffset = 0;
            }
            if (UCaseProps.hasSlot(excWord, 7)) {
                excOffset = excOffset0;
                value = this.getSlotValueAndOffset(excWord, 7, excOffset);
                int fullLength = (int)value;
                excOffset = (int)(value >> 32) + 1;
                excOffset += (fullLength &= 0xFFFF) & 0xF;
                int length = (fullLength >>= 4) & 0xF;
                if (length != 0) {
                    set.add(this.exceptions.substring(excOffset, excOffset + length));
                    excOffset += length;
                }
                excOffset += (fullLength >>= 4) & 0xF;
                closureOffset = excOffset += (fullLength >>= 4);
            }
            int limit = closureOffset + closureLength;
            for (int index = closureOffset; index < limit; index += UTF16.getCharCount(c)) {
                int mapping = this.exceptions.codePointAt(index);
                set.add(mapping);
            }
        }
    }

    private static void addOneSimpleCaseClosure(int c, int t, UnicodeSet set) {
        switch (c) {
            case 912: {
                if (t != 8147) break;
                return;
            }
            case 944: {
                if (t != 8163) break;
                return;
            }
            case 8147: {
                if (t != 912) break;
                return;
            }
            case 8163: {
                if (t != 944) break;
                return;
            }
            case 64261: {
                if (t != 64262) break;
                return;
            }
            case 64262: {
                if (t != 64261) break;
                return;
            }
        }
        set.add(t);
    }

    public final void addSimpleCaseClosure(int c, UnicodeSet set) {
        int props = this.trie.get(c);
        if (!UCaseProps.propsHasException(props)) {
            int delta;
            if (UCaseProps.getTypeFromProps(props) != 0 && (delta = UCaseProps.getDelta(props)) != 0) {
                set.add(c + delta);
            }
        } else {
            int closureOffset;
            int closureLength;
            long value;
            int mapping;
            int excOffset = UCaseProps.getExceptionsOffset(props);
            char excWord = this.exceptions.charAt(excOffset++);
            int excOffset0 = excOffset;
            if ((excWord & 0x8000) != 0) {
                if (c == 73) {
                    set.add(105);
                    return;
                }
                if (c == 304) {
                    return;
                }
            } else {
                if (c == 105) {
                    set.add(73);
                    return;
                }
                if (c == 305) {
                    return;
                }
            }
            for (int index = 0; index <= 3; ++index) {
                if (!UCaseProps.hasSlot(excWord, index)) continue;
                excOffset = excOffset0;
                mapping = this.getSlotValue(excWord, index, excOffset);
                UCaseProps.addOneSimpleCaseClosure(c, mapping, set);
            }
            if (UCaseProps.hasSlot(excWord, 4)) {
                excOffset = excOffset0;
                int delta = this.getSlotValue(excWord, 4, excOffset);
                mapping = (excWord & 0x400) == 0 ? c + delta : c - delta;
                UCaseProps.addOneSimpleCaseClosure(c, mapping, set);
            }
            if (UCaseProps.hasSlot(excWord, 6)) {
                excOffset = excOffset0;
                value = this.getSlotValueAndOffset(excWord, 6, excOffset);
                closureLength = (int)value & 0xF;
                closureOffset = (int)(value >> 32) + 1;
            } else {
                closureLength = 0;
                closureOffset = 0;
            }
            if (closureLength > 0 && UCaseProps.hasSlot(excWord, 7)) {
                excOffset = excOffset0;
                value = this.getSlotValueAndOffset(excWord, 7, excOffset);
                int fullLength = (int)value;
                excOffset = (int)(value >> 32) + 1;
                excOffset += (fullLength &= 0xFFFF) & 0xF;
                excOffset += (fullLength >>= 4) & 0xF;
                excOffset += (fullLength >>= 4) & 0xF;
                closureOffset = excOffset += (fullLength >>= 4);
            }
            int limit = closureOffset + closureLength;
            for (int index = closureOffset; index < limit; index += UTF16.getCharCount(c)) {
                int mapping2 = this.exceptions.codePointAt(index);
                UCaseProps.addOneSimpleCaseClosure(c, mapping2, set);
            }
        }
    }

    private final int strcmpMax(String s, int unfoldOffset, int max) {
        int length = s.length();
        max -= length;
        int i1 = 0;
        do {
            char c2;
            int c1 = s.charAt(i1++);
            if ((c2 = this.unfold[unfoldOffset++]) == '\u0000') {
                return 1;
            }
            if ((c1 -= c2) == 0) continue;
            return c1;
        } while (--length > 0);
        if (max == 0 || this.unfold[unfoldOffset] == '\u0000') {
            return 0;
        }
        return -max;
    }

    public final boolean addStringCaseClosure(String s, UnicodeSet set) {
        if (this.unfold == null || s == null) {
            return false;
        }
        int length = s.length();
        if (length <= 1) {
            return false;
        }
        int unfoldRows = this.unfold[0];
        char unfoldRowWidth = this.unfold[1];
        int unfoldStringWidth = this.unfold[2];
        if (length > unfoldStringWidth) {
            return false;
        }
        int start = 0;
        int limit = unfoldRows;
        while (start < limit) {
            int i = (start + limit) / 2;
            int unfoldOffset = (i + 1) * unfoldRowWidth;
            int result = this.strcmpMax(s, unfoldOffset, unfoldStringWidth);
            if (result == 0) {
                int c;
                for (i = unfoldStringWidth; i < unfoldRowWidth && this.unfold[unfoldOffset + i] != '\u0000'; i += UTF16.getCharCount(c)) {
                    c = UTF16.charAt(this.unfold, unfoldOffset, this.unfold.length, i);
                    set.add(c);
                    this.addCaseClosure(c, set);
                }
                return true;
            }
            if (result < 0) {
                limit = i;
                continue;
            }
            start = i + 1;
        }
        return false;
    }

    public final int getType(int c) {
        return UCaseProps.getTypeFromProps(this.trie.get(c));
    }

    public final int getTypeOrIgnorable(int c) {
        return UCaseProps.getTypeAndIgnorableFromProps(this.trie.get(c));
    }

    public final int getDotType(int c) {
        int props = this.trie.get(c);
        if (!UCaseProps.propsHasException(props)) {
            return props & 0x60;
        }
        return this.exceptions.charAt(UCaseProps.getExceptionsOffset(props)) >> 7 & 0x60;
    }

    public final boolean isSoftDotted(int c) {
        return this.getDotType(c) == 32;
    }

    public final boolean isCaseSensitive(int c) {
        int props = this.trie.get(c);
        if (!UCaseProps.propsHasException(props)) {
            return (props & 0x10) != 0;
        }
        return (this.exceptions.charAt(UCaseProps.getExceptionsOffset(props)) & 0x800) != 0;
    }

    public static final int getCaseLocale(Locale locale) {
        return UCaseProps.getCaseLocale(locale.getLanguage());
    }

    public static final int getCaseLocale(ULocale locale) {
        return UCaseProps.getCaseLocale(locale.getLanguage());
    }

    private static final int getCaseLocale(String language) {
        if (language.length() == 2) {
            if (language.equals("en") || language.charAt(0) > 't') {
                return 1;
            }
            if (language.equals("tr") || language.equals("az")) {
                return 2;
            }
            if (language.equals("el")) {
                return 4;
            }
            if (language.equals("lt")) {
                return 3;
            }
            if (language.equals("nl")) {
                return 5;
            }
            if (language.equals("hy")) {
                return 6;
            }
        } else if (language.length() == 3) {
            if (language.equals("tur") || language.equals("aze")) {
                return 2;
            }
            if (language.equals("ell")) {
                return 4;
            }
            if (language.equals("lit")) {
                return 3;
            }
            if (language.equals("nld")) {
                return 5;
            }
            if (language.equals("hye")) {
                return 6;
            }
        }
        return 1;
    }

    private final boolean isFollowedByCasedLetter(ContextIterator iter, int dir) {
        int c;
        if (iter == null) {
            return false;
        }
        iter.reset(dir);
        while ((c = iter.next()) >= 0) {
            int type = this.getTypeOrIgnorable(c);
            if ((type & 4) != 0) continue;
            return type != 0;
        }
        return false;
    }

    private final boolean isPrecededBySoftDotted(ContextIterator iter) {
        int c;
        if (iter == null) {
            return false;
        }
        iter.reset(-1);
        while ((c = iter.next()) >= 0) {
            int dotType = this.getDotType(c);
            if (dotType == 32) {
                return true;
            }
            if (dotType == 96) continue;
            return false;
        }
        return false;
    }

    private final boolean isPrecededBy_I(ContextIterator iter) {
        int c;
        if (iter == null) {
            return false;
        }
        iter.reset(-1);
        while ((c = iter.next()) >= 0) {
            if (c == 73) {
                return true;
            }
            int dotType = this.getDotType(c);
            if (dotType == 96) continue;
            return false;
        }
        return false;
    }

    private final boolean isFollowedByMoreAbove(ContextIterator iter) {
        int c;
        if (iter == null) {
            return false;
        }
        iter.reset(1);
        while ((c = iter.next()) >= 0) {
            int dotType = this.getDotType(c);
            if (dotType == 64) {
                return true;
            }
            if (dotType == 96) continue;
            return false;
        }
        return false;
    }

    private final boolean isFollowedByDotAbove(ContextIterator iter) {
        int c;
        if (iter == null) {
            return false;
        }
        iter.reset(1);
        while ((c = iter.next()) >= 0) {
            if (c == 775) {
                return true;
            }
            int dotType = this.getDotType(c);
            if (dotType == 96) continue;
            return false;
        }
        return false;
    }

    public final int toFullLower(int c, ContextIterator iter, Appendable out, int caseLocale) {
        int result = c;
        int props = this.trie.get(c);
        if (!UCaseProps.propsHasException(props)) {
            if (UCaseProps.isUpperOrTitleFromProps(props)) {
                result = c + UCaseProps.getDelta(props);
            }
        } else {
            long value;
            int full;
            int excOffset = UCaseProps.getExceptionsOffset(props);
            char excWord = this.exceptions.charAt(excOffset++);
            int excOffset2 = excOffset;
            if ((excWord & 0x4000) != 0) {
                if (caseLocale == 3 && ((c == 73 || c == 74 || c == 302) && this.isFollowedByMoreAbove(iter) || c == 204 || c == 205 || c == 296)) {
                    try {
                        switch (c) {
                            case 73: {
                                out.append(iDot);
                                return 2;
                            }
                            case 74: {
                                out.append(jDot);
                                return 2;
                            }
                            case 302: {
                                out.append(iOgonekDot);
                                return 2;
                            }
                            case 204: {
                                out.append(iDotGrave);
                                return 3;
                            }
                            case 205: {
                                out.append(iDotAcute);
                                return 3;
                            }
                            case 296: {
                                out.append(iDotTilde);
                                return 3;
                            }
                        }
                        return 0;
                    }
                    catch (IOException e) {
                        throw new ICUUncheckedIOException(e);
                    }
                }
                if (caseLocale == 2 && c == 304) {
                    return 105;
                }
                if (caseLocale == 2 && c == 775 && this.isPrecededBy_I(iter)) {
                    return 0;
                }
                if (caseLocale == 2 && c == 73 && !this.isFollowedByDotAbove(iter)) {
                    return 305;
                }
                if (c == 304) {
                    try {
                        out.append(iDot);
                        return 2;
                    }
                    catch (IOException e) {
                        throw new ICUUncheckedIOException(e);
                    }
                }
                if (c == 931 && !this.isFollowedByCasedLetter(iter, 1) && this.isFollowedByCasedLetter(iter, -1)) {
                    return 962;
                }
            } else if (UCaseProps.hasSlot(excWord, 7) && (full = (int)(value = this.getSlotValueAndOffset(excWord, 7, excOffset)) & 0xF) != 0) {
                excOffset = (int)(value >> 32) + 1;
                try {
                    out.append(this.exceptions, excOffset, excOffset + full);
                    return full;
                }
                catch (IOException e) {
                    throw new ICUUncheckedIOException(e);
                }
            }
            if (UCaseProps.hasSlot(excWord, 4) && UCaseProps.isUpperOrTitleFromProps(props)) {
                int delta = this.getSlotValue(excWord, 4, excOffset2);
                return (excWord & 0x400) == 0 ? c + delta : c - delta;
            }
            if (UCaseProps.hasSlot(excWord, 0)) {
                result = this.getSlotValue(excWord, 0, excOffset2);
            }
        }
        return result == c ? ~result : result;
    }

    private final int toUpperOrTitle(int c, ContextIterator iter, Appendable out, int loc, boolean upperNotTitle) {
        int result = c;
        int props = this.trie.get(c);
        if (!UCaseProps.propsHasException(props)) {
            if (UCaseProps.getTypeFromProps(props) == 1) {
                result = c + UCaseProps.getDelta(props);
            }
        } else {
            int index;
            int excOffset = UCaseProps.getExceptionsOffset(props);
            char excWord = this.exceptions.charAt(excOffset++);
            int excOffset2 = excOffset;
            if ((excWord & 0x4000) != 0) {
                if (loc == 2 && c == 105) {
                    return 304;
                }
                if (loc == 3 && c == 775 && this.isPrecededBySoftDotted(iter)) {
                    return 0;
                }
                if (c == 1415) {
                    try {
                        if (loc == 6) {
                            out.append(upperNotTitle ? "\u0535\u054e" : "\u0535\u057e");
                        } else {
                            out.append(upperNotTitle ? "\u0535\u0552" : "\u0535\u0582");
                        }
                        return 2;
                    }
                    catch (IOException e) {
                        throw new ICUUncheckedIOException(e);
                    }
                }
            } else if (UCaseProps.hasSlot(excWord, 7)) {
                long value = this.getSlotValueAndOffset(excWord, 7, excOffset);
                int full = (int)value & 0xFFFF;
                excOffset = (int)(value >> 32) + 1;
                excOffset += full & 0xF;
                excOffset += (full >>= 4) & 0xF;
                full >>= 4;
                if (upperNotTitle) {
                    full &= 0xF;
                } else {
                    excOffset += full & 0xF;
                    full = full >> 4 & 0xF;
                }
                if (full != 0) {
                    try {
                        out.append(this.exceptions, excOffset, excOffset + full);
                        return full;
                    }
                    catch (IOException e) {
                        throw new ICUUncheckedIOException(e);
                    }
                }
            }
            if (UCaseProps.hasSlot(excWord, 4) && UCaseProps.getTypeFromProps(props) == 1) {
                int delta = this.getSlotValue(excWord, 4, excOffset2);
                return (excWord & 0x400) == 0 ? c + delta : c - delta;
            }
            if (!upperNotTitle && UCaseProps.hasSlot(excWord, 3)) {
                index = 3;
            } else if (UCaseProps.hasSlot(excWord, 2)) {
                index = 2;
            } else {
                return ~c;
            }
            result = this.getSlotValue(excWord, index, excOffset2);
        }
        return result == c ? ~result : result;
    }

    public final int toFullUpper(int c, ContextIterator iter, Appendable out, int caseLocale) {
        return this.toUpperOrTitle(c, iter, out, caseLocale, true);
    }

    public final int toFullTitle(int c, ContextIterator iter, Appendable out, int caseLocale) {
        return this.toUpperOrTitle(c, iter, out, caseLocale, false);
    }

    public final int fold(int c, int options) {
        int props = this.trie.get(c);
        if (!UCaseProps.propsHasException(props)) {
            if (UCaseProps.isUpperOrTitleFromProps(props)) {
                c += UCaseProps.getDelta(props);
            }
        } else {
            int index;
            char excWord;
            int excOffset = UCaseProps.getExceptionsOffset(props);
            if (((excWord = this.exceptions.charAt(excOffset++)) & 0x8000) != 0) {
                if ((options & 7) == 0) {
                    if (c == 73) {
                        return 105;
                    }
                    if (c == 304) {
                        return c;
                    }
                } else {
                    if (c == 73) {
                        return 305;
                    }
                    if (c == 304) {
                        return 105;
                    }
                }
            }
            if ((excWord & 0x200) != 0) {
                return c;
            }
            if (UCaseProps.hasSlot(excWord, 4) && UCaseProps.isUpperOrTitleFromProps(props)) {
                int delta = this.getSlotValue(excWord, 4, excOffset);
                return (excWord & 0x400) == 0 ? c + delta : c - delta;
            }
            if (UCaseProps.hasSlot(excWord, 1)) {
                index = 1;
            } else if (UCaseProps.hasSlot(excWord, 0)) {
                index = 0;
            } else {
                return c;
            }
            c = this.getSlotValue(excWord, index, excOffset);
        }
        return c;
    }

    public final int toFullFolding(int c, Appendable out, int options) {
        int result = c;
        int props = this.trie.get(c);
        if (!UCaseProps.propsHasException(props)) {
            if (UCaseProps.isUpperOrTitleFromProps(props)) {
                result = c + UCaseProps.getDelta(props);
            }
        } else {
            int index;
            int excOffset = UCaseProps.getExceptionsOffset(props);
            char excWord = this.exceptions.charAt(excOffset++);
            int excOffset2 = excOffset;
            if ((excWord & 0x8000) != 0) {
                if ((options & 7) == 0) {
                    if (c == 73) {
                        return 105;
                    }
                    if (c == 304) {
                        try {
                            out.append(iDot);
                            return 2;
                        }
                        catch (IOException e) {
                            throw new ICUUncheckedIOException(e);
                        }
                    }
                } else {
                    if (c == 73) {
                        return 305;
                    }
                    if (c == 304) {
                        return 105;
                    }
                }
            } else if (UCaseProps.hasSlot(excWord, 7)) {
                long value = this.getSlotValueAndOffset(excWord, 7, excOffset);
                int full = (int)value & 0xFFFF;
                excOffset = (int)(value >> 32) + 1;
                excOffset += full & 0xF;
                if ((full = full >> 4 & 0xF) != 0) {
                    try {
                        out.append(this.exceptions, excOffset, excOffset + full);
                        return full;
                    }
                    catch (IOException e) {
                        throw new ICUUncheckedIOException(e);
                    }
                }
            }
            if ((excWord & 0x200) != 0) {
                return ~c;
            }
            if (UCaseProps.hasSlot(excWord, 4) && UCaseProps.isUpperOrTitleFromProps(props)) {
                int delta = this.getSlotValue(excWord, 4, excOffset2);
                return (excWord & 0x400) == 0 ? c + delta : c - delta;
            }
            if (UCaseProps.hasSlot(excWord, 1)) {
                index = 1;
            } else if (UCaseProps.hasSlot(excWord, 0)) {
                index = 0;
            } else {
                return ~c;
            }
            result = this.getSlotValue(excWord, index, excOffset2);
        }
        return result == c ? ~result : result;
    }

    public final boolean hasBinaryProperty(int c, int which) {
        switch (which) {
            case 22: {
                return 1 == this.getType(c);
            }
            case 30: {
                return 2 == this.getType(c);
            }
            case 27: {
                return this.isSoftDotted(c);
            }
            case 34: {
                return this.isCaseSensitive(c);
            }
            case 49: {
                return 0 != this.getType(c);
            }
            case 50: {
                return this.getTypeOrIgnorable(c) >> 2 != 0;
            }
            case 51: {
                dummyStringBuilder.setLength(0);
                return this.toFullLower(c, null, dummyStringBuilder, 1) >= 0;
            }
            case 52: {
                dummyStringBuilder.setLength(0);
                return this.toFullUpper(c, null, dummyStringBuilder, 1) >= 0;
            }
            case 53: {
                dummyStringBuilder.setLength(0);
                return this.toFullTitle(c, null, dummyStringBuilder, 1) >= 0;
            }
            case 55: {
                dummyStringBuilder.setLength(0);
                return this.toFullLower(c, null, dummyStringBuilder, 1) >= 0 || this.toFullUpper(c, null, dummyStringBuilder, 1) >= 0 || this.toFullTitle(c, null, dummyStringBuilder, 1) >= 0;
            }
        }
        return false;
    }

    static Trie2_16 getTrie() {
        return UCaseProps.INSTANCE.trie;
    }

    static final int getTypeFromProps(int props) {
        return props & 3;
    }

    private static final int getTypeAndIgnorableFromProps(int props) {
        return props & 7;
    }

    static final boolean isUpperOrTitleFromProps(int props) {
        return (props & 2) != 0;
    }

    static final int getDelta(int props) {
        return (short)props >> 7;
    }

    static {
        try {
            INSTANCE = new UCaseProps();
        }
        catch (IOException e) {
            throw new ICUUncheckedIOException(e);
        }
    }

    static final class LatinCase {
        static final char LIMIT = '\u0180';
        static final char LONG_S = '\u017f';
        static final byte EXC = -128;
        static final byte[] TO_LOWER_NORMAL = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 0, 32, 32, 32, 32, 32, 32, 32, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, -128, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, -128, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, -121, 1, 0, 1, 0, 1, 0, -128};
        static final byte[] TO_LOWER_TR_LT = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 32, 32, 32, 32, 32, 32, 32, -128, -128, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, -128, -128, 32, 32, 32, 32, 32, 32, 32, 32, 32, 0, 32, 32, 32, 32, 32, 32, 32, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, -128, 0, 1, 0, 1, 0, -128, 0, -128, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, -128, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, -121, 1, 0, 1, 0, 1, 0, -128};
        static final byte[] TO_UPPER_NORMAL = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -128, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, 0, -32, -32, -32, -32, -32, -32, -32, 121, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -128, 0, -1, 0, -1, 0, -1, 0, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, -128, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, 0, -1, 0, -1, 0, -1, -128};
        static final byte[] TO_UPPER_TR = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -32, -32, -32, -32, -32, -32, -32, -32, -128, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -128, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, -32, 0, -32, -32, -32, -32, -32, -32, -32, 121, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -128, 0, -1, 0, -1, 0, -1, 0, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, -128, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, -1, 0, 0, -1, 0, -1, 0, -1, -128};

        LatinCase() {
        }
    }

    public static interface ContextIterator {
        public void reset(int var1);

        public int next();
    }

    private static final class IsAcceptable
    implements ICUBinary.Authenticate {
        private IsAcceptable() {
        }

        @Override
        public boolean isDataVersionAcceptable(byte[] version) {
            return version[0] == 4;
        }
    }
}

