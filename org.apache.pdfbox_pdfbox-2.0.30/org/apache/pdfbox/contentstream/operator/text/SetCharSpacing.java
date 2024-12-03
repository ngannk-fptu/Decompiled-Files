/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator.text;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.MissingOperandException;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;

public class SetCharSpacing
extends OperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        if (arguments.isEmpty()) {
            throw new MissingOperandException(operator, arguments);
        }
        COSBase charSpacing = arguments.get(arguments.size() - 1);
        if (charSpacing instanceof COSNumber) {
            COSNumber characterSpacing = (COSNumber)charSpacing;
            this.context.getGraphicsState().getTextState().setCharacterSpacing(characterSpacing.floatValue());
        }
    }

    @Override
    public String getName() {
        return "Tc";
    }
}

