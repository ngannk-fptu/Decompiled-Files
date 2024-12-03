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

public class ShowTextLineAndSpace
extends OperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        if (arguments.size() < 3) {
            throw new MissingOperandException(operator, arguments);
        }
        this.context.processOperator("Tw", arguments.subList(0, 1));
        this.context.processOperator("Tc", arguments.subList(1, 2));
        this.context.processOperator("'", arguments.subList(2, 3));
    }

    @Override
    public String getName() {
        return "\"";
    }
}

