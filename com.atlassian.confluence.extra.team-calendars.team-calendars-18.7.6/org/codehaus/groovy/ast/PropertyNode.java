/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import groovyjarjarasm.asm.Opcodes;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.Statement;

public class PropertyNode
extends AnnotatedNode
implements Opcodes,
Variable {
    private FieldNode field;
    private Statement getterBlock;
    private Statement setterBlock;
    private final int modifiers;

    public PropertyNode(String name, int modifiers, ClassNode type, ClassNode owner, Expression initialValueExpression, Statement getterBlock, Statement setterBlock) {
        this(new FieldNode(name, modifiers & 8, type, owner, initialValueExpression), modifiers, getterBlock, setterBlock);
    }

    public PropertyNode(FieldNode field, int modifiers, Statement getterBlock, Statement setterBlock) {
        this.field = field;
        this.modifiers = modifiers;
        this.getterBlock = getterBlock;
        this.setterBlock = setterBlock;
    }

    public Statement getGetterBlock() {
        return this.getterBlock;
    }

    @Override
    public Expression getInitialExpression() {
        return this.field.getInitialExpression();
    }

    public void setGetterBlock(Statement getterBlock) {
        this.getterBlock = getterBlock;
    }

    public void setSetterBlock(Statement setterBlock) {
        this.setterBlock = setterBlock;
    }

    @Override
    public int getModifiers() {
        return this.modifiers;
    }

    @Override
    public String getName() {
        return this.field.getName();
    }

    public Statement getSetterBlock() {
        return this.setterBlock;
    }

    @Override
    public ClassNode getType() {
        return this.field.getType();
    }

    public void setType(ClassNode t) {
        this.field.setType(t);
    }

    public FieldNode getField() {
        return this.field;
    }

    public void setField(FieldNode fn) {
        this.field = fn;
    }

    public boolean isPrivate() {
        return (this.modifiers & 2) != 0;
    }

    public boolean isPublic() {
        return (this.modifiers & 1) != 0;
    }

    public boolean isStatic() {
        return (this.modifiers & 8) != 0;
    }

    @Override
    public boolean hasInitialExpression() {
        return this.field.hasInitialExpression();
    }

    @Override
    public boolean isInStaticContext() {
        return this.field.isInStaticContext();
    }

    @Override
    public boolean isDynamicTyped() {
        return this.field.isDynamicTyped();
    }

    @Override
    public boolean isClosureSharedVariable() {
        return false;
    }

    @Override
    @Deprecated
    public void setClosureSharedVariable(boolean inClosure) {
    }

    @Override
    public ClassNode getOriginType() {
        return this.getType();
    }
}

