/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.StreamType;

final class InputStreamGetterArgs {
    final StreamType streamType;
    final boolean isAdaptive;
    final boolean isStreaming;
    final String logContext;
    static final InputStreamGetterArgs defaultArgs = new InputStreamGetterArgs(StreamType.NONE, false, false, "");

    static final InputStreamGetterArgs getDefaultArgs() {
        return defaultArgs;
    }

    InputStreamGetterArgs(StreamType streamType, boolean isAdaptive, boolean isStreaming, String logContext) {
        this.streamType = streamType;
        this.isAdaptive = isAdaptive;
        this.isStreaming = isStreaming;
        this.logContext = logContext;
    }
}

