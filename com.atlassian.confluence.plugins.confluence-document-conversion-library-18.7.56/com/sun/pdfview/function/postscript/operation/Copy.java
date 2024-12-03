/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript.operation;

import com.sun.pdfview.function.postscript.operation.PostScriptOperation;
import java.util.Stack;

final class Copy
implements PostScriptOperation {
    Copy() {
    }

    @Override
    public void eval(Stack<Object> environment) {
        int i;
        Number count = (Number)environment.pop();
        Object[] buffer = new Object[count.intValue()];
        for (i = 0; i < buffer.length; ++i) {
            buffer[i] = environment.pop();
        }
        for (i = 0; i < buffer.length; ++i) {
            environment.push(buffer[buffer.length - i - 1]);
        }
        for (i = 0; i < buffer.length; ++i) {
            environment.push(buffer[buffer.length - i - 1]);
        }
    }
}

