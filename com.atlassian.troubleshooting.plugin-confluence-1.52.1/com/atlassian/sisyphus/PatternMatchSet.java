/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sisyphus;

import com.atlassian.sisyphus.LogLine;
import com.atlassian.sisyphus.SisyphusPattern;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

public class PatternMatchSet {
    private final SortedSet<LogLine> matchedLines = new TreeSet<LogLine>();
    private final SisyphusPattern pattern;

    public PatternMatchSet(SisyphusPattern pattern) {
        this.pattern = pattern;
    }

    public SisyphusPattern getPattern() {
        return this.pattern;
    }

    public void lineMatched(LogLine datedLine) {
        this.matchedLines.add(datedLine);
    }

    public int getFirstMatchedLine() {
        return this.matchedLines.first().getLineNo();
    }

    public int getLastMatchedLine() {
        return this.matchedLines.last().getLineNo();
    }

    public Date getFirstMatchedDate() {
        return this.matchedLines.first().getDate();
    }

    public Date getLastMatchedDate() {
        return this.matchedLines.last().getDate();
    }

    public int getMatchCount() {
        return this.matchedLines.size();
    }
}

