/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scripting.support;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;

public abstract class StandardScriptUtils {
    public static ScriptEngine retrieveEngineByName(ScriptEngineManager scriptEngineManager, String engineName) {
        ScriptEngine engine = scriptEngineManager.getEngineByName(engineName);
        if (engine == null) {
            LinkedHashSet<String> engineNames = new LinkedHashSet<String>();
            for (ScriptEngineFactory engineFactory : scriptEngineManager.getEngineFactories()) {
                List<String> factoryNames = engineFactory.getNames();
                if (factoryNames.contains(engineName)) {
                    try {
                        engine = engineFactory.getScriptEngine();
                        engine.setBindings(scriptEngineManager.getBindings(), 200);
                    }
                    catch (Throwable ex) {
                        throw new IllegalStateException("Script engine with name '" + engineName + "' failed to initialize", ex);
                    }
                }
                engineNames.addAll(factoryNames);
            }
            throw new IllegalArgumentException("Script engine with name '" + engineName + "' not found; registered engine names: " + engineNames);
        }
        return engine;
    }

    static Bindings getBindings(Map<String, Object> bindings) {
        return bindings instanceof Bindings ? (Bindings)bindings : new SimpleBindings(bindings);
    }
}

