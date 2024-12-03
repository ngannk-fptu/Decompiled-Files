/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.function;

import java.util.List;
import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public class PositionSubstringFunction
implements SQLFunction {
    @Override
    public boolean hasArguments() {
        return true;
    }

    @Override
    public boolean hasParenthesesIfNoArguments() {
        return true;
    }

    @Override
    public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
        return StandardBasicTypes.INTEGER;
    }

    @Override
    public String render(Type firstArgumentType, List args, SessionFactoryImplementor factory) throws QueryException {
        boolean threeArgs = args.size() > 2;
        Object pattern = args.get(0);
        Object string = args.get(1);
        Object start = threeArgs ? args.get(2) : null;
        StringBuilder buf = new StringBuilder();
        if (threeArgs) {
            buf.append('(');
        }
        buf.append("position(").append(pattern).append(" in ");
        if (threeArgs) {
            buf.append("substring(");
        }
        buf.append(string);
        if (threeArgs) {
            buf.append(", ").append((Object)start).append(')');
        }
        buf.append(')');
        if (threeArgs) {
            buf.append('+').append((Object)start).append("-1)");
        }
        return buf.toString();
    }
}

