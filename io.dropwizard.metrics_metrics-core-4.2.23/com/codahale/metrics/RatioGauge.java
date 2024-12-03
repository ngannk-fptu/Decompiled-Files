/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Gauge;

public abstract class RatioGauge
implements Gauge<Double> {
    protected abstract Ratio getRatio();

    @Override
    public Double getValue() {
        return this.getRatio().getValue();
    }

    public static class Ratio {
        private final double numerator;
        private final double denominator;

        public static Ratio of(double numerator, double denominator) {
            return new Ratio(numerator, denominator);
        }

        private Ratio(double numerator, double denominator) {
            this.numerator = numerator;
            this.denominator = denominator;
        }

        public double getValue() {
            double d = this.denominator;
            if (Double.isNaN(d) || Double.isInfinite(d) || d == 0.0) {
                return Double.NaN;
            }
            return this.numerator / d;
        }

        public String toString() {
            return this.numerator + ":" + this.denominator;
        }
    }
}

