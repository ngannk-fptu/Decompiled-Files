/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.TextCommandService;
import com.hazelcast.internal.ascii.memcache.EntryConverter;
import com.hazelcast.internal.ascii.memcache.GetCommand;
import com.hazelcast.internal.ascii.memcache.MapNameAndKeyPair;
import com.hazelcast.internal.ascii.memcache.MemcacheCommandProcessor;
import com.hazelcast.internal.ascii.memcache.MemcacheEntry;
import com.hazelcast.internal.ascii.memcache.MemcacheUtils;

public class GetCommandProcessor
extends MemcacheCommandProcessor<GetCommand> {
    private final EntryConverter entryConverter;

    public GetCommandProcessor(TextCommandService textCommandService, EntryConverter entryConverter) {
        super(textCommandService);
        this.entryConverter = entryConverter;
    }

    @Override
    @Deprecated
    public void handle(GetCommand getCommand) {
        MapNameAndKeyPair mapNameAndKeyPair;
        Object value;
        String memcacheKey = getCommand.getKey();
        MemcacheEntry entry = this.entryConverter.toEntry(memcacheKey, value = this.textCommandService.get((mapNameAndKeyPair = MemcacheUtils.parseMemcacheKey(memcacheKey)).getMapName(), mapNameAndKeyPair.getKey()));
        if (entry != null) {
            this.textCommandService.incrementGetHitCount();
        } else {
            this.textCommandService.incrementGetMissCount();
        }
        getCommand.setValue(entry);
        this.textCommandService.sendResponse(getCommand);
    }

    @Override
    public void handleRejection(GetCommand getCommand) {
        getCommand.setValue(null);
        this.textCommandService.sendResponse(getCommand);
    }
}

