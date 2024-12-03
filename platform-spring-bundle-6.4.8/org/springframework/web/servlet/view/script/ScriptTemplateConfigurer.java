/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.view.script;

import java.nio.charset.Charset;
import java.util.function.Supplier;
import javax.script.ScriptEngine;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.view.script.ScriptTemplateConfig;

public class ScriptTemplateConfigurer
implements ScriptTemplateConfig {
    @Nullable
    private ScriptEngine engine;
    @Nullable
    private Supplier<ScriptEngine> engineSupplier;
    @Nullable
    private String engineName;
    @Nullable
    private Boolean sharedEngine;
    @Nullable
    private String[] scripts;
    @Nullable
    private String renderObject;
    @Nullable
    private String renderFunction;
    @Nullable
    private String contentType;
    @Nullable
    private Charset charset;
    @Nullable
    private String resourceLoaderPath;

    public ScriptTemplateConfigurer() {
    }

    public ScriptTemplateConfigurer(String engineName) {
        this.engineName = engineName;
    }

    public void setEngine(@Nullable ScriptEngine engine) {
        this.engine = engine;
    }

    @Override
    @Nullable
    public ScriptEngine getEngine() {
        return this.engine;
    }

    public void setEngineSupplier(@Nullable Supplier<ScriptEngine> engineSupplier) {
        this.engineSupplier = engineSupplier;
    }

    @Override
    @Nullable
    public Supplier<ScriptEngine> getEngineSupplier() {
        return this.engineSupplier;
    }

    public void setEngineName(@Nullable String engineName) {
        this.engineName = engineName;
    }

    @Override
    @Nullable
    public String getEngineName() {
        return this.engineName;
    }

    public void setSharedEngine(@Nullable Boolean sharedEngine) {
        this.sharedEngine = sharedEngine;
    }

    @Override
    @Nullable
    public Boolean isSharedEngine() {
        return this.sharedEngine;
    }

    public void setScripts(String ... scriptNames) {
        this.scripts = scriptNames;
    }

    @Override
    @Nullable
    public String[] getScripts() {
        return this.scripts;
    }

    public void setRenderObject(@Nullable String renderObject) {
        this.renderObject = renderObject;
    }

    @Override
    @Nullable
    public String getRenderObject() {
        return this.renderObject;
    }

    public void setRenderFunction(@Nullable String renderFunction) {
        this.renderFunction = renderFunction;
    }

    @Override
    @Nullable
    public String getRenderFunction() {
        return this.renderFunction;
    }

    public void setContentType(@Nullable String contentType) {
        this.contentType = contentType;
    }

    @Override
    @Nullable
    public String getContentType() {
        return this.contentType;
    }

    public void setCharset(@Nullable Charset charset) {
        this.charset = charset;
    }

    @Override
    @Nullable
    public Charset getCharset() {
        return this.charset;
    }

    public void setResourceLoaderPath(@Nullable String resourceLoaderPath) {
        this.resourceLoaderPath = resourceLoaderPath;
    }

    @Override
    @Nullable
    public String getResourceLoaderPath() {
        return this.resourceLoaderPath;
    }
}

