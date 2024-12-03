/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.core.compiler;

import java.util.Arrays;
import java.util.BitSet;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;

class SubwordMatcher {
    private static final int[] EMPTY_REGIONS = new int[0];
    private final char[] name;
    private final BitSet wordBoundaries;

    public SubwordMatcher(String name) {
        this.name = name.toCharArray();
        this.wordBoundaries = new BitSet(name.length());
        int i = 0;
        while (i < this.name.length) {
            if (SubwordMatcher.isWordBoundary(this.caseAt(i - 1), this.caseAt(i), this.caseAt(i + 1))) {
                this.wordBoundaries.set(i);
            }
            ++i;
        }
    }

    private Case caseAt(int index) {
        if (index < 0 || index >= this.name.length) {
            return Case.SEPARATOR;
        }
        char c = this.name[index];
        if (c == '_') {
            return Case.SEPARATOR;
        }
        if (ScannerHelper.isUpperCase(c)) {
            return Case.UPPER;
        }
        return Case.LOWER;
    }

    private static boolean isWordBoundary(Case p, Case c, Case n) {
        if (p == c && c == n) {
            return false;
        }
        if (p == Case.SEPARATOR) {
            return true;
        }
        return c == Case.UPPER && (p == Case.LOWER || n == Case.LOWER);
    }

    public int[] getMatchingRegions(String pattern) {
        int segmentStart = 0;
        int[] segments = EMPTY_REGIONS;
        int iName = -1;
        int iPatternWordStart = 0;
        int iPattern = 0;
        while (iPattern < pattern.length()) {
            char nameChar;
            if (++iName == this.name.length) {
                return null;
            }
            char patternChar = pattern.charAt(iPattern);
            if (patternChar != (nameChar = this.name[iName]) && (this.isWordBoundary(iName) || !this.equalsIgnoreCase(patternChar, nameChar))) {
                int next;
                int wordStart;
                if (iName > segmentStart) {
                    segments = Arrays.copyOf(segments, segments.length + 2);
                    segments[segments.length - 2] = segmentStart;
                    segments[segments.length - 1] = iName - segmentStart;
                }
                if ((wordStart = this.indexOfWordStart(iName, patternChar)) < 0 && (next = this.indexOfWordStart(iName, pattern.charAt(iPatternWordStart))) > 0) {
                    wordStart = next;
                    iPattern = iPatternWordStart;
                    segments = Arrays.copyOfRange(segments, 0, segments.length - 2);
                }
                if (wordStart < 0) {
                    return null;
                }
                segmentStart = wordStart;
                iName = wordStart;
                iPatternWordStart = iPattern;
            }
            ++iPattern;
        }
        segments = Arrays.copyOf(segments, segments.length + 2);
        segments[segments.length - 2] = segmentStart;
        segments[segments.length - 1] = iName - segmentStart + 1;
        return segments;
    }

    private int indexOfWordStart(int nameStart, char patternChar) {
        int iName = nameStart;
        while (iName < this.name.length) {
            char nameChar = this.name[iName];
            if (this.isWordBoundary(iName) && this.equalsIgnoreCase(nameChar, patternChar)) {
                return iName;
            }
            if (!ScannerHelper.isJavaIdentifierPart(nameChar)) {
                return -1;
            }
            ++iName;
        }
        return -1;
    }

    private boolean equalsIgnoreCase(char a, char b) {
        return ScannerHelper.toLowerCase(a) == ScannerHelper.toLowerCase(b);
    }

    private boolean isWordBoundary(int iName) {
        return this.wordBoundaries.get(iName);
    }

    private static enum Case {
        SEPARATOR,
        LOWER,
        UPPER;

    }
}

