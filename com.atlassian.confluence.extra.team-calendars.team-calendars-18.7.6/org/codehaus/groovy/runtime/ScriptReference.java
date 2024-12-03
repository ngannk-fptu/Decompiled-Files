/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Reference;
import groovy.lang.Script;

public class ScriptReference
extends Reference {
    private Script script;
    private String variable;

    public ScriptReference(Script script, String variable) {
        this.script = script;
        this.variable = variable;
    }

    public Object get() {
        return this.script.getBinding().getVariable(this.variable);
    }

    public void set(Object value) {
        this.script.getBinding().setVariable(this.variable, value);
    }
}

