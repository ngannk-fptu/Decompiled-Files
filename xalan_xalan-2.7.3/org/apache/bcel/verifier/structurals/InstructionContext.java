/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.structurals;

import java.util.ArrayList;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.verifier.structurals.ExceptionHandler;
import org.apache.bcel.verifier.structurals.ExecutionVisitor;
import org.apache.bcel.verifier.structurals.Frame;
import org.apache.bcel.verifier.structurals.InstConstraintVisitor;

public interface InstructionContext {
    public boolean execute(Frame var1, ArrayList<InstructionContext> var2, InstConstraintVisitor var3, ExecutionVisitor var4);

    public ExceptionHandler[] getExceptionHandlers();

    public Frame getInFrame();

    public InstructionHandle getInstruction();

    public Frame getOutFrame(ArrayList<InstructionContext> var1);

    public InstructionContext[] getSuccessors();

    public int getTag();

    public void setTag(int var1);
}

