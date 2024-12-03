/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.internal.ascii.TextCommandServiceImpl;
import com.hazelcast.internal.ascii.memcache.IncrementCommand;
import com.hazelcast.internal.ascii.memcache.MemcacheCommandProcessor;
import com.hazelcast.internal.ascii.memcache.MemcacheEntry;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.StringUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class IncrementCommandProcessor
extends MemcacheCommandProcessor<IncrementCommand> {
    public IncrementCommandProcessor(TextCommandServiceImpl textCommandService) {
        super(textCommandService);
    }

    @Override
    public void handle(IncrementCommand incrementCommand) {
        String key;
        try {
            key = URLDecoder.decode(incrementCommand.getKey(), "UTF-8");
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
        try {
            this.textCommandService.lock(mapName, key);
        }
        catch (Exception e) {
            incrementCommand.setResponse(TextCommandConstants.NOT_FOUND);
            if (incrementCommand.shouldReply()) {
                this.textCommandService.sendResponse(incrementCommand);
            }
            return;
        }
        this.incrementUnderLock(incrementCommand, key, mapName);
        this.textCommandService.unlock(mapName, key);
        if (incrementCommand.shouldReply()) {
            this.textCommandService.sendResponse(incrementCommand);
        }
    }

    private void incrementUnderLock(IncrementCommand incrementCommand, String key, String mapName) {
        Object value = this.textCommandService.get(mapName, key);
        if (value != null) {
            MemcacheEntry entry;
            if (value instanceof MemcacheEntry) {
                entry = (MemcacheEntry)value;
            } else if (value instanceof byte[]) {
                entry = new MemcacheEntry(incrementCommand.getKey(), (byte[])value, 0);
            } else if (value instanceof String) {
                entry = new MemcacheEntry(incrementCommand.getKey(), StringUtil.stringToBytes((String)value), 0);
            } else {
                try {
                    entry = new MemcacheEntry(incrementCommand.getKey(), this.textCommandService.toByteArray(value), 0);
                }
                catch (Exception e) {
                    throw ExceptionUtil.rethrow(e);
                }
            }
            byte[] value1 = entry.getValue();
            long current = value1 == null || value1.length == 0 ? 0L : (long)IncrementCommandProcessor.byteArrayToLong(value1);
            long result = -1L;
            result = this.incrementCommandTypeCheck(incrementCommand, result, current);
            incrementCommand.setResponse(IncrementCommandProcessor.concatenate(StringUtil.stringToBytes(String.valueOf(result)), TextCommandConstants.RETURN));
            MemcacheEntry newValue = new MemcacheEntry(key, IncrementCommandProcessor.longToByteArray(result), entry.getFlag());
            this.textCommandService.put(mapName, key, newValue);
        } else {
            if (incrementCommand.getType() == TextCommandConstants.TextCommandType.INCREMENT) {
                this.textCommandService.incrementIncMissCount();
            } else {
                this.textCommandService.incrementDecrMissCount();
            }
            incrementCommand.setResponse(TextCommandConstants.NOT_FOUND);
        }
    }

    @Override
    public void handleRejection(IncrementCommand incrementCommand) {
        incrementCommand.setResponse(TextCommandConstants.NOT_FOUND);
        if (incrementCommand.shouldReply()) {
            this.textCommandService.sendResponse(incrementCommand);
        }
    }

    private long incrementCommandTypeCheck(IncrementCommand incrementCommand, long result, long current) {
        long paramResult = result;
        if (incrementCommand.getType() == TextCommandConstants.TextCommandType.INCREMENT) {
            paramResult = current + (long)incrementCommand.getValue();
            paramResult = 0L > paramResult ? Long.MAX_VALUE : paramResult;
            this.textCommandService.incrementIncHitCount();
        } else if (incrementCommand.getType() == TextCommandConstants.TextCommandType.DECREMENT) {
            paramResult = current - (long)incrementCommand.getValue();
            paramResult = 0L > paramResult ? 0L : paramResult;
            this.textCommandService.incrementDecrHitCount();
        }
        return paramResult;
    }
}

