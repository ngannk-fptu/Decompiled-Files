/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Fixed2ArgFunction;

public class Roman
extends Fixed2ArgFunction {
    private static final int[] VALUES = new int[]{1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    private static final String[] ROMAN = new String[]{"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
    private static final String[][] REPLACEMENTS = new String[][]{{"XLV", "VL", "XCV", "VC", "CDL", "LD", "CML", "LM", "CMVC", "LMVL"}, {"CDXC", "LDXL", "CDVC", "LDVL", "CMXC", "LMXL", "XCIX", "VCIV", "XLIX", "VLIV"}, {"XLIX", "IL", "XCIX", "IC", "CDXC", "XD", "CDVC", "XDV", "CDIC", "XDIX", "LMVL", "XMV", "CMIC", "XMIX", "CMXC", "XM"}, {"XDV", "VD", "XDIX", "VDIV", "XMV", "VM", "XMIX", "VMIV"}, {"VDIV", "ID", "VMIV", "IM"}};

    @Override
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval numberVE, ValueEval formVE) {
        int form;
        int number;
        try {
            ValueEval ve = OperandResolver.getSingleValue(numberVE, srcRowIndex, srcColumnIndex);
            number = OperandResolver.coerceValueToInt(ve);
        }
        catch (EvaluationException e) {
            return ErrorEval.VALUE_INVALID;
        }
        if (number < 0) {
            return ErrorEval.VALUE_INVALID;
        }
        if (number > 3999) {
            return ErrorEval.VALUE_INVALID;
        }
        if (number == 0) {
            return new StringEval("");
        }
        try {
            ValueEval ve = OperandResolver.getSingleValue(formVE, srcRowIndex, srcColumnIndex);
            form = OperandResolver.coerceValueToInt(ve);
        }
        catch (EvaluationException e) {
            return ErrorEval.NUM_ERROR;
        }
        if (form > 4 || form < 0) {
            return ErrorEval.VALUE_INVALID;
        }
        String result = this.integerToRoman(number);
        if (form == 0) {
            return new StringEval(result);
        }
        return new StringEval(this.makeConcise(result, form));
    }

    private String integerToRoman(int number) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 13; ++i) {
            while (number >= VALUES[i]) {
                number -= VALUES[i];
                result.append(ROMAN[i]);
            }
        }
        return result.toString();
    }

    public String makeConcise(String input, int form) {
        String result = input;
        for (int i = 0; i <= form && i <= 4 && form > 0; ++i) {
            if (i == 1 && form > 1) continue;
            String[] repl = REPLACEMENTS[i];
            for (int j = 0; j < repl.length; j += 2) {
                result = result.replace(repl[j], repl[j + 1]);
            }
        }
        return result;
    }
}

