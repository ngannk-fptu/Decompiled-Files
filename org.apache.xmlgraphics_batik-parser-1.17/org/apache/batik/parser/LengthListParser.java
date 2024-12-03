/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import java.io.IOException;
import org.apache.batik.parser.DefaultLengthListHandler;
import org.apache.batik.parser.LengthListHandler;
import org.apache.batik.parser.LengthParser;
import org.apache.batik.parser.ParseException;

public class LengthListParser
extends LengthParser {
    public LengthListParser() {
        this.lengthHandler = DefaultLengthListHandler.INSTANCE;
    }

    public void setLengthListHandler(LengthListHandler handler) {
        this.lengthHandler = handler;
    }

    public LengthListHandler getLengthListHandler() {
        return (LengthListHandler)this.lengthHandler;
    }

    @Override
    protected void doParse() throws ParseException, IOException {
        ((LengthListHandler)this.lengthHandler).startLengthList();
        this.current = this.reader.read();
        this.skipSpaces();
        try {
            do {
                this.lengthHandler.startLength();
                this.parseLength();
                this.lengthHandler.endLength();
                this.skipCommaSpaces();
            } while (this.current != -1);
        }
        catch (NumberFormatException e) {
            this.reportUnexpectedCharacterError(this.current);
        }
        ((LengthListHandler)this.lengthHandler).endLengthList();
    }
}

