/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.AddConcatExpression;
import freemarker.core.ArithmeticExpression;
import freemarker.core.AssignmentInstruction;
import freemarker.core.BugException;
import freemarker.core.Environment;
import freemarker.core.EvalUtil;
import freemarker.core.Expression;
import freemarker.core.InvalidReferenceException;
import freemarker.core.NonNamespaceException;
import freemarker.core.NonNumericalException;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import freemarker.core._CoreStringUtils;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;

final class Assignment
extends TemplateElement {
    private static final int OPERATOR_TYPE_EQUALS = 65536;
    private static final int OPERATOR_TYPE_PLUS_EQUALS = 65537;
    private static final int OPERATOR_TYPE_PLUS_PLUS = 65538;
    private static final int OPERATOR_TYPE_MINUS_MINUS = 65539;
    private final int scope;
    private final String variableName;
    private final int operatorType;
    private final Expression valueExp;
    private Expression namespaceExp;
    static final int NAMESPACE = 1;
    static final int LOCAL = 2;
    static final int GLOBAL = 3;
    private static final Number ONE = 1;

    Assignment(String variableName, int operator, Expression valueExp, int scope) {
        this.scope = scope;
        this.variableName = variableName;
        if (operator == 105) {
            this.operatorType = 65536;
        } else {
            switch (operator) {
                case 113: {
                    this.operatorType = 65538;
                    break;
                }
                case 114: {
                    this.operatorType = 65539;
                    break;
                }
                case 108: {
                    this.operatorType = 65537;
                    break;
                }
                case 109: {
                    this.operatorType = 0;
                    break;
                }
                case 110: {
                    this.operatorType = 1;
                    break;
                }
                case 111: {
                    this.operatorType = 2;
                    break;
                }
                case 112: {
                    this.operatorType = 3;
                    break;
                }
                default: {
                    throw new BugException();
                }
            }
        }
        this.valueExp = valueExp;
    }

    void setNamespaceExp(Expression namespaceExp) {
        if (this.scope != 1 && namespaceExp != null) {
            throw new BugException();
        }
        this.namespaceExp = namespaceExp;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    TemplateElement[] accept(Environment env) throws TemplateException {
        TemplateModel value;
        Environment.Namespace namespace;
        block23: {
            block22: {
                if (this.namespaceExp != null) break block22;
                switch (this.scope) {
                    case 2: {
                        namespace = null;
                        break block23;
                    }
                    case 3: {
                        namespace = env.getGlobalNamespace();
                        break block23;
                    }
                    case 1: {
                        namespace = env.getCurrentNamespace();
                        break block23;
                    }
                    default: {
                        throw new BugException("Unexpected scope type: " + this.scope);
                    }
                }
            }
            TemplateModel uncheckedNamespace = this.namespaceExp.eval(env);
            try {
                namespace = (Environment.Namespace)uncheckedNamespace;
            }
            catch (ClassCastException e) {
                throw new NonNamespaceException(this.namespaceExp, uncheckedNamespace, env);
            }
            if (namespace == null) {
                throw InvalidReferenceException.getInstance(this.namespaceExp, env);
            }
        }
        if (this.operatorType == 65536) {
            value = this.valueExp.eval(env);
            if (value == null) {
                if (!env.isClassicCompatible()) throw InvalidReferenceException.getInstance(this.valueExp, env);
                value = TemplateScalarModel.EMPTY_STRING;
            }
        } else {
            TemplateModel lhoValue = namespace == null ? env.getLocalVariable(this.variableName) : namespace.get(this.variableName);
            if (this.operatorType == 65537) {
                if (lhoValue == null) {
                    if (!env.isClassicCompatible()) throw InvalidReferenceException.getInstance(this.scope, this.variableName, this.getOperatorTypeAsString(), env);
                    lhoValue = TemplateScalarModel.EMPTY_STRING;
                }
                if ((value = this.valueExp.eval(env)) == null) {
                    if (!env.isClassicCompatible()) throw InvalidReferenceException.getInstance(this.valueExp, env);
                    value = TemplateScalarModel.EMPTY_STRING;
                }
                value = AddConcatExpression._eval(env, this.namespaceExp, null, lhoValue, this.valueExp, value);
            } else {
                if (!(lhoValue instanceof TemplateNumberModel)) {
                    if (lhoValue != null) throw new NonNumericalException(this.variableName, lhoValue, null, env);
                    throw InvalidReferenceException.getInstance(this.scope, this.variableName, this.getOperatorTypeAsString(), env);
                }
                Number lhoNumber = EvalUtil.modelToNumber((TemplateNumberModel)lhoValue, null);
                if (this.operatorType == 65538) {
                    value = AddConcatExpression._evalOnNumbers(env, this.getParentElement(), lhoNumber, ONE);
                } else if (this.operatorType == 65539) {
                    value = ArithmeticExpression._eval(env, this.getParentElement(), lhoNumber, 0, ONE);
                } else {
                    Number rhoNumber = this.valueExp.evalToNumber(env);
                    value = ArithmeticExpression._eval(env, this, lhoNumber, this.operatorType, rhoNumber);
                }
            }
        }
        if (namespace == null) {
            env.setLocalVariable(this.variableName, value);
            return null;
        } else {
            namespace.put(this.variableName, value);
        }
        return null;
    }

    @Override
    protected String dump(boolean canonical) {
        String dn;
        StringBuilder buf = new StringBuilder();
        String string = dn = this.getParentElement() instanceof AssignmentInstruction ? null : this.getNodeTypeSymbol();
        if (dn != null) {
            if (canonical) {
                buf.append("<");
            }
            buf.append(dn);
            buf.append(' ');
        }
        buf.append(_CoreStringUtils.toFTLTopLevelTragetIdentifier(this.variableName));
        if (this.valueExp != null) {
            buf.append(' ');
        }
        buf.append(this.getOperatorTypeAsString());
        if (this.valueExp != null) {
            buf.append(' ');
            buf.append(this.valueExp.getCanonicalForm());
        }
        if (dn != null) {
            if (this.namespaceExp != null) {
                buf.append(" in ");
                buf.append(this.namespaceExp.getCanonicalForm());
            }
            if (canonical) {
                buf.append(">");
            }
        }
        String result = buf.toString();
        return result;
    }

    @Override
    String getNodeTypeSymbol() {
        return Assignment.getDirectiveName(this.scope);
    }

    static String getDirectiveName(int scope) {
        if (scope == 2) {
            return "#local";
        }
        if (scope == 3) {
            return "#global";
        }
        if (scope == 1) {
            return "#assign";
        }
        return "#{unknown_assignment_type}";
    }

    @Override
    int getParameterCount() {
        return 5;
    }

    @Override
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0: {
                return this.variableName;
            }
            case 1: {
                return this.getOperatorTypeAsString();
            }
            case 2: {
                return this.valueExp;
            }
            case 3: {
                return this.scope;
            }
            case 4: {
                return this.namespaceExp;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0: {
                return ParameterRole.ASSIGNMENT_TARGET;
            }
            case 1: {
                return ParameterRole.ASSIGNMENT_OPERATOR;
            }
            case 2: {
                return ParameterRole.ASSIGNMENT_SOURCE;
            }
            case 3: {
                return ParameterRole.VARIABLE_SCOPE;
            }
            case 4: {
                return ParameterRole.NAMESPACE;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }

    private String getOperatorTypeAsString() {
        if (this.operatorType == 65536) {
            return "=";
        }
        if (this.operatorType == 65537) {
            return "+=";
        }
        if (this.operatorType == 65538) {
            return "++";
        }
        if (this.operatorType == 65539) {
            return "--";
        }
        return ArithmeticExpression.getOperatorSymbol(this.operatorType) + "=";
    }

    static String scopeAsString(int scope) {
        switch (scope) {
            case 1: {
                return "template namespace";
            }
            case 2: {
                return "local scope";
            }
            case 3: {
                return "global scope";
            }
        }
        throw new AssertionError((Object)("Unsupported scope: " + scope));
    }
}

