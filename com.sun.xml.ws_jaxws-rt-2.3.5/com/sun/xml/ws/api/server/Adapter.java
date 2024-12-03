/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.server;

import com.sun.xml.ws.api.Component;
import com.sun.xml.ws.api.config.management.Reconfigurable;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.util.Pool;

public abstract class Adapter<TK extends Toolkit>
implements Reconfigurable,
Component {
    protected final WSEndpoint<?> endpoint;
    protected volatile Pool<TK> pool = new Pool<TK>(){

        @Override
        protected TK create() {
            return Adapter.this.createToolkit();
        }
    };

    protected Adapter(WSEndpoint endpoint) {
        assert (endpoint != null);
        this.endpoint = endpoint;
        endpoint.getComponents().add(this.getEndpointComponent());
    }

    protected Component getEndpointComponent() {
        return new Component(){

            @Override
            public <S> S getSPI(Class<S> spiType) {
                if (spiType.isAssignableFrom(Reconfigurable.class)) {
                    return spiType.cast(Adapter.this);
                }
                return null;
            }
        };
    }

    @Override
    public void reconfigure() {
        this.pool = new Pool<TK>(){

            @Override
            protected TK create() {
                return Adapter.this.createToolkit();
            }
        };
    }

    @Override
    public <S> S getSPI(Class<S> spiType) {
        if (spiType.isAssignableFrom(Reconfigurable.class)) {
            return spiType.cast(this);
        }
        if (this.endpoint != null) {
            return this.endpoint.getSPI(spiType);
        }
        return null;
    }

    public WSEndpoint<?> getEndpoint() {
        return this.endpoint;
    }

    protected Pool<TK> getPool() {
        return this.pool;
    }

    protected abstract TK createToolkit();

    public class Toolkit {
        public final Codec codec;
        public final WSEndpoint.PipeHead head;

        public Toolkit() {
            this.codec = Adapter.this.endpoint.createCodec();
            this.head = Adapter.this.endpoint.createPipeHead();
        }
    }
}

