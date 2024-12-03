/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.exception;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.util.CsvContext;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SuperCsvCellProcessorException
extends SuperCsvException {
    private static final long serialVersionUID = 1L;
    private final CellProcessor processor;

    public SuperCsvCellProcessorException(String msg, CsvContext context, CellProcessor processor) {
        super(msg, context);
        this.processor = processor;
    }

    public SuperCsvCellProcessorException(String msg, CsvContext context, CellProcessor processor, Throwable t) {
        super(msg, context, t);
        this.processor = processor;
    }

    public SuperCsvCellProcessorException(Class<?> expectedType, Object actualValue, CsvContext context, CellProcessor processor) {
        super(SuperCsvCellProcessorException.getUnexpectedTypeMessage(expectedType, actualValue), context);
        this.processor = processor;
    }

    private static String getUnexpectedTypeMessage(Class<?> expectedType, Object actualValue) {
        if (expectedType == null) {
            throw new NullPointerException("expectedType should not be null");
        }
        String expectedClassName = expectedType.getName();
        String actualClassName = actualValue != null ? actualValue.getClass().getName() : "null";
        return String.format("the input value should be of type %s but is %s", expectedClassName, actualClassName);
    }

    public CellProcessor getProcessor() {
        return this.processor;
    }

    @Override
    public String toString() {
        return String.format("%s: %s%nprocessor=%s%ncontext=%s", this.getClass().getName(), this.getMessage(), this.processor, this.getCsvContext());
    }
}

