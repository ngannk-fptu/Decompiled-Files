/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.core5.http2.config;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class H2Config {
    public static final H2Config DEFAULT = H2Config.custom().build();
    public static final H2Config INIT = H2Config.initial().build();
    private final int headerTableSize;
    private final boolean pushEnabled;
    private final int maxConcurrentStreams;
    private final int initialWindowSize;
    private final int maxFrameSize;
    private final int maxHeaderListSize;
    private final boolean compressionEnabled;
    private static final int INIT_HEADER_TABLE_SIZE = 4096;
    private static final boolean INIT_ENABLE_PUSH = true;
    private static final int INIT_MAX_FRAME_SIZE = 16384;
    private static final int INIT_WINDOW_SIZE = 65535;
    private static final int INIT_CONCURRENT_STREAM = 250;

    H2Config(int headerTableSize, boolean pushEnabled, int maxConcurrentStreams, int initialWindowSize, int maxFrameSize, int maxHeaderListSize, boolean compressionEnabled) {
        this.headerTableSize = headerTableSize;
        this.pushEnabled = pushEnabled;
        this.maxConcurrentStreams = maxConcurrentStreams;
        this.initialWindowSize = initialWindowSize;
        this.maxFrameSize = maxFrameSize;
        this.maxHeaderListSize = maxHeaderListSize;
        this.compressionEnabled = compressionEnabled;
    }

    public int getHeaderTableSize() {
        return this.headerTableSize;
    }

    public boolean isPushEnabled() {
        return this.pushEnabled;
    }

    public int getMaxConcurrentStreams() {
        return this.maxConcurrentStreams;
    }

    public int getInitialWindowSize() {
        return this.initialWindowSize;
    }

    public int getMaxFrameSize() {
        return this.maxFrameSize;
    }

    public int getMaxHeaderListSize() {
        return this.maxHeaderListSize;
    }

    public boolean isCompressionEnabled() {
        return this.compressionEnabled;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[headerTableSize=").append(this.headerTableSize).append(", pushEnabled=").append(this.pushEnabled).append(", maxConcurrentStreams=").append(this.maxConcurrentStreams).append(", initialWindowSize=").append(this.initialWindowSize).append(", maxFrameSize=").append(this.maxFrameSize).append(", maxHeaderListSize=").append(this.maxHeaderListSize).append(", compressionEnabled=").append(this.compressionEnabled).append("]");
        return builder.toString();
    }

    public static Builder custom() {
        return new Builder();
    }

    public static Builder initial() {
        return new Builder().setHeaderTableSize(4096).setPushEnabled(true).setMaxConcurrentStreams(Integer.MAX_VALUE).setMaxFrameSize(16384).setInitialWindowSize(65535).setMaxHeaderListSize(Integer.MAX_VALUE);
    }

    public static Builder copy(H2Config config) {
        Args.notNull((Object)config, (String)"Connection config");
        return new Builder().setHeaderTableSize(config.getHeaderTableSize()).setPushEnabled(config.isPushEnabled()).setMaxConcurrentStreams(config.getMaxConcurrentStreams()).setInitialWindowSize(config.getInitialWindowSize()).setMaxFrameSize(config.getMaxFrameSize()).setMaxHeaderListSize(config.getMaxHeaderListSize()).setCompressionEnabled(config.isCompressionEnabled());
    }

    public static class Builder {
        private int headerTableSize = 8192;
        private boolean pushEnabled = true;
        private int maxConcurrentStreams = 250;
        private int initialWindowSize = 65535;
        private int maxFrameSize = 65536;
        private int maxHeaderListSize = 0xFFFFFF;
        private boolean compressionEnabled = true;

        Builder() {
        }

        public Builder setHeaderTableSize(int headerTableSize) {
            Args.notNegative((int)headerTableSize, (String)"Header table size");
            this.headerTableSize = headerTableSize;
            return this;
        }

        public Builder setPushEnabled(boolean pushEnabled) {
            this.pushEnabled = pushEnabled;
            return this;
        }

        public Builder setMaxConcurrentStreams(int maxConcurrentStreams) {
            Args.positive((int)maxConcurrentStreams, (String)"Max concurrent streams");
            this.maxConcurrentStreams = maxConcurrentStreams;
            return this;
        }

        public Builder setInitialWindowSize(int initialWindowSize) {
            Args.positive((int)initialWindowSize, (String)"Initial window size");
            this.initialWindowSize = initialWindowSize;
            return this;
        }

        public Builder setMaxFrameSize(int maxFrameSize) {
            this.maxFrameSize = Args.checkRange((int)maxFrameSize, (int)16384, (int)0xFFFFFF, (String)"Invalid max frame size");
            return this;
        }

        public Builder setMaxHeaderListSize(int maxHeaderListSize) {
            Args.positive((int)maxHeaderListSize, (String)"Max header list size");
            this.maxHeaderListSize = maxHeaderListSize;
            return this;
        }

        public Builder setCompressionEnabled(boolean compressionEnabled) {
            this.compressionEnabled = compressionEnabled;
            return this;
        }

        public H2Config build() {
            return new H2Config(this.headerTableSize, this.pushEnabled, this.maxConcurrentStreams, this.initialWindowSize, this.maxFrameSize, this.maxHeaderListSize, this.compressionEnabled);
        }
    }
}

