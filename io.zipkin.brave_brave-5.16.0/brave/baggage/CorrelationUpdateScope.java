/*
 * Decompiled with CFR 0.152.
 */
package brave.baggage;

import brave.baggage.BaggageField;
import brave.baggage.CorrelationScopeConfig;
import brave.baggage.CorrelationScopeDecorator;
import brave.internal.CorrelationContext;
import brave.internal.Nullable;
import brave.propagation.CurrentTraceContext;
import java.util.concurrent.atomic.AtomicBoolean;

abstract class CorrelationUpdateScope
extends AtomicBoolean
implements CurrentTraceContext.Scope {
    CorrelationContext context;

    CorrelationUpdateScope(CorrelationContext context) {
        this.context = context;
    }

    @Nullable
    abstract String name(BaggageField var1);

    abstract void handleUpdate(BaggageField var1, @Nullable String var2);

    static final class Multiple
    extends CorrelationUpdateScope {
        final CurrentTraceContext.Scope delegate;
        final CorrelationScopeConfig.SingleCorrelationField[] fields;
        final String[] valuesToRevert;
        int shouldRevert;

        Multiple(CurrentTraceContext.Scope delegate, CorrelationContext context, CorrelationScopeConfig.SingleCorrelationField[] fields, String[] valuesToRevert, int shouldRevert) {
            super(context);
            this.delegate = delegate;
            this.fields = fields;
            this.valuesToRevert = valuesToRevert;
            this.shouldRevert = shouldRevert;
        }

        @Override
        public void close() {
            if (!this.compareAndSet(false, true)) {
                return;
            }
            this.delegate.close();
            for (int i = 0; i < this.fields.length; ++i) {
                if (!CorrelationScopeDecorator.isSet(this.shouldRevert, i)) continue;
                this.context.update(this.fields[i].name, this.valuesToRevert[i]);
            }
        }

        @Override
        String name(BaggageField field) {
            for (int i = 0; i < this.fields.length; ++i) {
                if (!this.fields[i].baggageField.equals(field)) continue;
                return this.fields[i].name;
            }
            return null;
        }

        @Override
        void handleUpdate(BaggageField field, String value) {
            for (int i = 0; i < this.fields.length; ++i) {
                if (!this.fields[i].baggageField.equals(field)) continue;
                if (!CorrelationScopeDecorator.equal(value, this.valuesToRevert[i])) {
                    this.shouldRevert = CorrelationScopeDecorator.setBit(this.shouldRevert, i);
                }
                return;
            }
        }
    }

    static final class Single
    extends CorrelationUpdateScope {
        final CurrentTraceContext.Scope delegate;
        final CorrelationScopeConfig.SingleCorrelationField field;
        @Nullable
        final String valueToRevert;
        boolean shouldRevert;

        Single(CurrentTraceContext.Scope delegate, CorrelationContext context, CorrelationScopeConfig.SingleCorrelationField field, @Nullable String valueToRevert, boolean shouldRevert) {
            super(context);
            this.delegate = delegate;
            this.field = field;
            this.valueToRevert = valueToRevert;
            this.shouldRevert = shouldRevert;
        }

        @Override
        public void close() {
            if (!this.compareAndSet(false, true)) {
                return;
            }
            this.delegate.close();
            if (this.shouldRevert) {
                this.context.update(this.field.name, this.valueToRevert);
            }
        }

        @Override
        String name(BaggageField field) {
            return field.name;
        }

        @Override
        void handleUpdate(BaggageField field, String value) {
            if (!this.field.baggageField.equals(field)) {
                return;
            }
            if (!CorrelationScopeDecorator.equal(value, this.valueToRevert)) {
                this.shouldRevert = true;
            }
        }
    }
}

