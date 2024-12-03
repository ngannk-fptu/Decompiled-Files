/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.logging.sink;

import com.atlassian.confluence.logging.sink.LogEvent;
import java.util.function.Consumer;

public interface LogConsumerService {
    public void registerLogConsumer(String var1, Consumer<LogEvent> var2);

    public void unregisterLogConsumer(String var1);
}

