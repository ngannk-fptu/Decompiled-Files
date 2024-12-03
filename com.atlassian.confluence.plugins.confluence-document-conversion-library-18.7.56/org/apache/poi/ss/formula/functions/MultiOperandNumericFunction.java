/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.ThreeDEval;
import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.StringValueEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.DoubleList;
import org.apache.poi.ss.formula.functions.Function;

public abstract class MultiOperandNumericFunction
implements Function {
    private EvalConsumer<BoolEval, DoubleList> boolByRefConsumer;
    private EvalConsumer<BoolEval, DoubleList> boolByValueConsumer;
    private EvalConsumer<BlankEval, DoubleList> blankConsumer;
    private EvalConsumer<MissingArgEval, DoubleList> missingArgConsumer = ConsumerFactory.createForMissingArg(Policy.SKIP);
    private static final int DEFAULT_MAX_NUM_OPERANDS = SpreadsheetVersion.EXCEL2007.getMaxFunctionArgs();

    protected MultiOperandNumericFunction(boolean isReferenceBoolCounted, boolean isBlankCounted) {
        this.boolByRefConsumer = ConsumerFactory.createForBoolEval(isReferenceBoolCounted ? Policy.COERCE : Policy.SKIP);
        this.boolByValueConsumer = ConsumerFactory.createForBoolEval(Policy.COERCE);
        this.blankConsumer = ConsumerFactory.createForBlank(isBlankCounted ? Policy.COERCE : Policy.SKIP);
    }

    public void setMissingArgPolicy(Policy policy) {
        this.missingArgConsumer = ConsumerFactory.createForMissingArg(policy);
    }

    public void setBlankEvalPolicy(Policy policy) {
        this.blankConsumer = ConsumerFactory.createForBlank(policy);
    }

    protected boolean treatStringsAsZero() {
        return false;
    }

    @Override
    public final ValueEval evaluate(ValueEval[] args, int srcCellRow, int srcCellCol) {
        try {
            double[] values = this.getNumberArray(args);
            double d = this.evaluate(values);
            if (Double.isNaN(d) || Double.isInfinite(d)) {
                return ErrorEval.NUM_ERROR;
            }
            return new NumberEval(d);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    protected abstract double evaluate(double[] var1) throws EvaluationException;

    protected int getMaxNumOperands() {
        return DEFAULT_MAX_NUM_OPERANDS;
    }

    protected final double[] getNumberArray(ValueEval[] operands) throws EvaluationException {
        if (operands.length > this.getMaxNumOperands()) {
            throw EvaluationException.invalidValue();
        }
        DoubleList retval = new DoubleList();
        for (ValueEval operand : operands) {
            this.collectValues(operand, retval);
        }
        return retval.toArray();
    }

    public boolean isSubtotalCounted() {
        return true;
    }

    public boolean isHiddenRowCounted() {
        return true;
    }

    private void collectValues(ValueEval operand, DoubleList temp) throws EvaluationException {
        if (operand instanceof ThreeDEval) {
            ThreeDEval ae = (ThreeDEval)operand;
            for (int sIx = ae.getFirstSheetIndex(); sIx <= ae.getLastSheetIndex(); ++sIx) {
                int width = ae.getWidth();
                int height = ae.getHeight();
                for (int rrIx = 0; rrIx < height; ++rrIx) {
                    for (int rcIx = 0; rcIx < width; ++rcIx) {
                        ValueEval ve = ae.getValue(sIx, rrIx, rcIx);
                        if (!this.isSubtotalCounted() && ae.isSubTotal(rrIx, rcIx) || !this.isHiddenRowCounted() && ae.isRowHidden(rrIx)) continue;
                        this.collectValue(ve, !this.treatStringsAsZero(), temp);
                    }
                }
            }
            return;
        }
        if (operand instanceof TwoDEval) {
            TwoDEval ae = (TwoDEval)operand;
            int width = ae.getWidth();
            int height = ae.getHeight();
            for (int rrIx = 0; rrIx < height; ++rrIx) {
                for (int rcIx = 0; rcIx < width; ++rcIx) {
                    ValueEval ve = ae.getValue(rrIx, rcIx);
                    if (!this.isSubtotalCounted() && ae.isSubTotal(rrIx, rcIx)) continue;
                    this.collectValue(ve, !this.treatStringsAsZero(), temp);
                }
            }
            return;
        }
        if (operand instanceof RefEval) {
            RefEval re = (RefEval)operand;
            for (int sIx = re.getFirstSheetIndex(); sIx <= re.getLastSheetIndex(); ++sIx) {
                this.collectValue(re.getInnerValueEval(sIx), !this.treatStringsAsZero(), temp);
            }
            return;
        }
        this.collectValue(operand, false, temp);
    }

    private void collectValue(ValueEval ve, boolean isViaReference, DoubleList temp) throws EvaluationException {
        if (ve == null) {
            throw new IllegalArgumentException("ve must not be null");
        }
        if (ve instanceof BoolEval) {
            BoolEval boolEval = (BoolEval)ve;
            if (isViaReference) {
                this.boolByRefConsumer.accept(boolEval, temp);
            } else {
                this.boolByValueConsumer.accept(boolEval, temp);
            }
            return;
        }
        if (ve instanceof NumericValueEval) {
            NumericValueEval ne = (NumericValueEval)ve;
            temp.add(ne.getNumberValue());
            return;
        }
        if (ve instanceof StringValueEval) {
            if (isViaReference) {
                return;
            }
            if (this.treatStringsAsZero()) {
                temp.add(0.0);
            } else {
                String s = ((StringValueEval)ve).getStringValue().trim();
                Double d = OperandResolver.parseDouble(s);
                if (d == null) {
                    throw new EvaluationException(ErrorEval.VALUE_INVALID);
                }
                temp.add(d);
            }
            return;
        }
        if (ve instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval)ve);
        }
        if (ve == BlankEval.instance) {
            this.blankConsumer.accept((BlankEval)ve, temp);
            return;
        }
        if (ve == MissingArgEval.instance) {
            this.missingArgConsumer.accept((MissingArgEval)ve, temp);
            return;
        }
        throw new RuntimeException("Invalid ValueEval type passed for conversion: (" + ve.getClass() + ")");
    }

    private static class ConsumerFactory {
        private ConsumerFactory() {
        }

        static EvalConsumer<MissingArgEval, DoubleList> createForMissingArg(Policy policy) {
            EvalConsumer<MissingArgEval, DoubleList> coercer = (value, receiver) -> receiver.add(0.0);
            return ConsumerFactory.createAny(coercer, policy);
        }

        static EvalConsumer<BoolEval, DoubleList> createForBoolEval(Policy policy) {
            EvalConsumer<BoolEval, DoubleList> coercer = (value, receiver) -> receiver.add(value.getNumberValue());
            return ConsumerFactory.createAny(coercer, policy);
        }

        static EvalConsumer<BlankEval, DoubleList> createForBlank(Policy policy) {
            EvalConsumer<BlankEval, DoubleList> coercer = (value, receiver) -> receiver.add(0.0);
            return ConsumerFactory.createAny(coercer, policy);
        }

        private static <T> EvalConsumer<T, DoubleList> createAny(EvalConsumer<T, DoubleList> coercer, Policy policy) {
            switch (policy) {
                case COERCE: {
                    return coercer;
                }
                case SKIP: {
                    return ConsumerFactory.doNothing();
                }
                case ERROR: {
                    return ConsumerFactory.throwValueInvalid();
                }
            }
            throw new AssertionError();
        }

        private static <T> EvalConsumer<T, DoubleList> doNothing() {
            return (value, receiver) -> {};
        }

        private static <T> EvalConsumer<T, DoubleList> throwValueInvalid() {
            return (value, receiver) -> {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            };
        }
    }

    private static interface EvalConsumer<T, R> {
        public void accept(T var1, R var2) throws EvaluationException;
    }

    public static enum Policy {
        COERCE,
        SKIP,
        ERROR;

    }
}

