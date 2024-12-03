/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro.code;

import org.radeox.filter.context.FilterContext;
import org.radeox.macro.code.SourceCodeFormatter;

public class NullCodeFilter
implements SourceCodeFormatter {
    public String filter(String content, FilterContext context) {
        return content;
    }

    public String getName() {
        return "none";
    }

    public int getPriority() {
        return 0;
    }
}

