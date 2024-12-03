/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Joiner
 *  com.google.common.base.StandardSystemProperty
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 */
package com.querydsl.sql.support;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.StandardSystemProperty;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.querydsl.core.QueryException;
import com.querydsl.sql.support.SQLExceptionWrapper;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;

class JavaSE6SQLExceptionWrapper
extends SQLExceptionWrapper {
    private static final Joiner lineJoiner = Joiner.on((String)StandardSystemProperty.LINE_SEPARATOR.value());
    private static final Function<Throwable, String> exceptionMessageFunction = new Function<Throwable, String>(){

        public String apply(Throwable input) {
            if (input instanceof SQLException) {
                SQLException sqle = (SQLException)input;
                StringWriter writer = new StringWriter();
                new PrintWriter(writer, true).printf("SQLState: %s%n", sqle.getSQLState()).printf("ErrorCode: %s%n", sqle.getErrorCode()).printf("Message: %s%n", sqle.getMessage());
                return writer.toString();
            }
            return input.toString();
        }
    };

    JavaSE6SQLExceptionWrapper() {
    }

    @Override
    public RuntimeException wrap(SQLException exception) {
        Iterable<Throwable> linkedSQLExceptions = JavaSE6SQLExceptionWrapper.getLinkedSQLExceptions(exception);
        return new QueryException(new WrappedSQLCauseException(linkedSQLExceptions, exception));
    }

    @Override
    public RuntimeException wrap(String message, SQLException exception) {
        Iterable<Throwable> linkedSQLExceptions = JavaSE6SQLExceptionWrapper.getLinkedSQLExceptions(exception);
        return new QueryException(message, new WrappedSQLCauseException(linkedSQLExceptions, exception));
    }

    private static Iterable<Throwable> getLinkedSQLExceptions(SQLException exception) {
        ArrayList rv = Lists.newArrayList();
        for (SQLException nextException = exception.getNextException(); nextException != null; nextException = nextException.getNextException()) {
            rv.add(nextException);
        }
        return rv;
    }

    private static final class WrappedSQLCauseException
    extends Exception {
        private static final long serialVersionUID = 1L;

        private WrappedSQLCauseException(Iterable<Throwable> exceptions, SQLException exception) {
            super("Detailed SQLException information:" + StandardSystemProperty.LINE_SEPARATOR.value() + lineJoiner.join(Iterables.transform(exceptions, (Function)exceptionMessageFunction)), exception);
        }
    }
}

