/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.StreamableMacro;
import java.util.Map;

public abstract class StreamableMacroAdapter
implements StreamableMacro {
    public static String executeFromStream(StreamableMacro macro, Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException {
        return Streamables.writeToString(macro.executeToStream(parameters, Streamables.from(body), context));
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException {
        return StreamableMacroAdapter.executeFromStream(this, parameters, body, context);
    }
}

