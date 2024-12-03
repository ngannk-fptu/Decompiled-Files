/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import org.apache.tools.ant.filters.BaseParamFilterReader;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.util.LineTokenizer;

public final class TailFilter
extends BaseParamFilterReader
implements ChainableReader {
    private static final String LINES_KEY = "lines";
    private static final String SKIP_KEY = "skip";
    private static final int DEFAULT_NUM_LINES = 10;
    private long lines = 10L;
    private long skip = 0L;
    private boolean completedReadAhead = false;
    private LineTokenizer lineTokenizer = null;
    private String line = null;
    private int linePos = 0;
    private LinkedList<String> lineList = new LinkedList();

    public TailFilter() {
    }

    public TailFilter(Reader in) {
        super(in);
        this.lineTokenizer = new LineTokenizer();
        this.lineTokenizer.setIncludeDelims(true);
    }

    @Override
    public int read() throws IOException {
        if (!this.getInitialized()) {
            this.initialize();
            this.setInitialized(true);
        }
        while (this.line == null || this.line.isEmpty()) {
            this.line = this.lineTokenizer.getToken(this.in);
            this.line = this.tailFilter(this.line);
            if (this.line == null) {
                return -1;
            }
            this.linePos = 0;
        }
        char ch = this.line.charAt(this.linePos);
        ++this.linePos;
        if (this.linePos == this.line.length()) {
            this.line = null;
        }
        return ch;
    }

    public void setLines(long lines) {
        this.lines = lines;
    }

    private long getLines() {
        return this.lines;
    }

    public void setSkip(long skip) {
        this.skip = skip;
    }

    private long getSkip() {
        return this.skip;
    }

    @Override
    public Reader chain(Reader rdr) {
        TailFilter newFilter = new TailFilter(rdr);
        newFilter.setLines(this.getLines());
        newFilter.setSkip(this.getSkip());
        newFilter.setInitialized(true);
        return newFilter;
    }

    private void initialize() {
        Parameter[] params = this.getParameters();
        if (params != null) {
            for (Parameter param : params) {
                String paramName = param.getName();
                if (LINES_KEY.equals(paramName)) {
                    this.setLines(Long.parseLong(param.getValue()));
                    continue;
                }
                if (!SKIP_KEY.equals(paramName)) continue;
                this.skip = Long.parseLong(param.getValue());
            }
        }
    }

    private String tailFilter(String line) {
        if (!this.completedReadAhead) {
            if (line != null) {
                this.lineList.add(line);
                if (this.lines == -1L) {
                    if ((long)this.lineList.size() > this.skip) {
                        return this.lineList.removeFirst();
                    }
                } else {
                    long linesToKeep = this.lines + (this.skip > 0L ? this.skip : 0L);
                    if (linesToKeep < (long)this.lineList.size()) {
                        this.lineList.removeFirst();
                    }
                }
                return "";
            }
            this.completedReadAhead = true;
            if (this.skip > 0L) {
                int i = 0;
                while ((long)i < this.skip) {
                    this.lineList.removeLast();
                    ++i;
                }
            }
            if (this.lines > -1L) {
                while ((long)this.lineList.size() > this.lines) {
                    this.lineList.removeFirst();
                }
            }
        }
        if (this.lineList.size() > 0) {
            return this.lineList.removeFirst();
        }
        return null;
    }
}

