/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript.operation;

import com.sun.pdfview.function.postscript.operation.PostScriptOperation;
import java.util.Stack;

final class Sin
implements PostScriptOperation {
    Sin() {
    }

    @Override
    public void eval(Stack<Object> environment) {
        double radians = Math.toRadians((Double)environment.pop());
        environment.push(Math.toDegrees(Math.sin(radians)));
    }
}

