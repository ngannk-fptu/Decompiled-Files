/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.identity;

import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

class KeyValue {
    public final Object[] values;
    public final Locator locator;

    KeyValue(Object[] values, Locator loc) {
        this.values = values;
        this.locator = loc == null ? null : new LocatorImpl(loc);
    }

    public int hashCode() {
        int code = 0;
        for (int i = 0; i < this.values.length; ++i) {
            code ^= this.values[i].hashCode();
        }
        return code;
    }

    public boolean equals(Object o) {
        if (!(o instanceof KeyValue)) {
            return false;
        }
        KeyValue rhs = (KeyValue)o;
        if (this.values.length != rhs.values.length) {
            return false;
        }
        for (int i = 0; i < this.values.length; ++i) {
            if (this.values[i].equals(rhs.values[i])) continue;
            return false;
        }
        return true;
    }
}

