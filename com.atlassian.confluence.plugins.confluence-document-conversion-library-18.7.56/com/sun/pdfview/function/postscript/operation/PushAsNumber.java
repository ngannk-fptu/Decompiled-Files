/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript.operation;

import com.sun.pdfview.function.postscript.operation.PostScriptOperation;
import java.util.Stack;

final class PushAsNumber
implements PostScriptOperation {
    private String token;

    public PushAsNumber(String numberToken) {
        this.token = numberToken;
    }

    @Override
    public void eval(Stack<Object> environment) {
        try {
            double number = Double.parseDouble(this.token);
            environment.push(number);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("PS token is not supported " + this.token);
        }
    }
}

