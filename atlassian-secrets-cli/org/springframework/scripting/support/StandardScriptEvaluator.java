/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scripting.support;

import java.io.IOException;
import java.util.Map;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptEvaluator;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.scripting.support.StandardScriptEvalException;
import org.springframework.scripting.support.StandardScriptUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class StandardScriptEvaluator
implements ScriptEvaluator,
BeanClassLoaderAware {
    @Nullable
    private String engineName;
    @Nullable
    private volatile Bindings globalBindings;
    @Nullable
    private volatile ScriptEngineManager scriptEngineManager;

    public StandardScriptEvaluator() {
    }

    public StandardScriptEvaluator(ClassLoader classLoader) {
        this.scriptEngineManager = new ScriptEngineManager(classLoader);
    }

    public StandardScriptEvaluator(ScriptEngineManager scriptEngineManager) {
        this.scriptEngineManager = scriptEngineManager;
    }

    public void setLanguage(String language) {
        this.engineName = language;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public void setGlobalBindings(Map<String, Object> globalBindings) {
        Bindings bindings;
        this.globalBindings = bindings = StandardScriptUtils.getBindings(globalBindings);
        ScriptEngineManager scriptEngineManager = this.scriptEngineManager;
        if (scriptEngineManager != null) {
            scriptEngineManager.setBindings(bindings);
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        ScriptEngineManager scriptEngineManager = this.scriptEngineManager;
        if (scriptEngineManager == null) {
            this.scriptEngineManager = scriptEngineManager = new ScriptEngineManager(classLoader);
            Bindings bindings = this.globalBindings;
            if (bindings != null) {
                scriptEngineManager.setBindings(bindings);
            }
        }
    }

    @Override
    @Nullable
    public Object evaluate(ScriptSource script) {
        return this.evaluate(script, null);
    }

    @Override
    @Nullable
    public Object evaluate(ScriptSource script, @Nullable Map<String, Object> argumentBindings) {
        ScriptEngine engine = this.getScriptEngine(script);
        try {
            if (CollectionUtils.isEmpty(argumentBindings)) {
                return engine.eval(script.getScriptAsString());
            }
            Bindings bindings = StandardScriptUtils.getBindings(argumentBindings);
            return engine.eval(script.getScriptAsString(), bindings);
        }
        catch (IOException ex) {
            throw new ScriptCompilationException(script, "Cannot access script for ScriptEngine", ex);
        }
        catch (ScriptException ex) {
            throw new ScriptCompilationException(script, (Throwable)new StandardScriptEvalException(ex));
        }
    }

    protected ScriptEngine getScriptEngine(ScriptSource script) {
        ScriptEngineManager scriptEngineManager = this.scriptEngineManager;
        if (scriptEngineManager == null) {
            this.scriptEngineManager = scriptEngineManager = new ScriptEngineManager();
        }
        if (StringUtils.hasText(this.engineName)) {
            return StandardScriptUtils.retrieveEngineByName(scriptEngineManager, this.engineName);
        }
        if (script instanceof ResourceScriptSource) {
            Resource resource = ((ResourceScriptSource)script).getResource();
            String extension = StringUtils.getFilenameExtension(resource.getFilename());
            if (extension == null) {
                throw new IllegalStateException("No script language defined, and no file extension defined for resource: " + resource);
            }
            ScriptEngine engine = scriptEngineManager.getEngineByExtension(extension);
            if (engine == null) {
                throw new IllegalStateException("No matching engine found for file extension '" + extension + "'");
            }
            return engine;
        }
        throw new IllegalStateException("No script language defined, and no resource associated with script: " + script);
    }
}

