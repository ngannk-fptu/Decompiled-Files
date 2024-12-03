/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.LazilyLoadedCtor;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Slot;

public class LazyLoadSlot
extends Slot {
    LazyLoadSlot(Slot oldSlot) {
        super(oldSlot);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getValue(Scriptable start) {
        Object val = this.value;
        if (val instanceof LazilyLoadedCtor) {
            LazilyLoadedCtor initializer = (LazilyLoadedCtor)val;
            try {
                initializer.init();
            }
            finally {
                this.value = val = initializer.getValue();
            }
        }
        return val;
    }
}

