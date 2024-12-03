/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common.function.type4;

import java.util.Stack;
import org.apache.pdfbox.pdmodel.common.function.type4.ExecutionContext;
import org.apache.pdfbox.pdmodel.common.function.type4.Operator;

class ArithmeticOperators {
    private ArithmeticOperators() {
    }

    static class Truncate
    implements Operator {
        Truncate() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Number num = context.popNumber();
            if (num instanceof Integer) {
                context.getStack().push(num.intValue());
            } else {
                context.getStack().push(Float.valueOf((int)num.floatValue()));
            }
        }
    }

    static class Sub
    implements Operator {
        Sub() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            Number num2 = context.popNumber();
            Number num1 = context.popNumber();
            if (num1 instanceof Integer && num2 instanceof Integer) {
                long result = num1.longValue() - num2.longValue();
                if (result < Integer.MIN_VALUE || result > Integer.MAX_VALUE) {
                    stack.push(Float.valueOf(result));
                } else {
                    stack.push((int)result);
                }
            } else {
                float result = num1.floatValue() - num2.floatValue();
                stack.push(Float.valueOf(result));
            }
        }
    }

    static class Sqrt
    implements Operator {
        Sqrt() {
        }

        @Override
        public void execute(ExecutionContext context) {
            float num = context.popReal();
            if (num < 0.0f) {
                throw new IllegalArgumentException("argument must be nonnegative");
            }
            context.getStack().push(Float.valueOf((float)Math.sqrt(num)));
        }
    }

    static class Sin
    implements Operator {
        Sin() {
        }

        @Override
        public void execute(ExecutionContext context) {
            float angle = context.popReal();
            float sin = (float)Math.sin(Math.toRadians(angle));
            context.getStack().push(Float.valueOf(sin));
        }
    }

    static class Round
    implements Operator {
        Round() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Number num = context.popNumber();
            if (num instanceof Integer) {
                context.getStack().push(num.intValue());
            } else {
                context.getStack().push(Float.valueOf(Math.round(num.doubleValue())));
            }
        }
    }

    static class Neg
    implements Operator {
        Neg() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Number num = context.popNumber();
            if (num instanceof Integer) {
                int v = num.intValue();
                if (v == Integer.MIN_VALUE) {
                    context.getStack().push(Float.valueOf(-num.floatValue()));
                } else {
                    context.getStack().push(-num.intValue());
                }
            } else {
                context.getStack().push(Float.valueOf(-num.floatValue()));
            }
        }
    }

    static class Mul
    implements Operator {
        Mul() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Number num2 = context.popNumber();
            Number num1 = context.popNumber();
            if (num1 instanceof Integer && num2 instanceof Integer) {
                long result = num1.longValue() * num2.longValue();
                if (result >= Integer.MIN_VALUE && result <= Integer.MAX_VALUE) {
                    context.getStack().push((int)result);
                } else {
                    context.getStack().push(Float.valueOf(result));
                }
            } else {
                double result = num1.doubleValue() * num2.doubleValue();
                context.getStack().push(Float.valueOf((float)result));
            }
        }
    }

    static class Mod
    implements Operator {
        Mod() {
        }

        @Override
        public void execute(ExecutionContext context) {
            int int2 = context.popInt();
            int int1 = context.popInt();
            context.getStack().push(int1 % int2);
        }
    }

    static class Log
    implements Operator {
        Log() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Number num = context.popNumber();
            context.getStack().push(Float.valueOf((float)Math.log10(num.doubleValue())));
        }
    }

    static class Ln
    implements Operator {
        Ln() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Number num = context.popNumber();
            context.getStack().push(Float.valueOf((float)Math.log(num.doubleValue())));
        }
    }

    static class IDiv
    implements Operator {
        IDiv() {
        }

        @Override
        public void execute(ExecutionContext context) {
            int num2 = context.popInt();
            int num1 = context.popInt();
            context.getStack().push(num1 / num2);
        }
    }

    static class Floor
    implements Operator {
        Floor() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Number num = context.popNumber();
            if (num instanceof Integer) {
                context.getStack().push(num);
            } else {
                context.getStack().push(Float.valueOf((float)Math.floor(num.doubleValue())));
            }
        }
    }

    static class Exp
    implements Operator {
        Exp() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Number exp = context.popNumber();
            Number base = context.popNumber();
            double value = Math.pow(base.doubleValue(), exp.doubleValue());
            context.getStack().push(Float.valueOf((float)value));
        }
    }

    static class Div
    implements Operator {
        Div() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Number num2 = context.popNumber();
            Number num1 = context.popNumber();
            context.getStack().push(Float.valueOf(num1.floatValue() / num2.floatValue()));
        }
    }

    static class Cvr
    implements Operator {
        Cvr() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Number num = context.popNumber();
            context.getStack().push(Float.valueOf(num.floatValue()));
        }
    }

    static class Cvi
    implements Operator {
        Cvi() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Number num = context.popNumber();
            context.getStack().push(num.intValue());
        }
    }

    static class Cos
    implements Operator {
        Cos() {
        }

        @Override
        public void execute(ExecutionContext context) {
            float angle = context.popReal();
            float cos = (float)Math.cos(Math.toRadians(angle));
            context.getStack().push(Float.valueOf(cos));
        }
    }

    static class Ceiling
    implements Operator {
        Ceiling() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Number num = context.popNumber();
            if (num instanceof Integer) {
                context.getStack().push(num);
            } else {
                context.getStack().push(Float.valueOf((float)Math.ceil(num.doubleValue())));
            }
        }
    }

    static class Atan
    implements Operator {
        Atan() {
        }

        @Override
        public void execute(ExecutionContext context) {
            float den = context.popReal();
            float num = context.popReal();
            float atan = (float)Math.atan2(num, den);
            if ((atan = (float)Math.toDegrees(atan) % 360.0f) < 0.0f) {
                atan += 360.0f;
            }
            context.getStack().push(Float.valueOf(atan));
        }
    }

    static class Add
    implements Operator {
        Add() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Number num2 = context.popNumber();
            Number num1 = context.popNumber();
            if (num1 instanceof Integer && num2 instanceof Integer) {
                long sum = num1.longValue() + num2.longValue();
                if (sum < Integer.MIN_VALUE || sum > Integer.MAX_VALUE) {
                    context.getStack().push(Float.valueOf(sum));
                } else {
                    context.getStack().push((int)sum);
                }
            } else {
                float sum = num1.floatValue() + num2.floatValue();
                context.getStack().push(Float.valueOf(sum));
            }
        }
    }

    static class Abs
    implements Operator {
        Abs() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Number num = context.popNumber();
            if (num instanceof Integer) {
                context.getStack().push(Math.abs(num.intValue()));
            } else {
                context.getStack().push(Float.valueOf(Math.abs(num.floatValue())));
            }
        }
    }
}

