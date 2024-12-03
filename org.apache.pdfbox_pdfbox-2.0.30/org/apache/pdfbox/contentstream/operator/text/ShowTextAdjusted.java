/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator.text;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;

public class ShowTextAdjusted
extends OperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        if (arguments.isEmpty()) {
            return;
        }
        COSBase base = arguments.get(0);
        if (!(base instanceof COSArray)) {
            return;
        }
        if (this.context.getTextMatrix() == null) {
            return;
        }
        COSArray array = (COSArray)base;
        this.context.showTextStrings(array);
    }

    @Override
    public String getName() {
        return "TJ";
    }
}

