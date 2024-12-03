/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.dtd.DTDAttribute;
import com.ctc.wstx.dtd.DTDElement;
import com.ctc.wstx.dtd.DTDSubset;
import com.ctc.wstx.dtd.DTDValidatorBase;
import com.ctc.wstx.dtd.StructValidator;
import com.ctc.wstx.util.DataUtil;
import com.ctc.wstx.util.ElementId;
import com.ctc.wstx.util.ElementIdMap;
import com.ctc.wstx.util.StringUtil;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.ValidationContext;

public class DTDValidator
extends DTDValidatorBase {
    protected boolean mReportDuplicateErrors = false;
    protected ElementIdMap mIdMap = null;
    protected StructValidator[] mValidators = new StructValidator[16];
    protected BitSet mCurrSpecialAttrs = null;
    boolean mCurrHasAnyFixed = false;
    BitSet mTmpSpecialAttrs;

    public DTDValidator(DTDSubset schema, ValidationContext ctxt, boolean hasNsDefaults, Map elemSpecs, Map genEntities) {
        super(schema, ctxt, hasNsDefaults, elemSpecs, genEntities);
    }

    public final boolean reallyValidating() {
        return true;
    }

    public void validateElementStart(String localName, String uri, String prefix) throws XMLStreamException {
        String msg;
        StructValidator pv;
        this.mTmpKey.reset(prefix, localName);
        DTDElement elem = (DTDElement)this.mElemSpecs.get(this.mTmpKey);
        int elemCount = this.mElemCount++;
        if (elemCount >= this.mElems.length) {
            this.mElems = (DTDElement[])DataUtil.growArrayBy50Pct(this.mElems);
            this.mValidators = (StructValidator[])DataUtil.growArrayBy50Pct(this.mValidators);
        }
        this.mElems[elemCount] = this.mCurrElem = elem;
        if (elem == null || !elem.isDefined()) {
            this.reportValidationProblem(ErrorConsts.ERR_VLD_UNKNOWN_ELEM, this.mTmpKey.toString());
        }
        StructValidator structValidator = pv = elemCount > 0 ? this.mValidators[elemCount - 1] : null;
        if (pv != null && elem != null && (msg = pv.tryToValidate(elem.getName())) != null) {
            int ix = msg.indexOf("$END");
            String pname = this.mElems[elemCount - 1].toString();
            if (ix >= 0) {
                msg = msg.substring(0, ix) + "</" + pname + ">" + msg.substring(ix + 4);
            }
            this.reportValidationProblem("Validation error, encountered element <" + elem.getName() + "> as a child of <" + pname + ">: " + msg);
        }
        this.mAttrCount = 0;
        this.mIdAttrIndex = -2;
        if (elem == null) {
            this.mValidators[elemCount] = null;
            this.mCurrAttrDefs = EMPTY_MAP;
            this.mCurrHasAnyFixed = false;
            this.mCurrSpecialAttrs = null;
        } else {
            this.mValidators[elemCount] = elem.getValidator();
            this.mCurrAttrDefs = elem.getAttributes();
            if (this.mCurrAttrDefs == null) {
                this.mCurrAttrDefs = EMPTY_MAP;
            }
            this.mCurrHasAnyFixed = elem.hasFixedAttrs();
            int specCount = elem.getSpecialCount();
            if (specCount == 0) {
                this.mCurrSpecialAttrs = null;
            } else {
                BitSet bs = this.mTmpSpecialAttrs;
                if (bs == null) {
                    this.mTmpSpecialAttrs = bs = new BitSet(specCount);
                } else {
                    bs.clear();
                }
                this.mCurrSpecialAttrs = bs;
            }
        }
    }

    public String validateAttribute(String localName, String uri, String prefix, String value) throws XMLStreamException {
        String exp;
        String act;
        int specIndex;
        int index;
        DTDAttribute attr = (DTDAttribute)this.mCurrAttrDefs.get(this.mTmpKey.reset(prefix, localName));
        if (attr == null) {
            if (this.mCurrElem != null) {
                this.reportValidationProblem(ErrorConsts.ERR_VLD_UNKNOWN_ATTR, this.mCurrElem.toString(), this.mTmpKey.toString());
            }
            return value;
        }
        if ((index = this.mAttrCount++) >= this.mAttrSpecs.length) {
            this.mAttrSpecs = (DTDAttribute[])DataUtil.growArrayBy50Pct(this.mAttrSpecs);
        }
        this.mAttrSpecs[index] = attr;
        if (this.mCurrSpecialAttrs != null && (specIndex = attr.getSpecialIndex()) >= 0) {
            this.mCurrSpecialAttrs.set(specIndex);
        }
        String result = attr.validate(this, value, this.mNormAttrs);
        if (this.mCurrHasAnyFixed && attr.isFixed() && !(act = result == null ? value : result).equals(exp = attr.getDefaultValue(this.mContext, this))) {
            this.reportValidationProblem("Value of attribute \"" + attr + "\" (element <" + this.mCurrElem + ">) not \"" + exp + "\" as expected, but \"" + act + "\"");
        }
        return result;
    }

    public String validateAttribute(String localName, String uri, String prefix, char[] valueChars, int valueStart, int valueEnd) throws XMLStreamException {
        int specIndex;
        int index;
        DTDAttribute attr = (DTDAttribute)this.mCurrAttrDefs.get(this.mTmpKey.reset(prefix, localName));
        if (attr == null) {
            if (this.mCurrElem != null) {
                this.reportValidationProblem(ErrorConsts.ERR_VLD_UNKNOWN_ATTR, this.mCurrElem.toString(), this.mTmpKey.toString());
            }
            return new String(valueChars, valueStart, valueEnd);
        }
        if ((index = this.mAttrCount++) >= this.mAttrSpecs.length) {
            this.mAttrSpecs = (DTDAttribute[])DataUtil.growArrayBy50Pct(this.mAttrSpecs);
        }
        this.mAttrSpecs[index] = attr;
        if (this.mCurrSpecialAttrs != null && (specIndex = attr.getSpecialIndex()) >= 0) {
            this.mCurrSpecialAttrs.set(specIndex);
        }
        String result = attr.validate(this, valueChars, valueStart, valueEnd, this.mNormAttrs);
        if (this.mCurrHasAnyFixed && attr.isFixed()) {
            String exp = attr.getDefaultValue(this.mContext, this);
            boolean match = result == null ? StringUtil.matches(exp, valueChars, valueStart, valueEnd - valueStart) : exp.equals(result);
            if (!match) {
                String act = result == null ? new String(valueChars, valueStart, valueEnd) : result;
                this.reportValidationProblem("Value of #FIXED attribute \"" + attr + "\" (element <" + this.mCurrElem + ">) not \"" + exp + "\" as expected, but \"" + act + "\"");
            }
        }
        return result;
    }

    public int validateElementAndAttributes() throws XMLStreamException {
        DTDElement elem = this.mCurrElem;
        if (elem == null) {
            return 4;
        }
        if (this.mCurrSpecialAttrs != null) {
            BitSet specBits = this.mCurrSpecialAttrs;
            int specCount = elem.getSpecialCount();
            int ix = specBits.nextClearBit(0);
            while (ix < specCount) {
                List specAttrs = elem.getSpecialAttrs();
                DTDAttribute attr = (DTDAttribute)specAttrs.get(ix);
                if (attr.isRequired()) {
                    this.reportValidationProblem("Required attribute \"{0}\" missing from element <{1}>", attr, elem);
                } else {
                    this.doAddDefaultValue(attr);
                }
                ix = specBits.nextClearBit(ix + 1);
            }
        }
        return elem.getAllowedContent();
    }

    public int validateElementEnd(String localName, String uri, String prefix) throws XMLStreamException {
        String msg;
        int ix = this.mElemCount - 1;
        if (ix < 0) {
            return 1;
        }
        this.mElemCount = ix;
        DTDElement closingElem = this.mElems[ix];
        this.mElems[ix] = null;
        StructValidator v = this.mValidators[ix];
        this.mValidators[ix] = null;
        if (v != null && (msg = v.fullyValid()) != null) {
            this.reportValidationProblem("Validation error, element </" + closingElem + ">: " + msg);
        }
        if (ix < 1) {
            return 1;
        }
        return this.mElems[ix - 1].getAllowedContent();
    }

    public void validationCompleted(boolean eod) throws XMLStreamException {
        this.checkIdRefs();
    }

    protected ElementIdMap getIdMap() {
        if (this.mIdMap == null) {
            this.mIdMap = new ElementIdMap();
        }
        return this.mIdMap;
    }

    protected void checkIdRefs() throws XMLStreamException {
        ElementId ref;
        if (this.mIdMap != null && (ref = this.mIdMap.getFirstUndefined()) != null) {
            this.reportValidationProblem("Undefined id '" + ref.getId() + "': referenced from element <" + ref.getElemName() + ">, attribute '" + ref.getAttrName() + "'", ref.getLocation());
        }
    }
}

