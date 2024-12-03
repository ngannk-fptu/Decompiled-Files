/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro;

import java.io.IOException;
import java.io.Writer;
import org.radeox.api.engine.context.InitialRenderContext;
import org.radeox.macro.parameter.MacroParameter;

public interface Macro
extends Comparable {
    public String getName();

    public String getDescription();

    public String[] getParamDescription();

    public void setInitialContext(InitialRenderContext var1);

    public void execute(Writer var1, MacroParameter var2) throws IllegalArgumentException, IOException;
}

