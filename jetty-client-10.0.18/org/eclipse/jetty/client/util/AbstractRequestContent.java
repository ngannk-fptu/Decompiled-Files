/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.thread.AutoLock
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client.util;

import java.lang.invoke.LambdaMetafactory;
import java.nio.ByteBuffer;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRequestContent
implements Request.Content {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRequestContent.class);
    private final AutoLock lock = new AutoLock();
    private final String contentType;

    protected AbstractRequestContent(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public Request.Content.Subscription subscribe(Request.Content.Consumer consumer, boolean emitInitialContent) {
        Request.Content.Subscription subscription = this.newSubscription(consumer, emitInitialContent);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Content subscription for {}: {}", (Object)subscription, (Object)consumer);
        }
        return subscription;
    }

    protected abstract Request.Content.Subscription newSubscription(Request.Content.Consumer var1, boolean var2);

    public static interface Producer {
        public boolean produce(ByteBuffer var1, boolean var2, Callback var3);
    }

    public abstract class AbstractSubscription
    implements Request.Content.Subscription {
        private final Request.Content.Consumer consumer;
        private final boolean emitInitialContent;
        private Throwable failure;
        private int demand;
        private boolean stalled;
        private boolean committed;

        public AbstractSubscription(Request.Content.Consumer consumer, boolean emitInitialContent) {
            this.consumer = consumer;
            this.emitInitialContent = emitInitialContent;
            this.stalled = true;
        }

        @Override
        public void demand() {
            boolean produce;
            try (AutoLock ignored = AbstractRequestContent.this.lock.lock();){
                ++this.demand;
                produce = this.stalled;
                if (this.stalled) {
                    this.stalled = false;
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Content demand, producing {} for {}", (Object)produce, (Object)this);
            }
            if (produce) {
                this.produce();
            }
        }

        /*
         * Unable to fully structure code
         */
        private void produce() {
            while (true) lbl-1000:
            // 4 sources

            {
                ignored = AbstractRequestContent.this.lock.lock();
                try {
                    failure = this.failure;
                    committed = this.committed;
                }
                finally {
                    if (ignored != null) {
                        ignored.close();
                    }
                }
                if (failure != null) {
                    this.notifyFailure(failure);
                    return;
                }
                if (committed || this.emitInitialContent) {
                    try {
                        if (this.produceContent((Producer)LambdaMetafactory.metafactory(null, null, null, (Ljava/nio/ByteBuffer;ZLorg/eclipse/jetty/util/Callback;)Z, processContent(java.nio.ByteBuffer boolean org.eclipse.jetty.util.Callback ), (Ljava/nio/ByteBuffer;ZLorg/eclipse/jetty/util/Callback;)Z)((AbstractSubscription)this))) ** GOTO lbl-1000
                        return;
                    }
                    catch (Throwable x) {
                        this.fail(x);
                    }
                    continue;
                }
                if (!this.processContent(BufferUtil.EMPTY_BUFFER, false, Callback.NOOP)) break;
            }
        }

        protected abstract boolean produceContent(Producer var1) throws Exception;

        @Override
        public void fail(Throwable failure) {
            try (AutoLock ignored = AbstractRequestContent.this.lock.lock();){
                if (this.failure == null) {
                    this.failure = failure;
                }
            }
        }

        private boolean processContent(ByteBuffer content, boolean last, Callback callback) {
            boolean noDemand;
            try (AutoLock ignored = AbstractRequestContent.this.lock.lock();){
                this.committed = true;
                --this.demand;
            }
            if (content != null) {
                this.notifyContent(content, last, callback);
            } else {
                callback.succeeded();
            }
            try (AutoLock ignored = AbstractRequestContent.this.lock.lock();){
                boolean bl = noDemand = this.demand == 0;
                if (noDemand) {
                    this.stalled = true;
                }
            }
            if (noDemand) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("No demand, processing stalled for {}", (Object)this);
                }
                return false;
            }
            return true;
        }

        protected void notifyContent(ByteBuffer buffer, boolean last, Callback callback) {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Notifying content last={} {} for {}", new Object[]{last, BufferUtil.toDetailString((ByteBuffer)buffer), this});
                }
                this.consumer.onContent(buffer, last, callback);
            }
            catch (Throwable x) {
                callback.failed(x);
                this.fail(x);
            }
        }

        private void notifyFailure(Throwable failure) {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Notifying failure for {}", (Object)this, (Object)failure);
                }
                this.consumer.onFailure(failure);
            }
            catch (Exception x) {
                LOG.trace("Failure while notifying content failure {}", (Object)failure, (Object)x);
            }
        }

        public String toString() {
            boolean committed;
            boolean stalled;
            int demand;
            try (AutoLock ignored = AbstractRequestContent.this.lock.lock();){
                demand = this.demand;
                stalled = this.stalled;
                committed = this.committed;
            }
            return String.format("%s.%s@%x[demand=%d,stalled=%b,committed=%b,emitInitial=%b]", this.getClass().getEnclosingClass().getSimpleName(), this.getClass().getSimpleName(), this.hashCode(), demand, stalled, committed, this.emitInitialContent);
        }
    }
}

