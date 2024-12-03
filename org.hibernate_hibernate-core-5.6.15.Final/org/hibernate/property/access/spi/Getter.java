/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.spi;

import java.io.Serializable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface Getter
extends Serializable {
    public Object get(Object var1);

    public Object getForInsert(Object var1, Map var2, SharedSessionContractImplementor var3);

    public Class getReturnType();

    public Member getMember();

    public String getMethodName();

    public Method getMethod();
}

