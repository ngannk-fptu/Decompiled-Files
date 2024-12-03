/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import com.mchange.v2.lang.ObjectUtils;
import java.util.Map;

public abstract class AbstractMapEntry
implements Map.Entry {
    public abstract Object getKey();

    public abstract Object getValue();

    public abstract Object setValue(Object var1);

    @Override
    public boolean equals(Object object) {
        if (object instanceof Map.Entry) {
            Map.Entry entry = (Map.Entry)object;
            return ObjectUtils.eqOrBothNull(this.getKey(), entry.getKey()) && ObjectUtils.eqOrBothNull(this.getValue(), entry.getValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
    }
}

