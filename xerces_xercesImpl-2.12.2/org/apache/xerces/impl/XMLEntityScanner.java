/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl;

import java.io.EOFException;
import java.io.IOException;
import java.util.Locale;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.io.UCSReader;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLString;

public class XMLEntityScanner
implements XMLLocator {
    private static final boolean DEBUG_ENCODINGS = false;
    private static final boolean DEBUG_BUFFER = false;
    private static final EOFException END_OF_DOCUMENT_ENTITY = new EOFException(){
        private static final long serialVersionUID = 980337771224675268L;

        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    };
    private XMLEntityManager fEntityManager = null;
    protected XMLEntityManager.ScannedEntity fCurrentEntity = null;
    protected SymbolTable fSymbolTable = null;
    protected int fBufferSize = 2048;
    protected XMLErrorReporter fErrorReporter;

    @Override
    public final String getBaseSystemId() {
        return this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null ? this.fCurrentEntity.entityLocation.getExpandedSystemId() : null;
    }

    public final void setEncoding(String string) throws IOException {
        if (!(this.fCurrentEntity.stream == null || this.fCurrentEntity.encoding != null && this.fCurrentEntity.encoding.equals(string))) {
            if (this.fCurrentEntity.encoding != null && this.fCurrentEntity.encoding.startsWith("UTF-16")) {
                String string2 = string.toUpperCase(Locale.ENGLISH);
                if (string2.equals("UTF-16")) {
                    return;
                }
                if (string2.equals("ISO-10646-UCS-4")) {
                    this.fCurrentEntity.reader = this.fCurrentEntity.encoding.equals("UTF-16BE") ? new UCSReader(this.fCurrentEntity.stream, 8) : new UCSReader(this.fCurrentEntity.stream, 4);
                    return;
                }
                if (string2.equals("ISO-10646-UCS-2")) {
                    this.fCurrentEntity.reader = this.fCurrentEntity.encoding.equals("UTF-16BE") ? new UCSReader(this.fCurrentEntity.stream, 2) : new UCSReader(this.fCurrentEntity.stream, 1);
                    return;
                }
            }
            this.fCurrentEntity.setReader(this.fCurrentEntity.stream, string, null);
            this.fCurrentEntity.encoding = string;
        }
    }

    public final void setXMLVersion(String string) {
        this.fCurrentEntity.xmlVersion = string;
    }

    public final boolean isExternal() {
        return this.fCurrentEntity.isExternal();
    }

    public int peekChar() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        int n = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        if (this.fCurrentEntity.isExternal()) {
            return n != 13 ? n : 10;
        }
        return n;
    }

    public int scanChar() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        int n = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
        boolean bl = false;
        if (n == 10 || n == 13 && (bl = this.fCurrentEntity.isExternal())) {
            ++this.fCurrentEntity.lineNumber;
            this.fCurrentEntity.columnNumber = 1;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = (char)n;
                this.load(1, false);
            }
            if (n == 13 && bl) {
                if (this.fCurrentEntity.position < this.fCurrentEntity.count && this.fCurrentEntity.ch[this.fCurrentEntity.position++] != '\n') {
                    --this.fCurrentEntity.position;
                }
                n = 10;
            }
        }
        ++this.fCurrentEntity.columnNumber;
        return n;
    }

    public String scanNmtoken() throws IOException {
        int n;
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        int n2 = this.fCurrentEntity.position;
        while (XMLChar.isName(this.fCurrentEntity.ch[this.fCurrentEntity.position])) {
            if (++this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
            n = this.fCurrentEntity.position - n2;
            if (n == this.fCurrentEntity.ch.length) {
                this.resizeBuffer(n2, n);
            } else {
                System.arraycopy(this.fCurrentEntity.ch, n2, this.fCurrentEntity.ch, 0, n);
            }
            n2 = 0;
            if (!this.load(n, false)) continue;
            break;
        }
        n = this.fCurrentEntity.position - n2;
        this.fCurrentEntity.columnNumber += n;
        String string = null;
        if (n > 0) {
            string = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, n2, n);
        }
        return string;
    }

    public String scanName() throws IOException {
        int n;
        int n2;
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        if (XMLChar.isNameStart(this.fCurrentEntity.ch[n2 = this.fCurrentEntity.position++])) {
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[n2];
                n2 = 0;
                if (this.load(1, false)) {
                    ++this.fCurrentEntity.columnNumber;
                    String string = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
                    return string;
                }
            }
            while (XMLChar.isName(this.fCurrentEntity.ch[this.fCurrentEntity.position])) {
                if (++this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
                n = this.fCurrentEntity.position - n2;
                if (n == this.fCurrentEntity.ch.length) {
                    this.resizeBuffer(n2, n);
                } else {
                    System.arraycopy(this.fCurrentEntity.ch, n2, this.fCurrentEntity.ch, 0, n);
                }
                n2 = 0;
                if (!this.load(n, false)) continue;
                break;
            }
        }
        n = this.fCurrentEntity.position - n2;
        this.fCurrentEntity.columnNumber += n;
        String string = null;
        if (n > 0) {
            string = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, n2, n);
        }
        return string;
    }

    public String scanNCName() throws IOException {
        int n;
        int n2;
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        if (XMLChar.isNCNameStart(this.fCurrentEntity.ch[n2 = this.fCurrentEntity.position++])) {
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[n2];
                n2 = 0;
                if (this.load(1, false)) {
                    ++this.fCurrentEntity.columnNumber;
                    String string = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
                    return string;
                }
            }
            while (XMLChar.isNCName(this.fCurrentEntity.ch[this.fCurrentEntity.position])) {
                if (++this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
                n = this.fCurrentEntity.position - n2;
                if (n == this.fCurrentEntity.ch.length) {
                    this.resizeBuffer(n2, n);
                } else {
                    System.arraycopy(this.fCurrentEntity.ch, n2, this.fCurrentEntity.ch, 0, n);
                }
                n2 = 0;
                if (!this.load(n, false)) continue;
                break;
            }
        }
        n = this.fCurrentEntity.position - n2;
        this.fCurrentEntity.columnNumber += n;
        String string = null;
        if (n > 0) {
            string = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, n2, n);
        }
        return string;
    }

    public boolean scanQName(QName qName) throws IOException {
        int n;
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        if (XMLChar.isNCNameStart(this.fCurrentEntity.ch[n = this.fCurrentEntity.position++])) {
            int n2;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[n];
                n = 0;
                if (this.load(1, false)) {
                    ++this.fCurrentEntity.columnNumber;
                    String string = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
                    qName.setValues(null, string, string, null);
                    return true;
                }
            }
            int n3 = -1;
            while (XMLChar.isName(this.fCurrentEntity.ch[this.fCurrentEntity.position])) {
                n2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
                if (n2 == 58) {
                    if (n3 != -1) break;
                    n3 = this.fCurrentEntity.position;
                }
                if (++this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
                int n4 = this.fCurrentEntity.position - n;
                if (n4 == this.fCurrentEntity.ch.length) {
                    this.resizeBuffer(n, n4);
                } else {
                    System.arraycopy(this.fCurrentEntity.ch, n, this.fCurrentEntity.ch, 0, n4);
                }
                if (n3 != -1) {
                    n3 -= n;
                }
                n = 0;
                if (!this.load(n4, false)) continue;
                break;
            }
            n2 = this.fCurrentEntity.position - n;
            this.fCurrentEntity.columnNumber += n2;
            if (n2 > 0) {
                String string = null;
                String string2 = null;
                String string3 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, n, n2);
                if (n3 != -1) {
                    int n5 = n3 - n;
                    string = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, n, n5);
                    int n6 = n2 - n5 - 1;
                    int n7 = n3 + 1;
                    if (!XMLChar.isNCNameStart(this.fCurrentEntity.ch[n7])) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "IllegalQName", null, (short)2);
                    }
                    string2 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, n7, n6);
                } else {
                    string2 = string3;
                }
                qName.setValues(string, string2, string3, null);
                return true;
            }
        }
        return false;
    }

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
        if (n3 == 10 || n3 == 13 && bl) {
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
                    if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
                        ++this.fCurrentEntity.position;
                        ++n2;
                        continue;
                    }
                    ++n4;
                    continue;
                }
                if (n3 == 10) {
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
        while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
            if (XMLChar.isContent(n3 = this.fCurrentEntity.ch[this.fCurrentEntity.position++])) continue;
            --this.fCurrentEntity.position;
            break;
        }
        n = this.fCurrentEntity.position - n2;
        this.fCurrentEntity.columnNumber += n - n4;
        xMLString.setValues(this.fCurrentEntity.ch, n2, n);
        if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
            n3 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (n3 == 13 && bl) {
                n3 = 10;
            }
        } else {
            n3 = -1;
        }
        return n3;
    }

    public int scanLiteral(int n, XMLString xMLString) throws IOException {
        int n2;
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        } else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
            this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[this.fCurrentEntity.count - 1];
            this.load(1, false);
            this.fCurrentEntity.position = 0;
            this.fCurrentEntity.startPosition = 0;
        }
        int n3 = this.fCurrentEntity.position;
        int n4 = this.fCurrentEntity.ch[n3];
        int n5 = 0;
        boolean bl = this.fCurrentEntity.isExternal();
        if (n4 == 10 || n4 == 13 && bl) {
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
                    if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
                        ++this.fCurrentEntity.position;
                        ++n3;
                        continue;
                    }
                    ++n5;
                    continue;
                }
                if (n4 == 10) {
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
        while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
            if (((n4 = this.fCurrentEntity.ch[this.fCurrentEntity.position++]) != n || this.fCurrentEntity.literal && !bl) && n4 != 37 && (XMLChar.isContent(n4) || n4 == 13 && !bl)) continue;
            --this.fCurrentEntity.position;
            break;
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

    public boolean scanData(String string, XMLStringBuffer xMLStringBuffer) throws IOException {
        int n;
        boolean bl = false;
        int n2 = string.length();
        char c = string.charAt(0);
        boolean bl2 = this.fCurrentEntity.isExternal();
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        boolean bl3 = false;
        while (this.fCurrentEntity.position > this.fCurrentEntity.count - n2 && !bl3) {
            System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.ch, 0, this.fCurrentEntity.count - this.fCurrentEntity.position);
            bl3 = this.load(this.fCurrentEntity.count - this.fCurrentEntity.position, false);
            this.fCurrentEntity.position = 0;
            this.fCurrentEntity.startPosition = 0;
        }
        if (this.fCurrentEntity.position > this.fCurrentEntity.count - n2) {
            int n3 = this.fCurrentEntity.count - this.fCurrentEntity.position;
            xMLStringBuffer.append(this.fCurrentEntity.ch, this.fCurrentEntity.position, n3);
            this.fCurrentEntity.columnNumber += this.fCurrentEntity.count;
            this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
            this.fCurrentEntity.position = this.fCurrentEntity.count;
            this.fCurrentEntity.startPosition = this.fCurrentEntity.count;
            this.load(0, true);
            return false;
        }
        int n4 = this.fCurrentEntity.position;
        char c2 = this.fCurrentEntity.ch[n4];
        int n5 = 0;
        if (c2 == '\n' || c2 == '\r' && bl2) {
            do {
                if ((c2 = this.fCurrentEntity.ch[this.fCurrentEntity.position++]) == '\r' && bl2) {
                    ++n5;
                    ++this.fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                        n4 = 0;
                        this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                        this.fCurrentEntity.position = n5;
                        this.fCurrentEntity.startPosition = n5;
                        if (this.load(n5, false)) break;
                    }
                    if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
                        ++this.fCurrentEntity.position;
                        ++n4;
                        continue;
                    }
                    ++n5;
                    continue;
                }
                if (c2 == '\n') {
                    ++n5;
                    ++this.fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
                    n4 = 0;
                    this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                    this.fCurrentEntity.position = n5;
                    this.fCurrentEntity.startPosition = n5;
                    this.fCurrentEntity.count = n5;
                    if (!this.load(n5, false)) continue;
                    break;
                }
                --this.fCurrentEntity.position;
                break;
            } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
            for (n = n4; n < this.fCurrentEntity.position; ++n) {
                this.fCurrentEntity.ch[n] = 10;
            }
            n = this.fCurrentEntity.position - n4;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                xMLStringBuffer.append(this.fCurrentEntity.ch, n4, n);
                return true;
            }
        }
        block3: while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
            if ((c2 = this.fCurrentEntity.ch[this.fCurrentEntity.position++]) == c) {
                n = this.fCurrentEntity.position - 1;
                for (int i = 1; i < n2; ++i) {
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                        this.fCurrentEntity.position -= i;
                        break block3;
                    }
                    c2 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                    if (string.charAt(i) == c2) continue;
                    --this.fCurrentEntity.position;
                    break;
                }
                if (this.fCurrentEntity.position != n + n2) continue;
                bl = true;
                break;
            }
            if (c2 == '\n' || bl2 && c2 == '\r') {
                --this.fCurrentEntity.position;
                break;
            }
            if (!XMLChar.isInvalid(c2)) continue;
            --this.fCurrentEntity.position;
            n = this.fCurrentEntity.position - n4;
            this.fCurrentEntity.columnNumber += n - n5;
            xMLStringBuffer.append(this.fCurrentEntity.ch, n4, n);
            return true;
        }
        n = this.fCurrentEntity.position - n4;
        this.fCurrentEntity.columnNumber += n - n5;
        if (bl) {
            n -= n2;
        }
        xMLStringBuffer.append(this.fCurrentEntity.ch, n4, n);
        return !bl;
    }

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
        if (n == 10 && c == '\r' && this.fCurrentEntity.isExternal()) {
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c;
                this.load(1, false);
            }
            ++this.fCurrentEntity.position;
            if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
                ++this.fCurrentEntity.position;
            }
            ++this.fCurrentEntity.lineNumber;
            this.fCurrentEntity.columnNumber = 1;
            return true;
        }
        return false;
    }

    public boolean skipSpaces() throws IOException {
        char c;
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        if (XMLChar.isSpace(c = this.fCurrentEntity.ch[this.fCurrentEntity.position])) {
            boolean bl = this.fCurrentEntity.isExternal();
            do {
                boolean bl2 = false;
                if (c == '\n' || bl && c == '\r') {
                    ++this.fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                        this.fCurrentEntity.ch[0] = c;
                        bl2 = this.load(1, true);
                        if (!bl2) {
                            this.fCurrentEntity.position = 0;
                            this.fCurrentEntity.startPosition = 0;
                        }
                    }
                    if (c == '\r' && bl && this.fCurrentEntity.ch[++this.fCurrentEntity.position] != '\n') {
                        --this.fCurrentEntity.position;
                    }
                } else {
                    ++this.fCurrentEntity.columnNumber;
                }
                if (!bl2) {
                    ++this.fCurrentEntity.position;
                }
                if (this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
                this.load(0, true);
            } while (XMLChar.isSpace(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
            return true;
        }
        return false;
    }

    public final boolean skipDeclSpaces() throws IOException {
        char c;
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        if (XMLChar.isSpace(c = this.fCurrentEntity.ch[this.fCurrentEntity.position])) {
            boolean bl = this.fCurrentEntity.isExternal();
            do {
                boolean bl2 = false;
                if (c == '\n' || bl && c == '\r') {
                    ++this.fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                        this.fCurrentEntity.ch[0] = c;
                        bl2 = this.load(1, true);
                        if (!bl2) {
                            this.fCurrentEntity.position = 0;
                            this.fCurrentEntity.startPosition = 0;
                        }
                    }
                    if (c == '\r' && bl && this.fCurrentEntity.ch[++this.fCurrentEntity.position] != '\n') {
                        --this.fCurrentEntity.position;
                    }
                } else {
                    ++this.fCurrentEntity.columnNumber;
                }
                if (!bl2) {
                    ++this.fCurrentEntity.position;
                }
                if (this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
                this.load(0, true);
            } while (XMLChar.isSpace(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
            return true;
        }
        return false;
    }

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

    @Override
    public final String getPublicId() {
        return this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null ? this.fCurrentEntity.entityLocation.getPublicId() : null;
    }

    @Override
    public final String getExpandedSystemId() {
        if (this.fCurrentEntity != null) {
            if (this.fCurrentEntity.entityLocation != null && this.fCurrentEntity.entityLocation.getExpandedSystemId() != null) {
                return this.fCurrentEntity.entityLocation.getExpandedSystemId();
            }
            return this.fCurrentEntity.getExpandedSystemId();
        }
        return null;
    }

    @Override
    public final String getLiteralSystemId() {
        if (this.fCurrentEntity != null) {
            if (this.fCurrentEntity.entityLocation != null && this.fCurrentEntity.entityLocation.getLiteralSystemId() != null) {
                return this.fCurrentEntity.entityLocation.getLiteralSystemId();
            }
            return this.fCurrentEntity.getLiteralSystemId();
        }
        return null;
    }

    @Override
    public final int getLineNumber() {
        if (this.fCurrentEntity != null) {
            if (this.fCurrentEntity.isExternal()) {
                return this.fCurrentEntity.lineNumber;
            }
            return this.fCurrentEntity.getLineNumber();
        }
        return -1;
    }

    @Override
    public final int getColumnNumber() {
        if (this.fCurrentEntity != null) {
            if (this.fCurrentEntity.isExternal()) {
                return this.fCurrentEntity.columnNumber;
            }
            return this.fCurrentEntity.getColumnNumber();
        }
        return -1;
    }

    @Override
    public final int getCharacterOffset() {
        if (this.fCurrentEntity != null) {
            if (this.fCurrentEntity.isExternal()) {
                return this.fCurrentEntity.baseCharOffset + (this.fCurrentEntity.position - this.fCurrentEntity.startPosition);
            }
            return this.fCurrentEntity.getCharacterOffset();
        }
        return -1;
    }

    @Override
    public final String getEncoding() {
        if (this.fCurrentEntity != null) {
            if (this.fCurrentEntity.isExternal()) {
                return this.fCurrentEntity.encoding;
            }
            return this.fCurrentEntity.getEncoding();
        }
        return null;
    }

    @Override
    public final String getXMLVersion() {
        if (this.fCurrentEntity != null) {
            if (this.fCurrentEntity.isExternal()) {
                return this.fCurrentEntity.xmlVersion;
            }
            return this.fCurrentEntity.getXMLVersion();
        }
        return null;
    }

    public final void setCurrentEntity(XMLEntityManager.ScannedEntity scannedEntity) {
        this.fCurrentEntity = scannedEntity;
    }

    public final void setBufferSize(int n) {
        this.fBufferSize = n;
    }

    public final void reset(SymbolTable symbolTable, XMLEntityManager xMLEntityManager, XMLErrorReporter xMLErrorReporter) {
        this.fCurrentEntity = null;
        this.fSymbolTable = symbolTable;
        this.fEntityManager = xMLEntityManager;
        this.fErrorReporter = xMLErrorReporter;
    }

    final boolean load(int n, boolean bl) throws IOException {
        this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
        int n2 = this.fCurrentEntity.ch.length - n;
        if (!this.fCurrentEntity.mayReadChunks && n2 > 64) {
            n2 = 64;
        }
        int n3 = this.fCurrentEntity.reader.read(this.fCurrentEntity.ch, n, n2);
        boolean bl2 = false;
        if (n3 != -1) {
            if (n3 != 0) {
                this.fCurrentEntity.count = n3 + n;
                this.fCurrentEntity.position = n;
                this.fCurrentEntity.startPosition = n;
            }
        } else {
            this.fCurrentEntity.count = n;
            this.fCurrentEntity.position = n;
            this.fCurrentEntity.startPosition = n;
            bl2 = true;
            if (bl) {
                this.fEntityManager.endEntity();
                if (this.fCurrentEntity == null) {
                    throw END_OF_DOCUMENT_ENTITY;
                }
                if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                    this.load(0, true);
                }
            }
        }
        return bl2;
    }

    final void resizeBuffer(int n, int n2) {
        char[] cArray = new char[this.fCurrentEntity.ch.length << 1];
        System.arraycopy(this.fCurrentEntity.ch, n, cArray, 0, n2);
        this.fCurrentEntity.ch = cArray;
    }
}

