/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SwitchExpression;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.codegen.ExceptionLabel;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;

public abstract class SubRoutineStatement
extends Statement {
    ExceptionLabel anyExceptionLabel;
    protected SwitchExpression switchExpression = null;

    public static void reenterAllExceptionHandlers(SubRoutineStatement[] subroutines, int max, CodeStream codeStream) {
        if (subroutines == null) {
            return;
        }
        if (max < 0) {
            max = subroutines.length;
        }
        int i = 0;
        while (i < max) {
            SubRoutineStatement sub = subroutines[i];
            sub.enterAnyExceptionHandler(codeStream);
            sub.enterDeclaredExceptionHandlers(codeStream);
            ++i;
        }
    }

    public ExceptionLabel enterAnyExceptionHandler(CodeStream codeStream) {
        if (this.anyExceptionLabel == null) {
            this.anyExceptionLabel = new ExceptionLabel(codeStream, null);
        }
        this.anyExceptionLabel.placeStart();
        return this.anyExceptionLabel;
    }

    public void enterDeclaredExceptionHandlers(CodeStream codeStream) {
    }

    public void exitAnyExceptionHandler() {
        if (this.anyExceptionLabel != null) {
            this.anyExceptionLabel.placeEnd();
        }
    }

    public void exitDeclaredExceptionHandlers(CodeStream codeStream) {
    }

    public abstract boolean generateSubRoutineInvocation(BlockScope var1, CodeStream var2, Object var3, int var4, LocalVariableBinding var5);

    public abstract boolean isSubRoutineEscaping();

    public void placeAllAnyExceptionHandler() {
        this.anyExceptionLabel.place();
    }

    public SwitchExpression getSwitchExpression() {
        return this.switchExpression;
    }

    public void setSwitchExpression(SwitchExpression switchExpression) {
        this.switchExpression = switchExpression;
    }
}

