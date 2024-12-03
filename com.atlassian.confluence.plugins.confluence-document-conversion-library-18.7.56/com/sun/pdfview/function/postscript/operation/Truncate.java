/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript.operation;

import com.sun.pdfview.function.postscript.operation.PostScriptOperation;
import java.util.Stack;

final class Truncate
implements PostScriptOperation {
    Truncate() {
    }

    @Override
    public void eval(Stack<Object> environment) {
        double num1 = (Double)environment.pop();
        environment.push((double)((long)num1) - num1);
    }
}

