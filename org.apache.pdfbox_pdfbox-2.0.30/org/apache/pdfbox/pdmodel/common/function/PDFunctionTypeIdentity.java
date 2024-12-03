/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common.function;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.pdmodel.common.function.PDFunction;

public class PDFunctionTypeIdentity
extends PDFunction {
    public PDFunctionTypeIdentity(COSBase function) {
        super(null);
    }

    @Override
    public int getFunctionType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public float[] eval(float[] input) throws IOException {
        return input;
    }

    @Override
    protected COSArray getRangeValues() {
        return null;
    }

    @Override
    public String toString() {
        return "FunctionTypeIdentity";
    }
}

