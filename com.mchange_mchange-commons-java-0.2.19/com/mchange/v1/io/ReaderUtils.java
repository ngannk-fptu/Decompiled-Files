/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.io;

import java.io.IOException;
import java.io.Reader;

public final class ReaderUtils {
    public static void attemptClose(Reader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    private ReaderUtils() {
    }
}

