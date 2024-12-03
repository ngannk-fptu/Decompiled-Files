/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.engine.scripting;

import java.lang.invoke.MethodHandles;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.hibernate.validator.spi.scripting.AbstractCachingScriptEvaluatorFactory;
import org.hibernate.validator.spi.scripting.ScriptEngineScriptEvaluator;
import org.hibernate.validator.spi.scripting.ScriptEvaluationException;
import org.hibernate.validator.spi.scripting.ScriptEvaluator;

public class DefaultScriptEvaluatorFactory
extends AbstractCachingScriptEvaluatorFactory {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private ClassLoader classLoader;
    private volatile ScriptEngineManager scriptEngineManager;
    private volatile ScriptEngineManager threadContextClassLoaderScriptEngineManager;

    public DefaultScriptEvaluatorFactory(ClassLoader externalClassLoader) {
        this.classLoader = externalClassLoader == null ? DefaultScriptEvaluatorFactory.class.getClassLoader() : externalClassLoader;
    }

    @Override
    public void clear() {
        super.clear();
        this.classLoader = null;
        this.scriptEngineManager = null;
        this.threadContextClassLoaderScriptEngineManager = null;
    }

    @Override
    protected ScriptEvaluator createNewScriptEvaluator(String languageName) throws ScriptEvaluationException {
        ScriptEngine engine = this.getScriptEngineManager().getEngineByName(languageName);
        if (engine == null) {
            engine = this.getThreadContextClassLoaderScriptEngineManager().getEngineByName(languageName);
        }
        if (engine == null) {
            throw LOG.getUnableToFindScriptEngineException(languageName);
        }
        return new ScriptEngineScriptEvaluator(engine);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ScriptEngineManager getScriptEngineManager() {
        if (this.scriptEngineManager == null) {
            DefaultScriptEvaluatorFactory defaultScriptEvaluatorFactory = this;
            synchronized (defaultScriptEvaluatorFactory) {
                if (this.scriptEngineManager == null) {
                    this.scriptEngineManager = new ScriptEngineManager(this.classLoader);
                }
            }
        }
        return this.scriptEngineManager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ScriptEngineManager getThreadContextClassLoaderScriptEngineManager() {
        if (this.threadContextClassLoaderScriptEngineManager == null) {
            DefaultScriptEvaluatorFactory defaultScriptEvaluatorFactory = this;
            synchronized (defaultScriptEvaluatorFactory) {
                if (this.threadContextClassLoaderScriptEngineManager == null) {
                    this.threadContextClassLoaderScriptEngineManager = new ScriptEngineManager(DefaultScriptEvaluatorFactory.run(GetClassLoader.fromContext()));
                }
            }
        }
        return this.threadContextClassLoaderScriptEngineManager;
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }
}

