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
import org.mozilla.javascript.NativeSet;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolKey;

public class NativeWeakSet
extends IdScriptableObject {
    private static final long serialVersionUID = 2065753364224029534L;
    private static final Object MAP_TAG = "WeakSet";
    private boolean instanceOfWeakSet = false;
    private transient WeakHashMap<Scriptable, Boolean> map = new WeakHashMap();
    private static final int Id_constructor = 1;
    private static final int Id_add = 2;
    private static final int Id_delete = 3;
    private static final int Id_has = 4;
    private static final int SymbolId_toStringTag = 5;
    private static final int MAX_PROTOTYPE_ID = 5;

    static void init(Scriptable scope, boolean sealed) {
        NativeWeakSet m = new NativeWeakSet();
        m.exportAsJSClass(5, scope, sealed);
    }

    @Override
    public String getClassName() {
        return "WeakSet";
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
                    NativeWeakSet ns = new NativeWeakSet();
                    ns.instanceOfWeakSet = true;
                    if (args.length > 0) {
                        NativeSet.loadFromIterable(cx, scope, ns, NativeMap.key(args));
                    }
                    return ns;
                }
                throw ScriptRuntime.typeErrorById("msg.no.new", "WeakSet");
            }
            case 2: {
                return NativeWeakSet.realThis(thisObj, f).js_add(NativeMap.key(args));
            }
            case 3: {
                return NativeWeakSet.realThis(thisObj, f).js_delete(NativeMap.key(args));
            }
            case 4: {
                return NativeWeakSet.realThis(thisObj, f).js_has(NativeMap.key(args));
            }
        }
        throw new IllegalArgumentException("WeakMap.prototype has no method: " + f.getFunctionName());
    }

    private Object js_add(Object key) {
        if (!ScriptRuntime.isObject(key)) {
            throw ScriptRuntime.typeErrorById("msg.arg.not.object", ScriptRuntime.typeof(key));
        }
        this.map.put((Scriptable)key, Boolean.TRUE);
        return this;
    }

    private Object js_delete(Object key) {
        if (!ScriptRuntime.isObject(key)) {
            return Boolean.FALSE;
        }
        return this.map.remove(key) != null;
    }

    private Object js_has(Object key) {
        if (!ScriptRuntime.isObject(key)) {
            return Boolean.FALSE;
        }
        return this.map.containsKey(key);
    }

    private static NativeWeakSet realThis(Scriptable thisObj, IdFunctionObject f) {
        NativeWeakSet ns = NativeWeakSet.ensureType(thisObj, NativeWeakSet.class, f);
        if (!ns.instanceOfWeakSet) {
            throw ScriptRuntime.typeErrorById("msg.incompat.call", f.getFunctionName());
        }
        return ns;
    }

    @Override
    protected void initPrototypeId(int id) {
        String s;
        int arity;
        if (id == 5) {
            this.initPrototypeValue(5, SymbolKey.TO_STRING_TAG, (Object)this.getClassName(), 3);
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
                s = "add";
                break;
            }
            case 3: {
                arity = 1;
                s = "delete";
                break;
            }
            case 4: {
                arity = 1;
                s = "has";
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
            return 5;
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
            case "add": {
                id = 2;
                break;
            }
            case "delete": {
                id = 3;
                break;
            }
            case "has": {
                id = 4;
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

