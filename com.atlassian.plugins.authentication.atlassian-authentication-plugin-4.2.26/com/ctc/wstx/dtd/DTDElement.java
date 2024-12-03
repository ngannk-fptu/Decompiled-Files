/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.dtd.DTDAttribute;
import com.ctc.wstx.dtd.DTDCdataAttr;
import com.ctc.wstx.dtd.DTDEntitiesAttr;
import com.ctc.wstx.dtd.DTDEntityAttr;
import com.ctc.wstx.dtd.DTDEnumAttr;
import com.ctc.wstx.dtd.DTDIdAttr;
import com.ctc.wstx.dtd.DTDIdRefAttr;
import com.ctc.wstx.dtd.DTDIdRefsAttr;
import com.ctc.wstx.dtd.DTDNmTokenAttr;
import com.ctc.wstx.dtd.DTDNmTokensAttr;
import com.ctc.wstx.dtd.DTDNotationAttr;
import com.ctc.wstx.dtd.DefaultAttrValue;
import com.ctc.wstx.dtd.StructValidator;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.ExceptionUtil;
import com.ctc.wstx.util.PrefixedName;
import com.ctc.wstx.util.WordResolver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

public final class DTDElement {
    final PrefixedName mName;
    final Location mLocation;
    StructValidator mValidator;
    int mAllowedContent;
    final boolean mNsAware;
    final boolean mXml11;
    HashMap<PrefixedName, DTDAttribute> mAttrMap = null;
    ArrayList<DTDAttribute> mSpecAttrList = null;
    boolean mAnyFixed = false;
    boolean mAnyDefaults = false;
    boolean mValidateAttrs = false;
    DTDAttribute mIdAttr;
    DTDAttribute mNotationAttr;
    HashMap<String, DTDAttribute> mNsDefaults = null;

    private DTDElement(Location loc, PrefixedName name, StructValidator val, int allowedContent, boolean nsAware, boolean xml11) {
        this.mName = name;
        this.mLocation = loc;
        this.mValidator = val;
        this.mAllowedContent = allowedContent;
        this.mNsAware = nsAware;
        this.mXml11 = xml11;
    }

    public static DTDElement createDefined(ReaderConfig cfg, Location loc, PrefixedName name, StructValidator val, int allowedContent) {
        if (allowedContent == 5) {
            ExceptionUtil.throwInternal("trying to use XMLValidator.CONTENT_ALLOW_UNDEFINED via createDefined()");
        }
        return new DTDElement(loc, name, val, allowedContent, cfg.willSupportNamespaces(), cfg.isXml11());
    }

    public static DTDElement createPlaceholder(ReaderConfig cfg, Location loc, PrefixedName name) {
        return new DTDElement(loc, name, null, 5, cfg.willSupportNamespaces(), cfg.isXml11());
    }

    public DTDElement define(Location loc, StructValidator val, int allowedContent) {
        this.verifyUndefined();
        if (allowedContent == 5) {
            ExceptionUtil.throwInternal("trying to use CONTENT_ALLOW_UNDEFINED via define()");
        }
        DTDElement elem = new DTDElement(loc, this.mName, val, allowedContent, this.mNsAware, this.mXml11);
        elem.mAttrMap = this.mAttrMap;
        elem.mSpecAttrList = this.mSpecAttrList;
        elem.mAnyFixed = this.mAnyFixed;
        elem.mValidateAttrs = this.mValidateAttrs;
        elem.mAnyDefaults = this.mAnyDefaults;
        elem.mIdAttr = this.mIdAttr;
        elem.mNotationAttr = this.mNotationAttr;
        elem.mNsDefaults = this.mNsDefaults;
        return elem;
    }

    public void defineFrom(InputProblemReporter rep, DTDElement definedElem, boolean fullyValidate) throws XMLStreamException {
        if (fullyValidate) {
            this.verifyUndefined();
        }
        this.mValidator = definedElem.mValidator;
        this.mAllowedContent = definedElem.mAllowedContent;
        this.mergeMissingAttributesFrom(rep, definedElem, fullyValidate);
    }

    private void verifyUndefined() {
        if (this.mAllowedContent != 5) {
            ExceptionUtil.throwInternal("redefining defined element spec");
        }
    }

    public DTDAttribute addAttribute(InputProblemReporter rep, PrefixedName attrName, int valueType, DefaultAttrValue defValue, WordResolver enumValues, boolean fullyValidate) throws XMLStreamException {
        DTDAttribute attr;
        HashMap<PrefixedName, DTDAttribute> m = this.mAttrMap;
        if (m == null) {
            this.mAttrMap = m = new HashMap();
        }
        List<DTDAttribute> specList = defValue.isSpecial() ? this.getSpecialList() : null;
        int specIndex = specList == null ? -1 : specList.size();
        switch (valueType) {
            case 0: {
                attr = new DTDCdataAttr(attrName, defValue, specIndex, this.mNsAware, this.mXml11);
                break;
            }
            case 1: {
                attr = new DTDEnumAttr(attrName, defValue, specIndex, this.mNsAware, this.mXml11, enumValues);
                break;
            }
            case 2: {
                attr = new DTDIdAttr(attrName, defValue, specIndex, this.mNsAware, this.mXml11);
                break;
            }
            case 3: {
                attr = new DTDIdRefAttr(attrName, defValue, specIndex, this.mNsAware, this.mXml11);
                break;
            }
            case 4: {
                attr = new DTDIdRefsAttr(attrName, defValue, specIndex, this.mNsAware, this.mXml11);
                break;
            }
            case 5: {
                attr = new DTDEntityAttr(attrName, defValue, specIndex, this.mNsAware, this.mXml11);
                break;
            }
            case 6: {
                attr = new DTDEntitiesAttr(attrName, defValue, specIndex, this.mNsAware, this.mXml11);
                break;
            }
            case 7: {
                attr = new DTDNotationAttr(attrName, defValue, specIndex, this.mNsAware, this.mXml11, enumValues);
                break;
            }
            case 8: {
                attr = new DTDNmTokenAttr(attrName, defValue, specIndex, this.mNsAware, this.mXml11);
                break;
            }
            case 9: {
                attr = new DTDNmTokensAttr(attrName, defValue, specIndex, this.mNsAware, this.mXml11);
                break;
            }
            default: {
                ExceptionUtil.throwGenericInternal();
                attr = null;
            }
        }
        DTDAttribute old = this.doAddAttribute(m, rep, attr, specList, fullyValidate);
        return old == null ? attr : null;
    }

    public DTDAttribute addNsDefault(InputProblemReporter rep, PrefixedName attrName, int valueType, DefaultAttrValue defValue, boolean fullyValidate) throws XMLStreamException {
        DTDAttribute nsAttr;
        switch (valueType) {
            case 0: {
                nsAttr = new DTDCdataAttr(attrName, defValue, -1, this.mNsAware, this.mXml11);
                break;
            }
            default: {
                nsAttr = new DTDNmTokenAttr(attrName, defValue, -1, this.mNsAware, this.mXml11);
            }
        }
        String prefix = attrName.getPrefix();
        prefix = prefix == null || prefix.length() == 0 ? "" : attrName.getLocalName();
        if (this.mNsDefaults == null) {
            this.mNsDefaults = new HashMap();
        } else if (this.mNsDefaults.containsKey(prefix)) {
            return null;
        }
        this.mNsDefaults.put(prefix, nsAttr);
        return nsAttr;
    }

    public void mergeMissingAttributesFrom(InputProblemReporter rep, DTDElement other, boolean fullyValidate) throws XMLStreamException {
        HashMap<String, DTDAttribute> otherNs;
        HashMap<PrefixedName, DTDAttribute> otherMap = other.getAttributes();
        HashMap<PrefixedName, DTDAttribute> m = this.mAttrMap;
        if (m == null) {
            m = new HashMap();
            this.mAttrMap = m;
        }
        if (otherMap != null && otherMap.size() > 0) {
            for (Map.Entry me : otherMap.entrySet()) {
                List<DTDAttribute> specList;
                PrefixedName key = (PrefixedName)me.getKey();
                if (m.containsKey(key)) continue;
                DTDAttribute newAttr = (DTDAttribute)me.getValue();
                if (newAttr.isSpecial()) {
                    specList = this.getSpecialList();
                    newAttr = newAttr.cloneWith(specList.size());
                } else {
                    specList = null;
                }
                this.doAddAttribute(m, rep, newAttr, specList, fullyValidate);
            }
        }
        if ((otherNs = other.mNsDefaults) != null) {
            if (this.mNsDefaults == null) {
                this.mNsDefaults = new HashMap();
            }
            for (Map.Entry<String, DTDAttribute> en : otherNs.entrySet()) {
                String prefix = en.getKey();
                if (this.mNsDefaults.containsKey(prefix)) continue;
                this.mNsDefaults.put(prefix, en.getValue());
            }
        }
    }

    private DTDAttribute doAddAttribute(Map<PrefixedName, DTDAttribute> attrMap, InputProblemReporter rep, DTDAttribute attr, List<DTDAttribute> specList, boolean fullyValidate) throws XMLStreamException {
        PrefixedName attrName = attr.getName();
        DTDAttribute old = attrMap.get(attrName);
        if (old != null) {
            rep.reportProblem(null, ErrorConsts.WT_ATTR_DECL, ErrorConsts.W_DTD_DUP_ATTR, attrName, this.mName);
            return old;
        }
        switch (attr.getValueType()) {
            case 2: {
                if (fullyValidate && this.mIdAttr != null) {
                    rep.throwParseError("Invalid id attribute \"{0}\" for element <{1}>: already had id attribute \"" + this.mIdAttr.getName() + "\"", attrName, this.mName);
                }
                this.mIdAttr = attr;
                break;
            }
            case 7: {
                if (fullyValidate && this.mNotationAttr != null) {
                    rep.throwParseError("Invalid notation attribute '" + attrName + "' for element <" + this.mName + ">: already had notation attribute '" + this.mNotationAttr.getName() + "'");
                }
                this.mNotationAttr = attr;
            }
        }
        attrMap.put(attrName, attr);
        if (specList != null) {
            specList.add(attr);
        }
        if (!this.mAnyFixed) {
            this.mAnyFixed = attr.isFixed();
        }
        if (!this.mValidateAttrs) {
            this.mValidateAttrs = attr.needsValidation();
        }
        if (!this.mAnyDefaults) {
            this.mAnyDefaults = attr.hasDefaultValue();
        }
        return null;
    }

    public PrefixedName getName() {
        return this.mName;
    }

    public String toString() {
        return this.mName.toString();
    }

    public String getDisplayName() {
        return this.mName.toString();
    }

    public Location getLocation() {
        return this.mLocation;
    }

    public boolean isDefined() {
        return this.mAllowedContent != 5;
    }

    public int getAllowedContent() {
        return this.mAllowedContent;
    }

    public int getAllowedContentIfSpace() {
        int vld = this.mAllowedContent;
        return vld <= 1 ? 2 : 4;
    }

    public HashMap<PrefixedName, DTDAttribute> getAttributes() {
        return this.mAttrMap;
    }

    public int getSpecialCount() {
        return this.mSpecAttrList == null ? 0 : this.mSpecAttrList.size();
    }

    public List<DTDAttribute> getSpecialAttrs() {
        return this.mSpecAttrList;
    }

    public boolean attrsNeedValidation() {
        return this.mValidateAttrs;
    }

    public boolean hasFixedAttrs() {
        return this.mAnyFixed;
    }

    public boolean hasAttrDefaultValues() {
        return this.mAnyDefaults;
    }

    public DTDAttribute getIdAttribute() {
        return this.mIdAttr;
    }

    public DTDAttribute getNotationAttribute() {
        return this.mNotationAttr;
    }

    public boolean hasNsDefaults() {
        return this.mNsDefaults != null;
    }

    public StructValidator getValidator() {
        return this.mValidator == null ? null : this.mValidator.newInstance();
    }

    protected HashMap<String, DTDAttribute> getNsDefaults() {
        return this.mNsDefaults;
    }

    private List<DTDAttribute> getSpecialList() {
        ArrayList<DTDAttribute> l = this.mSpecAttrList;
        if (l == null) {
            this.mSpecAttrList = l = new ArrayList();
        }
        return l;
    }
}

