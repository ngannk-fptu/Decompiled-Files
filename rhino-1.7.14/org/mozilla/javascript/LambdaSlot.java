/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Slot;

public class LambdaSlot
extends Slot {
    private static final long serialVersionUID = -3046681698806493052L;
    transient Supplier<Object> getter;
    transient Consumer<Object> setter;

    LambdaSlot(Slot oldSlot) {
        super(oldSlot);
    }

    @Override
    boolean isValueSlot() {
        return false;
    }

    @Override
    boolean isSetterSlot() {
        return false;
    }

    @Override
    ScriptableObject getPropertyDescriptor(Context cx, Scriptable scope) {
        ScriptableObject desc = (ScriptableObject)cx.newObject(scope);
        if (this.getter != null) {
            desc.defineProperty("value", this.getter.get(), 0);
        } else {
            desc.defineProperty("value", this.value, 0);
        }
        desc.setCommonDescriptorProperties(this.getAttributes(), true);
        return desc;
    }

    @Override
    public boolean setValue(Object value, Scriptable owner, Scriptable start) {
        if (this.setter != null) {
            if (owner == start) {
                this.setter.accept(value);
                return true;
            }
            return false;
        }
        return super.setValue(value, owner, start);
    }

    @Override
    public Object getValue(Scriptable start) {
        if (this.getter != null) {
            return this.getter.get();
        }
        return super.getValue(start);
    }
}

