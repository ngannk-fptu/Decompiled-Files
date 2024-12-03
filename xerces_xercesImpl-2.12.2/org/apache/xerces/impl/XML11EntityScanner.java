/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl;

import java.io.IOException;
import org.apache.xerces.impl.XMLEntityScanner;
import org.apache.xerces.util.XML11Char;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLString;

public class XML11EntityScanner
extends XMLEntityScanner {
    @Override
    public int peekChar() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        int n = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        if (this.fCurrentEntity.isExternal()) {
            return n != 13 && n != 133 && n != 8232 ? n : 10;
        }
        return n;
    }

    @Override
    public int scanChar() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        int n = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
        boolean bl = false;
        if (n == 10 || (n == 13 || n == 133 || n == 8232) && (bl = this.fCurrentEntity.isExternal())) {
            char c;
            ++this.fCurrentEntity.lineNumber;
            this.fCurrentEntity.columnNumber = 1;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = (char)n;
                this.load(1, false);
            }
            if (n == 13 && bl && this.fCurrentEntity.position < this.fCurrentEntity.count && (c = this.fCurrentEntity.ch[this.fCurrentEntity.position++]) != '\n' && c != '\u0085') {
                --this.fCurrentEntity.position;
            }
            n = 10;
        }
        ++this.fCurrentEntity.columnNumber;
        return n;
    }

    @Override
    public String scanNmtoken() throws IOException {
        char c;
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        int n = this.fCurrentEntity.position;
        while (true) {
            char c2;
            if (XML11Char.isXML11Name(c = this.fCurrentEntity.ch[this.fCurrentEntity.position])) {
                if (++this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
                c2 = this.fCurrentEntity.position - n;
                if (c2 == this.fCurrentEntity.ch.length) {
                    this.resizeBuffer(n, c2);
                } else {
                    System.arraycopy(this.fCurrentEntity.ch, n, this.fCurrentEntity.ch, 0, c2);
                }
                n = 0;
                if (!this.load(c2, false)) continue;
                break;
            }
            if (!XML11Char.isXML11NameHighSurrogate(c)) break;
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                c2 = this.fCurrentEntity.position - n;
                if (c2 == this.fCurrentEntity.ch.length) {
                    this.resizeBuffer(n, c2);
                } else {
                    System.arraycopy(this.fCurrentEntity.ch, n, this.fCurrentEntity.ch, 0, c2);
                }
                n = 0;
                if (this.load(c2, false)) {
                    --this.fCurrentEntity.startPosition;
                    --this.fCurrentEntity.position;
                    break;
                }
            }
            if (!XMLChar.isLowSurrogate(c2 = this.fCurrentEntity.ch[this.fCurrentEntity.position]) || !XML11Char.isXML11Name(XMLChar.supplemental(c, c2))) {
                --this.fCurrentEntity.position;
                break;
            }
            if (++this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
            int n2 = this.fCurrentEntity.position - n;
            if (n2 == this.fCurrentEntity.ch.length) {
                this.resizeBuffer(n, n2);
            } else {
                System.arraycopy(this.fCurrentEntity.ch, n, this.fCurrentEntity.ch, 0, n2);
            }
            n = 0;
            if (this.load(n2, false)) break;
        }
        c = this.fCurrentEntity.position - n;
        this.fCurrentEntity.columnNumber += c;
        String string = null;
        if (c > '\u0000') {
            string = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, n, c);
        }
        return string;
    }

    @Override
    public String scanName() throws IOException {
        char c;
        int n;
        char c2;
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        if (XML11Char.isXML11NameStart(c2 = this.fCurrentEntity.ch[n = this.fCurrentEntity.position++])) {
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c2;
                n = 0;
                if (this.load(1, false)) {
                    ++this.fCurrentEntity.columnNumber;
                    String string = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
                    return string;
                }
            }
        } else if (XML11Char.isXML11NameHighSurrogate(c2)) {
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c2;
                n = 0;
                if (this.load(1, false)) {
                    --this.fCurrentEntity.position;
                    --this.fCurrentEntity.startPosition;
                    return null;
                }
            }
            if (!XMLChar.isLowSurrogate(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]) || !XML11Char.isXML11NameStart(XMLChar.supplemental(c2, c))) {
                --this.fCurrentEntity.position;
                return null;
            }
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c2;
                this.fCurrentEntity.ch[1] = c;
                n = 0;
                if (this.load(2, false)) {
                    this.fCurrentEntity.columnNumber += 2;
                    String string = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 2);
                    return string;
                }
            }
        } else {
            return null;
        }
        while (true) {
            if (XML11Char.isXML11Name(c2 = this.fCurrentEntity.ch[this.fCurrentEntity.position])) {
                if (++this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
                c = this.fCurrentEntity.position - n;
                if (c == this.fCurrentEntity.ch.length) {
                    this.resizeBuffer(n, c);
                } else {
                    System.arraycopy(this.fCurrentEntity.ch, n, this.fCurrentEntity.ch, 0, c);
                }
                n = 0;
                if (!this.load(c, false)) continue;
                break;
            }
            if (!XML11Char.isXML11NameHighSurrogate(c2)) break;
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                c = this.fCurrentEntity.position - n;
                if (c == this.fCurrentEntity.ch.length) {
                    this.resizeBuffer(n, c);
                } else {
                    System.arraycopy(this.fCurrentEntity.ch, n, this.fCurrentEntity.ch, 0, c);
                }
                n = 0;
                if (this.load(c, false)) {
                    --this.fCurrentEntity.position;
                    --this.fCurrentEntity.startPosition;
                    break;
                }
            }
            if (!XMLChar.isLowSurrogate(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]) || !XML11Char.isXML11Name(XMLChar.supplemental(c2, c))) {
                --this.fCurrentEntity.position;
                break;
            }
            if (++this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
            int n2 = this.fCurrentEntity.position - n;
            if (n2 == this.fCurrentEntity.ch.length) {
                this.resizeBuffer(n, n2);
            } else {
                System.arraycopy(this.fCurrentEntity.ch, n, this.fCurrentEntity.ch, 0, n2);
            }
            n = 0;
            if (this.load(n2, false)) break;
        }
        c = this.fCurrentEntity.position - n;
        this.fCurrentEntity.columnNumber += c;
        String string = null;
        if (c > '\u0000') {
            string = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, n, c);
        }
        return string;
    }

    @Override
    public String scanNCName() throws IOException {
        char c;
        int n;
        char c2;
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        if (XML11Char.isXML11NCNameStart(c2 = this.fCurrentEntity.ch[n = this.fCurrentEntity.position++])) {
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c2;
                n = 0;
                if (this.load(1, false)) {
                    ++this.fCurrentEntity.columnNumber;
                    String string = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
                    return string;
                }
            }
        } else if (XML11Char.isXML11NameHighSurrogate(c2)) {
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c2;
                n = 0;
                if (this.load(1, false)) {
                    --this.fCurrentEntity.position;
                    --this.fCurrentEntity.startPosition;
                    return null;
                }
            }
            if (!XMLChar.isLowSurrogate(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]) || !XML11Char.isXML11NCNameStart(XMLChar.supplemental(c2, c))) {
                --this.fCurrentEntity.position;
                return null;
            }
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c2;
                this.fCurrentEntity.ch[1] = c;
                n = 0;
                if (this.load(2, false)) {
                    this.fCurrentEntity.columnNumber += 2;
                    String string = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 2);
                    return string;
                }
            }
        } else {
            return null;
        }
        while (true) {
            if (XML11Char.isXML11NCName(c2 = this.fCurrentEntity.ch[this.fCurrentEntity.position])) {
                if (++this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
                c = this.fCurrentEntity.position - n;
                if (c == this.fCurrentEntity.ch.length) {
                    this.resizeBuffer(n, c);
                } else {
                    System.arraycopy(this.fCurrentEntity.ch, n, this.fCurrentEntity.ch, 0, c);
                }
                n = 0;
                if (!this.load(c, false)) continue;
                break;
            }
            if (!XML11Char.isXML11NameHighSurrogate(c2)) break;
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                c = this.fCurrentEntity.position - n;
                if (c == this.fCurrentEntity.ch.length) {
                    this.resizeBuffer(n, c);
                } else {
                    System.arraycopy(this.fCurrentEntity.ch, n, this.fCurrentEntity.ch, 0, c);
                }
                n = 0;
                if (this.load(c, false)) {
                    --this.fCurrentEntity.startPosition;
                    --this.fCurrentEntity.position;
                    break;
                }
            }
            if (!XMLChar.isLowSurrogate(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]) || !XML11Char.isXML11NCName(XMLChar.supplemental(c2, c))) {
                --this.fCurrentEntity.position;
                break;
            }
            if (++this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
            int n2 = this.fCurrentEntity.position - n;
            if (n2 == this.fCurrentEntity.ch.length) {
                this.resizeBuffer(n, n2);
            } else {
                System.arraycopy(this.fCurrentEntity.ch, n, this.fCurrentEntity.ch, 0, n2);
            }
            n = 0;
            if (this.load(n2, false)) break;
        }
        c = this.fCurrentEntity.position - n;
        this.fCurrentEntity.columnNumber += c;
        String string = null;
        if (c > '\u0000') {
            string = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, n, c);
        }
        return string;
    }

    @Override
    public boolean scanQName(QName qName) throws IOException {
        char c;
        int n;
        int n2;
        char c2;
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        if (XML11Char.isXML11NCNameStart(c2 = this.fCurrentEntity.ch[n2 = this.fCurrentEntity.position++])) {
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c2;
                n2 = 0;
                if (this.load(1, false)) {
                    ++this.fCurrentEntity.columnNumber;
                    String string = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
                    qName.setValues(null, string, string, null);
                    return true;
                }
            }
        } else if (XML11Char.isXML11NameHighSurrogate(c2)) {
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c2;
                n2 = 0;
                if (this.load(1, false)) {
                    --this.fCurrentEntity.startPosition;
                    --this.fCurrentEntity.position;
                    return false;
                }
            }
            if (!XMLChar.isLowSurrogate(n = this.fCurrentEntity.ch[this.fCurrentEntity.position]) || !XML11Char.isXML11NCNameStart(XMLChar.supplemental(c2, (char)n))) {
                --this.fCurrentEntity.position;
                return false;
            }
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c2;
                this.fCurrentEntity.ch[1] = n;
                n2 = 0;
                if (this.load(2, false)) {
                    this.fCurrentEntity.columnNumber += 2;
                    String string = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 2);
                    qName.setValues(null, string, string, null);
                    return true;
                }
            }
        } else {
            return false;
        }
        n = -1;
        boolean bl = false;
        while (true) {
            if (XML11Char.isXML11Name(c2 = this.fCurrentEntity.ch[this.fCurrentEntity.position])) {
                if (c2 == ':') {
                    if (n != -1) break;
                    n = this.fCurrentEntity.position;
                }
                if (++this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
                c = this.fCurrentEntity.position - n2;
                if (c == this.fCurrentEntity.ch.length) {
                    this.resizeBuffer(n2, c);
                } else {
                    System.arraycopy(this.fCurrentEntity.ch, n2, this.fCurrentEntity.ch, 0, c);
                }
                if (n != -1) {
                    n -= n2;
                }
                n2 = 0;
                if (!this.load(c, false)) continue;
                break;
            }
            if (!XML11Char.isXML11NameHighSurrogate(c2)) break;
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                c = this.fCurrentEntity.position - n2;
                if (c == this.fCurrentEntity.ch.length) {
                    this.resizeBuffer(n2, c);
                } else {
                    System.arraycopy(this.fCurrentEntity.ch, n2, this.fCurrentEntity.ch, 0, c);
                }
                if (n != -1) {
                    n -= n2;
                }
                n2 = 0;
                if (this.load(c, false)) {
                    bl = true;
                    --this.fCurrentEntity.startPosition;
                    --this.fCurrentEntity.position;
                    break;
                }
            }
            if (!XMLChar.isLowSurrogate(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]) || !XML11Char.isXML11Name(XMLChar.supplemental(c2, c))) {
                bl = true;
                --this.fCurrentEntity.position;
                break;
            }
            if (++this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
            int n3 = this.fCurrentEntity.position - n2;
            if (n3 == this.fCurrentEntity.ch.length) {
                this.resizeBuffer(n2, n3);
            } else {
                System.arraycopy(this.fCurrentEntity.ch, n2, this.fCurrentEntity.ch, 0, n3);
            }
            if (n != -1) {
                n -= n2;
            }
            n2 = 0;
            if (this.load(n3, false)) break;
        }
        c = this.fCurrentEntity.position - n2;
        this.fCurrentEntity.columnNumber += c;
        if (c > '\u0000') {
            String string = null;
            String string2 = null;
            String string3 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, n2, c);
            if (n != -1) {
                int n4 = n - n2;
                string = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, n2, n4);
                int n5 = c - n4 - 1;
                int n6 = n + 1;
                if (!(XML11Char.isXML11NCNameStart(this.fCurrentEntity.ch[n6]) || XML11Char.isXML11NameHighSurrogate(this.fCurrentEntity.ch[n6]) && !bl)) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "IllegalQName", null, (short)2);
                }
                string2 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, n + 1, n5);
            } else {
                string2 = string3;
            }
            qName.setValues(string, string2, string3, null);
            return true;
        }
        return false;
    }

    @Override
    public int scanContent(XMLString xMLString) throws IOException {
        int n;
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        } else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
            this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[this.fCurrentEntity.count - 1];
            this.load(1, false);
            this.fCurrentEntity.position = 0;
            this.fCurrentEntity.startPosition = 0;
        }
        int n2 = this.fCurrentEntity.position;
        int n3 = this.fCurrentEntity.ch[n2];
        int n4 = 0;
        boolean bl = this.fCurrentEntity.isExternal();
        if (n3 == 10 || (n3 == 13 || n3 == 133 || n3 == 8232) && bl) {
            do {
                if ((n3 = this.fCurrentEntity.ch[this.fCurrentEntity.position++]) == 13 && bl) {
                    ++n4;
                    ++this.fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                        n2 = 0;
                        this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                        this.fCurrentEntity.position = n4;
                        this.fCurrentEntity.startPosition = n4;
                        if (this.load(n4, false)) break;
                    }
                    if ((n = this.fCurrentEntity.ch[this.fCurrentEntity.position]) == 10 || n == 133) {
                        ++this.fCurrentEntity.position;
                        ++n2;
                        continue;
                    }
                    ++n4;
                    continue;
                }
                if (n3 == 10 || (n3 == 133 || n3 == 8232) && bl) {
                    ++n4;
                    ++this.fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
                    n2 = 0;
                    this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                    this.fCurrentEntity.position = n4;
                    this.fCurrentEntity.startPosition = n4;
                    if (!this.load(n4, false)) continue;
                    break;
                }
                --this.fCurrentEntity.position;
                break;
            } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
            for (n = n2; n < this.fCurrentEntity.position; ++n) {
                this.fCurrentEntity.ch[n] = 10;
            }
            n = this.fCurrentEntity.position - n2;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                xMLString.setValues(this.fCurrentEntity.ch, n2, n);
                return -1;
            }
        }
        if (bl) {
            while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
                if (XML11Char.isXML11Content(n3 = this.fCurrentEntity.ch[this.fCurrentEntity.position++]) && n3 != 133 && n3 != 8232) continue;
                --this.fCurrentEntity.position;
                break;
            }
        } else {
            while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
                if (XML11Char.isXML11InternalEntityContent(n3 = this.fCurrentEntity.ch[this.fCurrentEntity.position++])) continue;
                --this.fCurrentEntity.position;
                break;
            }
        }
        n = this.fCurrentEntity.position - n2;
        this.fCurrentEntity.columnNumber += n - n4;
        xMLString.setValues(this.fCurrentEntity.ch, n2, n);
        if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
            n3 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if ((n3 == 13 || n3 == 133 || n3 == 8232) && bl) {
                n3 = 10;
            }
        } else {
            n3 = -1;
        }
        return n3;
    }

    @Override
    public int scanLiteral(int n, XMLString xMLString) throws IOException {
        int n2;
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        } else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
            this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[this.fCurrentEntity.count - 1];
            this.load(1, false);
            this.fCurrentEntity.startPosition = 0;
            this.fCurrentEntity.position = 0;
        }
        int n3 = this.fCurrentEntity.position;
        int n4 = this.fCurrentEntity.ch[n3];
        int n5 = 0;
        boolean bl = this.fCurrentEntity.isExternal();
        if (n4 == 10 || (n4 == 13 || n4 == 133 || n4 == 8232) && bl) {
            do {
                if ((n4 = this.fCurrentEntity.ch[this.fCurrentEntity.position++]) == 13 && bl) {
                    ++n5;
                    ++this.fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                        n3 = 0;
                        this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                        this.fCurrentEntity.position = n5;
                        this.fCurrentEntity.startPosition = n5;
                        if (this.load(n5, false)) break;
                    }
                    if ((n2 = this.fCurrentEntity.ch[this.fCurrentEntity.position]) == 10 || n2 == 133) {
                        ++this.fCurrentEntity.position;
                        ++n3;
                        continue;
                    }
                    ++n5;
                    continue;
                }
                if (n4 == 10 || (n4 == 133 || n4 == 8232) && bl) {
                    ++n5;
                    ++this.fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
                    n3 = 0;
                    this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                    this.fCurrentEntity.position = n5;
                    this.fCurrentEntity.startPosition = n5;
                    if (!this.load(n5, false)) continue;
                    break;
                }
                --this.fCurrentEntity.position;
                break;
            } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
            for (n2 = n3; n2 < this.fCurrentEntity.position; ++n2) {
                this.fCurrentEntity.ch[n2] = 10;
            }
            n2 = this.fCurrentEntity.position - n3;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                xMLString.setValues(this.fCurrentEntity.ch, n3, n2);
                return -1;
            }
        }
        if (bl) {
            while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
                if ((n4 = this.fCurrentEntity.ch[this.fCurrentEntity.position++]) != n && n4 != 37 && XML11Char.isXML11Content(n4) && n4 != 133 && n4 != 8232) continue;
                --this.fCurrentEntity.position;
                break;
            }
        } else {
            while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
                if (((n4 = this.fCurrentEntity.ch[this.fCurrentEntity.position++]) != n || this.fCurrentEntity.literal) && n4 != 37 && (XML11Char.isXML11InternalEntityContent(n4) || n4 == 13)) continue;
                --this.fCurrentEntity.position;
                break;
            }
        }
        n2 = this.fCurrentEntity.position - n3;
        this.fCurrentEntity.columnNumber += n2 - n5;
        xMLString.setValues(this.fCurrentEntity.ch, n3, n2);
        if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
            n4 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (n4 == n && this.fCurrentEntity.literal) {
                n4 = -1;
            }
        } else {
            n4 = -1;
        }
        return n4;
    }

    @Override
    public boolean scanData(String string, XMLStringBuffer xMLStringBuffer) throws IOException {
        boolean bl = false;
        int n = string.length();
        char c = string.charAt(0);
        boolean bl2 = this.fCurrentEntity.isExternal();
        do {
            int n2;
            int n3;
            int n4;
            block25: {
                int n5;
                if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                    this.load(0, true);
                }
                boolean bl3 = false;
                while (this.fCurrentEntity.position >= this.fCurrentEntity.count - n && !bl3) {
                    System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.ch, 0, this.fCurrentEntity.count - this.fCurrentEntity.position);
                    bl3 = this.load(this.fCurrentEntity.count - this.fCurrentEntity.position, false);
                    this.fCurrentEntity.position = 0;
                    this.fCurrentEntity.startPosition = 0;
                }
                if (this.fCurrentEntity.position >= this.fCurrentEntity.count - n) {
                    n4 = this.fCurrentEntity.count - this.fCurrentEntity.position;
                    xMLStringBuffer.append(this.fCurrentEntity.ch, this.fCurrentEntity.position, n4);
                    this.fCurrentEntity.columnNumber += this.fCurrentEntity.count;
                    this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                    this.fCurrentEntity.position = this.fCurrentEntity.count;
                    this.fCurrentEntity.startPosition = this.fCurrentEntity.count;
                    this.load(0, true);
                    return false;
                }
                n4 = this.fCurrentEntity.position;
                char c2 = this.fCurrentEntity.ch[n4];
                n3 = 0;
                if (c2 == '\n' || (c2 == '\r' || c2 == '\u0085' || c2 == '\u2028') && bl2) {
                    do {
                        if ((c2 = this.fCurrentEntity.ch[this.fCurrentEntity.position++]) == '\r' && bl2) {
                            ++n3;
                            ++this.fCurrentEntity.lineNumber;
                            this.fCurrentEntity.columnNumber = 1;
                            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                                n4 = 0;
                                this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                                this.fCurrentEntity.position = n3;
                                this.fCurrentEntity.startPosition = n3;
                                if (this.load(n3, false)) break;
                            }
                            if ((n2 = this.fCurrentEntity.ch[this.fCurrentEntity.position]) == 10 || n2 == 133) {
                                ++this.fCurrentEntity.position;
                                ++n4;
                                continue;
                            }
                            ++n3;
                            continue;
                        }
                        if (c2 == '\n' || (c2 == '\u0085' || c2 == '\u2028') && bl2) {
                            ++n3;
                            ++this.fCurrentEntity.lineNumber;
                            this.fCurrentEntity.columnNumber = 1;
                            if (this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
                            n4 = 0;
                            this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                            this.fCurrentEntity.position = n3;
                            this.fCurrentEntity.startPosition = n3;
                            this.fCurrentEntity.count = n3;
                            if (!this.load(n3, false)) continue;
                            break;
                        }
                        --this.fCurrentEntity.position;
                        break;
                    } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
                    for (n2 = n4; n2 < this.fCurrentEntity.position; ++n2) {
                        this.fCurrentEntity.ch[n2] = 10;
                    }
                    n2 = this.fCurrentEntity.position - n4;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                        xMLStringBuffer.append(this.fCurrentEntity.ch, n4, n2);
                        return true;
                    }
                }
                if (bl2) {
                    while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
                        if ((c2 = this.fCurrentEntity.ch[this.fCurrentEntity.position++]) == c) {
                            n2 = this.fCurrentEntity.position - 1;
                            for (n5 = 1; n5 < n; ++n5) {
                                if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                                    this.fCurrentEntity.position -= n5;
                                    break block25;
                                }
                                c2 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                                if (string.charAt(n5) == c2) continue;
                                --this.fCurrentEntity.position;
                                break;
                            }
                            if (this.fCurrentEntity.position != n2 + n) continue;
                            bl = true;
                            break;
                        }
                        if (c2 == '\n' || c2 == '\r' || c2 == '\u0085' || c2 == '\u2028') {
                            --this.fCurrentEntity.position;
                            break;
                        }
                        if (XML11Char.isXML11ValidLiteral(c2)) continue;
                        --this.fCurrentEntity.position;
                        n2 = this.fCurrentEntity.position - n4;
                        this.fCurrentEntity.columnNumber += n2 - n3;
                        xMLStringBuffer.append(this.fCurrentEntity.ch, n4, n2);
                        return true;
                    }
                } else {
                    while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
                        if ((c2 = this.fCurrentEntity.ch[this.fCurrentEntity.position++]) == c) {
                            n2 = this.fCurrentEntity.position - 1;
                            for (n5 = 1; n5 < n; ++n5) {
                                if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                                    this.fCurrentEntity.position -= n5;
                                    break block25;
                                }
                                c2 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                                if (string.charAt(n5) == c2) continue;
                                --this.fCurrentEntity.position;
                                break;
                            }
                            if (this.fCurrentEntity.position != n2 + n) continue;
                            bl = true;
                            break;
                        }
                        if (c2 == '\n') {
                            --this.fCurrentEntity.position;
                            break;
                        }
                        if (XML11Char.isXML11Valid(c2)) continue;
                        --this.fCurrentEntity.position;
                        n2 = this.fCurrentEntity.position - n4;
                        this.fCurrentEntity.columnNumber += n2 - n3;
                        xMLStringBuffer.append(this.fCurrentEntity.ch, n4, n2);
                        return true;
                    }
                }
            }
            n2 = this.fCurrentEntity.position - n4;
            this.fCurrentEntity.columnNumber += n2 - n3;
            if (bl) {
                n2 -= n;
            }
            xMLStringBuffer.append(this.fCurrentEntity.ch, n4, n2);
        } while (!bl);
        return !bl;
    }

    @Override
    public boolean skipChar(int n) throws IOException {
        char c;
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        if ((c = this.fCurrentEntity.ch[this.fCurrentEntity.position]) == n) {
            ++this.fCurrentEntity.position;
            if (n == 10) {
                ++this.fCurrentEntity.lineNumber;
                this.fCurrentEntity.columnNumber = 1;
            } else {
                ++this.fCurrentEntity.columnNumber;
            }
            return true;
        }
        if (n == 10 && (c == '\u2028' || c == '\u0085') && this.fCurrentEntity.isExternal()) {
            ++this.fCurrentEntity.position;
            ++this.fCurrentEntity.lineNumber;
            this.fCurrentEntity.columnNumber = 1;
            return true;
        }
        if (n == 10 && c == '\r' && this.fCurrentEntity.isExternal()) {
            char c2;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c;
                this.load(1, false);
            }
            if ((c2 = this.fCurrentEntity.ch[++this.fCurrentEntity.position]) == '\n' || c2 == '\u0085') {
                ++this.fCurrentEntity.position;
            }
            ++this.fCurrentEntity.lineNumber;
            this.fCurrentEntity.columnNumber = 1;
            return true;
        }
        return false;
    }

    @Override
    public boolean skipSpaces() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        if (this.fCurrentEntity.isExternal()) {
            if (XML11Char.isXML11Space(c)) {
                do {
                    boolean bl = false;
                    if (c == '\n' || c == '\r' || c == '\u0085' || c == '\u2028') {
                        char c2;
                        ++this.fCurrentEntity.lineNumber;
                        this.fCurrentEntity.columnNumber = 1;
                        if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                            this.fCurrentEntity.ch[0] = c;
                            bl = this.load(1, true);
                            if (!bl) {
                                this.fCurrentEntity.startPosition = 0;
                                this.fCurrentEntity.position = 0;
                            }
                        }
                        if (c == '\r' && (c2 = this.fCurrentEntity.ch[++this.fCurrentEntity.position]) != '\n' && c2 != '\u0085') {
                            --this.fCurrentEntity.position;
                        }
                    } else {
                        ++this.fCurrentEntity.columnNumber;
                    }
                    if (!bl) {
                        ++this.fCurrentEntity.position;
                    }
                    if (this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
                    this.load(0, true);
                } while (XML11Char.isXML11Space(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
                return true;
            }
        } else if (XMLChar.isSpace(c)) {
            do {
                boolean bl = false;
                if (c == '\n') {
                    ++this.fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                        this.fCurrentEntity.ch[0] = c;
                        bl = this.load(1, true);
                        if (!bl) {
                            this.fCurrentEntity.startPosition = 0;
                            this.fCurrentEntity.position = 0;
                        }
                    }
                } else {
                    ++this.fCurrentEntity.columnNumber;
                }
                if (!bl) {
                    ++this.fCurrentEntity.position;
                }
                if (this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
                this.load(0, true);
            } while (XMLChar.isSpace(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
            return true;
        }
        return false;
    }

    @Override
    public boolean skipString(String string) throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        int n = string.length();
        for (int i = 0; i < n; ++i) {
            char c;
            if ((c = this.fCurrentEntity.ch[this.fCurrentEntity.position++]) != string.charAt(i)) {
                this.fCurrentEntity.position -= i + 1;
                return false;
            }
            if (i >= n - 1 || this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
            System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.count - i - 1, this.fCurrentEntity.ch, 0, i + 1);
            if (!this.load(i + 1, false)) continue;
            this.fCurrentEntity.startPosition -= i + 1;
            this.fCurrentEntity.position -= i + 1;
            return false;
        }
        this.fCurrentEntity.columnNumber += n;
        return true;
    }
}

