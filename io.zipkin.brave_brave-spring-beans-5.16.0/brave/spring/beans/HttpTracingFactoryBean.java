/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Tracing
 *  brave.http.HttpClientParser
 *  brave.http.HttpRequest
 *  brave.http.HttpRequestParser
 *  brave.http.HttpResponseParser
 *  brave.http.HttpServerParser
 *  brave.http.HttpTracing
 *  brave.http.HttpTracing$Builder
 *  brave.http.HttpTracingCustomizer
 *  brave.propagation.Propagation
 *  brave.sampler.SamplerFunction
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.FactoryBean
 */
package brave.spring.beans;

import brave.Tracing;
import brave.http.HttpClientParser;
import brave.http.HttpRequest;
import brave.http.HttpRequestParser;
import brave.http.HttpResponseParser;
import brave.http.HttpServerParser;
import brave.http.HttpTracing;
import brave.http.HttpTracingCustomizer;
import brave.propagation.Propagation;
import brave.sampler.SamplerFunction;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;

public class HttpTracingFactoryBean
implements FactoryBean {
    static final Log logger = LogFactory.getLog(HttpTracingFactoryBean.class);
    Tracing tracing;
    @Deprecated
    HttpClientParser clientParser;
    @Deprecated
    HttpServerParser serverParser;
    HttpRequestParser clientRequestParser;
    HttpRequestParser serverRequestParser;
    HttpResponseParser clientResponseParser;
    HttpResponseParser serverResponseParser;
    SamplerFunction<HttpRequest> clientSampler;
    SamplerFunction<HttpRequest> serverSampler;
    Propagation<String> propagation;
    List<HttpTracingCustomizer> customizers;

    public HttpTracing getObject() {
        HttpTracing.Builder builder = HttpTracing.newBuilder((Tracing)this.tracing);
        if (this.clientParser != null) {
            builder.clientParser(this.clientParser);
        }
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
        if (this.serverParser != null) {
            builder.serverParser(this.serverParser);
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
            for (HttpTracingCustomizer customizer : this.customizers) {
                customizer.customize(builder);
            }
        }
        return builder.build();
    }

    public Class<? extends HttpTracing> getObjectType() {
        return HttpTracing.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setTracing(Tracing tracing) {
        this.tracing = tracing;
    }

    @Deprecated
    public void setClientParser(HttpClientParser clientParser) {
        logger.warn((Object)"The property 'setClientParser' will be removed in a future release.\nUse the property 'clientRequestParser' or 'clientResponseParser' instead");
        this.clientParser = clientParser;
    }

    public void setClientRequestParser(HttpRequestParser clientRequestParser) {
        this.clientRequestParser = clientRequestParser;
    }

    public void setClientResponseParser(HttpResponseParser clientResponseParser) {
        this.clientResponseParser = clientResponseParser;
    }

    public void setServerRequestParser(HttpRequestParser serverRequestParser) {
        this.serverRequestParser = serverRequestParser;
    }

    public void setServerResponseParser(HttpResponseParser serverResponseParser) {
        this.serverResponseParser = serverResponseParser;
    }

    @Deprecated
    public void setServerParser(HttpServerParser serverParser) {
        logger.warn((Object)"The property 'setServerParser' will be removed in a future release.\nUse the property 'serverRequestParser' or 'serverResponseParser' instead");
        this.serverParser = serverParser;
    }

    public void setClientSampler(SamplerFunction<HttpRequest> clientSampler) {
        this.clientSampler = clientSampler;
    }

    public void setServerSampler(SamplerFunction<HttpRequest> serverSampler) {
        this.serverSampler = serverSampler;
    }

    public void setPropagation(Propagation<String> propagation) {
        this.propagation = propagation;
    }

    public void setCustomizers(List<HttpTracingCustomizer> customizers) {
        this.customizers = customizers;
    }
}

