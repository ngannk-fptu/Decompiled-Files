/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.filters;

import org.apache.tools.ant.filters.TokenFilter;
import org.apache.tools.ant.util.Native2AsciiUtils;

public class Native2AsciiFilter
extends TokenFilter.ChainableReaderFilter {
    private boolean reverse;

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public String filter(String line) {
        return this.reverse ? Native2AsciiUtils.ascii2native(line) : Native2AsciiUtils.native2ascii(line);
    }
}

