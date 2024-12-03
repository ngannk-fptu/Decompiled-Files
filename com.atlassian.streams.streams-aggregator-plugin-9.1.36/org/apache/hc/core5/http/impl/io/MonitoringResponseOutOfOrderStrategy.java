/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.impl.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.HttpClientConnection;
import org.apache.hc.core5.http.io.ResponseOutOfOrderStrategy;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Timeout;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public final class MonitoringResponseOutOfOrderStrategy
implements ResponseOutOfOrderStrategy {
    private static final int DEFAULT_CHUNK_SIZE = 8192;
    public static final MonitoringResponseOutOfOrderStrategy INSTANCE = new MonitoringResponseOutOfOrderStrategy();
    private final long chunkSize;
    private final long maxChunksToCheck;

    public MonitoringResponseOutOfOrderStrategy() {
        this(8192L);
    }

    public MonitoringResponseOutOfOrderStrategy(long chunkSize) {
        this(chunkSize, Long.MAX_VALUE);
    }

    public MonitoringResponseOutOfOrderStrategy(long chunkSize, long maxChunksToCheck) {
        this.chunkSize = Args.positive(chunkSize, "chunkSize");
        this.maxChunksToCheck = Args.positive(maxChunksToCheck, "maxChunksToCheck");
    }

    @Override
    public boolean isEarlyResponseDetected(ClassicHttpRequest request, HttpClientConnection connection, InputStream inputStream, long totalBytesSent, long nextWriteSize) throws IOException {
        if (this.nextWriteStartsNewChunk(totalBytesSent, nextWriteSize)) {
            boolean ssl;
            boolean bl = ssl = connection.getSSLSession() != null;
            return ssl ? connection.isDataAvailable(Timeout.ONE_MILLISECOND) : inputStream.available() > 0;
        }
        return false;
    }

    private boolean nextWriteStartsNewChunk(long totalBytesSent, long nextWriteSize) {
        long newChunkIndex;
        long currentChunkIndex = Math.min(totalBytesSent / this.chunkSize, this.maxChunksToCheck);
        return currentChunkIndex < (newChunkIndex = Math.min((totalBytesSent + nextWriteSize) / this.chunkSize, this.maxChunksToCheck));
    }

    public String toString() {
        return "DefaultResponseOutOfOrderStrategy{chunkSize=" + this.chunkSize + ", maxChunksToCheck=" + this.maxChunksToCheck + '}';
    }
}

