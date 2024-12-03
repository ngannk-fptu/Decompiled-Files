/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import java.util.Set;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.writer.writebehind.WriteBehind;

public interface TerracottaStore
extends Store {
    public Element unsafeGet(Object var1);

    public void quickClear();

    public int quickSize();

    public Set getLocalKeys();

    public CacheConfiguration.TransactionalMode getTransactionalMode();

    public WriteBehind createWriteBehind();

    public void notifyCacheEventListenersChanged();
}

