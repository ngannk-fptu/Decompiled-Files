/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.xmlparser;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;
import java.util.jar.JarFile;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.JspCompilationContext;
import org.apache.sling.scripting.jsp.jasper.compiler.ErrorDispatcher;
import org.apache.sling.scripting.jsp.jasper.compiler.JspUtil;
import org.apache.sling.scripting.jsp.jasper.xmlparser.ASCIIReader;
import org.apache.sling.scripting.jsp.jasper.xmlparser.EncodingMap;
import org.apache.sling.scripting.jsp.jasper.xmlparser.SymbolTable;
import org.apache.sling.scripting.jsp.jasper.xmlparser.UCSReader;
import org.apache.sling.scripting.jsp.jasper.xmlparser.UTF8Reader;
import org.apache.sling.scripting.jsp.jasper.xmlparser.XMLChar;
import org.apache.sling.scripting.jsp.jasper.xmlparser.XMLString;
import org.apache.sling.scripting.jsp.jasper.xmlparser.XMLStringBuffer;

public class XMLEncodingDetector {
    private InputStream stream;
    private String encoding;
    private boolean isEncodingSetInProlog;
    private boolean isBomPresent;
    private int skip;
    private Boolean isBigEndian;
    private Reader reader;
    public static final int DEFAULT_BUFFER_SIZE = 2048;
    public static final int DEFAULT_XMLDECL_BUFFER_SIZE = 64;
    private boolean fAllowJavaEncodings;
    private SymbolTable fSymbolTable;
    private XMLEncodingDetector fCurrentEntity;
    private int fBufferSize = 2048;
    private int lineNumber = 1;
    private int columnNumber = 1;
    private boolean literal;
    private char[] ch = new char[2048];
    private int position;
    private int count;
    private boolean mayReadChunks = false;
    private XMLString fString = new XMLString();
    private XMLStringBuffer fStringBuffer = new XMLStringBuffer();
    private XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
    private static final String fVersionSymbol = "version";
    private static final String fEncodingSymbol = "encoding";
    private static final String fStandaloneSymbol = "standalone";
    private int fMarkupDepth = 0;
    private String[] fStrings = new String[3];
    private ErrorDispatcher err;

    public XMLEncodingDetector() {
        this.fSymbolTable = new SymbolTable();
        this.fCurrentEntity = this;
    }

    public static Object[] getEncoding(String fname, JarFile jarFile, JspCompilationContext ctxt, ErrorDispatcher err) throws IOException, JasperException {
        InputStream inStream = JspUtil.getInputStream(fname, jarFile, ctxt, err);
        XMLEncodingDetector detector = new XMLEncodingDetector();
        Object[] ret = detector.getEncoding(inStream, err);
        inStream.close();
        return ret;
    }

    private Object[] getEncoding(InputStream in, ErrorDispatcher err) throws IOException, JasperException {
        this.stream = in;
        this.err = err;
        this.createInitialReader();
        this.scanXMLDecl();
        return new Object[]{this.encoding, this.isEncodingSetInProlog, this.isBomPresent, this.skip};
    }

    void endEntity() {
    }

    private void createInitialReader() throws IOException, JasperException {
        this.stream = new RewindableInputStream(this.stream);
        if (this.encoding == null) {
            int count;
            byte[] b4 = new byte[4];
            for (count = 0; count < 4; ++count) {
                b4[count] = (byte)this.stream.read();
            }
            if (count == 4) {
                Object[] encodingDesc = this.getEncodingName(b4, count);
                this.encoding = (String)encodingDesc[0];
                this.isBigEndian = (Boolean)encodingDesc[1];
                if (encodingDesc.length > 3) {
                    this.isBomPresent = (Boolean)encodingDesc[2];
                    this.skip = (Integer)encodingDesc[3];
                } else {
                    this.isBomPresent = true;
                    this.skip = (Integer)encodingDesc[2];
                }
                this.stream.reset();
                if (count > 2 && this.encoding.equals("UTF-8")) {
                    int b0 = b4[0] & 0xFF;
                    int b1 = b4[1] & 0xFF;
                    int b2 = b4[2] & 0xFF;
                    if (b0 == 239 && b1 == 187 && b2 == 191) {
                        this.stream.skip(3L);
                    }
                }
                this.reader = this.createReader(this.stream, this.encoding, this.isBigEndian);
            } else {
                this.reader = this.createReader(this.stream, this.encoding, this.isBigEndian);
            }
        }
    }

    private Reader createReader(InputStream inputStream, String encoding, Boolean isBigEndian) throws IOException, JasperException {
        String javaEncoding;
        String ENCODING;
        if (encoding == null) {
            encoding = "UTF-8";
        }
        if ((ENCODING = encoding.toUpperCase(Locale.ENGLISH)).equals("UTF-8")) {
            return new UTF8Reader(inputStream, this.fBufferSize);
        }
        if (ENCODING.equals("US-ASCII")) {
            return new ASCIIReader(inputStream, this.fBufferSize);
        }
        if (ENCODING.equals("ISO-10646-UCS-4")) {
            if (isBigEndian != null) {
                boolean isBE = isBigEndian;
                if (isBE) {
                    return new UCSReader(inputStream, 8);
                }
                return new UCSReader(inputStream, 4);
            }
            this.err.jspError("jsp.error.xml.encodingByteOrderUnsupported", encoding);
        }
        if (ENCODING.equals("ISO-10646-UCS-2")) {
            if (isBigEndian != null) {
                boolean isBE = isBigEndian;
                if (isBE) {
                    return new UCSReader(inputStream, 2);
                }
                return new UCSReader(inputStream, 1);
            }
            this.err.jspError("jsp.error.xml.encodingByteOrderUnsupported", encoding);
        }
        boolean validIANA = XMLChar.isValidIANAEncoding(encoding);
        boolean validJava = XMLChar.isValidJavaEncoding(encoding);
        if (!validIANA || this.fAllowJavaEncodings && !validJava) {
            this.err.jspError("jsp.error.xml.encodingDeclInvalid", encoding);
            encoding = "ISO-8859-1";
        }
        if ((javaEncoding = EncodingMap.getIANA2JavaMapping(ENCODING)) == null) {
            if (this.fAllowJavaEncodings) {
                javaEncoding = encoding;
            } else {
                this.err.jspError("jsp.error.xml.encodingDeclInvalid", encoding);
                javaEncoding = "ISO8859_1";
            }
        }
        return new InputStreamReader(inputStream, javaEncoding);
    }

    private Object[] getEncodingName(byte[] b4, int count) {
        if (count < 2) {
            return new Object[]{"UTF-8", null, Boolean.FALSE, 0};
        }
        int b0 = b4[0] & 0xFF;
        int b1 = b4[1] & 0xFF;
        if (b0 == 254 && b1 == 255) {
            return new Object[]{"UTF-16BE", Boolean.TRUE, 2};
        }
        if (b0 == 255 && b1 == 254) {
            return new Object[]{"UTF-16LE", Boolean.FALSE, 2};
        }
        if (count < 3) {
            return new Object[]{"UTF-8", null, Boolean.FALSE, 0};
        }
        int b2 = b4[2] & 0xFF;
        if (b0 == 239 && b1 == 187 && b2 == 191) {
            return new Object[]{"UTF-8", null, 3};
        }
        if (count < 4) {
            return new Object[]{"UTF-8", null, 0};
        }
        int b3 = b4[3] & 0xFF;
        if (b0 == 0 && b1 == 0 && b2 == 0 && b3 == 60) {
            return new Object[]{"ISO-10646-UCS-4", new Boolean(true), 4};
        }
        if (b0 == 60 && b1 == 0 && b2 == 0 && b3 == 0) {
            return new Object[]{"ISO-10646-UCS-4", new Boolean(false), 4};
        }
        if (b0 == 0 && b1 == 0 && b2 == 60 && b3 == 0) {
            return new Object[]{"ISO-10646-UCS-4", null, 4};
        }
        if (b0 == 0 && b1 == 60 && b2 == 0 && b3 == 0) {
            return new Object[]{"ISO-10646-UCS-4", null, 4};
        }
        if (b0 == 0 && b1 == 60 && b2 == 0 && b3 == 63) {
            return new Object[]{"UTF-16BE", new Boolean(true), 4};
        }
        if (b0 == 60 && b1 == 0 && b2 == 63 && b3 == 0) {
            return new Object[]{"UTF-16LE", new Boolean(false), 4};
        }
        if (b0 == 76 && b1 == 111 && b2 == 167 && b3 == 148) {
            return new Object[]{"CP037", null, 4};
        }
        return new Object[]{"UTF-8", null, Boolean.FALSE, 0};
    }

    public boolean isExternal() {
        return true;
    }

    public int peekChar() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        int c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        if (this.fCurrentEntity.isExternal()) {
            return c != 13 ? c : 10;
        }
        return c;
    }

    public int scanChar() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        int c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
        boolean external = false;
        if (c == 10 || c == 13 && (external = this.fCurrentEntity.isExternal())) {
            ++this.fCurrentEntity.lineNumber;
            this.fCurrentEntity.columnNumber = 1;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = (char)c;
                this.load(1, false);
            }
            if (c == 13 && external) {
                if (this.fCurrentEntity.ch[this.fCurrentEntity.position++] != '\n') {
                    --this.fCurrentEntity.position;
                }
                c = 10;
            }
        }
        ++this.fCurrentEntity.columnNumber;
        return c;
    }

    public String scanName() throws IOException {
        int length;
        int offset;
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        if (XMLChar.isNameStart(this.fCurrentEntity.ch[offset = this.fCurrentEntity.position++])) {
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[offset];
                offset = 0;
                if (this.load(1, false)) {
                    ++this.fCurrentEntity.columnNumber;
                    String symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
                    return symbol;
                }
            }
            while (XMLChar.isName(this.fCurrentEntity.ch[this.fCurrentEntity.position])) {
                if (++this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
                length = this.fCurrentEntity.position - offset;
                if (length == this.fBufferSize) {
                    char[] tmp = new char[this.fBufferSize * 2];
                    System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
                    this.fCurrentEntity.ch = tmp;
                    this.fBufferSize *= 2;
                } else {
                    System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
                }
                offset = 0;
                if (!this.load(length, false)) continue;
                break;
            }
        }
        length = this.fCurrentEntity.position - offset;
        this.fCurrentEntity.columnNumber += length;
        String symbol = null;
        if (length > 0) {
            symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
        }
        return symbol;
    }

    public int scanLiteral(int quote, XMLString content) throws IOException {
        int length;
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        } else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
            this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[this.fCurrentEntity.count - 1];
            this.load(1, false);
            this.fCurrentEntity.position = 0;
        }
        int offset = this.fCurrentEntity.position;
        int c = this.fCurrentEntity.ch[offset];
        int newlines = 0;
        boolean external = this.fCurrentEntity.isExternal();
        if (c == 10 || c == 13 && external) {
            do {
                if ((c = this.fCurrentEntity.ch[this.fCurrentEntity.position++]) == 13 && external) {
                    ++newlines;
                    ++this.fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                        offset = 0;
                        this.fCurrentEntity.position = newlines;
                        if (this.load(newlines, false)) break;
                    }
                    if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
                        ++this.fCurrentEntity.position;
                        ++offset;
                        continue;
                    }
                    ++newlines;
                    continue;
                }
                if (c == 10) {
                    ++newlines;
                    ++this.fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
                    offset = 0;
                    this.fCurrentEntity.position = newlines;
                    if (!this.load(newlines, false)) continue;
                    break;
                }
                --this.fCurrentEntity.position;
                break;
            } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
            for (int i = offset; i < this.fCurrentEntity.position; ++i) {
                this.fCurrentEntity.ch[i] = 10;
            }
            length = this.fCurrentEntity.position - offset;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                content.setValues(this.fCurrentEntity.ch, offset, length);
                return -1;
            }
        }
        while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
            if (((c = this.fCurrentEntity.ch[this.fCurrentEntity.position++]) != quote || this.fCurrentEntity.literal && !external) && c != 37 && XMLChar.isContent(c)) continue;
            --this.fCurrentEntity.position;
            break;
        }
        length = this.fCurrentEntity.position - offset;
        this.fCurrentEntity.columnNumber += length - newlines;
        content.setValues(this.fCurrentEntity.ch, offset, length);
        if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
            c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (c == quote && this.fCurrentEntity.literal) {
                c = -1;
            }
        } else {
            c = -1;
        }
        return c;
    }

    public boolean scanData(String delimiter, XMLStringBuffer buffer) throws IOException {
        boolean done = false;
        int delimLen = delimiter.length();
        char charAt0 = delimiter.charAt(0);
        boolean external = this.fCurrentEntity.isExternal();
        do {
            int length;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.load(0, true);
            } else if (this.fCurrentEntity.position >= this.fCurrentEntity.count - delimLen) {
                System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.ch, 0, this.fCurrentEntity.count - this.fCurrentEntity.position);
                this.load(this.fCurrentEntity.count - this.fCurrentEntity.position, false);
                this.fCurrentEntity.position = 0;
            }
            if (this.fCurrentEntity.position >= this.fCurrentEntity.count - delimLen) {
                int length2 = this.fCurrentEntity.count - this.fCurrentEntity.position;
                buffer.append(this.fCurrentEntity.ch, this.fCurrentEntity.position, length2);
                this.fCurrentEntity.columnNumber += this.fCurrentEntity.count;
                this.fCurrentEntity.position = this.fCurrentEntity.count;
                this.load(0, true);
                return false;
            }
            int offset = this.fCurrentEntity.position;
            char c = this.fCurrentEntity.ch[offset];
            int newlines = 0;
            if (c == '\n' || c == '\r' && external) {
                do {
                    if ((c = this.fCurrentEntity.ch[this.fCurrentEntity.position++]) == '\r' && external) {
                        ++newlines;
                        ++this.fCurrentEntity.lineNumber;
                        this.fCurrentEntity.columnNumber = 1;
                        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                            offset = 0;
                            this.fCurrentEntity.position = newlines;
                            if (this.load(newlines, false)) break;
                        }
                        if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
                            ++this.fCurrentEntity.position;
                            ++offset;
                            continue;
                        }
                        ++newlines;
                        continue;
                    }
                    if (c == '\n') {
                        ++newlines;
                        ++this.fCurrentEntity.lineNumber;
                        this.fCurrentEntity.columnNumber = 1;
                        if (this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
                        offset = 0;
                        this.fCurrentEntity.position = newlines;
                        this.fCurrentEntity.count = newlines;
                        if (!this.load(newlines, false)) continue;
                        break;
                    }
                    --this.fCurrentEntity.position;
                    break;
                } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
                for (int i = offset; i < this.fCurrentEntity.position; ++i) {
                    this.fCurrentEntity.ch[i] = 10;
                }
                length = this.fCurrentEntity.position - offset;
                if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                    buffer.append(this.fCurrentEntity.ch, offset, length);
                    return true;
                }
            }
            block3: while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
                if ((c = this.fCurrentEntity.ch[this.fCurrentEntity.position++]) == charAt0) {
                    int delimOffset = this.fCurrentEntity.position - 1;
                    for (int i = 1; i < delimLen; ++i) {
                        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                            this.fCurrentEntity.position -= i;
                            break block3;
                        }
                        c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                        if (delimiter.charAt(i) == c) continue;
                        --this.fCurrentEntity.position;
                        break;
                    }
                    if (this.fCurrentEntity.position != delimOffset + delimLen) continue;
                    done = true;
                    break;
                }
                if (c == '\n' || external && c == '\r') {
                    --this.fCurrentEntity.position;
                    break;
                }
                if (!XMLChar.isInvalid(c)) continue;
                --this.fCurrentEntity.position;
                length = this.fCurrentEntity.position - offset;
                this.fCurrentEntity.columnNumber += length - newlines;
                buffer.append(this.fCurrentEntity.ch, offset, length);
                return true;
            }
            length = this.fCurrentEntity.position - offset;
            this.fCurrentEntity.columnNumber += length - newlines;
            if (done) {
                length -= delimLen;
            }
            buffer.append(this.fCurrentEntity.ch, offset, length);
        } while (!done);
        return !done;
    }

    public boolean skipChar(int c) throws IOException {
        char cc;
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        if ((cc = this.fCurrentEntity.ch[this.fCurrentEntity.position]) == c) {
            ++this.fCurrentEntity.position;
            if (c == 10) {
                ++this.fCurrentEntity.lineNumber;
                this.fCurrentEntity.columnNumber = 1;
            } else {
                ++this.fCurrentEntity.columnNumber;
            }
            return true;
        }
        if (c == 10 && cc == '\r' && this.fCurrentEntity.isExternal()) {
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = cc;
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
            boolean external = this.fCurrentEntity.isExternal();
            do {
                boolean entityChanged = false;
                if (c == '\n' || external && c == '\r') {
                    ++this.fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                        this.fCurrentEntity.ch[0] = c;
                        entityChanged = this.load(1, true);
                        if (!entityChanged) {
                            this.fCurrentEntity.position = 0;
                        }
                    }
                    if (c == '\r' && external && this.fCurrentEntity.ch[++this.fCurrentEntity.position] != '\n') {
                        --this.fCurrentEntity.position;
                    }
                } else {
                    ++this.fCurrentEntity.columnNumber;
                }
                if (!entityChanged) {
                    ++this.fCurrentEntity.position;
                }
                if (this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
                this.load(0, true);
            } while (XMLChar.isSpace(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
            return true;
        }
        return false;
    }

    public boolean skipString(String s) throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        int length = s.length();
        for (int i = 0; i < length; ++i) {
            char c;
            if ((c = this.fCurrentEntity.ch[this.fCurrentEntity.position++]) != s.charAt(i)) {
                this.fCurrentEntity.position -= i + 1;
                return false;
            }
            if (i >= length - 1 || this.fCurrentEntity.position != this.fCurrentEntity.count) continue;
            System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.count - i - 1, this.fCurrentEntity.ch, 0, i + 1);
            if (!this.load(i + 1, false)) continue;
            this.fCurrentEntity.position -= i + 1;
            return false;
        }
        this.fCurrentEntity.columnNumber += length;
        return true;
    }

    final boolean load(int offset, boolean changeEntity) throws IOException {
        int length = this.fCurrentEntity.mayReadChunks ? this.fCurrentEntity.ch.length - offset : 64;
        int count = this.fCurrentEntity.reader.read(this.fCurrentEntity.ch, offset, length);
        boolean entityChanged = false;
        if (count != -1) {
            if (count != 0) {
                this.fCurrentEntity.count = count + offset;
                this.fCurrentEntity.position = offset;
            }
        } else {
            this.fCurrentEntity.count = offset;
            this.fCurrentEntity.position = offset;
            entityChanged = true;
            if (changeEntity) {
                this.endEntity();
                if (this.fCurrentEntity == null) {
                    throw new EOFException();
                }
                if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                    this.load(0, false);
                }
            }
        }
        return entityChanged;
    }

    private void scanXMLDecl() throws IOException, JasperException {
        if (this.skipString("<?xml")) {
            ++this.fMarkupDepth;
            if (XMLChar.isName(this.peekChar())) {
                this.fStringBuffer.clear();
                this.fStringBuffer.append("xml");
                while (XMLChar.isName(this.peekChar())) {
                    this.fStringBuffer.append((char)this.scanChar());
                }
                String target = this.fSymbolTable.addSymbol(this.fStringBuffer.ch, this.fStringBuffer.offset, this.fStringBuffer.length);
                this.scanPIData(target, this.fString);
            } else {
                this.scanXMLDeclOrTextDecl(false);
            }
        }
    }

    private void scanXMLDeclOrTextDecl(boolean scanningTextDecl) throws IOException, JasperException {
        this.scanXMLDeclOrTextDecl(scanningTextDecl, this.fStrings);
        --this.fMarkupDepth;
        String encodingPseudoAttr = this.fStrings[1];
        if (encodingPseudoAttr != null) {
            this.isEncodingSetInProlog = true;
            this.encoding = encodingPseudoAttr;
        }
    }

    private void scanXMLDeclOrTextDecl(boolean scanningTextDecl, String[] pseudoAttributeValues) throws IOException, JasperException {
        String version = null;
        String encoding = null;
        String standalone = null;
        boolean STATE_VERSION = false;
        boolean STATE_ENCODING = true;
        int STATE_STANDALONE = 2;
        int STATE_DONE = 3;
        int state = 0;
        boolean dataFoundForTarget = false;
        boolean sawSpace = this.skipSpaces();
        while (this.peekChar() != 63) {
            dataFoundForTarget = true;
            String name = this.scanPseudoAttribute(scanningTextDecl, this.fString);
            switch (state) {
                case 0: {
                    if (name == fVersionSymbol) {
                        if (!sawSpace) {
                            this.reportFatalError(scanningTextDecl ? "jsp.error.xml.spaceRequiredBeforeVersionInTextDecl" : "jsp.error.xml.spaceRequiredBeforeVersionInXMLDecl", null);
                        }
                        version = this.fString.toString();
                        state = 1;
                        if (version.equals("1.0")) break;
                        this.err.jspError("jsp.error.xml.versionNotSupported", version);
                        break;
                    }
                    if (name == fEncodingSymbol) {
                        if (!scanningTextDecl) {
                            this.err.jspError("jsp.error.xml.versionInfoRequired");
                        }
                        if (!sawSpace) {
                            this.reportFatalError(scanningTextDecl ? "jsp.error.xml.spaceRequiredBeforeEncodingInTextDecl" : "jsp.error.xml.spaceRequiredBeforeEncodingInXMLDecl", null);
                        }
                        encoding = this.fString.toString();
                        state = scanningTextDecl ? 3 : 2;
                        break;
                    }
                    if (scanningTextDecl) {
                        this.err.jspError("jsp.error.xml.encodingDeclRequired");
                        break;
                    }
                    this.err.jspError("jsp.error.xml.versionInfoRequired");
                    break;
                }
                case 1: {
                    if (name == fEncodingSymbol) {
                        if (!sawSpace) {
                            this.reportFatalError(scanningTextDecl ? "jsp.error.xml.spaceRequiredBeforeEncodingInTextDecl" : "jsp.error.xml.spaceRequiredBeforeEncodingInXMLDecl", null);
                        }
                        encoding = this.fString.toString();
                        state = scanningTextDecl ? 3 : 2;
                        break;
                    }
                    if (!scanningTextDecl && name == fStandaloneSymbol) {
                        if (!sawSpace) {
                            this.err.jspError("jsp.error.xml.spaceRequiredBeforeStandalone");
                        }
                        standalone = this.fString.toString();
                        state = 3;
                        if (standalone.equals("yes") || standalone.equals("no")) break;
                        this.err.jspError("jsp.error.xml.sdDeclInvalid");
                        break;
                    }
                    this.err.jspError("jsp.error.xml.encodingDeclRequired");
                    break;
                }
                case 2: {
                    if (name == fStandaloneSymbol) {
                        if (!sawSpace) {
                            this.err.jspError("jsp.error.xml.spaceRequiredBeforeStandalone");
                        }
                        standalone = this.fString.toString();
                        state = 3;
                        if (standalone.equals("yes") || standalone.equals("no")) break;
                        this.err.jspError("jsp.error.xml.sdDeclInvalid");
                        break;
                    }
                    this.err.jspError("jsp.error.xml.encodingDeclRequired");
                    break;
                }
                default: {
                    this.err.jspError("jsp.error.xml.noMorePseudoAttributes");
                }
            }
            sawSpace = this.skipSpaces();
        }
        if (scanningTextDecl && state != 3) {
            this.err.jspError("jsp.error.xml.morePseudoAttributes");
        }
        if (scanningTextDecl) {
            if (!dataFoundForTarget && encoding == null) {
                this.err.jspError("jsp.error.xml.encodingDeclRequired");
            }
        } else if (!dataFoundForTarget && version == null) {
            this.err.jspError("jsp.error.xml.versionInfoRequired");
        }
        if (!this.skipChar(63)) {
            this.err.jspError("jsp.error.xml.xmlDeclUnterminated");
        }
        if (!this.skipChar(62)) {
            this.err.jspError("jsp.error.xml.xmlDeclUnterminated");
        }
        pseudoAttributeValues[0] = version;
        pseudoAttributeValues[1] = encoding;
        pseudoAttributeValues[2] = standalone;
    }

    public String scanPseudoAttribute(boolean scanningTextDecl, XMLString value) throws IOException, JasperException {
        String name = this.scanName();
        if (name == null) {
            this.err.jspError("jsp.error.xml.pseudoAttrNameExpected");
        }
        this.skipSpaces();
        if (!this.skipChar(61)) {
            this.reportFatalError(scanningTextDecl ? "jsp.error.xml.eqRequiredInTextDecl" : "jsp.error.xml.eqRequiredInXMLDecl", name);
        }
        this.skipSpaces();
        int quote = this.peekChar();
        if (quote != 39 && quote != 34) {
            this.reportFatalError(scanningTextDecl ? "jsp.error.xml.quoteRequiredInTextDecl" : "jsp.error.xml.quoteRequiredInXMLDecl", name);
        }
        this.scanChar();
        int c = this.scanLiteral(quote, value);
        if (c != quote) {
            this.fStringBuffer2.clear();
            do {
                this.fStringBuffer2.append(value);
                if (c == -1) continue;
                if (c == 38 || c == 37 || c == 60 || c == 93) {
                    this.fStringBuffer2.append((char)this.scanChar());
                    continue;
                }
                if (XMLChar.isHighSurrogate(c)) {
                    this.scanSurrogates(this.fStringBuffer2);
                    continue;
                }
                if (!XMLChar.isInvalid(c)) continue;
                String key = scanningTextDecl ? "jsp.error.xml.invalidCharInTextDecl" : "jsp.error.xml.invalidCharInXMLDecl";
                this.reportFatalError(key, Integer.toString(c, 16));
                this.scanChar();
            } while ((c = this.scanLiteral(quote, value)) != quote);
            this.fStringBuffer2.append(value);
            value.setValues(this.fStringBuffer2);
        }
        if (!this.skipChar(quote)) {
            this.reportFatalError(scanningTextDecl ? "jsp.error.xml.closeQuoteMissingInTextDecl" : "jsp.error.xml.closeQuoteMissingInXMLDecl", name);
        }
        return name;
    }

    private void scanPIData(String target, XMLString data) throws IOException, JasperException {
        if (target.length() == 3) {
            char c0 = Character.toLowerCase(target.charAt(0));
            char c1 = Character.toLowerCase(target.charAt(1));
            char c2 = Character.toLowerCase(target.charAt(2));
            if (c0 == 'x' && c1 == 'm' && c2 == 'l') {
                this.err.jspError("jsp.error.xml.reservedPITarget");
            }
        }
        if (!this.skipSpaces()) {
            if (this.skipString("?>")) {
                data.clear();
                return;
            }
            this.err.jspError("jsp.error.xml.spaceRequiredInPI");
        }
        this.fStringBuffer.clear();
        if (this.scanData("?>", this.fStringBuffer)) {
            do {
                int c;
                if ((c = this.peekChar()) == -1) continue;
                if (XMLChar.isHighSurrogate(c)) {
                    this.scanSurrogates(this.fStringBuffer);
                    continue;
                }
                if (!XMLChar.isInvalid(c)) continue;
                this.err.jspError("jsp.error.xml.invalidCharInPI", Integer.toHexString(c));
                this.scanChar();
            } while (this.scanData("?>", this.fStringBuffer));
        }
        data.setValues(this.fStringBuffer);
    }

    private boolean scanSurrogates(XMLStringBuffer buf) throws IOException, JasperException {
        int high = this.scanChar();
        int low = this.peekChar();
        if (!XMLChar.isLowSurrogate(low)) {
            this.err.jspError("jsp.error.xml.invalidCharInContent", Integer.toString(high, 16));
            return false;
        }
        this.scanChar();
        int c = XMLChar.supplemental((char)high, (char)low);
        if (!XMLChar.isValid(c)) {
            this.err.jspError("jsp.error.xml.invalidCharInContent", Integer.toString(c, 16));
            return false;
        }
        buf.append((char)high);
        buf.append((char)low);
        return true;
    }

    private void reportFatalError(String msgId, String arg) throws JasperException {
        this.err.jspError(msgId, arg);
    }

    private final class RewindableInputStream
    extends InputStream {
        private InputStream fInputStream;
        private byte[] fData = new byte[64];
        private int fStartOffset;
        private int fEndOffset;
        private int fOffset;
        private int fLength;
        private int fMark;

        public RewindableInputStream(InputStream is) {
            this.fInputStream = is;
            this.fStartOffset = 0;
            this.fEndOffset = -1;
            this.fOffset = 0;
            this.fLength = 0;
            this.fMark = 0;
        }

        public void setStartOffset(int offset) {
            this.fStartOffset = offset;
        }

        public void rewind() {
            this.fOffset = this.fStartOffset;
        }

        @Override
        public int read() throws IOException {
            int b = 0;
            if (this.fOffset < this.fLength) {
                return this.fData[this.fOffset++] & 0xFF;
            }
            if (this.fOffset == this.fEndOffset) {
                return -1;
            }
            if (this.fOffset == this.fData.length) {
                byte[] newData = new byte[this.fOffset << 1];
                System.arraycopy(this.fData, 0, newData, 0, this.fOffset);
                this.fData = newData;
            }
            if ((b = this.fInputStream.read()) == -1) {
                this.fEndOffset = this.fOffset;
                return -1;
            }
            this.fData[this.fLength++] = (byte)b;
            ++this.fOffset;
            return b & 0xFF;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int bytesLeft = this.fLength - this.fOffset;
            if (bytesLeft == 0) {
                if (this.fOffset == this.fEndOffset) {
                    return -1;
                }
                if (XMLEncodingDetector.this.fCurrentEntity.mayReadChunks) {
                    return this.fInputStream.read(b, off, len);
                }
                int returnedVal = this.read();
                if (returnedVal == -1) {
                    this.fEndOffset = this.fOffset;
                    return -1;
                }
                b[off] = (byte)returnedVal;
                return 1;
            }
            if (len < bytesLeft) {
                if (len <= 0) {
                    return 0;
                }
            } else {
                len = bytesLeft;
            }
            if (b != null) {
                System.arraycopy(this.fData, this.fOffset, b, off, len);
            }
            this.fOffset += len;
            return len;
        }

        @Override
        public long skip(long n) throws IOException {
            if (n <= 0L) {
                return 0L;
            }
            int bytesLeft = this.fLength - this.fOffset;
            if (bytesLeft == 0) {
                if (this.fOffset == this.fEndOffset) {
                    return 0L;
                }
                return this.fInputStream.skip(n);
            }
            if (n <= (long)bytesLeft) {
                this.fOffset = (int)((long)this.fOffset + n);
                return n;
            }
            this.fOffset += bytesLeft;
            if (this.fOffset == this.fEndOffset) {
                return bytesLeft;
            }
            return this.fInputStream.skip(n -= (long)bytesLeft) + (long)bytesLeft;
        }

        @Override
        public int available() throws IOException {
            int bytesLeft = this.fLength - this.fOffset;
            if (bytesLeft == 0) {
                if (this.fOffset == this.fEndOffset) {
                    return -1;
                }
                return XMLEncodingDetector.this.fCurrentEntity.mayReadChunks ? this.fInputStream.available() : 0;
            }
            return bytesLeft;
        }

        @Override
        public void mark(int howMuch) {
            this.fMark = this.fOffset;
        }

        @Override
        public void reset() {
            this.fOffset = this.fMark;
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public void close() throws IOException {
            if (this.fInputStream != null) {
                this.fInputStream.close();
                this.fInputStream = null;
            }
        }
    }
}

