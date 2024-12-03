/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sax;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.dtd.DTDEventListener;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.io.DefaultInputResolver;
import com.ctc.wstx.io.InputBootstrapper;
import com.ctc.wstx.io.ReaderBootstrapper;
import com.ctc.wstx.io.StreamBootstrapper;
import com.ctc.wstx.io.SystemId;
import com.ctc.wstx.sax.SAXFeature;
import com.ctc.wstx.sax.SAXProperty;
import com.ctc.wstx.sax.WrappedSaxException;
import com.ctc.wstx.sr.AttributeCollector;
import com.ctc.wstx.sr.BasicStreamReader;
import com.ctc.wstx.sr.InputElementStack;
import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.util.ExceptionUtil;
import com.ctc.wstx.util.URLUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Locale;
import javax.xml.parsers.SAXParser;
import javax.xml.stream.Location;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.Attributes2;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.DefaultHandler;

public class WstxSAXParser
extends SAXParser
implements Parser,
XMLReader,
Attributes2,
Locator2,
DTDEventListener {
    static final boolean FEAT_DEFAULT_NS_PREFIXES = false;
    protected final WstxInputFactory mStaxFactory;
    protected final ReaderConfig mConfig;
    protected boolean mFeatNsPrefixes;
    protected BasicStreamReader mScanner;
    protected AttributeCollector mAttrCollector;
    protected InputElementStack mElemStack;
    protected String mEncoding;
    protected String mXmlVersion;
    protected boolean mStandalone;
    protected ContentHandler mContentHandler;
    protected DTDHandler mDTDHandler;
    protected EntityResolver mEntityResolver;
    protected ErrorHandler mErrorHandler;
    protected LexicalHandler mLexicalHandler;
    protected DeclHandler mDeclHandler;
    protected int mAttrCount;
    protected int mNsCount = 0;

    public WstxSAXParser(WstxInputFactory sf, boolean nsPrefixes) {
        this.mStaxFactory = sf;
        this.mFeatNsPrefixes = nsPrefixes;
        this.mConfig = sf.createPrivateConfig();
        this.mConfig.doSupportDTDs(true);
        ResolverProxy r = new ResolverProxy();
        this.mConfig.setDtdResolver(r);
        this.mConfig.setEntityResolver(r);
        this.mConfig.setDTDEventListener(this);
    }

    public WstxSAXParser() {
        this(new WstxInputFactory(), false);
    }

    @Override
    public final Parser getParser() {
        return this;
    }

    @Override
    public final XMLReader getXMLReader() {
        return this;
    }

    public final ReaderConfig getStaxConfig() {
        return this.mConfig;
    }

    @Override
    public boolean isNamespaceAware() {
        return this.mConfig.willSupportNamespaces();
    }

    @Override
    public boolean isValidating() {
        return this.mConfig.willValidateWithDTD();
    }

    @Override
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        SAXProperty prop = SAXProperty.findByUri(name);
        if (prop == SAXProperty.DECLARATION_HANDLER) {
            return this.mDeclHandler;
        }
        if (prop == SAXProperty.DOCUMENT_XML_VERSION) {
            return this.mXmlVersion;
        }
        if (prop == SAXProperty.DOM_NODE) {
            return null;
        }
        if (prop == SAXProperty.LEXICAL_HANDLER) {
            return this.mLexicalHandler;
        }
        if (prop == SAXProperty.XML_STRING) {
            return null;
        }
        throw new SAXNotRecognizedException("Property '" + name + "' not recognized");
    }

    @Override
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        SAXProperty prop = SAXProperty.findByUri(name);
        if (prop == SAXProperty.DECLARATION_HANDLER) {
            this.mDeclHandler = (DeclHandler)value;
            return;
        }
        if (prop != SAXProperty.DOCUMENT_XML_VERSION && prop != SAXProperty.DOM_NODE) {
            if (prop == SAXProperty.LEXICAL_HANDLER) {
                this.mLexicalHandler = (LexicalHandler)value;
                return;
            }
            if (prop != SAXProperty.XML_STRING) {
                throw new SAXNotRecognizedException("Property '" + name + "' not recognized");
            }
        }
        throw new SAXNotSupportedException("Property '" + name + "' is read-only, can not be modified");
    }

    @Override
    public void parse(InputSource is, HandlerBase hb) throws SAXException, IOException {
        if (hb != null) {
            if (this.mContentHandler == null) {
                this.setDocumentHandler(hb);
            }
            if (this.mEntityResolver == null) {
                this.setEntityResolver(hb);
            }
            if (this.mErrorHandler == null) {
                this.setErrorHandler(hb);
            }
            if (this.mDTDHandler == null) {
                this.setDTDHandler(hb);
            }
        }
        this.parse(is);
    }

    @Override
    public void parse(InputSource is, DefaultHandler dh) throws SAXException, IOException {
        if (dh != null) {
            if (this.mContentHandler == null) {
                this.setContentHandler(dh);
            }
            if (this.mEntityResolver == null) {
                this.setEntityResolver(dh);
            }
            if (this.mErrorHandler == null) {
                this.setErrorHandler(dh);
            }
            if (this.mDTDHandler == null) {
                this.setDTDHandler(dh);
            }
        }
        this.parse(is);
    }

    @Override
    public ContentHandler getContentHandler() {
        return this.mContentHandler;
    }

    @Override
    public DTDHandler getDTDHandler() {
        return this.mDTDHandler;
    }

    @Override
    public EntityResolver getEntityResolver() {
        return this.mEntityResolver;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return this.mErrorHandler;
    }

    @Override
    public boolean getFeature(String name) throws SAXNotRecognizedException {
        SAXFeature stdFeat = SAXFeature.findByUri(name);
        if (stdFeat == SAXFeature.EXTERNAL_GENERAL_ENTITIES) {
            return this.mConfig.willSupportExternalEntities();
        }
        if (stdFeat == SAXFeature.EXTERNAL_PARAMETER_ENTITIES) {
            return this.mConfig.willSupportExternalEntities();
        }
        if (stdFeat == SAXFeature.IS_STANDALONE) {
            return this.mStandalone;
        }
        if (stdFeat == SAXFeature.LEXICAL_HANDLER_PARAMETER_ENTITIES) {
            return false;
        }
        if (stdFeat == SAXFeature.NAMESPACES) {
            return this.mConfig.willSupportNamespaces();
        }
        if (stdFeat == SAXFeature.NAMESPACE_PREFIXES) {
            return !this.mConfig.willSupportNamespaces();
        }
        if (stdFeat == SAXFeature.RESOLVE_DTD_URIS) {
            return false;
        }
        if (stdFeat == SAXFeature.STRING_INTERNING) {
            return true;
        }
        if (stdFeat == SAXFeature.UNICODE_NORMALIZATION_CHECKING) {
            return false;
        }
        if (stdFeat == SAXFeature.USE_ATTRIBUTES2) {
            return true;
        }
        if (stdFeat == SAXFeature.USE_LOCATOR2) {
            return true;
        }
        if (stdFeat == SAXFeature.USE_ENTITY_RESOLVER2) {
            return true;
        }
        if (stdFeat == SAXFeature.VALIDATION) {
            return this.mConfig.willValidateWithDTD();
        }
        if (stdFeat == SAXFeature.XMLNS_URIS) {
            return true;
        }
        if (stdFeat == SAXFeature.XML_1_1) {
            return true;
        }
        throw new SAXNotRecognizedException("Feature '" + name + "' not recognized");
    }

    @Override
    public void setContentHandler(ContentHandler handler) {
        this.mContentHandler = handler;
    }

    @Override
    public void setDTDHandler(DTDHandler handler) {
        this.mDTDHandler = handler;
    }

    @Override
    public void setEntityResolver(EntityResolver resolver) {
        this.mEntityResolver = resolver;
    }

    @Override
    public void setErrorHandler(ErrorHandler handler) {
        this.mErrorHandler = handler;
    }

    @Override
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        boolean invalidValue = false;
        boolean readOnly = false;
        SAXFeature stdFeat = SAXFeature.findByUri(name);
        if (stdFeat == SAXFeature.EXTERNAL_GENERAL_ENTITIES) {
            this.mConfig.doSupportExternalEntities(value);
        } else if (stdFeat != SAXFeature.EXTERNAL_PARAMETER_ENTITIES) {
            if (stdFeat == SAXFeature.IS_STANDALONE) {
                readOnly = true;
            } else if (stdFeat != SAXFeature.LEXICAL_HANDLER_PARAMETER_ENTITIES) {
                if (stdFeat == SAXFeature.NAMESPACES) {
                    this.mConfig.doSupportNamespaces(value);
                } else if (stdFeat == SAXFeature.NAMESPACE_PREFIXES) {
                    this.mFeatNsPrefixes = value;
                } else if (stdFeat != SAXFeature.RESOLVE_DTD_URIS) {
                    if (stdFeat == SAXFeature.STRING_INTERNING) {
                        invalidValue = !value;
                    } else if (stdFeat == SAXFeature.UNICODE_NORMALIZATION_CHECKING) {
                        invalidValue = value;
                    } else if (stdFeat == SAXFeature.USE_ATTRIBUTES2) {
                        readOnly = true;
                    } else if (stdFeat == SAXFeature.USE_LOCATOR2) {
                        readOnly = true;
                    } else if (stdFeat == SAXFeature.USE_ENTITY_RESOLVER2) {
                        readOnly = true;
                    } else if (stdFeat == SAXFeature.VALIDATION) {
                        this.mConfig.doValidateWithDTD(value);
                    } else if (stdFeat == SAXFeature.XMLNS_URIS) {
                        invalidValue = !value;
                    } else if (stdFeat == SAXFeature.XML_1_1) {
                        readOnly = true;
                    } else {
                        throw new SAXNotRecognizedException("Feature '" + name + "' not recognized");
                    }
                }
            }
        }
        if (readOnly) {
            throw new SAXNotSupportedException("Feature '" + name + "' is read-only, can not be modified");
        }
        if (invalidValue) {
            throw new SAXNotSupportedException("Trying to set invalid value for feature '" + name + "', '" + value + "'");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void parse(InputSource input) throws SAXException {
        this.mScanner = null;
        String sysIdStr = input.getSystemId();
        ReaderConfig cfg = this.mConfig;
        URL srcUrl = null;
        InputStream is = null;
        Reader r = input.getCharacterStream();
        if (r == null && (is = input.getByteStream()) == null) {
            if (sysIdStr == null) {
                throw new SAXException("Invalid InputSource passed: neither character or byte stream passed, nor system id specified");
            }
            try {
                srcUrl = URLUtil.urlFromSystemId(sysIdStr);
                is = URLUtil.inputStreamFromURL(srcUrl);
            }
            catch (IOException ioe) {
                SAXException saxe = new SAXException(ioe);
                ExceptionUtil.setInitCause(saxe, ioe);
                throw saxe;
            }
        }
        if (this.mContentHandler != null) {
            this.mContentHandler.setDocumentLocator(this);
            this.mContentHandler.startDocument();
        }
        cfg.resetState();
        try {
            String inputEnc = input.getEncoding();
            String publicId = input.getPublicId();
            if (r == null && inputEnc != null && inputEnc.length() > 0) {
                r = DefaultInputResolver.constructOptimizedReader(cfg, is, false, inputEnc);
            }
            SystemId systemId = SystemId.construct(sysIdStr, srcUrl);
            if (r != null) {
                ReaderBootstrapper bs = ReaderBootstrapper.getInstance(publicId, systemId, r, inputEnc);
                this.mScanner = (BasicStreamReader)this.mStaxFactory.createSR(cfg, systemId, (InputBootstrapper)bs, false, false);
            } else {
                StreamBootstrapper bs = StreamBootstrapper.getInstance(publicId, systemId, is);
                this.mScanner = (BasicStreamReader)this.mStaxFactory.createSR(cfg, systemId, (InputBootstrapper)bs, false, false);
            }
            String enc2 = this.mScanner.getEncoding();
            if (enc2 == null) {
                enc2 = this.mScanner.getCharacterEncodingScheme();
            }
            this.mEncoding = enc2;
            this.mXmlVersion = this.mScanner.getVersion();
            this.mStandalone = this.mScanner.standaloneSet();
            this.mAttrCollector = this.mScanner.getAttributeCollector();
            this.mElemStack = this.mScanner.getInputElementStack();
            this.fireEvents();
        }
        catch (IOException io) {
            this.throwSaxException(io);
        }
        catch (XMLStreamException strex) {
            this.throwSaxException(strex);
        }
        finally {
            if (this.mContentHandler != null) {
                this.mContentHandler.endDocument();
            }
            if (this.mScanner != null) {
                BasicStreamReader sr = this.mScanner;
                this.mScanner = null;
                try {
                    sr.close();
                }
                catch (XMLStreamException xMLStreamException) {}
            }
            if (r != null) {
                try {
                    r.close();
                }
                catch (IOException sr) {}
            }
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException sr) {}
            }
        }
    }

    @Override
    public void parse(String systemId) throws SAXException {
        InputSource src = new InputSource(systemId);
        this.parse(src);
    }

    private final void fireEvents() throws IOException, SAXException, XMLStreamException {
        int type;
        this.mConfig.doParseLazily(false);
        while ((type = this.mScanner.next()) != 1) {
            this.fireAuxEvent(type, false);
        }
        this.fireStartTag();
        int depth = 1;
        while (true) {
            if ((type = this.mScanner.next()) == 1) {
                this.fireStartTag();
                ++depth;
                continue;
            }
            if (type == 2) {
                this.mScanner.fireSaxEndElement(this.mContentHandler);
                if (--depth >= 1) continue;
                break;
            }
            if (type == 4) {
                this.mScanner.fireSaxCharacterEvents(this.mContentHandler);
                continue;
            }
            this.fireAuxEvent(type, true);
        }
        while ((type = this.mScanner.next()) != 8) {
            if (type == 6) continue;
            this.fireAuxEvent(type, false);
        }
    }

    private final void fireAuxEvent(int type, boolean inTree) throws IOException, SAXException, XMLStreamException {
        switch (type) {
            case 5: {
                this.mScanner.fireSaxCommentEvent(this.mLexicalHandler);
                break;
            }
            case 12: {
                if (this.mLexicalHandler != null) {
                    this.mLexicalHandler.startCDATA();
                    this.mScanner.fireSaxCharacterEvents(this.mContentHandler);
                    this.mLexicalHandler.endCDATA();
                    break;
                }
                this.mScanner.fireSaxCharacterEvents(this.mContentHandler);
                break;
            }
            case 11: {
                if (this.mLexicalHandler == null) break;
                String rootName = this.mScanner.getDTDRootName();
                String sysId = this.mScanner.getDTDSystemId();
                String pubId = this.mScanner.getDTDPublicId();
                this.mLexicalHandler.startDTD(rootName, pubId, sysId);
                try {
                    this.mScanner.getDTDInfo();
                }
                catch (WrappedSaxException wse) {
                    throw wse.getSaxException();
                }
                this.mLexicalHandler.endDTD();
                break;
            }
            case 3: {
                this.mScanner.fireSaxPIEvent(this.mContentHandler);
                break;
            }
            case 6: {
                if (!inTree) break;
                this.mScanner.fireSaxSpaceEvents(this.mContentHandler);
                break;
            }
            case 9: {
                if (this.mContentHandler == null) break;
                this.mContentHandler.skippedEntity(this.mScanner.getLocalName());
                break;
            }
            default: {
                if (type == 8) {
                    this.throwSaxException("Unexpected end-of-input in " + (inTree ? "tree" : "prolog"));
                }
                throw new RuntimeException("Internal error: unexpected type, " + type);
            }
        }
    }

    private final void fireStartTag() throws SAXException {
        this.mAttrCount = this.mAttrCollector.getCount();
        if (this.mFeatNsPrefixes) {
            this.mNsCount = this.mElemStack.getCurrentNsCount();
        }
        this.mScanner.fireSaxStartElement(this.mContentHandler, this);
    }

    @Override
    public void setDocumentHandler(DocumentHandler handler) {
        this.setContentHandler(new DocHandlerWrapper(handler));
    }

    @Override
    public void setLocale(Locale locale) {
    }

    @Override
    public int getIndex(String qName) {
        if (this.mElemStack == null) {
            return -1;
        }
        int ix = this.mElemStack.findAttributeIndex(null, qName);
        return ix;
    }

    @Override
    public int getIndex(String uri, String localName) {
        if (this.mElemStack == null) {
            return -1;
        }
        int ix = this.mElemStack.findAttributeIndex(uri, localName);
        return ix;
    }

    @Override
    public int getLength() {
        return this.mAttrCount + this.mNsCount;
    }

    @Override
    public String getLocalName(int index) {
        if (index < this.mAttrCount) {
            return index < 0 ? null : this.mAttrCollector.getLocalName(index);
        }
        if ((index -= this.mAttrCount) < this.mNsCount) {
            String prefix = this.mElemStack.getLocalNsPrefix(index);
            return prefix == null || prefix.length() == 0 ? "xmlns" : prefix;
        }
        return null;
    }

    @Override
    public String getQName(int index) {
        if (index < this.mAttrCount) {
            if (index < 0) {
                return null;
            }
            String prefix = this.mAttrCollector.getPrefix(index);
            String ln = this.mAttrCollector.getLocalName(index);
            return prefix == null || prefix.length() == 0 ? ln : prefix + ":" + ln;
        }
        if ((index -= this.mAttrCount) < this.mNsCount) {
            String prefix = this.mElemStack.getLocalNsPrefix(index);
            if (prefix == null || prefix.length() == 0) {
                return "xmlns";
            }
            return "xmlns:" + prefix;
        }
        return null;
    }

    @Override
    public String getType(int index) {
        if (index < this.mAttrCount) {
            if (index < 0) {
                return null;
            }
            String type = this.mElemStack.getAttributeType(index);
            if (type == "ENUMERATED") {
                type = "NMTOKEN";
            }
            return type;
        }
        if ((index -= this.mAttrCount) < this.mNsCount) {
            return "CDATA";
        }
        return null;
    }

    @Override
    public String getType(String qName) {
        return this.getType(this.getIndex(qName));
    }

    @Override
    public String getType(String uri, String localName) {
        return this.getType(this.getIndex(uri, localName));
    }

    @Override
    public String getURI(int index) {
        if (index < this.mAttrCount) {
            if (index < 0) {
                return null;
            }
            String uri = this.mAttrCollector.getURI(index);
            return uri == null ? "" : uri;
        }
        if (index - this.mAttrCount < this.mNsCount) {
            return "http://www.w3.org/2000/xmlns/";
        }
        return null;
    }

    @Override
    public String getValue(int index) {
        if (index < this.mAttrCount) {
            return index < 0 ? null : this.mAttrCollector.getValue(index);
        }
        if ((index -= this.mAttrCount) < this.mNsCount) {
            String uri = this.mElemStack.getLocalNsURI(index);
            return uri == null ? "" : uri;
        }
        return null;
    }

    @Override
    public String getValue(String qName) {
        return this.getValue(this.getIndex(qName));
    }

    @Override
    public String getValue(String uri, String localName) {
        return this.getValue(this.getIndex(uri, localName));
    }

    @Override
    public boolean isDeclared(int index) {
        if (index < this.mAttrCount ? index >= 0 : (index -= this.mAttrCount) < this.mNsCount) {
            return true;
        }
        this.throwNoSuchAttribute(index);
        return false;
    }

    @Override
    public boolean isDeclared(String qName) {
        return false;
    }

    @Override
    public boolean isDeclared(String uri, String localName) {
        return false;
    }

    @Override
    public boolean isSpecified(int index) {
        if (index < this.mAttrCount) {
            if (index >= 0) {
                return this.mAttrCollector.isSpecified(index);
            }
        } else if ((index -= this.mAttrCount) < this.mNsCount) {
            return true;
        }
        this.throwNoSuchAttribute(index);
        return false;
    }

    @Override
    public boolean isSpecified(String qName) {
        int ix = this.getIndex(qName);
        if (ix < 0) {
            throw new IllegalArgumentException("No attribute with qName '" + qName + "'");
        }
        return this.isSpecified(ix);
    }

    @Override
    public boolean isSpecified(String uri, String localName) {
        int ix = this.getIndex(uri, localName);
        if (ix < 0) {
            throw new IllegalArgumentException("No attribute with uri " + uri + ", local name '" + localName + "'");
        }
        return this.isSpecified(ix);
    }

    @Override
    public int getColumnNumber() {
        if (this.mScanner != null) {
            Location loc = this.mScanner.getLocation();
            return loc.getColumnNumber();
        }
        return -1;
    }

    @Override
    public int getLineNumber() {
        if (this.mScanner != null) {
            Location loc = this.mScanner.getLocation();
            return loc.getLineNumber();
        }
        return -1;
    }

    @Override
    public String getPublicId() {
        if (this.mScanner != null) {
            Location loc = this.mScanner.getLocation();
            return loc.getPublicId();
        }
        return null;
    }

    @Override
    public String getSystemId() {
        if (this.mScanner != null) {
            Location loc = this.mScanner.getLocation();
            return loc.getSystemId();
        }
        return null;
    }

    @Override
    public String getEncoding() {
        return this.mEncoding;
    }

    @Override
    public String getXMLVersion() {
        return this.mXmlVersion;
    }

    @Override
    public boolean dtdReportComments() {
        return this.mLexicalHandler != null;
    }

    @Override
    public void dtdComment(char[] data, int offset, int len) {
        if (this.mLexicalHandler != null) {
            try {
                this.mLexicalHandler.comment(data, offset, len);
            }
            catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }

    @Override
    public void dtdProcessingInstruction(String target, String data) {
        if (this.mContentHandler != null) {
            try {
                this.mContentHandler.processingInstruction(target, data);
            }
            catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }

    @Override
    public void dtdSkippedEntity(String name) {
        if (this.mContentHandler != null) {
            try {
                this.mContentHandler.skippedEntity(name);
            }
            catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }

    @Override
    public void dtdNotationDecl(String name, String publicId, String systemId, URL baseURL) throws XMLStreamException {
        if (this.mDTDHandler != null) {
            if (systemId != null && systemId.indexOf(58) < 0) {
                try {
                    systemId = URLUtil.urlFromSystemId(systemId, baseURL).toExternalForm();
                }
                catch (IOException ioe) {
                    throw new WstxIOException(ioe);
                }
            }
            try {
                this.mDTDHandler.notationDecl(name, publicId, systemId);
            }
            catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }

    @Override
    public void dtdUnparsedEntityDecl(String name, String publicId, String systemId, String notationName, URL baseURL) throws XMLStreamException {
        if (this.mDTDHandler != null) {
            if (systemId.indexOf(58) < 0) {
                try {
                    systemId = URLUtil.urlFromSystemId(systemId, baseURL).toExternalForm();
                }
                catch (IOException ioe) {
                    throw new WstxIOException(ioe);
                }
            }
            try {
                this.mDTDHandler.unparsedEntityDecl(name, publicId, systemId, notationName);
            }
            catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }

    @Override
    public void attributeDecl(String eName, String aName, String type, String mode, String value) {
        if (this.mDeclHandler != null) {
            try {
                this.mDeclHandler.attributeDecl(eName, aName, type, mode, value);
            }
            catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }

    @Override
    public void dtdElementDecl(String name, String model) {
        if (this.mDeclHandler != null) {
            try {
                this.mDeclHandler.elementDecl(name, model);
            }
            catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }

    @Override
    public void dtdExternalEntityDecl(String name, String publicId, String systemId) {
        if (this.mDeclHandler != null) {
            try {
                this.mDeclHandler.externalEntityDecl(name, publicId, systemId);
            }
            catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }

    @Override
    public void dtdInternalEntityDecl(String name, String value) {
        if (this.mDeclHandler != null) {
            try {
                this.mDeclHandler.internalEntityDecl(name, value);
            }
            catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }

    private void throwSaxException(Exception src) throws SAXException {
        SAXParseException se = new SAXParseException(src.getMessage(), this, src);
        ExceptionUtil.setInitCause(se, src);
        if (this.mErrorHandler != null) {
            this.mErrorHandler.fatalError(se);
        }
        throw se;
    }

    private void throwSaxException(String msg) throws SAXException {
        SAXParseException se = new SAXParseException(msg, this);
        if (this.mErrorHandler != null) {
            this.mErrorHandler.fatalError(se);
        }
        throw se;
    }

    private void throwNoSuchAttribute(int index) {
        throw new IllegalArgumentException("No attribute with index " + index + " (have " + (this.mAttrCount + this.mNsCount) + " attributes)");
    }

    static final class AttributesWrapper
    implements AttributeList {
        Attributes mAttrs;

        public void setAttributes(Attributes a) {
            this.mAttrs = a;
        }

        @Override
        public int getLength() {
            return this.mAttrs.getLength();
        }

        @Override
        public String getName(int i) {
            String n = this.mAttrs.getQName(i);
            return n == null ? this.mAttrs.getLocalName(i) : n;
        }

        @Override
        public String getType(int i) {
            return this.mAttrs.getType(i);
        }

        @Override
        public String getType(String name) {
            return this.mAttrs.getType(name);
        }

        @Override
        public String getValue(int i) {
            return this.mAttrs.getValue(i);
        }

        @Override
        public String getValue(String name) {
            return this.mAttrs.getValue(name);
        }
    }

    static final class DocHandlerWrapper
    implements ContentHandler {
        final DocumentHandler mDocHandler;
        final AttributesWrapper mAttrWrapper = new AttributesWrapper();

        DocHandlerWrapper(DocumentHandler h) {
            this.mDocHandler = h;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            this.mDocHandler.characters(ch, start, length);
        }

        @Override
        public void endDocument() throws SAXException {
            this.mDocHandler.endDocument();
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName == null) {
                qName = localName;
            }
            this.mDocHandler.endElement(qName);
        }

        @Override
        public void endPrefixMapping(String prefix) {
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            this.mDocHandler.ignorableWhitespace(ch, start, length);
        }

        @Override
        public void processingInstruction(String target, String data) throws SAXException {
            this.mDocHandler.processingInstruction(target, data);
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            this.mDocHandler.setDocumentLocator(locator);
        }

        @Override
        public void skippedEntity(String name) {
        }

        @Override
        public void startDocument() throws SAXException {
            this.mDocHandler.startDocument();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
            if (qName == null) {
                qName = localName;
            }
            this.mAttrWrapper.setAttributes(attrs);
            this.mDocHandler.startElement(qName, this.mAttrWrapper);
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) {
        }
    }

    final class ResolverProxy
    implements XMLResolver {
        @Override
        public Object resolveEntity(String publicID, String systemID, String baseURI, String namespace) throws XMLStreamException {
            if (WstxSAXParser.this.mEntityResolver != null) {
                try {
                    URL url = new URL(baseURI);
                    String ref = new URL(url, systemID).toExternalForm();
                    InputSource isrc = WstxSAXParser.this.mEntityResolver.resolveEntity(publicID, ref);
                    if (isrc != null) {
                        InputStream in = isrc.getByteStream();
                        if (in != null) {
                            return in;
                        }
                        Reader r = isrc.getCharacterStream();
                        if (r != null) {
                            return r;
                        }
                    }
                    return null;
                }
                catch (IOException ex) {
                    throw new WstxIOException(ex);
                }
                catch (Exception ex) {
                    throw new XMLStreamException(ex.getMessage(), ex);
                }
            }
            return null;
        }
    }
}

