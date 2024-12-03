/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;

public class NextLine
extends OperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        ArrayList<COSBase> args = new ArrayList<COSBase>(2);
        args.add(new COSFloat(0.0f));
        args.add(new COSFloat(-this.context.getGraphicsState().getTextState().getLeading()));
        this.context.processOperator("Td", args);
    }

    @Override
    public String getName() {
        return "T*";
    }
}

