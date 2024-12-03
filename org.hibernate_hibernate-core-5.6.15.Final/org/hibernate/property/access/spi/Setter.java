/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.spi;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public interface Setter
extends Serializable {
    public void set(Object var1, Object var2, SessionFactoryImplementor var3);

    public String getMethodName();

    public Method getMethod();
}

