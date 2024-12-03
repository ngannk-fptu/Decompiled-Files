/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.data;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import net.fortuna.ical4j.data.AbstractOutputter;
import net.fortuna.ical4j.data.FoldingWriter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.validate.ValidationException;

public class CalendarOutputter
extends AbstractOutputter {
    public CalendarOutputter() {
    }

    public CalendarOutputter(boolean validating) {
        super(validating);
    }

    public CalendarOutputter(boolean validating, int foldLength) {
        super(validating, foldLength);
    }

    public final void output(Calendar calendar, OutputStream out) throws IOException, ValidationException {
        this.output(calendar, new OutputStreamWriter(out, DEFAULT_CHARSET));
    }

    public final void output(Calendar calendar, Writer out) throws IOException, ValidationException {
        if (this.isValidating()) {
            calendar.validate();
        }
        try (FoldingWriter writer = new FoldingWriter(out, this.foldLength);){
            writer.write(calendar.toString());
        }
    }
}

