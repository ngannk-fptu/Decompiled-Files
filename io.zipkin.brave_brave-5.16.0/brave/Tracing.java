/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zipkin2.Endpoint
 *  zipkin2.Span
 *  zipkin2.reporter.Reporter
 *  zipkin2.reporter.brave.ZipkinSpanHandler
 */
package brave;

import brave.Clock;
import brave.ErrorParser;
import brave.Tag;
import brave.Tracer;
import brave.handler.FinishedSpanHandler;
import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.internal.Nullable;
import brave.internal.Platform;
import brave.internal.codec.IpLiteral;
import brave.internal.handler.NoopAwareSpanHandler;
import brave.internal.handler.OrphanTracker;
import brave.internal.recorder.PendingSpans;
import brave.propagation.B3Propagation;
import brave.propagation.CurrentTraceContext;
import brave.propagation.Propagation;
import brave.propagation.TraceContext;
import brave.sampler.Sampler;
import java.io.Closeable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import zipkin2.Endpoint;
import zipkin2.Span;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.brave.ZipkinSpanHandler;

public abstract class Tracing
implements Closeable {
    static final AtomicReference<Tracing> CURRENT = new AtomicReference();

    public static Builder newBuilder() {
        return new Builder();
    }

    public abstract Tracer tracer();

    public abstract Propagation<String> propagation();

    @Deprecated
    public abstract Propagation.Factory propagationFactory();

    public abstract Sampler sampler();

    public abstract CurrentTraceContext currentTraceContext();

    public final Clock clock(TraceContext context) {
        return this.tracer().pendingSpans.getOrCreate(null, context, false).clock();
    }

    @Deprecated
    public abstract ErrorParser errorParser();

    @Nullable
    public static Tracing current() {
        return CURRENT.get();
    }

    @Nullable
    public static Tracer currentTracer() {
        Tracing tracing = Tracing.current();
        return tracing != null ? tracing.tracer() : null;
    }

    public abstract boolean isNoop();

    public abstract void setNoop(boolean var1);

    @Override
    public abstract void close();

    Tracing() {
    }

    static final class Default
    extends Tracing {
        final Tracer tracer;
        final Propagation.Factory propagationFactory;
        final Propagation<String> stringPropagation;
        final CurrentTraceContext currentTraceContext;
        final Sampler sampler;
        final Clock clock;
        final ErrorParser errorParser;
        final AtomicBoolean noop;

        Default(Builder builder) {
            this.clock = builder.clock != null ? builder.clock : Platform.get().clock();
            this.errorParser = builder.errorParser;
            this.propagationFactory = builder.propagationFactory;
            this.stringPropagation = builder.propagationFactory.get();
            this.currentTraceContext = builder.currentTraceContext;
            this.sampler = builder.sampler;
            this.noop = new AtomicBoolean();
            MutableSpan defaultSpan = new MutableSpan(builder.defaultSpan);
            if (defaultSpan.localIp() == null) {
                defaultSpan.localIp(Platform.get().linkLocalIp());
            }
            LinkedHashSet<SpanHandler> spanHandlers = new LinkedHashSet<SpanHandler>(builder.spanHandlers);
            if (builder.zipkinSpanReporter != null) {
                spanHandlers.add(ZipkinSpanHandler.newBuilder((Reporter)((Reporter)builder.zipkinSpanReporter)).errorTag((Tag)this.errorParser).alwaysReportSpans(builder.alwaysReportSpans).build());
            }
            if (spanHandlers.isEmpty()) {
                spanHandlers.add(new LogSpanHandler());
            }
            if (builder.trackOrphans) {
                spanHandlers.add(OrphanTracker.newBuilder().defaultSpan(defaultSpan).clock(this.clock).build());
            }
            SpanHandler spanHandler = NoopAwareSpanHandler.create(spanHandlers.toArray(new SpanHandler[0]), this.noop);
            boolean alwaysSampleLocal = builder.alwaysSampleLocal;
            for (SpanHandler handler : spanHandlers) {
                if (!(handler instanceof FinishedSpanHandler) || !((FinishedSpanHandler)handler).alwaysSampleLocal()) continue;
                alwaysSampleLocal = true;
            }
            this.tracer = new Tracer(builder.clock, builder.propagationFactory, spanHandler, new PendingSpans(defaultSpan, this.clock, spanHandler, this.noop), builder.sampler, builder.currentTraceContext, builder.traceId128Bit || this.propagationFactory.requires128BitTraceId(), builder.supportsJoin && this.propagationFactory.supportsJoin(), alwaysSampleLocal, this.noop);
            CURRENT.compareAndSet(null, this);
        }

        @Override
        public Tracer tracer() {
            return this.tracer;
        }

        @Override
        public Propagation<String> propagation() {
            return this.stringPropagation;
        }

        @Override
        public Propagation.Factory propagationFactory() {
            return this.propagationFactory;
        }

        @Override
        public Sampler sampler() {
            return this.sampler;
        }

        @Override
        public CurrentTraceContext currentTraceContext() {
            return this.currentTraceContext;
        }

        @Override
        @Deprecated
        public ErrorParser errorParser() {
            return this.errorParser;
        }

        @Override
        public boolean isNoop() {
            return this.noop.get();
        }

        @Override
        public void setNoop(boolean noop) {
            this.noop.set(noop);
        }

        public String toString() {
            return this.tracer.toString();
        }

        @Override
        public void close() {
            CURRENT.compareAndSet(this, null);
        }
    }

    static final class LogSpanHandler
    extends SpanHandler {
        final Logger logger = Logger.getLogger(Tracer.class.getName());

        LogSpanHandler() {
        }

        @Override
        public boolean end(TraceContext context, MutableSpan span, SpanHandler.Cause cause) {
            if (!this.logger.isLoggable(Level.INFO)) {
                return false;
            }
            this.logger.info(span.toString());
            return true;
        }

        public String toString() {
            return "LogSpanHandler{name=" + this.logger.getName() + "}";
        }
    }

    public static final class Builder {
        final MutableSpan defaultSpan = new MutableSpan();
        Object zipkinSpanReporter;
        Clock clock;
        Sampler sampler = Sampler.ALWAYS_SAMPLE;
        CurrentTraceContext currentTraceContext = CurrentTraceContext.Default.inheritable();
        boolean traceId128Bit = false;
        boolean supportsJoin = true;
        boolean alwaysSampleLocal = false;
        boolean alwaysReportSpans = false;
        boolean trackOrphans = false;
        Propagation.Factory propagationFactory = B3Propagation.FACTORY;
        ErrorParser errorParser = new ErrorParser();
        Set<SpanHandler> spanHandlers = new LinkedHashSet<SpanHandler>();

        Builder() {
            this.defaultSpan.localServiceName("unknown");
        }

        public Set<SpanHandler> spanHandlers() {
            return Collections.unmodifiableSet(new LinkedHashSet<SpanHandler>(this.spanHandlers));
        }

        public Builder clearSpanHandlers() {
            this.spanHandlers.clear();
            return this;
        }

        public Builder localServiceName(String localServiceName) {
            if (localServiceName == null || localServiceName.isEmpty()) {
                throw new IllegalArgumentException(localServiceName + " is not a valid serviceName");
            }
            this.defaultSpan.localServiceName(localServiceName);
            return this;
        }

        public Builder localIp(String localIp) {
            String maybeIp = IpLiteral.ipOrNull(localIp);
            if (maybeIp == null) {
                throw new IllegalArgumentException(localIp + " is not a valid IP");
            }
            this.defaultSpan.localIp(maybeIp);
            return this;
        }

        public Builder localPort(int localPort) {
            if (localPort > 65535) {
                throw new IllegalArgumentException("invalid localPort " + localPort);
            }
            if (localPort < 0) {
                localPort = 0;
            }
            this.defaultSpan.localPort(localPort);
            return this;
        }

        @Deprecated
        public Builder endpoint(Endpoint endpoint) {
            if (endpoint == null) {
                throw new NullPointerException("endpoint == null");
            }
            this.defaultSpan.localServiceName(endpoint.serviceName());
            this.defaultSpan.localIp(endpoint.ipv6() != null ? endpoint.ipv6() : endpoint.ipv4());
            this.defaultSpan.localPort(endpoint.portAsInt());
            return this;
        }

        @Deprecated
        public Builder spanReporter(Reporter<Span> spanReporter) {
            if (spanReporter == Reporter.NOOP) {
                return this;
            }
            if (spanReporter == null) {
                throw new NullPointerException("spanReporter == null");
            }
            this.zipkinSpanReporter = spanReporter;
            return this;
        }

        public Builder clock(Clock clock) {
            if (clock == null) {
                throw new NullPointerException("clock == null");
            }
            this.clock = clock;
            return this;
        }

        public Builder sampler(Sampler sampler) {
            if (sampler == null) {
                throw new NullPointerException("sampler == null");
            }
            this.sampler = sampler;
            return this;
        }

        public Builder currentTraceContext(CurrentTraceContext currentTraceContext) {
            if (currentTraceContext == null) {
                throw new NullPointerException("currentTraceContext == null");
            }
            this.currentTraceContext = currentTraceContext;
            return this;
        }

        public Builder propagationFactory(Propagation.Factory propagationFactory) {
            if (propagationFactory == null) {
                throw new NullPointerException("propagationFactory == null");
            }
            this.propagationFactory = propagationFactory;
            return this;
        }

        public Builder traceId128Bit(boolean traceId128Bit) {
            this.traceId128Bit = traceId128Bit;
            return this;
        }

        public Builder supportsJoin(boolean supportsJoin) {
            this.supportsJoin = supportsJoin;
            return this;
        }

        @Deprecated
        public Builder errorParser(ErrorParser errorParser) {
            this.errorParser = errorParser;
            return this;
        }

        public Builder addFinishedSpanHandler(FinishedSpanHandler handler) {
            if (handler == FinishedSpanHandler.NOOP) {
                return this;
            }
            return this.addSpanHandler(handler);
        }

        public Builder addSpanHandler(SpanHandler spanHandler) {
            if (spanHandler == null) {
                throw new NullPointerException("spanHandler == null");
            }
            if (spanHandler == SpanHandler.NOOP) {
                return this;
            }
            if (!this.spanHandlers.add(spanHandler)) {
                Platform.get().log("Please check configuration as %s was added twice", spanHandler, null);
            }
            return this;
        }

        public Builder alwaysSampleLocal() {
            this.alwaysSampleLocal = true;
            return this;
        }

        public Builder alwaysReportSpans() {
            this.alwaysReportSpans = true;
            return this;
        }

        public Builder trackOrphans() {
            this.trackOrphans = true;
            return this;
        }

        public Tracing build() {
            return new Default(this);
        }
    }
}

