/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sr;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.cfg.InputConfigFlags;
import com.ctc.wstx.cfg.ParsingErrorMsgs;
import com.ctc.wstx.dtd.MinimalDTDReader;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.ent.IntEntity;
import com.ctc.wstx.exc.WstxEOFException;
import com.ctc.wstx.exc.WstxException;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.exc.WstxLazyException;
import com.ctc.wstx.exc.WstxParsingException;
import com.ctc.wstx.exc.WstxUnexpectedCharException;
import com.ctc.wstx.exc.WstxValidationException;
import com.ctc.wstx.io.DefaultInputResolver;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.io.WstxInputLocation;
import com.ctc.wstx.io.WstxInputSource;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.ExceptionUtil;
import com.ctc.wstx.util.SymbolTable;
import com.ctc.wstx.util.TextBuffer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.XMLReporter2;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.validation.XMLValidationProblem;

public abstract class StreamScanner
extends WstxInputData
implements InputProblemReporter,
InputConfigFlags,
ParsingErrorMsgs {
    public static final char CHAR_CR_LF_OR_NULL = '\r';
    public static final int INT_CR_LF_OR_NULL = 13;
    protected static final char CHAR_FIRST_PURE_TEXT = '?';
    protected static final char CHAR_LOWEST_LEGAL_LOCALNAME_CHAR = '-';
    private static final int VALID_CHAR_COUNT = 256;
    private static final byte NAME_CHAR_INVALID_B = 0;
    private static final byte NAME_CHAR_ALL_VALID_B = 1;
    private static final byte NAME_CHAR_VALID_NONFIRST_B = -1;
    private static final byte[] sCharValidity;
    private static final int VALID_PUBID_CHAR_COUNT = 128;
    private static final byte[] sPubidValidity;
    private static final byte PUBID_CHAR_VALID_B = 1;
    protected final ReaderConfig mConfig;
    protected final boolean mCfgNsEnabled;
    protected boolean mCfgReplaceEntities;
    final SymbolTable mSymbols;
    protected String mCurrName;
    protected WstxInputSource mInput;
    protected final WstxInputSource mRootInput;
    protected XMLResolver mEntityResolver = null;
    protected int mCurrDepth;
    protected int mInputTopDepth;
    protected int mEntityExpansionCount;
    protected boolean mNormalizeLFs;
    protected boolean mAllowXml11EscapedCharsInXml10;
    protected char[] mNameBuffer = null;
    protected long mTokenInputTotal = 0L;
    protected int mTokenInputRow = 1;
    protected int mTokenInputCol = 0;
    protected String mDocInputEncoding = null;
    protected String mDocXmlEncoding = null;
    protected int mDocXmlVersion = 0;
    protected Map<String, IntEntity> mCachedEntities;
    protected boolean mCfgTreatCharRefsAsEntities;
    protected EntityDecl mCurrEntity;

    protected StreamScanner(WstxInputSource input, ReaderConfig cfg, XMLResolver res) {
        this.mInput = input;
        this.mRootInput = input;
        this.mConfig = cfg;
        this.mSymbols = cfg.getSymbols();
        int cf = cfg.getConfigFlags();
        this.mCfgNsEnabled = (cf & 1) != 0;
        this.mCfgReplaceEntities = (cf & 4) != 0;
        this.mAllowXml11EscapedCharsInXml10 = this.mConfig.willAllowXml11EscapedCharsInXml10();
        this.mNormalizeLFs = this.mConfig.willNormalizeLFs();
        this.mInputBuffer = null;
        this.mInputEnd = 0;
        this.mInputPtr = 0;
        this.mEntityResolver = res;
        this.mCfgTreatCharRefsAsEntities = this.mConfig.willTreatCharRefsAsEnts();
        this.mCachedEntities = this.mCfgTreatCharRefsAsEntities ? new HashMap<String, IntEntity>() : Collections.emptyMap();
    }

    public ReaderConfig getConfig() {
        return this.mConfig;
    }

    protected WstxInputLocation getLastCharLocation() {
        return this.mInput.getLocation(this.mCurrInputProcessed + (long)this.mInputPtr - 1L, this.mCurrInputRow, this.mInputPtr - this.mCurrInputRowStart);
    }

    protected URL getSource() throws IOException {
        return this.mInput.getSource();
    }

    protected String getSystemId() {
        return this.mInput.getSystemId();
    }

    @Override
    public abstract Location getLocation();

    public XMLStreamLocation2 getStartLocation() {
        return this.mInput.getLocation(this.mTokenInputTotal, this.mTokenInputRow, this.mTokenInputCol + 1);
    }

    public XMLStreamLocation2 getCurrentLocation() {
        return this.mInput.getLocation(this.mCurrInputProcessed + (long)this.mInputPtr, this.mCurrInputRow, this.mInputPtr - this.mCurrInputRowStart + 1);
    }

    public WstxException throwWfcException(String msg, boolean deferErrors) throws WstxException {
        WstxException ex = this.constructWfcException(msg);
        if (!deferErrors) {
            throw ex;
        }
        return ex;
    }

    @Override
    public void throwParseError(String msg) throws XMLStreamException {
        this.throwParseError(msg, null, null);
    }

    @Override
    public void throwParseError(String format, Object arg, Object arg2) throws XMLStreamException {
        String msg = arg == null && arg2 == null ? format : MessageFormat.format(format, arg, arg2);
        throw this.constructWfcException(msg);
    }

    public void reportProblem(String probType, String format, Object arg, Object arg2) throws XMLStreamException {
        XMLReporter rep = this.mConfig.getXMLReporter();
        if (rep != null) {
            this._reportProblem(rep, probType, MessageFormat.format(format, arg, arg2), null);
        }
    }

    @Override
    public void reportProblem(Location loc, String probType, String format, Object arg, Object arg2) throws XMLStreamException {
        XMLReporter rep = this.mConfig.getXMLReporter();
        if (rep != null) {
            String msg = arg != null || arg2 != null ? MessageFormat.format(format, arg, arg2) : format;
            this._reportProblem(rep, probType, msg, loc);
        }
    }

    protected void _reportProblem(XMLReporter rep, String probType, String msg, Location loc) throws XMLStreamException {
        if (loc == null) {
            loc = this.getLastCharLocation();
        }
        this._reportProblem(rep, new XMLValidationProblem(loc, msg, 2, probType));
    }

    protected void _reportProblem(XMLReporter rep, XMLValidationProblem prob) throws XMLStreamException {
        if (rep != null) {
            Location loc = prob.getLocation();
            if (loc == null) {
                loc = this.getLastCharLocation();
                prob.setLocation(loc);
            }
            if (prob.getType() == null) {
                prob.setType(ErrorConsts.WT_VALIDATION);
            }
            if (rep instanceof XMLReporter2) {
                ((XMLReporter2)rep).report(prob);
            } else {
                rep.report(prob.getMessage(), prob.getType(), prob, loc);
            }
        }
    }

    @Override
    public void reportValidationProblem(XMLValidationProblem prob) throws XMLStreamException {
        if (prob.getSeverity() > 2) {
            throw WstxValidationException.create(prob);
        }
        XMLReporter rep = this.mConfig.getXMLReporter();
        if (rep != null) {
            this._reportProblem(rep, prob);
        } else if (prob.getSeverity() >= 2) {
            throw WstxValidationException.create(prob);
        }
    }

    public void reportValidationProblem(String msg, int severity) throws XMLStreamException {
        this.reportValidationProblem(new XMLValidationProblem(this.getLastCharLocation(), msg, severity));
    }

    @Override
    public void reportValidationProblem(String msg) throws XMLStreamException {
        this.reportValidationProblem(new XMLValidationProblem(this.getLastCharLocation(), msg, 2));
    }

    public void reportValidationProblem(Location loc, String msg) throws XMLStreamException {
        this.reportValidationProblem(new XMLValidationProblem(loc, msg));
    }

    @Override
    public void reportValidationProblem(String format, Object arg, Object arg2) throws XMLStreamException {
        this.reportValidationProblem(MessageFormat.format(format, arg, arg2));
    }

    protected WstxException constructWfcException(String msg) {
        return new WstxParsingException(msg, this.getLastCharLocation());
    }

    protected WstxException constructFromIOE(IOException ioe) {
        return new WstxIOException(ioe);
    }

    protected WstxException constructNullCharException() {
        return new WstxUnexpectedCharException("Illegal character (NULL, unicode 0) encountered: not valid in any content", (Location)this.getLastCharLocation(), '\u0000');
    }

    protected void throwUnexpectedChar(int i, String msg) throws WstxException {
        char c = (char)i;
        String excMsg = "Unexpected character " + StreamScanner.getCharDesc(c) + msg;
        throw new WstxUnexpectedCharException(excMsg, (Location)this.getLastCharLocation(), c);
    }

    protected void throwNullChar() throws WstxException {
        throw this.constructNullCharException();
    }

    protected void throwInvalidSpace(int i) throws WstxException {
        this.throwInvalidSpace(i, false);
    }

    protected WstxException throwInvalidSpace(int i, boolean deferErrors) throws WstxException {
        WstxException ex;
        char c = (char)i;
        if (c == '\u0000') {
            ex = this.constructNullCharException();
        } else {
            String msg = "Illegal character (" + StreamScanner.getCharDesc(c) + ")";
            if (this.mXml11) {
                msg = msg + " [note: in XML 1.1, it could be included via entity expansion]";
            }
            ex = new WstxUnexpectedCharException(msg, (Location)this.getLastCharLocation(), c);
        }
        if (!deferErrors) {
            throw ex;
        }
        return ex;
    }

    protected void throwUnexpectedEOF(String msg) throws WstxException {
        throw new WstxEOFException("Unexpected EOF" + (msg == null ? "" : msg), this.getLastCharLocation());
    }

    protected void throwUnexpectedEOB(String msg) throws WstxException {
        throw new WstxEOFException("Unexpected end of input block" + (msg == null ? "" : msg), this.getLastCharLocation());
    }

    protected void throwFromIOE(IOException ioe) throws WstxException {
        throw new WstxIOException(ioe);
    }

    protected void throwFromStrE(XMLStreamException strex) throws WstxException {
        if (strex instanceof WstxException) {
            throw (WstxException)strex;
        }
        throw new WstxException(strex);
    }

    protected void throwLazyError(Exception e) {
        if (e instanceof XMLStreamException) {
            WstxLazyException.throwLazily((XMLStreamException)e);
        }
        ExceptionUtil.throwRuntimeException(e);
    }

    protected String tokenTypeDesc(int type) {
        return ErrorConsts.tokenTypeDesc(type);
    }

    public final WstxInputSource getCurrentInput() {
        return this.mInput;
    }

    protected final int inputInBuffer() {
        return this.mInputEnd - this.mInputPtr;
    }

    protected final int getNext() throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd && !this.loadMore()) {
            return -1;
        }
        return this.mInputBuffer[this.mInputPtr++];
    }

    protected final int peekNext() throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd && !this.loadMoreFromCurrent()) {
            return -1;
        }
        return this.mInputBuffer[this.mInputPtr];
    }

    protected final char getNextChar(String errorMsg) throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd) {
            this.loadMore(errorMsg);
        }
        return this.mInputBuffer[this.mInputPtr++];
    }

    protected final char getNextCharFromCurrent(String errorMsg) throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd) {
            this.loadMoreFromCurrent(errorMsg);
        }
        return this.mInputBuffer[this.mInputPtr++];
    }

    protected final int getNextAfterWS() throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd && !this.loadMore()) {
            return -1;
        }
        char c = this.mInputBuffer[this.mInputPtr++];
        while (c <= ' ') {
            if (c == '\n' || c == '\r') {
                this.skipCRLF(c);
            } else if (c != ' ' && c != '\t') {
                this.throwInvalidSpace(c);
            }
            if (this.mInputPtr >= this.mInputEnd && !this.loadMore()) {
                return -1;
            }
            c = this.mInputBuffer[this.mInputPtr++];
        }
        return c;
    }

    protected final char getNextCharAfterWS(String errorMsg) throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd) {
            this.loadMore(errorMsg);
        }
        char c = this.mInputBuffer[this.mInputPtr++];
        while (c <= ' ') {
            if (c == '\n' || c == '\r') {
                this.skipCRLF(c);
            } else if (c != ' ' && c != '\t') {
                this.throwInvalidSpace(c);
            }
            if (this.mInputPtr >= this.mInputEnd) {
                this.loadMore(errorMsg);
            }
            c = this.mInputBuffer[this.mInputPtr++];
        }
        return c;
    }

    protected final char getNextInCurrAfterWS(String errorMsg) throws XMLStreamException {
        return this.getNextInCurrAfterWS(errorMsg, this.getNextCharFromCurrent(errorMsg));
    }

    protected final char getNextInCurrAfterWS(String errorMsg, char c) throws XMLStreamException {
        while (c <= ' ') {
            if (c == '\n' || c == '\r') {
                this.skipCRLF(c);
            } else if (c != ' ' && c != '\t') {
                this.throwInvalidSpace(c);
            }
            if (this.mInputPtr >= this.mInputEnd) {
                this.loadMoreFromCurrent(errorMsg);
            }
            c = this.mInputBuffer[this.mInputPtr++];
        }
        return c;
    }

    protected final boolean skipCRLF(char c) throws XMLStreamException {
        boolean result;
        if (c == '\r' && this.peekNext() == 10) {
            ++this.mInputPtr;
            result = true;
        } else {
            result = false;
        }
        ++this.mCurrInputRow;
        this.mCurrInputRowStart = this.mInputPtr;
        return result;
    }

    protected final void markLF() {
        ++this.mCurrInputRow;
        this.mCurrInputRowStart = this.mInputPtr;
    }

    protected final void markLF(int inputPtr) {
        ++this.mCurrInputRow;
        this.mCurrInputRowStart = inputPtr;
    }

    protected final void pushback() {
        --this.mInputPtr;
    }

    protected void initInputSource(WstxInputSource newInput, boolean isExt, String entityId) throws XMLStreamException {
        this.mInputPtr = 0;
        this.mInputEnd = 0;
        this.mInputTopDepth = this.mCurrDepth;
        int entityDepth = this.mInput.getEntityDepth() + 1;
        this.verifyLimit("Maximum entity expansion depth", this.mConfig.getMaxEntityDepth(), entityDepth);
        this.mInput = newInput;
        this.mInput.initInputLocation(this, this.mCurrDepth, entityDepth);
        this.mNormalizeLFs = isExt;
    }

    protected boolean loadMore() throws XMLStreamException {
        WstxInputSource input = this.mInput;
        do {
            this.mCurrInputProcessed += (long)this.mInputEnd;
            this.verifyLimit("Maximum document characters", this.mConfig.getMaxCharacters(), this.mCurrInputProcessed);
            this.mCurrInputRowStart -= this.mInputEnd;
            try {
                int count = input.readInto(this);
                if (count > 0) {
                    return true;
                }
                input.close();
            }
            catch (IOException ioe) {
                throw this.constructFromIOE(ioe);
            }
            if (input == this.mRootInput) {
                return false;
            }
            WstxInputSource parent = input.getParent();
            if (parent == null) {
                this.throwNullParent(input);
            }
            if (this.mCurrDepth != input.getScopeId()) {
                this.handleIncompleteEntityProblem(input);
            }
            this.mInput = input = parent;
            input.restoreContext(this);
            this.mInputTopDepth = input.getScopeId();
            if (this.mNormalizeLFs) continue;
            boolean bl = this.mNormalizeLFs = !input.fromInternalEntity();
        } while (this.mInputPtr >= this.mInputEnd);
        return true;
    }

    protected final boolean loadMore(String errorMsg) throws XMLStreamException {
        if (!this.loadMore()) {
            this.throwUnexpectedEOF(errorMsg);
        }
        return true;
    }

    protected boolean loadMoreFromCurrent() throws XMLStreamException {
        this.mCurrInputProcessed += (long)this.mInputEnd;
        this.mCurrInputRowStart -= this.mInputEnd;
        this.verifyLimit("Maximum document characters", this.mConfig.getMaxCharacters(), this.mCurrInputProcessed);
        try {
            int count = this.mInput.readInto(this);
            return count > 0;
        }
        catch (IOException ie) {
            throw this.constructFromIOE(ie);
        }
    }

    protected final boolean loadMoreFromCurrent(String errorMsg) throws XMLStreamException {
        if (!this.loadMoreFromCurrent()) {
            this.throwUnexpectedEOB(errorMsg);
        }
        return true;
    }

    protected boolean ensureInput(int minAmount) throws XMLStreamException {
        int currAmount = this.mInputEnd - this.mInputPtr;
        if (currAmount >= minAmount) {
            return true;
        }
        try {
            return this.mInput.readMore(this, minAmount);
        }
        catch (IOException ie) {
            throw this.constructFromIOE(ie);
        }
    }

    protected void closeAllInput(boolean force) throws XMLStreamException {
        WstxInputSource input = this.mInput;
        while (true) {
            try {
                if (force) {
                    input.closeCompletely();
                } else {
                    input.close();
                }
            }
            catch (IOException ie) {
                throw this.constructFromIOE(ie);
            }
            if (input == this.mRootInput) break;
            WstxInputSource parent = input.getParent();
            if (parent == null) {
                this.throwNullParent(input);
            }
            this.mInput = input = parent;
        }
    }

    protected void throwNullParent(WstxInputSource curr) {
        throw new IllegalStateException(ErrorConsts.ERR_INTERNAL);
    }

    protected int resolveSimpleEntity(boolean checkStd) throws XMLStreamException {
        char c;
        char[] buf = this.mInputBuffer;
        int ptr = this.mInputPtr;
        if ((c = buf[ptr++]) == '#') {
            c = buf[ptr++];
            int value = 0;
            int inputLen = this.mInputEnd;
            if (c == 'x') {
                while (ptr < inputLen && (c = buf[ptr++]) != ';') {
                    value <<= 4;
                    if (c <= '9' && c >= '0') {
                        value += c - 48;
                    } else if (c >= 'a' && c <= 'f') {
                        value += 10 + (c - 97);
                    } else if (c >= 'A' && c <= 'F') {
                        value += 10 + (c - 65);
                    } else {
                        this.mInputPtr = ptr;
                        this.throwUnexpectedChar(c, "; expected a hex digit (0-9a-fA-F).");
                    }
                    if (value <= 0x10FFFF) continue;
                    this.reportUnicodeOverflow();
                }
            } else {
                while (c != ';') {
                    if (c <= '9' && c >= '0') {
                        if ((value = value * 10 + (c - 48)) > 0x10FFFF) {
                            this.reportUnicodeOverflow();
                        }
                    } else {
                        this.mInputPtr = ptr;
                        this.throwUnexpectedChar(c, "; expected a decimal number.");
                    }
                    if (ptr < inputLen) {
                        c = buf[ptr++];
                        continue;
                    }
                    break;
                }
            }
            if (c == ';') {
                this.mInputPtr = ptr;
                this.validateChar(value);
                return value;
            }
        } else if (checkStd) {
            int len;
            if (c == 'a') {
                int len2;
                if ((c = buf[ptr++]) == 'm') {
                    if (buf[ptr++] == 'p' && ptr < this.mInputEnd && buf[ptr++] == ';') {
                        this.mInputPtr = ptr;
                        return 38;
                    }
                } else if (c == 'p' && buf[ptr++] == 'o' && ptr < (len2 = this.mInputEnd) && buf[ptr++] == 's' && ptr < len2 && buf[ptr++] == ';') {
                    this.mInputPtr = ptr;
                    return 39;
                }
            } else if (c == 'g') {
                if (buf[ptr++] == 't' && buf[ptr++] == ';') {
                    this.mInputPtr = ptr;
                    return 62;
                }
            } else if (c == 'l') {
                if (buf[ptr++] == 't' && buf[ptr++] == ';') {
                    this.mInputPtr = ptr;
                    return 60;
                }
            } else if (c == 'q' && buf[ptr++] == 'u' && buf[ptr++] == 'o' && ptr < (len = this.mInputEnd) && buf[ptr++] == 't' && ptr < len && buf[ptr++] == ';') {
                this.mInputPtr = ptr;
                return 34;
            }
        }
        return 0;
    }

    protected int resolveCharOnlyEntity(boolean checkStd) throws XMLStreamException {
        char c;
        int avail = this.mInputEnd - this.mInputPtr;
        if (avail < 6) {
            --this.mInputPtr;
            if (!this.ensureInput(6)) {
                avail = this.inputInBuffer();
                if (avail < 3) {
                    this.throwUnexpectedEOF(" in entity reference");
                }
            } else {
                avail = 6;
            }
            ++this.mInputPtr;
        }
        if ((c = this.mInputBuffer[this.mInputPtr]) == '#') {
            ++this.mInputPtr;
            return this.resolveCharEnt(null);
        }
        if (checkStd) {
            if (c == 'a') {
                char d = this.mInputBuffer[this.mInputPtr + 1];
                if (d == 'm') {
                    if (avail >= 4 && this.mInputBuffer[this.mInputPtr + 2] == 'p' && this.mInputBuffer[this.mInputPtr + 3] == ';') {
                        this.mInputPtr += 4;
                        return 38;
                    }
                } else if (d == 'p' && avail >= 5 && this.mInputBuffer[this.mInputPtr + 2] == 'o' && this.mInputBuffer[this.mInputPtr + 3] == 's' && this.mInputBuffer[this.mInputPtr + 4] == ';') {
                    this.mInputPtr += 5;
                    return 39;
                }
            } else if (c == 'l') {
                if (this.mInputBuffer[this.mInputPtr + 1] == 't' && this.mInputBuffer[this.mInputPtr + 2] == ';') {
                    this.mInputPtr += 3;
                    return 60;
                }
            } else if (c == 'g') {
                if (this.mInputBuffer[this.mInputPtr + 1] == 't' && this.mInputBuffer[this.mInputPtr + 2] == ';') {
                    this.mInputPtr += 3;
                    return 62;
                }
            } else if (c == 'q' && avail >= 5 && this.mInputBuffer[this.mInputPtr + 1] == 'u' && this.mInputBuffer[this.mInputPtr + 2] == 'o' && this.mInputBuffer[this.mInputPtr + 3] == 't' && this.mInputBuffer[this.mInputPtr + 4] == ';') {
                this.mInputPtr += 5;
                return 34;
            }
        }
        return 0;
    }

    protected EntityDecl resolveNonCharEntity() throws XMLStreamException {
        String id;
        char d;
        char c;
        int avail = this.mInputEnd - this.mInputPtr;
        if (avail < 6) {
            --this.mInputPtr;
            if (!this.ensureInput(6)) {
                avail = this.inputInBuffer();
                if (avail < 3) {
                    this.throwUnexpectedEOF(" in entity reference");
                }
            } else {
                avail = 6;
            }
            ++this.mInputPtr;
        }
        if ((c = this.mInputBuffer[this.mInputPtr]) == '#') {
            return null;
        }
        if (c == 'a' ? ((d = this.mInputBuffer[this.mInputPtr + 1]) == 'm' ? avail >= 4 && this.mInputBuffer[this.mInputPtr + 2] == 'p' && this.mInputBuffer[this.mInputPtr + 3] == ';' : d == 'p' && avail >= 5 && this.mInputBuffer[this.mInputPtr + 2] == 'o' && this.mInputBuffer[this.mInputPtr + 3] == 's' && this.mInputBuffer[this.mInputPtr + 4] == ';') : (c == 'l' ? this.mInputBuffer[this.mInputPtr + 1] == 't' && this.mInputBuffer[this.mInputPtr + 2] == ';' : (c == 'g' ? this.mInputBuffer[this.mInputPtr + 1] == 't' && this.mInputBuffer[this.mInputPtr + 2] == ';' : c == 'q' && avail >= 5 && this.mInputBuffer[this.mInputPtr + 1] == 'u' && this.mInputBuffer[this.mInputPtr + 2] == 'o' && this.mInputBuffer[this.mInputPtr + 3] == 't' && this.mInputBuffer[this.mInputPtr + 4] == ';'))) {
            return null;
        }
        ++this.mInputPtr;
        this.mCurrName = id = this.parseEntityName(c);
        return this.findEntity(id, null);
    }

    protected int fullyResolveEntity(boolean allowExt) throws XMLStreamException {
        char c = this.getNextCharFromCurrent(" in entity reference");
        if (c == '#') {
            StringBuffer originalSurface = new StringBuffer("#");
            int ch = this.resolveCharEnt(originalSurface);
            if (this.mCfgTreatCharRefsAsEntities) {
                char[] originalChars = new char[originalSurface.length()];
                originalSurface.getChars(0, originalSurface.length(), originalChars, 0);
                this.mCurrEntity = this.getIntEntity(ch, originalChars);
                return 0;
            }
            return ch;
        }
        String id = this.parseEntityName(c);
        c = id.charAt(0);
        int d = 0;
        if (c == 'a') {
            if (id.equals("amp")) {
                d = 38;
            } else if (id.equals("apos")) {
                d = 39;
            }
        } else if (c == 'g') {
            if (id.length() == 2 && id.charAt(1) == 't') {
                d = 62;
            }
        } else if (c == 'l') {
            if (id.length() == 2 && id.charAt(1) == 't') {
                d = 60;
            }
        } else if (c == 'q' && id.equals("quot")) {
            d = 34;
        }
        if (d != 0) {
            if (this.mCfgTreatCharRefsAsEntities) {
                char[] originalChars = new char[id.length()];
                id.getChars(0, id.length(), originalChars, 0);
                this.mCurrEntity = this.getIntEntity(d, originalChars);
                return 0;
            }
            return d;
        }
        EntityDecl e = this.expandEntity(id, allowExt, null);
        if (this.mCfgTreatCharRefsAsEntities) {
            this.mCurrEntity = e;
        }
        return 0;
    }

    protected EntityDecl getIntEntity(int ch, char[] originalChars) {
        String cacheKey = new String(originalChars);
        IntEntity entity = this.mCachedEntities.get(cacheKey);
        if (entity == null) {
            String repl;
            if (ch <= 65535) {
                repl = Character.toString((char)ch);
            } else {
                StringBuffer sb = new StringBuffer(2);
                sb.append((char)(((ch -= 65536) >> 10) + 55296));
                sb.append((char)((ch & 0x3FF) + 56320));
                repl = sb.toString();
            }
            entity = IntEntity.create(new String(originalChars), repl);
            this.mCachedEntities.put(cacheKey, entity);
        }
        return entity;
    }

    protected EntityDecl expandEntity(String id, boolean allowExt, Object extraArg) throws XMLStreamException {
        this.mCurrName = id;
        EntityDecl ed = this.findEntity(id, extraArg);
        if (ed == null) {
            if (this.mCfgReplaceEntities) {
                this.mCurrEntity = this.expandUnresolvedEntity(id);
            }
            return null;
        }
        if (!this.mCfgTreatCharRefsAsEntities || this instanceof MinimalDTDReader) {
            this.expandEntity(ed, allowExt);
        }
        return ed;
    }

    private void expandEntity(EntityDecl ed, boolean allowExt) throws XMLStreamException {
        boolean isExt;
        String id = ed.getName();
        if (this.mInput.isOrIsExpandedFrom(id)) {
            this.throwRecursionError(id);
        }
        if (!ed.isParsed()) {
            this.throwParseError("Illegal reference to unparsed external entity \"{0}\"", id, null);
        }
        if (isExt = ed.isExternal()) {
            if (!allowExt) {
                this.throwParseError("Encountered a reference to external parsed entity \"{0}\" when expanding attribute value: not legal as per XML 1.0/1.1 #3.1", id, null);
            }
            if (!this.mConfig.willSupportExternalEntities()) {
                this.throwParseError("Encountered a reference to external entity \"{0}\", but stream reader has feature \"{1}\" disabled", id, "javax.xml.stream.isSupportingExternalEntities");
            }
        }
        this.verifyLimit("Maximum entity expansion count", this.mConfig.getMaxEntityCount(), ++this.mEntityExpansionCount);
        WstxInputSource oldInput = this.mInput;
        oldInput.saveContext(this);
        WstxInputSource newInput = null;
        try {
            newInput = ed.expand(oldInput, this.mEntityResolver, this.mConfig, this.mDocXmlVersion);
        }
        catch (FileNotFoundException fex) {
            this.throwParseError("(was {0}) {1}", fex.getClass().getName(), fex.getMessage());
        }
        catch (IOException ioe) {
            throw this.constructFromIOE(ioe);
        }
        this.initInputSource(newInput, isExt, id);
    }

    private EntityDecl expandUnresolvedEntity(String id) throws XMLStreamException {
        XMLResolver resolver = this.mConfig.getUndeclaredEntityResolver();
        if (resolver != null) {
            WstxInputSource newInput;
            if (this.mInput.isOrIsExpandedFrom(id)) {
                this.throwRecursionError(id);
            }
            WstxInputSource oldInput = this.mInput;
            oldInput.saveContext(this);
            int xmlVersion = this.mDocXmlVersion;
            if (xmlVersion == 0) {
                xmlVersion = 256;
            }
            try {
                newInput = DefaultInputResolver.resolveEntityUsing(oldInput, id, null, null, resolver, this.mConfig, xmlVersion);
                if (this.mCfgTreatCharRefsAsEntities) {
                    return new IntEntity(WstxInputLocation.getEmptyLocation(), newInput.getEntityId(), newInput.getSource(), new char[0], WstxInputLocation.getEmptyLocation());
                }
            }
            catch (IOException ioe) {
                throw this.constructFromIOE(ioe);
            }
            if (newInput != null) {
                this.initInputSource(newInput, true, id);
                return null;
            }
        }
        this.handleUndeclaredEntity(id);
        return null;
    }

    protected abstract EntityDecl findEntity(String var1, Object var2) throws XMLStreamException;

    protected abstract void handleUndeclaredEntity(String var1) throws XMLStreamException;

    protected abstract void handleIncompleteEntityProblem(WstxInputSource var1) throws XMLStreamException;

    protected String parseLocalName(char c) throws XMLStreamException {
        if (!this.isNameStartChar((char)c)) {
            if (c == 58) {
                this.throwUnexpectedChar(c, " (missing namespace prefix?)");
            }
            this.throwUnexpectedChar(c, " (expected a name start character)");
        }
        int ptr = this.mInputPtr;
        int hash = c;
        int inputLen = this.mInputEnd;
        int startPtr = ptr - 1;
        char[] inputBuf = this.mInputBuffer;
        while (true) {
            if (ptr >= inputLen) {
                this.mInputPtr = ptr;
                return this.parseLocalName2(startPtr, hash);
            }
            c = inputBuf[ptr];
            if (c < 45 || !this.isNameChar((char)c)) break;
            hash = hash * 31 + c;
            ++ptr;
        }
        this.mInputPtr = ptr;
        return this.mSymbols.findSymbol(this.mInputBuffer, startPtr, ptr - startPtr, hash);
    }

    protected String parseLocalName2(int start, int hash) throws XMLStreamException {
        char c;
        int ptr = this.mInputEnd - start;
        char[] outBuf = this.getNameBuffer(ptr + 8);
        if (ptr > 0) {
            System.arraycopy(this.mInputBuffer, start, outBuf, 0, ptr);
        }
        int outLen = outBuf.length;
        while ((this.mInputPtr < this.mInputEnd || this.loadMoreFromCurrent()) && (c = this.mInputBuffer[this.mInputPtr]) >= '-' && this.isNameChar(c)) {
            ++this.mInputPtr;
            if (ptr >= outLen) {
                outBuf = this.expandBy50Pct(outBuf);
                this.mNameBuffer = outBuf;
                outLen = outBuf.length;
            }
            outBuf[ptr++] = c;
            hash = hash * 31 + c;
        }
        return this.mSymbols.findSymbol(outBuf, 0, ptr, hash);
    }

    protected String parseFullName() throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd) {
            this.loadMoreFromCurrent();
        }
        return this.parseFullName(this.mInputBuffer[this.mInputPtr++]);
    }

    protected String parseFullName(char c) throws XMLStreamException {
        if (!this.isNameStartChar((char)c)) {
            if (c == 58) {
                if (this.mCfgNsEnabled) {
                    this.throwNsColonException(this.parseFNameForError());
                }
            } else {
                if (c <= 32) {
                    this.throwUnexpectedChar(c, " (missing name?)");
                }
                this.throwUnexpectedChar(c, " (expected a name start character)");
            }
        }
        int ptr = this.mInputPtr;
        int hash = c;
        int inputLen = this.mInputEnd;
        int startPtr = ptr - 1;
        while (true) {
            if (ptr >= inputLen) {
                this.mInputPtr = ptr;
                return this.parseFullName2(startPtr, hash);
            }
            c = this.mInputBuffer[ptr];
            if (c == 58) {
                if (this.mCfgNsEnabled) {
                    this.mInputPtr = ptr;
                    this.throwNsColonException(new String(this.mInputBuffer, startPtr, ptr - startPtr) + this.parseFNameForError());
                }
            } else if (c < 45 || !this.isNameChar((char)c)) break;
            hash = hash * 31 + c;
            ++ptr;
        }
        this.mInputPtr = ptr;
        return this.mSymbols.findSymbol(this.mInputBuffer, startPtr, ptr - startPtr, hash);
    }

    protected String parseFullName2(int start, int hash) throws XMLStreamException {
        int ptr = this.mInputEnd - start;
        char[] outBuf = this.getNameBuffer(ptr + 8);
        if (ptr > 0) {
            System.arraycopy(this.mInputBuffer, start, outBuf, 0, ptr);
        }
        int outLen = outBuf.length;
        while (this.mInputPtr < this.mInputEnd || this.loadMoreFromCurrent()) {
            char c = this.mInputBuffer[this.mInputPtr];
            if (c == ':') {
                if (this.mCfgNsEnabled) {
                    this.throwNsColonException(new String(outBuf, 0, ptr) + c + this.parseFNameForError());
                }
            } else if (c < '-' || !this.isNameChar(c)) break;
            ++this.mInputPtr;
            if (ptr >= outLen) {
                outBuf = this.expandBy50Pct(outBuf);
                this.mNameBuffer = outBuf;
                outLen = outBuf.length;
            }
            outBuf[ptr++] = c;
            hash = hash * 31 + c;
        }
        return this.mSymbols.findSymbol(outBuf, 0, ptr, hash);
    }

    protected String parseFNameForError() throws XMLStreamException {
        StringBuilder sb = new StringBuilder(100);
        while (true) {
            char c;
            if (this.mInputPtr < this.mInputEnd) {
                c = this.mInputBuffer[this.mInputPtr++];
            } else {
                int i = this.getNext();
                if (i < 0) break;
                c = (char)i;
            }
            if (c != ':' && !this.isNameChar(c)) {
                --this.mInputPtr;
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    protected final String parseEntityName(char c) throws XMLStreamException {
        String id = this.parseFullName(c);
        if (this.mInputPtr >= this.mInputEnd && !this.loadMoreFromCurrent()) {
            this.throwParseError("Missing semicolon after reference for entity \"{0}\"", id, null);
        }
        if ((c = this.mInputBuffer[this.mInputPtr++]) != ';') {
            this.throwUnexpectedChar(c, "; expected a semi-colon after the reference for entity '" + id + "'");
        }
        return id;
    }

    protected int skipFullName(char c) throws XMLStreamException {
        if (!this.isNameStartChar(c)) {
            --this.mInputPtr;
            return 0;
        }
        int count = 1;
        while (true) {
            char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar("; expected an identifier");
            if (c != ':' && !this.isNameChar(c)) break;
            ++count;
        }
        return count;
    }

    protected final String parseSystemId(char quoteChar, boolean convertLFs, String errorMsg) throws XMLStreamException {
        char[] buf = this.getNameBuffer(-1);
        int ptr = 0;
        while (true) {
            char c;
            char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(errorMsg);
            if (c == quoteChar) break;
            if (c == '\n') {
                this.markLF();
            } else if (c == '\r') {
                if (this.peekNext() == 10) {
                    ++this.mInputPtr;
                    if (!convertLFs) {
                        if (ptr >= buf.length) {
                            buf = this.expandBy50Pct(buf);
                        }
                        buf[ptr++] = 13;
                    }
                    c = '\n';
                } else if (convertLFs) {
                    c = '\n';
                }
            }
            if (ptr >= buf.length) {
                buf = this.expandBy50Pct(buf);
            }
            buf[ptr++] = c;
        }
        return ptr == 0 ? "" : new String(buf, 0, ptr);
    }

    protected final String parsePublicId(char quoteChar, String errorMsg) throws XMLStreamException {
        char[] buf = this.getNameBuffer(-1);
        int ptr = 0;
        boolean spaceToAdd = false;
        while (true) {
            char c;
            char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(errorMsg);
            if (c == quoteChar) break;
            if (c == '\n') {
                this.markLF();
                spaceToAdd = true;
                continue;
            }
            if (c == '\r') {
                if (this.peekNext() == 10) {
                    ++this.mInputPtr;
                }
                spaceToAdd = true;
                continue;
            }
            if (c == ' ') {
                spaceToAdd = true;
                continue;
            }
            if (c >= '\u0080' || sPubidValidity[c] != 1) {
                this.throwUnexpectedChar(c, " in public identifier");
            }
            if (ptr >= buf.length) {
                buf = this.expandBy50Pct(buf);
            }
            if (spaceToAdd) {
                spaceToAdd = false;
                if (ptr > 0) {
                    buf[ptr++] = 32;
                    if (ptr >= buf.length) {
                        buf = this.expandBy50Pct(buf);
                    }
                }
            }
            buf[ptr++] = c;
        }
        return ptr == 0 ? "" : new String(buf, 0, ptr);
    }

    protected final void parseUntil(TextBuffer tb, char endChar, boolean convertLFs, String errorMsg) throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd) {
            this.loadMore(errorMsg);
        }
        while (true) {
            int ptr;
            char[] inputBuf = this.mInputBuffer;
            int inputLen = this.mInputEnd;
            int startPtr = ptr = this.mInputPtr;
            while (ptr < inputLen) {
                int thisLen;
                char c;
                if ((c = inputBuf[ptr++]) == endChar) {
                    thisLen = ptr - startPtr - 1;
                    if (thisLen > 0) {
                        tb.append(inputBuf, startPtr, thisLen);
                    }
                    this.mInputPtr = ptr;
                    return;
                }
                if (c == '\n') {
                    this.mInputPtr = ptr;
                    this.markLF();
                    continue;
                }
                if (c != '\r') continue;
                if (!convertLFs && ptr < inputLen) {
                    if (inputBuf[ptr] == '\n') {
                        // empty if block
                    }
                    this.mInputPtr = ++ptr;
                    this.markLF();
                    continue;
                }
                thisLen = ptr - startPtr - 1;
                if (thisLen > 0) {
                    tb.append(inputBuf, startPtr, thisLen);
                }
                this.mInputPtr = ptr;
                c = this.getNextChar(errorMsg);
                if (c != '\n') {
                    --this.mInputPtr;
                    tb.append(convertLFs ? (char)'\n' : '\r');
                } else if (convertLFs) {
                    tb.append('\n');
                } else {
                    tb.append('\r');
                    tb.append('\n');
                }
                startPtr = ptr = this.mInputPtr;
                this.markLF();
            }
            int thisLen = ptr - startPtr;
            if (thisLen > 0) {
                tb.append(inputBuf, startPtr, thisLen);
            }
            this.loadMore(errorMsg);
            startPtr = ptr = this.mInputPtr;
            inputBuf = this.mInputBuffer;
            inputLen = this.mInputEnd;
        }
    }

    private int resolveCharEnt(StringBuffer originalCharacters) throws XMLStreamException {
        int value = 0;
        char c = this.getNextChar(" in entity reference");
        if (originalCharacters != null) {
            originalCharacters.append(c);
        }
        if (c == 'x') {
            while (true) {
                char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in entity reference");
                if (c != ';') {
                    if (originalCharacters != null) {
                        originalCharacters.append(c);
                    }
                    value <<= 4;
                    if (c <= '9' && c >= '0') {
                        value += c - 48;
                    } else if (c >= 'a' && c <= 'f') {
                        value += 10 + (c - 97);
                    } else if (c >= 'A' && c <= 'F') {
                        value += 10 + (c - 65);
                    } else {
                        this.throwUnexpectedChar(c, "; expected a hex digit (0-9a-fA-F).");
                    }
                    if (value <= 0x10FFFF) continue;
                    this.reportUnicodeOverflow();
                    continue;
                }
                break;
            }
        } else {
            while (c != ';') {
                if (c <= '9' && c >= '0') {
                    if ((value = value * 10 + (c - 48)) > 0x10FFFF) {
                        this.reportUnicodeOverflow();
                    }
                } else {
                    this.throwUnexpectedChar(c, "; expected a decimal number.");
                }
                char c3 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in entity reference");
                if (originalCharacters == null || c == ';') continue;
                originalCharacters.append(c);
            }
        }
        this.validateChar(value);
        return value;
    }

    private final void validateChar(int value) throws XMLStreamException {
        if (value >= 55296) {
            if (value < 57344) {
                this.reportIllegalChar(value);
            }
            if (value > 65535) {
                if (value > 0x10FFFF) {
                    this.reportUnicodeOverflow();
                }
            } else if (value >= 65534) {
                this.reportIllegalChar(value);
            }
        } else if (value < 32) {
            if (value == 0) {
                this.throwParseError("Invalid character reference: null character not allowed in XML content.");
            }
            if (!this.mXml11 && !this.mAllowXml11EscapedCharsInXml10 && value != 9 && value != 10 && value != 13) {
                this.reportIllegalChar(value);
            }
        }
    }

    protected final char[] getNameBuffer(int minSize) {
        char[] buf = this.mNameBuffer;
        if (buf == null) {
            this.mNameBuffer = buf = new char[minSize > 48 ? minSize + 16 : 64];
        } else if (minSize >= buf.length) {
            int len = buf.length;
            this.mNameBuffer = buf = new char[minSize >= (len += len >> 1) ? minSize + 16 : len];
        }
        return buf;
    }

    protected final char[] expandBy50Pct(char[] buf) {
        int len = buf.length;
        char[] newBuf = new char[len + (len >> 1)];
        System.arraycopy(buf, 0, newBuf, 0, len);
        return newBuf;
    }

    private void throwNsColonException(String name) throws XMLStreamException {
        this.throwParseError("Illegal name \"{0}\" (PI target, entity/notation name): can not contain a colon (XML Namespaces 1.0#6)", name, null);
    }

    private void throwRecursionError(String entityName) throws XMLStreamException {
        this.throwParseError("Illegal entity expansion: entity \"{0}\" expands itself recursively.", entityName, null);
    }

    private void reportUnicodeOverflow() throws XMLStreamException {
        this.throwParseError("Illegal character entity: value higher than max allowed (0x{0})", Integer.toHexString(0x10FFFF), null);
    }

    private void reportIllegalChar(int value) throws XMLStreamException {
        this.throwParseError("Illegal character entity: expansion character (code 0x{0}", Integer.toHexString(value), null);
    }

    protected void verifyLimit(String type, long maxValue, long currentValue) throws XMLStreamException {
        if (currentValue > maxValue) {
            throw this.constructLimitViolation(type, maxValue);
        }
    }

    protected XMLStreamException constructLimitViolation(String type, long limit) throws XMLStreamException {
        return new XMLStreamException(type + " limit (" + limit + ") exceeded");
    }

    static {
        int i;
        sCharValidity = new byte[256];
        StreamScanner.sCharValidity[95] = 1;
        int last = 25;
        for (i = 0; i <= last; ++i) {
            StreamScanner.sCharValidity[65 + i] = 1;
            StreamScanner.sCharValidity[97 + i] = 1;
        }
        for (i = 192; i < 246; ++i) {
            StreamScanner.sCharValidity[i] = 1;
        }
        StreamScanner.sCharValidity[215] = 0;
        StreamScanner.sCharValidity[247] = 0;
        StreamScanner.sCharValidity[45] = -1;
        StreamScanner.sCharValidity[46] = -1;
        StreamScanner.sCharValidity[183] = -1;
        for (i = 48; i <= 57; ++i) {
            StreamScanner.sCharValidity[i] = -1;
        }
        sPubidValidity = new byte[128];
        last = 25;
        for (i = 0; i <= last; ++i) {
            StreamScanner.sPubidValidity[65 + i] = 1;
            StreamScanner.sPubidValidity[97 + i] = 1;
        }
        for (i = 48; i <= 57; ++i) {
            StreamScanner.sPubidValidity[i] = 1;
        }
        StreamScanner.sPubidValidity[10] = 1;
        StreamScanner.sPubidValidity[13] = 1;
        StreamScanner.sPubidValidity[32] = 1;
        StreamScanner.sPubidValidity[45] = 1;
        StreamScanner.sPubidValidity[39] = 1;
        StreamScanner.sPubidValidity[40] = 1;
        StreamScanner.sPubidValidity[41] = 1;
        StreamScanner.sPubidValidity[43] = 1;
        StreamScanner.sPubidValidity[44] = 1;
        StreamScanner.sPubidValidity[46] = 1;
        StreamScanner.sPubidValidity[47] = 1;
        StreamScanner.sPubidValidity[58] = 1;
        StreamScanner.sPubidValidity[61] = 1;
        StreamScanner.sPubidValidity[63] = 1;
        StreamScanner.sPubidValidity[59] = 1;
        StreamScanner.sPubidValidity[33] = 1;
        StreamScanner.sPubidValidity[42] = 1;
        StreamScanner.sPubidValidity[35] = 1;
        StreamScanner.sPubidValidity[64] = 1;
        StreamScanner.sPubidValidity[36] = 1;
        StreamScanner.sPubidValidity[95] = 1;
        StreamScanner.sPubidValidity[37] = 1;
    }
}

