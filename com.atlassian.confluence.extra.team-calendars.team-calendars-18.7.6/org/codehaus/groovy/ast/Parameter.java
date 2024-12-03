/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.expr.Expression;

public class Parameter
extends AnnotatedNode
implements Variable {
    public static final Parameter[] EMPTY_ARRAY = new Parameter[0];
    private ClassNode type;
    private final String name;
    private boolean dynamicTyped;
    private Expression defaultValue;
    private boolean hasDefaultValue;
    private boolean inStaticContext;
    private boolean closureShare = false;
    private int modifiers;
    private ClassNode originType = ClassHelper.DYNAMIC_TYPE;

    public Parameter(ClassNode type, String name) {
        this.name = name;
        this.setType(type);
        this.originType = type;
        this.hasDefaultValue = false;
    }

    public Parameter(ClassNode type, String name, Expression defaultValue) {
        this(type, name);
        this.defaultValue = defaultValue;
        this.hasDefaultValue = defaultValue != null;
    }

    public String toString() {
        return super.toString() + "[name:" + this.name + (this.type == null ? "" : " type: " + this.type.getName()) + ", hasDefaultValue: " + this.hasInitialExpression() + "]";
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ClassNode getType() {
        return this.type;
    }

    public void setType(ClassNode type) {
        this.type = type;
        this.dynamicTyped |= type == ClassHelper.DYNAMIC_TYPE;
    }

    @Override
    public boolean hasInitialExpression() {
        return this.hasDefaultValue;
    }

    @Override
    public Expression getInitialExpression() {
        return this.defaultValue;
    }

    public void setInitialExpression(Expression init) {
        this.defaultValue = init;
        this.hasDefaultValue = this.defaultValue != null;
    }

    @Override
    public boolean isInStaticContext() {
        return this.inStaticContext;
    }

    public void setInStaticContext(boolean inStaticContext) {
        this.inStaticContext = inStaticContext;
    }

    @Override
    public boolean isDynamicTyped() {
        return this.dynamicTyped;
    }

    @Override
    public boolean isClosureSharedVariable() {
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

    @Override
    public ClassNode getOriginType() {
        return this.originType;
    }

    public void setOriginType(ClassNode cn) {
        this.originType = cn;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }
}

