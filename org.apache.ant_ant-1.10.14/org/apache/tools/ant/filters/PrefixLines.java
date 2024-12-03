/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.filters.BaseParamFilterReader;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.types.Parameter;

public final class PrefixLines
extends BaseParamFilterReader
implements ChainableReader {
    private static final String PREFIX_KEY = "prefix";
    private String prefix = null;
    private String queuedData = null;

    public PrefixLines() {
    }

    public PrefixLines(Reader in) {
        super(in);
    }

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
        if (this.queuedData != null) {
            ch = this.queuedData.charAt(0);
            this.queuedData = this.queuedData.substring(1);
            if (this.queuedData.isEmpty()) {
                this.queuedData = null;
            }
        } else {
            this.queuedData = this.readLine();
            if (this.queuedData == null) {
                ch = -1;
            } else {
                if (this.prefix != null) {
                    this.queuedData = this.prefix + this.queuedData;
                }
                return this.read();
            }
        }
        return ch;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    private String getPrefix() {
        return this.prefix;
    }

    @Override
    public Reader chain(Reader rdr) {
        PrefixLines newFilter = new PrefixLines(rdr);
        newFilter.setPrefix(this.getPrefix());
        newFilter.setInitialized(true);
        return newFilter;
    }

    private void initialize() {
        Parameter[] params = this.getParameters();
        if (params != null) {
            for (Parameter param : params) {
                if (!PREFIX_KEY.equals(param.getName())) continue;
                this.prefix = param.getValue();
                break;
            }
        }
    }
}

