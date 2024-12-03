/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.eventservice.impl;

import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.metrics.MetricsProvider;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.util.InvocationUtil;
import com.hazelcast.internal.util.counters.MwCounter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.EndpointManager;
import com.hazelcast.nio.Packet;
import com.hazelcast.spi.EventFilter;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.eventservice.InternalEventService;
import com.hazelcast.spi.impl.eventservice.impl.EventEnvelope;
import com.hazelcast.spi.impl.eventservice.impl.EventServiceSegment;
import com.hazelcast.spi.impl.eventservice.impl.LocalEventDispatcher;
import com.hazelcast.spi.impl.eventservice.impl.Registration;
import com.hazelcast.spi.impl.eventservice.impl.RemoteEventProcessor;
import com.hazelcast.spi.impl.eventservice.impl.TrueEventFilter;
import com.hazelcast.spi.impl.eventservice.impl.operations.DeregistrationOperationSupplier;
import com.hazelcast.spi.impl.eventservice.impl.operations.OnJoinRegistrationOperation;
import com.hazelcast.spi.impl.eventservice.impl.operations.RegistrationOperationSupplier;
import com.hazelcast.spi.impl.eventservice.impl.operations.SendEventOperation;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.ThreadUtil;
import com.hazelcast.util.UuidUtil;
import com.hazelcast.util.executor.StripedExecutor;
import com.hazelcast.util.function.Supplier;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class EventServiceImpl
implements InternalEventService,
MetricsProvider {
    public static final String SERVICE_NAME = "hz:core:eventService";
    public static final String EVENT_SYNC_FREQUENCY_PROP = "hazelcast.event.sync.frequency";
    private static final EventRegistration[] EMPTY_REGISTRATIONS = new EventRegistration[0];
    private static final int EVENT_SYNC_FREQUENCY = 100000;
    private static final int SEND_RETRY_COUNT = 50;
    private static final int WARNING_LOG_FREQUENCY = 1000;
    private static final int MAX_RETRIES = 100;
    final ILogger logger;
    final NodeEngineImpl nodeEngine;
    private final ConcurrentMap<String, EventServiceSegment> segments;
    private final StripedExecutor eventExecutor;
    private final long eventQueueTimeoutMs;
    @Probe(name="threadCount")
    private final int eventThreadCount;
    @Probe(name="queueCapacity")
    private final int eventQueueCapacity;
    @Probe(name="totalFailureCount")
    private final MwCounter totalFailures = MwCounter.newMwCounter();
    @Probe(name="rejectedCount")
    private final MwCounter rejectedCount = MwCounter.newMwCounter();
    @Probe(name="syncDeliveryFailureCount")
    private final MwCounter syncDeliveryFailureCount = MwCounter.newMwCounter();
    private final int sendEventSyncTimeoutMillis;
    private final InternalSerializationService serializationService;
    private final int eventSyncFrequency;

    public EventServiceImpl(NodeEngineImpl nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.serializationService = (InternalSerializationService)nodeEngine.getSerializationService();
        this.logger = nodeEngine.getLogger(EventService.class.getName());
        HazelcastProperties hazelcastProperties = nodeEngine.getProperties();
        this.eventThreadCount = hazelcastProperties.getInteger(GroupProperty.EVENT_THREAD_COUNT);
        this.eventQueueCapacity = hazelcastProperties.getInteger(GroupProperty.EVENT_QUEUE_CAPACITY);
        this.eventQueueTimeoutMs = hazelcastProperties.getMillis(GroupProperty.EVENT_QUEUE_TIMEOUT_MILLIS);
        this.sendEventSyncTimeoutMillis = hazelcastProperties.getInteger(GroupProperty.EVENT_SYNC_TIMEOUT_MILLIS);
        this.eventSyncFrequency = EventServiceImpl.loadEventSyncFrequency();
        this.eventExecutor = new StripedExecutor(nodeEngine.getNode().getLogger(EventServiceImpl.class), ThreadUtil.createThreadName(nodeEngine.getHazelcastInstance().getName(), "event"), this.eventThreadCount, this.eventQueueCapacity);
        this.segments = new ConcurrentHashMap<String, EventServiceSegment>();
    }

    private static int loadEventSyncFrequency() {
        try {
            int eventSyncFrequency = Integer.parseInt(System.getProperty(EVENT_SYNC_FREQUENCY_PROP));
            if (eventSyncFrequency <= 0) {
                eventSyncFrequency = 100000;
            }
            return eventSyncFrequency;
        }
        catch (Exception e) {
            return 100000;
        }
    }

    @Override
    public void provideMetrics(MetricsRegistry registry) {
        registry.scanAndRegister(this, "event");
    }

    @Override
    public void close(EventRegistration eventRegistration) {
        Registration registration = (Registration)eventRegistration;
        Object listener = registration.getListener();
        if (!(listener instanceof Closeable)) {
            return;
        }
        try {
            ((Closeable)listener).close();
        }
        catch (IOException e) {
            EmptyStatement.ignore(e);
        }
    }

    @Override
    public int getEventThreadCount() {
        return this.eventThreadCount;
    }

    @Override
    public int getEventQueueCapacity() {
        return this.eventQueueCapacity;
    }

    @Override
    @Probe(name="eventQueueSize", level=ProbeLevel.MANDATORY)
    public int getEventQueueSize() {
        return this.eventExecutor.getWorkQueueSize();
    }

    @Probe(level=ProbeLevel.MANDATORY)
    private long eventsProcessed() {
        return this.eventExecutor.processedCount();
    }

    @Override
    public EventRegistration registerLocalListener(String serviceName, String topic, Object listener) {
        return this.registerListenerInternal(serviceName, topic, TrueEventFilter.INSTANCE, listener, true);
    }

    @Override
    public EventRegistration registerLocalListener(String serviceName, String topic, EventFilter filter, Object listener) {
        return this.registerListenerInternal(serviceName, topic, filter, listener, true);
    }

    @Override
    public EventRegistration registerListener(String serviceName, String topic, Object listener) {
        return this.registerListenerInternal(serviceName, topic, TrueEventFilter.INSTANCE, listener, false);
    }

    @Override
    public EventRegistration registerListener(String serviceName, String topic, EventFilter filter, Object listener) {
        return this.registerListenerInternal(serviceName, topic, filter, listener, false);
    }

    private EventRegistration registerListenerInternal(String serviceName, String topic, EventFilter filter, Object listener, boolean localOnly) {
        String id;
        Registration reg;
        if (listener == null) {
            throw new IllegalArgumentException("Listener required!");
        }
        if (filter == null) {
            throw new IllegalArgumentException("EventFilter required!");
        }
        EventServiceSegment segment = this.getSegment(serviceName, true);
        if (!segment.addRegistration(topic, reg = new Registration(id = UuidUtil.newUnsecureUuidString(), serviceName, topic, filter, this.nodeEngine.getThisAddress(), listener, localOnly))) {
            return null;
        }
        if (!localOnly) {
            RegistrationOperationSupplier supplier = new RegistrationOperationSupplier(reg, this.nodeEngine.getClusterService());
            this.invokeOnAllMembers(supplier);
        }
        return reg;
    }

    public boolean handleRegistration(Registration reg) {
        if (this.nodeEngine.getThisAddress().equals(reg.getSubscriber())) {
            return false;
        }
        EventServiceSegment segment = this.getSegment(reg.getServiceName(), true);
        return segment.addRegistration(reg.getTopic(), reg);
    }

    @Override
    public boolean deregisterListener(String serviceName, String topic, Object id) {
        EventServiceSegment segment = this.getSegment(serviceName, false);
        if (segment == null) {
            return false;
        }
        Registration reg = segment.removeRegistration(topic, String.valueOf(id));
        if (reg != null && !reg.isLocalOnly()) {
            DeregistrationOperationSupplier supplier = new DeregistrationOperationSupplier(reg, this.nodeEngine.getClusterService());
            this.invokeOnAllMembers(supplier);
        }
        return reg != null;
    }

    private void invokeOnAllMembers(Supplier<Operation> operationSupplier) {
        ICompletableFuture<Object> future = InvocationUtil.invokeOnStableClusterSerial(this.nodeEngine, operationSupplier, 100);
        try {
            future.get();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw ExceptionUtil.rethrow(e);
        }
        catch (ExecutionException e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    @Override
    public void deregisterAllListeners(String serviceName, String topic) {
        EventServiceSegment segment = this.getSegment(serviceName, false);
        if (segment != null) {
            segment.removeRegistrations(topic);
        }
    }

    public StripedExecutor getEventExecutor() {
        return this.eventExecutor;
    }

    @Override
    public EventRegistration[] getRegistrationsAsArray(String serviceName, String topic) {
        EventServiceSegment segment = this.getSegment(serviceName, false);
        if (segment == null) {
            return EMPTY_REGISTRATIONS;
        }
        Collection<Registration> registrations = segment.getRegistrations(topic, false);
        if (registrations == null || registrations.isEmpty()) {
            return EMPTY_REGISTRATIONS;
        }
        return registrations.toArray(new Registration[0]);
    }

    @Override
    public Collection<EventRegistration> getRegistrations(String serviceName, String topic) {
        EventServiceSegment segment = this.getSegment(serviceName, false);
        if (segment == null) {
            return Collections.emptySet();
        }
        Collection<Registration> registrations = segment.getRegistrations(topic, false);
        if (registrations == null || registrations.isEmpty()) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(registrations);
    }

    @Override
    public boolean hasEventRegistration(String serviceName, String topic) {
        EventServiceSegment segment = this.getSegment(serviceName, false);
        if (segment == null) {
            return false;
        }
        return segment.hasRegistration(topic);
    }

    @Override
    public void publishEvent(String serviceName, String topic, Object event, int orderKey) {
        Collection<EventRegistration> registrations = this.getRegistrations(serviceName, topic);
        this.publishEvent(serviceName, registrations, event, orderKey);
    }

    @Override
    public void publishEvent(String serviceName, EventRegistration registration, Object event, int orderKey) {
        if (!(registration instanceof Registration)) {
            throw new IllegalArgumentException();
        }
        if (this.isLocal(registration)) {
            this.executeLocal(serviceName, event, registration, orderKey);
        } else {
            EventEnvelope eventEnvelope = new EventEnvelope(registration.getId(), serviceName, event);
            this.sendEvent(registration.getSubscriber(), eventEnvelope, orderKey);
        }
    }

    @Override
    public void publishEvent(String serviceName, Collection<EventRegistration> registrations, Object event, int orderKey) {
        Object eventData = null;
        for (EventRegistration registration : registrations) {
            if (!(registration instanceof Registration)) {
                throw new IllegalArgumentException();
            }
            if (this.isLocal(registration)) {
                this.executeLocal(serviceName, event, registration, orderKey);
                continue;
            }
            if (eventData == null) {
                eventData = this.serializationService.toData(event);
            }
            EventEnvelope eventEnvelope = new EventEnvelope(registration.getId(), serviceName, eventData);
            this.sendEvent(registration.getSubscriber(), eventEnvelope, orderKey);
        }
    }

    @Override
    public void publishRemoteEvent(String serviceName, Collection<EventRegistration> registrations, Object event, int orderKey) {
        if (registrations.isEmpty()) {
            return;
        }
        Object eventData = this.serializationService.toData(event);
        for (EventRegistration registration : registrations) {
            if (!(registration instanceof Registration)) {
                throw new IllegalArgumentException();
            }
            if (this.isLocal(registration)) continue;
            EventEnvelope eventEnvelope = new EventEnvelope(registration.getId(), serviceName, eventData);
            this.sendEvent(registration.getSubscriber(), eventEnvelope, orderKey);
        }
    }

    private void executeLocal(String serviceName, Object event, EventRegistration registration, int orderKey) {
        block5: {
            if (!this.nodeEngine.isRunning()) {
                return;
            }
            Registration reg = (Registration)registration;
            try {
                if (reg.getListener() != null) {
                    this.eventExecutor.execute(new LocalEventDispatcher(this, serviceName, event, reg.getListener(), orderKey, this.eventQueueTimeoutMs));
                } else {
                    this.logger.warning("Something seems wrong! Listener instance is null! -> " + reg);
                }
            }
            catch (RejectedExecutionException e) {
                this.rejectedCount.inc();
                if (!this.eventExecutor.isLive()) break block5;
                this.logFailure("EventQueue overloaded! %s failed to publish to %s:%s", event, reg.getServiceName(), reg.getTopic());
            }
        }
    }

    private void sendEvent(Address subscriber, EventEnvelope eventEnvelope, int orderKey) {
        boolean sync;
        String serviceName = eventEnvelope.getServiceName();
        EventServiceSegment segment = this.getSegment(serviceName, true);
        boolean bl = sync = segment.incrementPublish() % (long)this.eventSyncFrequency == 0L;
        if (sync) {
            SendEventOperation op = new SendEventOperation(eventEnvelope, orderKey);
            InternalCompletableFuture f = this.nodeEngine.getOperationService().createInvocationBuilder(serviceName, (Operation)op, subscriber).setTryCount(50).invoke();
            try {
                f.get(this.sendEventSyncTimeoutMillis, TimeUnit.MILLISECONDS);
            }
            catch (Exception e) {
                this.syncDeliveryFailureCount.inc();
                if (this.logger.isFinestEnabled()) {
                    this.logger.finest("Sync event delivery failed. Event: " + eventEnvelope, e);
                }
            }
        } else {
            Packet packet = new Packet(this.serializationService.toBytes(eventEnvelope), orderKey).setPacketType(Packet.Type.EVENT);
            EndpointManager em = this.nodeEngine.getNode().getNetworkingService().getEndpointManager(EndpointQualifier.MEMBER);
            if (!em.transmit(packet, subscriber) && this.nodeEngine.isRunning()) {
                this.logFailure("Failed to send event packet to: %s, connection might not be alive.", subscriber);
            }
        }
    }

    public EventServiceSegment getSegment(String service, boolean forceCreate) {
        EventServiceSegment segment = (EventServiceSegment)this.segments.get(service);
        if (segment == null && forceCreate) {
            EventServiceSegment newSegment = new EventServiceSegment(service, this.nodeEngine.getService(service));
            EventServiceSegment existingSegment = this.segments.putIfAbsent(service, newSegment);
            if (existingSegment == null) {
                segment = newSegment;
                this.nodeEngine.getMetricsRegistry().scanAndRegister(newSegment, "event.[" + service + "]");
            } else {
                segment = existingSegment;
            }
        }
        return segment;
    }

    boolean isLocal(EventRegistration reg) {
        return this.nodeEngine.getThisAddress().equals(reg.getSubscriber());
    }

    @Override
    public void executeEventCallback(Runnable callback) {
        block3: {
            if (!this.nodeEngine.isRunning()) {
                return;
            }
            try {
                this.eventExecutor.execute(callback);
            }
            catch (RejectedExecutionException e) {
                this.rejectedCount.inc();
                if (!this.eventExecutor.isLive()) break block3;
                this.logFailure("EventQueue overloaded! Failed to execute event callback: %s", callback);
            }
        }
    }

    @Override
    public void accept(Packet packet) {
        block2: {
            try {
                this.eventExecutor.execute(new RemoteEventProcessor(this, packet));
            }
            catch (RejectedExecutionException e) {
                this.rejectedCount.inc();
                if (!this.eventExecutor.isLive()) break block2;
                Connection conn = packet.getConn();
                String endpoint = conn.getEndPoint() != null ? conn.getEndPoint().toString() : conn.toString();
                this.logFailure("EventQueue overloaded! Failed to process event packet sent from: %s", endpoint);
            }
        }
    }

    @Override
    public Operation getPreJoinOperation() {
        return this.getOnJoinRegistrationOperation();
    }

    @Override
    public Operation getPostJoinOperation() {
        ClusterService clusterService = this.nodeEngine.getClusterService();
        return clusterService.isMaster() ? null : this.getOnJoinRegistrationOperation();
    }

    private OnJoinRegistrationOperation getOnJoinRegistrationOperation() {
        LinkedList<Registration> registrations = new LinkedList<Registration>();
        for (EventServiceSegment segment : this.segments.values()) {
            segment.collectRemoteRegistrations(registrations);
        }
        return registrations.isEmpty() ? null : new OnJoinRegistrationOperation(registrations);
    }

    public void shutdown() {
        this.logger.finest("Stopping event executor...");
        this.eventExecutor.shutdown();
        for (EventServiceSegment segment : this.segments.values()) {
            segment.clear();
        }
        this.segments.clear();
    }

    public void onMemberLeft(MemberImpl member) {
        Address address = member.getAddress();
        for (EventServiceSegment segment : this.segments.values()) {
            segment.onMemberLeft(address);
        }
    }

    private void logFailure(String message, Object ... args) {
        Level level;
        this.totalFailures.inc();
        long total = this.totalFailures.get();
        Level level2 = level = total % 1000L == 0L ? Level.WARNING : Level.FINEST;
        if (this.logger.isLoggable(level)) {
            this.logger.log(level, String.format(message, args));
        }
    }
}

