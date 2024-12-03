/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import groovyjarjarasm.asm.Opcodes;
import java.lang.reflect.Field;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.expr.Expression;

public class FieldNode
extends AnnotatedNode
implements Opcodes,
Variable {
    private String name;
    private int modifiers;
    private ClassNode type;
    private ClassNode owner;
    private Expression initialValueExpression;
    private boolean dynamicTyped;
    private boolean holder;
    private ClassNode originType = ClassHelper.DYNAMIC_TYPE;

    public static FieldNode newStatic(Class theClass, String name) throws SecurityException, NoSuchFieldException {
        Field field = theClass.getField(name);
        ClassNode fldType = ClassHelper.make(field.getType());
        return new FieldNode(name, 9, fldType, ClassHelper.make(theClass), null);
    }

    public FieldNode(String name, int modifiers, ClassNode type, ClassNode owner, Expression initialValueExpression) {
        this.name = name;
        this.modifiers = modifiers;
        this.type = type;
        if (this.type == ClassHelper.DYNAMIC_TYPE && initialValueExpression != null) {
            this.setType(initialValueExpression.getType());
        }
        this.setType(type);
        this.originType = type;
        this.owner = owner;
        this.initialValueExpression = initialValueExpression;
    }

    @Override
    public Expression getInitialExpression() {
        return this.initialValueExpression;
    }

    @Override
    public int getModifiers() {
        return this.modifiers;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ClassNode getType() {
        return this.type;
    }

    public void setType(ClassNode type) {
        this.type = type;
        this.originType = type;
        this.dynamicTyped |= type == ClassHelper.DYNAMIC_TYPE;
    }

    public ClassNode getOwner() {
        return this.owner;
    }

    public boolean isHolder() {
        return this.holder;
    }

    public void setHolder(boolean holder) {
        this.holder = holder;
    }

    @Override
    public boolean isDynamicTyped() {
        return this.dynamicTyped;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public boolean isStatic() {
        return (this.modifiers & 8) != 0;
    }

    public boolean isEnum() {
        return (this.modifiers & 0x4000) != 0;
    }

    public boolean isFinal() {
        return (this.modifiers & 0x10) != 0;
    }

    public boolean isVolatile() {
        return (this.modifiers & 0x40) != 0;
    }

    public boolean isPublic() {
        return (this.modifiers & 1) != 0;
    }

    public boolean isProtected() {
        return (this.modifiers & 4) != 0;
    }

    public void setOwner(ClassNode owner) {
        this.owner = owner;
    }

    @Override
    public boolean hasInitialExpression() {
        return this.initialValueExpression != null;
    }

    @Override
    public boolean isInStaticContext() {
        return this.isStatic();
    }

    public Expression getInitialValueExpression() {
        return this.initialValueExpression;
    }

    public void setInitialValueExpression(Expression initialValueExpression) {
        this.initialValueExpression = initialValueExpression;
    }

    @Override
    @Deprecated
    public boolean isClosureSharedVariable() {
        return false;
    }

    @Override
    @Deprecated
    public void setClosureSharedVariable(boolean inClosure) {
    }

    @Override
    public ClassNode getOriginType() {
        return this.originType;
    }

    public void setOriginType(ClassNode cn) {
        this.originType = cn;
    }

    public void rename(String name) {
        this.declaringClass.renameField(this.name, name);
        this.name = name;
    }
}

