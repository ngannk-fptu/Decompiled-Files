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

public final class DTDIdRefsAttr
extends DTDAttribute {
    public DTDIdRefsAttr(PrefixedName name, DefaultAttrValue defValue, int specIndex, boolean nsAware, boolean xml11) {
        super(name, defValue, specIndex, nsAware, xml11);
    }

    @Override
    public DTDAttribute cloneWith(int specIndex) {
        return new DTDIdRefsAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11);
    }

    @Override
    public int getValueType() {
        return 4;
    }

    @Override
    public String validate(DTDValidatorBase v, char[] cbuf, int start, int end, boolean normalize) throws XMLStreamException {
        while (start < end && WstxInputData.isSpaceChar(cbuf[start])) {
            ++start;
        }
        if (start >= end) {
            return this.reportValidationProblem(v, "Empty IDREFS value");
        }
        --end;
        while (end > start && WstxInputData.isSpaceChar(cbuf[end])) {
            --end;
        }
        ElementIdMap m = v.getIdMap();
        Location loc = v.getLocation();
        String idStr = null;
        StringBuilder sb = null;
        while (start <= end) {
            int i;
            int c = cbuf[start];
            if (!WstxInputData.isNameStartChar((char)c, this.mCfgNsAware, this.mCfgXml11)) {
                return this.reportInvalidChar(v, (char)c, "not valid as the first IDREFS character");
            }
            int hash = c;
            for (i = start + 1; i <= end && !WstxInputData.isSpaceChar((char)(c = cbuf[i])); ++i) {
                if (!WstxInputData.isNameChar((char)c, this.mCfgNsAware, this.mCfgXml11)) {
                    return this.reportInvalidChar(v, (char)c, "not valid as an IDREFS character");
                }
                hash = hash * 31 + c;
            }
            ElementId id = m.addReferenced(cbuf, start, i - start, hash, loc, v.getElemName(), this.mName);
            start = i + 1;
            if (normalize) {
                if (idStr == null) {
                    idStr = id.getId();
                } else {
                    if (sb == null) {
                        sb = new StringBuilder(idStr);
                    }
                    idStr = id.getId();
                    sb.append(' ');
                    sb.append(idStr);
                }
            }
            while (start <= end && WstxInputData.isSpaceChar(cbuf[start])) {
                ++start;
            }
        }
        if (normalize) {
            if (sb != null) {
                idStr = sb.toString();
            }
            return idStr;
        }
        return null;
    }

    @Override
    public void validateDefault(InputProblemReporter rep, boolean normalize) throws XMLStreamException {
        String def = this.validateDefaultNames(rep, normalize);
        if (normalize) {
            this.mDefValue.setValue(def);
        }
    }
}

