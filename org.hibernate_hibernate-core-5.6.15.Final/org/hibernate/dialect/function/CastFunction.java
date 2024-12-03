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

public class CastFunction
implements SQLFunction {
    public static final CastFunction INSTANCE = new CastFunction();

    @Override
    public boolean hasArguments() {
        return true;
    }

    @Override
    public boolean hasParenthesesIfNoArguments() {
        return true;
    }

    @Override
    public Type getReturnType(Type columnType, Mapping mapping) throws QueryException {
        return columnType;
    }

    @Override
    public String render(Type columnType, List args, SessionFactoryImplementor factory) throws QueryException {
        if (args.size() != 2) {
            throw new QueryException("cast() requires two arguments; found : " + args.size());
        }
        String type = (String)args.get(1);
        int[] sqlTypeCodes = factory.getTypeResolver().heuristicType(type).sqlTypes(factory);
        if (sqlTypeCodes.length != 1) {
            throw new QueryException("invalid Hibernate type for cast()");
        }
        String sqlType = factory.getDialect().getCastTypeName(sqlTypeCodes[0]);
        if (sqlType == null) {
            sqlType = type;
        }
        return "cast(" + args.get(0) + " as " + sqlType + ')';
    }
}

