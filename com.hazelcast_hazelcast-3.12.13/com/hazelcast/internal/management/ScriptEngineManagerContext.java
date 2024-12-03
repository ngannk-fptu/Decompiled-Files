/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management;

import com.hazelcast.util.Preconditions;
import javax.script.ScriptEngineManager;

public final class ScriptEngineManagerContext {
    private static volatile ScriptEngineManager scriptEngineManager = new ScriptEngineManager();

    private ScriptEngineManagerContext() {
    }

    public static ScriptEngineManager getScriptEngineManager() {
        return scriptEngineManager;
    }

    public static void setScriptEngineManager(ScriptEngineManager scriptEngineManager) {
        Preconditions.checkNotNull(scriptEngineManager, "ScriptEngineManager is required!");
        ScriptEngineManagerContext.scriptEngineManager = scriptEngineManager;
    }
}

