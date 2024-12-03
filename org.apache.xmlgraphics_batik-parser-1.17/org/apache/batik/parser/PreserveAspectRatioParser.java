/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import java.io.IOException;
import org.apache.batik.parser.AbstractParser;
import org.apache.batik.parser.DefaultPreserveAspectRatioHandler;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PreserveAspectRatioHandler;

public class PreserveAspectRatioParser
extends AbstractParser {
    protected PreserveAspectRatioHandler preserveAspectRatioHandler = DefaultPreserveAspectRatioHandler.INSTANCE;

    public void setPreserveAspectRatioHandler(PreserveAspectRatioHandler handler) {
        this.preserveAspectRatioHandler = handler;
    }

    public PreserveAspectRatioHandler getPreserveAspectRatioHandler() {
        return this.preserveAspectRatioHandler;
    }

    @Override
    protected void doParse() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        this.parsePreserveAspectRatio();
    }

    protected void parsePreserveAspectRatio() throws ParseException, IOException {
        this.preserveAspectRatioHandler.startPreserveAspectRatio();
        block0 : switch (this.current) {
            case 110: {
                this.current = this.reader.read();
                if (this.current != 111) {
                    this.reportCharacterExpectedError('o', this.current);
                    this.skipIdentifier();
                    break;
                }
                this.current = this.reader.read();
                if (this.current != 110) {
                    this.reportCharacterExpectedError('o', this.current);
                    this.skipIdentifier();
                    break;
                }
                this.current = this.reader.read();
                if (this.current != 101) {
                    this.reportCharacterExpectedError('e', this.current);
                    this.skipIdentifier();
                    break;
                }
                this.current = this.reader.read();
                this.skipSpaces();
                this.preserveAspectRatioHandler.none();
                break;
            }
            case 120: {
                this.current = this.reader.read();
                if (this.current != 77) {
                    this.reportCharacterExpectedError('M', this.current);
                    this.skipIdentifier();
                    break;
                }
                this.current = this.reader.read();
                switch (this.current) {
                    case 97: {
                        this.current = this.reader.read();
                        if (this.current != 120) {
                            this.reportCharacterExpectedError('x', this.current);
                            this.skipIdentifier();
                            break block0;
                        }
                        this.current = this.reader.read();
                        if (this.current != 89) {
                            this.reportCharacterExpectedError('Y', this.current);
                            this.skipIdentifier();
                            break block0;
                        }
                        this.current = this.reader.read();
                        if (this.current != 77) {
                            this.reportCharacterExpectedError('M', this.current);
                            this.skipIdentifier();
                            break block0;
                        }
                        this.current = this.reader.read();
                        switch (this.current) {
                            case 97: {
                                this.current = this.reader.read();
                                if (this.current != 120) {
                                    this.reportCharacterExpectedError('x', this.current);
                                    this.skipIdentifier();
                                    break block0;
                                }
                                this.preserveAspectRatioHandler.xMaxYMax();
                                this.current = this.reader.read();
                                break block0;
                            }
                            case 105: {
                                this.current = this.reader.read();
                                switch (this.current) {
                                    case 100: {
                                        this.preserveAspectRatioHandler.xMaxYMid();
                                        this.current = this.reader.read();
                                        break block0;
                                    }
                                    case 110: {
                                        this.preserveAspectRatioHandler.xMaxYMin();
                                        this.current = this.reader.read();
                                        break block0;
                                    }
                                }
                                this.reportUnexpectedCharacterError(this.current);
                                this.skipIdentifier();
                                break block0;
                            }
                        }
                        break block0;
                    }
                    case 105: {
                        this.current = this.reader.read();
                        switch (this.current) {
                            case 100: {
                                this.current = this.reader.read();
                                if (this.current != 89) {
                                    this.reportCharacterExpectedError('Y', this.current);
                                    this.skipIdentifier();
                                    break block0;
                                }
                                this.current = this.reader.read();
                                if (this.current != 77) {
                                    this.reportCharacterExpectedError('M', this.current);
                                    this.skipIdentifier();
                                    break block0;
                                }
                                this.current = this.reader.read();
                                switch (this.current) {
                                    case 97: {
                                        this.current = this.reader.read();
                                        if (this.current != 120) {
                                            this.reportCharacterExpectedError('x', this.current);
                                            this.skipIdentifier();
                                            break block0;
                                        }
                                        this.preserveAspectRatioHandler.xMidYMax();
                                        this.current = this.reader.read();
                                        break block0;
                                    }
                                    case 105: {
                                        this.current = this.reader.read();
                                        switch (this.current) {
                                            case 100: {
                                                this.preserveAspectRatioHandler.xMidYMid();
                                                this.current = this.reader.read();
                                                break block0;
                                            }
                                            case 110: {
                                                this.preserveAspectRatioHandler.xMidYMin();
                                                this.current = this.reader.read();
                                                break block0;
                                            }
                                        }
                                        this.reportUnexpectedCharacterError(this.current);
                                        this.skipIdentifier();
                                        break block0;
                                    }
                                }
                                break block0;
                            }
                            case 110: {
                                this.current = this.reader.read();
                                if (this.current != 89) {
                                    this.reportCharacterExpectedError('Y', this.current);
                                    this.skipIdentifier();
                                    break block0;
                                }
                                this.current = this.reader.read();
                                if (this.current != 77) {
                                    this.reportCharacterExpectedError('M', this.current);
                                    this.skipIdentifier();
                                    break block0;
                                }
                                this.current = this.reader.read();
                                switch (this.current) {
                                    case 97: {
                                        this.current = this.reader.read();
                                        if (this.current != 120) {
                                            this.reportCharacterExpectedError('x', this.current);
                                            this.skipIdentifier();
                                            break block0;
                                        }
                                        this.preserveAspectRatioHandler.xMinYMax();
                                        this.current = this.reader.read();
                                        break block0;
                                    }
                                    case 105: {
                                        this.current = this.reader.read();
                                        switch (this.current) {
                                            case 100: {
                                                this.preserveAspectRatioHandler.xMinYMid();
                                                this.current = this.reader.read();
                                                break block0;
                                            }
                                            case 110: {
                                                this.preserveAspectRatioHandler.xMinYMin();
                                                this.current = this.reader.read();
                                                break block0;
                                            }
                                        }
                                        this.reportUnexpectedCharacterError(this.current);
                                        this.skipIdentifier();
                                        break block0;
                                    }
                                }
                                break block0;
                            }
                        }
                        this.reportUnexpectedCharacterError(this.current);
                        this.skipIdentifier();
                        break block0;
                    }
                }
                this.reportUnexpectedCharacterError(this.current);
                this.skipIdentifier();
                break;
            }
            default: {
                if (this.current == -1) break;
                this.reportUnexpectedCharacterError(this.current);
                this.skipIdentifier();
            }
        }
        this.skipCommaSpaces();
        switch (this.current) {
            case 109: {
                this.current = this.reader.read();
                if (this.current != 101) {
                    this.reportCharacterExpectedError('e', this.current);
                    this.skipIdentifier();
                    break;
                }
                this.current = this.reader.read();
                if (this.current != 101) {
                    this.reportCharacterExpectedError('e', this.current);
                    this.skipIdentifier();
                    break;
                }
                this.current = this.reader.read();
                if (this.current != 116) {
                    this.reportCharacterExpectedError('t', this.current);
                    this.skipIdentifier();
                    break;
                }
                this.preserveAspectRatioHandler.meet();
                this.current = this.reader.read();
                break;
            }
            case 115: {
                this.current = this.reader.read();
                if (this.current != 108) {
                    this.reportCharacterExpectedError('l', this.current);
                    this.skipIdentifier();
                    break;
                }
                this.current = this.reader.read();
                if (this.current != 105) {
                    this.reportCharacterExpectedError('i', this.current);
                    this.skipIdentifier();
                    break;
                }
                this.current = this.reader.read();
                if (this.current != 99) {
                    this.reportCharacterExpectedError('c', this.current);
                    this.skipIdentifier();
                    break;
                }
                this.current = this.reader.read();
                if (this.current != 101) {
                    this.reportCharacterExpectedError('e', this.current);
                    this.skipIdentifier();
                    break;
                }
                this.preserveAspectRatioHandler.slice();
                this.current = this.reader.read();
                break;
            }
            default: {
                if (this.current == -1) break;
                this.reportUnexpectedCharacterError(this.current);
                this.skipIdentifier();
            }
        }
        this.skipSpaces();
        if (this.current != -1) {
            this.reportError("end.of.stream.expected", new Object[]{this.current});
        }
        this.preserveAspectRatioHandler.endPreserveAspectRatio();
    }

    protected void skipIdentifier() throws IOException {
        block3: while (true) {
            this.current = this.reader.read();
            switch (this.current) {
                case 9: 
                case 10: 
                case 13: 
                case 32: {
                    this.current = this.reader.read();
                    break block3;
                }
                default: {
                    if (this.current != -1) continue block3;
                    break block3;
                }
            }
            break;
        }
    }
}

