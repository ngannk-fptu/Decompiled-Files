/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.codec.support;

import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.support.BaseCodecConfigurer;
import org.springframework.http.codec.support.ServerDefaultCodecsImpl;

public class DefaultServerCodecConfigurer
extends BaseCodecConfigurer
implements ServerCodecConfigurer {
    public DefaultServerCodecConfigurer() {
        super(new ServerDefaultCodecsImpl());
    }

    @Override
    public ServerCodecConfigurer.ServerDefaultCodecs defaultCodecs() {
        return (ServerCodecConfigurer.ServerDefaultCodecs)super.defaultCodecs();
    }
}

