/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.DTDAttribute;
import com.ctc.wstx.dtd.DTDValidatorBase;
import com.ctc.wstx.dtd.DefaultAttrValue;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.PrefixedName;
import com.ctc.wstx.util.WordResolver;
import javax.xml.stream.XMLStreamException;

public final class DTDNotationAttr
extends DTDAttribute {
    final WordResolver mEnumValues;

    public DTDNotationAttr(PrefixedName name, DefaultAttrValue defValue, int specIndex, boolean nsAware, boolean xml11, WordResolver enumValues) {
        super(name, defValue, specIndex, nsAware, xml11);
        this.mEnumValues = enumValues;
    }

    @Override
    public DTDAttribute cloneWith(int specIndex) {
        return new DTDNotationAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11, this.mEnumValues);
    }

    @Override
    public int getValueType() {
        return 7;
    }

    @Override
    public boolean typeIsNotation() {
        return true;
    }

    @Override
    public String validate(DTDValidatorBase v, char[] cbuf, int start, int end, boolean normalize) throws XMLStreamException {
        String ok = this.validateEnumValue(cbuf, start, end, normalize, this.mEnumValues);
        if (ok == null) {
            String val = new String(cbuf, start, end - start);
            return this.reportValidationProblem(v, "Invalid notation value '" + val + "': has to be one of (" + this.mEnumValues + ")");
        }
        return ok;
    }

    @Override
    public void validateDefault(InputProblemReporter rep, boolean normalize) throws XMLStreamException {
        String def = this.validateDefaultName(rep, normalize);
        String shared = this.mEnumValues.find(def);
        if (shared == null) {
            this.reportValidationProblem(rep, "Invalid default value '" + def + "': has to be one of (" + this.mEnumValues + ")");
        }
        if (normalize) {
            this.mDefValue.setValue(shared);
        }
    }
}

