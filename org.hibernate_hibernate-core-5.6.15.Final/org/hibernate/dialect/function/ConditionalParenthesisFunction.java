/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.function;

import java.util.List;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;

public class ConditionalParenthesisFunction
extends StandardSQLFunction {
    public ConditionalParenthesisFunction(String name) {
        super(name);
    }

    public ConditionalParenthesisFunction(String name, Type type) {
        super(name, type);
    }

    @Override
    public boolean hasParenthesesIfNoArguments() {
        return false;
    }

    @Override
    public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor sessionFactory) {
        boolean hasArgs = !arguments.isEmpty();
        StringBuilder buf = new StringBuilder(this.getName());
        if (hasArgs) {
            buf.append("(");
            for (int i = 0; i < arguments.size(); ++i) {
                buf.append(arguments.get(i));
                if (i >= arguments.size() - 1) continue;
                buf.append(", ");
            }
            buf.append(")");
        }
        return buf.toString();
    }
}

