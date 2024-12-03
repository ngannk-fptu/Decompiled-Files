/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Tracing
 *  brave.propagation.Propagation
 *  brave.rpc.RpcRequest
 *  brave.rpc.RpcRequestParser
 *  brave.rpc.RpcResponseParser
 *  brave.rpc.RpcTracing
 *  brave.rpc.RpcTracing$Builder
 *  brave.rpc.RpcTracingCustomizer
 *  brave.sampler.SamplerFunction
 *  org.springframework.beans.factory.FactoryBean
 */
package brave.spring.beans;

import brave.Tracing;
import brave.propagation.Propagation;
import brave.rpc.RpcRequest;
import brave.rpc.RpcRequestParser;
import brave.rpc.RpcResponseParser;
import brave.rpc.RpcTracing;
import brave.rpc.RpcTracingCustomizer;
import brave.sampler.SamplerFunction;
import java.util.List;
import org.springframework.beans.factory.FactoryBean;

public class RpcTracingFactoryBean
implements FactoryBean {
    Tracing tracing;
    SamplerFunction<RpcRequest> clientSampler;
    SamplerFunction<RpcRequest> serverSampler;
    RpcRequestParser clientRequestParser;
    RpcRequestParser serverRequestParser;
    RpcResponseParser clientResponseParser;
    RpcResponseParser serverResponseParser;
    Propagation<String> propagation;
    List<RpcTracingCustomizer> customizers;

    public RpcTracing getObject() {
        RpcTracing.Builder builder = RpcTracing.newBuilder((Tracing)this.tracing);
        if (this.clientRequestParser != null) {
            builder.clientRequestParser(this.clientRequestParser);
        }
        if (this.clientResponseParser != null) {
            builder.clientResponseParser(this.clientResponseParser);
        }
        if (this.serverRequestParser != null) {
            builder.serverRequestParser(this.serverRequestParser);
        }
        if (this.serverResponseParser != null) {
            builder.serverResponseParser(this.serverResponseParser);
        }
        if (this.clientSampler != null) {
            builder.clientSampler(this.clientSampler);
        }
        if (this.serverSampler != null) {
            builder.serverSampler(this.serverSampler);
        }
        if (this.propagation != null) {
            builder.propagation(this.propagation);
        }
        if (this.customizers != null) {
            for (RpcTracingCustomizer customizer : this.customizers) {
                customizer.customize(builder);
            }
        }
        return builder.build();
    }

    public Class<? extends RpcTracing> getObjectType() {
        return RpcTracing.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setTracing(Tracing tracing) {
        this.tracing = tracing;
    }

    public void setClientRequestParser(RpcRequestParser clientRequestParser) {
        this.clientRequestParser = clientRequestParser;
    }

    public void setClientResponseParser(RpcResponseParser clientResponseParser) {
        this.clientResponseParser = clientResponseParser;
    }

    public void setServerRequestParser(RpcRequestParser serverRequestParser) {
        this.serverRequestParser = serverRequestParser;
    }

    public void setServerResponseParser(RpcResponseParser serverResponseParser) {
        this.serverResponseParser = serverResponseParser;
    }

    public void setClientSampler(SamplerFunction<RpcRequest> clientSampler) {
        this.clientSampler = clientSampler;
    }

    public void setServerSampler(SamplerFunction<RpcRequest> serverSampler) {
        this.serverSampler = serverSampler;
    }

    public void setPropagation(Propagation<String> propagation) {
        this.propagation = propagation;
    }

    public void setCustomizers(List<RpcTracingCustomizer> customizers) {
        this.customizers = customizers;
    }
}

