/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.DTDValidatorBase;
import com.ctc.wstx.dtd.DefaultAttrValue;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.PrefixedName;
import com.ctc.wstx.util.StringUtil;
import com.ctc.wstx.util.WordResolver;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.XMLValidator;

public abstract class DTDAttribute {
    static final char CHAR_SPACE = ' ';
    public static final int TYPE_CDATA = 0;
    public static final int TYPE_ENUMERATED = 1;
    public static final int TYPE_ID = 2;
    public static final int TYPE_IDREF = 3;
    public static final int TYPE_IDREFS = 4;
    public static final int TYPE_ENTITY = 5;
    public static final int TYPE_ENTITIES = 6;
    public static final int TYPE_NOTATION = 7;
    public static final int TYPE_NMTOKEN = 8;
    public static final int TYPE_NMTOKENS = 9;
    static final String[] sTypes = new String[]{"CDATA", "ENUMERATED", "ID", "IDREF", "IDREFS", "ENTITY", "ENTITIES", "NOTATION", "NMTOKEN", "NMTOKENS"};
    protected final PrefixedName mName;
    protected final int mSpecialIndex;
    protected final DefaultAttrValue mDefValue;
    protected final boolean mCfgNsAware;
    protected final boolean mCfgXml11;

    public DTDAttribute(PrefixedName name, DefaultAttrValue defValue, int specIndex, boolean nsAware, boolean xml11) {
        this.mName = name;
        this.mDefValue = defValue;
        this.mSpecialIndex = specIndex;
        this.mCfgNsAware = nsAware;
        this.mCfgXml11 = xml11;
    }

    public abstract DTDAttribute cloneWith(int var1);

    public final PrefixedName getName() {
        return this.mName;
    }

    public final String toString() {
        return this.mName.toString();
    }

    public final String getDefaultValue(ValidationContext ctxt, XMLValidator dtd) throws XMLStreamException {
        String val = this.mDefValue.getValueIfOk();
        if (val == null) {
            this.mDefValue.reportUndeclared(ctxt, dtd);
            val = this.mDefValue.getValue();
        }
        return val;
    }

    public final int getSpecialIndex() {
        return this.mSpecialIndex;
    }

    public final boolean needsValidation() {
        return this.getValueType() != 0;
    }

    public final boolean isFixed() {
        return this.mDefValue.isFixed();
    }

    public final boolean isRequired() {
        return this.mDefValue.isRequired();
    }

    public final boolean isSpecial() {
        return this.mDefValue.isSpecial();
    }

    public final boolean hasDefaultValue() {
        return this.mDefValue.hasDefaultValue();
    }

    public int getValueType() {
        return 0;
    }

    public String getValueTypeString() {
        return sTypes[this.getValueType()];
    }

    public boolean typeIsId() {
        return false;
    }

    public boolean typeIsNotation() {
        return false;
    }

    public abstract String validate(DTDValidatorBase var1, char[] var2, int var3, int var4, boolean var5) throws XMLStreamException;

    public String validate(DTDValidatorBase v, String value, boolean normalize) throws XMLStreamException {
        int len = value.length();
        char[] cbuf = v.getTempAttrValueBuffer(value.length());
        if (len > 0) {
            value.getChars(0, len, cbuf, 0);
        }
        return this.validate(v, cbuf, 0, len, normalize);
    }

    public abstract void validateDefault(InputProblemReporter var1, boolean var2) throws XMLStreamException;

    public String normalize(DTDValidatorBase v, char[] cbuf, int start, int end) {
        return StringUtil.normalizeSpaces(cbuf, start, end);
    }

    public void normalizeDefault() {
        char[] cbuf;
        String str;
        String val = this.mDefValue.getValue();
        if (val.length() > 0 && (str = StringUtil.normalizeSpaces(cbuf = val.toCharArray(), 0, cbuf.length)) != null) {
            this.mDefValue.setValue(str);
        }
    }

    protected String validateDefaultName(InputProblemReporter rep, boolean normalize) throws XMLStreamException {
        int illegalIx;
        String origDefValue = this.mDefValue.getValue();
        String defValue = origDefValue.trim();
        if (defValue.length() == 0) {
            this.reportValidationProblem(rep, "Invalid default value '" + defValue + "'; empty String is not a valid name");
        }
        if ((illegalIx = WstxInputData.findIllegalNameChar(defValue, this.mCfgNsAware, this.mCfgXml11)) >= 0) {
            if (illegalIx == 0) {
                this.reportValidationProblem(rep, "Invalid default value '" + defValue + "'; character " + WstxInputData.getCharDesc(defValue.charAt(0)) + ") not valid first character of a name");
            } else {
                this.reportValidationProblem(rep, "Invalid default value '" + defValue + "'; character #" + illegalIx + " (" + WstxInputData.getCharDesc(defValue.charAt(illegalIx)) + ") not valid name character");
            }
        }
        return normalize ? defValue : origDefValue;
    }

    protected String validateDefaultNames(InputProblemReporter rep, boolean normalize) throws XMLStreamException {
        String defValue = this.mDefValue.getValue().trim();
        int len = defValue.length();
        StringBuffer sb = null;
        int count = 0;
        int start = 0;
        block0: while (start < len) {
            int i;
            char c = defValue.charAt(start);
            while (WstxInputData.isSpaceChar(c)) {
                if (++start >= len) break block0;
                c = defValue.charAt(start);
            }
            for (i = start + 1; i < len && !WstxInputData.isSpaceChar(defValue.charAt(i)); ++i) {
            }
            String token = defValue.substring(start, i);
            int illegalIx = WstxInputData.findIllegalNameChar(token, this.mCfgNsAware, this.mCfgXml11);
            if (illegalIx >= 0) {
                if (illegalIx == 0) {
                    this.reportValidationProblem(rep, "Invalid default value '" + defValue + "'; character " + WstxInputData.getCharDesc(defValue.charAt(start)) + ") not valid first character of a name token");
                } else {
                    this.reportValidationProblem(rep, "Invalid default value '" + defValue + "'; character " + WstxInputData.getCharDesc(c) + ") not a valid name character");
                }
            }
            ++count;
            if (normalize) {
                if (sb == null) {
                    sb = new StringBuffer(i - start + 32);
                } else {
                    sb.append(' ');
                }
                sb.append(token);
            }
            start = i + 1;
        }
        if (count == 0) {
            this.reportValidationProblem(rep, "Invalid default value '" + defValue + "'; empty String is not a valid name value");
        }
        return normalize ? sb.toString() : defValue;
    }

    protected String validateDefaultNmToken(InputProblemReporter rep, boolean normalize) throws XMLStreamException {
        int illegalIx;
        String origDefValue = this.mDefValue.getValue();
        String defValue = origDefValue.trim();
        if (defValue.length() == 0) {
            this.reportValidationProblem(rep, "Invalid default value '" + defValue + "'; empty String is not a valid NMTOKEN");
        }
        if ((illegalIx = WstxInputData.findIllegalNmtokenChar(defValue, this.mCfgNsAware, this.mCfgXml11)) >= 0) {
            this.reportValidationProblem(rep, "Invalid default value '" + defValue + "'; character #" + illegalIx + " (" + WstxInputData.getCharDesc(defValue.charAt(illegalIx)) + ") not valid NMTOKEN character");
        }
        return normalize ? defValue : origDefValue;
    }

    public String validateEnumValue(char[] cbuf, int start, int end, boolean normalize, WordResolver res) {
        if (normalize) {
            while (start < end && cbuf[start] <= ' ') {
                ++start;
            }
            while (--end > start && cbuf[end] <= ' ') {
            }
            ++end;
        }
        if (start >= end) {
            return null;
        }
        return res.find(cbuf, start, end);
    }

    protected EntityDecl findEntityDecl(DTDValidatorBase v, char[] ch, int start, int len, int hash) throws XMLStreamException {
        String id;
        Map entMap = v.getEntityMap();
        EntityDecl ent = (EntityDecl)entMap.get(id = new String(ch, start, len));
        if (ent == null) {
            this.reportValidationProblem(v, "Referenced entity '" + id + "' not defined");
        } else if (ent.isParsed()) {
            this.reportValidationProblem(v, "Referenced entity '" + id + "' is not an unparsed entity");
        }
        return ent;
    }

    protected void checkEntity(InputProblemReporter rep, String id, EntityDecl ent) throws XMLStreamException {
        if (ent == null) {
            rep.reportValidationProblem("Referenced entity '" + id + "' not defined");
        } else if (ent.isParsed()) {
            rep.reportValidationProblem("Referenced entity '" + id + "' is not an unparsed entity");
        }
    }

    protected String reportInvalidChar(DTDValidatorBase v, char c, String msg) throws XMLStreamException {
        this.reportValidationProblem(v, "Invalid character " + WstxInputData.getCharDesc(c) + ": " + msg);
        return null;
    }

    protected String reportValidationProblem(DTDValidatorBase v, String msg) throws XMLStreamException {
        v.reportValidationProblem("Attribute '" + this.mName + "': " + msg);
        return null;
    }

    protected String reportValidationProblem(InputProblemReporter rep, String msg) throws XMLStreamException {
        rep.reportValidationProblem("Attribute definition '" + this.mName + "': " + msg);
        return null;
    }
}

