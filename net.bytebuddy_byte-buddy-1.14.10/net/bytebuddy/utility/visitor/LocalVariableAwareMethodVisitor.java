/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.utility.visitor;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.utility.OpenedClassReader;

public class LocalVariableAwareMethodVisitor
extends MethodVisitor {
    private int freeOffset;

    public LocalVariableAwareMethodVisitor(MethodVisitor methodVisitor, MethodDescription methodDescription) {
        super(OpenedClassReader.ASM_API, methodVisitor);
        this.freeOffset = methodDescription.getStackSize();
    }

    @SuppressFBWarnings(value={"SF_SWITCH_NO_DEFAULT"}, justification="No action required on default option.")
    public void visitVarInsn(int opcode, int offset) {
        switch (opcode) {
            case 54: 
            case 56: 
            case 58: {
                this.freeOffset = Math.max(this.freeOffset, offset + 1);
                break;
            }
            case 55: 
            case 57: {
                this.freeOffset = Math.max(this.freeOffset, offset + 2);
            }
        }
        super.visitVarInsn(opcode, offset);
    }

    public int getFreeOffset() {
        return this.freeOffset;
    }
}

