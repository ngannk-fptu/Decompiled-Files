/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.Expression;

public interface TypeChooser {
    public ClassNode resolveType(Expression var1, ClassNode var2);
}

