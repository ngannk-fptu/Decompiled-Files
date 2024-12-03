/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator.graphics;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.MissingOperandException;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.graphics.GraphicsOperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;

public final class ShadingFill
extends GraphicsOperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException {
        if (operands.isEmpty()) {
            throw new MissingOperandException(operator, operands);
        }
        COSBase base = operands.get(0);
        if (!(base instanceof COSName)) {
            throw new MissingOperandException(operator, operands);
        }
        this.context.shadingFill((COSName)base);
    }

    @Override
    public String getName() {
        return "sh";
    }
}

