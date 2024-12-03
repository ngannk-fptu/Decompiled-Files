/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator.markedcontent;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

public class BeginMarkedContentSequenceWithProperties
extends OperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        COSName tag = null;
        COSDictionary properties = null;
        for (COSBase argument : arguments) {
            if (argument instanceof COSName) {
                tag = (COSName)argument;
                continue;
            }
            if (!(argument instanceof COSDictionary)) continue;
            properties = (COSDictionary)argument;
        }
        this.context.beginMarkedContentSequence(tag, properties);
    }

    @Override
    public String getName() {
        return "BDC";
    }
}

