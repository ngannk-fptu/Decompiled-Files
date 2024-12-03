/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;

public abstract class OperatorProcessor {
    protected PDFStreamEngine context;

    protected OperatorProcessor() {
    }

    protected final PDFStreamEngine getContext() {
        return this.context;
    }

    public void setContext(PDFStreamEngine context) {
        this.context = context;
    }

    public abstract void process(Operator var1, List<COSBase> var2) throws IOException;

    public abstract String getName();

    public boolean checkArrayTypesClass(List<COSBase> operands, Class<?> clazz) {
        for (COSBase base : operands) {
            if (clazz.isInstance(base)) continue;
            return false;
        }
        return true;
    }
}

