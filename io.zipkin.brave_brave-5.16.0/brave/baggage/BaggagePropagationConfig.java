/*
 * Decompiled with CFR 0.152.
 */
package brave.baggage;

import brave.baggage.BaggageField;
import brave.internal.baggage.BaggageCodec;
import brave.internal.baggage.SingleFieldBaggageCodec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class BaggagePropagationConfig {
    final BaggageCodec baggageCodec;
    final int maxDynamicFields;

    BaggagePropagationConfig(BaggageCodec baggageCodec, int maxDynamicFields) {
        if (baggageCodec == null) {
            throw new NullPointerException("baggageCodec == null");
        }
        this.baggageCodec = baggageCodec;
        this.maxDynamicFields = maxDynamicFields;
    }

    public static class SingleBaggageField
    extends BaggagePropagationConfig {
        final BaggageField field;
        final Set<String> keyNames;

        public static SingleBaggageField local(BaggageField field) {
            return new Builder(field).build();
        }

        public static SingleBaggageField remote(BaggageField field) {
            return new Builder(field).addKeyName(field.lcName).build();
        }

        public static Builder newBuilder(BaggageField field) {
            return new Builder(field);
        }

        public Builder toBuilder() {
            return new Builder(this);
        }

        SingleBaggageField(Builder builder) {
            super(builder.keyNames.isEmpty() ? BaggageCodec.NOOP : SingleFieldBaggageCodec.single(builder.field, builder.keyNames), 0);
            this.field = builder.field;
            this.keyNames = builder.keyNames.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet<String>(builder.keyNames));
        }

        public BaggageField field() {
            return this.field;
        }

        public Set<String> keyNames() {
            return this.keyNames;
        }

        public static final class Builder {
            final BaggageField field;
            List<String> keyNames = new ArrayList<String>();

            Builder(BaggageField field) {
                this.field = field;
            }

            Builder(SingleBaggageField input) {
                this.field = input.field;
                this.keyNames = new ArrayList<String>(input.keyNames());
            }

            public Builder addKeyName(String keyName) {
                if (keyName == null) {
                    throw new NullPointerException("keyName == null");
                }
                String lcName = BaggageField.validateName(keyName).toLowerCase(Locale.ROOT);
                if (!this.keyNames.contains(lcName)) {
                    this.keyNames.add(lcName);
                }
                return this;
            }

            public SingleBaggageField build() {
                return new SingleBaggageField(this);
            }
        }
    }
}

