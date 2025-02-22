/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.channel.local;

import io.netty.channel.Channel;
import io.netty.util.internal.ObjectUtil;
import java.net.SocketAddress;
import java.util.UUID;

public final class LocalAddress
extends SocketAddress
implements Comparable<LocalAddress> {
    private static final long serialVersionUID = 4644331421130916435L;
    public static final LocalAddress ANY = new LocalAddress("ANY");
    private final String id;
    private final String strVal;

    LocalAddress(Channel channel) {
        StringBuilder buf = new StringBuilder(16);
        buf.append("local:E");
        buf.append(Long.toHexString((long)channel.hashCode() & 0xFFFFFFFFL | 0x100000000L));
        buf.setCharAt(7, ':');
        this.id = buf.substring(6);
        this.strVal = buf.toString();
    }

    public LocalAddress(String id) {
        this.id = ObjectUtil.checkNonEmptyAfterTrim((String)id, (String)"id").toLowerCase();
        this.strVal = "local:" + this.id;
    }

    public LocalAddress(Class<?> cls) {
        this(cls.getSimpleName() + '/' + UUID.randomUUID());
    }

    public String id() {
        return this.id;
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof LocalAddress)) {
            return false;
        }
        return this.id.equals(((LocalAddress)o).id);
    }

    @Override
    public int compareTo(LocalAddress o) {
        return this.id.compareTo(o.id);
    }

    public String toString() {
        return this.strVal;
    }
}

