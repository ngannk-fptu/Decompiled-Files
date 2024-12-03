/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ICounter;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MaxResultBufferCounter
implements ICounter {
    private final Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.MaxResultBufferCounter");
    private long counter = 0L;
    private final long maxResultBuffer;

    public MaxResultBufferCounter(long maxResultBuffer) {
        this.maxResultBuffer = maxResultBuffer;
    }

    @Override
    public void increaseCounter(long bytes) throws SQLServerException {
        if (this.maxResultBuffer > 0L) {
            this.counter += bytes;
            this.checkForMaxResultBufferOverflow(this.counter);
        }
    }

    @Override
    public void resetCounter() {
        this.counter = 0L;
    }

    private void checkForMaxResultBufferOverflow(long number) throws SQLServerException {
        if (number > this.maxResultBuffer) {
            if (this.logger.isLoggable(Level.SEVERE)) {
                this.logger.log(Level.SEVERE, SQLServerException.getErrString("R_maxResultBufferPropertyExceeded"), new Object[]{number, this.maxResultBuffer});
            }
            this.throwExceededMaxResultBufferException(this.counter, this.maxResultBuffer);
        }
    }

    private void throwExceededMaxResultBufferException(Object ... arguments) throws SQLServerException {
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_maxResultBufferPropertyExceeded"));
        throw new SQLServerException(form.format(arguments), null);
    }
}

