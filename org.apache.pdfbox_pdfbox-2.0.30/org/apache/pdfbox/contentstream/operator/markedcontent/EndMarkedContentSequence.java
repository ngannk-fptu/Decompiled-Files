/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator.markedcontent;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;

public class EndMarkedContentSequence
extends OperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        this.context.endMarkedContentSequence();
    }

    @Override
    public String getName() {
        return "EMC";
    }
}

