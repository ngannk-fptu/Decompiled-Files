/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.api.engine.context;

import java.util.Map;
import org.radeox.api.engine.RenderEngine;

public interface RenderContext {
    public static final String INPUT_BUNDLE_NAME = "RenderContext.input_bundle_name";
    public static final String OUTPUT_BUNDLE_NAME = "RenderContext.output_bundle_name";
    public static final String LANGUAGE_BUNDLE_NAME = "RenderContext.language_bundle_name";
    public static final String LANGUAGE_LOCALE = "RenderContext.language_locale";
    public static final String INPUT_LOCALE = "RenderContext.input_locale";
    public static final String OUTPUT_LOCALE = "RenderContext.output_locale";
    public static final String DEFAULT_FORMATTER = "RenderContext.default_formatter";

    public RenderEngine getRenderEngine();

    public void setRenderEngine(RenderEngine var1);

    public Object get(String var1);

    public void set(String var1, Object var2);

    public Map getParameters();

    public void setParameters(Map var1);

    public void setCacheable(boolean var1);

    public void commitCache();

    public boolean isCacheable();
}

