/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sw;

import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.cfg.OutputConfigFlags;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.exc.WstxValidationException;
import com.ctc.wstx.io.WstxInputLocation;
import com.ctc.wstx.sr.AttributeCollector;
import com.ctc.wstx.sr.InputElementStack;
import com.ctc.wstx.sr.StreamReaderImpl;
import com.ctc.wstx.sw.XmlWriter;
import com.ctc.wstx.util.DataUtil;
import com.ctc.wstx.util.StringUtil;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.ri.Stax2WriterImpl;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.ValidatorPair;
import org.codehaus.stax2.validation.XMLValidationProblem;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

public abstract class BaseStreamWriter
extends Stax2WriterImpl
implements ValidationContext,
OutputConfigFlags {
    protected static final int STATE_PROLOG = 1;
    protected static final int STATE_TREE = 2;
    protected static final int STATE_EPILOG = 3;
    protected static final char CHAR_SPACE = ' ';
    protected static final int MIN_ARRAYCOPY = 12;
    protected static final int ATTR_MIN_ARRAYCOPY = 12;
    protected static final int DEFAULT_COPYBUFFER_LEN = 512;
    protected final XmlWriter mWriter;
    protected char[] mCopyBuffer = null;
    protected final WriterConfig mConfig;
    protected boolean mCfgAutomaticEmptyElems;
    protected boolean mCheckStructure;
    protected boolean mCheckAttrs;
    protected String mEncoding;
    protected XMLValidator mValidator = null;
    protected boolean mXml11 = false;
    protected ValidationProblemHandler mVldProbHandler = null;
    protected int mState = 1;
    protected boolean mAnyOutput = false;
    protected boolean mStartElementOpen = false;
    protected boolean mEmptyElement = false;
    protected int mVldContent = 4;
    protected String mDtdRootElem = null;
    protected boolean mReturnNullForDefaultNamespace;

    protected BaseStreamWriter(XmlWriter xw, String enc, WriterConfig cfg) {
        this.mWriter = xw;
        this.mEncoding = enc;
        this.mConfig = cfg;
        int flags = cfg.getConfigFlags();
        this.mCheckStructure = (flags & 0x100) != 0;
        this.mCheckAttrs = (flags & 0x800) != 0;
        this.mCfgAutomaticEmptyElems = (flags & 4) != 0;
        this.mReturnNullForDefaultNamespace = this.mConfig.returnNullForDefaultNamespace();
    }

    public void close() throws XMLStreamException {
        this._finishDocument(false);
    }

    public void flush() throws XMLStreamException {
        try {
            this.mWriter.flush();
        }
        catch (IOException ie) {
            throw new WstxIOException(ie);
        }
    }

    public abstract NamespaceContext getNamespaceContext();

    public abstract String getPrefix(String var1);

    public Object getProperty(String name) {
        if (name.equals("com.ctc.wstx.outputUnderlyingStream")) {
            return this.mWriter.getOutputStream();
        }
        if (name.equals("com.ctc.wstx.outputUnderlyingWriter")) {
            return this.mWriter.getWriter();
        }
        return this.mConfig.getProperty(name);
    }

    public abstract void setDefaultNamespace(String var1) throws XMLStreamException;

    public abstract void setNamespaceContext(NamespaceContext var1) throws XMLStreamException;

    public abstract void setPrefix(String var1, String var2) throws XMLStreamException;

    public abstract void writeAttribute(String var1, String var2) throws XMLStreamException;

    public abstract void writeAttribute(String var1, String var2, String var3) throws XMLStreamException;

    public abstract void writeAttribute(String var1, String var2, String var3, String var4) throws XMLStreamException;

    public void writeCData(String data) throws XMLStreamException {
        int ix;
        if (this.mConfig.willOutputCDataAsText()) {
            this.writeCharacters(data);
            return;
        }
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        this.verifyWriteCData();
        if (this.mVldContent == 3 && this.mValidator != null) {
            this.mValidator.validateText(data, false);
        }
        try {
            ix = this.mWriter.writeCData(data);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
        if (ix >= 0) {
            BaseStreamWriter.reportNwfContent(ErrorConsts.WERR_CDATA_CONTENT, DataUtil.Integer(ix));
        }
    }

    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        if (this.mCheckStructure && this.inPrologOrEpilog() && !StringUtil.isAllWhitespace(text, start, len)) {
            BaseStreamWriter.reportNwfStructure(ErrorConsts.WERR_PROLOG_NONWS_TEXT);
        }
        if (this.mVldContent <= 1) {
            if (this.mVldContent == 0) {
                this.reportInvalidContent(4);
            } else if (!StringUtil.isAllWhitespace(text, start, len)) {
                this.reportInvalidContent(4);
            }
        } else if (this.mVldContent == 3 && this.mValidator != null) {
            this.mValidator.validateText(text, start, len, false);
        }
        if (len > 0) {
            try {
                if (this.inPrologOrEpilog()) {
                    this.mWriter.writeRaw(text, start, len);
                } else {
                    this.mWriter.writeCharacters(text, start, len);
                }
            }
            catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
        }
    }

    public void writeCharacters(String text) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        if (this.mCheckStructure && this.inPrologOrEpilog() && !StringUtil.isAllWhitespace(text)) {
            BaseStreamWriter.reportNwfStructure(ErrorConsts.WERR_PROLOG_NONWS_TEXT);
        }
        if (this.mVldContent <= 1) {
            if (this.mVldContent == 0) {
                this.reportInvalidContent(4);
            } else if (!StringUtil.isAllWhitespace(text)) {
                this.reportInvalidContent(4);
            }
        } else if (this.mVldContent == 3 && this.mValidator != null) {
            this.mValidator.validateText(text, false);
        }
        if (this.inPrologOrEpilog()) {
            try {
                this.mWriter.writeRaw(text);
            }
            catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
            return;
        }
        int len = text.length();
        if (len >= 12) {
            char[] buf = this.getCopyBuffer();
            int offset = 0;
            while (len > 0) {
                int thisLen = len > buf.length ? buf.length : len;
                text.getChars(offset, offset + thisLen, buf, 0);
                try {
                    this.mWriter.writeCharacters(buf, 0, thisLen);
                }
                catch (IOException ioe) {
                    throw new WstxIOException(ioe);
                }
                offset += thisLen;
                len -= thisLen;
            }
        } else {
            try {
                this.mWriter.writeCharacters(text);
            }
            catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
        }
    }

    public void writeComment(String data) throws XMLStreamException {
        int ix;
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        if (this.mVldContent == 0) {
            this.reportInvalidContent(5);
        }
        try {
            ix = this.mWriter.writeComment(data);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
        if (ix >= 0) {
            BaseStreamWriter.reportNwfContent(ErrorConsts.WERR_COMMENT_CONTENT, DataUtil.Integer(ix));
        }
    }

    public abstract void writeDefaultNamespace(String var1) throws XMLStreamException;

    public void writeDTD(String dtd) throws XMLStreamException {
        this.verifyWriteDTD();
        this.mDtdRootElem = "";
        try {
            this.mWriter.writeDTD(dtd);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    public abstract void writeEmptyElement(String var1) throws XMLStreamException;

    public abstract void writeEmptyElement(String var1, String var2) throws XMLStreamException;

    public abstract void writeEmptyElement(String var1, String var2, String var3) throws XMLStreamException;

    public void writeEndDocument() throws XMLStreamException {
        this._finishDocument(false);
    }

    public abstract void writeEndElement() throws XMLStreamException;

    public void writeEntityRef(String name) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        if (this.mCheckStructure && this.inPrologOrEpilog()) {
            BaseStreamWriter.reportNwfStructure("Trying to output an entity reference outside main element tree (in prolog or epilog)");
        }
        if (this.mVldContent == 0) {
            this.reportInvalidContent(9);
        }
        try {
            this.mWriter.writeEntityReference(name);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    public abstract void writeNamespace(String var1, String var2) throws XMLStreamException;

    public void writeProcessingInstruction(String target) throws XMLStreamException {
        this.writeProcessingInstruction(target, null);
    }

    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        int ix;
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        if (this.mVldContent == 0) {
            this.reportInvalidContent(3);
        }
        try {
            ix = this.mWriter.writePI(target, data);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
        if (ix >= 0) {
            throw new XMLStreamException("Illegal input: processing instruction content has embedded '?>' in it (index " + ix + ")");
        }
    }

    public void writeStartDocument() throws XMLStreamException {
        if (this.mEncoding == null) {
            this.mEncoding = "UTF-8";
        }
        this.writeStartDocument(this.mEncoding, "1.0");
    }

    public void writeStartDocument(String version) throws XMLStreamException {
        this.writeStartDocument(this.mEncoding, version);
    }

    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        this.doWriteStartDocument(version, encoding, null);
    }

    protected void doWriteStartDocument(String version, String encoding, String standAlone) throws XMLStreamException {
        if (this.mCheckStructure && this.mAnyOutput) {
            BaseStreamWriter.reportNwfStructure("Can not output XML declaration, after other output has already been done.");
        }
        this.mAnyOutput = true;
        if (this.mConfig.willValidateContent() && version != null && version.length() > 0 && !version.equals("1.0") && !version.equals("1.1")) {
            BaseStreamWriter.reportNwfContent("Illegal version argument ('" + version + "'); should only use '" + "1.0" + "' or '" + "1.1" + "'");
        }
        if (version == null || version.length() == 0) {
            version = "1.0";
        }
        this.mXml11 = "1.1".equals(version);
        if (this.mXml11) {
            this.mWriter.enableXml11();
        }
        if (encoding != null && encoding.length() > 0 && (this.mEncoding == null || this.mEncoding.length() == 0)) {
            this.mEncoding = encoding;
        }
        try {
            this.mWriter.writeXmlDeclaration(version, encoding, standAlone);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    public abstract void writeStartElement(String var1) throws XMLStreamException;

    public abstract void writeStartElement(String var1, String var2) throws XMLStreamException;

    public abstract void writeStartElement(String var1, String var2, String var3) throws XMLStreamException;

    public void copyEventFromReader(XMLStreamReader2 sr, boolean preserveEventData) throws XMLStreamException {
        try {
            switch (sr.getEventType()) {
                case 7: {
                    String version = sr.getVersion();
                    if (version != null && version.length() != 0) {
                        if (sr.standaloneSet()) {
                            this.writeStartDocument(sr.getVersion(), sr.getCharacterEncodingScheme(), sr.isStandalone());
                        } else {
                            this.writeStartDocument(sr.getCharacterEncodingScheme(), sr.getVersion());
                        }
                    }
                    return;
                }
                case 8: {
                    this.writeEndDocument();
                    return;
                }
                case 1: {
                    if (sr instanceof StreamReaderImpl) {
                        StreamReaderImpl impl = (StreamReaderImpl)sr;
                        this.copyStartElement(impl.getInputElementStack(), impl.getAttributeCollector());
                    } else {
                        super.copyStartElement(sr);
                    }
                    return;
                }
                case 2: {
                    this.writeEndElement();
                    return;
                }
                case 6: {
                    this.mAnyOutput = true;
                    if (this.mStartElementOpen) {
                        this.closeStartElement(this.mEmptyElement);
                    }
                    sr.getText(this.wrapAsRawWriter(), preserveEventData);
                    return;
                }
                case 12: {
                    if (!this.mConfig.willOutputCDataAsText()) {
                        this.mAnyOutput = true;
                        if (this.mStartElementOpen) {
                            this.closeStartElement(this.mEmptyElement);
                        }
                        if (this.mCheckStructure && this.inPrologOrEpilog()) {
                            BaseStreamWriter.reportNwfStructure(ErrorConsts.WERR_PROLOG_CDATA);
                        }
                        this.mWriter.writeCDataStart();
                        sr.getText(this.wrapAsRawWriter(), preserveEventData);
                        this.mWriter.writeCDataEnd();
                        return;
                    }
                }
                case 4: {
                    this.mAnyOutput = true;
                    if (this.mStartElementOpen) {
                        this.closeStartElement(this.mEmptyElement);
                    }
                    sr.getText(this.wrapAsTextWriter(), preserveEventData);
                    return;
                }
                case 5: {
                    this.mAnyOutput = true;
                    if (this.mStartElementOpen) {
                        this.closeStartElement(this.mEmptyElement);
                    }
                    this.mWriter.writeCommentStart();
                    sr.getText(this.wrapAsRawWriter(), preserveEventData);
                    this.mWriter.writeCommentEnd();
                    return;
                }
                case 3: {
                    this.mWriter.writePIStart(sr.getPITarget(), true);
                    sr.getText(this.wrapAsRawWriter(), preserveEventData);
                    this.mWriter.writePIEnd();
                    return;
                }
                case 11: {
                    DTDInfo info = sr.getDTDInfo();
                    if (info == null) {
                        BaseStreamWriter.throwOutputError("Current state DOCTYPE, but not DTDInfo Object returned -- reader doesn't support DTDs?");
                    }
                    this.writeDTD(info);
                    return;
                }
                case 9: {
                    this.writeEntityRef(sr.getLocalName());
                    return;
                }
            }
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
        throw new XMLStreamException("Unrecognized event type (" + sr.getEventType() + "); not sure how to copy");
    }

    public void closeCompletely() throws XMLStreamException {
        this._finishDocument(true);
    }

    public boolean isPropertySupported(String name) {
        return this.mConfig.isPropertySupported(name);
    }

    public boolean setProperty(String name, Object value) {
        int oldFlags = this.mConfig.getConfigFlags();
        boolean wasChanged = this.mConfig.setProperty(name, value);
        int newFlags = this.mConfig.getConfigFlags();
        int changes = oldFlags ^ newFlags;
        if (changes != 0) {
            if ((changes & 0x100) != 0) {
                boolean bl = this.mCheckStructure = (newFlags & 0x100) != 0;
            }
            if ((changes & 0x800) != 0) {
                boolean bl = this.mCheckAttrs = (newFlags & 0x800) != 0;
            }
            if ((changes & 4) != 0) {
                this.mCfgAutomaticEmptyElems = (newFlags & 4) != 0;
            }
        }
        return wasChanged;
    }

    public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
        XMLValidator vld = schema.createValidator(this);
        if (this.mValidator == null) {
            this.mCheckStructure = true;
            this.mCheckAttrs = true;
            this.mValidator = vld;
        } else {
            this.mValidator = new ValidatorPair(this.mValidator, vld);
        }
        return vld;
    }

    public XMLValidator stopValidatingAgainst(XMLValidationSchema schema) throws XMLStreamException {
        XMLValidator[] results = new XMLValidator[2];
        XMLValidator found = null;
        if (ValidatorPair.removeValidator(this.mValidator, schema, results)) {
            found = results[0];
            this.mValidator = results[1];
            found.validationCompleted(false);
            if (this.mValidator == null) {
                this.resetValidationFlags();
            }
        }
        return found;
    }

    public XMLValidator stopValidatingAgainst(XMLValidator validator) throws XMLStreamException {
        XMLValidator[] results = new XMLValidator[2];
        XMLValidator found = null;
        if (ValidatorPair.removeValidator(this.mValidator, validator, results)) {
            found = results[0];
            this.mValidator = results[1];
            found.validationCompleted(false);
            if (this.mValidator == null) {
                this.resetValidationFlags();
            }
        }
        return found;
    }

    public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler h) {
        ValidationProblemHandler oldH = this.mVldProbHandler;
        this.mVldProbHandler = h;
        return oldH;
    }

    private void resetValidationFlags() {
        int flags = this.mConfig.getConfigFlags();
        this.mCheckStructure = (flags & 0x100) != 0;
        this.mCheckAttrs = (flags & 0x800) != 0;
    }

    public XMLStreamLocation2 getLocation() {
        return new WstxInputLocation(null, null, (String)null, (long)this.mWriter.getAbsOffset(), this.mWriter.getRow(), this.mWriter.getColumn());
    }

    public String getEncoding() {
        return this.mEncoding;
    }

    public void writeCData(char[] cbuf, int start, int len) throws XMLStreamException {
        int ix;
        if (this.mConfig.willOutputCDataAsText()) {
            this.writeCharacters(cbuf, start, len);
            return;
        }
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        this.verifyWriteCData();
        if (this.mVldContent == 3 && this.mValidator != null) {
            this.mValidator.validateText(cbuf, start, len, false);
        }
        try {
            ix = this.mWriter.writeCData(cbuf, start, len);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
        if (ix >= 0) {
            BaseStreamWriter.throwOutputError(ErrorConsts.WERR_CDATA_CONTENT, DataUtil.Integer(ix));
        }
    }

    public void writeDTD(DTDInfo info) throws XMLStreamException {
        this.writeDTD(info.getDTDRootName(), info.getDTDSystemId(), info.getDTDPublicId(), info.getDTDInternalSubset());
    }

    public void writeDTD(String rootName, String systemId, String publicId, String internalSubset) throws XMLStreamException {
        this.verifyWriteDTD();
        this.mDtdRootElem = rootName;
        try {
            this.mWriter.writeDTD(rootName, systemId, publicId, internalSubset);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    public abstract void writeFullEndElement() throws XMLStreamException;

    public void writeStartDocument(String version, String encoding, boolean standAlone) throws XMLStreamException {
        this.doWriteStartDocument(version, encoding, standAlone ? "yes" : "no");
    }

    public void writeRaw(String text) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        try {
            this.mWriter.writeRaw(text, 0, text.length());
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    public void writeRaw(String text, int start, int offset) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        try {
            this.mWriter.writeRaw(text, start, offset);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    public void writeRaw(char[] text, int start, int offset) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        try {
            this.mWriter.writeRaw(text, start, offset);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    public void writeSpace(String text) throws XMLStreamException {
        this.writeRaw(text);
    }

    public void writeSpace(char[] text, int offset, int length) throws XMLStreamException {
        this.writeRaw(text, offset, length);
    }

    public String getXmlVersion() {
        return this.mXml11 ? "1.1" : "1.0";
    }

    public abstract QName getCurrentElementName();

    public abstract String getNamespaceURI(String var1);

    public String getBaseUri() {
        return null;
    }

    public Location getValidationLocation() {
        return this.getLocation();
    }

    public void reportProblem(XMLValidationProblem prob) throws XMLStreamException {
        if (this.mVldProbHandler != null) {
            this.mVldProbHandler.reportProblem(prob);
            return;
        }
        if (prob.getSeverity() > 2) {
            throw WstxValidationException.create(prob);
        }
        XMLReporter rep = this.mConfig.getProblemReporter();
        if (rep != null) {
            this.doReportProblem(rep, prob);
        } else if (prob.getSeverity() >= 2) {
            throw WstxValidationException.create(prob);
        }
    }

    public int addDefaultAttribute(String localName, String uri, String prefix, String value) {
        return -1;
    }

    public boolean isNotationDeclared(String name) {
        return false;
    }

    public boolean isUnparsedEntityDeclared(String name) {
        return false;
    }

    public int getAttributeCount() {
        return 0;
    }

    public String getAttributeLocalName(int index) {
        return null;
    }

    public String getAttributeNamespace(int index) {
        return null;
    }

    public String getAttributePrefix(int index) {
        return null;
    }

    public String getAttributeValue(int index) {
        return null;
    }

    public String getAttributeValue(String nsURI, String localName) {
        return null;
    }

    public String getAttributeType(int index) {
        return "";
    }

    public int findAttributeIndex(String nsURI, String localName) {
        return -1;
    }

    public final Writer wrapAsRawWriter() {
        return this.mWriter.wrapAsRawWriter();
    }

    public final Writer wrapAsTextWriter() {
        return this.mWriter.wrapAsTextWriter();
    }

    protected boolean isValidating() {
        return this.mValidator != null;
    }

    public abstract void writeStartElement(StartElement var1) throws XMLStreamException;

    public abstract void writeEndElement(QName var1) throws XMLStreamException;

    public void writeCharacters(Characters ch) throws XMLStreamException {
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        if (this.mCheckStructure && this.inPrologOrEpilog() && !ch.isIgnorableWhiteSpace() && !ch.isWhiteSpace()) {
            BaseStreamWriter.reportNwfStructure(ErrorConsts.WERR_PROLOG_NONWS_TEXT);
        }
        if (this.mVldContent <= 1) {
            if (this.mVldContent == 0) {
                this.reportInvalidContent(4);
            } else if (!ch.isIgnorableWhiteSpace() && !ch.isWhiteSpace()) {
                this.reportInvalidContent(4);
            }
        } else if (this.mVldContent == 3 && this.mValidator != null) {
            this.mValidator.validateText(ch.getData(), false);
        }
        try {
            this.mWriter.writeCharacters(ch.getData());
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    protected abstract void closeStartElement(boolean var1) throws XMLStreamException;

    protected final boolean inPrologOrEpilog() {
        return this.mState != 2;
    }

    private final void _finishDocument(boolean forceRealClose) throws XMLStreamException {
        char[] buf;
        if (this.mState != 3) {
            if (this.mCheckStructure && this.mState == 1) {
                BaseStreamWriter.reportNwfStructure("Trying to write END_DOCUMENT when document has no root (ie. trying to output empty document).");
            }
            if (this.mStartElementOpen) {
                this.closeStartElement(this.mEmptyElement);
            }
            if (this.mState == 2 && this.mConfig.automaticEndElementsEnabled()) {
                do {
                    this.writeEndElement();
                } while (this.mState == 2);
            }
        }
        if ((buf = this.mCopyBuffer) != null) {
            this.mCopyBuffer = null;
            this.mConfig.freeMediumCBuffer(buf);
        }
        try {
            this.mWriter.close(forceRealClose);
        }
        catch (IOException ie) {
            throw new WstxIOException(ie);
        }
    }

    public abstract void copyStartElement(InputElementStack var1, AttributeCollector var2) throws IOException, XMLStreamException;

    public abstract String validateQNamePrefix(QName var1) throws XMLStreamException;

    protected final void verifyWriteCData() throws XMLStreamException {
        if (this.mCheckStructure && this.inPrologOrEpilog()) {
            BaseStreamWriter.reportNwfStructure(ErrorConsts.WERR_PROLOG_CDATA);
        }
        if (this.mVldContent <= 1) {
            this.reportInvalidContent(12);
        }
    }

    protected final void verifyWriteDTD() throws XMLStreamException {
        if (this.mCheckStructure) {
            if (this.mState != 1) {
                throw new XMLStreamException("Can not write DOCTYPE declaration (DTD) when not in prolog any more (state " + this.mState + "; start element(s) written)");
            }
            if (this.mDtdRootElem != null) {
                throw new XMLStreamException("Trying to write multiple DOCTYPE declarations");
            }
        }
    }

    protected void verifyRootElement(String localName, String prefix) throws XMLStreamException {
        if (this.isValidating() && this.mDtdRootElem != null && this.mDtdRootElem.length() > 0) {
            String wrongElem = null;
            if (!localName.equals(this.mDtdRootElem)) {
                int lnLen = localName.length();
                int oldLen = this.mDtdRootElem.length();
                if (oldLen <= lnLen || !this.mDtdRootElem.endsWith(localName) || this.mDtdRootElem.charAt(oldLen - lnLen - 1) != ':') {
                    wrongElem = prefix == null ? localName : (prefix.length() == 0 ? "[unknown]:" + localName : prefix + ":" + localName);
                }
            }
            if (wrongElem != null) {
                this.reportValidationProblem(ErrorConsts.ERR_VLD_WRONG_ROOT, wrongElem, this.mDtdRootElem);
            }
        }
        this.mState = 2;
    }

    protected static void throwOutputError(String msg) throws XMLStreamException {
        throw new XMLStreamException(msg);
    }

    protected static void throwOutputError(String format, Object arg) throws XMLStreamException {
        String msg = MessageFormat.format(format, arg);
        BaseStreamWriter.throwOutputError(msg);
    }

    protected static void reportIllegalMethod(String msg) throws XMLStreamException {
        BaseStreamWriter.throwOutputError(msg);
    }

    protected static void reportNwfStructure(String msg) throws XMLStreamException {
        BaseStreamWriter.throwOutputError(msg);
    }

    protected static void reportNwfStructure(String msg, Object arg) throws XMLStreamException {
        BaseStreamWriter.throwOutputError(msg, arg);
    }

    protected static void reportNwfContent(String msg) throws XMLStreamException {
        BaseStreamWriter.throwOutputError(msg);
    }

    protected static void reportNwfContent(String msg, Object arg) throws XMLStreamException {
        BaseStreamWriter.throwOutputError(msg, arg);
    }

    protected static void reportNwfAttr(String msg) throws XMLStreamException {
        BaseStreamWriter.throwOutputError(msg);
    }

    protected static void reportNwfAttr(String msg, Object arg) throws XMLStreamException {
        BaseStreamWriter.throwOutputError(msg, arg);
    }

    protected static void throwFromIOE(IOException ioe) throws XMLStreamException {
        throw new WstxIOException(ioe);
    }

    protected static void reportIllegalArg(String msg) throws IllegalArgumentException {
        throw new IllegalArgumentException(msg);
    }

    protected void reportInvalidContent(int evtType) throws XMLStreamException {
        switch (this.mVldContent) {
            case 0: {
                this.reportValidationProblem(ErrorConsts.ERR_VLD_EMPTY, this.getTopElementDesc(), ErrorConsts.tokenTypeDesc(evtType));
                break;
            }
            case 1: {
                this.reportValidationProblem(ErrorConsts.ERR_VLD_NON_MIXED, (Object)this.getTopElementDesc());
                break;
            }
            case 3: 
            case 4: {
                this.reportValidationProblem(ErrorConsts.ERR_VLD_ANY, this.getTopElementDesc(), ErrorConsts.tokenTypeDesc(evtType));
                break;
            }
            default: {
                this.reportValidationProblem("Internal error: trying to report invalid content for " + evtType);
            }
        }
    }

    public void reportValidationProblem(String msg, Location loc, int severity) throws XMLStreamException {
        this.reportProblem(new XMLValidationProblem(loc, msg, severity));
    }

    public void reportValidationProblem(String msg, int severity) throws XMLStreamException {
        this.reportProblem(new XMLValidationProblem(this.getValidationLocation(), msg, severity));
    }

    public void reportValidationProblem(String msg) throws XMLStreamException {
        this.reportProblem(new XMLValidationProblem(this.getValidationLocation(), msg, 2));
    }

    public void reportValidationProblem(Location loc, String msg) throws XMLStreamException {
        this.reportProblem(new XMLValidationProblem(this.getValidationLocation(), msg));
    }

    public void reportValidationProblem(String format, Object arg) throws XMLStreamException {
        String msg = MessageFormat.format(format, arg);
        this.reportProblem(new XMLValidationProblem(this.getValidationLocation(), msg));
    }

    public void reportValidationProblem(String format, Object arg, Object arg2) throws XMLStreamException {
        String msg = MessageFormat.format(format, arg, arg2);
        this.reportProblem(new XMLValidationProblem(this.getValidationLocation(), msg));
    }

    protected void doReportProblem(XMLReporter rep, String probType, String msg, Location loc) throws XMLStreamException {
        if (loc == null) {
            loc = this.getLocation();
        }
        this.doReportProblem(rep, new XMLValidationProblem(loc, msg, 2, probType));
    }

    protected void doReportProblem(XMLReporter rep, XMLValidationProblem prob) throws XMLStreamException {
        if (rep != null) {
            Location loc = prob.getLocation();
            if (loc == null) {
                loc = this.getLocation();
                prob.setLocation(loc);
            }
            if (prob.getType() == null) {
                prob.setType(ErrorConsts.WT_VALIDATION);
            }
            rep.report(prob.getMessage(), prob.getType(), prob, loc);
        }
    }

    protected abstract String getTopElementDesc();

    protected final char[] getCopyBuffer() {
        char[] buf = this.mCopyBuffer;
        if (buf == null) {
            this.mCopyBuffer = buf = this.mConfig.allocMediumCBuffer(512);
        }
        return buf;
    }

    protected final char[] getCopyBuffer(int minLen) {
        char[] buf = this.mCopyBuffer;
        if (buf == null || minLen > buf.length) {
            this.mCopyBuffer = buf = this.mConfig.allocMediumCBuffer(Math.max(512, minLen));
        }
        return buf;
    }

    public String toString() {
        return "[StreamWriter: " + this.getClass() + ", underlying outputter: " + (this.mWriter == null ? "NULL" : this.mWriter.toString() + "]");
    }
}

