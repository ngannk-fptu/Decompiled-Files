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

public final class AppendRectangleToPath
extends GraphicsOperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException {
        if (operands.size() < 4) {
            throw new MissingOperandException(operator, operands);
        }
        if (!this.checkArrayTypesClass(operands, COSNumber.class)) {
            return;
        }
        COSNumber x = (COSNumber)operands.get(0);
        COSNumber y = (COSNumber)operands.get(1);
        COSNumber w = (COSNumber)operands.get(2);
        COSNumber h = (COSNumber)operands.get(3);
        float x1 = x.floatValue();
        float y1 = y.floatValue();
        float x2 = w.floatValue() + x1;
        float y2 = h.floatValue() + y1;
        Point2D.Float p0 = this.context.transformedPoint(x1, y1);
        Point2D.Float p1 = this.context.transformedPoint(x2, y1);
        Point2D.Float p2 = this.context.transformedPoint(x2, y2);
        Point2D.Float p3 = this.context.transformedPoint(x1, y2);
        this.context.appendRectangle(p0, p1, p2, p3);
    }

    @Override
    public String getName() {
        return "re";
    }
}

