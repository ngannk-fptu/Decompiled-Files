/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.function.PDFFunction;
import com.sun.pdfview.function.postscript.PostScriptParser;
import com.sun.pdfview.function.postscript.operation.OperationSet;
import com.sun.pdfview.function.postscript.operation.PostScriptOperation;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Stack;

public class FunctionType4
extends PDFFunction {
    private List<String> tokens;
    private Stack<Object> stack;

    protected FunctionType4() {
        super(4);
    }

    @Override
    protected void parse(PDFObject obj) throws IOException {
        ByteBuffer buf = obj.getStreamBuffer();
        byte[] byteA = new byte[buf.remaining()];
        buf.get(byteA);
        String scriptContent = new String(byteA, "UTF-8");
        this.tokens = new PostScriptParser().parse(scriptContent);
    }

    @Override
    protected void doFunction(float[] inputs, int inputOffset, float[] outputs, int outputOffset) {
        this.prepareInitialStack(inputs, inputOffset);
        for (String token : this.tokens) {
            PostScriptOperation op = OperationSet.getInstance().getOperation(token);
            op.eval(this.stack);
        }
        this.assertResultIsCorrect(outputs, outputOffset);
        this.prepareResult(outputs, outputOffset);
    }

    private void prepareResult(float[] outputs, int outputOffset) {
        for (int i = outputOffset; i < outputs.length; ++i) {
            outputs[outputs.length - i - 1] = ((Double)this.stack.pop()).floatValue();
        }
    }

    private void prepareInitialStack(float[] inputs, int inputOffset) {
        this.stack = new Stack();
        for (int i = inputOffset; i < inputs.length; ++i) {
            this.stack.push(new Double(inputs[i]));
        }
    }

    private void assertResultIsCorrect(float[] outputs, int outputOffset) {
        int expectedResults = outputs.length - outputOffset;
        if (this.stack.size() != expectedResults) {
            throw new IllegalStateException("Output does not match result " + expectedResults + "/" + this.stack);
        }
    }
}

