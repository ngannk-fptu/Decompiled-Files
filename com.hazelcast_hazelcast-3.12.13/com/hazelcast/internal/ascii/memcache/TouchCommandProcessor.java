/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.internal.ascii.TextCommandServiceImpl;
import com.hazelcast.internal.ascii.memcache.MemcacheCommandProcessor;
import com.hazelcast.internal.ascii.memcache.TouchCommand;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class TouchCommandProcessor
extends MemcacheCommandProcessor<TouchCommand> {
    public TouchCommandProcessor(TextCommandServiceImpl textCommandService) {
        super(textCommandService);
    }

    @Override
    public void handle(TouchCommand touchCommand) {
        String key;
        try {
            key = URLDecoder.decode(touchCommand.getKey(), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new HazelcastException(e);
        }
        String mapName = "hz_memcache_default";
        int index = key.indexOf(58);
        if (index != -1) {
            mapName = "hz_memcache_" + key.substring(0, index);
            key = key.substring(index + 1);
        }
        int ttl = this.textCommandService.getAdjustedTTLSeconds(touchCommand.getExpiration());
        try {
            this.textCommandService.lock(mapName, key);
        }
        catch (Exception e) {
            touchCommand.setResponse(TextCommandConstants.NOT_STORED);
            if (touchCommand.shouldReply()) {
                this.textCommandService.sendResponse(touchCommand);
            }
            return;
        }
        Object value = this.textCommandService.get(mapName, key);
        this.textCommandService.incrementTouchCount();
        if (value != null) {
            this.textCommandService.put(mapName, key, value, ttl);
            touchCommand.setResponse(TextCommandConstants.TOUCHED);
        } else {
            touchCommand.setResponse(TextCommandConstants.NOT_STORED);
        }
        this.textCommandService.unlock(mapName, key);
        if (touchCommand.shouldReply()) {
            this.textCommandService.sendResponse(touchCommand);
        }
    }

    @Override
    public void handleRejection(TouchCommand request) {
        request.setResponse(TextCommandConstants.NOT_STORED);
        if (request.shouldReply()) {
            this.textCommandService.sendResponse(request);
        }
    }
}

