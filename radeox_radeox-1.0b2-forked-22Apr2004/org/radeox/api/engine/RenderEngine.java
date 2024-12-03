/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.api.engine;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.radeox.api.engine.context.RenderContext;

public interface RenderEngine {
    public String getName();

    public String render(String var1, RenderContext var2);

    public void render(Writer var1, String var2, RenderContext var3) throws IOException;

    public String render(Reader var1, RenderContext var2) throws IOException;
}

