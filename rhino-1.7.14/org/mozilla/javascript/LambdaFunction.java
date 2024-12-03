/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;

public class LambdaFunction
extends BaseFunction {
    private static final long serialVersionUID = -8388132362854748293L;
    private final transient Callable target;
    private final String name;
    private final int length;

    public LambdaFunction(Scriptable scope, String name, int length, Callable target) {
        this.target = target;
        this.name = name;
        this.length = length;
        ScriptRuntime.setFunctionProtoAndParent(this, scope);
        this.setupDefaultPrototype();
    }

    public LambdaFunction(Scriptable scope, int length, Callable target) {
        this.target = target;
        this.length = length;
        this.name = "";
        ScriptRuntime.setFunctionProtoAndParent(this, scope);
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        return this.target.call(cx, scope, thisObj, args);
    }

    @Override
    public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
        throw ScriptRuntime.typeErrorById("msg.no.new", this.getFunctionName());
    }

    @Override
    public int getLength() {
        return this.length;
    }

    @Override
    public int getArity() {
        return this.length;
    }

    @Override
    public String getFunctionName() {
        return this.name;
    }
}

