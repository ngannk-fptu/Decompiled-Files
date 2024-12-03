/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.TextCommandServiceImpl;
import com.hazelcast.internal.ascii.memcache.MemcacheCommandProcessor;
import com.hazelcast.internal.ascii.memcache.VersionCommand;

public class VersionCommandProcessor
extends MemcacheCommandProcessor<VersionCommand> {
    public VersionCommandProcessor(TextCommandServiceImpl textCommandService) {
        super(textCommandService);
    }

    @Override
    public void handle(VersionCommand request) {
        this.textCommandService.sendResponse(request);
    }

    @Override
    public void handleRejection(VersionCommand request) {
        this.handle(request);
    }
}

