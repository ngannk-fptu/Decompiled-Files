/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript.operation;

import com.sun.pdfview.function.postscript.operation.PostScriptOperation;
import java.util.Stack;

final class Mod
implements PostScriptOperation {
    Mod() {
    }

    @Override
    public void eval(Stack<Object> environment) {
        long int2 = (Long)environment.pop();
        long int1 = (Long)environment.pop();
        environment.push(int1 % int2);
    }
}

