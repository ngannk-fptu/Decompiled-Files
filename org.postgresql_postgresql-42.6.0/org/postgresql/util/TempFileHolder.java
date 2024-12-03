/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.postgresql.util.GT;
import org.postgresql.util.LazyCleaner;
import org.postgresql.util.StreamWrapper;
import org.postgresql.util.internal.Nullness;

class TempFileHolder
implements LazyCleaner.CleaningAction<IOException> {
    private static final Logger LOGGER = Logger.getLogger(StreamWrapper.class.getName());
    private @Nullable InputStream stream;
    private @Nullable Path tempFile;

    TempFileHolder(Path tempFile) {
        this.tempFile = tempFile;
    }

    public InputStream getStream() throws IOException {
        InputStream stream = this.stream;
        if (stream == null) {
            this.stream = stream = Files.newInputStream(Nullness.castNonNull(this.tempFile), new OpenOption[0]);
        }
        return stream;
    }

    @Override
    public void onClean(boolean leak) throws IOException {
        InputStream stream;
        Path tempFile;
        if (leak) {
            LOGGER.log(Level.WARNING, GT.tr("StreamWrapper leak detected StreamWrapper.close() was not called. ", new Object[0]));
        }
        if ((tempFile = this.tempFile) != null) {
            tempFile.toFile().delete();
            this.tempFile = null;
        }
        if ((stream = this.stream) != null) {
            stream.close();
            this.stream = null;
        }
    }
}

