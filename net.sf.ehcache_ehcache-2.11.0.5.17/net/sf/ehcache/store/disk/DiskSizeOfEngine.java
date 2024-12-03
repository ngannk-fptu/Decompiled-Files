/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store.disk;

import net.sf.ehcache.pool.Size;
import net.sf.ehcache.pool.SizeOfEngine;
import net.sf.ehcache.store.disk.DiskStorageFactory;

public class DiskSizeOfEngine
implements SizeOfEngine {
    @Override
    public Size sizeOf(Object key, Object value, Object container) {
        if (container != null && !(container instanceof DiskStorageFactory.DiskMarker)) {
            throw new IllegalArgumentException("can only size DiskStorageFactory.DiskMarker");
        }
        if (container == null) {
            return new Size(0L, true);
        }
        DiskStorageFactory.DiskMarker marker = (DiskStorageFactory.DiskMarker)container;
        return new Size(marker.getSize(), true);
    }

    @Override
    public SizeOfEngine copyWith(int maxDepth, boolean abortWhenMaxDepthExceeded) {
        return new DiskSizeOfEngine();
    }
}

