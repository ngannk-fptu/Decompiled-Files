/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.baggage;

import brave.baggage.BaggageField;
import brave.internal.Nullable;
import brave.internal.collect.UnsafeArrayMap;
import brave.internal.extra.MapExtra;
import brave.internal.extra.MapExtraFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class BaggageFields
extends MapExtra<BaggageField, String, BaggageFields, Factory>
implements BaggageField.ValueUpdater {
    static final UnsafeArrayMap.Mapper<Object, String> FIELD_TO_NAME = new UnsafeArrayMap.Mapper<Object, String>(){

        @Override
        public String map(Object input) {
            return ((BaggageField)input).name();
        }
    };
    static final UnsafeArrayMap.Builder<String, String> MAP_STRING_STRING_BUILDER = UnsafeArrayMap.newBuilder().mapKeys(FIELD_TO_NAME);

    public static Factory newFactory(List<BaggageField> fields, int maxDynamicEntries) {
        if (fields == null) {
            throw new NullPointerException("fields == null");
        }
        FactoryBuilder builder = new FactoryBuilder();
        for (BaggageField field : fields) {
            builder.addInitialKey(field);
        }
        return ((FactoryBuilder)builder.maxDynamicEntries(maxDynamicEntries)).build();
    }

    BaggageFields(Factory factory) {
        super(factory);
    }

    Object[] state() {
        return (Object[])this.state;
    }

    @Override
    public boolean updateValue(BaggageField field, String value) {
        return this.put(field, value);
    }

    @Nullable
    public String getValue(BaggageField key) {
        return (String)super.get(key);
    }

    public List<BaggageField> getAllFields() {
        return Collections.unmodifiableList(new ArrayList(this.keySet()));
    }

    public Map<String, String> toMapFilteringFieldNames(String ... filtered) {
        return UnsafeArrayMap.newBuilder().mapKeys(FIELD_TO_NAME).filterKeys((String[])filtered).build(this.state());
    }

    public Map<String, String> getAllValues() {
        return MAP_STRING_STRING_BUILDER.build(this.state());
    }

    public static final class Factory
    extends MapExtraFactory<BaggageField, String, BaggageFields, Factory> {
        Factory(FactoryBuilder builder) {
            super(builder);
        }

        @Override
        public BaggageFields create() {
            return new BaggageFields(this);
        }
    }

    static final class FactoryBuilder
    extends MapExtraFactory.Builder<BaggageField, String, BaggageFields, Factory, FactoryBuilder> {
        FactoryBuilder() {
        }

        @Override
        protected Factory build() {
            return new Factory(this);
        }
    }
}

