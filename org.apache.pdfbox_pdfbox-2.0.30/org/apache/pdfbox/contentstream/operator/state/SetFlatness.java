/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator.state;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.MissingOperandException;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;

public class SetFlatness
extends OperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException {
        if (operands.isEmpty()) {
            throw new MissingOperandException(operator, operands);
        }
        if (!this.checkArrayTypesClass(operands, COSNumber.class)) {
            return;
        }
        COSNumber value = (COSNumber)operands.get(0);
        this.context.getGraphicsState().setFlatness(value.floatValue());
    }

    @Override
    public String getName() {
        return "i";
    }
}

