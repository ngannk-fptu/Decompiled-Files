/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import java.io.IOException;
import org.apache.batik.parser.DefaultPathHandler;
import org.apache.batik.parser.NumberParser;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathHandler;

public class PathParser
extends NumberParser {
    protected PathHandler pathHandler = DefaultPathHandler.INSTANCE;

    public void setPathHandler(PathHandler handler) {
        this.pathHandler = handler;
    }

    public PathHandler getPathHandler() {
        return this.pathHandler;
    }

    @Override
    protected void doParse() throws ParseException, IOException {
        this.pathHandler.startPath();
        this.current = this.reader.read();
        block25: while (true) {
            try {
                while (true) {
                    switch (this.current) {
                        case 9: 
                        case 10: 
                        case 13: 
                        case 32: {
                            this.current = this.reader.read();
                            break;
                        }
                        case 90: 
                        case 122: {
                            this.current = this.reader.read();
                            this.pathHandler.closePath();
                            break;
                        }
                        case 109: {
                            this.parsem();
                            break;
                        }
                        case 77: {
                            this.parseM();
                            break;
                        }
                        case 108: {
                            this.parsel();
                            break;
                        }
                        case 76: {
                            this.parseL();
                            break;
                        }
                        case 104: {
                            this.parseh();
                            break;
                        }
                        case 72: {
                            this.parseH();
                            break;
                        }
                        case 118: {
                            this.parsev();
                            break;
                        }
                        case 86: {
                            this.parseV();
                            break;
                        }
                        case 99: {
                            this.parsec();
                            break;
                        }
                        case 67: {
                            this.parseC();
                            break;
                        }
                        case 113: {
                            this.parseq();
                            break;
                        }
                        case 81: {
                            this.parseQ();
                            break;
                        }
                        case 115: {
                            this.parses();
                            break;
                        }
                        case 83: {
                            this.parseS();
                            break;
                        }
                        case 116: {
                            this.parset();
                            break;
                        }
                        case 84: {
                            this.parseT();
                            break;
                        }
                        case 97: {
                            this.parsea();
                            break;
                        }
                        case 65: {
                            this.parseA();
                            break;
                        }
                        case -1: {
                            break block25;
                        }
                        default: {
                            this.reportUnexpected(this.current);
                            break;
                        }
                    }
                }
            }
            catch (ParseException e) {
                this.errorHandler.error(e);
                this.skipSubPath();
                continue;
            }
            break;
        }
        this.skipSpaces();
        if (this.current != -1) {
            this.reportError("end.of.stream.expected", new Object[]{this.current});
        }
        this.pathHandler.endPath();
    }

    protected void parsem() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        float x = this.parseFloat();
        this.skipCommaSpaces();
        float y = this.parseFloat();
        this.pathHandler.movetoRel(x, y);
        boolean expectNumber = this.skipCommaSpaces2();
        this._parsel(expectNumber);
    }

    protected void parseM() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        float x = this.parseFloat();
        this.skipCommaSpaces();
        float y = this.parseFloat();
        this.pathHandler.movetoAbs(x, y);
        boolean expectNumber = this.skipCommaSpaces2();
        this._parseL(expectNumber);
    }

    protected void parsel() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        this._parsel(true);
    }

    protected void _parsel(boolean expectNumber) throws ParseException, IOException {
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                    return;
                }
                case 43: 
                case 45: 
                case 46: 
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
            float x = this.parseFloat();
            this.skipCommaSpaces();
            float y = this.parseFloat();
            this.pathHandler.linetoRel(x, y);
            expectNumber = this.skipCommaSpaces2();
        }
    }

    protected void parseL() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        this._parseL(true);
    }

    protected void _parseL(boolean expectNumber) throws ParseException, IOException {
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                    return;
                }
                case 43: 
                case 45: 
                case 46: 
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
            float x = this.parseFloat();
            this.skipCommaSpaces();
            float y = this.parseFloat();
            this.pathHandler.linetoAbs(x, y);
            expectNumber = this.skipCommaSpaces2();
        }
    }

    protected void parseh() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                    return;
                }
                case 43: 
                case 45: 
                case 46: 
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
            float x = this.parseFloat();
            this.pathHandler.linetoHorizontalRel(x);
            expectNumber = this.skipCommaSpaces2();
        }
    }

    protected void parseH() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                    return;
                }
                case 43: 
                case 45: 
                case 46: 
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
            float x = this.parseFloat();
            this.pathHandler.linetoHorizontalAbs(x);
            expectNumber = this.skipCommaSpaces2();
        }
    }

    protected void parsev() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                    return;
                }
                case 43: 
                case 45: 
                case 46: 
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
            float x = this.parseFloat();
            this.pathHandler.linetoVerticalRel(x);
            expectNumber = this.skipCommaSpaces2();
        }
    }

    protected void parseV() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                    return;
                }
                case 43: 
                case 45: 
                case 46: 
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
            float x = this.parseFloat();
            this.pathHandler.linetoVerticalAbs(x);
            expectNumber = this.skipCommaSpaces2();
        }
    }

    protected void parsec() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                    return;
                }
                case 43: 
                case 45: 
                case 46: 
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
            float x1 = this.parseFloat();
            this.skipCommaSpaces();
            float y1 = this.parseFloat();
            this.skipCommaSpaces();
            float x2 = this.parseFloat();
            this.skipCommaSpaces();
            float y2 = this.parseFloat();
            this.skipCommaSpaces();
            float x = this.parseFloat();
            this.skipCommaSpaces();
            float y = this.parseFloat();
            this.pathHandler.curvetoCubicRel(x1, y1, x2, y2, x, y);
            expectNumber = this.skipCommaSpaces2();
        }
    }

    protected void parseC() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                    return;
                }
                case 43: 
                case 45: 
                case 46: 
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
            float x1 = this.parseFloat();
            this.skipCommaSpaces();
            float y1 = this.parseFloat();
            this.skipCommaSpaces();
            float x2 = this.parseFloat();
            this.skipCommaSpaces();
            float y2 = this.parseFloat();
            this.skipCommaSpaces();
            float x = this.parseFloat();
            this.skipCommaSpaces();
            float y = this.parseFloat();
            this.pathHandler.curvetoCubicAbs(x1, y1, x2, y2, x, y);
            expectNumber = this.skipCommaSpaces2();
        }
    }

    protected void parseq() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                    return;
                }
                case 43: 
                case 45: 
                case 46: 
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
            float x1 = this.parseFloat();
            this.skipCommaSpaces();
            float y1 = this.parseFloat();
            this.skipCommaSpaces();
            float x = this.parseFloat();
            this.skipCommaSpaces();
            float y = this.parseFloat();
            this.pathHandler.curvetoQuadraticRel(x1, y1, x, y);
            expectNumber = this.skipCommaSpaces2();
        }
    }

    protected void parseQ() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                    return;
                }
                case 43: 
                case 45: 
                case 46: 
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
            float x1 = this.parseFloat();
            this.skipCommaSpaces();
            float y1 = this.parseFloat();
            this.skipCommaSpaces();
            float x = this.parseFloat();
            this.skipCommaSpaces();
            float y = this.parseFloat();
            this.pathHandler.curvetoQuadraticAbs(x1, y1, x, y);
            expectNumber = this.skipCommaSpaces2();
        }
    }

    protected void parses() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                    return;
                }
                case 43: 
                case 45: 
                case 46: 
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
            float x2 = this.parseFloat();
            this.skipCommaSpaces();
            float y2 = this.parseFloat();
            this.skipCommaSpaces();
            float x = this.parseFloat();
            this.skipCommaSpaces();
            float y = this.parseFloat();
            this.pathHandler.curvetoCubicSmoothRel(x2, y2, x, y);
            expectNumber = this.skipCommaSpaces2();
        }
    }

    protected void parseS() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                    return;
                }
                case 43: 
                case 45: 
                case 46: 
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
            float x2 = this.parseFloat();
            this.skipCommaSpaces();
            float y2 = this.parseFloat();
            this.skipCommaSpaces();
            float x = this.parseFloat();
            this.skipCommaSpaces();
            float y = this.parseFloat();
            this.pathHandler.curvetoCubicSmoothAbs(x2, y2, x, y);
            expectNumber = this.skipCommaSpaces2();
        }
    }

    protected void parset() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                    return;
                }
                case 43: 
                case 45: 
                case 46: 
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
            float x = this.parseFloat();
            this.skipCommaSpaces();
            float y = this.parseFloat();
            this.pathHandler.curvetoQuadraticSmoothRel(x, y);
            expectNumber = this.skipCommaSpaces2();
        }
    }

    protected void parseT() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                    return;
                }
                case 43: 
                case 45: 
                case 46: 
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
            float x = this.parseFloat();
            this.skipCommaSpaces();
            float y = this.parseFloat();
            this.pathHandler.curvetoQuadraticSmoothAbs(x, y);
            expectNumber = this.skipCommaSpaces2();
        }
    }

    protected void parsea() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            boolean sf;
            boolean laf;
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                    return;
                }
                case 43: 
                case 45: 
                case 46: 
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
            float rx = this.parseFloat();
            this.skipCommaSpaces();
            float ry = this.parseFloat();
            this.skipCommaSpaces();
            float ax = this.parseFloat();
            this.skipCommaSpaces();
            switch (this.current) {
                default: {
                    this.reportUnexpected(this.current);
                    return;
                }
                case 48: {
                    laf = false;
                    break;
                }
                case 49: {
                    laf = true;
                }
            }
            this.current = this.reader.read();
            this.skipCommaSpaces();
            switch (this.current) {
                default: {
                    this.reportUnexpected(this.current);
                    return;
                }
                case 48: {
                    sf = false;
                    break;
                }
                case 49: {
                    sf = true;
                }
            }
            this.current = this.reader.read();
            this.skipCommaSpaces();
            float x = this.parseFloat();
            this.skipCommaSpaces();
            float y = this.parseFloat();
            this.pathHandler.arcRel(rx, ry, ax, laf, sf, x, y);
            expectNumber = this.skipCommaSpaces2();
        }
    }

    protected void parseA() throws ParseException, IOException {
        this.current = this.reader.read();
        this.skipSpaces();
        boolean expectNumber = true;
        while (true) {
            boolean sf;
            boolean laf;
            switch (this.current) {
                default: {
                    if (expectNumber) {
                        this.reportUnexpected(this.current);
                    }
                    return;
                }
                case 43: 
                case 45: 
                case 46: 
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
            float rx = this.parseFloat();
            this.skipCommaSpaces();
            float ry = this.parseFloat();
            this.skipCommaSpaces();
            float ax = this.parseFloat();
            this.skipCommaSpaces();
            switch (this.current) {
                default: {
                    this.reportUnexpected(this.current);
                    return;
                }
                case 48: {
                    laf = false;
                    break;
                }
                case 49: {
                    laf = true;
                }
            }
            this.current = this.reader.read();
            this.skipCommaSpaces();
            switch (this.current) {
                default: {
                    this.reportUnexpected(this.current);
                    return;
                }
                case 48: {
                    sf = false;
                    break;
                }
                case 49: {
                    sf = true;
                }
            }
            this.current = this.reader.read();
            this.skipCommaSpaces();
            float x = this.parseFloat();
            this.skipCommaSpaces();
            float y = this.parseFloat();
            this.pathHandler.arcAbs(rx, ry, ax, laf, sf, x, y);
            expectNumber = this.skipCommaSpaces2();
        }
    }

    protected void skipSubPath() throws ParseException, IOException {
        while (true) {
            switch (this.current) {
                case -1: 
                case 77: 
                case 109: {
                    return;
                }
            }
            this.current = this.reader.read();
        }
    }

    protected void reportUnexpected(int ch) throws ParseException, IOException {
        this.reportUnexpectedCharacterError(this.current);
        this.skipSubPath();
    }

    protected boolean skipCommaSpaces2() throws IOException {
        block6: while (true) {
            switch (this.current) {
                default: {
                    break block6;
                }
                case 9: 
                case 10: 
                case 13: 
                case 32: {
                    this.current = this.reader.read();
                    continue block6;
                }
            }
            break;
        }
        if (this.current != 44) {
            return false;
        }
        block7: while (true) {
            this.current = this.reader.read();
            switch (this.current) {
                default: {
                    break block7;
                }
                case 9: 
                case 10: 
                case 13: 
                case 32: {
                    continue block7;
                }
            }
            break;
        }
        return true;
    }
}

