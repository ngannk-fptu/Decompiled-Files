/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.MacroExecutionException;
import java.util.Map;

public interface Macro {
    public String execute(Map<String, String> var1, String var2, ConversionContext var3) throws MacroExecutionException;

    public BodyType getBodyType();

    public OutputType getOutputType();

    public static enum OutputType {
        INLINE,
        BLOCK;

    }

    public static enum BodyType {
        PLAIN_TEXT,
        RICH_TEXT,
        NONE;

    }
}

