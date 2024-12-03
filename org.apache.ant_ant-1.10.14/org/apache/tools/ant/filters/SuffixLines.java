/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.filters.BaseParamFilterReader;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.types.Parameter;

public final class SuffixLines
extends BaseParamFilterReader
implements ChainableReader {
    private static final String SUFFIX_KEY = "suffix";
    private String suffix = null;
    private String queuedData = null;

    public SuffixLines() {
    }

    public SuffixLines(Reader in) {
        super(in);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public int read() throws IOException {
        if (!this.getInitialized()) {
            this.initialize();
            this.setInitialized(true);
        }
        int ch = -1;
        if (this.queuedData != null && this.queuedData.isEmpty()) {
            this.queuedData = null;
        }
        if (this.queuedData == null) {
            this.queuedData = this.readLine();
            if (this.queuedData == null) {
                return -1;
            }
            if (this.suffix == null) return this.read();
            String lf = "";
            if (this.queuedData.endsWith("\r\n")) {
                lf = "\r\n";
            } else if (this.queuedData.endsWith("\n")) {
                lf = "\n";
            }
            this.queuedData = this.queuedData.substring(0, this.queuedData.length() - lf.length()) + this.suffix + lf;
            return this.read();
        }
        ch = this.queuedData.charAt(0);
        this.queuedData = this.queuedData.substring(1);
        if (!this.queuedData.isEmpty()) return ch;
        this.queuedData = null;
        return ch;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    private String getSuffix() {
        return this.suffix;
    }

    @Override
    public Reader chain(Reader rdr) {
        SuffixLines newFilter = new SuffixLines(rdr);
        newFilter.setSuffix(this.getSuffix());
        newFilter.setInitialized(true);
        return newFilter;
    }

    private void initialize() {
        Parameter[] params = this.getParameters();
        if (params != null) {
            for (Parameter param : params) {
                if (!SUFFIX_KEY.equals(param.getName())) continue;
                this.suffix = param.getValue();
                break;
            }
        }
    }
}

