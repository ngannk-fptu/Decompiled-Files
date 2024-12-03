/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.osgi.impl;

import com.hazelcast.osgi.impl.OSGiScriptEngineFactory;
import java.io.Reader;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

class OSGiScriptEngine
implements ScriptEngine {
    private ScriptEngine engine;
    private OSGiScriptEngineFactory factory;

    public OSGiScriptEngine(ScriptEngine engine, OSGiScriptEngineFactory factory) {
        this.engine = engine;
        this.factory = factory;
    }

    @Override
    public Bindings createBindings() {
        return this.engine.createBindings();
    }

    @Override
    public Object eval(Reader reader, Bindings n) throws ScriptException {
        return this.engine.eval(reader, n);
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        return this.engine.eval(reader, context);
    }

    @Override
    public Object eval(Reader reader) throws ScriptException {
        return this.engine.eval(reader);
    }

    @Override
    public Object eval(String script, Bindings n) throws ScriptException {
        return this.engine.eval(script, n);
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        return this.engine.eval(script, context);
    }

    @Override
    public Object eval(String script) throws ScriptException {
        return this.engine.eval(script);
    }

    @Override
    public Object get(String key) {
        return this.engine.get(key);
    }

    @Override
    public Bindings getBindings(int scope) {
        return this.engine.getBindings(scope);
    }

    @Override
    public ScriptContext getContext() {
        return this.engine.getContext();
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return this.factory;
    }

    @Override
    public void put(String key, Object value) {
        this.engine.put(key, value);
    }

    @Override
    public void setBindings(Bindings bindings, int scope) {
        this.engine.setBindings(bindings, scope);
    }

    @Override
    public void setContext(ScriptContext context) {
        this.engine.setContext(context);
    }
}

