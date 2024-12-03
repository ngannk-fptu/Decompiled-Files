/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.log.jdk14logging;

import com.mchange.v2.log.LogUtils;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.log.jdk14logging.Jdk14LoggingUtils;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class ForwardingLogger
extends Logger {
    MLogger forwardTo;

    public ForwardingLogger(MLogger mLogger, String string) {
        super(mLogger.getName(), string);
        this.forwardTo = mLogger;
    }

    @Override
    public void log(LogRecord logRecord) {
        String string;
        Level level = logRecord.getLevel();
        MLevel mLevel = Jdk14LoggingUtils.mlevelFromLevel(level);
        String string2 = logRecord.getResourceBundleName();
        String string3 = logRecord.getMessage();
        Object[] objectArray = logRecord.getParameters();
        String string4 = LogUtils.formatMessage(string2, string3, objectArray);
        Throwable throwable = logRecord.getThrown();
        String string5 = logRecord.getSourceClassName();
        boolean bl = string5 != null & (string = logRecord.getSourceMethodName()) != null;
        if (!bl) {
            this.forwardTo.log(mLevel, string4, throwable);
        } else {
            this.forwardTo.logp(mLevel, string5, string, string4, throwable);
        }
    }
}

