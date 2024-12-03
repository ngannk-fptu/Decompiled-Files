/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.exception;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class SuperCsvConstraintViolationException
extends SuperCsvCellProcessorException {
    private static final long serialVersionUID = 1L;

    public SuperCsvConstraintViolationException(String msg, CsvContext context, CellProcessor processor) {
        super(msg, context, processor);
    }

    public SuperCsvConstraintViolationException(String msg, CsvContext context, CellProcessor processor, Throwable t) {
        super(msg, context, processor, t);
    }
}

