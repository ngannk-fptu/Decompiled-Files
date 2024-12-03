/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.cellprocessor.constraint;

import java.util.HashSet;
import java.util.Set;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.util.CsvContext;

public class Strlen
extends CellProcessorAdaptor
implements StringCellProcessor {
    private final Set<Integer> requiredLengths = new HashSet<Integer>();

    public Strlen(int ... requiredLengths) {
        Strlen.checkPreconditions(requiredLengths);
        this.checkAndAddLengths(requiredLengths);
    }

    public Strlen(int requiredLength, CellProcessor next) {
        this(new int[]{requiredLength}, next);
    }

    public Strlen(int[] requiredLengths, CellProcessor next) {
        super(next);
        Strlen.checkPreconditions(requiredLengths);
        this.checkAndAddLengths(requiredLengths);
    }

    private static void checkPreconditions(int ... requiredLengths) {
        if (requiredLengths == null) {
            throw new NullPointerException("requiredLengths should not be null");
        }
        if (requiredLengths.length == 0) {
            throw new IllegalArgumentException("requiredLengths should not be empty");
        }
    }

    private void checkAndAddLengths(int ... requiredLengths) {
        for (int length : requiredLengths) {
            if (length < 0) {
                throw new IllegalArgumentException(String.format("required length cannot be negative but was %d", length));
            }
            this.requiredLengths.add(length);
        }
    }

    public Object execute(Object value, CsvContext context) {
        this.validateInputNotNull(value, context);
        String stringValue = value.toString();
        int length = stringValue.length();
        if (!this.requiredLengths.contains(length)) {
            throw new SuperCsvConstraintViolationException(String.format("the length (%d) of value '%s' not any of the required lengths", length, stringValue), context, this);
        }
        return this.next.execute(value, context);
    }
}

