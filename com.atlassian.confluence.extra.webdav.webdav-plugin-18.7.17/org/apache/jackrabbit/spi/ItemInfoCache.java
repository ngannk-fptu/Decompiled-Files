/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import org.apache.jackrabbit.spi.ItemInfo;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.NodeInfo;
import org.apache.jackrabbit.spi.PropertyId;
import org.apache.jackrabbit.spi.PropertyInfo;

public interface ItemInfoCache {
    public Entry<NodeInfo> getNodeInfo(NodeId var1);

    public Entry<PropertyInfo> getPropertyInfo(PropertyId var1);

    public void put(ItemInfo var1, long var2);

    public void dispose();

    public static class Entry<T extends ItemInfo> {
        public final T info;
        public final long generation;

        public Entry(T info, long generation) {
            this.info = info;
            this.generation = generation;
        }

        public int hashCode() {
            return this.info.hashCode() + (int)this.generation;
        }

        public boolean equals(Object that) {
            if (that == null) {
                return false;
            }
            if (that == this) {
                return true;
            }
            if (that instanceof Entry) {
                Entry other = (Entry)that;
                return this.generation == other.generation && this.info.equals(other.info);
            }
            return false;
        }
    }
}

