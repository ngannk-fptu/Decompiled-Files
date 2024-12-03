/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import java.io.IOException;
import org.apache.batik.parser.AbstractParser;
import org.apache.batik.parser.DefaultLengthHandler;
import org.apache.batik.parser.LengthHandler;
import org.apache.batik.parser.NumberParser;
import org.apache.batik.parser.ParseException;

public class LengthParser
extends AbstractParser {
    protected LengthHandler lengthHandler = DefaultLengthHandler.INSTANCE;

    public void setLengthHandler(LengthHandler handler) {
        this.lengthHandler = handler;
    }

    public LengthHandler getLengthHandler() {
        return this.lengthHandler;
    }

    @Override
    protected void doParse() throws ParseException, IOException {
        this.lengthHandler.startLength();
        this.current = this.reader.read();
        this.skipSpaces();
        this.parseLength();
        this.skipSpaces();
        if (this.current != -1) {
            this.reportError("end.of.stream.expected", new Object[]{this.current});
        }
        this.lengthHandler.endLength();
    }

    protected void parseLength() throws ParseException, IOException {
        int mant = 0;
        int mantDig = 0;
        boolean mantPos = true;
        boolean mantRead = false;
        int exp = 0;
        int expDig = 0;
        int expAdj = 0;
        boolean expPos = true;
        int unitState = 0;
        switch (this.current) {
            case 45: {
                mantPos = false;
            }
            case 43: {
                this.current = this.reader.read();
            }
        }
        block4 : switch (this.current) {
            default: {
                this.reportUnexpectedCharacterError(this.current);
                return;
            }
            case 46: {
                break;
            }
            case 48: {
                mantRead = true;
                block73: while (true) {
                    this.current = this.reader.read();
                    switch (this.current) {
                        case 49: 
                        case 50: 
                        case 51: 
                        case 52: 
                        case 53: 
                        case 54: 
                        case 55: 
                        case 56: 
                        case 57: {
                            break block73;
                        }
                        default: {
                            break block4;
                        }
                        case 48: {
                            continue block73;
                        }
                    }
                    break;
                }
            }
            case 49: 
            case 50: 
            case 51: 
            case 52: 
            case 53: 
            case 54: 
            case 55: 
            case 56: 
            case 57: {
                mantRead = true;
                while (true) {
                    if (mantDig < 9) {
                        ++mantDig;
                        mant = mant * 10 + (this.current - 48);
                    } else {
                        ++expAdj;
                    }
                    this.current = this.reader.read();
                    switch (this.current) {
                        default: {
                            break block4;
                        }
                        case 48: 
                        case 49: 
                        case 50: 
                        case 51: 
                        case 52: 
                        case 53: 
                        case 54: 
                        case 55: 
                        case 56: 
                        case 57: 
                    }
                }
            }
        }
        if (this.current == 46) {
            this.current = this.reader.read();
            block16 : switch (this.current) {
                default: {
                    if (mantRead) break;
                    this.reportUnexpectedCharacterError(this.current);
                    return;
                }
                case 48: {
                    if (mantDig == 0) {
                        block75: while (true) {
                            this.current = this.reader.read();
                            --expAdj;
                            switch (this.current) {
                                case 49: 
                                case 50: 
                                case 51: 
                                case 52: 
                                case 53: 
                                case 54: 
                                case 55: 
                                case 56: 
                                case 57: {
                                    break block75;
                                }
                                default: {
                                    break block16;
                                }
                                case 48: {
                                    continue block75;
                                }
                            }
                            break;
                        }
                    }
                }
                case 49: 
                case 50: 
                case 51: 
                case 52: 
                case 53: 
                case 54: 
                case 55: 
                case 56: 
                case 57: {
                    while (true) {
                        if (mantDig < 9) {
                            ++mantDig;
                            mant = mant * 10 + (this.current - 48);
                            --expAdj;
                        }
                        this.current = this.reader.read();
                        switch (this.current) {
                            default: {
                                break block16;
                            }
                            case 48: 
                            case 49: 
                            case 50: 
                            case 51: 
                            case 52: 
                            case 53: 
                            case 54: 
                            case 55: 
                            case 56: 
                            case 57: 
                        }
                    }
                }
            }
        }
        boolean le = false;
        block27 : switch (this.current) {
            case 101: {
                le = true;
            }
            case 69: {
                this.current = this.reader.read();
                switch (this.current) {
                    default: {
                        this.reportUnexpectedCharacterError(this.current);
                        return;
                    }
                    case 109: {
                        if (!le) {
                            this.reportUnexpectedCharacterError(this.current);
                            return;
                        }
                        unitState = 1;
                        break block27;
                    }
                    case 120: {
                        if (!le) {
                            this.reportUnexpectedCharacterError(this.current);
                            return;
                        }
                        unitState = 2;
                        break block27;
                    }
                    case 45: {
                        expPos = false;
                    }
                    case 43: {
                        this.current = this.reader.read();
                        switch (this.current) {
                            default: {
                                this.reportUnexpectedCharacterError(this.current);
                                return;
                            }
                            case 48: 
                            case 49: 
                            case 50: 
                            case 51: 
                            case 52: 
                            case 53: 
                            case 54: 
                            case 55: 
                            case 56: 
                            case 57: 
                        }
                    }
                    case 48: 
                    case 49: 
                    case 50: 
                    case 51: 
                    case 52: 
                    case 53: 
                    case 54: 
                    case 55: 
                    case 56: 
                    case 57: 
                }
                switch (this.current) {
                    case 48: {
                        block77: while (true) {
                            this.current = this.reader.read();
                            switch (this.current) {
                                case 49: 
                                case 50: 
                                case 51: 
                                case 52: 
                                case 53: 
                                case 54: 
                                case 55: 
                                case 56: 
                                case 57: {
                                    break block77;
                                }
                                default: {
                                    break block27;
                                }
                                case 48: {
                                    continue block77;
                                }
                            }
                            break;
                        }
                    }
                    case 49: 
                    case 50: 
                    case 51: 
                    case 52: 
                    case 53: 
                    case 54: 
                    case 55: 
                    case 56: 
                    case 57: {
                        while (true) {
                            if (expDig < 3) {
                                ++expDig;
                                exp = exp * 10 + (this.current - 48);
                            }
                            this.current = this.reader.read();
                            switch (this.current) {
                                default: {
                                    break block27;
                                }
                                case 48: 
                                case 49: 
                                case 50: 
                                case 51: 
                                case 52: 
                                case 53: 
                                case 54: 
                                case 55: 
                                case 56: 
                                case 57: 
                            }
                        }
                    }
                }
            }
        }
        if (!expPos) {
            exp = -exp;
        }
        exp += expAdj;
        if (!mantPos) {
            mant = -mant;
        }
        this.lengthHandler.lengthValue(NumberParser.buildFloat(mant, exp));
        switch (unitState) {
            case 1: {
                this.lengthHandler.em();
                this.current = this.reader.read();
                return;
            }
            case 2: {
                this.lengthHandler.ex();
                this.current = this.reader.read();
                return;
            }
        }
        block56 : switch (this.current) {
            case 101: {
                this.current = this.reader.read();
                switch (this.current) {
                    case 109: {
                        this.lengthHandler.em();
                        this.current = this.reader.read();
                        break block56;
                    }
                    case 120: {
                        this.lengthHandler.ex();
                        this.current = this.reader.read();
                        break block56;
                    }
                }
                this.reportUnexpectedCharacterError(this.current);
                break;
            }
            case 112: {
                this.current = this.reader.read();
                switch (this.current) {
                    case 99: {
                        this.lengthHandler.pc();
                        this.current = this.reader.read();
                        break block56;
                    }
                    case 116: {
                        this.lengthHandler.pt();
                        this.current = this.reader.read();
                        break block56;
                    }
                    case 120: {
                        this.lengthHandler.px();
                        this.current = this.reader.read();
                        break block56;
                    }
                }
                this.reportUnexpectedCharacterError(this.current);
                break;
            }
            case 105: {
                this.current = this.reader.read();
                if (this.current != 110) {
                    this.reportCharacterExpectedError('n', this.current);
                    break;
                }
                this.lengthHandler.in();
                this.current = this.reader.read();
                break;
            }
            case 99: {
                this.current = this.reader.read();
                if (this.current != 109) {
                    this.reportCharacterExpectedError('m', this.current);
                    break;
                }
                this.lengthHandler.cm();
                this.current = this.reader.read();
                break;
            }
            case 109: {
                this.current = this.reader.read();
                if (this.current != 109) {
                    this.reportCharacterExpectedError('m', this.current);
                    break;
                }
                this.lengthHandler.mm();
                this.current = this.reader.read();
                break;
            }
            case 37: {
                this.lengthHandler.percentage();
                this.current = this.reader.read();
            }
        }
    }
}

