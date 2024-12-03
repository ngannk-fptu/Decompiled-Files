/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import java.io.IOException;
import org.apache.batik.parser.ClockHandler;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.TimingParser;

public class ClockParser
extends TimingParser {
    protected ClockHandler clockHandler;
    protected boolean parseOffset;

    public ClockParser(boolean parseOffset) {
        super(false, false);
        this.parseOffset = parseOffset;
    }

    public void setClockHandler(ClockHandler handler) {
        this.clockHandler = handler;
    }

    public ClockHandler getClockHandler() {
        return this.clockHandler;
    }

    @Override
    protected void doParse() throws ParseException, IOException {
        float clockValue;
        this.current = this.reader.read();
        float f = clockValue = this.parseOffset ? this.parseOffset() : this.parseClockValue();
        if (this.current != -1) {
            this.reportError("end.of.stream.expected", new Object[]{this.current});
        }
        if (this.clockHandler != null) {
            this.clockHandler.clockValue(clockValue);
        }
    }
}

