/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.DTDAttribute;
import com.ctc.wstx.dtd.DTDValidatorBase;
import com.ctc.wstx.dtd.DefaultAttrValue;
import com.ctc.wstx.dtd.MinimalDTDReader;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.PrefixedName;
import javax.xml.stream.XMLStreamException;

public final class DTDEntityAttr
extends DTDAttribute {
    public DTDEntityAttr(PrefixedName name, DefaultAttrValue defValue, int specIndex, boolean nsAware, boolean xml11) {
        super(name, defValue, specIndex, nsAware, xml11);
    }

    public DTDAttribute cloneWith(int specIndex) {
        return new DTDEntityAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11);
    }

    public int getValueType() {
        return 5;
    }

    public String validate(DTDValidatorBase v, char[] cbuf, int start, int end, boolean normalize) throws XMLStreamException {
        while (start < end && WstxInputData.isSpaceChar(cbuf[start])) {
            ++start;
        }
        if (start >= end) {
            return this.reportValidationProblem(v, "Empty ENTITY value");
        }
        --end;
        while (end > start && WstxInputData.isSpaceChar(cbuf[end])) {
            --end;
        }
        int c = cbuf[start];
        if (!WstxInputData.isNameStartChar((char)c, this.mCfgNsAware, this.mCfgXml11) && c != 58) {
            return this.reportInvalidChar(v, (char)c, "not valid as the first ID character");
        }
        int hash = c;
        for (int i = start + 1; i <= end; ++i) {
            c = cbuf[i];
            if (!WstxInputData.isNameChar((char)c, this.mCfgNsAware, this.mCfgXml11)) {
                return this.reportInvalidChar(v, (char)c, "not valid as an ID character");
            }
            hash = hash * 31 + c;
        }
        EntityDecl ent = this.findEntityDecl(v, cbuf, start, end - start + 1, hash);
        return normalize ? ent.getName() : null;
    }

    public void validateDefault(InputProblemReporter rep, boolean normalize) throws XMLStreamException {
        String normStr = this.validateDefaultName(rep, normalize);
        if (normalize) {
            this.mDefValue.setValue(normStr);
        }
        EntityDecl ent = ((MinimalDTDReader)rep).findEntity(normStr);
        this.checkEntity(rep, normStr, ent);
    }
}

