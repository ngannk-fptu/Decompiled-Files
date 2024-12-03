/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.filters.BaseParamFilterReader;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.types.Parameter;

public final class TabsToSpaces
extends BaseParamFilterReader
implements ChainableReader {
    private static final int DEFAULT_TAB_LENGTH = 8;
    private static final String TAB_LENGTH_KEY = "tablength";
    private int tabLength = 8;
    private int spacesRemaining = 0;

    public TabsToSpaces() {
    }

    public TabsToSpaces(Reader in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        if (!this.getInitialized()) {
            this.initialize();
            this.setInitialized(true);
        }
        int ch = -1;
        if (this.spacesRemaining > 0) {
            --this.spacesRemaining;
            ch = 32;
        } else {
            ch = this.in.read();
            if (ch == 9) {
                this.spacesRemaining = this.tabLength - 1;
                ch = 32;
            }
        }
        return ch;
    }

    public void setTablength(int tabLength) {
        this.tabLength = tabLength;
    }

    private int getTablength() {
        return this.tabLength;
    }

    @Override
    public Reader chain(Reader rdr) {
        TabsToSpaces newFilter = new TabsToSpaces(rdr);
        newFilter.setTablength(this.getTablength());
        newFilter.setInitialized(true);
        return newFilter;
    }

    private void initialize() {
        Parameter[] params = this.getParameters();
        if (params != null) {
            for (Parameter param : params) {
                if (param == null || !TAB_LENGTH_KEY.equals(param.getName())) continue;
                this.tabLength = Integer.parseInt(param.getValue());
                break;
            }
        }
    }
}

