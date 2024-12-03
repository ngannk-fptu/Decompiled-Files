/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript.operation;

import com.sun.pdfview.function.postscript.operation.PostScriptOperation;
import java.util.Stack;

final class Ln
implements PostScriptOperation {
    Ln() {
    }

    @Override
    public void eval(Stack<Object> environment) {
        environment.push(Math.log((Double)environment.pop()));
    }
}

