/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandlerContext
 *  org.jboss.marshalling.MarshallerFactory
 *  org.jboss.marshalling.MarshallingConfiguration
 *  org.jboss.marshalling.Unmarshaller
 */
package io.netty.handler.codec.marshalling;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.Unmarshaller;

public class DefaultUnmarshallerProvider
implements UnmarshallerProvider {
    private final MarshallerFactory factory;
    private final MarshallingConfiguration config;

    public DefaultUnmarshallerProvider(MarshallerFactory factory, MarshallingConfiguration config) {
        this.factory = factory;
        this.config = config;
    }

    @Override
    public Unmarshaller getUnmarshaller(ChannelHandlerContext ctx) throws Exception {
        return this.factory.createUnmarshaller(this.config);
    }
}

