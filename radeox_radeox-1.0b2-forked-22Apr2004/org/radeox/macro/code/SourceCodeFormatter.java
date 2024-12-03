/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro.code;

import org.radeox.filter.context.FilterContext;

public interface SourceCodeFormatter {
    public String getName();

    public int getPriority();

    public String filter(String var1, FilterContext var2);
}

