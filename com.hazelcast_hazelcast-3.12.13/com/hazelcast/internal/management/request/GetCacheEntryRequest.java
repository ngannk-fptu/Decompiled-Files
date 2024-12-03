/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.expiry.ExpiryPolicy
 *  javax.cache.processor.EntryProcessor
 *  javax.cache.processor.EntryProcessorException
 *  javax.cache.processor.MutableEntry
 */
package com.hazelcast.internal.management.request;

import com.hazelcast.cache.CacheEntryView;
import com.hazelcast.cache.ICache;
import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.CacheEntryProcessorEntry;
import com.hazelcast.cache.impl.record.AbstractCacheRecord;
import com.hazelcast.core.ReadOnly;
import com.hazelcast.instance.HazelcastInstanceCacheManager;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.management.request.ConsoleRequest;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.util.JsonUtil;
import java.io.IOException;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;

public class GetCacheEntryRequest
implements ConsoleRequest {
    private static final GetCacheEntryViewEntryProcessor ENTRY_PROCESSOR = new GetCacheEntryViewEntryProcessor();
    private String cacheName;
    private String type;
    private String key;

    public GetCacheEntryRequest() {
    }

    public GetCacheEntryRequest(String type, String cacheName, String key) {
        this.type = type;
        this.cacheName = cacheName;
        this.key = key;
    }

    @Override
    public int getType() {
        return 41;
    }

    @Override
    public void writeResponse(ManagementCenterService mcs, JsonObject root) {
        InternalSerializationService serializationService = mcs.getHazelcastInstance().getSerializationService();
        HazelcastInstanceCacheManager cacheManager = mcs.getHazelcastInstance().getCacheManager();
        ICache cache = cacheManager.getCache(this.cacheName);
        CacheEntryView cacheEntry = null;
        if ("string".equals(this.type)) {
            cacheEntry = (CacheEntryView)cache.invoke(this.key, ENTRY_PROCESSOR, new Object[0]);
        } else if ("long".equals(this.type)) {
            cacheEntry = (CacheEntryView)cache.invoke(Long.valueOf(this.key), ENTRY_PROCESSOR, new Object[0]);
        } else if ("integer".equals(this.type)) {
            cacheEntry = (CacheEntryView)cache.invoke(Integer.valueOf(this.key), ENTRY_PROCESSOR, new Object[0]);
        }
        JsonObject result = new JsonObject();
        if (cacheEntry != null) {
            Object value = serializationService.toObject(cacheEntry.getValue());
            result.add("cacheBrowse_value", value != null ? value.toString() : "null");
            result.add("cacheBrowse_class", value != null ? value.getClass().getName() : "null");
            result.add("date_cache_creation_time", Long.toString(cacheEntry.getCreationTime()));
            result.add("date_cache_expiration_time", Long.toString(cacheEntry.getExpirationTime()));
            result.add("cacheBrowse_hits", Long.toString(cacheEntry.getAccessHit()));
            result.add("date_cache_access_time", Long.toString(cacheEntry.getLastAccessTime()));
        }
        root.add("result", result);
    }

    @Override
    public void fromJson(JsonObject json) {
        this.cacheName = JsonUtil.getString(json, "cacheName");
        this.type = JsonUtil.getString(json, "type");
        this.key = JsonUtil.getString(json, "key");
    }

    public static class CacheBrowserEntryView
    implements CacheEntryView<Object, Object>,
    IdentifiedDataSerializable,
    Versioned {
        private Object value;
        private long expirationTime;
        private long creationTime;
        private long lastAccessTime;
        private long accessHit;
        private ExpiryPolicy expiryPolicy;

        public CacheBrowserEntryView() {
        }

        CacheBrowserEntryView(CacheEntryProcessorEntry entry) {
            this.value = entry.getValue();
            Object record = entry.getRecord();
            this.expirationTime = record.getExpirationTime();
            this.creationTime = record.getCreationTime();
            this.lastAccessTime = record.getLastAccessTime();
            this.accessHit = record.getAccessHit();
            this.expiryPolicy = (ExpiryPolicy)record.getExpiryPolicy();
        }

        @Override
        public Object getKey() {
            return null;
        }

        @Override
        public Object getValue() {
            return this.value;
        }

        @Override
        public long getExpirationTime() {
            return this.expirationTime;
        }

        @Override
        public long getCreationTime() {
            return this.creationTime;
        }

        @Override
        public long getLastAccessTime() {
            return this.lastAccessTime;
        }

        @Override
        public long getAccessHit() {
            return this.accessHit;
        }

        public ExpiryPolicy getExpiryPolicy() {
            return this.expiryPolicy;
        }

        @Override
        public int getFactoryId() {
            return CacheDataSerializerHook.F_ID;
        }

        @Override
        public int getId() {
            return 62;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeObject(this.value);
            out.writeLong(this.expirationTime);
            out.writeLong(this.creationTime);
            out.writeLong(this.lastAccessTime);
            out.writeLong(this.accessHit);
            if (out.getVersion().isGreaterOrEqual(AbstractCacheRecord.EXPIRY_POLICY_VERSION)) {
                out.writeObject(this.expiryPolicy);
            }
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            this.value = in.readObject();
            this.expirationTime = in.readLong();
            this.creationTime = in.readLong();
            this.lastAccessTime = in.readLong();
            this.accessHit = in.readLong();
            if (in.getVersion().isGreaterOrEqual(AbstractCacheRecord.EXPIRY_POLICY_VERSION)) {
                this.expiryPolicy = (ExpiryPolicy)in.readObject();
            }
        }
    }

    public static class GetCacheEntryViewEntryProcessor
    implements EntryProcessor<Object, Object, CacheEntryView>,
    IdentifiedDataSerializable,
    ReadOnly {
        public CacheEntryView process(MutableEntry mutableEntry, Object ... objects) throws EntryProcessorException {
            CacheEntryProcessorEntry entry = (CacheEntryProcessorEntry)mutableEntry;
            if (entry.getRecord() == null) {
                return null;
            }
            return new CacheBrowserEntryView(entry);
        }

        @Override
        public int getFactoryId() {
            return CacheDataSerializerHook.F_ID;
        }

        @Override
        public int getId() {
            return 63;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
        }
    }
}

