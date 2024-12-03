/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.context;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.xhtmlrenderer.context.StandardAttributeResolver;
import org.xhtmlrenderer.context.StylesheetFactoryImpl;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.extend.lib.DOMTreeResolver;
import org.xhtmlrenderer.css.newmatch.CascadedStyle;
import org.xhtmlrenderer.css.newmatch.Matcher;
import org.xhtmlrenderer.css.newmatch.PageInfo;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;
import org.xhtmlrenderer.css.sheet.Stylesheet;
import org.xhtmlrenderer.css.sheet.StylesheetInfo;
import org.xhtmlrenderer.extend.NamespaceHandler;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.extend.UserInterface;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.util.XRLog;

public class StyleReference {
    private SharedContext _context;
    private NamespaceHandler _nsh;
    private Document _doc;
    private StylesheetFactoryImpl _stylesheetFactory;
    private Matcher _matcher;
    private UserAgentCallback _uac;

    public StyleReference(UserAgentCallback userAgent) {
        this._uac = userAgent;
        this._stylesheetFactory = new StylesheetFactoryImpl(userAgent);
    }

    public void setDocumentContext(SharedContext context, NamespaceHandler nsh, Document doc, UserInterface ui) {
        this._context = context;
        this._nsh = nsh;
        this._doc = doc;
        StandardAttributeResolver attRes = new StandardAttributeResolver(this._nsh, this._uac, ui);
        List infos = this.getStylesheets();
        XRLog.match("media = " + this._context.getMedia());
        this._matcher = new Matcher(new DOMTreeResolver(), attRes, this._stylesheetFactory, this.readAndParseAll(infos, this._context.getMedia()), this._context.getMedia());
    }

    private List readAndParseAll(List infos, String medium) {
        ArrayList<Stylesheet> result = new ArrayList<Stylesheet>(infos.size() + 15);
        for (StylesheetInfo info : infos) {
            if (!info.appliesToMedia(medium)) continue;
            Stylesheet sheet = info.getStylesheet();
            if (sheet == null) {
                sheet = this._stylesheetFactory.getStylesheet(info);
            }
            if (sheet != null) {
                if (sheet.getImportRules().size() > 0) {
                    result.addAll(this.readAndParseAll(sheet.getImportRules(), medium));
                }
                result.add(sheet);
                continue;
            }
            XRLog.load(Level.WARNING, "Unable to load CSS from " + info.getUri());
        }
        return result;
    }

    public boolean isHoverStyled(Element e) {
        return this._matcher.isHoverStyled(e);
    }

    public Map getCascadedPropertiesMap(Element e) {
        CascadedStyle cs = this._matcher.getCascadedStyle(e, false);
        LinkedHashMap<String, CSSPrimitiveValue> props = new LinkedHashMap<String, CSSPrimitiveValue>();
        Iterator i = cs.getCascadedPropertyDeclarations();
        while (i.hasNext()) {
            PropertyDeclaration pd = (PropertyDeclaration)i.next();
            String propName = pd.getPropertyName();
            CSSName cssName = CSSName.getByPropertyName(propName);
            props.put(propName, cs.propertyByName(cssName).getValue());
        }
        return props;
    }

    public CascadedStyle getPseudoElementStyle(Node node, String pseudoElement) {
        Element e = null;
        e = node.getNodeType() == 1 ? (Element)node : (Element)node.getParentNode();
        return this._matcher.getPECascadedStyle(e, pseudoElement);
    }

    public CascadedStyle getCascadedStyle(Element e, boolean restyle) {
        if (e == null) {
            return CascadedStyle.emptyCascadedStyle;
        }
        return this._matcher.getCascadedStyle(e, restyle);
    }

    public PageInfo getPageStyle(String pageName, String pseudoPage) {
        return this._matcher.getPageCascadedStyle(pageName, pseudoPage);
    }

    public void flushStyleSheets() {
        String uri = this._uac.getBaseURL();
        StylesheetInfo info = new StylesheetInfo();
        info.setUri(uri);
        info.setOrigin(2);
        if (this._stylesheetFactory.containsStylesheet(uri)) {
            this._stylesheetFactory.removeCachedStylesheet(uri);
            XRLog.cssParse("Removing stylesheet '" + uri + "' from cache by request.");
        } else {
            XRLog.cssParse("Requested removing stylesheet '" + uri + "', but it's not in cache.");
        }
    }

    public void flushAllStyleSheets() {
        this._stylesheetFactory.flushCachedStylesheets();
    }

    private List getStylesheets() {
        LinkedList<StylesheetInfo> infos = new LinkedList<StylesheetInfo>();
        long st = System.currentTimeMillis();
        StylesheetInfo defaultStylesheet = this._nsh.getDefaultStylesheet(this._stylesheetFactory);
        if (defaultStylesheet != null) {
            infos.add(defaultStylesheet);
        }
        StylesheetInfo[] refs = this._nsh.getStylesheets(this._doc);
        int inlineStyleCount = 0;
        if (refs != null) {
            for (int i = 0; i < refs.length; ++i) {
                if (!refs[i].isInline()) {
                    String uri = this._uac.resolveURI(refs[i].getUri());
                    refs[i].setUri(uri);
                    continue;
                }
                refs[i].setUri(this._uac.getBaseURL() + "#inline_style_" + ++inlineStyleCount);
                Stylesheet sheet = this._stylesheetFactory.parse(new StringReader(refs[i].getContent()), refs[i]);
                refs[i].setStylesheet(sheet);
                refs[i].setUri(null);
            }
        }
        infos.addAll(Arrays.asList(refs));
        long el = System.currentTimeMillis() - st;
        XRLog.load("TIME: parse stylesheets  " + el + "ms");
        return infos;
    }

    public void removeStyle(Element e) {
        if (this._matcher != null) {
            this._matcher.removeStyle(e);
        }
    }

    public List getFontFaceRules() {
        return this._matcher.getFontFaceRules();
    }

    public void setUserAgentCallback(UserAgentCallback userAgentCallback) {
        this._uac = userAgentCallback;
        this._stylesheetFactory.setUserAgentCallback(userAgentCallback);
    }

    public void setSupportCMYKColors(boolean b) {
        this._stylesheetFactory.setSupportCMYKColors(b);
    }
}

