/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.lookup.ConstraintTypeFormula;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public abstract class ReductionResult {
    protected static final ConstraintTypeFormula TRUE = new ConstraintTypeFormula(){

        @Override
        public Object reduce(InferenceContext18 context) {
            return this;
        }

        @Override
        public String toString() {
            return "TRUE";
        }
    };
    protected static final ConstraintTypeFormula FALSE = new ConstraintTypeFormula(){

        @Override
        public Object reduce(InferenceContext18 context) {
            return this;
        }

        @Override
        public String toString() {
            return "FALSE";
        }
    };
    protected static final int COMPATIBLE = 1;
    protected static final int SUBTYPE = 2;
    protected static final int SUPERTYPE = 3;
    protected static final int SAME = 4;
    protected static final int TYPE_ARGUMENT_CONTAINED = 5;
    protected static final int CAPTURE = 6;
    static final int EXCEPTIONS_CONTAINED = 7;
    protected static final int POTENTIALLY_COMPATIBLE = 8;
    protected TypeBinding right;
    protected int relation;

    protected static String relationToString(int relation) {
        switch (relation) {
            case 4: {
                return " = ";
            }
            case 1: {
                return " \u2192 ";
            }
            case 8: {
                return " \u2192? ";
            }
            case 2: {
                return " <: ";
            }
            case 3: {
                return " :> ";
            }
            case 5: {
                return " <= ";
            }
            case 6: {
                return " captureOf ";
            }
        }
        throw new IllegalArgumentException("Unknown type relation " + relation);
    }
}

