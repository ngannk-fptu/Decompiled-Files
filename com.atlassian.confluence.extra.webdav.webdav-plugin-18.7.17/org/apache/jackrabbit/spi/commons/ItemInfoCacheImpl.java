/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.jackrabbit.spi.ItemId;
import org.apache.jackrabbit.spi.ItemInfo;
import org.apache.jackrabbit.spi.ItemInfoCache;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.NodeInfo;
import org.apache.jackrabbit.spi.PropertyId;
import org.apache.jackrabbit.spi.PropertyInfo;

public class ItemInfoCacheImpl
implements ItemInfoCache {
    public static final int DEFAULT_CACHE_SIZE = 5000;
    private final int cacheSize;
    private final LinkedMap entries;

    public ItemInfoCacheImpl() {
        this(5000);
    }

    public ItemInfoCacheImpl(int cacheSize) {
        this.cacheSize = cacheSize;
        this.entries = new LinkedMap(cacheSize);
    }

    @Override
    public ItemInfoCache.Entry<NodeInfo> getNodeInfo(NodeId nodeId) {
        Object entry = this.entries.remove(nodeId);
        if (entry == null) {
            entry = this.entries.remove(nodeId.getPath());
        } else {
            this.entries.remove(((NodeInfo)ItemInfoCacheImpl.node(entry).info).getPath());
        }
        return ItemInfoCacheImpl.node(entry);
    }

    @Override
    public ItemInfoCache.Entry<PropertyInfo> getPropertyInfo(PropertyId propertyId) {
        Object entry = this.entries.remove(propertyId);
        if (entry == null) {
            entry = this.entries.remove(propertyId.getPath());
        } else {
            this.entries.remove(((PropertyInfo)ItemInfoCacheImpl.property(entry).info).getPath());
        }
        return ItemInfoCacheImpl.property(entry);
    }

    @Override
    public void put(ItemInfo info, long generation) {
        ItemId id = info.getId();
        ItemInfoCache.Entry<NodeInfo> entry = info.denotesNode() ? new ItemInfoCache.Entry<NodeInfo>((NodeInfo)info, generation) : new ItemInfoCache.Entry<PropertyInfo>((PropertyInfo)info, generation);
        this.put(id, entry);
        if (id.getUniqueID() != null && id.getPath() == null) {
            this.put(info.getPath(), entry);
        }
    }

    @Override
    public void dispose() {
        this.entries.clear();
    }

    private void put(Object key, ItemInfoCache.Entry<? extends ItemInfo> entry) {
        this.entries.remove(key);
        if (this.entries.size() >= this.cacheSize) {
            this.entries.remove(this.entries.firstKey());
        }
        this.entries.put(key, entry);
    }

    private static ItemInfoCache.Entry<NodeInfo> node(Object entry) {
        if (entry != null && ((ItemInfoCache.Entry)entry).info.denotesNode()) {
            return (ItemInfoCache.Entry)entry;
        }
        return null;
    }

    private static ItemInfoCache.Entry<PropertyInfo> property(Object entry) {
        if (entry != null && !((ItemInfoCache.Entry)entry).info.denotesNode()) {
            return (ItemInfoCache.Entry)entry;
        }
        return null;
    }
}

