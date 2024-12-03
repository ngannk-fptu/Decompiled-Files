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

public class BoundFunction
extends BaseFunction {
    private static final long serialVersionUID = 2118137342826470729L;
    private final Callable targetFunction;
    private final Scriptable boundThis;
    private final Object[] boundArgs;
    private final int length;

    public BoundFunction(Context cx, Scriptable scope, Callable targetFunction, Scriptable boundThis, Object[] boundArgs) {
        this.targetFunction = targetFunction;
        this.boundThis = boundThis;
        this.boundArgs = boundArgs;
        this.length = targetFunction instanceof BaseFunction ? Math.max(0, ((BaseFunction)targetFunction).getLength() - boundArgs.length) : 0;
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
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] extraArgs) {
        Scriptable callThis = this.boundThis != null ? this.boundThis : ScriptRuntime.getTopCallScope(cx);
        return this.targetFunction.call(cx, scope, callThis, BoundFunction.concat(this.boundArgs, extraArgs));
    }

    @Override
    public Scriptable construct(Context cx, Scriptable scope, Object[] extraArgs) {
        if (this.targetFunction instanceof Function) {
            return ((Function)this.targetFunction).construct(cx, scope, BoundFunction.concat(this.boundArgs, extraArgs));
        }
        throw ScriptRuntime.typeErrorById("msg.not.ctor", new Object[0]);
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
        return this.length;
    }

    private static Object[] concat(Object[] first, Object[] second) {
        Object[] args = new Object[first.length + second.length];
        System.arraycopy(first, 0, args, 0, first.length);
        System.arraycopy(second, 0, args, first.length, second.length);
        return args;
    }

    static boolean equalObjectGraphs(BoundFunction f1, BoundFunction f2, EqualObjectGraphs eq) {
        return eq.equalGraphs(f1.boundThis, f2.boundThis) && eq.equalGraphs(f1.targetFunction, f2.targetFunction) && eq.equalGraphs(f1.boundArgs, f2.boundArgs);
    }
}

