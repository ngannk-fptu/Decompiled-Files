/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.util.Iterator;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Hashtable;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.IteratorLikeIterable;
import org.mozilla.javascript.NativeCollectionIterator;
import org.mozilla.javascript.NativeMap;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolKey;
import org.mozilla.javascript.Undefined;

public class NativeSet
extends IdScriptableObject {
    private static final long serialVersionUID = -8442212766987072986L;
    private static final Object SET_TAG = "Set";
    static final String ITERATOR_TAG = "Set Iterator";
    static final SymbolKey GETSIZE = new SymbolKey("[Symbol.getSize]");
    private final Hashtable entries = new Hashtable();
    private boolean instanceOfSet = false;
    private static final int Id_constructor = 1;
    private static final int Id_add = 2;
    private static final int Id_delete = 3;
    private static final int Id_has = 4;
    private static final int Id_clear = 5;
    private static final int Id_keys = 6;
    private static final int Id_values = 6;
    private static final int Id_entries = 7;
    private static final int Id_forEach = 8;
    private static final int SymbolId_getSize = 9;
    private static final int SymbolId_toStringTag = 10;
    private static final int MAX_PROTOTYPE_ID = 10;

    static void init(Context cx, Scriptable scope, boolean sealed) {
        NativeSet obj = new NativeSet();
        obj.exportAsJSClass(10, scope, false);
        ScriptableObject desc = (ScriptableObject)cx.newObject(scope);
        desc.put("enumerable", (Scriptable)desc, (Object)Boolean.FALSE);
        desc.put("configurable", (Scriptable)desc, (Object)Boolean.TRUE);
        desc.put("get", (Scriptable)desc, obj.get(GETSIZE, (Scriptable)obj));
        obj.defineOwnProperty(cx, "size", desc);
        if (sealed) {
            obj.sealObject();
        }
    }

    @Override
    public String getClassName() {
        return "Set";
    }

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(SET_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case 1: {
                if (thisObj == null) {
                    NativeSet ns = new NativeSet();
                    ns.instanceOfSet = true;
                    if (args.length > 0) {
                        NativeSet.loadFromIterable(cx, scope, ns, NativeMap.key(args));
                    }
                    return ns;
                }
                throw ScriptRuntime.typeErrorById("msg.no.new", "Set");
            }
            case 2: {
                return NativeSet.realThis(thisObj, f).js_add(NativeMap.key(args));
            }
            case 3: {
                return NativeSet.realThis(thisObj, f).js_delete(NativeMap.key(args));
            }
            case 4: {
                return NativeSet.realThis(thisObj, f).js_has(NativeMap.key(args));
            }
            case 5: {
                return NativeSet.realThis(thisObj, f).js_clear();
            }
            case 6: {
                return NativeSet.realThis(thisObj, f).js_iterator(scope, NativeCollectionIterator.Type.VALUES);
            }
            case 7: {
                return NativeSet.realThis(thisObj, f).js_iterator(scope, NativeCollectionIterator.Type.BOTH);
            }
            case 8: {
                return NativeSet.realThis(thisObj, f).js_forEach(cx, scope, NativeMap.key(args), args.length > 1 ? args[1] : Undefined.instance);
            }
            case 9: {
                return NativeSet.realThis(thisObj, f).js_getSize();
            }
        }
        throw new IllegalArgumentException("Set.prototype has no method: " + f.getFunctionName());
    }

    private Object js_add(Object k) {
        Object key = k;
        if (key instanceof Number && ((Number)key).doubleValue() == ScriptRuntime.negativeZero) {
            key = ScriptRuntime.zeroObj;
        }
        this.entries.put(key, key);
        return this;
    }

    private Object js_delete(Object arg) {
        return this.entries.deleteEntry(arg);
    }

    private Object js_has(Object arg) {
        return this.entries.has(arg);
    }

    private Object js_clear() {
        this.entries.clear();
        return Undefined.instance;
    }

    private Object js_getSize() {
        return this.entries.size();
    }

    private Object js_iterator(Scriptable scope, NativeCollectionIterator.Type type) {
        return new NativeCollectionIterator(scope, ITERATOR_TAG, type, this.entries.iterator());
    }

    private Object js_forEach(Context cx, Scriptable scope, Object arg1, Object arg2) {
        if (!(arg1 instanceof Callable)) {
            throw ScriptRuntime.notFunctionError(arg1);
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
            f.call(cx, scope, thisObj, new Object[]{e.value, e.value, this});
        }
        return Undefined.instance;
    }

    static void loadFromIterable(Context cx, Scriptable scope, ScriptableObject set, Object arg1) {
        if (arg1 == null || Undefined.instance.equals(arg1)) {
            return;
        }
        Object ito = ScriptRuntime.callIterator(arg1, cx, scope);
        if (Undefined.instance.equals(ito)) {
            return;
        }
        ScriptableObject dummy = NativeSet.ensureScriptableObject(cx.newObject(scope, set.getClassName()));
        Callable add = ScriptRuntime.getPropFunctionAndThis(dummy.getPrototype(), "add", cx, scope);
        ScriptRuntime.lastStoredScriptable(cx);
        try (IteratorLikeIterable it = new IteratorLikeIterable(cx, scope, ito);){
            for (Object val : it) {
                Object finalVal = val == Scriptable.NOT_FOUND ? Undefined.instance : val;
                add.call(cx, scope, set, new Object[]{finalVal});
            }
        }
    }

    private static NativeSet realThis(Scriptable thisObj, IdFunctionObject f) {
        NativeSet ns = NativeSet.ensureType(thisObj, NativeSet.class, f);
        if (!ns.instanceOfSet) {
            throw ScriptRuntime.typeErrorById("msg.incompat.call", f.getFunctionName());
        }
        return ns;
    }

    @Override
    protected void initPrototypeId(int id) {
        String s;
        int arity;
        switch (id) {
            case 9: {
                this.initPrototypeMethod(SET_TAG, id, GETSIZE, "get size", 0);
                return;
            }
            case 10: {
                this.initPrototypeValue(10, SymbolKey.TO_STRING_TAG, (Object)this.getClassName(), 3);
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
            case 5: {
                arity = 0;
                s = "clear";
                break;
            }
            case 7: {
                arity = 0;
                s = "entries";
                break;
            }
            case 6: {
                arity = 0;
                s = "values";
                break;
            }
            case 8: {
                arity = 1;
                s = "forEach";
                break;
            }
            default: {
                throw new IllegalArgumentException(String.valueOf(id));
            }
        }
        this.initPrototypeMethod(SET_TAG, id, s, fnName, arity);
    }

    @Override
    protected int findPrototypeId(Symbol k) {
        if (GETSIZE.equals(k)) {
            return 9;
        }
        if (SymbolKey.ITERATOR.equals(k)) {
            return 6;
        }
        if (SymbolKey.TO_STRING_TAG.equals(k)) {
            return 10;
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
            case "clear": {
                id = 5;
                break;
            }
            case "keys": {
                id = 6;
                break;
            }
            case "values": {
                id = 6;
                break;
            }
            case "entries": {
                id = 7;
                break;
            }
            case "forEach": {
                id = 8;
                break;
            }
            default: {
                id = 0;
            }
        }
        return id;
    }
}

