/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.spi;

import org.hibernate.jpa.spi.ParameterRegistration;

public interface NullTypeBindableParameterRegistration<T>
extends ParameterRegistration<T> {
    public void bindNullValue(Class<?> var1);
}

