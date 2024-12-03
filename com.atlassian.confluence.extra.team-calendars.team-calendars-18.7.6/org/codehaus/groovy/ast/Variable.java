/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.Expression;

public interface Variable {
    public ClassNode getType();

    public ClassNode getOriginType();

    public String getName();

    public Expression getInitialExpression();

    public boolean hasInitialExpression();

    public boolean isInStaticContext();

    public boolean isDynamicTyped();

    public boolean isClosureSharedVariable();

    public void setClosureSharedVariable(boolean var1);

    public int getModifiers();
}

