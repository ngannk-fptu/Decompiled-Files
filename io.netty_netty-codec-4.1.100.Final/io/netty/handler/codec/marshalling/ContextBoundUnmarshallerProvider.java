/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.util.Attribute
 *  io.netty.util.AttributeKey
 *  org.jboss.marshalling.MarshallerFactory
 *  org.jboss.marshalling.MarshallingConfiguration
 *  org.jboss.marshalling.Unmarshaller
 */
package io.netty.handler.codec.marshalling;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.Unmarshaller;

public class ContextBoundUnmarshallerProvider
extends DefaultUnmarshallerProvider {
    private static final AttributeKey<Unmarshaller> UNMARSHALLER = AttributeKey.valueOf(ContextBoundUnmarshallerProvider.class, (String)"UNMARSHALLER");

    public ContextBoundUnmarshallerProvider(MarshallerFactory factory, MarshallingConfiguration config) {
        super(factory, config);
    }

    @Override
    public Unmarshaller getUnmarshaller(ChannelHandlerContext ctx) throws Exception {
        Attribute attr = ctx.channel().attr(UNMARSHALLER);
        Unmarshaller unmarshaller = (Unmarshaller)attr.get();
        if (unmarshaller == null) {
            unmarshaller = super.getUnmarshaller(ctx);
            attr.set((Object)unmarshaller);
        }
        return unmarshaller;
    }
}

