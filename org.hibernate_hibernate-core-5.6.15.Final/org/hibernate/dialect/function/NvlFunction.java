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

public class NvlFunction
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
    public Type getReturnType(Type argumentType, Mapping mapping) throws QueryException {
        return argumentType;
    }

    @Override
    public String render(Type argumentType, List args, SessionFactoryImplementor factory) throws QueryException {
        int lastIndex = args.size() - 1;
        Object last = args.remove(lastIndex);
        if (lastIndex == 0) {
            return last.toString();
        }
        Object secondLast = args.get(lastIndex - 1);
        String nvl = "nvl(" + secondLast + ", " + last + ")";
        args.set(lastIndex - 1, nvl);
        return this.render(argumentType, args, factory);
    }
}

