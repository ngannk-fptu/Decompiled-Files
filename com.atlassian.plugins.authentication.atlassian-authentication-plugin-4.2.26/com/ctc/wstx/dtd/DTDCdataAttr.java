/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.DTDAttribute;
import com.ctc.wstx.dtd.DTDValidatorBase;
import com.ctc.wstx.dtd.DefaultAttrValue;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.PrefixedName;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationException;

public final class DTDCdataAttr
extends DTDAttribute {
    public DTDCdataAttr(PrefixedName name, DefaultAttrValue defValue, int specIndex, boolean nsAware, boolean xml11) {
        super(name, defValue, specIndex, nsAware, xml11);
    }

    @Override
    public DTDAttribute cloneWith(int specIndex) {
        return new DTDCdataAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11);
    }

    @Override
    public String validate(DTDValidatorBase v, char[] cbuf, int start, int end, boolean normalize) throws XMLValidationException {
        return null;
    }

    @Override
    public void validateDefault(InputProblemReporter rep, boolean normalize) throws XMLStreamException {
    }

    @Override
    public String normalize(DTDValidatorBase v, char[] cbuf, int start, int end) {
        return null;
    }

    @Override
    public void normalizeDefault() {
    }
}

