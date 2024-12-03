/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.postgresql.util.GT;
import org.postgresql.util.LazyCleaner;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;
import org.postgresql.util.TempFileHolder;
import org.postgresql.util.internal.Nullness;

public final class StreamWrapper
implements Closeable {
    private static final int MAX_MEMORY_BUFFER_BYTES = 51200;
    private static final String TEMP_FILE_PREFIX = "postgres-pgjdbc-stream";
    private final @Nullable InputStream stream;
    private @Nullable TempFileHolder tempFileHolder;
    private final Object leakHandle;
    private @Nullable LazyCleaner.Cleanable<IOException> cleaner;
    private final byte @Nullable [] rawData;
    private final int offset;
    private final int length;

    public StreamWrapper(byte[] data, int offset, int length) {
        this.leakHandle = new Object();
        this.stream = null;
        this.rawData = data;
        this.offset = offset;
        this.length = length;
    }

    public StreamWrapper(InputStream stream, int length) {
        this.leakHandle = new Object();
        this.stream = stream;
        this.rawData = null;
        this.offset = 0;
        this.length = length;
    }

    public StreamWrapper(InputStream stream) throws PSQLException {
        block21: {
            this.leakHandle = new Object();
            try {
                ByteArrayOutputStream memoryOutputStream = new ByteArrayOutputStream();
                int memoryLength = StreamWrapper.copyStream(stream, memoryOutputStream, 51200);
                byte[] rawData = memoryOutputStream.toByteArray();
                if (memoryLength == -1) {
                    TempFileHolder tempFileHolder;
                    int diskLength;
                    Path tempFile = Files.createTempFile(TEMP_FILE_PREFIX, ".tmp", new FileAttribute[0]);
                    try (OutputStream diskOutputStream = Files.newOutputStream(tempFile, new OpenOption[0]);){
                        diskOutputStream.write(rawData);
                        diskLength = StreamWrapper.copyStream(stream, diskOutputStream, Integer.MAX_VALUE - rawData.length);
                        if (diskLength == -1) {
                            throw new PSQLException(GT.tr("Object is too large to send over the protocol.", new Object[0]), PSQLState.NUMERIC_CONSTANT_OUT_OF_RANGE);
                        }
                    }
                    catch (Error | RuntimeException | PSQLException e) {
                        try {
                            tempFile.toFile().delete();
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                        throw e;
                    }
                    this.offset = 0;
                    this.length = rawData.length + diskLength;
                    this.rawData = null;
                    this.stream = null;
                    this.tempFileHolder = tempFileHolder = new TempFileHolder(tempFile);
                    this.cleaner = LazyCleaner.getInstance().register(this.leakHandle, tempFileHolder);
                    break block21;
                }
                this.rawData = rawData;
                this.stream = null;
                this.offset = 0;
                this.length = rawData.length;
            }
            catch (IOException e) {
                throw new PSQLException(GT.tr("An I/O error occurred while sending to the backend.", new Object[0]), PSQLState.IO_ERROR, (Throwable)e);
            }
        }
    }

    public InputStream getStream() throws IOException {
        if (this.stream != null) {
            return this.stream;
        }
        TempFileHolder finalizeAction = this.tempFileHolder;
        if (finalizeAction != null) {
            return finalizeAction.getStream();
        }
        return new ByteArrayInputStream(Nullness.castNonNull(this.rawData), this.offset, this.length);
    }

    @Override
    public void close() throws IOException {
        if (this.cleaner != null) {
            this.cleaner.clean();
        }
    }

    public int getLength() {
        return this.length;
    }

    public int getOffset() {
        return this.offset;
    }

    public byte @Nullable [] getBytes() {
        return this.rawData;
    }

    public String toString() {
        return "<stream of " + this.length + " bytes>";
    }

    private static int copyStream(InputStream inputStream, OutputStream outputStream, int limit) throws IOException {
        int totalLength = 0;
        byte[] buffer = new byte[2048];
        int readLength = inputStream.read(buffer);
        while (readLength > 0) {
            outputStream.write(buffer, 0, readLength);
            if ((totalLength += readLength) >= limit) {
                return -1;
            }
            readLength = inputStream.read(buffer);
        }
        return totalLength;
    }
}

