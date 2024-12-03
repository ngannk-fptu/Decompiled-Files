/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.cellprocessor;

import java.util.regex.Pattern;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.BoolCellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

public class StrReplace
extends CellProcessorAdaptor
implements BoolCellProcessor,
DateCellProcessor,
DoubleCellProcessor,
LongCellProcessor,
StringCellProcessor {
    private final Pattern regexPattern;
    private final String replacement;

    public StrReplace(String regex, String replacement) {
        StrReplace.checkPreconditions(regex, replacement);
        this.regexPattern = Pattern.compile(regex);
        this.replacement = replacement;
    }

    public StrReplace(String regex, String replacement, StringCellProcessor next) {
        super(next);
        StrReplace.checkPreconditions(regex, replacement);
        this.regexPattern = Pattern.compile(regex);
        this.replacement = replacement;
    }

    private static void checkPreconditions(String regex, String replacement) {
        if (regex == null) {
            throw new NullPointerException("regex should not be null");
        }
        if (regex.length() == 0) {
            throw new IllegalArgumentException("regex should not be empty");
        }
        if (replacement == null) {
            throw new NullPointerException("replacement should not be null");
        }
    }

    public Object execute(Object value, CsvContext context) {
        this.validateInputNotNull(value, context);
        String result = this.regexPattern.matcher(value.toString()).replaceAll(this.replacement);
        return this.next.execute(result, context);
    }
}

