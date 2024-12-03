/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript.operation;

import com.sun.pdfview.function.postscript.operation.PostScriptOperation;
import java.util.Stack;

final class If
implements PostScriptOperation {
    If() {
    }

    @Override
    public void eval(Stack<Object> environment) {
        if (((Boolean)environment.pop()).booleanValue()) {
            environment.push(environment.pop());
        } else {
            environment.pop();
        }
    }
}

