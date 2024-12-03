/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.util.Iterator;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Delegator;
import org.mozilla.javascript.Hashtable;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.NativeCollectionIterator;
import org.mozilla.javascript.NativeSet;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolKey;
import org.mozilla.javascript.Undefined;

public class NativeMap
extends IdScriptableObject {
    private static final long serialVersionUID = 1171922614280016891L;
    private static final Object MAP_TAG = "Map";
    static final String ITERATOR_TAG = "Map Iterator";
    private final Hashtable entries = new Hashtable();
    private boolean instanceOfMap = false;
    private static final int Id_constructor = 1;
    private static final int Id_set = 2;
    private static final int Id_get = 3;
    private static final int Id_delete = 4;
    private static final int Id_has = 5;
    private static final int Id_clear = 6;
    private static final int Id_keys = 7;
    private static final int Id_values = 8;
    private static final int Id_entries = 9;
    private static final int Id_forEach = 10;
    private static final int SymbolId_getSize = 11;
    private static final int SymbolId_toStringTag = 12;
    private static final int MAX_PROTOTYPE_ID = 12;

    static void init(Context cx, Scriptable scope, boolean sealed) {
        NativeMap obj = new NativeMap();
        obj.exportAsJSClass(12, scope, false);
        ScriptableObject desc = (ScriptableObject)cx.newObject(scope);
        desc.put("enumerable", (Scriptable)desc, (Object)Boolean.FALSE);
        desc.put("configurable", (Scriptable)desc, (Object)Boolean.TRUE);
        desc.put("get", (Scriptable)desc, obj.get(NativeSet.GETSIZE, (Scriptable)obj));
        obj.defineOwnProperty(cx, "size", desc);
        if (sealed) {
            obj.sealObject();
        }
    }

    @Override
    public String getClassName() {
        return "Map";
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
                    NativeMap nm = new NativeMap();
                    nm.instanceOfMap = true;
                    if (args.length > 0) {
                        NativeMap.loadFromIterable(cx, scope, nm, NativeMap.key(args));
                    }
                    return nm;
                }
                throw ScriptRuntime.typeErrorById("msg.no.new", "Map");
            }
            case 2: {
                return NativeMap.realThis(thisObj, f).js_set(NativeMap.key(args), args.length > 1 ? args[1] : Undefined.instance);
            }
            case 4: {
                return NativeMap.realThis(thisObj, f).js_delete(NativeMap.key(args));
            }
            case 3: {
                return NativeMap.realThis(thisObj, f).js_get(NativeMap.key(args));
            }
            case 5: {
                return NativeMap.realThis(thisObj, f).js_has(NativeMap.key(args));
            }
            case 6: {
                return NativeMap.realThis(thisObj, f).js_clear();
            }
            case 7: {
                return NativeMap.realThis(thisObj, f).js_iterator(scope, NativeCollectionIterator.Type.KEYS);
            }
            case 8: {
                return NativeMap.realThis(thisObj, f).js_iterator(scope, NativeCollectionIterator.Type.VALUES);
            }
            case 9: {
                return NativeMap.realThis(thisObj, f).js_iterator(scope, NativeCollectionIterator.Type.BOTH);
            }
            case 10: {
                return NativeMap.realThis(thisObj, f).js_forEach(cx, scope, args.length > 0 ? args[0] : Undefined.instance, args.length > 1 ? args[1] : Undefined.instance);
            }
            case 11: {
                return NativeMap.realThis(thisObj, f).js_getSize();
            }
        }
        throw new IllegalArgumentException("Map.prototype has no method: " + f.getFunctionName());
    }

    private Object js_set(Object k, Object v) {
        Object key = k;
        if (key instanceof Number && ((Number)key).doubleValue() == ScriptRuntime.negativeZero) {
            key = ScriptRuntime.zeroObj;
        }
        this.entries.put(key, v);
        return this;
    }

    private Object js_delete(Object arg) {
        return this.entries.deleteEntry(arg);
    }

    private Object js_get(Object arg) {
        Hashtable.Entry entry = this.entries.getEntry(arg);
        if (entry == null) {
            return Undefined.instance;
        }
        return entry.value;
    }

    private Object js_has(Object arg) {
        return this.entries.has(arg);
    }

    private Object js_getSize() {
        return this.entries.size();
    }

    private Object js_iterator(Scriptable scope, NativeCollectionIterator.Type type) {
        return new NativeCollectionIterator(scope, ITERATOR_TAG, type, this.entries.iterator());
    }

    private Object js_clear() {
        this.entries.clear();
        return Undefined.instance;
    }

    private Object js_forEach(Context cx, Scriptable scope, Object arg1, Object arg2) {
        if (!(arg1 instanceof Callable)) {
            throw ScriptRuntime.typeErrorById("msg.isnt.function", arg1, ScriptRuntime.typeof(arg1));
        }
        Callable f = (Callable)arg1;
        boolean isStrict = cx.isStrictMode();
        Iterator<Hashtable.Entry> i = this.entries.iterator();
        while (i.hasNext()) {
            Scriptable thisObj = ScriptRuntime.toObjectOrNull(cx, arg2, scope);
            if (thisObj == null && !isStrict) {
                thisObj = scope;
            }
            if (thisObj == null) {
                thisObj = Undefined.SCRIPTABLE_UNDEFINED;
            }
            Hashtable.Entry e = i.next();
            f.call(cx, scope, thisObj, new Object[]{e.value, e.key, this});
        }
        return Undefined.instance;
    }

    static void loadFromIterable(Context cx, Scriptable scope, ScriptableObject map, Object arg1) {
        if (arg1 == null || Undefined.instance.equals(arg1)) {
            return;
        }
        Object ito = ScriptRuntime.callIterator(arg1, cx, scope);
        if (Undefined.instance.equals(ito)) {
            return;
        }
        Scriptable proto = ScriptableObject.getClassPrototype(scope, map.getClassName());
        Callable set = ScriptRuntime.getPropFunctionAndThis(proto, "set", cx, scope);
        ScriptRuntime.lastStoredScriptable(cx);
        ScriptRuntime.loadFromIterable(cx, scope, arg1, (Object key, Object value) -> set.call(cx, scope, map, new Object[]{key, value}));
    }

    private static NativeMap realThis(Scriptable thisObj, IdFunctionObject f) {
        NativeMap nm = NativeMap.ensureType(thisObj, NativeMap.class, f);
        if (!nm.instanceOfMap) {
            throw ScriptRuntime.typeErrorById("msg.incompat.call", f.getFunctionName());
        }
        return nm;
    }

    @Override
    protected void initPrototypeId(int id) {
        String s;
        int arity;
        switch (id) {
            case 11: {
                this.initPrototypeMethod(MAP_TAG, id, NativeSet.GETSIZE, "get size", 0);
                return;
            }
            case 12: {
                this.initPrototypeValue(12, SymbolKey.TO_STRING_TAG, (Object)this.getClassName(), 3);
                return;
            }
        }
        String fnName = null;
        switch (id) {
            case 1: {
                arity = 0;
                s = "constructor";
                break;
            }
            case 2: {
                arity = 2;
                s = "set";
                break;
            }
            case 3: {
                arity = 1;
                s = "get";
                break;
            }
            case 4: {
                arity = 1;
                s = "delete";
                break;
            }
            case 5: {
                arity = 1;
                s = "has";
                break;
            }
            case 6: {
                arity = 0;
                s = "clear";
                break;
            }
            case 7: {
                arity = 0;
                s = "keys";
                break;
            }
            case 8: {
                arity = 0;
                s = "values";
                break;
            }
            case 9: {
                arity = 0;
                s = "entries";
                break;
            }
            case 10: {
                arity = 1;
                s = "forEach";
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
        if (NativeSet.GETSIZE.equals(k)) {
            return 11;
        }
        if (SymbolKey.ITERATOR.equals(k)) {
            return 9;
        }
        if (SymbolKey.TO_STRING_TAG.equals(k)) {
            return 12;
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
            case "set": {
                id = 2;
                break;
            }
            case "get": {
                id = 3;
                break;
            }
            case "delete": {
                id = 4;
                break;
            }
            case "has": {
                id = 5;
                break;
            }
            case "clear": {
                id = 6;
                break;
            }
            case "keys": {
                id = 7;
                break;
            }
            case "values": {
                id = 8;
                break;
            }
            case "entries": {
                id = 9;
                break;
            }
            case "forEach": {
                id = 10;
                break;
            }
            default: {
                id = 0;
            }
        }
        return id;
    }

    static Object key(Object[] args) {
        if (args.length > 0) {
            Object key = args[0];
            if (key instanceof Delegator) {
                return ((Delegator)key).getDelegee();
            }
            return key;
        }
        return Undefined.instance;
    }
}

