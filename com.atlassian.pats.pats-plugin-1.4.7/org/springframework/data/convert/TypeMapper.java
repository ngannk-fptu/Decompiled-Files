/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.convert;

import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;

public interface TypeMapper<S> {
    @Nullable
    public TypeInformation<?> readType(S var1);

    public <T> TypeInformation<? extends T> readType(S var1, TypeInformation<T> var2);

    public void writeType(Class<?> var1, S var2);

    public void writeType(TypeInformation<?> var1, S var2);
}

