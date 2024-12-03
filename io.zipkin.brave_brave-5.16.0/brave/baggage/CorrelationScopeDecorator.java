/*
 * Decompiled with CFR 0.152.
 */
package brave.baggage;

import brave.baggage.BaggageFields;
import brave.baggage.CorrelationFlushScope;
import brave.baggage.CorrelationScopeConfig;
import brave.baggage.CorrelationUpdateScope;
import brave.internal.CorrelationContext;
import brave.internal.Nullable;
import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public abstract class CorrelationScopeDecorator
implements CurrentTraceContext.ScopeDecorator {
    final CorrelationContext context;

    CorrelationScopeDecorator(CorrelationContext context) {
        this.context = context;
    }

    static int setBit(int bitset, int i) {
        return bitset | 1 << i;
    }

    static boolean isSet(int bitset, int i) {
        return (bitset & 1 << i) != 0;
    }

    static boolean equal(@Nullable Object a, @Nullable Object b) {
        return a == null ? b == null : a.equals(b);
    }

    static final class Multiple
    extends CorrelationScopeDecorator {
        final CorrelationScopeConfig.SingleCorrelationField[] fields;

        Multiple(CorrelationContext context, CorrelationScopeConfig.SingleCorrelationField[] fields) {
            super(context);
            this.fields = fields;
        }

        @Override
        public CurrentTraceContext.Scope decorateScope(@Nullable TraceContext traceContext, CurrentTraceContext.Scope scope) {
            int dirty = 0;
            boolean flushOnUpdate = false;
            String[] valuesToRevert = new String[this.fields.length];
            for (int i = 0; i < this.fields.length; ++i) {
                CorrelationScopeConfig.SingleCorrelationField field = this.fields[i];
                String valueToRevert = this.context.getValue(field.name);
                String currentValue = field.baggageField.getValue(traceContext);
                if (!(scope == CurrentTraceContext.Scope.NOOP && field.readOnly || Multiple.equal(valueToRevert, currentValue))) {
                    this.context.update(field.name, currentValue);
                    dirty = Multiple.setBit(dirty, i);
                }
                if (field.dirty) {
                    dirty = Multiple.setBit(dirty, i);
                }
                if (field.flushOnUpdate) {
                    flushOnUpdate = true;
                }
                valuesToRevert[i] = valueToRevert;
            }
            if (dirty == 0 && !flushOnUpdate) {
                return scope;
            }
            CorrelationUpdateScope.Multiple updateScope = new CorrelationUpdateScope.Multiple(scope, this.context, this.fields, valuesToRevert, dirty);
            return flushOnUpdate ? new CorrelationFlushScope(updateScope) : updateScope;
        }
    }

    static final class Single
    extends CorrelationScopeDecorator {
        final CorrelationScopeConfig.SingleCorrelationField field;

        Single(CorrelationContext context, CorrelationScopeConfig.SingleCorrelationField field) {
            super(context);
            this.field = field;
        }

        @Override
        public CurrentTraceContext.Scope decorateScope(@Nullable TraceContext traceContext, CurrentTraceContext.Scope scope) {
            String valueToRevert = this.context.getValue(this.field.name);
            String currentValue = this.field.baggageField.getValue(traceContext);
            boolean dirty = false;
            if (scope != CurrentTraceContext.Scope.NOOP || !this.field.readOnly()) {
                boolean bl = dirty = !Single.equal(valueToRevert, currentValue);
                if (dirty) {
                    this.context.update(this.field.name, currentValue);
                }
            }
            boolean bl = dirty = dirty || this.field.dirty;
            if (!dirty && !this.field.flushOnUpdate) {
                return scope;
            }
            CorrelationUpdateScope.Single updateScope = new CorrelationUpdateScope.Single(scope, this.context, this.field, valueToRevert, dirty);
            return this.field.flushOnUpdate ? new CorrelationFlushScope(updateScope) : updateScope;
        }
    }

    public static abstract class Builder {
        final CorrelationContext context;
        final Set<String> allNames = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        final Set<CorrelationScopeConfig.SingleCorrelationField> fields = new LinkedHashSet<CorrelationScopeConfig.SingleCorrelationField>();

        protected Builder(CorrelationContext context) {
            if (context == null) {
                throw new NullPointerException("context == null");
            }
            this.context = context;
            this.add(CorrelationScopeConfig.SingleCorrelationField.create(BaggageFields.TRACE_ID));
            this.add(CorrelationScopeConfig.SingleCorrelationField.create(BaggageFields.SPAN_ID));
        }

        public Set<CorrelationScopeConfig> configs() {
            return Collections.unmodifiableSet(new LinkedHashSet<CorrelationScopeConfig.SingleCorrelationField>(this.fields));
        }

        public Builder clear() {
            this.allNames.clear();
            this.fields.clear();
            return this;
        }

        public Builder add(CorrelationScopeConfig config) {
            if (config == null) {
                throw new NullPointerException("config == null");
            }
            if (!(config instanceof CorrelationScopeConfig.SingleCorrelationField)) {
                throw new UnsupportedOperationException("dynamic fields not yet supported");
            }
            CorrelationScopeConfig.SingleCorrelationField field = (CorrelationScopeConfig.SingleCorrelationField)config;
            if (this.fields.contains(field)) {
                throw new IllegalArgumentException("Baggage Field already added: " + field.baggageField.name);
            }
            if (this.allNames.contains(field.name)) {
                throw new IllegalArgumentException("Correlation name already in use: " + field.name);
            }
            this.fields.add(field);
            return this;
        }

        public final CurrentTraceContext.ScopeDecorator build() {
            int fieldCount = this.fields.size();
            if (fieldCount == 0) {
                return CurrentTraceContext.ScopeDecorator.NOOP;
            }
            if (fieldCount == 1) {
                return new Single(this.context, this.fields.iterator().next());
            }
            if (fieldCount > 32) {
                throw new IllegalArgumentException("over 32 baggage fields");
            }
            return new Multiple(this.context, this.fields.toArray(new CorrelationScopeConfig.SingleCorrelationField[0]));
        }
    }
}

