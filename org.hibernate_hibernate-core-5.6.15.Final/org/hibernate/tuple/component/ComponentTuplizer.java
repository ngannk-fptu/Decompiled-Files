/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.component;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.tuple.Tuplizer;

public interface ComponentTuplizer
extends Tuplizer,
Serializable {
    public Object getParent(Object var1);

    public void setParent(Object var1, Object var2, SessionFactoryImplementor var3);

    public boolean hasParentProperty();

    public boolean isMethodOf(Method var1);
}

