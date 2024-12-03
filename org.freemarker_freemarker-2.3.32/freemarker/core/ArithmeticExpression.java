/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.ArithmeticEngine;
import freemarker.core.Environment;
import freemarker.core.EvalUtil;
import freemarker.core.Expression;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateObject;
import freemarker.core._MiscTemplateException;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

final class ArithmeticExpression
extends Expression {
    static final int TYPE_SUBSTRACTION = 0;
    static final int TYPE_MULTIPLICATION = 1;
    static final int TYPE_DIVISION = 2;
    static final int TYPE_MODULO = 3;
    private static final char[] OPERATOR_IMAGES = new char[]{'-', '*', '/', '%'};
    private final Expression lho;
    private final Expression rho;
    private final int operator;

    ArithmeticExpression(Expression lho, Expression rho, int operator) {
        this.lho = lho;
        this.rho = rho;
        this.operator = operator;
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        return ArithmeticExpression._eval(env, this, this.lho.evalToNumber(env), this.operator, this.rho.evalToNumber(env));
    }

    static TemplateModel _eval(Environment env, TemplateObject parent, Number lhoNumber, int operator, Number rhoNumber) throws TemplateException, _MiscTemplateException {
        ArithmeticEngine ae = EvalUtil.getArithmeticEngine(env, parent);
        try {
            switch (operator) {
                case 0: {
                    return new SimpleNumber(ae.subtract(lhoNumber, rhoNumber));
                }
                case 1: {
                    return new SimpleNumber(ae.multiply(lhoNumber, rhoNumber));
                }
                case 2: {
                    return new SimpleNumber(ae.divide(lhoNumber, rhoNumber));
                }
                case 3: {
                    return new SimpleNumber(ae.modulus(lhoNumber, rhoNumber));
                }
            }
            if (parent instanceof Expression) {
                throw new _MiscTemplateException((Expression)parent, "Unknown operation: ", operator);
            }
            throw new _MiscTemplateException("Unknown operation: ", operator);
        }
        catch (ArithmeticException e) {
            String[] stringArray;
            Object[] objectArray = new Object[2];
            objectArray[0] = "Arithmetic operation failed";
            if (e.getMessage() != null) {
                String[] stringArray2 = new String[2];
                stringArray2[0] = ": ";
                stringArray = stringArray2;
                stringArray2[1] = e.getMessage();
            } else {
                stringArray = " (see cause exception)";
            }
            objectArray[1] = stringArray;
            throw new _MiscTemplateException((Throwable)e, env, objectArray);
        }
    }

    @Override
    public String getCanonicalForm() {
        return this.lho.getCanonicalForm() + ' ' + ArithmeticExpression.getOperatorSymbol(this.operator) + ' ' + this.rho.getCanonicalForm();
    }

    @Override
    String getNodeTypeSymbol() {
        return String.valueOf(ArithmeticExpression.getOperatorSymbol(this.operator));
    }

    static char getOperatorSymbol(int operator) {
        return OPERATOR_IMAGES[operator];
    }

    @Override
    boolean isLiteral() {
        return this.constantValue != null || this.lho.isLiteral() && this.rho.isLiteral();
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new ArithmeticExpression(this.lho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.rho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.operator);
    }

    @Override
    int getParameterCount() {
        return 3;
    }

    @Override
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0: {
                return this.lho;
            }
            case 1: {
                return this.rho;
            }
            case 2: {
                return this.operator;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0: {
                return ParameterRole.LEFT_HAND_OPERAND;
            }
            case 1: {
                return ParameterRole.RIGHT_HAND_OPERAND;
            }
            case 2: {
                return ParameterRole.AST_NODE_SUBTYPE;
            }
        }
        throw new IndexOutOfBoundsException();
    }
}

