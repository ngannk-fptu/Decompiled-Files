/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.ParsedURL
 *  org.w3c.css.sac.CSSException
 *  org.w3c.css.sac.DocumentHandler
 *  org.w3c.css.sac.InputSource
 *  org.w3c.css.sac.LexicalUnit
 *  org.w3c.css.sac.SACMediaList
 *  org.w3c.css.sac.SelectorList
 */
package org.apache.batik.css.engine;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.batik.css.engine.CSSContext;
import org.apache.batik.css.engine.CSSEngineEvent;
import org.apache.batik.css.engine.CSSEngineListener;
import org.apache.batik.css.engine.CSSEngineUserAgent;
import org.apache.batik.css.engine.CSSNavigableDocument;
import org.apache.batik.css.engine.CSSNavigableDocumentListener;
import org.apache.batik.css.engine.CSSNavigableNode;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.CSSStyleSheetNode;
import org.apache.batik.css.engine.FontFaceRule;
import org.apache.batik.css.engine.ImportRule;
import org.apache.batik.css.engine.MediaRule;
import org.apache.batik.css.engine.Messages;
import org.apache.batik.css.engine.Rule;
import org.apache.batik.css.engine.StringIntMap;
import org.apache.batik.css.engine.StyleDeclaration;
import org.apache.batik.css.engine.StyleDeclarationProvider;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.StyleRule;
import org.apache.batik.css.engine.StyleSheet;
import org.apache.batik.css.engine.sac.CSSConditionFactory;
import org.apache.batik.css.engine.sac.CSSSelectorFactory;
import org.apache.batik.css.engine.sac.ExtendedSelector;
import org.apache.batik.css.engine.value.ComputedValue;
import org.apache.batik.css.engine.value.InheritValue;
import org.apache.batik.css.engine.value.ShorthandManager;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.css.parser.ExtendedParser;
import org.apache.batik.util.ParsedURL;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.SelectorList;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

public abstract class CSSEngine {
    protected CSSEngineUserAgent userAgent;
    protected CSSContext cssContext;
    protected Document document;
    protected ParsedURL documentURI;
    protected boolean isCSSNavigableDocument;
    protected StringIntMap indexes;
    protected StringIntMap shorthandIndexes;
    protected ValueManager[] valueManagers;
    protected ShorthandManager[] shorthandManagers;
    protected ExtendedParser parser;
    protected String[] pseudoElementNames;
    protected int fontSizeIndex = -1;
    protected int lineHeightIndex = -1;
    protected int colorIndex = -1;
    protected StyleSheet userAgentStyleSheet;
    protected StyleSheet userStyleSheet;
    protected SACMediaList media;
    protected List styleSheetNodes;
    protected List fontFaces = new LinkedList();
    protected String styleNamespaceURI;
    protected String styleLocalName;
    protected String classNamespaceURI;
    protected String classLocalName;
    protected Set nonCSSPresentationalHints;
    protected String nonCSSPresentationalHintsNamespaceURI;
    protected StyleDeclarationDocumentHandler styleDeclarationDocumentHandler = new StyleDeclarationDocumentHandler();
    protected StyleDeclarationUpdateHandler styleDeclarationUpdateHandler;
    protected StyleSheetDocumentHandler styleSheetDocumentHandler = new StyleSheetDocumentHandler();
    protected StyleDeclarationBuilder styleDeclarationBuilder = new StyleDeclarationBuilder();
    protected CSSStylableElement element;
    protected ParsedURL cssBaseURI;
    protected String alternateStyleSheet;
    protected CSSNavigableDocumentHandler cssNavigableDocumentListener;
    protected EventListener domAttrModifiedListener;
    protected EventListener domNodeInsertedListener;
    protected EventListener domNodeRemovedListener;
    protected EventListener domSubtreeModifiedListener;
    protected EventListener domCharacterDataModifiedListener;
    protected boolean styleSheetRemoved;
    protected Node removedStylableElementSibling;
    protected List listeners = Collections.synchronizedList(new LinkedList());
    protected Set selectorAttributes;
    protected final int[] ALL_PROPERTIES;
    protected CSSConditionFactory cssConditionFactory;
    protected static final CSSEngineListener[] LISTENER_ARRAY = new CSSEngineListener[0];

    public static Node getCSSParentNode(Node n) {
        if (n instanceof CSSNavigableNode) {
            return ((CSSNavigableNode)((Object)n)).getCSSParentNode();
        }
        return n.getParentNode();
    }

    protected static Node getCSSFirstChild(Node n) {
        if (n instanceof CSSNavigableNode) {
            return ((CSSNavigableNode)((Object)n)).getCSSFirstChild();
        }
        return n.getFirstChild();
    }

    protected static Node getCSSNextSibling(Node n) {
        if (n instanceof CSSNavigableNode) {
            return ((CSSNavigableNode)((Object)n)).getCSSNextSibling();
        }
        return n.getNextSibling();
    }

    protected static Node getCSSPreviousSibling(Node n) {
        if (n instanceof CSSNavigableNode) {
            return ((CSSNavigableNode)((Object)n)).getCSSPreviousSibling();
        }
        return n.getPreviousSibling();
    }

    public static CSSStylableElement getParentCSSStylableElement(Element elt) {
        Node n = CSSEngine.getCSSParentNode(elt);
        while (n != null) {
            if (n instanceof CSSStylableElement) {
                return (CSSStylableElement)n;
            }
            n = CSSEngine.getCSSParentNode(n);
        }
        return null;
    }

    protected CSSEngine(Document doc, ParsedURL uri, ExtendedParser p, ValueManager[] vm, ShorthandManager[] sm, String[] pe, String sns, String sln, String cns, String cln, boolean hints, String hintsNS, CSSContext ctx) {
        String pn;
        int i;
        this.document = doc;
        this.documentURI = uri;
        this.parser = p;
        this.pseudoElementNames = pe;
        this.styleNamespaceURI = sns;
        this.styleLocalName = sln;
        this.classNamespaceURI = cns;
        this.classLocalName = cln;
        this.cssContext = ctx;
        this.isCSSNavigableDocument = doc instanceof CSSNavigableDocument;
        this.cssConditionFactory = new CSSConditionFactory(cns, cln, null, "id");
        int len = vm.length;
        this.indexes = new StringIntMap(len);
        this.valueManagers = vm;
        for (i = len - 1; i >= 0; --i) {
            pn = vm[i].getPropertyName();
            this.indexes.put(pn, i);
            if (this.fontSizeIndex == -1 && pn.equals("font-size")) {
                this.fontSizeIndex = i;
            }
            if (this.lineHeightIndex == -1 && pn.equals("line-height")) {
                this.lineHeightIndex = i;
            }
            if (this.colorIndex != -1 || !pn.equals("color")) continue;
            this.colorIndex = i;
        }
        len = sm.length;
        this.shorthandIndexes = new StringIntMap(len);
        this.shorthandManagers = sm;
        for (i = len - 1; i >= 0; --i) {
            this.shorthandIndexes.put(sm[i].getPropertyName(), i);
        }
        if (hints) {
            this.nonCSSPresentationalHints = new HashSet(vm.length + sm.length);
            this.nonCSSPresentationalHintsNamespaceURI = hintsNS;
            len = vm.length;
            for (i = 0; i < len; ++i) {
                pn = vm[i].getPropertyName();
                this.nonCSSPresentationalHints.add(pn);
            }
            len = sm.length;
            for (i = 0; i < len; ++i) {
                pn = sm[i].getPropertyName();
                this.nonCSSPresentationalHints.add(pn);
            }
        }
        if (this.cssContext.isDynamic() && this.document instanceof EventTarget) {
            this.addEventListeners((EventTarget)((Object)this.document));
            this.styleDeclarationUpdateHandler = new StyleDeclarationUpdateHandler();
        }
        this.ALL_PROPERTIES = new int[this.getNumberOfProperties()];
        for (i = this.getNumberOfProperties() - 1; i >= 0; --i) {
            this.ALL_PROPERTIES[i] = i;
        }
    }

    protected void addEventListeners(EventTarget doc) {
        if (this.isCSSNavigableDocument) {
            this.cssNavigableDocumentListener = new CSSNavigableDocumentHandler();
            CSSNavigableDocument cnd = (CSSNavigableDocument)((Object)doc);
            cnd.addCSSNavigableDocumentListener(this.cssNavigableDocumentListener);
        } else {
            this.domAttrModifiedListener = new DOMAttrModifiedListener();
            doc.addEventListener("DOMAttrModified", this.domAttrModifiedListener, false);
            this.domNodeInsertedListener = new DOMNodeInsertedListener();
            doc.addEventListener("DOMNodeInserted", this.domNodeInsertedListener, false);
            this.domNodeRemovedListener = new DOMNodeRemovedListener();
            doc.addEventListener("DOMNodeRemoved", this.domNodeRemovedListener, false);
            this.domSubtreeModifiedListener = new DOMSubtreeModifiedListener();
            doc.addEventListener("DOMSubtreeModified", this.domSubtreeModifiedListener, false);
            this.domCharacterDataModifiedListener = new DOMCharacterDataModifiedListener();
            doc.addEventListener("DOMCharacterDataModified", this.domCharacterDataModifiedListener, false);
        }
    }

    protected void removeEventListeners(EventTarget doc) {
        if (this.isCSSNavigableDocument) {
            CSSNavigableDocument cnd = (CSSNavigableDocument)((Object)doc);
            cnd.removeCSSNavigableDocumentListener(this.cssNavigableDocumentListener);
        } else {
            doc.removeEventListener("DOMAttrModified", this.domAttrModifiedListener, false);
            doc.removeEventListener("DOMNodeInserted", this.domNodeInsertedListener, false);
            doc.removeEventListener("DOMNodeRemoved", this.domNodeRemovedListener, false);
            doc.removeEventListener("DOMSubtreeModified", this.domSubtreeModifiedListener, false);
            doc.removeEventListener("DOMCharacterDataModified", this.domCharacterDataModifiedListener, false);
        }
    }

    public void dispose() {
        this.setCSSEngineUserAgent(null);
        this.disposeStyleMaps(this.document.getDocumentElement());
        if (this.document instanceof EventTarget) {
            this.removeEventListeners((EventTarget)((Object)this.document));
        }
    }

    protected void disposeStyleMaps(Node node) {
        if (node instanceof CSSStylableElement) {
            ((CSSStylableElement)node).setComputedStyleMap(null, null);
        }
        Node n = CSSEngine.getCSSFirstChild(node);
        while (n != null) {
            if (n.getNodeType() == 1) {
                this.disposeStyleMaps(n);
            }
            n = CSSEngine.getCSSNextSibling(n);
        }
    }

    public CSSContext getCSSContext() {
        return this.cssContext;
    }

    public Document getDocument() {
        return this.document;
    }

    public int getFontSizeIndex() {
        return this.fontSizeIndex;
    }

    public int getLineHeightIndex() {
        return this.lineHeightIndex;
    }

    public int getColorIndex() {
        return this.colorIndex;
    }

    public int getNumberOfProperties() {
        return this.valueManagers.length;
    }

    public int getPropertyIndex(String name) {
        return this.indexes.get(name);
    }

    public int getShorthandIndex(String name) {
        return this.shorthandIndexes.get(name);
    }

    public String getPropertyName(int idx) {
        return this.valueManagers[idx].getPropertyName();
    }

    public void setCSSEngineUserAgent(CSSEngineUserAgent userAgent) {
        this.userAgent = userAgent;
    }

    public CSSEngineUserAgent getCSSEngineUserAgent() {
        return this.userAgent;
    }

    public void setUserAgentStyleSheet(StyleSheet ss) {
        this.userAgentStyleSheet = ss;
    }

    public void setUserStyleSheet(StyleSheet ss) {
        this.userStyleSheet = ss;
    }

    public ValueManager[] getValueManagers() {
        return this.valueManagers;
    }

    public ShorthandManager[] getShorthandManagers() {
        return this.shorthandManagers;
    }

    public List getFontFaces() {
        return this.fontFaces;
    }

    public void setMedia(String str) {
        try {
            this.media = this.parser.parseMedia(str);
        }
        catch (Exception e) {
            String m = e.getMessage();
            if (m == null) {
                m = "";
            }
            String s = Messages.formatMessage("media.error", new Object[]{str, m});
            throw new DOMException(12, s);
        }
    }

    public void setAlternateStyleSheet(String str) {
        this.alternateStyleSheet = str;
    }

    public void importCascadedStyleMaps(Element src, CSSEngine srceng, Element dest) {
        if (src instanceof CSSStylableElement) {
            CSSStylableElement csrc = (CSSStylableElement)src;
            CSSStylableElement cdest = (CSSStylableElement)dest;
            StyleMap sm = srceng.getCascadedStyleMap(csrc, null);
            sm.setFixedCascadedStyle(true);
            cdest.setComputedStyleMap(null, sm);
            if (this.pseudoElementNames != null) {
                int len = this.pseudoElementNames.length;
                for (String pe : this.pseudoElementNames) {
                    sm = srceng.getCascadedStyleMap(csrc, pe);
                    cdest.setComputedStyleMap(pe, sm);
                }
            }
        }
        Node dn = CSSEngine.getCSSFirstChild(dest);
        Node sn = CSSEngine.getCSSFirstChild(src);
        while (dn != null) {
            if (sn.getNodeType() == 1) {
                this.importCascadedStyleMaps((Element)sn, srceng, (Element)dn);
            }
            dn = CSSEngine.getCSSNextSibling(dn);
            sn = CSSEngine.getCSSNextSibling(sn);
        }
    }

    public ParsedURL getCSSBaseURI() {
        if (this.cssBaseURI == null) {
            this.cssBaseURI = this.element.getCSSBase();
        }
        return this.cssBaseURI;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public StyleMap getCascadedStyleMap(CSSStylableElement elt, String pseudo) {
        ArrayList rules;
        int props = this.getNumberOfProperties();
        final StyleMap result = new StyleMap(props);
        if (this.userAgentStyleSheet != null) {
            rules = new ArrayList();
            this.addMatchingRules(rules, this.userAgentStyleSheet, elt, pseudo);
            this.addRules(elt, pseudo, result, rules, (short)0);
        }
        if (this.userStyleSheet != null) {
            rules = new ArrayList();
            this.addMatchingRules(rules, this.userStyleSheet, elt, pseudo);
            this.addRules(elt, pseudo, result, rules, (short)8192);
        }
        this.element = elt;
        try {
            StyleDeclaration over;
            StyleDeclarationProvider p;
            String style;
            CSSEngine eng;
            List snodes;
            int slen;
            if (this.nonCSSPresentationalHints != null) {
                ShorthandManager.PropertyHandler ph = new ShorthandManager.PropertyHandler(){

                    @Override
                    public void property(String pname, LexicalUnit lu, boolean important) {
                        int idx = CSSEngine.this.getPropertyIndex(pname);
                        if (idx != -1) {
                            ValueManager vm = CSSEngine.this.valueManagers[idx];
                            Value v = vm.createValue(lu, CSSEngine.this);
                            CSSEngine.this.putAuthorProperty(result, idx, v, important, (short)16384);
                            return;
                        }
                        idx = CSSEngine.this.getShorthandIndex(pname);
                        if (idx == -1) {
                            return;
                        }
                        CSSEngine.this.shorthandManagers[idx].setValues(CSSEngine.this, this, lu, important);
                    }
                };
                NamedNodeMap attrs = elt.getAttributes();
                int len = attrs.getLength();
                for (int i = 0; i < len; ++i) {
                    Node attr = attrs.item(i);
                    String an = attr.getNodeName();
                    if (!this.nonCSSPresentationalHints.contains(an)) continue;
                    try {
                        LexicalUnit lu = this.parser.parsePropertyValue(attr.getNodeValue());
                        ph.property(an, lu, false);
                        continue;
                    }
                    catch (Exception e) {
                        String m = e.getMessage();
                        if (m == null) {
                            m = "";
                        }
                        String u = this.documentURI == null ? "<unknown>" : this.documentURI.toString();
                        String s = Messages.formatMessage("property.syntax.error.at", new Object[]{u, an, attr.getNodeValue(), m});
                        DOMException de = new DOMException(12, s);
                        if (this.userAgent == null) {
                            throw de;
                        }
                        this.userAgent.displayError(de);
                    }
                }
            }
            if ((slen = (snodes = (eng = this.cssContext.getCSSEngineForElement(elt)).getStyleSheetNodes()).size()) > 0) {
                ArrayList rules2 = new ArrayList();
                for (Object snode : snodes) {
                    CSSStyleSheetNode ssn = (CSSStyleSheetNode)snode;
                    StyleSheet ss = ssn.getCSSStyleSheet();
                    if (ss == null || ss.isAlternate() && ss.getTitle() != null && !ss.getTitle().equals(this.alternateStyleSheet) || !this.mediaMatch(ss.getMedia())) continue;
                    this.addMatchingRules(rules2, ss, elt, pseudo);
                }
                this.addRules(elt, pseudo, result, rules2, (short)24576);
            }
            if (this.styleLocalName != null && (style = elt.getAttributeNS(this.styleNamespaceURI, this.styleLocalName)).length() > 0) {
                try {
                    this.parser.setSelectorFactory(CSSSelectorFactory.INSTANCE);
                    this.parser.setConditionFactory(this.cssConditionFactory);
                    this.styleDeclarationDocumentHandler.styleMap = result;
                    this.parser.setDocumentHandler(this.styleDeclarationDocumentHandler);
                    this.parser.parseStyleDeclaration(style);
                    this.styleDeclarationDocumentHandler.styleMap = null;
                }
                catch (Exception e) {
                    String m = e.getMessage();
                    if (m == null) {
                        m = e.getClass().getName();
                    }
                    String u = this.documentURI == null ? "<unknown>" : this.documentURI.toString();
                    String s = Messages.formatMessage("style.syntax.error.at", new Object[]{u, this.styleLocalName, style, m});
                    DOMException de = new DOMException(12, s);
                    if (this.userAgent == null) {
                        throw de;
                    }
                    this.userAgent.displayError(de);
                }
            }
            if ((p = elt.getOverrideStyleDeclarationProvider()) != null && (over = p.getStyleDeclaration()) != null) {
                int ol = over.size();
                for (int i = 0; i < ol; ++i) {
                    int idx = over.getIndex(i);
                    Value value = over.getValue(i);
                    boolean important = over.getPriority(i);
                    if (result.isImportant(idx) && !important) continue;
                    result.putValue(idx, value);
                    result.putImportant(idx, important);
                    result.putOrigin(idx, (short)-24576);
                }
            }
        }
        finally {
            this.element = null;
            this.cssBaseURI = null;
        }
        return result;
    }

    public Value getComputedStyle(CSSStylableElement elt, String pseudo, int propidx) {
        StyleMap sm = elt.getComputedStyleMap(pseudo);
        if (sm == null) {
            sm = this.getCascadedStyleMap(elt, pseudo);
            elt.setComputedStyleMap(pseudo, sm);
        }
        Value value = sm.getValue(propidx);
        if (sm.isComputed(propidx)) {
            return value;
        }
        Value result = value;
        ValueManager vm = this.valueManagers[propidx];
        CSSStylableElement p = CSSEngine.getParentCSSStylableElement(elt);
        if (value == null) {
            if (p == null || !vm.isInheritedProperty()) {
                result = vm.getDefaultValue();
            }
        } else if (p != null && value == InheritValue.INSTANCE) {
            result = null;
        }
        if (result == null) {
            result = this.getComputedStyle(p, null, propidx);
            sm.putParentRelative(propidx, true);
            sm.putInherited(propidx, true);
        } else {
            result = vm.computeValue(elt, pseudo, this, propidx, sm, result);
        }
        if (value == null) {
            sm.putValue(propidx, result);
            sm.putNullCascaded(propidx, true);
        } else if (result != value) {
            ComputedValue cv = new ComputedValue(value);
            cv.setComputedValue(result);
            sm.putValue(propidx, cv);
            result = cv;
        }
        sm.putComputed(propidx, true);
        return result;
    }

    public List getStyleSheetNodes() {
        if (this.styleSheetNodes == null) {
            this.styleSheetNodes = new ArrayList();
            this.selectorAttributes = new HashSet();
            this.findStyleSheetNodes(this.document);
            int len = this.styleSheetNodes.size();
            for (Object styleSheetNode : this.styleSheetNodes) {
                CSSStyleSheetNode ssn = (CSSStyleSheetNode)styleSheetNode;
                StyleSheet ss = ssn.getCSSStyleSheet();
                if (ss == null) continue;
                this.findSelectorAttributes(this.selectorAttributes, ss);
            }
        }
        return this.styleSheetNodes;
    }

    protected void findStyleSheetNodes(Node n) {
        if (n instanceof CSSStyleSheetNode) {
            this.styleSheetNodes.add(n);
        }
        Node nd = CSSEngine.getCSSFirstChild(n);
        while (nd != null) {
            this.findStyleSheetNodes(nd);
            nd = CSSEngine.getCSSNextSibling(nd);
        }
    }

    protected void findSelectorAttributes(Set attrs, StyleSheet ss) {
        int len = ss.getSize();
        block4: for (int i = 0; i < len; ++i) {
            Rule r = ss.getRule(i);
            switch (r.getType()) {
                case 0: {
                    StyleRule style = (StyleRule)r;
                    SelectorList sl = style.getSelectorList();
                    int slen = sl.getLength();
                    for (int j = 0; j < slen; ++j) {
                        ExtendedSelector s = (ExtendedSelector)sl.item(j);
                        s.fillAttributeSet(attrs);
                    }
                    continue block4;
                }
                case 1: 
                case 2: {
                    MediaRule mr = (MediaRule)r;
                    if (!this.mediaMatch(mr.getMediaList())) continue block4;
                    this.findSelectorAttributes(attrs, mr);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setMainProperties(CSSStylableElement elt, final MainPropertyReceiver dst, String pname, String value, boolean important) {
        try {
            this.element = elt;
            LexicalUnit lu = this.parser.parsePropertyValue(value);
            ShorthandManager.PropertyHandler ph = new ShorthandManager.PropertyHandler(){

                @Override
                public void property(String pname, LexicalUnit lu, boolean important) {
                    int idx = CSSEngine.this.getPropertyIndex(pname);
                    if (idx != -1) {
                        ValueManager vm = CSSEngine.this.valueManagers[idx];
                        Value v = vm.createValue(lu, CSSEngine.this);
                        dst.setMainProperty(pname, v, important);
                        return;
                    }
                    idx = CSSEngine.this.getShorthandIndex(pname);
                    if (idx == -1) {
                        return;
                    }
                    CSSEngine.this.shorthandManagers[idx].setValues(CSSEngine.this, this, lu, important);
                }
            };
            ph.property(pname, lu, important);
        }
        catch (Exception e) {
            String m = e.getMessage();
            if (m == null) {
                m = "";
            }
            String u = this.documentURI == null ? "<unknown>" : this.documentURI.toString();
            String s = Messages.formatMessage("property.syntax.error.at", new Object[]{u, pname, value, m});
            DOMException de = new DOMException(12, s);
            if (this.userAgent == null) {
                throw de;
            }
            this.userAgent.displayError(de);
        }
        finally {
            this.element = null;
            this.cssBaseURI = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Value parsePropertyValue(CSSStylableElement elt, String prop, String value) {
        int idx = this.getPropertyIndex(prop);
        if (idx == -1) {
            return null;
        }
        ValueManager vm = this.valueManagers[idx];
        try {
            this.element = elt;
            LexicalUnit lu = this.parser.parsePropertyValue(value);
            Value value2 = vm.createValue(lu, this);
            return value2;
        }
        catch (Exception e) {
            String m = e.getMessage();
            if (m == null) {
                m = "";
            }
            String u = this.documentURI == null ? "<unknown>" : this.documentURI.toString();
            String s = Messages.formatMessage("property.syntax.error.at", new Object[]{u, prop, value, m});
            DOMException de = new DOMException(12, s);
            if (this.userAgent == null) {
                throw de;
            }
            this.userAgent.displayError(de);
        }
        finally {
            this.element = null;
            this.cssBaseURI = null;
        }
        return vm.getDefaultValue();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public StyleDeclaration parseStyleDeclaration(CSSStylableElement elt, String value) {
        this.styleDeclarationBuilder.styleDeclaration = new StyleDeclaration();
        try {
            this.element = elt;
            this.parser.setSelectorFactory(CSSSelectorFactory.INSTANCE);
            this.parser.setConditionFactory(this.cssConditionFactory);
            this.parser.setDocumentHandler(this.styleDeclarationBuilder);
            this.parser.parseStyleDeclaration(value);
        }
        catch (Exception e) {
            String m = e.getMessage();
            if (m == null) {
                m = "";
            }
            String u = this.documentURI == null ? "<unknown>" : this.documentURI.toString();
            String s = Messages.formatMessage("syntax.error.at", new Object[]{u, m});
            DOMException de = new DOMException(12, s);
            if (this.userAgent == null) {
                throw de;
            }
            this.userAgent.displayError(de);
        }
        finally {
            this.element = null;
            this.cssBaseURI = null;
        }
        return this.styleDeclarationBuilder.styleDeclaration;
    }

    public StyleSheet parseStyleSheet(ParsedURL uri, String media) throws DOMException {
        StyleSheet ss = new StyleSheet();
        try {
            ss.setMedia(this.parser.parseMedia(media));
        }
        catch (Exception e) {
            String m = e.getMessage();
            if (m == null) {
                m = "";
            }
            String u = this.documentURI == null ? "<unknown>" : this.documentURI.toString();
            String s = Messages.formatMessage("syntax.error.at", new Object[]{u, m});
            DOMException de = new DOMException(12, s);
            if (this.userAgent == null) {
                throw de;
            }
            this.userAgent.displayError(de);
            return ss;
        }
        this.parseStyleSheet(ss, uri);
        return ss;
    }

    public StyleSheet parseStyleSheet(InputSource is, ParsedURL uri, String media) throws DOMException {
        StyleSheet ss = new StyleSheet();
        try {
            ss.setMedia(this.parser.parseMedia(media));
            this.parseStyleSheet(ss, is, uri);
        }
        catch (Exception e) {
            String m = e.getMessage();
            if (m == null) {
                m = "";
            }
            String u = this.documentURI == null ? "<unknown>" : this.documentURI.toString();
            String s = Messages.formatMessage("syntax.error.at", new Object[]{u, m});
            DOMException de = new DOMException(12, s);
            if (this.userAgent == null) {
                throw de;
            }
            this.userAgent.displayError(de);
        }
        return ss;
    }

    public void parseStyleSheet(StyleSheet ss, ParsedURL uri) throws DOMException {
        if (uri == null) {
            String s = Messages.formatMessage("syntax.error.at", new Object[]{"Null Document reference", ""});
            DOMException de = new DOMException(12, s);
            if (this.userAgent == null) {
                throw de;
            }
            this.userAgent.displayError(de);
            return;
        }
        try {
            this.cssContext.checkLoadExternalResource(uri, this.documentURI);
            this.parseStyleSheet(ss, new InputSource(uri.toString()), uri);
        }
        catch (SecurityException e) {
            throw e;
        }
        catch (Exception e) {
            String m = e.getMessage();
            if (m == null) {
                m = e.getClass().getName();
            }
            String s = Messages.formatMessage("syntax.error.at", new Object[]{uri.toString(), m});
            DOMException de = new DOMException(12, s);
            if (this.userAgent == null) {
                throw de;
            }
            this.userAgent.displayError(de);
        }
    }

    public StyleSheet parseStyleSheet(String rules, ParsedURL uri, String media) throws DOMException {
        StyleSheet ss = new StyleSheet();
        try {
            ss.setMedia(this.parser.parseMedia(media));
        }
        catch (Exception e) {
            String m = e.getMessage();
            if (m == null) {
                m = "";
            }
            String u = this.documentURI == null ? "<unknown>" : this.documentURI.toString();
            String s = Messages.formatMessage("syntax.error.at", new Object[]{u, m});
            DOMException de = new DOMException(12, s);
            if (this.userAgent == null) {
                throw de;
            }
            this.userAgent.displayError(de);
            return ss;
        }
        this.parseStyleSheet(ss, rules, uri);
        return ss;
    }

    public void parseStyleSheet(StyleSheet ss, String rules, ParsedURL uri) throws DOMException {
        try {
            this.parseStyleSheet(ss, new InputSource((Reader)new StringReader(rules)), uri);
        }
        catch (Exception e) {
            String m = e.getMessage();
            if (m == null) {
                m = "";
            }
            String s = Messages.formatMessage("stylesheet.syntax.error", new Object[]{uri.toString(), rules, m});
            DOMException de = new DOMException(12, s);
            if (this.userAgent == null) {
                throw de;
            }
            this.userAgent.displayError(de);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void parseStyleSheet(StyleSheet ss, InputSource is, ParsedURL uri) throws IOException {
        this.parser.setSelectorFactory(CSSSelectorFactory.INSTANCE);
        this.parser.setConditionFactory(this.cssConditionFactory);
        try {
            this.cssBaseURI = uri;
            this.styleSheetDocumentHandler.styleSheet = ss;
            this.parser.setDocumentHandler(this.styleSheetDocumentHandler);
            this.parser.parseStyleSheet(is);
            int len = ss.getSize();
            for (int i = 0; i < len; ++i) {
                Rule r = ss.getRule(i);
                if (r.getType() != 2) {
                    break;
                }
                ImportRule ir = (ImportRule)r;
                this.parseStyleSheet(ir, ir.getURI());
            }
        }
        finally {
            this.cssBaseURI = null;
        }
    }

    protected void putAuthorProperty(StyleMap dest, int idx, Value sval, boolean imp, short origin) {
        boolean cond;
        Value dval = dest.getValue(idx);
        short dorg = dest.getOrigin(idx);
        boolean dimp = dest.isImportant(idx);
        boolean bl = cond = dval == null;
        if (!cond) {
            switch (dorg) {
                case 8192: {
                    cond = !dimp;
                    break;
                }
                case 24576: {
                    cond = !dimp || imp;
                    break;
                }
                case -24576: {
                    cond = false;
                    break;
                }
                default: {
                    cond = true;
                }
            }
        }
        if (cond) {
            dest.putValue(idx, sval);
            dest.putImportant(idx, imp);
            dest.putOrigin(idx, origin);
        }
    }

    protected void addMatchingRules(List rules, StyleSheet ss, Element elt, String pseudo) {
        int len = ss.getSize();
        block4: for (int i = 0; i < len; ++i) {
            Rule r = ss.getRule(i);
            switch (r.getType()) {
                case 0: {
                    StyleRule style = (StyleRule)r;
                    SelectorList sl = style.getSelectorList();
                    int slen = sl.getLength();
                    for (int j = 0; j < slen; ++j) {
                        ExtendedSelector s = (ExtendedSelector)sl.item(j);
                        if (!s.match(elt, pseudo)) continue;
                        rules.add(style);
                    }
                    continue block4;
                }
                case 1: 
                case 2: {
                    MediaRule mr = (MediaRule)r;
                    if (!this.mediaMatch(mr.getMediaList())) continue block4;
                    this.addMatchingRules(rules, mr, elt, pseudo);
                }
            }
        }
    }

    protected void addRules(Element elt, String pseudo, StyleMap sm, ArrayList rules, short origin) {
        this.sortRules(rules, elt, pseudo);
        int rlen = rules.size();
        if (origin == 24576) {
            for (Object rule : rules) {
                StyleRule sr = (StyleRule)rule;
                StyleDeclaration sd = sr.getStyleDeclaration();
                int len = sd.size();
                for (int i = 0; i < len; ++i) {
                    this.putAuthorProperty(sm, sd.getIndex(i), sd.getValue(i), sd.getPriority(i), origin);
                }
            }
        } else {
            for (Object rule : rules) {
                StyleRule sr = (StyleRule)rule;
                StyleDeclaration sd = sr.getStyleDeclaration();
                int len = sd.size();
                for (int i = 0; i < len; ++i) {
                    int idx = sd.getIndex(i);
                    sm.putValue(idx, sd.getValue(i));
                    sm.putImportant(idx, sd.getPriority(i));
                    sm.putOrigin(idx, origin);
                }
            }
        }
    }

    protected void sortRules(ArrayList rules, Element elt, String pseudo) {
        int i;
        int len = rules.size();
        int[] specificities = new int[len];
        for (i = 0; i < len; ++i) {
            StyleRule r = (StyleRule)rules.get(i);
            SelectorList sl = r.getSelectorList();
            int spec = 0;
            int slen = sl.getLength();
            for (int k = 0; k < slen; ++k) {
                int sp;
                ExtendedSelector s = (ExtendedSelector)sl.item(k);
                if (!s.match(elt, pseudo) || (sp = s.getSpecificity()) <= spec) continue;
                spec = sp;
            }
            specificities[i] = spec;
        }
        for (i = 1; i < len; ++i) {
            int j;
            Object rule = rules.get(i);
            int spec = specificities[i];
            for (j = i - 1; j >= 0 && specificities[j] > spec; --j) {
                rules.set(j + 1, rules.get(j));
                specificities[j + 1] = specificities[j];
            }
            rules.set(j + 1, rule);
            specificities[j + 1] = spec;
        }
    }

    protected boolean mediaMatch(SACMediaList ml) {
        if (this.media == null || ml == null || this.media.getLength() == 0 || ml.getLength() == 0) {
            return true;
        }
        for (int i = 0; i < ml.getLength(); ++i) {
            if (ml.item(i).equalsIgnoreCase("all")) {
                return true;
            }
            for (int j = 0; j < this.media.getLength(); ++j) {
                if (!this.media.item(j).equalsIgnoreCase("all") && !ml.item(i).equalsIgnoreCase(this.media.item(j))) continue;
                return true;
            }
        }
        return false;
    }

    public void addCSSEngineListener(CSSEngineListener l) {
        this.listeners.add(l);
    }

    public void removeCSSEngineListener(CSSEngineListener l) {
        this.listeners.remove(l);
    }

    protected void firePropertiesChangedEvent(Element target, int[] props) {
        CSSEngineListener[] ll = this.listeners.toArray(LISTENER_ARRAY);
        int len = ll.length;
        if (len > 0) {
            CSSEngineEvent evt = new CSSEngineEvent(this, target, props);
            for (CSSEngineListener aLl : ll) {
                aLl.propertiesChanged(evt);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void inlineStyleAttributeUpdated(CSSStylableElement elt, StyleMap style, short attrChange, String prevValue, String newValue) {
        boolean[] updated = this.styleDeclarationUpdateHandler.updatedProperties;
        for (int i = this.getNumberOfProperties() - 1; i >= 0; --i) {
            updated[i] = false;
        }
        switch (attrChange) {
            case 1: 
            case 2: {
                if (newValue.length() > 0) {
                    this.element = elt;
                    try {
                        this.parser.setSelectorFactory(CSSSelectorFactory.INSTANCE);
                        this.parser.setConditionFactory(this.cssConditionFactory);
                        this.styleDeclarationUpdateHandler.styleMap = style;
                        this.parser.setDocumentHandler(this.styleDeclarationUpdateHandler);
                        this.parser.parseStyleDeclaration(newValue);
                        this.styleDeclarationUpdateHandler.styleMap = null;
                    }
                    catch (Exception e) {
                        String m = e.getMessage();
                        if (m == null) {
                            m = "";
                        }
                        String u = this.documentURI == null ? "<unknown>" : this.documentURI.toString();
                        String s = Messages.formatMessage("style.syntax.error.at", new Object[]{u, this.styleLocalName, newValue, m});
                        DOMException de = new DOMException(12, s);
                        if (this.userAgent == null) {
                            throw de;
                        }
                        this.userAgent.displayError(de);
                    }
                    finally {
                        this.element = null;
                        this.cssBaseURI = null;
                    }
                }
            }
            case 3: {
                boolean removed = false;
                if (prevValue != null && prevValue.length() > 0) {
                    for (int i = this.getNumberOfProperties() - 1; i >= 0; --i) {
                        short origin;
                        if (!style.isComputed(i) || updated[i] || (origin = style.getOrigin(i)) < Short.MIN_VALUE) continue;
                        removed = true;
                        updated[i] = true;
                    }
                }
                if (removed) {
                    this.invalidateProperties(elt, null, updated, true);
                    break;
                }
                int count = 0;
                boolean fs = this.fontSizeIndex == -1 ? false : updated[this.fontSizeIndex];
                boolean lh = this.lineHeightIndex == -1 ? false : updated[this.lineHeightIndex];
                boolean cl = this.colorIndex == -1 ? false : updated[this.colorIndex];
                for (int i = this.getNumberOfProperties() - 1; i >= 0; --i) {
                    if (updated[i]) {
                        ++count;
                        continue;
                    }
                    if (!(fs && style.isFontSizeRelative(i) || lh && style.isLineHeightRelative(i)) && (!cl || !style.isColorRelative(i))) continue;
                    updated[i] = true;
                    CSSEngine.clearComputedValue(style, i);
                    ++count;
                }
                if (count <= 0) break;
                int[] props = new int[count];
                count = 0;
                for (int i = this.getNumberOfProperties() - 1; i >= 0; --i) {
                    if (!updated[i]) continue;
                    props[count++] = i;
                }
                this.invalidateProperties(elt, props, null, true);
                break;
            }
            default: {
                throw new IllegalStateException("Invalid attrChangeType");
            }
        }
    }

    private static void clearComputedValue(StyleMap style, int n) {
        if (style.isNullCascaded(n)) {
            style.putValue(n, null);
        } else {
            Value v = style.getValue(n);
            if (v instanceof ComputedValue) {
                ComputedValue cv = (ComputedValue)v;
                v = cv.getCascadedValue();
                style.putValue(n, v);
            }
        }
        style.putComputed(n, false);
    }

    protected void invalidateProperties(Node node, int[] properties, boolean[] updated, boolean recascade) {
        int i;
        if (!(node instanceof CSSStylableElement)) {
            return;
        }
        CSSStylableElement elt = (CSSStylableElement)node;
        StyleMap style = elt.getComputedStyleMap(null);
        if (style == null) {
            return;
        }
        boolean[] diffs = new boolean[this.getNumberOfProperties()];
        if (updated != null) {
            System.arraycopy(updated, 0, diffs, 0, updated.length);
        }
        if (properties != null) {
            for (int property : properties) {
                diffs[property] = true;
            }
        }
        int count = 0;
        if (!recascade) {
            for (boolean diff : diffs) {
                if (!diff) continue;
                ++count;
            }
        } else {
            StyleMap newStyle = this.getCascadedStyleMap(elt, null);
            elt.setComputedStyleMap(null, newStyle);
            for (i = 0; i < diffs.length; ++i) {
                if (diffs[i]) {
                    ++count;
                    continue;
                }
                Value nv = newStyle.getValue(i);
                Value ov = null;
                if (!style.isNullCascaded(i) && (ov = style.getValue(i)) instanceof ComputedValue) {
                    ov = ((ComputedValue)ov).getCascadedValue();
                }
                if (nv == ov) continue;
                if (nv != null && ov != null) {
                    if (nv.equals(ov)) continue;
                    String ovCssText = ov.getCssText();
                    String nvCssText = nv.getCssText();
                    if (nvCssText == ovCssText || nvCssText != null && nvCssText.equals(ovCssText)) continue;
                }
                ++count;
                diffs[i] = true;
            }
        }
        int[] props = null;
        if (count != 0) {
            props = new int[count];
            count = 0;
            for (i = 0; i < diffs.length; ++i) {
                if (!diffs[i]) continue;
                props[count++] = i;
            }
        }
        this.propagateChanges(elt, props, recascade);
    }

    protected void propagateChanges(Node node, int[] props, boolean recascade) {
        if (!(node instanceof CSSStylableElement)) {
            return;
        }
        CSSStylableElement elt = (CSSStylableElement)node;
        StyleMap style = elt.getComputedStyleMap(null);
        if (style != null) {
            int i;
            int i2;
            boolean[] updated = this.styleDeclarationUpdateHandler.updatedProperties;
            for (i2 = this.getNumberOfProperties() - 1; i2 >= 0; --i2) {
                updated[i2] = false;
            }
            if (props != null) {
                for (i2 = props.length - 1; i2 >= 0; --i2) {
                    int idx = props[i2];
                    updated[idx] = true;
                }
            }
            boolean fs = this.fontSizeIndex == -1 ? false : updated[this.fontSizeIndex];
            boolean lh = this.lineHeightIndex == -1 ? false : updated[this.lineHeightIndex];
            boolean cl = this.colorIndex == -1 ? false : updated[this.colorIndex];
            int count = 0;
            for (i = this.getNumberOfProperties() - 1; i >= 0; --i) {
                if (updated[i]) {
                    ++count;
                    continue;
                }
                if (!(fs && style.isFontSizeRelative(i) || lh && style.isLineHeightRelative(i)) && (!cl || !style.isColorRelative(i))) continue;
                updated[i] = true;
                CSSEngine.clearComputedValue(style, i);
                ++count;
            }
            if (count == 0) {
                props = null;
            } else {
                props = new int[count];
                count = 0;
                for (i = this.getNumberOfProperties() - 1; i >= 0; --i) {
                    if (!updated[i]) continue;
                    props[count++] = i;
                }
                this.firePropertiesChangedEvent(elt, props);
            }
        }
        int[] inherited = props;
        if (props != null) {
            int count = 0;
            for (int i = 0; i < props.length; ++i) {
                ValueManager vm = this.valueManagers[props[i]];
                if (vm.isInheritedProperty()) {
                    ++count;
                    continue;
                }
                props[i] = -1;
            }
            if (count == 0) {
                inherited = null;
            } else {
                inherited = new int[count];
                count = 0;
                for (int prop : props) {
                    if (prop == -1) continue;
                    inherited[count++] = prop;
                }
            }
        }
        Node n = CSSEngine.getCSSFirstChild(node);
        while (n != null) {
            if (n.getNodeType() == 1) {
                this.invalidateProperties(n, inherited, null, recascade);
            }
            n = CSSEngine.getCSSNextSibling(n);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void nonCSSPresentationalHintUpdated(CSSStylableElement elt, StyleMap style, String property, short attrChange, String newValue) {
        int idx = this.getPropertyIndex(property);
        if (style.isImportant(idx)) {
            return;
        }
        if (style.getOrigin(idx) >= 24576) {
            return;
        }
        switch (attrChange) {
            case 1: 
            case 2: {
                this.element = elt;
                try {
                    LexicalUnit lu = this.parser.parsePropertyValue(newValue);
                    ValueManager vm = this.valueManagers[idx];
                    Value v = vm.createValue(lu, this);
                    style.putMask(idx, (short)0);
                    style.putValue(idx, v);
                    style.putOrigin(idx, (short)16384);
                    break;
                }
                catch (Exception e) {
                    String m = e.getMessage();
                    if (m == null) {
                        m = "";
                    }
                    String u = this.documentURI == null ? "<unknown>" : this.documentURI.toString();
                    String s = Messages.formatMessage("property.syntax.error.at", new Object[]{u, property, newValue, m});
                    DOMException de = new DOMException(12, s);
                    if (this.userAgent == null) {
                        throw de;
                    }
                    this.userAgent.displayError(de);
                    break;
                }
                finally {
                    this.element = null;
                    this.cssBaseURI = null;
                }
            }
            case 3: {
                int[] invalid = new int[]{idx};
                this.invalidateProperties(elt, invalid, null, true);
                return;
            }
        }
        boolean[] updated = this.styleDeclarationUpdateHandler.updatedProperties;
        for (int i = this.getNumberOfProperties() - 1; i >= 0; --i) {
            updated[i] = false;
        }
        updated[idx] = true;
        boolean fs = idx == this.fontSizeIndex;
        boolean lh = idx == this.lineHeightIndex;
        boolean cl = idx == this.colorIndex;
        int count = 0;
        for (int i = this.getNumberOfProperties() - 1; i >= 0; --i) {
            if (updated[i]) {
                ++count;
                continue;
            }
            if (!(fs && style.isFontSizeRelative(i) || lh && style.isLineHeightRelative(i)) && (!cl || !style.isColorRelative(i))) continue;
            updated[i] = true;
            CSSEngine.clearComputedValue(style, i);
            ++count;
        }
        int[] props = new int[count];
        count = 0;
        for (int i = this.getNumberOfProperties() - 1; i >= 0; --i) {
            if (!updated[i]) continue;
            props[count++] = i;
        }
        this.invalidateProperties(elt, props, null, true);
    }

    protected boolean hasStyleSheetNode(Node n) {
        if (n instanceof CSSStyleSheetNode) {
            return true;
        }
        n = CSSEngine.getCSSFirstChild(n);
        while (n != null) {
            if (this.hasStyleSheetNode(n)) {
                return true;
            }
            n = CSSEngine.getCSSNextSibling(n);
        }
        return false;
    }

    protected void handleAttrModified(Element e, Attr attr, short attrChange, String prevValue, String newValue) {
        if (!(e instanceof CSSStylableElement)) {
            return;
        }
        if (newValue.equals(prevValue)) {
            return;
        }
        String attrNS = attr.getNamespaceURI();
        String name = attrNS == null ? attr.getNodeName() : attr.getLocalName();
        CSSStylableElement elt = (CSSStylableElement)e;
        StyleMap style = elt.getComputedStyleMap(null);
        if (style != null) {
            if ((attrNS == this.styleNamespaceURI || attrNS != null && attrNS.equals(this.styleNamespaceURI)) && name.equals(this.styleLocalName)) {
                this.inlineStyleAttributeUpdated(elt, style, attrChange, prevValue, newValue);
                return;
            }
            if (this.nonCSSPresentationalHints != null && (attrNS == this.nonCSSPresentationalHintsNamespaceURI || attrNS != null && attrNS.equals(this.nonCSSPresentationalHintsNamespaceURI)) && this.nonCSSPresentationalHints.contains(name)) {
                this.nonCSSPresentationalHintUpdated(elt, style, name, attrChange, newValue);
                return;
            }
        }
        if (this.selectorAttributes != null && this.selectorAttributes.contains(name)) {
            this.invalidateProperties(elt, null, null, true);
            Node n = CSSEngine.getCSSNextSibling(elt);
            while (n != null) {
                this.invalidateProperties(n, null, null, true);
                n = CSSEngine.getCSSNextSibling(n);
            }
        }
    }

    protected void handleNodeInserted(Node n) {
        if (this.hasStyleSheetNode(n)) {
            this.styleSheetNodes = null;
            this.invalidateProperties(this.document.getDocumentElement(), null, null, true);
        } else if (n instanceof CSSStylableElement) {
            n = CSSEngine.getCSSNextSibling(n);
            while (n != null) {
                this.invalidateProperties(n, null, null, true);
                n = CSSEngine.getCSSNextSibling(n);
            }
        }
    }

    protected void handleNodeRemoved(Node n) {
        if (this.hasStyleSheetNode(n)) {
            this.styleSheetRemoved = true;
        } else if (n instanceof CSSStylableElement) {
            this.removedStylableElementSibling = CSSEngine.getCSSNextSibling(n);
        }
        this.disposeStyleMaps(n);
    }

    protected void handleSubtreeModified(Node ignored) {
        if (this.styleSheetRemoved) {
            this.styleSheetRemoved = false;
            this.styleSheetNodes = null;
            this.invalidateProperties(this.document.getDocumentElement(), null, null, true);
        } else if (this.removedStylableElementSibling != null) {
            Node n = this.removedStylableElementSibling;
            while (n != null) {
                this.invalidateProperties(n, null, null, true);
                n = CSSEngine.getCSSNextSibling(n);
            }
            this.removedStylableElementSibling = null;
        }
    }

    protected void handleCharacterDataModified(Node n) {
        if (CSSEngine.getCSSParentNode(n) instanceof CSSStyleSheetNode) {
            this.styleSheetNodes = null;
            this.invalidateProperties(this.document.getDocumentElement(), null, null, true);
        }
    }

    protected class DOMAttrModifiedListener
    implements EventListener {
        protected DOMAttrModifiedListener() {
        }

        @Override
        public void handleEvent(Event evt) {
            MutationEvent mevt = (MutationEvent)evt;
            CSSEngine.this.handleAttrModified((Element)((Object)evt.getTarget()), (Attr)mevt.getRelatedNode(), mevt.getAttrChange(), mevt.getPrevValue(), mevt.getNewValue());
        }
    }

    protected class DOMCharacterDataModifiedListener
    implements EventListener {
        protected DOMCharacterDataModifiedListener() {
        }

        @Override
        public void handleEvent(Event evt) {
            CSSEngine.this.handleCharacterDataModified((Node)((Object)evt.getTarget()));
        }
    }

    protected class DOMSubtreeModifiedListener
    implements EventListener {
        protected DOMSubtreeModifiedListener() {
        }

        @Override
        public void handleEvent(Event evt) {
            CSSEngine.this.handleSubtreeModified((Node)((Object)evt.getTarget()));
        }
    }

    protected class DOMNodeRemovedListener
    implements EventListener {
        protected DOMNodeRemovedListener() {
        }

        @Override
        public void handleEvent(Event evt) {
            CSSEngine.this.handleNodeRemoved((Node)((Object)evt.getTarget()));
        }
    }

    protected class DOMNodeInsertedListener
    implements EventListener {
        protected DOMNodeInsertedListener() {
        }

        @Override
        public void handleEvent(Event evt) {
            CSSEngine.this.handleNodeInserted((Node)((Object)evt.getTarget()));
        }
    }

    protected class CSSNavigableDocumentHandler
    implements CSSNavigableDocumentListener,
    MainPropertyReceiver {
        protected boolean[] mainPropertiesChanged;
        protected StyleDeclaration declaration;

        protected CSSNavigableDocumentHandler() {
        }

        @Override
        public void nodeInserted(Node newNode) {
            CSSEngine.this.handleNodeInserted(newNode);
        }

        @Override
        public void nodeToBeRemoved(Node oldNode) {
            CSSEngine.this.handleNodeRemoved(oldNode);
        }

        @Override
        public void subtreeModified(Node rootOfModifications) {
            CSSEngine.this.handleSubtreeModified(rootOfModifications);
        }

        @Override
        public void characterDataModified(Node text) {
            CSSEngine.this.handleCharacterDataModified(text);
        }

        @Override
        public void attrModified(Element e, Attr attr, short attrChange, String prevValue, String newValue) {
            CSSEngine.this.handleAttrModified(e, attr, attrChange, prevValue, newValue);
        }

        @Override
        public void overrideStyleTextChanged(CSSStylableElement elt, String text) {
            int i;
            StyleDeclarationProvider p = elt.getOverrideStyleDeclarationProvider();
            StyleDeclaration declaration = p.getStyleDeclaration();
            int ds = declaration.size();
            boolean[] updated = new boolean[CSSEngine.this.getNumberOfProperties()];
            for (i = 0; i < ds; ++i) {
                updated[declaration.getIndex((int)i)] = true;
            }
            declaration = CSSEngine.this.parseStyleDeclaration(elt, text);
            p.setStyleDeclaration(declaration);
            ds = declaration.size();
            for (i = 0; i < ds; ++i) {
                updated[declaration.getIndex((int)i)] = true;
            }
            CSSEngine.this.invalidateProperties(elt, null, updated, true);
        }

        @Override
        public void overrideStylePropertyRemoved(CSSStylableElement elt, String name) {
            StyleDeclarationProvider p = elt.getOverrideStyleDeclarationProvider();
            StyleDeclaration declaration = p.getStyleDeclaration();
            int idx = CSSEngine.this.getPropertyIndex(name);
            int ds = declaration.size();
            for (int i = 0; i < ds; ++i) {
                if (idx != declaration.getIndex(i)) continue;
                declaration.remove(i);
                StyleMap style = elt.getComputedStyleMap(null);
                if (style == null || style.getOrigin(idx) != -24576) break;
                CSSEngine.this.invalidateProperties(elt, new int[]{idx}, null, true);
                break;
            }
        }

        @Override
        public void overrideStylePropertyChanged(CSSStylableElement elt, String name, String val, String prio) {
            boolean important = prio != null && prio.length() != 0;
            StyleDeclarationProvider p = elt.getOverrideStyleDeclarationProvider();
            this.declaration = p.getStyleDeclaration();
            CSSEngine.this.setMainProperties(elt, this, name, val, important);
            this.declaration = null;
            CSSEngine.this.invalidateProperties(elt, null, this.mainPropertiesChanged, true);
        }

        @Override
        public void setMainProperty(String name, Value v, boolean important) {
            int i;
            int idx = CSSEngine.this.getPropertyIndex(name);
            if (idx == -1) {
                return;
            }
            for (i = 0; i < this.declaration.size() && idx != this.declaration.getIndex(i); ++i) {
            }
            if (i < this.declaration.size()) {
                this.declaration.put(i, v, idx, important);
            } else {
                this.declaration.append(v, idx, important);
            }
        }
    }

    protected class StyleDeclarationUpdateHandler
    extends DocumentAdapter
    implements ShorthandManager.PropertyHandler {
        public StyleMap styleMap;
        public boolean[] updatedProperties;

        protected StyleDeclarationUpdateHandler() {
            this.updatedProperties = new boolean[CSSEngine.this.getNumberOfProperties()];
        }

        @Override
        public void property(String name, LexicalUnit value, boolean important) throws CSSException {
            int i = CSSEngine.this.getPropertyIndex(name);
            if (i == -1) {
                i = CSSEngine.this.getShorthandIndex(name);
                if (i == -1) {
                    return;
                }
                CSSEngine.this.shorthandManagers[i].setValues(CSSEngine.this, this, value, important);
            } else {
                if (this.styleMap.isImportant(i)) {
                    return;
                }
                this.updatedProperties[i] = true;
                Value v = CSSEngine.this.valueManagers[i].createValue(value, CSSEngine.this);
                this.styleMap.putMask(i, (short)0);
                this.styleMap.putValue(i, v);
                this.styleMap.putOrigin(i, (short)Short.MIN_VALUE);
            }
        }
    }

    protected static class DocumentAdapter
    implements DocumentHandler {
        protected DocumentAdapter() {
        }

        public void startDocument(InputSource source) {
            this.throwUnsupportedEx();
        }

        public void endDocument(InputSource source) {
            this.throwUnsupportedEx();
        }

        public void comment(String text) {
        }

        public void ignorableAtRule(String atRule) {
            this.throwUnsupportedEx();
        }

        public void namespaceDeclaration(String prefix, String uri) {
            this.throwUnsupportedEx();
        }

        public void importStyle(String uri, SACMediaList media, String defaultNamespaceURI) {
            this.throwUnsupportedEx();
        }

        public void startMedia(SACMediaList media) {
            this.throwUnsupportedEx();
        }

        public void endMedia(SACMediaList media) {
            this.throwUnsupportedEx();
        }

        public void startPage(String name, String pseudo_page) {
            this.throwUnsupportedEx();
        }

        public void endPage(String name, String pseudo_page) {
            this.throwUnsupportedEx();
        }

        public void startFontFace() {
            this.throwUnsupportedEx();
        }

        public void endFontFace() {
            this.throwUnsupportedEx();
        }

        public void startSelector(SelectorList selectors) {
            this.throwUnsupportedEx();
        }

        public void endSelector(SelectorList selectors) {
            this.throwUnsupportedEx();
        }

        public void property(String name, LexicalUnit value, boolean important) {
            this.throwUnsupportedEx();
        }

        private void throwUnsupportedEx() {
            throw new UnsupportedOperationException("you try to use an empty method in Adapter-class");
        }
    }

    protected class StyleSheetDocumentHandler
    extends DocumentAdapter
    implements ShorthandManager.PropertyHandler {
        public StyleSheet styleSheet;
        protected StyleRule styleRule;
        protected StyleDeclaration styleDeclaration;

        protected StyleSheetDocumentHandler() {
        }

        @Override
        public void startDocument(InputSource source) throws CSSException {
        }

        @Override
        public void endDocument(InputSource source) throws CSSException {
        }

        @Override
        public void ignorableAtRule(String atRule) throws CSSException {
        }

        @Override
        public void importStyle(String uri, SACMediaList media, String defaultNamespaceURI) throws CSSException {
            ImportRule ir = new ImportRule();
            ir.setMediaList(media);
            ir.setParent(this.styleSheet);
            ParsedURL base = CSSEngine.this.getCSSBaseURI();
            ParsedURL url = base == null ? new ParsedURL(uri) : new ParsedURL(base, uri);
            ir.setURI(url);
            this.styleSheet.append(ir);
        }

        @Override
        public void startMedia(SACMediaList media) throws CSSException {
            MediaRule mr = new MediaRule();
            mr.setMediaList(media);
            mr.setParent(this.styleSheet);
            this.styleSheet.append(mr);
            this.styleSheet = mr;
        }

        @Override
        public void endMedia(SACMediaList media) throws CSSException {
            this.styleSheet = this.styleSheet.getParent();
        }

        @Override
        public void startPage(String name, String pseudo_page) throws CSSException {
        }

        @Override
        public void endPage(String name, String pseudo_page) throws CSSException {
        }

        @Override
        public void startFontFace() throws CSSException {
            this.styleDeclaration = new StyleDeclaration();
        }

        @Override
        public void endFontFace() throws CSSException {
            StyleMap sm = new StyleMap(CSSEngine.this.getNumberOfProperties());
            int len = this.styleDeclaration.size();
            for (int i = 0; i < len; ++i) {
                int idx = this.styleDeclaration.getIndex(i);
                sm.putValue(idx, this.styleDeclaration.getValue(i));
                sm.putImportant(idx, this.styleDeclaration.getPriority(i));
                sm.putOrigin(idx, (short)24576);
            }
            this.styleDeclaration = null;
            int pidx = CSSEngine.this.getPropertyIndex("font-family");
            Value fontFamily = sm.getValue(pidx);
            if (fontFamily == null) {
                return;
            }
            ParsedURL base = CSSEngine.this.getCSSBaseURI();
            CSSEngine.this.fontFaces.add(new FontFaceRule(sm, base));
        }

        @Override
        public void startSelector(SelectorList selectors) throws CSSException {
            this.styleRule = new StyleRule();
            this.styleRule.setSelectorList(selectors);
            this.styleDeclaration = new StyleDeclaration();
            this.styleRule.setStyleDeclaration(this.styleDeclaration);
            this.styleSheet.append(this.styleRule);
        }

        @Override
        public void endSelector(SelectorList selectors) throws CSSException {
            this.styleRule = null;
            this.styleDeclaration = null;
        }

        @Override
        public void property(String name, LexicalUnit value, boolean important) throws CSSException {
            int i = CSSEngine.this.getPropertyIndex(name);
            if (i == -1) {
                i = CSSEngine.this.getShorthandIndex(name);
                if (i == -1) {
                    return;
                }
                CSSEngine.this.shorthandManagers[i].setValues(CSSEngine.this, this, value, important);
            } else {
                Value v = CSSEngine.this.valueManagers[i].createValue(value, CSSEngine.this);
                this.styleDeclaration.append(v, i, important);
            }
        }
    }

    protected class StyleDeclarationBuilder
    extends DocumentAdapter
    implements ShorthandManager.PropertyHandler {
        public StyleDeclaration styleDeclaration;

        protected StyleDeclarationBuilder() {
        }

        @Override
        public void property(String name, LexicalUnit value, boolean important) throws CSSException {
            int i = CSSEngine.this.getPropertyIndex(name);
            if (i == -1) {
                i = CSSEngine.this.getShorthandIndex(name);
                if (i == -1) {
                    return;
                }
                CSSEngine.this.shorthandManagers[i].setValues(CSSEngine.this, this, value, important);
            } else {
                Value v = CSSEngine.this.valueManagers[i].createValue(value, CSSEngine.this);
                this.styleDeclaration.append(v, i, important);
            }
        }
    }

    protected class StyleDeclarationDocumentHandler
    extends DocumentAdapter
    implements ShorthandManager.PropertyHandler {
        public StyleMap styleMap;

        protected StyleDeclarationDocumentHandler() {
        }

        @Override
        public void property(String name, LexicalUnit value, boolean important) throws CSSException {
            int i = CSSEngine.this.getPropertyIndex(name);
            if (i == -1) {
                i = CSSEngine.this.getShorthandIndex(name);
                if (i == -1) {
                    return;
                }
                CSSEngine.this.shorthandManagers[i].setValues(CSSEngine.this, this, value, important);
            } else {
                Value v = CSSEngine.this.valueManagers[i].createValue(value, CSSEngine.this);
                CSSEngine.this.putAuthorProperty(this.styleMap, i, v, important, (short)Short.MIN_VALUE);
            }
        }
    }

    public static interface MainPropertyReceiver {
        public void setMainProperty(String var1, Value var2, boolean var3);
    }
}

