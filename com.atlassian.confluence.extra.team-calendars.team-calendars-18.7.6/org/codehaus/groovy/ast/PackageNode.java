/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;

public class PackageNode
extends AnnotatedNode {
    private String name;

    public PackageNode(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String getText() {
        return "package " + this.name;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
    }
}

