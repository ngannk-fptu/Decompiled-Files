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

public class SetLineCapStyle
extends OperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        if (arguments.isEmpty()) {
            throw new MissingOperandException(operator, arguments);
        }
        int lineCapStyle = ((COSNumber)arguments.get(0)).intValue();
        this.context.getGraphicsState().setLineCap(lineCapStyle);
    }

    @Override
    public String getName() {
        return "J";
    }
}

