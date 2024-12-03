/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.DTDAttribute;
import com.ctc.wstx.dtd.DTDValidatorBase;
import com.ctc.wstx.dtd.DefaultAttrValue;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.ElementId;
import com.ctc.wstx.util.ElementIdMap;
import com.ctc.wstx.util.PrefixedName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

public final class DTDIdRefAttr
extends DTDAttribute {
    public DTDIdRefAttr(PrefixedName name, DefaultAttrValue defValue, int specIndex, boolean nsAware, boolean xml11) {
        super(name, defValue, specIndex, nsAware, xml11);
    }

    @Override
    public DTDAttribute cloneWith(int specIndex) {
        return new DTDIdRefAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11);
    }

    @Override
    public int getValueType() {
        return 3;
    }

    @Override
    public String validate(DTDValidatorBase v, char[] cbuf, int start, int end, boolean normalize) throws XMLStreamException {
        while (start < end && WstxInputData.isSpaceChar(cbuf[start])) {
            ++start;
        }
        if (start >= end) {
            return this.reportValidationProblem(v, "Empty IDREF value");
        }
        --end;
        while (end > start && WstxInputData.isSpaceChar(cbuf[end])) {
            --end;
        }
        int c = cbuf[start];
        if (!WstxInputData.isNameStartChar((char)c, this.mCfgNsAware, this.mCfgXml11)) {
            return this.reportInvalidChar(v, (char)c, "not valid as the first IDREF character");
        }
        int hash = c;
        for (int i = start + 1; i <= end; ++i) {
            c = cbuf[i];
            if (!WstxInputData.isNameChar((char)c, this.mCfgNsAware, this.mCfgXml11)) {
                return this.reportInvalidChar(v, (char)c, "not valid as an IDREF character");
            }
            hash = hash * 31 + c;
        }
        ElementIdMap m = v.getIdMap();
        Location loc = v.getLocation();
        ElementId id = m.addReferenced(cbuf, start, end - start + 1, hash, loc, v.getElemName(), this.mName);
        return normalize ? id.getId() : null;
    }

    @Override
    public void validateDefault(InputProblemReporter rep, boolean normalize) throws XMLStreamException {
        String def = this.validateDefaultName(rep, normalize);
        if (normalize) {
            this.mDefValue.setValue(def);
        }
    }
}

