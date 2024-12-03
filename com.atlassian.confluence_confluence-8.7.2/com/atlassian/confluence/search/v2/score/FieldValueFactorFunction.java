/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.score;

import com.atlassian.confluence.search.v2.score.ComposableScoreFunction;
import com.atlassian.confluence.search.v2.score.FieldValueSource;

public final class FieldValueFactorFunction
implements ComposableScoreFunction {
    private final FieldValueSource source;
    private final double factor;
    private final Modifier modifier;

    public FieldValueFactorFunction(FieldValueSource source) {
        this(source, 1.0, Modifier.NONE);
    }

    public FieldValueFactorFunction(FieldValueSource source, double factor, Modifier modifier) {
        this.source = source;
        this.factor = factor;
        this.modifier = modifier;
    }

    public double getFactor() {
        return this.factor;
    }

    public Modifier getModifier() {
        return this.modifier;
    }

    public FieldValueSource getSource() {
        return this.source;
    }

    public static enum Modifier {
        NONE{

            @Override
            public double apply(double n) {
                return n;
            }
        }
        ,
        LOG{

            @Override
            public double apply(double n) {
                return Math.log10(n);
            }
        }
        ,
        LOG1P{

            @Override
            public double apply(double n) {
                return Math.log10(n + 1.0);
            }
        }
        ,
        LOG2P{

            @Override
            public double apply(double n) {
                return Math.log10(n + 2.0);
            }
        }
        ,
        LN{

            @Override
            public double apply(double n) {
                return Math.log(n);
            }
        }
        ,
        LN1P{

            @Override
            public double apply(double n) {
                return Math.log1p(n);
            }
        }
        ,
        LN2P{

            @Override
            public double apply(double n) {
                return Math.log1p(n + 1.0);
            }
        }
        ,
        SQUARE{

            @Override
            public double apply(double n) {
                return Math.pow(n, 2.0);
            }
        }
        ,
        SQRT{

            @Override
            public double apply(double n) {
                return Math.sqrt(n);
            }
        }
        ,
        RECIPROCAL{

            @Override
            public double apply(double n) {
                return 1.0 / n;
            }
        };


        public abstract double apply(double var1);
    }
}

