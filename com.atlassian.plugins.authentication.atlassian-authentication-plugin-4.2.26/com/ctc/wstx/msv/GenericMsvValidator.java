/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.msv;

import com.ctc.wstx.msv.AttributeProxy;
import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2;
import com.ctc.wstx.shaded.msv_core.util.DatatypeRef;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.StringRef;
import com.ctc.wstx.shaded.msv_core.verifier.Acceptor;
import com.ctc.wstx.shaded.msv_core.verifier.DocumentDeclaration;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.StringToken;
import com.ctc.wstx.util.ElementId;
import com.ctc.wstx.util.ElementIdMap;
import com.ctc.wstx.util.PrefixedName;
import com.ctc.wstx.util.TextAccumulator;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.XMLValidationProblem;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;
import org.xml.sax.Attributes;

public final class GenericMsvValidator
extends XMLValidator
implements IDContextProvider2,
XMLStreamConstants {
    final XMLValidationSchema mParentSchema;
    final ValidationContext mContext;
    final DocumentDeclaration mVGM;
    final ArrayList<Object> mAcceptors = new ArrayList();
    Acceptor mCurrAcceptor = null;
    final TextAccumulator mTextAccumulator = new TextAccumulator();
    ElementIdMap mIdDefs;
    String mCurrAttrPrefix;
    String mCurrAttrLocalName;
    XMLValidationProblem mProblem;
    final StringRef mErrorRef = new StringRef();
    final StartTagInfo mStartTag = new StartTagInfo("", "", "", null, (IDContextProvider2)null);
    protected String mStartTagPrefix = "";
    final AttributeProxy mAttributeProxy;

    public GenericMsvValidator(XMLValidationSchema parent, ValidationContext ctxt, DocumentDeclaration vgm) {
        this.mParentSchema = parent;
        this.mContext = ctxt;
        this.mVGM = vgm;
        this.mCurrAcceptor = this.mVGM.createAcceptor();
        this.mAttributeProxy = new AttributeProxy(ctxt);
    }

    @Override
    public String getBaseUri() {
        return this.mContext.getBaseUri();
    }

    @Override
    public boolean isNotation(String notationName) {
        return this.mContext.isNotationDeclared(notationName);
    }

    @Override
    public boolean isUnparsedEntity(String entityName) {
        return this.mContext.isUnparsedEntityDeclared(entityName);
    }

    @Override
    public String resolveNamespacePrefix(String prefix) {
        return this.mContext.getNamespaceURI(prefix);
    }

    @Override
    public void onID(Datatype datatype, StringToken idToken) throws IllegalArgumentException {
        if (this.mIdDefs == null) {
            this.mIdDefs = new ElementIdMap();
        }
        int idType = datatype.getIdType();
        Location loc = this.mContext.getValidationLocation();
        PrefixedName elemPName = this.getElementPName();
        PrefixedName attrPName = this.getAttrPName();
        if (idType == 1) {
            String idStr = idToken.literal.trim();
            ElementId eid = this.mIdDefs.addDefined(idStr, loc, elemPName, attrPName);
            if (eid.getLocation() != loc) {
                this.mProblem = new XMLValidationProblem(loc, "Duplicate id '" + idStr + "', first declared at " + eid.getLocation());
                this.mProblem.setReporter(this);
            }
        } else if (idType == 2) {
            String idStr = idToken.literal.trim();
            this.mIdDefs.addReferenced(idStr, loc, elemPName, attrPName);
        } else if (idType == 3) {
            StringTokenizer tokens = new StringTokenizer(idToken.literal);
            while (tokens.hasMoreTokens()) {
                this.mIdDefs.addReferenced(tokens.nextToken(), loc, elemPName, attrPName);
            }
        } else {
            throw new IllegalStateException("Internal error: unexpected ID datatype: " + datatype);
        }
    }

    @Override
    public XMLValidationSchema getSchema() {
        return this.mParentSchema;
    }

    @Override
    public void validateElementStart(String localName, String uri, String prefix) throws XMLStreamException {
        if (this.mCurrAcceptor == null) {
            return;
        }
        if (this.mTextAccumulator.hasText()) {
            this.doValidateText(this.mTextAccumulator);
        }
        if (uri == null) {
            uri = "";
        }
        String qname = localName;
        this.mStartTag.reinit(uri, localName, qname, (Attributes)this.mAttributeProxy, this);
        this.mStartTagPrefix = prefix;
        this.mCurrAcceptor = this.mCurrAcceptor.createChildAcceptor(this.mStartTag, this.mErrorRef);
        if (this.mErrorRef.str != null) {
            this.reportError(this.mErrorRef, 1, this._qname(uri, localName, prefix));
        }
        if (this.mProblem != null) {
            XMLValidationProblem p = this.mProblem;
            this.mProblem = null;
            this.mContext.reportProblem(p);
        }
        this.mAcceptors.add(this.mCurrAcceptor);
    }

    @Override
    public String validateAttribute(String localName, String uri, String prefix, String value) throws XMLStreamException {
        this.mCurrAttrLocalName = localName;
        this.mCurrAttrPrefix = prefix;
        if (this.mCurrAcceptor != null) {
            String qname = localName;
            DatatypeRef typeRef = null;
            if (uri == null) {
                uri = "";
            }
            if (!this.mCurrAcceptor.onAttribute2(uri, localName, qname, value, this, this.mErrorRef, typeRef) || this.mErrorRef.str != null) {
                this.reportError(this.mErrorRef, 10, this._qname(uri, localName, prefix));
            }
            if (this.mProblem != null) {
                XMLValidationProblem p = this.mProblem;
                this.mProblem = null;
                this.mContext.reportProblem(p);
            }
        }
        return null;
    }

    @Override
    public String validateAttribute(String localName, String uri, String prefix, char[] valueChars, int valueStart, int valueEnd) throws XMLStreamException {
        int len = valueEnd - valueStart;
        return this.validateAttribute(localName, uri, prefix, new String(valueChars, valueStart, len));
    }

    @Override
    public int validateElementAndAttributes() throws XMLStreamException {
        this.mCurrAttrPrefix = "";
        this.mCurrAttrLocalName = "";
        if (this.mCurrAcceptor != null) {
            if (!this.mCurrAcceptor.onEndAttributes(this.mStartTag, this.mErrorRef) || this.mErrorRef.str != null) {
                this.reportError(this.mErrorRef, 2, this._startTagAsQName());
            }
            int stringChecks = this.mCurrAcceptor.getStringCareLevel();
            switch (stringChecks) {
                case 0: {
                    return 1;
                }
                case 1: {
                    return 4;
                }
                case 2: {
                    return 3;
                }
            }
            throw new IllegalArgumentException("Internal error: unexpected string care level value return by MSV: " + stringChecks);
        }
        return 4;
    }

    @Override
    public int validateElementEnd(String localName, String uri, String prefix) throws XMLStreamException {
        this.doValidateText(this.mTextAccumulator);
        int lastIx = this.mAcceptors.size() - 1;
        if (lastIx < 0) {
            return 1;
        }
        Acceptor acc = (Acceptor)this.mAcceptors.remove(lastIx);
        if (!(acc == null || acc.isAcceptState(this.mErrorRef) && this.mErrorRef.str == null)) {
            this.reportError(this.mErrorRef, 2, this._qname(uri, localName, prefix));
        }
        this.mCurrAcceptor = lastIx == 0 ? null : (Acceptor)this.mAcceptors.get(lastIx - 1);
        if (this.mCurrAcceptor != null && acc != null) {
            if (!this.mCurrAcceptor.stepForward(acc, this.mErrorRef) || this.mErrorRef.str != null) {
                this.reportError(this.mErrorRef, 2, this._qname(uri, localName, prefix));
            }
            int stringChecks = this.mCurrAcceptor.getStringCareLevel();
            switch (stringChecks) {
                case 0: {
                    return 1;
                }
                case 1: {
                    return 4;
                }
                case 2: {
                    return 3;
                }
            }
            throw new IllegalArgumentException("Internal error: unexpected string care level value return by MSV: " + stringChecks);
        }
        return 4;
    }

    @Override
    public void validateText(String text, boolean lastTextSegment) throws XMLStreamException {
        this.mTextAccumulator.addText(text);
        if (lastTextSegment) {
            this.doValidateText(this.mTextAccumulator);
        }
    }

    @Override
    public void validateText(char[] cbuf, int textStart, int textEnd, boolean lastTextSegment) throws XMLStreamException {
        this.mTextAccumulator.addText(cbuf, textStart, textEnd);
        if (lastTextSegment) {
            this.doValidateText(this.mTextAccumulator);
        }
    }

    @Override
    public void validationCompleted(boolean eod) throws XMLStreamException {
        ElementId ref;
        if (eod && this.mIdDefs != null && (ref = this.mIdDefs.getFirstUndefined()) != null) {
            String msg = "Undefined ID '" + ref.getId() + "': referenced from element <" + ref.getElemName() + ">, attribute '" + ref.getAttrName() + "'";
            this.reportError(msg, ref.getLocation());
        }
    }

    @Override
    public String getAttributeType(int index) {
        return null;
    }

    @Override
    public int getIdAttrIndex() {
        return -1;
    }

    @Override
    public int getNotationAttrIndex() {
        return -1;
    }

    PrefixedName getElementPName() {
        return PrefixedName.valueOf(this.mContext.getCurrentElementName());
    }

    PrefixedName getAttrPName() {
        return new PrefixedName(this.mCurrAttrPrefix, this.mCurrAttrLocalName);
    }

    void doValidateText(TextAccumulator textAcc) throws XMLStreamException {
        DatatypeRef typeRef;
        String str;
        if (!(this.mCurrAcceptor == null || this.mCurrAcceptor.onText2(str = textAcc.getAndClear(), this, this.mErrorRef, typeRef = null) && this.mErrorRef.str == null)) {
            this.reportError(this.mErrorRef, 12, this._startTagAsQName());
        }
    }

    private void reportError(StringRef errorRef, int type, QName name) throws XMLStreamException {
        String msg = errorRef.str;
        errorRef.str = null;
        if (msg == null || msg.isEmpty()) {
            switch (type) {
                case 1: {
                    msg = "Unknown reason (at start element " + this._name(name, "<", ">") + ")";
                    break;
                }
                case 2: {
                    msg = "Unknown reason (at end element " + this._name(name, "</", ">") + ")";
                    break;
                }
                case 10: {
                    msg = "Unknown reason (at attribute " + this._name(name, "'", "'") + ")";
                    break;
                }
                default: {
                    msg = "Unknown reason (at CDATA section, inside element " + this._name(name, "<", ">") + ")";
                }
            }
        }
        this.reportError(msg);
    }

    private void reportError(String msg) throws XMLStreamException {
        this.reportError(msg, this.mContext.getValidationLocation());
    }

    private void reportError(String msg, Location loc) throws XMLStreamException {
        XMLValidationProblem prob = new XMLValidationProblem(loc, msg, 2);
        prob.setReporter(this);
        this.mContext.reportProblem(prob);
    }

    private String _name(QName qn, String prefix, String suffix) {
        if (qn == null) {
            return "UNKNOWN";
        }
        String name = qn.getLocalPart();
        String p = qn.getPrefix();
        if (p != null && !p.isEmpty()) {
            name = p + ":" + name;
        }
        return prefix + name + suffix;
    }

    private QName _startTagAsQName() {
        return this._qname(this.mStartTag.namespaceURI, this.mStartTag.localName, this.mStartTagPrefix);
    }

    private QName _qname(String ns, String local, String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        if (ns == null) {
            ns = "";
        }
        if (local == null) {
            local = "";
        }
        return new QName(ns, local, prefix);
    }
}

