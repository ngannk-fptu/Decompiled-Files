/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator.graphics;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.graphics.GraphicsOperatorProcessor;
import org.apache.pdfbox.cos.COSBase;

public final class StrokePath
extends GraphicsOperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException {
        this.context.strokePath();
    }

    @Override
    public String getName() {
        return "S";
    }
}

