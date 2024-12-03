/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

final class NativeBoolean
extends IdScriptableObject {
    private static final long serialVersionUID = -3716996899943880933L;
    private static final Object BOOLEAN_TAG = "Boolean";
    private static final int Id_constructor = 1;
    private static final int Id_toString = 2;
    private static final int Id_toSource = 3;
    private static final int Id_valueOf = 4;
    private static final int MAX_PROTOTYPE_ID = 4;
    private boolean booleanValue;

    static void init(Scriptable scope, boolean sealed) {
        NativeBoolean obj = new NativeBoolean(false);
        obj.exportAsJSClass(4, scope, sealed);
    }

    NativeBoolean(boolean b) {
        this.booleanValue = b;
    }

    @Override
    public String getClassName() {
        return "Boolean";
    }

    @Override
    public Object getDefaultValue(Class<?> typeHint) {
        if (typeHint == ScriptRuntime.BooleanClass) {
            return ScriptRuntime.wrapBoolean(this.booleanValue);
        }
        return super.getDefaultValue(typeHint);
    }

    @Override
    protected void initPrototypeId(int id) {
        String s;
        int arity;
        switch (id) {
            case 1: {
                arity = 1;
                s = "constructor";
                break;
            }
            case 2: {
                arity = 0;
                s = "toString";
                break;
            }
            case 3: {
                arity = 0;
                s = "toSource";
                break;
            }
            case 4: {
                arity = 0;
                s = "valueOf";
                break;
            }
            default: {
                throw new IllegalArgumentException(String.valueOf(id));
            }
        }
        this.initPrototypeMethod(BOOLEAN_TAG, id, s, arity);
    }

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(BOOLEAN_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        if (id == 1) {
            boolean b;
            if (args.length == 0) {
                b = false;
            } else {
                boolean bl = b = args[0] instanceof ScriptableObject && ((ScriptableObject)args[0]).avoidObjectDetection() ? false : ScriptRuntime.toBoolean(args[0]);
            }
            if (thisObj == null) {
                return new NativeBoolean(b);
            }
            return ScriptRuntime.wrapBoolean(b);
        }
        boolean value = NativeBoolean.ensureType((Object)thisObj, NativeBoolean.class, (IdFunctionObject)f).booleanValue;
        switch (id) {
            case 2: {
                return value ? "true" : "false";
            }
            case 3: {
                return value ? "(new Boolean(true))" : "(new Boolean(false))";
            }
            case 4: {
                return ScriptRuntime.wrapBoolean(value);
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
            case "toString": {
                id = 2;
                break;
            }
            case "toSource": {
                id = 3;
                break;
            }
            case "valueOf": {
                id = 4;
                break;
            }
            default: {
                id = 0;
            }
        }
        return id;
    }
}

