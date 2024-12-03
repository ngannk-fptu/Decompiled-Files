/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript.operation;

import com.sun.pdfview.function.postscript.operation.PostScriptOperation;
import java.util.Stack;

final class Pop
implements PostScriptOperation {
    Pop() {
    }

    @Override
    public void eval(Stack<Object> environment) {
        environment.pop();
    }
}

