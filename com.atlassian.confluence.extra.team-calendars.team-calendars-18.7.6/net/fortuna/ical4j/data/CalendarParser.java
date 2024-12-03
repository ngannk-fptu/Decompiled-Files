/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import net.fortuna.ical4j.data.ContentHandler;
import net.fortuna.ical4j.data.ParserException;

public interface CalendarParser {
    public void parse(InputStream var1, ContentHandler var2) throws IOException, ParserException;

    public void parse(Reader var1, ContentHandler var2) throws IOException, ParserException;
}

