/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.mozilla.javascript.AbstractEcmaObjectOperations;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.ScriptRuntimeES6;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolScriptable;
import org.mozilla.javascript.Undefined;

public class NativeObject
extends IdScriptableObject
implements Map {
    private static final long serialVersionUID = -6345305608474346996L;
    private static final Object OBJECT_TAG = "Object";
    private static final int ConstructorId_getPrototypeOf = -1;
    private static final int ConstructorId_keys = -2;
    private static final int ConstructorId_getOwnPropertyNames = -3;
    private static final int ConstructorId_getOwnPropertyDescriptor = -4;
    private static final int ConstructorId_defineProperty = -5;
    private static final int ConstructorId_isExtensible = -6;
    private static final int ConstructorId_preventExtensions = -7;
    private static final int ConstructorId_defineProperties = -8;
    private static final int ConstructorId_create = -9;
    private static final int ConstructorId_isSealed = -10;
    private static final int ConstructorId_isFrozen = -11;
    private static final int ConstructorId_seal = -12;
    private static final int ConstructorId_freeze = -13;
    private static final int ConstructorId_getOwnPropertySymbols = -14;
    private static final int ConstructorId_assign = -15;
    private static final int ConstructorId_is = -16;
    private static final int ConstructorId_setPrototypeOf = -17;
    private static final int ConstructorId_entries = -18;
    private static final int ConstructorId_fromEntries = -19;
    private static final int ConstructorId_values = -20;
    private static final int Id_constructor = 1;
    private static final int Id_toString = 2;
    private static final int Id_toLocaleString = 3;
    private static final int Id_valueOf = 4;
    private static final int Id_hasOwnProperty = 5;
    private static final int Id_propertyIsEnumerable = 6;
    private static final int Id_isPrototypeOf = 7;
    private static final int Id_toSource = 8;
    private static final int Id___defineGetter__ = 9;
    private static final int Id___defineSetter__ = 10;
    private static final int Id___lookupGetter__ = 11;
    private static final int Id___lookupSetter__ = 12;
    private static final int MAX_PROTOTYPE_ID = 12;

    static void init(Scriptable scope, boolean sealed) {
        NativeObject obj = new NativeObject();
        obj.exportAsJSClass(12, scope, sealed);
    }

    @Override
    public String getClassName() {
        return "Object";
    }

    public String toString() {
        return ScriptRuntime.defaultObjectToString(this);
    }

    @Override
    protected void fillConstructorProperties(IdFunctionObject ctor) {
        this.addIdFunctionProperty(ctor, OBJECT_TAG, -1, "getPrototypeOf", 1);
        if (Context.getCurrentContext().version >= 200) {
            this.addIdFunctionProperty(ctor, OBJECT_TAG, -17, "setPrototypeOf", 2);
            this.addIdFunctionProperty(ctor, OBJECT_TAG, -18, "entries", 1);
            this.addIdFunctionProperty(ctor, OBJECT_TAG, -19, "fromEntries", 1);
            this.addIdFunctionProperty(ctor, OBJECT_TAG, -20, "values", 1);
        }
        this.addIdFunctionProperty(ctor, OBJECT_TAG, -2, "keys", 1);
        this.addIdFunctionProperty(ctor, OBJECT_TAG, -3, "getOwnPropertyNames", 1);
        this.addIdFunctionProperty(ctor, OBJECT_TAG, -14, "getOwnPropertySymbols", 1);
        this.addIdFunctionProperty(ctor, OBJECT_TAG, -4, "getOwnPropertyDescriptor", 2);
        this.addIdFunctionProperty(ctor, OBJECT_TAG, -5, "defineProperty", 3);
        this.addIdFunctionProperty(ctor, OBJECT_TAG, -6, "isExtensible", 1);
        this.addIdFunctionProperty(ctor, OBJECT_TAG, -7, "preventExtensions", 1);
        this.addIdFunctionProperty(ctor, OBJECT_TAG, -8, "defineProperties", 2);
        this.addIdFunctionProperty(ctor, OBJECT_TAG, -9, "create", 2);
        this.addIdFunctionProperty(ctor, OBJECT_TAG, -10, "isSealed", 1);
        this.addIdFunctionProperty(ctor, OBJECT_TAG, -11, "isFrozen", 1);
        this.addIdFunctionProperty(ctor, OBJECT_TAG, -12, "seal", 1);
        this.addIdFunctionProperty(ctor, OBJECT_TAG, -13, "freeze", 1);
        this.addIdFunctionProperty(ctor, OBJECT_TAG, -15, "assign", 2);
        this.addIdFunctionProperty(ctor, OBJECT_TAG, -16, "is", 2);
        super.fillConstructorProperties(ctor);
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
                s = "toLocaleString";
                break;
            }
            case 4: {
                arity = 0;
                s = "valueOf";
                break;
            }
            case 5: {
                arity = 1;
                s = "hasOwnProperty";
                break;
            }
            case 6: {
                arity = 1;
                s = "propertyIsEnumerable";
                break;
            }
            case 7: {
                arity = 1;
                s = "isPrototypeOf";
                break;
            }
            case 8: {
                arity = 0;
                s = "toSource";
                break;
            }
            case 9: {
                arity = 2;
                s = "__defineGetter__";
                break;
            }
            case 10: {
                arity = 2;
                s = "__defineSetter__";
                break;
            }
            case 11: {
                arity = 1;
                s = "__lookupGetter__";
                break;
            }
            case 12: {
                arity = 1;
                s = "__lookupSetter__";
                break;
            }
            default: {
                throw new IllegalArgumentException(String.valueOf(id));
            }
        }
        this.initPrototypeMethod(OBJECT_TAG, id, s, arity);
    }

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(OBJECT_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case 1: {
                if (thisObj != null) {
                    return f.construct(cx, scope, args);
                }
                if (args.length == 0 || args[0] == null || Undefined.isUndefined(args[0])) {
                    return new NativeObject();
                }
                return ScriptRuntime.toObject(cx, scope, args[0]);
            }
            case 3: {
                if (thisObj == null) {
                    throw ScriptRuntime.notFunctionError(null);
                }
                Object toString = ScriptableObject.getProperty(thisObj, "toString");
                if (!(toString instanceof Callable)) {
                    throw ScriptRuntime.notFunctionError(toString);
                }
                Callable fun = (Callable)toString;
                return fun.call(cx, scope, thisObj, ScriptRuntime.emptyArgs);
            }
            case 2: {
                if (cx.hasFeature(4)) {
                    String s = ScriptRuntime.defaultObjectToSource(cx, scope, thisObj, args);
                    int L = s.length();
                    if (L != 0 && s.charAt(0) == '(' && s.charAt(L - 1) == ')') {
                        s = s.substring(1, L - 1);
                    }
                    return s;
                }
                return ScriptRuntime.defaultObjectToString(thisObj);
            }
            case 4: {
                if (cx.getLanguageVersion() >= 180 && (thisObj == null || Undefined.isUndefined(thisObj))) {
                    throw ScriptRuntime.typeErrorById("msg." + (thisObj == null ? "null" : "undef") + ".to.object", new Object[0]);
                }
                return thisObj;
            }
            case 5: {
                boolean result;
                Object arg;
                if (cx.getLanguageVersion() >= 180 && (thisObj == null || Undefined.isUndefined(thisObj))) {
                    throw ScriptRuntime.typeErrorById("msg." + (thisObj == null ? "null" : "undef") + ".to.object", new Object[0]);
                }
                Object object = arg = args.length < 1 ? Undefined.instance : args[0];
                if (arg instanceof Symbol) {
                    result = NativeObject.ensureSymbolScriptable(thisObj).has((Symbol)arg, thisObj);
                } else {
                    ScriptRuntime.StringIdOrIndex s = ScriptRuntime.toStringIdOrIndex(cx, arg);
                    result = s.stringId == null ? thisObj.has(s.index, thisObj) : thisObj.has(s.stringId, thisObj);
                }
                return ScriptRuntime.wrapBoolean(result);
            }
            case 6: {
                boolean result;
                Object arg;
                if (cx.getLanguageVersion() >= 180 && (thisObj == null || Undefined.isUndefined(thisObj))) {
                    throw ScriptRuntime.typeErrorById("msg." + (thisObj == null ? "null" : "undef") + ".to.object", new Object[0]);
                }
                Object object = arg = args.length < 1 ? Undefined.instance : args[0];
                if (arg instanceof Symbol) {
                    result = ((SymbolScriptable)((Object)thisObj)).has((Symbol)arg, thisObj);
                    result = result && this.isEnumerable((Symbol)arg, (Object)thisObj);
                } else {
                    ScriptRuntime.StringIdOrIndex s = ScriptRuntime.toStringIdOrIndex(cx, arg);
                    try {
                        result = s.stringId == null ? (result = thisObj.has(s.index, thisObj)) && this.isEnumerable(s.index, (Object)thisObj) : (result = thisObj.has(s.stringId, thisObj)) && this.isEnumerable(s.stringId, (Object)thisObj);
                    }
                    catch (EvaluatorException ee) {
                        Object[] objectArray = new Object[1];
                        Object object2 = objectArray[0] = s.stringId == null ? Integer.toString(s.index) : s.stringId;
                        if (ee.getMessage().startsWith(ScriptRuntime.getMessageById("msg.prop.not.found", objectArray))) {
                            result = false;
                        }
                        throw ee;
                    }
                }
                return ScriptRuntime.wrapBoolean(result);
            }
            case 7: {
                if (cx.getLanguageVersion() >= 180 && (thisObj == null || Undefined.isUndefined(thisObj))) {
                    throw ScriptRuntime.typeErrorById("msg." + (thisObj == null ? "null" : "undef") + ".to.object", new Object[0]);
                }
                boolean result = false;
                if (args.length != 0 && args[0] instanceof Scriptable) {
                    Scriptable v = (Scriptable)args[0];
                    do {
                        if ((v = v.getPrototype()) != thisObj) continue;
                        result = true;
                        break;
                    } while (v != null);
                }
                return ScriptRuntime.wrapBoolean(result);
            }
            case 8: {
                return ScriptRuntime.defaultObjectToSource(cx, scope, thisObj, args);
            }
            case 9: 
            case 10: {
                if (args.length < 2 || !(args[1] instanceof Callable)) {
                    Object badArg = args.length >= 2 ? args[1] : Undefined.instance;
                    throw ScriptRuntime.notFunctionError(badArg);
                }
                if (!(thisObj instanceof ScriptableObject)) {
                    throw Context.reportRuntimeErrorById("msg.extend.scriptable", thisObj == null ? "null" : thisObj.getClass().getName(), String.valueOf(args[0]));
                }
                ScriptableObject so = (ScriptableObject)thisObj;
                ScriptRuntime.StringIdOrIndex s = ScriptRuntime.toStringIdOrIndex(cx, args[0]);
                int index = s.stringId != null ? 0 : s.index;
                Callable getterOrSetter = (Callable)args[1];
                boolean isSetter = id == 10;
                so.setGetterOrSetter(s.stringId, index, getterOrSetter, isSetter);
                if (so instanceof NativeArray) {
                    ((NativeArray)so).setDenseOnly(false);
                }
                return Undefined.instance;
            }
            case 11: 
            case 12: {
                Scriptable v;
                Object gs;
                boolean isSetter;
                if (args.length < 1 || !(thisObj instanceof ScriptableObject)) {
                    return Undefined.instance;
                }
                ScriptableObject so = (ScriptableObject)thisObj;
                ScriptRuntime.StringIdOrIndex s = ScriptRuntime.toStringIdOrIndex(cx, args[0]);
                int index = s.stringId != null ? 0 : s.index;
                boolean bl = isSetter = id == 12;
                while ((gs = so.getGetterOrSetter(s.stringId, index, this, isSetter)) == null && (v = so.getPrototype()) != null && v instanceof ScriptableObject) {
                    so = (ScriptableObject)v;
                }
                if (gs != null) {
                    return gs;
                }
                return Undefined.instance;
            }
            case -1: {
                Object arg = args.length < 1 ? Undefined.instance : args[0];
                Scriptable obj = NativeObject.getCompatibleObject(cx, scope, arg);
                return obj.getPrototype();
            }
            case -17: {
                Scriptable proto;
                if (args.length < 2) {
                    throw ScriptRuntime.typeErrorById("msg.method.missing.parameter", "Object.setPrototypeOf", "2", Integer.toString(args.length));
                }
                Scriptable scriptable = proto = args[1] == null ? null : NativeObject.ensureScriptable(args[1]);
                if (proto instanceof Symbol) {
                    throw ScriptRuntime.typeErrorById("msg.arg.not.object", ScriptRuntime.typeof(proto));
                }
                Object arg0 = args[0];
                if (cx.getLanguageVersion() >= 200) {
                    ScriptRuntimeES6.requireObjectCoercible(cx, arg0, f);
                }
                if (!(arg0 instanceof ScriptableObject)) {
                    return arg0;
                }
                ScriptableObject obj = (ScriptableObject)arg0;
                if (!obj.isExtensible()) {
                    throw ScriptRuntime.typeErrorById("msg.not.extensible", new Object[0]);
                }
                for (Scriptable prototypeProto = proto; prototypeProto != null; prototypeProto = prototypeProto.getPrototype()) {
                    if (prototypeProto != obj) continue;
                    throw ScriptRuntime.typeErrorById("msg.object.cyclic.prototype", obj.getClass().getSimpleName());
                }
                obj.setPrototype(proto);
                return obj;
            }
            case -2: {
                Object arg = args.length < 1 ? Undefined.instance : args[0];
                Scriptable obj = NativeObject.getCompatibleObject(cx, scope, arg);
                Object[] ids = obj.getIds();
                for (int i = 0; i < ids.length; ++i) {
                    ids[i] = ScriptRuntime.toString(ids[i]);
                }
                return cx.newArray(scope, ids);
            }
            case -18: {
                Object arg = args.length < 1 ? Undefined.instance : args[0];
                Scriptable obj = NativeObject.getCompatibleObject(cx, scope, arg);
                Object[] ids = obj.getIds();
                int j = 0;
                for (int i = 0; i < ids.length; ++i) {
                    if (ids[i] instanceof Integer) {
                        int intId = (Integer)ids[i];
                        if (!obj.has(intId, obj) || !this.isEnumerable(intId, (Object)obj)) continue;
                        String stringId = ScriptRuntime.toString(ids[i]);
                        Object[] entry = new Object[]{stringId, obj.get(intId, obj)};
                        ids[j++] = cx.newArray(scope, entry);
                        continue;
                    }
                    String stringId = ScriptRuntime.toString(ids[i]);
                    if (!obj.has(stringId, obj) || !this.isEnumerable(stringId, (Object)obj)) continue;
                    Object[] entry = new Object[]{stringId, obj.get(stringId, obj)};
                    ids[j++] = cx.newArray(scope, entry);
                }
                if (j != ids.length) {
                    ids = Arrays.copyOf(ids, j);
                }
                return cx.newArray(scope, ids);
            }
            case -19: {
                Object arg = args.length < 1 ? Undefined.instance : args[0];
                arg = NativeObject.getCompatibleObject(cx, scope, arg);
                Scriptable obj = cx.newObject(scope);
                ScriptRuntime.loadFromIterable(cx, scope, arg, (key, value) -> {
                    if (key instanceof Integer) {
                        obj.put((Integer)key, obj, value);
                    } else if (key instanceof Symbol && obj instanceof SymbolScriptable) {
                        ((SymbolScriptable)((Object)obj)).put((Symbol)key, obj, value);
                    } else {
                        obj.put(ScriptRuntime.toString(key), obj, value);
                    }
                });
                return obj;
            }
            case -20: {
                Object arg = args.length < 1 ? Undefined.instance : args[0];
                Scriptable obj = NativeObject.getCompatibleObject(cx, scope, arg);
                Object[] ids = obj.getIds();
                int j = 0;
                for (int i = 0; i < ids.length; ++i) {
                    if (ids[i] instanceof Integer) {
                        int intId = (Integer)ids[i];
                        if (!obj.has(intId, obj) || !this.isEnumerable(intId, (Object)obj)) continue;
                        ids[j++] = obj.get(intId, obj);
                        continue;
                    }
                    String stringId = ScriptRuntime.toString(ids[i]);
                    if (!obj.has(stringId, obj) || !this.isEnumerable(stringId, (Object)obj)) continue;
                    ids[j++] = obj.get(stringId, obj);
                }
                if (j != ids.length) {
                    ids = Arrays.copyOf(ids, j);
                }
                return cx.newArray(scope, ids);
            }
            case -3: {
                Object arg = args.length < 1 ? Undefined.instance : args[0];
                Scriptable s = NativeObject.getCompatibleObject(cx, scope, arg);
                ScriptableObject obj = NativeObject.ensureScriptableObject(s);
                Object[] ids = obj.getIds(true, false);
                for (int i = 0; i < ids.length; ++i) {
                    ids[i] = ScriptRuntime.toString(ids[i]);
                }
                return cx.newArray(scope, ids);
            }
            case -14: {
                Object arg = args.length < 1 ? Undefined.instance : args[0];
                Scriptable s = NativeObject.getCompatibleObject(cx, scope, arg);
                ScriptableObject obj = NativeObject.ensureScriptableObject(s);
                Object[] ids = obj.getIds(true, true);
                ArrayList<Object> syms = new ArrayList<Object>();
                for (int i = 0; i < ids.length; ++i) {
                    if (!(ids[i] instanceof Symbol)) continue;
                    syms.add(ids[i]);
                }
                return cx.newArray(scope, syms.toArray());
            }
            case -4: {
                Object arg = args.length < 1 ? Undefined.instance : args[0];
                Scriptable s = NativeObject.getCompatibleObject(cx, scope, arg);
                ScriptableObject obj = NativeObject.ensureScriptableObject(s);
                Object nameArg = args.length < 2 ? Undefined.instance : args[1];
                ScriptableObject desc = obj.getOwnPropertyDescriptor(cx, nameArg);
                return desc == null ? Undefined.instance : desc;
            }
            case -5: {
                Object arg = args.length < 1 ? Undefined.instance : args[0];
                ScriptableObject obj = NativeObject.ensureScriptableObject(arg);
                Object name = args.length < 2 ? Undefined.instance : args[1];
                Object descArg = args.length < 3 ? Undefined.instance : args[2];
                ScriptableObject desc = NativeObject.ensureScriptableObject(descArg);
                obj.defineOwnProperty(cx, name, desc);
                return obj;
            }
            case -6: {
                Object arg;
                Object object = arg = args.length < 1 ? Undefined.instance : args[0];
                if (cx.getLanguageVersion() >= 200 && !(arg instanceof ScriptableObject)) {
                    return Boolean.FALSE;
                }
                ScriptableObject obj = NativeObject.ensureScriptableObject(arg);
                return obj.isExtensible();
            }
            case -7: {
                Object arg;
                Object object = arg = args.length < 1 ? Undefined.instance : args[0];
                if (cx.getLanguageVersion() >= 200 && !(arg instanceof ScriptableObject)) {
                    return arg;
                }
                ScriptableObject obj = NativeObject.ensureScriptableObject(arg);
                obj.preventExtensions();
                return obj;
            }
            case -8: {
                Object arg = args.length < 1 ? Undefined.instance : args[0];
                ScriptableObject obj = NativeObject.ensureScriptableObject(arg);
                Object propsObj = args.length < 2 ? Undefined.instance : args[1];
                Scriptable props = Context.toObject(propsObj, scope);
                obj.defineOwnProperties(cx, NativeObject.ensureScriptableObject(props));
                return obj;
            }
            case -9: {
                Object arg = args.length < 1 ? Undefined.instance : args[0];
                Scriptable obj = arg == null ? null : NativeObject.ensureScriptable(arg);
                NativeObject newObject = new NativeObject();
                newObject.setParentScope(scope);
                newObject.setPrototype(obj);
                if (args.length > 1 && !Undefined.isUndefined(args[1])) {
                    Scriptable props = Context.toObject(args[1], scope);
                    newObject.defineOwnProperties(cx, NativeObject.ensureScriptableObject(props));
                }
                return newObject;
            }
            case -10: {
                Object arg;
                Object object = arg = args.length < 1 ? Undefined.instance : args[0];
                if (cx.getLanguageVersion() >= 200 && !(arg instanceof ScriptableObject)) {
                    return Boolean.TRUE;
                }
                return AbstractEcmaObjectOperations.testIntegrityLevel(cx, arg, AbstractEcmaObjectOperations.INTEGRITY_LEVEL.SEALED);
            }
            case -11: {
                Object arg;
                Object object = arg = args.length < 1 ? Undefined.instance : args[0];
                if (cx.getLanguageVersion() >= 200 && !(arg instanceof ScriptableObject)) {
                    return Boolean.TRUE;
                }
                return AbstractEcmaObjectOperations.testIntegrityLevel(cx, arg, AbstractEcmaObjectOperations.INTEGRITY_LEVEL.FROZEN);
            }
            case -12: {
                Object arg;
                Object object = arg = args.length < 1 ? Undefined.instance : args[0];
                if (cx.getLanguageVersion() >= 200 && !(arg instanceof ScriptableObject)) {
                    return arg;
                }
                AbstractEcmaObjectOperations.setIntegrityLevel(cx, arg, AbstractEcmaObjectOperations.INTEGRITY_LEVEL.SEALED);
                return arg;
            }
            case -13: {
                Object arg;
                Object object = arg = args.length < 1 ? Undefined.instance : args[0];
                if (cx.getLanguageVersion() >= 200 && !(arg instanceof ScriptableObject)) {
                    return arg;
                }
                AbstractEcmaObjectOperations.setIntegrityLevel(cx, arg, AbstractEcmaObjectOperations.INTEGRITY_LEVEL.FROZEN);
                return arg;
            }
            case -15: {
                Scriptable targetObj = args.length > 0 ? ScriptRuntime.toObject(cx, scope, args[0]) : ScriptRuntime.toObject(cx, scope, Undefined.instance);
                for (int i = 1; i < args.length; ++i) {
                    Object[] ids;
                    if (args[i] == null || Undefined.isUndefined(args[i])) continue;
                    Scriptable sourceObj = ScriptRuntime.toObject(cx, scope, args[i]);
                    for (Object key2 : ids = sourceObj.getIds()) {
                        int ii;
                        Object val;
                        ScriptableObject desc;
                        if (targetObj instanceof ScriptableObject && (desc = ((ScriptableObject)targetObj).getOwnPropertyDescriptor(cx, key2)) != null && this.isDataDescriptor(desc) && NativeObject.isFalse(desc.get("writable"))) {
                            throw ScriptRuntime.typeErrorById("msg.change.value.with.writable.false", key2);
                        }
                        if (key2 instanceof String) {
                            Object val2 = sourceObj.get((String)key2, sourceObj);
                            if (val2 == Scriptable.NOT_FOUND || Undefined.isUndefined(val2)) continue;
                            targetObj.put((String)key2, targetObj, val2);
                            continue;
                        }
                        if (!(key2 instanceof Number) || (val = sourceObj.get(ii = ScriptRuntime.toInt32(key2), sourceObj)) == Scriptable.NOT_FOUND || Undefined.isUndefined(val)) continue;
                        targetObj.put(ii, targetObj, val);
                    }
                }
                return targetObj;
            }
            case -16: {
                Object a1 = args.length < 1 ? Undefined.instance : args[0];
                Object a2 = args.length < 2 ? Undefined.instance : args[1];
                return ScriptRuntime.wrapBoolean(ScriptRuntime.same(a1, a2));
            }
        }
        throw new IllegalArgumentException(String.valueOf(id));
    }

    private boolean isEnumerable(int index, Object obj) {
        if (obj instanceof ScriptableObject) {
            ScriptableObject so = (ScriptableObject)obj;
            int attrs = so.getAttributes(index);
            return (attrs & 2) == 0;
        }
        return true;
    }

    private boolean isEnumerable(String key, Object obj) {
        if (obj instanceof ScriptableObject) {
            ScriptableObject so = (ScriptableObject)obj;
            int attrs = so.getAttributes(key);
            return (attrs & 2) == 0;
        }
        return true;
    }

    private boolean isEnumerable(Symbol sym, Object obj) {
        if (obj instanceof ScriptableObject) {
            ScriptableObject so = (ScriptableObject)obj;
            int attrs = so.getAttributes(sym);
            return (attrs & 2) == 0;
        }
        return true;
    }

    private static Scriptable getCompatibleObject(Context cx, Scriptable scope, Object arg) {
        if (cx.getLanguageVersion() >= 200) {
            Scriptable s = ScriptRuntime.toObject(cx, scope, arg);
            return NativeObject.ensureScriptable(s);
        }
        return NativeObject.ensureScriptable(arg);
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            return this.has((String)key, (Scriptable)this);
        }
        if (key instanceof Number) {
            return this.has(((Number)key).intValue(), (Scriptable)this);
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Object obj : this.values()) {
            if (value != obj && (value == null || !value.equals(obj))) continue;
            return true;
        }
        return false;
    }

    public Object remove(Object key) {
        Object value = this.get(key);
        if (key instanceof String) {
            this.delete((String)key);
        } else if (key instanceof Number) {
            this.delete(((Number)key).intValue());
        }
        return value;
    }

    public Set<Object> keySet() {
        return new KeySet();
    }

    public Collection<Object> values() {
        return new ValueCollection();
    }

    public Set<Map.Entry<Object, Object>> entrySet() {
        return new EntrySet();
    }

    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    public void putAll(Map m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
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
            case "toLocaleString": {
                id = 3;
                break;
            }
            case "valueOf": {
                id = 4;
                break;
            }
            case "hasOwnProperty": {
                id = 5;
                break;
            }
            case "propertyIsEnumerable": {
                id = 6;
                break;
            }
            case "isPrototypeOf": {
                id = 7;
                break;
            }
            case "toSource": {
                id = 8;
                break;
            }
            case "__defineGetter__": {
                id = 9;
                break;
            }
            case "__defineSetter__": {
                id = 10;
                break;
            }
            case "__lookupGetter__": {
                id = 11;
                break;
            }
            case "__lookupSetter__": {
                id = 12;
                break;
            }
            default: {
                id = 0;
            }
        }
        return id;
    }

    class ValueCollection
    extends AbstractCollection<Object> {
        ValueCollection() {
        }

        @Override
        public Iterator<Object> iterator() {
            return new Iterator<Object>(){
                Object[] ids;
                Object key;
                int index;
                {
                    this.ids = NativeObject.this.getIds();
                    this.index = 0;
                }

                @Override
                public boolean hasNext() {
                    return this.index < this.ids.length;
                }

                @Override
                public Object next() {
                    this.key = this.ids[this.index++];
                    return NativeObject.this.get(this.key);
                }

                @Override
                public void remove() {
                    if (this.key == null) {
                        throw new IllegalStateException();
                    }
                    NativeObject.this.remove(this.key);
                    this.key = null;
                }
            };
        }

        @Override
        public int size() {
            return NativeObject.this.size();
        }
    }

    class KeySet
    extends AbstractSet<Object> {
        KeySet() {
        }

        @Override
        public boolean contains(Object key) {
            return NativeObject.this.containsKey(key);
        }

        @Override
        public Iterator<Object> iterator() {
            return new Iterator<Object>(){
                Object[] ids;
                Object key;
                int index;
                {
                    this.ids = NativeObject.this.getIds();
                    this.index = 0;
                }

                @Override
                public boolean hasNext() {
                    return this.index < this.ids.length;
                }

                @Override
                public Object next() {
                    try {
                        this.key = this.ids[this.index++];
                        return this.key;
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        this.key = null;
                        throw new NoSuchElementException();
                    }
                }

                @Override
                public void remove() {
                    if (this.key == null) {
                        throw new IllegalStateException();
                    }
                    NativeObject.this.remove(this.key);
                    this.key = null;
                }
            };
        }

        @Override
        public int size() {
            return NativeObject.this.size();
        }
    }

    class EntrySet
    extends AbstractSet<Map.Entry<Object, Object>> {
        EntrySet() {
        }

        @Override
        public Iterator<Map.Entry<Object, Object>> iterator() {
            return new Iterator<Map.Entry<Object, Object>>(){
                Object[] ids;
                Object key;
                int index;
                {
                    this.ids = NativeObject.this.getIds();
                    this.key = null;
                    this.index = 0;
                }

                @Override
                public boolean hasNext() {
                    return this.index < this.ids.length;
                }

                @Override
                public Map.Entry<Object, Object> next() {
                    final Object ekey = this.key = this.ids[this.index++];
                    final Object value = NativeObject.this.get(this.key);
                    return new Map.Entry<Object, Object>(){

                        @Override
                        public Object getKey() {
                            return ekey;
                        }

                        @Override
                        public Object getValue() {
                            return value;
                        }

                        @Override
                        public Object setValue(Object value2) {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public boolean equals(Object other) {
                            if (!(other instanceof Map.Entry)) {
                                return false;
                            }
                            Map.Entry e = (Map.Entry)other;
                            return (ekey == null ? e.getKey() == null : ekey.equals(e.getKey())) && (value == null ? e.getValue() == null : value.equals(e.getValue()));
                        }

                        @Override
                        public int hashCode() {
                            return (ekey == null ? 0 : ekey.hashCode()) ^ (value == null ? 0 : value.hashCode());
                        }

                        public String toString() {
                            return ekey + "=" + value;
                        }
                    };
                }

                @Override
                public void remove() {
                    if (this.key == null) {
                        throw new IllegalStateException();
                    }
                    NativeObject.this.remove(this.key);
                    this.key = null;
                }
            };
        }

        @Override
        public int size() {
            return NativeObject.this.size();
        }
    }
}

