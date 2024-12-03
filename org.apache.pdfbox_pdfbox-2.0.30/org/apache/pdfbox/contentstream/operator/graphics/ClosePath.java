/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.contentstream.operator.graphics;

import java.io.IOException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.graphics.GraphicsOperatorProcessor;
import org.apache.pdfbox.cos.COSBase;

public final class ClosePath
extends GraphicsOperatorProcessor {
    private static final Log LOG = LogFactory.getLog(ClosePath.class);

    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException {
        if (this.context.getCurrentPoint() == null) {
            LOG.warn((Object)"ClosePath without initial MoveTo");
            return;
        }
        this.context.closePath();
    }

    @Override
    public String getName() {
        return "h";
    }
}

