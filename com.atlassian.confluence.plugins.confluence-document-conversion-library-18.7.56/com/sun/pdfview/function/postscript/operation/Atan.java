/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript.operation;

import com.sun.pdfview.function.postscript.operation.PostScriptOperation;
import java.util.Stack;

final class Atan
implements PostScriptOperation {
    Atan() {
    }

    @Override
    public void eval(Stack<Object> environment) {
        double den = (Double)environment.pop();
        double num = (Double)environment.pop();
        if (den == 0.0) {
            environment.push(90.0);
        } else {
            environment.push(Math.toDegrees(Math.atan(num / den)));
        }
    }
}

