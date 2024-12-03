/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolKey;
import org.mozilla.javascript.Undefined;

public abstract class ES6Iterator
extends IdScriptableObject {
    private static final long serialVersionUID = 2438373029140003950L;
    protected boolean exhausted = false;
    private String tag;
    private static final int Id_next = 1;
    private static final int SymbolId_iterator = 2;
    private static final int SymbolId_toStringTag = 3;
    private static final int MAX_PROTOTYPE_ID = 3;
    public static final String NEXT_METHOD = "next";
    public static final String DONE_PROPERTY = "done";
    public static final String RETURN_PROPERTY = "return";
    public static final String VALUE_PROPERTY = "value";
    public static final String RETURN_METHOD = "return";

    protected static void init(ScriptableObject scope, boolean sealed, IdScriptableObject prototype, String tag) {
        if (scope != null) {
            prototype.setParentScope(scope);
            prototype.setPrototype(ES6Iterator.getObjectPrototype(scope));
        }
        prototype.activatePrototypeMap(3);
        if (sealed) {
            prototype.sealObject();
        }
        if (scope != null) {
            scope.associateValue(tag, prototype);
        }
    }

    protected ES6Iterator() {
    }

    protected ES6Iterator(Scriptable scope, String tag) {
        this.tag = tag;
        Scriptable top = ScriptableObject.getTopLevelScope(scope);
        this.setParentScope(top);
        IdScriptableObject prototype = (IdScriptableObject)ScriptableObject.getTopScopeValue(top, tag);
        this.setPrototype(prototype);
    }

    @Override
    protected void initPrototypeId(int id) {
        switch (id) {
            case 1: {
                this.initPrototypeMethod(this.getTag(), id, NEXT_METHOD, 0);
                return;
            }
            case 2: {
                this.initPrototypeMethod((Object)this.getTag(), id, SymbolKey.ITERATOR, "[Symbol.iterator]", 3);
                return;
            }
            case 3: {
                this.initPrototypeValue(3, SymbolKey.TO_STRING_TAG, (Object)this.getClassName(), 3);
                return;
            }
        }
        throw new IllegalArgumentException(String.valueOf(id));
    }

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(this.getTag())) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        ES6Iterator iterator = ES6Iterator.ensureType(thisObj, ES6Iterator.class, f);
        switch (id) {
            case 1: {
                return iterator.next(cx, scope);
            }
            case 2: {
                return iterator;
            }
        }
        throw new IllegalArgumentException(String.valueOf(id));
    }

    @Override
    protected int findPrototypeId(Symbol k) {
        if (SymbolKey.ITERATOR.equals(k)) {
            return 2;
        }
        if (SymbolKey.TO_STRING_TAG.equals(k)) {
            return 3;
        }
        return 0;
    }

    @Override
    protected int findPrototypeId(String s) {
        if (NEXT_METHOD.equals(s)) {
            return 1;
        }
        return 0;
    }

    protected abstract boolean isDone(Context var1, Scriptable var2);

    protected abstract Object nextValue(Context var1, Scriptable var2);

    protected Object next(Context cx, Scriptable scope) {
        boolean done;
        Object value = Undefined.instance;
        boolean bl = done = this.isDone(cx, scope) || this.exhausted;
        if (!done) {
            value = this.nextValue(cx, scope);
        } else {
            this.exhausted = true;
        }
        return ES6Iterator.makeIteratorResult(cx, scope, done, value);
    }

    protected String getTag() {
        return this.tag;
    }

    static Scriptable makeIteratorResult(Context cx, Scriptable scope, Boolean done) {
        return ES6Iterator.makeIteratorResult(cx, scope, done, Undefined.instance);
    }

    static Scriptable makeIteratorResult(Context cx, Scriptable scope, Boolean done, Object value) {
        Scriptable iteratorResult = cx.newObject(scope);
        ScriptableObject.putProperty(iteratorResult, VALUE_PROPERTY, value);
        ScriptableObject.putProperty(iteratorResult, DONE_PROPERTY, (Object)done);
        return iteratorResult;
    }
}

