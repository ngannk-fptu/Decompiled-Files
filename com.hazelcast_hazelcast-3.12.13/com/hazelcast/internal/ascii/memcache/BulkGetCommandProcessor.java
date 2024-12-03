/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.TextCommandService;
import com.hazelcast.internal.ascii.memcache.BulkGetCommand;
import com.hazelcast.internal.ascii.memcache.EntryConverter;
import com.hazelcast.internal.ascii.memcache.MapNameAndKeyPair;
import com.hazelcast.internal.ascii.memcache.MemcacheCommandProcessor;
import com.hazelcast.internal.ascii.memcache.MemcacheEntry;
import com.hazelcast.internal.ascii.memcache.MemcacheUtils;
import com.hazelcast.util.collection.ComposedKeyMap;
import com.hazelcast.util.collection.InternalSetMultimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BulkGetCommandProcessor
extends MemcacheCommandProcessor<BulkGetCommand> {
    private final EntryConverter entryConverter;

    public BulkGetCommandProcessor(TextCommandService textCommandService, EntryConverter entryConverter) {
        super(textCommandService);
        this.entryConverter = entryConverter;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void handle(BulkGetCommand request) {
        void var7_13;
        String mapName;
        List<String> memcacheKeys = request.getKeys();
        InternalSetMultimap<String, String> keysPerMap = new InternalSetMultimap<String, String>();
        ComposedKeyMap<String, String, String> mapNameAndKey2memcacheKey = new ComposedKeyMap<String, String, String>();
        for (String string : memcacheKeys) {
            MapNameAndKeyPair mapNameAndKeyPair = MemcacheUtils.parseMemcacheKey(string);
            mapName = mapNameAndKeyPair.getMapName();
            String hzKey = mapNameAndKeyPair.getKey();
            keysPerMap.put(mapName, hzKey);
            mapNameAndKey2memcacheKey.put(mapName, hzKey, string);
        }
        ArrayList<MemcacheEntry> allResults = new ArrayList<MemcacheEntry>();
        for (Map.Entry entry : keysPerMap.entrySet()) {
            mapName = (String)entry.getKey();
            Set<String> keys = entry.getValue();
            Collection<MemcacheEntry> mapResult = this.getAll(mapName, keys, mapNameAndKey2memcacheKey);
            allResults.addAll(mapResult);
        }
        int n = memcacheKeys.size() - allResults.size();
        boolean bl = false;
        while (var7_13 < n) {
            this.textCommandService.incrementGetMissCount();
            ++var7_13;
        }
        request.setResult(allResults);
        this.textCommandService.sendResponse(request);
    }

    private Collection<MemcacheEntry> getAll(String mapName, Set<String> keys, ComposedKeyMap<String, String, String> mapNameAndKey2memcacheKey) {
        Map<String, Object> entries = this.textCommandService.getAll(mapName, keys);
        ArrayList<MemcacheEntry> result = new ArrayList<MemcacheEntry>(entries.size());
        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String origKey = mapNameAndKey2memcacheKey.get(mapName, key);
            MemcacheEntry memcacheEntry = this.entryConverter.toEntry(origKey, value);
            this.textCommandService.incrementGetHitCount();
            result.add(memcacheEntry);
        }
        return result;
    }

    @Override
    public void handleRejection(BulkGetCommand request) {
        throw new UnsupportedOperationException("not used, this method should be removed from the interface");
    }
}

