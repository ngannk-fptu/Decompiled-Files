/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;

public final class MissingOperandException
extends IOException {
    public MissingOperandException(Operator operator, List<COSBase> operands) {
        super("Operator " + operator.getName() + " has too few operands: " + operands);
    }
}

