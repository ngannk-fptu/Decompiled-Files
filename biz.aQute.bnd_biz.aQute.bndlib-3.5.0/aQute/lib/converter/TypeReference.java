/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeReference<T>
implements Type {
    protected TypeReference() {
    }

    public Type getType() {
        return ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}

