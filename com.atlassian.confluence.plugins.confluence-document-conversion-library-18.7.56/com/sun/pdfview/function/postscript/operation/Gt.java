/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript.operation;

import com.sun.pdfview.function.postscript.operation.PostScriptOperation;
import java.util.Stack;

final class Gt
implements PostScriptOperation {
    Gt() {
    }

    @Override
    public void eval(Stack<Object> environment) {
        double num2 = (Double)environment.pop();
        double num1 = (Double)environment.pop();
        environment.push(num1 > num2);
    }
}

