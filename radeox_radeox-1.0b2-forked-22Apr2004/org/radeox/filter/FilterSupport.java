/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.filter;

import org.radeox.api.engine.context.InitialRenderContext;
import org.radeox.filter.Filter;
import org.radeox.filter.FilterPipe;

public abstract class FilterSupport
implements Filter {
    protected InitialRenderContext initialContext;

    public String[] replaces() {
        return FilterPipe.NO_REPLACES;
    }

    public String[] before() {
        return FilterPipe.EMPTY_BEFORE;
    }

    public void setInitialContext(InitialRenderContext context) {
        this.initialContext = context;
    }

    public String getDescription() {
        return "";
    }
}

