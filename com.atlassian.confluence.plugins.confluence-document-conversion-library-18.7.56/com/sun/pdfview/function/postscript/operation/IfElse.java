/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript.operation;

import com.sun.pdfview.function.postscript.operation.PostScriptOperation;
import java.util.Stack;

final class IfElse
implements PostScriptOperation {
    IfElse() {
    }

    @Override
    public void eval(Stack<Object> environment) {
        if (((Boolean)environment.pop()).booleanValue()) {
            environment.pop();
        } else {
            environment.pop();
        }
    }
}

