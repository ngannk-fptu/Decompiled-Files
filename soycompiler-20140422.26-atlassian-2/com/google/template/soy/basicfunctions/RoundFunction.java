/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.inject.Inject
 *  com.google.inject.Singleton
 */
package com.google.template.soy.basicfunctions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.FloatData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.exprtree.Operator;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsCodeUtils;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import com.google.template.soy.shared.restricted.SoyPureFunction;
import java.util.List;
import java.util.Set;

@Singleton
@SoyPureFunction
class RoundFunction
implements SoyJavaFunction,
SoyJsSrcFunction {
    @Inject
    RoundFunction() {
    }

    @Override
    public String getName() {
        return "round";
    }

    @Override
    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)1, (Object)2);
    }

    @Override
    public SoyValue computeForJava(List<SoyValue> args) {
        int numDigitsAfterPt;
        SoyValue value = args.get(0);
        int n = numDigitsAfterPt = args.size() == 2 ? args.get(1).integerValue() : 0;
        if (numDigitsAfterPt == 0) {
            if (value instanceof IntegerData) {
                return IntegerData.forValue(value.longValue());
            }
            return IntegerData.forValue((int)Math.round(value.numberValue()));
        }
        if (numDigitsAfterPt > 0) {
            double valueDouble = value.numberValue();
            double shift = Math.pow(10.0, numDigitsAfterPt);
            return FloatData.forValue((double)Math.round(valueDouble * shift) / shift);
        }
        double valueDouble = value.numberValue();
        double shift = Math.pow(10.0, -numDigitsAfterPt);
        return IntegerData.forValue((int)((double)Math.round(valueDouble / shift) * shift));
    }

    @Override
    public JsExpr computeForJsSrc(List<JsExpr> args) {
        JsExpr value = args.get(0);
        JsExpr numDigitsAfterPt = args.size() == 2 ? args.get(1) : null;
        int numDigitsAfterPtAsInt = 0;
        if (numDigitsAfterPt != null) {
            try {
                numDigitsAfterPtAsInt = Integer.parseInt(numDigitsAfterPt.getText());
            }
            catch (NumberFormatException nfe) {
                numDigitsAfterPtAsInt = Integer.MIN_VALUE;
            }
        }
        if (numDigitsAfterPtAsInt == 0) {
            return new JsExpr("Math.round(" + value.getText() + ")", Integer.MAX_VALUE);
        }
        if (numDigitsAfterPtAsInt >= 0 && numDigitsAfterPtAsInt <= 12 || numDigitsAfterPtAsInt == Integer.MIN_VALUE) {
            String shiftExprText = numDigitsAfterPtAsInt >= 0 && numDigitsAfterPtAsInt <= 12 ? "1" + "000000000000".substring(0, numDigitsAfterPtAsInt) : "Math.pow(10, " + numDigitsAfterPt.getText() + ")";
            JsExpr shift = new JsExpr(shiftExprText, Integer.MAX_VALUE);
            JsExpr valueTimesShift = SoyJsCodeUtils.genJsExprUsingSoySyntax(Operator.TIMES, Lists.newArrayList((Object[])new JsExpr[]{value, shift}));
            return new JsExpr("Math.round(" + valueTimesShift.getText() + ") / " + shift.getText(), Operator.DIVIDE_BY.getPrecedence());
        }
        if (numDigitsAfterPtAsInt < 0 && numDigitsAfterPtAsInt >= -12) {
            String shiftExprText = "1" + "000000000000".substring(0, -numDigitsAfterPtAsInt);
            JsExpr shift = new JsExpr(shiftExprText, Integer.MAX_VALUE);
            JsExpr valueDivideByShift = SoyJsCodeUtils.genJsExprUsingSoySyntax(Operator.DIVIDE_BY, Lists.newArrayList((Object[])new JsExpr[]{value, shift}));
            return new JsExpr("Math.round(" + valueDivideByShift.getText() + ") * " + shift.getText(), Operator.TIMES.getPrecedence());
        }
        throw new IllegalArgumentException("Second argument to round() function is " + numDigitsAfterPtAsInt + ", which is too large in magnitude.");
    }
}

