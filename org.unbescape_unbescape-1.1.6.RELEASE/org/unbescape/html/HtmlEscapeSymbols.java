/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.html;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.unbescape.html.Html4EscapeSymbolsInitializer;
import org.unbescape.html.Html5EscapeSymbolsInitializer;

final class HtmlEscapeSymbols {
    static final int NCRS_BY_CODEPOINT_LEN = 12287;
    final short[] NCRS_BY_CODEPOINT = new short[12287];
    final Map<Integer, Short> NCRS_BY_CODEPOINT_OVERFLOW;
    static final char MAX_ASCII_CHAR = '\u007f';
    final byte[] ESCAPE_LEVELS = new byte[129];
    final char[][] SORTED_NCRS;
    final int[] SORTED_CODEPOINTS;
    final int[][] DOUBLE_CODEPOINTS;
    static final short NO_NCR = 0;
    static final HtmlEscapeSymbols HTML4_SYMBOLS = Html4EscapeSymbolsInitializer.initializeHtml4();
    static final HtmlEscapeSymbols HTML5_SYMBOLS = Html5EscapeSymbolsInitializer.initializeHtml5();

    HtmlEscapeSymbols(References references, byte[] escapeLevels) {
        short i;
        System.arraycopy(escapeLevels, 0, this.ESCAPE_LEVELS, 0, 129);
        ArrayList<char[]> ncrs = new ArrayList<char[]>(references.references.size() + 5);
        ArrayList<Integer> codepoints = new ArrayList<Integer>(references.references.size() + 5);
        ArrayList<int[]> doubleCodepoints = new ArrayList<int[]>(100);
        HashMap<Integer, Short> ncrsByCodepointOverflow = new HashMap<Integer, Short>(20);
        for (Reference reference : references.references) {
            char[] referenceNcr = reference.ncr;
            int[] referenceCodepoints = reference.codepoints;
            ncrs.add(referenceNcr);
            if (referenceCodepoints.length == 1) {
                int referenceCodepoint = referenceCodepoints[0];
                codepoints.add(referenceCodepoint);
                continue;
            }
            if (referenceCodepoints.length == 2) {
                doubleCodepoints.add(referenceCodepoints);
                codepoints.add(-1 * doubleCodepoints.size());
                continue;
            }
            throw new RuntimeException("Unsupported codepoints #: " + referenceCodepoints.length + " for " + new String(referenceNcr));
        }
        Arrays.fill(this.NCRS_BY_CODEPOINT, (short)0);
        this.SORTED_NCRS = new char[ncrs.size()][];
        this.SORTED_CODEPOINTS = new int[codepoints.size()];
        ArrayList ncrsOrdered = new ArrayList(ncrs);
        Collections.sort(ncrsOrdered, new Comparator<char[]>(){

            @Override
            public int compare(char[] o1, char[] o2) {
                return HtmlEscapeSymbols.compare(o1, o2, 0, o2.length);
            }
        });
        block1: for (i = 0; i < this.SORTED_NCRS.length; i = (short)((short)(i + 1))) {
            char[] ncr = (char[])ncrsOrdered.get(i);
            this.SORTED_NCRS[i] = ncr;
            for (int j = 0; j < this.SORTED_NCRS.length; j = (int)((short)(j + 1))) {
                int cp;
                if (!Arrays.equals(ncr, (char[])ncrs.get(j))) continue;
                this.SORTED_CODEPOINTS[i] = cp = ((Integer)codepoints.get(j)).intValue();
                if (cp <= 0) continue block1;
                if (cp < 12287) {
                    if (this.NCRS_BY_CODEPOINT[cp] == 0) {
                        this.NCRS_BY_CODEPOINT[cp] = i;
                        continue block1;
                    }
                    int positionOfCurrent = HtmlEscapeSymbols.positionInList(ncrs, this.SORTED_NCRS[this.NCRS_BY_CODEPOINT[cp]]);
                    int positionOfNew = HtmlEscapeSymbols.positionInList(ncrs, ncr);
                    if (positionOfNew >= positionOfCurrent) continue block1;
                    this.NCRS_BY_CODEPOINT[cp] = i;
                    continue block1;
                }
                ncrsByCodepointOverflow.put(cp, i);
                continue block1;
            }
        }
        this.NCRS_BY_CODEPOINT_OVERFLOW = ncrsByCodepointOverflow.size() > 0 ? ncrsByCodepointOverflow : null;
        if (doubleCodepoints.size() > 0) {
            this.DOUBLE_CODEPOINTS = new int[doubleCodepoints.size()][];
            for (i = 0; i < this.DOUBLE_CODEPOINTS.length; ++i) {
                this.DOUBLE_CODEPOINTS[i] = (int[])doubleCodepoints.get(i);
            }
        } else {
            this.DOUBLE_CODEPOINTS = null;
        }
    }

    private static int positionInList(List<char[]> list, char[] element) {
        int i = 0;
        for (char[] e : list) {
            if (Arrays.equals(e, element)) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    private static int compare(char[] ncr, String text, int start, int end) {
        int i;
        int textLen = end - start;
        int maxCommon = Math.min(ncr.length, textLen);
        for (i = 1; i < maxCommon; ++i) {
            char tc = text.charAt(start + i);
            if (ncr[i] < tc) {
                if (tc == ';') {
                    return 1;
                }
                return -1;
            }
            if (ncr[i] <= tc) continue;
            if (ncr[i] == ';') {
                return -1;
            }
            return 1;
        }
        if (ncr.length > i) {
            if (ncr[i] == ';') {
                return -1;
            }
            return 1;
        }
        if (textLen > i) {
            if (text.charAt(start + i) == ';') {
                return 1;
            }
            return -(textLen - i + 10);
        }
        return 0;
    }

    private static int compare(char[] ncr, char[] text, int start, int end) {
        int i;
        int textLen = end - start;
        int maxCommon = Math.min(ncr.length, textLen);
        for (i = 1; i < maxCommon; ++i) {
            char tc = text[start + i];
            if (ncr[i] < tc) {
                if (tc == ';') {
                    return 1;
                }
                return -1;
            }
            if (ncr[i] <= tc) continue;
            if (ncr[i] == ';') {
                return -1;
            }
            return 1;
        }
        if (ncr.length > i) {
            if (ncr[i] == ';') {
                return -1;
            }
            return 1;
        }
        if (textLen > i) {
            if (text[start + i] == ';') {
                return 1;
            }
            return -(textLen - i + 10);
        }
        return 0;
    }

    static int binarySearch(char[][] values, String text, int start, int end) {
        int low = 0;
        int high = values.length - 1;
        int partialIndex = Integer.MIN_VALUE;
        int partialValue = Integer.MIN_VALUE;
        while (low <= high) {
            int mid = low + high >>> 1;
            char[] midVal = values[mid];
            int cmp = HtmlEscapeSymbols.compare(midVal, text, start, end);
            if (cmp == -1) {
                low = mid + 1;
                continue;
            }
            if (cmp == 1) {
                high = mid - 1;
                continue;
            }
            if (cmp < -10) {
                low = mid + 1;
                if (partialIndex != Integer.MIN_VALUE && partialValue >= cmp) continue;
                partialIndex = mid;
                partialValue = cmp;
                continue;
            }
            return mid;
        }
        if (partialIndex != Integer.MIN_VALUE) {
            return -1 * (partialIndex + 10);
        }
        return Integer.MIN_VALUE;
    }

    static int binarySearch(char[][] values, char[] text, int start, int end) {
        int low = 0;
        int high = values.length - 1;
        int partialIndex = Integer.MIN_VALUE;
        int partialValue = Integer.MIN_VALUE;
        while (low <= high) {
            int mid = low + high >>> 1;
            char[] midVal = values[mid];
            int cmp = HtmlEscapeSymbols.compare(midVal, text, start, end);
            if (cmp == -1) {
                low = mid + 1;
                continue;
            }
            if (cmp == 1) {
                high = mid - 1;
                continue;
            }
            if (cmp < -10) {
                low = mid + 1;
                if (partialIndex != Integer.MIN_VALUE && partialValue >= cmp) continue;
                partialIndex = mid;
                partialValue = cmp;
                continue;
            }
            return mid;
        }
        if (partialIndex != Integer.MIN_VALUE) {
            return -1 * (partialIndex + 10);
        }
        return Integer.MIN_VALUE;
    }

    private static final class Reference {
        private final char[] ncr;
        private final int[] codepoints;

        private Reference(String ncr, int[] codepoints) {
            this.ncr = ncr.toCharArray();
            this.codepoints = codepoints;
        }
    }

    static final class References {
        private final List<Reference> references = new ArrayList<Reference>(200);

        References() {
        }

        void addReference(int codepoint, String ncr) {
            this.references.add(new Reference(ncr, new int[]{codepoint}));
        }

        void addReference(int codepoint0, int codepoint1, String ncr) {
            this.references.add(new Reference(ncr, new int[]{codepoint0, codepoint1}));
        }
    }
}

