/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.asm.MethodVisitor
 *  org.springframework.asm.Opcodes
 */
package org.springframework.expression.spel;

import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.spel.CodeFlow;

public interface CompilablePropertyAccessor
extends PropertyAccessor,
Opcodes {
    public boolean isCompilable();

    public Class<?> getPropertyType();

    public void generateCode(String var1, MethodVisitor var2, CodeFlow var3);
}

