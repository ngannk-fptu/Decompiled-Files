/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.structurals;

import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.ObjectType;

public class ExceptionHandler {
    static final ExceptionHandler[] EMPTY_ARRAY = new ExceptionHandler[0];
    private final ObjectType catchType;
    private final InstructionHandle handlerPc;

    ExceptionHandler(ObjectType catchType, InstructionHandle handlerPc) {
        this.catchType = catchType;
        this.handlerPc = handlerPc;
    }

    public ObjectType getExceptionType() {
        return this.catchType;
    }

    public InstructionHandle getHandlerStart() {
        return this.handlerPc;
    }
}

