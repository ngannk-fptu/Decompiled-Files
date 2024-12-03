/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.aggregator;

import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.aggregator.AggregatorException;
import net.sf.ehcache.search.aggregator.AggregatorInstance;

public class Sum
implements AggregatorInstance<Long> {
    private final Attribute<?> attribute;
    private Engine engine;

    public Sum(Attribute<?> attribute) {
        this.attribute = attribute;
    }

    public Sum createClone() {
        return new Sum(this.attribute);
    }

    @Override
    public void accept(Object input) throws AggregatorException {
        if (input == null) {
            return;
        }
        if (input instanceof Number) {
            if (this.engine == null) {
                this.engine = Engine.create((Number)input);
            } else {
                this.engine.accept((Number)input);
            }
        } else {
            throw new AggregatorException("Non-number type encountered: " + input.getClass());
        }
    }

    @Override
    public Number aggregateResult() {
        if (this.engine == null) {
            return null;
        }
        return this.engine.result();
    }

    @Override
    public Attribute getAttribute() {
        return this.attribute;
    }

    static abstract class Engine {
        Engine() {
        }

        static Engine create(Number value) {
            if (value instanceof Float) {
                return new FloatEngine(value.floatValue());
            }
            if (value instanceof Double) {
                return new DoubleEngine(value.doubleValue());
            }
            return new LongEngine(value.longValue());
        }

        abstract void accept(Number var1) throws AggregatorException;

        abstract Number result();

        static class DoubleEngine
        extends Engine {
            private double sum;

            DoubleEngine(double value) {
                this.sum = value;
            }

            @Override
            void accept(Number input) throws AggregatorException {
                this.sum += input.doubleValue();
            }

            @Override
            Number result() {
                return this.sum;
            }
        }

        static class FloatEngine
        extends Engine {
            private float sum;

            FloatEngine(float value) {
                this.sum = value;
            }

            @Override
            void accept(Number input) throws AggregatorException {
                this.sum += input.floatValue();
            }

            @Override
            Number result() {
                return Float.valueOf(this.sum);
            }
        }

        static class LongEngine
        extends Engine {
            private long sum;

            LongEngine(long value) {
                this.sum = value;
            }

            @Override
            void accept(Number input) throws AggregatorException {
                this.sum += input.longValue();
            }

            @Override
            Number result() {
                return this.sum;
            }
        }
    }
}

