/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.api;

import com.ctc.wstx.api.CommonConfig;
import com.ctc.wstx.api.WstxInputProperties;
import com.ctc.wstx.cfg.InputConfigFlags;
import com.ctc.wstx.dtd.DTDEventListener;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.ent.IntEntity;
import com.ctc.wstx.io.BufferRecycler;
import com.ctc.wstx.util.ArgUtil;
import com.ctc.wstx.util.DataUtil;
import com.ctc.wstx.util.SymbolTable;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLResolver;
import org.codehaus.stax2.validation.DTDValidationSchema;

public final class ReaderConfig
extends CommonConfig
implements InputConfigFlags {
    public static final int DEFAULT_MAX_ATTRIBUTES_PER_ELEMENT = 1000;
    public static final int DEFAULT_MAX_ATTRIBUTE_LENGTH = 524288;
    public static final int DEFAULT_MAX_ELEMENT_DEPTH = 1000;
    public static final int DEFAULT_MAX_ENTITY_DEPTH = 500;
    public static final int DEFAULT_MAX_ENTITY_COUNT = 100000;
    static final int PROP_COALESCE_TEXT = 1;
    static final int PROP_NAMESPACE_AWARE = 2;
    static final int PROP_REPLACE_ENTITY_REFS = 3;
    static final int PROP_SUPPORT_EXTERNAL_ENTITIES = 4;
    static final int PROP_VALIDATE_AGAINST_DTD = 5;
    static final int PROP_SUPPORT_DTD = 6;
    public static final int PROP_EVENT_ALLOCATOR = 7;
    static final int PROP_WARNING_REPORTER = 8;
    static final int PROP_XML_RESOLVER = 9;
    static final int PROP_INTERN_NS_URIS = 20;
    static final int PROP_INTERN_NAMES = 21;
    static final int PROP_REPORT_CDATA = 22;
    static final int PROP_REPORT_PROLOG_WS = 23;
    static final int PROP_PRESERVE_LOCATION = 24;
    static final int PROP_AUTO_CLOSE_INPUT = 25;
    static final int PROP_SUPPORT_XMLID = 26;
    static final int PROP_DTD_OVERRIDE = 27;
    static final int PROP_NORMALIZE_LFS = 40;
    static final int PROP_CACHE_DTDS = 42;
    static final int PROP_CACHE_DTDS_BY_PUBLIC_ID = 43;
    static final int PROP_LAZY_PARSING = 44;
    static final int PROP_SUPPORT_DTDPP = 45;
    static final int PROP_TREAT_CHAR_REFS_AS_ENTS = 46;
    static final int PROP_INPUT_BUFFER_LENGTH = 50;
    static final int PROP_MIN_TEXT_SEGMENT = 52;
    static final int PROP_CUSTOM_INTERNAL_ENTITIES = 53;
    static final int PROP_DTD_RESOLVER = 54;
    static final int PROP_ENTITY_RESOLVER = 55;
    static final int PROP_UNDECLARED_ENTITY_RESOLVER = 56;
    static final int PROP_BASE_URL = 57;
    static final int PROP_INPUT_PARSING_MODE = 58;
    static final int PROP_MAX_ATTRIBUTES_PER_ELEMENT = 60;
    static final int PROP_MAX_CHILDREN_PER_ELEMENT = 61;
    static final int PROP_MAX_ELEMENT_COUNT = 62;
    static final int PROP_MAX_ELEMENT_DEPTH = 63;
    static final int PROP_MAX_CHARACTERS = 64;
    static final int PROP_MAX_ATTRIBUTE_SIZE = 65;
    static final int PROP_MAX_TEXT_LENGTH = 66;
    static final int PROP_MAX_ENTITY_COUNT = 67;
    static final int PROP_MAX_ENTITY_DEPTH = 68;
    static final int MIN_INPUT_BUFFER_LENGTH = 8;
    static final int DTD_CACHE_SIZE_J2SE = 12;
    static final int DTD_CACHE_SIZE_J2ME = 5;
    static final int DEFAULT_SHORTEST_TEXT_SEGMENT = 64;
    static final int DEFAULT_FLAGS_FULL = 2973213;
    static final int DEFAULT_FLAGS_J2ME = 2973213;
    static final HashMap sProperties = new HashMap(64);
    protected final boolean mIsJ2MESubset;
    protected final SymbolTable mSymbols;
    protected int mConfigFlags;
    protected int mConfigFlagMods;
    static final int PROP_INTERN_NAMES_EXPLICIT = 26;
    static final int PROP_INTERN_NS_URIS_EXPLICIT = 27;
    protected int mInputBufferLen;
    protected int mMinTextSegmentLen;
    protected int mMaxAttributesPerElement = 1000;
    protected int mMaxAttributeSize = 524288;
    protected int mMaxChildrenPerElement = Integer.MAX_VALUE;
    protected int mMaxElementDepth = 1000;
    protected long mMaxElementCount = Long.MAX_VALUE;
    protected long mMaxCharacters = Long.MAX_VALUE;
    protected int mMaxTextLength = Integer.MAX_VALUE;
    protected int mMaxEntityDepth = 500;
    protected long mMaxEntityCount = 100000L;
    protected URL mBaseURL;
    protected WstxInputProperties.ParsingMode mParsingMode = WstxInputProperties.PARSING_MODE_DOCUMENT;
    protected boolean mXml11 = false;
    XMLReporter mReporter;
    XMLResolver mDtdResolver = null;
    XMLResolver mEntityResolver = null;
    Object[] mSpecialProperties = null;
    private static final int SPEC_PROC_COUNT = 4;
    private static final int SP_IX_CUSTOM_ENTITIES = 0;
    private static final int SP_IX_UNDECL_ENT_RESOLVER = 1;
    private static final int SP_IX_DTD_EVENT_LISTENER = 2;
    private static final int SP_IX_DTD_OVERRIDE = 3;
    static final ThreadLocal mRecyclerRef;
    BufferRecycler mCurrRecycler = null;

    private ReaderConfig(ReaderConfig base, boolean j2meSubset, SymbolTable symbols, int configFlags, int configFlagMods, int inputBufLen, int minTextSegmentLen) {
        super(base);
        SoftReference ref;
        this.mIsJ2MESubset = j2meSubset;
        this.mSymbols = symbols;
        this.mConfigFlags = configFlags;
        this.mConfigFlagMods = configFlagMods;
        this.mInputBufferLen = inputBufLen;
        this.mMinTextSegmentLen = minTextSegmentLen;
        if (base != null) {
            this.mMaxAttributesPerElement = base.mMaxAttributesPerElement;
            this.mMaxAttributeSize = base.mMaxAttributeSize;
            this.mMaxChildrenPerElement = base.mMaxChildrenPerElement;
            this.mMaxElementCount = base.mMaxElementCount;
            this.mMaxElementDepth = base.mMaxElementDepth;
            this.mMaxCharacters = base.mMaxCharacters;
            this.mMaxTextLength = base.mMaxTextLength;
            this.mMaxEntityDepth = base.mMaxEntityDepth;
            this.mMaxEntityCount = base.mMaxEntityCount;
        }
        if ((ref = (SoftReference)mRecyclerRef.get()) != null) {
            this.mCurrRecycler = (BufferRecycler)ref.get();
        }
    }

    public static ReaderConfig createJ2MEDefaults() {
        ReaderConfig rc = new ReaderConfig(null, true, null, 2973213, 0, 2000, 64);
        return rc;
    }

    public static ReaderConfig createFullDefaults() {
        ReaderConfig rc = new ReaderConfig(null, false, null, 2973213, 0, 4000, 64);
        return rc;
    }

    public ReaderConfig createNonShared(SymbolTable sym) {
        ReaderConfig rc = new ReaderConfig(this, this.mIsJ2MESubset, sym, this.mConfigFlags, this.mConfigFlagMods, this.mInputBufferLen, this.mMinTextSegmentLen);
        rc.mReporter = this.mReporter;
        rc.mDtdResolver = this.mDtdResolver;
        rc.mEntityResolver = this.mEntityResolver;
        rc.mBaseURL = this.mBaseURL;
        rc.mParsingMode = this.mParsingMode;
        rc.mMaxAttributesPerElement = this.mMaxAttributesPerElement;
        rc.mMaxAttributeSize = this.mMaxAttributeSize;
        rc.mMaxChildrenPerElement = this.mMaxChildrenPerElement;
        rc.mMaxElementCount = this.mMaxElementCount;
        rc.mMaxCharacters = this.mMaxCharacters;
        rc.mMaxTextLength = this.mMaxTextLength;
        rc.mMaxElementDepth = this.mMaxElementDepth;
        rc.mMaxEntityDepth = this.mMaxEntityDepth;
        rc.mMaxEntityCount = this.mMaxEntityCount;
        if (this.mSpecialProperties != null) {
            int len = this.mSpecialProperties.length;
            Object[] specProps = new Object[len];
            System.arraycopy(this.mSpecialProperties, 0, specProps, 0, len);
            rc.mSpecialProperties = specProps;
        }
        return rc;
    }

    public void resetState() {
        this.mXml11 = false;
    }

    protected int findPropertyId(String propName) {
        Integer I = (Integer)sProperties.get(propName);
        return I == null ? -1 : I;
    }

    public SymbolTable getSymbols() {
        return this.mSymbols;
    }

    public int getDtdCacheSize() {
        return this.mIsJ2MESubset ? 5 : 12;
    }

    public int getConfigFlags() {
        return this.mConfigFlags;
    }

    public boolean willCoalesceText() {
        return this._hasConfigFlag(2);
    }

    public boolean willSupportNamespaces() {
        return this._hasConfigFlag(1);
    }

    public boolean willReplaceEntityRefs() {
        return this._hasConfigFlag(4);
    }

    public boolean willSupportExternalEntities() {
        return this._hasConfigFlag(8);
    }

    public boolean willSupportDTDs() {
        return this._hasConfigFlag(16);
    }

    public boolean willValidateWithDTD() {
        return this._hasConfigFlag(32);
    }

    public boolean willReportCData() {
        return this._hasConfigFlag(512);
    }

    public boolean willParseLazily() {
        return this._hasConfigFlag(262144);
    }

    public boolean willInternNames() {
        return this._hasConfigFlag(1024);
    }

    public boolean willInternNsURIs() {
        return this._hasConfigFlag(2048);
    }

    public boolean willPreserveLocation() {
        return this._hasConfigFlag(4096);
    }

    public boolean willAutoCloseInput() {
        return this._hasConfigFlag(8192);
    }

    public boolean willReportPrologWhitespace() {
        return this._hasConfigFlag(256);
    }

    public boolean willCacheDTDs() {
        return this._hasConfigFlag(65536);
    }

    public boolean willCacheDTDsByPublicId() {
        return this._hasConfigFlag(131072);
    }

    public boolean willDoXmlIdTyping() {
        return this._hasConfigFlag(0x200000);
    }

    public boolean willDoXmlIdUniqChecks() {
        return this._hasConfigFlag(0x400000);
    }

    public boolean willSupportDTDPP() {
        return this._hasConfigFlag(524288);
    }

    public boolean willNormalizeLFs() {
        return this._hasConfigFlag(16384);
    }

    public boolean willTreatCharRefsAsEnts() {
        return this._hasConfigFlag(0x800000);
    }

    public int getInputBufferLength() {
        return this.mInputBufferLen;
    }

    public int getShortestReportedTextSegment() {
        return this.mMinTextSegmentLen;
    }

    public int getMaxAttributesPerElement() {
        return this.mMaxAttributesPerElement;
    }

    public int getMaxAttributeSize() {
        return this.mMaxAttributeSize;
    }

    public int getMaxChildrenPerElement() {
        return this.mMaxChildrenPerElement;
    }

    public int getMaxElementDepth() {
        return this.mMaxElementDepth;
    }

    public long getMaxElementCount() {
        return this.mMaxElementCount;
    }

    public long getMaxCharacters() {
        return this.mMaxCharacters;
    }

    public long getMaxTextLength() {
        return this.mMaxTextLength;
    }

    public int getMaxEntityDepth() {
        return this.mMaxEntityDepth;
    }

    public long getMaxEntityCount() {
        return this.mMaxEntityCount;
    }

    public Map getCustomInternalEntities() {
        Map custEnt = (Map)this._getSpecialProperty(0);
        if (custEnt == null) {
            return Collections.EMPTY_MAP;
        }
        int len = custEnt.size();
        HashMap m = new HashMap(len + (len >> 2), 0.81f);
        Iterator it = custEnt.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry me = it.next();
            m.put(me.getKey(), (EntityDecl)me.getValue());
        }
        return m;
    }

    public EntityDecl findCustomInternalEntity(String id) {
        Map custEnt = (Map)this._getSpecialProperty(0);
        if (custEnt == null) {
            return null;
        }
        return (EntityDecl)custEnt.get(id);
    }

    public XMLReporter getXMLReporter() {
        return this.mReporter;
    }

    public XMLResolver getXMLResolver() {
        return this.mEntityResolver;
    }

    public XMLResolver getDtdResolver() {
        return this.mDtdResolver;
    }

    public XMLResolver getEntityResolver() {
        return this.mEntityResolver;
    }

    public XMLResolver getUndeclaredEntityResolver() {
        return (XMLResolver)this._getSpecialProperty(1);
    }

    public URL getBaseURL() {
        return this.mBaseURL;
    }

    public WstxInputProperties.ParsingMode getInputParsingMode() {
        return this.mParsingMode;
    }

    public boolean inputParsingModeDocuments() {
        return this.mParsingMode == WstxInputProperties.PARSING_MODE_DOCUMENTS;
    }

    public boolean inputParsingModeFragment() {
        return this.mParsingMode == WstxInputProperties.PARSING_MODE_FRAGMENT;
    }

    public boolean isXml11() {
        return this.mXml11;
    }

    public DTDEventListener getDTDEventListener() {
        return (DTDEventListener)this._getSpecialProperty(2);
    }

    public DTDValidationSchema getDTDOverride() {
        return (DTDValidationSchema)this._getSpecialProperty(3);
    }

    public boolean hasInternNamesBeenEnabled() {
        return this._hasExplicitConfigFlag(1024);
    }

    public boolean hasInternNsURIsBeenEnabled() {
        return this._hasExplicitConfigFlag(2048);
    }

    public void setConfigFlag(int flag) {
        this.mConfigFlags |= flag;
        this.mConfigFlagMods |= flag;
    }

    public void clearConfigFlag(int flag) {
        this.mConfigFlags &= ~flag;
        this.mConfigFlagMods |= flag;
    }

    public void doCoalesceText(boolean state) {
        this.setConfigFlag(2, state);
    }

    public void doSupportNamespaces(boolean state) {
        this.setConfigFlag(1, state);
    }

    public void doReplaceEntityRefs(boolean state) {
        this.setConfigFlag(4, state);
    }

    public void doSupportExternalEntities(boolean state) {
        this.setConfigFlag(8, state);
    }

    public void doSupportDTDs(boolean state) {
        this.setConfigFlag(16, state);
    }

    public void doValidateWithDTD(boolean state) {
        this.setConfigFlag(32, state);
    }

    public void doInternNames(boolean state) {
        this.setConfigFlag(1024, state);
    }

    public void doInternNsURIs(boolean state) {
        this.setConfigFlag(2048, state);
    }

    public void doReportPrologWhitespace(boolean state) {
        this.setConfigFlag(256, state);
    }

    public void doReportCData(boolean state) {
        this.setConfigFlag(512, state);
    }

    public void doCacheDTDs(boolean state) {
        this.setConfigFlag(65536, state);
    }

    public void doCacheDTDsByPublicId(boolean state) {
        this.setConfigFlag(131072, state);
    }

    public void doParseLazily(boolean state) {
        this.setConfigFlag(262144, state);
    }

    public void doXmlIdTyping(boolean state) {
        this.setConfigFlag(0x200000, state);
    }

    public void doXmlIdUniqChecks(boolean state) {
        this.setConfigFlag(0x400000, state);
    }

    public void doPreserveLocation(boolean state) {
        this.setConfigFlag(4096, state);
    }

    public void doAutoCloseInput(boolean state) {
        this.setConfigFlag(8192, state);
    }

    public void doSupportDTDPP(boolean state) {
        this.setConfigFlag(524288, state);
    }

    public void doTreatCharRefsAsEnts(boolean state) {
        this.setConfigFlag(0x800000, state);
    }

    public void doNormalizeLFs(boolean state) {
        this.setConfigFlag(16384, state);
    }

    public void setInputBufferLength(int value) {
        if (value < 8) {
            value = 8;
        }
        this.mInputBufferLen = value;
    }

    public void setMaxAttributesPerElement(int value) {
        this.mMaxAttributesPerElement = value;
    }

    public void setMaxAttributeSize(int value) {
        this.mMaxAttributeSize = value;
    }

    public void setMaxChildrenPerElement(int value) {
        this.mMaxChildrenPerElement = value;
    }

    public void setMaxElementDepth(int value) {
        this.mMaxElementDepth = value;
    }

    public void setMaxElementCount(long value) {
        this.mMaxElementCount = value;
    }

    public void setMaxCharacters(long value) {
        this.mMaxCharacters = value;
    }

    public void setMaxTextLength(int value) {
        this.mMaxTextLength = value;
    }

    public void setMaxEntityDepth(int value) {
        this.mMaxEntityDepth = value;
    }

    public void setMaxEntityCount(long value) {
        this.mMaxEntityCount = value;
    }

    public void setShortestReportedTextSegment(int value) {
        this.mMinTextSegmentLen = value;
    }

    public void setCustomInternalEntities(Map m) {
        HashMap<String, IntEntity> entMap;
        if (m == null || m.size() < 1) {
            entMap = Collections.EMPTY_MAP;
        } else {
            int len = m.size();
            entMap = new HashMap<String, IntEntity>(len + (len >> 1), 0.75f);
            Iterator it = m.entrySet().iterator();
            while (it.hasNext()) {
                char[] ch;
                Map.Entry me = it.next();
                Object val = me.getValue();
                if (val == null) {
                    ch = DataUtil.getEmptyCharArray();
                } else if (val instanceof char[]) {
                    ch = (char[])val;
                } else {
                    String str = val.toString();
                    ch = str.toCharArray();
                }
                String name = (String)me.getKey();
                entMap.put(name, IntEntity.create(name, ch));
            }
        }
        this._setSpecialProperty(0, entMap);
    }

    public void setXMLReporter(XMLReporter r) {
        this.mReporter = r;
    }

    public void setXMLResolver(XMLResolver r) {
        this.mEntityResolver = r;
        this.mDtdResolver = r;
    }

    public void setDtdResolver(XMLResolver r) {
        this.mDtdResolver = r;
    }

    public void setEntityResolver(XMLResolver r) {
        this.mEntityResolver = r;
    }

    public void setUndeclaredEntityResolver(XMLResolver r) {
        this._setSpecialProperty(1, r);
    }

    public void setBaseURL(URL baseURL) {
        this.mBaseURL = baseURL;
    }

    public void setInputParsingMode(WstxInputProperties.ParsingMode mode) {
        this.mParsingMode = mode;
    }

    public void enableXml11(boolean state) {
        this.mXml11 = state;
    }

    public void setDTDEventListener(DTDEventListener l) {
        this._setSpecialProperty(2, l);
    }

    public void setDTDOverride(DTDValidationSchema schema) {
        this._setSpecialProperty(3, schema);
    }

    public void configureForXmlConformance() {
        this.doSupportNamespaces(true);
        this.doSupportDTDs(true);
        this.doSupportExternalEntities(true);
        this.doReplaceEntityRefs(true);
        this.doXmlIdTyping(true);
        this.doXmlIdUniqChecks(true);
    }

    public void configureForConvenience() {
        this.doCoalesceText(true);
        this.doReplaceEntityRefs(true);
        this.doReportCData(false);
        this.doReportPrologWhitespace(false);
        this.doPreserveLocation(true);
        this.doParseLazily(false);
    }

    public void configureForSpeed() {
        this.doCoalesceText(false);
        this.doPreserveLocation(false);
        this.doReportPrologWhitespace(false);
        this.doInternNsURIs(true);
        this.doXmlIdUniqChecks(false);
        this.doCacheDTDs(true);
        this.doParseLazily(true);
        this.setShortestReportedTextSegment(16);
        this.setInputBufferLength(8000);
    }

    public void configureForLowMemUsage() {
        this.doCoalesceText(false);
        this.doPreserveLocation(false);
        this.doCacheDTDs(false);
        this.doParseLazily(true);
        this.doXmlIdUniqChecks(false);
        this.setShortestReportedTextSegment(64);
        this.setInputBufferLength(512);
    }

    public void configureForRoundTripping() {
        this.doCoalesceText(false);
        this.doReplaceEntityRefs(false);
        this.doReportCData(true);
        this.doReportPrologWhitespace(true);
        this.doTreatCharRefsAsEnts(true);
        this.doNormalizeLFs(false);
        this.setShortestReportedTextSegment(Integer.MAX_VALUE);
    }

    public char[] allocSmallCBuffer(int minSize) {
        char[] result;
        if (this.mCurrRecycler != null && (result = this.mCurrRecycler.getSmallCBuffer(minSize)) != null) {
            return result;
        }
        return new char[minSize];
    }

    public void freeSmallCBuffer(char[] buffer) {
        if (this.mCurrRecycler == null) {
            this.mCurrRecycler = this.createRecycler();
        }
        this.mCurrRecycler.returnSmallCBuffer(buffer);
    }

    public char[] allocMediumCBuffer(int minSize) {
        char[] result;
        if (this.mCurrRecycler != null && (result = this.mCurrRecycler.getMediumCBuffer(minSize)) != null) {
            return result;
        }
        return new char[minSize];
    }

    public void freeMediumCBuffer(char[] buffer) {
        if (this.mCurrRecycler == null) {
            this.mCurrRecycler = this.createRecycler();
        }
        this.mCurrRecycler.returnMediumCBuffer(buffer);
    }

    public char[] allocFullCBuffer(int minSize) {
        char[] result;
        if (this.mCurrRecycler != null && (result = this.mCurrRecycler.getFullCBuffer(minSize)) != null) {
            return result;
        }
        return new char[minSize];
    }

    public void freeFullCBuffer(char[] buffer) {
        if (this.mCurrRecycler == null) {
            this.mCurrRecycler = this.createRecycler();
        }
        this.mCurrRecycler.returnFullCBuffer(buffer);
    }

    public byte[] allocFullBBuffer(int minSize) {
        byte[] result;
        if (this.mCurrRecycler != null && (result = this.mCurrRecycler.getFullBBuffer(minSize)) != null) {
            return result;
        }
        return new byte[minSize];
    }

    public void freeFullBBuffer(byte[] buffer) {
        if (this.mCurrRecycler == null) {
            this.mCurrRecycler = this.createRecycler();
        }
        this.mCurrRecycler.returnFullBBuffer(buffer);
    }

    private BufferRecycler createRecycler() {
        BufferRecycler recycler = new BufferRecycler();
        mRecyclerRef.set(new SoftReference<BufferRecycler>(recycler));
        return recycler;
    }

    private void setConfigFlag(int flag, boolean state) {
        this.mConfigFlags = state ? (this.mConfigFlags |= flag) : (this.mConfigFlags &= ~flag);
        this.mConfigFlagMods |= flag;
    }

    public Object getProperty(int id) {
        switch (id) {
            case 1: {
                return this.willCoalesceText() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 2: {
                return this.willSupportNamespaces() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 3: {
                return this.willReplaceEntityRefs() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 4: {
                return this.willSupportExternalEntities() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 5: {
                return this.willValidateWithDTD() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 6: {
                return this.willSupportDTDs() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 8: {
                return this.getXMLReporter();
            }
            case 9: {
                return this.getXMLResolver();
            }
            case 7: {
                return null;
            }
            case 23: {
                return this.willReportPrologWhitespace() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 22: {
                return this.willReportCData() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 21: {
                return this.willInternNames() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 20: {
                return this.willInternNsURIs() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 24: {
                return this.willPreserveLocation() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 25: {
                return this.willAutoCloseInput() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 27: {
                return this.getDTDOverride();
            }
            case 42: {
                return this.willCacheDTDs() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 43: {
                return this.willCacheDTDsByPublicId() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 44: {
                return this.willParseLazily() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 26: {
                if (!this._hasConfigFlag(0x200000)) {
                    return "disable";
                }
                return this._hasConfigFlag(0x400000) ? "xmlidFull" : "xmlidTyping";
            }
            case 46: {
                return this.willTreatCharRefsAsEnts() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 40: {
                return this.willNormalizeLFs() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 50: {
                return DataUtil.Integer(this.getInputBufferLength());
            }
            case 60: {
                return DataUtil.Integer(this.getMaxAttributesPerElement());
            }
            case 65: {
                return DataUtil.Integer(this.getMaxAttributeSize());
            }
            case 61: {
                return DataUtil.Integer(this.getMaxChildrenPerElement());
            }
            case 63: {
                return DataUtil.Integer(this.getMaxElementDepth());
            }
            case 62: {
                return DataUtil.Long(this.getMaxElementCount());
            }
            case 64: {
                return DataUtil.Long(this.getMaxCharacters());
            }
            case 66: {
                return DataUtil.Long(this.getMaxTextLength());
            }
            case 68: {
                return DataUtil.Integer(this.getMaxEntityDepth());
            }
            case 67: {
                return DataUtil.Long(this.getMaxEntityCount());
            }
            case 52: {
                return DataUtil.Integer(this.getShortestReportedTextSegment());
            }
            case 53: {
                return this.getCustomInternalEntities();
            }
            case 54: {
                return this.getDtdResolver();
            }
            case 55: {
                return this.getEntityResolver();
            }
            case 56: {
                return this.getUndeclaredEntityResolver();
            }
            case 57: {
                return this.getBaseURL();
            }
            case 58: {
                return this.getInputParsingMode();
            }
        }
        throw new IllegalStateException("Internal error: no handler for property with internal id " + id + ".");
    }

    public boolean setProperty(String propName, int id, Object value) {
        switch (id) {
            case 1: {
                this.doCoalesceText(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 2: {
                this.doSupportNamespaces(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 3: {
                this.doReplaceEntityRefs(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 4: {
                this.doSupportExternalEntities(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 6: {
                this.doSupportDTDs(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 5: {
                this.doValidateWithDTD(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 8: {
                this.setXMLReporter((XMLReporter)value);
                break;
            }
            case 9: {
                this.setXMLResolver((XMLResolver)value);
                break;
            }
            case 7: {
                return false;
            }
            case 20: {
                this.doInternNsURIs(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 21: {
                this.doInternNames(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 22: {
                this.doReportCData(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 23: {
                this.doReportPrologWhitespace(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 24: {
                this.doPreserveLocation(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 25: {
                this.doAutoCloseInput(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 26: {
                boolean typing;
                boolean uniq;
                if ("disable".equals(value)) {
                    uniq = false;
                    typing = false;
                } else if ("xmlidTyping".equals(value)) {
                    typing = true;
                    uniq = false;
                } else if ("xmlidFull".equals(value)) {
                    uniq = true;
                    typing = true;
                } else {
                    throw new IllegalArgumentException("Illegal argument ('" + value + "') to set property " + "org.codehaus.stax2.supportXmlId" + " to: has to be one of '" + "disable" + "', '" + "xmlidTyping" + "' or '" + "xmlidFull" + "'");
                }
                this.setConfigFlag(0x200000, typing);
                this.setConfigFlag(0x400000, uniq);
                break;
            }
            case 27: {
                this.setDTDOverride((DTDValidationSchema)value);
                break;
            }
            case 42: {
                this.doCacheDTDs(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 43: {
                this.doCacheDTDsByPublicId(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 44: {
                this.doParseLazily(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 46: {
                this.doTreatCharRefsAsEnts(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 40: {
                this.doNormalizeLFs(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 50: {
                this.setInputBufferLength(ArgUtil.convertToInt(propName, value, 1));
                break;
            }
            case 60: {
                this.setMaxAttributesPerElement(ArgUtil.convertToInt(propName, value, 1));
                break;
            }
            case 65: {
                this.setMaxAttributeSize(ArgUtil.convertToInt(propName, value, 1));
                break;
            }
            case 61: {
                this.setMaxChildrenPerElement(ArgUtil.convertToInt(propName, value, 1));
                break;
            }
            case 63: {
                this.setMaxElementDepth(ArgUtil.convertToInt(propName, value, 1));
                break;
            }
            case 62: {
                this.setMaxElementCount(ArgUtil.convertToLong(propName, value, 1L));
                break;
            }
            case 64: {
                this.setMaxCharacters(ArgUtil.convertToLong(propName, value, 1L));
                break;
            }
            case 66: {
                this.setMaxTextLength(ArgUtil.convertToInt(propName, value, 1));
                break;
            }
            case 68: {
                this.setMaxEntityDepth(ArgUtil.convertToInt(propName, value, 1));
                break;
            }
            case 67: {
                this.setMaxEntityCount(ArgUtil.convertToLong(propName, value, 1L));
                break;
            }
            case 52: {
                this.setShortestReportedTextSegment(ArgUtil.convertToInt(propName, value, 1));
                break;
            }
            case 53: {
                this.setCustomInternalEntities((Map)value);
                break;
            }
            case 54: {
                this.setDtdResolver((XMLResolver)value);
                break;
            }
            case 55: {
                this.setEntityResolver((XMLResolver)value);
                break;
            }
            case 56: {
                this.setUndeclaredEntityResolver((XMLResolver)value);
                break;
            }
            case 57: {
                URL u;
                if (value == null) {
                    u = null;
                } else if (value instanceof URL) {
                    u = (URL)value;
                } else {
                    try {
                        u = new URL(value.toString());
                    }
                    catch (Exception ioe) {
                        throw new IllegalArgumentException(ioe.getMessage(), ioe);
                    }
                }
                this.setBaseURL(u);
                break;
            }
            case 58: {
                this.setInputParsingMode((WstxInputProperties.ParsingMode)value);
                break;
            }
            default: {
                throw new IllegalStateException("Internal error: no handler for property with internal id " + id + ".");
            }
        }
        return true;
    }

    protected boolean _hasConfigFlag(int flag) {
        return (this.mConfigFlags & flag) != 0;
    }

    protected boolean _hasExplicitConfigFlag(int flag) {
        return this._hasConfigFlag(flag) && (this.mConfigFlagMods & flag) != 0;
    }

    private final Object _getSpecialProperty(int ix) {
        if (this.mSpecialProperties == null) {
            return null;
        }
        return this.mSpecialProperties[ix];
    }

    private final void _setSpecialProperty(int ix, Object value) {
        if (this.mSpecialProperties == null) {
            this.mSpecialProperties = new Object[4];
        }
        this.mSpecialProperties[ix] = value;
    }

    static {
        sProperties.put("javax.xml.stream.isCoalescing", DataUtil.Integer(1));
        sProperties.put("javax.xml.stream.isNamespaceAware", DataUtil.Integer(2));
        sProperties.put("javax.xml.stream.isReplacingEntityReferences", DataUtil.Integer(3));
        sProperties.put("javax.xml.stream.isSupportingExternalEntities", DataUtil.Integer(4));
        sProperties.put("javax.xml.stream.isValidating", DataUtil.Integer(5));
        sProperties.put("javax.xml.stream.supportDTD", DataUtil.Integer(6));
        sProperties.put("javax.xml.stream.allocator", DataUtil.Integer(7));
        sProperties.put("javax.xml.stream.reporter", DataUtil.Integer(8));
        sProperties.put("javax.xml.stream.resolver", DataUtil.Integer(9));
        sProperties.put("org.codehaus.stax2.internNames", DataUtil.Integer(21));
        sProperties.put("org.codehaus.stax2.internNsUris", DataUtil.Integer(20));
        sProperties.put("http://java.sun.com/xml/stream/properties/report-cdata-event", DataUtil.Integer(22));
        sProperties.put("org.codehaus.stax2.reportPrologWhitespace", DataUtil.Integer(23));
        sProperties.put("org.codehaus.stax2.preserveLocation", DataUtil.Integer(24));
        sProperties.put("org.codehaus.stax2.closeInputSource", DataUtil.Integer(25));
        sProperties.put("org.codehaus.stax2.supportXmlId", DataUtil.Integer(26));
        sProperties.put("org.codehaus.stax2.propDtdOverride", DataUtil.Integer(27));
        sProperties.put("com.ctc.wstx.cacheDTDs", DataUtil.Integer(42));
        sProperties.put("com.ctc.wstx.cacheDTDsByPublicId", DataUtil.Integer(43));
        sProperties.put("com.ctc.wstx.lazyParsing", DataUtil.Integer(44));
        sProperties.put("com.ctc.wstx.supportDTDPP", DataUtil.Integer(45));
        sProperties.put("com.ctc.wstx.treatCharRefsAsEnts", DataUtil.Integer(46));
        sProperties.put("com.ctc.wstx.normalizeLFs", DataUtil.Integer(40));
        sProperties.put("com.ctc.wstx.inputBufferLength", DataUtil.Integer(50));
        sProperties.put("com.ctc.wstx.minTextSegment", DataUtil.Integer(52));
        sProperties.put("com.ctc.wstx.maxAttributesPerElement", DataUtil.Integer(60));
        sProperties.put("com.ctc.wstx.maxAttributeSize", DataUtil.Integer(65));
        sProperties.put("com.ctc.wstx.maxChildrenPerElement", DataUtil.Integer(61));
        sProperties.put("com.ctc.wstx.maxTextLength", DataUtil.Integer(66));
        sProperties.put("com.ctc.wstx.maxElementCount", DataUtil.Integer(62));
        sProperties.put("com.ctc.wstx.maxElementDepth", DataUtil.Integer(63));
        sProperties.put("com.ctc.wstx.maxCharacters", DataUtil.Integer(64));
        sProperties.put("com.ctc.wstx.maxEntityDepth", DataUtil.Integer(68));
        sProperties.put("com.ctc.wstx.maxEntityCount", DataUtil.Integer(67));
        sProperties.put("com.ctc.wstx.customInternalEntities", DataUtil.Integer(53));
        sProperties.put("com.ctc.wstx.dtdResolver", DataUtil.Integer(54));
        sProperties.put("com.ctc.wstx.entityResolver", DataUtil.Integer(55));
        sProperties.put("com.ctc.wstx.undeclaredEntityResolver", DataUtil.Integer(56));
        sProperties.put("com.ctc.wstx.baseURL", DataUtil.Integer(57));
        sProperties.put("com.ctc.wstx.fragmentMode", DataUtil.Integer(58));
        mRecyclerRef = new ThreadLocal();
    }
}

