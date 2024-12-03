/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.DriverError;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerError;
import com.microsoft.sqlserver.jdbc.SQLServerResource;
import com.microsoft.sqlserver.jdbc.SQLState;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.text.MessageFormat;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SQLServerException
extends SQLException {
    private static final long serialVersionUID = -2195310557661496761L;
    static final String EXCEPTION_XOPEN_CONNECTION_CANT_ESTABLISH = "08001";
    static final String EXCEPTION_XOPEN_CONNECTION_DOES_NOT_EXIST = "08003";
    static final String EXCEPTION_XOPEN_CONNECTION_FAILURE = "08006";
    static final String LOG_CLIENT_CONNECTION_ID_PREFIX = " ClientConnectionId:";
    static final int LOGON_FAILED = 18456;
    static final int PASSWORD_EXPIRED = 18488;
    static final int USER_ACCOUNT_LOCKED = 18486;
    static final int IMPERSONATION_CONTEXT_NOT_SUPPORTED = 40529;
    static final int DRIVER_ERROR_NONE = 0;
    static final int DRIVER_ERROR_FROM_DATABASE = 2;
    static final int DRIVER_ERROR_IO_FAILED = 3;
    static final int DRIVER_ERROR_INVALID_TDS = 4;
    static final int DRIVER_ERROR_SSL_FAILED = 5;
    static final int DRIVER_ERROR_UNSUPPORTED_CONFIG = 6;
    static final int DRIVER_ERROR_INTERMITTENT_TLS_FAILED = 7;
    static final int ERROR_SOCKET_TIMEOUT = 8;
    static final int ERROR_QUERY_TIMEOUT = 9;
    static final int DATA_CLASSIFICATION_INVALID_VERSION = 10;
    static final int DATA_CLASSIFICATION_NOT_EXPECTED = 11;
    static final int DATA_CLASSIFICATION_INVALID_LABEL_INDEX = 12;
    static final int DATA_CLASSIFICATION_INVALID_INFORMATION_TYPE_INDEX = 13;
    static final Logger exLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerException");
    private int driverErrorCode = 0;
    private SQLServerError sqlServerError;

    final int getDriverErrorCode() {
        return this.driverErrorCode;
    }

    final void setDriverErrorCode(int value) {
        this.driverErrorCode = value;
    }

    private void logException(Object o, String errText, boolean bStack) {
        String id = "";
        if (o != null) {
            id = o.toString();
        }
        if (exLogger.isLoggable(Level.FINE)) {
            exLogger.fine("*** SQLException:" + id + " " + this.toString() + " " + errText);
        }
        if (bStack && exLogger.isLoggable(Level.FINE)) {
            StackTraceElement[] st;
            StringBuilder sb = new StringBuilder(100);
            for (StackTraceElement aSt : st = this.getStackTrace()) {
                sb.append(aSt.toString());
            }
            Throwable t = this.getCause();
            if (t != null) {
                StackTraceElement[] tst;
                sb.append("\n caused by ").append(t).append("\n");
                for (StackTraceElement aTst : tst = t.getStackTrace()) {
                    sb.append(aTst.toString());
                }
            }
            exLogger.fine(sb.toString());
        }
        if (SQLServerException.getErrString("R_queryTimedOut").equals(errText)) {
            this.setDriverErrorCode(9);
        }
    }

    static String getErrString(String errCode) {
        return SQLServerResource.getResource(errCode);
    }

    SQLServerException(String errText, SQLState sqlState, DriverError driverError, Throwable cause) {
        this(errText, sqlState.getSQLStateCode(), driverError.getErrorCode(), cause);
    }

    SQLServerException(String errText, String errState, int errNum, Throwable cause) {
        super(errText, errState, errNum);
        this.initCause(cause);
        this.logException(null, errText, true);
    }

    SQLServerException(String errText, Throwable cause) {
        super(errText);
        this.initCause(cause);
        this.logException(null, errText, true);
    }

    SQLServerException(Object obj, String errText, String errState, int errNum, boolean bStack) {
        super(errText, errState, errNum);
        this.logException(obj, errText, bStack);
    }

    SQLServerException(Object obj, String errText, String errState, SQLServerError sqlServerError, boolean bStack) {
        super((String)errText, errState, sqlServerError.getErrorNumber());
        this.sqlServerError = sqlServerError;
        errText = "Msg " + sqlServerError.getErrorNumber() + ", Level " + sqlServerError.getErrorSeverity() + ", State " + sqlServerError.getErrorState() + ", " + (String)errText;
        this.logException(obj, (String)errText, bStack);
    }

    static void makeFromDriverError(SQLServerConnection con, Object obj, String errText, String state, boolean bStack) throws SQLServerException {
        String stateCode = "";
        if (state != null) {
            stateCode = state;
        }
        if (con == null || !con.xopenStates) {
            stateCode = SQLServerException.mapFromXopen(state);
        }
        SQLServerException theException = new SQLServerException(obj, SQLServerException.checkAndAppendClientConnId(errText, con), stateCode, 0, bStack);
        if (null != state && state.equals(EXCEPTION_XOPEN_CONNECTION_FAILURE) && null != con) {
            con.notifyPooledConnection(theException);
            con.close();
        }
        throw theException;
    }

    static void makeFromDatabaseError(SQLServerConnection con, Object obj, String errText, SQLServerError sqlServerError, boolean bStack) throws SQLServerException {
        String state = SQLServerException.generateStateCode(con, sqlServerError.getErrorNumber(), sqlServerError.getErrorState());
        SQLServerException theException = new SQLServerException(obj, SQLServerException.checkAndAppendClientConnId(errText, con), state, sqlServerError, bStack);
        theException.setDriverErrorCode(2);
        if (sqlServerError.getErrorSeverity() >= 20 && null != con) {
            con.notifyPooledConnection(theException);
            con.close();
        }
        throw theException;
    }

    static void convertConnectExceptionToSQLServerException(String hostName, int portNumber, SQLServerConnection conn, Exception ex) throws SQLServerException {
        Exception connectException = ex;
        if (connectException != null) {
            MessageFormat formDetail = new MessageFormat(SQLServerException.getErrString("R_tcpOpenFailed"));
            Object[] msgArgsDetail = new Object[]{connectException.getMessage()};
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_tcpipConnectionFailed"));
            Object[] msgArgs = new Object[]{conn.getServerNameString(hostName), Integer.toString(portNumber), formDetail.format(msgArgsDetail)};
            SQLServerException.makeFromDriverError(conn, conn, form.format(msgArgs), EXCEPTION_XOPEN_CONNECTION_CANT_ESTABLISH, false);
        }
    }

    static String mapFromXopen(String state) {
        if (null != state) {
            switch (state) {
                case "07009": {
                    return "S1093";
                }
                case "08001": {
                    return "08S01";
                }
                case "08006": {
                    return "08S01";
                }
            }
            return "";
        }
        return null;
    }

    static String generateStateCode(SQLServerConnection con, int errNum, Integer databaseState) {
        boolean xopenStates;
        boolean bl = xopenStates = con != null && con.xopenStates;
        if (xopenStates) {
            switch (errNum) {
                case 4060: {
                    return EXCEPTION_XOPEN_CONNECTION_CANT_ESTABLISH;
                }
                case 18456: {
                    return EXCEPTION_XOPEN_CONNECTION_CANT_ESTABLISH;
                }
                case 2714: {
                    return "42S01";
                }
                case 208: {
                    return "42S02";
                }
                case 207: {
                    return "42S22";
                }
            }
            return "42000";
        }
        switch (errNum) {
            case 8152: {
                return "22001";
            }
            case 515: 
            case 547: 
            case 2601: 
            case 2627: {
                return "23000";
            }
            case 2714: {
                return "S0001";
            }
            case 208: {
                return "S0002";
            }
            case 1205: {
                return "40001";
            }
        }
        String dbState = databaseState.toString();
        StringBuilder trailingZeroes = new StringBuilder("S");
        for (int i = 0; i < 4 - dbState.length(); ++i) {
            trailingZeroes.append("0");
        }
        return trailingZeroes.append(dbState).toString();
    }

    static String checkAndAppendClientConnId(String errMsg, SQLServerConnection conn) {
        if (null != conn && conn.attachConnId()) {
            UUID clientConnId = conn.getClientConIdInternal();
            assert (null != clientConnId);
            StringBuilder sb = new StringBuilder(errMsg);
            sb.append(LOG_CLIENT_CONNECTION_ID_PREFIX);
            sb.append(clientConnId.toString());
            return sb.toString();
        }
        return errMsg;
    }

    static void throwNotSupportedException(SQLServerConnection con, Object obj) throws SQLServerException {
        SQLServerException.makeFromDriverError(con, obj, SQLServerException.getErrString("R_notSupported"), null, false);
    }

    static void throwFeatureNotSupportedException() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException(SQLServerException.getErrString("R_notSupported"));
    }

    public SQLServerError getSQLServerError() {
        return this.sqlServerError;
    }
}

