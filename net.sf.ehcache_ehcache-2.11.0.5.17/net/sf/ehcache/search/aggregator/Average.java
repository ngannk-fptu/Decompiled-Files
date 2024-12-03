/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.aggregator;

import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.aggregator.AggregatorException;
import net.sf.ehcache.search.aggregator.AggregatorInstance;

public class Average
implements AggregatorInstance<Double> {
    private final Attribute<?> attribute;
    private Engine engine;

    public Average(Attribute<?> attribute) {
        this.attribute = attribute;
    }

    public Average createClone() {
        return new Average(this.attribute);
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
            if (value instanceof Long) {
                return new LongEngine(value.longValue());
            }
            return new IntegerEngine(value.intValue());
        }

        abstract void accept(Number var1) throws AggregatorException;

        abstract Number result();

        static class DoubleEngine
        extends Engine {
            private int count = 1;
            private double sum;

            DoubleEngine(double value) {
                this.sum = value;
            }

            @Override
            void accept(Number input) throws AggregatorException {
                ++this.count;
                this.sum += input.doubleValue();
            }

            @Override
            Number result() {
                return this.sum / (double)this.count;
            }
        }

        static class FloatEngine
        extends Engine {
            private int count = 1;
            private float sum;

            FloatEngine(float value) {
                this.sum = value;
            }

            @Override
            void accept(Number input) throws AggregatorException {
                ++this.count;
                this.sum += input.floatValue();
            }

            @Override
            Number result() {
                return Float.valueOf(this.sum / (float)this.count);
            }
        }

        static class LongEngine
        extends Engine {
            private int count = 1;
            private long sum;

            LongEngine(long value) {
                this.sum = value;
            }

            @Override
            void accept(Number input) throws AggregatorException {
                ++this.count;
                this.sum += input.longValue();
            }

            @Override
            Number result() {
                return (double)this.sum / (double)this.count;
            }
        }

        static class IntegerEngine
        extends Engine {
            private int count = 1;
            private long sum;

            IntegerEngine(int value) {
                this.sum = value;
            }

            @Override
            void accept(Number input) throws AggregatorException {
                ++this.count;
                this.sum += (long)input.intValue();
            }

            @Override
            Number result() {
                return Float.valueOf((float)this.sum / (float)this.count);
            }
        }
    }
}

