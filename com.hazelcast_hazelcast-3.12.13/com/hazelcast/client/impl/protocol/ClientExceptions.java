/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.CacheException
 *  javax.cache.integration.CacheLoaderException
 *  javax.cache.integration.CacheWriterException
 *  javax.cache.processor.EntryProcessorException
 */
package com.hazelcast.client.impl.protocol;

import com.hazelcast.cache.CacheNotExistsException;
import com.hazelcast.client.impl.StubAuthenticationException;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ErrorCodec;
import com.hazelcast.client.impl.protocol.exception.MaxMessageSizeExceeded;
import com.hazelcast.config.ConfigurationException;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.core.ConsistencyLostException;
import com.hazelcast.core.DuplicateInstanceNameException;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.HazelcastOverloadException;
import com.hazelcast.core.IndeterminateOperationStateException;
import com.hazelcast.core.LocalMemberResetException;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.cp.exception.CPGroupDestroyedException;
import com.hazelcast.cp.exception.CannotReplicateException;
import com.hazelcast.cp.exception.LeaderDemotedException;
import com.hazelcast.cp.exception.NotLeaderException;
import com.hazelcast.cp.exception.StaleAppendRequestException;
import com.hazelcast.cp.internal.datastructures.exception.WaitKeyCancelledException;
import com.hazelcast.cp.internal.session.SessionExpiredException;
import com.hazelcast.cp.lock.exception.LockAcquireLimitReachedException;
import com.hazelcast.cp.lock.exception.LockOwnershipLostException;
import com.hazelcast.crdt.MutationDisallowedException;
import com.hazelcast.crdt.TargetNotReplicaException;
import com.hazelcast.durableexecutor.StaleTaskIdException;
import com.hazelcast.flakeidgen.impl.NodeIdOutOfRangeException;
import com.hazelcast.internal.cluster.impl.ConfigMismatchException;
import com.hazelcast.map.QueryResultSizeExceededException;
import com.hazelcast.map.ReachedMaxSizeException;
import com.hazelcast.mapreduce.RemoteMapReduceException;
import com.hazelcast.mapreduce.TopologyChangedException;
import com.hazelcast.memory.NativeOutOfMemoryError;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.partition.NoDataMemberInClusterException;
import com.hazelcast.query.QueryException;
import com.hazelcast.quorum.QuorumException;
import com.hazelcast.replicatedmap.ReplicatedMapCantBeCreatedOnLiteMemberException;
import com.hazelcast.ringbuffer.StaleSequenceException;
import com.hazelcast.scheduledexecutor.DuplicateTaskException;
import com.hazelcast.scheduledexecutor.StaleTaskException;
import com.hazelcast.spi.exception.CallerNotMemberException;
import com.hazelcast.spi.exception.DistributedObjectDestroyedException;
import com.hazelcast.spi.exception.PartitionMigratingException;
import com.hazelcast.spi.exception.ResponseAlreadySentException;
import com.hazelcast.spi.exception.RetryableHazelcastException;
import com.hazelcast.spi.exception.RetryableIOException;
import com.hazelcast.spi.exception.ServiceNotFoundException;
import com.hazelcast.spi.exception.TargetDisconnectedException;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.spi.exception.WrongTargetException;
import com.hazelcast.topic.TopicOverloadException;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionNotActiveException;
import com.hazelcast.transaction.TransactionTimedOutException;
import com.hazelcast.util.AddressUtil;
import com.hazelcast.wan.WANReplicationQueueFullException;
import java.io.EOFException;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.UTFDataFormatException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;
import javax.cache.CacheException;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import javax.cache.processor.EntryProcessorException;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.transaction.xa.XAException;

public class ClientExceptions {
    private static final String CAUSED_BY_STACKTRACE_MARKER = "###### Caused by:";
    private final Map<Class, Integer> classToInt = new HashMap<Class, Integer>();

    public ClientExceptions(boolean jcacheAvailable) {
        if (jcacheAvailable) {
            this.register(4, CacheException.class);
            this.register(5, CacheLoaderException.class);
            this.register(7, CacheWriterException.class);
            this.register(18, EntryProcessorException.class);
        }
        this.register(1, ArrayIndexOutOfBoundsException.class);
        this.register(2, ArrayStoreException.class);
        this.register(3, StubAuthenticationException.class);
        this.register(6, CacheNotExistsException.class);
        this.register(8, CallerNotMemberException.class);
        this.register(9, CancellationException.class);
        this.register(10, ClassCastException.class);
        this.register(11, ClassNotFoundException.class);
        this.register(12, ConcurrentModificationException.class);
        this.register(13, ConfigMismatchException.class);
        this.register(14, ConfigurationException.class);
        this.register(15, DistributedObjectDestroyedException.class);
        this.register(16, DuplicateInstanceNameException.class);
        this.register(17, EOFException.class);
        this.register(19, ExecutionException.class);
        this.register(20, HazelcastException.class);
        this.register(21, HazelcastInstanceNotActiveException.class);
        this.register(22, HazelcastOverloadException.class);
        this.register(23, HazelcastSerializationException.class);
        this.register(24, IOException.class);
        this.register(25, IllegalArgumentException.class);
        this.register(26, IllegalAccessException.class);
        this.register(27, IllegalAccessError.class);
        this.register(28, IllegalMonitorStateException.class);
        this.register(29, IllegalStateException.class);
        this.register(30, IllegalThreadStateException.class);
        this.register(31, IndexOutOfBoundsException.class);
        this.register(32, InterruptedException.class);
        this.register(33, AddressUtil.InvalidAddressException.class);
        this.register(34, InvalidConfigurationException.class);
        this.register(35, MemberLeftException.class);
        this.register(36, NegativeArraySizeException.class);
        this.register(37, NoSuchElementException.class);
        this.register(38, NotSerializableException.class);
        this.register(39, NullPointerException.class);
        this.register(40, OperationTimeoutException.class);
        this.register(41, PartitionMigratingException.class);
        this.register(42, QueryException.class);
        this.register(43, QueryResultSizeExceededException.class);
        this.register(44, QuorumException.class);
        this.register(45, ReachedMaxSizeException.class);
        this.register(46, RejectedExecutionException.class);
        this.register(47, RemoteMapReduceException.class);
        this.register(48, ResponseAlreadySentException.class);
        this.register(49, RetryableHazelcastException.class);
        this.register(50, RetryableIOException.class);
        this.register(51, RuntimeException.class);
        this.register(52, SecurityException.class);
        this.register(53, SocketException.class);
        this.register(54, StaleSequenceException.class);
        this.register(55, TargetDisconnectedException.class);
        this.register(56, TargetNotMemberException.class);
        this.register(57, TimeoutException.class);
        this.register(58, TopicOverloadException.class);
        this.register(59, TopologyChangedException.class);
        this.register(60, TransactionException.class);
        this.register(61, TransactionNotActiveException.class);
        this.register(62, TransactionTimedOutException.class);
        this.register(63, URISyntaxException.class);
        this.register(64, UTFDataFormatException.class);
        this.register(65, UnsupportedOperationException.class);
        this.register(66, WrongTargetException.class);
        this.register(67, XAException.class);
        this.register(68, AccessControlException.class);
        this.register(69, LoginException.class);
        this.register(70, UnsupportedCallbackException.class);
        this.register(71, NoDataMemberInClusterException.class);
        this.register(72, ReplicatedMapCantBeCreatedOnLiteMemberException.class);
        this.register(73, MaxMessageSizeExceeded.class);
        this.register(74, WANReplicationQueueFullException.class);
        this.register(75, AssertionError.class);
        this.register(76, OutOfMemoryError.class);
        this.register(77, StackOverflowError.class);
        this.register(78, NativeOutOfMemoryError.class);
        this.register(79, ServiceNotFoundException.class);
        this.register(80, StaleTaskIdException.class);
        this.register(81, DuplicateTaskException.class);
        this.register(82, StaleTaskException.class);
        this.register(83, LocalMemberResetException.class);
        this.register(84, IndeterminateOperationStateException.class);
        this.register(85, NodeIdOutOfRangeException.class);
        this.register(86, TargetNotReplicaException.class);
        this.register(87, MutationDisallowedException.class);
        this.register(88, ConsistencyLostException.class);
        this.register(89, SessionExpiredException.class);
        this.register(90, WaitKeyCancelledException.class);
        this.register(91, LockAcquireLimitReachedException.class);
        this.register(92, LockOwnershipLostException.class);
        this.register(93, CPGroupDestroyedException.class);
        this.register(94, CannotReplicateException.class);
        this.register(95, LeaderDemotedException.class);
        this.register(96, StaleAppendRequestException.class);
        this.register(97, NotLeaderException.class);
        this.register(98, NoSuchMethodError.class);
        this.register(99, NoSuchMethodException.class);
        this.register(100, NoSuchFieldError.class);
        this.register(101, NoSuchFieldException.class);
        this.register(102, NoClassDefFoundError.class);
    }

    public ClientMessage createExceptionMessage(Throwable throwable) {
        String causeClassName;
        int causeErrorCode;
        int errorCode = this.getErrorCode(throwable);
        String message = throwable.getMessage();
        ArrayList<StackTraceElement> combinedStackTrace = new ArrayList<StackTraceElement>();
        Throwable t = throwable;
        while (t != null) {
            combinedStackTrace.addAll(Arrays.asList(t.getStackTrace()));
            if ((t = t.getCause()) == null) continue;
            String throwableToString = t.getClass().getName() + (t.getLocalizedMessage() != null ? ": " + t.getLocalizedMessage() : "");
            combinedStackTrace.add(new StackTraceElement("###### Caused by: (" + this.getErrorCode(t) + ") " + throwableToString + " ------", "", null, -1));
        }
        Throwable cause = throwable.getCause();
        if (cause != null) {
            causeErrorCode = this.getErrorCode(cause);
            causeClassName = cause.getClass().getName();
        } else {
            causeErrorCode = 0;
            causeClassName = null;
        }
        StackTraceElement[] combinedStackTraceArray = combinedStackTrace.toArray(new StackTraceElement[combinedStackTrace.size()]);
        return ErrorCodec.encode(errorCode, throwable.getClass().getName(), message, combinedStackTraceArray, causeErrorCode, causeClassName);
    }

    public void register(int errorCode, Class clazz) {
        Integer currentCode = this.classToInt.get(clazz);
        if (currentCode != null) {
            throw new HazelcastException("Class " + clazz.getName() + " already added with code: " + currentCode);
        }
        this.classToInt.put(clazz, errorCode);
    }

    private int getErrorCode(Throwable e) {
        Integer errorCode = this.classToInt.get(e.getClass());
        if (errorCode == null) {
            return 0;
        }
        return errorCode;
    }

    boolean isKnownClass(Class<? extends Throwable> aClass) {
        return this.classToInt.containsKey(aClass);
    }
}

