/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.query.impl.getters;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class JsonPathCursor {
    private static final Charset UTF8_CHARSET = Charset.forName("UTF8");
    private static final int DEFAULT_PATH_ELEMENT_COUNT = 5;
    private List<Triple> triples;
    private String attributePath;
    private String current;
    private byte[] currentAsUtf8;
    private int currentArrayIndex = -1;
    private boolean isArray;
    private boolean isAny;
    private int cursor;

    private JsonPathCursor(String originalPath, List<Triple> triples) {
        this.attributePath = originalPath;
        this.triples = triples;
    }

    JsonPathCursor(JsonPathCursor other) {
        this.attributePath = other.attributePath;
        this.triples = other.triples;
    }

    public static JsonPathCursor createCursor(String attributePath) {
        ArrayList<Triple> triples = new ArrayList<Triple>(5);
        int start = 0;
        while (start < attributePath.length()) {
            char c;
            int end;
            boolean isArray = false;
            try {
                while (attributePath.charAt(start) == '[' || attributePath.charAt(start) == '.') {
                    ++start;
                }
            }
            catch (IndexOutOfBoundsException e) {
                throw JsonPathCursor.createIllegalArgumentException(attributePath);
            }
            for (end = start + 1; end < attributePath.length() && '.' != (c = attributePath.charAt(end)) && '[' != c; ++end) {
                if (']' != c) continue;
                isArray = true;
                break;
            }
            String part = attributePath.substring(start, end);
            Triple triple = new Triple(part, part.getBytes(UTF8_CHARSET), isArray);
            triples.add(triple);
            start = end + 1;
        }
        return new JsonPathCursor(attributePath, triples);
    }

    private static IllegalArgumentException createIllegalArgumentException(String attributePath) {
        return new IllegalArgumentException("Malformed query path " + attributePath);
    }

    public String getAttributePath() {
        return this.attributePath;
    }

    public String getNext() {
        this.next();
        return this.current;
    }

    public String getCurrent() {
        return this.current;
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"}, justification="Making a copy reverses the benefit of this method")
    public byte[] getCurrentAsUTF8() {
        return this.currentAsUtf8;
    }

    public boolean isArray() {
        return this.isArray;
    }

    public boolean isAny() {
        return this.isAny;
    }

    public int getArrayIndex() {
        return this.currentArrayIndex;
    }

    public void reset() {
        this.current = null;
        this.currentArrayIndex = -1;
        this.isArray = false;
        this.isAny = false;
        this.cursor = 0;
    }

    public boolean hasNext() {
        return this.cursor < this.triples.size();
    }

    private void next() {
        if (this.cursor < this.triples.size()) {
            Triple triple = this.triples.get(this.cursor);
            this.current = triple.string;
            this.currentAsUtf8 = triple.stringAsUtf8;
            this.isArray = triple.isArray;
            this.currentArrayIndex = -1;
            this.isAny = false;
            if (this.isArray) {
                if ("any".equals(this.current)) {
                    this.isAny = true;
                } else {
                    this.isAny = false;
                    this.currentArrayIndex = Integer.parseInt(this.current);
                }
            }
            ++this.cursor;
        } else {
            this.current = null;
            this.currentAsUtf8 = null;
            this.currentArrayIndex = -1;
            this.isAny = false;
            this.isArray = false;
        }
    }

    private static final class Triple {
        private final String string;
        private final byte[] stringAsUtf8;
        private final boolean isArray;

        private Triple(String string, byte[] stringAsUtf8, boolean isArray) {
            this.string = string;
            this.stringAsUtf8 = stringAsUtf8;
            this.isArray = isArray;
        }
    }
}

