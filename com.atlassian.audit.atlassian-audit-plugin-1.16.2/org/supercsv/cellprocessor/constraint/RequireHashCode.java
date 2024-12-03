/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.cellprocessor.constraint;

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

public class RequireHashCode
extends CellProcessorAdaptor
implements BoolCellProcessor,
DateCellProcessor,
DoubleCellProcessor,
LongCellProcessor,
StringCellProcessor {
    private final Set<Integer> requiredHashCodes = new HashSet<Integer>();

    public RequireHashCode(int ... requiredHashcodes) {
        RequireHashCode.checkPreconditions(requiredHashcodes);
        for (int hash : requiredHashcodes) {
            this.requiredHashCodes.add(hash);
        }
    }

    public RequireHashCode(int requiredHashcode, CellProcessor next) {
        this(new int[]{requiredHashcode}, next);
    }

    public RequireHashCode(int[] requiredHashcodes, CellProcessor next) {
        super(next);
        RequireHashCode.checkPreconditions(requiredHashcodes);
        for (int hash : requiredHashcodes) {
            this.requiredHashCodes.add(hash);
        }
    }

    private static void checkPreconditions(int ... requiredHashcodes) {
        if (requiredHashcodes == null) {
            throw new NullPointerException("requiredHashcodes should not be null");
        }
        if (requiredHashcodes.length == 0) {
            throw new IllegalArgumentException("requiredHashcodes should not be empty");
        }
    }

    public Object execute(Object value, CsvContext context) {
        this.validateInputNotNull(value, context);
        int hash = value.hashCode();
        if (!this.requiredHashCodes.contains(hash)) {
            throw new SuperCsvConstraintViolationException(String.format("the hashcode of %d for value '%s' does not match any of the required hashcodes", hash, value), context, this);
        }
        return this.next.execute(value, context);
    }
}

