/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript.operation;

import com.sun.pdfview.function.postscript.operation.PostScriptOperation;
import java.util.Stack;

final class Exp
implements PostScriptOperation {
    Exp() {
    }

    @Override
    public void eval(Stack<Object> environment) {
        double exponent = (Double)environment.pop();
        double base = (Double)environment.pop();
        environment.push(Math.pow(exponent, base));
    }
}

