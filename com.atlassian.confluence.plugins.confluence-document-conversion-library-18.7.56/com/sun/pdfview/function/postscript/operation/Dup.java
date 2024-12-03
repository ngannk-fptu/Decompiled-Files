/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript.operation;

import com.sun.pdfview.function.postscript.operation.PostScriptOperation;
import java.util.Stack;

final class Dup
implements PostScriptOperation {
    Dup() {
    }

    @Override
    public void eval(Stack<Object> environment) {
        Object obj = environment.pop();
        environment.push(obj);
        environment.push(obj);
    }
}

