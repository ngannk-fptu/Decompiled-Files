/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import java.io.IOException;
import org.apache.batik.parser.DefaultTransformListHandler;
import org.apache.batik.parser.NumberParser;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.TransformListHandler;

public class TransformListParser
extends NumberParser {
    protected TransformListHandler transformListHandler = DefaultTransformListHandler.INSTANCE;

    public void setTransformListHandler(TransformListHandler handler) {
        this.transformListHandler = handler;
    }

    public TransformListHandler getTransformListHandler() {
        return this.transformListHandler;
    }

    @Override
    protected void doParse() throws ParseException, IOException {
        this.transformListHandler.startTransformList();
        block14: while (true) {
            try {
                while (true) {
                    this.current = this.reader.read();
                    block1 : switch (this.current) {
                        case 9: 
                        case 10: 
                        case 13: 
                        case 32: 
                        case 44: {
                            break;
                        }
                        case 109: {
                            this.parseMatrix();
                            break;
                        }
                        case 114: {
                            this.parseRotate();
                            break;
                        }
                        case 116: {
                            this.parseTranslate();
                            break;
                        }
                        case 115: {
                            this.current = this.reader.read();
                            switch (this.current) {
                                case 99: {
                                    this.parseScale();
                                    break block1;
                                }
                                case 107: {
                                    this.parseSkew();
                                    break block1;
                                }
                            }
                            this.reportUnexpectedCharacterError(this.current);
                            this.skipTransform();
                            break;
                        }
                        case -1: {
                            break block14;
                        }
                        default: {
                            this.reportUnexpectedCharacterError(this.current);
                            this.skipTransform();
                            break;
                        }
                    }
                }
            }
            catch (ParseException e) {
                this.errorHandler.error(e);
                this.skipTransform();
                continue;
            }
            break;
        }
        this.skipSpaces();
        if (this.current != -1) {
            this.reportError("end.of.stream.expected", new Object[]{this.current});
        }
        this.transformListHandler.endTransformList();
    }

    protected void parseMatrix() throws ParseException, IOException {
        this.current = this.reader.read();
        if (this.current != 97) {
            this.reportCharacterExpectedError('a', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 116) {
            this.reportCharacterExpectedError('t', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 114) {
            this.reportCharacterExpectedError('r', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 105) {
            this.reportCharacterExpectedError('i', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 120) {
            this.reportCharacterExpectedError('x', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        this.skipSpaces();
        if (this.current != 40) {
            this.reportCharacterExpectedError('(', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        this.skipSpaces();
        float a = this.parseFloat();
        this.skipCommaSpaces();
        float b = this.parseFloat();
        this.skipCommaSpaces();
        float c = this.parseFloat();
        this.skipCommaSpaces();
        float d = this.parseFloat();
        this.skipCommaSpaces();
        float e = this.parseFloat();
        this.skipCommaSpaces();
        float f = this.parseFloat();
        this.skipSpaces();
        if (this.current != 41) {
            this.reportCharacterExpectedError(')', this.current);
            this.skipTransform();
            return;
        }
        this.transformListHandler.matrix(a, b, c, d, e, f);
    }

    protected void parseRotate() throws ParseException, IOException {
        this.current = this.reader.read();
        if (this.current != 111) {
            this.reportCharacterExpectedError('o', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 116) {
            this.reportCharacterExpectedError('t', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 97) {
            this.reportCharacterExpectedError('a', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 116) {
            this.reportCharacterExpectedError('t', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 101) {
            this.reportCharacterExpectedError('e', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        this.skipSpaces();
        if (this.current != 40) {
            this.reportCharacterExpectedError('(', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        this.skipSpaces();
        float theta = this.parseFloat();
        this.skipSpaces();
        switch (this.current) {
            case 41: {
                this.transformListHandler.rotate(theta);
                return;
            }
            case 44: {
                this.current = this.reader.read();
                this.skipSpaces();
            }
        }
        float cx = this.parseFloat();
        this.skipCommaSpaces();
        float cy = this.parseFloat();
        this.skipSpaces();
        if (this.current != 41) {
            this.reportCharacterExpectedError(')', this.current);
            this.skipTransform();
            return;
        }
        this.transformListHandler.rotate(theta, cx, cy);
    }

    protected void parseTranslate() throws ParseException, IOException {
        this.current = this.reader.read();
        if (this.current != 114) {
            this.reportCharacterExpectedError('r', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 97) {
            this.reportCharacterExpectedError('a', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 110) {
            this.reportCharacterExpectedError('n', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 115) {
            this.reportCharacterExpectedError('s', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 108) {
            this.reportCharacterExpectedError('l', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 97) {
            this.reportCharacterExpectedError('a', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 116) {
            this.reportCharacterExpectedError('t', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 101) {
            this.reportCharacterExpectedError('e', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        this.skipSpaces();
        if (this.current != 40) {
            this.reportCharacterExpectedError('(', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        this.skipSpaces();
        float tx = this.parseFloat();
        this.skipSpaces();
        switch (this.current) {
            case 41: {
                this.transformListHandler.translate(tx);
                return;
            }
            case 44: {
                this.current = this.reader.read();
                this.skipSpaces();
            }
        }
        float ty = this.parseFloat();
        this.skipSpaces();
        if (this.current != 41) {
            this.reportCharacterExpectedError(')', this.current);
            this.skipTransform();
            return;
        }
        this.transformListHandler.translate(tx, ty);
    }

    protected void parseScale() throws ParseException, IOException {
        this.current = this.reader.read();
        if (this.current != 97) {
            this.reportCharacterExpectedError('a', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 108) {
            this.reportCharacterExpectedError('l', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 101) {
            this.reportCharacterExpectedError('e', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        this.skipSpaces();
        if (this.current != 40) {
            this.reportCharacterExpectedError('(', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        this.skipSpaces();
        float sx = this.parseFloat();
        this.skipSpaces();
        switch (this.current) {
            case 41: {
                this.transformListHandler.scale(sx);
                return;
            }
            case 44: {
                this.current = this.reader.read();
                this.skipSpaces();
            }
        }
        float sy = this.parseFloat();
        this.skipSpaces();
        if (this.current != 41) {
            this.reportCharacterExpectedError(')', this.current);
            this.skipTransform();
            return;
        }
        this.transformListHandler.scale(sx, sy);
    }

    protected void parseSkew() throws ParseException, IOException {
        this.current = this.reader.read();
        if (this.current != 101) {
            this.reportCharacterExpectedError('e', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        if (this.current != 119) {
            this.reportCharacterExpectedError('w', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        boolean skewX = false;
        switch (this.current) {
            case 88: {
                skewX = true;
            }
            case 89: {
                break;
            }
            default: {
                this.reportCharacterExpectedError('X', this.current);
                this.skipTransform();
                return;
            }
        }
        this.current = this.reader.read();
        this.skipSpaces();
        if (this.current != 40) {
            this.reportCharacterExpectedError('(', this.current);
            this.skipTransform();
            return;
        }
        this.current = this.reader.read();
        this.skipSpaces();
        float sk = this.parseFloat();
        this.skipSpaces();
        if (this.current != 41) {
            this.reportCharacterExpectedError(')', this.current);
            this.skipTransform();
            return;
        }
        if (skewX) {
            this.transformListHandler.skewX(sk);
        } else {
            this.transformListHandler.skewY(sk);
        }
    }

    protected void skipTransform() throws IOException {
        block3: while (true) {
            this.current = this.reader.read();
            switch (this.current) {
                case 41: {
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

