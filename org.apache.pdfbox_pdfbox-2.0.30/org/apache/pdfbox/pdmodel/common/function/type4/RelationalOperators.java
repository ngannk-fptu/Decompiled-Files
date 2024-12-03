/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common.function.type4;

import java.util.Stack;
import org.apache.pdfbox.pdmodel.common.function.type4.ExecutionContext;
import org.apache.pdfbox.pdmodel.common.function.type4.Operator;

class RelationalOperators {
    private RelationalOperators() {
    }

    static class Ne
    extends Eq {
        Ne() {
        }

        @Override
        protected boolean isEqual(Object op1, Object op2) {
            boolean result = super.isEqual(op1, op2);
            return !result;
        }
    }

    static class Lt
    extends AbstractNumberComparisonOperator {
        Lt() {
        }

        @Override
        protected boolean compare(Number num1, Number num2) {
            return num1.floatValue() < num2.floatValue();
        }
    }

    static class Le
    extends AbstractNumberComparisonOperator {
        Le() {
        }

        @Override
        protected boolean compare(Number num1, Number num2) {
            return num1.floatValue() <= num2.floatValue();
        }
    }

    static class Gt
    extends AbstractNumberComparisonOperator {
        Gt() {
        }

        @Override
        protected boolean compare(Number num1, Number num2) {
            return num1.floatValue() > num2.floatValue();
        }
    }

    static class Ge
    extends AbstractNumberComparisonOperator {
        Ge() {
        }

        @Override
        protected boolean compare(Number num1, Number num2) {
            return num1.floatValue() >= num2.floatValue();
        }
    }

    private static abstract class AbstractNumberComparisonOperator
    implements Operator {
        private AbstractNumberComparisonOperator() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            Object op2 = stack.pop();
            Object op1 = stack.pop();
            Number num1 = (Number)op1;
            Number num2 = (Number)op2;
            boolean result = this.compare(num1, num2);
            stack.push(result);
        }

        protected abstract boolean compare(Number var1, Number var2);
    }

    static class Eq
    implements Operator {
        Eq() {
        }

        @Override
        public void execute(ExecutionContext context) {
            Stack<Object> stack = context.getStack();
            Object op2 = stack.pop();
            Object op1 = stack.pop();
            boolean result = this.isEqual(op1, op2);
            stack.push(result);
        }

        protected boolean isEqual(Object op1, Object op2) {
            boolean result = false;
            if (op1 instanceof Number && op2 instanceof Number) {
                Number num1 = (Number)op1;
                Number num2 = (Number)op2;
                result = num1.floatValue() == num2.floatValue();
            } else {
                result = op1.equals(op2);
            }
            return result;
        }
    }
}

