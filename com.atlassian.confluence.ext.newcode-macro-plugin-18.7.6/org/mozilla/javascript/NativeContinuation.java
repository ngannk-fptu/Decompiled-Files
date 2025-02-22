/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.util.Objects;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.Interpreter;
import org.mozilla.javascript.Scriptable;

public final class NativeContinuation
extends IdScriptableObject
implements Function {
    private static final long serialVersionUID = 1794167133757605367L;
    private static final Object FTAG = "Continuation";
    private Object implementation;
    private static final int Id_constructor = 1;
    private static final int MAX_PROTOTYPE_ID = 1;

    public static void init(Context cx, Scriptable scope, boolean sealed) {
        NativeContinuation obj = new NativeContinuation();
        obj.exportAsJSClass(1, scope, sealed);
    }

    public Object getImplementation() {
        return this.implementation;
    }

    public void initImplementation(Object implementation) {
        this.implementation = implementation;
    }

    @Override
    public String getClassName() {
        return "Continuation";
    }

    @Override
    public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
        throw Context.reportRuntimeError("Direct call is not supported");
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        return Interpreter.restartContinuation(this, cx, scope, args);
    }

    public static boolean isContinuationConstructor(IdFunctionObject f) {
        return f.hasTag(FTAG) && f.methodId() == 1;
    }

    public static boolean equalImplementations(NativeContinuation c1, NativeContinuation c2) {
        return Objects.equals(c1.implementation, c2.implementation);
    }

    @Override
    protected void initPrototypeId(int id) {
        String s;
        int arity;
        switch (id) {
            case 1: {
                arity = 0;
                s = "constructor";
                break;
            }
            default: {
                throw new IllegalArgumentException(String.valueOf(id));
            }
        }
        this.initPrototypeMethod(FTAG, id, s, arity);
    }

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(FTAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case 1: {
                throw Context.reportRuntimeError("Direct call is not supported");
            }
        }
        throw new IllegalArgumentException(String.valueOf(id));
    }

    @Override
    protected int findPrototypeId(String s) {
        int id;
        switch (s) {
            case "constructor": {
                id = 1;
                break;
            }
            default: {
                id = 0;
            }
        }
        return id;
    }
}

