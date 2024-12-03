/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.AbstractTextCommandProcessor;
import com.hazelcast.internal.ascii.TextCommandService;
import com.hazelcast.internal.ascii.memcache.ErrorCommand;

public class ErrorCommandProcessor
extends AbstractTextCommandProcessor<ErrorCommand> {
    public ErrorCommandProcessor(TextCommandService textCommandService) {
        super(textCommandService);
    }

    @Override
    public void handle(ErrorCommand command) {
        this.textCommandService.sendResponse(command);
    }

    @Override
    public void handleRejection(ErrorCommand command) {
        this.handle(command);
    }
}

