/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.BaseCharFilter;
import com.atlassian.lucene36.analysis.CharReader;
import com.atlassian.lucene36.analysis.CharStream;
import com.atlassian.lucene36.analysis.NormalizeCharMap;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;

public class MappingCharFilter
extends BaseCharFilter {
    private final NormalizeCharMap normMap;
    private LinkedList<Character> buffer;
    private String replacement;
    private int charPointer;
    private int nextCharCounter;

    public MappingCharFilter(NormalizeCharMap normMap, CharStream in) {
        super(in);
        this.normMap = normMap;
    }

    public MappingCharFilter(NormalizeCharMap normMap, Reader in) {
        super(CharReader.get(in));
        this.normMap = normMap;
    }

    public int read() throws IOException {
        block0: while (this.replacement == null || this.charPointer >= this.replacement.length()) {
            NormalizeCharMap nm;
            int firstChar = this.nextChar();
            if (firstChar == -1) {
                return -1;
            }
            NormalizeCharMap normalizeCharMap = nm = this.normMap.submap != null ? this.normMap.submap.get(Character.valueOf((char)firstChar)) : null;
            if (nm == null) {
                return firstChar;
            }
            NormalizeCharMap result = this.match(nm);
            if (result == null) {
                return firstChar;
            }
            this.replacement = result.normStr;
            this.charPointer = 0;
            if (result.diff == 0) continue;
            int prevCumulativeDiff = this.getLastCumulativeDiff();
            if (result.diff < 0) {
                int i = 0;
                while (true) {
                    if (i >= -result.diff) continue block0;
                    this.addOffCorrectMap(this.nextCharCounter + i - prevCumulativeDiff, prevCumulativeDiff - 1 - i);
                    ++i;
                }
            }
            this.addOffCorrectMap(this.nextCharCounter - result.diff - prevCumulativeDiff, prevCumulativeDiff + result.diff);
        }
        return this.replacement.charAt(this.charPointer++);
    }

    private int nextChar() throws IOException {
        if (this.buffer != null && !this.buffer.isEmpty()) {
            ++this.nextCharCounter;
            return this.buffer.removeFirst().charValue();
        }
        int nextChar = this.input.read();
        if (nextChar != -1) {
            ++this.nextCharCounter;
        }
        return nextChar;
    }

    private void pushChar(int c) {
        --this.nextCharCounter;
        if (this.buffer == null) {
            this.buffer = new LinkedList();
        }
        this.buffer.addFirst(Character.valueOf((char)c));
    }

    private void pushLastChar(int c) {
        if (this.buffer == null) {
            this.buffer = new LinkedList();
        }
        this.buffer.addLast(Character.valueOf((char)c));
    }

    private NormalizeCharMap match(NormalizeCharMap map) throws IOException {
        int chr;
        NormalizeCharMap result = null;
        if (map.submap != null && (chr = this.nextChar()) != -1) {
            NormalizeCharMap subMap = map.submap.get(Character.valueOf((char)chr));
            if (subMap != null) {
                result = this.match(subMap);
            }
            if (result == null) {
                this.pushChar(chr);
            }
        }
        if (result == null && map.normStr != null) {
            result = map;
        }
        return result;
    }

    public int read(char[] cbuf, int off, int len) throws IOException {
        int c;
        int i;
        char[] tmp = new char[len];
        int l = this.input.read(tmp, 0, len);
        if (l != -1) {
            for (i = 0; i < l; ++i) {
                this.pushLastChar(tmp[i]);
            }
        }
        l = 0;
        for (i = off; i < off + len && (c = this.read()) != -1; ++i) {
            cbuf[i] = (char)c;
            ++l;
        }
        return l == 0 ? -1 : l;
    }
}

