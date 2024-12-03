/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.MissingOperandException;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSNumber;

public class MoveTextSetLeading
extends OperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        if (arguments.size() < 2) {
            throw new MissingOperandException(operator, arguments);
        }
        COSBase base1 = arguments.get(1);
        if (!(base1 instanceof COSNumber)) {
            return;
        }
        COSNumber y = (COSNumber)base1;
        ArrayList<COSBase> args = new ArrayList<COSBase>();
        args.add(new COSFloat(-y.floatValue()));
        this.context.processOperator("TL", args);
        this.context.processOperator("Td", arguments);
    }

    @Override
    public String getName() {
        return "TD";
    }
}

