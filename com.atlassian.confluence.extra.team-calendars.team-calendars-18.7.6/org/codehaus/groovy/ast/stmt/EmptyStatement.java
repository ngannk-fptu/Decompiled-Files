/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.stmt;

import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.stmt.Statement;

public class EmptyStatement
extends Statement {
    public static final EmptyStatement INSTANCE = new EmptyStatement();

    @Override
    public void visit(GroovyCodeVisitor visitor) {
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}

