/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.Assert
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.data.activeobjects.repository.query.Queryable;
import com.atlassian.data.activeobjects.repository.query.StringQuery;
import com.querydsl.core.types.ParamExpression;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

interface QueryParameterSetter {
    public static final QueryParameterSetter NOOP = (query, values, errorHandling) -> {};

    public void setParameter(Queryable var1, Object[] var2, ErrorHandling var3);

    public static enum ErrorHandling {
        STRICT{

            @Override
            public void execute(Runnable block) {
                block.run();
            }
        }
        ,
        LENIENT{

            @Override
            public void execute(Runnable block) {
                try {
                    block.run();
                }
                catch (RuntimeException rex) {
                    log.info("Silently ignoring", (Throwable)rex);
                }
            }
        };

        private static final Logger log;

        abstract void execute(Runnable var1);

        static {
            log = LoggerFactory.getLogger(ErrorHandling.class);
        }
    }

    public static class PredicateParameterSetter
    implements QueryParameterSetter {
        private final Function<Object[], Object> valueExtractor;
        private final ParamExpression<?> paramExpression;

        public PredicateParameterSetter(Function<Object[], Object> valueExtractor, ParamExpression<?> paramExpression) {
            Assert.notNull(valueExtractor, (String)"ValueExtractor must not be null!");
            this.valueExtractor = valueExtractor;
            this.paramExpression = paramExpression;
        }

        @Override
        public void setParameter(Queryable query, Object[] values, ErrorHandling errorHandling) {
            Object value = this.valueExtractor.apply(values);
            errorHandling.execute(() -> query.setParameter(this.paramExpression, value));
        }
    }

    public static class NamedOrIndexedQueryParameterSetter
    implements QueryParameterSetter {
        private final Function<Object[], Object> valueExtractor;
        private final StringQuery.ParameterBinding binding;

        NamedOrIndexedQueryParameterSetter(Function<Object[], Object> valueExtractor, StringQuery.ParameterBinding binding) {
            Assert.notNull(valueExtractor, (String)"ValueExtractor must not be null!");
            this.valueExtractor = valueExtractor;
            this.binding = binding;
        }

        @Override
        public void setParameter(Queryable query, Object[] values, ErrorHandling errorHandling) {
            Integer position = this.binding.getPosition();
            if (position != null && (query.getWhereQueryParams().length >= position || errorHandling == ErrorHandling.LENIENT)) {
                errorHandling.execute(() -> {
                    Object value = this.valueExtractor.apply(values);
                    query.setParameter(position, value);
                });
            }
        }
    }
}

