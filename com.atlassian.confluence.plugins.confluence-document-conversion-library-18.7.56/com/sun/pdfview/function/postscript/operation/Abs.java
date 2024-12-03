/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript.operation;

import com.sun.pdfview.function.postscript.operation.PostScriptOperation;
import java.util.Stack;

final class Abs
implements PostScriptOperation {
    Abs() {
    }

    @Override
    public void eval(Stack<Object> environment) {
        environment.push(Math.abs((Double)environment.pop()));
    }
}

