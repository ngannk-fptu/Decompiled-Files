/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.topic.impl.reliable;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.Member;
import com.hazelcast.core.Message;
import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.logging.ILogger;
import com.hazelcast.ringbuffer.ReadResultSet;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.ringbuffer.StaleSequenceException;
import com.hazelcast.spi.exception.DistributedObjectDestroyedException;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.topic.ReliableMessageListener;
import com.hazelcast.topic.impl.reliable.ReliableTopicMessage;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;

public abstract class MessageRunner<E>
implements ExecutionCallback<ReadResultSet<ReliableTopicMessage>> {
    protected final Ringbuffer<ReliableTopicMessage> ringbuffer;
    protected final ILogger logger;
    protected final ReliableMessageListener<E> listener;
    protected final String topicName;
    protected long sequence;
    private final SerializationService serializationService;
    private final ConcurrentMap<String, MessageRunner<E>> runnersMap;
    private final String id;
    private final Executor executor;
    private final int batchSze;
    private volatile boolean cancelled;

    public MessageRunner(String id, ReliableMessageListener<E> listener, Ringbuffer<ReliableTopicMessage> ringbuffer, String topicName, int batchSze, SerializationService serializationService, Executor executor, ConcurrentMap<String, MessageRunner<E>> runnersMap, ILogger logger) {
        this.id = id;
        this.listener = listener;
        this.ringbuffer = ringbuffer;
        this.topicName = topicName;
        this.serializationService = serializationService;
        this.logger = logger;
        this.batchSze = batchSze;
        this.executor = executor;
        this.runnersMap = runnersMap;
        long initialSequence = listener.retrieveInitialSequence();
        if (initialSequence == -1L) {
            initialSequence = ringbuffer.tailSequence() + 1L;
        }
        this.sequence = initialSequence;
    }

    public void next() {
        if (this.cancelled) {
            return;
        }
        ICompletableFuture<ReadResultSet<ReliableTopicMessage>> f = this.ringbuffer.readManyAsync(this.sequence, 1, this.batchSze, null);
        f.andThen(this, this.executor);
    }

    @Override
    public void onResponse(ReadResultSet<ReliableTopicMessage> result) {
        for (Object t : result) {
            block4: {
                ReliableTopicMessage message = (ReliableTopicMessage)t;
                if (this.cancelled) {
                    return;
                }
                try {
                    this.listener.storeSequence(this.sequence);
                    this.process(message);
                }
                catch (Throwable t2) {
                    if (!this.terminate(t2)) break block4;
                    this.cancel();
                    return;
                }
            }
            ++this.sequence;
        }
        this.next();
    }

    private void process(ReliableTopicMessage message) {
        this.updateStatistics();
        this.listener.onMessage(this.toMessage(message));
    }

    protected abstract void updateStatistics();

    private Message<E> toMessage(ReliableTopicMessage m) {
        Member member = this.getMember(m);
        Object payload = this.serializationService.toObject(m.getPayload());
        return new Message(this.topicName, payload, m.getPublishTime(), member);
    }

    protected abstract Member getMember(ReliableTopicMessage var1);

    @Override
    public void onFailure(Throwable t) {
        if (this.cancelled) {
            return;
        }
        if (this.handleInternalException(t = this.adjustThrowable(t))) {
            this.next();
        } else {
            this.cancel();
        }
    }

    protected boolean handleInternalException(Throwable t) {
        if (t instanceof OperationTimeoutException) {
            return this.handleOperationTimeoutException();
        }
        if (t instanceof IllegalArgumentException) {
            return this.handleIllegalArgumentException((IllegalArgumentException)t);
        }
        if (t instanceof StaleSequenceException) {
            return this.handleStaleSequenceException((StaleSequenceException)t);
        }
        if (t instanceof HazelcastInstanceNotActiveException) {
            if (this.logger.isFinestEnabled()) {
                this.logger.finest("Terminating MessageListener " + this.listener + " on topic: " + this.topicName + ".  Reason: HazelcastInstance is shutting down");
            }
        } else if (t instanceof DistributedObjectDestroyedException) {
            if (this.logger.isFinestEnabled()) {
                this.logger.finest("Terminating MessageListener " + this.listener + " on topic: " + this.topicName + ". Reason: Topic is destroyed");
            }
        } else {
            this.logger.warning("Terminating MessageListener " + this.listener + " on topic: " + this.topicName + ". Reason: Unhandled exception, message: " + t.getMessage(), t);
        }
        return false;
    }

    private boolean handleOperationTimeoutException() {
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("MessageListener " + this.listener + " on topic: " + this.topicName + " timed out. Continuing from last known sequence: " + this.sequence);
        }
        return true;
    }

    protected abstract Throwable adjustThrowable(Throwable var1);

    private boolean handleStaleSequenceException(StaleSequenceException staleSequenceException) {
        long headSeq = this.getHeadSequence(staleSequenceException);
        if (this.listener.isLossTolerant()) {
            if (this.logger.isFinestEnabled()) {
                this.logger.finest("MessageListener " + this.listener + " on topic: " + this.topicName + " ran into a stale sequence. Jumping from oldSequence: " + this.sequence + " to sequence: " + headSeq);
            }
            this.sequence = headSeq;
            return true;
        }
        this.logger.warning("Terminating MessageListener:" + this.listener + " on topic: " + this.topicName + ". Reason: The listener was too slow or the retention period of the message has been violated. head: " + headSeq + " sequence:" + this.sequence);
        return false;
    }

    protected abstract long getHeadSequence(StaleSequenceException var1);

    private boolean handleIllegalArgumentException(IllegalArgumentException t) {
        long currentHeadSequence = this.ringbuffer.headSequence();
        if (this.listener.isLossTolerant()) {
            if (this.logger.isFinestEnabled()) {
                this.logger.finest(String.format("MessageListener %s on topic %s requested a too large sequence: %s. . Jumping from old sequence: %s to sequence: %s", this.listener, this.topicName, t.getMessage(), this.sequence, currentHeadSequence));
            }
            this.sequence = currentHeadSequence;
            return true;
        }
        this.logger.warning("Terminating MessageListener:" + this.listener + " on topic: " + this.topicName + ". Reason: Underlying ring buffer data related to reliable topic is lost. ");
        return false;
    }

    public void cancel() {
        this.cancelled = true;
        this.runnersMap.remove(this.id);
    }

    private boolean terminate(Throwable failure) {
        if (this.cancelled) {
            return true;
        }
        try {
            boolean terminate = this.listener.isTerminal(failure);
            if (terminate) {
                this.logger.warning("Terminating MessageListener " + this.listener + " on topic: " + this.topicName + ". Reason: Unhandled exception, message: " + failure.getMessage(), failure);
            } else if (this.logger.isFinestEnabled()) {
                this.logger.finest("MessageListener " + this.listener + " on topic: " + this.topicName + " ran into an exception: message:" + failure.getMessage(), failure);
            }
            return terminate;
        }
        catch (Throwable t) {
            this.logger.warning("Terminating messageListener:" + this.listener + " on topic: " + this.topicName + ". Reason: Unhandled exception while calling ReliableMessageListener.isTerminal() method", t);
            return true;
        }
    }

    public boolean isCancelled() {
        return this.cancelled;
    }
}

