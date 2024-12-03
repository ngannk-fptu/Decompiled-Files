/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii;

import com.hazelcast.internal.ascii.TextCommandProcessor;
import com.hazelcast.internal.ascii.TextCommandService;

public abstract class AbstractTextCommandProcessor<T>
implements TextCommandProcessor<T> {
    protected final TextCommandService textCommandService;

    protected AbstractTextCommandProcessor(TextCommandService textCommandService) {
        this.textCommandService = textCommandService;
    }
}

