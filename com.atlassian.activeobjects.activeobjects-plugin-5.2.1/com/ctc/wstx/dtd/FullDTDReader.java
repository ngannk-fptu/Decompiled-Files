/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.dtd.ChoiceContentSpec;
import com.ctc.wstx.dtd.ContentSpec;
import com.ctc.wstx.dtd.DFAState;
import com.ctc.wstx.dtd.DFAValidator;
import com.ctc.wstx.dtd.DTDAttribute;
import com.ctc.wstx.dtd.DTDElement;
import com.ctc.wstx.dtd.DTDEventListener;
import com.ctc.wstx.dtd.DTDSubset;
import com.ctc.wstx.dtd.DTDSubsetImpl;
import com.ctc.wstx.dtd.DTDWriter;
import com.ctc.wstx.dtd.DefaultAttrValue;
import com.ctc.wstx.dtd.EmptyValidator;
import com.ctc.wstx.dtd.MinimalDTDReader;
import com.ctc.wstx.dtd.SeqContentSpec;
import com.ctc.wstx.dtd.StructValidator;
import com.ctc.wstx.dtd.TokenContentSpec;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.ent.IntEntity;
import com.ctc.wstx.ent.ParsedExtEntity;
import com.ctc.wstx.ent.UnparsedExtEntity;
import com.ctc.wstx.evt.WNotationDeclaration;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.io.WstxInputLocation;
import com.ctc.wstx.io.WstxInputSource;
import com.ctc.wstx.util.InternCache;
import com.ctc.wstx.util.PrefixedName;
import com.ctc.wstx.util.SymbolTable;
import com.ctc.wstx.util.TextBuffer;
import com.ctc.wstx.util.WordResolver;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.NotationDeclaration;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.validation.XMLValidationProblem;

public class FullDTDReader
extends MinimalDTDReader {
    static final boolean INTERN_SHARED_NAMES = false;
    static final Boolean ENTITY_EXP_GE = Boolean.FALSE;
    static final Boolean ENTITY_EXP_PE = Boolean.TRUE;
    final int mConfigFlags;
    final boolean mCfgSupportDTDPP;
    final boolean mCfgFullyValidating;
    HashMap mParamEntities;
    final HashMap mPredefdPEs;
    Set mRefdPEs;
    HashMap mGeneralEntities;
    final HashMap mPredefdGEs;
    Set mRefdGEs;
    boolean mUsesPredefdEntities = false;
    HashMap mNotations;
    final HashMap mPredefdNotations;
    boolean mUsesPredefdNotations = false;
    HashMap mNotationForwardRefs;
    HashMap mSharedNames = null;
    HashMap mElements;
    HashMap mSharedEnumValues = null;
    DefaultAttrValue mCurrAttrDefault = null;
    boolean mExpandingPE = false;
    TextBuffer mValueBuffer = null;
    int mIncludeCount = 0;
    boolean mCheckForbiddenPEs = false;
    String mCurrDeclaration;
    boolean mAnyDTDppFeatures = false;
    String mDefaultNsURI = "";
    HashMap mNamespaces = null;
    DTDWriter mFlattenWriter = null;
    final DTDEventListener mEventListener;
    transient TextBuffer mTextBuffer = null;
    final PrefixedName mAccessKey = new PrefixedName(null, null);

    private FullDTDReader(WstxInputSource input, ReaderConfig cfg, boolean constructFully, int xmlVersion) {
        this(input, cfg, false, null, constructFully, xmlVersion);
    }

    private FullDTDReader(WstxInputSource input, ReaderConfig cfg, DTDSubset intSubset, boolean constructFully, int xmlVersion) {
        this(input, cfg, true, intSubset, constructFully, xmlVersion);
        input.initInputLocation(this, this.mCurrDepth, 0);
    }

    private FullDTDReader(WstxInputSource input, ReaderConfig cfg, boolean isExt, DTDSubset intSubset, boolean constructFully, int xmlVersion) {
        super(input, cfg, isExt);
        int cfgFlags;
        this.mDocXmlVersion = xmlVersion;
        this.mXml11 = cfg.isXml11();
        this.mConfigFlags = cfgFlags = cfg.getConfigFlags();
        this.mCfgSupportDTDPP = (cfgFlags & 0x80000) != 0;
        this.mCfgFullyValidating = constructFully;
        this.mUsesPredefdEntities = false;
        this.mParamEntities = null;
        this.mRefdPEs = null;
        this.mRefdGEs = null;
        this.mGeneralEntities = null;
        HashMap pes = intSubset == null ? null : intSubset.getParameterEntityMap();
        this.mPredefdPEs = pes == null || pes.isEmpty() ? null : pes;
        HashMap ges = intSubset == null ? null : intSubset.getGeneralEntityMap();
        this.mPredefdGEs = ges == null || ges.isEmpty() ? null : ges;
        HashMap not = intSubset == null ? null : intSubset.getNotationMap();
        this.mPredefdNotations = not == null || not.isEmpty() ? null : not;
        this.mEventListener = this.mConfig.getDTDEventListener();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static DTDSubset readInternalSubset(WstxInputData srcData, WstxInputSource input, ReaderConfig cfg, boolean constructFully, int xmlVersion) throws XMLStreamException {
        DTDSubset ss;
        FullDTDReader r = new FullDTDReader(input, cfg, constructFully, xmlVersion);
        r.copyBufferStateFrom(srcData);
        try {
            ss = r.parseDTD();
        }
        finally {
            srcData.copyBufferStateFrom(r);
        }
        return ss;
    }

    public static DTDSubset readExternalSubset(WstxInputSource src, ReaderConfig cfg, DTDSubset intSubset, boolean constructFully, int xmlVersion) throws XMLStreamException {
        FullDTDReader r = new FullDTDReader(src, cfg, intSubset, constructFully, xmlVersion);
        return r.parseDTD();
    }

    public static DTDSubset flattenExternalSubset(WstxInputSource src, Writer flattenWriter, boolean inclComments, boolean inclConditionals, boolean inclPEs) throws IOException, XMLStreamException {
        ReaderConfig cfg = ReaderConfig.createFullDefaults();
        cfg = cfg.createNonShared(new SymbolTable());
        FullDTDReader r = new FullDTDReader(src, cfg, null, true, 0);
        r.setFlattenWriter(flattenWriter, inclComments, inclConditionals, inclPEs);
        DTDSubset ss = r.parseDTD();
        r.flushFlattenWriter();
        flattenWriter.flush();
        return ss;
    }

    private TextBuffer getTextBuffer() {
        if (this.mTextBuffer == null) {
            this.mTextBuffer = TextBuffer.createTemporaryBuffer();
            this.mTextBuffer.resetInitialized();
        } else {
            this.mTextBuffer.resetWithEmpty();
        }
        return this.mTextBuffer;
    }

    public void setFlattenWriter(Writer w, boolean inclComments, boolean inclConditionals, boolean inclPEs) {
        this.mFlattenWriter = new DTDWriter(w, inclComments, inclConditionals, inclPEs);
    }

    private void flushFlattenWriter() throws XMLStreamException {
        this.mFlattenWriter.flush(this.mInputBuffer, this.mInputPtr);
    }

    public EntityDecl findEntity(String entName) {
        EntityDecl decl;
        if (this.mPredefdGEs != null && (decl = (EntityDecl)this.mPredefdGEs.get(entName)) != null) {
            return decl;
        }
        return (EntityDecl)this.mGeneralEntities.get(entName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected DTDSubset parseDTD() throws XMLStreamException {
        DTDSubsetImpl ss;
        while (true) {
            this.mCheckForbiddenPEs = false;
            int i = this.getNextAfterWS();
            if (i < 0) {
                if (this.mIsExternal) break;
                this.throwUnexpectedEOF(" in internal DTD subset");
            }
            if (i == 37) {
                this.expandPE();
                continue;
            }
            this.mTokenInputTotal = this.mCurrInputProcessed + (long)this.mInputPtr;
            this.mTokenInputRow = this.mCurrInputRow;
            this.mTokenInputCol = this.mInputPtr - this.mCurrInputRowStart;
            if (i == 60) {
                boolean bl = this.mCheckForbiddenPEs = !this.mIsExternal && this.mInput == this.mRootInput;
                if (this.mFlattenWriter == null) {
                    this.parseDirective();
                    continue;
                }
                this.parseDirectiveFlattened();
                continue;
            }
            if (i == 93) {
                if (this.mIncludeCount == 0 && !this.mIsExternal) break;
                if (this.mIncludeCount > 0) {
                    boolean suppress;
                    boolean bl = suppress = this.mFlattenWriter != null && !this.mFlattenWriter.includeConditionals();
                    if (suppress) {
                        this.mFlattenWriter.flush(this.mInputBuffer, this.mInputPtr - 1);
                        this.mFlattenWriter.disableOutput();
                    }
                    try {
                        char c = this.dtdNextFromCurr();
                        if (c == ']' && (c = this.dtdNextFromCurr()) == '>') {
                            --this.mIncludeCount;
                            continue;
                        }
                        this.throwDTDUnexpectedChar(c, "; expected ']]>' to close conditional include section");
                    }
                    finally {
                        if (!suppress) continue;
                        this.mFlattenWriter.enableOutput(this.mInputPtr);
                        continue;
                    }
                }
            }
            if (this.mIsExternal) {
                this.throwDTDUnexpectedChar(i, "; expected a '<' to start a directive");
            }
            this.throwDTDUnexpectedChar(i, "; expected a '<' to start a directive, or \"]>\" to end internal subset");
        }
        if (this.mIncludeCount > 0) {
            String suffix = this.mIncludeCount == 1 ? "an INCLUDE block" : "" + this.mIncludeCount + " INCLUDE blocks";
            this.throwUnexpectedEOF(this.getErrorMsg() + "; expected closing marker for " + suffix);
        }
        if (this.mNotationForwardRefs != null && this.mNotationForwardRefs.size() > 0) {
            this._reportUndefinedNotationRefs();
        }
        if (this.mIsExternal) {
            boolean cachable = !this.mUsesPredefdEntities && !this.mUsesPredefdNotations;
            ss = DTDSubsetImpl.constructInstance(cachable, this.mGeneralEntities, this.mRefdGEs, null, this.mRefdPEs, this.mNotations, this.mElements, this.mCfgFullyValidating);
        } else {
            ss = DTDSubsetImpl.constructInstance(false, this.mGeneralEntities, null, this.mParamEntities, null, this.mNotations, this.mElements, this.mCfgFullyValidating);
        }
        return ss;
    }

    protected void parseDirective() throws XMLStreamException {
        char c = this.dtdNextFromCurr();
        if (c == '?') {
            this.readPI();
            return;
        }
        if (c != '!') {
            this.throwDTDUnexpectedChar(c, "; expected '!' to start a directive");
        }
        if ((c = this.dtdNextFromCurr()) == '-') {
            c = this.dtdNextFromCurr();
            if (c != '-') {
                this.throwDTDUnexpectedChar(c, "; expected '-' for a comment");
            }
            if (this.mEventListener != null && this.mEventListener.dtdReportComments()) {
                this.readComment(this.mEventListener);
            } else {
                this.skipComment();
            }
        } else if (c == '[') {
            this.checkInclusion();
        } else if (c >= 'A' && c <= 'Z') {
            this.handleDeclaration(c);
        } else {
            this.throwDTDUnexpectedChar(c, ErrorConsts.ERR_DTD_MAINLEVEL_KEYWORD);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void parseDirectiveFlattened() throws XMLStreamException {
        this.mFlattenWriter.flush(this.mInputBuffer, this.mInputPtr - 1);
        this.mFlattenWriter.disableOutput();
        char c = this.dtdNextFromCurr();
        if (c == '?') {
            this.mFlattenWriter.enableOutput(this.mInputPtr);
            this.mFlattenWriter.output("<?");
            this.readPI();
            return;
        }
        if (c != '!') {
            this.throwDTDUnexpectedChar(c, ErrorConsts.ERR_DTD_MAINLEVEL_KEYWORD);
        }
        if ((c = this.dtdNextFromCurr()) == '-') {
            boolean comm;
            c = this.dtdNextFromCurr();
            if (c != '-') {
                this.throwDTDUnexpectedChar(c, "; expected '-' for a comment");
            }
            if (comm = this.mFlattenWriter.includeComments()) {
                this.mFlattenWriter.enableOutput(this.mInputPtr);
                this.mFlattenWriter.output("<!--");
            }
            try {
                this.skipComment();
            }
            finally {
                if (!comm) {
                    this.mFlattenWriter.enableOutput(this.mInputPtr);
                }
            }
        } else if (c == '[') {
            boolean cond = this.mFlattenWriter.includeConditionals();
            if (cond) {
                this.mFlattenWriter.enableOutput(this.mInputPtr);
                this.mFlattenWriter.output("<![");
            }
            try {
                this.checkInclusion();
            }
            finally {
                if (!cond) {
                    this.mFlattenWriter.enableOutput(this.mInputPtr);
                }
            }
        } else {
            boolean filterPEs;
            boolean bl = filterPEs = c == 'E' && !this.mFlattenWriter.includeParamEntities();
            if (filterPEs) {
                this.handleSuppressedDeclaration();
            } else if (c >= 'A' && c <= 'Z') {
                this.mFlattenWriter.enableOutput(this.mInputPtr);
                this.mFlattenWriter.output("<!");
                this.mFlattenWriter.output(c);
                this.handleDeclaration(c);
            } else {
                this.throwDTDUnexpectedChar(c, ErrorConsts.ERR_DTD_MAINLEVEL_KEYWORD);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void initInputSource(WstxInputSource newInput, boolean isExt, String entityId) throws XMLStreamException {
        if (this.mFlattenWriter != null) {
            this.mFlattenWriter.flush(this.mInputBuffer, this.mInputPtr);
            this.mFlattenWriter.disableOutput();
            try {
                super.initInputSource(newInput, isExt, entityId);
            }
            finally {
                this.mFlattenWriter.enableOutput(this.mInputPtr);
            }
        } else {
            super.initInputSource(newInput, isExt, entityId);
        }
    }

    protected boolean loadMore() throws XMLStreamException {
        WstxInputSource input = this.mInput;
        if (this.mFlattenWriter != null) {
            this.mFlattenWriter.flush(this.mInputBuffer, this.mInputEnd);
        }
        do {
            this.mCurrInputProcessed += (long)this.mInputEnd;
            this.mCurrInputRowStart -= this.mInputEnd;
            try {
                int count = input.readInto(this);
                if (count > 0) {
                    if (this.mFlattenWriter != null) {
                        this.mFlattenWriter.setFlattenStart(this.mInputPtr);
                    }
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
            if (this.mFlattenWriter != null) {
                this.mFlattenWriter.setFlattenStart(this.mInputPtr);
            }
            this.mInputTopDepth = input.getScopeId();
            if (this.mNormalizeLFs) continue;
            boolean bl = this.mNormalizeLFs = !input.fromInternalEntity();
        } while (this.mInputPtr >= this.mInputEnd);
        return true;
    }

    protected boolean loadMoreFromCurrent() throws XMLStreamException {
        if (this.mFlattenWriter != null) {
            this.mFlattenWriter.flush(this.mInputBuffer, this.mInputEnd);
        }
        this.mCurrInputProcessed += (long)this.mInputEnd;
        this.mCurrInputRowStart -= this.mInputEnd;
        try {
            int count = this.mInput.readInto(this);
            if (count > 0) {
                if (this.mFlattenWriter != null) {
                    this.mFlattenWriter.setFlattenStart(this.mInputPtr);
                }
                return true;
            }
        }
        catch (IOException ie) {
            this.throwFromIOE(ie);
        }
        return false;
    }

    protected boolean ensureInput(int minAmount) throws XMLStreamException {
        int currAmount = this.mInputEnd - this.mInputPtr;
        if (currAmount >= minAmount) {
            return true;
        }
        if (this.mFlattenWriter != null) {
            this.mFlattenWriter.flush(this.mInputBuffer, this.mInputEnd);
        }
        try {
            if (this.mInput.readMore(this, minAmount)) {
                if (this.mFlattenWriter != null) {
                    this.mFlattenWriter.setFlattenStart(currAmount);
                }
                return true;
            }
        }
        catch (IOException ie) {
            this.throwFromIOE(ie);
        }
        return false;
    }

    private void loadMoreScoped(WstxInputSource currScope, String entityName, Location loc) throws XMLStreamException {
        boolean check = this.mInput == currScope;
        this.loadMore(this.getErrorMsg());
        if (check && this.mInput != currScope) {
            this._reportWFCViolation("Unterminated entity value for entity '" + entityName + "' (definition started at " + loc + ")");
        }
    }

    private char dtdNextIfAvailable() throws XMLStreamException {
        char c;
        if (this.mInputPtr < this.mInputEnd) {
            c = this.mInputBuffer[this.mInputPtr++];
        } else {
            int i = this.peekNext();
            if (i < 0) {
                return '\u0000';
            }
            ++this.mInputPtr;
            c = (char)i;
        }
        if (c == '\u0000') {
            this.throwNullChar();
        }
        return c;
    }

    private char getNextExpanded() throws XMLStreamException {
        while (true) {
            char c;
            char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(this.getErrorMsg());
            if (c != '%') {
                return c;
            }
            this.expandPE();
        }
    }

    private char skipDtdWs(boolean handlePEs) throws XMLStreamException {
        while (true) {
            char c;
            char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(this.getErrorMsg());
            if (c > ' ') {
                if (c == '%' && handlePEs) {
                    this.expandPE();
                    continue;
                }
                return c;
            }
            if (c == '\n' || c == '\r') {
                this.skipCRLF(c);
                continue;
            }
            if (c == ' ' || c == '\t') continue;
            this.throwInvalidSpace(c);
        }
    }

    private char skipObligatoryDtdWs() throws XMLStreamException {
        char c;
        int i = this.peekNext();
        if (i == -1) {
            c = this.getNextChar(this.getErrorMsg());
            if (c > ' ' && c != '%') {
                return c;
            }
        } else if ((c = this.mInputBuffer[this.mInputPtr++]) > ' ' && c != '%') {
            this.throwDTDUnexpectedChar(c, "; expected a separating white space");
        }
        while (true) {
            if (c == '%') {
                this.expandPE();
            } else {
                if (c > ' ') break;
                if (c == '\n' || c == '\r') {
                    this.skipCRLF(c);
                } else if (c != ' ' && c != '\t') {
                    this.throwInvalidSpace(c);
                }
            }
            c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(this.getErrorMsg());
        }
        return c;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void expandPE() throws XMLStreamException {
        String id;
        char c;
        if (this.mCheckForbiddenPEs) {
            this.throwForbiddenPE();
        }
        if (this.mFlattenWriter != null) {
            this.mFlattenWriter.flush(this.mInputBuffer, this.mInputPtr - 1);
            this.mFlattenWriter.disableOutput();
            c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
            id = this.readDTDName(c);
            try {
                c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
            }
            finally {
                this.mFlattenWriter.enableOutput(this.mInputPtr);
            }
        } else {
            c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
            id = this.readDTDName(c);
            char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
        }
        if (c != ';') {
            this.throwDTDUnexpectedChar(c, "; expected ';' to end parameter entity name");
        }
        this.mExpandingPE = true;
        this.expandEntity(id, true, ENTITY_EXP_PE);
    }

    protected String checkDTDKeyword(String exp) throws XMLStreamException {
        int i;
        int len = exp.length();
        char c = ' ';
        for (i = 0; i < len; ++i) {
            if (this.mInputPtr < this.mInputEnd) {
                c = this.mInputBuffer[this.mInputPtr++];
            } else {
                c = this.dtdNextIfAvailable();
                if (c == '\u0000') {
                    return exp.substring(0, i);
                }
            }
            if (c != exp.charAt(i)) break;
        }
        if (i == len) {
            c = this.dtdNextIfAvailable();
            if (c == '\u0000') {
                return null;
            }
            if (!this.isNameChar(c)) {
                --this.mInputPtr;
                return null;
            }
        }
        StringBuffer sb = new StringBuffer(exp.substring(0, i));
        sb.append(c);
        while ((c = this.dtdNextIfAvailable()) != '\u0000') {
            if (!this.isNameChar(c) && c != ':') {
                --this.mInputPtr;
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    protected String readDTDKeyword(String prefix) throws XMLStreamException {
        StringBuffer sb = new StringBuffer(prefix);
        while (true) {
            char c;
            if (this.mInputPtr < this.mInputEnd) {
                c = this.mInputBuffer[this.mInputPtr++];
            } else {
                c = this.dtdNextIfAvailable();
                if (c == '\u0000') break;
            }
            if (!this.isNameChar(c) && c != ':') {
                --this.mInputPtr;
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private boolean checkPublicSystemKeyword(char c) throws XMLStreamException {
        String errId;
        if (c == 'P') {
            errId = this.checkDTDKeyword("UBLIC");
            if (errId == null) {
                return true;
            }
            errId = "P" + errId;
        } else if (c == 'S') {
            errId = this.checkDTDKeyword("YSTEM");
            if (errId == null) {
                return false;
            }
            errId = "S" + errId;
        } else {
            if (!this.isNameStartChar(c)) {
                this.throwDTDUnexpectedChar(c, "; expected 'PUBLIC' or 'SYSTEM' keyword");
            }
            errId = this.readDTDKeyword(String.valueOf(c));
        }
        this._reportWFCViolation("Unrecognized keyword '" + errId + "'; expected 'PUBLIC' or 'SYSTEM'");
        return false;
    }

    private String readDTDName(char c) throws XMLStreamException {
        if (!this.isNameStartChar(c)) {
            this.throwDTDUnexpectedChar(c, "; expected an identifier");
        }
        return this.parseFullName(c);
    }

    private String readDTDLocalName(char c, boolean checkChar) throws XMLStreamException {
        if (checkChar && !this.isNameStartChar(c)) {
            this.throwDTDUnexpectedChar(c, "; expected an identifier");
        }
        return this.parseLocalName(c);
    }

    private String readDTDNmtoken(char c) throws XMLStreamException {
        char[] outBuf = this.getNameBuffer(64);
        int outLen = outBuf.length;
        int outPtr = 0;
        while (true) {
            if (!this.isNameChar(c) && c != ':') {
                if (outPtr == 0) {
                    this.throwDTDUnexpectedChar(c, "; expected a NMTOKEN character to start a NMTOKEN");
                }
                --this.mInputPtr;
                break;
            }
            if (outPtr >= outLen) {
                outBuf = this.expandBy50Pct(outBuf);
                outLen = outBuf.length;
            }
            outBuf[outPtr++] = c;
            if (this.mInputPtr < this.mInputEnd) {
                c = this.mInputBuffer[this.mInputPtr++];
                continue;
            }
            c = this.dtdNextIfAvailable();
            if (c == '\u0000') break;
        }
        return new String(outBuf, 0, outPtr);
    }

    private PrefixedName readDTDQName(char firstChar) throws XMLStreamException {
        String localName;
        String prefix;
        if (!this.mCfgNsEnabled) {
            prefix = null;
            localName = this.parseFullName(firstChar);
        } else {
            localName = this.parseLocalName(firstChar);
            char c = this.dtdNextIfAvailable();
            if (c == '\u0000') {
                prefix = null;
            } else if (c == ':') {
                prefix = localName;
                c = this.dtdNextFromCurr();
                localName = this.parseLocalName(c);
            } else {
                prefix = null;
                --this.mInputPtr;
            }
        }
        return this.findSharedName(prefix, localName);
    }

    private char readArity() throws XMLStreamException {
        char c;
        char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(this.getErrorMsg());
        if (c == '?' || c == '*' || c == '+') {
            return c;
        }
        --this.mInputPtr;
        return ' ';
    }

    /*
     * Enabled aggressive block sorting
     */
    private char[] parseEntityValue(String id, Location loc, char quoteChar) throws XMLStreamException {
        WstxInputSource currScope = this.mInput;
        TextBuffer tb = this.mValueBuffer;
        if (tb == null) {
            tb = TextBuffer.createTemporaryBuffer();
        }
        tb.resetInitialized();
        char[] outBuf = tb.getCurrentSegment();
        int outPtr = tb.getCurrentSegmentSize();
        while (true) {
            char c;
            block21: {
                block26: {
                    block23: {
                        block24: {
                            block28: {
                                block27: {
                                    block25: {
                                        block22: {
                                            if (this.mInputPtr >= this.mInputEnd) {
                                                this.loadMoreScoped(currScope, id, loc);
                                            }
                                            if ((c = this.mInputBuffer[this.mInputPtr++]) >= '?') break block21;
                                            if (c != quoteChar) break block22;
                                            if (this.mInput != currScope) break block21;
                                            tb.setCurrentLength(outPtr);
                                            c = this.skipDtdWs(true);
                                            if (c == '>') break block23;
                                            break block24;
                                        }
                                        if (c != '&') break block25;
                                        int d = this.resolveCharOnlyEntity(false);
                                        if (d == 0) break block26;
                                        if (d <= 65535) {
                                            c = (char)d;
                                            break block21;
                                        } else {
                                            if (outPtr >= outBuf.length) {
                                                outBuf = tb.finishCurrentSegment();
                                                outPtr = 0;
                                            }
                                            outBuf[outPtr++] = (char)(((d -= 65536) >> 10) + 55296);
                                            c = (char)((d & 0x3FF) + 56320);
                                        }
                                        break block21;
                                    }
                                    if (c == '%') {
                                        this.expandPE();
                                        continue;
                                    }
                                    if (c >= ' ') break block21;
                                    if (c != '\n') break block27;
                                    this.markLF();
                                    break block21;
                                }
                                if (c != '\r') break block28;
                                if (this.skipCRLF(c)) {
                                    if (!this.mNormalizeLFs) {
                                        if (outPtr >= outBuf.length) {
                                            outBuf = tb.finishCurrentSegment();
                                            outPtr = 0;
                                        }
                                        outBuf[outPtr++] = c;
                                    }
                                    c = '\n';
                                    break block21;
                                } else if (this.mNormalizeLFs) {
                                    c = '\n';
                                }
                                break block21;
                            }
                            if (c != '\t') {
                                this.throwInvalidSpace(c);
                            }
                            break block21;
                        }
                        this.throwDTDUnexpectedChar(c, "; expected closing '>' after ENTITY declaration");
                    }
                    char[] result = tb.contentsAsArray();
                    this.mValueBuffer = tb;
                    return result;
                }
                boolean first = true;
                while (true) {
                    if (outPtr >= outBuf.length) {
                        outBuf = tb.finishCurrentSegment();
                        outPtr = 0;
                    }
                    outBuf[outPtr++] = c;
                    if (this.mInputPtr >= this.mInputEnd) {
                        this.loadMoreScoped(currScope, id, loc);
                    }
                    if ((c = this.mInputBuffer[this.mInputPtr++]) == ';') break;
                    if (first) {
                        first = false;
                        if (this.isNameStartChar(c)) {
                            continue;
                        }
                    } else if (this.isNameChar(c)) continue;
                    if (c == ':' && !this.mCfgNsEnabled) continue;
                    if (first) {
                        this.throwDTDUnexpectedChar(c, "; expected entity name after '&'");
                    }
                    this.throwDTDUnexpectedChar(c, "; expected semi-colon after entity name");
                }
            }
            if (outPtr >= outBuf.length) {
                outBuf = tb.finishCurrentSegment();
                outPtr = 0;
            }
            outBuf[outPtr++] = c;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void parseAttrDefaultValue(DefaultAttrValue defVal, char quoteChar, PrefixedName attrName, Location loc, boolean gotFixed) throws XMLStreamException {
        if (quoteChar != '\"' && quoteChar != '\'') {
            String msg = "; expected a single or double quote to enclose the default value";
            if (!gotFixed) {
                msg = msg + ", or one of keywords (#REQUIRED, #IMPLIED, #FIXED)";
            }
            msg = msg + " (for attribute '" + attrName + "')";
            this.throwDTDUnexpectedChar(quoteChar, msg);
        }
        WstxInputSource currScope = this.mInput;
        TextBuffer tb = this.mValueBuffer;
        if (tb == null) {
            tb = TextBuffer.createTemporaryBuffer();
        }
        tb.resetInitialized();
        int outPtr = 0;
        char[] outBuf = tb.getCurrentSegment();
        int outLen = outBuf.length;
        while (true) {
            char c;
            if (this.mInputPtr >= this.mInputEnd) {
                boolean check = this.mInput == currScope;
                this.loadMore(this.getErrorMsg());
                if (check && this.mInput != currScope) {
                    this._reportWFCViolation("Unterminated attribute default value for attribute '" + attrName + "' (definition started at " + loc + ")");
                }
            }
            if ((c = this.mInputBuffer[this.mInputPtr++]) < '?') {
                if (c <= ' ') {
                    if (c == '\n') {
                        this.markLF();
                    } else if (c == '\r') {
                        c = this.getNextChar(" in attribute default value");
                        if (c != '\n') {
                            --this.mInputPtr;
                            c = this.mNormalizeLFs ? (char)'\n' : '\r';
                        }
                        this.markLF();
                    } else if (c != ' ' && c != '\t') {
                        this.throwInvalidSpace(c);
                    }
                    c = ' ';
                } else if (c == quoteChar) {
                    if (this.mInput == currScope) {
                        break;
                    }
                } else if (c == '&') {
                    int d = this.inputInBuffer() >= 3 ? this.resolveSimpleEntity(true) : this.resolveCharOnlyEntity(true);
                    if (d == 0) {
                        c = this.getNextChar(" in entity reference");
                        String id = this.parseEntityName(c);
                        try {
                            this.mCurrAttrDefault = defVal;
                            this.mExpandingPE = false;
                            this.expandEntity(id, false, ENTITY_EXP_GE);
                        }
                        finally {
                            this.mCurrAttrDefault = null;
                        }
                        continue;
                    }
                    if (c > '\uffff') {
                        if (d <= 65535) {
                            c = (char)d;
                        } else {
                            if (outPtr >= outBuf.length) {
                                outBuf = tb.finishCurrentSegment();
                                outPtr = 0;
                            }
                            outBuf[outPtr++] = (char)(((d -= 65536) >> 10) + 55296);
                            c = (char)((d & 0x3FF) + 56320);
                        }
                    }
                } else if (c == '<') {
                    this.throwDTDUnexpectedChar(c, " in attribute default value");
                }
            }
            if (outPtr >= outLen) {
                outBuf = tb.finishCurrentSegment();
                outPtr = 0;
                outLen = outBuf.length;
            }
            outBuf[outPtr++] = c;
        }
        tb.setCurrentLength(outPtr);
        defVal.setValue(tb.contentsAsString());
        this.mValueBuffer = tb;
    }

    protected void readPI() throws XMLStreamException {
        block27: {
            int c;
            String target = this.parseFullName();
            if (target.length() == 0) {
                this._reportWFCViolation(ErrorConsts.ERR_WF_PI_MISSING_TARGET);
            }
            if (target.equalsIgnoreCase("xml")) {
                this._reportWFCViolation(ErrorConsts.ERR_WF_PI_XML_TARGET, target);
            }
            if (!FullDTDReader.isSpaceChar((char)(c = this.dtdNextFromCurr()))) {
                if (c != 63 || this.dtdNextFromCurr() != '>') {
                    this.throwUnexpectedChar(c, ErrorConsts.ERR_WF_PI_XML_MISSING_SPACE);
                }
                if (this.mEventListener != null) {
                    this.mEventListener.dtdProcessingInstruction(target, "");
                }
            } else {
                if (this.mEventListener == null) {
                    while (true) {
                        int n = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
                        if (c == 63) {
                            while ((c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr()) == 63) {
                            }
                            if (c == 62) break block27;
                        }
                        if (c >= 32) continue;
                        if (c == 10 || c == 13) {
                            this.skipCRLF((char)c);
                            continue;
                        }
                        if (c == 9) continue;
                        this.throwInvalidSpace(c);
                    }
                }
                while (c <= 32) {
                    if (c == 10 || c == 13) {
                        this.skipCRLF((char)c);
                    } else if (c != 9 && c != 32) {
                        this.throwInvalidSpace(c);
                    }
                    c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
                }
                TextBuffer tb = this.getTextBuffer();
                char[] outBuf = tb.getCurrentSegment();
                int outPtr = 0;
                while (true) {
                    if (c == 63) {
                        while (true) {
                            int n = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
                            if (c != 63) break;
                            if (outPtr >= outBuf.length) {
                                outBuf = tb.finishCurrentSegment();
                                outPtr = 0;
                            }
                            outBuf[outPtr++] = c;
                        }
                        if (c == 62) break;
                        --this.mInputPtr;
                        c = 63;
                    } else if (c < 32) {
                        if (c == 10 || c == 13) {
                            this.skipCRLF((char)c);
                            c = 10;
                        } else if (c != 9) {
                            this.throwInvalidSpace(c);
                        }
                    }
                    if (outPtr >= outBuf.length) {
                        outBuf = tb.finishCurrentSegment();
                        outPtr = 0;
                    }
                    outBuf[outPtr++] = c;
                    c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
                }
                tb.setCurrentLength(outPtr);
                String data = tb.contentsAsString();
                this.mEventListener.dtdProcessingInstruction(target, data);
            }
        }
    }

    protected void readComment(DTDEventListener l) throws XMLStreamException {
        TextBuffer tb = this.getTextBuffer();
        char[] outBuf = tb.getCurrentSegment();
        int outPtr = 0;
        while (true) {
            int c;
            int n = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
            if (c < 32) {
                if (c == 10 || c == 13) {
                    this.skipCRLF((char)c);
                    c = 10;
                } else if (c != 9) {
                    this.throwInvalidSpace(c);
                }
            } else if (c == 45) {
                c = this.dtdNextFromCurr();
                if (c == 45) {
                    c = this.dtdNextFromCurr();
                    if (c == 62) break;
                    this.throwParseError(ErrorConsts.ERR_HYPHENS_IN_COMMENT);
                    break;
                }
                c = 45;
                --this.mInputPtr;
            }
            if (outPtr >= outBuf.length) {
                outBuf = tb.finishCurrentSegment();
                outPtr = 0;
            }
            outBuf[outPtr++] = c;
        }
        tb.setCurrentLength(outPtr);
        tb.fireDtdCommentEvent(l);
    }

    private void checkInclusion() throws XMLStreamException {
        String keyword;
        char c;
        if (!this.mIsExternal && this.mInput == this.mRootInput) {
            this._reportWFCViolation("Internal DTD subset can not use (INCLUDE/IGNORE) directives (except via external entities)");
        }
        if ((c = this.skipDtdWs(true)) != 'I') {
            keyword = this.readDTDKeyword(String.valueOf(c));
        } else {
            c = this.dtdNextFromCurr();
            if (c == 'G') {
                keyword = this.checkDTDKeyword("NORE");
                if (keyword == null) {
                    this.handleIgnored();
                    return;
                }
                keyword = "IG" + keyword;
            } else if (c == 'N') {
                keyword = this.checkDTDKeyword("CLUDE");
                if (keyword == null) {
                    this.handleIncluded();
                    return;
                }
                keyword = "IN" + keyword;
            } else {
                --this.mInputPtr;
                keyword = this.readDTDKeyword("I");
            }
        }
        this._reportWFCViolation("Unrecognized directive '" + keyword + "'; expected either 'IGNORE' or 'INCLUDE'");
    }

    private void handleIncluded() throws XMLStreamException {
        char c = this.skipDtdWs(false);
        if (c != '[') {
            this.throwDTDUnexpectedChar(c, "; expected '[' to follow 'INCLUDE' directive");
        }
        ++this.mIncludeCount;
    }

    private void handleIgnored() throws XMLStreamException {
        char c = this.skipDtdWs(false);
        int count = 1;
        if (c != '[') {
            this.throwDTDUnexpectedChar(c, "; expected '[' to follow 'IGNORE' directive");
        }
        String errorMsg = this.getErrorMsg();
        while (true) {
            char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(errorMsg);
            if (c < ' ') {
                if (c == '\n' || c == '\r') {
                    this.skipCRLF(c);
                    continue;
                }
                if (c == '\t') continue;
                this.throwInvalidSpace(c);
                continue;
            }
            if (c == ']') {
                if (this.getNextChar(errorMsg) == ']' && this.getNextChar(errorMsg) == '>') {
                    if (--count >= 1) continue;
                    return;
                }
                --this.mInputPtr;
                continue;
            }
            if (c != '<') continue;
            if (this.getNextChar(errorMsg) == '!' && this.getNextChar(errorMsg) == '[') {
                ++count;
                continue;
            }
            --this.mInputPtr;
        }
    }

    private void _reportUndefinedNotationRefs() throws XMLStreamException {
        int count = this.mNotationForwardRefs.size();
        String id = (String)this.mNotationForwardRefs.keySet().iterator().next();
        String msg = "" + count + " referenced notation" + (count == 1 ? "" : "s") + " undefined: first one '" + id + "'";
        this._reportVCViolation(msg);
    }

    private void _reportBadDirective(String dir) throws XMLStreamException {
        String msg = "Unrecognized DTD directive '<!" + dir + " >'; expected ATTLIST, ELEMENT, ENTITY or NOTATION";
        if (this.mCfgSupportDTDPP) {
            msg = msg + " (or, for DTD++, TARGETNS)";
        }
        this._reportWFCViolation(msg);
    }

    private void _reportVCViolation(String msg) throws XMLStreamException {
        if (this.mCfgFullyValidating) {
            this.reportValidationProblem(msg, 2);
        } else {
            this.reportValidationProblem(msg, 1);
        }
    }

    private void _reportWFCViolation(String msg) throws XMLStreamException {
        this.throwParseError(msg);
    }

    private void _reportWFCViolation(String format, Object arg) throws XMLStreamException {
        this.throwParseError(format, arg, null);
    }

    private void throwDTDElemError(String msg, Object elem) throws XMLStreamException {
        this._reportWFCViolation(this.elemDesc(elem) + ": " + msg);
    }

    private void throwDTDAttrError(String msg, DTDElement elem, PrefixedName attrName) throws XMLStreamException {
        this._reportWFCViolation(this.attrDesc(elem, attrName) + ": " + msg);
    }

    private void throwDTDUnexpectedChar(int i, String extraMsg) throws XMLStreamException {
        if (extraMsg == null) {
            this.throwUnexpectedChar(i, this.getErrorMsg());
        }
        this.throwUnexpectedChar(i, this.getErrorMsg() + extraMsg);
    }

    private void throwForbiddenPE() throws XMLStreamException {
        this._reportWFCViolation("Can not have parameter entities in the internal subset, except for defining complete declarations (XML 1.0, #2.8, WFC 'PEs In Internal Subset')");
    }

    private String elemDesc(Object elem) {
        return "Element <" + elem + ">)";
    }

    private String attrDesc(Object elem, PrefixedName attrName) {
        return "Attribute '" + attrName + "' (of element <" + elem + ">)";
    }

    private String entityDesc(WstxInputSource input) {
        return "Entity &" + input.getEntityId() + ";";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleDeclaration(char c) throws XMLStreamException {
        String keyw = null;
        this.mCurrDepth = 1;
        try {
            block7: {
                block8: {
                    block16: {
                        block17: {
                            block14: {
                                block15: {
                                    block9: {
                                        block12: {
                                            block13: {
                                                block10: {
                                                    block11: {
                                                        block5: {
                                                            block6: {
                                                                if (c != 'A') break block5;
                                                                keyw = this.checkDTDKeyword("TTLIST");
                                                                if (keyw != null) break block6;
                                                                this.mCurrDeclaration = "ATTLIST";
                                                                this.handleAttlistDecl();
                                                                break block7;
                                                            }
                                                            keyw = "A" + keyw;
                                                            break block8;
                                                        }
                                                        if (c != 'E') break block9;
                                                        c = this.dtdNextFromCurr();
                                                        if (c != 'N') break block10;
                                                        keyw = this.checkDTDKeyword("TITY");
                                                        if (keyw != null) break block11;
                                                        this.mCurrDeclaration = "ENTITY";
                                                        this.handleEntityDecl(false);
                                                        break block7;
                                                    }
                                                    keyw = "EN" + keyw;
                                                    break block8;
                                                }
                                                if (c != 'L') break block12;
                                                keyw = this.checkDTDKeyword("EMENT");
                                                if (keyw != null) break block13;
                                                this.mCurrDeclaration = "ELEMENT";
                                                this.handleElementDecl();
                                                break block7;
                                            }
                                            keyw = "EL" + keyw;
                                            break block8;
                                        }
                                        keyw = this.readDTDKeyword("E");
                                        break block8;
                                    }
                                    if (c != 'N') break block14;
                                    keyw = this.checkDTDKeyword("OTATION");
                                    if (keyw != null) break block15;
                                    this.mCurrDeclaration = "NOTATION";
                                    this.handleNotationDecl();
                                    break block7;
                                }
                                keyw = "N" + keyw;
                                break block8;
                            }
                            if (c != 'T' || !this.mCfgSupportDTDPP) break block16;
                            keyw = this.checkDTDKeyword("ARGETNS");
                            if (keyw != null) break block17;
                            this.mCurrDeclaration = "TARGETNS";
                            this.handleTargetNsDecl();
                            break block7;
                        }
                        keyw = "T" + keyw;
                        break block8;
                    }
                    keyw = this.readDTDKeyword(String.valueOf(c));
                }
                this._reportBadDirective(keyw);
            }
            if (this.mInput.getScopeId() > 0) {
                this.handleGreedyEntityProblem(this.mInput);
            }
        }
        finally {
            this.mCurrDepth = 0;
            this.mCurrDeclaration = null;
        }
    }

    private void handleSuppressedDeclaration() throws XMLStreamException {
        String keyw;
        char c = this.dtdNextFromCurr();
        if (c == 'N') {
            keyw = this.checkDTDKeyword("TITY");
            if (keyw == null) {
                this.handleEntityDecl(true);
                return;
            }
            keyw = "EN" + keyw;
            this.mFlattenWriter.enableOutput(this.mInputPtr);
        } else {
            this.mFlattenWriter.enableOutput(this.mInputPtr);
            this.mFlattenWriter.output("<!E");
            this.mFlattenWriter.output(c);
            if (c == 'L') {
                keyw = this.checkDTDKeyword("EMENT");
                if (keyw == null) {
                    this.handleElementDecl();
                    return;
                }
                keyw = "EL" + keyw;
            } else {
                keyw = this.readDTDKeyword("E");
            }
        }
        this._reportBadDirective(keyw);
    }

    private void handleAttlistDecl() throws XMLStreamException {
        char c = this.skipObligatoryDtdWs();
        PrefixedName elemName = this.readDTDQName(c);
        XMLStreamLocation2 loc = this.getStartLocation();
        HashMap m = this.getElementMap();
        DTDElement elem = (DTDElement)m.get(elemName);
        if (elem == null) {
            elem = DTDElement.createPlaceholder(this.mConfig, loc, elemName);
            m.put(elemName, elem);
        }
        int index = 0;
        while (true) {
            if (FullDTDReader.isSpaceChar(c = this.getNextExpanded())) {
                --this.mInputPtr;
                c = this.skipDtdWs(true);
            }
            if (c == '>') break;
            this.handleAttrDecl(elem, c, index, loc);
            ++index;
        }
    }

    private void handleElementDecl() throws XMLStreamException {
        HashMap m;
        DTDElement oldElem;
        int vldContent;
        StructValidator val;
        XMLStreamLocation2 loc;
        PrefixedName elemName;
        char c;
        block10: {
            block11: {
                String keyw;
                block14: {
                    block15: {
                        block16: {
                            block12: {
                                block13: {
                                    block9: {
                                        c = this.skipObligatoryDtdWs();
                                        elemName = this.readDTDQName(c);
                                        loc = this.getStartLocation();
                                        c = this.skipObligatoryDtdWs();
                                        val = null;
                                        vldContent = 4;
                                        if (c != '(') break block9;
                                        c = this.skipDtdWs(true);
                                        if (c == '#') {
                                            val = this.readMixedSpec(elemName, this.mCfgFullyValidating);
                                            vldContent = 4;
                                        } else {
                                            --this.mInputPtr;
                                            ContentSpec spec = this.readContentSpec(elemName, true, this.mCfgFullyValidating);
                                            val = spec.getSimpleValidator();
                                            if (val == null) {
                                                val = new DFAValidator(DFAState.constructDFA(spec));
                                            }
                                            vldContent = 1;
                                        }
                                        break block10;
                                    }
                                    if (!this.isNameStartChar(c)) break block11;
                                    keyw = null;
                                    if (c != 'A') break block12;
                                    keyw = this.checkDTDKeyword("NY");
                                    if (keyw != null) break block13;
                                    val = null;
                                    vldContent = 4;
                                    break block10;
                                }
                                keyw = "A" + keyw;
                                break block14;
                            }
                            if (c != 'E') break block15;
                            keyw = this.checkDTDKeyword("MPTY");
                            if (keyw != null) break block16;
                            val = EmptyValidator.getPcdataInstance();
                            vldContent = 0;
                            break block10;
                        }
                        keyw = "E" + keyw;
                        break block14;
                    }
                    --this.mInputPtr;
                    keyw = this.readDTDKeyword(String.valueOf(c));
                }
                this._reportWFCViolation("Unrecognized DTD content spec keyword '" + keyw + "' (for element <" + elemName + ">); expected ANY or EMPTY");
                break block10;
            }
            this.throwDTDUnexpectedChar(c, ": excepted '(' to start content specification for element <" + elemName + ">");
        }
        c = this.skipDtdWs(true);
        if (c != '>') {
            this.throwDTDUnexpectedChar(c, "; expected '>' to finish the element declaration for <" + elemName + ">");
        }
        if ((oldElem = (DTDElement)(m = this.getElementMap()).get(elemName)) != null) {
            if (oldElem.isDefined()) {
                if (this.mCfgFullyValidating) {
                    DTDSubsetImpl.throwElementException(oldElem, loc);
                } else {
                    return;
                }
            }
            oldElem = oldElem.define(loc, val, vldContent);
        } else {
            oldElem = DTDElement.createDefined(this.mConfig, loc, elemName, val, vldContent);
        }
        m.put(elemName, oldElem);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleEntityDecl(boolean suppressPEDecl) throws XMLStreamException {
        Object old;
        HashMap m;
        EntityDecl ent;
        char c = this.dtdNextFromCurr();
        boolean gotSeparator = false;
        boolean isParam = false;
        while (true) {
            if (c == '%') {
                char d = this.dtdNextIfAvailable();
                if (d == '\u0000' || FullDTDReader.isSpaceChar(d)) {
                    isParam = true;
                    if (d != '\n' && c != '\r') break;
                    this.skipCRLF(d);
                    break;
                }
                if (!this.isNameStartChar(d)) {
                    this.throwDTDUnexpectedChar(d, "; expected a space (for PE declaration) or PE reference name");
                }
                --this.mInputPtr;
                gotSeparator = true;
                this.expandPE();
                c = this.dtdNextChar();
                continue;
            }
            if (!FullDTDReader.isSpaceChar(c)) break;
            gotSeparator = true;
            c = this.dtdNextFromCurr();
        }
        if (!gotSeparator) {
            this.throwDTDUnexpectedChar(c, "; expected a space separating ENTITY keyword and entity name");
        }
        if (isParam) {
            c = this.skipDtdWs(true);
        }
        if (suppressPEDecl && !isParam) {
            this.mFlattenWriter.enableOutput(this.mInputPtr);
            this.mFlattenWriter.output("<!ENTITY ");
            this.mFlattenWriter.output(c);
        }
        String id = this.readDTDName(c);
        XMLStreamLocation2 evtLoc = this.getStartLocation();
        try {
            c = this.skipObligatoryDtdWs();
            if (c == '\'' || c == '\"') {
                this.dtdNextFromCurr();
                WstxInputLocation contentLoc = this.getLastCharLocation();
                --this.mInputPtr;
                char[] contents = this.parseEntityValue(id, contentLoc, c);
                try {
                    ent = new IntEntity(evtLoc, id, this.getSource(), contents, contentLoc);
                }
                catch (IOException e) {
                    throw new WstxIOException(e);
                }
            } else {
                if (!this.isNameStartChar(c)) {
                    this.throwDTDUnexpectedChar(c, "; expected either quoted value, or keyword 'PUBLIC' or 'SYSTEM'");
                }
                ent = this.handleExternalEntityDecl(this.mInput, isParam, id, c, evtLoc);
            }
            if (this.mIsExternal) {
                ent.markAsExternallyDeclared();
            }
        }
        finally {
            if (suppressPEDecl && isParam) {
                this.mFlattenWriter.enableOutput(this.mInputPtr);
            }
        }
        if (isParam) {
            m = this.mParamEntities;
            if (m == null) {
                this.mParamEntities = m = new HashMap();
            }
        } else {
            m = this.mGeneralEntities;
            if (m == null) {
                m = new LinkedHashMap();
                this.mGeneralEntities = m;
            }
        }
        if (m.size() > 0 && (old = m.get(id)) != null) {
            XMLReporter rep = this.mConfig.getXMLReporter();
            if (rep != null) {
                EntityDecl oldED = (EntityDecl)old;
                String str = " entity '" + id + "' defined more than once: first declaration at " + oldED.getLocation();
                str = isParam ? "Parameter" + str : "General" + str;
                this._reportWarning(rep, ErrorConsts.WT_ENT_DECL, str, evtLoc);
            }
        } else {
            m.put(id, ent);
        }
        if (this.mEventListener != null && !ent.isParsed()) {
            URL src;
            try {
                src = this.mInput.getSource();
            }
            catch (IOException e) {
                throw new WstxIOException(e);
            }
            this.mEventListener.dtdUnparsedEntityDecl(id, ent.getPublicId(), ent.getSystemId(), ent.getNotationName(), src);
        }
    }

    private void handleNotationDecl() throws XMLStreamException {
        LinkedHashMap<String, WNotationDeclaration> m;
        NotationDeclaration oldDecl;
        URL baseURL;
        String sysId;
        String pubId;
        char c = this.skipObligatoryDtdWs();
        String id = this.readDTDName(c);
        c = this.skipObligatoryDtdWs();
        boolean isPublic = this.checkPublicSystemKeyword(c);
        c = this.skipObligatoryDtdWs();
        if (isPublic) {
            if (c != '\"' && c != '\'') {
                this.throwDTDUnexpectedChar(c, "; expected a quote to start the public identifier");
            }
            pubId = this.parsePublicId(c, this.getErrorMsg());
            c = this.skipDtdWs(true);
        } else {
            pubId = null;
        }
        if (c == '\"' || c == '\'') {
            sysId = this.parseSystemId(c, this.mNormalizeLFs, this.getErrorMsg());
            c = this.skipDtdWs(true);
        } else {
            if (!isPublic) {
                this.throwDTDUnexpectedChar(c, "; expected a quote to start the system identifier");
            }
            sysId = null;
        }
        if (c != '>') {
            this.throwDTDUnexpectedChar(c, "; expected closing '>' after NOTATION declaration");
        }
        try {
            baseURL = this.mInput.getSource();
        }
        catch (IOException e) {
            throw new WstxIOException(e);
        }
        if (this.mEventListener != null) {
            this.mEventListener.dtdNotationDecl(id, pubId, sysId, baseURL);
        }
        XMLStreamLocation2 evtLoc = this.getStartLocation();
        WNotationDeclaration nd = new WNotationDeclaration(evtLoc, id, pubId, sysId, baseURL);
        if (this.mPredefdNotations != null && (oldDecl = (NotationDeclaration)this.mPredefdNotations.get(id)) != null) {
            DTDSubsetImpl.throwNotationException(oldDecl, nd);
        }
        if ((m = this.mNotations) == null) {
            this.mNotations = m = new LinkedHashMap<String, WNotationDeclaration>();
        } else {
            NotationDeclaration oldDecl2 = (NotationDeclaration)((HashMap)m).get(id);
            if (oldDecl2 != null) {
                DTDSubsetImpl.throwNotationException(oldDecl2, nd);
            }
        }
        if (this.mNotationForwardRefs != null) {
            this.mNotationForwardRefs.remove(id);
        }
        m.put(id, nd);
    }

    private void handleTargetNsDecl() throws XMLStreamException {
        String name;
        this.mAnyDTDppFeatures = true;
        char c = this.skipObligatoryDtdWs();
        if (this.isNameStartChar(c)) {
            name = this.readDTDLocalName(c, false);
            c = this.skipObligatoryDtdWs();
        } else {
            name = null;
        }
        if (c != '\"' && c != '\'') {
            if (c == '>') {
                this._reportWFCViolation("Missing namespace URI for TARGETNS directive");
            }
            this.throwDTDUnexpectedChar(c, "; expected a single or double quote to enclose the namespace URI");
        }
        String uri = this.parseSystemId(c, false, "in namespace URI");
        if ((this.mConfigFlags & 0x800) != 0) {
            uri = InternCache.getInstance().intern(uri);
        }
        if ((c = this.skipDtdWs(true)) != '>') {
            this.throwDTDUnexpectedChar(c, "; expected '>' to end TARGETNS directive");
        }
        if (name == null) {
            this.mDefaultNsURI = uri;
        } else {
            if (this.mNamespaces == null) {
                this.mNamespaces = new HashMap();
            }
            this.mNamespaces.put(name, uri);
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    private void handleAttrDecl(DTDElement elem, char c, int index, Location loc) throws XMLStreamException {
        DTDAttribute attr;
        DefaultAttrValue defVal;
        WordResolver enumValues;
        int type;
        PrefixedName attrName;
        block31: {
            block33: {
                String defTypeStr;
                block34: {
                    block30: {
                        block32: {
                            attrName = this.readDTDQName(c);
                            c = this.skipObligatoryDtdWs();
                            type = 0;
                            enumValues = null;
                            if (c != '(') break block32;
                            enumValues = this.parseEnumerated(elem, attrName, false);
                            type = 1;
                            break block30;
                        }
                        String typeStr = this.readDTDName(c);
                        switch (typeStr.charAt(0)) {
                            case 'C': {
                                if (typeStr != "CDATA") break;
                                type = 0;
                                break block30;
                            }
                            case 'I': {
                                if (typeStr == "ID") {
                                    type = 2;
                                    break block30;
                                } else if (typeStr == "IDREF") {
                                    type = 3;
                                    break block30;
                                } else {
                                    if (typeStr != "IDREFS") break;
                                    type = 4;
                                }
                                break block30;
                            }
                            case 'E': {
                                if (typeStr == "ENTITY") {
                                    type = 5;
                                    break block30;
                                } else {
                                    if (typeStr != "ENTITIES") break;
                                    type = 6;
                                }
                                break block30;
                            }
                            case 'N': {
                                if (typeStr == "NOTATION") {
                                    type = 7;
                                    c = this.skipObligatoryDtdWs();
                                    if (c != '(') {
                                        this.throwDTDUnexpectedChar(c, "Excepted '(' to start the list of NOTATION ids");
                                    }
                                    enumValues = this.parseEnumerated(elem, attrName, true);
                                } else if (typeStr == "NMTOKEN") {
                                    type = 8;
                                } else {
                                    if (typeStr != "NMTOKENS") break;
                                    type = 9;
                                }
                                break block30;
                            }
                        }
                        this.throwDTDAttrError("Unrecognized attribute type '" + typeStr + "'" + ErrorConsts.ERR_DTD_ATTR_TYPE, elem, attrName);
                    }
                    c = this.skipObligatoryDtdWs();
                    if (c != '#') break block33;
                    defTypeStr = this.readDTDName(this.getNextExpanded());
                    if (defTypeStr != "REQUIRED") break block34;
                    defVal = DefaultAttrValue.constructRequired();
                    break block31;
                }
                if (defTypeStr == "IMPLIED") {
                    defVal = DefaultAttrValue.constructImplied();
                    break block31;
                } else if (defTypeStr == "FIXED") {
                    defVal = DefaultAttrValue.constructFixed();
                    c = this.skipObligatoryDtdWs();
                    this.parseAttrDefaultValue(defVal, c, attrName, loc, true);
                    break block31;
                } else {
                    this.throwDTDAttrError("Unrecognized attribute default value directive #" + defTypeStr + ErrorConsts.ERR_DTD_DEFAULT_TYPE, elem, attrName);
                    defVal = null;
                }
                break block31;
            }
            defVal = DefaultAttrValue.constructOptional();
            this.parseAttrDefaultValue(defVal, c, attrName, loc, false);
        }
        if (type == 2 && defVal.hasDefaultValue()) {
            if (this.mCfgFullyValidating) {
                this.throwDTDAttrError("has type ID; can not have a default (or #FIXED) value (XML 1.0/#3.3.1)", elem, attrName);
            }
        } else if (this.mConfig.willDoXmlIdTyping() && attrName.isXmlReservedAttr(this.mCfgNsEnabled, "id")) {
            this.checkXmlIdAttr(type);
        }
        if (attrName.isXmlReservedAttr(this.mCfgNsEnabled, "space")) {
            this.checkXmlSpaceAttr(type, enumValues);
        }
        if (this.mCfgNsEnabled && attrName.isaNsDeclaration()) {
            if (!defVal.hasDefaultValue()) {
                return;
            }
            attr = elem.addNsDefault(this, attrName, type, defVal, this.mCfgFullyValidating);
        } else {
            attr = elem.addAttribute(this, attrName, type, defVal, enumValues, this.mCfgFullyValidating);
        }
        if (attr == null) {
            XMLReporter rep = this.mConfig.getXMLReporter();
            if (rep == null) return;
            String msg = MessageFormat.format(ErrorConsts.W_DTD_ATTR_REDECL, attrName, elem);
            this._reportWarning(rep, ErrorConsts.WT_ATTR_DECL, msg, loc);
            return;
        }
        if (!defVal.hasDefaultValue()) return;
        attr.normalizeDefault();
        if (!this.mCfgFullyValidating) return;
        attr.validateDefault(this, true);
    }

    private WordResolver parseEnumerated(DTDElement elem, PrefixedName attrName, boolean isNotation) throws XMLStreamException {
        HashMap sharedEnums;
        TreeSet<String> set = new TreeSet<String>();
        char c = this.skipDtdWs(true);
        if (c == ')') {
            this.throwDTDUnexpectedChar(c, " (empty list; missing identifier(s))?");
        }
        if (isNotation) {
            sharedEnums = null;
        } else {
            sharedEnums = this.mSharedEnumValues;
            if (sharedEnums == null && !isNotation) {
                this.mSharedEnumValues = sharedEnums = new HashMap();
            }
        }
        String id = isNotation ? this.readNotationEntry(c, attrName, elem.getLocation()) : this.readEnumEntry(c, sharedEnums);
        set.add(id);
        while ((c = this.skipDtdWs(true)) != ')') {
            if (c != '|') {
                this.throwDTDUnexpectedChar(c, "; missing '|' separator?");
            }
            c = this.skipDtdWs(true);
            id = isNotation ? this.readNotationEntry(c, attrName, elem.getLocation()) : this.readEnumEntry(c, sharedEnums);
            if (set.add(id) || !this.mCfgFullyValidating) continue;
            this.throwDTDAttrError("Duplicate enumeration value '" + id + "'", elem, attrName);
        }
        return WordResolver.constructInstance(set);
    }

    private String readNotationEntry(char c, PrefixedName attrName, Location refLoc) throws XMLStreamException {
        NotationDeclaration decl;
        String id = this.readDTDName(c);
        if (this.mPredefdNotations != null && (decl = (NotationDeclaration)this.mPredefdNotations.get(id)) != null) {
            this.mUsesPredefdNotations = true;
            return decl.getName();
        }
        NotationDeclaration notationDeclaration = decl = this.mNotations == null ? null : (NotationDeclaration)this.mNotations.get(id);
        if (decl == null) {
            if (this.mCfgFullyValidating) {
                if (this.mNotationForwardRefs == null) {
                    this.mNotationForwardRefs = new LinkedHashMap();
                }
                this.mNotationForwardRefs.put(id, refLoc);
            }
            return id;
        }
        return decl.getName();
    }

    private String readEnumEntry(char c, HashMap sharedEnums) throws XMLStreamException {
        String id = this.readDTDNmtoken(c);
        String sid = (String)sharedEnums.get(id);
        if (sid == null) {
            sid = id;
            sharedEnums.put(sid, sid);
        }
        return sid;
    }

    private StructValidator readMixedSpec(PrefixedName elemName, boolean construct) throws XMLStreamException {
        char c;
        String keyw = this.checkDTDKeyword("PCDATA");
        if (keyw != null) {
            this._reportWFCViolation("Unrecognized directive #" + keyw + "'; expected #PCDATA (or element name)");
        }
        LinkedHashMap<PrefixedName, TokenContentSpec> m = new LinkedHashMap<PrefixedName, TokenContentSpec>();
        while ((c = this.skipDtdWs(true)) != ')') {
            PrefixedName n;
            TokenContentSpec old;
            if (c == '|') {
                c = this.skipDtdWs(true);
            } else if (c == ',') {
                this.throwDTDUnexpectedChar(c, " (sequences not allowed within mixed content)");
            } else if (c == '(') {
                this.throwDTDUnexpectedChar(c, " (sub-content specs not allowed within mixed content)");
            } else {
                this.throwDTDUnexpectedChar(c, "; expected either '|' to separate elements, or ')' to close the list");
            }
            if ((old = m.put(n = this.readDTDQName(c), TokenContentSpec.construct(' ', n))) == null || !this.mCfgFullyValidating) continue;
            this.throwDTDElemError("duplicate child element <" + n + "> in mixed content model", elemName);
        }
        char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(this.getErrorMsg());
        if (c != '*') {
            if (m.size() > 0) {
                this._reportWFCViolation("Missing trailing '*' after a non-empty mixed content specification");
            }
            --this.mInputPtr;
        }
        if (!construct) {
            return null;
        }
        if (m.isEmpty()) {
            return EmptyValidator.getPcdataInstance();
        }
        ChoiceContentSpec spec = ChoiceContentSpec.constructMixed(this.mCfgNsEnabled, ((HashMap)m).values());
        StructValidator val = ((ContentSpec)spec).getSimpleValidator();
        if (val == null) {
            DFAState dfa = DFAState.constructDFA(spec);
            val = new DFAValidator(dfa);
        }
        return val;
    }

    private ContentSpec readContentSpec(PrefixedName elemName, boolean mainLevel, boolean construct) throws XMLStreamException {
        ContentSpec cs;
        ArrayList<ContentSpec> subSpecs = new ArrayList<ContentSpec>();
        boolean isChoice = false;
        boolean choiceSet = false;
        while (true) {
            char c;
            if ((c = this.skipDtdWs(true)) == ')') {
                if (!subSpecs.isEmpty()) break;
                this._reportWFCViolation("Empty content specification for '" + elemName + "' (need at least one entry)");
                break;
            }
            if (c == '|' || c == ',') {
                boolean newChoice;
                boolean bl = newChoice = c == '|';
                if (!choiceSet) {
                    isChoice = newChoice;
                    choiceSet = true;
                } else if (isChoice != newChoice) {
                    this._reportWFCViolation("Can not mix content spec separators ('|' and ','); need to use parenthesis groups");
                }
                c = this.skipDtdWs(true);
            } else if (!subSpecs.isEmpty()) {
                this.throwDTDUnexpectedChar(c, " (missing separator '|' or ','?)");
            }
            if (c == '(') {
                cs = this.readContentSpec(elemName, false, construct);
                subSpecs.add(cs);
                continue;
            }
            if (c == '|' || c == ',') {
                this.throwDTDUnexpectedChar(c, " (missing element name?)");
            }
            PrefixedName thisName = this.readDTDQName(c);
            char arity = this.readArity();
            TokenContentSpec cs2 = construct ? TokenContentSpec.construct(arity, thisName) : TokenContentSpec.getDummySpec();
            subSpecs.add(cs2);
        }
        char arity = this.readArity();
        if (!construct) {
            return TokenContentSpec.getDummySpec();
        }
        if (subSpecs.size() == 1) {
            cs = (ContentSpec)subSpecs.get(0);
            char otherArity = cs.getArity();
            if (arity != otherArity) {
                cs.setArity(FullDTDReader.combineArities(arity, otherArity));
            }
            return cs;
        }
        if (isChoice) {
            return ChoiceContentSpec.constructChoice(this.mCfgNsEnabled, arity, subSpecs);
        }
        return SeqContentSpec.construct(this.mCfgNsEnabled, arity, subSpecs);
    }

    private static char combineArities(char arity1, char arity2) {
        if (arity1 == arity2) {
            return arity1;
        }
        if (arity1 == ' ') {
            return arity2;
        }
        if (arity2 == ' ') {
            return arity1;
        }
        if (arity1 == '*' || arity2 == '*') {
            return '*';
        }
        return '*';
    }

    private EntityDecl handleExternalEntityDecl(WstxInputSource inputSource, boolean isParam, String id, char c, Location evtLoc) throws XMLStreamException {
        URL ctxt;
        boolean isPublic = this.checkPublicSystemKeyword(c);
        String pubId = null;
        if (isPublic) {
            c = this.skipObligatoryDtdWs();
            if (c != '\"' && c != '\'') {
                this.throwDTDUnexpectedChar(c, "; expected a quote to start the public identifier");
            }
            pubId = this.parsePublicId(c, this.getErrorMsg());
            c = this.getNextExpanded();
            if (c <= ' ') {
                c = this.skipDtdWs(true);
            } else if (c != '>') {
                --this.mInputPtr;
                c = this.skipObligatoryDtdWs();
            }
            if (c == '>') {
                this._reportWFCViolation("Unexpected end of ENTITY declaration (expected a system id after public id): trying to use an SGML DTD instead of XML one?");
            }
        } else {
            c = this.skipObligatoryDtdWs();
        }
        if (c != '\"' && c != '\'') {
            this.throwDTDUnexpectedChar(c, "; expected a quote to start the system identifier");
        }
        String sysId = this.parseSystemId(c, this.mNormalizeLFs, this.getErrorMsg());
        String notationId = null;
        if (isParam) {
            c = this.skipDtdWs(true);
        } else {
            int i = this.peekNext();
            if (i == 62) {
                c = (char)62;
                ++this.mInputPtr;
            } else if (i < 0) {
                c = this.skipDtdWs(true);
            } else if (i == 37) {
                c = this.getNextExpanded();
            } else {
                ++this.mInputPtr;
                c = (char)i;
                if (!FullDTDReader.isSpaceChar(c)) {
                    this.throwDTDUnexpectedChar(c, "; expected a separating space or closing '>'");
                }
                c = this.skipDtdWs(true);
            }
            if (c != '>') {
                String keyw;
                if (!this.isNameStartChar(c)) {
                    this.throwDTDUnexpectedChar(c, "; expected either NDATA keyword, or closing '>'");
                }
                if ((keyw = this.checkDTDKeyword("DATA")) != null) {
                    this._reportWFCViolation("Unrecognized keyword '" + keyw + "'; expected NOTATION (or closing '>')");
                }
                c = this.skipObligatoryDtdWs();
                notationId = this.readNotationEntry(c, null, evtLoc);
                c = this.skipDtdWs(true);
            }
        }
        if (c != '>') {
            this.throwDTDUnexpectedChar(c, "; expected closing '>'");
        }
        try {
            ctxt = inputSource.getSource();
        }
        catch (IOException e) {
            throw new WstxIOException(e);
        }
        if (notationId == null) {
            return new ParsedExtEntity(evtLoc, id, ctxt, pubId, sysId);
        }
        return new UnparsedExtEntity(evtLoc, id, ctxt, pubId, sysId, notationId);
    }

    private HashMap getElementMap() {
        LinkedHashMap m = this.mElements;
        if (m == null) {
            this.mElements = m = new LinkedHashMap();
        }
        return m;
    }

    private PrefixedName findSharedName(String prefix, String localName) {
        HashMap<PrefixedName, PrefixedName> m = this.mSharedNames;
        if (this.mSharedNames == null) {
            this.mSharedNames = m = new HashMap<PrefixedName, PrefixedName>();
        } else {
            PrefixedName key = this.mAccessKey;
            key.reset(prefix, localName);
            key = (PrefixedName)m.get(key);
            if (key != null) {
                return key;
            }
        }
        PrefixedName result = new PrefixedName(prefix, localName);
        m.put(result, result);
        return result;
    }

    protected EntityDecl findEntity(String id, Object arg) {
        if (arg == ENTITY_EXP_PE) {
            EntityDecl ed;
            EntityDecl entityDecl = ed = this.mPredefdPEs == null ? null : (EntityDecl)this.mPredefdPEs.get(id);
            if (ed != null) {
                this.mUsesPredefdEntities = true;
                this.mRefdPEs = null;
            } else if (this.mParamEntities != null && (ed = (EntityDecl)this.mParamEntities.get(id)) != null && !this.mUsesPredefdEntities) {
                HashSet<String> used = this.mRefdPEs;
                if (used == null) {
                    this.mRefdPEs = used = new HashSet<String>();
                }
                used.add(id);
            }
            return ed;
        }
        if (arg == ENTITY_EXP_GE) {
            EntityDecl ed;
            EntityDecl entityDecl = ed = this.mPredefdGEs == null ? null : (EntityDecl)this.mPredefdGEs.get(id);
            if (ed != null) {
                this.mUsesPredefdEntities = true;
                this.mRefdGEs = null;
            } else if (this.mGeneralEntities != null && (ed = (EntityDecl)this.mGeneralEntities.get(id)) != null && !this.mUsesPredefdEntities) {
                if (this.mRefdGEs == null) {
                    this.mRefdGEs = new HashSet();
                }
                this.mRefdGEs.add(id);
            }
            return ed;
        }
        throw new IllegalStateException(ErrorConsts.ERR_INTERNAL);
    }

    protected void handleUndeclaredEntity(String id) throws XMLStreamException {
        this._reportVCViolation("Undeclared parameter entity '" + id + "'.");
        if (this.mCurrAttrDefault != null) {
            WstxInputLocation loc = this.getLastCharLocation();
            if (this.mExpandingPE) {
                this.mCurrAttrDefault.addUndeclaredPE(id, loc);
            } else {
                this.mCurrAttrDefault.addUndeclaredGE(id, loc);
            }
        }
        if (this.mEventListener != null && this.mExpandingPE) {
            this.mEventListener.dtdSkippedEntity("%" + id);
        }
    }

    protected void handleIncompleteEntityProblem(WstxInputSource closing) throws XMLStreamException {
        if (closing.getScopeId() == 0) {
            this._reportWFCViolation(this.entityDesc(closing) + ": " + "Incomplete PE: has to fully contain a declaration (as per xml 1.0.3, section 2.8, WFC 'PE Between Declarations')");
        } else if (this.mCfgFullyValidating) {
            this._reportVCViolation(this.entityDesc(closing) + ": " + "Incomplete PE: has to be fully contained in a declaration (as per xml 1.0.3, section 2.8, VC 'Proper Declaration/PE Nesting')");
        }
    }

    protected void handleGreedyEntityProblem(WstxInputSource input) throws XMLStreamException {
        if (this.mCfgFullyValidating) {
            this._reportWFCViolation(this.entityDesc(input) + ": " + "Unbalanced PE: has to be fully contained in a declaration (as per xml 1.0.3, section 2.8, VC 'Proper Declaration/PE Nesting')");
        }
    }

    protected void checkXmlSpaceAttr(int type, WordResolver enumValues) throws XMLStreamException {
        boolean ok;
        boolean bl = ok = type == 1;
        if (ok) {
            switch (enumValues.size()) {
                case 1: {
                    ok = enumValues.find("preserve") != null || enumValues.find("default") != null;
                    break;
                }
                case 2: {
                    ok = enumValues.find("preserve") != null && enumValues.find("default") != null;
                    break;
                }
                default: {
                    ok = false;
                }
            }
        }
        if (!ok) {
            this._reportVCViolation(ErrorConsts.ERR_DTD_XML_SPACE);
        }
    }

    protected void checkXmlIdAttr(int type) throws XMLStreamException {
        if (type != 2) {
            this._reportVCViolation(ErrorConsts.ERR_DTD_XML_ID);
        }
    }

    private void _reportWarning(XMLReporter rep, String probType, String msg, Location loc) throws XMLStreamException {
        if (rep != null) {
            XMLValidationProblem prob = new XMLValidationProblem(loc, msg, 1, probType);
            rep.report(msg, probType, prob, loc);
        }
    }
}

