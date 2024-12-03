/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.filters;

import org.apache.tools.ant.filters.TokenFilter;

public class UniqFilter
extends TokenFilter.ChainableReaderFilter {
    private String lastLine = null;

    @Override
    public String filter(String string) {
        if (this.lastLine == null || !this.lastLine.equals(string)) {
            this.lastLine = string;
            return this.lastLine;
        }
        return null;
    }
}

