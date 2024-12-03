/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import java.util.LinkedList;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MixinNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.stmt.Statement;

public class InnerClassNode
extends ClassNode {
    private ClassNode outerClass;
    private VariableScope scope;
    private boolean anonymous;

    public InnerClassNode(ClassNode outerClass, String name, int modifiers, ClassNode superClass) {
        this(outerClass, name, modifiers, superClass, ClassHelper.EMPTY_TYPE_ARRAY, MixinNode.EMPTY_ARRAY);
    }

    public InnerClassNode(ClassNode outerClass, String name, int modifiers, ClassNode superClass, ClassNode[] interfaces, MixinNode[] mixins) {
        super(name, modifiers, superClass, interfaces, mixins);
        this.outerClass = outerClass;
        if (outerClass.innerClasses == null) {
            outerClass.innerClasses = new LinkedList<InnerClassNode>();
        }
        outerClass.innerClasses.add(this);
    }

    @Override
    public ClassNode getOuterClass() {
        return this.outerClass;
    }

    public ClassNode getOuterMostClass() {
        ClassNode outerClass = this.getOuterClass();
        while (outerClass instanceof InnerClassNode) {
            outerClass = outerClass.getOuterClass();
        }
        return outerClass;
    }

    @Override
    public FieldNode getOuterField(String name) {
        return this.outerClass.getDeclaredField(name);
    }

    public VariableScope getVariableScope() {
        return this.scope;
    }

    public void setVariableScope(VariableScope scope) {
        this.scope = scope;
    }

    public boolean isAnonymous() {
        return this.anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    @Override
    public void addConstructor(ConstructorNode node) {
        super.addConstructor(node);
    }

    @Override
    public ConstructorNode addConstructor(int modifiers, Parameter[] parameters, ClassNode[] exceptions, Statement code) {
        return super.addConstructor(modifiers, parameters, exceptions, code);
    }
}

