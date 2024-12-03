/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript.operation;

import com.sun.pdfview.function.postscript.operation.PostScriptOperation;
import java.util.Stack;

final class Roll
implements PostScriptOperation {
    Roll() {
    }

    @Override
    public void eval(Stack<Object> environment) {
        int i;
        int j = ((Number)environment.pop()).intValue();
        int n = ((Number)environment.pop()).intValue();
        Object[] topN = new Object[n];
        for (i = 0; i < n; ++i) {
            topN[i] = environment.pop();
        }
        for (i = 0; i < n; ++i) {
            int k = (-i + j - 1) % n;
            if (k < 0) {
                k += n;
            }
            environment.push(topN[k]);
        }
    }
}

