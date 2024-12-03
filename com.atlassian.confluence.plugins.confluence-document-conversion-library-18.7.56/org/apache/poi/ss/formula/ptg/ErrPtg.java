/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.ss.formula.ptg.ScalarConstantPtg;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class ErrPtg
extends ScalarConstantPtg {
    public static final ErrPtg NULL_INTERSECTION = new ErrPtg(FormulaError.NULL.getCode());
    public static final ErrPtg DIV_ZERO = new ErrPtg(FormulaError.DIV0.getCode());
    public static final ErrPtg VALUE_INVALID = new ErrPtg(FormulaError.VALUE.getCode());
    public static final ErrPtg REF_INVALID = new ErrPtg(FormulaError.REF.getCode());
    public static final ErrPtg NAME_INVALID = new ErrPtg(FormulaError.NAME.getCode());
    public static final ErrPtg NUM_ERROR = new ErrPtg(FormulaError.NUM.getCode());
    public static final ErrPtg N_A = new ErrPtg(FormulaError.NA.getCode());
    public static final short sid = 28;
    private static final int SIZE = 2;
    private final int field_1_error_code;

    private ErrPtg(int errorCode) {
        if (!FormulaError.isValidCode(errorCode)) {
            throw new IllegalArgumentException("Invalid error code (" + errorCode + ")");
        }
        this.field_1_error_code = errorCode;
    }

    public static ErrPtg read(LittleEndianInput in) {
        return ErrPtg.valueOf(in.readByte());
    }

    @Override
    public void write(LittleEndianOutput out) {
        out.writeByte(28 + this.getPtgClass());
        out.writeByte(this.field_1_error_code);
    }

    @Override
    public String toFormulaString() {
        return FormulaError.forInt(this.field_1_error_code).getString();
    }

    @Override
    public byte getSid() {
        return 28;
    }

    @Override
    public int getSize() {
        return 2;
    }

    public int getErrorCode() {
        return this.field_1_error_code;
    }

    public static ErrPtg valueOf(int code) {
        switch (FormulaError.forInt(code)) {
            case DIV0: {
                return DIV_ZERO;
            }
            case NA: {
                return N_A;
            }
            case NAME: {
                return NAME_INVALID;
            }
            case NULL: {
                return NULL_INTERSECTION;
            }
            case NUM: {
                return NUM_ERROR;
            }
            case REF: {
                return REF_INVALID;
            }
            case VALUE: {
                return VALUE_INVALID;
            }
        }
        throw new RuntimeException("Unexpected error code (" + code + ")");
    }

    @Override
    public ErrPtg copy() {
        return this;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("errorCode", this::getErrorCode);
    }
}

