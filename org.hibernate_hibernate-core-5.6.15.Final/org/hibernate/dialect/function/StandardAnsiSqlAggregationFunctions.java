/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.function;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public class StandardAnsiSqlAggregationFunctions {
    public static void primeFunctionMap(Map<String, SQLFunction> functionMap) {
        functionMap.put(AvgFunction.INSTANCE.getName(), AvgFunction.INSTANCE);
        functionMap.put(CountFunction.INSTANCE.getName(), CountFunction.INSTANCE);
        functionMap.put(MaxFunction.INSTANCE.getName(), MaxFunction.INSTANCE);
        functionMap.put(MinFunction.INSTANCE.getName(), MinFunction.INSTANCE);
        functionMap.put(SumFunction.INSTANCE.getName(), SumFunction.INSTANCE);
    }

    private StandardAnsiSqlAggregationFunctions() {
    }

    public static class SumFunction
    extends StandardSQLFunction {
        public static final SumFunction INSTANCE = new SumFunction();

        protected SumFunction() {
            super("sum");
        }

        @Override
        public Type getReturnType(Type firstArgumentType, Mapping mapping) {
            int jdbcType = this.determineJdbcTypeCode(firstArgumentType, mapping);
            if (firstArgumentType == StandardBasicTypes.BIG_INTEGER) {
                return StandardBasicTypes.BIG_INTEGER;
            }
            if (firstArgumentType == StandardBasicTypes.BIG_DECIMAL) {
                return StandardBasicTypes.BIG_DECIMAL;
            }
            if (firstArgumentType == StandardBasicTypes.LONG || firstArgumentType == StandardBasicTypes.SHORT || firstArgumentType == StandardBasicTypes.INTEGER) {
                return StandardBasicTypes.LONG;
            }
            if (firstArgumentType == StandardBasicTypes.FLOAT || firstArgumentType == StandardBasicTypes.DOUBLE) {
                return StandardBasicTypes.DOUBLE;
            }
            if (jdbcType == 6 || jdbcType == 8 || jdbcType == 3 || jdbcType == 7) {
                return StandardBasicTypes.DOUBLE;
            }
            if (jdbcType == -5 || jdbcType == 4 || jdbcType == 5 || jdbcType == -6) {
                return StandardBasicTypes.LONG;
            }
            return firstArgumentType;
        }

        protected final int determineJdbcTypeCode(Type type, Mapping mapping) throws QueryException {
            try {
                int[] jdbcTypeCodes = type.sqlTypes(mapping);
                if (jdbcTypeCodes.length != 1) {
                    throw new QueryException("multiple-column type in sum()");
                }
                return jdbcTypeCodes[0];
            }
            catch (MappingException me) {
                throw new QueryException((Exception)((Object)me));
            }
        }
    }

    public static class MinFunction
    extends StandardSQLFunction {
        public static final MinFunction INSTANCE = new MinFunction();

        protected MinFunction() {
            super("min");
        }
    }

    public static class MaxFunction
    extends StandardSQLFunction {
        public static final MaxFunction INSTANCE = new MaxFunction();

        protected MaxFunction() {
            super("max");
        }
    }

    public static class AvgFunction
    extends StandardSQLFunction {
        public static final AvgFunction INSTANCE = new AvgFunction();

        protected AvgFunction() {
            super("avg", StandardBasicTypes.DOUBLE);
        }

        @Override
        public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) throws QueryException {
            int jdbcTypeCode = this.determineJdbcTypeCode(firstArgumentType, factory);
            return this.render(jdbcTypeCode, arguments.get(0).toString(), factory);
        }

        protected final int determineJdbcTypeCode(Type firstArgumentType, SessionFactoryImplementor factory) throws QueryException {
            try {
                int[] jdbcTypeCodes = firstArgumentType.sqlTypes(factory);
                if (jdbcTypeCodes.length != 1) {
                    throw new QueryException("multiple-column type in avg()");
                }
                return jdbcTypeCodes[0];
            }
            catch (MappingException me) {
                throw new QueryException((Exception)((Object)me));
            }
        }

        protected String render(int firstArgumentJdbcType, String argument, SessionFactoryImplementor factory) {
            return "avg(" + this.renderArgument(argument, firstArgumentJdbcType) + ")";
        }

        protected String renderArgument(String argument, int firstArgumentJdbcType) {
            return argument;
        }
    }

    public static class CountFunction
    extends StandardSQLFunction {
        public static final CountFunction INSTANCE = new CountFunction();

        protected CountFunction() {
            super("count", StandardBasicTypes.LONG);
        }

        @Override
        public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) {
            if (arguments.size() > 1 && "distinct".equalsIgnoreCase(arguments.get(0).toString())) {
                return this.renderCountDistinct(arguments, factory.getDialect());
            }
            return super.render(firstArgumentType, arguments, factory);
        }

        private String renderCountDistinct(List arguments, Dialect dialect) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("count(distinct ");
            if (dialect.requiresParensForTupleDistinctCounts()) {
                buffer.append("(");
            }
            String sep = "";
            Iterator itr = arguments.iterator();
            itr.next();
            while (itr.hasNext()) {
                buffer.append(sep).append(itr.next());
                sep = ", ";
            }
            if (dialect.requiresParensForTupleDistinctCounts()) {
                buffer.append(")");
            }
            return buffer.append(")").toString();
        }
    }
}

