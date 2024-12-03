/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.terracotta.toolkit.ToolkitFeatureType
 *  org.terracotta.toolkit.feature.NonStopFeature
 *  org.terracotta.toolkit.nonstop.NonStopConfiguration
 *  org.terracotta.toolkit.nonstop.NonStopException
 */
package org.terracotta.modules.ehcache.event;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.NonstopConfiguration;
import net.sf.ehcache.constructs.nonstop.NonStopCacheException;
import net.sf.ehcache.event.CacheEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.modules.ehcache.ToolkitInstanceFactory;
import org.terracotta.modules.ehcache.store.ToolkitNonStopExceptionOnTimeoutConfiguration;
import org.terracotta.toolkit.ToolkitFeatureType;
import org.terracotta.toolkit.feature.NonStopFeature;
import org.terracotta.toolkit.nonstop.NonStopConfiguration;
import org.terracotta.toolkit.nonstop.NonStopException;

public class NonStopEventReplicator
implements CacheEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(NonStopEventReplicator.class);
    private final CacheEventListener delegate;
    private final NonStopFeature nonStop;
    private final ToolkitNonStopExceptionOnTimeoutConfiguration toolkitNonStopConfiguration;
    private final NonstopConfiguration ehcacheNonStopConfiguration;

    public NonStopEventReplicator(CacheEventListener delegate, ToolkitInstanceFactory toolkitInstanceFactory, NonstopConfiguration nonStopConfiguration) {
        this.delegate = delegate;
        this.ehcacheNonStopConfiguration = nonStopConfiguration;
        this.toolkitNonStopConfiguration = new ToolkitNonStopExceptionOnTimeoutConfiguration(this.ehcacheNonStopConfiguration);
        this.nonStop = (NonStopFeature)toolkitInstanceFactory.getToolkit().getFeature(ToolkitFeatureType.NONSTOP);
    }

    private void handleNonStopException(NonStopException e, String eventType, Ehcache cache, Element element) {
        String cacheName = cache == null ? null : cache.getName();
        String msg = "Terracotta clustered event notification timed out: operation: " + eventType + ", cache: " + cacheName + ", element: " + element;
        switch (this.ehcacheNonStopConfiguration.getTimeoutBehavior().getTimeoutBehaviorType()) {
            case EXCEPTION: {
                throw new NonStopCacheException(msg);
            }
        }
        LOGGER.info(msg);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.delegate.notifyElementRemoved(cache, element);
        }
        catch (NonStopException e) {
            this.handleNonStopException(e, "REMOVED", cache, element);
        }
        finally {
            this.nonStop.finish();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.delegate.notifyElementPut(cache, element);
        }
        catch (NonStopException e) {
            this.handleNonStopException(e, "PUT", cache, element);
        }
        finally {
            this.nonStop.finish();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.delegate.notifyElementUpdated(cache, element);
        }
        catch (NonStopException e) {
            this.handleNonStopException(e, "UPDATED", cache, element);
        }
        finally {
            this.nonStop.finish();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void notifyElementExpired(Ehcache cache, Element element) {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.delegate.notifyElementExpired(cache, element);
        }
        catch (NonStopException e) {
            this.handleNonStopException(e, "EXPIRED", cache, element);
        }
        finally {
            this.nonStop.finish();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void notifyElementEvicted(Ehcache cache, Element element) {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.delegate.notifyElementEvicted(cache, element);
        }
        catch (NonStopException e) {
            this.handleNonStopException(e, "EVICTED", cache, element);
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public void notifyRemoveAll(Ehcache cache) {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.delegate.notifyRemoveAll(cache);
        }
        catch (NonStopException e) {
            this.handleNonStopException(e, "REMOVEALL", cache, null);
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public void dispose() {
        this.nonStop.start((NonStopConfiguration)this.toolkitNonStopConfiguration);
        try {
            this.delegate.dispose();
        }
        catch (NonStopException e) {
            this.handleNonStopException(e, "DISPOSE", null, null);
        }
        finally {
            this.nonStop.finish();
        }
    }

    @Override
    public NonStopEventReplicator clone() throws CloneNotSupportedException {
        return (NonStopEventReplicator)super.clone();
    }
}

