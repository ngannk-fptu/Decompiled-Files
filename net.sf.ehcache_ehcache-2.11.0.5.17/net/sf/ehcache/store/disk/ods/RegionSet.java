/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store.disk.ods;

import net.sf.ehcache.store.disk.ods.AATreeSet;
import net.sf.ehcache.store.disk.ods.Region;

class RegionSet
extends AATreeSet<Region> {
    private final long size;

    protected RegionSet(long size) {
        this.add(new Region(0L, size - 1L));
        this.size = size;
    }

    @Override
    public Region removeAndReturn(Object o) {
        Region r = (Region)super.removeAndReturn(o);
        if (r != null) {
            return new Region(r);
        }
        return null;
    }

    @Override
    public Region find(Object o) {
        Region r = (Region)super.find(o);
        if (r != null) {
            return new Region(r);
        }
        return null;
    }

    public Region find(long size) {
        AATreeSet.Node currentNode = this.getRoot();
        Region currentRegion = (Region)currentNode.getPayload();
        if (currentRegion == null || size > currentRegion.contiguous()) {
            throw new IllegalArgumentException("Need to grow the region set");
        }
        while (true) {
            if (currentRegion.size() >= size) {
                return new Region(currentRegion.start(), currentRegion.start() + size - 1L);
            }
            Region left = (Region)currentNode.getLeft().getPayload();
            Region right = (Region)currentNode.getRight().getPayload();
            if (left != null && left.contiguous() >= size) {
                currentNode = currentNode.getLeft();
                currentRegion = (Region)currentNode.getPayload();
                continue;
            }
            if (right == null || right.contiguous() < size) break;
            currentNode = currentNode.getRight();
            currentRegion = (Region)currentNode.getPayload();
        }
        throw new IllegalArgumentException("Couldn't find a " + size + " sized free area in " + currentRegion);
    }
}

