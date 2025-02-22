/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.internal.ascii.TextCommandService;
import com.hazelcast.internal.ascii.memcache.DeleteCommand;
import com.hazelcast.internal.ascii.memcache.MemcacheCommandProcessor;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class DeleteCommandProcessor
extends MemcacheCommandProcessor<DeleteCommand> {
    public DeleteCommandProcessor(TextCommandService textCommandService) {
        super(textCommandService);
    }

    @Override
    public void handle(DeleteCommand command) {
        String key;
        try {
            key = URLDecoder.decode(command.getKey(), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(String.format("failed to decode key [%s] using UTF-8", command.getKey()));
        }
        String mapName = "hz_memcache_default";
        int index = key.indexOf(58);
        if (index != -1) {
            mapName = "hz_memcache_" + key.substring(0, index);
            key = key.substring(index + 1);
        }
        if (key.equals("")) {
            this.textCommandService.deleteAll(mapName);
        } else {
            Object oldValue = this.textCommandService.delete(mapName, key);
            if (oldValue == null) {
                this.textCommandService.incrementDeleteMissCount();
                command.setResponse(TextCommandConstants.NOT_FOUND);
            } else {
                this.textCommandService.incrementDeleteHitCount(1);
                command.setResponse(TextCommandConstants.DELETED);
            }
        }
        if (command.shouldReply()) {
            this.textCommandService.sendResponse(command);
        }
    }

    @Override
    public void handleRejection(DeleteCommand command) {
        this.handle(command);
    }
}

