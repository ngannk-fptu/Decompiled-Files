/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator.graphics;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.graphics.GraphicsOperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.pdmodel.graphics.image.PDInlineImage;

public final class BeginInlineImage
extends GraphicsOperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException {
        if (operator.getImageData() == null || operator.getImageData().length == 0) {
            return;
        }
        PDInlineImage image = new PDInlineImage(operator.getImageParameters(), operator.getImageData(), this.context.getResources());
        this.context.drawImage(image);
    }

    @Override
    public String getName() {
        return "BI";
    }
}

