/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.TDSCommand;
import java.util.logging.Level;

abstract class UninterruptableTDSCommand
extends TDSCommand {
    private static final long serialVersionUID = -6457195977162963793L;

    UninterruptableTDSCommand(String logContext) {
        super(logContext, 0, 0);
    }

    @Override
    final void interrupt(String reason) throws SQLServerException {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(this.toString() + " Ignoring interrupt of uninterruptable TDS command; Reason:" + reason);
        }
    }
}

