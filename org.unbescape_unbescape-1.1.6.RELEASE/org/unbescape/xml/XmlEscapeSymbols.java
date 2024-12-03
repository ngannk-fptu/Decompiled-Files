/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.unbescape.xml.Xml10EscapeSymbolsInitializer;
import org.unbescape.xml.Xml11EscapeSymbolsInitializer;
import org.unbescape.xml.XmlCodepointValidator;

final class XmlEscapeSymbols {
    static final XmlEscapeSymbols XML10_SYMBOLS = Xml10EscapeSymbolsInitializer.initializeXml10(false);
    static final XmlEscapeSymbols XML11_SYMBOLS = Xml11EscapeSymbolsInitializer.initializeXml11(false);
    static final XmlEscapeSymbols XML10_ATTRIBUTE_SYMBOLS = Xml10EscapeSymbolsInitializer.initializeXml10(true);
    static final XmlEscapeSymbols XML11_ATTRIBUTE_SYMBOLS = Xml11EscapeSymbolsInitializer.initializeXml11(true);
    static final char LEVELS_LEN = '\u00a1';
    final byte[] ESCAPE_LEVELS = new byte[161];
    final int[] SORTED_CODEPOINTS;
    final char[][] SORTED_CERS_BY_CODEPOINT;
    final char[][] SORTED_CERS;
    final int[] SORTED_CODEPOINTS_BY_CER;
    final XmlCodepointValidator CODEPOINT_VALIDATOR;

    XmlEscapeSymbols(References references, byte[] escapeLevels, XmlCodepointValidator codepointValidator) {
        int j;
        int i;
        this.CODEPOINT_VALIDATOR = codepointValidator;
        System.arraycopy(escapeLevels, 0, this.ESCAPE_LEVELS, 0, 161);
        int structureLen = references.references.size();
        ArrayList<char[]> cers = new ArrayList<char[]>(structureLen + 5);
        ArrayList<Integer> codepoints = new ArrayList<Integer>(structureLen + 5);
        for (Reference reference : references.references) {
            cers.add(reference.cer);
            codepoints.add(reference.codepoint);
        }
        this.SORTED_CODEPOINTS = new int[structureLen];
        this.SORTED_CERS_BY_CODEPOINT = new char[structureLen][];
        this.SORTED_CERS = new char[structureLen][];
        this.SORTED_CODEPOINTS_BY_CER = new int[structureLen];
        ArrayList cersOrdered = new ArrayList(cers);
        Collections.sort(cersOrdered, new Comparator<char[]>(){

            @Override
            public int compare(char[] o1, char[] o2) {
                return new String(o1).compareTo(new String(o2));
            }
        });
        ArrayList codepointsOrdered = new ArrayList(codepoints);
        Collections.sort(codepointsOrdered);
        block1: for (i = 0; i < structureLen; i = (int)((short)(i + 1))) {
            int codepoint;
            this.SORTED_CODEPOINTS[i] = codepoint = ((Integer)codepointsOrdered.get(i)).intValue();
            for (j = 0; j < structureLen; j = (int)((short)(j + 1))) {
                if (codepoint != (Integer)codepoints.get(j)) continue;
                this.SORTED_CERS_BY_CODEPOINT[i] = (char[])cers.get(j);
                continue block1;
            }
        }
        block3: for (i = 0; i < structureLen; i = (int)((short)(i + 1))) {
            char[] cer = (char[])cersOrdered.get(i);
            this.SORTED_CERS[i] = cer;
            for (j = 0; j < structureLen; j = (int)((short)(j + 1))) {
                if (!Arrays.equals(cer, (char[])cers.get(j))) continue;
                this.SORTED_CODEPOINTS_BY_CER[i] = (Integer)codepoints.get(j);
                continue block3;
            }
        }
    }

    private static int compare(char[] cer, String text, int start, int end) {
        int i;
        int textLen = end - start;
        int maxCommon = Math.min(cer.length, textLen);
        for (i = 1; i < maxCommon; ++i) {
            char tc = text.charAt(start + i);
            if (cer[i] < tc) {
                return -1;
            }
            if (cer[i] <= tc) continue;
            return 1;
        }
        if (cer.length > i) {
            return 1;
        }
        if (textLen > i) {
            return -1;
        }
        return 0;
    }

    private static int compare(char[] cer, char[] text, int start, int end) {
        int i;
        int textLen = end - start;
        int maxCommon = Math.min(cer.length, textLen);
        for (i = 1; i < maxCommon; ++i) {
            char tc = text[start + i];
            if (cer[i] < tc) {
                return -1;
            }
            if (cer[i] <= tc) continue;
            return 1;
        }
        if (cer.length > i) {
            return 1;
        }
        if (textLen > i) {
            return -1;
        }
        return 0;
    }

    static int binarySearch(char[][] values, String text, int start, int end) {
        int low = 0;
        int high = values.length - 1;
        while (low <= high) {
            int mid = low + high >>> 1;
            char[] midVal = values[mid];
            int cmp = XmlEscapeSymbols.compare(midVal, text, start, end);
            if (cmp == -1) {
                low = mid + 1;
                continue;
            }
            if (cmp == 1) {
                high = mid - 1;
                continue;
            }
            return mid;
        }
        return Integer.MIN_VALUE;
    }

    static int binarySearch(char[][] values, char[] text, int start, int end) {
        int low = 0;
        int high = values.length - 1;
        while (low <= high) {
            int mid = low + high >>> 1;
            char[] midVal = values[mid];
            int cmp = XmlEscapeSymbols.compare(midVal, text, start, end);
            if (cmp == -1) {
                low = mid + 1;
                continue;
            }
            if (cmp == 1) {
                high = mid - 1;
                continue;
            }
            return mid;
        }
        return Integer.MIN_VALUE;
    }

    private static final class Reference {
        private final char[] cer;
        private final int codepoint;

        private Reference(String cer, int codepoint) {
            this.cer = cer.toCharArray();
            this.codepoint = codepoint;
        }
    }

    static final class References {
        private final List<Reference> references = new ArrayList<Reference>(200);

        References() {
        }

        void addReference(int codepoint, String cer) {
            this.references.add(new Reference(cer, codepoint));
        }
    }
}

