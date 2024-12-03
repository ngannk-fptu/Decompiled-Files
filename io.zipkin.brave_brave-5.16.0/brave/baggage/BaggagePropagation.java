/*
 * Decompiled with CFR 0.152.
 */
package brave.baggage;

import brave.baggage.BaggageField;
import brave.baggage.BaggagePropagationConfig;
import brave.internal.Nullable;
import brave.internal.baggage.BaggageCodec;
import brave.internal.baggage.BaggageFields;
import brave.internal.baggage.ExtraBaggageContext;
import brave.internal.collect.Lists;
import brave.internal.propagation.StringPropagationAdapter;
import brave.propagation.Propagation;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class BaggagePropagation<K>
implements Propagation<K> {
    final Propagation<K> delegate;

    public static FactoryBuilder newFactoryBuilder(Propagation.Factory delegate) {
        return new FactoryBuilder(delegate);
    }

    BaggagePropagation(Propagation<K> delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<K> keys() {
        return this.delegate.keys();
    }

    public static List<String> allKeyNames(Propagation<String> propagation) {
        if (propagation == null) {
            throw new NullPointerException("propagation == null");
        }
        TraceContextOrSamplingFlags emptyExtraction = propagation.extractor(NoopGetter.INSTANCE).extract(Boolean.TRUE);
        List<String> baggageKeyNames = BaggagePropagation.getAllKeyNames(emptyExtraction);
        if (baggageKeyNames.isEmpty()) {
            return propagation.keys();
        }
        ArrayList<String> result = new ArrayList<String>(propagation.keys().size() + baggageKeyNames.size());
        result.addAll(propagation.keys());
        result.addAll(baggageKeyNames);
        return Collections.unmodifiableList(result);
    }

    static List<String> getAllKeyNames(TraceContextOrSamplingFlags extracted) {
        List<Object> extraList = extracted.context() != null ? extracted.context().extra() : extracted.extra();
        Extra extra = ExtraBaggageContext.findExtra(Extra.class, extraList);
        if (extra == null) {
            return Collections.emptyList();
        }
        return extra.extractKeyNames;
    }

    @Override
    public <R> TraceContext.Injector<R> injector(Propagation.Setter<R, K> setter) {
        return this.delegate.injector(setter);
    }

    @Override
    public <R> TraceContext.Extractor<R> extractor(Propagation.Getter<R, K> getter) {
        return this.delegate.extractor(getter);
    }

    static final class BaggageExtractor<R>
    implements TraceContext.Extractor<R> {
        final Factory factory;
        final TraceContext.Extractor<R> delegate;
        final Propagation.Getter<R, String> getter;

        BaggageExtractor(Factory factory, Propagation.Getter<R, String> getter) {
            this.delegate = factory.delegate.extractor(getter);
            this.factory = factory;
            this.getter = getter;
        }

        @Override
        public TraceContextOrSamplingFlags extract(R request) {
            TraceContextOrSamplingFlags.Builder builder = this.delegate.extract(request).toBuilder();
            BaggageFields extra = this.factory.baggageFactory.create();
            builder.addExtra(extra);
            if (this.factory.extra == null) {
                return builder.build();
            }
            for (BaggagePropagationConfig config : this.factory.configs) {
                String value;
                if (config.baggageCodec == BaggageCodec.NOOP) continue;
                List<String> keys = config.baggageCodec.injectKeyNames();
                int length = keys.size();
                for (int i = 0; !(i >= length || (value = this.getter.get(request, keys.get(i))) != null && config.baggageCodec.decode(extra, request, value)); ++i) {
                }
            }
            return builder.addExtra(this.factory.extra).build();
        }
    }

    static final class BaggageInjector<R>
    implements TraceContext.Injector<R> {
        final TraceContext.Injector<R> delegate;
        final Factory factory;
        final Propagation.Setter<R, String> setter;

        BaggageInjector(Factory factory, Propagation.Setter<R, String> setter) {
            this.delegate = factory.delegate.injector(setter);
            this.factory = factory;
            this.setter = setter;
        }

        @Override
        public void inject(TraceContext context, R request) {
            this.delegate.inject(context, request);
            BaggageFields extra = context.findExtra(BaggageFields.class);
            if (extra == null) {
                return;
            }
            Map<String, String> values = extra.toMapFilteringFieldNames(this.factory.localFieldNames);
            if (values.isEmpty()) {
                return;
            }
            for (BaggagePropagationConfig config : this.factory.configs) {
                String value;
                if (config.baggageCodec == BaggageCodec.NOOP || (value = config.baggageCodec.encode(values, context, request)) == null) continue;
                List<String> keys = config.baggageCodec.injectKeyNames();
                int length = keys.size();
                for (int i = 0; i < length; ++i) {
                    this.setter.put(request, keys.get(i), value);
                }
            }
        }
    }

    static enum NoopGetter implements Propagation.Getter<Boolean, String>
    {
        INSTANCE;


        @Override
        public String get(Boolean request, String key) {
            return null;
        }
    }

    static final class Factory
    extends Propagation.Factory
    implements Propagation<String> {
        final Propagation.Factory delegateFactory;
        final Propagation<String> delegate;
        final BaggageFields.Factory baggageFactory;
        final BaggagePropagationConfig[] configs;
        final String[] localFieldNames;
        @Nullable
        final Extra extra;

        Factory(FactoryBuilder factoryBuilder) {
            this.delegateFactory = factoryBuilder.delegate;
            this.delegate = this.delegateFactory.get();
            List<String> extractKeyNames = Lists.ensureImmutable(factoryBuilder.extractKeyNames);
            this.extra = !extractKeyNames.isEmpty() ? new Extra(extractKeyNames) : null;
            this.configs = factoryBuilder.configs.toArray(new BaggagePropagationConfig[0]);
            ArrayList<BaggageField> fields = new ArrayList<BaggageField>();
            LinkedHashSet<String> localFieldNames = new LinkedHashSet<String>();
            int maxDynamicFields = 0;
            for (BaggagePropagationConfig config : factoryBuilder.configs) {
                maxDynamicFields += config.maxDynamicFields;
                if (!(config instanceof BaggagePropagationConfig.SingleBaggageField)) continue;
                BaggageField field = ((BaggagePropagationConfig.SingleBaggageField)config).field;
                fields.add(field);
                if (config.baggageCodec != BaggageCodec.NOOP) continue;
                localFieldNames.add(field.name());
            }
            this.baggageFactory = BaggageFields.newFactory(fields, maxDynamicFields);
            this.localFieldNames = localFieldNames.toArray(new String[0]);
        }

        @Deprecated
        public <K1> BaggagePropagation<K1> create(Propagation.KeyFactory<K1> keyFactory) {
            return new BaggagePropagation<K1>(StringPropagationAdapter.create(this.get(), keyFactory));
        }

        public BaggagePropagation<String> get() {
            return new BaggagePropagation<String>(this);
        }

        @Override
        public TraceContext decorate(TraceContext context) {
            TraceContext result = this.delegateFactory.decorate(context);
            return this.baggageFactory.decorate(result);
        }

        @Override
        public boolean supportsJoin() {
            return this.delegateFactory.supportsJoin();
        }

        @Override
        public boolean requires128BitTraceId() {
            return this.delegateFactory.requires128BitTraceId();
        }

        @Override
        public List<String> keys() {
            return this.delegate.keys();
        }

        @Override
        public <R> TraceContext.Injector<R> injector(Propagation.Setter<R, String> setter) {
            return new BaggageInjector<R>(this, setter);
        }

        @Override
        public <R> TraceContext.Extractor<R> extractor(Propagation.Getter<R, String> getter) {
            return new BaggageExtractor<R>(this, getter);
        }
    }

    static final class Extra {
        final List<String> extractKeyNames;

        Extra(List<String> extractKeyNames) {
            this.extractKeyNames = extractKeyNames;
        }
    }

    public static class FactoryBuilder {
        final Propagation.Factory delegate;
        final List<String> extractKeyNames = new ArrayList<String>();
        final Set<BaggagePropagationConfig> configs = new LinkedHashSet<BaggagePropagationConfig>();

        FactoryBuilder(Propagation.Factory delegate) {
            if (delegate == null) {
                throw new NullPointerException("delegate == null");
            }
            this.delegate = delegate;
        }

        public Set<BaggagePropagationConfig> configs() {
            return Collections.unmodifiableSet(new LinkedHashSet<BaggagePropagationConfig>(this.configs));
        }

        public FactoryBuilder clear() {
            this.extractKeyNames.clear();
            this.configs.clear();
            return this;
        }

        public FactoryBuilder add(BaggagePropagationConfig config) {
            if (config == null) {
                throw new NullPointerException("config == null");
            }
            if (this.configs.contains(config)) {
                throw new IllegalArgumentException(config + " already added");
            }
            for (String extractKeyName : config.baggageCodec.extractKeyNames()) {
                if (this.extractKeyNames.contains(extractKeyName)) {
                    throw new IllegalArgumentException("Propagation key already in use: " + extractKeyName);
                }
                this.extractKeyNames.add(extractKeyName);
            }
            this.configs.add(config);
            return this;
        }

        public Propagation.Factory build() {
            if (this.configs.isEmpty()) {
                return this.delegate;
            }
            return new Factory(this);
        }
    }
}

