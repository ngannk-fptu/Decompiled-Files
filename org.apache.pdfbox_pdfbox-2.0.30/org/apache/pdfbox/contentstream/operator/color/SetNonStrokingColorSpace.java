/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator.color;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;

public class SetNonStrokingColorSpace
extends OperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        if (arguments.isEmpty()) {
            return;
        }
        COSBase base = arguments.get(0);
        if (!(base instanceof COSName)) {
            return;
        }
        PDColorSpace cs = this.context.getResources().getColorSpace((COSName)base);
        this.context.getGraphicsState().setNonStrokingColorSpace(cs);
        this.context.getGraphicsState().setNonStrokingColor(cs.getInitialColor());
    }

    @Override
    public String getName() {
        return "cs";
    }
}

