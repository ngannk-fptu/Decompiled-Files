/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript.operation;

import com.sun.pdfview.function.postscript.operation.PostScriptOperation;
import java.util.Stack;

final class Not
implements PostScriptOperation {
    Not() {
    }

    @Override
    public void eval(Stack<Object> environment) {
        environment.push((Long)environment.pop() ^ 0xFFFFFFFFFFFFFFFFL);
    }
}

