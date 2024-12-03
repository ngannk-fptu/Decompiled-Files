/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr.observation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.ObservationManager;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.jcr.observation.SubscriptionImpl;
import org.apache.jackrabbit.webdav.jcr.transaction.TransactionListener;
import org.apache.jackrabbit.webdav.observation.EventDiscovery;
import org.apache.jackrabbit.webdav.observation.ObservationResource;
import org.apache.jackrabbit.webdav.observation.Subscription;
import org.apache.jackrabbit.webdav.observation.SubscriptionDiscovery;
import org.apache.jackrabbit.webdav.observation.SubscriptionInfo;
import org.apache.jackrabbit.webdav.observation.SubscriptionManager;
import org.apache.jackrabbit.webdav.transaction.TransactionResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SubscriptionManagerImpl
implements SubscriptionManager,
TransactionListener {
    private static Logger log = LoggerFactory.getLogger(SubscriptionManagerImpl.class);
    private final SubscriptionMap subscriptions = new SubscriptionMap();
    private final Map<String, List<TransactionListener>> transactionListenerById = new HashMap<String, List<TransactionListener>>();

    @Override
    public SubscriptionDiscovery getSubscriptionDiscovery(ObservationResource resource) {
        Subscription[] subsForResource = this.subscriptions.getByPath(resource.getLocator());
        return new SubscriptionDiscovery(subsForResource);
    }

    @Override
    public Subscription subscribe(SubscriptionInfo info, String subscriptionId, ObservationResource resource) throws DavException {
        Subscription subscription;
        if (subscriptionId == null) {
            SubscriptionImpl newSubs = new SubscriptionImpl(info, resource);
            this.registerSubscription(newSubs, resource);
            this.subscriptions.put(newSubs.getSubscriptionId(), newSubs);
            resource.getSession().addReference(newSubs.getSubscriptionId());
            subscription = newSubs;
        } else {
            SubscriptionImpl existing = this.validate(subscriptionId, resource);
            existing.setInfo(info);
            this.registerSubscription(existing, resource);
            subscription = new WrappedSubscription(existing);
        }
        return subscription;
    }

    private void registerSubscription(SubscriptionImpl subscription, ObservationResource resource) throws DavException {
        try {
            Session session = SubscriptionManagerImpl.getRepositorySession(resource);
            ObservationManager oMgr = session.getWorkspace().getObservationManager();
            String itemPath = subscription.getLocator().getRepositoryPath();
            oMgr.addEventListener(subscription, subscription.getJcrEventTypes(), itemPath, subscription.isDeep(), subscription.getUuidFilters(), subscription.getNodetypeNameFilters(), subscription.isNoLocal());
        }
        catch (RepositoryException e) {
            log.error("Unable to register eventlistener: " + e.getMessage());
            throw new JcrDavException(e);
        }
    }

    @Override
    public void unsubscribe(String subscriptionId, ObservationResource resource) throws DavException {
        SubscriptionImpl subs = this.validate(subscriptionId, resource);
        this.unregisterSubscription(subs, resource);
    }

    private void unregisterSubscription(SubscriptionImpl subscription, ObservationResource resource) throws DavException {
        try {
            Session session = SubscriptionManagerImpl.getRepositorySession(resource);
            session.getWorkspace().getObservationManager().removeEventListener(subscription);
            String sId = subscription.getSubscriptionId();
            this.subscriptions.remove(sId);
            resource.getSession().removeReference(sId);
        }
        catch (RepositoryException e) {
            log.error("Unable to remove eventlistener: " + e.getMessage());
            throw new JcrDavException(e);
        }
    }

    @Override
    public EventDiscovery poll(String subscriptionId, long timeout, ObservationResource resource) throws DavException {
        SubscriptionImpl subs = this.validate(subscriptionId, resource);
        return subs.discoverEvents(timeout);
    }

    private SubscriptionImpl validate(String subscriptionId, ObservationResource resource) throws DavException {
        if (this.subscriptions.contains(subscriptionId)) {
            SubscriptionImpl subs = this.subscriptions.get(subscriptionId);
            if (!subs.isSubscribedToResource(resource)) {
                throw new DavException(412, "Attempt to operate on subscription with invalid resource path.");
            }
            if (subs.isExpired()) {
                this.unregisterSubscription(subs, resource);
                throw new DavException(412, "Attempt to  operate on expired subscription.");
            }
            return subs;
        }
        throw new DavException(412, "Attempt to modify or to poll for non-existing subscription.");
    }

    private static Session getRepositorySession(ObservationResource resource) throws DavException {
        return JcrDavSession.getRepositorySession(resource.getSession());
    }

    @Override
    public synchronized void beforeCommit(TransactionResource resource, String lockToken) {
        ArrayList<TransactionListener> transactionListeners = new ArrayList<TransactionListener>();
        Iterator it = this.subscriptions.iterator();
        while (it.hasNext()) {
            SubscriptionImpl sub = (SubscriptionImpl)it.next();
            TransactionListener tl = sub.createTransactionListener();
            tl.beforeCommit(resource, lockToken);
            transactionListeners.add(tl);
        }
        this.transactionListenerById.put(lockToken, transactionListeners);
    }

    @Override
    public void afterCommit(TransactionResource resource, String lockToken, boolean success) {
        List<TransactionListener> transactionListeners = this.transactionListenerById.remove(lockToken);
        if (transactionListeners != null) {
            for (TransactionListener txListener : transactionListeners) {
                txListener.afterCommit(resource, lockToken, success);
            }
        }
    }

    private class SubscriptionMap {
        private HashMap<String, SubscriptionImpl> subscriptions = new HashMap();
        private HashMap<DavResourceLocator, Set<String>> ids = new HashMap();

        private SubscriptionMap() {
        }

        private boolean contains(String subscriptionId) {
            return this.subscriptions.containsKey(subscriptionId);
        }

        private SubscriptionImpl get(String subscriptionId) {
            return this.subscriptions.get(subscriptionId);
        }

        private Iterator<SubscriptionImpl> iterator() {
            return this.subscriptions.values().iterator();
        }

        private void put(String subscriptionId, SubscriptionImpl subscription) {
            Set<Object> idSet;
            this.subscriptions.put(subscriptionId, subscription);
            DavResourceLocator key = subscription.getLocator();
            if (this.ids.containsKey(key)) {
                idSet = this.ids.get(key);
            } else {
                idSet = new HashSet();
                this.ids.put(key, idSet);
            }
            if (!idSet.contains(subscriptionId)) {
                idSet.add(subscriptionId);
            }
        }

        private void remove(String subscriptionId) {
            SubscriptionImpl sub = this.subscriptions.remove(subscriptionId);
            this.ids.get(sub.getLocator()).remove(subscriptionId);
        }

        private Subscription[] getByPath(DavResourceLocator locator) {
            Set<String> idSet = this.ids.get(locator);
            if (idSet != null && !idSet.isEmpty()) {
                Subscription[] subsForResource = new Subscription[idSet.size()];
                int i = 0;
                for (String id : idSet) {
                    SubscriptionImpl s = this.subscriptions.get(id);
                    subsForResource[i] = new WrappedSubscription(s);
                    ++i;
                }
                return subsForResource;
            }
            return new Subscription[0];
        }
    }

    private static class WrappedSubscription
    implements Subscription {
        private final Subscription delegatee;

        private WrappedSubscription(Subscription subsc) {
            this.delegatee = subsc;
        }

        @Override
        public String getSubscriptionId() {
            return null;
        }

        @Override
        public Element toXml(Document document) {
            return this.delegatee.toXml(document);
        }

        @Override
        public boolean eventsProvideNodeTypeInformation() {
            return this.delegatee.eventsProvideNodeTypeInformation();
        }

        @Override
        public boolean eventsProvideNoLocalFlag() {
            return this.delegatee.eventsProvideNoLocalFlag();
        }
    }
}

