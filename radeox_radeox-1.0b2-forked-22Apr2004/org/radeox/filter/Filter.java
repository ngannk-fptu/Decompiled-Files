/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.filter;

import org.radeox.api.engine.context.InitialRenderContext;
import org.radeox.filter.context.FilterContext;

public interface Filter {
    public String filter(String var1, FilterContext var2);

    public String[] replaces();

    public String[] before();

    public void setInitialContext(InitialRenderContext var1);

    public String getDescription();
}

