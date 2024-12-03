/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import java.io.Serializable;

public class ListValueType
implements Serializable {
    public final Object[] values;
    private static final long serialVersionUID = 1L;

    public ListValueType(Object[] values) {
        this.values = values;
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof ListValueType)) {
            return false;
        }
        ListValueType rhs = (ListValueType)o;
        int len = this.values.length;
        if (len != rhs.values.length) {
            return false;
        }
        for (int i = 0; i < len; ++i) {
            if (this.values[i].equals(rhs.values[i])) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        int h = 1;
        int len = this.values.length;
        for (int i = 0; i < len; ++i) {
            h += this.values[i].hashCode();
        }
        return h;
    }
}

