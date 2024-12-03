/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.codec.support;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.support.BaseCodecConfigurer;
import org.springframework.http.codec.support.BaseDefaultCodecs;
import org.springframework.http.codec.support.ClientDefaultCodecsImpl;

public class DefaultClientCodecConfigurer
extends BaseCodecConfigurer
implements ClientCodecConfigurer {
    public DefaultClientCodecConfigurer() {
        super(new ClientDefaultCodecsImpl());
        ((ClientDefaultCodecsImpl)this.defaultCodecs()).setPartWritersSupplier(this::getPartWriters);
    }

    private DefaultClientCodecConfigurer(DefaultClientCodecConfigurer other) {
        super(other);
        ((ClientDefaultCodecsImpl)this.defaultCodecs()).setPartWritersSupplier(this::getPartWriters);
    }

    @Override
    public ClientCodecConfigurer.ClientDefaultCodecs defaultCodecs() {
        return (ClientCodecConfigurer.ClientDefaultCodecs)super.defaultCodecs();
    }

    @Override
    public DefaultClientCodecConfigurer clone() {
        return new DefaultClientCodecConfigurer(this);
    }

    @Override
    protected BaseDefaultCodecs cloneDefaultCodecs() {
        return new ClientDefaultCodecsImpl((ClientDefaultCodecsImpl)this.defaultCodecs());
    }

    private List<HttpMessageWriter<?>> getPartWriters() {
        ArrayList result = new ArrayList();
        result.addAll(this.customCodecs.getTypedWriters().keySet());
        result.addAll(this.defaultCodecs.getBaseTypedWriters());
        result.addAll(this.customCodecs.getObjectWriters().keySet());
        result.addAll(this.defaultCodecs.getBaseObjectWriters());
        result.addAll(this.defaultCodecs.getCatchAllWriters());
        return result;
    }
}

