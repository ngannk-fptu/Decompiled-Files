/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.xml.XMLUtilities
 */
package org.apache.batik.parser;

import java.io.IOException;
import org.apache.batik.parser.DefaultFragmentIdentifierHandler;
import org.apache.batik.parser.FragmentIdentifierHandler;
import org.apache.batik.parser.NumberParser;
import org.apache.batik.parser.ParseException;
import org.apache.batik.xml.XMLUtilities;

public class FragmentIdentifierParser
extends NumberParser {
    protected char[] buffer = new char[16];
    protected int bufferSize;
    protected FragmentIdentifierHandler fragmentIdentifierHandler = DefaultFragmentIdentifierHandler.INSTANCE;

    public void setFragmentIdentifierHandler(FragmentIdentifierHandler handler) {
        this.fragmentIdentifierHandler = handler;
    }

    public FragmentIdentifierHandler getFragmentIdentifierHandler() {
        return this.fragmentIdentifierHandler;
    }

    @Override
    protected void doParse() throws ParseException, IOException {
        block33: {
            this.bufferSize = 0;
            this.current = this.reader.read();
            this.fragmentIdentifierHandler.startFragmentIdentifier();
            String id = null;
            switch (this.current) {
                case 120: {
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 112) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 111) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 105) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 110) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 116) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 101) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 114) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 40) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferSize = 0;
                    this.current = this.reader.read();
                    if (this.current != 105) {
                        this.reportCharacterExpectedError('i', this.current);
                    } else {
                        this.current = this.reader.read();
                        if (this.current != 100) {
                            this.reportCharacterExpectedError('d', this.current);
                        } else {
                            this.current = this.reader.read();
                            if (this.current != 40) {
                                this.reportCharacterExpectedError('(', this.current);
                            } else {
                                this.current = this.reader.read();
                                if (this.current != 34 && this.current != 39) {
                                    this.reportCharacterExpectedError('\'', this.current);
                                } else {
                                    char q = (char)this.current;
                                    this.current = this.reader.read();
                                    this.parseIdentifier();
                                    id = this.getBufferContent();
                                    this.bufferSize = 0;
                                    this.fragmentIdentifierHandler.idReference(id);
                                    if (this.current != q) {
                                        this.reportCharacterExpectedError(q, this.current);
                                    } else {
                                        this.current = this.reader.read();
                                        if (this.current != 41) {
                                            this.reportCharacterExpectedError(')', this.current);
                                        } else {
                                            this.current = this.reader.read();
                                            if (this.current != 41) {
                                                this.reportCharacterExpectedError(')', this.current);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break block33;
                }
                case 115: {
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 118) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 103) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 86) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 105) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 101) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 119) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferize();
                    this.current = this.reader.read();
                    if (this.current != 40) {
                        this.parseIdentifier();
                        break;
                    }
                    this.bufferSize = 0;
                    this.current = this.reader.read();
                    this.parseViewAttributes();
                    if (this.current != 41) {
                        this.reportCharacterExpectedError(')', this.current);
                    }
                    break block33;
                }
                default: {
                    if (this.current == -1 || !XMLUtilities.isXMLNameFirstCharacter((char)((char)this.current))) break block33;
                    this.bufferize();
                    this.current = this.reader.read();
                    this.parseIdentifier();
                }
            }
            id = this.getBufferContent();
            this.fragmentIdentifierHandler.idReference(id);
        }
        this.fragmentIdentifierHandler.endFragmentIdentifier();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected void parseViewAttributes() throws ParseException, IOException {
        boolean first = true;
        block33: while (true) {
            switch (this.current) {
                case -1: 
                case 41: {
                    if (!first) return;
                    this.reportUnexpectedCharacterError(this.current);
                    return;
                }
                default: {
                    return;
                }
                case 59: {
                    if (first) {
                        this.reportUnexpectedCharacterError(this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    continue block33;
                }
                case 118: {
                    first = false;
                    this.current = this.reader.read();
                    if (this.current != 105) {
                        this.reportCharacterExpectedError('i', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 101) {
                        this.reportCharacterExpectedError('e', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 119) {
                        this.reportCharacterExpectedError('w', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    switch (this.current) {
                        case 66: {
                            this.current = this.reader.read();
                            if (this.current != 111) {
                                this.reportCharacterExpectedError('o', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            if (this.current != 120) {
                                this.reportCharacterExpectedError('x', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            if (this.current != 40) {
                                this.reportCharacterExpectedError('(', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            float x = this.parseFloat();
                            if (this.current != 44) {
                                this.reportCharacterExpectedError(',', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            float y = this.parseFloat();
                            if (this.current != 44) {
                                this.reportCharacterExpectedError(',', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            float w = this.parseFloat();
                            if (this.current != 44) {
                                this.reportCharacterExpectedError(',', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            float h = this.parseFloat();
                            if (this.current != 41) {
                                this.reportCharacterExpectedError(')', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            this.fragmentIdentifierHandler.viewBox(x, y, w, h);
                            if (this.current == 41 || this.current == 59) continue block33;
                            this.reportCharacterExpectedError(')', this.current);
                            return;
                        }
                        case 84: {
                            this.current = this.reader.read();
                            if (this.current != 97) {
                                this.reportCharacterExpectedError('a', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            if (this.current != 114) {
                                this.reportCharacterExpectedError('r', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            if (this.current != 103) {
                                this.reportCharacterExpectedError('g', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            if (this.current != 101) {
                                this.reportCharacterExpectedError('e', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            if (this.current != 116) {
                                this.reportCharacterExpectedError('t', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            if (this.current != 40) {
                                this.reportCharacterExpectedError('(', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            this.fragmentIdentifierHandler.startViewTarget();
                            block34: while (true) {
                                this.bufferSize = 0;
                                if (this.current == -1 || !XMLUtilities.isXMLNameFirstCharacter((char)((char)this.current))) {
                                    this.reportUnexpectedCharacterError(this.current);
                                    return;
                                }
                                this.bufferize();
                                this.current = this.reader.read();
                                this.parseIdentifier();
                                String s = this.getBufferContent();
                                this.fragmentIdentifierHandler.viewTarget(s);
                                this.bufferSize = 0;
                                switch (this.current) {
                                    case 41: {
                                        this.current = this.reader.read();
                                        break block34;
                                    }
                                    case 44: 
                                    case 59: {
                                        this.current = this.reader.read();
                                        continue block34;
                                    }
                                    default: {
                                        this.reportUnexpectedCharacterError(this.current);
                                        return;
                                    }
                                }
                                break;
                            }
                            this.fragmentIdentifierHandler.endViewTarget();
                            continue block33;
                        }
                    }
                    this.reportUnexpectedCharacterError(this.current);
                    return;
                }
                case 112: {
                    first = false;
                    this.current = this.reader.read();
                    if (this.current != 114) {
                        this.reportCharacterExpectedError('r', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 101) {
                        this.reportCharacterExpectedError('e', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 115) {
                        this.reportCharacterExpectedError('s', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 101) {
                        this.reportCharacterExpectedError('e', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 114) {
                        this.reportCharacterExpectedError('r', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 118) {
                        this.reportCharacterExpectedError('v', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 101) {
                        this.reportCharacterExpectedError('e', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 65) {
                        this.reportCharacterExpectedError('A', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 115) {
                        this.reportCharacterExpectedError('s', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 112) {
                        this.reportCharacterExpectedError('p', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 101) {
                        this.reportCharacterExpectedError('e', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 99) {
                        this.reportCharacterExpectedError('c', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 116) {
                        this.reportCharacterExpectedError('t', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 82) {
                        this.reportCharacterExpectedError('R', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 97) {
                        this.reportCharacterExpectedError('a', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 116) {
                        this.reportCharacterExpectedError('t', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 105) {
                        this.reportCharacterExpectedError('i', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 111) {
                        this.reportCharacterExpectedError('o', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 40) {
                        this.reportCharacterExpectedError('(', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    this.parsePreserveAspectRatio();
                    if (this.current != 41) {
                        this.reportCharacterExpectedError(')', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    continue block33;
                }
                case 116: {
                    first = false;
                    this.current = this.reader.read();
                    if (this.current != 114) {
                        this.reportCharacterExpectedError('r', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 97) {
                        this.reportCharacterExpectedError('a', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 110) {
                        this.reportCharacterExpectedError('n', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 115) {
                        this.reportCharacterExpectedError('s', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 102) {
                        this.reportCharacterExpectedError('f', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 111) {
                        this.reportCharacterExpectedError('o', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 114) {
                        this.reportCharacterExpectedError('r', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 109) {
                        this.reportCharacterExpectedError('m', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 40) {
                        this.reportCharacterExpectedError('(', this.current);
                        return;
                    }
                    this.fragmentIdentifierHandler.startTransformList();
                    block35: while (true) {
                        try {
                            this.current = this.reader.read();
                            switch (this.current) {
                                case 44: {
                                    continue block35;
                                }
                                case 109: {
                                    this.parseMatrix();
                                    continue block35;
                                }
                                case 114: {
                                    this.parseRotate();
                                    continue block35;
                                }
                                case 116: {
                                    this.parseTranslate();
                                    continue block35;
                                }
                                case 115: {
                                    this.current = this.reader.read();
                                    switch (this.current) {
                                        case 99: {
                                            this.parseScale();
                                            continue block35;
                                        }
                                        case 107: {
                                            this.parseSkew();
                                            continue block35;
                                        }
                                    }
                                    this.reportUnexpectedCharacterError(this.current);
                                    this.skipTransform();
                                    continue block35;
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
                    this.fragmentIdentifierHandler.endTransformList();
                    continue block33;
                }
                case 122: {
                    first = false;
                    this.current = this.reader.read();
                    if (this.current != 111) {
                        this.reportCharacterExpectedError('o', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 111) {
                        this.reportCharacterExpectedError('o', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 109) {
                        this.reportCharacterExpectedError('m', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 65) {
                        this.reportCharacterExpectedError('A', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 110) {
                        this.reportCharacterExpectedError('n', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 100) {
                        this.reportCharacterExpectedError('d', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 80) {
                        this.reportCharacterExpectedError('P', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 97) {
                        this.reportCharacterExpectedError('a', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 110) {
                        this.reportCharacterExpectedError('n', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    if (this.current != 40) {
                        this.reportCharacterExpectedError('(', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    switch (this.current) {
                        case 109: {
                            this.current = this.reader.read();
                            if (this.current != 97) {
                                this.reportCharacterExpectedError('a', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            if (this.current != 103) {
                                this.reportCharacterExpectedError('g', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            if (this.current != 110) {
                                this.reportCharacterExpectedError('n', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            if (this.current != 105) {
                                this.reportCharacterExpectedError('i', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            if (this.current != 102) {
                                this.reportCharacterExpectedError('f', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            if (this.current != 121) {
                                this.reportCharacterExpectedError('y', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            this.fragmentIdentifierHandler.zoomAndPan(true);
                            break;
                        }
                        case 100: {
                            this.current = this.reader.read();
                            if (this.current != 105) {
                                this.reportCharacterExpectedError('i', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            if (this.current != 115) {
                                this.reportCharacterExpectedError('s', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            if (this.current != 97) {
                                this.reportCharacterExpectedError('a', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            if (this.current != 98) {
                                this.reportCharacterExpectedError('b', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            if (this.current != 108) {
                                this.reportCharacterExpectedError('l', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            if (this.current != 101) {
                                this.reportCharacterExpectedError('e', this.current);
                                return;
                            }
                            this.current = this.reader.read();
                            this.fragmentIdentifierHandler.zoomAndPan(false);
                            break;
                        }
                        default: {
                            this.reportUnexpectedCharacterError(this.current);
                            return;
                        }
                    }
                    if (this.current != 41) {
                        this.reportCharacterExpectedError(')', this.current);
                        return;
                    }
                    this.current = this.reader.read();
                    continue block33;
                }
            }
            break;
        }
    }

    protected void parseIdentifier() throws ParseException, IOException {
        while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)((char)this.current))) {
            this.bufferize();
            this.current = this.reader.read();
        }
    }

    protected String getBufferContent() {
        return new String(this.buffer, 0, this.bufferSize);
    }

    protected void bufferize() {
        if (this.bufferSize >= this.buffer.length) {
            char[] t = new char[this.buffer.length * 2];
            System.arraycopy(this.buffer, 0, t, 0, this.bufferSize);
            this.buffer = t;
        }
        this.buffer[this.bufferSize++] = (char)this.current;
    }

    @Override
    protected void skipSpaces() throws IOException {
        if (this.current == 44) {
            this.current = this.reader.read();
        }
    }

    @Override
    protected void skipCommaSpaces() throws IOException {
        if (this.current == 44) {
            this.current = this.reader.read();
        }
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
        this.fragmentIdentifierHandler.matrix(a, b, c, d, e, f);
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
                this.fragmentIdentifierHandler.rotate(theta);
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
        this.fragmentIdentifierHandler.rotate(theta, cx, cy);
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
                this.fragmentIdentifierHandler.translate(tx);
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
        this.fragmentIdentifierHandler.translate(tx, ty);
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
                this.fragmentIdentifierHandler.scale(sx);
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
        this.fragmentIdentifierHandler.scale(sx, sy);
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
            this.fragmentIdentifierHandler.skewX(sk);
        } else {
            this.fragmentIdentifierHandler.skewY(sk);
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

    protected void parsePreserveAspectRatio() throws ParseException, IOException {
        this.fragmentIdentifierHandler.startPreserveAspectRatio();
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
                    this.reportCharacterExpectedError('n', this.current);
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
                this.fragmentIdentifierHandler.none();
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
                                this.fragmentIdentifierHandler.xMaxYMax();
                                this.current = this.reader.read();
                                break block0;
                            }
                            case 105: {
                                this.current = this.reader.read();
                                switch (this.current) {
                                    case 100: {
                                        this.fragmentIdentifierHandler.xMaxYMid();
                                        this.current = this.reader.read();
                                        break block0;
                                    }
                                    case 110: {
                                        this.fragmentIdentifierHandler.xMaxYMin();
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
                                        this.fragmentIdentifierHandler.xMidYMax();
                                        this.current = this.reader.read();
                                        break block0;
                                    }
                                    case 105: {
                                        this.current = this.reader.read();
                                        switch (this.current) {
                                            case 100: {
                                                this.fragmentIdentifierHandler.xMidYMid();
                                                this.current = this.reader.read();
                                                break block0;
                                            }
                                            case 110: {
                                                this.fragmentIdentifierHandler.xMidYMin();
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
                                        this.fragmentIdentifierHandler.xMinYMax();
                                        this.current = this.reader.read();
                                        break block0;
                                    }
                                    case 105: {
                                        this.current = this.reader.read();
                                        switch (this.current) {
                                            case 100: {
                                                this.fragmentIdentifierHandler.xMinYMid();
                                                this.current = this.reader.read();
                                                break block0;
                                            }
                                            case 110: {
                                                this.fragmentIdentifierHandler.xMinYMin();
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
                this.fragmentIdentifierHandler.meet();
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
                this.fragmentIdentifierHandler.slice();
                this.current = this.reader.read();
            }
        }
        this.fragmentIdentifierHandler.endPreserveAspectRatio();
    }

    protected void skipIdentifier() throws IOException {
        block4: while (true) {
            this.current = this.reader.read();
            switch (this.current) {
                case 9: 
                case 10: 
                case 13: 
                case 32: {
                    this.current = this.reader.read();
                }
                case -1: {
                    break block4;
                }
                default: {
                    continue block4;
                }
            }
            break;
        }
    }
}

