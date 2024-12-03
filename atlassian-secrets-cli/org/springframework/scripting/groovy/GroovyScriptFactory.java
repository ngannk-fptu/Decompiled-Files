/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.lang.GroovyClassLoader
 *  groovy.lang.GroovyObject
 *  groovy.lang.MetaClass
 *  groovy.lang.Script
 *  org.codehaus.groovy.control.CompilationFailedException
 *  org.codehaus.groovy.control.CompilerConfiguration
 *  org.codehaus.groovy.control.customizers.CompilationCustomizer
 */
package org.springframework.scripting.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Script;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptFactory;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.groovy.GroovyObjectCustomizer;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

public class GroovyScriptFactory
implements ScriptFactory,
BeanFactoryAware,
BeanClassLoaderAware {
    private final String scriptSourceLocator;
    @Nullable
    private GroovyObjectCustomizer groovyObjectCustomizer;
    @Nullable
    private CompilerConfiguration compilerConfiguration;
    @Nullable
    private GroovyClassLoader groovyClassLoader;
    @Nullable
    private Class<?> scriptClass;
    @Nullable
    private Class<?> scriptResultClass;
    @Nullable
    private CachedResultHolder cachedResult;
    private final Object scriptClassMonitor = new Object();
    private boolean wasModifiedForTypeCheck = false;

    public GroovyScriptFactory(String scriptSourceLocator) {
        Assert.hasText(scriptSourceLocator, "'scriptSourceLocator' must not be empty");
        this.scriptSourceLocator = scriptSourceLocator;
    }

    public GroovyScriptFactory(String scriptSourceLocator, @Nullable GroovyObjectCustomizer groovyObjectCustomizer) {
        this(scriptSourceLocator);
        this.groovyObjectCustomizer = groovyObjectCustomizer;
    }

    public GroovyScriptFactory(String scriptSourceLocator, @Nullable CompilerConfiguration compilerConfiguration) {
        this(scriptSourceLocator);
        this.compilerConfiguration = compilerConfiguration;
    }

    public GroovyScriptFactory(String scriptSourceLocator, CompilationCustomizer ... compilationCustomizers) {
        this(scriptSourceLocator);
        if (!ObjectUtils.isEmpty(compilationCustomizers)) {
            this.compilerConfiguration = new CompilerConfiguration();
            this.compilerConfiguration.addCompilationCustomizers(compilationCustomizers);
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            ((ConfigurableListableBeanFactory)beanFactory).ignoreDependencyType(MetaClass.class);
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.groovyClassLoader = this.buildGroovyClassLoader(classLoader);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GroovyClassLoader getGroovyClassLoader() {
        Object object = this.scriptClassMonitor;
        synchronized (object) {
            if (this.groovyClassLoader == null) {
                this.groovyClassLoader = this.buildGroovyClassLoader(ClassUtils.getDefaultClassLoader());
            }
            return this.groovyClassLoader;
        }
    }

    protected GroovyClassLoader buildGroovyClassLoader(@Nullable ClassLoader classLoader) {
        return this.compilerConfiguration != null ? new GroovyClassLoader(classLoader, this.compilerConfiguration) : new GroovyClassLoader(classLoader);
    }

    @Override
    public String getScriptSourceLocator() {
        return this.scriptSourceLocator;
    }

    @Override
    @Nullable
    public Class<?>[] getScriptInterfaces() {
        return null;
    }

    @Override
    public boolean requiresConfigInterface() {
        return false;
    }

    @Override
    @Nullable
    public Object getScriptedObject(ScriptSource scriptSource, Class<?> ... actualInterfaces) throws IOException, ScriptCompilationException {
        Object object = this.scriptClassMonitor;
        synchronized (object) {
            try {
                this.wasModifiedForTypeCheck = false;
                if (this.cachedResult != null) {
                    Object result = this.cachedResult.object;
                    this.cachedResult = null;
                    return result;
                }
                if (this.scriptClass == null || scriptSource.isModified()) {
                    this.scriptClass = this.getGroovyClassLoader().parseClass(scriptSource.getScriptAsString(), scriptSource.suggestedClassName());
                    if (Script.class.isAssignableFrom(this.scriptClass)) {
                        Object result = this.executeScript(scriptSource, this.scriptClass);
                        this.scriptResultClass = result != null ? result.getClass() : null;
                        return result;
                    }
                    this.scriptResultClass = this.scriptClass;
                }
                Class<?> scriptClassToExecute = this.scriptClass;
                return this.executeScript(scriptSource, scriptClassToExecute);
            }
            catch (CompilationFailedException ex) {
                this.scriptClass = null;
                this.scriptResultClass = null;
                throw new ScriptCompilationException(scriptSource, (Throwable)ex);
            }
        }
    }

    @Override
    @Nullable
    public Class<?> getScriptedObjectType(ScriptSource scriptSource) throws IOException, ScriptCompilationException {
        Object object = this.scriptClassMonitor;
        synchronized (object) {
            try {
                if (this.scriptClass == null || scriptSource.isModified()) {
                    this.wasModifiedForTypeCheck = true;
                    this.scriptClass = this.getGroovyClassLoader().parseClass(scriptSource.getScriptAsString(), scriptSource.suggestedClassName());
                    if (Script.class.isAssignableFrom(this.scriptClass)) {
                        Object result = this.executeScript(scriptSource, this.scriptClass);
                        this.scriptResultClass = result != null ? result.getClass() : null;
                        this.cachedResult = new CachedResultHolder(result);
                    } else {
                        this.scriptResultClass = this.scriptClass;
                    }
                }
                return this.scriptResultClass;
            }
            catch (CompilationFailedException ex) {
                this.scriptClass = null;
                this.scriptResultClass = null;
                this.cachedResult = null;
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

    @Nullable
    protected Object executeScript(ScriptSource scriptSource, Class<?> scriptClass) throws ScriptCompilationException {
        try {
            GroovyObject goo = (GroovyObject)ReflectionUtils.accessibleConstructor(scriptClass, new Class[0]).newInstance(new Object[0]);
            if (this.groovyObjectCustomizer != null) {
                this.groovyObjectCustomizer.customize(goo);
            }
            if (goo instanceof Script) {
                return ((Script)goo).run();
            }
            return goo;
        }
        catch (NoSuchMethodException ex) {
            throw new ScriptCompilationException("No default constructor on Groovy script class: " + scriptClass.getName(), (Throwable)ex);
        }
        catch (InstantiationException ex) {
            throw new ScriptCompilationException(scriptSource, "Unable to instantiate Groovy script class: " + scriptClass.getName(), ex);
        }
        catch (IllegalAccessException ex) {
            throw new ScriptCompilationException(scriptSource, "Could not access Groovy script constructor: " + scriptClass.getName(), ex);
        }
        catch (InvocationTargetException ex) {
            throw new ScriptCompilationException("Failed to invoke Groovy script constructor: " + scriptClass.getName(), ex.getTargetException());
        }
    }

    public String toString() {
        return "GroovyScriptFactory: script source locator [" + this.scriptSourceLocator + "]";
    }

    private static class CachedResultHolder {
        @Nullable
        public final Object object;

        public CachedResultHolder(@Nullable Object object) {
            this.object = object;
        }
    }
}

