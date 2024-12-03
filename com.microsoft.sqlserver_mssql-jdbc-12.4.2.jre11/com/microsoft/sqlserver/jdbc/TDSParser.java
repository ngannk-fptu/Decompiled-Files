/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.TDS;
import com.microsoft.sqlserver.jdbc.TDSReader;
import com.microsoft.sqlserver.jdbc.TDSTokenHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

final class TDSParser {
    private static Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.TDS.TOKEN");

    private TDSParser() {
        throw new UnsupportedOperationException(SQLServerException.getErrString("R_notSupported"));
    }

    static void parse(TDSReader tdsReader, String logContext) throws SQLServerException {
        TDSParser.parse(tdsReader, new TDSTokenHandler(logContext));
    }

    static void parse(TDSReader tdsReader, TDSTokenHandler tdsTokenHandler) throws SQLServerException {
        TDSParser.parse(tdsReader, tdsTokenHandler, false);
    }

    static void parse(TDSReader tdsReader, TDSTokenHandler tdsTokenHandler, boolean readOnlyWarningsFlag) throws SQLServerException {
        boolean isLogging = logger.isLoggable(Level.FINEST);
        boolean parsing = true;
        boolean isLoginAck = false;
        boolean isFeatureExtAck = false;
        block21: while (parsing) {
            int tdsTokenType = tdsReader.peekTokenType();
            if (isLogging) {
                logger.finest(tdsReader.toString() + ": " + tdsTokenHandler.logContext + ": Processing " + (-1 == tdsTokenType ? "EOF" : TDS.getTokenName(tdsTokenType)));
            }
            if (readOnlyWarningsFlag && 171 != tdsTokenType) {
                return;
            }
            switch (tdsTokenType) {
                case 237: {
                    parsing = tdsTokenHandler.onSSPI(tdsReader);
                    continue block21;
                }
                case 173: {
                    isLoginAck = true;
                    parsing = tdsTokenHandler.onLoginAck(tdsReader);
                    continue block21;
                }
                case 174: {
                    isFeatureExtAck = true;
                    tdsReader.getConnection().processFeatureExtAck(tdsReader);
                    parsing = true;
                    continue block21;
                }
                case 227: {
                    parsing = tdsTokenHandler.onEnvChange(tdsReader);
                    continue block21;
                }
                case 228: {
                    parsing = tdsTokenHandler.onSessionState(tdsReader);
                    continue block21;
                }
                case 121: {
                    parsing = tdsTokenHandler.onRetStatus(tdsReader);
                    continue block21;
                }
                case 172: {
                    parsing = tdsTokenHandler.onRetValue(tdsReader);
                    continue block21;
                }
                case 253: 
                case 254: 
                case 255: {
                    tdsReader.getCommand().checkForInterrupt();
                    parsing = tdsTokenHandler.onDone(tdsReader);
                    continue block21;
                }
                case 170: {
                    parsing = tdsTokenHandler.onError(tdsReader);
                    continue block21;
                }
                case 171: {
                    parsing = tdsTokenHandler.onInfo(tdsReader);
                    continue block21;
                }
                case 169: {
                    parsing = tdsTokenHandler.onOrder(tdsReader);
                    continue block21;
                }
                case 129: {
                    parsing = tdsTokenHandler.onColMetaData(tdsReader);
                    continue block21;
                }
                case 209: {
                    parsing = tdsTokenHandler.onRow(tdsReader);
                    continue block21;
                }
                case 210: {
                    parsing = tdsTokenHandler.onNBCRow(tdsReader);
                    continue block21;
                }
                case 165: {
                    parsing = tdsTokenHandler.onColInfo(tdsReader);
                    continue block21;
                }
                case 164: {
                    parsing = tdsTokenHandler.onTabName(tdsReader);
                    continue block21;
                }
                case 238: {
                    parsing = tdsTokenHandler.onFedAuthInfo(tdsReader);
                    continue block21;
                }
                case 163: {
                    parsing = tdsTokenHandler.onDataClassification(tdsReader);
                    continue block21;
                }
                case -1: {
                    tdsReader.getCommand().onTokenEOF();
                    tdsTokenHandler.onEOF(tdsReader);
                    parsing = false;
                    continue block21;
                }
            }
            TDSParser.throwUnexpectedTokenException(tdsReader, tdsTokenHandler.logContext);
        }
        if (isLoginAck && !isFeatureExtAck) {
            tdsReader.tryProcessFeatureExtAck(isFeatureExtAck);
        }
    }

    static void throwUnexpectedTokenException(TDSReader tdsReader, String logContext) throws SQLServerException {
        if (logger.isLoggable(Level.SEVERE)) {
            logger.severe(tdsReader.toString() + ": " + logContext + ": Encountered unexpected " + TDS.getTokenName(tdsReader.peekTokenType()));
        }
        tdsReader.throwInvalidTDSToken(TDS.getTokenName(tdsReader.peekTokenType()));
    }

    static void ignoreLengthPrefixedToken(TDSReader tdsReader) throws SQLServerException {
        tdsReader.readUnsignedByte();
        int envValueLength = tdsReader.readUnsignedShort();
        byte[] envValueData = new byte[envValueLength];
        tdsReader.readBytes(envValueData, 0, envValueLength);
    }
}

