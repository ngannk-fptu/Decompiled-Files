/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ObjectUtil
 *  io.netty.util.internal.StringUtil
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class DefaultByteBufHolder
implements ByteBufHolder {
    private final ByteBuf data;

    public DefaultByteBufHolder(ByteBuf data) {
        this.data = (ByteBuf)ObjectUtil.checkNotNull((Object)data, (String)"data");
    }

    @Override
    public ByteBuf content() {
        return ByteBufUtil.ensureAccessible(this.data);
    }

    @Override
    public ByteBufHolder copy() {
        return this.replace(this.data.copy());
    }

    @Override
    public ByteBufHolder duplicate() {
        return this.replace(this.data.duplicate());
    }

    @Override
    public ByteBufHolder retainedDuplicate() {
        return this.replace(this.data.retainedDuplicate());
    }

    @Override
    public ByteBufHolder replace(ByteBuf content) {
        return new DefaultByteBufHolder(content);
    }

    public int refCnt() {
        return this.data.refCnt();
    }

    @Override
    public ByteBufHolder retain() {
        this.data.retain();
        return this;
    }

    @Override
    public ByteBufHolder retain(int increment) {
        this.data.retain(increment);
        return this;
    }

    @Override
    public ByteBufHolder touch() {
        this.data.touch();
        return this;
    }

    @Override
    public ByteBufHolder touch(Object hint) {
        this.data.touch(hint);
        return this;
    }

    public boolean release() {
        return this.data.release();
    }

    public boolean release(int decrement) {
        return this.data.release(decrement);
    }

    protected final String contentToString() {
        return this.data.toString();
    }

    public String toString() {
        return StringUtil.simpleClassName((Object)this) + '(' + this.contentToString() + ')';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && this.getClass() == o.getClass()) {
            return this.data.equals(((DefaultByteBufHolder)o).data);
        }
        return false;
    }

    public int hashCode() {
        return this.data.hashCode();
    }
}

