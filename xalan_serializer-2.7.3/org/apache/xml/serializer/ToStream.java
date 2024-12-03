/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import org.apache.xml.serializer.CharInfo;
import org.apache.xml.serializer.EncodingInfo;
import org.apache.xml.serializer.Encodings;
import org.apache.xml.serializer.NamespaceMappings;
import org.apache.xml.serializer.SecuritySupport;
import org.apache.xml.serializer.SerializerBase;
import org.apache.xml.serializer.SerializerTraceWriter;
import org.apache.xml.serializer.TreeWalker;
import org.apache.xml.serializer.WriterChain;
import org.apache.xml.serializer.WriterToASCI;
import org.apache.xml.serializer.WriterToUTF8Buffered;
import org.apache.xml.serializer.utils.Utils;
import org.apache.xml.serializer.utils.WrappedRuntimeException;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class ToStream
extends SerializerBase {
    private static final String COMMENT_BEGIN = "<!--";
    private static final String COMMENT_END = "-->";
    protected BoolStack m_disableOutputEscapingStates = new BoolStack();
    EncodingInfo m_encodingInfo = new EncodingInfo(null, null, '\u0000');
    protected BoolStack m_preserves = new BoolStack();
    protected boolean m_ispreserve = false;
    protected boolean m_isprevtext = false;
    private static final char[] s_systemLineSep = SecuritySupport.getSystemProperty("line.separator").toCharArray();
    protected char[] m_lineSep = s_systemLineSep;
    protected boolean m_lineSepUse = true;
    protected int m_lineSepLen = this.m_lineSep.length;
    protected CharInfo m_charInfo;
    boolean m_shouldFlush = true;
    protected boolean m_spaceBeforeClose = false;
    boolean m_startNewLine;
    protected boolean m_inDoctype = false;
    boolean m_isUTF8 = false;
    protected boolean m_cdataStartCalled = false;
    private boolean m_expandDTDEntities = true;
    protected boolean m_escaping = true;
    OutputStream m_outputStream;
    private boolean m_writer_set_by_user;

    protected void closeCDATA() throws SAXException {
        try {
            this.m_writer.write("]]>");
            this.m_cdataTagOpen = false;
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void serialize(Node node) throws IOException {
        try {
            TreeWalker walker = new TreeWalker(this);
            walker.traverse(node);
        }
        catch (SAXException se) {
            throw new WrappedRuntimeException(se);
        }
    }

    protected final void flushWriter() throws SAXException {
        Writer writer = this.m_writer;
        if (null != writer) {
            try {
                if (writer instanceof WriterToUTF8Buffered) {
                    if (this.m_shouldFlush) {
                        ((WriterToUTF8Buffered)writer).flush();
                    } else {
                        ((WriterToUTF8Buffered)writer).flushBuffer();
                    }
                }
                if (writer instanceof WriterToASCI) {
                    if (this.m_shouldFlush) {
                        writer.flush();
                    }
                } else {
                    writer.flush();
                }
            }
            catch (IOException ioe) {
                throw new SAXException(ioe);
            }
        }
    }

    @Override
    public OutputStream getOutputStream() {
        return this.m_outputStream;
    }

    @Override
    public void elementDecl(String name, String model) throws SAXException {
        if (this.m_inExternalDTD) {
            return;
        }
        try {
            Writer writer = this.m_writer;
            this.DTDprolog();
            writer.write("<!ELEMENT ");
            writer.write(name);
            writer.write(32);
            writer.write(model);
            writer.write(62);
            writer.write(this.m_lineSep, 0, this.m_lineSepLen);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void internalEntityDecl(String name, String value) throws SAXException {
        if (this.m_inExternalDTD) {
            return;
        }
        try {
            this.DTDprolog();
            this.outputEntityDecl(name, value);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    void outputEntityDecl(String name, String value) throws IOException {
        Writer writer = this.m_writer;
        writer.write("<!ENTITY ");
        writer.write(name);
        writer.write(" \"");
        writer.write(value);
        writer.write("\">");
        writer.write(this.m_lineSep, 0, this.m_lineSepLen);
    }

    protected final void outputLineSep() throws IOException {
        this.m_writer.write(this.m_lineSep, 0, this.m_lineSepLen);
    }

    @Override
    void setProp(String name, String val, boolean defaultVal) {
        if (val != null) {
            char first = ToStream.getFirstCharLocName(name);
            switch (first) {
                case 'c': {
                    if (!"cdata-section-elements".equals(name)) break;
                    String cdataSectionNames = val;
                    this.addCdataSectionElements(cdataSectionNames);
                    break;
                }
                case 'd': {
                    if ("doctype-system".equals(name)) {
                        this.m_doctypeSystem = val;
                        break;
                    }
                    if (!"doctype-public".equals(name)) break;
                    this.m_doctypePublic = val;
                    if (!val.startsWith("-//W3C//DTD XHTML")) break;
                    this.m_spaceBeforeClose = true;
                    break;
                }
                case 'e': {
                    OutputStream os;
                    String newEncoding = val;
                    if (!"encoding".equals(name)) break;
                    String possible_encoding = Encodings.getMimeEncoding(val);
                    if (possible_encoding != null) {
                        super.setProp("mime-name", possible_encoding, defaultVal);
                    }
                    String oldExplicitEncoding = this.getOutputPropertyNonDefault("encoding");
                    String oldDefaultEncoding = this.getOutputPropertyDefault("encoding");
                    if ((!defaultVal || oldDefaultEncoding != null && oldDefaultEncoding.equalsIgnoreCase(newEncoding)) && (defaultVal || oldExplicitEncoding != null && oldExplicitEncoding.equalsIgnoreCase(newEncoding))) break;
                    EncodingInfo encodingInfo = Encodings.getEncodingInfo(newEncoding);
                    if (newEncoding != null && encodingInfo.name == null) {
                        String msg = Utils.messages.createMessage("ER_ENCODING_NOT_SUPPORTED", new Object[]{newEncoding});
                        String msg2 = "Warning: encoding \"" + newEncoding + "\" not supported, using " + "UTF-8";
                        try {
                            Transformer tran = super.getTransformer();
                            if (tran != null) {
                                ErrorListener errHandler = tran.getErrorListener();
                                if (null != errHandler && this.m_sourceLocator != null) {
                                    errHandler.warning(new TransformerException(msg, this.m_sourceLocator));
                                    errHandler.warning(new TransformerException(msg2, this.m_sourceLocator));
                                } else {
                                    System.out.println(msg);
                                    System.out.println(msg2);
                                }
                            } else {
                                System.out.println(msg);
                                System.out.println(msg2);
                            }
                        }
                        catch (Exception tran) {
                            // empty catch block
                        }
                        newEncoding = "UTF-8";
                        val = "UTF-8";
                        encodingInfo = Encodings.getEncodingInfo(newEncoding);
                    }
                    if (defaultVal && oldExplicitEncoding != null) break;
                    this.m_encodingInfo = encodingInfo;
                    if (newEncoding != null) {
                        this.m_isUTF8 = newEncoding.equals("UTF-8");
                    }
                    if ((os = this.getOutputStream()) == null) break;
                    Writer w = this.getWriter();
                    String oldEncoding = this.getOutputProperty("encoding");
                    if (w != null && this.m_writer_set_by_user || newEncoding.equalsIgnoreCase(oldEncoding)) break;
                    super.setProp(name, val, defaultVal);
                    this.setOutputStreamInternal(os, false);
                    break;
                }
                case 'i': {
                    boolean b;
                    if ("{http://xml.apache.org/xalan}indent-amount".equals(name)) {
                        this.setIndentAmount(Integer.parseInt(val));
                        break;
                    }
                    if (!"indent".equals(name)) break;
                    this.m_doIndent = b = "yes".equals(val);
                    break;
                }
                case 'l': {
                    if (!"{http://xml.apache.org/xalan}line-separator".equals(name)) break;
                    this.m_lineSep = val.toCharArray();
                    this.m_lineSepLen = this.m_lineSep.length;
                    break;
                }
                case 'm': {
                    if (!"media-type".equals(name)) break;
                    this.m_mediatype = val;
                    break;
                }
                case 'o': {
                    boolean b;
                    if (!"omit-xml-declaration".equals(name)) break;
                    this.m_shouldNotWriteXMLHeader = b = "yes".equals(val);
                    break;
                }
                case 's': {
                    if (!"standalone".equals(name)) break;
                    if (defaultVal) {
                        this.setStandaloneInternal(val);
                        break;
                    }
                    this.m_standaloneWasSpecified = true;
                    this.setStandaloneInternal(val);
                    break;
                }
                case 'v': {
                    if (!"version".equals(name)) break;
                    this.m_version = val;
                    break;
                }
            }
            super.setProp(name, val, defaultVal);
        }
    }

    @Override
    public void setOutputFormat(Properties format) {
        String entitiesFileName;
        boolean shouldFlush = this.m_shouldFlush;
        if (format != null) {
            Enumeration<?> propNames = format.propertyNames();
            while (propNames.hasMoreElements()) {
                String key = (String)propNames.nextElement();
                String value = format.getProperty(key);
                String explicitValue = (String)format.get(key);
                if (explicitValue == null && value != null) {
                    this.setOutputPropertyDefault(key, value);
                }
                if (explicitValue == null) continue;
                this.setOutputProperty(key, explicitValue);
            }
        }
        if (null != (entitiesFileName = (String)format.get("{http://xml.apache.org/xalan}entities"))) {
            String method = (String)format.get("method");
            this.m_charInfo = CharInfo.getCharInfo(entitiesFileName, method);
        }
        this.m_shouldFlush = shouldFlush;
    }

    @Override
    public Properties getOutputFormat() {
        Properties def = new Properties();
        Set s = this.getOutputPropDefaultKeys();
        for (String key : s) {
            String val = this.getOutputPropertyDefault(key);
            def.put(key, val);
        }
        Properties props = new Properties(def);
        Set s2 = this.getOutputPropKeys();
        for (String key : s2) {
            String val = this.getOutputPropertyNonDefault(key);
            if (val == null) continue;
            props.put(key, val);
        }
        return props;
    }

    @Override
    public void setWriter(Writer writer) {
        this.setWriterInternal(writer, true);
    }

    private void setWriterInternal(Writer writer, boolean setByUser) {
        this.m_writer_set_by_user = setByUser;
        this.m_writer = writer;
        if (this.m_tracer != null) {
            boolean noTracerYet = true;
            Writer w2 = this.m_writer;
            while (w2 instanceof WriterChain) {
                if (w2 instanceof SerializerTraceWriter) {
                    noTracerYet = false;
                    break;
                }
                w2 = ((WriterChain)((Object)w2)).getWriter();
            }
            if (noTracerYet) {
                this.m_writer = new SerializerTraceWriter(this.m_writer, this.m_tracer);
            }
        }
    }

    public boolean setLineSepUse(boolean use_sytem_line_break) {
        boolean oldValue = this.m_lineSepUse;
        this.m_lineSepUse = use_sytem_line_break;
        return oldValue;
    }

    @Override
    public void setOutputStream(OutputStream output) {
        this.setOutputStreamInternal(output, true);
    }

    private void setOutputStreamInternal(OutputStream output, boolean setByUser) {
        this.m_outputStream = output;
        String encoding = this.getOutputProperty("encoding");
        if ("UTF-8".equalsIgnoreCase(encoding)) {
            this.setWriterInternal(new WriterToUTF8Buffered(output), false);
        } else if ("WINDOWS-1250".equals(encoding) || "US-ASCII".equals(encoding) || "ASCII".equals(encoding)) {
            this.setWriterInternal(new WriterToASCI(output), false);
        } else if (encoding != null) {
            Writer osw = null;
            try {
                osw = Encodings.getWriter(output, encoding);
            }
            catch (UnsupportedEncodingException uee) {
                osw = null;
            }
            if (osw == null) {
                System.out.println("Warning: encoding \"" + encoding + "\" not supported, using " + "UTF-8");
                encoding = "UTF-8";
                this.setEncoding(encoding);
                try {
                    osw = Encodings.getWriter(output, encoding);
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            this.setWriterInternal(osw, false);
        } else {
            OutputStreamWriter osw = new OutputStreamWriter(output);
            this.setWriterInternal(osw, false);
        }
    }

    @Override
    public boolean setEscaping(boolean escape) {
        boolean temp = this.m_escaping;
        this.m_escaping = escape;
        return temp;
    }

    protected void indent(int depth) throws IOException {
        if (this.m_startNewLine) {
            this.outputLineSep();
        }
        if (this.m_indentAmount > 0) {
            this.printSpace(depth * this.m_indentAmount);
        }
    }

    protected void indent() throws IOException {
        this.indent(this.m_elemContext.m_currentElemDepth);
    }

    private void printSpace(int n) throws IOException {
        Writer writer = this.m_writer;
        for (int i = 0; i < n; ++i) {
            writer.write(32);
        }
    }

    @Override
    public void attributeDecl(String eName, String aName, String type, String valueDefault, String value) throws SAXException {
        if (this.m_inExternalDTD) {
            return;
        }
        try {
            Writer writer = this.m_writer;
            this.DTDprolog();
            writer.write("<!ATTLIST ");
            writer.write(eName);
            writer.write(32);
            writer.write(aName);
            writer.write(32);
            writer.write(type);
            if (valueDefault != null) {
                writer.write(32);
                writer.write(valueDefault);
            }
            writer.write(62);
            writer.write(this.m_lineSep, 0, this.m_lineSepLen);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public Writer getWriter() {
        return this.m_writer;
    }

    @Override
    public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {
        try {
            this.DTDprolog();
            this.m_writer.write("<!ENTITY ");
            this.m_writer.write(name);
            if (publicId != null) {
                this.m_writer.write(" PUBLIC \"");
                this.m_writer.write(publicId);
            } else {
                this.m_writer.write(" SYSTEM \"");
                this.m_writer.write(systemId);
            }
            this.m_writer.write("\" >");
            this.m_writer.write(this.m_lineSep, 0, this.m_lineSepLen);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected boolean escapingNotNeeded(char ch) {
        boolean ret = ch < '\u007f' ? ch >= ' ' || '\n' == ch || '\r' == ch || '\t' == ch : this.m_encodingInfo.isInEncoding(ch);
        return ret;
    }

    protected int writeUTF16Surrogate(char c, char[] ch, int i, int end) throws IOException {
        int codePoint = 0;
        if (i + 1 >= end) {
            throw new IOException(Utils.messages.createMessage("ER_INVALID_UTF16_SURROGATE", new Object[]{Integer.toHexString(c)}));
        }
        char high = c;
        char low = ch[i + 1];
        if (!Encodings.isLowUTF16Surrogate(low)) {
            throw new IOException(Utils.messages.createMessage("ER_INVALID_UTF16_SURROGATE", new Object[]{Integer.toHexString(c) + " " + Integer.toHexString(low)}));
        }
        Writer writer = this.m_writer;
        if (this.m_encodingInfo.isInEncoding(c, low)) {
            writer.write(ch, i, 2);
        } else {
            String encoding = this.getEncoding();
            if (encoding != null) {
                codePoint = Encodings.toCodePoint(high, low);
                writer.write(38);
                writer.write(35);
                writer.write(Integer.toString(codePoint));
                writer.write(59);
            } else {
                writer.write(ch, i, 2);
            }
        }
        return codePoint;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    int accumDefaultEntity(Writer writer, char ch, int i, char[] chars, int len, boolean fromTextNode, boolean escLF) throws IOException {
        if (!escLF && '\n' == ch) {
            writer.write(this.m_lineSep, 0, this.m_lineSepLen);
            return i + 1;
        } else {
            if ((!fromTextNode || !this.m_charInfo.shouldMapTextChar(ch)) && (fromTextNode || !this.m_charInfo.shouldMapAttrChar(ch))) return i;
            String outputStringForChar = this.m_charInfo.getOutputStringForChar(ch);
            if (null == outputStringForChar) return i;
            writer.write(outputStringForChar);
        }
        return i + 1;
    }

    void writeNormalizedChars(char[] ch, int start, int length, boolean isCData, boolean useSystemLineSeparator) throws IOException, SAXException {
        Writer writer = this.m_writer;
        int end = start + length;
        for (int i = start; i < end; ++i) {
            String intStr;
            char c = ch[i];
            if ('\n' == c && useSystemLineSeparator) {
                writer.write(this.m_lineSep, 0, this.m_lineSepLen);
                continue;
            }
            if (isCData && !this.escapingNotNeeded(c)) {
                if (this.m_cdataTagOpen) {
                    this.closeCDATA();
                }
                if (Encodings.isHighUTF16Surrogate(c)) {
                    this.writeUTF16Surrogate(c, ch, i, end);
                    ++i;
                    continue;
                }
                writer.write("&#");
                intStr = Integer.toString(c);
                writer.write(intStr);
                writer.write(59);
                continue;
            }
            if (isCData && i < end - 2 && ']' == c && ']' == ch[i + 1] && '>' == ch[i + 2]) {
                writer.write("]]]]><![CDATA[>");
                i += 2;
                continue;
            }
            if (this.escapingNotNeeded(c)) {
                if (isCData && !this.m_cdataTagOpen) {
                    writer.write("<![CDATA[");
                    this.m_cdataTagOpen = true;
                }
                writer.write(c);
                continue;
            }
            if (Encodings.isHighUTF16Surrogate(c)) {
                if (this.m_cdataTagOpen) {
                    this.closeCDATA();
                }
                this.writeUTF16Surrogate(c, ch, i, end);
                ++i;
                continue;
            }
            if (this.m_cdataTagOpen) {
                this.closeCDATA();
            }
            writer.write("&#");
            intStr = Integer.toString(c);
            writer.write(intStr);
            writer.write(59);
        }
    }

    public void endNonEscaping() throws SAXException {
        this.m_disableOutputEscapingStates.pop();
    }

    public void startNonEscaping() throws SAXException {
        this.m_disableOutputEscapingStates.push(true);
    }

    protected void cdata(char[] ch, int start, int length) throws SAXException {
        try {
            boolean writeCDataBrackets;
            int old_start = start;
            if (this.m_elemContext.m_startTagOpen) {
                this.closeStartTag();
                this.m_elemContext.m_startTagOpen = false;
            }
            this.m_ispreserve = true;
            if (this.shouldIndent()) {
                this.indent();
            }
            boolean bl = writeCDataBrackets = length >= 1 && this.escapingNotNeeded(ch[start]);
            if (writeCDataBrackets && !this.m_cdataTagOpen) {
                this.m_writer.write("<![CDATA[");
                this.m_cdataTagOpen = true;
            }
            if (this.isEscapingDisabled()) {
                this.charactersRaw(ch, start, length);
            } else {
                this.writeNormalizedChars(ch, start, length, true, this.m_lineSepUse);
            }
            if (writeCDataBrackets && ch[start + length - 1] == ']') {
                this.closeCDATA();
            }
            if (this.m_tracer != null) {
                super.fireCDATAEvent(ch, old_start, length);
            }
        }
        catch (IOException ioe) {
            throw new SAXException(Utils.messages.createMessage("ER_OIERROR", null), ioe);
        }
    }

    private boolean isEscapingDisabled() {
        return this.m_disableOutputEscapingStates.peekOrFalse();
    }

    protected void charactersRaw(char[] ch, int start, int length) throws SAXException {
        if (this.m_inEntityRef) {
            return;
        }
        try {
            if (this.m_elemContext.m_startTagOpen) {
                this.closeStartTag();
                this.m_elemContext.m_startTagOpen = false;
            }
            this.m_ispreserve = true;
            this.m_writer.write(ch, start, length);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void characters(char[] chars, int start, int length) throws SAXException {
        if (length == 0 || this.m_inEntityRef && !this.m_expandDTDEntities) {
            return;
        }
        this.m_docIsEmpty = false;
        if (this.m_elemContext.m_startTagOpen) {
            this.closeStartTag();
            this.m_elemContext.m_startTagOpen = false;
        } else if (this.m_needToCallStartDocument) {
            this.startDocumentInternal();
        }
        if (this.m_cdataStartCalled || this.m_elemContext.m_isCdataSection) {
            this.cdata(chars, start, length);
            return;
        }
        if (this.m_cdataTagOpen) {
            this.closeCDATA();
        }
        if (this.m_disableOutputEscapingStates.peekOrFalse() || !this.m_escaping) {
            this.charactersRaw(chars, start, length);
            if (this.m_tracer != null) {
                super.fireCharEvent(chars, start, length);
            }
            return;
        }
        if (this.m_elemContext.m_startTagOpen) {
            this.closeStartTag();
            this.m_elemContext.m_startTagOpen = false;
        }
        try {
            String outputStringForChar;
            int end = start + length;
            int lastDirtyCharProcessed = start - 1;
            Writer writer = this.m_writer;
            boolean isAllWhitespace = true;
            int i = start;
            block13: while (i < end && isAllWhitespace) {
                char ch1 = chars[i];
                if (this.m_charInfo.shouldMapTextChar(ch1)) {
                    this.writeOutCleanChars(chars, i, lastDirtyCharProcessed);
                    outputStringForChar = this.m_charInfo.getOutputStringForChar(ch1);
                    writer.write(outputStringForChar);
                    isAllWhitespace = false;
                    lastDirtyCharProcessed = i++;
                    continue;
                }
                switch (ch1) {
                    case ' ': {
                        ++i;
                        continue block13;
                    }
                    case '\n': {
                        lastDirtyCharProcessed = this.processLineFeed(chars, i, lastDirtyCharProcessed, writer);
                        ++i;
                        continue block13;
                    }
                    case '\r': {
                        this.writeOutCleanChars(chars, i, lastDirtyCharProcessed);
                        writer.write("&#13;");
                        lastDirtyCharProcessed = i++;
                        continue block13;
                    }
                    case '\t': {
                        ++i;
                        continue block13;
                    }
                }
                isAllWhitespace = false;
            }
            if (i < end || !isAllWhitespace) {
                this.m_ispreserve = true;
            }
            while (i < end) {
                char ch = chars[i];
                if (this.m_charInfo.shouldMapTextChar(ch)) {
                    this.writeOutCleanChars(chars, i, lastDirtyCharProcessed);
                    outputStringForChar = this.m_charInfo.getOutputStringForChar(ch);
                    writer.write(outputStringForChar);
                    lastDirtyCharProcessed = i;
                } else if (ch <= '\u001f') {
                    switch (ch) {
                        case '\t': {
                            break;
                        }
                        case '\n': {
                            lastDirtyCharProcessed = this.processLineFeed(chars, i, lastDirtyCharProcessed, writer);
                            break;
                        }
                        case '\r': {
                            this.writeOutCleanChars(chars, i, lastDirtyCharProcessed);
                            writer.write("&#13;");
                            lastDirtyCharProcessed = i;
                            break;
                        }
                        default: {
                            this.writeOutCleanChars(chars, i, lastDirtyCharProcessed);
                            writer.write("&#");
                            writer.write(Integer.toString(ch));
                            writer.write(59);
                            lastDirtyCharProcessed = i;
                            break;
                        }
                    }
                } else if (ch >= '\u007f') {
                    if (ch <= '\u009f') {
                        this.writeOutCleanChars(chars, i, lastDirtyCharProcessed);
                        writer.write("&#");
                        writer.write(Integer.toString(ch));
                        writer.write(59);
                        lastDirtyCharProcessed = i;
                    } else if (ch == '\u2028') {
                        this.writeOutCleanChars(chars, i, lastDirtyCharProcessed);
                        writer.write("&#8232;");
                        lastDirtyCharProcessed = i;
                    } else if (!this.m_encodingInfo.isInEncoding(ch)) {
                        this.writeOutCleanChars(chars, i, lastDirtyCharProcessed);
                        writer.write("&#");
                        writer.write(Integer.toString(ch));
                        writer.write(59);
                        lastDirtyCharProcessed = i;
                    }
                }
                ++i;
            }
            int startClean = lastDirtyCharProcessed + 1;
            if (i > startClean) {
                int lengthClean = i - startClean;
                this.m_writer.write(chars, startClean, lengthClean);
            }
            this.m_isprevtext = true;
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
        if (this.m_tracer != null) {
            super.fireCharEvent(chars, start, length);
        }
    }

    private int processLineFeed(char[] chars, int i, int lastProcessed, Writer writer) throws IOException {
        if (this.m_lineSepUse && (this.m_lineSepLen != 1 || this.m_lineSep[0] != '\n')) {
            this.writeOutCleanChars(chars, i, lastProcessed);
            writer.write(this.m_lineSep, 0, this.m_lineSepLen);
            lastProcessed = i;
        }
        return lastProcessed;
    }

    private void writeOutCleanChars(char[] chars, int i, int lastProcessed) throws IOException {
        int startClean = lastProcessed + 1;
        if (startClean < i) {
            int lengthClean = i - startClean;
            this.m_writer.write(chars, startClean, lengthClean);
        }
    }

    private static boolean isCharacterInC0orC1Range(char ch) {
        if (ch == '\t' || ch == '\n' || ch == '\r') {
            return false;
        }
        return ch >= '\u007f' && ch <= '\u009f' || ch >= '\u0001' && ch <= '\u001f';
    }

    private static boolean isNELorLSEPCharacter(char ch) {
        return ch == '\u0085' || ch == '\u2028';
    }

    private int processDirty(char[] chars, int end, int i, char ch, int lastDirty, boolean fromTextNode) throws IOException {
        int startClean = lastDirty + 1;
        if (i > startClean) {
            int lengthClean = i - startClean;
            this.m_writer.write(chars, startClean, lengthClean);
        }
        if ('\n' == ch && fromTextNode) {
            this.m_writer.write(this.m_lineSep, 0, this.m_lineSepLen);
        } else {
            startClean = this.accumDefaultEscape(this.m_writer, ch, i, chars, end, fromTextNode, false);
            i = startClean - 1;
        }
        return i;
    }

    @Override
    public void characters(String s) throws SAXException {
        if (this.m_inEntityRef && !this.m_expandDTDEntities) {
            return;
        }
        int length = s.length();
        if (length > this.m_charsBuff.length) {
            this.m_charsBuff = new char[length * 2 + 1];
        }
        s.getChars(0, length, this.m_charsBuff, 0);
        this.characters(this.m_charsBuff, 0, length);
    }

    private int accumDefaultEscape(Writer writer, char ch, int i, char[] chars, int len, boolean fromTextNode, boolean escLF) throws IOException {
        int pos = this.accumDefaultEntity(writer, ch, i, chars, len, fromTextNode, escLF);
        if (i == pos) {
            if (Encodings.isHighUTF16Surrogate(ch)) {
                char next;
                int codePoint = 0;
                if (i + 1 >= len) {
                    throw new IOException(Utils.messages.createMessage("ER_INVALID_UTF16_SURROGATE", new Object[]{Integer.toHexString(ch)}));
                }
                if (!Encodings.isLowUTF16Surrogate(next = chars[++i])) {
                    throw new IOException(Utils.messages.createMessage("ER_INVALID_UTF16_SURROGATE", new Object[]{Integer.toHexString(ch) + " " + Integer.toHexString(next)}));
                }
                codePoint = Encodings.toCodePoint(ch, next);
                writer.write("&#");
                writer.write(Integer.toString(codePoint));
                writer.write(59);
                pos += 2;
            } else {
                if (ToStream.isCharacterInC0orC1Range(ch) || ToStream.isNELorLSEPCharacter(ch)) {
                    writer.write("&#");
                    writer.write(Integer.toString(ch));
                    writer.write(59);
                } else if ((!this.escapingNotNeeded(ch) || fromTextNode && this.m_charInfo.shouldMapTextChar(ch) || !fromTextNode && this.m_charInfo.shouldMapAttrChar(ch)) && this.m_elemContext.m_currentElemDepth > 0) {
                    writer.write("&#");
                    writer.write(Integer.toString(ch));
                    writer.write(59);
                } else {
                    writer.write(ch);
                }
                ++pos;
            }
        }
        return pos;
    }

    @Override
    public void startElement(String namespaceURI, String localName, String name, Attributes atts) throws SAXException {
        if (this.m_inEntityRef) {
            return;
        }
        if (this.m_needToCallStartDocument) {
            this.startDocumentInternal();
            this.m_needToCallStartDocument = false;
            this.m_docIsEmpty = false;
        } else if (this.m_cdataTagOpen) {
            this.closeCDATA();
        }
        try {
            if (this.m_needToOutputDocTypeDecl) {
                if (null != this.getDoctypeSystem()) {
                    this.outputDocTypeDecl(name, true);
                }
                this.m_needToOutputDocTypeDecl = false;
            }
            if (this.m_elemContext.m_startTagOpen) {
                this.closeStartTag();
                this.m_elemContext.m_startTagOpen = false;
            }
            if (namespaceURI != null) {
                this.ensurePrefixIsDeclared(namespaceURI, name);
            }
            this.m_ispreserve = false;
            if (this.shouldIndent() && this.m_startNewLine) {
                this.indent();
            }
            this.m_startNewLine = true;
            Writer writer = this.m_writer;
            writer.write(60);
            writer.write(name);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
        if (atts != null) {
            this.addAttributes(atts);
        }
        this.m_elemContext = this.m_elemContext.push(namespaceURI, localName, name);
        this.m_isprevtext = false;
        if (this.m_tracer != null) {
            this.firePseudoAttributes();
        }
    }

    @Override
    public void startElement(String elementNamespaceURI, String elementLocalName, String elementName) throws SAXException {
        this.startElement(elementNamespaceURI, elementLocalName, elementName, null);
    }

    @Override
    public void startElement(String elementName) throws SAXException {
        this.startElement(null, null, elementName, null);
    }

    void outputDocTypeDecl(String name, boolean closeDecl) throws SAXException {
        if (this.m_cdataTagOpen) {
            this.closeCDATA();
        }
        try {
            String doctypeSystem;
            Writer writer = this.m_writer;
            writer.write("<!DOCTYPE ");
            writer.write(name);
            String doctypePublic = this.getDoctypePublic();
            if (null != doctypePublic) {
                writer.write(" PUBLIC \"");
                writer.write(doctypePublic);
                writer.write(34);
            }
            if (null != (doctypeSystem = this.getDoctypeSystem())) {
                if (null == doctypePublic) {
                    writer.write(" SYSTEM \"");
                } else {
                    writer.write(" \"");
                }
                writer.write(doctypeSystem);
                if (closeDecl) {
                    writer.write("\">");
                    writer.write(this.m_lineSep, 0, this.m_lineSepLen);
                    closeDecl = false;
                } else {
                    writer.write(34);
                }
            }
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    public void processAttributes(Writer writer, int nAttrs) throws IOException, SAXException {
        String encoding = this.getEncoding();
        for (int i = 0; i < nAttrs; ++i) {
            String name = this.m_attributes.getQName(i);
            String value = this.m_attributes.getValue(i);
            writer.write(32);
            writer.write(name);
            writer.write("=\"");
            this.writeAttrString(writer, value, encoding);
            writer.write(34);
        }
    }

    public void writeAttrString(Writer writer, String string, String encoding) throws IOException {
        int len = string.length();
        if (len > this.m_attrBuff.length) {
            this.m_attrBuff = new char[len * 2 + 1];
        }
        string.getChars(0, len, this.m_attrBuff, 0);
        char[] stringChars = this.m_attrBuff;
        for (int i = 0; i < len; ++i) {
            char ch = stringChars[i];
            if (this.m_charInfo.shouldMapAttrChar(ch)) {
                this.accumDefaultEscape(writer, ch, i, stringChars, len, false, true);
                continue;
            }
            if ('\u0000' <= ch && ch <= '\u001f') {
                switch (ch) {
                    case '\t': {
                        writer.write("&#9;");
                        break;
                    }
                    case '\n': {
                        writer.write("&#10;");
                        break;
                    }
                    case '\r': {
                        writer.write("&#13;");
                        break;
                    }
                    default: {
                        writer.write("&#");
                        writer.write(Integer.toString(ch));
                        writer.write(59);
                        break;
                    }
                }
                continue;
            }
            if (ch < '\u007f') {
                writer.write(ch);
                continue;
            }
            if (ch <= '\u009f') {
                writer.write("&#");
                writer.write(Integer.toString(ch));
                writer.write(59);
                continue;
            }
            if (ch == '\u2028') {
                writer.write("&#8232;");
                continue;
            }
            if (this.m_encodingInfo.isInEncoding(ch)) {
                writer.write(ch);
                continue;
            }
            writer.write("&#");
            writer.write(Integer.toString(ch));
            writer.write(59);
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String name) throws SAXException {
        if (this.m_inEntityRef) {
            return;
        }
        this.m_prefixMap.popNamespaces(this.m_elemContext.m_currentElemDepth, null);
        try {
            Writer writer = this.m_writer;
            if (this.m_elemContext.m_startTagOpen) {
                int nAttrs;
                if (this.m_tracer != null) {
                    super.fireStartElem(this.m_elemContext.m_elementName);
                }
                if ((nAttrs = this.m_attributes.getLength()) > 0) {
                    this.processAttributes(this.m_writer, nAttrs);
                    this.m_attributes.clear();
                }
                if (this.m_spaceBeforeClose) {
                    writer.write(" />");
                } else {
                    writer.write("/>");
                }
            } else {
                if (this.m_cdataTagOpen) {
                    this.closeCDATA();
                }
                if (this.shouldIndent()) {
                    this.indent(this.m_elemContext.m_currentElemDepth - 1);
                }
                writer.write(60);
                writer.write(47);
                writer.write(name);
                writer.write(62);
            }
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
        if (!this.m_elemContext.m_startTagOpen && this.m_doIndent) {
            this.m_ispreserve = this.m_preserves.isEmpty() ? false : this.m_preserves.pop();
        }
        this.m_isprevtext = false;
        if (this.m_tracer != null) {
            super.fireEndElem(name);
        }
        this.m_elemContext = this.m_elemContext.m_prev;
    }

    @Override
    public void endElement(String name) throws SAXException {
        this.endElement(null, null, name);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        this.startPrefixMapping(prefix, uri, true);
    }

    @Override
    public boolean startPrefixMapping(String prefix, String uri, boolean shouldFlush) throws SAXException {
        int pushDepth;
        if (shouldFlush) {
            this.flushPending();
            pushDepth = this.m_elemContext.m_currentElemDepth + 1;
        } else {
            pushDepth = this.m_elemContext.m_currentElemDepth;
        }
        boolean pushed = this.m_prefixMap.pushNamespace(prefix, uri, pushDepth);
        if (pushed) {
            if ("".equals(prefix)) {
                String name = "xmlns";
                this.addAttributeAlways("http://www.w3.org/2000/xmlns/", name, name, "CDATA", uri, false);
            } else if (!"".equals(uri)) {
                String name = "xmlns:" + prefix;
                this.addAttributeAlways("http://www.w3.org/2000/xmlns/", prefix, name, "CDATA", uri, false);
            }
        }
        return pushed;
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        int start_old = start;
        if (this.m_inEntityRef) {
            return;
        }
        if (this.m_elemContext.m_startTagOpen) {
            this.closeStartTag();
            this.m_elemContext.m_startTagOpen = false;
        } else if (this.m_needToCallStartDocument) {
            this.startDocumentInternal();
            this.m_needToCallStartDocument = false;
        }
        try {
            int limit = start + length;
            boolean wasDash = false;
            if (this.m_cdataTagOpen) {
                this.closeCDATA();
            }
            if (this.shouldIndent()) {
                this.indent();
            }
            Writer writer = this.m_writer;
            writer.write(COMMENT_BEGIN);
            for (int i = start; i < limit; ++i) {
                if (wasDash && ch[i] == '-') {
                    writer.write(ch, start, i - start);
                    writer.write(" -");
                    start = i + 1;
                }
                wasDash = ch[i] == '-';
            }
            if (length > 0) {
                int remainingChars = limit - start;
                if (remainingChars > 0) {
                    writer.write(ch, start, remainingChars);
                }
                if (ch[limit - 1] == '-') {
                    writer.write(32);
                }
            }
            writer.write(COMMENT_END);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
        this.m_startNewLine = true;
        if (this.m_tracer != null) {
            super.fireCommentEvent(ch, start_old, length);
        }
    }

    @Override
    public void endCDATA() throws SAXException {
        if (this.m_cdataTagOpen) {
            this.closeCDATA();
        }
        this.m_cdataStartCalled = false;
    }

    @Override
    public void endDTD() throws SAXException {
        try {
            if (this.m_needToOutputDocTypeDecl) {
                this.outputDocTypeDecl(this.m_elemContext.m_elementName, false);
                this.m_needToOutputDocTypeDecl = false;
            }
            Writer writer = this.m_writer;
            if (!this.m_inDoctype) {
                writer.write("]>");
            } else {
                writer.write(62);
            }
            writer.write(this.m_lineSep, 0, this.m_lineSepLen);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        if (0 == length) {
            return;
        }
        this.characters(ch, start, length);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
    }

    @Override
    public void startCDATA() throws SAXException {
        this.m_cdataStartCalled = true;
    }

    @Override
    public void startEntity(String name) throws SAXException {
        if (name.equals("[dtd]")) {
            this.m_inExternalDTD = true;
        }
        if (!this.m_expandDTDEntities && !this.m_inExternalDTD) {
            this.startNonEscaping();
            this.characters("&" + name + ';');
            this.endNonEscaping();
        }
        this.m_inEntityRef = true;
    }

    protected void closeStartTag() throws SAXException {
        if (this.m_elemContext.m_startTagOpen) {
            try {
                int nAttrs;
                if (this.m_tracer != null) {
                    super.fireStartElem(this.m_elemContext.m_elementName);
                }
                if ((nAttrs = this.m_attributes.getLength()) > 0) {
                    this.processAttributes(this.m_writer, nAttrs);
                    this.m_attributes.clear();
                }
                this.m_writer.write(62);
            }
            catch (IOException e) {
                throw new SAXException(e);
            }
            if (this.m_CdataElems != null) {
                this.m_elemContext.m_isCdataSection = this.isCdataSection();
            }
            if (this.m_doIndent) {
                this.m_isprevtext = false;
                this.m_preserves.push(this.m_ispreserve);
            }
        }
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        this.setDoctypeSystem(systemId);
        this.setDoctypePublic(publicId);
        this.m_elemContext.m_elementName = name;
        this.m_inDoctype = true;
    }

    @Override
    public int getIndentAmount() {
        return this.m_indentAmount;
    }

    @Override
    public void setIndentAmount(int m_indentAmount) {
        this.m_indentAmount = m_indentAmount;
    }

    protected boolean shouldIndent() {
        return this.m_doIndent && !this.m_ispreserve && !this.m_isprevtext && this.m_elemContext.m_currentElemDepth > 0;
    }

    private void setCdataSectionElements(String key, Properties props) {
        String s = props.getProperty(key);
        if (null != s) {
            Vector v = new Vector();
            int l = s.length();
            boolean inCurly = false;
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < l; ++i) {
                char c = s.charAt(i);
                if (Character.isWhitespace(c)) {
                    if (!inCurly) {
                        if (buf.length() <= 0) continue;
                        this.addCdataSectionElement(buf.toString(), v);
                        buf.setLength(0);
                        continue;
                    }
                } else if ('{' == c) {
                    inCurly = true;
                } else if ('}' == c) {
                    inCurly = false;
                }
                buf.append(c);
            }
            if (buf.length() > 0) {
                this.addCdataSectionElement(buf.toString(), v);
                buf.setLength(0);
            }
            this.setCdataSectionElements(v);
        }
    }

    private void addCdataSectionElement(String URI_and_localName, Vector v) {
        String s2;
        StringTokenizer tokenizer = new StringTokenizer(URI_and_localName, "{}", false);
        String s1 = tokenizer.nextToken();
        String string = s2 = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
        if (null == s2) {
            v.addElement(null);
            v.addElement(s1);
        } else {
            v.addElement(s1);
            v.addElement(s2);
        }
    }

    @Override
    public void setCdataSectionElements(Vector URI_and_localNames) {
        int len;
        if (URI_and_localNames != null && (len = URI_and_localNames.size() - 1) > 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < len; i += 2) {
                if (i != 0) {
                    sb.append(' ');
                }
                String uri = (String)URI_and_localNames.elementAt(i);
                String localName = (String)URI_and_localNames.elementAt(i + 1);
                if (uri != null) {
                    sb.append('{');
                    sb.append(uri);
                    sb.append('}');
                }
                sb.append(localName);
            }
            this.m_StringOfCDATASections = sb.toString();
        }
        this.initCdataElems(this.m_StringOfCDATASections);
    }

    protected String ensureAttributesNamespaceIsDeclared(String ns, String localName, String rawName) throws SAXException {
        if (ns != null && ns.length() > 0) {
            String prefixFromRawName;
            int index = 0;
            index = rawName.indexOf(":");
            String string = prefixFromRawName = index < 0 ? "" : rawName.substring(0, index);
            if (index > 0) {
                String uri = this.m_prefixMap.lookupNamespace(prefixFromRawName);
                if (uri != null && uri.equals(ns)) {
                    return null;
                }
                this.startPrefixMapping(prefixFromRawName, ns, false);
                this.addAttribute("http://www.w3.org/2000/xmlns/", prefixFromRawName, "xmlns:" + prefixFromRawName, "CDATA", ns, false);
                return prefixFromRawName;
            }
            String prefix = this.m_prefixMap.lookupPrefix(ns);
            if (prefix == null) {
                prefix = this.m_prefixMap.generateNextPrefix();
                this.startPrefixMapping(prefix, ns, false);
                this.addAttribute("http://www.w3.org/2000/xmlns/", prefix, "xmlns:" + prefix, "CDATA", ns, false);
            }
            return prefix;
        }
        return null;
    }

    void ensurePrefixIsDeclared(String ns, String rawName) throws SAXException {
        if (ns != null && ns.length() > 0) {
            String foundURI;
            String prefix;
            int index = rawName.indexOf(":");
            boolean no_prefix = index < 0;
            String string = prefix = no_prefix ? "" : rawName.substring(0, index);
            if (!(null == prefix || null != (foundURI = this.m_prefixMap.lookupNamespace(prefix)) && foundURI.equals(ns))) {
                this.startPrefixMapping(prefix, ns);
                this.addAttributeAlways("http://www.w3.org/2000/xmlns/", no_prefix ? "xmlns" : prefix, no_prefix ? "xmlns" : "xmlns:" + prefix, "CDATA", ns, false);
            }
        }
    }

    @Override
    public void flushPending() throws SAXException {
        if (this.m_needToCallStartDocument) {
            this.startDocumentInternal();
            this.m_needToCallStartDocument = false;
        }
        if (this.m_elemContext.m_startTagOpen) {
            this.closeStartTag();
            this.m_elemContext.m_startTagOpen = false;
        }
        if (this.m_cdataTagOpen) {
            this.closeCDATA();
            this.m_cdataTagOpen = false;
        }
        if (this.m_writer != null) {
            try {
                this.m_writer.flush();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    @Override
    public void setContentHandler(ContentHandler ch) {
    }

    @Override
    public boolean addAttributeAlways(String uri, String localName, String rawName, String type, String value, boolean xslAttribute) {
        boolean was_added;
        int index = uri == null || localName == null || uri.length() == 0 ? this.m_attributes.getIndex(rawName) : this.m_attributes.getIndex(uri, localName);
        if (index >= 0) {
            String old_value = null;
            if (this.m_tracer != null && value.equals(old_value = this.m_attributes.getValue(index))) {
                old_value = null;
            }
            this.m_attributes.setValue(index, value);
            was_added = false;
            if (old_value != null) {
                this.firePseudoAttributes();
            }
        } else {
            if (xslAttribute) {
                String prefix;
                NamespaceMappings.MappingRecord existing_mapping;
                int colonIndex = rawName.indexOf(58);
                if (colonIndex > 0 && (existing_mapping = this.m_prefixMap.getMappingFromPrefix(prefix = rawName.substring(0, colonIndex))) != null && existing_mapping.m_declarationDepth == this.m_elemContext.m_currentElemDepth && !existing_mapping.m_uri.equals(uri)) {
                    prefix = this.m_prefixMap.lookupPrefix(uri);
                    if (prefix == null) {
                        prefix = this.m_prefixMap.generateNextPrefix();
                    }
                    rawName = prefix + ':' + localName;
                }
                try {
                    prefix = this.ensureAttributesNamespaceIsDeclared(uri, localName, rawName);
                }
                catch (SAXException e) {
                    e.printStackTrace();
                }
            }
            this.m_attributes.addAttribute(uri, localName, rawName, type, value);
            was_added = true;
            if (this.m_tracer != null) {
                this.firePseudoAttributes();
            }
        }
        return was_added;
    }

    protected void firePseudoAttributes() {
        if (this.m_tracer != null) {
            try {
                this.m_writer.flush();
                StringBuffer sb = new StringBuffer();
                int nAttrs = this.m_attributes.getLength();
                if (nAttrs > 0) {
                    WritertoStringBuffer writer = new WritertoStringBuffer(sb);
                    this.processAttributes(writer, nAttrs);
                }
                sb.append('>');
                char[] ch = sb.toString().toCharArray();
                this.m_tracer.fireGenerateEvent(11, ch, 0, ch.length);
            }
            catch (IOException iOException) {
            }
            catch (SAXException sAXException) {
                // empty catch block
            }
        }
    }

    @Override
    public void setTransformer(Transformer transformer) {
        super.setTransformer(transformer);
        if (this.m_tracer != null && !(this.m_writer instanceof SerializerTraceWriter)) {
            this.setWriterInternal(new SerializerTraceWriter(this.m_writer, this.m_tracer), false);
        }
    }

    @Override
    public boolean reset() {
        boolean wasReset = false;
        if (super.reset()) {
            this.resetToStream();
            wasReset = true;
        }
        return wasReset;
    }

    private void resetToStream() {
        this.m_cdataStartCalled = false;
        this.m_disableOutputEscapingStates.clear();
        this.m_escaping = true;
        this.m_expandDTDEntities = true;
        this.m_inDoctype = false;
        this.m_ispreserve = false;
        this.m_isprevtext = false;
        this.m_isUTF8 = false;
        this.m_lineSep = s_systemLineSep;
        this.m_lineSepLen = s_systemLineSep.length;
        this.m_lineSepUse = true;
        this.m_preserves.clear();
        this.m_shouldFlush = true;
        this.m_spaceBeforeClose = false;
        this.m_startNewLine = false;
        this.m_writer_set_by_user = false;
    }

    @Override
    public void setEncoding(String encoding) {
        this.setOutputProperty("encoding", encoding);
    }

    @Override
    public void notationDecl(String name, String pubID, String sysID) throws SAXException {
        try {
            this.DTDprolog();
            this.m_writer.write("<!NOTATION ");
            this.m_writer.write(name);
            if (pubID != null) {
                this.m_writer.write(" PUBLIC \"");
                this.m_writer.write(pubID);
            } else {
                this.m_writer.write(" SYSTEM \"");
                this.m_writer.write(sysID);
            }
            this.m_writer.write("\" >");
            this.m_writer.write(this.m_lineSep, 0, this.m_lineSepLen);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unparsedEntityDecl(String name, String pubID, String sysID, String notationName) throws SAXException {
        try {
            this.DTDprolog();
            this.m_writer.write("<!ENTITY ");
            this.m_writer.write(name);
            if (pubID != null) {
                this.m_writer.write(" PUBLIC \"");
                this.m_writer.write(pubID);
            } else {
                this.m_writer.write(" SYSTEM \"");
                this.m_writer.write(sysID);
            }
            this.m_writer.write("\" NDATA ");
            this.m_writer.write(notationName);
            this.m_writer.write(" >");
            this.m_writer.write(this.m_lineSep, 0, this.m_lineSepLen);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void DTDprolog() throws SAXException, IOException {
        Writer writer = this.m_writer;
        if (this.m_needToOutputDocTypeDecl) {
            this.outputDocTypeDecl(this.m_elemContext.m_elementName, false);
            this.m_needToOutputDocTypeDecl = false;
        }
        if (this.m_inDoctype) {
            writer.write(" [");
            writer.write(this.m_lineSep, 0, this.m_lineSepLen);
            this.m_inDoctype = false;
        }
    }

    @Override
    public void setDTDEntityExpansion(boolean expand) {
        this.m_expandDTDEntities = expand;
    }

    public void setNewLine(char[] eolChars) {
        this.m_lineSep = eolChars;
        this.m_lineSepLen = eolChars.length;
    }

    public void addCdataSectionElements(String URI_and_localNames) {
        if (URI_and_localNames != null) {
            this.initCdataElems(URI_and_localNames);
        }
        this.m_StringOfCDATASections = this.m_StringOfCDATASections == null ? URI_and_localNames : this.m_StringOfCDATASections + " " + URI_and_localNames;
    }

    static final class BoolStack {
        private boolean[] m_values;
        private int m_allocatedSize;
        private int m_index;

        public BoolStack() {
            this(32);
        }

        public BoolStack(int size) {
            this.m_allocatedSize = size;
            this.m_values = new boolean[size];
            this.m_index = -1;
        }

        public final int size() {
            return this.m_index + 1;
        }

        public final void clear() {
            this.m_index = -1;
        }

        public final boolean push(boolean val) {
            if (this.m_index == this.m_allocatedSize - 1) {
                this.grow();
            }
            boolean bl = val;
            this.m_values[++this.m_index] = bl;
            return bl;
        }

        public final boolean pop() {
            return this.m_values[this.m_index--];
        }

        public final boolean popAndTop() {
            --this.m_index;
            return this.m_index >= 0 ? this.m_values[this.m_index] : false;
        }

        public final void setTop(boolean b) {
            this.m_values[this.m_index] = b;
        }

        public final boolean peek() {
            return this.m_values[this.m_index];
        }

        public final boolean peekOrFalse() {
            return this.m_index > -1 ? this.m_values[this.m_index] : false;
        }

        public final boolean peekOrTrue() {
            return this.m_index > -1 ? this.m_values[this.m_index] : true;
        }

        public boolean isEmpty() {
            return this.m_index == -1;
        }

        private void grow() {
            this.m_allocatedSize *= 2;
            boolean[] newVector = new boolean[this.m_allocatedSize];
            System.arraycopy(this.m_values, 0, newVector, 0, this.m_index + 1);
            this.m_values = newVector;
        }
    }

    private static class WritertoStringBuffer
    extends Writer {
        private final StringBuffer m_stringbuf;

        WritertoStringBuffer(StringBuffer sb) {
            this.m_stringbuf = sb;
        }

        @Override
        public void write(char[] arg0, int arg1, int arg2) throws IOException {
            this.m_stringbuf.append(arg0, arg1, arg2);
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public void write(int i) {
            this.m_stringbuf.append((char)i);
        }

        @Override
        public void write(String s) {
            this.m_stringbuf.append(s);
        }
    }
}

