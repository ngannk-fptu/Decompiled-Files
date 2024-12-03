/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.codec.support;

import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.support.BaseCodecConfigurer;
import org.springframework.http.codec.support.BaseDefaultCodecs;
import org.springframework.http.codec.support.ServerDefaultCodecsImpl;

public class DefaultServerCodecConfigurer
extends BaseCodecConfigurer
implements ServerCodecConfigurer {
    public DefaultServerCodecConfigurer() {
        super(new ServerDefaultCodecsImpl());
    }

    private DefaultServerCodecConfigurer(BaseCodecConfigurer other) {
        super(other);
    }

    @Override
    public ServerCodecConfigurer.ServerDefaultCodecs defaultCodecs() {
        return (ServerCodecConfigurer.ServerDefaultCodecs)super.defaultCodecs();
    }

    @Override
    public DefaultServerCodecConfigurer clone() {
        return new DefaultServerCodecConfigurer(this);
    }

    @Override
    protected BaseDefaultCodecs cloneDefaultCodecs() {
        return new ServerDefaultCodecsImpl((ServerDefaultCodecsImpl)this.defaultCodecs());
    }
}

