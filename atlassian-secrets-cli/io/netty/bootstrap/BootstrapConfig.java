/*
 * Decompiled with CFR 0.152.
 */
package io.netty.bootstrap;

import io.netty.bootstrap.AbstractBootstrapConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.resolver.AddressResolverGroup;
import java.net.SocketAddress;

public final class BootstrapConfig
extends AbstractBootstrapConfig<Bootstrap, Channel> {
    BootstrapConfig(Bootstrap bootstrap) {
        super(bootstrap);
    }

    public SocketAddress remoteAddress() {
        return ((Bootstrap)this.bootstrap).remoteAddress();
    }

    public AddressResolverGroup<?> resolver() {
        return ((Bootstrap)this.bootstrap).resolver();
    }

    @Override
    public String toString() {
        SocketAddress remoteAddress;
        StringBuilder buf = new StringBuilder(super.toString());
        buf.setLength(buf.length() - 1);
        AddressResolverGroup<?> resolver = this.resolver();
        if (resolver != null) {
            buf.append(", resolver: ").append(resolver);
        }
        if ((remoteAddress = this.remoteAddress()) != null) {
            buf.append(", remoteAddress: ").append(remoteAddress);
        }
        return buf.append(')').toString();
    }
}

