/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.OpenSslApplicationProtocolNegotiator;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

@Deprecated
public final class OpenSslDefaultApplicationProtocolNegotiator
implements OpenSslApplicationProtocolNegotiator {
    private final ApplicationProtocolConfig config;

    public OpenSslDefaultApplicationProtocolNegotiator(ApplicationProtocolConfig config) {
        this.config = (ApplicationProtocolConfig)ObjectUtil.checkNotNull((Object)config, (String)"config");
    }

    @Override
    public List<String> protocols() {
        return this.config.supportedProtocols();
    }

    @Override
    public ApplicationProtocolConfig.Protocol protocol() {
        return this.config.protocol();
    }

    @Override
    public ApplicationProtocolConfig.SelectorFailureBehavior selectorFailureBehavior() {
        return this.config.selectorFailureBehavior();
    }

    @Override
    public ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedListenerFailureBehavior() {
        return this.config.selectedListenerFailureBehavior();
    }
}

