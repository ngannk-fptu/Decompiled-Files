/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import java.io.IOException;
import org.apache.batik.parser.DefaultNumberListHandler;
import org.apache.batik.parser.NumberListHandler;
import org.apache.batik.parser.NumberParser;
import org.apache.batik.parser.ParseException;

public class NumberListParser
extends NumberParser {
    protected NumberListHandler numberListHandler = DefaultNumberListHandler.INSTANCE;

    public void setNumberListHandler(NumberListHandler handler) {
        this.numberListHandler = handler;
    }

    public NumberListHandler getNumberListHandler() {
        return this.numberListHandler;
    }

    @Override
    protected void doParse() throws ParseException, IOException {
        this.numberListHandler.startNumberList();
        this.current = this.reader.read();
        this.skipSpaces();
        try {
            do {
                this.numberListHandler.startNumber();
                float f = this.parseFloat();
                this.numberListHandler.numberValue(f);
                this.numberListHandler.endNumber();
                this.skipCommaSpaces();
            } while (this.current != -1);
        }
        catch (NumberFormatException e) {
            this.reportUnexpectedCharacterError(this.current);
        }
        this.numberListHandler.endNumberList();
    }
}

