/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.baggage;

import brave.baggage.BaggageField;
import brave.internal.baggage.BaggageCodec;
import brave.propagation.TraceContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class SingleFieldBaggageCodec
implements BaggageCodec {
    final BaggageField field;
    final List<String> keyNamesList;

    public static SingleFieldBaggageCodec single(BaggageField field, Collection<String> keyNames) {
        if (field == null) {
            throw new NullPointerException("field == null");
        }
        return new SingleFieldBaggageCodec(field, keyNames);
    }

    SingleFieldBaggageCodec(BaggageField field, Collection<String> keyNames) {
        this.field = field;
        this.keyNamesList = Collections.unmodifiableList(new ArrayList<String>(keyNames));
    }

    @Override
    public List<String> extractKeyNames() {
        return this.keyNamesList;
    }

    @Override
    public List<String> injectKeyNames() {
        return this.keyNamesList;
    }

    @Override
    public boolean decode(BaggageField.ValueUpdater valueUpdater, Object request, String value) {
        return valueUpdater.updateValue(this.field, value);
    }

    @Override
    public String encode(Map<String, String> values, TraceContext context, Object request) {
        return this.field.getValue(context);
    }
}

