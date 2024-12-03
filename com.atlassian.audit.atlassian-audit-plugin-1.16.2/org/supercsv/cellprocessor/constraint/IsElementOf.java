/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.cellprocessor.constraint;

import java.util.Collection;
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
public class IsElementOf
extends CellProcessorAdaptor
implements BoolCellProcessor,
DateCellProcessor,
DoubleCellProcessor,
LongCellProcessor,
StringCellProcessor {
    private final Collection<Object> collection;

    public IsElementOf(Collection<Object> collection) {
        IsElementOf.checkPreconditions(collection);
        this.collection = collection;
    }

    public IsElementOf(Collection<Object> collection, CellProcessor next) {
        super(next);
        IsElementOf.checkPreconditions(collection);
        this.collection = collection;
    }

    private static void checkPreconditions(Collection<Object> collection) {
        if (collection == null) {
            throw new NullPointerException("collection should not be null");
        }
    }

    @Override
    public Object execute(Object value, CsvContext context) {
        if (!this.collection.contains(value)) {
            throw new SuperCsvConstraintViolationException(String.format("'%s' is not an element of the supplied Collection", value), context, this);
        }
        return this.next.execute(value, context);
    }
}

