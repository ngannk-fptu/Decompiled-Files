/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript.operation;

import com.sun.pdfview.function.postscript.operation.PostScriptOperation;
import java.util.Stack;

final class Exch
implements PostScriptOperation {
    Exch() {
    }

    @Override
    public void eval(Stack<Object> environment) {
        Object any1 = environment.pop();
        Object any2 = environment.pop();
        environment.push(any1);
        environment.push(any2);
    }
}

