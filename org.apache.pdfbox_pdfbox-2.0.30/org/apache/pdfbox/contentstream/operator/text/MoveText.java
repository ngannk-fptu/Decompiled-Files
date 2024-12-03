/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.contentstream.operator.text;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.operator.MissingOperandException;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.util.Matrix;

public class MoveText
extends OperatorProcessor {
    private static final Log LOG = LogFactory.getLog(MoveText.class);

    @Override
    public void process(Operator operator, List<COSBase> arguments) throws MissingOperandException {
        if (arguments.size() < 2) {
            throw new MissingOperandException(operator, arguments);
        }
        Matrix textLineMatrix = this.context.getTextLineMatrix();
        if (textLineMatrix == null) {
            LOG.warn((Object)("TextLineMatrix is null, " + this.getName() + " operator will be ignored"));
            return;
        }
        COSBase base0 = arguments.get(0);
        COSBase base1 = arguments.get(1);
        if (!(base0 instanceof COSNumber)) {
            return;
        }
        if (!(base1 instanceof COSNumber)) {
            return;
        }
        COSNumber x = (COSNumber)base0;
        COSNumber y = (COSNumber)base1;
        Matrix matrix = new Matrix(1.0f, 0.0f, 0.0f, 1.0f, x.floatValue(), y.floatValue());
        textLineMatrix.concatenate(matrix);
        this.context.setTextMatrix(textLineMatrix.clone());
    }

    @Override
    public String getName() {
        return "Td";
    }
}

