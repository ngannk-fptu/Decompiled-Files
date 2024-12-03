/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.jsr223;

import groovy.lang.Binding;
import java.io.Reader;
import java.util.Map;
import java.util.Set;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class ScriptExtensions {
    public static Object eval(ScriptEngine self, String script, Binding binding) throws ScriptException {
        ScriptExtensions.storeBindingVars(self, binding);
        Object result = self.eval(script);
        ScriptExtensions.retrieveBindingVars(self, binding);
        return result;
    }

    public static Object eval(ScriptEngine self, Reader reader, Binding binding) throws ScriptException {
        ScriptExtensions.storeBindingVars(self, binding);
        Object result = self.eval(reader);
        ScriptExtensions.retrieveBindingVars(self, binding);
        return result;
    }

    private static void retrieveBindingVars(ScriptEngine self, Binding binding) {
        Set returnVars = self.getBindings(100).entrySet();
        for (Map.Entry me : returnVars) {
            binding.setVariable((String)me.getKey(), me.getValue());
        }
    }

    private static void storeBindingVars(ScriptEngine self, Binding binding) {
        Set vars = binding.getVariables().entrySet();
        for (Map.Entry me : vars) {
            self.put(me.getKey().toString(), me.getValue());
        }
    }
}

