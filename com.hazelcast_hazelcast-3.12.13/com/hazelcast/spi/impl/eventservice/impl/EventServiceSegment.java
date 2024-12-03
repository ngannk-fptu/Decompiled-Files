/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.eventservice.impl;

import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.EventFilter;
import com.hazelcast.spi.ListenerWrapperEventFilter;
import com.hazelcast.spi.NotifiableEventListener;
import com.hazelcast.spi.impl.eventservice.impl.Registration;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class EventServiceSegment<S> {
    private final String serviceName;
    private final S service;
    private final ConcurrentMap<String, Collection<Registration>> registrations = new ConcurrentHashMap<String, Collection<Registration>>();
    @Probe(name="listenerCount")
    private final ConcurrentMap<String, Registration> registrationIdMap = new ConcurrentHashMap<String, Registration>();
    @Probe(name="publicationCount")
    private final AtomicLong totalPublishes = new AtomicLong();

    public EventServiceSegment(String serviceName, S service) {
        this.serviceName = serviceName;
        this.service = service;
    }

    private void pingNotifiableEventListener(String topic, Registration registration, boolean register) {
        EventFilter filter;
        Object listener = registration.getListener();
        if (!(listener instanceof NotifiableEventListener) && (filter = registration.getFilter()) instanceof ListenerWrapperEventFilter) {
            listener = ((ListenerWrapperEventFilter)filter).getListener();
        }
        this.pingNotifiableEventListenerInternal(listener, topic, registration, register);
        this.pingNotifiableEventListenerInternal(this.service, topic, registration, register);
    }

    private void pingNotifiableEventListenerInternal(Object object, String topic, Registration registration, boolean register) {
        if (!(object instanceof NotifiableEventListener)) {
            return;
        }
        NotifiableEventListener listener = (NotifiableEventListener)object;
        if (register) {
            listener.onRegister(this.service, this.serviceName, topic, registration);
        } else {
            listener.onDeregister(this.service, this.serviceName, topic, registration);
        }
    }

    public Collection<Registration> getRegistrations(String topic, boolean forceCreate) {
        Collection listenerList = (Collection)this.registrations.get(topic);
        if (listenerList == null && forceCreate) {
            ConstructorFunction<String, Collection<Registration>> func = new ConstructorFunction<String, Collection<Registration>>(){

                @Override
                public Collection<Registration> createNew(String key) {
                    return Collections.newSetFromMap(new ConcurrentHashMap());
                }
            };
            return ConcurrencyUtil.getOrPutIfAbsent(this.registrations, topic, func);
        }
        return listenerList;
    }

    public ConcurrentMap<String, Registration> getRegistrationIdMap() {
        return this.registrationIdMap;
    }

    public ConcurrentMap<String, Collection<Registration>> getRegistrations() {
        return this.registrations;
    }

    public boolean addRegistration(String topic, Registration registration) {
        Collection<Registration> registrations = this.getRegistrations(topic, true);
        if (registrations.add(registration)) {
            this.registrationIdMap.put(registration.getId(), registration);
            this.pingNotifiableEventListener(topic, registration, true);
            return true;
        }
        return false;
    }

    public Registration removeRegistration(String topic, String id) {
        Registration registration = (Registration)this.registrationIdMap.remove(id);
        if (registration != null) {
            Collection all = (Collection)this.registrations.get(topic);
            if (all != null) {
                all.remove(registration);
            }
            this.pingNotifiableEventListener(topic, registration, false);
        }
        return registration;
    }

    void removeRegistrations(String topic) {
        Collection all = (Collection)this.registrations.remove(topic);
        if (all == null) {
            return;
        }
        for (Registration reg : all) {
            this.registrationIdMap.remove(reg.getId());
            this.pingNotifiableEventListener(topic, reg, false);
        }
    }

    void clear() {
        for (Collection all : this.registrations.values()) {
            Iterator iter = all.iterator();
            while (iter.hasNext()) {
                Registration reg = (Registration)iter.next();
                iter.remove();
                this.registrationIdMap.remove(reg.getId());
                this.pingNotifiableEventListener(reg.getTopic(), reg, false);
            }
        }
    }

    void onMemberLeft(Address address) {
        for (Collection all : this.registrations.values()) {
            Iterator iter = all.iterator();
            while (iter.hasNext()) {
                Registration reg = (Registration)iter.next();
                if (!address.equals(reg.getSubscriber())) continue;
                iter.remove();
                this.registrationIdMap.remove(reg.getId());
                this.pingNotifiableEventListener(reg.getTopic(), reg, false);
            }
        }
    }

    long incrementPublish() {
        return this.totalPublishes.incrementAndGet();
    }

    boolean hasRegistration(String topic) {
        Collection topicRegistrations = (Collection)this.registrations.get(topic);
        return topicRegistrations != null && !topicRegistrations.isEmpty();
    }

    void collectRemoteRegistrations(Collection<Registration> result) {
        for (Registration registration : this.registrationIdMap.values()) {
            if (registration.isLocalOnly()) continue;
            result.add(registration);
        }
    }
}

