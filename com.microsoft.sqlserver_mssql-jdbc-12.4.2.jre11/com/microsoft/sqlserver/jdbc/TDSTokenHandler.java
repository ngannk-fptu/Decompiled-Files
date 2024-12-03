/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerError;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.StreamColumns;
import com.microsoft.sqlserver.jdbc.StreamDone;
import com.microsoft.sqlserver.jdbc.StreamRetStatus;
import com.microsoft.sqlserver.jdbc.TDS;
import com.microsoft.sqlserver.jdbc.TDSParser;
import com.microsoft.sqlserver.jdbc.TDSReader;
import java.util.logging.Level;
import java.util.logging.Logger;

class TDSTokenHandler {
    final String logContext;
    private SQLServerError databaseError;
    private static Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.TDS.TOKEN");

    final SQLServerError getDatabaseError() {
        return this.databaseError;
    }

    TDSTokenHandler(String logContext) {
        this.logContext = logContext;
    }

    boolean onSSPI(TDSReader tdsReader) throws SQLServerException {
        TDSParser.throwUnexpectedTokenException(tdsReader, this.logContext);
        return false;
    }

    boolean onLoginAck(TDSReader tdsReader) throws SQLServerException {
        TDSParser.throwUnexpectedTokenException(tdsReader, this.logContext);
        return false;
    }

    boolean onFeatureExtensionAck(TDSReader tdsReader) throws SQLServerException {
        TDSParser.throwUnexpectedTokenException(tdsReader, this.logContext);
        return false;
    }

    boolean onEnvChange(TDSReader tdsReader) throws SQLServerException {
        tdsReader.getConnection().processEnvChange(tdsReader);
        return true;
    }

    boolean onSessionState(TDSReader tdsReader) throws SQLServerException {
        tdsReader.getConnection().processSessionState(tdsReader);
        return true;
    }

    boolean onRetStatus(TDSReader tdsReader) throws SQLServerException {
        new StreamRetStatus().setFromTDS(tdsReader);
        return true;
    }

    boolean onRetValue(TDSReader tdsReader) throws SQLServerException {
        TDSParser.throwUnexpectedTokenException(tdsReader, this.logContext);
        return false;
    }

    boolean onDone(TDSReader tdsReader) throws SQLServerException {
        StreamDone doneToken = new StreamDone();
        doneToken.setFromTDS(tdsReader);
        if (doneToken.isFinal()) {
            tdsReader.getConnection().getSessionRecovery().decrementUnprocessedResponseCount();
        }
        return true;
    }

    boolean onError(TDSReader tdsReader) throws SQLServerException {
        if (null == this.databaseError) {
            this.databaseError = new SQLServerError();
            this.databaseError.setFromTDS(tdsReader);
        } else {
            new SQLServerError().setFromTDS(tdsReader);
        }
        return true;
    }

    boolean onInfo(TDSReader tdsReader) throws SQLServerException {
        TDSParser.ignoreLengthPrefixedToken(tdsReader);
        return true;
    }

    boolean onOrder(TDSReader tdsReader) throws SQLServerException {
        TDSParser.ignoreLengthPrefixedToken(tdsReader);
        return true;
    }

    boolean onColMetaData(TDSReader tdsReader) throws SQLServerException {
        if (logger.isLoggable(Level.WARNING)) {
            logger.warning(tdsReader.toString() + ": " + this.logContext + ": Discarding unexpected " + TDS.getTokenName(tdsReader.peekTokenType()));
        }
        new StreamColumns(false).setFromTDS(tdsReader);
        return false;
    }

    boolean onRow(TDSReader tdsReader) throws SQLServerException {
        TDSParser.throwUnexpectedTokenException(tdsReader, this.logContext);
        return false;
    }

    boolean onNBCRow(TDSReader tdsReader) throws SQLServerException {
        TDSParser.throwUnexpectedTokenException(tdsReader, this.logContext);
        return false;
    }

    boolean onColInfo(TDSReader tdsReader) throws SQLServerException {
        TDSParser.ignoreLengthPrefixedToken(tdsReader);
        return true;
    }

    boolean onTabName(TDSReader tdsReader) throws SQLServerException {
        TDSParser.ignoreLengthPrefixedToken(tdsReader);
        return true;
    }

    void onEOF(TDSReader tdsReader) throws SQLServerException {
        if (null != this.getDatabaseError()) {
            SQLServerException.makeFromDatabaseError(tdsReader.getConnection(), null, this.getDatabaseError().getErrorMessage(), this.getDatabaseError(), false);
        }
    }

    boolean onFedAuthInfo(TDSReader tdsReader) throws SQLServerException {
        tdsReader.getConnection().processFedAuthInfo(tdsReader, this);
        return true;
    }

    boolean onDataClassification(TDSReader tdsReader) throws SQLServerException {
        TDSParser.throwUnexpectedTokenException(tdsReader, this.logContext);
        return false;
    }
}

