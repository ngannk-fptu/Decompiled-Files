/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.api;

import com.ctc.wstx.api.CommonConfig;
import com.ctc.wstx.api.EmptyElementHandler;
import com.ctc.wstx.api.InvalidCharHandler;
import com.ctc.wstx.cfg.OutputConfigFlags;
import com.ctc.wstx.io.BufferRecycler;
import com.ctc.wstx.util.ArgUtil;
import com.ctc.wstx.util.DataUtil;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import javax.xml.stream.XMLReporter;
import org.codehaus.stax2.io.EscapingWriterFactory;

public final class WriterConfig
extends CommonConfig
implements OutputConfigFlags {
    protected static final String DEFAULT_AUTOMATIC_NS_PREFIX = "wstxns";
    static final int PROP_AUTOMATIC_NS = 1;
    static final int PROP_AUTOMATIC_EMPTY_ELEMENTS = 2;
    static final int PROP_AUTO_CLOSE_OUTPUT = 3;
    static final int PROP_ENABLE_NS = 4;
    static final int PROP_AUTOMATIC_NS_PREFIX = 5;
    static final int PROP_TEXT_ESCAPER = 6;
    static final int PROP_ATTR_VALUE_ESCAPER = 7;
    static final int PROP_PROBLEM_REPORTER = 8;
    static final int PROP_USE_DOUBLE_QUOTES_IN_XML_DECL = 10;
    static final int PROP_OUTPUT_CDATA_AS_TEXT = 11;
    static final int PROP_COPY_DEFAULT_ATTRS = 12;
    static final int PROP_ESCAPE_CR = 13;
    static final int PROP_ADD_SPACE_AFTER_EMPTY_ELEM = 14;
    static final int PROP_AUTOMATIC_END_ELEMENTS = 15;
    static final int PROP_VALIDATE_STRUCTURE = 16;
    static final int PROP_VALIDATE_CONTENT = 17;
    static final int PROP_VALIDATE_ATTR = 18;
    static final int PROP_VALIDATE_NAMES = 19;
    static final int PROP_FIX_CONTENT = 20;
    static final int PROP_OUTPUT_INVALID_CHAR_HANDLER = 21;
    static final int PROP_OUTPUT_EMPTY_ELEMENT_HANDLER = 22;
    static final int PROP_UNDERLYING_STREAM = 30;
    static final int PROP_UNDERLYING_WRITER = 31;
    static final boolean DEFAULT_USE_DOUBLE_QUOTES_IN_XML_DECL = false;
    static final boolean DEFAULT_OUTPUT_CDATA_AS_TEXT = false;
    static final boolean DEFAULT_COPY_DEFAULT_ATTRS = false;
    static final boolean DEFAULT_ESCAPE_CR = true;
    static final boolean DEFAULT_ADD_SPACE_AFTER_EMPTY_ELEM = false;
    static final boolean DEFAULT_VALIDATE_STRUCTURE = true;
    static final boolean DEFAULT_VALIDATE_CONTENT = true;
    static final boolean DEFAULT_VALIDATE_ATTR = false;
    static final boolean DEFAULT_VALIDATE_NAMES = false;
    static final boolean DEFAULT_FIX_CONTENT = false;
    static final int DEFAULT_FLAGS_J2ME = 933;
    static final int DEFAULT_FLAGS_FULL = 933;
    static final HashMap sProperties = new HashMap(8);
    final boolean mIsJ2MESubset;
    protected int mConfigFlags;
    Object[] mSpecialProperties = null;
    private static final int SPEC_PROC_COUNT = 6;
    private static final int SP_IX_AUTO_NS_PREFIX = 0;
    private static final int SP_IX_TEXT_ESCAPER_FACTORY = 1;
    private static final int SP_IX_ATTR_VALUE_ESCAPER_FACTORY = 2;
    private static final int SP_IX_PROBLEM_REPORTER = 3;
    private static final int SP_IX_INVALID_CHAR_HANDLER = 4;
    private static final int SP_IX_EMPTY_ELEMENT_HANDLER = 5;
    static final ThreadLocal mRecyclerRef;
    BufferRecycler mCurrRecycler = null;

    private WriterConfig(WriterConfig base, boolean j2meSubset, int flags, Object[] specProps) {
        super(base);
        this.mIsJ2MESubset = j2meSubset;
        this.mConfigFlags = flags;
        this.mSpecialProperties = specProps;
        SoftReference ref = (SoftReference)mRecyclerRef.get();
        if (ref != null) {
            this.mCurrRecycler = (BufferRecycler)ref.get();
        }
    }

    public static WriterConfig createJ2MEDefaults() {
        return new WriterConfig(null, true, 933, null);
    }

    public static WriterConfig createFullDefaults() {
        return new WriterConfig(null, false, 933, null);
    }

    public WriterConfig createNonShared() {
        Object[] specProps;
        if (this.mSpecialProperties != null) {
            int len = this.mSpecialProperties.length;
            specProps = new Object[len];
            System.arraycopy(this.mSpecialProperties, 0, specProps, 0, len);
        } else {
            specProps = null;
        }
        return new WriterConfig(this, this.mIsJ2MESubset, this.mConfigFlags, specProps);
    }

    protected int findPropertyId(String propName) {
        Integer I = (Integer)sProperties.get(propName);
        return I == null ? -1 : I;
    }

    public Object getProperty(int id) {
        switch (id) {
            case 1: {
                return this.automaticNamespacesEnabled() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 4: {
                return this.willSupportNamespaces() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 8: {
                return this.getProblemReporter();
            }
            case 2: {
                return this.automaticEmptyElementsEnabled() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 3: {
                return this.willAutoCloseOutput() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 5: {
                return this.getAutomaticNsPrefix();
            }
            case 6: {
                return this.getTextEscaperFactory();
            }
            case 7: {
                return this.getAttrValueEscaperFactory();
            }
            case 10: {
                return this.willUseDoubleQuotesInXmlDecl() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 11: {
                return this.willOutputCDataAsText() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 12: {
                return this.willCopyDefaultAttrs() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 13: {
                return this.willEscapeCr() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 14: {
                return this.willAddSpaceAfterEmptyElem() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 15: {
                return this.automaticEndElementsEnabled() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 16: {
                return this.willValidateStructure() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 17: {
                return this.willValidateContent() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 18: {
                return this.willValidateAttributes() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 19: {
                return this.willValidateNames() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 20: {
                return this.willFixContent() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 21: {
                return this.getInvalidCharHandler();
            }
            case 22: {
                return this.getEmptyElementHandler();
            }
            case 30: 
            case 31: {
                throw new IllegalStateException("Can not access per-stream-writer properties via factory");
            }
        }
        throw new IllegalStateException("Internal error: no handler for property with internal id " + id + ".");
    }

    public boolean setProperty(String name, int id, Object value) {
        switch (id) {
            case 1: {
                this.enableAutomaticNamespaces(ArgUtil.convertToBoolean(name, value));
                break;
            }
            case 4: {
                this.doSupportNamespaces(ArgUtil.convertToBoolean(name, value));
                break;
            }
            case 8: {
                this.setProblemReporter((XMLReporter)value);
                break;
            }
            case 2: {
                this.enableAutomaticEmptyElements(ArgUtil.convertToBoolean(name, value));
                break;
            }
            case 3: {
                this.doAutoCloseOutput(ArgUtil.convertToBoolean(name, value));
                break;
            }
            case 5: {
                this.setAutomaticNsPrefix(value.toString());
                break;
            }
            case 6: {
                this.setTextEscaperFactory((EscapingWriterFactory)value);
                break;
            }
            case 7: {
                this.setAttrValueEscaperFactory((EscapingWriterFactory)value);
                break;
            }
            case 10: {
                this.doUseDoubleQuotesInXmlDecl(ArgUtil.convertToBoolean(name, value));
                break;
            }
            case 11: {
                this.doOutputCDataAsText(ArgUtil.convertToBoolean(name, value));
                break;
            }
            case 12: {
                this.doCopyDefaultAttrs(ArgUtil.convertToBoolean(name, value));
                break;
            }
            case 13: {
                this.doEscapeCr(ArgUtil.convertToBoolean(name, value));
                break;
            }
            case 14: {
                this.doAddSpaceAfterEmptyElem(ArgUtil.convertToBoolean(name, value));
                break;
            }
            case 15: {
                this.enableAutomaticEndElements(ArgUtil.convertToBoolean(name, value));
                break;
            }
            case 16: {
                this.doValidateStructure(ArgUtil.convertToBoolean(name, value));
                break;
            }
            case 17: {
                this.doValidateContent(ArgUtil.convertToBoolean(name, value));
                break;
            }
            case 18: {
                this.doValidateAttributes(ArgUtil.convertToBoolean(name, value));
                break;
            }
            case 19: {
                this.doValidateNames(ArgUtil.convertToBoolean(name, value));
                break;
            }
            case 20: {
                this.doFixContent(ArgUtil.convertToBoolean(name, value));
                break;
            }
            case 21: {
                this.setInvalidCharHandler((InvalidCharHandler)value);
                break;
            }
            case 22: {
                this.setEmptyElementHandler((EmptyElementHandler)value);
                break;
            }
            case 30: 
            case 31: {
                throw new IllegalStateException("Can not modify per-stream-writer properties via factory");
            }
            default: {
                throw new IllegalStateException("Internal error: no handler for property with internal id " + id + ".");
            }
        }
        return true;
    }

    public int getConfigFlags() {
        return this.mConfigFlags;
    }

    public boolean automaticNamespacesEnabled() {
        return this.hasConfigFlag(2);
    }

    public boolean automaticEmptyElementsEnabled() {
        return this.hasConfigFlag(4);
    }

    public boolean willAutoCloseOutput() {
        return this.hasConfigFlag(8192);
    }

    public boolean willSupportNamespaces() {
        return this.hasConfigFlag(1);
    }

    public boolean willUseDoubleQuotesInXmlDecl() {
        return this.hasConfigFlag(16384);
    }

    public boolean willOutputCDataAsText() {
        return this.hasConfigFlag(8);
    }

    public boolean willCopyDefaultAttrs() {
        return this.hasConfigFlag(16);
    }

    public boolean willEscapeCr() {
        return this.hasConfigFlag(32);
    }

    public boolean willAddSpaceAfterEmptyElem() {
        return this.hasConfigFlag(64);
    }

    public boolean automaticEndElementsEnabled() {
        return this.hasConfigFlag(128);
    }

    public boolean willValidateStructure() {
        return this.hasConfigFlag(256);
    }

    public boolean willValidateContent() {
        return this.hasConfigFlag(512);
    }

    public boolean willValidateAttributes() {
        return this.hasConfigFlag(2048);
    }

    public boolean willValidateNames() {
        return this.hasConfigFlag(1024);
    }

    public boolean willFixContent() {
        return this.hasConfigFlag(4096);
    }

    public String getAutomaticNsPrefix() {
        String prefix = (String)this.getSpecialProperty(0);
        if (prefix == null) {
            prefix = DEFAULT_AUTOMATIC_NS_PREFIX;
        }
        return prefix;
    }

    public EscapingWriterFactory getTextEscaperFactory() {
        return (EscapingWriterFactory)this.getSpecialProperty(1);
    }

    public EscapingWriterFactory getAttrValueEscaperFactory() {
        return (EscapingWriterFactory)this.getSpecialProperty(2);
    }

    public XMLReporter getProblemReporter() {
        return (XMLReporter)this.getSpecialProperty(3);
    }

    public InvalidCharHandler getInvalidCharHandler() {
        return (InvalidCharHandler)this.getSpecialProperty(4);
    }

    public EmptyElementHandler getEmptyElementHandler() {
        return (EmptyElementHandler)this.getSpecialProperty(5);
    }

    public void enableAutomaticNamespaces(boolean state) {
        this.setConfigFlag(2, state);
    }

    public void enableAutomaticEmptyElements(boolean state) {
        this.setConfigFlag(4, state);
    }

    public void doAutoCloseOutput(boolean state) {
        this.setConfigFlag(8192, state);
    }

    public void doSupportNamespaces(boolean state) {
        this.setConfigFlag(1, state);
    }

    public void doUseDoubleQuotesInXmlDecl(boolean state) {
        this.setConfigFlag(16384, state);
    }

    public void doOutputCDataAsText(boolean state) {
        this.setConfigFlag(8, state);
    }

    public void doCopyDefaultAttrs(boolean state) {
        this.setConfigFlag(16, state);
    }

    public void doEscapeCr(boolean state) {
        this.setConfigFlag(32, state);
    }

    public void doAddSpaceAfterEmptyElem(boolean state) {
        this.setConfigFlag(64, state);
    }

    public void enableAutomaticEndElements(boolean state) {
        this.setConfigFlag(128, state);
    }

    public void doValidateStructure(boolean state) {
        this.setConfigFlag(256, state);
    }

    public void doValidateContent(boolean state) {
        this.setConfigFlag(512, state);
    }

    public void doValidateAttributes(boolean state) {
        this.setConfigFlag(2048, state);
    }

    public void doValidateNames(boolean state) {
        this.setConfigFlag(1024, state);
    }

    public void doFixContent(boolean state) {
        this.setConfigFlag(4096, state);
    }

    public void setAutomaticNsPrefix(String prefix) {
        this.setSpecialProperty(0, prefix);
    }

    public void setTextEscaperFactory(EscapingWriterFactory f) {
        this.setSpecialProperty(1, f);
    }

    public void setAttrValueEscaperFactory(EscapingWriterFactory f) {
        this.setSpecialProperty(2, f);
    }

    public void setProblemReporter(XMLReporter rep) {
        this.setSpecialProperty(3, rep);
    }

    public void setInvalidCharHandler(InvalidCharHandler h) {
        this.setSpecialProperty(4, h);
    }

    public void setEmptyElementHandler(EmptyElementHandler h) {
        this.setSpecialProperty(5, h);
    }

    public void configureForXmlConformance() {
        this.doValidateAttributes(true);
        this.doValidateContent(true);
        this.doValidateStructure(true);
        this.doValidateNames(true);
    }

    public void configureForRobustness() {
        this.doValidateAttributes(true);
        this.doValidateStructure(true);
        this.doValidateNames(true);
        this.doValidateContent(true);
        this.doFixContent(true);
    }

    public void configureForSpeed() {
        this.doValidateAttributes(false);
        this.doValidateContent(false);
        this.doValidateNames(false);
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
    }

    private final boolean hasConfigFlag(int flag) {
        return (this.mConfigFlags & flag) == flag;
    }

    private final Object getSpecialProperty(int ix) {
        if (this.mSpecialProperties == null) {
            return null;
        }
        return this.mSpecialProperties[ix];
    }

    private final void setSpecialProperty(int ix, Object value) {
        if (this.mSpecialProperties == null) {
            this.mSpecialProperties = new Object[6];
        }
        this.mSpecialProperties[ix] = value;
    }

    static {
        sProperties.put("javax.xml.stream.isRepairingNamespaces", DataUtil.Integer(1));
        sProperties.put("javax.xml.stream.isNamespaceAware", DataUtil.Integer(4));
        sProperties.put("org.codehaus.stax2.automaticEmptyElements", DataUtil.Integer(2));
        sProperties.put("org.codehaus.stax2.autoCloseOutput", DataUtil.Integer(3));
        sProperties.put("org.codehaus.stax2.automaticNsPrefix", DataUtil.Integer(5));
        sProperties.put("org.codehaus.stax2.textEscaper", DataUtil.Integer(6));
        sProperties.put("org.codehaus.stax2.attrValueEscaper", DataUtil.Integer(7));
        sProperties.put("javax.xml.stream.reporter", DataUtil.Integer(8));
        sProperties.put("com.ctc.wstx.useDoubleQuotesInXmlDecl", DataUtil.Integer(10));
        sProperties.put("com.ctc.wstx.outputCDataAsText", DataUtil.Integer(11));
        sProperties.put("com.ctc.wstx.copyDefaultAttrs", DataUtil.Integer(12));
        sProperties.put("com.ctc.wstx.outputEscapeCr", DataUtil.Integer(13));
        sProperties.put("com.ctc.wstx.addSpaceAfterEmptyElem", DataUtil.Integer(14));
        sProperties.put("com.ctc.wstx.automaticEndElements", DataUtil.Integer(15));
        sProperties.put("com.ctc.wstx.outputInvalidCharHandler", DataUtil.Integer(21));
        sProperties.put("com.ctc.wstx.outputEmptyElementHandler", DataUtil.Integer(22));
        sProperties.put("com.ctc.wstx.outputValidateStructure", DataUtil.Integer(16));
        sProperties.put("com.ctc.wstx.outputValidateContent", DataUtil.Integer(17));
        sProperties.put("com.ctc.wstx.outputValidateAttr", DataUtil.Integer(18));
        sProperties.put("com.ctc.wstx.outputValidateNames", DataUtil.Integer(19));
        sProperties.put("com.ctc.wstx.outputFixContent", DataUtil.Integer(20));
        sProperties.put("com.ctc.wstx.outputUnderlyingStream", DataUtil.Integer(30));
        sProperties.put("com.ctc.wstx.outputUnderlyingStream", DataUtil.Integer(30));
        mRecyclerRef = new ThreadLocal();
    }
}

