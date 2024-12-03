/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sr;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.compat.QNameCreator;
import com.ctc.wstx.dtd.DTDValidatorBase;
import com.ctc.wstx.sr.Attribute;
import com.ctc.wstx.sr.AttributeCollector;
import com.ctc.wstx.sr.CompactNsContext;
import com.ctc.wstx.sr.Element;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.sr.NsDefaultProvider;
import com.ctc.wstx.util.BaseNsContext;
import com.ctc.wstx.util.EmptyNamespaceContext;
import com.ctc.wstx.util.StringVector;
import com.ctc.wstx.util.TextBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.AttributeInfo;
import org.codehaus.stax2.ri.EmptyIterator;
import org.codehaus.stax2.ri.SingletonIterator;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.ValidatorPair;
import org.codehaus.stax2.validation.XMLValidationProblem;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

public final class InputElementStack
implements AttributeInfo,
NamespaceContext,
ValidationContext {
    static final int ID_ATTR_NONE = -1;
    protected final boolean mNsAware;
    protected final AttributeCollector mAttrCollector;
    protected final ReaderConfig mConfig;
    protected InputProblemReporter mReporter = null;
    protected NsDefaultProvider mNsDefaultProvider;
    protected int mDepth = 0;
    protected long mTotalElements = 0L;
    protected final StringVector mNamespaces = new StringVector(64);
    protected Element mCurrElement;
    protected boolean mMayHaveNsDefaults = false;
    protected XMLValidator mValidator = null;
    protected int mIdAttrIndex = -1;
    protected String mLastLocalName = null;
    protected String mLastPrefix = null;
    protected String mLastNsURI = null;
    protected QName mLastName = null;
    protected BaseNsContext mLastNsContext = null;
    protected Element mFreeElement = null;

    protected InputElementStack(ReaderConfig cfg, boolean nsAware) {
        this.mConfig = cfg;
        this.mNsAware = nsAware;
        this.mAttrCollector = new AttributeCollector(cfg, nsAware);
    }

    protected void connectReporter(InputProblemReporter rep) {
        this.mReporter = rep;
    }

    protected XMLValidator addValidator(XMLValidator vld) {
        this.mValidator = this.mValidator == null ? vld : new ValidatorPair(this.mValidator, vld);
        return vld;
    }

    protected void setAutomaticDTDValidator(XMLValidator validator, NsDefaultProvider nsDefs) {
        this.mNsDefaultProvider = nsDefs;
        this.addValidator(validator);
    }

    public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return this.addValidator(schema.createValidator(this));
    }

    public XMLValidator stopValidatingAgainst(XMLValidationSchema schema) throws XMLStreamException {
        XMLValidator[] results = new XMLValidator[2];
        if (ValidatorPair.removeValidator(this.mValidator, schema, results)) {
            XMLValidator found = results[0];
            this.mValidator = results[1];
            found.validationCompleted(false);
            return found;
        }
        return null;
    }

    public XMLValidator stopValidatingAgainst(XMLValidator validator) throws XMLStreamException {
        XMLValidator[] results = new XMLValidator[2];
        if (ValidatorPair.removeValidator(this.mValidator, validator, results)) {
            XMLValidator found = results[0];
            this.mValidator = results[1];
            found.validationCompleted(false);
            return found;
        }
        return null;
    }

    protected boolean reallyValidating() {
        if (this.mValidator == null) {
            return false;
        }
        if (!(this.mValidator instanceof DTDValidatorBase)) {
            return true;
        }
        return ((DTDValidatorBase)this.mValidator).reallyValidating();
    }

    public final AttributeCollector getAttrCollector() {
        return this.mAttrCollector;
    }

    public BaseNsContext createNonTransientNsContext(Location loc) {
        if (this.mLastNsContext != null) {
            return this.mLastNsContext;
        }
        int totalNsSize = this.mNamespaces.size();
        if (totalNsSize < 1) {
            this.mLastNsContext = EmptyNamespaceContext.getInstance();
            return this.mLastNsContext;
        }
        int localCount = this.getCurrentNsCount() << 1;
        CompactNsContext nsCtxt = new CompactNsContext(loc, this.getDefaultNsURI(), this.mNamespaces.asArray(), totalNsSize, totalNsSize - localCount);
        if (localCount == 0) {
            this.mLastNsContext = nsCtxt;
        }
        return nsCtxt;
    }

    public final void push(String prefix, String localName) throws XMLStreamException {
        String defaultNs;
        if (++this.mDepth > this.mConfig.getMaxElementDepth()) {
            throw new XMLStreamException("Maximum Element Depth (" + this.mConfig.getMaxElementDepth() + ") Exceeded");
        }
        if (++this.mTotalElements > this.mConfig.getMaxElementCount()) {
            throw new XMLStreamException("Maximum Element Count (" + this.mConfig.getMaxElementCount() + ") Exceeded");
        }
        String string = defaultNs = this.mCurrElement == null ? "" : this.mCurrElement.mDefaultNsURI;
        if (this.mCurrElement != null) {
            ++this.mCurrElement.mChildCount;
            int max = this.mConfig.getMaxChildrenPerElement();
            if (max > 0 && this.mCurrElement.mChildCount > max) {
                throw new XMLStreamException("Maximum Number of Children Elements (" + max + ") Exceeded");
            }
        }
        if (this.mFreeElement == null) {
            this.mCurrElement = new Element(this.mCurrElement, this.mNamespaces.size(), prefix, localName);
        } else {
            Element newElem = this.mFreeElement;
            this.mFreeElement = newElem.mParent;
            newElem.reset(this.mCurrElement, this.mNamespaces.size(), prefix, localName);
            this.mCurrElement = newElem;
        }
        this.mCurrElement.mDefaultNsURI = defaultNs;
        this.mAttrCollector.reset();
        if (this.mNsDefaultProvider != null) {
            this.mMayHaveNsDefaults = this.mNsDefaultProvider.mayHaveNsDefaults(prefix, localName);
        }
    }

    public final boolean pop() throws XMLStreamException {
        Element parent;
        if (this.mCurrElement == null) {
            throw new IllegalStateException("Popping from empty stack");
        }
        --this.mDepth;
        Element child = this.mCurrElement;
        this.mCurrElement = parent = child.mParent;
        child.relink(this.mFreeElement);
        this.mFreeElement = child;
        int nsCount = this.mNamespaces.size() - child.mNsOffset;
        if (nsCount > 0) {
            this.mLastNsContext = null;
            this.mNamespaces.removeLast(nsCount);
        }
        return parent != null;
    }

    public int resolveAndValidateElement() throws XMLStreamException {
        int xmlidIx;
        String ns;
        String prefix;
        if (this.mDepth == 0) {
            throw new IllegalStateException("Calling validate() on empty stack.");
        }
        AttributeCollector ac = this.mAttrCollector;
        int nsCount = ac.getNsCount();
        if (nsCount > 0) {
            this.mLastNsContext = null;
            boolean internNsUris = this.mConfig.willInternNsURIs();
            for (int i = 0; i < nsCount; ++i) {
                Attribute ns2 = ac.resolveNamespaceDecl(i, internNsUris);
                String nsUri = ns2.mNamespaceURI;
                String prefix2 = ns2.mLocalName;
                if (prefix2 == "xmlns") {
                    this.mReporter.throwParseError(ErrorConsts.ERR_NS_REDECL_XMLNS);
                    continue;
                }
                if (prefix2 == "xml") {
                    if (nsUri.equals("http://www.w3.org/XML/1998/namespace")) continue;
                    this.mReporter.throwParseError(ErrorConsts.ERR_NS_REDECL_XML, nsUri, null);
                    continue;
                }
                if (nsUri == null || nsUri.length() == 0) {
                    nsUri = "";
                }
                if (prefix2 == null) {
                    this.mCurrElement.mDefaultNsURI = nsUri;
                }
                if (internNsUris) {
                    if (nsUri == "http://www.w3.org/XML/1998/namespace") {
                        this.mReporter.throwParseError(ErrorConsts.ERR_NS_REDECL_XML_URI, prefix2, null);
                    } else if (nsUri == "http://www.w3.org/2000/xmlns/") {
                        this.mReporter.throwParseError(ErrorConsts.ERR_NS_REDECL_XMLNS_URI);
                    }
                } else if (nsUri.equals("http://www.w3.org/XML/1998/namespace")) {
                    this.mReporter.throwParseError(ErrorConsts.ERR_NS_REDECL_XML_URI, prefix2, null);
                } else if (nsUri.equals("http://www.w3.org/2000/xmlns/")) {
                    this.mReporter.throwParseError(ErrorConsts.ERR_NS_REDECL_XMLNS_URI);
                }
                this.mNamespaces.addStrings(prefix2, nsUri);
            }
        }
        if (this.mMayHaveNsDefaults) {
            this.mNsDefaultProvider.checkNsDefaults(this);
        }
        if ((prefix = this.mCurrElement.mPrefix) == null) {
            ns = this.mCurrElement.mDefaultNsURI;
        } else if (prefix == "xml") {
            ns = "http://www.w3.org/XML/1998/namespace";
        } else {
            ns = this.mNamespaces.findLastFromMap(prefix);
            if (ns == null || ns.length() == 0) {
                this.mReporter.throwParseError(ErrorConsts.ERR_NS_UNDECLARED, prefix, null);
            }
        }
        this.mCurrElement.mNamespaceURI = ns;
        this.mIdAttrIndex = xmlidIx = ac.resolveNamespaces(this.mReporter, this.mNamespaces);
        XMLValidator vld = this.mValidator;
        if (vld == null) {
            if (xmlidIx >= 0) {
                ac.normalizeSpacesInValue(xmlidIx);
            }
            return 4;
        }
        vld.validateElementStart(this.mCurrElement.mLocalName, this.mCurrElement.mNamespaceURI, this.mCurrElement.mPrefix);
        int attrLen = ac.getCount();
        if (attrLen > 0) {
            for (int i = 0; i < attrLen; ++i) {
                ac.validateAttribute(i, this.mValidator);
            }
        }
        return this.mValidator.validateElementAndAttributes();
    }

    public int validateEndElement() throws XMLStreamException {
        if (this.mValidator == null) {
            return 4;
        }
        int result = this.mValidator.validateElementEnd(this.mCurrElement.mLocalName, this.mCurrElement.mNamespaceURI, this.mCurrElement.mPrefix);
        if (this.mDepth == 1) {
            this.mValidator.validationCompleted(true);
        }
        return result;
    }

    public final int getAttributeCount() {
        return this.mAttrCollector.getCount();
    }

    public final int findAttributeIndex(String nsURI, String localName) {
        return this.mAttrCollector.findIndex(nsURI, localName);
    }

    public final int getIdAttributeIndex() {
        if (this.mIdAttrIndex >= 0) {
            return this.mIdAttrIndex;
        }
        return this.mValidator == null ? -1 : this.mValidator.getIdAttrIndex();
    }

    public final int getNotationAttributeIndex() {
        return this.mValidator == null ? -1 : this.mValidator.getNotationAttrIndex();
    }

    public final String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException(ErrorConsts.ERR_NULL_ARG);
        }
        if (prefix.length() == 0) {
            if (this.mDepth == 0) {
                return "";
            }
            return this.mCurrElement.mDefaultNsURI;
        }
        if (prefix.equals("xml")) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if (prefix.equals("xmlns")) {
            return "http://www.w3.org/2000/xmlns/";
        }
        return this.mNamespaces.findLastNonInterned(prefix);
    }

    public final String getPrefix(String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            return "xmlns";
        }
        String prefix = null;
        String[] strs = this.mNamespaces.getInternalArray();
        int len = this.mNamespaces.size();
        block0: for (int index = len - 1; index > 0; index -= 2) {
            if (!nsURI.equals(strs[index])) continue;
            prefix = strs[index - 1];
            for (int j = index + 1; j < len; j += 2) {
                if (strs[j] != prefix) continue;
                prefix = null;
                continue block0;
            }
            if (prefix != null) break;
            prefix = "";
            break;
        }
        return prefix;
    }

    public final Iterator getPrefixes(String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return new SingletonIterator("xml");
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            return new SingletonIterator("xmlns");
        }
        String[] strs = this.mNamespaces.getInternalArray();
        int len = this.mNamespaces.size();
        ArrayList<String> l = null;
        block0: for (int index = len - 1; index > 0; index -= 2) {
            if (!nsURI.equals(strs[index])) continue;
            String prefix = strs[index - 1];
            for (int j = index + 1; j < len; j += 2) {
                if (strs[j] == prefix) continue block0;
            }
            if (l == null) {
                l = new ArrayList<String>();
            }
            l.add(prefix);
        }
        return l == null ? EmptyIterator.getInstance() : l.iterator();
    }

    public final String getXmlVersion() {
        return this.mConfig.isXml11() ? "1.1" : "1.0";
    }

    public String getAttributeLocalName(int index) {
        return this.getAttrCollector().getLocalName(index);
    }

    public String getAttributeNamespace(int index) {
        return this.getAttrCollector().getURI(index);
    }

    public String getAttributePrefix(int index) {
        return this.getAttrCollector().getPrefix(index);
    }

    public String getAttributeValue(int index) {
        return this.getAttrCollector().getValue(index);
    }

    public String getAttributeValue(String nsURI, String localName) {
        int ix = this.findAttributeIndex(nsURI, localName);
        return ix < 0 ? null : this.getAttributeValue(ix);
    }

    public boolean isNotationDeclared(String name) {
        return false;
    }

    public boolean isUnparsedEntityDeclared(String name) {
        return false;
    }

    public String getBaseUri() {
        return null;
    }

    public final QName getCurrentElementName() {
        QName n;
        if (this.mDepth == 0) {
            return null;
        }
        String prefix = this.mCurrElement.mPrefix;
        if (prefix == null) {
            prefix = "";
        }
        String nsURI = this.mCurrElement.mNamespaceURI;
        String ln = this.mCurrElement.mLocalName;
        if (ln != this.mLastLocalName) {
            this.mLastLocalName = ln;
            this.mLastPrefix = prefix;
            this.mLastNsURI = nsURI;
        } else if (prefix != this.mLastPrefix) {
            this.mLastPrefix = prefix;
            this.mLastNsURI = nsURI;
        } else if (nsURI != this.mLastNsURI) {
            this.mLastNsURI = nsURI;
        } else {
            return this.mLastName;
        }
        this.mLastName = n = QNameCreator.create(nsURI, ln, prefix);
        return n;
    }

    public Location getValidationLocation() {
        return this.mReporter.getLocation();
    }

    public void reportProblem(XMLValidationProblem problem) throws XMLStreamException {
        this.mReporter.reportValidationProblem(problem);
    }

    public int addDefaultAttribute(String localName, String uri, String prefix, String value) throws XMLStreamException {
        return this.mAttrCollector.addDefaultAttribute(localName, uri, prefix, value);
    }

    public boolean isPrefixLocallyDeclared(String internedPrefix) {
        if (internedPrefix != null && internedPrefix.length() == 0) {
            internedPrefix = null;
        }
        int len = this.mNamespaces.size();
        for (int offset = this.mCurrElement.mNsOffset; offset < len; offset += 2) {
            String thisPrefix = this.mNamespaces.getString(offset);
            if (thisPrefix != internedPrefix) continue;
            return true;
        }
        return false;
    }

    public void addNsBinding(String prefix, String uri) {
        if (uri == null || uri.length() == 0) {
            uri = null;
        }
        if (prefix == null || prefix.length() == 0) {
            prefix = null;
            this.mCurrElement.mDefaultNsURI = uri;
        }
        this.mNamespaces.addStrings(prefix, uri);
    }

    public final void validateText(TextBuffer tb, boolean lastTextSegment) throws XMLStreamException {
        tb.validateText(this.mValidator, lastTextSegment);
    }

    public final void validateText(String contents, boolean lastTextSegment) throws XMLStreamException {
        this.mValidator.validateText(contents, lastTextSegment);
    }

    public final boolean isNamespaceAware() {
        return this.mNsAware;
    }

    public final boolean isEmpty() {
        return this.mDepth == 0;
    }

    public final int getDepth() {
        return this.mDepth;
    }

    public final String getDefaultNsURI() {
        if (this.mDepth == 0) {
            throw new IllegalStateException("Illegal access, empty stack.");
        }
        return this.mCurrElement.mDefaultNsURI;
    }

    public final String getNsURI() {
        if (this.mDepth == 0) {
            throw new IllegalStateException("Illegal access, empty stack.");
        }
        return this.mCurrElement.mNamespaceURI;
    }

    public final String getPrefix() {
        if (this.mDepth == 0) {
            throw new IllegalStateException("Illegal access, empty stack.");
        }
        return this.mCurrElement.mPrefix;
    }

    public final String getLocalName() {
        if (this.mDepth == 0) {
            throw new IllegalStateException("Illegal access, empty stack.");
        }
        return this.mCurrElement.mLocalName;
    }

    public final boolean matches(String prefix, String localName) {
        if (this.mDepth == 0) {
            throw new IllegalStateException("Illegal access, empty stack.");
        }
        String thisPrefix = this.mCurrElement.mPrefix;
        if (prefix == null || prefix.length() == 0 ? thisPrefix != null && thisPrefix.length() > 0 : thisPrefix != prefix && !thisPrefix.equals(prefix)) {
            return false;
        }
        String thisName = this.mCurrElement.mLocalName;
        return thisName == localName || thisName.equals(localName);
    }

    public final String getTopElementDesc() {
        if (this.mDepth == 0) {
            throw new IllegalStateException("Illegal access, empty stack.");
        }
        String name = this.mCurrElement.mLocalName;
        String prefix = this.mCurrElement.mPrefix;
        if (prefix == null) {
            return name;
        }
        return prefix + ":" + name;
    }

    public final int getTotalNsCount() {
        return this.mNamespaces.size() >> 1;
    }

    public final int getCurrentNsCount() {
        return this.mNamespaces.size() - this.mCurrElement.mNsOffset >> 1;
    }

    public final String getLocalNsPrefix(int index) {
        int offset = this.mCurrElement.mNsOffset;
        int localCount = this.mNamespaces.size() - offset;
        if ((index <<= 1) < 0 || index >= localCount) {
            this.throwIllegalIndex(index >> 1, localCount >> 1);
        }
        return this.mNamespaces.getString(offset + index);
    }

    public final String getLocalNsURI(int index) {
        int offset = this.mCurrElement.mNsOffset;
        int localCount = this.mNamespaces.size() - offset;
        if ((index <<= 1) < 0 || index >= localCount) {
            this.throwIllegalIndex(index >> 1, localCount >> 1);
        }
        return this.mNamespaces.getString(offset + index + 1);
    }

    private void throwIllegalIndex(int index, int localCount) {
        throw new IllegalArgumentException("Illegal namespace index " + (index >> 1) + "; current scope only has " + (localCount >> 1) + " namespace declarations.");
    }

    public final String getAttributeType(int index) {
        if (index == this.mIdAttrIndex && index >= 0) {
            return "ID";
        }
        return this.mValidator == null ? "CDATA" : this.mValidator.getAttributeType(index);
    }
}

