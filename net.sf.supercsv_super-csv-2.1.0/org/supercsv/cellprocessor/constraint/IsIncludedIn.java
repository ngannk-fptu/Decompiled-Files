/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.cellprocessor.constraint;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.BoolCellProcessor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.util.CsvContext;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class IsIncludedIn
extends CellProcessorAdaptor
implements BoolCellProcessor,
DateCellProcessor,
DoubleCellProcessor,
LongCellProcessor,
StringCellProcessor {
    private final Set<Object> possibleValues = new HashSet<Object>();

    public IsIncludedIn(Set<Object> possibleValues) {
        IsIncludedIn.checkPreconditions(possibleValues);
        this.possibleValues.addAll(possibleValues);
    }

    public IsIncludedIn(Set<Object> possibleValues, CellProcessor next) {
        super(next);
        IsIncludedIn.checkPreconditions(possibleValues);
        this.possibleValues.addAll(possibleValues);
    }

    public IsIncludedIn(Object[] possibleValues) {
        IsIncludedIn.checkPreconditions(possibleValues);
        Collections.addAll(this.possibleValues, possibleValues);
    }

    public IsIncludedIn(Object[] possibleValues, CellProcessor next) {
        super(next);
        IsIncludedIn.checkPreconditions(possibleValues);
        Collections.addAll(this.possibleValues, possibleValues);
    }

    private static void checkPreconditions(Set<Object> possibleValues) {
        if (possibleValues == null) {
            throw new NullPointerException("possibleValues Set should not be null");
        }
        if (possibleValues.isEmpty()) {
            throw new IllegalArgumentException("possibleValues Set should not be empty");
        }
    }

    private static void checkPreconditions(Object ... possibleValues) {
        if (possibleValues == null) {
            throw new NullPointerException("possibleValues array should not be null");
        }
        if (possibleValues.length == 0) {
            throw new IllegalArgumentException("possibleValues array should not be empty");
        }
    }

    @Override
    public Object execute(Object value, CsvContext context) {
        this.validateInputNotNull(value, context);
        if (!this.possibleValues.contains(value)) {
            throw new SuperCsvConstraintViolationException(String.format("'%s' is not included in the allowed set of values", value), context, this);
        }
        return this.next.execute(value, context);
    }
}

