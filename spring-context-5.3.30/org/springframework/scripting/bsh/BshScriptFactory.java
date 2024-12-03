/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bsh.EvalError
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.scripting.bsh;

import bsh.EvalError;
import java.io.IOException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptFactory;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.bsh.BshScriptUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public class BshScriptFactory
implements ScriptFactory,
BeanClassLoaderAware {
    private final String scriptSourceLocator;
    @Nullable
    private final Class<?>[] scriptInterfaces;
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    @Nullable
    private Class<?> scriptClass;
    private final Object scriptClassMonitor = new Object();
    private boolean wasModifiedForTypeCheck = false;

    public BshScriptFactory(String scriptSourceLocator) {
        Assert.hasText((String)scriptSourceLocator, (String)"'scriptSourceLocator' must not be empty");
        this.scriptSourceLocator = scriptSourceLocator;
        this.scriptInterfaces = null;
    }

    public BshScriptFactory(String scriptSourceLocator, Class<?> ... scriptInterfaces) {
        Assert.hasText((String)scriptSourceLocator, (String)"'scriptSourceLocator' must not be empty");
        this.scriptSourceLocator = scriptSourceLocator;
        this.scriptInterfaces = scriptInterfaces;
    }

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
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public Object getScriptedObject(ScriptSource scriptSource, Class<?> ... actualInterfaces) throws IOException, ScriptCompilationException {
        Class<?> clazz;
        try {
            Object object = this.scriptClassMonitor;
            synchronized (object) {
                boolean requiresScriptEvaluation = this.wasModifiedForTypeCheck && this.scriptClass == null;
                this.wasModifiedForTypeCheck = false;
                if (scriptSource.isModified() || requiresScriptEvaluation) {
                    Object result = BshScriptUtils.evaluateBshScript(scriptSource.getScriptAsString(), actualInterfaces, this.beanClassLoader);
                    if (result instanceof Class) {
                        this.scriptClass = (Class)result;
                    } else {
                        return result;
                    }
                }
                clazz = this.scriptClass;
            }
        }
        catch (EvalError ex) {
            this.scriptClass = null;
            throw new ScriptCompilationException(scriptSource, (Throwable)ex);
        }
        if (clazz != null) {
            try {
                return ReflectionUtils.accessibleConstructor(clazz, (Class[])new Class[0]).newInstance(new Object[0]);
            }
            catch (Throwable ex) {
                throw new ScriptCompilationException(scriptSource, "Could not instantiate script class: " + clazz.getName(), ex);
            }
        }
        try {
            return BshScriptUtils.createBshObject(scriptSource.getScriptAsString(), actualInterfaces, this.beanClassLoader);
        }
        catch (EvalError ex) {
            throw new ScriptCompilationException(scriptSource, (Throwable)ex);
        }
    }

    @Override
    @Nullable
    public Class<?> getScriptedObjectType(ScriptSource scriptSource) throws IOException, ScriptCompilationException {
        Object object = this.scriptClassMonitor;
        synchronized (object) {
            try {
                if (scriptSource.isModified()) {
                    this.wasModifiedForTypeCheck = true;
                    this.scriptClass = BshScriptUtils.determineBshObjectType(scriptSource.getScriptAsString(), this.beanClassLoader);
                }
                return this.scriptClass;
            }
            catch (EvalError ex) {
                this.scriptClass = null;
                throw new ScriptCompilationException(scriptSource, (Throwable)ex);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean requiresScriptedObjectRefresh(ScriptSource scriptSource) {
        Object object = this.scriptClassMonitor;
        synchronized (object) {
            return scriptSource.isModified() || this.wasModifiedForTypeCheck;
        }
    }

    public String toString() {
        return "BshScriptFactory: script source locator [" + this.scriptSourceLocator + "]";
    }
}

