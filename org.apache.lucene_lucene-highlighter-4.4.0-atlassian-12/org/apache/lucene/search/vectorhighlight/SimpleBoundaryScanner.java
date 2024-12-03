/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.vectorhighlight;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.search.vectorhighlight.BoundaryScanner;

public class SimpleBoundaryScanner
implements BoundaryScanner {
    public static final int DEFAULT_MAX_SCAN = 20;
    public static final Character[] DEFAULT_BOUNDARY_CHARS = new Character[]{Character.valueOf('.'), Character.valueOf(','), Character.valueOf('!'), Character.valueOf('?'), Character.valueOf(' '), Character.valueOf('\t'), Character.valueOf('\n')};
    protected int maxScan;
    protected Set<Character> boundaryChars;

    public SimpleBoundaryScanner() {
        this(20, DEFAULT_BOUNDARY_CHARS);
    }

    public SimpleBoundaryScanner(int maxScan) {
        this(maxScan, DEFAULT_BOUNDARY_CHARS);
    }

    public SimpleBoundaryScanner(Character[] boundaryChars) {
        this(20, boundaryChars);
    }

    public SimpleBoundaryScanner(int maxScan, Character[] boundaryChars) {
        this.maxScan = maxScan;
        this.boundaryChars = new HashSet<Character>();
        this.boundaryChars.addAll(Arrays.asList(boundaryChars));
    }

    public SimpleBoundaryScanner(int maxScan, Set<Character> boundaryChars) {
        this.maxScan = maxScan;
        this.boundaryChars = boundaryChars;
    }

    @Override
    public int findStartOffset(StringBuilder buffer, int start) {
        int offset;
        if (start > buffer.length() || start < 1) {
            return start;
        }
        int count = this.maxScan;
        for (offset = start; offset > 0 && count > 0; --offset, --count) {
            if (!this.boundaryChars.contains(Character.valueOf(buffer.charAt(offset - 1)))) continue;
            return offset;
        }
        if (offset == 0) {
            return 0;
        }
        return start;
    }

    @Override
    public int findEndOffset(StringBuilder buffer, int start) {
        if (start > buffer.length() || start < 0) {
            return start;
        }
        int count = this.maxScan;
        for (int offset = start; offset < buffer.length() && count > 0; ++offset, --count) {
            if (!this.boundaryChars.contains(Character.valueOf(buffer.charAt(offset)))) continue;
            return offset;
        }
        return start;
    }
}

