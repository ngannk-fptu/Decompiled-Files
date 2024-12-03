/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.stc;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.stmt.Statement;

public class ExtensionMethodNode
extends MethodNode {
    private final MethodNode extensionMethodNode;
    private final boolean isStaticExtension;

    public ExtensionMethodNode(MethodNode extensionMethodNode, String name, int modifiers, ClassNode returnType, Parameter[] parameters, ClassNode[] exceptions, Statement code, boolean isStaticExtension) {
        super(name, modifiers, returnType, parameters, exceptions, code);
        this.extensionMethodNode = extensionMethodNode;
        this.isStaticExtension = isStaticExtension;
    }

    public ExtensionMethodNode(MethodNode extensionMethodNode, String name, int modifiers, ClassNode returnType, Parameter[] parameters, ClassNode[] exceptions, Statement code) {
        this(extensionMethodNode, name, modifiers, returnType, parameters, exceptions, code, false);
    }

    public MethodNode getExtensionMethodNode() {
        return this.extensionMethodNode;
    }

    public boolean isStaticExtension() {
        return this.isStaticExtension;
    }
}

