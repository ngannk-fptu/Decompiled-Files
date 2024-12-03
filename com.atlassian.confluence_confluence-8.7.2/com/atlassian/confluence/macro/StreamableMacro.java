/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import java.util.Map;

public interface StreamableMacro
extends Macro {
    public Streamable executeToStream(Map<String, String> var1, Streamable var2, ConversionContext var3) throws MacroExecutionException;
}

