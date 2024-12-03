/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.filters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.filters.BaseParamFilterReader;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.types.Parameter;

public final class ConcatFilter
extends BaseParamFilterReader
implements ChainableReader {
    private File prepend;
    private File append;
    private Reader prependReader = null;
    private Reader appendReader = null;

    public ConcatFilter() {
    }

    public ConcatFilter(Reader in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        if (!this.getInitialized()) {
            this.initialize();
            this.setInitialized(true);
        }
        int ch = -1;
        if (this.prependReader != null && (ch = this.prependReader.read()) == -1) {
            this.prependReader.close();
            this.prependReader = null;
        }
        if (ch == -1) {
            ch = super.read();
        }
        if (ch == -1 && this.appendReader != null && (ch = this.appendReader.read()) == -1) {
            this.appendReader.close();
            this.appendReader = null;
        }
        return ch;
    }

    public void setPrepend(File prepend) {
        this.prepend = prepend;
    }

    public File getPrepend() {
        return this.prepend;
    }

    public void setAppend(File append) {
        this.append = append;
    }

    public File getAppend() {
        return this.append;
    }

    @Override
    public Reader chain(Reader rdr) {
        ConcatFilter newFilter = new ConcatFilter(rdr);
        newFilter.setPrepend(this.getPrepend());
        newFilter.setAppend(this.getAppend());
        return newFilter;
    }

    private void initialize() throws IOException {
        Parameter[] params = this.getParameters();
        if (params != null) {
            for (Parameter param : params) {
                String paramName = param.getName();
                if ("prepend".equals(paramName)) {
                    this.setPrepend(new File(param.getValue()));
                    continue;
                }
                if (!"append".equals(paramName)) continue;
                this.setAppend(new File(param.getValue()));
            }
        }
        if (this.prepend != null) {
            if (!this.prepend.isAbsolute()) {
                this.prepend = new File(this.getProject().getBaseDir(), this.prepend.getPath());
            }
            this.prependReader = new BufferedReader(new FileReader(this.prepend));
        }
        if (this.append != null) {
            if (!this.append.isAbsolute()) {
                this.append = new File(this.getProject().getBaseDir(), this.append.getPath());
            }
            this.appendReader = new BufferedReader(new FileReader(this.append));
        }
    }
}

