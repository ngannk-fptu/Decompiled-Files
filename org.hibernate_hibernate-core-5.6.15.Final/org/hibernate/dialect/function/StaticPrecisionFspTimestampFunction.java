/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.function;

import java.util.List;
import org.hibernate.QueryException;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public class StaticPrecisionFspTimestampFunction
extends NoArgSQLFunction {
    private final String renderedString;

    public StaticPrecisionFspTimestampFunction(String name, boolean hasParenthesesIfNoArguments) {
        super(name, StandardBasicTypes.TIMESTAMP, hasParenthesesIfNoArguments);
        this.renderedString = null;
    }

    public StaticPrecisionFspTimestampFunction(String name, int fsp) {
        super(name, StandardBasicTypes.TIMESTAMP);
        if (fsp < 0) {
            throw new IllegalArgumentException("fsp must be >= 0");
        }
        this.renderedString = name + '(' + fsp + ')';
    }

    @Override
    public String render(Type argumentType, List args, SessionFactoryImplementor factory) throws QueryException {
        if (args.size() > 0) {
            throw new QueryException("function takes no arguments: " + this.getName());
        }
        return this.renderedString == null ? super.render(argumentType, args, factory) : this.renderedString;
    }
}

