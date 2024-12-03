/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.stc;

import java.util.concurrent.atomic.AtomicReference;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.control.SourceUnit;

class PropertyLookupVisitor
extends ClassCodeVisitorSupport {
    private final AtomicReference<ClassNode> result;

    public PropertyLookupVisitor(AtomicReference<ClassNode> result) {
        this.result = result;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return null;
    }

    @Override
    public void visitMethod(MethodNode node) {
        this.result.set(node.getReturnType());
    }

    @Override
    public void visitProperty(PropertyNode node) {
        this.result.set(node.getType());
    }

    @Override
    public void visitField(FieldNode field) {
        this.result.set(field.getType());
    }
}

