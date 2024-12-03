/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.function;

import java.util.List;
import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;

public class NoArgSQLFunction
implements SQLFunction {
    private Type returnType;
    private boolean hasParenthesesIfNoArguments;
    private String name;

    public NoArgSQLFunction(String name, Type returnType) {
        this(name, returnType, true);
    }

    public NoArgSQLFunction(String name, Type returnType, boolean hasParenthesesIfNoArguments) {
        this.returnType = returnType;
        this.hasParenthesesIfNoArguments = hasParenthesesIfNoArguments;
        this.name = name;
    }

    @Override
    public boolean hasArguments() {
        return false;
    }

    @Override
    public boolean hasParenthesesIfNoArguments() {
        return this.hasParenthesesIfNoArguments;
    }

    @Override
    public Type getReturnType(Type argumentType, Mapping mapping) throws QueryException {
        return this.returnType;
    }

    @Override
    public String render(Type argumentType, List args, SessionFactoryImplementor factory) throws QueryException {
        if (args.size() > 0) {
            throw new QueryException("function takes no arguments: " + this.name);
        }
        return this.hasParenthesesIfNoArguments ? this.name + "()" : this.name;
    }

    protected String getName() {
        return this.name;
    }
}

