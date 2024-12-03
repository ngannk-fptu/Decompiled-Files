/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii;

import com.hazelcast.internal.ascii.AbstractTextCommandProcessor;
import com.hazelcast.internal.ascii.NoOpCommand;
import com.hazelcast.internal.ascii.TextCommandService;

public class NoOpCommandProcessor
extends AbstractTextCommandProcessor<NoOpCommand> {
    public NoOpCommandProcessor(TextCommandService textCommandService) {
        super(textCommandService);
    }

    @Override
    public void handle(NoOpCommand command) {
        this.textCommandService.sendResponse(command);
    }

    @Override
    public void handleRejection(NoOpCommand command) {
        this.handle(command);
    }
}

