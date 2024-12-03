/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator.graphics;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.graphics.GraphicsOperatorProcessor;
import org.apache.pdfbox.cos.COSBase;

public class CloseAndStrokePath
extends GraphicsOperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        this.context.processOperator("h", arguments);
        this.context.processOperator("S", arguments);
    }

    @Override
    public String getName() {
        return "s";
    }
}

