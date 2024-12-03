/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.protocol.util;

import org.apache.abdera.protocol.ItemManager;
import org.apache.abdera.protocol.Request;
import org.apache.abdera.protocol.util.PoolManager;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractItemManager<T>
extends PoolManager<T>
implements ItemManager<T> {
    public AbstractItemManager() {
    }

    public AbstractItemManager(int max) {
        super(max);
    }

    @Override
    public T get(Request request) {
        return this.getInstance();
    }
}

