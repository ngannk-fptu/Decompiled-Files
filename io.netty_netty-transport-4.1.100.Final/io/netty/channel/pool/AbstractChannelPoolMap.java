/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  io.netty.util.concurrent.GlobalEventExecutor
 *  io.netty.util.concurrent.Promise
 *  io.netty.util.internal.ObjectUtil
 *  io.netty.util.internal.PlatformDependent
 *  io.netty.util.internal.ReadOnlyIterator
 */
package io.netty.channel.pool;

import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReadOnlyIterator;
import java.io.Closeable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractChannelPoolMap<K, P extends ChannelPool>
implements ChannelPoolMap<K, P>,
Iterable<Map.Entry<K, P>>,
Closeable {
    private final ConcurrentMap<K, P> map = PlatformDependent.newConcurrentHashMap();

    @Override
    public final P get(K key) {
        ChannelPool old;
        ChannelPool pool = (ChannelPool)this.map.get(ObjectUtil.checkNotNull(key, (String)"key"));
        if (pool == null && (old = this.map.putIfAbsent(key, pool = this.newPool(key))) != null) {
            AbstractChannelPoolMap.poolCloseAsyncIfSupported(pool);
            pool = old;
        }
        return (P)pool;
    }

    public final boolean remove(K key) {
        ChannelPool pool = (ChannelPool)this.map.remove(ObjectUtil.checkNotNull(key, (String)"key"));
        if (pool != null) {
            AbstractChannelPoolMap.poolCloseAsyncIfSupported(pool);
            return true;
        }
        return false;
    }

    private Future<Boolean> removeAsyncIfSupported(K key) {
        ChannelPool pool = (ChannelPool)this.map.remove(ObjectUtil.checkNotNull(key, (String)"key"));
        if (pool != null) {
            final Promise removePromise = GlobalEventExecutor.INSTANCE.newPromise();
            AbstractChannelPoolMap.poolCloseAsyncIfSupported(pool).addListener((GenericFutureListener)new GenericFutureListener<Future<? super Void>>(){

                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (future.isSuccess()) {
                        removePromise.setSuccess((Object)Boolean.TRUE);
                    } else {
                        removePromise.setFailure(future.cause());
                    }
                }
            });
            return removePromise;
        }
        return GlobalEventExecutor.INSTANCE.newSucceededFuture((Object)Boolean.FALSE);
    }

    private static Future<Void> poolCloseAsyncIfSupported(ChannelPool pool) {
        if (pool instanceof SimpleChannelPool) {
            return ((SimpleChannelPool)pool).closeAsync();
        }
        try {
            pool.close();
            return GlobalEventExecutor.INSTANCE.newSucceededFuture(null);
        }
        catch (Exception e) {
            return GlobalEventExecutor.INSTANCE.newFailedFuture((Throwable)e);
        }
    }

    @Override
    public final Iterator<Map.Entry<K, P>> iterator() {
        return new ReadOnlyIterator(this.map.entrySet().iterator());
    }

    public final int size() {
        return this.map.size();
    }

    public final boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public final boolean contains(K key) {
        return this.map.containsKey(ObjectUtil.checkNotNull(key, (String)"key"));
    }

    protected abstract P newPool(K var1);

    @Override
    public final void close() {
        for (Object key : this.map.keySet()) {
            this.removeAsyncIfSupported(key).syncUninterruptibly();
        }
    }
}

