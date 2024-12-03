/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.util.HashMap;
import java.util.Map;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolKey;
import org.mozilla.javascript.Undefined;

public class NativeSymbol
extends IdScriptableObject
implements Symbol {
    private static final long serialVersionUID = -589539749749830003L;
    public static final String CLASS_NAME = "Symbol";
    public static final String TYPE_NAME = "symbol";
    private static final Object GLOBAL_TABLE_KEY = new Object();
    private static final Object CONSTRUCTOR_SLOT = new Object();
    private final SymbolKey key;
    private final NativeSymbol symbolData;
    private static final int ConstructorId_keyFor = -2;
    private static final int ConstructorId_for = -1;
    private static final int Id_constructor = 1;
    private static final int Id_toString = 2;
    private static final int Id_valueOf = 4;
    private static final int SymbolId_toStringTag = 3;
    private static final int SymbolId_toPrimitive = 5;
    private static final int MAX_PROTOTYPE_ID = 5;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void init(Context cx, Scriptable scope, boolean sealed) {
        NativeSymbol obj = new NativeSymbol("");
        IdFunctionObject ctor = obj.exportAsJSClass(5, scope, false);
        cx.putThreadLocal(CONSTRUCTOR_SLOT, Boolean.TRUE);
        try {
            NativeSymbol.createStandardSymbol(cx, scope, ctor, "iterator", SymbolKey.ITERATOR);
            NativeSymbol.createStandardSymbol(cx, scope, ctor, "species", SymbolKey.SPECIES);
            NativeSymbol.createStandardSymbol(cx, scope, ctor, "toStringTag", SymbolKey.TO_STRING_TAG);
            NativeSymbol.createStandardSymbol(cx, scope, ctor, "hasInstance", SymbolKey.HAS_INSTANCE);
            NativeSymbol.createStandardSymbol(cx, scope, ctor, "isConcatSpreadable", SymbolKey.IS_CONCAT_SPREADABLE);
            NativeSymbol.createStandardSymbol(cx, scope, ctor, "isRegExp", SymbolKey.IS_REGEXP);
            NativeSymbol.createStandardSymbol(cx, scope, ctor, "toPrimitive", SymbolKey.TO_PRIMITIVE);
            NativeSymbol.createStandardSymbol(cx, scope, ctor, "match", SymbolKey.MATCH);
            NativeSymbol.createStandardSymbol(cx, scope, ctor, "replace", SymbolKey.REPLACE);
            NativeSymbol.createStandardSymbol(cx, scope, ctor, "search", SymbolKey.SEARCH);
            NativeSymbol.createStandardSymbol(cx, scope, ctor, "split", SymbolKey.SPLIT);
            NativeSymbol.createStandardSymbol(cx, scope, ctor, "unscopables", SymbolKey.UNSCOPABLES);
        }
        finally {
            cx.removeThreadLocal(CONSTRUCTOR_SLOT);
        }
        if (sealed) {
            ctor.sealObject();
        }
    }

    private NativeSymbol(String desc) {
        this.key = new SymbolKey(desc);
        this.symbolData = null;
    }

    private NativeSymbol(SymbolKey key) {
        this.key = key;
        this.symbolData = this;
    }

    public NativeSymbol(NativeSymbol s) {
        this.key = s.key;
        this.symbolData = s.symbolData;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static NativeSymbol construct(Context cx, Scriptable scope, Object[] args) {
        cx.putThreadLocal(CONSTRUCTOR_SLOT, Boolean.TRUE);
        try {
            NativeSymbol nativeSymbol = (NativeSymbol)cx.newObject(scope, CLASS_NAME, args);
            return nativeSymbol;
        }
        finally {
            cx.removeThreadLocal(CONSTRUCTOR_SLOT);
        }
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }

    @Override
    protected void fillConstructorProperties(IdFunctionObject ctor) {
        super.fillConstructorProperties(ctor);
        this.addIdFunctionProperty(ctor, CLASS_NAME, -1, "for", 1);
        this.addIdFunctionProperty(ctor, CLASS_NAME, -2, "keyFor", 1);
    }

    private static void createStandardSymbol(Context cx, Scriptable scope, ScriptableObject ctor, String name, SymbolKey key) {
        Scriptable sym = cx.newObject(scope, CLASS_NAME, new Object[]{name, key});
        ctor.defineProperty(name, (Object)sym, 7);
    }

    @Override
    protected int findPrototypeId(String s) {
        int id = 0;
        switch (s) {
            case "constructor": {
                id = 1;
                break;
            }
            case "toString": {
                id = 2;
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

    @Override
    protected int findPrototypeId(Symbol key) {
        if (SymbolKey.TO_STRING_TAG.equals(key)) {
            return 3;
        }
        if (SymbolKey.TO_PRIMITIVE.equals(key)) {
            return 5;
        }
        return 0;
    }

    @Override
    protected void initPrototypeId(int id) {
        switch (id) {
            case 1: {
                this.initPrototypeMethod(CLASS_NAME, id, "constructor", 0);
                break;
            }
            case 2: {
                this.initPrototypeMethod(CLASS_NAME, id, "toString", 0);
                break;
            }
            case 4: {
                this.initPrototypeMethod(CLASS_NAME, id, "valueOf", 0);
                break;
            }
            case 3: {
                this.initPrototypeValue(id, SymbolKey.TO_STRING_TAG, (Object)CLASS_NAME, 3);
                break;
            }
            case 5: {
                this.initPrototypeMethod((Object)CLASS_NAME, id, SymbolKey.TO_PRIMITIVE, "Symbol.toPrimitive", 1);
                break;
            }
            default: {
                super.initPrototypeId(id);
            }
        }
    }

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(CLASS_NAME)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case -1: {
                return this.js_for(cx, scope, args);
            }
            case -2: {
                return this.js_keyFor(cx, scope, args);
            }
            case 1: {
                if (thisObj == null) {
                    if (cx.getThreadLocal(CONSTRUCTOR_SLOT) == null) {
                        throw ScriptRuntime.typeErrorById("msg.no.symbol.new", new Object[0]);
                    }
                    return NativeSymbol.js_constructor(args);
                }
                return NativeSymbol.construct(cx, scope, args);
            }
            case 2: {
                return NativeSymbol.getSelf(cx, scope, thisObj).toString();
            }
            case 4: 
            case 5: {
                return NativeSymbol.getSelf(cx, scope, thisObj).js_valueOf();
            }
        }
        return super.execIdCall(f, cx, scope, thisObj, args);
    }

    private static NativeSymbol getSelf(Context cx, Scriptable scope, Object thisObj) {
        try {
            return (NativeSymbol)ScriptRuntime.toObject(cx, scope, thisObj);
        }
        catch (ClassCastException cce) {
            throw ScriptRuntime.typeErrorById("msg.invalid.type", thisObj.getClass().getName());
        }
    }

    private static NativeSymbol js_constructor(Object[] args) {
        String desc = args.length > 0 ? (Undefined.instance.equals(args[0]) ? "" : ScriptRuntime.toString(args[0])) : "";
        if (args.length > 1) {
            return new NativeSymbol((SymbolKey)args[1]);
        }
        return new NativeSymbol(new SymbolKey(desc));
    }

    private Object js_valueOf() {
        return this.symbolData;
    }

    private Object js_for(Context cx, Scriptable scope, Object[] args) {
        String name = args.length > 0 ? ScriptRuntime.toString(args[0]) : ScriptRuntime.toString(Undefined.instance);
        Map<String, NativeSymbol> table = this.getGlobalMap();
        NativeSymbol ret = table.get(name);
        if (ret == null) {
            ret = NativeSymbol.construct(cx, scope, new Object[]{name});
            table.put(name, ret);
        }
        return ret;
    }

    private Object js_keyFor(Context cx, Scriptable scope, Object[] args) {
        Object s;
        Object object = s = args.length > 0 ? args[0] : Undefined.instance;
        if (!(s instanceof NativeSymbol)) {
            throw ScriptRuntime.throwCustomError(cx, scope, "TypeError", "Not a Symbol");
        }
        NativeSymbol sym = (NativeSymbol)s;
        Map<String, NativeSymbol> table = this.getGlobalMap();
        for (Map.Entry<String, NativeSymbol> e : table.entrySet()) {
            if (e.getValue().key != sym.key) continue;
            return e.getKey();
        }
        return Undefined.instance;
    }

    public String toString() {
        return this.key.toString();
    }

    private static boolean isStrictMode() {
        Context cx = Context.getCurrentContext();
        return cx != null && cx.isStrictMode();
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        if (!this.isSymbol()) {
            super.put(name, start, value);
        } else if (NativeSymbol.isStrictMode()) {
            throw ScriptRuntime.typeErrorById("msg.no.assign.symbol.strict", new Object[0]);
        }
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        if (!this.isSymbol()) {
            super.put(index, start, value);
        } else if (NativeSymbol.isStrictMode()) {
            throw ScriptRuntime.typeErrorById("msg.no.assign.symbol.strict", new Object[0]);
        }
    }

    @Override
    public void put(Symbol key, Scriptable start, Object value) {
        if (!this.isSymbol()) {
            super.put(key, start, value);
        } else if (NativeSymbol.isStrictMode()) {
            throw ScriptRuntime.typeErrorById("msg.no.assign.symbol.strict", new Object[0]);
        }
    }

    public boolean isSymbol() {
        return this.symbolData == this;
    }

    @Override
    public String getTypeOf() {
        return this.isSymbol() ? TYPE_NAME : super.getTypeOf();
    }

    public int hashCode() {
        return this.key.hashCode();
    }

    public boolean equals(Object x) {
        return this.key.equals(x);
    }

    SymbolKey getKey() {
        return this.key;
    }

    private Map<String, NativeSymbol> getGlobalMap() {
        ScriptableObject top = (ScriptableObject)NativeSymbol.getTopLevelScope(this);
        HashMap map = (HashMap)top.getAssociatedValue(GLOBAL_TABLE_KEY);
        if (map == null) {
            map = new HashMap();
            top.associateValue(GLOBAL_TABLE_KEY, map);
        }
        return map;
    }
}

