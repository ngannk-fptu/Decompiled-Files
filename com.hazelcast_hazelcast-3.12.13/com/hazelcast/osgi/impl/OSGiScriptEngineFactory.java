/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.osgi.impl;

import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

public class OSGiScriptEngineFactory
implements ScriptEngineFactory {
    private ScriptEngineFactory factory;
    private ClassLoader contextClassLoader;

    public OSGiScriptEngineFactory(ScriptEngineFactory factory, ClassLoader contextClassLoader) {
        this.factory = factory;
        this.contextClassLoader = contextClassLoader;
    }

    @Override
    public String getEngineName() {
        return this.factory.getEngineName();
    }

    @Override
    public String getEngineVersion() {
        return this.factory.getEngineVersion();
    }

    @Override
    public List<String> getExtensions() {
        return this.factory.getExtensions();
    }

    @Override
    public String getLanguageName() {
        return this.factory.getLanguageName();
    }

    @Override
    public String getLanguageVersion() {
        return this.factory.getLanguageVersion();
    }

    @Override
    public String getMethodCallSyntax(String obj, String m, String ... args) {
        return this.factory.getMethodCallSyntax(obj, m, args);
    }

    @Override
    public List<String> getMimeTypes() {
        return this.factory.getMimeTypes();
    }

    @Override
    public List<String> getNames() {
        return this.factory.getNames();
    }

    @Override
    public String getOutputStatement(String toDisplay) {
        return this.factory.getOutputStatement(toDisplay);
    }

    @Override
    public Object getParameter(String key) {
        return this.factory.getParameter(key);
    }

    @Override
    public String getProgram(String ... statements) {
        return this.factory.getProgram(statements);
    }

    @Override
    public ScriptEngine getScriptEngine() {
        ScriptEngine engine;
        if (this.contextClassLoader == null) {
            engine = this.factory.getScriptEngine();
        } else {
            Thread currentThread = Thread.currentThread();
            ClassLoader old = currentThread.getContextClassLoader();
            currentThread.setContextClassLoader(this.contextClassLoader);
            engine = this.factory.getScriptEngine();
            currentThread.setContextClassLoader(old);
        }
        return engine;
    }
}

