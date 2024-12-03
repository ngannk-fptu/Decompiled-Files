/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript.operation;

import com.sun.pdfview.function.postscript.operation.PostScriptOperation;
import java.util.Stack;

final class Index
implements PostScriptOperation {
    Index() {
    }

    @Override
    public void eval(Stack<Object> environment) {
        long n = Math.round((Double)environment.pop());
        environment.push(environment.get((int)((long)environment.size() - n - 1L)));
    }
}

