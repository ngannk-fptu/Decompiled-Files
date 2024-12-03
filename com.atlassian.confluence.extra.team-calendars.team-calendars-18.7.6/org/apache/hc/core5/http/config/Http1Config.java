/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.config;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Timeout;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class Http1Config {
    public static final Http1Config DEFAULT = new Builder().build();
    private final int bufferSize;
    private final int chunkSizeHint;
    private final Timeout waitForContinueTimeout;
    private final int maxLineLength;
    private final int maxHeaderCount;
    private final int maxEmptyLineCount;
    private final int initialWindowSize;
    private static final int INIT_WINDOW_SIZE = 65535;
    private static final int INIT_BUF_SIZE = 8192;
    private static final Timeout INIT_WAIT_FOR_CONTINUE = Timeout.ofSeconds(3L);
    private static final int INIT_BUF_CHUNK = -1;
    private static final int INIT_MAX_HEADER_COUNT = -1;
    private static final int INIT_MAX_LINE_LENGTH = -1;
    private static final int INIT_MAX_EMPTY_LINE_COUNT = 10;

    Http1Config(int bufferSize, int chunkSizeHint, Timeout waitForContinueTimeout, int maxLineLength, int maxHeaderCount, int maxEmptyLineCount, int initialWindowSize) {
        this.bufferSize = bufferSize;
        this.chunkSizeHint = chunkSizeHint;
        this.waitForContinueTimeout = waitForContinueTimeout;
        this.maxLineLength = maxLineLength;
        this.maxHeaderCount = maxHeaderCount;
        this.maxEmptyLineCount = maxEmptyLineCount;
        this.initialWindowSize = initialWindowSize;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public int getChunkSizeHint() {
        return this.chunkSizeHint;
    }

    public Timeout getWaitForContinueTimeout() {
        return this.waitForContinueTimeout;
    }

    public int getMaxLineLength() {
        return this.maxLineLength;
    }

    public int getMaxHeaderCount() {
        return this.maxHeaderCount;
    }

    public int getMaxEmptyLineCount() {
        return this.maxEmptyLineCount;
    }

    public int getInitialWindowSize() {
        return this.initialWindowSize;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[bufferSize=").append(this.bufferSize).append(", chunkSizeHint=").append(this.chunkSizeHint).append(", waitForContinueTimeout=").append(this.waitForContinueTimeout).append(", maxLineLength=").append(this.maxLineLength).append(", maxHeaderCount=").append(this.maxHeaderCount).append(", maxEmptyLineCount=").append(this.maxEmptyLineCount).append(", initialWindowSize=").append(this.initialWindowSize).append("]");
        return builder.toString();
    }

    public static Builder custom() {
        return new Builder();
    }

    public static Builder copy(Http1Config config) {
        Args.notNull(config, "Config");
        return new Builder().setBufferSize(config.getBufferSize()).setChunkSizeHint(config.getChunkSizeHint()).setWaitForContinueTimeout(config.getWaitForContinueTimeout()).setMaxHeaderCount(config.getMaxHeaderCount()).setMaxLineLength(config.getMaxLineLength()).setMaxEmptyLineCount(config.getMaxEmptyLineCount()).setInitialWindowSize(config.getInitialWindowSize());
    }

    static /* synthetic */ Timeout access$000() {
        return INIT_WAIT_FOR_CONTINUE;
    }

    public static class Builder {
        private int bufferSize = 8192;
        private int chunkSizeHint = -1;
        private Timeout waitForContinueTimeout = Http1Config.access$000();
        private int maxLineLength = -1;
        private int maxHeaderCount = -1;
        private int maxEmptyLineCount = 10;
        private int initialWindowSize = 65535;

        Builder() {
        }

        public Builder setBufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public Builder setChunkSizeHint(int chunkSizeHint) {
            this.chunkSizeHint = chunkSizeHint;
            return this;
        }

        public Builder setWaitForContinueTimeout(Timeout waitForContinueTimeout) {
            this.waitForContinueTimeout = waitForContinueTimeout;
            return this;
        }

        public Builder setMaxLineLength(int maxLineLength) {
            this.maxLineLength = maxLineLength;
            return this;
        }

        public Builder setMaxHeaderCount(int maxHeaderCount) {
            this.maxHeaderCount = maxHeaderCount;
            return this;
        }

        public Builder setMaxEmptyLineCount(int maxEmptyLineCount) {
            this.maxEmptyLineCount = maxEmptyLineCount;
            return this;
        }

        public Builder setInitialWindowSize(int initialWindowSize) {
            Args.positive(initialWindowSize, "Initial window size");
            this.initialWindowSize = initialWindowSize;
            return this;
        }

        public Http1Config build() {
            return new Http1Config(this.bufferSize, this.chunkSizeHint, this.waitForContinueTimeout, this.maxLineLength, this.maxHeaderCount, this.maxEmptyLineCount, this.initialWindowSize);
        }
    }
}

