/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelPipeline
 *  io.netty.util.Attribute
 *  io.netty.util.AttributeKey
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal.utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.util.NoSuchElementException;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class ChannelUtils {
    private ChannelUtils() {
    }

    @SafeVarargs
    public static void removeIfExists(ChannelPipeline pipeline, Class<? extends ChannelHandler> ... handlers) {
        for (Class<? extends ChannelHandler> handler : handlers) {
            if (pipeline.get(handler) == null) continue;
            try {
                pipeline.remove(handler);
            }
            catch (NoSuchElementException noSuchElementException) {
                // empty catch block
            }
        }
    }

    public static <T> Optional<T> getAttribute(Channel channel, AttributeKey<T> key) {
        return Optional.ofNullable(channel.attr(key)).map(Attribute::get);
    }
}

