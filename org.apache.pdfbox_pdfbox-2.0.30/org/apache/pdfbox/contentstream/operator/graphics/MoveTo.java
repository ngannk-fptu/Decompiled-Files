/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator.graphics;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.MissingOperandException;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.graphics.GraphicsOperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;

public final class MoveTo
extends GraphicsOperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException {
        if (operands.size() < 2) {
            throw new MissingOperandException(operator, operands);
        }
        COSBase base0 = operands.get(0);
        if (!(base0 instanceof COSNumber)) {
            return;
        }
        COSBase base1 = operands.get(1);
        if (!(base1 instanceof COSNumber)) {
            return;
        }
        COSNumber x = (COSNumber)base0;
        COSNumber y = (COSNumber)base1;
        Point2D.Float pos = this.context.transformedPoint(x.floatValue(), y.floatValue());
        this.context.moveTo(pos.x, pos.y);
    }

    @Override
    public String getName() {
        return "m";
    }
}

