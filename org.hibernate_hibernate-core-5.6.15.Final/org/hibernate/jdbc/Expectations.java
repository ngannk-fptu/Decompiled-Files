/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jdbc;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.StaleStateException;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.jdbc.BatchFailedException;
import org.hibernate.jdbc.BatchedTooManyRowsAffectedException;
import org.hibernate.jdbc.Expectation;
import org.hibernate.jdbc.TooManyRowsAffectedException;

public class Expectations {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(Expectations.class);
    private static SqlExceptionHelper sqlExceptionHelper = new SqlExceptionHelper(false);
    public static final int USUAL_EXPECTED_COUNT = 1;
    public static final int USUAL_PARAM_POSITION = 1;
    public static final Expectation NONE = new Expectation(){

        @Override
        public void verifyOutcome(int rowCount, PreparedStatement statement, int batchPosition, String statementSQL) {
        }

        @Override
        public int prepare(PreparedStatement statement) {
            return 0;
        }

        @Override
        public boolean canBeBatched() {
            return true;
        }
    };
    public static final Expectation BASIC = new BasicExpectation(1);
    public static final Expectation PARAM = new BasicParamExpectation(1, 1);

    public static Expectation appropriateExpectation(ExecuteUpdateResultCheckStyle style) {
        if (style == ExecuteUpdateResultCheckStyle.NONE) {
            return NONE;
        }
        if (style == ExecuteUpdateResultCheckStyle.COUNT) {
            return BASIC;
        }
        if (style == ExecuteUpdateResultCheckStyle.PARAM) {
            return PARAM;
        }
        throw new HibernateException("unknown check style : " + (Object)((Object)style));
    }

    private Expectations() {
    }

    public static class BasicParamExpectation
    extends BasicExpectation {
        private final int parameterPosition;

        protected BasicParamExpectation(int expectedRowCount, int parameterPosition) {
            super(expectedRowCount);
            this.parameterPosition = parameterPosition;
        }

        @Override
        public int prepare(PreparedStatement statement) throws SQLException, HibernateException {
            this.toCallableStatement(statement).registerOutParameter(this.parameterPosition, 2);
            return 1;
        }

        @Override
        public boolean canBeBatched() {
            return false;
        }

        @Override
        protected int determineRowCount(int reportedRowCount, PreparedStatement statement) {
            try {
                return this.toCallableStatement(statement).getInt(this.parameterPosition);
            }
            catch (SQLException sqle) {
                sqlExceptionHelper.logExceptions(sqle, "could not extract row counts from CallableStatement");
                throw new GenericJDBCException("could not extract row counts from CallableStatement", sqle);
            }
        }

        private CallableStatement toCallableStatement(PreparedStatement statement) {
            if (!CallableStatement.class.isInstance(statement)) {
                throw new HibernateException("BasicParamExpectation operates exclusively on CallableStatements : " + statement.getClass());
            }
            return (CallableStatement)statement;
        }
    }

    public static class BasicExpectation
    implements Expectation {
        private final int expectedRowCount;

        protected BasicExpectation(int expectedRowCount) {
            this.expectedRowCount = expectedRowCount;
            if (expectedRowCount < 0) {
                throw new IllegalArgumentException("Expected row count must be greater than zero");
            }
        }

        @Override
        public final void verifyOutcome(int rowCount, PreparedStatement statement, int batchPosition, String statementSQL) {
            rowCount = this.determineRowCount(rowCount, statement);
            if (batchPosition < 0) {
                this.checkNonBatched(rowCount, statementSQL);
            } else {
                this.checkBatched(rowCount, batchPosition, statementSQL);
            }
        }

        private void checkBatched(int rowCount, int batchPosition, String statementSQL) {
            if (rowCount == -2) {
                LOG.debugf("Success of batch update unknown: %s", batchPosition);
            } else {
                if (rowCount == -3) {
                    throw new BatchFailedException("Batch update failed: " + batchPosition);
                }
                if (this.expectedRowCount > rowCount) {
                    throw new StaleStateException("Batch update returned unexpected row count from update [" + batchPosition + "]; actual row count: " + rowCount + "; expected: " + this.expectedRowCount + "; statement executed: " + statementSQL);
                }
                if (this.expectedRowCount < rowCount) {
                    String msg = "Batch update returned unexpected row count from update [" + batchPosition + "]; actual row count: " + rowCount + "; expected: " + this.expectedRowCount;
                    throw new BatchedTooManyRowsAffectedException(msg, this.expectedRowCount, rowCount, batchPosition);
                }
            }
        }

        private void checkNonBatched(int rowCount, String statementSQL) {
            if (this.expectedRowCount > rowCount) {
                throw new StaleStateException("Unexpected row count: " + rowCount + "; expected: " + this.expectedRowCount + "; statement executed: " + statementSQL);
            }
            if (this.expectedRowCount < rowCount) {
                String msg = "Unexpected row count: " + rowCount + "; expected: " + this.expectedRowCount;
                throw new TooManyRowsAffectedException(msg, this.expectedRowCount, rowCount);
            }
        }

        @Override
        public int prepare(PreparedStatement statement) throws SQLException, HibernateException {
            return 0;
        }

        @Override
        public boolean canBeBatched() {
            return true;
        }

        protected int determineRowCount(int reportedRowCount, PreparedStatement statement) {
            return reportedRowCount;
        }
    }
}

