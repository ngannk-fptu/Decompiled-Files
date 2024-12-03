/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.writebehind;

import net.sf.ehcache.writer.writebehind.operations.KeyBasedOperation;

public class KeyBasedOperationWrapper
implements KeyBasedOperation {
    private final Object key;
    private final long creationTime;

    public KeyBasedOperationWrapper(Object key, long creationTime) {
        this.key = key;
        this.creationTime = creationTime;
    }

    @Override
    public Object getKey() {
        return this.key;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }
}

