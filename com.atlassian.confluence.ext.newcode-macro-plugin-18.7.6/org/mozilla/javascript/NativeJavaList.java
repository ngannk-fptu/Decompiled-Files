/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.util.ArrayList;
import java.util.List;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolKey;
import org.mozilla.javascript.Undefined;

public class NativeJavaList
extends NativeJavaObject {
    private static final long serialVersionUID = 660285467829047519L;
    private List<Object> list;

    public NativeJavaList(Scriptable scope, Object list) {
        super(scope, list, list.getClass());
        assert (list instanceof List);
        this.list = (List)list;
    }

    @Override
    public String getClassName() {
        return "JavaList";
    }

    @Override
    public boolean has(String name, Scriptable start) {
        if (name.equals("length")) {
            return true;
        }
        return super.has(name, start);
    }

    @Override
    public boolean has(int index, Scriptable start) {
        if (this.isWithValidIndex(index)) {
            return true;
        }
        return super.has(index, start);
    }

    @Override
    public void delete(int index) {
        if (this.isWithValidIndex(index)) {
            this.list.set(index, null);
        }
    }

    @Override
    public boolean has(Symbol key, Scriptable start) {
        if (SymbolKey.IS_CONCAT_SPREADABLE.equals(key)) {
            return true;
        }
        return super.has(key, start);
    }

    @Override
    public Object get(String name, Scriptable start) {
        if ("length".equals(name)) {
            return this.list.size();
        }
        return super.get(name, start);
    }

    @Override
    public Object get(int index, Scriptable start) {
        if (this.isWithValidIndex(index)) {
            Context cx = Context.getCurrentContext();
            Object obj = this.list.get(index);
            if (cx != null) {
                return cx.getWrapFactory().wrap(cx, this, obj, obj == null ? null : obj.getClass());
            }
            return obj;
        }
        return Undefined.instance;
    }

    @Override
    public Object get(Symbol key, Scriptable start) {
        if (SymbolKey.IS_CONCAT_SPREADABLE.equals(key)) {
            return Boolean.TRUE;
        }
        return super.get(key, start);
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        if (index >= 0) {
            Object javaValue = Context.jsToJava(value, Object.class);
            if (index == this.list.size()) {
                this.list.add(javaValue);
            } else {
                this.ensureCapacity(index + 1);
                this.list.set(index, javaValue);
            }
            return;
        }
        super.put(index, start, value);
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        if (this.list != null && "length".equals(name)) {
            this.setLength(value);
            return;
        }
        super.put(name, start, value);
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > this.list.size()) {
            if (this.list instanceof ArrayList) {
                ((ArrayList)this.list).ensureCapacity(minCapacity);
            }
            while (minCapacity > this.list.size()) {
                this.list.add(null);
            }
        }
    }

    private void setLength(Object val) {
        double d = ScriptRuntime.toNumber(val);
        long longVal = ScriptRuntime.toUint32(d);
        if ((double)longVal != d || longVal > Integer.MAX_VALUE) {
            String msg = ScriptRuntime.getMessageById("msg.arraylength.bad", new Object[0]);
            throw ScriptRuntime.rangeError(msg);
        }
        if (longVal < (long)this.list.size()) {
            this.list.subList((int)longVal, this.list.size()).clear();
        } else {
            this.ensureCapacity((int)longVal);
        }
    }

    @Override
    public Object[] getIds() {
        List list = (List)this.javaObject;
        Object[] result = new Object[list.size()];
        int i = list.size();
        while (--i >= 0) {
            result[i] = i;
        }
        return result;
    }

    private boolean isWithValidIndex(int index) {
        return index >= 0 && index < this.list.size();
    }
}

