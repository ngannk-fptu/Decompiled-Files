/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.lookup;

import java.util.Objects;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.apache.commons.text.lookup.AbstractStringLookup;
import org.apache.commons.text.lookup.IllegalArgumentExceptions;

final class ScriptStringLookup
extends AbstractStringLookup {
    static final ScriptStringLookup INSTANCE = new ScriptStringLookup();

    private ScriptStringLookup() {
    }

    @Override
    public String lookup(String key) {
        if (key == null) {
            return null;
        }
        String[] keys = key.split(SPLIT_STR, 2);
        int keyLen = keys.length;
        if (keyLen != 2) {
            throw IllegalArgumentExceptions.format("Bad script key format [%s]; expected format is EngineName:Script.", key);
        }
        String engineName = keys[0];
        String script = keys[1];
        try {
            ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName(engineName);
            if (scriptEngine == null) {
                throw new IllegalArgumentException("No script engine named " + engineName);
            }
            return Objects.toString(scriptEngine.eval(script), null);
        }
        catch (Exception e) {
            throw IllegalArgumentExceptions.format(e, "Error in script engine [%s] evaluating script [%s].", engineName, script);
        }
    }
}

