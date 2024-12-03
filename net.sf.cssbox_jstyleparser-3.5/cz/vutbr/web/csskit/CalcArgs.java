/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermFloatValue;
import cz.vutbr.web.css.TermInteger;
import cz.vutbr.web.css.TermNumber;
import cz.vutbr.web.css.TermNumeric;
import cz.vutbr.web.css.TermOperator;
import cz.vutbr.web.css.TermPercent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalcArgs
extends ArrayList<Term<?>> {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(CalcArgs.class);
    public static final StringEvaluator stringEvaluator = new StringEvaluator();
    private TermNumeric.Unit.Type utype = TermNumeric.Unit.Type.none;
    private boolean isint = true;
    private boolean valid = true;

    public CalcArgs(List<Term<?>> terms) {
        super(terms.size());
        this.scanArguments(terms);
    }

    public TermNumeric.Unit.Type getType() {
        return this.utype;
    }

    public boolean isInt() {
        return this.isint;
    }

    public boolean isValid() {
        return this.valid;
    }

    protected void scanArguments(List<Term<?>> args) {
        ArrayDeque<TermOperator> stack = new ArrayDeque<TermOperator>(5);
        boolean unary = true;
        for (Term<?> t : args) {
            if (t instanceof TermFloatValue) {
                this.add(t);
                this.considerType((TermFloatValue)t);
                unary = false;
                if (this.valid) continue;
                break;
            }
            if (t instanceof TermOperator) {
                TermOperator top;
                int p;
                TermOperator op = (TermOperator)t;
                if (unary && ((Character)op.getValue()).charValue() == '-') {
                    op = (TermOperator)op.shallowClone();
                    op.setValue(Character.valueOf('~'));
                }
                if ((p = this.getPriority(op)) != -1) {
                    top = (TermOperator)stack.peek();
                    if (top == null || ((Character)top.getValue()).charValue() == '(' || p > this.getPriority(top)) {
                        stack.push(op);
                    } else {
                        do {
                            this.add(top);
                            stack.pop();
                        } while ((top = (TermOperator)stack.peek()) != null && ((Character)top.getValue()).charValue() != '(' && p <= this.getPriority(top));
                        stack.push(op);
                    }
                    unary = true;
                    continue;
                }
                if (((Character)op.getValue()).charValue() == '(') {
                    stack.push(op);
                    unary = true;
                    continue;
                }
                if (((Character)op.getValue()).charValue() == ')') {
                    top = (TermOperator)stack.pop();
                    while (top != null && ((Character)top.getValue()).charValue() != '(') {
                        this.add(top);
                        top = (TermOperator)stack.pop();
                    }
                    unary = false;
                    continue;
                }
                this.valid = false;
                break;
            }
            this.valid = false;
            break;
        }
        while (!stack.isEmpty()) {
            this.add(stack.pop());
        }
    }

    private int getPriority(TermOperator op) {
        char c = ((Character)op.getValue()).charValue();
        switch (c) {
            case '+': 
            case '-': {
                return 0;
            }
            case '*': 
            case '/': {
                return 1;
            }
            case '~': {
                return 2;
            }
        }
        return -1;
    }

    private void considerType(TermFloatValue term) {
        TermNumeric.Unit unit = term.getUnit();
        if (this.utype == TermNumeric.Unit.Type.none) {
            if (unit != null && unit.getType() != TermNumeric.Unit.Type.none) {
                this.utype = unit.getType();
            } else if (term instanceof TermPercent) {
                this.utype = TermNumeric.Unit.Type.length;
            } else if (term instanceof TermNumber) {
                this.isint = false;
            }
        } else if (unit != null && unit.getType() != TermNumeric.Unit.Type.none && unit.getType() != this.utype) {
            this.valid = false;
        }
    }

    public <T> T evaluate(Evaluator<T> eval) throws IllegalArgumentException {
        try {
            ArrayDeque<T> stack = new ArrayDeque<T>();
            for (Term t : this) {
                T val;
                if (t instanceof TermOperator) {
                    if (((Character)((TermOperator)t).getValue()).charValue() == '~') {
                        Object val1 = stack.pop();
                        val = eval.evaluateOperator(val1, (TermOperator)t);
                    } else {
                        Object val2 = stack.pop();
                        Object val1 = stack.pop();
                        val = eval.evaluateOperator(val1, val2, (TermOperator)t);
                    }
                    stack.push(val);
                    continue;
                }
                if (!(t instanceof TermFloatValue)) continue;
                val = eval.evaluateArgument((TermFloatValue)t);
                stack.push(val);
            }
            return (T)stack.peek();
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Couldn't evaluate calc() expression", e);
        }
    }

    public static abstract class DoubleEvaluator
    implements Evaluator<Double> {
        @Override
        public Double evaluateArgument(TermFloatValue val) {
            if (val instanceof TermNumber || val instanceof TermInteger) {
                return ((Float)val.getValue()).floatValue();
            }
            return this.resolveValue(val);
        }

        @Override
        public Double evaluateOperator(Double val1, Double val2, TermOperator op) {
            switch (((Character)op.getValue()).charValue()) {
                case '+': {
                    return val1 + val2;
                }
                case '-': {
                    return val1 - val2;
                }
                case '*': {
                    return val1 * val2;
                }
                case '/': {
                    return val1 / val2;
                }
            }
            log.error("Unknown operator {} in expression", (Object)op);
            return 0.0;
        }

        @Override
        public Double evaluateOperator(Double val, TermOperator op) {
            if (((Character)op.getValue()).charValue() == '~') {
                return -val.doubleValue();
            }
            log.error("Unknown unary operator {} in expression", (Object)op);
            return val;
        }

        public abstract double resolveValue(TermFloatValue var1);
    }

    public static class StringEvaluator
    implements Evaluator<String> {
        @Override
        public String evaluateArgument(TermFloatValue val) {
            return val.toString();
        }

        @Override
        public String evaluateOperator(String val1, String val2, TermOperator op) {
            return "(" + val1 + " " + op.toString() + " " + val2.toString() + ")";
        }

        @Override
        public String evaluateOperator(String val, TermOperator op) {
            if (((Character)op.getValue()).charValue() == '~') {
                return "-" + val;
            }
            return op.getValue() + val;
        }
    }

    public static interface Evaluator<T> {
        public T evaluateArgument(TermFloatValue var1);

        public T evaluateOperator(T var1, T var2, TermOperator var3);

        public T evaluateOperator(T var1, TermOperator var2);
    }
}

