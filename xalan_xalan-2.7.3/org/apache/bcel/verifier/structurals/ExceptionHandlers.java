/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.structurals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.bcel.generic.CodeExceptionGen;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.verifier.structurals.ExceptionHandler;

public class ExceptionHandlers {
    private static final ExceptionHandler[] EMPTY_ARRAY = new ExceptionHandler[0];
    private final Map<InstructionHandle, Set<ExceptionHandler>> exceptionHandlers = new HashMap<InstructionHandle, Set<ExceptionHandler>>();

    public ExceptionHandlers(MethodGen mg) {
        CodeExceptionGen[] cegs;
        for (CodeExceptionGen ceg : cegs = mg.getExceptionHandlers()) {
            ExceptionHandler eh = new ExceptionHandler(ceg.getCatchType(), ceg.getHandlerPC());
            for (InstructionHandle ih = ceg.getStartPC(); ih != ceg.getEndPC().getNext(); ih = ih.getNext()) {
                this.exceptionHandlers.computeIfAbsent(ih, k -> new HashSet()).add(eh);
            }
        }
    }

    public ExceptionHandler[] getExceptionHandlers(InstructionHandle ih) {
        Set<ExceptionHandler> hsSet = this.exceptionHandlers.get(ih);
        if (hsSet == null) {
            return EMPTY_ARRAY;
        }
        return hsSet.toArray(ExceptionHandler.EMPTY_ARRAY);
    }
}

