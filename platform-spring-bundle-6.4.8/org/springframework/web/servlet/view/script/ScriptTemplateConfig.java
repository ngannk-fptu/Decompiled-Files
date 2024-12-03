/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.view.script;

import java.nio.charset.Charset;
import java.util.function.Supplier;
import javax.script.ScriptEngine;
import org.springframework.lang.Nullable;

public interface ScriptTemplateConfig {
    @Nullable
    public ScriptEngine getEngine();

    @Nullable
    public Supplier<ScriptEngine> getEngineSupplier();

    @Nullable
    public String getEngineName();

    @Nullable
    public Boolean isSharedEngine();

    @Nullable
    public String[] getScripts();

    @Nullable
    public String getRenderObject();

    @Nullable
    public String getRenderFunction();

    @Nullable
    public String getContentType();

    @Nullable
    public Charset getCharset();

    @Nullable
    public String getResourceLoaderPath();
}

