/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import net.fortuna.ical4j.data.AbstractOutputter;
import net.fortuna.ical4j.data.FoldingWriter;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.VCard;

public class VCardOutputter
extends AbstractOutputter {
    public VCardOutputter() {
    }

    public VCardOutputter(boolean validating) {
        super(validating);
    }

    public VCardOutputter(boolean validating, int foldLength) {
        super(validating, foldLength);
    }

    public final void output(VCard card, OutputStream out) throws IOException, ValidationException {
        this.output(card, new OutputStreamWriter(out, DEFAULT_CHARSET));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void output(VCard card, Writer out) throws IOException, ValidationException {
        if (this.isValidating()) {
            card.validate();
        }
        FoldingWriter writer = new FoldingWriter(out, this.foldLength);
        try {
            writer.write(card.toString());
        }
        finally {
            writer.close();
        }
    }
}

