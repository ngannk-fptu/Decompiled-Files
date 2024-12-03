/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sr;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.dtd.MinimalDTDReader;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.exc.WstxException;
import com.ctc.wstx.io.BranchingReaderSource;
import com.ctc.wstx.io.InputBootstrapper;
import com.ctc.wstx.io.WstxInputSource;
import com.ctc.wstx.sr.AttributeCollector;
import com.ctc.wstx.sr.ElemCallback;
import com.ctc.wstx.sr.InputElementStack;
import com.ctc.wstx.sr.ReaderCreator;
import com.ctc.wstx.sr.StreamReaderImpl;
import com.ctc.wstx.sr.StreamScanner;
import com.ctc.wstx.util.DefaultXmlSymbolTable;
import com.ctc.wstx.util.ExceptionUtil;
import com.ctc.wstx.util.TextBuffer;
import com.ctc.wstx.util.TextBuilder;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.AttributeInfo;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.LocationInfo;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.typed.TypedXMLStreamException;
import org.codehaus.stax2.validation.DTDValidationSchema;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public abstract class BasicStreamReader
extends StreamScanner
implements StreamReaderImpl,
DTDInfo,
LocationInfo {
    static final int DOC_STANDALONE_UNKNOWN = 0;
    static final int DOC_STANDALONE_YES = 1;
    static final int DOC_STANDALONE_NO = 2;
    static final int STATE_PROLOG = 0;
    static final int STATE_TREE = 1;
    static final int STATE_EPILOG = 2;
    static final int STATE_MULTIDOC_HACK = 3;
    static final int STATE_CLOSED = 4;
    static final int TOKEN_NOT_STARTED = 0;
    static final int TOKEN_STARTED = 1;
    static final int TOKEN_PARTIAL_SINGLE = 2;
    static final int TOKEN_FULL_SINGLE = 3;
    static final int TOKEN_FULL_COALESCED = 4;
    protected static final int MASK_GET_TEXT = 6768;
    protected static final int MASK_GET_TEXT_XXX = 4208;
    protected static final int MASK_GET_TEXT_WITH_WRITER = 6776;
    protected static final int MASK_GET_ELEMENT_TEXT = 4688;
    static final int ALL_WS_UNKNOWN = 0;
    static final int ALL_WS_YES = 1;
    static final int ALL_WS_NO = 2;
    private static final int INDENT_CHECK_START = 16;
    private static final int INDENT_CHECK_MAX = 40;
    protected static final String sPrefixXml = DefaultXmlSymbolTable.getXmlSymbol();
    protected static final String sPrefixXmlns = DefaultXmlSymbolTable.getXmlnsSymbol();
    protected final int mConfigFlags;
    protected final boolean mCfgCoalesceText;
    protected final boolean mCfgReportTextAsChars;
    protected final boolean mCfgLazyParsing;
    protected final int mShortestTextSegment;
    protected final ReaderCreator mOwner;
    protected int mDocStandalone = 0;
    protected String mRootPrefix;
    protected String mRootLName;
    protected String mDtdPublicId;
    protected String mDtdSystemId;
    protected final TextBuffer mTextBuffer;
    protected final InputElementStack mElementStack;
    protected final AttributeCollector mAttrCollector;
    protected boolean mStDoctypeFound = false;
    protected int mTokenState = 4;
    protected final int mStTextThreshold;
    protected int mCurrTextLength;
    protected boolean mStEmptyElem = false;
    protected int mParseState;
    protected int mCurrToken = 7;
    protected int mSecondaryToken = 7;
    protected int mWsStatus;
    protected boolean mValidateText = false;
    protected int mCheckIndentation;
    protected XMLStreamException mPendingException = null;
    protected Map<String, EntityDecl> mGeneralEntities = null;
    protected int mVldContent = 4;
    protected boolean mReturnNullForDefaultNamespace;

    protected BasicStreamReader(InputBootstrapper bs, BranchingReaderSource input, ReaderCreator owner, ReaderConfig cfg, InputElementStack elemStack, boolean forER) throws XMLStreamException {
        super(input, cfg, cfg.getEntityResolver());
        this.mOwner = owner;
        this.mTextBuffer = TextBuffer.createRecyclableBuffer(cfg);
        this.mConfigFlags = cfg.getConfigFlags();
        this.mCfgCoalesceText = (this.mConfigFlags & 2) != 0;
        this.mCfgReportTextAsChars = (this.mConfigFlags & 0x200) == 0;
        this.mXml11 = cfg.isXml11();
        this.mCheckIndentation = this.mNormalizeLFs ? 16 : 0;
        boolean bl = this.mCfgLazyParsing = !forER && (this.mConfigFlags & 0x40000) != 0;
        if (this.mCfgCoalesceText) {
            this.mStTextThreshold = 4;
            this.mShortestTextSegment = Integer.MAX_VALUE;
        } else {
            this.mStTextThreshold = 2;
            this.mShortestTextSegment = forER && !cfg.isCoalescingExplicitlyDisabled() ? Integer.MAX_VALUE : cfg.getShortestReportedTextSegment();
        }
        this.mDocXmlVersion = bs.getDeclaredVersion();
        this.mDocInputEncoding = bs.getInputEncoding();
        this.mDocXmlEncoding = bs.getDeclaredEncoding();
        String sa = bs.getStandalone();
        this.mDocStandalone = sa == null ? 0 : ("yes".equals(sa) ? 1 : 2);
        this.mParseState = this.mConfig.inputParsingModeFragment() ? 1 : 0;
        this.mElementStack = elemStack;
        this.mAttrCollector = elemStack.getAttrCollector();
        input.initInputLocation(this, this.mCurrDepth, 0);
        elemStack.connectReporter(this);
        this.mReturnNullForDefaultNamespace = this.mConfig.returnNullForDefaultNamespace();
    }

    protected static InputElementStack createElementStack(ReaderConfig cfg) {
        return new InputElementStack(cfg, cfg.willSupportNamespaces());
    }

    @Override
    public String getCharacterEncodingScheme() {
        return this.mDocXmlEncoding;
    }

    @Override
    public String getEncoding() {
        return this.mDocInputEncoding;
    }

    @Override
    public String getVersion() {
        if (this.mDocXmlVersion == 256) {
            return "1.0";
        }
        if (this.mDocXmlVersion == 272) {
            return "1.1";
        }
        return null;
    }

    @Override
    public boolean isStandalone() {
        return this.mDocStandalone == 1;
    }

    @Override
    public boolean standaloneSet() {
        return this.mDocStandalone != 0;
    }

    @Override
    public Object getProperty(String name) {
        if ("com.ctc.wstx.baseURL".equals(name)) {
            try {
                return this.mInput.getSource();
            }
            catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return this.mConfig.safeGetProperty(name);
    }

    @Override
    public int getAttributeCount() {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        return this.mAttrCollector.getCount();
    }

    @Override
    public String getAttributeLocalName(int index) {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        return this.mAttrCollector.getLocalName(index);
    }

    @Override
    public QName getAttributeName(int index) {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        return this.mAttrCollector.getQName(index);
    }

    @Override
    public String getAttributeNamespace(int index) {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        String uri = this.mAttrCollector.getURI(index);
        return uri == null ? "" : uri;
    }

    @Override
    public String getAttributePrefix(int index) {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        String p = this.mAttrCollector.getPrefix(index);
        return p == null ? "" : p;
    }

    @Override
    public String getAttributeType(int index) {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        return this.mElementStack.getAttributeType(index);
    }

    @Override
    public String getAttributeValue(int index) {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        return this.mAttrCollector.getValue(index);
    }

    @Override
    public String getAttributeValue(String nsURI, String localName) {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        if (nsURI == null) {
            return this.mAttrCollector.getValueByLocalName(localName);
        }
        return this.mAttrCollector.getValue(nsURI, localName);
    }

    @Override
    public String getElementText() throws XMLStreamException {
        int type;
        int type2;
        if (this.mCurrToken != 1) {
            this.throwParseError(ErrorConsts.ERR_STATE_NOT_STELEM, null, null);
        }
        if (this.mStEmptyElem) {
            this.mStEmptyElem = false;
            this.mCurrToken = 2;
            return "";
        }
        do {
            if ((type2 = this.next()) != 2) continue;
            return "";
        } while (type2 == 5 || type2 == 3);
        if ((1 << type2 & 0x1250) == 0) {
            throw this._constructUnexpectedInTyped(type2);
        }
        if (this.mTokenState < 4) {
            this.readCoalescedText(this.mCurrToken, false);
        }
        if (this.mInputPtr + 1 < this.mInputEnd && this.mInputBuffer[this.mInputPtr] == '<' && this.mInputBuffer[this.mInputPtr + 1] == '/') {
            this.mInputPtr += 2;
            this.mCurrToken = 2;
            String result = this.mTextBuffer.contentsAsString();
            this.readEndElem();
            return result;
        }
        int extra = 1 + (this.mTextBuffer.size() >> 1);
        StringBuilder sb = this.mTextBuffer.contentsAsStringBuilder(extra);
        while ((type = this.next()) != 2) {
            if ((1 << type & 0x1250) != 0) {
                if (this.mTokenState < this.mStTextThreshold) {
                    this.finishToken(false);
                }
                this.verifyLimit("Text size", this.mConfig.getMaxTextLength(), sb.length());
                this.mTextBuffer.contentsToStringBuilder(sb);
                continue;
            }
            if (type == 5 || type == 3) continue;
            throw this._constructUnexpectedInTyped(type);
        }
        return sb.toString();
    }

    @Override
    public int getEventType() {
        if (this.mCurrToken == 12 && (this.mCfgCoalesceText || this.mCfgReportTextAsChars)) {
            return 4;
        }
        return this.mCurrToken;
    }

    @Override
    public String getLocalName() {
        if (this.mCurrToken == 1 || this.mCurrToken == 2) {
            return this.mElementStack.getLocalName();
        }
        if (this.mCurrToken == 9) {
            return this.mCurrEntity == null ? this.mCurrName : this.mCurrEntity.getName();
        }
        throw new IllegalStateException("Current state not START_ELEMENT, END_ELEMENT or ENTITY_REFERENCE");
    }

    @Override
    public QName getName() {
        if (this.mCurrToken != 1 && this.mCurrToken != 2) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_ELEM);
        }
        return this.mElementStack.getCurrentElementName();
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return this.mElementStack;
    }

    @Override
    public int getNamespaceCount() {
        if (this.mCurrToken != 1 && this.mCurrToken != 2) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_ELEM);
        }
        return this.mElementStack.getCurrentNsCount();
    }

    @Override
    public String getNamespacePrefix(int index) {
        if (this.mCurrToken != 1 && this.mCurrToken != 2) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_ELEM);
        }
        String p = this.mElementStack.getLocalNsPrefix(index);
        if (p == null) {
            return this.mReturnNullForDefaultNamespace ? null : "";
        }
        return p;
    }

    @Override
    public String getNamespaceURI() {
        if (this.mCurrToken != 1 && this.mCurrToken != 2) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_ELEM);
        }
        String uri = this.mElementStack.getNsURI();
        return uri == null ? "" : uri;
    }

    @Override
    public String getNamespaceURI(int index) {
        if (this.mCurrToken != 1 && this.mCurrToken != 2) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_ELEM);
        }
        String uri = this.mElementStack.getLocalNsURI(index);
        return uri == null ? "" : uri;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        if (this.mCurrToken != 1 && this.mCurrToken != 2) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_ELEM);
        }
        return this.mElementStack.getNamespaceURI(prefix);
    }

    @Override
    public String getPIData() {
        if (this.mCurrToken != 3) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_PI);
        }
        if (this.mTokenState <= 1) {
            this.safeFinishToken();
        }
        return this.mTextBuffer.contentsAsString();
    }

    @Override
    public String getPITarget() {
        if (this.mCurrToken != 3) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_PI);
        }
        return this.mCurrName;
    }

    @Override
    public String getPrefix() {
        if (this.mCurrToken != 1 && this.mCurrToken != 2) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_ELEM);
        }
        String p = this.mElementStack.getPrefix();
        return p == null ? "" : p;
    }

    @Override
    public String getText() {
        int currToken = this.mCurrToken;
        if ((1 << currToken & 0x1A70) == 0) {
            this.throwNotTextual(currToken);
        }
        if (this.mTokenState < this.mStTextThreshold) {
            this.safeFinishToken();
        }
        if (currToken == 9) {
            return this.mCurrEntity == null ? null : this.mCurrEntity.getReplacementText();
        }
        if (currToken == 11) {
            return this.getDTDInternalSubset();
        }
        return this.mTextBuffer.contentsAsString();
    }

    @Override
    public char[] getTextCharacters() {
        int currToken = this.mCurrToken;
        if ((1 << currToken & 0x1070) == 0) {
            this.throwNotTextXxx(currToken);
        }
        if (this.mTokenState < this.mStTextThreshold) {
            this.safeFinishToken();
        }
        if (currToken == 9) {
            return this.mCurrEntity.getReplacementChars();
        }
        if (currToken == 11) {
            return this.getDTDInternalSubsetArray();
        }
        return this.mTextBuffer.getTextBuffer();
    }

    @Override
    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int len) {
        int currToken = this.mCurrToken;
        if ((1 << currToken & 0x1070) == 0) {
            this.throwNotTextXxx(currToken);
        }
        if (this.mTokenState < this.mStTextThreshold) {
            this.safeFinishToken();
        }
        return this.mTextBuffer.contentsToArray(sourceStart, target, targetStart, len);
    }

    @Override
    public int getTextLength() {
        int currToken = this.mCurrToken;
        if ((1 << currToken & 0x1070) == 0) {
            this.throwNotTextXxx(currToken);
        }
        if (this.mTokenState < this.mStTextThreshold) {
            this.safeFinishToken();
        }
        return this.mTextBuffer.size();
    }

    @Override
    public int getTextStart() {
        int currToken = this.mCurrToken;
        if ((1 << currToken & 0x1070) == 0) {
            this.throwNotTextXxx(currToken);
        }
        if (this.mTokenState < this.mStTextThreshold) {
            this.safeFinishToken();
        }
        return this.mTextBuffer.getTextStart();
    }

    @Override
    public boolean hasName() {
        return this.mCurrToken == 1 || this.mCurrToken == 2;
    }

    @Override
    public boolean hasNext() {
        return this.mCurrToken != 8 || this.mParseState == 3;
    }

    @Override
    public boolean hasText() {
        return (1 << this.mCurrToken & 0x1A70) != 0;
    }

    @Override
    public boolean isAttributeSpecified(int index) {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        return this.mAttrCollector.isSpecified(index);
    }

    @Override
    public boolean isCharacters() {
        return 4 == this.getEventType();
    }

    @Override
    public boolean isEndElement() {
        return this.mCurrToken == 2;
    }

    @Override
    public boolean isStartElement() {
        return this.mCurrToken == 1;
    }

    @Override
    public boolean isWhiteSpace() {
        int currToken = this.mCurrToken;
        if (currToken == 4 || currToken == 12) {
            if (this.mTokenState < this.mStTextThreshold) {
                this.safeFinishToken();
            }
            if (this.mWsStatus == 0) {
                this.mWsStatus = this.mTextBuffer.isAllWhitespace() ? 1 : 2;
            }
            return this.mWsStatus == 1;
        }
        return currToken == 6;
    }

    @Override
    public void require(int type, String nsUri, String localName) throws XMLStreamException {
        int curr = this.mCurrToken;
        if (curr != type) {
            if (curr == 12) {
                if (this.mCfgCoalesceText || this.mCfgReportTextAsChars) {
                    curr = 4;
                }
            } else if (curr == 6) {
                // empty if block
            }
        }
        if (type != curr) {
            this.throwParseError("Expected type " + this.tokenTypeDesc(type) + ", current type " + this.tokenTypeDesc(curr));
        }
        if (localName != null) {
            String n;
            if (curr != 1 && curr != 2 && curr != 9) {
                this.throwParseError("Expected non-null local name, but current token not a START_ELEMENT, END_ELEMENT or ENTITY_REFERENCE (was " + this.tokenTypeDesc(this.mCurrToken) + ")");
            }
            if ((n = this.getLocalName()) != localName && !n.equals(localName)) {
                this.throwParseError("Expected local name '" + localName + "'; current local name '" + n + "'.");
            }
        }
        if (nsUri != null) {
            if (curr != 1 && curr != 2) {
                this.throwParseError("Expected non-null NS URI, but current token not a START_ELEMENT or END_ELEMENT (was " + this.tokenTypeDesc(curr) + ")");
            }
            String uri = this.mElementStack.getNsURI();
            if (nsUri.length() == 0) {
                if (uri != null && uri.length() > 0) {
                    this.throwParseError("Expected empty namespace, instead have '" + uri + "'.");
                }
            } else if (nsUri != uri && !nsUri.equals(uri)) {
                this.throwParseError("Expected namespace '" + nsUri + "'; have '" + uri + "'.");
            }
        }
    }

    @Override
    public final int next() throws XMLStreamException {
        if (this.mPendingException != null) {
            XMLStreamException strEx = this.mPendingException;
            this.mPendingException = null;
            throw strEx;
        }
        if (this.mParseState == 1) {
            int type;
            this.mCurrToken = type = this.nextFromTree();
            if (this.mTokenState < this.mStTextThreshold && (!this.mCfgLazyParsing || this.mValidateText && (type == 4 || type == 12))) {
                this.finishToken(false);
            }
            if (type == 12) {
                if (this.mValidateText) {
                    this.mElementStack.validateText(this.mTextBuffer, false);
                }
                if (this.mCfgCoalesceText || this.mCfgReportTextAsChars) {
                    return 4;
                }
                this.mCurrTextLength += this.mTextBuffer.size();
                this.verifyLimit("Text size", this.mConfig.getMaxTextLength(), this.mCurrTextLength);
            } else if (type == 4) {
                if (this.mValidateText) {
                    if (this.mInputPtr + 1 < this.mInputEnd && this.mInputBuffer[this.mInputPtr] == '<' && this.mInputBuffer[this.mInputPtr + 1] == '/') {
                        this.mElementStack.validateText(this.mTextBuffer, true);
                    } else {
                        this.mElementStack.validateText(this.mTextBuffer, false);
                    }
                }
                this.mCurrTextLength += this.mTextBuffer.size();
                this.verifyLimit("Text size", this.mConfig.getMaxTextLength(), this.mCurrTextLength);
            } else if (type == 1 || type == 2) {
                this.mCurrTextLength = 0;
            }
            return type;
        }
        if (this.mParseState == 0) {
            this.nextFromProlog(true);
        } else if (this.mParseState == 2) {
            if (this.nextFromProlog(false)) {
                this.mSecondaryToken = 0;
            }
        } else if (this.mParseState == 3) {
            this.mCurrToken = this.nextFromMultiDocState();
        } else {
            if (this.mSecondaryToken == 8) {
                this.mSecondaryToken = 0;
                return 8;
            }
            throw new NoSuchElementException();
        }
        return this.mCurrToken;
    }

    @Override
    public int nextTag() throws XMLStreamException {
        block5: while (true) {
            int next = this.next();
            switch (next) {
                case 3: 
                case 5: 
                case 6: {
                    continue block5;
                }
                case 4: 
                case 12: {
                    if (this.mTokenState < this.mStTextThreshold) {
                        this.finishToken(false);
                    }
                    if (this.mWsStatus == 0) {
                        int n = this.mWsStatus = this.mTextBuffer.isAllWhitespace() ? 1 : 2;
                    }
                    if (this.mWsStatus == 1) continue block5;
                    this.throwParseError("Received non-all-whitespace CHARACTERS or CDATA event in nextTag().");
                    break;
                }
                case 1: 
                case 2: {
                    return next;
                }
            }
            this.throwParseError("Received event " + ErrorConsts.tokenTypeDesc(next) + ", instead of START_ELEMENT or END_ELEMENT.");
        }
    }

    @Override
    public void close() throws XMLStreamException {
        if (this.mParseState != 4) {
            this.mParseState = 4;
            if (this.mCurrToken != 8) {
                this.mSecondaryToken = 8;
                this.mCurrToken = 8;
                if (this.mSymbols.isDirty()) {
                    this.mOwner.updateSymbolTable(this.mSymbols);
                }
            }
            this.closeAllInput(false);
            this.mTextBuffer.recycle(true);
        }
    }

    @Override
    @Deprecated
    public Object getFeature(String name) {
        throw new IllegalArgumentException(MessageFormat.format(ErrorConsts.ERR_UNKNOWN_FEATURE, name));
    }

    @Override
    @Deprecated
    public void setFeature(String name, Object value) {
        throw new IllegalArgumentException(MessageFormat.format(ErrorConsts.ERR_UNKNOWN_FEATURE, name));
    }

    @Override
    public boolean isPropertySupported(String name) {
        return this.mConfig.isPropertySupported(name);
    }

    @Override
    public boolean setProperty(String name, Object value) {
        boolean ok = this.mConfig.setProperty(name, value);
        if (ok && "com.ctc.wstx.baseURL".equals(name)) {
            this.mInput.overrideSource(this.mConfig.getBaseURL());
        }
        return ok;
    }

    @Override
    public void skipElement() throws XMLStreamException {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        int nesting = 1;
        while (true) {
            int type;
            if ((type = this.next()) == 1) {
                ++nesting;
                continue;
            }
            if (type == 2 && --nesting == 0) break;
        }
    }

    @Override
    public AttributeInfo getAttributeInfo() throws XMLStreamException {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        return this.mElementStack;
    }

    @Override
    public DTDInfo getDTDInfo() throws XMLStreamException {
        if (this.mCurrToken != 11) {
            return null;
        }
        if (this.mTokenState < 3) {
            this.finishToken(false);
        }
        return this;
    }

    @Override
    public final LocationInfo getLocationInfo() {
        return this;
    }

    @Override
    public int getText(Writer w, boolean preserveContents) throws IOException, XMLStreamException {
        int currToken = this.mCurrToken;
        if ((1 << currToken & 0x1A78) == 0) {
            this.throwNotTextual(currToken);
        }
        if (!preserveContents) {
            if (currToken == 4) {
                int count = this.mTextBuffer.rawContentsTo(w);
                this.mTextBuffer.resetWithEmpty();
                if (this.mTokenState < 3) {
                    count += this.readAndWriteText(w);
                }
                if (this.mCfgCoalesceText && this.mTokenState < 4 && this.mCfgCoalesceText) {
                    count += this.readAndWriteCoalesced(w, false);
                }
                return count;
            }
            if (currToken == 12) {
                int count = this.mTextBuffer.rawContentsTo(w);
                this.mTextBuffer.resetWithEmpty();
                if (this.mTokenState < 3) {
                    count += this.readAndWriteCData(w);
                }
                if (this.mCfgCoalesceText && this.mTokenState < 4 && this.mCfgCoalesceText) {
                    count += this.readAndWriteCoalesced(w, true);
                }
                return count;
            }
        }
        if (this.mTokenState < this.mStTextThreshold) {
            this.finishToken(false);
        }
        if (currToken == 9) {
            return this.mCurrEntity.getReplacementText(w);
        }
        if (currToken == 11) {
            char[] ch = this.getDTDInternalSubsetArray();
            if (ch != null) {
                w.write(ch);
                return ch.length;
            }
            return 0;
        }
        return this.mTextBuffer.rawContentsTo(w);
    }

    @Override
    public int getDepth() {
        return this.mElementStack.getDepth();
    }

    @Override
    public boolean isEmptyElement() throws XMLStreamException {
        return this.mCurrToken == 1 ? this.mStEmptyElem : false;
    }

    @Override
    public NamespaceContext getNonTransientNamespaceContext() {
        return this.mElementStack.createNonTransientNsContext(null);
    }

    @Override
    public String getPrefixedName() {
        switch (this.mCurrToken) {
            case 1: 
            case 2: {
                String prefix = this.mElementStack.getPrefix();
                String ln = this.mElementStack.getLocalName();
                if (prefix == null) {
                    return ln;
                }
                StringBuilder sb = new StringBuilder(ln.length() + 1 + prefix.length());
                sb.append(prefix);
                sb.append(':');
                sb.append(ln);
                return sb.toString();
            }
            case 9: {
                return this.getLocalName();
            }
            case 3: {
                return this.getPITarget();
            }
            case 11: {
                return this.getDTDRootName();
            }
        }
        throw new IllegalStateException("Current state not START_ELEMENT, END_ELEMENT, ENTITY_REFERENCE, PROCESSING_INSTRUCTION or DTD");
    }

    @Override
    public void closeCompletely() throws XMLStreamException {
        this.closeAllInput(true);
    }

    @Override
    public Object getProcessedDTD() {
        return null;
    }

    @Override
    public String getDTDRootName() {
        if (this.mRootPrefix == null) {
            return this.mRootLName;
        }
        return this.mRootPrefix + ":" + this.mRootLName;
    }

    @Override
    public String getDTDPublicId() {
        return this.mDtdPublicId;
    }

    @Override
    public String getDTDSystemId() {
        return this.mDtdSystemId;
    }

    @Override
    public String getDTDInternalSubset() {
        if (this.mCurrToken != 11) {
            return null;
        }
        return this.mTextBuffer.contentsAsString();
    }

    private char[] getDTDInternalSubsetArray() {
        return this.mTextBuffer.contentsAsArray();
    }

    @Override
    public DTDValidationSchema getProcessedDTDSchema() {
        return null;
    }

    @Override
    public long getStartingByteOffset() {
        return -1L;
    }

    @Override
    public long getStartingCharOffset() {
        return this.mTokenInputTotal;
    }

    @Override
    public long getEndingByteOffset() throws XMLStreamException {
        return -1L;
    }

    @Override
    public long getEndingCharOffset() throws XMLStreamException {
        if (this.mTokenState < this.mStTextThreshold) {
            this.finishToken(false);
        }
        return this.mCurrInputProcessed + (long)this.mInputPtr;
    }

    @Override
    public final Location getLocation() {
        return this.getStartLocation();
    }

    @Override
    public final XMLStreamLocation2 getEndLocation() throws XMLStreamException {
        if (this.mTokenState < this.mStTextThreshold) {
            this.finishToken(false);
        }
        return this.getCurrentLocation();
    }

    @Override
    public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return null;
    }

    @Override
    public XMLValidator stopValidatingAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return null;
    }

    @Override
    public XMLValidator stopValidatingAgainst(XMLValidator validator) throws XMLStreamException {
        return null;
    }

    @Override
    public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler h) {
        return null;
    }

    @Override
    public EntityDecl getCurrentEntityDecl() {
        return this.mCurrEntity;
    }

    @Override
    public Object withStartElement(ElemCallback cb, Location loc) {
        if (this.mCurrToken != 1) {
            return null;
        }
        return cb.withStartElement(loc, this.getName(), this.mElementStack.createNonTransientNsContext(loc), this.mAttrCollector.buildAttrOb(), this.mStEmptyElem);
    }

    @Override
    public boolean isNamespaceAware() {
        return this.mCfgNsEnabled;
    }

    @Override
    public InputElementStack getInputElementStack() {
        return this.mElementStack;
    }

    @Override
    public AttributeCollector getAttributeCollector() {
        return this.mAttrCollector;
    }

    public void fireSaxStartElement(ContentHandler h, Attributes attrs) throws SAXException {
        if (h != null) {
            int nsCount = this.mElementStack.getCurrentNsCount();
            for (int i = 0; i < nsCount; ++i) {
                String prefix = this.mElementStack.getLocalNsPrefix(i);
                String uri = this.mElementStack.getLocalNsURI(i);
                h.startPrefixMapping(prefix == null ? "" : prefix, uri);
            }
            String uri = this.mElementStack.getNsURI();
            h.startElement(uri == null ? "" : uri, this.mElementStack.getLocalName(), this.getPrefixedName(), attrs);
        }
    }

    public void fireSaxEndElement(ContentHandler h) throws SAXException {
        if (h != null) {
            String uri = this.mElementStack.getNsURI();
            h.endElement(uri == null ? "" : uri, this.mElementStack.getLocalName(), this.getPrefixedName());
            int nsCount = this.mElementStack.getCurrentNsCount();
            for (int i = 0; i < nsCount; ++i) {
                String prefix = this.mElementStack.getLocalNsPrefix(i);
                h.endPrefixMapping(prefix == null ? "" : prefix);
            }
        }
    }

    public void fireSaxCharacterEvents(ContentHandler h) throws XMLStreamException, SAXException {
        if (h != null) {
            if (this.mPendingException != null) {
                XMLStreamException sex = this.mPendingException;
                this.mPendingException = null;
                throw sex;
            }
            if (this.mTokenState < this.mStTextThreshold) {
                this.finishToken(false);
            }
            this.mTextBuffer.fireSaxCharacterEvents(h);
        }
    }

    public void fireSaxSpaceEvents(ContentHandler h) throws XMLStreamException, SAXException {
        if (h != null) {
            if (this.mTokenState < this.mStTextThreshold) {
                this.finishToken(false);
            }
            this.mTextBuffer.fireSaxSpaceEvents(h);
        }
    }

    public void fireSaxCommentEvent(LexicalHandler h) throws XMLStreamException, SAXException {
        if (h != null) {
            if (this.mTokenState < this.mStTextThreshold) {
                this.finishToken(false);
            }
            this.mTextBuffer.fireSaxCommentEvent(h);
        }
    }

    public void fireSaxPIEvent(ContentHandler h) throws XMLStreamException, SAXException {
        if (h != null) {
            if (this.mTokenState < this.mStTextThreshold) {
                this.finishToken(false);
            }
            h.processingInstruction(this.mCurrName, this.mTextBuffer.contentsAsString());
        }
    }

    protected final boolean hasConfigFlags(int flags) {
        return (this.mConfigFlags & flags) == flags;
    }

    protected String checkKeyword(char c, String expected) throws XMLStreamException {
        int i;
        int ptr = 0;
        int len = expected.length();
        while (expected.charAt(ptr) == c && ++ptr < len) {
            if (this.mInputPtr < this.mInputEnd) {
                c = this.mInputBuffer[this.mInputPtr++];
                continue;
            }
            int ci = this.getNext();
            if (ci < 0) break;
            c = (char)ci;
        }
        if (ptr == len && ((i = this.peekNext()) < 0 || !this.isNameChar((char)i) && i != 58)) {
            return null;
        }
        StringBuilder sb = new StringBuilder(expected.length() + 16);
        sb.append(expected.substring(0, ptr));
        if (ptr < len) {
            sb.append(c);
        }
        while (true) {
            if (this.mInputPtr < this.mInputEnd) {
                c = this.mInputBuffer[this.mInputPtr++];
            } else {
                int ci = this.getNext();
                if (ci < 0) break;
                c = (char)ci;
            }
            if (!this.isNameChar(c)) {
                --this.mInputPtr;
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    protected void checkCData() throws XMLStreamException {
        char c;
        String wrong = this.checkKeyword(this.getNextCharFromCurrent(" in CDATA section"), "CDATA");
        if (wrong != null) {
            this.throwParseError("Unrecognized XML directive '" + wrong + "'; expected 'CDATA'.");
        }
        if ((c = this.getNextCharFromCurrent(" in CDATA section")) != '[') {
            this.throwUnexpectedChar(c, "expected '[' after '<![CDATA'");
        }
    }

    private final void parseAttrValue(char openingQuote, TextBuilder tb) throws XMLStreamException {
        int outPtr;
        char[] outBuf = tb.getCharBuffer();
        int startingOffset = outPtr = tb.getCharSize();
        int maxAttrSize = this.mConfig.getMaxAttributeSize();
        int outLimit = this._outputLimit(outBuf, startingOffset, maxAttrSize);
        WstxInputSource currScope = this.mInput;
        while (true) {
            char c;
            char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(" in attribute value");
            if (c <= '\'') {
                if (c < ' ') {
                    if (c == '\n') {
                        this.markLF();
                    } else if (c == '\r') {
                        if (this.mNormalizeLFs && (c = (char)this.getNextChar(" in attribute value")) != '\n') {
                            --this.mInputPtr;
                        }
                        this.markLF();
                    } else if (c != '\t') {
                        this.throwInvalidSpace(c);
                    }
                    c = ' ';
                } else if (c == openingQuote) {
                    if (this.mInput == currScope) {
                        break;
                    }
                } else if (c == '&') {
                    int ch;
                    if ((this.inputInBuffer() < 3 || (ch = this.resolveSimpleEntity(true)) == 0) && (ch = this.fullyResolveEntity(false)) == 0) continue;
                    if (ch <= 65535) {
                        c = (char)ch;
                    } else {
                        ch -= 65536;
                        if (outPtr >= outLimit) {
                            outBuf = this._checkAttributeLimit(tb, outBuf, outPtr, outPtr - startingOffset, maxAttrSize);
                            outLimit = this._outputLimit(outBuf, startingOffset, maxAttrSize);
                        }
                        outBuf[outPtr++] = (char)((ch >> 10) + 55296);
                        c = (char)((ch & 0x3FF) + 56320);
                    }
                }
            } else if (c == '<') {
                this.throwUnexpectedChar(c, " in attribute value");
            }
            if (outPtr >= outLimit) {
                outBuf = this._checkAttributeLimit(tb, outBuf, outPtr, outPtr - startingOffset, maxAttrSize);
                outLimit = this._outputLimit(outBuf, startingOffset, maxAttrSize);
            }
            outBuf[outPtr++] = c;
        }
        tb.setBufferSize(outPtr);
    }

    private final int _outputLimit(char[] outBuf, int offset, int maxAttrLen) {
        return Math.min(outBuf.length, Math.max(maxAttrLen, offset + maxAttrLen));
    }

    private final char[] _checkAttributeLimit(TextBuilder tb, char[] outBuf, int outPtr, int currAttrSize, int maxAttrSize) throws XMLStreamException {
        this.verifyLimit("Maximum attribute size", maxAttrSize, currAttrSize + 1);
        if (outPtr < outBuf.length) {
            ExceptionUtil.throwInternal(String.format("Expected either currAttrSize (%d) > maxAttrSize (%d) OR outPtr (%d) >= outBuf.length (%d)", currAttrSize, maxAttrSize, outPtr, outBuf.length));
        }
        return tb.bufferFull(1);
    }

    private boolean nextFromProlog(boolean isProlog) throws XMLStreamException {
        char c;
        int i;
        if (this.mTokenState < this.mStTextThreshold) {
            this.mTokenState = 4;
            i = this.skipToken();
        } else {
            this.mTokenInputTotal = this.mCurrInputProcessed + (long)this.mInputPtr;
            this.mTokenInputRow = this.mCurrInputRow;
            this.mTokenInputCol = this.mInputPtr - this.mCurrInputRowStart;
            i = this.getNext();
        }
        if (i <= 32 && i >= 0) {
            if (this.hasConfigFlags(256)) {
                this.mCurrToken = 6;
                if (this.readSpacePrimary((char)i, true)) {
                    this.mTokenState = 4;
                } else if (this.mCfgLazyParsing) {
                    this.mTokenState = 1;
                } else {
                    this.readSpaceSecondary(true);
                    this.mTokenState = 4;
                }
                return false;
            }
            --this.mInputPtr;
            i = this.getNextAfterWS();
            if (i >= 0) {
                this.mTokenInputTotal = this.mCurrInputProcessed + (long)this.mInputPtr - 1L;
                this.mTokenInputRow = this.mCurrInputRow;
                this.mTokenInputCol = this.mInputPtr - this.mCurrInputRowStart - 1;
            }
        }
        if (i < 0) {
            this.handleEOF(isProlog);
            this.mParseState = 4;
            return true;
        }
        if (i != 60) {
            this.throwUnexpectedChar(i, (isProlog ? " in prolog" : " in epilog") + "; expected '<'");
        }
        if ((c = this.getNextChar(isProlog ? " in prolog" : " in epilog")) == '?') {
            this.mCurrToken = this.readPIPrimary();
        } else if (c == '!') {
            this.nextFromPrologBang(isProlog);
        } else if (c == '/') {
            if (isProlog) {
                this.throwParseError("Unexpected character combination '</' in prolog.");
            }
            this.throwParseError("Unexpected character combination '</' in epilog (extra close tag?).");
        } else if (c == ':' || this.isNameStartChar(c)) {
            if (!isProlog) {
                this.mCurrToken = this.handleExtraRoot(c);
                return false;
            }
            this.handleRootElem(c);
            this.mCurrToken = 1;
        } else {
            this.throwUnexpectedChar(c, (isProlog ? " in prolog" : " in epilog") + ", after '<'.");
        }
        if (!this.mCfgLazyParsing && this.mTokenState < this.mStTextThreshold) {
            this.finishToken(false);
        }
        return false;
    }

    protected void handleRootElem(char c) throws XMLStreamException {
        this.mParseState = 1;
        this.initValidation();
        this.handleStartElem(c);
        if (this.mRootLName != null && this.hasConfigFlags(32) && !this.mElementStack.matches(this.mRootPrefix, this.mRootLName)) {
            String actual = this.mRootPrefix == null ? this.mRootLName : this.mRootPrefix + ":" + this.mRootLName;
            this.reportValidationProblem(ErrorConsts.ERR_VLD_WRONG_ROOT, actual, this.mRootLName);
        }
    }

    protected void initValidation() throws XMLStreamException {
    }

    protected int handleEOF(boolean isProlog) throws XMLStreamException {
        this.mSecondaryToken = 8;
        this.mCurrToken = 8;
        this.mTextBuffer.recycle(true);
        if (isProlog) {
            this.throwUnexpectedEOF(" in prolog");
        }
        return this.mCurrToken;
    }

    private int handleExtraRoot(char c) throws XMLStreamException {
        if (!this.mConfig.inputParsingModeDocuments()) {
            this.throwParseError("Illegal to have multiple roots (start tag in epilog?).");
        }
        --this.mInputPtr;
        return this.handleMultiDocStart(1);
    }

    protected int handleMultiDocStart(int nextEvent) {
        this.mParseState = 3;
        this.mTokenState = 4;
        this.mSecondaryToken = nextEvent;
        return 8;
    }

    private int nextFromMultiDocState() throws XMLStreamException {
        if (this.mCurrToken == 8) {
            if (this.mSecondaryToken == 7) {
                this.handleMultiDocXmlDecl();
            } else {
                this.mDocXmlEncoding = null;
                this.mDocXmlVersion = 0;
                this.mDocStandalone = 0;
            }
            return 7;
        }
        if (this.mCurrToken == 7) {
            this.mParseState = 0;
            if (this.mSecondaryToken == 7) {
                this.nextFromProlog(true);
                return this.mCurrToken;
            }
            if (this.mSecondaryToken == 1) {
                this.handleRootElem(this.getNextChar(" in start tag"));
                return 1;
            }
            if (this.mSecondaryToken == 11) {
                this.mStDoctypeFound = true;
                this.startDTD();
                return 11;
            }
        }
        throw new IllegalStateException("Internal error: unexpected state; current event " + this.tokenTypeDesc(this.mCurrToken) + ", sec. state: " + this.tokenTypeDesc(this.mSecondaryToken));
    }

    protected void handleMultiDocXmlDecl() throws XMLStreamException {
        this.mDocStandalone = 0;
        this.mDocXmlEncoding = null;
        char c = this.getNextInCurrAfterWS(" in xml declaration");
        String wrong = this.checkKeyword(c, "version");
        if (wrong != null) {
            this.throwParseError(ErrorConsts.ERR_UNEXP_KEYWORD, wrong, "version");
        }
        c = this.skipEquals("version", " in xml declaration");
        TextBuffer tb = this.mTextBuffer;
        tb.resetInitialized();
        this.parseQuoted("version", c, tb);
        if (tb.equalsString("1.0")) {
            this.mDocXmlVersion = 256;
            this.mXml11 = false;
        } else if (tb.equalsString("1.1")) {
            this.mDocXmlVersion = 272;
            this.mXml11 = true;
        } else {
            this.mDocXmlVersion = 0;
            this.mXml11 = false;
            this.throwParseError("Unexpected xml version '" + tb.toString() + "'; expected '" + "1.0" + "' or '" + "1.1" + "'");
        }
        c = this.getNextInCurrAfterWS(" in xml declaration");
        if (c != '?') {
            if (c == 'e') {
                wrong = this.checkKeyword(c, "encoding");
                if (wrong != null) {
                    this.throwParseError(ErrorConsts.ERR_UNEXP_KEYWORD, wrong, "encoding");
                }
                c = this.skipEquals("encoding", " in xml declaration");
                tb.resetWithEmpty();
                this.parseQuoted("encoding", c, tb);
                this.mDocXmlEncoding = tb.toString();
                c = this.getNextInCurrAfterWS(" in xml declaration");
            } else if (c != 's') {
                this.throwUnexpectedChar(c, " in xml declaration; expected either 'encoding' or 'standalone' pseudo-attribute");
            }
            if (c == 's') {
                wrong = this.checkKeyword(c, "standalone");
                if (wrong != null) {
                    this.throwParseError(ErrorConsts.ERR_UNEXP_KEYWORD, wrong, "standalone");
                }
                c = this.skipEquals("standalone", " in xml declaration");
                tb.resetWithEmpty();
                this.parseQuoted("standalone", c, tb);
                if (tb.equalsString("yes")) {
                    this.mDocStandalone = 1;
                } else if (tb.equalsString("no")) {
                    this.mDocStandalone = 2;
                } else {
                    this.throwParseError("Unexpected xml 'standalone' pseudo-attribute value '" + tb.toString() + "'; expected '" + "yes" + "' or '" + "no" + "'");
                }
                c = this.getNextInCurrAfterWS(" in xml declaration");
            }
        }
        if (c != '?') {
            this.throwUnexpectedChar(c, " in xml declaration; expected '?>' as the end marker");
        }
        if ((c = this.getNextCharFromCurrent(" in xml declaration")) != '>') {
            this.throwUnexpectedChar(c, " in xml declaration; expected '>' to close the declaration");
        }
    }

    protected final char skipEquals(String name, String eofMsg) throws XMLStreamException {
        char c = this.getNextInCurrAfterWS(eofMsg);
        if (c != '=') {
            this.throwUnexpectedChar(c, " in xml declaration; expected '=' to follow pseudo-attribute '" + name + "'");
        }
        return this.getNextInCurrAfterWS(eofMsg);
    }

    protected final void parseQuoted(String name, char quoteChar, TextBuffer tbuf) throws XMLStreamException {
        if (quoteChar != '\"' && quoteChar != '\'') {
            this.throwUnexpectedChar(quoteChar, " in xml declaration; waited ' or \" to start a value for pseudo-attribute '" + name + "'");
        }
        char[] outBuf = tbuf.getCurrentSegment();
        int outPtr = 0;
        while (true) {
            char c;
            char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(" in xml declaration");
            if (c == quoteChar) break;
            if (c < ' ' || c == '<') {
                if (c == '\u0000') {
                    this.throwNullChar();
                } else {
                    this.throwUnexpectedChar(c, " in xml declaration");
                }
            }
            if (outPtr >= outBuf.length) {
                outBuf = tbuf.finishCurrentSegment();
                outPtr = 0;
            }
            outBuf[outPtr++] = c;
        }
        tbuf.setCurrentLength(outPtr);
    }

    private void nextFromPrologBang(boolean isProlog) throws XMLStreamException {
        int i = this.getNext();
        if (i < 0) {
            this.throwUnexpectedEOF(" in prolog");
        }
        if (i == 68) {
            String keyw = this.checkKeyword('D', "DOCTYPE");
            if (keyw != null) {
                this.throwParseError("Unrecognized XML directive '<!" + keyw + "' (misspelled DOCTYPE?).");
            }
            if (!isProlog) {
                if (this.mConfig.inputParsingModeDocuments()) {
                    if (!this.mStDoctypeFound) {
                        this.mCurrToken = this.handleMultiDocStart(11);
                        return;
                    }
                } else {
                    this.throwParseError(ErrorConsts.ERR_DTD_IN_EPILOG);
                }
            }
            if (this.mStDoctypeFound) {
                this.throwParseError(ErrorConsts.ERR_DTD_DUP);
            }
            this.mStDoctypeFound = true;
            this.mCurrToken = 11;
            this.startDTD();
            return;
        }
        if (i == 45) {
            char c = this.getNextChar(isProlog ? " in prolog" : " in epilog");
            if (c != '-') {
                this.throwUnexpectedChar(i, " (malformed comment?)");
            }
            this.mTokenState = 1;
            this.mCurrToken = 5;
            return;
        }
        if (i == 91 && (i = this.peekNext()) == 67) {
            this.throwUnexpectedChar(i, ErrorConsts.ERR_CDATA_IN_EPILOG);
        }
        this.throwUnexpectedChar(i, " after '<!' (malformed comment?)");
    }

    private void startDTD() throws XMLStreamException {
        this.mTextBuffer.resetInitialized();
        char c = this.getNextInCurrAfterWS(" in DOCTYPE declaration");
        if (this.mCfgNsEnabled) {
            String str = this.parseLocalName(c);
            c = this.getNextChar(" in DOCTYPE declaration");
            if (c == ':') {
                this.mRootPrefix = str;
                this.mRootLName = this.parseLocalName(this.getNextChar("; expected an identifier"));
            } else if (c <= ' ' || c == '[' || c == '>') {
                --this.mInputPtr;
                this.mRootPrefix = null;
                this.mRootLName = str;
            } else {
                this.throwUnexpectedChar(c, " in DOCTYPE declaration; expected '[' or white space.");
            }
        } else {
            this.mRootLName = this.parseFullName(c);
            this.mRootPrefix = null;
        }
        c = this.getNextInCurrAfterWS(" in DOCTYPE declaration");
        if (c != '[' && c != '>') {
            String keyw = null;
            if (c == 'P') {
                keyw = this.checkKeyword(this.getNextChar(" in DOCTYPE declaration"), "UBLIC");
                if (keyw != null) {
                    keyw = "P" + keyw;
                } else {
                    if (!this.skipWS(this.getNextChar(" in DOCTYPE declaration"))) {
                        this.throwUnexpectedChar(c, " in DOCTYPE declaration; expected a space between PUBLIC keyword and public id");
                    }
                    if ((c = this.getNextCharFromCurrent(" in DOCTYPE declaration")) != '\"' && c != '\'') {
                        this.throwUnexpectedChar(c, " in DOCTYPE declaration; expected a public identifier.");
                    }
                    this.mDtdPublicId = this.parsePublicId(c, " in DOCTYPE declaration");
                    if (this.mDtdPublicId.length() == 0) {
                        // empty if block
                    }
                    if (!this.skipWS(this.getNextChar(" in DOCTYPE declaration"))) {
                        this.throwUnexpectedChar(c, " in DOCTYPE declaration; expected a space between public and system identifiers");
                    }
                    if ((c = this.getNextCharFromCurrent(" in DOCTYPE declaration")) != '\"' && c != '\'') {
                        this.throwParseError(" in DOCTYPE declaration; expected a system identifier.");
                    }
                    this.mDtdSystemId = this.parseSystemId(c, this.mNormalizeLFs, " in DOCTYPE declaration");
                    if (this.mDtdSystemId.length() == 0) {
                        // empty if block
                    }
                }
            } else if (c == 'S') {
                this.mDtdPublicId = null;
                keyw = this.checkKeyword(this.getNextChar(" in DOCTYPE declaration"), "YSTEM");
                if (keyw != null) {
                    keyw = "S" + keyw;
                } else {
                    c = this.getNextInCurrAfterWS(" in DOCTYPE declaration");
                    if (c != '\"' && c != '\'') {
                        this.throwUnexpectedChar(c, " in DOCTYPE declaration; expected a system identifier.");
                    }
                    this.mDtdSystemId = this.parseSystemId(c, this.mNormalizeLFs, " in DOCTYPE declaration");
                    if (this.mDtdSystemId.length() == 0) {
                        this.mDtdSystemId = null;
                    }
                }
            } else if (!this.isNameStartChar(c)) {
                this.throwUnexpectedChar(c, " in DOCTYPE declaration; expected keywords 'PUBLIC' or 'SYSTEM'.");
            } else {
                --this.mInputPtr;
                keyw = this.checkKeyword(c, "SYSTEM");
            }
            if (keyw != null) {
                this.throwParseError("Unexpected keyword '" + keyw + "'; expected 'PUBLIC' or 'SYSTEM'");
            }
            c = this.getNextInCurrAfterWS(" in DOCTYPE declaration");
        }
        if (c != '[' && c != '>') {
            this.throwUnexpectedChar(c, " in DOCTYPE declaration; expected closing '>'.");
        }
        --this.mInputPtr;
        this.mTokenState = 1;
    }

    protected void finishDTD(boolean copyContents) throws XMLStreamException {
        char c = this.getNextChar(" in DOCTYPE declaration");
        if (c == '[') {
            if (copyContents) {
                ((BranchingReaderSource)this.mInput).startBranch(this.mTextBuffer, this.mInputPtr, this.mNormalizeLFs);
            }
            try {
                MinimalDTDReader.skipInternalSubset(this, this.mInput, this.mConfig);
            }
            finally {
                if (copyContents) {
                    ((BranchingReaderSource)this.mInput).endBranch(this.mInputPtr - 1);
                }
            }
            c = this.getNextCharAfterWS(" in internal DTD subset");
        }
        if (c != '>') {
            this.throwUnexpectedChar(c, "; expected '>' to finish DOCTYPE declaration.");
        }
    }

    private final int nextFromTree() throws XMLStreamException {
        char c;
        int i;
        if (this.mTokenState < this.mStTextThreshold) {
            if (this.mVldContent == 3 && (this.mCurrToken == 4 || this.mCurrToken == 12)) {
                this.throwParseError("Internal error: skipping validatable text");
            }
            i = this.skipToken();
        } else {
            if (this.mCurrToken == 1) {
                if (this.mStEmptyElem) {
                    int vld;
                    this.mStEmptyElem = false;
                    this.mVldContent = vld = this.mElementStack.validateEndElement();
                    this.mValidateText = vld == 3;
                    return 2;
                }
            } else if (this.mCurrToken == 2) {
                if (!this.mElementStack.pop() && !this.mConfig.inputParsingModeFragment()) {
                    return this.closeContentTree();
                }
            } else if (this.mCurrToken == 12 && this.mTokenState <= 2) {
                this.mTokenInputTotal = this.mCurrInputProcessed + (long)this.mInputPtr;
                this.mTokenInputRow = this.mCurrInputRow;
                this.mTokenInputCol = this.mInputPtr - this.mCurrInputRowStart;
                char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(" in CDATA section");
                if (this.readCDataPrimary(c)) {
                    if (this.mTextBuffer.size() > 0) {
                        return 12;
                    }
                } else if (this.mTextBuffer.size() == 0 && this.readCDataSecondary(this.mCfgLazyParsing ? 1 : this.mShortestTextSegment)) {
                    if (this.mTextBuffer.size() > 0) {
                        this.mTokenState = 3;
                        return 12;
                    }
                } else {
                    this.mTokenState = 2;
                    return 12;
                }
            }
            this.mTokenInputTotal = this.mCurrInputProcessed + (long)this.mInputPtr;
            this.mTokenInputRow = this.mCurrInputRow;
            this.mTokenInputCol = this.mInputPtr - this.mCurrInputRowStart;
            i = this.getNext();
        }
        if (i < 0) {
            if (!this.mElementStack.isEmpty()) {
                this.throwUnexpectedEOF();
            }
            return this.handleEOF(false);
        }
        while (i == 38) {
            int ch;
            this.mWsStatus = 0;
            if (this.mVldContent == 0) {
                this.reportInvalidContent(9);
            }
            int n = ch = this.mCfgReplaceEntities ? this.fullyResolveEntity(true) : this.resolveCharOnlyEntity(true);
            if (ch != 0) {
                if (this.mVldContent <= 1 && ch > 32) {
                    this.reportInvalidContent(4);
                }
                TextBuffer tb = this.mTextBuffer;
                tb.resetInitialized();
                if (ch <= 65535) {
                    tb.append((char)ch);
                } else {
                    tb.append((char)(((ch -= 65536) >> 10) + 55296));
                    tb.append((char)((ch & 0x3FF) + 56320));
                }
                this.mTokenState = 1;
                return 4;
            }
            if (!this.mCfgReplaceEntities || this.mCfgTreatCharRefsAsEntities) {
                if (!this.mCfgTreatCharRefsAsEntities) {
                    EntityDecl ed;
                    this.mCurrEntity = ed = this.resolveNonCharEntity();
                }
                this.mTokenState = 4;
                return 9;
            }
            i = this.getNextChar(" in main document content");
        }
        if (i == 60) {
            c = this.getNextChar(" in start tag");
            if (c == '?') {
                if (this.mVldContent == 0) {
                    this.reportInvalidContent(3);
                }
                return this.readPIPrimary();
            }
            if (c == '!') {
                int type = this.nextFromTreeCommentOrCData();
                if (this.mVldContent == 0) {
                    this.reportInvalidContent(type);
                }
                return type;
            }
            if (c == '/') {
                this.readEndElem();
                return 2;
            }
            if (c == ':' || this.isNameStartChar(c)) {
                this.handleStartElem(c);
                return 1;
            }
            if (c == '[') {
                this.throwUnexpectedChar(c, " in content after '<' (malformed <![CDATA[]] directive?)");
            }
            this.throwUnexpectedChar(c, " in content after '<' (malformed start element?).");
        }
        if (this.mVldContent <= 2) {
            if (this.mVldContent == 0 && this.mElementStack.reallyValidating()) {
                this.reportInvalidContent(4);
            }
            if (i <= 32) {
                this.mTokenState = this.readSpacePrimary((char)i, false) ? 4 : 1;
                return 6;
            }
            if (this.mElementStack.reallyValidating()) {
                this.reportInvalidContent(4);
            }
        }
        this.mTokenState = this.readTextPrimary((char)i) ? 3 : (!this.mCfgCoalesceText && this.mTextBuffer.size() >= this.mShortestTextSegment ? 2 : 1);
        return 4;
    }

    private int closeContentTree() throws XMLStreamException {
        this.mParseState = 2;
        if (this.nextFromProlog(false)) {
            this.mSecondaryToken = 0;
        }
        if (this.mSymbols.isDirty()) {
            this.mOwner.updateSymbolTable(this.mSymbols);
        }
        this.mTextBuffer.recycle(false);
        return this.mCurrToken;
    }

    private final void handleStartElem(char c) throws XMLStreamException {
        int vld;
        boolean empty;
        this.mTokenState = 4;
        if (this.mCfgNsEnabled) {
            String str = this.parseLocalName(c);
            char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent("; expected an identifier");
            if (c == ':') {
                c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent("; expected an identifier");
                this.mElementStack.push(str, this.parseLocalName(c));
                c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in start tag");
            } else {
                this.mElementStack.push(null, str);
            }
            empty = c == '>' ? false : this.handleNsAttrs(c);
        } else {
            this.mElementStack.push(null, this.parseFullName(c));
            c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in start tag");
            boolean bl = empty = c == '>' ? false : this.handleNonNsAttrs(c);
        }
        if (!empty) {
            ++this.mCurrDepth;
        }
        this.mStEmptyElem = empty;
        this.mVldContent = vld = this.mElementStack.resolveAndValidateElement();
        this.mValidateText = vld == 3;
    }

    private final boolean handleNsAttrs(char c) throws XMLStreamException {
        AttributeCollector ac = this.mAttrCollector;
        while (true) {
            TextBuilder tb;
            String localName;
            String prefix;
            if (c <= ' ') {
                c = this.getNextInCurrAfterWS(" in start tag", c);
            } else if (c != '/' && c != '>') {
                this.throwUnexpectedChar(c, " expected space, or '>' or \"/>\"");
            }
            if (c == '/') {
                c = this.getNextCharFromCurrent(" in start tag");
                if (c != '>') {
                    this.throwUnexpectedChar(c, " expected '>'");
                }
                return true;
            }
            if (c == '>') {
                return false;
            }
            if (c == '<') {
                this.throwParseError("Unexpected '<' character in element (missing closing '>'?)");
            }
            String str = this.parseLocalName(c);
            char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent("; expected an identifier");
            if (c == ':') {
                prefix = str;
                c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent("; expected an identifier");
                localName = this.parseLocalName(c);
            } else {
                --this.mInputPtr;
                prefix = null;
                localName = str;
            }
            char c3 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in start tag");
            if (c <= ' ') {
                c = this.getNextInCurrAfterWS(" in start tag", c);
            }
            if (c != '=') {
                this.throwUnexpectedChar(c, " expected '='");
            }
            char c4 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in start tag");
            if (c <= ' ') {
                c = this.getNextInCurrAfterWS(" in start tag", c);
            }
            if (c != '\"' && c != '\'') {
                this.throwUnexpectedChar(c, " in start tag Expected a quote");
            }
            int startLen = -1;
            if (prefix == sPrefixXmlns) {
                tb = ac.getNsBuilder(localName);
                if (null == tb) {
                    this.throwParseError("Duplicate declaration for namespace prefix '" + localName + "'.");
                    return false;
                }
                startLen = tb.getCharSize();
            } else if (localName == sPrefixXmlns && prefix == null) {
                tb = ac.getDefaultNsBuilder();
                if (null == tb) {
                    this.throwParseError("Duplicate default namespace declaration.");
                }
            } else {
                tb = ac.getAttrBuilder(prefix, localName);
            }
            this.parseAttrValue(c, tb);
            if (!this.mXml11 && startLen >= 0 && tb.getCharSize() == startLen) {
                this.throwParseError(ErrorConsts.ERR_NS_EMPTY);
            }
            c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in start tag");
        }
    }

    private final boolean handleNonNsAttrs(char c) throws XMLStreamException {
        AttributeCollector ac = this.mAttrCollector;
        while (true) {
            if (c <= ' ') {
                c = this.getNextInCurrAfterWS(" in start tag", c);
            } else if (c != '/' && c != '>') {
                this.throwUnexpectedChar(c, " expected space, or '>' or \"/>\"");
            }
            if (c == '/') {
                c = this.getNextCharFromCurrent(" in start tag");
                if (c != '>') {
                    this.throwUnexpectedChar(c, " expected '>'");
                }
                return true;
            }
            if (c == '>') {
                return false;
            }
            if (c == '<') {
                this.throwParseError("Unexpected '<' character in element (missing closing '>'?)");
            }
            String name = this.parseFullName(c);
            TextBuilder tb = ac.getAttrBuilder(null, name);
            char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in start tag");
            if (c <= ' ') {
                c = this.getNextInCurrAfterWS(" in start tag", c);
            }
            if (c != '=') {
                this.throwUnexpectedChar(c, " expected '='");
            }
            char c3 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in start tag");
            if (c <= ' ') {
                c = this.getNextInCurrAfterWS(" in start tag", c);
            }
            if (c != '\"' && c != '\'') {
                this.throwUnexpectedChar(c, " in start tag Expected a quote");
            }
            this.parseAttrValue(c, tb);
            c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in start tag");
        }
    }

    protected final void readEndElem() throws XMLStreamException {
        int vld;
        int i;
        int len;
        char c;
        this.mTokenState = 4;
        if (this.mElementStack.isEmpty()) {
            this.reportExtraEndElem();
            return;
        }
        char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in end tag");
        if (!this.isNameStartChar(c) && c != ':') {
            if (c <= ' ') {
                this.throwUnexpectedChar(c, "; missing element name?");
            }
            this.throwUnexpectedChar(c, "; expected an element name.");
        }
        String expPrefix = this.mElementStack.getPrefix();
        String expLocalName = this.mElementStack.getLocalName();
        if (expPrefix != null && expPrefix.length() > 0) {
            len = expPrefix.length();
            i = 0;
            while (true) {
                if (c != expPrefix.charAt(i)) {
                    this.reportWrongEndPrefix(expPrefix, expLocalName, i);
                    return;
                }
                if (++i >= len) break;
                c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in end tag");
            }
            char c3 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in end tag");
            if (c != ':') {
                this.reportWrongEndPrefix(expPrefix, expLocalName, i);
                return;
            }
            c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in end tag");
        }
        len = expLocalName.length();
        i = 0;
        while (true) {
            if (c != expLocalName.charAt(i)) {
                this.reportWrongEndElem(expPrefix, expLocalName, i);
                return;
            }
            if (++i >= len) break;
            c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in end tag");
        }
        char c4 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in end tag");
        if (c <= ' ') {
            c = this.getNextInCurrAfterWS(" in end tag", c);
        } else if (c != '>' && (c == ':' || this.isNameChar(c))) {
            this.reportWrongEndElem(expPrefix, expLocalName, len);
        }
        if (c != '>') {
            this.throwUnexpectedChar(c, " in end tag Expected '>'.");
        }
        this.mVldContent = vld = this.mElementStack.validateEndElement();
        boolean bl = this.mValidateText = vld == 3;
        if (this.mCurrDepth == this.mInputTopDepth) {
            this.handleGreedyEntityProblem(this.mInput);
        }
        --this.mCurrDepth;
    }

    private void reportExtraEndElem() throws XMLStreamException {
        String name = this.parseFNameForError();
        this.throwParseError("Unbalanced close tag </" + name + ">; no open start tag.");
    }

    private void reportWrongEndPrefix(String prefix, String localName, int done) throws XMLStreamException {
        --this.mInputPtr;
        String fullName = prefix + ":" + localName;
        String rest = this.parseFNameForError();
        String actName = fullName.substring(0, done) + rest;
        this.throwParseError("Unexpected close tag </" + actName + ">; expected </" + fullName + ">.");
    }

    private void reportWrongEndElem(String prefix, String localName, int done) throws XMLStreamException {
        String fullName;
        --this.mInputPtr;
        if (prefix != null && prefix.length() > 0) {
            fullName = prefix + ":" + localName;
            done += 1 + prefix.length();
        } else {
            fullName = localName;
        }
        String rest = this.parseFNameForError();
        String actName = fullName.substring(0, done) + rest;
        this.throwParseError("Unexpected close tag </" + actName + ">; expected </" + fullName + ">.");
    }

    private int nextFromTreeCommentOrCData() throws XMLStreamException {
        char c = this.getNextCharFromCurrent(" in main document content");
        if (c == '[') {
            this.checkCData();
            c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in CDATA section");
            this.readCDataPrimary(c);
            return 12;
        }
        if (c == '-' && this.getNextCharFromCurrent(" in main document content") == '-') {
            this.mTokenState = 1;
            return 5;
        }
        this.throwParseError("Unrecognized XML directive; expected CDATA or comment ('<![CDATA[' or '<!--').");
        return 0;
    }

    private int skipToken() throws XMLStreamException {
        int result;
        block0 : switch (this.mCurrToken) {
            case 12: {
                if (this.mTokenState <= 2) {
                    this.skipCommentOrCData(" in CDATA section", ']', false);
                }
                result = this.getNext();
                if (!this.mCfgCoalesceText) break;
                result = this.skipCoalescedText(result);
                break;
            }
            case 5: {
                this.skipCommentOrCData(" in comment", '-', true);
                result = 0;
                break;
            }
            case 4: {
                result = this.skipTokenText(this.getNext());
                if (!this.mCfgCoalesceText) break;
                result = this.skipCoalescedText(result);
                break;
            }
            case 11: {
                this.finishDTD(false);
                result = 0;
                break;
            }
            case 3: {
                while (true) {
                    char c;
                    char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in processing instruction");
                    if (c == '?') {
                        while ((c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in processing instruction")) == '?') {
                        }
                        if (c == '>') {
                            result = 0;
                            break block0;
                        }
                    }
                    if (c >= ' ') continue;
                    if (c == '\n' || c == '\r') {
                        this.skipCRLF(c);
                        continue;
                    }
                    if (c == '\t') continue;
                    this.throwInvalidSpace(c);
                }
            }
            case 6: {
                while (true) {
                    if (this.mInputPtr < this.mInputEnd) {
                        int c;
                        if ((c = this.mInputBuffer[this.mInputPtr++]) > 32) {
                            result = c;
                            break block0;
                        }
                        if (c == 10 || c == 13) {
                            this.skipCRLF((char)c);
                            continue;
                        }
                        if (c == 32 || c == 9) continue;
                        this.throwInvalidSpace(c);
                        continue;
                    }
                    if (!this.loadMore()) break;
                }
                result = -1;
                break;
            }
            case 7: 
            case 8: 
            case 9: 
            case 14: 
            case 15: {
                throw new IllegalStateException("skipToken() called when current token is " + this.tokenTypeDesc(this.mCurrToken));
            }
            default: {
                throw new IllegalStateException("Internal error: unexpected token " + this.tokenTypeDesc(this.mCurrToken));
            }
        }
        if (result < 1) {
            this.mTokenInputRow = this.mCurrInputRow;
            this.mTokenInputTotal = this.mCurrInputProcessed + (long)this.mInputPtr;
            this.mTokenInputCol = this.mInputPtr - this.mCurrInputRowStart;
            return result < 0 ? result : this.getNext();
        }
        this.mTokenInputRow = this.mCurrInputRow;
        this.mTokenInputTotal = this.mCurrInputProcessed + (long)this.mInputPtr - 1L;
        this.mTokenInputCol = this.mInputPtr - this.mCurrInputRowStart - 1;
        return result;
    }

    private void skipCommentOrCData(String errorMsg, char endChar, boolean preventDoubles) throws XMLStreamException {
        int count = 0;
        while (true) {
            char c;
            block16: {
                block15: {
                    block14: {
                        if (this.mInputPtr >= this.mInputEnd) {
                            this.verifyLimit("Text size", this.mConfig.getMaxTextLength(), count);
                            c = this.getNextCharFromCurrent(errorMsg);
                        } else {
                            c = this.mInputBuffer[this.mInputPtr++];
                        }
                        if (c >= ' ') break block14;
                        if (c == '\n' || c == '\r') {
                            this.skipCRLF(c);
                        } else if (c != '\t') {
                            this.throwInvalidSpace(c);
                        }
                        break block15;
                    }
                    if (c == endChar) break block16;
                }
                ++count;
                continue;
            }
            c = this.getNextChar(errorMsg);
            if (c == endChar) {
                c = this.getNextChar(errorMsg);
                if (c == '>') break;
                if (preventDoubles) {
                    this.throwParseError("String '--' not allowed in comment (missing '>'?)");
                }
                while (c == endChar) {
                    c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(errorMsg);
                }
                if (c == '>') break;
            }
            if (c >= ' ') continue;
            if (c == '\n' || c == '\r') {
                this.skipCRLF(c);
                continue;
            }
            if (c == '\t') continue;
            this.throwInvalidSpace(c);
        }
    }

    private int skipCoalescedText(int i) throws XMLStreamException {
        while (true) {
            if (i == 60) {
                if (!this.ensureInput(3)) {
                    return i;
                }
                if (this.mInputBuffer[this.mInputPtr] != '!' || this.mInputBuffer[this.mInputPtr + 1] != '[') {
                    return i;
                }
                this.mInputPtr += 2;
                this.checkCData();
                this.skipCommentOrCData(" in CDATA section", ']', false);
                i = this.getNext();
                continue;
            }
            if (i < 0) {
                return i;
            }
            if ((i = this.skipTokenText(i)) == 38 || i < 0) break;
        }
        return i;
    }

    private int skipTokenText(int i) throws XMLStreamException {
        int count = 0;
        block0: while (i != 60) {
            if (i == 38) {
                if (this.mCfgReplaceEntities) {
                    if (this.mInputEnd - this.mInputPtr < 3 || this.resolveSimpleEntity(true) == 0) {
                        i = this.fullyResolveEntity(true);
                    }
                } else if (this.resolveCharOnlyEntity(true) == 0) {
                    return i;
                }
            } else if (i < 32) {
                if (i == 13 || i == 10) {
                    this.skipCRLF((char)i);
                } else {
                    if (i < 0) {
                        return i;
                    }
                    if (i != 9) {
                        this.throwInvalidSpace(i);
                    }
                }
            }
            this.verifyLimit("Text size", this.mConfig.getMaxTextLength(), ++count);
            while (this.mInputPtr < this.mInputEnd) {
                int c;
                if ((c = this.mInputBuffer[this.mInputPtr++]) >= 63) continue;
                i = c;
                continue block0;
            }
            i = this.getNext();
        }
        return i;
    }

    protected void ensureFinishToken() throws XMLStreamException {
        if (this.mTokenState < this.mStTextThreshold) {
            this.finishToken(false);
        }
    }

    protected void safeEnsureFinishToken() {
        if (this.mTokenState < this.mStTextThreshold) {
            this.safeFinishToken();
        }
    }

    protected void safeFinishToken() {
        try {
            boolean deferErrors = this.mCurrToken == 4;
            this.finishToken(deferErrors);
        }
        catch (XMLStreamException strex) {
            this.throwLazyError(strex);
        }
    }

    protected void finishToken(boolean deferErrors) throws XMLStreamException {
        switch (this.mCurrToken) {
            case 12: {
                if (this.mCfgCoalesceText) {
                    this.readCoalescedText(this.mCurrToken, deferErrors);
                } else {
                    this.mTokenState = this.readCDataSecondary(Integer.MAX_VALUE) ? 3 : 2;
                }
                return;
            }
            case 4: {
                if (this.mCfgCoalesceText) {
                    if (this.mTokenState == 3 && this.mInputPtr + 1 < this.mInputEnd && this.mInputBuffer[this.mInputPtr + 1] != '!') {
                        this.mTokenState = 4;
                        return;
                    }
                    this.readCoalescedText(this.mCurrToken, deferErrors);
                } else {
                    this.mTokenState = this.readTextSecondary(this.mShortestTextSegment, deferErrors) ? 3 : 2;
                }
                return;
            }
            case 6: {
                boolean prolog = this.mParseState != 1;
                this.readSpaceSecondary(prolog);
                this.mTokenState = 4;
                return;
            }
            case 5: {
                this.readComment();
                this.mTokenState = 4;
                return;
            }
            case 11: {
                try {
                    this.finishDTD(true);
                }
                finally {
                    this.mTokenState = 4;
                }
                return;
            }
            case 3: {
                this.readPI();
                this.mTokenState = 4;
                return;
            }
            case 1: 
            case 2: 
            case 7: 
            case 8: 
            case 9: 
            case 14: 
            case 15: {
                throw new IllegalStateException("finishToken() called when current token is " + this.tokenTypeDesc(this.mCurrToken));
            }
        }
        throw new IllegalStateException("Internal error: unexpected token " + this.tokenTypeDesc(this.mCurrToken));
    }

    private void readComment() throws XMLStreamException {
        int ptr;
        char[] inputBuf = this.mInputBuffer;
        int inputLen = this.mInputEnd;
        int start = ptr = this.mInputPtr;
        while (ptr < inputLen) {
            char c;
            if ((c = inputBuf[ptr++]) > '-') continue;
            if (c < ' ') {
                if (c == '\n') {
                    this.markLF(ptr);
                    continue;
                }
                if (c == '\r') {
                    if (!this.mNormalizeLFs && ptr < inputLen) {
                        if (inputBuf[ptr] == '\n') {
                            ++ptr;
                        }
                        this.markLF(ptr);
                        continue;
                    }
                    --ptr;
                    break;
                }
                if (c == '\t') continue;
                this.throwInvalidSpace(c);
                continue;
            }
            if (c != '-') continue;
            if (ptr + 1 >= inputLen) {
                --ptr;
                break;
            }
            if (inputBuf[ptr] != '-') continue;
            c = inputBuf[ptr + 1];
            if (c != '>') {
                this.throwParseError("String '--' not allowed in comment (missing '>'?)");
            }
            this.mTextBuffer.resetWithShared(inputBuf, start, ptr - start - 1);
            this.mInputPtr = ptr + 2;
            return;
        }
        this.mInputPtr = ptr;
        this.mTextBuffer.resetWithCopy(inputBuf, start, ptr - start);
        this.readComment2(this.mTextBuffer);
    }

    private void readComment2(TextBuffer tb) throws XMLStreamException {
        char[] outBuf = tb.getCurrentSegment();
        int outPtr = tb.getCurrentSegmentSize();
        int outLen = outBuf.length;
        while (true) {
            int c;
            int n = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in comment");
            if (c < 32) {
                if (c == 10) {
                    this.markLF();
                } else if (c == 13) {
                    if (this.skipCRLF((char)c)) {
                        if (!this.mNormalizeLFs) {
                            if (outPtr >= outLen) {
                                outBuf = this.mTextBuffer.finishCurrentSegment();
                                outLen = outBuf.length;
                                outPtr = 0;
                            }
                            outBuf[outPtr++] = c;
                        }
                        c = 10;
                    } else if (this.mNormalizeLFs) {
                        c = 10;
                    }
                } else if (c != 9) {
                    this.throwInvalidSpace(c);
                }
            } else if (c == 45) {
                c = this.getNextCharFromCurrent(" in comment");
                if (c == 45) {
                    c = this.getNextCharFromCurrent(" in comment");
                    if (c == 62) break;
                    this.throwParseError(ErrorConsts.ERR_HYPHENS_IN_COMMENT);
                    break;
                }
                c = 45;
                --this.mInputPtr;
            }
            if (outPtr >= outLen) {
                outBuf = this.mTextBuffer.finishCurrentSegment();
                outLen = outBuf.length;
                outPtr = 0;
                this.verifyLimit("Text size", this.mConfig.getMaxTextLength(), this.mTextBuffer.size());
            }
            outBuf[outPtr++] = c;
        }
        this.mTextBuffer.setCurrentLength(outPtr);
    }

    private final int readPIPrimary() throws XMLStreamException {
        char c;
        String target;
        this.mCurrName = target = this.parseFullName();
        if (target.length() == 0) {
            this.throwParseError(ErrorConsts.ERR_WF_PI_MISSING_TARGET);
        }
        if (target.equalsIgnoreCase("xml")) {
            char c2;
            if (!this.mConfig.inputParsingModeDocuments()) {
                this.throwParseError(ErrorConsts.ERR_WF_PI_XML_TARGET, target, null);
            }
            if (!BasicStreamReader.isSpaceChar(c2 = this.getNextCharFromCurrent(" in xml declaration"))) {
                this.throwUnexpectedChar(c2, "expected a space in xml declaration after 'xml'");
            }
            return this.handleMultiDocStart(7);
        }
        char c3 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in processing instruction");
        if (BasicStreamReader.isSpaceChar(c)) {
            this.mTokenState = 1;
            this.skipWS(c);
        } else {
            this.mTokenState = 4;
            this.mTextBuffer.resetWithEmpty();
            if (c != '?' || this.getNextCharFromCurrent(" in processing instruction") != '>') {
                this.throwUnexpectedChar(c, ErrorConsts.ERR_WF_PI_XML_MISSING_SPACE);
            }
        }
        return 3;
    }

    private void readPI() throws XMLStreamException {
        int ptr;
        int start = ptr = this.mInputPtr;
        char[] inputBuf = this.mInputBuffer;
        int inputLen = this.mInputEnd;
        block0: while (ptr < inputLen) {
            char c;
            if ((c = inputBuf[ptr++]) < ' ') {
                if (c == '\n') {
                    this.markLF(ptr);
                    continue;
                }
                if (c == '\r') {
                    if (ptr < inputLen && !this.mNormalizeLFs) {
                        if (inputBuf[ptr] == '\n') {
                            ++ptr;
                        }
                        this.markLF(ptr);
                        continue;
                    }
                    --ptr;
                    break;
                }
                if (c == '\t') continue;
                this.throwInvalidSpace(c);
                continue;
            }
            if (c != '?') continue;
            do {
                if (ptr >= inputLen) {
                    --ptr;
                    break block0;
                }
                if ((c = inputBuf[ptr++]) != '>') continue;
                this.mInputPtr = ptr;
                this.mTextBuffer.resetWithShared(inputBuf, start, ptr - start - 2);
                return;
            } while (c == '?');
            --ptr;
        }
        this.mInputPtr = ptr;
        this.mTextBuffer.resetWithCopy(inputBuf, start, ptr - start);
        this.readPI2(this.mTextBuffer);
    }

    private void readPI2(TextBuffer tb) throws XMLStreamException {
        char[] inputBuf = this.mInputBuffer;
        int inputLen = this.mInputEnd;
        int inputPtr = this.mInputPtr;
        char[] outBuf = tb.getCurrentSegment();
        int outPtr = tb.getCurrentSegmentSize();
        block0: while (true) {
            int c;
            if (inputPtr >= inputLen) {
                this.loadMoreFromCurrent(" in processing instruction");
                inputBuf = this.mInputBuffer;
                inputPtr = this.mInputPtr;
                inputLen = this.mInputEnd;
            }
            if ((c = inputBuf[inputPtr++]) < 32) {
                if (c == 10) {
                    this.markLF(inputPtr);
                } else if (c == 13) {
                    this.mInputPtr = inputPtr;
                    if (this.skipCRLF((char)c)) {
                        if (!this.mNormalizeLFs) {
                            if (outPtr >= outBuf.length) {
                                outBuf = this.mTextBuffer.finishCurrentSegment();
                                outPtr = 0;
                            }
                            outBuf[outPtr++] = c;
                        }
                        c = 10;
                    } else if (this.mNormalizeLFs) {
                        c = 10;
                    }
                    inputPtr = this.mInputPtr;
                    inputBuf = this.mInputBuffer;
                    inputLen = this.mInputEnd;
                } else if (c != 9) {
                    this.throwInvalidSpace(c);
                }
            } else if (c == 63) {
                this.mInputPtr = inputPtr;
                while (true) {
                    int n = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in processing instruction");
                    if (c == 62) break block0;
                    if (c != 63) break;
                    if (outPtr >= outBuf.length) {
                        outBuf = tb.finishCurrentSegment();
                        outPtr = 0;
                    }
                    outBuf[outPtr++] = c;
                }
                inputPtr = --this.mInputPtr;
                inputBuf = this.mInputBuffer;
                inputLen = this.mInputEnd;
                c = 63;
            }
            if (outPtr >= outBuf.length) {
                outBuf = tb.finishCurrentSegment();
                outPtr = 0;
            }
            outBuf[outPtr++] = c;
        }
        tb.setCurrentLength(outPtr);
    }

    protected void readCoalescedText(int currType, boolean deferErrors) throws XMLStreamException {
        boolean wasCData;
        if (currType == 4 || currType == 6) {
            this.readTextSecondary(Integer.MAX_VALUE, deferErrors);
            wasCData = false;
        } else if (currType == 12) {
            if (this.mTokenState <= 2) {
                this.readCDataSecondary(Integer.MAX_VALUE);
            }
            wasCData = true;
        } else {
            throw new IllegalStateException("Internal error: unexpected token " + this.tokenTypeDesc(this.mCurrToken) + "; expected CHARACTERS, CDATA or SPACE.");
        }
        while (!deferErrors || this.mPendingException == null) {
            char c;
            if (this.mInputPtr >= this.mInputEnd) {
                this.mTextBuffer.ensureNotShared();
                if (!this.loadMore()) break;
            }
            if ((c = this.mInputBuffer[this.mInputPtr]) == '<') {
                if (this.mInputEnd - this.mInputPtr < 9) {
                    this.mTextBuffer.ensureNotShared();
                    if (!this.ensureInput(3)) break;
                }
                if (this.mInputBuffer[this.mInputPtr + 1] != '!' || this.mInputBuffer[this.mInputPtr + 2] != '[') break;
                this.mInputPtr += 3;
                this.checkCData();
                this.readCDataSecondary(Integer.MAX_VALUE);
                wasCData = true;
                continue;
            }
            if (c == '&' && !wasCData) break;
            this.readTextSecondary(Integer.MAX_VALUE, deferErrors);
            wasCData = false;
        }
        this.mTokenState = 4;
    }

    private final boolean readCDataPrimary(char c) throws XMLStreamException {
        this.mWsStatus = c <= ' ' ? 0 : 2;
        int ptr = this.mInputPtr;
        int inputLen = this.mInputEnd;
        char[] inputBuf = this.mInputBuffer;
        int start = ptr - 1;
        while (true) {
            block18: {
                if (c < ' ') {
                    if (c == '\n') {
                        this.markLF(ptr);
                    } else if (c == '\r') {
                        if (ptr >= inputLen) {
                            --ptr;
                            break;
                        }
                        if (this.mNormalizeLFs) {
                            if (inputBuf[ptr] == '\n') {
                                --ptr;
                                break;
                            }
                            inputBuf[ptr - 1] = 10;
                        } else if (inputBuf[ptr] == '\n') {
                            ++ptr;
                        }
                        this.markLF(ptr);
                    } else if (c != '\t') {
                        this.throwInvalidSpace(c);
                    }
                } else if (c == ']') {
                    if (ptr + 1 >= inputLen) {
                        --ptr;
                        break;
                    }
                    if (inputBuf[ptr] == ']') {
                        ++ptr;
                        do {
                            if (ptr >= inputLen) {
                                ptr -= 2;
                                break block18;
                            }
                            if ((c = inputBuf[ptr++]) != '>') continue;
                            this.mInputPtr = ptr;
                            this.mTextBuffer.resetWithShared(inputBuf, start, ptr -= start + 3);
                            this.mTokenState = 3;
                            return true;
                        } while (c == ']');
                        --ptr;
                    }
                }
            }
            if (ptr >= inputLen) break;
            c = inputBuf[ptr++];
        }
        this.mInputPtr = ptr;
        int len = ptr - start;
        this.mTextBuffer.resetWithShared(inputBuf, start, len);
        this.mTokenState = this.mCfgCoalesceText || this.mTextBuffer.size() < this.mShortestTextSegment ? 1 : 2;
        return false;
    }

    protected boolean readCDataSecondary(int shortestSegment) throws XMLStreamException {
        char[] inputBuf = this.mInputBuffer;
        int inputLen = this.mInputEnd;
        int inputPtr = this.mInputPtr;
        char[] outBuf = this.mTextBuffer.getCurrentSegment();
        int outPtr = this.mTextBuffer.getCurrentSegmentSize();
        while (true) {
            int c;
            block13: {
                block11: {
                    block14: {
                        block12: {
                            if (inputPtr >= inputLen) {
                                this.loadMore(" in CDATA section");
                                inputBuf = this.mInputBuffer;
                                inputPtr = this.mInputPtr;
                                inputLen = this.mInputEnd;
                            }
                            if ((c = inputBuf[inputPtr++]) >= 32) break block11;
                            if (c != 10) break block12;
                            this.markLF(inputPtr);
                            break block13;
                        }
                        if (c != 13) break block14;
                        this.mInputPtr = inputPtr;
                        if (this.skipCRLF((char)c)) {
                            if (!this.mNormalizeLFs) {
                                outBuf[outPtr++] = c;
                                if (outPtr >= outBuf.length) {
                                    outBuf = this.mTextBuffer.finishCurrentSegment();
                                    outPtr = 0;
                                }
                            }
                            c = 10;
                        } else if (this.mNormalizeLFs) {
                            c = 10;
                        }
                        inputPtr = this.mInputPtr;
                        inputBuf = this.mInputBuffer;
                        inputLen = this.mInputEnd;
                        break block13;
                    }
                    if (c == 9) break block13;
                    this.throwInvalidSpace(c);
                    break block13;
                }
                if (c == 93) {
                    this.mInputPtr = inputPtr;
                    if (this.checkCDataEnd(outBuf, outPtr)) {
                        return true;
                    }
                    inputPtr = this.mInputPtr;
                    inputBuf = this.mInputBuffer;
                    inputLen = this.mInputEnd;
                    outBuf = this.mTextBuffer.getCurrentSegment();
                    outPtr = this.mTextBuffer.getCurrentSegmentSize();
                    continue;
                }
            }
            outBuf[outPtr++] = c;
            if (outPtr < outBuf.length) continue;
            TextBuffer tb = this.mTextBuffer;
            if (!this.mCfgCoalesceText) {
                tb.setCurrentLength(outBuf.length);
                if (tb.size() >= shortestSegment) {
                    this.mInputPtr = inputPtr;
                    return false;
                }
            }
            outBuf = tb.finishCurrentSegment();
            outPtr = 0;
            this.verifyLimit("Text size", this.mConfig.getMaxTextLength(), this.mTextBuffer.size());
        }
    }

    private boolean checkCDataEnd(char[] outBuf, int outPtr) throws XMLStreamException {
        boolean match;
        char c;
        int bracketCount = 0;
        do {
            ++bracketCount;
        } while ((c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in CDATA section")) == ']');
        boolean bl = match = bracketCount >= 2 && c == '>';
        if (match) {
            bracketCount -= 2;
        }
        while (bracketCount > 0) {
            --bracketCount;
            outBuf[outPtr++] = 93;
            if (outPtr < outBuf.length) continue;
            outBuf = this.mTextBuffer.finishCurrentSegment();
            outPtr = 0;
        }
        this.mTextBuffer.setCurrentLength(outPtr);
        if (match) {
            return true;
        }
        --this.mInputPtr;
        return false;
    }

    private final boolean readTextPrimary(char c) throws XMLStreamException {
        int start;
        int ptr;
        block27: {
            block22: {
                block23: {
                    block26: {
                        block24: {
                            block25: {
                                ptr = this.mInputPtr;
                                start = ptr - 1;
                                if (c > ' ') break block22;
                                int len = this.mInputEnd;
                                if (ptr >= len || !this.mNormalizeLFs) break block23;
                                if (c != '\r') break block24;
                                c = (char)10;
                                if (this.mInputBuffer[ptr] != c) break block25;
                                ++start;
                                if (++ptr < len) break block26;
                                break block23;
                            }
                            this.mInputBuffer[start] = c;
                            break block26;
                        }
                        if (c != '\n') break block23;
                    }
                    this.markLF(ptr);
                    if (this.mCheckIndentation > 0 && (ptr = this.readIndentation(c, ptr)) < 0) {
                        return true;
                    }
                    c = this.mInputBuffer[ptr++];
                }
                this.mWsStatus = 0;
                break block27;
            }
            this.mWsStatus = 2;
        }
        char[] inputBuf = this.mInputBuffer;
        int inputLen = this.mInputEnd;
        while (true) {
            if (c < '?') {
                if (c == '<') {
                    this.mInputPtr = --ptr;
                    this.mTextBuffer.resetWithShared(inputBuf, start, ptr - start);
                    return true;
                }
                if (c < ' ') {
                    if (c == '\n') {
                        this.markLF(ptr);
                    } else if (c == '\r') {
                        if (ptr >= inputLen) {
                            --ptr;
                            break;
                        }
                        if (this.mNormalizeLFs) {
                            if (inputBuf[ptr] == '\n') {
                                --ptr;
                                break;
                            }
                            inputBuf[ptr - 1] = 10;
                        } else if (inputBuf[ptr] == '\n') {
                            ++ptr;
                        }
                        this.markLF(ptr);
                    } else if (c != '\t') {
                        this.mInputPtr = ptr;
                        this.mTextBuffer.resetWithShared(inputBuf, start, ptr - start - 1);
                        boolean deferErrors = ptr - start > 1;
                        this.mPendingException = this.throwInvalidSpace(c, deferErrors);
                        return true;
                    }
                } else {
                    if (c == '&') {
                        --ptr;
                        break;
                    }
                    if (c == '>' && ptr - start >= 3 && inputBuf[ptr - 3] == ']' && inputBuf[ptr - 2] == ']') {
                        this.mInputPtr = ptr;
                        this.mTextBuffer.resetWithShared(inputBuf, start, ptr - start - 1);
                        this.mPendingException = this.throwWfcException(ErrorConsts.ERR_BRACKET_IN_TEXT, true);
                        return true;
                    }
                }
            }
            if (ptr >= inputLen) break;
            c = inputBuf[ptr++];
        }
        this.mInputPtr = ptr;
        this.mTextBuffer.resetWithShared(inputBuf, start, ptr - start);
        return false;
    }

    protected final boolean readTextSecondary(int shortestSegment, boolean deferErrors) throws XMLStreamException {
        char[] outBuf = this.mTextBuffer.getCurrentSegment();
        int outPtr = this.mTextBuffer.getCurrentSegmentSize();
        int inputPtr = this.mInputPtr;
        char[] inputBuffer = this.mInputBuffer;
        int inputLen = this.mInputEnd;
        while (true) {
            int c;
            if (inputPtr >= inputLen) {
                this.mInputPtr = inputPtr;
                if (!this.loadMore()) break;
                inputPtr = this.mInputPtr;
                inputBuffer = this.mInputBuffer;
                inputLen = this.mInputEnd;
            }
            if ((c = inputBuffer[inputPtr++]) < 63) {
                if (c < 32) {
                    if (c == 10) {
                        this.markLF(inputPtr);
                    } else if (c == 13) {
                        this.mInputPtr = inputPtr;
                        if (this.skipCRLF((char)c)) {
                            if (!this.mNormalizeLFs) {
                                outBuf[outPtr++] = c;
                                if (outPtr >= outBuf.length) {
                                    outBuf = this.mTextBuffer.finishCurrentSegment();
                                    outPtr = 0;
                                }
                            }
                            c = 10;
                        } else if (this.mNormalizeLFs) {
                            c = 10;
                        }
                        inputLen = this.mInputEnd;
                        inputPtr = this.mInputPtr;
                    } else if (c != 9) {
                        this.mTextBuffer.setCurrentLength(outPtr);
                        this.mInputPtr = inputPtr;
                        this.mPendingException = this.throwInvalidSpace(c, deferErrors);
                        break;
                    }
                } else {
                    if (c == 60) {
                        this.mInputPtr = inputPtr - 1;
                        break;
                    }
                    if (c == 38) {
                        int ch;
                        this.mInputPtr = inputPtr;
                        if (this.mCfgReplaceEntities) {
                            if ((inputLen - inputPtr < 3 || (ch = this.resolveSimpleEntity(true)) == 0) && (ch = this.fullyResolveEntity(true)) == 0) {
                                inputBuffer = this.mInputBuffer;
                                inputLen = this.mInputEnd;
                                inputPtr = this.mInputPtr;
                                continue;
                            }
                        } else {
                            ch = this.resolveCharOnlyEntity(true);
                            if (ch == 0) {
                                --this.mInputPtr;
                                break;
                            }
                        }
                        if (ch <= 65535) {
                            c = (char)ch;
                        } else {
                            ch -= 65536;
                            if (outPtr >= outBuf.length) {
                                outBuf = this.mTextBuffer.finishCurrentSegment();
                                outPtr = 0;
                            }
                            outBuf[outPtr++] = (char)((ch >> 10) + 55296);
                            if (outPtr >= outBuf.length) {
                                if ((outBuf = this._expandOutputForText(inputPtr, outBuf, Integer.MAX_VALUE)) == null) {
                                    return false;
                                }
                                outPtr = 0;
                            }
                            c = (char)((ch & 0x3FF) + 56320);
                        }
                        inputPtr = this.mInputPtr;
                        inputLen = this.mInputEnd;
                    } else if (c == 62 && inputPtr > 2 && inputBuffer[inputPtr - 3] == ']' && inputBuffer[inputPtr - 2] == ']') {
                        this.mInputPtr = inputPtr;
                        this.mTextBuffer.setCurrentLength(outPtr);
                        this.mPendingException = this.throwWfcException(ErrorConsts.ERR_BRACKET_IN_TEXT, deferErrors);
                        break;
                    }
                }
            }
            outBuf[outPtr++] = c;
            if (outPtr < outBuf.length) continue;
            if ((outBuf = this._expandOutputForText(inputPtr, outBuf, shortestSegment)) == null) {
                return false;
            }
            this.verifyLimit("Text size", this.mConfig.getMaxTextLength(), this.mTextBuffer.size());
            outPtr = 0;
        }
        this.mTextBuffer.setCurrentLength(outPtr);
        return true;
    }

    private final char[] _expandOutputForText(int inputPtr, char[] outBuf, int shortestSegment) {
        TextBuffer tb = this.mTextBuffer;
        tb.setCurrentLength(outBuf.length);
        if (tb.size() >= shortestSegment) {
            this.mInputPtr = inputPtr;
            return null;
        }
        return tb.finishCurrentSegment();
    }

    /*
     * Unable to fully structure code
     */
    private final int readIndentation(char c, int ptr) throws XMLStreamException {
        block7: {
            block8: {
                inputLen = this.mInputEnd;
                inputBuf = this.mInputBuffer;
                start = ptr - 1;
                lf = c;
                if ((c = inputBuf[ptr++]) != ' ' && c != '\t') break block8;
                v0 = lastIndCharPos = c == ' ' ? 32 : 8;
                if ((lastIndCharPos += ptr) > inputLen) {
                    lastIndCharPos = inputLen;
                }
                do {
                    if (ptr < lastIndCharPos) continue;
                    --ptr;
                    break block7;
                } while ((d = inputBuf[ptr++]) == c);
                if (d == '<') ** GOTO lbl-1000
                --ptr;
                break block7;
            }
            if (c != '<') {
                --ptr;
            } else lbl-1000:
            // 2 sources

            {
                if (ptr < inputLen && inputBuf[ptr] != '!') {
                    this.mInputPtr = --ptr;
                    this.mTextBuffer.resetWithIndentation(ptr - start - 1, c);
                    if (this.mCheckIndentation < 40) {
                        this.mCheckIndentation += 16;
                    }
                    this.mWsStatus = 1;
                    return -1;
                }
                --ptr;
            }
        }
        --this.mCheckIndentation;
        if (lf == '\r') {
            inputBuf[start] = 10;
        }
        return ptr;
    }

    private final boolean readSpacePrimary(char c, boolean prologWS) throws XMLStreamException {
        int ptr = this.mInputPtr;
        char[] inputBuf = this.mInputBuffer;
        int inputLen = this.mInputEnd;
        int start = ptr - 1;
        while (true) {
            if (c > ' ') {
                this.mInputPtr = --ptr;
                this.mTextBuffer.resetWithShared(this.mInputBuffer, start, ptr - start);
                return true;
            }
            if (c == '\n') {
                this.markLF(ptr);
            } else if (c == '\r') {
                if (ptr >= this.mInputEnd) {
                    --ptr;
                    break;
                }
                if (this.mNormalizeLFs) {
                    if (inputBuf[ptr] == '\n') {
                        --ptr;
                        break;
                    }
                    inputBuf[ptr - 1] = 10;
                } else if (inputBuf[ptr] == '\n') {
                    ++ptr;
                }
                this.markLF(ptr);
            } else if (c != ' ' && c != '\t') {
                this.throwInvalidSpace(c);
            }
            if (ptr >= inputLen) break;
            c = inputBuf[ptr++];
        }
        this.mInputPtr = ptr;
        this.mTextBuffer.resetWithShared(inputBuf, start, ptr - start);
        return false;
    }

    private void readSpaceSecondary(boolean prologWS) throws XMLStreamException {
        int c;
        char[] outBuf = this.mTextBuffer.getCurrentSegment();
        int outPtr = this.mTextBuffer.getCurrentSegmentSize();
        while ((this.mInputPtr < this.mInputEnd || this.loadMore()) && (c = this.mInputBuffer[this.mInputPtr]) <= 32) {
            ++this.mInputPtr;
            if (c == 10) {
                this.markLF();
            } else if (c == 13) {
                if (this.skipCRLF((char)c)) {
                    if (!this.mNormalizeLFs) {
                        outBuf[outPtr++] = c;
                        if (outPtr >= outBuf.length) {
                            outBuf = this.mTextBuffer.finishCurrentSegment();
                            outPtr = 0;
                        }
                    }
                    c = 10;
                } else if (this.mNormalizeLFs) {
                    c = 10;
                }
            } else if (c != 32 && c != 9) {
                this.throwInvalidSpace(c);
            }
            outBuf[outPtr++] = c;
            if (outPtr < outBuf.length) continue;
            outBuf = this.mTextBuffer.finishCurrentSegment();
            outPtr = 0;
        }
        this.mTextBuffer.setCurrentLength(outPtr);
    }

    private int readAndWriteText(Writer w) throws IOException, XMLStreamException {
        this.mTokenState = 3;
        int start = this.mInputPtr;
        int count = 0;
        while (true) {
            char c;
            int len;
            if (this.mInputPtr >= this.mInputEnd) {
                len = this.mInputPtr - start;
                if (len > 0) {
                    w.write(this.mInputBuffer, start, len);
                    count += len;
                }
                c = this.getNextChar(" in document text content");
                start = this.mInputPtr - 1;
            } else {
                c = this.mInputBuffer[this.mInputPtr++];
            }
            if (c >= '?') continue;
            if (c < ' ') {
                if (c == '\n') {
                    this.markLF();
                    continue;
                }
                if (c == '\r') {
                    char d;
                    int len2;
                    boolean atBoundary;
                    boolean bl = atBoundary = this.mInputPtr >= this.mInputEnd;
                    if (atBoundary) {
                        len2 = this.mInputPtr - start - 1;
                        if (len2 > 0) {
                            w.write(this.mInputBuffer, start, len2);
                            count += len2;
                        }
                        d = this.getNextChar(" in document text content");
                        start = this.mInputPtr;
                    } else {
                        d = this.mInputBuffer[this.mInputPtr++];
                    }
                    if (d == '\n') {
                        if (this.mNormalizeLFs) {
                            len2 = this.mInputPtr - start - 2;
                            if (len2 > 0) {
                                w.write(this.mInputBuffer, start, len2);
                                count += len2;
                            }
                            start = this.mInputPtr - 1;
                        } else if (atBoundary) {
                            w.write(c);
                        }
                    } else {
                        --this.mInputPtr;
                        if (atBoundary) {
                            w.write(this.mNormalizeLFs ? 10 : (int)c);
                            ++count;
                        } else if (this.mNormalizeLFs) {
                            this.mInputBuffer[this.mInputPtr - 1] = 10;
                        }
                    }
                    this.markLF();
                    continue;
                }
                if (c == '\t') continue;
                this.throwInvalidSpace(c);
                continue;
            }
            if (c == '<') break;
            if (c == '&') {
                int ch;
                len = this.mInputPtr - 1 - start;
                if (len > 0) {
                    w.write(this.mInputBuffer, start, len);
                    count += len;
                }
                if (this.mCfgReplaceEntities) {
                    if (this.mInputEnd - this.mInputPtr < 3 || (ch = this.resolveSimpleEntity(true)) == 0) {
                        ch = this.fullyResolveEntity(true);
                    }
                } else {
                    ch = this.resolveCharOnlyEntity(true);
                    if (ch == 0) {
                        start = this.mInputPtr;
                        break;
                    }
                }
                if (ch != 0) {
                    if (ch <= 65535) {
                        c = (char)ch;
                    } else {
                        w.write((char)(((ch -= 65536) >> 10) + 55296));
                        c = (char)((ch & 0x3FF) + 56320);
                    }
                    w.write(c);
                    ++count;
                }
                start = this.mInputPtr;
                continue;
            }
            if (c != '>' || this.mInputPtr < 2 || this.mInputBuffer[this.mInputPtr - 2] != ']' || this.mInputBuffer[this.mInputPtr - 1] != ']') continue;
            len = this.mInputPtr - start;
            if (len > 0) {
                w.write(this.mInputBuffer, start, len);
            }
            this.throwParseError(ErrorConsts.ERR_BRACKET_IN_TEXT);
        }
        --this.mInputPtr;
        int len = this.mInputPtr - start;
        if (len > 0) {
            w.write(this.mInputBuffer, start, len);
            count += len;
        }
        return count;
    }

    private int readAndWriteCData(Writer w) throws IOException, XMLStreamException {
        boolean match;
        this.mTokenState = 3;
        char c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(" in CDATA section");
        int count = 0;
        do {
            int len;
            int start = this.mInputPtr - 1;
            while (true) {
                if (c >= ' ') {
                    if (c == ']') {
                        break;
                    }
                } else if (c == '\n') {
                    this.markLF();
                } else if (c == '\r') {
                    char d;
                    int len2;
                    if (this.mInputPtr >= this.mInputEnd) {
                        len2 = this.mInputPtr - start;
                        if (len2 > 0) {
                            w.write(this.mInputBuffer, start, len2);
                            count += len2;
                        }
                        d = this.getNextChar(" in CDATA section");
                        start = this.mInputPtr;
                    } else {
                        d = this.mInputBuffer[this.mInputPtr++];
                    }
                    if (d == '\n') {
                        if (this.mNormalizeLFs) {
                            len2 = this.mInputPtr - 2 - start;
                            if (len2 > 0) {
                                w.write(this.mInputBuffer, start, len2);
                                count += len2;
                            }
                            start = this.mInputPtr - 1;
                        }
                    } else {
                        --this.mInputPtr;
                        if (this.mNormalizeLFs) {
                            this.mInputBuffer[this.mInputPtr - 1] = 10;
                        }
                    }
                    this.markLF();
                } else if (c != '\t') {
                    this.throwInvalidSpace(c);
                }
                if (this.mInputPtr >= this.mInputEnd) {
                    len = this.mInputPtr - start;
                    if (len > 0) {
                        w.write(this.mInputBuffer, start, len);
                        count += len;
                    }
                    start = 0;
                    c = this.getNextChar(" in CDATA section");
                    continue;
                }
                c = this.mInputBuffer[this.mInputPtr++];
            }
            len = this.mInputPtr - start - 1;
            if (len > 0) {
                w.write(this.mInputBuffer, start, len);
                count += len;
            }
            int bracketCount = 0;
            do {
                ++bracketCount;
            } while ((c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in CDATA section")) == ']');
            boolean bl = match = bracketCount >= 2 && c == '>';
            if (match) {
                bracketCount -= 2;
            }
            while (bracketCount > 0) {
                --bracketCount;
                w.write(93);
                ++count;
            }
        } while (!match);
        return count;
    }

    private int readAndWriteCoalesced(Writer w, boolean wasCData) throws IOException, XMLStreamException {
        this.mTokenState = 4;
        int count = 0;
        while (this.mInputPtr < this.mInputEnd || this.loadMore()) {
            char c = this.mInputBuffer[this.mInputPtr];
            if (c == '<') {
                if (this.mInputEnd - this.mInputPtr < 3 && !this.ensureInput(3) || this.mInputBuffer[this.mInputPtr + 1] != '!' || this.mInputBuffer[this.mInputPtr + 2] != '[') break;
                this.mInputPtr += 3;
                this.checkCData();
                count += this.readAndWriteCData(w);
                wasCData = true;
                continue;
            }
            if (c == '&' && !wasCData) break;
            count += this.readAndWriteText(w);
            wasCData = false;
        }
        return count;
    }

    protected final boolean skipWS(char c) throws XMLStreamException {
        if (c > ' ') {
            return false;
        }
        while (true) {
            if (c == '\n' || c == '\r') {
                this.skipCRLF(c);
            } else if (c != ' ' && c != '\t') {
                this.throwInvalidSpace(c);
            }
            if (this.mInputPtr >= this.mInputEnd && !this.loadMoreFromCurrent()) {
                return true;
            }
            c = this.mInputBuffer[this.mInputPtr];
            if (c > ' ') {
                return true;
            }
            ++this.mInputPtr;
        }
    }

    @Override
    protected EntityDecl findEntity(String id, Object arg) throws XMLStreamException {
        EntityDecl ed = this.mConfig.findCustomInternalEntity(id);
        if (ed == null && this.mGeneralEntities != null) {
            ed = this.mGeneralEntities.get(id);
        }
        if (this.mDocStandalone == 1 && ed != null && ed.wasDeclaredExternally()) {
            this.throwParseError(ErrorConsts.ERR_WF_ENTITY_EXT_DECLARED, ed.getName(), null);
        }
        return ed;
    }

    @Override
    protected void handleUndeclaredEntity(String id) throws XMLStreamException {
        this.throwParseError(this.mDocStandalone == 1 ? ErrorConsts.ERR_WF_GE_UNDECLARED_SA : ErrorConsts.ERR_WF_GE_UNDECLARED, id, null);
    }

    @Override
    protected void handleIncompleteEntityProblem(WstxInputSource closing) throws XMLStreamException {
        String top = this.mElementStack.isEmpty() ? "[ROOT]" : this.mElementStack.getTopElementDesc();
        this.throwParseError("Unexpected end of entity expansion for entity &{0}; was expecting a close tag for element <{1}>", closing.getEntityId(), top);
    }

    protected void handleGreedyEntityProblem(WstxInputSource input) throws XMLStreamException {
        String top = this.mElementStack.isEmpty() ? "[ROOT]" : this.mElementStack.getTopElementDesc();
        this.throwParseError("Improper GE/element nesting: entity &" + input.getEntityId() + " contains closing tag for <" + top + ">");
    }

    private void throwNotTextual(int type) {
        throw new IllegalStateException("Not a textual event (" + this.tokenTypeDesc(type) + ")");
    }

    private void throwNotTextXxx(int type) {
        throw new IllegalStateException("getTextXxx() methods can not be called on " + this.tokenTypeDesc(type));
    }

    protected void throwNotTextualOrElem(int type) {
        throw new IllegalStateException(MessageFormat.format(ErrorConsts.ERR_STATE_NOT_ELEM_OR_TEXT, this.tokenTypeDesc(type)));
    }

    protected void throwUnexpectedEOF() throws WstxException {
        this.throwUnexpectedEOF("; was expecting a close tag for element <" + this.mElementStack.getTopElementDesc() + ">");
    }

    protected XMLStreamException _constructUnexpectedInTyped(int nextToken) {
        if (nextToken == 1) {
            return this._constructTypeException("Element content can not contain child START_ELEMENT when using Typed Access methods", null);
        }
        return this._constructTypeException("Expected a text token, got " + this.tokenTypeDesc(nextToken), null);
    }

    protected TypedXMLStreamException _constructTypeException(String msg, String lexicalValue) {
        return new TypedXMLStreamException(lexicalValue, msg, this.getStartLocation());
    }

    protected void reportInvalidContent(int evtType) throws XMLStreamException {
        this.throwParseError("Internal error: sub-class should override method");
    }
}

