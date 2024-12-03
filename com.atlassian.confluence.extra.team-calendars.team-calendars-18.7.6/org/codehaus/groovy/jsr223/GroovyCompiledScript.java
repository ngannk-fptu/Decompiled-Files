/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.jsr223;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;

public class GroovyCompiledScript
extends CompiledScript {
    private final GroovyScriptEngineImpl engine;
    private final Class<?> clasz;

    public GroovyCompiledScript(GroovyScriptEngineImpl engine, Class<?> clazz) {
        this.engine = engine;
        this.clasz = clazz;
    }

    @Override
    public Object eval(ScriptContext context) throws ScriptException {
        return this.engine.eval(this.clasz, context);
    }

    @Override
    public ScriptEngine getEngine() {
        return this.engine;
    }
}

