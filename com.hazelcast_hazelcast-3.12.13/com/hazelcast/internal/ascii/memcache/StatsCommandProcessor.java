/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.TextCommandService;
import com.hazelcast.internal.ascii.memcache.MemcacheCommandProcessor;
import com.hazelcast.internal.ascii.memcache.Stats;
import com.hazelcast.internal.ascii.memcache.StatsCommand;

public class StatsCommandProcessor
extends MemcacheCommandProcessor<StatsCommand> {
    public StatsCommandProcessor(TextCommandService textCommandService) {
        super(textCommandService);
    }

    @Override
    public void handle(StatsCommand command) {
        Stats stats = this.textCommandService.getStats();
        command.setResponse(stats);
        this.textCommandService.sendResponse(command);
    }

    @Override
    public void handleRejection(StatsCommand command) {
        this.handle(command);
    }
}

