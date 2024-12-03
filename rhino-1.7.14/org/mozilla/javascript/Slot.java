/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class Slot
implements Serializable {
    private static final long serialVersionUID = -6090581677123995491L;
    Object name;
    int indexOrHash;
    private short attributes;
    Object value;
    transient Slot next;
    transient Slot orderedNext;

    Slot(Object name, int indexOrHash, int attributes) {
        this.name = name;
        this.indexOrHash = indexOrHash;
        this.attributes = (short)attributes;
    }

    boolean isValueSlot() {
        return true;
    }

    boolean isSetterSlot() {
        return false;
    }

    protected Slot(Slot oldSlot) {
        this.name = oldSlot.name;
        this.indexOrHash = oldSlot.indexOrHash;
        this.attributes = oldSlot.attributes;
        this.value = oldSlot.value;
        this.next = oldSlot.next;
        this.orderedNext = oldSlot.orderedNext;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (this.name != null) {
            this.indexOrHash = this.name.hashCode();
        }
    }

    public boolean setValue(Object value, Scriptable owner, Scriptable start) {
        if ((this.attributes & 1) != 0) {
            if (Context.isCurrentContextStrict()) {
                throw ScriptRuntime.typeErrorById("msg.modify.readonly", this.name);
            }
            return true;
        }
        if (owner == start) {
            this.value = value;
            return true;
        }
        return false;
    }

    public Object getValue(Scriptable start) {
        return this.value;
    }

    int getAttributes() {
        return this.attributes;
    }

    synchronized void setAttributes(int value) {
        ScriptableObject.checkValidAttributes(value);
        this.attributes = (short)value;
    }

    ScriptableObject getPropertyDescriptor(Context cx, Scriptable scope) {
        return ScriptableObject.buildDataDescriptor(scope, this.value, this.attributes);
    }

    protected void throwNoSetterException(Scriptable start, Object newValue) {
        Context cx = Context.getContext();
        if (cx.isStrictMode() || cx.hasFeature(11)) {
            String prop = "";
            if (this.name != null) {
                prop = "[" + start.getClassName() + "]." + this.name;
            }
            throw ScriptRuntime.typeErrorById("msg.set.prop.no.setter", prop, Context.toString(newValue));
        }
    }

    Function getSetterFunction(String name, Scriptable scope) {
        return null;
    }

    Function getGetterFunction(String name, Scriptable scope) {
        return null;
    }
}

