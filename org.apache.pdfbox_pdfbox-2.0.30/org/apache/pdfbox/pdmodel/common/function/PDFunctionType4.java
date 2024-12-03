/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common.function;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.pdmodel.common.PDRange;
import org.apache.pdfbox.pdmodel.common.function.PDFunction;
import org.apache.pdfbox.pdmodel.common.function.type4.ExecutionContext;
import org.apache.pdfbox.pdmodel.common.function.type4.InstructionSequence;
import org.apache.pdfbox.pdmodel.common.function.type4.InstructionSequenceBuilder;
import org.apache.pdfbox.pdmodel.common.function.type4.Operators;

public class PDFunctionType4
extends PDFunction {
    private static final Operators OPERATORS = new Operators();
    private final InstructionSequence instructions;

    public PDFunctionType4(COSBase functionStream) throws IOException {
        super(functionStream);
        byte[] bytes = this.getPDStream().toByteArray();
        String string = new String(bytes, "ISO-8859-1");
        this.instructions = InstructionSequenceBuilder.parse(string);
    }

    @Override
    public int getFunctionType() {
        return 4;
    }

    @Override
    public float[] eval(float[] input) throws IOException {
        ExecutionContext context = new ExecutionContext(OPERATORS);
        for (int i = 0; i < input.length; ++i) {
            PDRange domain = this.getDomainForInput(i);
            float value = this.clipToRange(input[i], domain.getMin(), domain.getMax());
            context.getStack().push(Float.valueOf(value));
        }
        this.instructions.execute(context);
        int numberOfOutputValues = this.getNumberOfOutputParameters();
        int numberOfActualOutputValues = context.getStack().size();
        if (numberOfActualOutputValues < numberOfOutputValues) {
            throw new IllegalStateException("The type 4 function returned " + numberOfActualOutputValues + " values but the Range entry indicates that " + numberOfOutputValues + " values be returned.");
        }
        float[] outputValues = new float[numberOfOutputValues];
        for (int i = numberOfOutputValues - 1; i >= 0; --i) {
            PDRange range = this.getRangeForOutput(i);
            outputValues[i] = context.popReal();
            outputValues[i] = this.clipToRange(outputValues[i], range.getMin(), range.getMax());
        }
        return outputValues;
    }
}

