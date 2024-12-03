/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator.state;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.contentstream.operator.state.EmptyGraphicsStackException;
import org.apache.pdfbox.cos.COSBase;

public class Restore
extends OperatorProcessor {
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        if (this.context.getGraphicsStackSize() <= 1) {
            throw new EmptyGraphicsStackException();
        }
        this.context.restoreGraphicsState();
    }

    @Override
    public String getName() {
        return "Q";
    }
}

