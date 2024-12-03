/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.codec.support;

import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.support.BaseCodecConfigurer;
import org.springframework.http.codec.support.ClientDefaultCodecsImpl;

public class DefaultClientCodecConfigurer
extends BaseCodecConfigurer
implements ClientCodecConfigurer {
    public DefaultClientCodecConfigurer() {
        super(new ClientDefaultCodecsImpl());
        ((ClientDefaultCodecsImpl)this.defaultCodecs()).setPartWritersSupplier(() -> this.getWritersInternal(true));
    }

    @Override
    public ClientCodecConfigurer.ClientDefaultCodecs defaultCodecs() {
        return (ClientCodecConfigurer.ClientDefaultCodecs)super.defaultCodecs();
    }
}

