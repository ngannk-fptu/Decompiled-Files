/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;

public class VariableExpression
extends Expression
implements Variable {
    public static final VariableExpression THIS_EXPRESSION = new VariableExpression("this", ClassHelper.DYNAMIC_TYPE);
    public static final VariableExpression SUPER_EXPRESSION = new VariableExpression("super", ClassHelper.DYNAMIC_TYPE);
    private String variable;
    private int modifiers;
    private boolean inStaticContext;
    private boolean isDynamicTyped = false;
    private Variable accessedVariable;
    boolean closureShare = false;
    boolean useRef = false;
    private final ClassNode originType;

    public Variable getAccessedVariable() {
        return this.accessedVariable;
    }

    public void setAccessedVariable(Variable origin) {
        this.accessedVariable = origin;
    }

    public VariableExpression(String variable, ClassNode type) {
        this.variable = variable;
        this.originType = type;
        this.setType(ClassHelper.getWrapper(type));
    }

    public VariableExpression(String variable) {
        this(variable, ClassHelper.DYNAMIC_TYPE);
    }

    public VariableExpression(Variable variable) {
        this(variable.getName(), variable.getOriginType());
        this.setAccessedVariable(variable);
        this.setModifiers(variable.getModifiers());
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitVariableExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        return this;
    }

    @Override
    public String getText() {
        return this.variable;
    }

    @Override
    public String getName() {
        return this.variable;
    }

    public String toString() {
        return super.toString() + "[variable: " + this.variable + (this.isDynamicTyped() ? "" : " type: " + this.getType()) + "]";
    }

    @Override
    public Expression getInitialExpression() {
        return null;
    }

    @Override
    public boolean hasInitialExpression() {
        return false;
    }

    @Override
    public boolean isInStaticContext() {
        if (this.accessedVariable != null && this.accessedVariable != this) {
            return this.accessedVariable.isInStaticContext();
        }
        return this.inStaticContext;
    }

    public void setInStaticContext(boolean inStaticContext) {
        this.inStaticContext = inStaticContext;
    }

    @Override
    public void setType(ClassNode cn) {
        super.setType(cn);
        this.isDynamicTyped |= ClassHelper.DYNAMIC_TYPE == cn;
    }

    @Override
    public boolean isDynamicTyped() {
        if (this.accessedVariable != null && this.accessedVariable != this) {
            return this.accessedVariable.isDynamicTyped();
        }
        return this.isDynamicTyped;
    }

    @Override
    public boolean isClosureSharedVariable() {
        if (this.accessedVariable != null && this.accessedVariable != this) {
            return this.accessedVariable.isClosureSharedVariable();
        }
        return this.closureShare;
    }

    @Override
    public void setClosureSharedVariable(boolean inClosure) {
        this.closureShare = inClosure;
    }

    @Override
    public int getModifiers() {
        return this.modifiers;
    }

    public void setUseReferenceDirectly(boolean useRef) {
        this.useRef = useRef;
    }

    public boolean isUseReferenceDirectly() {
        return this.useRef;
    }

    @Override
    public ClassNode getType() {
        if (this.accessedVariable != null && this.accessedVariable != this) {
            return this.accessedVariable.getType();
        }
        return super.getType();
    }

    @Override
    public ClassNode getOriginType() {
        if (this.accessedVariable != null && this.accessedVariable != this) {
            return this.accessedVariable.getOriginType();
        }
        return this.originType;
    }

    public boolean isThisExpression() {
        return "this".equals(this.variable);
    }

    public boolean isSuperExpression() {
        return "super".equals(this.variable);
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }
}

