/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.cellprocessor;

import java.util.Map;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.BoolCellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class HashMapper
extends CellProcessorAdaptor
implements BoolCellProcessor,
DateCellProcessor,
DoubleCellProcessor,
LongCellProcessor,
StringCellProcessor {
    private final Map<Object, Object> mapping;
    private final Object defaultValue;

    public HashMapper(Map<Object, Object> mapping) {
        this(mapping, (Object)null);
    }

    public HashMapper(Map<Object, Object> mapping, Object defaultValue) {
        HashMapper.checkPreconditions(mapping);
        this.mapping = mapping;
        this.defaultValue = defaultValue;
    }

    public HashMapper(Map<Object, Object> mapping, BoolCellProcessor next) {
        this(mapping, null, next);
    }

    public HashMapper(Map<Object, Object> mapping, Object defaultValue, BoolCellProcessor next) {
        super(next);
        HashMapper.checkPreconditions(mapping);
        this.mapping = mapping;
        this.defaultValue = defaultValue;
    }

    private static void checkPreconditions(Map<Object, Object> mapping) {
        if (mapping == null) {
            throw new NullPointerException("mapping should not be null");
        }
        if (mapping.isEmpty()) {
            throw new IllegalArgumentException("mapping should not be empty");
        }
    }

    @Override
    public Object execute(Object value, CsvContext context) {
        this.validateInputNotNull(value, context);
        Object result = this.mapping.get(value);
        if (result == null) {
            result = this.defaultValue;
        }
        return this.next.execute(result, context);
    }
}

