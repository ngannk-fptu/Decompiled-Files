/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.ReferenceCountUtil
 *  io.netty.util.ReferenceCounted
 *  io.netty.util.internal.ObjectUtil
 *  io.netty.util.internal.StringUtil
 */
package io.netty.channel;

import io.netty.channel.AddressedEnvelope;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.SocketAddress;

public class DefaultAddressedEnvelope<M, A extends SocketAddress>
implements AddressedEnvelope<M, A> {
    private final M message;
    private final A sender;
    private final A recipient;

    public DefaultAddressedEnvelope(M message, A recipient, A sender) {
        ObjectUtil.checkNotNull(message, (String)"message");
        if (recipient == null && sender == null) {
            throw new NullPointerException("recipient and sender");
        }
        this.message = message;
        this.sender = sender;
        this.recipient = recipient;
    }

    public DefaultAddressedEnvelope(M message, A recipient) {
        this(message, recipient, null);
    }

    @Override
    public M content() {
        return this.message;
    }

    @Override
    public A sender() {
        return this.sender;
    }

    @Override
    public A recipient() {
        return this.recipient;
    }

    public int refCnt() {
        if (this.message instanceof ReferenceCounted) {
            return ((ReferenceCounted)this.message).refCnt();
        }
        return 1;
    }

    @Override
    public AddressedEnvelope<M, A> retain() {
        ReferenceCountUtil.retain(this.message);
        return this;
    }

    @Override
    public AddressedEnvelope<M, A> retain(int increment) {
        ReferenceCountUtil.retain(this.message, (int)increment);
        return this;
    }

    public boolean release() {
        return ReferenceCountUtil.release(this.message);
    }

    public boolean release(int decrement) {
        return ReferenceCountUtil.release(this.message, (int)decrement);
    }

    @Override
    public AddressedEnvelope<M, A> touch() {
        ReferenceCountUtil.touch(this.message);
        return this;
    }

    @Override
    public AddressedEnvelope<M, A> touch(Object hint) {
        ReferenceCountUtil.touch(this.message, (Object)hint);
        return this;
    }

    public String toString() {
        if (this.sender != null) {
            return StringUtil.simpleClassName((Object)this) + '(' + this.sender + " => " + this.recipient + ", " + this.message + ')';
        }
        return StringUtil.simpleClassName((Object)this) + "(=> " + this.recipient + ", " + this.message + ')';
    }
}

