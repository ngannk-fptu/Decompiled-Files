/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.fortuna.ical4j.data;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Arrays;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnfoldingReader
extends PushbackReader {
    private Logger log = LoggerFactory.getLogger(UnfoldingReader.class);
    private static final char[] DEFAULT_FOLD_PATTERN_1 = new char[]{'\r', '\n', ' '};
    private static final char[] DEFAULT_FOLD_PATTERN_2 = new char[]{'\r', '\n', '\t'};
    private static final char[] RELAXED_FOLD_PATTERN_1 = new char[]{'\n', ' '};
    private static final char[] RELAXED_FOLD_PATTERN_2 = new char[]{'\n', '\t'};
    private char[][] patterns;
    private char[][] buffers;
    private int linesUnfolded;
    private int maxPatternLength = 0;

    public UnfoldingReader(Reader in) {
        this(in, DEFAULT_FOLD_PATTERN_1.length, CompatibilityHints.isHintEnabled("ical4j.unfolding.relaxed"));
    }

    public UnfoldingReader(Reader in, int size) {
        this(in, size, CompatibilityHints.isHintEnabled("ical4j.unfolding.relaxed"));
    }

    public UnfoldingReader(Reader in, boolean relaxed) {
        this(in, DEFAULT_FOLD_PATTERN_1.length, relaxed);
    }

    public UnfoldingReader(Reader in, int size, boolean relaxed) {
        super(in, size);
        if (relaxed) {
            this.patterns = new char[4][];
            this.patterns[0] = DEFAULT_FOLD_PATTERN_1;
            this.patterns[1] = DEFAULT_FOLD_PATTERN_2;
            this.patterns[2] = RELAXED_FOLD_PATTERN_1;
            this.patterns[3] = RELAXED_FOLD_PATTERN_2;
        } else {
            this.patterns = new char[2][];
            this.patterns[0] = DEFAULT_FOLD_PATTERN_1;
            this.patterns[1] = DEFAULT_FOLD_PATTERN_2;
        }
        this.buffers = new char[this.patterns.length][];
        for (int i = 0; i < this.patterns.length; ++i) {
            this.buffers[i] = new char[this.patterns[i].length];
            this.maxPatternLength = Math.max(this.maxPatternLength, this.patterns[i].length);
        }
    }

    public final int getLinesUnfolded() {
        return this.linesUnfolded;
    }

    @Override
    public final int read() throws IOException {
        int c = super.read();
        boolean doUnfold = false;
        for (char[] pattern : this.patterns) {
            if (c != pattern[0]) continue;
            doUnfold = true;
            break;
        }
        if (!doUnfold) {
            return c;
        }
        this.unread(c);
        this.unfold();
        return super.read();
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int read = super.read(cbuf, off, len);
        boolean doUnfold = false;
        for (char[] pattern : this.patterns) {
            if (read > 0 && cbuf[0] == pattern[0]) {
                doUnfold = true;
                break;
            }
            for (int j = 0; j < read; ++j) {
                if (cbuf[j] != pattern[0]) continue;
                this.unread(cbuf, j, read - j);
                return j;
            }
        }
        if (!doUnfold) {
            return read;
        }
        this.unread(cbuf, off, read);
        this.unfold();
        return super.read(cbuf, off, this.maxPatternLength);
    }

    private void unfold() throws IOException {
        boolean didUnfold;
        do {
            didUnfold = false;
            for (int i = 0; i < this.buffers.length; ++i) {
                int read;
                int partialRead;
                for (read = 0; read < this.buffers[i].length && (partialRead = super.read(this.buffers[i], read, this.buffers[i].length - read)) >= 0; read += partialRead) {
                }
                if (read <= 0) continue;
                if (!Arrays.equals(this.patterns[i], this.buffers[i])) {
                    this.unread(this.buffers[i], 0, read);
                    continue;
                }
                if (this.log.isTraceEnabled()) {
                    this.log.trace("Unfolding...");
                }
                ++this.linesUnfolded;
                didUnfold = true;
            }
        } while (didUnfold);
    }
}

