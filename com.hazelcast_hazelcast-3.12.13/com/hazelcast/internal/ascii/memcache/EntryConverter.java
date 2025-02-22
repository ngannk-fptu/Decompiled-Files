/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.TextCommandService;
import com.hazelcast.internal.ascii.memcache.MemcacheEntry;
import com.hazelcast.logging.ILogger;
import com.hazelcast.util.StringUtil;

public final class EntryConverter {
    private final TextCommandService textCommandService;
    private final ILogger logger;

    public EntryConverter(TextCommandService textCommandService, ILogger logger) {
        this.textCommandService = textCommandService;
        this.logger = logger;
    }

    public MemcacheEntry toEntry(String key, Object value) {
        if (value == null) {
            return null;
        }
        MemcacheEntry entry = null;
        if (value instanceof MemcacheEntry) {
            entry = (MemcacheEntry)value;
        } else if (value instanceof byte[]) {
            entry = new MemcacheEntry(key, (byte[])value, 0);
        } else if (value instanceof String) {
            entry = new MemcacheEntry(key, StringUtil.stringToBytes((String)value), 0);
        } else {
            try {
                entry = new MemcacheEntry(key, this.textCommandService.toByteArray(value), 0);
            }
            catch (Exception e) {
                this.logger.warning(e);
            }
        }
        return entry;
    }
}

