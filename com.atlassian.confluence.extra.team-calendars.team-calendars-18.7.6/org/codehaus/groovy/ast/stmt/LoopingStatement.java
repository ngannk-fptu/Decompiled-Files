/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.stmt;

import org.codehaus.groovy.ast.stmt.Statement;

public interface LoopingStatement {
    public Statement getLoopBlock();

    public void setLoopBlock(Statement var1);
}

