/*
 * Decompiled with CFR 0.152.
 */
package org.apache.juli;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class VerbatimFormatter
extends Formatter {
    @Override
    public String format(LogRecord record) {
        return record.getMessage() + System.lineSeparator();
    }
}

