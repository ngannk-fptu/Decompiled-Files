/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io;

import com.mchange.util.RobustMessageLogger;
import java.io.IOException;
import java.io.OutputStream;

public final class OutputStreamUtils {
    public static void attemptClose(OutputStream outputStream) {
        OutputStreamUtils.attemptClose(outputStream, null);
    }

    public static void attemptClose(OutputStream outputStream, RobustMessageLogger robustMessageLogger) {
        block4: {
            try {
                outputStream.close();
            }
            catch (IOException iOException) {
                if (robustMessageLogger != null) {
                    robustMessageLogger.log(iOException, "IOException trying to close OutputStream");
                }
            }
            catch (NullPointerException nullPointerException) {
                if (robustMessageLogger == null) break block4;
                robustMessageLogger.log(nullPointerException, "NullPointerException trying to close OutputStream");
            }
        }
    }

    private OutputStreamUtils() {
    }
}

