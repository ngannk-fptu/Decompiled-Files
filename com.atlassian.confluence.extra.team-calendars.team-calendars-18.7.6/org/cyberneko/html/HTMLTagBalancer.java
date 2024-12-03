/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.util.XMLAttributesImpl
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.NamespaceContext
 *  org.apache.xerces.xni.QName
 *  org.apache.xerces.xni.XMLAttributes
 *  org.apache.xerces.xni.XMLDocumentHandler
 *  org.apache.xerces.xni.XMLLocator
 *  org.apache.xerces.xni.XMLResourceIdentifier
 *  org.apache.xerces.xni.XMLString
 *  org.apache.xerces.xni.XNIException
 *  org.apache.xerces.xni.parser.XMLComponentManager
 *  org.apache.xerces.xni.parser.XMLConfigurationException
 *  org.apache.xerces.xni.parser.XMLDocumentFilter
 *  org.apache.xerces.xni.parser.XMLDocumentSource
 */
package org.cyberneko.html;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.cyberneko.html.HTMLAugmentations;
import org.cyberneko.html.HTMLComponent;
import org.cyberneko.html.HTMLElements;
import org.cyberneko.html.HTMLErrorReporter;
import org.cyberneko.html.HTMLEventInfo;
import org.cyberneko.html.HTMLTagBalancingListener;
import org.cyberneko.html.LostText;
import org.cyberneko.html.xercesbridge.XercesBridge;

public class HTMLTagBalancer
implements XMLDocumentFilter,
HTMLComponent {
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String AUGMENTATIONS = "http://cyberneko.org/html/features/augmentations";
    protected static final String REPORT_ERRORS = "http://cyberneko.org/html/features/report-errors";
    protected static final String DOCUMENT_FRAGMENT_DEPRECATED = "http://cyberneko.org/html/features/document-fragment";
    protected static final String DOCUMENT_FRAGMENT = "http://cyberneko.org/html/features/balance-tags/document-fragment";
    protected static final String IGNORE_OUTSIDE_CONTENT = "http://cyberneko.org/html/features/balance-tags/ignore-outside-content";
    private static final String[] RECOGNIZED_FEATURES = new String[]{"http://xml.org/sax/features/namespaces", "http://cyberneko.org/html/features/augmentations", "http://cyberneko.org/html/features/report-errors", "http://cyberneko.org/html/features/document-fragment", "http://cyberneko.org/html/features/balance-tags/document-fragment", "http://cyberneko.org/html/features/balance-tags/ignore-outside-content"};
    private static final Boolean[] RECOGNIZED_FEATURES_DEFAULTS = new Boolean[]{null, null, null, null, Boolean.FALSE, Boolean.FALSE};
    protected static final String NAMES_ELEMS = "http://cyberneko.org/html/properties/names/elems";
    protected static final String NAMES_ATTRS = "http://cyberneko.org/html/properties/names/attrs";
    protected static final String ERROR_REPORTER = "http://cyberneko.org/html/properties/error-reporter";
    public static final String FRAGMENT_CONTEXT_STACK = "http://cyberneko.org/html/properties/balance-tags/fragment-context-stack";
    private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://cyberneko.org/html/properties/names/elems", "http://cyberneko.org/html/properties/names/attrs", "http://cyberneko.org/html/properties/error-reporter", "http://cyberneko.org/html/properties/balance-tags/fragment-context-stack"};
    private static final Object[] RECOGNIZED_PROPERTIES_DEFAULTS = new Object[]{null, null, null, null};
    protected static final short NAMES_NO_CHANGE = 0;
    protected static final short NAMES_MATCH = 0;
    protected static final short NAMES_UPPERCASE = 1;
    protected static final short NAMES_LOWERCASE = 2;
    protected static final HTMLEventInfo SYNTHESIZED_ITEM = new HTMLEventInfo.SynthesizedItem();
    protected boolean fNamespaces;
    protected boolean fAugmentations;
    protected boolean fReportErrors;
    protected boolean fDocumentFragment;
    protected boolean fIgnoreOutsideContent;
    protected boolean fAllowSelfclosingIframe;
    protected boolean fAllowSelfclosingTags;
    protected short fNamesElems;
    protected short fNamesAttrs;
    protected HTMLErrorReporter fErrorReporter;
    protected XMLDocumentSource fDocumentSource;
    protected XMLDocumentHandler fDocumentHandler;
    protected final InfoStack fElementStack = new InfoStack();
    protected final InfoStack fInlineStack = new InfoStack();
    protected boolean fSeenAnything;
    protected boolean fSeenDoctype;
    protected boolean fSeenRootElement;
    protected boolean fSeenRootElementEnd;
    protected boolean fSeenHeadElement;
    protected boolean fSeenBodyElement;
    private boolean fSeenBodyElementEnd;
    private boolean fSeenFramesetElement;
    protected boolean fOpenedForm;
    private final QName fQName = new QName();
    private final XMLAttributes fEmptyAttrs = new XMLAttributesImpl();
    private final HTMLAugmentations fInfosetAugs = new HTMLAugmentations();
    protected HTMLTagBalancingListener tagBalancingListener;
    private LostText lostText_ = new LostText();
    private boolean forcedStartElement_ = false;
    private boolean forcedEndElement_ = false;
    private QName[] fragmentContextStack_ = null;
    private int fragmentContextStackSize_ = 0;
    private List endElementsBuffer_ = new ArrayList();

    @Override
    public Boolean getFeatureDefault(String featureId) {
        int length = RECOGNIZED_FEATURES != null ? RECOGNIZED_FEATURES.length : 0;
        for (int i = 0; i < length; ++i) {
            if (!RECOGNIZED_FEATURES[i].equals(featureId)) continue;
            return RECOGNIZED_FEATURES_DEFAULTS[i];
        }
        return null;
    }

    @Override
    public Object getPropertyDefault(String propertyId) {
        int length = RECOGNIZED_PROPERTIES != null ? RECOGNIZED_PROPERTIES.length : 0;
        for (int i = 0; i < length; ++i) {
            if (!RECOGNIZED_PROPERTIES[i].equals(propertyId)) continue;
            return RECOGNIZED_PROPERTIES_DEFAULTS[i];
        }
        return null;
    }

    public String[] getRecognizedFeatures() {
        return RECOGNIZED_FEATURES;
    }

    public String[] getRecognizedProperties() {
        return RECOGNIZED_PROPERTIES;
    }

    public void reset(XMLComponentManager manager) throws XMLConfigurationException {
        this.fNamespaces = manager.getFeature(NAMESPACES);
        this.fAugmentations = manager.getFeature(AUGMENTATIONS);
        this.fReportErrors = manager.getFeature(REPORT_ERRORS);
        this.fDocumentFragment = manager.getFeature(DOCUMENT_FRAGMENT) || manager.getFeature(DOCUMENT_FRAGMENT_DEPRECATED);
        this.fIgnoreOutsideContent = manager.getFeature(IGNORE_OUTSIDE_CONTENT);
        this.fAllowSelfclosingIframe = manager.getFeature("http://cyberneko.org/html/features/scanner/allow-selfclosing-iframe");
        this.fAllowSelfclosingTags = manager.getFeature("http://cyberneko.org/html/features/scanner/allow-selfclosing-tags");
        this.fNamesElems = HTMLTagBalancer.getNamesValue(String.valueOf(manager.getProperty(NAMES_ELEMS)));
        this.fNamesAttrs = HTMLTagBalancer.getNamesValue(String.valueOf(manager.getProperty(NAMES_ATTRS)));
        this.fErrorReporter = (HTMLErrorReporter)manager.getProperty(ERROR_REPORTER);
        this.fragmentContextStack_ = (QName[])manager.getProperty(FRAGMENT_CONTEXT_STACK);
        this.fSeenAnything = false;
        this.fSeenDoctype = false;
        this.fSeenRootElement = false;
        this.fSeenRootElementEnd = false;
        this.fSeenHeadElement = false;
        this.fSeenBodyElement = false;
        this.fSeenBodyElementEnd = false;
        this.fSeenFramesetElement = false;
    }

    public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
        if (featureId.equals(AUGMENTATIONS)) {
            this.fAugmentations = state;
            return;
        }
        if (featureId.equals(REPORT_ERRORS)) {
            this.fReportErrors = state;
            return;
        }
        if (featureId.equals(IGNORE_OUTSIDE_CONTENT)) {
            this.fIgnoreOutsideContent = state;
            return;
        }
    }

    public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
        if (propertyId.equals(NAMES_ELEMS)) {
            this.fNamesElems = HTMLTagBalancer.getNamesValue(String.valueOf(value));
            return;
        }
        if (propertyId.equals(NAMES_ATTRS)) {
            this.fNamesAttrs = HTMLTagBalancer.getNamesValue(String.valueOf(value));
            return;
        }
    }

    public void setDocumentHandler(XMLDocumentHandler handler) {
        this.fDocumentHandler = handler;
    }

    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }

    public void startDocument(XMLLocator locator, String encoding, NamespaceContext nscontext, Augmentations augs) throws XNIException {
        this.fElementStack.top = 0;
        if (this.fragmentContextStack_ != null) {
            this.fragmentContextStackSize_ = this.fragmentContextStack_.length;
            for (int i = 0; i < this.fragmentContextStack_.length; ++i) {
                QName name = this.fragmentContextStack_[i];
                HTMLElements.Element elt = HTMLElements.getElement(name.localpart);
                this.fElementStack.push(new Info(elt, name));
            }
        } else {
            this.fragmentContextStackSize_ = 0;
        }
        if (this.fDocumentHandler != null) {
            XercesBridge.getInstance().XMLDocumentHandler_startDocument(this.fDocumentHandler, locator, encoding, nscontext, augs);
        }
    }

    public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
        if (!this.fSeenAnything && this.fDocumentHandler != null) {
            this.fDocumentHandler.xmlDecl(version, encoding, standalone, augs);
        }
    }

    public void doctypeDecl(String rootElementName, String publicId, String systemId, Augmentations augs) throws XNIException {
        this.fSeenAnything = true;
        if (this.fReportErrors) {
            if (this.fSeenRootElement) {
                this.fErrorReporter.reportError("HTML2010", null);
            } else if (this.fSeenDoctype) {
                this.fErrorReporter.reportError("HTML2011", null);
            }
        }
        if (!this.fSeenRootElement && !this.fSeenDoctype) {
            this.fSeenDoctype = true;
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.doctypeDecl(rootElementName, publicId, systemId, augs);
            }
        }
    }

    public void endDocument(Augmentations augs) throws XNIException {
        this.fIgnoreOutsideContent = true;
        this.consumeBufferedEndElements();
        if (!this.fSeenRootElement && !this.fDocumentFragment) {
            if (this.fReportErrors) {
                this.fErrorReporter.reportError("HTML2000", null);
            }
            if (this.fDocumentHandler != null) {
                this.fSeenRootElementEnd = false;
                this.forceStartBody();
                String body = HTMLTagBalancer.modifyName("body", this.fNamesElems);
                this.fQName.setValues(null, body, body, null);
                this.callEndElement(this.fQName, this.synthesizedAugs());
                String ename = HTMLTagBalancer.modifyName("html", this.fNamesElems);
                this.fQName.setValues(null, ename, ename, null);
                this.callEndElement(this.fQName, this.synthesizedAugs());
            }
        } else {
            int length = this.fElementStack.top - this.fragmentContextStackSize_;
            for (int i = 0; i < length; ++i) {
                Info info = this.fElementStack.pop();
                if (this.fReportErrors) {
                    String ename = info.qname.rawname;
                    this.fErrorReporter.reportWarning("HTML2001", new Object[]{ename});
                }
                if (this.fDocumentHandler == null) continue;
                this.callEndElement(info.qname, this.synthesizedAugs());
            }
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endDocument(augs);
        }
    }

    private void consumeBufferedEndElements() {
        ArrayList toConsume = new ArrayList(this.endElementsBuffer_);
        this.endElementsBuffer_.clear();
        for (int i = 0; i < toConsume.size(); ++i) {
            ElementEntry entry = (ElementEntry)toConsume.get(i);
            this.forcedEndElement_ = true;
            this.endElement(entry.name_, entry.augs_);
        }
        this.endElementsBuffer_.clear();
    }

    public void comment(XMLString text, Augmentations augs) throws XNIException {
        this.fSeenAnything = true;
        this.consumeEarlyTextIfNeeded();
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.comment(text, augs);
        }
    }

    private void consumeEarlyTextIfNeeded() {
        if (!this.lostText_.isEmpty()) {
            if (!this.fSeenBodyElement) {
                this.forceStartBody();
            }
            this.lostText_.refeed((XMLDocumentHandler)this);
        }
    }

    public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
        this.fSeenAnything = true;
        this.consumeEarlyTextIfNeeded();
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.processingInstruction(target, data, augs);
        }
    }

    public void startElement(QName elem, XMLAttributes attrs, Augmentations augs) throws XNIException {
        QName head;
        this.fSeenAnything = true;
        boolean isForcedCreation = this.forcedStartElement_;
        this.forcedStartElement_ = false;
        if (this.fSeenRootElementEnd) {
            this.notifyDiscardedStartElement(elem, attrs, augs);
            return;
        }
        HTMLElements.Element element = this.getElement(elem);
        short elementCode = element.code;
        if (isForcedCreation && (elementCode == 102 || elementCode == 92)) {
            return;
        }
        if (this.fSeenRootElement && elementCode == 46) {
            this.notifyDiscardedStartElement(elem, attrs, augs);
            return;
        }
        if (this.fSeenFramesetElement && elementCode != 36 && elementCode != 37 && elementCode != 70) {
            this.notifyDiscardedStartElement(elem, attrs, augs);
            return;
        }
        if (elementCode == 44) {
            if (this.fSeenHeadElement) {
                this.notifyDiscardedStartElement(elem, attrs, augs);
                return;
            }
            this.fSeenHeadElement = true;
        } else if (elementCode == 37) {
            if (!this.fSeenHeadElement) {
                head = this.createQName("head");
                this.forceStartElement(head, null, this.synthesizedAugs());
                this.endElement(head, this.synthesizedAugs());
            }
            this.consumeBufferedEndElements();
            this.fSeenFramesetElement = true;
        } else if (elementCode == 14) {
            if (!this.fSeenHeadElement) {
                head = this.createQName("head");
                this.forceStartElement(head, null, this.synthesizedAugs());
                this.endElement(head, this.synthesizedAugs());
            }
            this.consumeBufferedEndElements();
            if (this.fSeenBodyElement) {
                this.notifyDiscardedStartElement(elem, attrs, augs);
                return;
            }
            this.fSeenBodyElement = true;
        } else if (elementCode == 35) {
            if (this.fOpenedForm) {
                this.notifyDiscardedStartElement(elem, attrs, augs);
                return;
            }
            this.fOpenedForm = true;
        } else if (elementCode == 118) {
            this.consumeBufferedEndElements();
        }
        if (element.parent != null) {
            HTMLElements.Element preferedParent = element.parent[0];
            if (!this.fDocumentFragment || preferedParent.code != 44 && preferedParent.code != 14) {
                int depth;
                if (!this.fSeenRootElement && !this.fDocumentFragment) {
                    QName qname;
                    boolean parentCreated;
                    String pname = preferedParent.name;
                    pname = HTMLTagBalancer.modifyName(pname, this.fNamesElems);
                    if (this.fReportErrors) {
                        String ename = elem.rawname;
                        this.fErrorReporter.reportWarning("HTML2002", new Object[]{ename, pname});
                    }
                    if (!(parentCreated = this.forceStartElement(qname = new QName(null, pname, pname, null), null, this.synthesizedAugs()))) {
                        if (!isForcedCreation) {
                            this.notifyDiscardedStartElement(elem, attrs, augs);
                        }
                        return;
                    }
                } else if ((preferedParent.code != 44 || !this.fSeenBodyElement && !this.fDocumentFragment) && (depth = this.getParentDepth(element.parent, element.bounds)) == -1) {
                    boolean parentCreated;
                    String pname = HTMLTagBalancer.modifyName(preferedParent.name, this.fNamesElems);
                    QName qname = new QName(null, pname, pname, null);
                    if (this.fReportErrors) {
                        String ename = elem.rawname;
                        this.fErrorReporter.reportWarning("HTML2004", new Object[]{ename, pname});
                    }
                    if (!(parentCreated = this.forceStartElement(qname, null, this.synthesizedAugs()))) {
                        if (!isForcedCreation) {
                            this.notifyDiscardedStartElement(elem, attrs, augs);
                        }
                        return;
                    }
                }
            }
        }
        int depth = 0;
        if (element.flags == 0) {
            int length = this.fElementStack.top;
            this.fInlineStack.top = 0;
            for (int i = length - 1; i >= 0; --i) {
                Info info = this.fElementStack.data[i];
                if (!info.element.isInline()) break;
                this.fInlineStack.push(info);
                this.endElement(info.qname, this.synthesizedAugs());
            }
            depth = this.fInlineStack.top;
        }
        if (this.fElementStack.top > 1 && this.fElementStack.peek().element.code == 90 || this.fElementStack.top > 2 && this.fElementStack.data[this.fElementStack.top - 2].element.code == 44) {
            Info info = this.fElementStack.pop();
            if (this.fDocumentHandler != null) {
                this.callEndElement(info.qname, this.synthesizedAugs());
            }
        }
        if (element.closes != null) {
            int length = this.fElementStack.top;
            for (int i = length - 1; i >= 0; --i) {
                Info info = this.fElementStack.data[i];
                if (element.closes(info.element.code)) {
                    if (this.fReportErrors) {
                        String ename = elem.rawname;
                        String iname = info.qname.rawname;
                        this.fErrorReporter.reportWarning("HTML2005", new Object[]{ename, iname});
                    }
                    for (int j = length - 1; j >= i; --j) {
                        info = this.fElementStack.pop();
                        if (this.fDocumentHandler == null) continue;
                        this.callEndElement(info.qname, this.synthesizedAugs());
                    }
                    length = i;
                    continue;
                }
                if (info.element.isBlock() || element.isParent(info.element)) break;
            }
        }
        this.fSeenRootElement = true;
        if (element != null && element.isEmpty()) {
            if (attrs == null) {
                attrs = this.emptyAttributes();
            }
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.emptyElement(elem, attrs, augs);
            }
        } else {
            boolean inline = element != null && element.isInline();
            this.fElementStack.push(new Info(element, elem, (XMLAttributes)(inline ? attrs : null)));
            if (attrs == null) {
                attrs = this.emptyAttributes();
            }
            if (this.fDocumentHandler != null) {
                this.callStartElement(elem, attrs, augs);
            }
        }
        for (int i = 0; i < depth; ++i) {
            Info info = this.fInlineStack.pop();
            this.forceStartElement(info.qname, info.attributes, this.synthesizedAugs());
        }
        if (elementCode == 14) {
            this.lostText_.refeed((XMLDocumentHandler)this);
        }
    }

    private boolean forceStartElement(QName elem, XMLAttributes attrs, Augmentations augs) throws XNIException {
        this.forcedStartElement_ = true;
        this.startElement(elem, attrs, augs);
        return this.fElementStack.top > 0 && elem.equals((Object)this.fElementStack.peek().qname);
    }

    private QName createQName(String tagName) {
        tagName = HTMLTagBalancer.modifyName(tagName, this.fNamesElems);
        return new QName(null, tagName, tagName, "http://www.w3.org/1999/xhtml");
    }

    public void emptyElement(QName element, XMLAttributes attrs, Augmentations augs) throws XNIException {
        this.startElement(element, attrs, augs);
        HTMLElements.Element elem = this.getElement(element);
        if (elem.isEmpty() || this.fAllowSelfclosingTags || elem.code == 118 || elem.code == 48 && this.fAllowSelfclosingIframe) {
            this.endElement(element, augs);
        }
    }

    public void startGeneralEntity(String name, XMLResourceIdentifier id, String encoding, Augmentations augs) throws XNIException {
        this.fSeenAnything = true;
        if (this.fSeenRootElementEnd) {
            return;
        }
        if (!this.fDocumentFragment) {
            boolean insertBody;
            boolean bl = insertBody = !this.fSeenRootElement;
            if (!insertBody) {
                Info info = this.fElementStack.peek();
                if (info.element.code == 44 || info.element.code == 46) {
                    String hname = HTMLTagBalancer.modifyName("head", this.fNamesElems);
                    String bname = HTMLTagBalancer.modifyName("body", this.fNamesElems);
                    if (this.fReportErrors) {
                        this.fErrorReporter.reportWarning("HTML2009", new Object[]{hname, bname});
                    }
                    this.fQName.setValues(null, hname, hname, null);
                    this.endElement(this.fQName, this.synthesizedAugs());
                    insertBody = true;
                }
            }
            if (insertBody) {
                this.forceStartBody();
            }
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startGeneralEntity(name, id, encoding, augs);
        }
    }

    private void forceStartBody() {
        QName body = this.createQName("body");
        if (this.fReportErrors) {
            this.fErrorReporter.reportWarning("HTML2006", new Object[]{body.localpart});
        }
        this.forceStartElement(body, null, this.synthesizedAugs());
    }

    public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
        this.fSeenAnything = true;
        if (this.fSeenRootElementEnd) {
            return;
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.textDecl(version, encoding, augs);
        }
    }

    public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
        if (this.fSeenRootElementEnd) {
            return;
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endGeneralEntity(name, augs);
        }
    }

    public void startCDATA(Augmentations augs) throws XNIException {
        this.fSeenAnything = true;
        this.consumeEarlyTextIfNeeded();
        if (this.fSeenRootElementEnd) {
            return;
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startCDATA(augs);
        }
    }

    public void endCDATA(Augmentations augs) throws XNIException {
        if (this.fSeenRootElementEnd) {
            return;
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endCDATA(augs);
        }
    }

    public void characters(XMLString text, Augmentations augs) throws XNIException {
        if (this.fSeenRootElementEnd || this.fSeenBodyElementEnd) {
            return;
        }
        if (this.fElementStack.top == 0 && !this.fDocumentFragment) {
            this.lostText_.add(text, augs);
            return;
        }
        boolean whitespace = true;
        for (int i = 0; i < text.length; ++i) {
            if (Character.isWhitespace(text.ch[text.offset + i])) continue;
            whitespace = false;
            break;
        }
        if (!this.fDocumentFragment) {
            if (!this.fSeenRootElement) {
                if (whitespace) {
                    return;
                }
                this.forceStartBody();
            }
            if (whitespace && (this.fElementStack.top < 2 || this.endElementsBuffer_.size() == 1)) {
                return;
            }
            if (!whitespace) {
                Info info = this.fElementStack.peek();
                if (info.element.code == 44 || info.element.code == 46) {
                    String hname = HTMLTagBalancer.modifyName("head", this.fNamesElems);
                    String bname = HTMLTagBalancer.modifyName("body", this.fNamesElems);
                    if (this.fReportErrors) {
                        this.fErrorReporter.reportWarning("HTML2009", new Object[]{hname, bname});
                    }
                    this.forceStartBody();
                }
            }
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.characters(text, augs);
        }
    }

    public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
        this.characters(text, augs);
    }

    public void endElement(QName element, Augmentations augs) throws XNIException {
        Info info;
        int i;
        int size;
        boolean forcedEndElement = this.forcedEndElement_;
        if (this.fSeenRootElementEnd) {
            this.notifyDiscardedEndElement(element, augs);
            return;
        }
        HTMLElements.Element elem = this.getElement(element);
        if (!(this.fIgnoreOutsideContent || elem.code != 14 && elem.code != 46)) {
            this.endElementsBuffer_.add(new ElementEntry(element, augs));
            return;
        }
        if (this.fSeenFramesetElement && elem.code != 36 && elem.code != 37) {
            this.notifyDiscardedEndElement(element, augs);
            return;
        }
        if (elem.code == 46) {
            this.fSeenRootElementEnd = true;
        } else if (this.fIgnoreOutsideContent) {
            if (elem.code == 14) {
                this.fSeenBodyElementEnd = true;
            } else if (this.fSeenBodyElementEnd) {
                this.notifyDiscardedEndElement(element, augs);
                return;
            }
        } else if (elem.code == 35) {
            this.fOpenedForm = false;
        } else if (elem.code == 44 && !forcedEndElement) {
            this.endElementsBuffer_.add(new ElementEntry(element, augs));
            return;
        }
        int depth = this.getElementDepth(elem);
        if (depth == -1) {
            if (elem.code == 77) {
                this.forceStartElement(element, this.emptyAttributes(), this.synthesizedAugs());
                this.endElement(element, augs);
            } else if (!elem.isEmpty()) {
                this.notifyDiscardedEndElement(element, augs);
            }
            return;
        }
        if (depth > 1 && elem.isInline()) {
            size = this.fElementStack.top;
            this.fInlineStack.top = 0;
            for (i = 0; i < depth - 1; ++i) {
                info = this.fElementStack.data[size - i - 1];
                HTMLElements.Element pelem = info.element;
                if (!pelem.isInline() && pelem.code != 34) continue;
                this.fInlineStack.push(info);
            }
        }
        for (int i2 = 0; i2 < depth; ++i2) {
            Info info2 = this.fElementStack.pop();
            if (this.fReportErrors && i2 < depth - 1) {
                String ename = HTMLTagBalancer.modifyName(element.rawname, this.fNamesElems);
                String iname = info2.qname.rawname;
                this.fErrorReporter.reportWarning("HTML2007", new Object[]{ename, iname});
            }
            if (this.fDocumentHandler == null) continue;
            this.callEndElement(info2.qname, i2 < depth - 1 ? this.synthesizedAugs() : augs);
        }
        if (depth > 1) {
            size = this.fInlineStack.top;
            for (i = 0; i < size; ++i) {
                info = this.fInlineStack.pop();
                XMLAttributes attributes = info.attributes;
                if (this.fReportErrors) {
                    String iname = info.qname.rawname;
                    this.fErrorReporter.reportWarning("HTML2008", new Object[]{iname});
                }
                this.forceStartElement(info.qname, attributes, this.synthesizedAugs());
            }
        }
    }

    public void setDocumentSource(XMLDocumentSource source) {
        this.fDocumentSource = source;
    }

    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }

    public void startDocument(XMLLocator locator, String encoding, Augmentations augs) throws XNIException {
        this.startDocument(locator, encoding, null, augs);
    }

    public void startPrefixMapping(String prefix, String uri, Augmentations augs) throws XNIException {
        if (this.fSeenRootElementEnd) {
            return;
        }
        if (this.fDocumentHandler != null) {
            XercesBridge.getInstance().XMLDocumentHandler_startPrefixMapping(this.fDocumentHandler, prefix, uri, augs);
        }
    }

    public void endPrefixMapping(String prefix, Augmentations augs) throws XNIException {
        if (this.fSeenRootElementEnd) {
            return;
        }
        if (this.fDocumentHandler != null) {
            XercesBridge.getInstance().XMLDocumentHandler_endPrefixMapping(this.fDocumentHandler, prefix, augs);
        }
    }

    protected HTMLElements.Element getElement(QName elementName) {
        int index;
        String name = elementName.rawname;
        if (this.fNamespaces && "http://www.w3.org/1999/xhtml".equals(elementName.uri) && (index = name.indexOf(58)) != -1) {
            name = name.substring(index + 1);
        }
        return HTMLElements.getElement(name);
    }

    protected final void callStartElement(QName element, XMLAttributes attrs, Augmentations augs) throws XNIException {
        this.fDocumentHandler.startElement(element, attrs, augs);
    }

    protected final void callEndElement(QName element, Augmentations augs) throws XNIException {
        this.fDocumentHandler.endElement(element, augs);
    }

    protected final int getElementDepth(HTMLElements.Element element) {
        boolean container = element.isContainer();
        short elementCode = element.code;
        boolean tableBodyOrHtml = elementCode == 102 || elementCode == 14 || elementCode == 46;
        int depth = -1;
        for (int i = this.fElementStack.top - 1; i >= this.fragmentContextStackSize_; --i) {
            Info info = this.fElementStack.data[i];
            if (info.element.code == element.code && (elementCode != 118 || elementCode == 118 && element.name.equals(info.element.name))) {
                depth = this.fElementStack.top - i;
                break;
            }
            if (!container && info.element.isBlock()) break;
            if (info.element.code == 102 && !tableBodyOrHtml) {
                return -1;
            }
            if (element.isParent(info.element)) break;
        }
        return depth;
    }

    protected int getParentDepth(HTMLElements.Element[] parents, short bounds) {
        if (parents != null) {
            for (int i = this.fElementStack.top - 1; i >= 0; --i) {
                Info info = this.fElementStack.data[i];
                if (info.element.code == bounds) break;
                for (int j = 0; j < parents.length; ++j) {
                    if (info.element.code != parents[j].code) continue;
                    return this.fElementStack.top - i;
                }
            }
        }
        return -1;
    }

    protected final XMLAttributes emptyAttributes() {
        this.fEmptyAttrs.removeAllAttributes();
        return this.fEmptyAttrs;
    }

    protected final Augmentations synthesizedAugs() {
        HTMLAugmentations augs = null;
        if (this.fAugmentations) {
            augs = this.fInfosetAugs;
            augs.removeAllItems();
            augs.putItem(AUGMENTATIONS, SYNTHESIZED_ITEM);
        }
        return augs;
    }

    protected static final String modifyName(String name, short mode) {
        switch (mode) {
            case 1: {
                return name.toUpperCase(Locale.ENGLISH);
            }
            case 2: {
                return name.toLowerCase(Locale.ENGLISH);
            }
        }
        return name;
    }

    protected static final short getNamesValue(String value) {
        if (value.equals("lower")) {
            return 2;
        }
        if (value.equals("upper")) {
            return 1;
        }
        return 0;
    }

    void setTagBalancingListener(HTMLTagBalancingListener tagBalancingListener) {
        this.tagBalancingListener = tagBalancingListener;
    }

    private void notifyDiscardedStartElement(QName elem, XMLAttributes attrs, Augmentations augs) {
        if (this.tagBalancingListener != null) {
            this.tagBalancingListener.ignoredStartElement(elem, attrs, augs);
        }
    }

    private void notifyDiscardedEndElement(QName element, Augmentations augs) {
        if (this.tagBalancingListener != null) {
            this.tagBalancingListener.ignoredEndElement(element, augs);
        }
    }

    static class ElementEntry {
        private final QName name_;
        private final Augmentations augs_;

        ElementEntry(QName element, Augmentations augs) {
            this.name_ = new QName(element);
            this.augs_ = augs == null ? null : new HTMLAugmentations(augs);
        }
    }

    public static class InfoStack {
        public int top;
        public Info[] data = new Info[10];

        public void push(Info info) {
            if (this.top == this.data.length) {
                Info[] newarray = new Info[this.top + 10];
                System.arraycopy(this.data, 0, newarray, 0, this.top);
                this.data = newarray;
            }
            this.data[this.top++] = info;
        }

        public Info peek() {
            return this.data[this.top - 1];
        }

        public Info pop() {
            return this.data[--this.top];
        }

        public String toString() {
            StringBuffer sb = new StringBuffer("InfoStack(");
            for (int i = this.top - 1; i >= 0; --i) {
                sb.append(this.data[i]);
                if (i == 0) continue;
                sb.append(", ");
            }
            sb.append(")");
            return sb.toString();
        }
    }

    public static class Info {
        public HTMLElements.Element element;
        public QName qname;
        public XMLAttributes attributes;

        public Info(HTMLElements.Element element, QName qname) {
            this(element, qname, null);
        }

        public Info(HTMLElements.Element element, QName qname, XMLAttributes attributes) {
            int length;
            this.element = element;
            this.qname = new QName(qname);
            if (attributes != null && (length = attributes.getLength()) > 0) {
                QName aqname = new QName();
                XMLAttributesImpl newattrs = new XMLAttributesImpl();
                for (int i = 0; i < length; ++i) {
                    attributes.getName(i, aqname);
                    String type = attributes.getType(i);
                    String value = attributes.getValue(i);
                    String nonNormalizedValue = attributes.getNonNormalizedValue(i);
                    boolean specified = attributes.isSpecified(i);
                    newattrs.addAttribute(aqname, type, value);
                    newattrs.setNonNormalizedValue(i, nonNormalizedValue);
                    newattrs.setSpecified(i, specified);
                }
                this.attributes = newattrs;
            }
        }

        public String toString() {
            return super.toString() + this.qname;
        }
    }
}

