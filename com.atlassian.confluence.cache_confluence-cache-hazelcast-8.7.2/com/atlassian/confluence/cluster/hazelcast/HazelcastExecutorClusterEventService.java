/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.cluster.ClusterEventWrapper
 *  com.atlassian.confluence.impl.metrics.CoreMetrics
 *  com.atlassian.confluence.util.logging.LoggingContext
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableMap
 *  com.hazelcast.core.Cluster
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IExecutorService
 *  com.hazelcast.core.Member
 *  com.hazelcast.nio.ObjectDataInput
 *  com.hazelcast.nio.ObjectDataOutput
 *  com.hazelcast.nio.serialization.DataSerializable
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.Tag
 *  io.micrometer.core.instrument.Timer
 *  javax.annotation.Nullable
 *  org.joda.time.Duration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster.hazelcast;

import com.atlassian.confluence.cluster.hazelcast.HazelcastClusterEventService;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.impl.metrics.CoreMetrics;
import com.atlassian.confluence.util.logging.LoggingContext;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class HazelcastExecutorClusterEventService
implements HazelcastClusterEventService {
    private static final Logger log = LoggerFactory.getLogger(HazelcastExecutorClusterEventService.class);
    private static final Duration REMOTE_EVENT_TIMEOUT = Duration.standardSeconds((long)10L);
    protected static final String EXECUTOR_SVC_NAME = "cluster-manager-executor";
    private final HazelcastInstance hazelcast;
    private final MeterRegistry micrometerRegistry;

    public HazelcastExecutorClusterEventService(HazelcastInstance hazelcast, @Nullable MeterRegistry micrometerRegistry) {
        this.hazelcast = hazelcast;
        this.micrometerRegistry = micrometerRegistry;
    }

    @Override
    public void publishEventToCluster(Object clusterEvent) {
        IExecutorService svc = this.getExecutorService();
        Cluster cluster = this.hazelcast.getCluster();
        Member thisNode = cluster.getLocalMember();
        PublishConfluenceEvent command = new PublishConfluenceEvent(thisNode.getUuid(), clusterEvent);
        Object rawEvent = HazelcastExecutorClusterEventService.unwrapEvent(clusterEvent);
        Collection<Member> targetNodes = this.allNodesButThisNode();
        if (!targetNodes.isEmpty()) {
            log.debug("Executing publish command for {} from {} to other nodes", rawEvent, (Object)thisNode.getUuid());
            Timer timer = new Timer(this.micrometerRegistry, rawEvent.getClass());
            timer.timeWaitForAllNodes(targetNodes.size(), () -> {
                Map nodeResults = svc.submitToMembers((Callable)command, targetNodes);
                for (Map.Entry entry : nodeResults.entrySet()) {
                    Member member = (Member)entry.getKey();
                    timer.timeWaitForNode(member, () -> this.waitForNodeResponse(member, (Future)entry.getValue(), rawEvent));
                }
            });
            log.debug("Finished waiting for all nodes to respond to {}", rawEvent);
        } else {
            log.debug("No other nodes in the cluster, noone to propagate {} to", rawEvent);
        }
    }

    private void waitForNodeResponse(Member node, Future<Void> result, Object event) {
        String nodeId = node.getUuid();
        log.debug("Waiting for node {} to respond to {}", (Object)nodeId, event);
        try {
            result.get(REMOTE_EVENT_TIMEOUT.getStandardSeconds(), TimeUnit.SECONDS);
            log.debug("Received response from node {} for {}", (Object)nodeId, event);
        }
        catch (InterruptedException e) {
            log.warn("Interrupted while waiting for node {} to respond to {}", (Object)nodeId, event);
        }
        catch (ExecutionException e) {
            log.warn("Exception received from node {} while responding to {}", new Object[]{nodeId, event, e.getCause()});
        }
        catch (TimeoutException e) {
            log.warn("Timed out while waiting for node {} to respond to {}", (Object)nodeId, event);
        }
    }

    private Collection<Member> allNodesButThisNode() {
        Cluster cluster = this.hazelcast.getCluster();
        return Collections2.filter((Collection)cluster.getMembers(), (Predicate)Predicates.not((Predicate)Predicates.equalTo((Object)cluster.getLocalMember())));
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public IExecutorService getExecutorService() {
        return this.hazelcast.getExecutorService(EXECUTOR_SVC_NAME);
    }

    private static Object unwrapEvent(Object event) {
        return event instanceof ClusterEventWrapper ? ((ClusterEventWrapper)event).getEvent() : event;
    }

    private static final class Timer {
        private final MeterRegistry micrometerRegistry;
        private final Tag eventTypeTag;

        Timer(@Nullable MeterRegistry micrometerRegistry, Class<?> eventType) {
            this.micrometerRegistry = micrometerRegistry;
            this.eventTypeTag = Timer.eventType(eventType);
        }

        void timeWaitForAllNodes(int nodeCount, Runnable call) {
            this.callTimer(CoreMetrics.HAZELCAST_CLUSTER_EVENT_TOTAL_WAIT_TIME, this.eventTypeTag, Timer.memberCount(nodeCount)).accept(call);
        }

        void timeWaitForNode(Member member, Runnable call) {
            this.callTimer(CoreMetrics.HAZELCAST_CLUSTER_EVENT_MEMBER_WAIT_TIME, this.eventTypeTag, Timer.member(member)).accept(call);
        }

        private static Tag memberCount(int nodeCount) {
            return Tag.of((String)"memberCount", (String)String.valueOf(nodeCount));
        }

        private static Tag eventType(Class<?> eventType) {
            return Tag.of((String)"eventType", (String)eventType.getTypeName());
        }

        private static Tag member(Member member) {
            return Tag.of((String)"memberAddress", (String)String.valueOf(member.getAddress()));
        }

        private Consumer<Runnable> callTimer(CoreMetrics meter, Tag ... tags) {
            if (this.micrometerRegistry != null) {
                return arg_0 -> ((io.micrometer.core.instrument.Timer)meter.timer(this.micrometerRegistry, tags)).record(arg_0);
            }
            return Runnable::run;
        }
    }

    private static class PublishConfluenceEvent
    implements Callable<Void>,
    Serializable,
    DataSerializable {
        private String originatingMemberUuid;
        private Object event;

        private PublishConfluenceEvent() {
        }

        public PublishConfluenceEvent(String originatingMemberUuid, Object event) {
            this.originatingMemberUuid = originatingMemberUuid;
            this.event = event;
        }

        @Override
        public Void call() {
            ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(PublishConfluenceEvent.class.getClassLoader());
                log.debug("Relaying {} from node [{}]", HazelcastExecutorClusterEventService.unwrapEvent(this.event), (Object)this.originatingMemberUuid);
                LoggingContext.executeWithContext((Map)ImmutableMap.of((Object)"event", (Object)HazelcastExecutorClusterEventService.unwrapEvent(this.event).toString(), (Object)"originatingMemberUuid", (Object)this.originatingMemberUuid), () -> {
                    if (!ContainerManager.isContainerSetup()) {
                        log.debug("Cannot relay {} from node [{}] - container not yet set up", HazelcastExecutorClusterEventService.unwrapEvent(this.event), (Object)this.originatingMemberUuid);
                    } else {
                        PublishConfluenceEvent.getEventManager().publish(this.event);
                    }
                });
                Void void_ = null;
                return void_;
            }
            finally {
                Thread.currentThread().setContextClassLoader(originalClassLoader);
            }
        }

        private static EventPublisher getEventManager() {
            EventPublisher eventPublisher = (EventPublisher)ContainerManager.getComponent((String)"eventPublisher");
            if (null == eventPublisher) {
                throw new RuntimeException("ContainerManager.getComponent(\"eventPublisher\") returned null.");
            }
            return eventPublisher;
        }

        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeUTF(this.originatingMemberUuid);
            out.writeObject(this.event);
        }

        public void readData(ObjectDataInput in) throws IOException {
            this.originatingMemberUuid = in.readUTF();
            this.event = in.readObject();
        }
    }
}

