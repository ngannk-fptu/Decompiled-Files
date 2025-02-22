/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.internal.ascii.TextCommandService;
import com.hazelcast.internal.ascii.memcache.MemcacheCommandProcessor;
import com.hazelcast.internal.ascii.memcache.MemcacheEntry;
import com.hazelcast.internal.ascii.memcache.SetCommand;
import com.hazelcast.logging.ILogger;
import com.hazelcast.util.StringUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class SetCommandProcessor
extends MemcacheCommandProcessor<SetCommand> {
    private final ILogger logger;

    public SetCommandProcessor(TextCommandService textCommandService) {
        super(textCommandService);
        this.logger = textCommandService.getNode().getLogger(this.getClass().getName());
    }

    @Override
    public void handle(SetCommand setCommand) {
        String key = null;
        try {
            key = URLDecoder.decode(setCommand.getKey(), "UTF-8");
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
        MemcacheEntry value = new MemcacheEntry(setCommand.getKey(), setCommand.getValue(), setCommand.getFlag());
        int ttl = this.textCommandService.getAdjustedTTLSeconds(setCommand.getExpiration());
        this.textCommandService.incrementSetCount();
        if (TextCommandConstants.TextCommandType.SET == setCommand.getType()) {
            this.textCommandService.put(mapName, key, value, ttl);
            setCommand.setResponse(TextCommandConstants.STORED);
        } else if (TextCommandConstants.TextCommandType.ADD == setCommand.getType()) {
            this.addCommandType(setCommand, mapName, key, value, ttl);
        } else if (TextCommandConstants.TextCommandType.REPLACE == setCommand.getType()) {
            this.replaceCommandType(setCommand, mapName, key, value, ttl);
        } else if (TextCommandConstants.TextCommandType.APPEND == setCommand.getType()) {
            this.appendCommandType(setCommand, mapName, key, ttl);
        } else if (TextCommandConstants.TextCommandType.PREPEND == setCommand.getType()) {
            this.prependCommandType(setCommand, mapName, key, ttl);
        }
        if (setCommand.shouldReply()) {
            this.textCommandService.sendResponse(setCommand);
        }
    }

    private void replaceCommandType(SetCommand setCommand, String mapName, String key, Object value, int ttl) {
        boolean replaced;
        boolean bl = replaced = this.textCommandService.replace(mapName, key, value) != null;
        if (replaced) {
            setCommand.setResponse(TextCommandConstants.STORED);
        } else {
            setCommand.setResponse(TextCommandConstants.NOT_STORED);
        }
    }

    private void addCommandType(SetCommand setCommand, String mapName, String key, Object value, int ttl) {
        boolean added;
        boolean bl = added = this.textCommandService.putIfAbsent(mapName, key, value, ttl) == null;
        if (added) {
            setCommand.setResponse(TextCommandConstants.STORED);
        } else {
            setCommand.setResponse(TextCommandConstants.NOT_STORED);
        }
    }

    private void prependCommandType(SetCommand setCommand, String mapName, String key, int ttl) {
        try {
            this.textCommandService.lock(mapName, key);
        }
        catch (Exception e) {
            setCommand.setResponse(TextCommandConstants.NOT_STORED);
            if (setCommand.shouldReply()) {
                this.textCommandService.sendResponse(setCommand);
            }
            return;
        }
        Object oldValue = this.textCommandService.get(mapName, key);
        MemcacheEntry entry = null;
        if (oldValue != null) {
            if (oldValue instanceof MemcacheEntry) {
                MemcacheEntry oldEntry = (MemcacheEntry)oldValue;
                entry = new MemcacheEntry(setCommand.getKey(), SetCommandProcessor.concatenate(setCommand.getValue(), oldEntry.getValue()), oldEntry.getFlag());
            } else if (oldValue instanceof byte[]) {
                entry = new MemcacheEntry(setCommand.getKey(), SetCommandProcessor.concatenate(setCommand.getValue(), (byte[])oldValue), 0);
            } else if (oldValue instanceof String) {
                entry = new MemcacheEntry(setCommand.getKey(), SetCommandProcessor.concatenate(setCommand.getValue(), StringUtil.stringToBytes((String)oldValue)), 0);
            } else {
                try {
                    entry = new MemcacheEntry(setCommand.getKey(), SetCommandProcessor.concatenate(setCommand.getValue(), this.textCommandService.toByteArray(oldValue)), 0);
                }
                catch (Exception e) {
                    this.logger.warning(e);
                }
            }
            this.textCommandService.put(mapName, key, entry, ttl);
            setCommand.setResponse(TextCommandConstants.STORED);
        } else {
            setCommand.setResponse(TextCommandConstants.NOT_STORED);
        }
        this.textCommandService.unlock(mapName, key);
    }

    private void appendCommandType(SetCommand setCommand, String mapName, String key, int ttl) {
        try {
            this.textCommandService.lock(mapName, key);
        }
        catch (Exception e) {
            setCommand.setResponse(TextCommandConstants.NOT_STORED);
            if (setCommand.shouldReply()) {
                this.textCommandService.sendResponse(setCommand);
            }
            return;
        }
        Object oldValue = this.textCommandService.get(mapName, key);
        MemcacheEntry entry = null;
        if (oldValue != null) {
            if (oldValue instanceof MemcacheEntry) {
                MemcacheEntry oldEntry = (MemcacheEntry)oldValue;
                entry = new MemcacheEntry(setCommand.getKey(), SetCommandProcessor.concatenate(oldEntry.getValue(), setCommand.getValue()), 0);
            } else if (oldValue instanceof byte[]) {
                entry = new MemcacheEntry(setCommand.getKey(), SetCommandProcessor.concatenate((byte[])oldValue, setCommand.getValue()), 0);
            } else if (oldValue instanceof String) {
                entry = new MemcacheEntry(setCommand.getKey(), SetCommandProcessor.concatenate(StringUtil.stringToBytes((String)oldValue), setCommand.getValue()), 0);
            } else {
                try {
                    entry = new MemcacheEntry(setCommand.getKey(), SetCommandProcessor.concatenate(this.textCommandService.toByteArray(oldValue), setCommand.getValue()), 0);
                }
                catch (Exception e) {
                    this.logger.warning(e);
                }
            }
            this.textCommandService.put(mapName, key, entry, ttl);
            setCommand.setResponse(TextCommandConstants.STORED);
        } else {
            setCommand.setResponse(TextCommandConstants.NOT_STORED);
        }
        this.textCommandService.unlock(mapName, key);
    }

    @Override
    public void handleRejection(SetCommand request) {
        request.setResponse(TextCommandConstants.NOT_STORED);
        if (request.shouldReply()) {
            this.textCommandService.sendResponse(request);
        }
    }
}

