/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator.markedcontent;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;

public class BeginMarkedContentSequence
extends OperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        COSName tag = null;
        for (COSBase argument : arguments) {
            if (!(argument instanceof COSName)) continue;
            tag = (COSName)argument;
        }
        this.context.beginMarkedContentSequence(tag, null);
    }

    @Override
    public String getName() {
        return "BMC";
    }
}

