/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.function;

import java.util.List;
import org.hibernate.QueryException;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;

public interface SQLFunction {
    public boolean hasArguments();

    public boolean hasParenthesesIfNoArguments();

    public Type getReturnType(Type var1, Mapping var2) throws QueryException;

    public String render(Type var1, List var2, SessionFactoryImplementor var3) throws QueryException;
}

