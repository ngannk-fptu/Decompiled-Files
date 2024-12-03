/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.htmlunit.cyberneko.HTMLComponent;
import org.htmlunit.cyberneko.HTMLConfiguration;
import org.htmlunit.cyberneko.HTMLElements;
import org.htmlunit.cyberneko.HTMLErrorReporter;
import org.htmlunit.cyberneko.HTMLEventInfo;
import org.htmlunit.cyberneko.HTMLTagBalancingListener;
import org.htmlunit.cyberneko.LostText;
import org.htmlunit.cyberneko.xerces.util.XMLAttributesImpl;
import org.htmlunit.cyberneko.xerces.xni.Augmentations;
import org.htmlunit.cyberneko.xerces.xni.NamespaceContext;
import org.htmlunit.cyberneko.xerces.xni.QName;
import org.htmlunit.cyberneko.xerces.xni.XMLAttributes;
import org.htmlunit.cyberneko.xerces.xni.XMLDocumentHandler;
import org.htmlunit.cyberneko.xerces.xni.XMLLocator;
import org.htmlunit.cyberneko.xerces.xni.XMLString;
import org.htmlunit.cyberneko.xerces.xni.XNIException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLComponentManager;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLConfigurationException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLDocumentFilter;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLDocumentSource;

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
    private static final short NAMES_NO_CHANGE = 0;
    private static final short NAMES_UPPERCASE = 1;
    private static final short NAMES_LOWERCASE = 2;
    private static final HTMLEventInfo SYNTHESIZED_ITEM = new HTMLEventInfo.SynthesizedItem();
    protected boolean fNamespaces;
    protected boolean fAugmentations;
    protected boolean fReportErrors;
    protected boolean fDocumentFragment;
    protected boolean fTemplateFragment;
    protected boolean fIgnoreOutsideContent;
    protected boolean fAllowSelfclosingIframe;
    protected boolean fAllowSelfclosingTags;
    protected short fNamesElems;
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
    private boolean fSeenCharacters;
    protected boolean fOpenedForm;
    protected boolean fOpenedSvg;
    protected boolean fOpenedSelect;
    private final QName fQName = new QName();
    protected HTMLTagBalancingListener tagBalancingListener;
    private final LostText lostText_ = new LostText();
    private boolean forcedStartElement_;
    private boolean forcedEndElement_;
    private QName[] fragmentContextStack_ = null;
    private int fragmentContextStackSize_ = 0;
    private final List<ElementEntry> endElementsBuffer_ = new ArrayList<ElementEntry>();
    private final List<String> discardedStartElements = new ArrayList<String>();
    private final HTMLConfiguration htmlConfiguration_;

    HTMLTagBalancer(HTMLConfiguration htmlConfiguration) {
        this.htmlConfiguration_ = htmlConfiguration;
    }

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

    @Override
    public String[] getRecognizedFeatures() {
        return RECOGNIZED_FEATURES;
    }

    @Override
    public String[] getRecognizedProperties() {
        return RECOGNIZED_PROPERTIES;
    }

    @Override
    public void reset(XMLComponentManager manager) throws XMLConfigurationException {
        this.fNamespaces = manager.getFeature(NAMESPACES);
        this.fAugmentations = manager.getFeature(AUGMENTATIONS);
        this.fReportErrors = manager.getFeature(REPORT_ERRORS);
        this.fDocumentFragment = manager.getFeature(DOCUMENT_FRAGMENT) || manager.getFeature(DOCUMENT_FRAGMENT_DEPRECATED);
        this.fIgnoreOutsideContent = manager.getFeature(IGNORE_OUTSIDE_CONTENT);
        this.fAllowSelfclosingIframe = manager.getFeature("http://cyberneko.org/html/features/scanner/allow-selfclosing-iframe");
        this.fAllowSelfclosingTags = manager.getFeature("http://cyberneko.org/html/features/scanner/allow-selfclosing-tags");
        this.fNamesElems = HTMLTagBalancer.getNamesValue(String.valueOf(manager.getProperty(NAMES_ELEMS)));
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
        this.fSeenCharacters = false;
        this.fTemplateFragment = false;
        this.fOpenedForm = false;
        this.fOpenedSvg = false;
        this.fOpenedSelect = false;
        this.lostText_.clear();
        this.forcedStartElement_ = false;
        this.forcedEndElement_ = false;
        this.endElementsBuffer_.clear();
        this.discardedStartElements.clear();
    }

    @Override
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

    @Override
    public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
        if (propertyId.equals(NAMES_ELEMS)) {
            this.fNamesElems = HTMLTagBalancer.getNamesValue(String.valueOf(value));
            return;
        }
    }

    @Override
    public void setDocumentHandler(XMLDocumentHandler handler) {
        this.fDocumentHandler = handler;
    }

    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }

    @Override
    public void startDocument(XMLLocator locator, String encoding, NamespaceContext nscontext, Augmentations augs) throws XNIException {
        this.fElementStack.top = 0;
        if (this.fragmentContextStack_ != null) {
            this.fragmentContextStackSize_ = this.fragmentContextStack_.length;
            for (QName name : this.fragmentContextStack_) {
                HTMLElements.Element elt = this.htmlConfiguration_.getHtmlElements().getElement(name.localpart);
                this.fElementStack.push(new Info(elt, name));
            }
        } else {
            this.fragmentContextStackSize_ = 0;
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startDocument(locator, encoding, nscontext, augs);
        }
    }

    @Override
    public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
        if (!this.fSeenAnything && this.fDocumentHandler != null) {
            this.fDocumentHandler.xmlDecl(version, encoding, standalone, augs);
        }
    }

    @Override
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

    @Override
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
                this.addBodyIfNeeded(info.element.code);
                this.callEndElement(info.qname, this.synthesizedAugs());
            }
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endDocument(augs);
        }
    }

    private void consumeBufferedEndElements() {
        if (this.endElementsBuffer_.isEmpty()) {
            return;
        }
        ArrayList<ElementEntry> toConsume = new ArrayList<ElementEntry>(this.endElementsBuffer_);
        this.endElementsBuffer_.clear();
        for (ElementEntry entry : toConsume) {
            this.forcedEndElement_ = true;
            this.endElement(entry.name_, entry.augs_);
        }
        this.endElementsBuffer_.clear();
    }

    @Override
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
            this.lostText_.refeed(this);
        }
    }

    @Override
    public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
        this.fSeenAnything = true;
        this.consumeEarlyTextIfNeeded();
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.processingInstruction(target, data, augs);
        }
    }

    @Override
    public void startElement(QName elem, XMLAttributes attrs, Augmentations augs) throws XNIException {
        Info info;
        this.fSeenAnything = true;
        boolean isForcedCreation = this.forcedStartElement_;
        this.forcedStartElement_ = false;
        if (this.fSeenRootElementEnd) {
            this.notifyDiscardedStartElement(elem, attrs, augs);
            return;
        }
        HTMLElements.Element element = this.getElement(elem);
        short elementCode = element.code;
        if (elementCode == 128) {
            this.fTemplateFragment = true;
        }
        if (isForcedCreation && (elementCode == 125 || elementCode == 111)) {
            return;
        }
        if (this.fSeenRootElement && elementCode == 59 && !this.fOpenedSvg) {
            this.notifyDiscardedStartElement(elem, attrs, augs);
            return;
        }
        if (this.fSeenFramesetElement && elementCode != 48 && elementCode != 49 && elementCode != 87) {
            this.notifyDiscardedStartElement(elem, attrs, augs);
            return;
        }
        if (!this.fTemplateFragment && this.fOpenedSelect) {
            if (elementCode == 111) {
                QName head = this.createQName("SELECT");
                this.endElement(head, this.synthesizedAugs());
                this.notifyDiscardedStartElement(elem, attrs, augs);
                return;
            }
            if (elementCode != 93 && elementCode != 92 && elementCode != 109) {
                this.notifyDiscardedStartElement(elem, attrs, augs);
                return;
            }
        }
        if (elementCode == 56) {
            if (this.fSeenHeadElement) {
                this.notifyDiscardedStartElement(elem, attrs, augs);
                return;
            }
            this.fSeenHeadElement = true;
        } else if (!this.fOpenedSvg && elementCode == 49) {
            if (this.fSeenBodyElement && this.fSeenCharacters) {
                this.notifyDiscardedStartElement(elem, attrs, augs);
                return;
            }
            if (!this.fSeenHeadElement) {
                QName head = this.createQName("head");
                this.forceStartElement(head, new XMLAttributesImpl(), this.synthesizedAugs());
                this.endElement(head, this.synthesizedAugs());
            }
            this.consumeBufferedEndElements();
            this.fSeenFramesetElement = true;
        } else if (elementCode == 18) {
            if (!this.fSeenHeadElement) {
                QName head = this.createQName("head");
                this.forceStartElement(head, new XMLAttributesImpl(), this.synthesizedAugs());
                this.endElement(head, this.synthesizedAugs());
            }
            this.consumeBufferedEndElements();
            if (this.fSeenBodyElement) {
                this.notifyDiscardedStartElement(elem, attrs, augs);
                return;
            }
            this.fSeenBodyElement = true;
        } else if (elementCode == 47) {
            if (this.fOpenedForm) {
                this.notifyDiscardedStartElement(elem, attrs, augs);
                return;
            }
            this.fOpenedForm = true;
            for (int i = this.fElementStack.top - 1; i >= 0; --i) {
                info = this.fElementStack.data[i];
                if (info.element.code != 127 && info.element.code != 131 && info.element.code != 22) {
                    if (info.element.code != 135 && info.element.code != 132 && info.element.code != 126 && info.element.code != 130 && info.element.code != 125) continue;
                    if (this.fDocumentHandler != null) {
                        this.callStartElement(elem, attrs, augs);
                        this.callEndElement(this.createQName("form"), this.synthesizedAugs());
                    }
                    this.fOpenedForm = false;
                    return;
                }
                break;
            }
        } else if (elementCode == 146) {
            this.consumeBufferedEndElements();
        } else if (elementCode == 125) {
            for (int i = this.fElementStack.top - 1; i >= 0; --i) {
                info = this.fElementStack.data[i];
                if (info.element.code == 127 || info.element.code == 131 || info.element.code == 22) break;
                if (info.element.code != 135 && info.element.code != 132 && info.element.code != 126 && info.element.code != 130 && info.element.code != 125) continue;
                QName table = this.createQName("table");
                this.endElement(table, this.synthesizedAugs());
                break;
            }
        }
        if (element.parent != null && !this.fOpenedSvg) {
            HTMLElements.Element preferedParent = element.parent[0];
            if (!(this.fDocumentFragment && (preferedParent.code == 56 || preferedParent.code == 18) || this.fTemplateFragment && this.fElementStack.top > 0 && this.fElementStack.data[this.fElementStack.top - 1].element.code == 128)) {
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
                    if (!(parentCreated = this.forceStartElement(qname = this.createQName(pname), new XMLAttributesImpl(), this.synthesizedAugs()))) {
                        if (!isForcedCreation) {
                            this.notifyDiscardedStartElement(elem, attrs, augs);
                        }
                        return;
                    }
                } else if ((preferedParent.code != 56 || !this.fSeenBodyElement && !this.fDocumentFragment) && (depth = this.getParentDepth(element.parent, element.bounds)) == -1) {
                    boolean parentCreated;
                    String pname = HTMLTagBalancer.modifyName(preferedParent.name, this.fNamesElems);
                    QName qname = this.createQName(pname);
                    if (this.fReportErrors) {
                        String ename = elem.rawname;
                        this.fErrorReporter.reportWarning("HTML2004", new Object[]{ename, pname});
                    }
                    if (!(parentCreated = this.forceStartElement(qname, new XMLAttributesImpl(), this.synthesizedAugs()))) {
                        if (!isForcedCreation) {
                            this.notifyDiscardedStartElement(elem, attrs, augs);
                        }
                        return;
                    }
                }
            }
        }
        if (elementCode == 124) {
            this.fOpenedSvg = true;
        } else if (!this.fTemplateFragment && elementCode == 111) {
            this.fOpenedSelect = true;
        }
        int depth = 0;
        if (element.flags == 0) {
            int length = this.fElementStack.top;
            this.fInlineStack.top = 0;
            for (int i = length - 1; i >= 0; --i) {
                Info info2 = this.fElementStack.data[i];
                if (!info2.element.isInline()) break;
                this.fInlineStack.push(info2);
                this.endElement(info2.qname, this.synthesizedAugs());
            }
            depth = this.fInlineStack.top;
        }
        if (this.fElementStack.top > 1 && this.fElementStack.peek().element.code == 109 || this.fElementStack.top > 2 && this.fElementStack.data[this.fElementStack.top - 2].element.code == 56) {
            Info info3 = this.fElementStack.pop();
            if (this.fDocumentHandler != null) {
                this.callEndElement(info3.qname, this.synthesizedAugs());
            }
        }
        if (element.closes != null) {
            int length = this.fElementStack.top;
            for (int i = length - 1; i >= 0; --i) {
                Info info4 = this.fElementStack.data[i];
                if (element.closes(info4.element.code)) {
                    if (this.fReportErrors) {
                        String ename = elem.rawname;
                        String iname = info4.qname.rawname;
                        this.fErrorReporter.reportWarning("HTML2005", new Object[]{ename, iname});
                    }
                    for (int j = length - 1; j >= i; --j) {
                        info4 = this.fElementStack.pop();
                        if (j < this.fragmentContextStackSize_) {
                            --this.fragmentContextStackSize_;
                        }
                        if (this.fDocumentHandler == null) continue;
                        this.callEndElement(info4.qname, this.synthesizedAugs());
                    }
                    length = i;
                    continue;
                }
                if (info4.element.code == 128 || info4.element.isBlock() || element.isParent(info4.element)) break;
            }
        }
        this.fSeenRootElement = true;
        if (element.isEmpty()) {
            if (attrs == null) {
                attrs = new XMLAttributesImpl();
            }
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.emptyElement(elem, attrs, augs);
            }
        } else {
            boolean inline = element.isInline();
            this.fElementStack.push(new Info(element, elem, inline ? attrs : null));
            if (attrs == null) {
                attrs = new XMLAttributesImpl();
            }
            if (this.fDocumentHandler != null) {
                this.callStartElement(elem, attrs, augs);
            }
        }
        for (int i = 0; i < depth; ++i) {
            Info info5 = this.fInlineStack.pop();
            this.forceStartElement(info5.qname, info5.attributes, this.synthesizedAugs());
        }
        if (elementCode == 18) {
            this.lostText_.refeed(this);
        }
    }

    private boolean forceStartElement(QName elem, XMLAttributes attrs, Augmentations augs) throws XNIException {
        this.forcedStartElement_ = true;
        this.startElement(elem, attrs, augs);
        return this.fElementStack.top > 0 && elem.equals(this.fElementStack.peek().qname);
    }

    private QName createQName(String tagName) {
        tagName = HTMLTagBalancer.modifyName(tagName, this.fNamesElems);
        return new QName(null, tagName, tagName, "http://www.w3.org/1999/xhtml");
    }

    @Override
    public void emptyElement(QName element, XMLAttributes attrs, Augmentations augs) throws XNIException {
        this.startElement(element, attrs, augs);
        HTMLElements.Element elem = this.getElement(element);
        if (elem.isEmpty() || this.fAllowSelfclosingTags || elem.code == 146 || elem.code == 61 && this.fAllowSelfclosingIframe) {
            this.endElement(element, augs);
        }
    }

    @Override
    public void startGeneralEntity(String name, String encoding, Augmentations augs) throws XNIException {
        this.fSeenAnything = true;
        if (this.fSeenRootElementEnd) {
            return;
        }
        if (!this.fDocumentFragment) {
            boolean insertBody;
            boolean bl = insertBody = !this.fSeenRootElement;
            if (!insertBody) {
                Info info = this.fElementStack.peek();
                if (info.element.code == 56 || info.element.code == 59) {
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
            this.fDocumentHandler.startGeneralEntity(name, encoding, augs);
        }
    }

    private void forceStartBody() {
        QName body = this.createQName("body");
        if (this.fReportErrors) {
            this.fErrorReporter.reportWarning("HTML2006", new Object[]{body.localpart});
        }
        this.forceStartElement(body, new XMLAttributesImpl(), this.synthesizedAugs());
    }

    @Override
    public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
        this.fSeenAnything = true;
        if (this.fSeenRootElementEnd) {
            return;
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.textDecl(version, encoding, augs);
        }
    }

    @Override
    public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
        if (this.fSeenRootElementEnd) {
            return;
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endGeneralEntity(name, augs);
        }
    }

    @Override
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

    @Override
    public void endCDATA(Augmentations augs) throws XNIException {
        if (this.fSeenRootElementEnd) {
            return;
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endCDATA(augs);
        }
    }

    @Override
    public void characters(XMLString text, Augmentations augs) throws XNIException {
        if (this.fSeenRootElementEnd || this.fSeenBodyElementEnd) {
            return;
        }
        if (this.fElementStack.top == 0 && !this.fDocumentFragment) {
            this.lostText_.add(text, augs);
            return;
        }
        boolean whitespace = text.isWhitespace();
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
                if (info.element.code == 56 || info.element.code == 59) {
                    String hname = HTMLTagBalancer.modifyName("head", this.fNamesElems);
                    String bname = HTMLTagBalancer.modifyName("body", this.fNamesElems);
                    if (this.fReportErrors) {
                        this.fErrorReporter.reportWarning("HTML2009", new Object[]{hname, bname});
                    }
                    this.forceStartBody();
                }
            }
        }
        boolean bl = this.fSeenCharacters = this.fSeenCharacters || !whitespace;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.characters(text, augs);
        }
    }

    @Override
    public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
        this.characters(text, augs);
    }

    @Override
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
        short elementCode = elem.code;
        if (!this.fTemplateFragment && this.fOpenedSelect) {
            if (elementCode == 111) {
                this.fOpenedSelect = false;
            } else if (elementCode != 93 && elementCode != 92 && elementCode != 109) {
                this.notifyDiscardedEndElement(element, augs);
                return;
            }
        }
        if (elementCode == 128) {
            this.fTemplateFragment = false;
        }
        if (!(this.fIgnoreOutsideContent || elementCode != 18 && elementCode != 59)) {
            Iterator<String> it = this.discardedStartElements.iterator();
            while (it.hasNext()) {
                if (!element.rawname.equals(it.next())) continue;
                it.remove();
                return;
            }
            this.endElementsBuffer_.add(new ElementEntry(element, augs));
            return;
        }
        if (this.fSeenFramesetElement && elementCode != 48 && elementCode != 49) {
            this.notifyDiscardedEndElement(element, augs);
            return;
        }
        if (elementCode == 59) {
            this.fSeenRootElementEnd = true;
        } else if (this.fIgnoreOutsideContent) {
            if (elementCode == 18) {
                this.fSeenBodyElementEnd = true;
            } else if (this.fSeenBodyElementEnd) {
                this.notifyDiscardedEndElement(element, augs);
                return;
            }
        } else if (elementCode == 47) {
            this.fOpenedForm = false;
        } else if (elementCode == 124) {
            this.fOpenedSvg = false;
        } else if (elementCode == 56 && !forcedEndElement) {
            this.endElementsBuffer_.add(new ElementEntry(element, augs));
            return;
        }
        int depth = this.getElementDepth(elem);
        if (depth == -1) {
            if (elementCode == 94) {
                this.forceStartElement(element, new XMLAttributesImpl(), this.synthesizedAugs());
                this.endElement(element, augs);
            } else if (elementCode == 19) {
                this.forceStartElement(element, new XMLAttributesImpl(), this.synthesizedAugs());
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
                if (!pelem.isInline() && pelem.code != 45) continue;
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
            this.addBodyIfNeeded(info2.element.code);
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

    @Override
    public void setDocumentSource(XMLDocumentSource source) {
        this.fDocumentSource = source;
    }

    @Override
    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }

    protected HTMLElements.Element getElement(QName elementName) {
        int index;
        String name = elementName.rawname;
        if (this.fNamespaces && "http://www.w3.org/1999/xhtml".equals(elementName.uri) && (index = name.indexOf(58)) != -1) {
            name = name.substring(index + 1);
        }
        return this.htmlConfiguration_.getHtmlElements().getElement(name);
    }

    protected final void callStartElement(QName element, XMLAttributes attrs, Augmentations augs) throws XNIException {
        this.fDocumentHandler.startElement(element, attrs, augs);
    }

    private void addBodyIfNeeded(short element) {
        if (!this.fDocumentFragment && !this.fSeenFramesetElement && element == 59) {
            if (!this.fSeenHeadElement) {
                QName head = this.createQName("head");
                this.callStartElement(head, new XMLAttributesImpl(), this.synthesizedAugs());
                this.callEndElement(head, this.synthesizedAugs());
            }
            if (!this.fSeenBodyElement) {
                QName body = this.createQName("body");
                this.callStartElement(body, new XMLAttributesImpl(), this.synthesizedAugs());
                this.callEndElement(body, this.synthesizedAugs());
            }
        }
    }

    protected final void callEndElement(QName element, Augmentations augs) throws XNIException {
        this.fDocumentHandler.endElement(element, augs);
    }

    protected final int getElementDepth(HTMLElements.Element element) {
        boolean container = element.isContainer();
        short elementCode = element.code;
        boolean tableBodyOrHtml = elementCode == 125 || elementCode == 18 || elementCode == 59;
        int depth = -1;
        for (int i = this.fElementStack.top - 1; i >= this.fragmentContextStackSize_; --i) {
            Info info = this.fElementStack.data[i];
            if (info.element.code == element.code && (elementCode != 146 || elementCode == 146 && element.name.equals(info.element.name))) {
                depth = this.fElementStack.top - i;
                break;
            }
            if (!container && info.element.isBlock()) break;
            if (info.element.code == 125 && !tableBodyOrHtml) {
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
                for (HTMLElements.Element parent : parents) {
                    if (info.element.code != parent.code) continue;
                    return this.fElementStack.top - i;
                }
            }
        }
        return -1;
    }

    protected final Augmentations synthesizedAugs() {
        if (this.fAugmentations) {
            return SYNTHESIZED_ITEM;
        }
        return null;
    }

    protected static String modifyName(String name, short mode) {
        switch (mode) {
            case 1: {
                return name.toUpperCase(Locale.ROOT);
            }
            case 2: {
                return name.toLowerCase(Locale.ROOT);
            }
        }
        return name;
    }

    protected static short getNamesValue(String value) {
        if ("lower".equals(value)) {
            return 2;
        }
        if ("upper".equals(value)) {
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
        this.discardedStartElements.add(elem.rawname);
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
            this.augs_ = augs;
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
            StringBuilder sb = new StringBuilder("InfoStack(");
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
        public final HTMLElements.Element element;
        public final QName qname;
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
                    boolean specified = attributes.isSpecified(i);
                    newattrs.addAttribute(aqname, type, value);
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

