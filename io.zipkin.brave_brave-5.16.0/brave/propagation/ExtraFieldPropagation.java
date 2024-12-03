/*
 * Decompiled with CFR 0.152.
 */
package brave.propagation;

import brave.baggage.BaggageField;
import brave.baggage.BaggagePropagation;
import brave.baggage.BaggagePropagationConfig;
import brave.internal.Nullable;
import brave.propagation.Propagation;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Deprecated
public class ExtraFieldPropagation<K>
implements Propagation<K> {
    final Propagation<K> delegate;
    final List<K> extraKeys;

    @Deprecated
    public static Factory newFactory(Propagation.Factory delegate, String ... names) {
        if (delegate == null) {
            throw new NullPointerException("delegate == null");
        }
        if (names == null) {
            throw new NullPointerException("names == null");
        }
        return ExtraFieldPropagation.newFactory(delegate, Arrays.asList(names));
    }

    @Deprecated
    public static Factory newFactory(Propagation.Factory delegate, Collection<String> names) {
        if (delegate == null) {
            throw new NullPointerException("delegate == null");
        }
        if (names == null) {
            throw new NullPointerException("field names == null");
        }
        if (names.isEmpty()) {
            throw new IllegalArgumentException("no field names");
        }
        FactoryBuilder builder = new FactoryBuilder(delegate);
        for (String name : names) {
            builder.addField(name);
        }
        return builder.build();
    }

    @Deprecated
    public static FactoryBuilder newFactoryBuilder(Propagation.Factory delegate) {
        return new FactoryBuilder(delegate);
    }

    @Deprecated
    @Nullable
    public static String current(String name) {
        return ExtraFieldPropagation.get(name);
    }

    @Deprecated
    @Nullable
    public static String get(String name) {
        BaggageField field = BaggageField.getByName(ExtraFieldPropagation.validateFieldName(name));
        if (field == null) {
            return null;
        }
        return field.getValue();
    }

    @Deprecated
    public static void set(String name, String value) {
        BaggageField field = BaggageField.getByName(ExtraFieldPropagation.validateFieldName(name));
        if (field == null) {
            return;
        }
        field.updateValue(value);
    }

    @Deprecated
    public static Map<String, String> getAll() {
        return BaggageField.getAllValues();
    }

    @Deprecated
    public static Map<String, String> getAll(TraceContextOrSamplingFlags extracted) {
        if (extracted.context() != null) {
            return ExtraFieldPropagation.getAll(extracted.context());
        }
        return BaggageField.getAllValues(extracted);
    }

    @Deprecated
    public static Map<String, String> getAll(TraceContext context) {
        return BaggageField.getAllValues(context);
    }

    @Deprecated
    @Nullable
    public static String get(TraceContext context, String name) {
        BaggageField field = BaggageField.getByName(context, ExtraFieldPropagation.validateFieldName(name));
        if (field == null) {
            return null;
        }
        return field.getValue(context);
    }

    @Deprecated
    public static void set(TraceContext context, String name, String value) {
        BaggageField field = BaggageField.getByName(context, ExtraFieldPropagation.validateFieldName(name));
        if (field == null) {
            return;
        }
        field.updateValue(context, value);
    }

    ExtraFieldPropagation(Propagation<K> delegate, List<K> extraKeys) {
        this.delegate = delegate;
        this.extraKeys = extraKeys;
    }

    @Deprecated
    public List<K> extraKeys() {
        return this.extraKeys;
    }

    @Override
    public List<K> keys() {
        return this.delegate.keys();
    }

    @Override
    public <R> TraceContext.Injector<R> injector(Propagation.Setter<R, K> setter) {
        return this.delegate.injector(setter);
    }

    @Override
    public <R> TraceContext.Extractor<R> extractor(Propagation.Getter<R, K> getter) {
        return this.delegate.extractor(getter);
    }

    static String validateFieldName(String fieldName) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName == null");
        }
        if ((fieldName = fieldName.toLowerCase(Locale.ROOT).trim()).isEmpty()) {
            throw new IllegalArgumentException("fieldName is empty");
        }
        return fieldName;
    }

    public static class Factory
    extends Propagation.Factory {
        final Propagation.Factory delegate;
        final String[] extraKeyNames;

        Factory(Propagation.Factory delegate, String[] extraKeyNames) {
            this.delegate = delegate;
            this.extraKeyNames = extraKeyNames;
        }

        public ExtraFieldPropagation<String> get() {
            return this.create((Propagation.KeyFactory)Propagation.KeyFactory.STRING);
        }

        @Deprecated
        public <K> ExtraFieldPropagation<K> create(Propagation.KeyFactory<K> keyFactory) {
            ArrayList<K> extraKeys = new ArrayList<K>();
            for (String extraKeyName : this.extraKeyNames) {
                extraKeys.add(keyFactory.create(extraKeyName));
            }
            return new ExtraFieldPropagation<K>(this.delegate.create(keyFactory), Collections.unmodifiableList(extraKeys));
        }

        @Override
        public boolean supportsJoin() {
            return this.delegate.supportsJoin();
        }

        @Override
        public boolean requires128BitTraceId() {
            return this.delegate.requires128BitTraceId();
        }

        @Override
        public TraceContext decorate(TraceContext context) {
            return this.delegate.decorate(context);
        }
    }

    @Deprecated
    public static final class FactoryBuilder {
        final Propagation.Factory delegate;
        final BaggagePropagation.FactoryBuilder baggageFactory;
        final Set<String> redactedNames = new LinkedHashSet<String>();
        final Map<String, Set<String>> nameToKeyNames = new LinkedHashMap<String, Set<String>>();

        FactoryBuilder(Propagation.Factory delegate) {
            this.delegate = delegate;
            this.baggageFactory = BaggagePropagation.newFactoryBuilder(delegate);
        }

        @Deprecated
        public FactoryBuilder addRedactedField(String fieldName) {
            fieldName = ExtraFieldPropagation.validateFieldName(fieldName);
            this.redactedNames.add(fieldName);
            this.nameToKeyNames.put(fieldName, Collections.emptySet());
            return this;
        }

        @Deprecated
        public FactoryBuilder addField(String fieldName) {
            fieldName = ExtraFieldPropagation.validateFieldName(fieldName);
            this.addKeyName(fieldName, fieldName);
            return this;
        }

        @Deprecated
        public FactoryBuilder addPrefixedFields(String prefix, Collection<String> names) {
            if (prefix == null) {
                throw new NullPointerException("prefix == null");
            }
            prefix = ExtraFieldPropagation.validateFieldName(prefix);
            if (names == null) {
                throw new NullPointerException("names == null");
            }
            for (String name : names) {
                name = ExtraFieldPropagation.validateFieldName(name);
                this.addKeyName(name, prefix + name);
            }
            return this;
        }

        void addKeyName(String name, String keyName) {
            Set<String> keyNames = this.nameToKeyNames.get(name);
            if (keyNames == null) {
                keyNames = new LinkedHashSet<String>();
                this.nameToKeyNames.put(name, keyNames);
            }
            keyNames.add(keyName);
        }

        public Factory build() {
            LinkedHashSet extraKeyNames = new LinkedHashSet();
            for (Map.Entry<String, Set<String>> entry : this.nameToKeyNames.entrySet()) {
                BaggageField field = BaggageField.create(entry.getKey());
                if (this.redactedNames.contains(field.name())) {
                    this.baggageFactory.add(BaggagePropagationConfig.SingleBaggageField.local(field));
                    continue;
                }
                extraKeyNames.addAll(entry.getValue());
                BaggagePropagationConfig.SingleBaggageField.Builder builder = BaggagePropagationConfig.SingleBaggageField.newBuilder(field);
                for (String keyName : entry.getValue()) {
                    builder.addKeyName(keyName);
                }
                this.baggageFactory.add(builder.build());
            }
            return new Factory(this.baggageFactory.build(), extraKeyNames.toArray(new String[0]));
        }
    }
}

