/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.spi;

import org.hibernate.type.Type;

public interface QueryParameterBindingTypeResolver {
    public Type resolveParameterBindType(Object var1);

    public Type resolveParameterBindType(Class var1);
}

