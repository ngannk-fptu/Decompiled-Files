/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.expr.Expression;

public class DynamicVariable
implements Variable {
    private String name;
    private boolean closureShare = false;
    private boolean staticContext = false;

    public DynamicVariable(String name, boolean context) {
        this.name = name;
        this.staticContext = context;
    }

    @Override
    public ClassNode getType() {
        return ClassHelper.DYNAMIC_TYPE;
    }

    @Override
    public String getName() {
        return this.name;
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
        return this.staticContext;
    }

    @Override
    public boolean isDynamicTyped() {
        return true;
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
        return 0;
    }

    @Override
    public ClassNode getOriginType() {
        return this.getType();
    }
}

