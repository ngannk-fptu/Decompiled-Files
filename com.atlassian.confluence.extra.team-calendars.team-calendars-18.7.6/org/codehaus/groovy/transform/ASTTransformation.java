/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.control.SourceUnit;

public interface ASTTransformation {
    public void visit(ASTNode[] var1, SourceUnit var2);
}

