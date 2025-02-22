/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.properties;

import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.internal.cluster.fd.ClusterFailureDetectorType;
import com.hazelcast.internal.diagnostics.HealthMonitorLevel;
import com.hazelcast.query.impl.IndexCopyBehavior;
import com.hazelcast.query.impl.predicates.QueryOptimizerFactory;
import com.hazelcast.spi.properties.HazelcastProperty;
import java.util.concurrent.TimeUnit;

public final class GroupProperty {
    public static final HazelcastProperty APPLICATION_VALIDATION_TOKEN = new HazelcastProperty("hazelcast.application.validation.token");
    public static final HazelcastProperty PARTITION_COUNT = new HazelcastProperty("hazelcast.partition.count", 271);
    public static final HazelcastProperty PARTITION_OPERATION_THREAD_COUNT = new HazelcastProperty("hazelcast.operation.thread.count", -1);
    public static final HazelcastProperty GENERIC_OPERATION_THREAD_COUNT = new HazelcastProperty("hazelcast.operation.generic.thread.count", -1);
    public static final HazelcastProperty PRIORITY_GENERIC_OPERATION_THREAD_COUNT = new HazelcastProperty("hazelcast.operation.priority.generic.thread.count", 1);
    public static final HazelcastProperty RESPONSE_THREAD_COUNT = new HazelcastProperty("hazelcast.operation.response.thread.count", 2);
    public static final HazelcastProperty CLIENT_ENGINE_THREAD_COUNT = new HazelcastProperty("hazelcast.clientengine.thread.count", -1);
    public static final HazelcastProperty CLIENT_ENGINE_QUERY_THREAD_COUNT = new HazelcastProperty("hazelcast.clientengine.query.thread.count", -1);
    public static final HazelcastProperty CLIENT_ENGINE_BLOCKING_THREAD_COUNT = new HazelcastProperty("hazelcast.clientengine.blocking.thread.count", -1);
    public static final HazelcastProperty CLIENT_ENDPOINT_REMOVE_DELAY_SECONDS = new HazelcastProperty("hazelcast.client.endpoint.remove.delay.seconds", 60);
    public static final HazelcastProperty EVENT_THREAD_COUNT = new HazelcastProperty("hazelcast.event.thread.count", 5);
    public static final HazelcastProperty EVENT_QUEUE_CAPACITY = new HazelcastProperty("hazelcast.event.queue.capacity", 1000000);
    public static final HazelcastProperty EVENT_QUEUE_TIMEOUT_MILLIS = new HazelcastProperty("hazelcast.event.queue.timeout.millis", 250, TimeUnit.MILLISECONDS);
    public static final HazelcastProperty EVENT_SYNC_TIMEOUT_MILLIS = new HazelcastProperty("hazelcast.event.sync.timeout.millis", 5000, TimeUnit.MILLISECONDS);
    public static final HazelcastProperty HEALTH_MONITORING_LEVEL = new HazelcastProperty("hazelcast.health.monitoring.level", HealthMonitorLevel.SILENT.toString());
    public static final HazelcastProperty HEALTH_MONITORING_DELAY_SECONDS = new HazelcastProperty("hazelcast.health.monitoring.delay.seconds", 20, TimeUnit.SECONDS);
    public static final HazelcastProperty HEALTH_MONITORING_THRESHOLD_MEMORY_PERCENTAGE = new HazelcastProperty("hazelcast.health.monitoring.threshold.memory.percentage", 70);
    public static final HazelcastProperty HEALTH_MONITORING_THRESHOLD_CPU_PERCENTAGE = new HazelcastProperty("hazelcast.health.monitoring.threshold.cpu.percentage", 70);
    public static final HazelcastProperty IO_THREAD_COUNT = new HazelcastProperty("hazelcast.io.thread.count", 3);
    public static final HazelcastProperty IO_INPUT_THREAD_COUNT = new HazelcastProperty("hazelcast.io.input.thread.count", IO_THREAD_COUNT);
    public static final HazelcastProperty IO_OUTPUT_THREAD_COUNT = new HazelcastProperty("hazelcast.io.output.thread.count", IO_THREAD_COUNT);
    public static final HazelcastProperty IO_BALANCER_INTERVAL_SECONDS = new HazelcastProperty("hazelcast.io.balancer.interval.seconds", 20, TimeUnit.SECONDS);
    public static final HazelcastProperty PREFER_IPv4_STACK = new HazelcastProperty("hazelcast.prefer.ipv4.stack", true);
    @Deprecated
    public static final HazelcastProperty VERSION_CHECK_ENABLED = new HazelcastProperty("hazelcast.version.check.enabled", true);
    public static final HazelcastProperty PHONE_HOME_ENABLED = new HazelcastProperty("hazelcast.phone.home.enabled", true);
    public static final HazelcastProperty CONNECT_ALL_WAIT_SECONDS = new HazelcastProperty("hazelcast.connect.all.wait.seconds", 120, TimeUnit.SECONDS);
    @Deprecated
    public static final HazelcastProperty MEMCACHE_ENABLED = new HazelcastProperty("hazelcast.memcache.enabled", false);
    @Deprecated
    public static final HazelcastProperty REST_ENABLED = new HazelcastProperty("hazelcast.rest.enabled", false);
    @Deprecated
    public static final HazelcastProperty HTTP_HEALTHCHECK_ENABLED = new HazelcastProperty("hazelcast.http.healthcheck.enabled", false);
    public static final HazelcastProperty MAP_LOAD_CHUNK_SIZE = new HazelcastProperty("hazelcast.map.load.chunk.size", 1000);
    public static final HazelcastProperty MERGE_FIRST_RUN_DELAY_SECONDS = new HazelcastProperty("hazelcast.merge.first.run.delay.seconds", 300, TimeUnit.SECONDS);
    public static final HazelcastProperty MERGE_NEXT_RUN_DELAY_SECONDS = new HazelcastProperty("hazelcast.merge.next.run.delay.seconds", 120, TimeUnit.SECONDS);
    public static final HazelcastProperty OPERATION_CALL_TIMEOUT_MILLIS = new HazelcastProperty("hazelcast.operation.call.timeout.millis", 60000, TimeUnit.MILLISECONDS);
    public static final HazelcastProperty OPERATION_BACKUP_TIMEOUT_MILLIS = new HazelcastProperty("hazelcast.operation.backup.timeout.millis", 5000, TimeUnit.MILLISECONDS);
    public static final HazelcastProperty FAIL_ON_INDETERMINATE_OPERATION_STATE = new HazelcastProperty("hazelcast.operation.fail.on.indeterminate.state", false);
    public static final HazelcastProperty INVOCATION_MAX_RETRY_COUNT = new HazelcastProperty("hazelcast.invocation.max.retry.count", 250);
    public static final HazelcastProperty INVOCATION_RETRY_PAUSE = new HazelcastProperty("hazelcast.invocation.retry.pause.millis", 500L, TimeUnit.MILLISECONDS);
    public static final HazelcastProperty SOCKET_BIND_ANY = new HazelcastProperty("hazelcast.socket.bind.any", true);
    public static final HazelcastProperty SOCKET_SERVER_BIND_ANY = new HazelcastProperty("hazelcast.socket.server.bind.any", SOCKET_BIND_ANY);
    public static final HazelcastProperty SOCKET_CLIENT_BIND_ANY = new HazelcastProperty("hazelcast.socket.client.bind.any", SOCKET_BIND_ANY);
    public static final HazelcastProperty SOCKET_CLIENT_BIND = new HazelcastProperty("hazelcast.socket.client.bind", true);
    public static final HazelcastProperty SOCKET_RECEIVE_BUFFER_SIZE = new HazelcastProperty("hazelcast.socket.receive.buffer.size", 128);
    public static final HazelcastProperty SOCKET_SEND_BUFFER_SIZE = new HazelcastProperty("hazelcast.socket.send.buffer.size", 128);
    public static final HazelcastProperty SOCKET_BUFFER_DIRECT = new HazelcastProperty("hazelcast.socket.buffer.direct", false);
    public static final HazelcastProperty SOCKET_CLIENT_RECEIVE_BUFFER_SIZE = new HazelcastProperty("hazelcast.socket.client.receive.buffer.size", -1);
    public static final HazelcastProperty SOCKET_CLIENT_SEND_BUFFER_SIZE = new HazelcastProperty("hazelcast.socket.client.send.buffer.size", -1);
    public static final HazelcastProperty SOCKET_CLIENT_BUFFER_DIRECT = new HazelcastProperty("hazelcast.socket.client.buffer.direct", false);
    public static final HazelcastProperty SOCKET_LINGER_SECONDS = new HazelcastProperty("hazelcast.socket.linger.seconds", -1, TimeUnit.SECONDS);
    public static final HazelcastProperty SOCKET_CONNECT_TIMEOUT_SECONDS = new HazelcastProperty("hazelcast.socket.connect.timeout.seconds", 10, TimeUnit.SECONDS);
    public static final HazelcastProperty SOCKET_KEEP_ALIVE = new HazelcastProperty("hazelcast.socket.keep.alive", true);
    public static final HazelcastProperty SOCKET_NO_DELAY = new HazelcastProperty("hazelcast.socket.no.delay", true);
    public static final HazelcastProperty SHUTDOWNHOOK_ENABLED = new HazelcastProperty("hazelcast.shutdownhook.enabled", true);
    public static final HazelcastProperty SHUTDOWNHOOK_POLICY = new HazelcastProperty("hazelcast.shutdownhook.policy", "TERMINATE");
    public static final HazelcastProperty WAIT_SECONDS_BEFORE_JOIN = new HazelcastProperty("hazelcast.wait.seconds.before.join", 5, TimeUnit.SECONDS);
    public static final HazelcastProperty MAX_WAIT_SECONDS_BEFORE_JOIN = new HazelcastProperty("hazelcast.max.wait.seconds.before.join", 20, TimeUnit.SECONDS);
    public static final HazelcastProperty MAX_JOIN_SECONDS = new HazelcastProperty("hazelcast.max.join.seconds", 300, TimeUnit.SECONDS);
    public static final HazelcastProperty MAX_JOIN_MERGE_TARGET_SECONDS = new HazelcastProperty("hazelcast.max.join.merge.target.seconds", 20, TimeUnit.SECONDS);
    public static final HazelcastProperty HEARTBEAT_INTERVAL_SECONDS = new HazelcastProperty("hazelcast.heartbeat.interval.seconds", 5, TimeUnit.SECONDS);
    public static final HazelcastProperty MASTERSHIP_CLAIM_TIMEOUT_SECONDS = new HazelcastProperty("hazelcast.mastership.claim.timeout.seconds", 120, TimeUnit.SECONDS);
    public static final HazelcastProperty MAX_NO_HEARTBEAT_SECONDS = new HazelcastProperty("hazelcast.max.no.heartbeat.seconds", 60, TimeUnit.SECONDS);
    @Deprecated
    public static final HazelcastProperty MASTER_CONFIRMATION_INTERVAL_SECONDS = new HazelcastProperty("hazelcast.master.confirmation.interval.seconds", 30, TimeUnit.SECONDS);
    @Deprecated
    public static final HazelcastProperty MAX_NO_MASTER_CONFIRMATION_SECONDS = new HazelcastProperty("hazelcast.max.no.master.confirmation.seconds", 150, TimeUnit.SECONDS);
    public static final HazelcastProperty HEARTBEAT_FAILURE_DETECTOR_TYPE = new HazelcastProperty("hazelcast.heartbeat.failuredetector.type", ClusterFailureDetectorType.DEADLINE.toString());
    public static final HazelcastProperty MEMBER_LIST_PUBLISH_INTERVAL_SECONDS = new HazelcastProperty("hazelcast.member.list.publish.interval.seconds", 60, TimeUnit.SECONDS);
    public static final HazelcastProperty CLIENT_HEARTBEAT_TIMEOUT_SECONDS = new HazelcastProperty("hazelcast.client.max.no.heartbeat.seconds", 300, TimeUnit.SECONDS);
    public static final HazelcastProperty CLUSTER_SHUTDOWN_TIMEOUT_SECONDS = new HazelcastProperty("hazelcast.cluster.shutdown.timeout.seconds", 900, TimeUnit.SECONDS);
    public static final HazelcastProperty ICMP_ENABLED = new HazelcastProperty("hazelcast.icmp.enabled", false);
    public static final HazelcastProperty ICMP_PARALLEL_MODE = new HazelcastProperty("hazelcast.icmp.parallel.mode", true);
    public static final HazelcastProperty ICMP_ECHO_FAIL_FAST = new HazelcastProperty("hazelcast.icmp.echo.fail.fast.on.startup", true);
    public static final HazelcastProperty ICMP_TIMEOUT = new HazelcastProperty("hazelcast.icmp.timeout", 1000, TimeUnit.MILLISECONDS);
    public static final HazelcastProperty ICMP_INTERVAL = new HazelcastProperty("hazelcast.icmp.interval", 1000, TimeUnit.MILLISECONDS);
    public static final HazelcastProperty ICMP_MAX_ATTEMPTS = new HazelcastProperty("hazelcast.icmp.max.attempts", 3);
    public static final HazelcastProperty ICMP_TTL = new HazelcastProperty("hazelcast.icmp.ttl", 0);
    public static final HazelcastProperty INITIAL_MIN_CLUSTER_SIZE = new HazelcastProperty("hazelcast.initial.min.cluster.size", 0);
    public static final HazelcastProperty INITIAL_WAIT_SECONDS = new HazelcastProperty("hazelcast.initial.wait.seconds", 0, TimeUnit.SECONDS);
    public static final HazelcastProperty TCP_JOIN_PORT_TRY_COUNT = new HazelcastProperty("hazelcast.tcp.join.port.try.count", 3);
    public static final HazelcastProperty TCP_PREVIOUSLY_JOINED_MEMBER_ADDRESS_RETENTION_DURATION = new HazelcastProperty("hazelcast.tcp.join.previously.joined.member.address.retention.seconds", 14400, TimeUnit.SECONDS);
    public static final HazelcastProperty MAP_REPLICA_SCHEDULED_TASK_DELAY_SECONDS = new HazelcastProperty("hazelcast.map.replica.scheduled.task.delay.seconds", 10, TimeUnit.SECONDS);
    public static final HazelcastProperty MAP_EXPIRY_DELAY_SECONDS = new HazelcastProperty("hazelcast.map.expiry.delay.seconds", 10, TimeUnit.SECONDS);
    public static final HazelcastProperty MAP_EVICTION_BATCH_SIZE = new HazelcastProperty("hazelcast.map.eviction.batch.size", 1);
    public static final HazelcastProperty MAP_LOAD_ALL_PUBLISHES_ADDED_EVENT = new HazelcastProperty("hazelcast.map.loadAll.publishes.added.event", false);
    public static final HazelcastProperty LOGGING_TYPE = new HazelcastProperty("hazelcast.logging.type", "jdk");
    public static final HazelcastProperty ENABLE_JMX = new HazelcastProperty("hazelcast.jmx", false);
    public static final HazelcastProperty JMX_UPDATE_INTERVAL_SECONDS = new HazelcastProperty("hazelcast.jmx.update.interval.seconds", 5, TimeUnit.SECONDS);
    public static final HazelcastProperty MC_MAX_VISIBLE_SLOW_OPERATION_COUNT = new HazelcastProperty("hazelcast.mc.max.visible.slow.operations.count", 10);
    @Deprecated
    public static final HazelcastProperty MC_URL_CHANGE_ENABLED = new HazelcastProperty("hazelcast.mc.url.change.enabled", true);
    public static final HazelcastProperty CONNECTION_MONITOR_INTERVAL = new HazelcastProperty("hazelcast.connection.monitor.interval", 100, TimeUnit.MILLISECONDS);
    public static final HazelcastProperty CONNECTION_MONITOR_MAX_FAULTS = new HazelcastProperty("hazelcast.connection.monitor.max.faults", 3);
    public static final HazelcastProperty PARTITION_MIGRATION_INTERVAL = new HazelcastProperty("hazelcast.partition.migration.interval", 0, TimeUnit.SECONDS);
    public static final HazelcastProperty PARTITION_MIGRATION_TIMEOUT = new HazelcastProperty("hazelcast.partition.migration.timeout", 300, TimeUnit.SECONDS);
    public static final HazelcastProperty PARTITION_FRAGMENTED_MIGRATION_ENABLED = new HazelcastProperty("hazelcast.partition.migration.fragments.enabled", true);
    public static final HazelcastProperty DISABLE_STALE_READ_ON_PARTITION_MIGRATION = new HazelcastProperty("hazelcast.partition.migration.stale.read.disabled", false);
    public static final HazelcastProperty PARTITION_TABLE_SEND_INTERVAL = new HazelcastProperty("hazelcast.partition.table.send.interval", 15, TimeUnit.SECONDS);
    public static final HazelcastProperty PARTITION_BACKUP_SYNC_INTERVAL = new HazelcastProperty("hazelcast.partition.backup.sync.interval", 30, TimeUnit.SECONDS);
    public static final HazelcastProperty PARTITION_MAX_PARALLEL_REPLICATIONS = new HazelcastProperty("hazelcast.partition.max.parallel.replications", 5);
    public static final HazelcastProperty PARTITIONING_STRATEGY_CLASS = new HazelcastProperty("hazelcast.partitioning.strategy.class", "");
    public static final HazelcastProperty GRACEFUL_SHUTDOWN_MAX_WAIT = new HazelcastProperty("hazelcast.graceful.shutdown.max.wait", 600, TimeUnit.SECONDS);
    public static final HazelcastProperty SLOW_OPERATION_DETECTOR_ENABLED = new HazelcastProperty("hazelcast.slow.operation.detector.enabled", true);
    public static final HazelcastProperty SLOW_OPERATION_DETECTOR_THRESHOLD_MILLIS = new HazelcastProperty("hazelcast.slow.operation.detector.threshold.millis", 10000, TimeUnit.MILLISECONDS);
    public static final HazelcastProperty SLOW_OPERATION_DETECTOR_LOG_RETENTION_SECONDS = new HazelcastProperty("hazelcast.slow.operation.detector.log.retention.seconds", 3600, TimeUnit.SECONDS);
    public static final HazelcastProperty SLOW_OPERATION_DETECTOR_LOG_PURGE_INTERVAL_SECONDS = new HazelcastProperty("hazelcast.slow.operation.detector.log.purge.interval.seconds", 300, TimeUnit.SECONDS);
    public static final HazelcastProperty SLOW_OPERATION_DETECTOR_STACK_TRACE_LOGGING_ENABLED = new HazelcastProperty("hazelcast.slow.operation.detector.stacktrace.logging.enabled", false);
    @Deprecated
    public static final HazelcastProperty SLOW_INVOCATION_DETECTOR_THRESHOLD_MILLIS = new HazelcastProperty("hazelcast.slow.invocation.detector.threshold.millis", -1, TimeUnit.MILLISECONDS);
    public static final HazelcastProperty LOCK_MAX_LEASE_TIME_SECONDS = new HazelcastProperty("hazelcast.lock.max.lease.time.seconds", Long.MAX_VALUE, TimeUnit.SECONDS);
    public static final HazelcastProperty ENTERPRISE_LICENSE_KEY = new HazelcastProperty("hazelcast.enterprise.license.key");
    public static final HazelcastProperty MAP_WRITE_BEHIND_QUEUE_CAPACITY = new HazelcastProperty("hazelcast.map.write.behind.queue.capacity", 50000);
    public static final HazelcastProperty CACHE_INVALIDATION_MESSAGE_BATCH_ENABLED = new HazelcastProperty("hazelcast.cache.invalidation.batch.enabled", true);
    public static final HazelcastProperty CACHE_INVALIDATION_MESSAGE_BATCH_SIZE = new HazelcastProperty("hazelcast.cache.invalidation.batch.size", 100);
    public static final HazelcastProperty CACHE_INVALIDATION_MESSAGE_BATCH_FREQUENCY_SECONDS = new HazelcastProperty("hazelcast.cache.invalidation.batchfrequency.seconds", 10, TimeUnit.SECONDS);
    public static final HazelcastProperty MAP_INVALIDATION_MESSAGE_BATCH_ENABLED = new HazelcastProperty("hazelcast.map.invalidation.batch.enabled", true);
    public static final HazelcastProperty MAP_INVALIDATION_MESSAGE_BATCH_SIZE = new HazelcastProperty("hazelcast.map.invalidation.batch.size", 100);
    public static final HazelcastProperty MAP_INVALIDATION_MESSAGE_BATCH_FREQUENCY_SECONDS = new HazelcastProperty("hazelcast.map.invalidation.batchfrequency.seconds", 10, TimeUnit.SECONDS);
    public static final HazelcastProperty BACKPRESSURE_ENABLED = new HazelcastProperty("hazelcast.backpressure.enabled", false);
    public static final HazelcastProperty BACKPRESSURE_SYNCWINDOW = new HazelcastProperty("hazelcast.backpressure.syncwindow", 100);
    public static final HazelcastProperty BACKPRESSURE_BACKOFF_TIMEOUT_MILLIS = new HazelcastProperty("hazelcast.backpressure.backoff.timeout.millis", 60000, TimeUnit.MILLISECONDS);
    public static final HazelcastProperty BACKPRESSURE_MAX_CONCURRENT_INVOCATIONS_PER_PARTITION = new HazelcastProperty("hazelcast.backpressure.max.concurrent.invocations.per.partition", 100);
    public static final HazelcastProperty QUERY_PREDICATE_PARALLEL_EVALUATION = new HazelcastProperty("hazelcast.query.predicate.parallel.evaluation", false);
    public static final HazelcastProperty AGGREGATION_ACCUMULATION_PARALLEL_EVALUATION = new HazelcastProperty("hazelcast.aggregation.accumulation.parallel.evaluation", true);
    public static final HazelcastProperty QUERY_RESULT_SIZE_LIMIT = new HazelcastProperty("hazelcast.query.result.size.limit", -1);
    public static final HazelcastProperty QUERY_MAX_LOCAL_PARTITION_LIMIT_FOR_PRE_CHECK = new HazelcastProperty("hazelcast.query.max.local.partition.limit.for.precheck", 3);
    public static final HazelcastProperty QUERY_OPTIMIZER_TYPE = new HazelcastProperty("hazelcast.query.optimizer.type", QueryOptimizerFactory.Type.RULES.toString());
    public static final HazelcastProperty INDEX_COPY_BEHAVIOR = new HazelcastProperty("hazelcast.index.copy.behavior", IndexCopyBehavior.COPY_ON_READ.toString());
    public static final HazelcastProperty JCACHE_PROVIDER_TYPE = new HazelcastProperty("hazelcast.jcache.provider.type");
    public static final HazelcastProperty DISCOVERY_SPI_ENABLED = new HazelcastProperty("hazelcast.discovery.enabled", false);
    public static final HazelcastProperty DISCOVERY_SPI_PUBLIC_IP_ENABLED = new HazelcastProperty("hazelcast.discovery.public.ip.enabled", false);
    public static final HazelcastProperty COMPATIBILITY_3_6_CLIENT_ENABLED = new HazelcastProperty("hazelcast.compatibility.3.6.client", false);
    public static final HazelcastProperty SERIALIZATION_VERSION = new HazelcastProperty("hazelcast.serialization.version", BuildInfoProvider.getBuildInfo().getSerializationVersion());
    public static final HazelcastProperty INIT_CLUSTER_VERSION = new HazelcastProperty("hazelcast.init.cluster.version");
    public static final HazelcastProperty USE_LEGACY_MEMBER_LIST_FORMAT = new HazelcastProperty("hazelcast.legacy.memberlist.format.enabled", false);
    public static final HazelcastProperty BIND_SPOOFING_CHECKS = new HazelcastProperty("hazelcast.nio.tcp.spoofing.checks", false);
    public static final HazelcastProperty TASK_SCHEDULER_REMOVE_ON_CANCEL = new HazelcastProperty("hazelcast.executionservice.taskscheduler.remove.oncancel", true);
    public static final HazelcastProperty SEARCH_DYNAMIC_CONFIG_FIRST = new HazelcastProperty("hazelcast.data.search.dynamic.config.first.enabled", false);
    public static final HazelcastProperty WAN_CONSUMER_INVOCATION_THRESHOLD = new HazelcastProperty("hazelcast.wan.consumer.invocation.threshold", 50000);
    public static final HazelcastProperty WAN_CONSUMER_ACK_DELAY_BACKOFF_INIT_MS = new HazelcastProperty("hazelcast.wan.consumer.ack.delay.backoff.init", 1);
    public static final HazelcastProperty WAN_CONSUMER_ACK_DELAY_BACKOFF_MAX_MS = new HazelcastProperty("hazelcast.wan.consumer.ack.delay.backoff.max", 100);
    public static final HazelcastProperty WAN_CONSUMER_ACK_DELAY_BACKOFF_MULTIPLIER = new HazelcastProperty("hazelcast.wan.consumer.ack.delay.backoff.multiplier", "1.5");

    private GroupProperty() {
    }
}

