/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io;

import com.mchange.util.RobustMessageLogger;
import java.io.IOException;
import java.io.Reader;

public final class ReaderUtils {
    public static void attemptClose(Reader reader) {
        ReaderUtils.attemptClose(reader, null);
    }

    public static void attemptClose(Reader reader, RobustMessageLogger robustMessageLogger) {
        block4: {
            try {
                reader.close();
            }
            catch (IOException iOException) {
                if (robustMessageLogger != null) {
                    robustMessageLogger.log(iOException, "IOException trying to close Reader");
                }
            }
            catch (NullPointerException nullPointerException) {
                if (robustMessageLogger == null) break block4;
                robustMessageLogger.log(nullPointerException, "NullPointerException trying to close Reader");
            }
        }
    }

    private ReaderUtils() {
    }
}

