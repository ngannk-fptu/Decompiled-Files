/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.stax2.io.EscapingWriterFactory
 *  org.codehaus.stax2.ri.typed.AsciiValueEncoder
 *  org.codehaus.stax2.validation.XMLValidator
 */
package com.ctc.wstx.sw;

import com.ctc.wstx.api.InvalidCharHandler;
import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.sw.XmlWriterWrapper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.text.MessageFormat;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.io.EscapingWriterFactory;
import org.codehaus.stax2.ri.typed.AsciiValueEncoder;
import org.codehaus.stax2.validation.XMLValidator;

public abstract class XmlWriter {
    protected static final int SURR1_FIRST = 55296;
    protected static final int SURR1_LAST = 56319;
    protected static final int SURR2_FIRST = 56320;
    protected static final int SURR2_LAST = 57343;
    protected static final char DEFAULT_QUOTE_CHAR = '\"';
    protected final WriterConfig mConfig;
    protected final String mEncoding;
    protected final boolean mNsAware;
    protected final boolean mCheckStructure;
    protected final boolean mCheckContent;
    protected final boolean mCheckNames;
    protected final boolean mFixContent;
    final boolean mEscapeCR;
    final boolean mAddSpaceAfterEmptyElem;
    final boolean mUseDoubleQuotesInXmlDecl;
    protected final boolean mAutoCloseOutput;
    protected Writer mTextWriter;
    protected Writer mAttrValueWriter;
    protected boolean mXml11 = false;
    protected XmlWriterWrapper mRawWrapper = null;
    protected XmlWriterWrapper mTextWrapper = null;
    protected int mLocPastChars = 0;
    protected int mLocRowNr = 1;
    protected int mLocRowStartOffset = 0;

    protected XmlWriter(WriterConfig cfg, String encoding, boolean autoclose) throws IOException {
        String enc;
        this.mConfig = cfg;
        this.mEncoding = encoding;
        this.mAutoCloseOutput = autoclose;
        int flags = cfg.getConfigFlags();
        this.mNsAware = (flags & 1) != 0;
        this.mCheckStructure = (flags & 0x100) != 0;
        this.mCheckContent = (flags & 0x200) != 0;
        this.mCheckNames = (flags & 0x400) != 0;
        this.mFixContent = (flags & 0x1000) != 0;
        this.mEscapeCR = (flags & 0x20) != 0;
        this.mAddSpaceAfterEmptyElem = (flags & 0x40) != 0;
        this.mUseDoubleQuotesInXmlDecl = (flags & 0x4000) != 0;
        EscapingWriterFactory f = this.mConfig.getTextEscaperFactory();
        if (f == null) {
            this.mTextWriter = null;
        } else {
            enc = this.mEncoding == null || this.mEncoding.length() == 0 ? "UTF-8" : this.mEncoding;
            this.mTextWriter = f.createEscapingWriterFor(this.wrapAsRawWriter(), enc);
        }
        f = this.mConfig.getAttrValueEscaperFactory();
        if (f == null) {
            this.mAttrValueWriter = null;
        } else {
            enc = this.mEncoding == null || this.mEncoding.length() == 0 ? "UTF-8" : this.mEncoding;
            this.mAttrValueWriter = f.createEscapingWriterFor(this.wrapAsRawWriter(), enc);
        }
    }

    public void enableXml11() {
        this.mXml11 = true;
    }

    protected abstract OutputStream getOutputStream();

    protected abstract Writer getWriter();

    public abstract void close(boolean var1) throws IOException;

    public abstract void flush() throws IOException;

    public abstract void writeRaw(String var1, int var2, int var3) throws IOException;

    public void writeRaw(String str) throws IOException {
        this.writeRaw(str, 0, str.length());
    }

    public abstract void writeRaw(char[] var1, int var2, int var3) throws IOException;

    public abstract void writeRawAscii(char[] var1, int var2, int var3) throws IOException;

    public abstract void writeCDataStart() throws IOException;

    public abstract void writeCDataEnd() throws IOException;

    public abstract void writeCommentStart() throws IOException;

    public abstract void writeCommentEnd() throws IOException;

    public abstract void writePIStart(String var1, boolean var2) throws IOException;

    public abstract void writePIEnd() throws IOException;

    public abstract int writeCData(String var1) throws IOException, XMLStreamException;

    public abstract int writeCData(char[] var1, int var2, int var3) throws IOException, XMLStreamException;

    public abstract void writeCharacters(String var1) throws IOException;

    public abstract void writeCharacters(char[] var1, int var2, int var3) throws IOException;

    public abstract int writeComment(String var1) throws IOException, XMLStreamException;

    public abstract void writeDTD(String var1) throws IOException, XMLStreamException;

    public abstract void writeDTD(String var1, String var2, String var3, String var4) throws IOException, XMLStreamException;

    public abstract void writeEntityReference(String var1) throws IOException, XMLStreamException;

    public abstract int writePI(String var1, String var2) throws IOException, XMLStreamException;

    public abstract void writeXmlDeclaration(String var1, String var2, String var3) throws IOException;

    public abstract void writeStartTagStart(String var1) throws IOException, XMLStreamException;

    public abstract void writeStartTagStart(String var1, String var2) throws IOException, XMLStreamException;

    public abstract void writeStartTagEnd() throws IOException;

    public abstract void writeStartTagEmptyEnd() throws IOException;

    public abstract void writeEndTag(String var1) throws IOException;

    public abstract void writeEndTag(String var1, String var2) throws IOException;

    public abstract void writeAttribute(String var1, String var2) throws IOException, XMLStreamException;

    public abstract void writeAttribute(String var1, char[] var2, int var3, int var4) throws IOException, XMLStreamException;

    public abstract void writeAttribute(String var1, String var2, String var3) throws IOException, XMLStreamException;

    public abstract void writeAttribute(String var1, String var2, char[] var3, int var4, int var5) throws IOException, XMLStreamException;

    public abstract void writeTypedElement(AsciiValueEncoder var1) throws IOException;

    public abstract void writeTypedElement(AsciiValueEncoder var1, XMLValidator var2, char[] var3) throws IOException, XMLStreamException;

    public abstract void writeTypedAttribute(String var1, AsciiValueEncoder var2) throws IOException, XMLStreamException;

    public abstract void writeTypedAttribute(String var1, String var2, AsciiValueEncoder var3) throws IOException, XMLStreamException;

    public abstract void writeTypedAttribute(String var1, String var2, String var3, AsciiValueEncoder var4, XMLValidator var5, char[] var6) throws IOException, XMLStreamException;

    protected abstract int getOutputPtr();

    public int getRow() {
        return this.mLocRowNr;
    }

    public int getColumn() {
        return this.getOutputPtr() - this.mLocRowStartOffset + 1;
    }

    public int getAbsOffset() {
        return this.mLocPastChars + this.getOutputPtr();
    }

    public final Writer wrapAsRawWriter() {
        if (this.mRawWrapper == null) {
            this.mRawWrapper = XmlWriterWrapper.wrapWriteRaw(this);
        }
        return this.mRawWrapper;
    }

    public final Writer wrapAsTextWriter() {
        if (this.mTextWrapper == null) {
            this.mTextWrapper = XmlWriterWrapper.wrapWriteCharacters(this);
        }
        return this.mTextWrapper;
    }

    public final void verifyNameValidity(String name, boolean checkNs) throws XMLStreamException {
        int illegalIx;
        if (name == null || name.length() == 0) {
            this.reportNwfName(ErrorConsts.WERR_NAME_EMPTY);
        }
        if ((illegalIx = WstxInputData.findIllegalNameChar(name, checkNs, this.mXml11)) >= 0) {
            String msg = illegalIx == 0 ? MessageFormat.format(ErrorConsts.WERR_NAME_ILLEGAL_FIRST_CHAR, WstxInputData.getCharDesc(name.charAt(0)), name) : MessageFormat.format(ErrorConsts.WERR_NAME_ILLEGAL_CHAR, WstxInputData.getCharDesc(name.charAt(illegalIx)), name, illegalIx);
            this.reportNwfName(msg);
        }
    }

    protected void reportNwfName(String msg) throws XMLStreamException {
        this.throwOutputError(msg);
    }

    protected void reportNwfContent(String msg) throws XMLStreamException {
        this.throwOutputError(msg);
    }

    protected void throwOutputError(String msg) throws XMLStreamException {
        try {
            this.flush();
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
        throw new XMLStreamException(msg);
    }

    protected void throwOutputError(String format, Object arg) throws XMLStreamException {
        this.throwOutputError(MessageFormat.format(format, arg));
    }

    protected char handleInvalidChar(int c) throws IOException {
        this.flush();
        InvalidCharHandler h = this.mConfig.getInvalidCharHandler();
        if (h == null) {
            h = InvalidCharHandler.FailingHandler.getInstance();
        }
        return h.convertInvalidChar(c);
    }
}

