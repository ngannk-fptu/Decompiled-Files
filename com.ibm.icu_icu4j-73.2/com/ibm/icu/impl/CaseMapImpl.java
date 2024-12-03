/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import com.ibm.icu.impl.Trie2_16;
import com.ibm.icu.impl.UCaseProps;
import com.ibm.icu.impl.UCharacterProperty;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.Edits;
import com.ibm.icu.util.ICUUncheckedIOException;
import com.ibm.icu.util.ULocale;
import java.io.IOException;
import java.text.CharacterIterator;
import java.util.Locale;

public final class CaseMapImpl {
    public static final int TITLECASE_WHOLE_STRING = 32;
    public static final int TITLECASE_SENTENCES = 64;
    private static final int TITLECASE_ITERATOR_MASK = 224;
    public static final int TITLECASE_ADJUST_TO_CASED = 1024;
    private static final int TITLECASE_ADJUSTMENT_MASK = 1536;
    private static final char ACUTE = '\u0301';
    private static final int U_GC_M_MASK = 448;
    private static final int LNS = 251792942;
    public static final int OMIT_UNCHANGED_TEXT = 16384;
    private static final Trie2_16 CASE_TRIE = UCaseProps.getTrie();

    public static int addTitleAdjustmentOption(int options, int newOption) {
        int adjOptions = options & 0x600;
        if (adjOptions != 0 && adjOptions != newOption) {
            throw new IllegalArgumentException("multiple titlecasing index adjustment options");
        }
        return options | newOption;
    }

    private static boolean isLNS(int c) {
        int gc = UCharacterProperty.INSTANCE.getType(c);
        return (1 << gc & 0xF020E2E) != 0 || gc == 4 && UCaseProps.INSTANCE.getType(c) != 0;
    }

    public static int addTitleIteratorOption(int options, int newOption) {
        int iterOptions = options & 0xE0;
        if (iterOptions != 0 && iterOptions != newOption) {
            throw new IllegalArgumentException("multiple titlecasing iterator options");
        }
        return options | newOption;
    }

    public static BreakIterator getTitleBreakIterator(Locale locale, int options, BreakIterator iter) {
        if ((options &= 0xE0) != 0 && iter != null) {
            throw new IllegalArgumentException("titlecasing iterator option together with an explicit iterator");
        }
        if (iter == null) {
            switch (options) {
                case 0: {
                    iter = BreakIterator.getWordInstance(locale);
                    break;
                }
                case 32: {
                    iter = new WholeStringBreakIterator();
                    break;
                }
                case 64: {
                    iter = BreakIterator.getSentenceInstance(locale);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("unknown titlecasing iterator option");
                }
            }
        }
        return iter;
    }

    public static BreakIterator getTitleBreakIterator(ULocale locale, int options, BreakIterator iter) {
        if ((options &= 0xE0) != 0 && iter != null) {
            throw new IllegalArgumentException("titlecasing iterator option together with an explicit iterator");
        }
        if (iter == null) {
            switch (options) {
                case 0: {
                    iter = BreakIterator.getWordInstance(locale);
                    break;
                }
                case 32: {
                    iter = new WholeStringBreakIterator();
                    break;
                }
                case 64: {
                    iter = BreakIterator.getSentenceInstance(locale);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("unknown titlecasing iterator option");
                }
            }
        }
        return iter;
    }

    private static int appendCodePoint(Appendable a, int c) throws IOException {
        if (c <= 65535) {
            a.append((char)c);
            return 1;
        }
        a.append((char)(55232 + (c >> 10)));
        a.append((char)(56320 + (c & 0x3FF)));
        return 2;
    }

    private static void appendResult(int result, Appendable dest, int cpLength, int options, Edits edits) throws IOException {
        if (result < 0) {
            if (edits != null) {
                edits.addUnchanged(cpLength);
            }
            if ((options & 0x4000) != 0) {
                return;
            }
            CaseMapImpl.appendCodePoint(dest, ~result);
        } else if (result <= 31) {
            if (edits != null) {
                edits.addReplace(cpLength, result);
            }
        } else {
            int length = CaseMapImpl.appendCodePoint(dest, result);
            if (edits != null) {
                edits.addReplace(cpLength, length);
            }
        }
    }

    private static final void appendUnchanged(CharSequence src, int start, int length, Appendable dest, int options, Edits edits) throws IOException {
        if (length > 0) {
            if (edits != null) {
                edits.addUnchanged(length);
            }
            if ((options & 0x4000) != 0) {
                return;
            }
            dest.append(src, start, start + length);
        }
    }

    private static String applyEdits(CharSequence src, StringBuilder replacementChars, Edits edits) {
        if (!edits.hasChanges()) {
            return src.toString();
        }
        StringBuilder result = new StringBuilder(src.length() + edits.lengthDelta());
        Edits.Iterator ei = edits.getCoarseIterator();
        while (ei.next()) {
            int i;
            if (ei.hasChange()) {
                i = ei.replacementIndex();
                result.append(replacementChars, i, i + ei.newLength());
                continue;
            }
            i = ei.sourceIndex();
            result.append(src, i, i + ei.oldLength());
        }
        return result.toString();
    }

    private static void internalToLower(int caseLocale, int options, CharSequence src, int srcStart, int srcLimit, StringContextIterator iter, Appendable dest, Edits edits) throws IOException {
        byte[] latinToLower = caseLocale == 1 || (caseLocale >= 0 ? caseLocale != 2 && caseLocale != 3 : (options & 7) == 0) ? UCaseProps.LatinCase.TO_LOWER_NORMAL : UCaseProps.LatinCase.TO_LOWER_TR_LT;
        int prev = srcStart;
        int srcIndex = srcStart;
        while (srcIndex < srcLimit) {
            int c;
            char trail;
            int lead;
            block10: {
                int delta;
                block11: {
                    int props;
                    block9: {
                        lead = src.charAt(srcIndex);
                        if (lead >= 383) break block9;
                        int d = latinToLower[lead];
                        if (d == -128) break block10;
                        ++srcIndex;
                        if (d == 0) continue;
                        delta = d;
                        break block11;
                    }
                    if (lead < 55296 && !UCaseProps.propsHasException(props = CASE_TRIE.getFromU16SingleLead((char)lead))) {
                        ++srcIndex;
                        if (!UCaseProps.isUpperOrTitleFromProps(props) || (delta = UCaseProps.getDelta(props)) == 0) continue;
                    }
                    break block10;
                }
                lead = (char)(lead + delta);
                CaseMapImpl.appendUnchanged(src, prev, srcIndex - 1 - prev, dest, options, edits);
                dest.append((char)lead);
                if (edits != null) {
                    edits.addReplace(1, 1);
                }
                prev = srcIndex;
                continue;
            }
            int cpStart = srcIndex++;
            if (Character.isHighSurrogate((char)lead) && srcIndex < srcLimit && Character.isLowSurrogate(trail = src.charAt(srcIndex))) {
                c = Character.toCodePoint((char)lead, trail);
                ++srcIndex;
            } else {
                c = lead;
            }
            CaseMapImpl.appendUnchanged(src, prev, cpStart - prev, dest, options, edits);
            prev = cpStart;
            if (caseLocale >= 0) {
                if (iter == null) {
                    iter = new StringContextIterator(src, cpStart, srcIndex);
                } else {
                    iter.setCPStartAndLimit(cpStart, srcIndex);
                }
                c = UCaseProps.INSTANCE.toFullLower(c, iter, dest, caseLocale);
            } else {
                c = UCaseProps.INSTANCE.toFullFolding(c, dest, options);
            }
            if (c < 0) continue;
            CaseMapImpl.appendResult(c, dest, srcIndex - cpStart, options, edits);
            prev = srcIndex;
        }
        CaseMapImpl.appendUnchanged(src, prev, srcIndex - prev, dest, options, edits);
    }

    private static void internalToUpper(int caseLocale, int options, CharSequence src, Appendable dest, Edits edits) throws IOException {
        StringContextIterator iter = null;
        byte[] latinToUpper = caseLocale == 2 ? UCaseProps.LatinCase.TO_UPPER_TR : UCaseProps.LatinCase.TO_UPPER_NORMAL;
        int prev = 0;
        int srcIndex = 0;
        int srcLength = src.length();
        while (srcIndex < srcLength) {
            int c;
            char trail;
            int lead;
            block8: {
                int delta;
                block9: {
                    int props;
                    block7: {
                        lead = src.charAt(srcIndex);
                        if (lead >= 383) break block7;
                        int d = latinToUpper[lead];
                        if (d == -128) break block8;
                        ++srcIndex;
                        if (d == 0) continue;
                        delta = d;
                        break block9;
                    }
                    if (lead < 55296 && !UCaseProps.propsHasException(props = CASE_TRIE.getFromU16SingleLead((char)lead))) {
                        ++srcIndex;
                        if (UCaseProps.getTypeFromProps(props) != 1 || (delta = UCaseProps.getDelta(props)) == 0) continue;
                    }
                    break block8;
                }
                lead = (char)(lead + delta);
                CaseMapImpl.appendUnchanged(src, prev, srcIndex - 1 - prev, dest, options, edits);
                dest.append((char)lead);
                if (edits != null) {
                    edits.addReplace(1, 1);
                }
                prev = srcIndex;
                continue;
            }
            int cpStart = srcIndex++;
            if (Character.isHighSurrogate((char)lead) && srcIndex < srcLength && Character.isLowSurrogate(trail = src.charAt(srcIndex))) {
                c = Character.toCodePoint((char)lead, trail);
                ++srcIndex;
            } else {
                c = lead;
            }
            if (iter == null) {
                iter = new StringContextIterator(src, cpStart, srcIndex);
            } else {
                iter.setCPStartAndLimit(cpStart, srcIndex);
            }
            CaseMapImpl.appendUnchanged(src, prev, cpStart - prev, dest, options, edits);
            prev = cpStart;
            if ((c = UCaseProps.INSTANCE.toFullUpper(c, iter, dest, caseLocale)) < 0) continue;
            CaseMapImpl.appendResult(c, dest, srcIndex - cpStart, options, edits);
            prev = srcIndex;
        }
        CaseMapImpl.appendUnchanged(src, prev, srcIndex - prev, dest, options, edits);
    }

    public static String toLower(int caseLocale, int options, CharSequence src) {
        if (src.length() <= 100 && (options & 0x4000) == 0) {
            if (src.length() == 0) {
                return src.toString();
            }
            Edits edits = new Edits();
            StringBuilder replacementChars = CaseMapImpl.toLower(caseLocale, options | 0x4000, src, new StringBuilder(), edits);
            return CaseMapImpl.applyEdits(src, replacementChars, edits);
        }
        return CaseMapImpl.toLower(caseLocale, options, src, new StringBuilder(src.length()), null).toString();
    }

    public static <A extends Appendable> A toLower(int caseLocale, int options, CharSequence src, A dest, Edits edits) {
        try {
            if (edits != null) {
                edits.reset();
            }
            CaseMapImpl.internalToLower(caseLocale, options, src, 0, src.length(), null, dest, edits);
            return dest;
        }
        catch (IOException e) {
            throw new ICUUncheckedIOException(e);
        }
    }

    public static String toUpper(int caseLocale, int options, CharSequence src) {
        if (src.length() <= 100 && (options & 0x4000) == 0) {
            if (src.length() == 0) {
                return src.toString();
            }
            Edits edits = new Edits();
            StringBuilder replacementChars = CaseMapImpl.toUpper(caseLocale, options | 0x4000, src, new StringBuilder(), edits);
            return CaseMapImpl.applyEdits(src, replacementChars, edits);
        }
        return CaseMapImpl.toUpper(caseLocale, options, src, new StringBuilder(src.length()), null).toString();
    }

    public static <A extends Appendable> A toUpper(int caseLocale, int options, CharSequence src, A dest, Edits edits) {
        try {
            if (edits != null) {
                edits.reset();
            }
            if (caseLocale == 4) {
                return (A)GreekUpper.toUpper(options, src, dest, edits);
            }
            CaseMapImpl.internalToUpper(caseLocale, options, src, dest, edits);
            return dest;
        }
        catch (IOException e) {
            throw new ICUUncheckedIOException(e);
        }
    }

    public static String toTitle(int caseLocale, int options, BreakIterator iter, CharSequence src) {
        if (src.length() <= 100 && (options & 0x4000) == 0) {
            if (src.length() == 0) {
                return src.toString();
            }
            Edits edits = new Edits();
            StringBuilder replacementChars = CaseMapImpl.toTitle(caseLocale, options | 0x4000, iter, src, new StringBuilder(), edits);
            return CaseMapImpl.applyEdits(src, replacementChars, edits);
        }
        return CaseMapImpl.toTitle(caseLocale, options, iter, src, new StringBuilder(src.length()), null).toString();
    }

    public static <A extends Appendable> A toTitle(int caseLocale, int options, BreakIterator titleIter, CharSequence src, A dest, Edits edits) {
        try {
            if (edits != null) {
                edits.reset();
            }
            StringContextIterator iter = new StringContextIterator(src);
            int srcLength = src.length();
            int prev = 0;
            boolean isFirstIndex = true;
            while (prev < srcLength) {
                int index;
                if (isFirstIndex) {
                    isFirstIndex = false;
                    index = titleIter.first();
                } else {
                    index = titleIter.next();
                }
                if (index == -1 || index > srcLength) {
                    index = srcLength;
                }
                if (prev < index) {
                    int titleStart = prev;
                    iter.setLimit(index);
                    int c = iter.nextCaseMapCP();
                    if ((options & 0x200) == 0) {
                        boolean toCased;
                        boolean bl = toCased = (options & 0x400) != 0;
                        while ((toCased ? 0 == UCaseProps.INSTANCE.getType(c) : !CaseMapImpl.isLNS(c)) && (c = iter.nextCaseMapCP()) >= 0) {
                        }
                        titleStart = iter.getCPStart();
                        if (prev < titleStart) {
                            CaseMapImpl.appendUnchanged(src, prev, titleStart - prev, dest, options, edits);
                        }
                    }
                    if (titleStart < index) {
                        int titleLimit;
                        c = UCaseProps.INSTANCE.toFullTitle(c, iter, dest, caseLocale);
                        CaseMapImpl.appendResult(c, dest, iter.getCPLength(), options, edits);
                        if (titleStart + 1 < index && caseLocale == 5) {
                            if (c < 0) {
                                c ^= 0xFFFFFFFF;
                            }
                            if (c == 73 || c == 205) {
                                titleLimit = CaseMapImpl.maybeTitleDutchIJ(src, c, titleStart + 1, index, dest, options, edits);
                                iter.moveTo(titleLimit);
                            } else {
                                titleLimit = iter.getCPLimit();
                            }
                        } else {
                            titleLimit = iter.getCPLimit();
                        }
                        if (titleLimit < index) {
                            if ((options & 0x100) == 0) {
                                CaseMapImpl.internalToLower(caseLocale, options, src, titleLimit, index, iter, dest, edits);
                            } else {
                                CaseMapImpl.appendUnchanged(src, titleLimit, index - titleLimit, dest, options, edits);
                            }
                            iter.moveToLimit();
                        }
                    }
                }
                prev = index;
            }
            return dest;
        }
        catch (IOException e) {
            throw new ICUUncheckedIOException(e);
        }
    }

    private static <A extends Appendable> int maybeTitleDutchIJ(CharSequence src, int c, int start, int segmentLimit, A dest, int options, Edits edits) throws IOException {
        int cp;
        int bit;
        assert (start < segmentLimit);
        int index = start;
        boolean withAcute = false;
        int unchanged1 = 0;
        boolean doTitleJ = false;
        int unchanged2 = 0;
        char c2 = src.charAt(index++);
        if (c == 73) {
            if (c2 == '\u0301') {
                withAcute = true;
                unchanged1 = 1;
                if (index == segmentLimit) {
                    return start;
                }
                c2 = src.charAt(index++);
            }
        } else {
            withAcute = true;
        }
        if (c2 == 'j') {
            doTitleJ = true;
        } else if (c2 == 'J') {
            ++unchanged1;
        } else {
            return start;
        }
        if (withAcute) {
            if (index == segmentLimit || src.charAt(index++) != '\u0301') {
                return start;
            }
            if (doTitleJ) {
                unchanged2 = 1;
            } else {
                ++unchanged1;
            }
        }
        if (index < segmentLimit && ((bit = 1 << UCharacter.getType(cp = Character.codePointAt(src, index))) & 0x1C0) != 0) {
            return start;
        }
        CaseMapImpl.appendUnchanged(src, start, unchanged1, dest, options, edits);
        start += unchanged1;
        if (doTitleJ) {
            dest.append('J');
            if (edits != null) {
                edits.addReplace(1, 1);
            }
            ++start;
        }
        CaseMapImpl.appendUnchanged(src, start, unchanged2, dest, options, edits);
        assert (start + unchanged2 == index);
        return index;
    }

    public static String fold(int options, CharSequence src) {
        if (src.length() <= 100 && (options & 0x4000) == 0) {
            if (src.length() == 0) {
                return src.toString();
            }
            Edits edits = new Edits();
            StringBuilder replacementChars = CaseMapImpl.fold(options | 0x4000, src, new StringBuilder(), edits);
            return CaseMapImpl.applyEdits(src, replacementChars, edits);
        }
        return CaseMapImpl.fold(options, src, new StringBuilder(src.length()), null).toString();
    }

    public static <A extends Appendable> A fold(int options, CharSequence src, A dest, Edits edits) {
        try {
            if (edits != null) {
                edits.reset();
            }
            CaseMapImpl.internalToLower(-1, options, src, 0, src.length(), null, dest, edits);
            return dest;
        }
        catch (IOException e) {
            throw new ICUUncheckedIOException(e);
        }
    }

    private static final class GreekUpper {
        private static final int UPPER_MASK = 1023;
        private static final int HAS_VOWEL = 4096;
        private static final int HAS_YPOGEGRAMMENI = 8192;
        private static final int HAS_ACCENT = 16384;
        private static final int HAS_DIALYTIKA = 32768;
        private static final int HAS_COMBINING_DIALYTIKA = 65536;
        private static final int HAS_OTHER_GREEK_DIACRITIC = 131072;
        private static final int HAS_VOWEL_AND_ACCENT = 20480;
        private static final int HAS_VOWEL_AND_ACCENT_AND_DIALYTIKA = 53248;
        private static final int HAS_EITHER_DIALYTIKA = 98304;
        private static final int AFTER_CASED = 1;
        private static final int AFTER_VOWEL_WITH_ACCENT = 2;
        private static final char[] data0370 = new char[]{'\u0370', '\u0370', '\u0372', '\u0372', '\u0000', '\u0000', '\u0376', '\u0376', '\u0000', '\u0000', '\u037a', '\u03fd', '\u03fe', '\u03ff', '\u0000', '\u037f', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u5391', '\u0000', '\u5395', '\u5397', '\u5399', '\u0000', '\u539f', '\u0000', '\u53a5', '\u53a9', '\ud399', '\u1391', '\u0392', '\u0393', '\u0394', '\u1395', '\u0396', '\u1397', '\u0398', '\u1399', '\u039a', '\u039b', '\u039c', '\u039d', '\u039e', '\u139f', '\u03a0', '\u03a1', '\u0000', '\u03a3', '\u03a4', '\u13a5', '\u03a6', '\u03a7', '\u03a8', '\u13a9', '\u9399', '\u93a5', '\u5391', '\u5395', '\u5397', '\u5399', '\ud3a5', '\u1391', '\u0392', '\u0393', '\u0394', '\u1395', '\u0396', '\u1397', '\u0398', '\u1399', '\u039a', '\u039b', '\u039c', '\u039d', '\u039e', '\u139f', '\u03a0', '\u03a1', '\u03a3', '\u03a3', '\u03a4', '\u13a5', '\u03a6', '\u03a7', '\u03a8', '\u13a9', '\u9399', '\u93a5', '\u539f', '\u53a5', '\u53a9', '\u03cf', '\u0392', '\u0398', '\u03d2', '\u43d2', '\u83d2', '\u03a6', '\u03a0', '\u03cf', '\u03d8', '\u03d8', '\u03da', '\u03da', '\u03dc', '\u03dc', '\u03de', '\u03de', '\u03e0', '\u03e0', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u039a', '\u03a1', '\u03f9', '\u037f', '\u03f4', '\u1395', '\u0000', '\u03f7', '\u03f7', '\u03f9', '\u03fa', '\u03fa', '\u03fc', '\u03fd', '\u03fe', '\u03ff'};
        private static final char[] data1F00 = new char[]{'\u1391', '\u1391', '\u5391', '\u5391', '\u5391', '\u5391', '\u5391', '\u5391', '\u1391', '\u1391', '\u5391', '\u5391', '\u5391', '\u5391', '\u5391', '\u5391', '\u1395', '\u1395', '\u5395', '\u5395', '\u5395', '\u5395', '\u0000', '\u0000', '\u1395', '\u1395', '\u5395', '\u5395', '\u5395', '\u5395', '\u0000', '\u0000', '\u1397', '\u1397', '\u5397', '\u5397', '\u5397', '\u5397', '\u5397', '\u5397', '\u1397', '\u1397', '\u5397', '\u5397', '\u5397', '\u5397', '\u5397', '\u5397', '\u1399', '\u1399', '\u5399', '\u5399', '\u5399', '\u5399', '\u5399', '\u5399', '\u1399', '\u1399', '\u5399', '\u5399', '\u5399', '\u5399', '\u5399', '\u5399', '\u139f', '\u139f', '\u539f', '\u539f', '\u539f', '\u539f', '\u0000', '\u0000', '\u139f', '\u139f', '\u539f', '\u539f', '\u539f', '\u539f', '\u0000', '\u0000', '\u13a5', '\u13a5', '\u53a5', '\u53a5', '\u53a5', '\u53a5', '\u53a5', '\u53a5', '\u0000', '\u13a5', '\u0000', '\u53a5', '\u0000', '\u53a5', '\u0000', '\u53a5', '\u13a9', '\u13a9', '\u53a9', '\u53a9', '\u53a9', '\u53a9', '\u53a9', '\u53a9', '\u13a9', '\u13a9', '\u53a9', '\u53a9', '\u53a9', '\u53a9', '\u53a9', '\u53a9', '\u5391', '\u5391', '\u5395', '\u5395', '\u5397', '\u5397', '\u5399', '\u5399', '\u539f', '\u539f', '\u53a5', '\u53a5', '\u53a9', '\u53a9', '\u0000', '\u0000', '\u3391', '\u3391', '\u7391', '\u7391', '\u7391', '\u7391', '\u7391', '\u7391', '\u3391', '\u3391', '\u7391', '\u7391', '\u7391', '\u7391', '\u7391', '\u7391', '\u3397', '\u3397', '\u7397', '\u7397', '\u7397', '\u7397', '\u7397', '\u7397', '\u3397', '\u3397', '\u7397', '\u7397', '\u7397', '\u7397', '\u7397', '\u7397', '\u33a9', '\u33a9', '\u73a9', '\u73a9', '\u73a9', '\u73a9', '\u73a9', '\u73a9', '\u33a9', '\u33a9', '\u73a9', '\u73a9', '\u73a9', '\u73a9', '\u73a9', '\u73a9', '\u1391', '\u1391', '\u7391', '\u3391', '\u7391', '\u0000', '\u5391', '\u7391', '\u1391', '\u1391', '\u5391', '\u5391', '\u3391', '\u0000', '\u1399', '\u0000', '\u0000', '\u0000', '\u7397', '\u3397', '\u7397', '\u0000', '\u5397', '\u7397', '\u5395', '\u5395', '\u5397', '\u5397', '\u3397', '\u0000', '\u0000', '\u0000', '\u1399', '\u1399', '\ud399', '\ud399', '\u0000', '\u0000', '\u5399', '\ud399', '\u1399', '\u1399', '\u5399', '\u5399', '\u0000', '\u0000', '\u0000', '\u0000', '\u13a5', '\u13a5', '\ud3a5', '\ud3a5', '\u03a1', '\u03a1', '\u53a5', '\ud3a5', '\u13a5', '\u13a5', '\u53a5', '\u53a5', '\u03a1', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u73a9', '\u33a9', '\u73a9', '\u0000', '\u53a9', '\u73a9', '\u539f', '\u539f', '\u53a9', '\u53a9', '\u33a9', '\u0000', '\u0000', '\u0000'};
        private static final char data2126 = '\u13a9';

        private GreekUpper() {
        }

        private static final int getLetterData(int c) {
            if (c < 880 || 8486 < c || 1023 < c && c < 7936) {
                return 0;
            }
            if (c <= 1023) {
                return data0370[c - 880];
            }
            if (c <= 8191) {
                return data1F00[c - 7936];
            }
            if (c == 8486) {
                return 5033;
            }
            return 0;
        }

        private static final int getDiacriticData(int c) {
            switch (c) {
                case 768: 
                case 769: 
                case 770: 
                case 771: 
                case 785: 
                case 834: {
                    return 16384;
                }
                case 776: {
                    return 65536;
                }
                case 836: {
                    return 81920;
                }
                case 837: {
                    return 8192;
                }
                case 772: 
                case 774: 
                case 787: 
                case 788: 
                case 835: {
                    return 131072;
                }
            }
            return 0;
        }

        private static boolean isFollowedByCasedLetter(CharSequence s, int i) {
            while (i < s.length()) {
                int c = Character.codePointAt(s, i);
                int type = UCaseProps.INSTANCE.getTypeOrIgnorable(c);
                if ((type & 4) != 0) {
                    i += Character.charCount(c);
                    continue;
                }
                return type != 0;
            }
            return false;
        }

        private static <A extends Appendable> A toUpper(int options, CharSequence src, A dest, Edits edits) throws IOException {
            int state = 0;
            int i = 0;
            while (i < src.length()) {
                int c = Character.codePointAt(src, i);
                int nextIndex = i + Character.charCount(c);
                int nextState = 0;
                int type = UCaseProps.INSTANCE.getTypeOrIgnorable(c);
                if ((type & 4) != 0) {
                    nextState |= state & true;
                } else if (type != 0) {
                    nextState |= 1;
                }
                int data = GreekUpper.getLetterData(c);
                if (data > 0) {
                    boolean change;
                    int diacriticData;
                    int upper = data & 0x3FF;
                    if ((data & 0x1000) != 0 && (state & 2) != 0 && (upper == 921 || upper == 933)) {
                        data |= 0x8000;
                    }
                    int numYpogegrammeni = 0;
                    if ((data & 0x2000) != 0) {
                        numYpogegrammeni = 1;
                    }
                    while (nextIndex < src.length() && (diacriticData = GreekUpper.getDiacriticData(src.charAt(nextIndex))) != 0) {
                        data |= diacriticData;
                        if ((diacriticData & 0x2000) != 0) {
                            ++numYpogegrammeni;
                        }
                        ++nextIndex;
                    }
                    if ((data & 0xD000) == 20480) {
                        nextState |= 2;
                    }
                    boolean addTonos = false;
                    if (upper == 919 && (data & 0x4000) != 0 && numYpogegrammeni == 0 && (state & 1) == 0 && !GreekUpper.isFollowedByCasedLetter(src, nextIndex)) {
                        if (i == nextIndex) {
                            upper = 905;
                        } else {
                            addTonos = true;
                        }
                    } else if ((data & 0x8000) != 0) {
                        if (upper == 921) {
                            upper = 938;
                            data &= 0xFFFE7FFF;
                        } else if (upper == 933) {
                            upper = 939;
                            data &= 0xFFFE7FFF;
                        }
                    }
                    if (edits == null && (options & 0x4000) == 0) {
                        change = true;
                    } else {
                        int newLength;
                        int oldLength;
                        change = src.charAt(i) != upper || numYpogegrammeni > 0;
                        int i2 = i + 1;
                        if ((data & 0x18000) != 0) {
                            change |= i2 >= nextIndex || src.charAt(i2) != '\u0308';
                            ++i2;
                        }
                        if (addTonos) {
                            change |= i2 >= nextIndex || src.charAt(i2) != '\u0301';
                            ++i2;
                        }
                        if (change |= (oldLength = nextIndex - i) != (newLength = i2 - i + numYpogegrammeni)) {
                            if (edits != null) {
                                edits.addReplace(oldLength, newLength);
                            }
                        } else {
                            if (edits != null) {
                                edits.addUnchanged(oldLength);
                            }
                            boolean bl = change = (options & 0x4000) == 0;
                        }
                    }
                    if (change) {
                        dest.append((char)upper);
                        if ((data & 0x18000) != 0) {
                            dest.append('\u0308');
                        }
                        if (addTonos) {
                            dest.append('\u0301');
                        }
                        while (numYpogegrammeni > 0) {
                            dest.append('\u0399');
                            --numYpogegrammeni;
                        }
                    }
                } else {
                    c = UCaseProps.INSTANCE.toFullUpper(c, null, dest, 4);
                    CaseMapImpl.appendResult(c, dest, nextIndex - i, options, edits);
                }
                i = nextIndex;
                state = nextState;
            }
            return dest;
        }
    }

    private static final class WholeStringBreakIterator
    extends BreakIterator {
        private int length;

        private WholeStringBreakIterator() {
        }

        private static void notImplemented() {
            throw new UnsupportedOperationException("should not occur");
        }

        @Override
        public int first() {
            return 0;
        }

        @Override
        public int last() {
            WholeStringBreakIterator.notImplemented();
            return 0;
        }

        @Override
        public int next(int n) {
            WholeStringBreakIterator.notImplemented();
            return 0;
        }

        @Override
        public int next() {
            return this.length;
        }

        @Override
        public int previous() {
            WholeStringBreakIterator.notImplemented();
            return 0;
        }

        @Override
        public int following(int offset) {
            WholeStringBreakIterator.notImplemented();
            return 0;
        }

        @Override
        public int current() {
            WholeStringBreakIterator.notImplemented();
            return 0;
        }

        @Override
        public CharacterIterator getText() {
            WholeStringBreakIterator.notImplemented();
            return null;
        }

        @Override
        public void setText(CharacterIterator newText) {
            this.length = newText.getEndIndex();
        }

        @Override
        public void setText(CharSequence newText) {
            this.length = newText.length();
        }

        @Override
        public void setText(String newText) {
            this.length = newText.length();
        }
    }

    public static final class StringContextIterator
    implements UCaseProps.ContextIterator {
        protected CharSequence s;
        protected int index;
        protected int limit;
        protected int cpStart;
        protected int cpLimit;
        protected int dir;

        public StringContextIterator(CharSequence src) {
            this.s = src;
            this.limit = src.length();
            this.index = 0;
            this.cpLimit = 0;
            this.cpStart = 0;
            this.dir = 0;
        }

        public StringContextIterator(CharSequence src, int cpStart, int cpLimit) {
            this.s = src;
            this.index = 0;
            this.limit = src.length();
            this.cpStart = cpStart;
            this.cpLimit = cpLimit;
            this.dir = 0;
        }

        public void setLimit(int lim) {
            this.limit = 0 <= lim && lim <= this.s.length() ? lim : this.s.length();
        }

        public void moveToLimit() {
            this.cpStart = this.cpLimit = this.limit;
        }

        public void moveTo(int i) {
            this.cpStart = this.cpLimit = i;
        }

        public int nextCaseMapCP() {
            this.cpStart = this.cpLimit;
            if (this.cpLimit < this.limit) {
                int c = Character.codePointAt(this.s, this.cpLimit);
                this.cpLimit += Character.charCount(c);
                return c;
            }
            return -1;
        }

        public void setCPStartAndLimit(int s, int l) {
            this.cpStart = s;
            this.cpLimit = l;
            this.dir = 0;
        }

        public int getCPStart() {
            return this.cpStart;
        }

        public int getCPLimit() {
            return this.cpLimit;
        }

        public int getCPLength() {
            return this.cpLimit - this.cpStart;
        }

        @Override
        public void reset(int direction) {
            if (direction > 0) {
                this.dir = 1;
                this.index = this.cpLimit;
            } else if (direction < 0) {
                this.dir = -1;
                this.index = this.cpStart;
            } else {
                this.dir = 0;
                this.index = 0;
            }
        }

        @Override
        public int next() {
            if (this.dir > 0 && this.index < this.s.length()) {
                int c = Character.codePointAt(this.s, this.index);
                this.index += Character.charCount(c);
                return c;
            }
            if (this.dir < 0 && this.index > 0) {
                int c = Character.codePointBefore(this.s, this.index);
                this.index -= Character.charCount(c);
                return c;
            }
            return -1;
        }
    }
}

