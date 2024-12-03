/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.io;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.log.NullMLogger;
import java.io.IOException;
import java.io.OutputStream;

public final class OutputStreamUtils {
    private static final MLogger logger = MLog.getLogger(OutputStreamUtils.class);

    public static void attemptClose(OutputStream outputStream, MLogger mLogger) {
        block3: {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            catch (IOException iOException) {
                if (!mLogger.isLoggable(MLevel.WARNING)) break block3;
                mLogger.log(MLevel.WARNING, "OutputStream close FAILED.", iOException);
            }
        }
    }

    public static void attemptClose(OutputStream outputStream) {
        OutputStreamUtils.attemptClose(outputStream, logger);
    }

    public static void attemptCloseIgnore(OutputStream outputStream) {
        OutputStreamUtils.attemptClose(outputStream, NullMLogger.instance());
    }

    private OutputStreamUtils() {
    }
}

