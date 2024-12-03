/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.PropertyNode;

public interface GroovyClassVisitor {
    public void visitClass(ClassNode var1);

    public void visitConstructor(ConstructorNode var1);

    public void visitMethod(MethodNode var1);

    public void visitField(FieldNode var1);

    public void visitProperty(PropertyNode var1);
}

