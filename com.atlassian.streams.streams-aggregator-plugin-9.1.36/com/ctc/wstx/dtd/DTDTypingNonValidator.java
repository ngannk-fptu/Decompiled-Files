/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.DTDAttribute;
import com.ctc.wstx.dtd.DTDElement;
import com.ctc.wstx.dtd.DTDSubset;
import com.ctc.wstx.dtd.DTDValidatorBase;
import com.ctc.wstx.util.DataUtil;
import com.ctc.wstx.util.ElementIdMap;
import com.ctc.wstx.util.ExceptionUtil;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.ValidationContext;

public class DTDTypingNonValidator
extends DTDValidatorBase {
    protected boolean mHasAttrDefaults = false;
    protected BitSet mCurrDefaultAttrs = null;
    protected boolean mHasNormalizableAttrs = false;
    BitSet mTmpDefaultAttrs;

    public DTDTypingNonValidator(DTDSubset schema, ValidationContext ctxt, boolean hasNsDefaults, Map elemSpecs, Map genEntities) {
        super(schema, ctxt, hasNsDefaults, elemSpecs, genEntities);
    }

    public final boolean reallyValidating() {
        return false;
    }

    public void setAttrValueNormalization(boolean state) {
    }

    public void validateElementStart(String localName, String uri, String prefix) throws XMLStreamException {
        this.mTmpKey.reset(prefix, localName);
        DTDElement elem = (DTDElement)this.mElemSpecs.get(this.mTmpKey);
        int elemCount = this.mElemCount++;
        if (elemCount >= this.mElems.length) {
            this.mElems = (DTDElement[])DataUtil.growArrayBy50Pct(this.mElems);
        }
        this.mElems[elemCount] = this.mCurrElem = elem;
        this.mAttrCount = 0;
        this.mIdAttrIndex = -2;
        if (elem == null) {
            this.mCurrAttrDefs = EMPTY_MAP;
            this.mHasAttrDefaults = false;
            this.mCurrDefaultAttrs = null;
            this.mHasNormalizableAttrs = false;
            return;
        }
        this.mCurrAttrDefs = elem.getAttributes();
        if (this.mCurrAttrDefs == null) {
            this.mCurrAttrDefs = EMPTY_MAP;
            this.mHasAttrDefaults = false;
            this.mCurrDefaultAttrs = null;
            this.mHasNormalizableAttrs = false;
            return;
        }
        this.mHasNormalizableAttrs = this.mNormAttrs || elem.attrsNeedValidation();
        this.mHasAttrDefaults = elem.hasAttrDefaultValues();
        if (this.mHasAttrDefaults) {
            int specCount = elem.getSpecialCount();
            BitSet bs = this.mTmpDefaultAttrs;
            if (bs == null) {
                this.mTmpDefaultAttrs = bs = new BitSet(specCount);
            } else {
                bs.clear();
            }
            this.mCurrDefaultAttrs = bs;
        } else {
            this.mCurrDefaultAttrs = null;
        }
    }

    public String validateAttribute(String localName, String uri, String prefix, String value) throws XMLStreamException {
        int index;
        DTDAttribute attr = (DTDAttribute)this.mCurrAttrDefs.get(this.mTmpKey.reset(prefix, localName));
        if ((index = this.mAttrCount++) >= this.mAttrSpecs.length) {
            this.mAttrSpecs = (DTDAttribute[])DataUtil.growArrayBy50Pct(this.mAttrSpecs);
        }
        this.mAttrSpecs[index] = attr;
        if (attr != null) {
            int specIndex;
            if (this.mHasAttrDefaults && (specIndex = attr.getSpecialIndex()) >= 0) {
                this.mCurrDefaultAttrs.set(specIndex);
            }
            if (this.mHasNormalizableAttrs) {
                // empty if block
            }
        }
        return null;
    }

    public String validateAttribute(String localName, String uri, String prefix, char[] valueChars, int valueStart, int valueEnd) throws XMLStreamException {
        int index;
        DTDAttribute attr = (DTDAttribute)this.mCurrAttrDefs.get(this.mTmpKey.reset(prefix, localName));
        if ((index = this.mAttrCount++) >= this.mAttrSpecs.length) {
            this.mAttrSpecs = (DTDAttribute[])DataUtil.growArrayBy50Pct(this.mAttrSpecs);
        }
        this.mAttrSpecs[index] = attr;
        if (attr != null) {
            int specIndex;
            if (this.mHasAttrDefaults && (specIndex = attr.getSpecialIndex()) >= 0) {
                this.mCurrDefaultAttrs.set(specIndex);
            }
            if (this.mHasNormalizableAttrs) {
                return attr.normalize(this, valueChars, valueStart, valueEnd);
            }
        }
        return null;
    }

    public int validateElementAndAttributes() throws XMLStreamException {
        DTDElement elem = this.mCurrElem;
        if (this.mHasAttrDefaults) {
            BitSet specBits = this.mCurrDefaultAttrs;
            int specCount = elem.getSpecialCount();
            int ix = specBits.nextClearBit(0);
            while (ix < specCount) {
                List specAttrs = elem.getSpecialAttrs();
                DTDAttribute attr = (DTDAttribute)specAttrs.get(ix);
                if (attr.hasDefaultValue()) {
                    this.doAddDefaultValue(attr);
                }
                ix = specBits.nextClearBit(ix + 1);
            }
        }
        return elem == null ? 4 : elem.getAllowedContentIfSpace();
    }

    public int validateElementEnd(String localName, String uri, String prefix) throws XMLStreamException {
        int ix = --this.mElemCount;
        this.mElems[ix] = null;
        if (ix < 1) {
            return 4;
        }
        DTDElement elem = this.mElems[ix - 1];
        return elem == null ? 4 : this.mElems[ix - 1].getAllowedContentIfSpace();
    }

    public void validationCompleted(boolean eod) {
    }

    protected ElementIdMap getIdMap() {
        ExceptionUtil.throwGenericInternal();
        return null;
    }
}

