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

public class ConvertFunction
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
        return StandardBasicTypes.STRING;
    }

    @Override
    public String render(Type firstArgumentType, List args, SessionFactoryImplementor factory) throws QueryException {
        if (args.size() != 2 && args.size() != 3) {
            throw new QueryException("convert() requires two or three arguments");
        }
        String type = (String)args.get(1);
        if (args.size() == 2) {
            return "{fn convert(" + args.get(0) + " , " + type + ")}";
        }
        return "convert(" + args.get(0) + " , " + type + "," + args.get(2) + ")";
    }
}

