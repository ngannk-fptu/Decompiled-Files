/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.WeakHashMap;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.NativeMap;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolKey;
import org.mozilla.javascript.Undefined;

public class NativeWeakMap
extends IdScriptableObject {
    private static final long serialVersionUID = 8670434366883930453L;
    private static final Object MAP_TAG = "WeakMap";
    private boolean instanceOfWeakMap = false;
    private transient WeakHashMap<Scriptable, Object> map = new WeakHashMap();
    private static final Object NULL_VALUE = new Object();
    private static final int Id_constructor = 1;
    private static final int Id_delete = 2;
    private static final int Id_get = 3;
    private static final int Id_has = 4;
    private static final int Id_set = 5;
    private static final int SymbolId_toStringTag = 6;
    private static final int MAX_PROTOTYPE_ID = 6;

    static void init(Scriptable scope, boolean sealed) {
        NativeWeakMap m = new NativeWeakMap();
        m.exportAsJSClass(6, scope, sealed);
    }

    @Override
    public String getClassName() {
        return "WeakMap";
    }

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(MAP_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case 1: {
                if (thisObj == null) {
                    NativeWeakMap nm = new NativeWeakMap();
                    nm.instanceOfWeakMap = true;
                    if (args.length > 0) {
                        NativeMap.loadFromIterable(cx, scope, nm, NativeMap.key(args));
                    }
                    return nm;
                }
                throw ScriptRuntime.typeErrorById("msg.no.new", "WeakMap");
            }
            case 2: {
                return NativeWeakMap.realThis(thisObj, f).js_delete(NativeMap.key(args));
            }
            case 3: {
                return NativeWeakMap.realThis(thisObj, f).js_get(NativeMap.key(args));
            }
            case 4: {
                return NativeWeakMap.realThis(thisObj, f).js_has(NativeMap.key(args));
            }
            case 5: {
                return NativeWeakMap.realThis(thisObj, f).js_set(NativeMap.key(args), args.length > 1 ? args[1] : Undefined.instance);
            }
        }
        throw new IllegalArgumentException("WeakMap.prototype has no method: " + f.getFunctionName());
    }

    private Object js_delete(Object key) {
        if (!ScriptRuntime.isObject(key)) {
            return Boolean.FALSE;
        }
        return this.map.remove(key) != null;
    }

    private Object js_get(Object key) {
        if (!ScriptRuntime.isObject(key)) {
            return Undefined.instance;
        }
        Object result = this.map.get(key);
        if (result == null) {
            return Undefined.instance;
        }
        if (result == NULL_VALUE) {
            return null;
        }
        return result;
    }

    private Object js_has(Object key) {
        if (!ScriptRuntime.isObject(key)) {
            return Boolean.FALSE;
        }
        return this.map.containsKey(key);
    }

    private Object js_set(Object key, Object v) {
        if (!ScriptRuntime.isObject(key)) {
            throw ScriptRuntime.typeErrorById("msg.arg.not.object", ScriptRuntime.typeof(key));
        }
        Object value = v == null ? NULL_VALUE : v;
        this.map.put((Scriptable)key, value);
        return this;
    }

    private static NativeWeakMap realThis(Scriptable thisObj, IdFunctionObject f) {
        NativeWeakMap nm = NativeWeakMap.ensureType(thisObj, NativeWeakMap.class, f);
        if (!nm.instanceOfWeakMap) {
            throw ScriptRuntime.typeErrorById("msg.incompat.call", f.getFunctionName());
        }
        return nm;
    }

    @Override
    protected void initPrototypeId(int id) {
        String s;
        int arity;
        if (id == 6) {
            this.initPrototypeValue(6, SymbolKey.TO_STRING_TAG, (Object)this.getClassName(), 3);
            return;
        }
        String fnName = null;
        switch (id) {
            case 1: {
                arity = 0;
                s = "constructor";
                break;
            }
            case 2: {
                arity = 1;
                s = "delete";
                break;
            }
            case 3: {
                arity = 1;
                s = "get";
                break;
            }
            case 4: {
                arity = 1;
                s = "has";
                break;
            }
            case 5: {
                arity = 2;
                s = "set";
                break;
            }
            default: {
                throw new IllegalArgumentException(String.valueOf(id));
            }
        }
        this.initPrototypeMethod(MAP_TAG, id, s, fnName, arity);
    }

    @Override
    protected int findPrototypeId(Symbol k) {
        if (SymbolKey.TO_STRING_TAG.equals(k)) {
            return 6;
        }
        return 0;
    }

    @Override
    protected int findPrototypeId(String s) {
        int id;
        switch (s) {
            case "constructor": {
                id = 1;
                break;
            }
            case "delete": {
                id = 2;
                break;
            }
            case "get": {
                id = 3;
                break;
            }
            case "has": {
                id = 4;
                break;
            }
            case "set": {
                id = 5;
                break;
            }
            default: {
                id = 0;
            }
        }
        return id;
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.map = new WeakHashMap();
    }
}

