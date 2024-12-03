/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.StreamPacket;
import com.microsoft.sqlserver.jdbc.TDSReader;
import java.io.Serializable;

public final class SQLServerError
extends StreamPacket
implements Serializable {
    private static final long serialVersionUID = -7304033613218700719L;
    private String errorMessage = "";
    private int errorNumber;
    private int errorState;
    private int errorSeverity;
    private String serverName;
    private String procName;
    private long lineNumber;

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public int getErrorNumber() {
        return this.errorNumber;
    }

    public int getErrorState() {
        return this.errorState;
    }

    public int getErrorSeverity() {
        return this.errorSeverity;
    }

    public String getServerName() {
        return this.serverName;
    }

    public String getProcedureName() {
        return this.procName;
    }

    public long getLineNumber() {
        return this.lineNumber;
    }

    SQLServerError() {
        super(170);
    }

    @Override
    void setFromTDS(TDSReader tdsReader) throws SQLServerException {
        if (170 != tdsReader.readUnsignedByte()) assert (false);
        this.setContentsFromTDS(tdsReader);
    }

    void setContentsFromTDS(TDSReader tdsReader) throws SQLServerException {
        tdsReader.readUnsignedShort();
        this.errorNumber = tdsReader.readInt();
        this.errorState = tdsReader.readUnsignedByte();
        this.errorSeverity = tdsReader.readUnsignedByte();
        this.errorMessage = tdsReader.readUnicodeString(tdsReader.readUnsignedShort());
        this.serverName = tdsReader.readUnicodeString(tdsReader.readUnsignedByte());
        this.procName = tdsReader.readUnicodeString(tdsReader.readUnsignedByte());
        this.lineNumber = tdsReader.readUnsignedInt();
    }

    static enum TransientError {
        SQLSERVER_ERROR_4060(4060),
        SQLSERVER_ERROR_40197(40197),
        SQLSERVER_ERROR_40143(40143),
        SQLSERVER_ERROR_40166(40166),
        SQLSERVER_ERROR_40540(40540),
        SQLSERVER_ERROR_40501(40501),
        SQLSERVER_ERROR_40613(40613),
        SQLSERVER_ERROR_49918(49918),
        SQLSERVER_ERROR_49919(49919),
        SQLSERVER_ERROR_49920(49920),
        SQLSERVER_ERROR_4221(4221),
        SQLSERVER_ERROR_10928(10928),
        SQLSERVER_ERROR_40020(40020),
        SQLSERVER_ERROR_10929(10929),
        SQLSERVER_ERROR_42108(42108),
        SQLSERVER_ERROR_42109(42109),
        SQLSERVER_ERROR_10053(10053),
        SQLSERVER_ERROR_10054(10054),
        SQLSERVER_ERROR_233(233),
        SQLSERVER_ERROR_64(64);

        private final int errNo;

        private TransientError(int errNo) {
            this.errNo = errNo;
        }

        public int getErrNo() {
            return this.errNo;
        }

        public static boolean isTransientError(SQLServerError sqlServerError) {
            if (null == sqlServerError) {
                return false;
            }
            int errNo = sqlServerError.getErrorNumber();
            for (TransientError p : TransientError.values()) {
                if (errNo != p.errNo) continue;
                return true;
            }
            return false;
        }
    }
}

