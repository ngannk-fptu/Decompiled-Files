/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.ReferenceCounted
 *  io.netty.util.internal.ReferenceCountUpdater
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ReferenceCountUpdater;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class AbstractReferenceCountedByteBuf
extends AbstractByteBuf {
    private static final long REFCNT_FIELD_OFFSET = ReferenceCountUpdater.getUnsafeOffset(AbstractReferenceCountedByteBuf.class, (String)"refCnt");
    private static final AtomicIntegerFieldUpdater<AbstractReferenceCountedByteBuf> AIF_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AbstractReferenceCountedByteBuf.class, "refCnt");
    private static final ReferenceCountUpdater<AbstractReferenceCountedByteBuf> updater = new ReferenceCountUpdater<AbstractReferenceCountedByteBuf>(){

        protected AtomicIntegerFieldUpdater<AbstractReferenceCountedByteBuf> updater() {
            return AIF_UPDATER;
        }

        protected long unsafeOffset() {
            return REFCNT_FIELD_OFFSET;
        }
    };
    private volatile int refCnt;

    protected AbstractReferenceCountedByteBuf(int maxCapacity) {
        super(maxCapacity);
        updater.setInitialValue((ReferenceCounted)this);
    }

    @Override
    boolean isAccessible() {
        return updater.isLiveNonVolatile((ReferenceCounted)this);
    }

    public int refCnt() {
        return updater.refCnt((ReferenceCounted)this);
    }

    protected final void setRefCnt(int refCnt) {
        updater.setRefCnt((ReferenceCounted)this, refCnt);
    }

    protected final void resetRefCnt() {
        updater.resetRefCnt((ReferenceCounted)this);
    }

    @Override
    public ByteBuf retain() {
        return (ByteBuf)updater.retain((ReferenceCounted)this);
    }

    @Override
    public ByteBuf retain(int increment) {
        return (ByteBuf)updater.retain((ReferenceCounted)this, increment);
    }

    @Override
    public ByteBuf touch() {
        return this;
    }

    @Override
    public ByteBuf touch(Object hint) {
        return this;
    }

    public boolean release() {
        return this.handleRelease(updater.release((ReferenceCounted)this));
    }

    public boolean release(int decrement) {
        return this.handleRelease(updater.release((ReferenceCounted)this, decrement));
    }

    private boolean handleRelease(boolean result) {
        if (result) {
            this.deallocate();
        }
        return result;
    }

    protected abstract void deallocate();
}

