/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.jsr223;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ScriptStaticExtensions {
    public static ScriptEngine $static_propertyMissing(ScriptEngineManager self, String languageShortName) {
        ScriptEngineManager manager = new ScriptEngineManager();
        return manager.getEngineByName(languageShortName);
    }
}

