/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import java.io.IOException;
import org.apache.batik.parser.LengthListHandler;
import org.apache.batik.parser.LengthListParser;
import org.apache.batik.parser.ParseException;

public class LengthPairListParser
extends LengthListParser {
    @Override
    protected void doParse() throws ParseException, IOException {
        ((LengthListHandler)this.lengthHandler).startLengthList();
        this.current = this.reader.read();
        this.skipSpaces();
        try {
            while (true) {
                this.lengthHandler.startLength();
                this.parseLength();
                this.lengthHandler.endLength();
                this.skipCommaSpaces();
                this.lengthHandler.startLength();
                this.parseLength();
                this.lengthHandler.endLength();
                this.skipSpaces();
                if (this.current != -1) {
                    if (this.current != 59) {
                        this.reportUnexpectedCharacterError(this.current);
                    }
                    this.current = this.reader.read();
                    this.skipSpaces();
                    continue;
                }
                break;
            }
        }
        catch (NumberFormatException e) {
            this.reportUnexpectedCharacterError(this.current);
        }
        ((LengthListHandler)this.lengthHandler).endLengthList();
    }
}

