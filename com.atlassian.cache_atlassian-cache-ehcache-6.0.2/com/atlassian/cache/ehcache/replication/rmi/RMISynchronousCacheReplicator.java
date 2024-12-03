/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.ehcache.CacheException
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.Element
 *  net.sf.ehcache.distribution.RMISynchronousCacheReplicator
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.cache.ehcache.replication.rmi;

import java.io.Serializable;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RMISynchronousCacheReplicator
extends net.sf.ehcache.distribution.RMISynchronousCacheReplicator {
    private static final Logger LOG = LoggerFactory.getLogger(net.sf.ehcache.distribution.RMISynchronousCacheReplicator.class);

    public RMISynchronousCacheReplicator(boolean replicatePuts, boolean replicatePutsViaCopy, boolean replicateUpdates, boolean replicateUpdatesViaCopy, boolean replicateRemovals) {
        super(replicatePuts, replicatePutsViaCopy, replicateUpdates, replicateUpdatesViaCopy, replicateRemovals);
    }

    public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
        if (this.notAlive() || !this.replicatePuts) {
            return;
        }
        if (this.replicatePutsViaCopy) {
            this.replicateViaCopy(cache, element);
        } else {
            this.replicateViaKeyInvalidation(cache, element);
        }
    }

    private void replicateViaCopy(Ehcache cache, Element element) {
        if (element.isSerializable()) {
            RMISynchronousCacheReplicator.replicatePutNotification((Ehcache)cache, (Element)element);
            return;
        }
        if (!element.isKeySerializable()) {
            this.logUnserializableKey(element);
        }
        if (LOG.isWarnEnabled() && !(element.getObjectValue() instanceof Serializable)) {
            LOG.error("Value class {} is not Serializable => cannot be replicated", (Object)element.getObjectValue().getClass().getName());
        }
    }

    private void replicateViaKeyInvalidation(Ehcache cache, Element element) {
        if (element.isKeySerializable()) {
            RMISynchronousCacheReplicator.replicateRemovalNotification((Ehcache)cache, (Serializable)((Serializable)element.getObjectKey()));
            return;
        }
        this.logUnserializableKey(element);
    }

    private void logUnserializableKey(Element element) {
        if (LOG.isWarnEnabled()) {
            LOG.error("Key class {} is not Serializable => cannot be replicated", (Object)element.getObjectKey().getClass().getName());
        }
    }
}

