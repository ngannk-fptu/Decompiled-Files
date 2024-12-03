/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolScriptable;

public class Delegator
implements Function,
SymbolScriptable {
    protected Scriptable obj = null;

    public Delegator() {
    }

    public Delegator(Scriptable obj) {
        this.obj = obj;
    }

    protected Delegator newInstance() {
        try {
            return (Delegator)this.getClass().newInstance();
        }
        catch (Exception ex) {
            throw Context.throwAsScriptRuntimeEx(ex);
        }
    }

    public Scriptable getDelegee() {
        return this.obj;
    }

    public void setDelegee(Scriptable obj) {
        this.obj = obj;
    }

    @Override
    public String getClassName() {
        return this.getDelegee().getClassName();
    }

    @Override
    public Object get(String name, Scriptable start) {
        return this.getDelegee().get(name, start);
    }

    @Override
    public Object get(Symbol key, Scriptable start) {
        Scriptable delegee = this.getDelegee();
        if (delegee instanceof SymbolScriptable) {
            return ((SymbolScriptable)((Object)delegee)).get(key, start);
        }
        return Scriptable.NOT_FOUND;
    }

    @Override
    public Object get(int index, Scriptable start) {
        return this.getDelegee().get(index, start);
    }

    @Override
    public boolean has(String name, Scriptable start) {
        return this.getDelegee().has(name, start);
    }

    @Override
    public boolean has(Symbol key, Scriptable start) {
        Scriptable delegee = this.getDelegee();
        if (delegee instanceof SymbolScriptable) {
            return ((SymbolScriptable)((Object)delegee)).has(key, start);
        }
        return false;
    }

    @Override
    public boolean has(int index, Scriptable start) {
        return this.getDelegee().has(index, start);
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        this.getDelegee().put(name, start, value);
    }

    @Override
    public void put(Symbol symbol, Scriptable start, Object value) {
        Scriptable delegee = this.getDelegee();
        if (delegee instanceof SymbolScriptable) {
            ((SymbolScriptable)((Object)delegee)).put(symbol, start, value);
        }
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        this.getDelegee().put(index, start, value);
    }

    @Override
    public void delete(String name) {
        this.getDelegee().delete(name);
    }

    @Override
    public void delete(Symbol key) {
        Scriptable delegee = this.getDelegee();
        if (delegee instanceof SymbolScriptable) {
            ((SymbolScriptable)((Object)delegee)).delete(key);
        }
    }

    @Override
    public void delete(int index) {
        this.getDelegee().delete(index);
    }

    @Override
    public Scriptable getPrototype() {
        return this.getDelegee().getPrototype();
    }

    @Override
    public void setPrototype(Scriptable prototype) {
        this.getDelegee().setPrototype(prototype);
    }

    @Override
    public Scriptable getParentScope() {
        return this.getDelegee().getParentScope();
    }

    @Override
    public void setParentScope(Scriptable parent) {
        this.getDelegee().setParentScope(parent);
    }

    @Override
    public Object[] getIds() {
        return this.getDelegee().getIds();
    }

    @Override
    public Object getDefaultValue(Class<?> hint) {
        return hint == null || hint == ScriptRuntime.ScriptableClass || hint == ScriptRuntime.FunctionClass ? this : this.getDelegee().getDefaultValue(hint);
    }

    @Override
    public boolean hasInstance(Scriptable instance) {
        return this.getDelegee().hasInstance(instance);
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        return ((Function)this.getDelegee()).call(cx, scope, thisObj, args);
    }

    @Override
    public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
        Scriptable myDelegee = this.getDelegee();
        if (myDelegee == null) {
            Delegator n = this.newInstance();
            Scriptable delegee = args.length == 0 ? new NativeObject() : ScriptRuntime.toObject(cx, scope, args[0]);
            n.setDelegee(delegee);
            return n;
        }
        return ((Function)myDelegee).construct(cx, scope, args);
    }
}

