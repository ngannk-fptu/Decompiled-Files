/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.io.ElementTypesAreNonnullByDefault;
import java.io.Flushable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public final class Flushables {
    private static final Logger logger = Logger.getLogger(Flushables.class.getName());

    private Flushables() {
    }

    public static void flush(Flushable flushable, boolean swallowIOException) throws IOException {
        try {
            flushable.flush();
        }
        catch (IOException e) {
            if (swallowIOException) {
                logger.log(Level.WARNING, "IOException thrown while flushing Flushable.", e);
            }
            throw e;
        }
    }

    @Beta
    public static void flushQuietly(Flushable flushable) {
        try {
            Flushables.flush(flushable, true);
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, "IOException should not have been thrown.", e);
        }
    }
}

