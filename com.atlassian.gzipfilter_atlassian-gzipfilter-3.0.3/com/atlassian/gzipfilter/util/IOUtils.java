/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gzipfilter.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class IOUtils {
    public static void closeQuietly(Reader input) {
        try {
            if (input != null) {
                input.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void closeQuietly(Writer output) {
        try {
            if (output != null) {
                output.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void closeQuietly(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void closeQuietly(OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

