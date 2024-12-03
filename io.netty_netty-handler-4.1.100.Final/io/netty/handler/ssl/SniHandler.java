/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBufAllocator
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.DecoderException
 *  io.netty.util.AsyncMapping
 *  io.netty.util.DomainNameMapping
 *  io.netty.util.Mapping
 *  io.netty.util.ReferenceCountUtil
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.Promise
 *  io.netty.util.internal.ObjectUtil
 *  io.netty.util.internal.PlatformDependent
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.ssl.AbstractSniHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AsyncMapping;
import io.netty.util.DomainNameMapping;
import io.netty.util.Mapping;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;

public class SniHandler
extends AbstractSniHandler<SslContext> {
    private static final Selection EMPTY_SELECTION = new Selection(null, null);
    protected final AsyncMapping<String, SslContext> mapping;
    private volatile Selection selection = EMPTY_SELECTION;

    public SniHandler(Mapping<? super String, ? extends SslContext> mapping) {
        this(new AsyncMappingAdapter(mapping));
    }

    public SniHandler(Mapping<? super String, ? extends SslContext> mapping, int maxClientHelloLength, long handshakeTimeoutMillis) {
        this(new AsyncMappingAdapter(mapping), maxClientHelloLength, handshakeTimeoutMillis);
    }

    public SniHandler(DomainNameMapping<? extends SslContext> mapping) {
        this((Mapping<? super String, ? extends SslContext>)mapping);
    }

    public SniHandler(AsyncMapping<? super String, ? extends SslContext> mapping) {
        this(mapping, 0, 0L);
    }

    public SniHandler(AsyncMapping<? super String, ? extends SslContext> mapping, int maxClientHelloLength, long handshakeTimeoutMillis) {
        super(maxClientHelloLength, handshakeTimeoutMillis);
        this.mapping = (AsyncMapping)ObjectUtil.checkNotNull(mapping, (String)"mapping");
    }

    public SniHandler(Mapping<? super String, ? extends SslContext> mapping, long handshakeTimeoutMillis) {
        this(new AsyncMappingAdapter(mapping), handshakeTimeoutMillis);
    }

    public SniHandler(AsyncMapping<? super String, ? extends SslContext> mapping, long handshakeTimeoutMillis) {
        this(mapping, 0, handshakeTimeoutMillis);
    }

    public String hostname() {
        return this.selection.hostname;
    }

    public SslContext sslContext() {
        return this.selection.context;
    }

    @Override
    protected Future<SslContext> lookup(ChannelHandlerContext ctx, String hostname) throws Exception {
        return this.mapping.map((Object)hostname, ctx.executor().newPromise());
    }

    @Override
    protected final void onLookupComplete(ChannelHandlerContext ctx, String hostname, Future<SslContext> future) throws Exception {
        if (!future.isSuccess()) {
            Throwable cause = future.cause();
            if (cause instanceof Error) {
                throw (Error)cause;
            }
            throw new DecoderException("failed to get the SslContext for " + hostname, cause);
        }
        SslContext sslContext = (SslContext)future.getNow();
        this.selection = new Selection(sslContext, hostname);
        try {
            this.replaceHandler(ctx, hostname, sslContext);
        }
        catch (Throwable cause) {
            this.selection = EMPTY_SELECTION;
            PlatformDependent.throwException((Throwable)cause);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void replaceHandler(ChannelHandlerContext ctx, String hostname, SslContext sslContext) throws Exception {
        SslHandler sslHandler = null;
        try {
            sslHandler = this.newSslHandler(sslContext, ctx.alloc());
            ctx.pipeline().replace((ChannelHandler)this, SslHandler.class.getName(), (ChannelHandler)sslHandler);
            sslHandler = null;
        }
        finally {
            if (sslHandler != null) {
                ReferenceCountUtil.safeRelease((Object)sslHandler.engine());
            }
        }
    }

    protected SslHandler newSslHandler(SslContext context, ByteBufAllocator allocator) {
        SslHandler sslHandler = context.newHandler(allocator);
        sslHandler.setHandshakeTimeoutMillis(this.handshakeTimeoutMillis);
        return sslHandler;
    }

    private static final class Selection {
        final SslContext context;
        final String hostname;

        Selection(SslContext context, String hostname) {
            this.context = context;
            this.hostname = hostname;
        }
    }

    private static final class AsyncMappingAdapter
    implements AsyncMapping<String, SslContext> {
        private final Mapping<? super String, ? extends SslContext> mapping;

        private AsyncMappingAdapter(Mapping<? super String, ? extends SslContext> mapping) {
            this.mapping = (Mapping)ObjectUtil.checkNotNull(mapping, (String)"mapping");
        }

        public Future<SslContext> map(String input, Promise<SslContext> promise) {
            SslContext context;
            try {
                context = (SslContext)this.mapping.map((Object)input);
            }
            catch (Throwable cause) {
                return promise.setFailure(cause);
            }
            return promise.setSuccess((Object)context);
        }
    }
}

