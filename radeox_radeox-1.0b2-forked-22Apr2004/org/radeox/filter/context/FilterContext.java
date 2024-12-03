/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.filter.context;

import org.radeox.api.engine.context.RenderContext;
import org.radeox.macro.parameter.MacroParameter;

public interface FilterContext {
    public MacroParameter getMacroParameter();

    public void setRenderContext(RenderContext var1);

    public RenderContext getRenderContext();
}

