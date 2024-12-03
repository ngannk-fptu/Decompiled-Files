/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.DTDAttribute;
import com.ctc.wstx.dtd.DTDValidatorBase;
import com.ctc.wstx.dtd.DefaultAttrValue;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.PrefixedName;
import javax.xml.stream.XMLStreamException;

public final class DTDNmTokensAttr
extends DTDAttribute {
    public DTDNmTokensAttr(PrefixedName name, DefaultAttrValue defValue, int specIndex, boolean nsAware, boolean xml11) {
        super(name, defValue, specIndex, nsAware, xml11);
    }

    @Override
    public DTDAttribute cloneWith(int specIndex) {
        return new DTDNmTokensAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11);
    }

    @Override
    public int getValueType() {
        return 9;
    }

    @Override
    public String validate(DTDValidatorBase v, char[] cbuf, int start, int end, boolean normalize) throws XMLStreamException {
        while (start < end && WstxInputData.isSpaceChar(cbuf[start])) {
            ++start;
        }
        if (start >= end) {
            return this.reportValidationProblem(v, "Empty NMTOKENS value");
        }
        if (!normalize) {
            while (start < end) {
                char c = cbuf[start];
                if (!WstxInputData.isSpaceChar(c) && !WstxInputData.isNameChar(c, this.mCfgNsAware, this.mCfgXml11)) {
                    return this.reportInvalidChar(v, c, "not valid as NMTOKENS character");
                }
                ++start;
            }
            return null;
        }
        --end;
        while (end > start && WstxInputData.isSpaceChar(cbuf[end])) {
            --end;
        }
        StringBuilder sb = null;
        while (start <= end) {
            char c;
            int i;
            for (i = start; i <= end && !WstxInputData.isSpaceChar(c = cbuf[i]); ++i) {
                if (WstxInputData.isNameChar(c, this.mCfgNsAware, this.mCfgXml11)) continue;
                return this.reportInvalidChar(v, c, "not valid as an NMTOKENS character");
            }
            if (sb == null) {
                sb = new StringBuilder(end - start + 1);
            } else {
                sb.append(' ');
            }
            sb.append(cbuf, start, i - start);
            for (start = i + 1; start <= end && WstxInputData.isSpaceChar(cbuf[start]); ++start) {
            }
        }
        return sb == null ? null : sb.toString();
    }

    @Override
    public void validateDefault(InputProblemReporter rep, boolean normalize) throws XMLStreamException {
        String defValue = this.mDefValue.getValue();
        int len = defValue.length();
        StringBuilder sb = null;
        int count = 0;
        int start = 0;
        block0: while (start < len) {
            char c = defValue.charAt(start);
            while (WstxInputData.isSpaceChar(c)) {
                if (++start >= len) break block0;
                c = defValue.charAt(start);
            }
            int i = start + 1;
            while (++i < len && !WstxInputData.isSpaceChar(c = defValue.charAt(i))) {
            }
            ++count;
            String token = defValue.substring(start, i);
            int illegalIx = WstxInputData.findIllegalNmtokenChar(token, this.mCfgNsAware, this.mCfgXml11);
            if (illegalIx >= 0) {
                this.reportValidationProblem(rep, "Invalid default value '" + defValue + "'; character #" + illegalIx + " (" + WstxInputData.getCharDesc(defValue.charAt(illegalIx)) + ") not a valid NMTOKENS character");
            }
            if (normalize) {
                if (sb == null) {
                    sb = new StringBuilder(i - start + 32);
                } else {
                    sb.append(' ');
                }
                sb.append(token);
            }
            start = i + 1;
        }
        if (count == 0) {
            this.reportValidationProblem(rep, "Invalid default value '" + defValue + "'; empty String is not a valid NMTOKENS value");
            return;
        }
        if (sb != null) {
            this.mDefValue.setValue(sb.toString());
        }
    }
}

