/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ISQLServerSavepoint;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.text.MessageFormat;

public final class SQLServerSavepoint
implements ISQLServerSavepoint {
    private static final long serialVersionUID = 1857415943191289598L;
    private final String sName;
    private final int nId;
    private final SQLServerConnection con;

    public SQLServerSavepoint(SQLServerConnection con, String sName) {
        this.con = con;
        if (sName == null) {
            this.nId = con.getNextSavepointId();
            this.sName = null;
        } else {
            this.sName = sName;
            this.nId = 0;
        }
    }

    @Override
    public String getSavepointName() throws SQLServerException {
        if (this.sName == null) {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_savepointNotNamed"), null, false);
        }
        return this.sName;
    }

    @Override
    public String getLabel() {
        if (this.sName == null) {
            return "S" + this.nId;
        }
        return this.sName;
    }

    @Override
    public boolean isNamed() {
        return this.sName != null;
    }

    @Override
    public int getSavepointId() throws SQLServerException {
        if (this.sName != null) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_savepointNamed"));
            Object[] msgArgs = new Object[]{this.sName};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, false);
        }
        return this.nId;
    }
}

