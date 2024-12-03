/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.util.Properties;
import org.hibernate.type.BasicType;
import org.hibernate.type.Type;

public interface TypeHelper {
    public BasicType basic(String var1);

    public BasicType basic(Class var1);

    public Type heuristicType(String var1);

    public Type entity(Class var1);

    public Type entity(String var1);

    public Type custom(Class var1);

    public Type custom(Class var1, Properties var2);

    public Type any(Type var1, Type var2);
}

