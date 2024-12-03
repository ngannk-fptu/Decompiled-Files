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
import org.apache.pdfbox.util.Matrix;

public class Concatenate
extends OperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        if (arguments.size() < 6) {
            throw new MissingOperandException(operator, arguments);
        }
        if (!this.checkArrayTypesClass(arguments, COSNumber.class)) {
            return;
        }
        COSNumber a = (COSNumber)arguments.get(0);
        COSNumber b = (COSNumber)arguments.get(1);
        COSNumber c = (COSNumber)arguments.get(2);
        COSNumber d = (COSNumber)arguments.get(3);
        COSNumber e = (COSNumber)arguments.get(4);
        COSNumber f = (COSNumber)arguments.get(5);
        Matrix matrix = new Matrix(a.floatValue(), b.floatValue(), c.floatValue(), d.floatValue(), e.floatValue(), f.floatValue());
        this.context.getGraphicsState().getCurrentTransformationMatrix().concatenate(matrix);
    }

    @Override
    public String getName() {
        return "cm";
    }
}

