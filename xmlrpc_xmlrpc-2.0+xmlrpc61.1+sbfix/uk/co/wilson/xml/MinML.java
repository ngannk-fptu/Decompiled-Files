/*
 * Decompiled with CFR 0.152.
 */
package uk.co.wilson.xml;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.EmptyStackException;
import java.util.Locale;
import java.util.Stack;
import java.util.Vector;
import org.xml.sax.AttributeList;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import uk.org.xml.sax.DocumentHandler;
import uk.org.xml.sax.Parser;

public class MinML
implements Parser,
Locator,
DocumentHandler,
ErrorHandler {
    public static final int endStartName = 0;
    public static final int emitStartElement = 1;
    public static final int emitEndElement = 2;
    public static final int possiblyEmitCharacters = 3;
    public static final int emitCharacters = 4;
    public static final int emitCharactersSave = 5;
    public static final int saveAttributeName = 6;
    public static final int saveAttributeValue = 7;
    public static final int startComment = 8;
    public static final int endComment = 9;
    public static final int incLevel = 10;
    public static final int decLevel = 11;
    public static final int startCDATA = 12;
    public static final int endCDATA = 13;
    public static final int processCharRef = 14;
    public static final int writeCdata = 15;
    public static final int exitParser = 16;
    public static final int parseError = 17;
    public static final int discardAndChange = 18;
    public static final int discardSaveAndChange = 19;
    public static final int saveAndChange = 20;
    public static final int change = 21;
    public static final int inSkipping = 0;
    public static final int inSTag = 1;
    public static final int inPossiblyAttribute = 2;
    public static final int inNextAttribute = 3;
    public static final int inAttribute = 4;
    public static final int inAttribute1 = 5;
    public static final int inAttributeValue = 6;
    public static final int inAttributeQuoteValue = 7;
    public static final int inAttributeQuotesValue = 8;
    public static final int inETag = 9;
    public static final int inETag1 = 10;
    public static final int inMTTag = 11;
    public static final int inTag = 12;
    public static final int inTag1 = 13;
    public static final int inPI = 14;
    public static final int inPI1 = 15;
    public static final int inPossiblySkipping = 16;
    public static final int inCharData = 17;
    public static final int inCDATA = 18;
    public static final int inCDATA1 = 19;
    public static final int inComment = 20;
    public static final int inDTD = 21;
    private DocumentHandler extDocumentHandler = this;
    private org.xml.sax.DocumentHandler documentHandler = this;
    private ErrorHandler errorHandler = this;
    private final Stack tags = new Stack();
    private int lineNumber = 1;
    private int columnNumber = 0;
    private final int initialBufferSize;
    private final int bufferIncrement;
    private static final byte[] charClasses = new byte[]{13, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 12, -1, -1, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 8, 7, 14, 14, 14, 3, 6, 14, 14, 14, 14, 14, 11, 14, 2, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 0, 5, 1, 4, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 9, 14, 10};
    private static final String[] operands = new String[]{"\u0d15\u1611\u1611\u1611\u1611\u1611\u1611\u1611\u1611\u1611\u1611\u1611\u0015\u0010\u1611", "\u1711\u1000\u0b00\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u0114\u0200\u1811\u0114", "\u1711\u1001\u0b01\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u0215\u1811\u0414", "\u1711\u1001\u0b01\u1711\u1911\u1911\u1911\u1911\u1911\u1911\u1911\u1911\u0315\u1811\u0414", "\u1911\u1911\u1911\u1911\u1911\u0606\u1911\u1911\u1911\u1911\u1911\u0414\u0515\u1811\u0414", "\u1911\u1911\u1911\u1911\u1911\u0606\u1911\u1911\u1911\u1911\u1911\u1911\u0515\u1811\u1911", "\u1a11\u1a11\u1a11\u1a11\u1a11\u1a11\u0715\u0815\u1a11\u1a11\u1a11\u1a11\u0615\u1811\u1a11", "\u0714\u0714\u0714\u070e\u0714\u0714\u0307\u0714\u0714\u0714\u0714\u0714\u0714\u1811\u0714", "\u0814\u0814\u0814\u080e\u0814\u0814\u0814\u0307\u0814\u0814\u0814\u0814\u0814\u1811\u0814", "\u1711\u1002\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u0914\u0915\u1811\u0914", "\u1b11\u1b11\u0904\u1b11\u1b11\u1b11\u1b11\u1b11\u1215\u1b11\u1b11\u1b11\u1b11\u1811\u0105", "\u1711\u1012\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u1811\u1711", "\u1711\u1c11\u0912\u1711\u0e12\u1711\u1711\u1711\u1212\u1711\u1711\u1711\u1711\u1811\u0113", "\u1711\u1c11\u0912\u1711\u0e12\u1711\u1711\u1711\u1212\u1711\u1711\u1711\u1711\u1811\u0113", "\u0e15\u0e15\u0e15\u0e15\u0f15\u0e15\u0e15\u0e15\u0e15\u0e15\u0e15\u0e15\u0e15\u1811\u0e15", "\u0e15\u0015\u0e15\u0e15\u0f15\u0e15\u0e15\u0e15\u0e15\u0e15\u0e15\u0e15\u0e15\u1811\u0e15", "\u0c03\u110f\u110f\u110e\u110f\u110f\u110f\u110f\u110f\u110f\u110f\u110f\u1014\u1811\u110f", "\u0a15\u110f\u110f\u110e\u110f\u110f\u110f\u110f\u110f\u110f\u110f\u110f\u110f\u1811\u110f", "\u1d11\u1d11\u1d11\u1d11\u1d11\u1d11\u1d11\u1d11\u1d11\u130c\u1d11\u1408\u1d11\u1811\u1515", "\u130f\u130f\u130f\u130f\u130f\u130f\u130f\u130f\u130f\u130f\u110d\u130f\u130f\u1811\u130f", "\u1415\u1415\u1415\u1415\u1415\u1415\u1415\u1415\u1415\u1415\u1415\t\u1415\u1811\u1415", "\u150a\u000b\u1515\u1515\u1515\u1515\u1515\u1515\u1515\u1515\u1515\u1515\u1515\u1811\u1515", "expected Element", "unexpected character in tag", "unexpected end of file found", "attribute name not followed by '='", "invalid attribute value", "expecting end tag", "empty tag", "unexpected character after <!"};

    public MinML(int initialBufferSize, int bufferIncrement) {
        this.initialBufferSize = initialBufferSize;
        this.bufferIncrement = bufferIncrement;
    }

    public MinML() {
        this(256, 128);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void parse(Reader in) throws SAXException, IOException {
        attributeNames = new Vector<String>();
        attributeValues = new Vector<String>();
        attrs = new AttributeList(){

            public int getLength() {
                return attributeNames.size();
            }

            public String getName(int i) {
                return (String)attributeNames.elementAt(i);
            }

            public String getType(int i) {
                return "CDATA";
            }

            public String getValue(int i) {
                return (String)attributeValues.elementAt(i);
            }

            public String getType(String name) {
                return "CDATA";
            }

            public String getValue(String name) {
                int index = attributeNames.indexOf(name);
                return index == -1 ? null : (String)attributeValues.elementAt(index);
            }
        };
        buffer = new MinMLBuffer(in);
        currentChar = 0;
        charCount = 0;
        level = 0;
        mixedContentLevel = -1;
        elementName = null;
        state = MinML.operands[0];
        this.lineNumber = 1;
        this.columnNumber = 0;
        try {
            try {
                block29: while (true) {
                    ++charCount;
                    v0 = currentChar = MinMLBuffer.access$000(buffer) == MinMLBuffer.access$100(buffer) ? buffer.read() : MinMLBuffer.access$200(buffer)[MinMLBuffer.access$008(buffer)];
                    if (currentChar > 93) {
                        transition = state.charAt(14);
                    } else {
                        charClass = MinML.charClasses[currentChar + 1];
                        if (charClass == -1) {
                            this.fatalError("Document contains illegal control character with value " + currentChar, this.lineNumber, this.columnNumber);
                        }
                        if (charClass == 12) {
                            if (currentChar == 13) {
                                currentChar = 10;
                                charCount = -1;
                            }
                            if (currentChar == 10) {
                                if (charCount == 0) continue;
                                if (charCount != -1) {
                                    charCount = 0;
                                }
                                ++this.lineNumber;
                                this.columnNumber = 0;
                            }
                        }
                        transition = state.charAt(charClass);
                    }
                    ++this.columnNumber;
                    operand = MinML.operands[transition >>> 8];
                    block3 : switch (transition & 255) {
                        case 0: {
                            elementName = buffer.getString();
                            if (currentChar != 62 && currentChar != 47) break;
                        }
                        case 1: {
                            newWriter = this.extDocumentHandler.startElement(elementName, attrs, this.tags.empty() != false ? this.extDocumentHandler.startDocument(buffer) : buffer.getWriter());
                            buffer.pushWriter(newWriter);
                            this.tags.push(elementName);
                            attributeValues.removeAllElements();
                            attributeNames.removeAllElements();
                            if (mixedContentLevel != -1) {
                                ++mixedContentLevel;
                            }
                            if (currentChar != 47) break;
                        }
                        case 2: {
                            try {
                                begin = (String)this.tags.pop();
                                buffer.popWriter();
                                elementName = buffer.getString();
                                if (currentChar != 47 && !elementName.equals(begin)) {
                                    this.fatalError("end tag </" + elementName + "> does not match begin tag <" + begin + ">", this.lineNumber, this.columnNumber);
                                } else {
                                    this.documentHandler.endElement(begin);
                                    if (this.tags.empty()) {
                                        this.documentHandler.endDocument();
                                    }
                                }
                                ** GOTO lbl73
                            }
                            catch (EmptyStackException e) {
                                this.fatalError("end tag at begining of document", this.lineNumber, this.columnNumber);
                                ** GOTO lbl73
                            }
                        }
                        var20_23 = null;
                        this.errorHandler = this;
                        this.extDocumentHandler = this;
                        this.documentHandler = this.extDocumentHandler;
                        this.tags.removeAllElements();
                        return;
lbl73:
                        // 3 sources

                        if (mixedContentLevel == -1) break;
                        --mixedContentLevel;
                        break;
                        case 4: {
                            buffer.flush();
                            break;
                        }
                        case 5: {
                            if (mixedContentLevel == -1) {
                                mixedContentLevel = 0;
                            }
                            buffer.flush();
                            buffer.saveChar((char)currentChar);
                            break;
                        }
                        case 3: {
                            if (mixedContentLevel == -1) break;
                            buffer.flush();
                            break;
                        }
                        case 6: {
                            attributeNames.addElement(buffer.getString());
                            break;
                        }
                        case 7: {
                            attributeValues.addElement(buffer.getString());
                            break;
                        }
                        case 8: {
                            if (buffer.read() == 45) break;
                            continue block29;
                        }
                        case 9: {
                            currentChar = buffer.read();
                            if (currentChar != 45) continue block29;
                            while ((currentChar = buffer.read()) == 45) {
                            }
                            if (currentChar != 62) continue block29;
                            break;
                        }
                        case 10: {
                            ++level;
                            break;
                        }
                        case 11: {
                            if (level == 0) break;
                            --level;
                            continue block29;
                        }
                        case 12: {
                            if (buffer.read() == 67 && buffer.read() == 68 && buffer.read() == 65 && buffer.read() == 84 && buffer.read() == 65 && buffer.read() == 91) break;
                            continue block29;
                        }
                        case 13: {
                            currentChar = buffer.read();
                            if (currentChar == 93) {
                                while ((currentChar = buffer.read()) == 93) {
                                    buffer.write(93);
                                }
                                if (currentChar == 62) break;
                                buffer.write(93);
                            }
                            buffer.write(93);
                            buffer.write(currentChar);
                            continue block29;
                        }
                        case 14: {
                            crefState = 0;
                            currentChar = buffer.read();
                            while (true) {
                                if ("#amp;&pos;'quot;\"gt;>lt;<".charAt(crefState) != currentChar) ** GOTO lbl157
                                ++crefState;
                                if (currentChar == 59) {
                                    buffer.write("#amp;&pos;'quot;\"gt;>lt;<".charAt(crefState));
                                    break block3;
                                }
                                if (currentChar != 35) ** GOTO lbl155
                                currentChar = buffer.read();
                                if (currentChar == 120) {
                                    radix = 16;
                                    currentChar = buffer.read();
                                } else {
                                    radix = 10;
                                }
                                charRef = Character.digit((char)currentChar, radix);
                                while (true) {
                                    if ((digit = Character.digit((char)(currentChar = buffer.read()), radix)) != -1) ** GOTO lbl148
                                    if (currentChar == 59) {
                                        break;
                                    }
                                    ** GOTO lbl153
lbl148:
                                    // 1 sources

                                    charRef = (char)(charRef * radix + digit);
                                }
                                if (charRef != -1) {
                                    buffer.write(charRef);
                                    break block3;
                                }
lbl153:
                                // 3 sources

                                this.fatalError("invalid Character Entitiy", this.lineNumber, this.columnNumber);
                                continue;
lbl155:
                                // 1 sources

                                currentChar = buffer.read();
                                continue;
lbl157:
                                // 1 sources

                                if ((crefState = (int)"\u0001\u000b\u0006\u00ff\u00ff\u00ff\u00ff\u00ff\u00ff\u00ff\u00ff\u0011\u00ff\u00ff\u00ff\u00ff\u00ff\u0015\u00ff\u00ff\u00ff\u00ff\u00ff\u00ff".charAt(crefState)) != 255) continue;
                                this.fatalError("invalid Character Entitiy", this.lineNumber, this.columnNumber);
                            }
                        }
                        case 17: {
                            this.fatalError(operand, this.lineNumber, this.columnNumber);
                        }
                        case 16: {
                            var20_24 = null;
                            this.errorHandler = this;
                            this.extDocumentHandler = this;
                            this.documentHandler = this.extDocumentHandler;
                            this.tags.removeAllElements();
                            return;
                        }
                        case 15: {
                            buffer.write(currentChar);
                            break;
                        }
                        case 18: {
                            buffer.reset();
                            break;
                        }
                        case 19: {
                            buffer.reset();
                        }
                        case 20: {
                            buffer.saveChar((char)currentChar);
                            break;
                        }
                    }
                    state = operand;
                }
            }
            catch (IOException e) {
                this.errorHandler.fatalError(new SAXParseException(e.toString(), null, null, this.lineNumber, this.columnNumber, e));
                var20_25 = null;
                this.errorHandler = this;
                this.extDocumentHandler = this;
                this.documentHandler = this.extDocumentHandler;
                this.tags.removeAllElements();
                return;
            }
        }
        catch (Throwable var19_27) {
            var20_26 = null;
            this.errorHandler = this;
            this.extDocumentHandler = this;
            this.documentHandler = this.extDocumentHandler;
            this.tags.removeAllElements();
            throw var19_27;
        }
    }

    public void parse(InputSource source) throws SAXException, IOException {
        if (source.getCharacterStream() != null) {
            this.parse(source.getCharacterStream());
        } else if (source.getByteStream() != null) {
            this.parse(new InputStreamReader(source.getByteStream()));
        } else {
            this.parse(new InputStreamReader(new URL(source.getSystemId()).openStream()));
        }
    }

    public void parse(String systemId) throws SAXException, IOException {
        this.parse(new InputSource(systemId));
    }

    public void setLocale(Locale locale) throws SAXException {
        throw new SAXException("Not supported");
    }

    public void setEntityResolver(EntityResolver resolver) {
    }

    public void setDTDHandler(DTDHandler handler) {
    }

    public void setDocumentHandler(org.xml.sax.DocumentHandler handler) {
        this.documentHandler = handler == null ? this : handler;
        this.extDocumentHandler = this;
    }

    public void setDocumentHandler(DocumentHandler handler) {
        this.extDocumentHandler = handler == null ? this : handler;
        this.documentHandler = this.extDocumentHandler;
        this.documentHandler.setDocumentLocator(this);
    }

    public void setErrorHandler(ErrorHandler handler) {
        this.errorHandler = handler == null ? this : handler;
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void startDocument() throws SAXException {
    }

    public Writer startDocument(Writer writer) throws SAXException {
        this.documentHandler.startDocument();
        return writer;
    }

    public void endDocument() throws SAXException {
    }

    public void startElement(String name, AttributeList attributes) throws SAXException {
    }

    public Writer startElement(String name, AttributeList attributes, Writer writer) throws SAXException {
        this.documentHandler.startElement(name, attributes);
        return writer;
    }

    public void endElement(String name) throws SAXException {
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void warning(SAXParseException e) throws SAXException {
    }

    public void error(SAXParseException e) throws SAXException {
    }

    public void fatalError(SAXParseException e) throws SAXException {
        throw e;
    }

    public String getPublicId() {
        return "";
    }

    public String getSystemId() {
        return "";
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public int getColumnNumber() {
        return this.columnNumber;
    }

    private void fatalError(String msg, int lineNumber, int columnNumber) throws SAXException {
        this.errorHandler.fatalError(new SAXParseException(msg, null, null, lineNumber, columnNumber));
    }

    static /* synthetic */ int access$600(MinML x0) {
        return x0.initialBufferSize;
    }

    private class MinMLBuffer
    extends Writer {
        private int nextIn = 0;
        private int lastIn = 0;
        private char[] chars = new char[MinML.access$600(MinML.this)];
        private final Reader in;
        private int count = 0;
        private Writer writer = this;
        private boolean flushed = false;
        private boolean written = false;

        public MinMLBuffer(Reader in) {
            this.in = in;
        }

        public void close() throws IOException {
            this.flush();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void flush() throws IOException {
            try {
                this._flush();
                if (this.writer != this) {
                    this.writer.flush();
                }
            }
            finally {
                this.flushed = true;
            }
        }

        public void write(int c) throws IOException {
            this.written = true;
            this.chars[this.count++] = (char)c;
        }

        public void write(char[] cbuf, int off, int len) throws IOException {
            this.written = true;
            System.arraycopy(cbuf, off, this.chars, this.count, len);
            this.count += len;
        }

        public void saveChar(char c) {
            this.written = false;
            this.chars[this.count++] = c;
        }

        public void pushWriter(Writer writer) {
            MinML.this.tags.push(this.writer);
            this.writer = writer == null ? this : writer;
            this.written = false;
            this.flushed = false;
        }

        public Writer getWriter() {
            return this.writer;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void popWriter() throws IOException {
            try {
                if (!this.flushed && this.writer != this) {
                    this.writer.flush();
                }
                Object var2_1 = null;
            }
            catch (Throwable throwable) {
                Object var2_2 = null;
                this.writer = (Writer)MinML.this.tags.pop();
                this.written = false;
                this.flushed = false;
                throw throwable;
            }
            this.writer = (Writer)MinML.this.tags.pop();
            this.written = false;
            this.flushed = false;
        }

        public String getString() {
            String result = new String(this.chars, 0, this.count);
            this.count = 0;
            return result;
        }

        public void reset() {
            this.count = 0;
        }

        public int read() throws IOException {
            if (this.nextIn == this.lastIn) {
                int numRead;
                if (this.count != 0) {
                    if (this.written) {
                        this._flush();
                    } else if (this.count >= this.chars.length - MinML.this.bufferIncrement) {
                        char[] newChars = new char[this.chars.length + MinML.this.bufferIncrement];
                        System.arraycopy(this.chars, 0, newChars, 0, this.count);
                        this.chars = newChars;
                    }
                }
                if ((numRead = this.in.read(this.chars, this.count, this.chars.length - this.count)) == -1) {
                    return -1;
                }
                this.nextIn = this.count;
                this.lastIn = this.count + numRead;
            }
            return this.chars[this.nextIn++];
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void _flush() throws IOException {
            block7: {
                if (this.count != 0) {
                    try {
                        if (this.writer == this) {
                            try {
                                MinML.this.documentHandler.characters(this.chars, 0, this.count);
                                break block7;
                            }
                            catch (SAXException e) {
                                throw new IOException(e.toString());
                            }
                        }
                        this.writer.write(this.chars, 0, this.count);
                    }
                    finally {
                        this.count = 0;
                    }
                }
            }
        }

        static /* synthetic */ int access$000(MinMLBuffer x0) {
            return x0.nextIn;
        }

        static /* synthetic */ int access$100(MinMLBuffer x0) {
            return x0.lastIn;
        }

        static /* synthetic */ char[] access$200(MinMLBuffer x0) {
            return x0.chars;
        }

        static /* synthetic */ int access$008(MinMLBuffer x0) {
            return x0.nextIn++;
        }
    }
}

