/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.DTDAttribute;
import com.ctc.wstx.dtd.DTDElement;
import com.ctc.wstx.dtd.DTDSubset;
import com.ctc.wstx.sr.InputElementStack;
import com.ctc.wstx.sr.NsDefaultProvider;
import com.ctc.wstx.util.DataUtil;
import com.ctc.wstx.util.ElementIdMap;
import com.ctc.wstx.util.ExceptionUtil;
import com.ctc.wstx.util.PrefixedName;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.XMLValidationProblem;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

public abstract class DTDValidatorBase
extends XMLValidator
implements NsDefaultProvider {
    static final int DEFAULT_STACK_SIZE = 16;
    static final int EXP_MAX_ATTRS = 16;
    protected static final HashMap EMPTY_MAP = new HashMap();
    final boolean mHasNsDefaults;
    final DTDSubset mSchema;
    final ValidationContext mContext;
    final Map mElemSpecs;
    final Map mGeneralEntities;
    protected boolean mNormAttrs;
    protected DTDElement mCurrElem = null;
    protected DTDElement[] mElems = null;
    protected int mElemCount = 0;
    protected HashMap mCurrAttrDefs = null;
    protected DTDAttribute[] mAttrSpecs = new DTDAttribute[16];
    protected int mAttrCount = 0;
    protected int mIdAttrIndex = -1;
    protected final transient PrefixedName mTmpKey = new PrefixedName(null, null);
    char[] mTmpAttrValueBuffer = null;

    public DTDValidatorBase(DTDSubset schema, ValidationContext ctxt, boolean hasNsDefaults, Map elemSpecs, Map genEntities) {
        this.mSchema = schema;
        this.mContext = ctxt;
        this.mHasNsDefaults = hasNsDefaults;
        this.mElemSpecs = elemSpecs == null || elemSpecs.size() == 0 ? Collections.EMPTY_MAP : elemSpecs;
        this.mGeneralEntities = genEntities;
        this.mNormAttrs = true;
        this.mElems = new DTDElement[16];
    }

    public void setAttrValueNormalization(boolean state) {
        this.mNormAttrs = state;
    }

    public abstract boolean reallyValidating();

    public final XMLValidationSchema getSchema() {
        return this.mSchema;
    }

    public abstract void validateElementStart(String var1, String var2, String var3) throws XMLStreamException;

    public abstract String validateAttribute(String var1, String var2, String var3, String var4) throws XMLStreamException;

    public abstract String validateAttribute(String var1, String var2, String var3, char[] var4, int var5, int var6) throws XMLStreamException;

    public abstract int validateElementAndAttributes() throws XMLStreamException;

    public abstract int validateElementEnd(String var1, String var2, String var3) throws XMLStreamException;

    public void validateText(String text, boolean lastTextSegment) throws XMLStreamException {
    }

    public void validateText(char[] cbuf, int textStart, int textEnd, boolean lastTextSegment) throws XMLStreamException {
    }

    public abstract void validationCompleted(boolean var1) throws XMLStreamException;

    public String getAttributeType(int index) {
        DTDAttribute attr = this.mAttrSpecs[index];
        return attr == null ? "CDATA" : attr.getValueTypeString();
    }

    public int getIdAttrIndex() {
        int ix = this.mIdAttrIndex;
        if (ix == -2) {
            DTDAttribute idAttr;
            ix = -1;
            if (this.mCurrElem != null && (idAttr = this.mCurrElem.getIdAttribute()) != null) {
                DTDAttribute[] attrs = this.mAttrSpecs;
                int len = attrs.length;
                for (int i = 0; i < len; ++i) {
                    if (attrs[i] != idAttr) continue;
                    ix = i;
                    break;
                }
            }
            this.mIdAttrIndex = ix;
        }
        return ix;
    }

    public int getNotationAttrIndex() {
        int len = this.mAttrCount;
        for (int i = 0; i < len; ++i) {
            if (!this.mAttrSpecs[i].typeIsNotation()) continue;
            return i;
        }
        return -1;
    }

    public boolean mayHaveNsDefaults(String elemPrefix, String elemLN) {
        DTDElement elem;
        this.mTmpKey.reset(elemPrefix, elemLN);
        this.mCurrElem = elem = (DTDElement)this.mElemSpecs.get(this.mTmpKey);
        return elem != null && elem.hasNsDefaults();
    }

    public void checkNsDefaults(InputElementStack nsStack) throws XMLStreamException {
        HashMap m = this.mCurrElem.getNsDefaults();
        if (m != null) {
            Iterator it = m.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry me = it.next();
                String prefix = (String)me.getKey();
                if (nsStack.isPrefixLocallyDeclared(prefix)) continue;
                DTDAttribute attr = (DTDAttribute)me.getValue();
                String uri = attr.getDefaultValue(this.mContext, this);
                nsStack.addNsBinding(prefix, uri);
            }
        }
    }

    PrefixedName getElemName() {
        DTDElement elem = this.mElems[this.mElemCount - 1];
        return elem.getName();
    }

    Location getLocation() {
        return this.mContext.getValidationLocation();
    }

    protected abstract ElementIdMap getIdMap();

    Map getEntityMap() {
        return this.mGeneralEntities;
    }

    char[] getTempAttrValueBuffer(int neededLength) {
        if (this.mTmpAttrValueBuffer == null || this.mTmpAttrValueBuffer.length < neededLength) {
            int size = neededLength < 100 ? 100 : neededLength;
            this.mTmpAttrValueBuffer = new char[size];
        }
        return this.mTmpAttrValueBuffer;
    }

    public boolean hasNsDefaults() {
        return this.mHasNsDefaults;
    }

    void reportValidationProblem(String msg) throws XMLStreamException {
        this.doReportValidationProblem(msg, null);
    }

    void reportValidationProblem(String msg, Location loc) throws XMLStreamException {
        this.doReportValidationProblem(msg, loc);
    }

    void reportValidationProblem(String format, Object arg) throws XMLStreamException {
        this.doReportValidationProblem(MessageFormat.format(format, arg), null);
    }

    void reportValidationProblem(String format, Object arg1, Object arg2) throws XMLStreamException {
        this.doReportValidationProblem(MessageFormat.format(format, arg1, arg2), null);
    }

    protected void doReportValidationProblem(String msg, Location loc) throws XMLStreamException {
        if (loc == null) {
            loc = this.getLocation();
        }
        XMLValidationProblem prob = new XMLValidationProblem(loc, msg, 2);
        prob.setReporter(this);
        this.mContext.reportProblem(prob);
    }

    protected void doAddDefaultValue(DTDAttribute attr) throws XMLStreamException {
        int defIx;
        String def = attr.getDefaultValue(this.mContext, this);
        if (def == null) {
            ExceptionUtil.throwInternal("null default attribute value");
        }
        PrefixedName an = attr.getName();
        String prefix = an.getPrefix();
        String uri = "";
        if (prefix != null && prefix.length() > 0 && ((uri = this.mContext.getNamespaceURI(prefix)) == null || uri.length() == 0)) {
            this.reportValidationProblem("Unbound namespace prefix \"{0}\" for default attribute \"{1}\"", prefix, attr);
            uri = "";
        }
        if ((defIx = this.mContext.addDefaultAttribute(an.getLocalName(), uri, prefix, def)) >= 0) {
            while (defIx >= this.mAttrSpecs.length) {
                this.mAttrSpecs = (DTDAttribute[])DataUtil.growArrayBy50Pct(this.mAttrSpecs);
            }
            while (this.mAttrCount < defIx) {
                this.mAttrSpecs[this.mAttrCount++] = null;
            }
            this.mAttrSpecs[defIx] = attr;
            this.mAttrCount = defIx + 1;
        }
    }
}

