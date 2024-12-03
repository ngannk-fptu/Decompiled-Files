/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.Callback
 */
package org.eclipse.jetty.client.util;

import java.io.EOFException;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.AbstractRequestContent;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;

public class ByteBufferRequestContent
extends AbstractRequestContent {
    private final ByteBuffer[] buffers;
    private final long length;

    public ByteBufferRequestContent(ByteBuffer ... buffers) {
        this("application/octet-stream", buffers);
    }

    public ByteBufferRequestContent(String contentType, ByteBuffer ... buffers) {
        super(contentType);
        this.buffers = buffers;
        this.length = Arrays.stream(buffers).mapToLong(Buffer::remaining).sum();
    }

    @Override
    public long getLength() {
        return this.length;
    }

    @Override
    public boolean isReproducible() {
        return true;
    }

    @Override
    protected Request.Content.Subscription newSubscription(Request.Content.Consumer consumer, boolean emitInitialContent) {
        return new SubscriptionImpl(consumer, emitInitialContent);
    }

    private class SubscriptionImpl
    extends AbstractRequestContent.AbstractSubscription {
        private int index;

        private SubscriptionImpl(Request.Content.Consumer consumer, boolean emitInitialContent) {
            super(ByteBufferRequestContent.this, consumer, emitInitialContent);
        }

        @Override
        protected boolean produceContent(AbstractRequestContent.Producer producer) throws IOException {
            boolean lastContent;
            if (this.index < 0) {
                throw new EOFException("Demand after last content");
            }
            ByteBuffer buffer = BufferUtil.EMPTY_BUFFER;
            if (this.index < ByteBufferRequestContent.this.buffers.length) {
                buffer = ByteBufferRequestContent.this.buffers[this.index++];
            }
            boolean bl = lastContent = this.index == ByteBufferRequestContent.this.buffers.length;
            if (lastContent) {
                this.index = -1;
            }
            return producer.produce(buffer.slice(), lastContent, Callback.NOOP);
        }
    }
}

