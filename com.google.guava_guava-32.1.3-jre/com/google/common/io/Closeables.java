/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.ElementTypesAreNonnullByDefault;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public final class Closeables {
    @VisibleForTesting
    static final Logger logger = Logger.getLogger(Closeables.class.getName());

    private Closeables() {
    }

    public static void close(@CheckForNull Closeable closeable, boolean swallowIOException) throws IOException {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        }
        catch (IOException e) {
            if (swallowIOException) {
                logger.log(Level.WARNING, "IOException thrown while closing Closeable.", e);
            }
            throw e;
        }
    }

    public static void closeQuietly(@CheckForNull InputStream inputStream) {
        try {
            Closeables.close(inputStream, true);
        }
        catch (IOException impossible) {
            throw new AssertionError((Object)impossible);
        }
    }

    public static void closeQuietly(@CheckForNull Reader reader) {
        try {
            Closeables.close(reader, true);
        }
        catch (IOException impossible) {
            throw new AssertionError((Object)impossible);
        }
    }
}

