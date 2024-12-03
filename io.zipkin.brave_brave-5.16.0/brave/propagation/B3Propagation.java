/*
 * Decompiled with CFR 0.152.
 */
package brave.propagation;

import brave.Span;
import brave.internal.Platform;
import brave.internal.propagation.InjectorFactory;
import brave.internal.propagation.StringPropagationAdapter;
import brave.propagation.B3SingleFormat;
import brave.propagation.Propagation;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class B3Propagation<K>
implements Propagation<K> {
    public static final Propagation.Factory FACTORY = new Factory(B3Propagation.newFactoryBuilder());
    static final Propagation<String> INSTANCE = FACTORY.get();
    static final String B3 = "b3";
    static final String TRACE_ID = "X-B3-TraceId";
    static final String SPAN_ID = "X-B3-SpanId";
    static final String PARENT_SPAN_ID = "X-B3-ParentSpanId";
    static final String SAMPLED = "X-B3-Sampled";
    static final String SAMPLED_MALFORMED = "Invalid input: expected 0 or 1 for X-B3-Sampled, but found '{0}'";
    static final String FLAGS = "X-B3-Flags";

    public static Propagation<String> get() {
        return INSTANCE;
    }

    public static FactoryBuilder newFactoryBuilder() {
        return new FactoryBuilder();
    }

    B3Propagation() {
    }

    static final class B3Extractor<R>
    implements TraceContext.Extractor<R> {
        final Factory factory;
        final Propagation.Getter<R, String> getter;

        B3Extractor(Factory factory, Propagation.Getter<R, String> getter) {
            this.factory = factory;
            this.getter = getter;
        }

        /*
         * Enabled aggressive block sorting
         */
        @Override
        public TraceContextOrSamplingFlags extract(R request) {
            TraceContext.Builder result;
            Boolean sampledV;
            TraceContextOrSamplingFlags extracted;
            if (request == null) {
                throw new NullPointerException("request == null");
            }
            String b3 = this.getter.get(request, B3Propagation.B3);
            TraceContextOrSamplingFlags traceContextOrSamplingFlags = extracted = b3 != null ? B3SingleFormat.parseB3SingleFormat(b3) : null;
            if (extracted != null) {
                return extracted;
            }
            String sampled = this.getter.get(request, B3Propagation.SAMPLED);
            if (sampled == null) {
                sampledV = null;
            } else if (sampled.length() == 1) {
                char sampledC = sampled.charAt(0);
                if (sampledC == '1') {
                    sampledV = true;
                } else {
                    if (sampledC != '0') {
                        Platform.get().log(B3Propagation.SAMPLED_MALFORMED, sampled, null);
                        return TraceContextOrSamplingFlags.EMPTY;
                    }
                    sampledV = false;
                }
            } else if (sampled.equalsIgnoreCase("true")) {
                sampledV = true;
            } else {
                if (!sampled.equalsIgnoreCase("false")) {
                    Platform.get().log(B3Propagation.SAMPLED_MALFORMED, sampled, null);
                    return TraceContextOrSamplingFlags.EMPTY;
                }
                sampledV = false;
            }
            boolean debug = "1".equals(this.getter.get(request, B3Propagation.FLAGS));
            String traceIdString = this.getter.get(request, B3Propagation.TRACE_ID);
            if (traceIdString == null) {
                if (debug) {
                    return TraceContextOrSamplingFlags.DEBUG;
                }
                if (sampledV != null) {
                    TraceContextOrSamplingFlags traceContextOrSamplingFlags2;
                    if (sampledV.booleanValue()) {
                        traceContextOrSamplingFlags2 = TraceContextOrSamplingFlags.SAMPLED;
                        return traceContextOrSamplingFlags2;
                    }
                    traceContextOrSamplingFlags2 = TraceContextOrSamplingFlags.NOT_SAMPLED;
                    return traceContextOrSamplingFlags2;
                }
            }
            if (!(result = TraceContext.newBuilder()).parseTraceId(traceIdString, B3Propagation.TRACE_ID)) return TraceContextOrSamplingFlags.EMPTY;
            if (!result.parseSpanId(this.getter, request, B3Propagation.SPAN_ID)) return TraceContextOrSamplingFlags.EMPTY;
            if (!result.parseParentId(this.getter, request, B3Propagation.PARENT_SPAN_ID)) return TraceContextOrSamplingFlags.EMPTY;
            if (sampledV != null) {
                result.sampled((boolean)sampledV);
            }
            if (!debug) return TraceContextOrSamplingFlags.create(result.build());
            result.debug(true);
            return TraceContextOrSamplingFlags.create(result.build());
        }
    }

    static final class Factory
    extends Propagation.Factory
    implements Propagation<String> {
        final InjectorFactory injectorFactory;

        Factory(FactoryBuilder builder) {
            this.injectorFactory = builder.injectorFactoryBuilder.build();
        }

        @Override
        public List<String> keys() {
            return this.injectorFactory.keyNames();
        }

        @Override
        public Propagation<String> get() {
            return this;
        }

        public <K1> Propagation<K1> create(Propagation.KeyFactory<K1> keyFactory) {
            return StringPropagationAdapter.create(this, keyFactory);
        }

        @Override
        public boolean supportsJoin() {
            return true;
        }

        @Override
        public <R> TraceContext.Injector<R> injector(Propagation.Setter<R, String> setter) {
            return this.injectorFactory.newInjector(setter);
        }

        @Override
        public <R> TraceContext.Extractor<R> extractor(Propagation.Getter<R, String> getter) {
            if (getter == null) {
                throw new NullPointerException("getter == null");
            }
            return new B3Extractor<R>(this, getter);
        }

        public int hashCode() {
            return this.injectorFactory.hashCode();
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Factory)) {
                return false;
            }
            Factory that = (Factory)o;
            return this.injectorFactory.equals(that.injectorFactory);
        }

        public String toString() {
            return "B3Propagation";
        }
    }

    public static final class FactoryBuilder {
        InjectorFactory.Builder injectorFactoryBuilder = InjectorFactory.newBuilder(Format.MULTI).clientInjectorFunctions(Format.MULTI).producerInjectorFunctions(Format.SINGLE_NO_PARENT).consumerInjectorFunctions(Format.SINGLE_NO_PARENT);

        public FactoryBuilder injectFormat(Format format) {
            if (format == null) {
                throw new NullPointerException("format == null");
            }
            this.injectorFactoryBuilder.injectorFunctions(format);
            return this;
        }

        public FactoryBuilder injectFormat(Span.Kind kind, Format format) {
            if (kind == null) {
                throw new NullPointerException("kind == null");
            }
            if (format == null) {
                throw new NullPointerException("format == null");
            }
            switch (kind) {
                case CLIENT: {
                    this.injectorFactoryBuilder.clientInjectorFunctions(format);
                    break;
                }
                case PRODUCER: {
                    this.injectorFactoryBuilder.producerInjectorFunctions(format);
                    break;
                }
                case CONSUMER: {
                    this.injectorFactoryBuilder.consumerInjectorFunctions(format);
                    break;
                }
            }
            return this;
        }

        public FactoryBuilder injectFormats(Span.Kind kind, Format format1, Format format2) {
            if (kind == null) {
                throw new NullPointerException("kind == null");
            }
            if (format1 == null) {
                throw new NullPointerException("format1 == null");
            }
            if (format2 == null) {
                throw new NullPointerException("format2 == null");
            }
            if (format1.equals(format2)) {
                throw new IllegalArgumentException("format1 == format2");
            }
            if (!format1.equals(Format.MULTI) && !format2.equals(Format.MULTI)) {
                throw new IllegalArgumentException("One argument must be Format.MULTI");
            }
            switch (kind) {
                case CLIENT: {
                    this.injectorFactoryBuilder.clientInjectorFunctions(format1, format2);
                    break;
                }
                case PRODUCER: {
                    this.injectorFactoryBuilder.producerInjectorFunctions(format1, format2);
                    break;
                }
                case CONSUMER: {
                    this.injectorFactoryBuilder.consumerInjectorFunctions(format1, format2);
                    break;
                }
            }
            return this;
        }

        public Propagation.Factory build() {
            Factory result = new Factory(this);
            if (result.equals(FACTORY)) {
                return FACTORY;
            }
            return result;
        }

        FactoryBuilder() {
        }
    }

    public static enum Format implements InjectorFactory.InjectorFunction
    {
        MULTI{

            @Override
            public List<String> keyNames() {
                return MULTI_KEY_NAMES;
            }

            @Override
            public <R> void inject(Propagation.Setter<R, String> setter, TraceContext context, R request) {
                setter.put(request, B3Propagation.TRACE_ID, context.traceIdString());
                setter.put(request, B3Propagation.SPAN_ID, context.spanIdString());
                String parentId = context.parentIdString();
                if (parentId != null) {
                    setter.put(request, B3Propagation.PARENT_SPAN_ID, parentId);
                }
                if (context.debug()) {
                    setter.put(request, B3Propagation.FLAGS, "1");
                } else if (context.sampled() != null) {
                    setter.put(request, B3Propagation.SAMPLED, context.sampled() != false ? "1" : "0");
                }
            }
        }
        ,
        SINGLE{

            @Override
            public List<String> keyNames() {
                return SINGLE_KEY_NAMES;
            }

            @Override
            public <R> void inject(Propagation.Setter<R, String> setter, TraceContext context, R request) {
                setter.put(request, B3Propagation.B3, B3SingleFormat.writeB3SingleFormat(context));
            }
        }
        ,
        SINGLE_NO_PARENT{

            @Override
            public List<String> keyNames() {
                return SINGLE_KEY_NAMES;
            }

            @Override
            public <R> void inject(Propagation.Setter<R, String> setter, TraceContext context, R request) {
                setter.put(request, B3Propagation.B3, B3SingleFormat.writeB3SingleFormatWithoutParentId(context));
            }
        };

        static final List<String> SINGLE_KEY_NAMES;
        static final List<String> MULTI_KEY_NAMES;

        static {
            SINGLE_KEY_NAMES = Collections.singletonList(B3Propagation.B3);
            MULTI_KEY_NAMES = Collections.unmodifiableList(Arrays.asList(B3Propagation.TRACE_ID, B3Propagation.SPAN_ID, B3Propagation.PARENT_SPAN_ID, B3Propagation.SAMPLED, B3Propagation.FLAGS));
        }
    }
}

