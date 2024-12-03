/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.cellprocessor.constraint;

import java.util.HashSet;
import java.util.Set;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.util.CsvContext;

public class Unique
extends CellProcessorAdaptor {
    private final Set<Object> encounteredElements = new HashSet<Object>();

    public Unique() {
    }

    public Unique(CellProcessor next) {
        super(next);
    }

    public Object execute(Object value, CsvContext context) {
        this.validateInputNotNull(value, context);
        if (!this.encounteredElements.add(value)) {
            throw new SuperCsvConstraintViolationException(String.format("duplicate value '%s' encountered", value), context, this);
        }
        return this.next.execute(value, context);
    }
}

