/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EqualObjectGraphs;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;

public class ArrowFunction
extends BaseFunction {
    private static final long serialVersionUID = -7377989503697220633L;
    private final Callable targetFunction;
    private final Scriptable boundThis;

    public ArrowFunction(Context cx, Scriptable scope, Callable targetFunction, Scriptable boundThis) {
        this.targetFunction = targetFunction;
        this.boundThis = boundThis;
        ScriptRuntime.setFunctionProtoAndParent(this, scope);
        BaseFunction thrower = ScriptRuntime.typeErrorThrower(cx);
        NativeObject throwing = new NativeObject();
        throwing.put("get", (Scriptable)throwing, (Object)thrower);
        throwing.put("set", (Scriptable)throwing, (Object)thrower);
        throwing.put("enumerable", (Scriptable)throwing, (Object)Boolean.FALSE);
        throwing.put("configurable", (Scriptable)throwing, (Object)Boolean.FALSE);
        throwing.preventExtensions();
        this.defineOwnProperty(cx, "caller", throwing, false);
        this.defineOwnProperty(cx, "arguments", throwing, false);
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        Scriptable callThis = this.boundThis != null ? this.boundThis : ScriptRuntime.getTopCallScope(cx);
        return this.targetFunction.call(cx, scope, callThis, args);
    }

    @Override
    public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
        throw ScriptRuntime.typeErrorById("msg.not.ctor", this.decompile(0, 0));
    }

    @Override
    public boolean hasInstance(Scriptable instance) {
        if (this.targetFunction instanceof Function) {
            return ((Function)this.targetFunction).hasInstance(instance);
        }
        throw ScriptRuntime.typeErrorById("msg.not.ctor", new Object[0]);
    }

    @Override
    public int getLength() {
        if (this.targetFunction instanceof BaseFunction) {
            return ((BaseFunction)this.targetFunction).getLength();
        }
        return 0;
    }

    @Override
    public int getArity() {
        return this.getLength();
    }

    @Override
    String decompile(int indent, int flags) {
        if (this.targetFunction instanceof BaseFunction) {
            return ((BaseFunction)this.targetFunction).decompile(indent, flags);
        }
        return super.decompile(indent, flags);
    }

    static boolean equalObjectGraphs(ArrowFunction f1, ArrowFunction f2, EqualObjectGraphs eq) {
        return eq.equalGraphs(f1.boundThis, f2.boundThis) && eq.equalGraphs(f1.targetFunction, f2.targetFunction);
    }
}

