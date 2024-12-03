/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.msv.grammar.IDContextProvider2
 *  com.sun.msv.util.DatatypeRef
 *  com.sun.msv.util.StartTagInfo
 *  com.sun.msv.util.StringRef
 *  com.sun.msv.verifier.Acceptor
 *  com.sun.msv.verifier.DocumentDeclaration
 *  com.sun.msv.verifier.regexp.StringToken
 *  org.relaxng.datatype.Datatype
 */
package com.ctc.wstx.msv;

import com.ctc.wstx.msv.AttributeProxy;
import com.ctc.wstx.util.ElementId;
import com.ctc.wstx.util.ElementIdMap;
import com.ctc.wstx.util.PrefixedName;
import com.ctc.wstx.util.TextAccumulator;
import com.sun.msv.grammar.IDContextProvider2;
import com.sun.msv.util.DatatypeRef;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringRef;
import com.sun.msv.verifier.Acceptor;
import com.sun.msv.verifier.DocumentDeclaration;
import com.sun.msv.verifier.regexp.StringToken;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.XMLValidationProblem;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;
import org.relaxng.datatype.Datatype;
import org.xml.sax.Attributes;

public final class GenericMsvValidator
extends XMLValidator
implements IDContextProvider2 {
    protected final XMLValidationSchema mParentSchema;
    protected final ValidationContext mContext;
    protected final DocumentDeclaration mVGM;
    protected final ArrayList mAcceptors = new ArrayList();
    protected Acceptor mCurrAcceptor = null;
    protected final TextAccumulator mTextAccumulator = new TextAccumulator();
    protected ElementIdMap mIdDefs;
    protected String mCurrAttrPrefix;
    protected String mCurrAttrLocalName;
    protected XMLValidationProblem mProblem;
    final StringRef mErrorRef = new StringRef();
    final StartTagInfo mStartTag = new StartTagInfo("", "", "", null, (IDContextProvider2)null);
    final AttributeProxy mAttributeProxy;

    public GenericMsvValidator(XMLValidationSchema parent, ValidationContext ctxt, DocumentDeclaration vgm) {
        this.mParentSchema = parent;
        this.mContext = ctxt;
        this.mVGM = vgm;
        this.mCurrAcceptor = this.mVGM.createAcceptor();
        this.mAttributeProxy = new AttributeProxy(ctxt);
    }

    public String getBaseUri() {
        return this.mContext.getBaseUri();
    }

    public boolean isNotation(String notationName) {
        return this.mContext.isNotationDeclared(notationName);
    }

    public boolean isUnparsedEntity(String entityName) {
        return this.mContext.isUnparsedEntityDeclared(entityName);
    }

    public String resolveNamespacePrefix(String prefix) {
        return this.mContext.getNamespaceURI(prefix);
    }

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

    public XMLValidationSchema getSchema() {
        return this.mParentSchema;
    }

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
        this.mStartTag.reinit(uri, localName, qname, (Attributes)this.mAttributeProxy, (IDContextProvider2)this);
        this.mCurrAcceptor = this.mCurrAcceptor.createChildAcceptor(this.mStartTag, this.mErrorRef);
        if (this.mErrorRef.str != null) {
            this.reportError(this.mErrorRef);
        }
        if (this.mProblem != null) {
            XMLValidationProblem p = this.mProblem;
            this.mProblem = null;
            this.mContext.reportProblem(p);
        }
        this.mAcceptors.add(this.mCurrAcceptor);
    }

    public String validateAttribute(String localName, String uri, String prefix, String value) throws XMLStreamException {
        this.mCurrAttrLocalName = localName;
        this.mCurrAttrPrefix = prefix;
        if (this.mCurrAcceptor != null) {
            String qname = localName;
            DatatypeRef typeRef = null;
            if (uri == null) {
                uri = "";
            }
            if (!this.mCurrAcceptor.onAttribute2(uri, localName, qname, value, (IDContextProvider2)this, this.mErrorRef, typeRef) || this.mErrorRef.str != null) {
                this.reportError(this.mErrorRef);
            }
            if (this.mProblem != null) {
                XMLValidationProblem p = this.mProblem;
                this.mProblem = null;
                this.mContext.reportProblem(p);
            }
        }
        return null;
    }

    public String validateAttribute(String localName, String uri, String prefix, char[] valueChars, int valueStart, int valueEnd) throws XMLStreamException {
        int len = valueEnd - valueStart;
        return this.validateAttribute(localName, uri, prefix, new String(valueChars, valueStart, len));
    }

    public int validateElementAndAttributes() throws XMLStreamException {
        this.mCurrAttrPrefix = "";
        this.mCurrAttrLocalName = "";
        if (this.mCurrAcceptor != null) {
            if (!this.mCurrAcceptor.onEndAttributes(this.mStartTag, this.mErrorRef) || this.mErrorRef.str != null) {
                this.reportError(this.mErrorRef);
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

    public int validateElementEnd(String localName, String uri, String prefix) throws XMLStreamException {
        this.doValidateText(this.mTextAccumulator);
        int lastIx = this.mAcceptors.size() - 1;
        if (lastIx < 0) {
            return 1;
        }
        Acceptor acc = (Acceptor)this.mAcceptors.remove(lastIx);
        if (!(acc == null || acc.isAcceptState(this.mErrorRef) && this.mErrorRef.str == null)) {
            this.reportError(this.mErrorRef);
        }
        this.mCurrAcceptor = lastIx == 0 ? null : (Acceptor)this.mAcceptors.get(lastIx - 1);
        if (this.mCurrAcceptor != null && acc != null) {
            if (!this.mCurrAcceptor.stepForward(acc, this.mErrorRef) || this.mErrorRef.str != null) {
                this.reportError(this.mErrorRef);
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

    public void validateText(String text, boolean lastTextSegment) throws XMLStreamException {
        this.mTextAccumulator.addText(text);
        if (lastTextSegment) {
            this.doValidateText(this.mTextAccumulator);
        }
    }

    public void validateText(char[] cbuf, int textStart, int textEnd, boolean lastTextSegment) throws XMLStreamException {
        this.mTextAccumulator.addText(cbuf, textStart, textEnd);
        if (lastTextSegment) {
            this.doValidateText(this.mTextAccumulator);
        }
    }

    public void validationCompleted(boolean eod) throws XMLStreamException {
        ElementId ref;
        if (eod && this.mIdDefs != null && (ref = this.mIdDefs.getFirstUndefined()) != null) {
            String msg = "Undefined ID '" + ref.getId() + "': referenced from element <" + ref.getElemName() + ">, attribute '" + ref.getAttrName() + "'";
            this.reportError(msg, ref.getLocation());
        }
    }

    public String getAttributeType(int index) {
        return null;
    }

    public int getIdAttrIndex() {
        return -1;
    }

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
        if (!(this.mCurrAcceptor == null || this.mCurrAcceptor.onText2(str = textAcc.getAndClear(), (IDContextProvider2)this, this.mErrorRef, typeRef = null) && this.mErrorRef.str == null)) {
            this.reportError(this.mErrorRef);
        }
    }

    private void reportError(StringRef errorRef) throws XMLStreamException {
        String msg = errorRef.str;
        errorRef.str = null;
        if (msg == null) {
            msg = "Unknown reason";
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
}

