/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import java.io.IOException;
import org.apache.batik.parser.AngleHandler;
import org.apache.batik.parser.DefaultAngleHandler;
import org.apache.batik.parser.NumberParser;
import org.apache.batik.parser.ParseException;

public class AngleParser
extends NumberParser {
    protected AngleHandler angleHandler = DefaultAngleHandler.INSTANCE;

    public void setAngleHandler(AngleHandler handler) {
        this.angleHandler = handler;
    }

    public AngleHandler getAngleHandler() {
        return this.angleHandler;
    }

    @Override
    protected void doParse() throws ParseException, IOException {
        this.angleHandler.startAngle();
        this.current = this.reader.read();
        this.skipSpaces();
        try {
            float f = this.parseFloat();
            this.angleHandler.angleValue(f);
            if (this.current != -1) {
                block1 : switch (this.current) {
                    case 9: 
                    case 10: 
                    case 13: 
                    case 32: {
                        break;
                    }
                    default: {
                        switch (this.current) {
                            case 100: {
                                this.current = this.reader.read();
                                if (this.current != 101) {
                                    this.reportCharacterExpectedError('e', this.current);
                                    break block1;
                                }
                                this.current = this.reader.read();
                                if (this.current != 103) {
                                    this.reportCharacterExpectedError('g', this.current);
                                    break block1;
                                }
                                this.angleHandler.deg();
                                this.current = this.reader.read();
                                break block1;
                            }
                            case 103: {
                                this.current = this.reader.read();
                                if (this.current != 114) {
                                    this.reportCharacterExpectedError('r', this.current);
                                    break block1;
                                }
                                this.current = this.reader.read();
                                if (this.current != 97) {
                                    this.reportCharacterExpectedError('a', this.current);
                                    break block1;
                                }
                                this.current = this.reader.read();
                                if (this.current != 100) {
                                    this.reportCharacterExpectedError('d', this.current);
                                    break block1;
                                }
                                this.angleHandler.grad();
                                this.current = this.reader.read();
                                break block1;
                            }
                            case 114: {
                                this.current = this.reader.read();
                                if (this.current != 97) {
                                    this.reportCharacterExpectedError('a', this.current);
                                    break block1;
                                }
                                this.current = this.reader.read();
                                if (this.current != 100) {
                                    this.reportCharacterExpectedError('d', this.current);
                                    break block1;
                                }
                                this.angleHandler.rad();
                                this.current = this.reader.read();
                                break block1;
                            }
                        }
                        this.reportUnexpectedCharacterError(this.current);
                    }
                }
            }
            this.skipSpaces();
            if (this.current != -1) {
                this.reportError("end.of.stream.expected", new Object[]{this.current});
            }
        }
        catch (NumberFormatException e) {
            this.reportUnexpectedCharacterError(this.current);
        }
        this.angleHandler.endAngle();
    }
}

