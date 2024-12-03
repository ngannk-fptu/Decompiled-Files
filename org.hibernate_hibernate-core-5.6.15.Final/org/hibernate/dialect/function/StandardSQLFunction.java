/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.function;

import java.util.List;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;

public class StandardSQLFunction
implements SQLFunction {
    private final String name;
    private final Type registeredType;

    public StandardSQLFunction(String name) {
        this(name, null);
    }

    public StandardSQLFunction(String name, Type registeredType) {
        this.name = name;
        this.registeredType = registeredType;
    }

    public String getName() {
        return this.name;
    }

    public Type getType() {
        return this.registeredType;
    }

    @Override
    public boolean hasArguments() {
        return true;
    }

    @Override
    public boolean hasParenthesesIfNoArguments() {
        return true;
    }

    @Override
    public Type getReturnType(Type firstArgumentType, Mapping mapping) {
        return this.registeredType == null ? firstArgumentType : this.registeredType;
    }

    @Override
    public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor sessionFactory) {
        StringBuilder buf = new StringBuilder();
        buf.append(this.getRenderedName(arguments)).append('(');
        for (int i = 0; i < arguments.size(); ++i) {
            buf.append(arguments.get(i));
            if (i >= arguments.size() - 1) continue;
            buf.append(", ");
        }
        return buf.append(')').toString();
    }

    protected String getRenderedName(List arguments) {
        return this.getName();
    }

    public String toString() {
        return this.name;
    }
}

