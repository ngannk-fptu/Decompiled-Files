/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scripting.support;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptFactory;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.scripting.support.StandardScriptUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class StandardScriptFactory
implements ScriptFactory,
BeanClassLoaderAware {
    @Nullable
    private final String scriptEngineName;
    private final String scriptSourceLocator;
    @Nullable
    private final Class<?>[] scriptInterfaces;
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    @Nullable
    private volatile ScriptEngine scriptEngine;

    public StandardScriptFactory(String scriptSourceLocator) {
        this(null, scriptSourceLocator, (Class[])null);
    }

    public StandardScriptFactory(String scriptSourceLocator, Class<?> ... scriptInterfaces) {
        this(null, scriptSourceLocator, scriptInterfaces);
    }

    public StandardScriptFactory(String scriptEngineName, String scriptSourceLocator) {
        this(scriptEngineName, scriptSourceLocator, (Class[])null);
    }

    public StandardScriptFactory(@Nullable String scriptEngineName, String scriptSourceLocator, Class<?> ... scriptInterfaces) {
        Assert.hasText(scriptSourceLocator, "'scriptSourceLocator' must not be empty");
        this.scriptEngineName = scriptEngineName;
        this.scriptSourceLocator = scriptSourceLocator;
        this.scriptInterfaces = scriptInterfaces;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public String getScriptSourceLocator() {
        return this.scriptSourceLocator;
    }

    @Override
    @Nullable
    public Class<?>[] getScriptInterfaces() {
        return this.scriptInterfaces;
    }

    @Override
    public boolean requiresConfigInterface() {
        return false;
    }

    @Override
    @Nullable
    public Object getScriptedObject(ScriptSource scriptSource, Class<?> ... actualInterfaces) throws IOException, ScriptCompilationException {
        Object script = this.evaluateScript(scriptSource);
        if (!ObjectUtils.isEmpty(actualInterfaces)) {
            boolean adaptationRequired = false;
            for (Class<?> requestedIfc : actualInterfaces) {
                if (!(script instanceof Class ? !requestedIfc.isAssignableFrom((Class)script) : !requestedIfc.isInstance(script))) continue;
                adaptationRequired = true;
                break;
            }
            if (adaptationRequired) {
                script = this.adaptToInterfaces(script, scriptSource, actualInterfaces);
            }
        }
        if (script instanceof Class) {
            Class scriptClass = (Class)script;
            try {
                return ReflectionUtils.accessibleConstructor(scriptClass, new Class[0]).newInstance(new Object[0]);
            }
            catch (NoSuchMethodException ex) {
                throw new ScriptCompilationException("No default constructor on script class: " + scriptClass.getName(), (Throwable)ex);
            }
            catch (InstantiationException ex) {
                throw new ScriptCompilationException(scriptSource, "Unable to instantiate script class: " + scriptClass.getName(), ex);
            }
            catch (IllegalAccessException ex) {
                throw new ScriptCompilationException(scriptSource, "Could not access script constructor: " + scriptClass.getName(), ex);
            }
            catch (InvocationTargetException ex) {
                throw new ScriptCompilationException("Failed to invoke script constructor: " + scriptClass.getName(), ex.getTargetException());
            }
        }
        return script;
    }

    protected Object evaluateScript(ScriptSource scriptSource) {
        try {
            ScriptEngine scriptEngine = this.scriptEngine;
            if (scriptEngine == null) {
                scriptEngine = this.retrieveScriptEngine(scriptSource);
                if (scriptEngine == null) {
                    throw new IllegalStateException("Could not determine script engine for " + scriptSource);
                }
                this.scriptEngine = scriptEngine;
            }
            return scriptEngine.eval(scriptSource.getScriptAsString());
        }
        catch (Exception ex) {
            throw new ScriptCompilationException(scriptSource, (Throwable)ex);
        }
    }

    @Nullable
    protected ScriptEngine retrieveScriptEngine(ScriptSource scriptSource) {
        ScriptEngine engine;
        String extension;
        String filename;
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager(this.beanClassLoader);
        if (this.scriptEngineName != null) {
            return StandardScriptUtils.retrieveEngineByName(scriptEngineManager, this.scriptEngineName);
        }
        if (scriptSource instanceof ResourceScriptSource && (filename = ((ResourceScriptSource)scriptSource).getResource().getFilename()) != null && (extension = StringUtils.getFilenameExtension(filename)) != null && (engine = scriptEngineManager.getEngineByExtension(extension)) != null) {
            return engine;
        }
        return null;
    }

    @Nullable
    protected Object adaptToInterfaces(@Nullable Object script, ScriptSource scriptSource, Class<?> ... actualInterfaces) {
        Class<?> adaptedIfc = actualInterfaces.length == 1 ? actualInterfaces[0] : ClassUtils.createCompositeInterface(actualInterfaces, this.beanClassLoader);
        if (adaptedIfc != null) {
            ScriptEngine scriptEngine = this.scriptEngine;
            if (!(scriptEngine instanceof Invocable)) {
                throw new ScriptCompilationException(scriptSource, "ScriptEngine must implement Invocable in order to adapt it to an interface: " + scriptEngine);
            }
            Invocable invocable = (Invocable)((Object)scriptEngine);
            if (script != null) {
                script = invocable.getInterface(script, adaptedIfc);
            }
            if (script == null && (script = invocable.getInterface(adaptedIfc)) == null) {
                throw new ScriptCompilationException(scriptSource, "Could not adapt script to interface [" + adaptedIfc.getName() + "]");
            }
        }
        return script;
    }

    @Override
    @Nullable
    public Class<?> getScriptedObjectType(ScriptSource scriptSource) throws IOException, ScriptCompilationException {
        return null;
    }

    @Override
    public boolean requiresScriptedObjectRefresh(ScriptSource scriptSource) {
        return scriptSource.isModified();
    }

    public String toString() {
        return "StandardScriptFactory: script source locator [" + this.scriptSourceLocator + "]";
    }
}

