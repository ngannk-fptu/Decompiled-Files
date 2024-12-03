/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.scanner.dtd;

import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener;
import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;
import com.ctc.wstx.shaded.msv_core.scanner.dtd.EndOfInputException;
import com.ctc.wstx.shaded.msv_core.scanner.dtd.XmlChars;
import com.ctc.wstx.shaded.msv_core.scanner.dtd.XmlReader;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Locale;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class InputEntity {
    private int start;
    private int finish;
    private char[] buf;
    private int lineNumber = 1;
    private boolean returnedFirstHalf = false;
    private boolean maybeInCRLF = false;
    private String name;
    private InputEntity next;
    private InputSource input;
    private Reader reader;
    private boolean isClosed;
    private DTDEventListener errHandler;
    private Locale locale;
    private StringBuffer rememberedText;
    private int startRemember;
    private boolean isPE;
    private static final int BUFSIZ = 8193;
    private static final char[] newline = new char[]{'\n'};

    public static InputEntity getInputEntity(DTDEventListener h, Locale l) {
        InputEntity retval = new InputEntity();
        retval.errHandler = h;
        retval.locale = l;
        return retval;
    }

    private InputEntity() {
    }

    public boolean isInternal() {
        return this.reader == null;
    }

    public boolean isDocument() {
        return this.next == null;
    }

    public boolean isParameterEntity() {
        return this.isPE;
    }

    public String getName() {
        return this.name;
    }

    public void init(InputSource in, String name, InputEntity stack, boolean isPE) throws IOException, SAXException {
        this.input = in;
        this.isPE = isPE;
        this.reader = in.getCharacterStream();
        if (this.reader == null) {
            InputStream bytes = in.getByteStream();
            this.reader = bytes == null ? XmlReader.createReader(new URL(in.getSystemId()).openStream()) : (in.getEncoding() != null ? XmlReader.createReader(in.getByteStream(), in.getEncoding()) : XmlReader.createReader(in.getByteStream()));
        }
        this.next = stack;
        this.buf = new char[8193];
        this.name = name;
        this.checkRecursion(stack);
    }

    public void init(char[] b, String name, InputEntity stack, boolean isPE) throws SAXException {
        this.next = stack;
        this.buf = b;
        this.finish = b.length;
        this.name = name;
        this.isPE = isPE;
        this.checkRecursion(stack);
    }

    private void checkRecursion(InputEntity stack) throws SAXException {
        if (stack == null) {
            return;
        }
        stack = stack.next;
        while (stack != null) {
            if (stack.name != null && stack.name.equals(this.name)) {
                this.fatal("P-069", new Object[]{this.name});
            }
            stack = stack.next;
        }
    }

    public InputEntity pop() throws IOException {
        this.close();
        return this.next;
    }

    public boolean isEOF() throws IOException, SAXException {
        if (this.start >= this.finish) {
            this.fillbuf();
            return this.start >= this.finish;
        }
        return false;
    }

    public String getEncoding() {
        if (this.reader == null) {
            return null;
        }
        if (this.reader instanceof XmlReader) {
            return ((XmlReader)this.reader).getEncoding();
        }
        if (this.reader instanceof InputStreamReader) {
            return ((InputStreamReader)this.reader).getEncoding();
        }
        return null;
    }

    public char getNameChar() throws IOException, SAXException {
        if (this.finish <= this.start) {
            this.fillbuf();
        }
        if (this.finish > this.start) {
            char c;
            if (XmlChars.isNameChar(c = this.buf[this.start++])) {
                return c;
            }
            --this.start;
        }
        return '\u0000';
    }

    public char getc() throws IOException, SAXException {
        if (this.finish <= this.start) {
            this.fillbuf();
        }
        if (this.finish > this.start) {
            char c = this.buf[this.start++];
            if (this.returnedFirstHalf) {
                if (c >= '\udc00' && c <= '\udfff') {
                    this.returnedFirstHalf = false;
                    return c;
                }
                this.fatal("P-070", new Object[]{Integer.toHexString(c)});
            }
            if (c >= ' ' && c <= '\ud7ff' || c == '\t' || c >= '\ue000' && c <= '\ufffd') {
                return c;
            }
            if (c == '\r' && !this.isInternal()) {
                this.maybeInCRLF = true;
                c = this.getc();
                if (c != '\n') {
                    this.ungetc();
                }
                this.maybeInCRLF = false;
                ++this.lineNumber;
                return '\n';
            }
            if (c == '\n' || c == '\r') {
                if (!this.isInternal() && !this.maybeInCRLF) {
                    ++this.lineNumber;
                }
                return c;
            }
            if (c >= '\ud800' && c < '\udc00') {
                this.returnedFirstHalf = true;
                return c;
            }
            this.fatal("P-071", new Object[]{Integer.toHexString(c)});
        }
        throw new EndOfInputException();
    }

    public boolean peekc(char c) throws IOException, SAXException {
        if (this.finish <= this.start) {
            this.fillbuf();
        }
        if (this.finish > this.start) {
            if (this.buf[this.start] == c) {
                ++this.start;
                return true;
            }
            return false;
        }
        return false;
    }

    public void ungetc() {
        if (this.start == 0) {
            throw new InternalError("ungetc");
        }
        --this.start;
        if (this.buf[this.start] == '\n' || this.buf[this.start] == '\r') {
            if (!this.isInternal()) {
                --this.lineNumber;
            }
        } else if (this.returnedFirstHalf) {
            this.returnedFirstHalf = false;
        }
    }

    public boolean maybeWhitespace() throws IOException, SAXException {
        boolean isSpace = false;
        boolean sawCR = false;
        while (true) {
            char c;
            if (this.finish <= this.start) {
                this.fillbuf();
            }
            if (this.finish <= this.start) {
                return isSpace;
            }
            if ((c = this.buf[this.start++]) != ' ' && c != '\t' && c != '\n' && c != '\r') break;
            isSpace = true;
            if (c != '\n' && c != '\r' || this.isInternal()) continue;
            if (c != '\n' || !sawCR) {
                ++this.lineNumber;
                sawCR = false;
            }
            if (c != '\r') continue;
            sawCR = true;
        }
        --this.start;
        return isSpace;
    }

    /*
     * Enabled aggressive block sorting
     */
    public boolean parsedContent(DTDEventListener docHandler) throws IOException, SAXException {
        int last;
        int first = last = this.start;
        boolean sawContent = false;
        while (true) {
            block21: {
                char c;
                block26: {
                    block25: {
                        block24: {
                            block23: {
                                block22: {
                                    if (last < this.finish) break block22;
                                    if (last > first) {
                                        docHandler.characters(this.buf, first, last - first);
                                        sawContent = true;
                                        this.start = last;
                                    }
                                    if (this.isEOF()) {
                                        return sawContent;
                                    }
                                    first = this.start;
                                    last = first - 1;
                                    break block21;
                                }
                                c = this.buf[last];
                                if (c > ']' && c <= '\ud7ff' || c < '&' && c >= ' ' || c > '<' && c < ']' || c > '&' && c < '<' || c == '\t' || c >= '\ue000' && c <= '\ufffd') break block21;
                                if (c == '<' || c == '&') break;
                                if (c != '\n') break block23;
                                if (!this.isInternal()) {
                                    ++this.lineNumber;
                                }
                                break block21;
                            }
                            if (c != '\r') break block24;
                            if (!this.isInternal()) {
                                docHandler.characters(this.buf, first, last - first);
                                docHandler.characters(newline, 0, 1);
                                sawContent = true;
                                ++this.lineNumber;
                                if (this.finish > last + 1 && this.buf[last + 1] == '\n') {
                                    ++last;
                                }
                                first = this.start = last + 1;
                            }
                            break block21;
                        }
                        if (c != ']') break block25;
                        switch (this.finish - last) {
                            case 2: {
                                if (this.buf[last + 1] != ']') break;
                            }
                            case 1: {
                                if (this.reader != null) {
                                    if (this.isClosed) break;
                                    if (last == first) {
                                        throw new InternalError("fillbuf");
                                    }
                                    if (--last > first) {
                                        docHandler.characters(this.buf, first, last - first);
                                        sawContent = true;
                                        this.start = last;
                                    }
                                    this.fillbuf();
                                    first = last = this.start;
                                    break;
                                }
                                break block21;
                            }
                            default: {
                                if (this.buf[last + 1] == ']' && this.buf[last + 2] == '>') {
                                    this.fatal("P-072", null);
                                    break;
                                }
                                break block21;
                            }
                        }
                        break block21;
                    }
                    if (c < '\ud800' || c > '\udfff') break block26;
                    if (last + 1 >= this.finish) {
                        if (last > first) {
                            docHandler.characters(this.buf, first, last - first);
                            sawContent = true;
                            this.start = last + 1;
                        }
                        if (this.isEOF()) {
                            this.fatal("P-081", new Object[]{Integer.toHexString(c)});
                        }
                        last = first = this.start;
                        break block21;
                    } else if (this.checkSurrogatePair(last)) {
                        ++last;
                        break block21;
                    } else {
                        --last;
                        break;
                    }
                }
                this.fatal("P-071", new Object[]{Integer.toHexString(c)});
            }
            ++last;
        }
        if (last == first) {
            return sawContent;
        }
        docHandler.characters(this.buf, first, last - first);
        this.start = last;
        return true;
    }

    public boolean unparsedContent(DTDEventListener docHandler, boolean ignorableWhitespace, String whitespaceInvalidMessage) throws IOException, SAXException {
        int last;
        if (!this.peek("![CDATA[", null)) {
            return false;
        }
        docHandler.startCDATA();
        while (true) {
            boolean done = false;
            boolean white = ignorableWhitespace;
            for (last = this.start; last < this.finish; ++last) {
                char c = this.buf[last];
                if (!XmlChars.isChar(c)) {
                    white = false;
                    if (c >= '\ud800' && c <= '\udfff') {
                        if (this.checkSurrogatePair(last)) {
                            ++last;
                            continue;
                        }
                        --last;
                        break;
                    }
                    this.fatal("P-071", new Object[]{Integer.toHexString(this.buf[last])});
                }
                if (c == '\n') {
                    if (this.isInternal()) continue;
                    ++this.lineNumber;
                    continue;
                }
                if (c == '\r') {
                    if (this.isInternal()) continue;
                    if (white) {
                        if (whitespaceInvalidMessage != null) {
                            this.errHandler.error(new SAXParseException(DTDParser.messages.getMessage(this.locale, whitespaceInvalidMessage), null));
                        }
                        docHandler.ignorableWhitespace(this.buf, this.start, last - this.start);
                        docHandler.ignorableWhitespace(newline, 0, 1);
                    } else {
                        docHandler.characters(this.buf, this.start, last - this.start);
                        docHandler.characters(newline, 0, 1);
                    }
                    ++this.lineNumber;
                    if (this.finish > last + 1 && this.buf[last + 1] == '\n') {
                        ++last;
                    }
                    this.start = last + 1;
                    continue;
                }
                if (c != ']') {
                    if (c == ' ' || c == '\t') continue;
                    white = false;
                    continue;
                }
                if (last + 2 >= this.finish) break;
                if (this.buf[last + 1] == ']' && this.buf[last + 2] == '>') {
                    done = true;
                    break;
                }
                white = false;
            }
            if (white) {
                if (whitespaceInvalidMessage != null) {
                    this.errHandler.error(new SAXParseException(DTDParser.messages.getMessage(this.locale, whitespaceInvalidMessage), null));
                }
                docHandler.ignorableWhitespace(this.buf, this.start, last - this.start);
            } else {
                docHandler.characters(this.buf, this.start, last - this.start);
            }
            if (done) break;
            this.start = last;
            if (!this.isEOF()) continue;
            this.fatal("P-073", null);
        }
        this.start = last + 3;
        docHandler.endCDATA();
        return true;
    }

    private boolean checkSurrogatePair(int offset) throws SAXException {
        if (offset + 1 >= this.finish) {
            return false;
        }
        char c1 = this.buf[offset++];
        char c2 = this.buf[offset];
        if (c1 >= '\ud800' && c1 < '\udc00' && c2 >= '\udc00' && c2 <= '\udfff') {
            return true;
        }
        this.fatal("P-074", new Object[]{Integer.toHexString(c1 & 0xFFFF), Integer.toHexString(c2 & 0xFFFF)});
        return false;
    }

    public boolean ignorableWhitespace(DTDEventListener handler) throws IOException, SAXException {
        boolean isSpace = false;
        int first = this.start;
        block5: while (true) {
            if (this.finish <= this.start) {
                if (isSpace) {
                    handler.ignorableWhitespace(this.buf, first, this.start - first);
                }
                this.fillbuf();
                first = this.start;
            }
            if (this.finish <= this.start) {
                return isSpace;
            }
            char c = this.buf[this.start++];
            switch (c) {
                case '\n': {
                    if (!this.isInternal()) {
                        ++this.lineNumber;
                    }
                }
                case '\t': 
                case ' ': {
                    isSpace = true;
                    continue block5;
                }
                case '\r': {
                    isSpace = true;
                    if (!this.isInternal()) {
                        ++this.lineNumber;
                    }
                    handler.ignorableWhitespace(this.buf, first, this.start - 1 - first);
                    handler.ignorableWhitespace(newline, 0, 1);
                    if (this.start >= this.finish || this.buf[this.start] == '\n') {
                        // empty if block
                    }
                    first = ++this.start;
                    continue block5;
                }
            }
            break;
        }
        this.ungetc();
        if (isSpace) {
            handler.ignorableWhitespace(this.buf, first, this.start - first);
        }
        return isSpace;
    }

    public boolean peek(String next, char[] chars) throws IOException, SAXException {
        int i;
        int len = chars != null ? chars.length : next.length();
        if (this.finish <= this.start || this.finish - this.start < len) {
            this.fillbuf();
        }
        if (this.finish <= this.start) {
            return false;
        }
        if (chars != null) {
            for (i = 0; i < len && this.start + i < this.finish; ++i) {
                if (this.buf[this.start + i] == chars[i]) continue;
                return false;
            }
        } else {
            for (i = 0; i < len && this.start + i < this.finish; ++i) {
                if (this.buf[this.start + i] == next.charAt(i)) continue;
                return false;
            }
        }
        if (i < len) {
            if (this.reader == null || this.isClosed) {
                return false;
            }
            if (len > this.buf.length) {
                this.fatal("P-077", new Object[]{new Integer(this.buf.length)});
            }
            this.fillbuf();
            return this.peek(next, chars);
        }
        this.start += len;
        return true;
    }

    public void startRemembering() {
        if (this.startRemember != 0) {
            throw new InternalError();
        }
        this.startRemember = this.start;
    }

    public String rememberText() {
        String retval;
        if (this.rememberedText != null) {
            this.rememberedText.append(this.buf, this.startRemember, this.start - this.startRemember);
            retval = this.rememberedText.toString();
        } else {
            retval = new String(this.buf, this.startRemember, this.start - this.startRemember);
        }
        this.startRemember = 0;
        this.rememberedText = null;
        return retval;
    }

    private InputEntity getTopEntity() {
        InputEntity current = this;
        while (current != null && current.input == null) {
            current = current.next;
        }
        return current == null ? this : current;
    }

    public String getPublicId() {
        InputEntity where = this.getTopEntity();
        if (where == this) {
            return this.input.getPublicId();
        }
        return where.getPublicId();
    }

    public String getSystemId() {
        InputEntity where = this.getTopEntity();
        if (where == this) {
            return this.input.getSystemId();
        }
        return where.getSystemId();
    }

    public int getLineNumber() {
        InputEntity where = this.getTopEntity();
        if (where == this) {
            return this.lineNumber;
        }
        return where.getLineNumber();
    }

    public int getColumnNumber() {
        return -1;
    }

    private void fillbuf() throws IOException, SAXException {
        boolean extra;
        if (this.reader == null || this.isClosed) {
            return;
        }
        if (this.startRemember != 0) {
            if (this.rememberedText == null) {
                this.rememberedText = new StringBuffer(this.buf.length);
            }
            this.rememberedText.append(this.buf, this.startRemember, this.start - this.startRemember);
        }
        boolean bl = extra = this.finish > 0 && this.start > 0;
        if (extra) {
            --this.start;
        }
        int len = this.finish - this.start;
        System.arraycopy(this.buf, this.start, this.buf, 0, len);
        this.start = 0;
        this.finish = len;
        try {
            len = this.buf.length - len;
            len = this.reader.read(this.buf, this.finish, len);
        }
        catch (UnsupportedEncodingException e) {
            this.fatal("P-075", new Object[]{e.getMessage()});
        }
        catch (CharConversionException e) {
            this.fatal("P-076", new Object[]{e.getMessage()});
        }
        if (len >= 0) {
            this.finish += len;
        } else {
            this.close();
        }
        if (extra) {
            ++this.start;
        }
        if (this.startRemember != 0) {
            this.startRemember = 1;
        }
    }

    public void close() {
        try {
            if (this.reader != null && !this.isClosed) {
                this.reader.close();
            }
            this.isClosed = true;
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private void fatal(String messageId, Object[] params) throws SAXException {
        SAXParseException x = new SAXParseException(DTDParser.messages.getMessage(this.locale, messageId, params), null);
        this.close();
        this.errHandler.fatalError(x);
        throw x;
    }
}

