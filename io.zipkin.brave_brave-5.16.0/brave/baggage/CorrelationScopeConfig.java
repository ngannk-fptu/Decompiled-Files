/*
 * Decompiled with CFR 0.152.
 */
package brave.baggage;

import brave.baggage.BaggageField;
import brave.internal.baggage.BaggageContext;

public class CorrelationScopeConfig {
    CorrelationScopeConfig() {
    }

    public static class SingleCorrelationField
    extends CorrelationScopeConfig {
        final BaggageField baggageField;
        final String name;
        final boolean dirty;
        final boolean flushOnUpdate;
        final boolean readOnly;

        public static SingleCorrelationField create(BaggageField baggageField) {
            return new Builder(baggageField).build();
        }

        public static Builder newBuilder(BaggageField baggageField) {
            return new Builder(baggageField);
        }

        public Builder toBuilder() {
            return new Builder(this);
        }

        SingleCorrelationField(Builder builder) {
            this.baggageField = builder.baggageField;
            this.name = builder.name;
            this.dirty = builder.dirty;
            this.flushOnUpdate = builder.flushOnUpdate;
            this.readOnly = this.baggageField.context instanceof BaggageContext.ReadOnly;
        }

        public BaggageField baggageField() {
            return this.baggageField;
        }

        public String name() {
            return this.name;
        }

        public boolean dirty() {
            return this.dirty;
        }

        public boolean flushOnUpdate() {
            return this.flushOnUpdate;
        }

        public boolean readOnly() {
            return this.readOnly;
        }

        public String toString() {
            String baggageName = this.baggageField.name;
            if (baggageName.equals(this.name)) {
                return "SingleCorrelationField{" + this.name + "}";
            }
            return "SingleCorrelationField{" + baggageName + "->" + this.name + "}";
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof SingleCorrelationField)) {
                return false;
            }
            return this.baggageField.equals(((SingleCorrelationField)o).baggageField);
        }

        public int hashCode() {
            return this.baggageField.hashCode();
        }

        public static final class Builder {
            final BaggageField baggageField;
            String name;
            boolean dirty;
            boolean flushOnUpdate;

            Builder(BaggageField baggageField) {
                this.baggageField = baggageField;
                this.name = baggageField.name();
            }

            Builder(SingleCorrelationField input) {
                this.baggageField = input.baggageField;
                this.name = input.name;
                this.dirty = input.dirty;
                this.flushOnUpdate = input.flushOnUpdate;
            }

            public Builder name(String name) {
                this.name = BaggageField.validateName(name);
                return this;
            }

            public Builder dirty() {
                this.dirty = true;
                return this;
            }

            public Builder flushOnUpdate() {
                this.flushOnUpdate = true;
                return this;
            }

            public SingleCorrelationField build() {
                return new SingleCorrelationField(this);
            }
        }
    }
}

