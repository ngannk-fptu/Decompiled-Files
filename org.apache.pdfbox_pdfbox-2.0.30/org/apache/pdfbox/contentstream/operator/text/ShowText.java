/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator.text;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSString;

public class ShowText
extends OperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        if (arguments.isEmpty()) {
            return;
        }
        COSBase base = arguments.get(0);
        if (!(base instanceof COSString)) {
            return;
        }
        if (this.context.getTextMatrix() == null) {
            return;
        }
        COSString string = (COSString)base;
        this.context.showTextString(string.getBytes());
    }

    @Override
    public String getName() {
        return "Tj";
    }
}

