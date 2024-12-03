/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator.text;

import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;

public class SetWordSpacing
extends OperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> arguments) {
        if (arguments.isEmpty()) {
            return;
        }
        COSBase base = arguments.get(0);
        if (!(base instanceof COSNumber)) {
            return;
        }
        COSNumber wordSpacing = (COSNumber)base;
        this.context.getGraphicsState().getTextState().setWordSpacing(wordSpacing.floatValue());
    }

    @Override
    public String getName() {
        return "Tw";
    }
}

