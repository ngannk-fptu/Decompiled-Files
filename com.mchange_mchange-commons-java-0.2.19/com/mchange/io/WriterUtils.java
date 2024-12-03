/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io;

import com.mchange.util.RobustMessageLogger;
import java.io.IOException;
import java.io.Writer;

public final class WriterUtils {
    public static void attemptClose(Writer writer) {
        WriterUtils.attemptClose(writer, null);
    }

    public static void attemptClose(Writer writer, RobustMessageLogger robustMessageLogger) {
        block4: {
            try {
                writer.close();
            }
            catch (IOException iOException) {
                if (robustMessageLogger != null) {
                    robustMessageLogger.log(iOException, "IOException trying to close Writer");
                }
            }
            catch (NullPointerException nullPointerException) {
                if (robustMessageLogger == null) break block4;
                robustMessageLogger.log(nullPointerException, "NullPointerException trying to close Writer");
            }
        }
    }

    private WriterUtils() {
    }
}

