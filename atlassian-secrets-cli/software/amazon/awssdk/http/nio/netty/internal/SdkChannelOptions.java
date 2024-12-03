/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.ChannelOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class SdkChannelOptions {
    private Map<ChannelOption, Object> options = new HashMap<ChannelOption, Object>();

    public SdkChannelOptions() {
        this.options.put(ChannelOption.TCP_NODELAY, Boolean.TRUE);
    }

    public <T> SdkChannelOptions putOption(ChannelOption<T> channelOption, T channelOptionValue) {
        channelOption.validate(channelOptionValue);
        this.options.put(channelOption, channelOptionValue);
        return this;
    }

    public Map<ChannelOption, Object> channelOptions() {
        return Collections.unmodifiableMap(this.options);
    }
}

