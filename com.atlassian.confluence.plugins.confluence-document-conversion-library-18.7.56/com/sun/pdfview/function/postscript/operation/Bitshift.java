/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript.operation;

import com.sun.pdfview.function.postscript.operation.PostScriptOperation;
import java.util.Stack;

final class Bitshift
implements PostScriptOperation {
    Bitshift() {
    }

    @Override
    public void eval(Stack<Object> environment) {
        long shift = (Long)environment.pop();
        long int1 = (Long)environment.pop();
        environment.push(int1 << (int)shift);
    }
}

