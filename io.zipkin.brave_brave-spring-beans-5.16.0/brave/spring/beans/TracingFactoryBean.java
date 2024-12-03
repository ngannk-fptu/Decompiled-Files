/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Clock
 *  brave.ErrorParser
 *  brave.Tracing
 *  brave.Tracing$Builder
 *  brave.TracingCustomizer
 *  brave.handler.SpanHandler
 *  brave.propagation.CurrentTraceContext
 *  brave.propagation.Propagation$Factory
 *  brave.sampler.Sampler
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.config.AbstractFactoryBean
 *  zipkin2.Endpoint
 *  zipkin2.reporter.Reporter
 */
package brave.spring.beans;

import brave.Clock;
import brave.ErrorParser;
import brave.Tracing;
import brave.TracingCustomizer;
import brave.handler.SpanHandler;
import brave.propagation.CurrentTraceContext;
import brave.propagation.Propagation;
import brave.sampler.Sampler;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import zipkin2.Endpoint;
import zipkin2.reporter.Reporter;

public class TracingFactoryBean
extends AbstractFactoryBean {
    static final Log logger = LogFactory.getLog(TracingFactoryBean.class);
    String localServiceName;
    @Deprecated
    Object localEndpoint;
    @Deprecated
    Object endpoint;
    @Deprecated
    Object spanReporter;
    List<SpanHandler> spanHandlers = new ArrayList<SpanHandler>();
    Clock clock;
    Sampler sampler;
    @Deprecated
    ErrorParser errorParser;
    CurrentTraceContext currentTraceContext;
    Propagation.Factory propagationFactory;
    Boolean traceId128Bit;
    Boolean supportsJoin;
    List<TracingCustomizer> customizers;

    protected Tracing createInstance() {
        Tracing.Builder builder = Tracing.newBuilder();
        if (this.localServiceName != null) {
            builder.localServiceName(this.localServiceName);
        }
        if (this.localEndpoint == null) {
            this.localEndpoint = this.endpoint;
        }
        if (this.localEndpoint != null) {
            builder.endpoint((Endpoint)this.localEndpoint);
        }
        if (this.spanReporter != null) {
            builder.spanReporter((Reporter)this.spanReporter);
        }
        for (SpanHandler spanHandler : this.spanHandlers) {
            builder.addSpanHandler(spanHandler);
        }
        if (this.errorParser != null) {
            builder.errorParser(this.errorParser);
        }
        if (this.clock != null) {
            builder.clock(this.clock);
        }
        if (this.sampler != null) {
            builder.sampler(this.sampler);
        }
        if (this.currentTraceContext != null) {
            builder.currentTraceContext(this.currentTraceContext);
        }
        if (this.propagationFactory != null) {
            builder.propagationFactory(this.propagationFactory);
        }
        if (this.traceId128Bit != null) {
            builder.traceId128Bit(this.traceId128Bit.booleanValue());
        }
        if (this.supportsJoin != null) {
            builder.supportsJoin(this.supportsJoin.booleanValue());
        }
        if (this.customizers != null) {
            for (TracingCustomizer customizer : this.customizers) {
                customizer.customize(builder);
            }
        }
        return builder.build();
    }

    protected void destroyInstance(Object instance) {
        ((Tracing)instance).close();
    }

    public Class<? extends Tracing> getObjectType() {
        return Tracing.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setLocalServiceName(String localServiceName) {
        this.localServiceName = localServiceName;
    }

    @Deprecated
    public void setLocalEndpoint(Object localEndpoint) {
        logger.warn((Object)"The property 'localEndpoint' will be removed in a future release.\nUse the property 'localServiceName' instead");
        this.localEndpoint = localEndpoint;
    }

    @Deprecated
    public void setEndpoint(Object endpoint) {
        logger.warn((Object)"The property 'endpoint' will be removed in a future release.\nUse the property 'localServiceName' instead");
        this.endpoint = endpoint;
    }

    @Deprecated
    public void setSpanReporter(Object spanReporter) {
        logger.warn((Object)"The property 'spanReporter' will be removed in a future release.\nAdd ZipkinSpanHandler the list property 'spanHandlers' instead");
        this.spanReporter = spanReporter;
    }

    @Deprecated
    public void setFinishedSpanHandlers(List<SpanHandler> finishedSpanHandlers) {
        logger.warn((Object)"The list property 'finishedSpanHandlers' will be removed in a future release.\nUse the list property 'spanHandlers' instead");
        this.spanHandlers.addAll(finishedSpanHandlers);
    }

    public void setSpanHandlers(List<SpanHandler> spanHandlers) {
        this.spanHandlers.addAll(spanHandlers);
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    @Deprecated
    public void setErrorParser(ErrorParser errorParser) {
        logger.warn((Object)"The property 'errorParser' will be removed in a future release.\nAdd ZipkinSpanHandler with the 'errorTag' you want into list property 'spanHandlers'");
        this.errorParser = errorParser;
    }

    public void setSampler(Sampler sampler) {
        this.sampler = sampler;
    }

    public void setCurrentTraceContext(CurrentTraceContext currentTraceContext) {
        this.currentTraceContext = currentTraceContext;
    }

    public void setPropagationFactory(Propagation.Factory propagationFactory) {
        this.propagationFactory = propagationFactory;
    }

    public void setTraceId128Bit(boolean traceId128Bit) {
        this.traceId128Bit = traceId128Bit;
    }

    public void setSupportsJoin(Boolean supportsJoin) {
        this.supportsJoin = supportsJoin;
    }

    public void setCustomizers(List<TracingCustomizer> customizers) {
        this.customizers = customizers;
    }
}

