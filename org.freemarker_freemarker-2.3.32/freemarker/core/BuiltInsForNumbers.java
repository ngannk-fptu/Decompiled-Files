/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BuiltIn;
import freemarker.core.BuiltInForNumber;
import freemarker.core.Environment;
import freemarker.core.EvalUtil;
import freemarker.core._TemplateModelException;
import freemarker.template.SimpleDate;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.utility.NumberUtil;
import freemarker.template.utility.StringUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

class BuiltInsForNumbers {
    private static final BigDecimal BIG_DECIMAL_ONE = new BigDecimal("1");
    private static final BigDecimal BIG_DECIMAL_LONG_MIN = BigDecimal.valueOf(Long.MIN_VALUE);
    private static final BigDecimal BIG_DECIMAL_LONG_MAX = BigDecimal.valueOf(Long.MAX_VALUE);
    private static final BigInteger BIG_INTEGER_LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger BIG_INTEGER_LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);

    private static final long safeToLong(Number num) throws TemplateModelException {
        if (num instanceof Double) {
            double d = Math.round(num.doubleValue());
            if (d > 9.223372036854776E18 || d < -9.223372036854776E18) {
                throw new _TemplateModelException("Number doesn't fit into a 64 bit signed integer (long): ", d);
            }
            return (long)d;
        }
        if (num instanceof Float) {
            float f = Math.round(num.floatValue());
            if (f > 9.223372E18f || f < -9.223372E18f) {
                throw new _TemplateModelException("Number doesn't fit into a 64 bit signed integer (long): ", Float.valueOf(f));
            }
            return (long)f;
        }
        if (num instanceof BigDecimal) {
            BigDecimal bd = ((BigDecimal)num).setScale(0, 4);
            if (bd.compareTo(BIG_DECIMAL_LONG_MAX) > 0 || bd.compareTo(BIG_DECIMAL_LONG_MIN) < 0) {
                throw new _TemplateModelException("Number doesn't fit into a 64 bit signed integer (long): ", bd);
            }
            return bd.longValue();
        }
        if (num instanceof BigInteger) {
            BigInteger bi = (BigInteger)num;
            if (bi.compareTo(BIG_INTEGER_LONG_MAX) > 0 || bi.compareTo(BIG_INTEGER_LONG_MIN) < 0) {
                throw new _TemplateModelException("Number doesn't fit into a 64 bit signed integer (long): ", bi);
            }
            return bi.longValue();
        }
        if (num instanceof Long || num instanceof Integer || num instanceof Byte || num instanceof Short) {
            return num.longValue();
        }
        throw new _TemplateModelException("Unsupported number type: ", num.getClass());
    }

    private BuiltInsForNumbers() {
    }

    static class shortBI
    extends BuiltInForNumber {
        shortBI() {
        }

        @Override
        TemplateModel calculateResult(Number num, TemplateModel model) {
            if (num instanceof Short) {
                return model;
            }
            return new SimpleNumber((Number)num.shortValue());
        }
    }

    static class roundBI
    extends BuiltInForNumber {
        private static final BigDecimal half = new BigDecimal("0.5");

        roundBI() {
        }

        @Override
        TemplateModel calculateResult(Number num, TemplateModel model) {
            return new SimpleNumber(new BigDecimal(num.doubleValue()).add(half).divide(BIG_DECIMAL_ONE, 0, 3));
        }
    }

    static class number_to_dateBI
    extends BuiltInForNumber {
        private final int dateType;

        number_to_dateBI(int dateType) {
            this.dateType = dateType;
        }

        @Override
        TemplateModel calculateResult(Number num, TemplateModel model) throws TemplateModelException {
            return new SimpleDate(new Date(BuiltInsForNumbers.safeToLong(num)), this.dateType);
        }
    }

    static class longBI
    extends BuiltIn {
        longBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel model = this.target.eval(env);
            if (!(model instanceof TemplateNumberModel) && model instanceof TemplateDateModel) {
                Date date = EvalUtil.modelToDate((TemplateDateModel)model, this.target);
                return new SimpleNumber(date.getTime());
            }
            Number num = this.target.modelToNumber(model, env);
            if (num instanceof Long) {
                return model;
            }
            return new SimpleNumber(num.longValue());
        }
    }

    static class is_nanBI
    extends BuiltInForNumber {
        is_nanBI() {
        }

        @Override
        TemplateModel calculateResult(Number num, TemplateModel model) throws TemplateModelException {
            return NumberUtil.isNaN(num) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_infiniteBI
    extends BuiltInForNumber {
        is_infiniteBI() {
        }

        @Override
        TemplateModel calculateResult(Number num, TemplateModel model) throws TemplateModelException {
            return NumberUtil.isInfinite(num) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class intBI
    extends BuiltInForNumber {
        intBI() {
        }

        @Override
        TemplateModel calculateResult(Number num, TemplateModel model) {
            if (num instanceof Integer) {
                return model;
            }
            return new SimpleNumber(num.intValue());
        }
    }

    static class floorBI
    extends BuiltInForNumber {
        floorBI() {
        }

        @Override
        TemplateModel calculateResult(Number num, TemplateModel model) {
            return new SimpleNumber(new BigDecimal(num.doubleValue()).divide(BIG_DECIMAL_ONE, 0, 3));
        }
    }

    static class floatBI
    extends BuiltInForNumber {
        floatBI() {
        }

        @Override
        TemplateModel calculateResult(Number num, TemplateModel model) {
            if (num instanceof Float) {
                return model;
            }
            return new SimpleNumber(num.floatValue());
        }
    }

    static class doubleBI
    extends BuiltInForNumber {
        doubleBI() {
        }

        @Override
        TemplateModel calculateResult(Number num, TemplateModel model) {
            if (num instanceof Double) {
                return model;
            }
            return new SimpleNumber(num.doubleValue());
        }
    }

    static class ceilingBI
    extends BuiltInForNumber {
        ceilingBI() {
        }

        @Override
        TemplateModel calculateResult(Number num, TemplateModel model) {
            return new SimpleNumber(new BigDecimal(num.doubleValue()).divide(BIG_DECIMAL_ONE, 0, 2));
        }
    }

    static class byteBI
    extends BuiltInForNumber {
        byteBI() {
        }

        @Override
        TemplateModel calculateResult(Number num, TemplateModel model) {
            if (num instanceof Byte) {
                return model;
            }
            return new SimpleNumber((Number)num.byteValue());
        }
    }

    static class absBI
    extends BuiltInForNumber {
        absBI() {
        }

        @Override
        TemplateModel calculateResult(Number num, TemplateModel model) throws TemplateModelException {
            if (num instanceof Integer) {
                int n = num.intValue();
                if (n < 0) {
                    return new SimpleNumber(-n);
                }
                return model;
            }
            if (num instanceof BigDecimal) {
                BigDecimal n = (BigDecimal)num;
                if (n.signum() < 0) {
                    return new SimpleNumber(n.negate());
                }
                return model;
            }
            if (num instanceof Double) {
                double n = num.doubleValue();
                if (n < 0.0) {
                    return new SimpleNumber(-n);
                }
                return model;
            }
            if (num instanceof Float) {
                float n = num.floatValue();
                if (n < 0.0f) {
                    return new SimpleNumber(-n);
                }
                return model;
            }
            if (num instanceof Long) {
                long n = num.longValue();
                if (n < 0L) {
                    return new SimpleNumber(-n);
                }
                return model;
            }
            if (num instanceof Short) {
                short n = num.shortValue();
                if (n < 0) {
                    return new SimpleNumber((int)(-n));
                }
                return model;
            }
            if (num instanceof Byte) {
                byte n = num.byteValue();
                if (n < 0) {
                    return new SimpleNumber((int)(-n));
                }
                return model;
            }
            if (num instanceof BigInteger) {
                BigInteger n = (BigInteger)num;
                if (n.signum() < 0) {
                    return new SimpleNumber(n.negate());
                }
                return model;
            }
            throw new _TemplateModelException("Unsupported number class: ", num.getClass());
        }
    }

    static class upper_abcBI
    extends abcBI {
        upper_abcBI() {
        }

        @Override
        protected String toABC(int n) {
            return StringUtil.toUpperABC(n);
        }
    }

    static class lower_abcBI
    extends abcBI {
        lower_abcBI() {
        }

        @Override
        protected String toABC(int n) {
            return StringUtil.toLowerABC(n);
        }
    }

    private static abstract class abcBI
    extends BuiltInForNumber {
        private abcBI() {
        }

        @Override
        TemplateModel calculateResult(Number num, TemplateModel model) throws TemplateModelException {
            int n;
            try {
                n = NumberUtil.toIntExact(num);
            }
            catch (ArithmeticException e) {
                throw new _TemplateModelException(this.target, "The left side operand value isn't compatible with ?", this.key, ": ", e.getMessage());
            }
            if (n <= 0) {
                throw new _TemplateModelException(this.target, "The left side operand of to ?", this.key, " must be at least 1, but was ", n, ".");
            }
            return new SimpleScalar(this.toABC(n));
        }

        protected abstract String toABC(int var1);
    }
}

