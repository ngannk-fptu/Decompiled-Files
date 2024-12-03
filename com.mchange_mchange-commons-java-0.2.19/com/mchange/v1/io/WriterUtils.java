/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.io;

import java.io.IOException;
import java.io.Writer;

public final class WriterUtils {
    public static void attemptClose(Writer writer) {
        try {
            if (writer != null) {
                writer.close();
            }
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    private WriterUtils() {
    }
}

