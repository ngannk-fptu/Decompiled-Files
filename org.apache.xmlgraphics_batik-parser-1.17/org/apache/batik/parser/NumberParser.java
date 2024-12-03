/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import java.io.IOException;
import org.apache.batik.parser.AbstractParser;
import org.apache.batik.parser.ParseException;

public abstract class NumberParser
extends AbstractParser {
    private static final double[] pow10 = new double[128];

    protected float parseFloat() throws ParseException, IOException {
        int mant = 0;
        int mantDig = 0;
        boolean mantPos = true;
        boolean mantRead = false;
        int exp = 0;
        int expDig = 0;
        int expAdj = 0;
        boolean expPos = true;
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
                return 0.0f;
            }
            case 46: {
                break;
            }
            case 48: {
                mantRead = true;
                block50: while (true) {
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
                            break block50;
                        }
                        case 46: 
                        case 69: 
                        case 101: {
                            break block4;
                        }
                        default: {
                            return 0.0f;
                        }
                        case 48: {
                            continue block50;
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
            block17 : switch (this.current) {
                default: {
                    if (mantRead) break;
                    this.reportUnexpectedCharacterError(this.current);
                    return 0.0f;
                }
                case 48: {
                    if (mantDig == 0) {
                        block52: while (true) {
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
                                    break block52;
                                }
                                default: {
                                    if (mantRead) break block17;
                                    return 0.0f;
                                }
                                case 48: {
                                    continue block52;
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
                                break block17;
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
        block28 : switch (this.current) {
            case 69: 
            case 101: {
                this.current = this.reader.read();
                switch (this.current) {
                    default: {
                        this.reportUnexpectedCharacterError(this.current);
                        return 0.0f;
                    }
                    case 45: {
                        expPos = false;
                    }
                    case 43: {
                        this.current = this.reader.read();
                        switch (this.current) {
                            default: {
                                this.reportUnexpectedCharacterError(this.current);
                                return 0.0f;
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
                        block54: while (true) {
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
                                    break block54;
                                }
                                default: {
                                    break block28;
                                }
                                case 48: {
                                    continue block54;
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
                                    break block28;
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
        return NumberParser.buildFloat(mant, exp);
    }

    public static float buildFloat(int mant, int exp) {
        if (exp < -125 || mant == 0) {
            return 0.0f;
        }
        if (exp >= 128) {
            return mant > 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
        }
        if (exp == 0) {
            return mant;
        }
        if (mant >= 0x4000000) {
            ++mant;
        }
        return (float)(exp > 0 ? (double)mant * pow10[exp] : (double)mant / pow10[-exp]);
    }

    static {
        for (int i = 0; i < pow10.length; ++i) {
            NumberParser.pow10[i] = Math.pow(10.0, i);
        }
    }
}

