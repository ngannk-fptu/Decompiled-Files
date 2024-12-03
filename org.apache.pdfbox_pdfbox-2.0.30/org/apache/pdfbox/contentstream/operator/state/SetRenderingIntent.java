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
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingIntent;

public class SetRenderingIntent
extends OperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException {
        if (operands.isEmpty()) {
            throw new MissingOperandException(operator, operands);
        }
        COSBase base = operands.get(0);
        if (!(base instanceof COSName)) {
            return;
        }
        this.context.getGraphicsState().setRenderingIntent(RenderingIntent.fromString(((COSName)base).getName()));
    }

    @Override
    public String getName() {
        return "ri";
    }
}

