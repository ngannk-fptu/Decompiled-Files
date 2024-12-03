/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.filter.context;

import org.radeox.api.engine.context.RenderContext;
import org.radeox.filter.context.FilterContext;
import org.radeox.macro.parameter.BaseMacroParameter;
import org.radeox.macro.parameter.MacroParameter;

public class BaseFilterContext
implements FilterContext {
    protected RenderContext context;

    public MacroParameter getMacroParameter() {
        return new BaseMacroParameter(this.context);
    }

    public void setRenderContext(RenderContext context) {
        this.context = context;
    }

    public RenderContext getRenderContext() {
        return this.context;
    }
}

