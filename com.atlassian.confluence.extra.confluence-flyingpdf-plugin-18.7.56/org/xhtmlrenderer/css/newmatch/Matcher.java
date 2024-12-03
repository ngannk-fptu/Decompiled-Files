/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.newmatch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import org.xhtmlrenderer.css.extend.AttributeResolver;
import org.xhtmlrenderer.css.extend.StylesheetFactory;
import org.xhtmlrenderer.css.extend.TreeResolver;
import org.xhtmlrenderer.css.newmatch.CascadedStyle;
import org.xhtmlrenderer.css.newmatch.PageInfo;
import org.xhtmlrenderer.css.newmatch.Selector;
import org.xhtmlrenderer.css.sheet.MediaRule;
import org.xhtmlrenderer.css.sheet.PageRule;
import org.xhtmlrenderer.css.sheet.Ruleset;
import org.xhtmlrenderer.css.sheet.Stylesheet;
import org.xhtmlrenderer.util.Util;
import org.xhtmlrenderer.util.XRLog;

public class Matcher {
    Mapper docMapper;
    private AttributeResolver _attRes;
    private TreeResolver _treeRes;
    private StylesheetFactory _styleFactory;
    private Map _map;
    private Set _hoverElements;
    private Set _activeElements;
    private Set _focusElements;
    private Set _visitElements;
    private List _pageRules;
    private List _fontFaceRules;

    public Matcher(TreeResolver tr, AttributeResolver ar, StylesheetFactory factory, List stylesheets, String medium) {
        this.newMaps();
        this._treeRes = tr;
        this._attRes = ar;
        this._styleFactory = factory;
        this._pageRules = new ArrayList();
        this._fontFaceRules = new ArrayList();
        this.docMapper = this.createDocumentMapper(stylesheets, medium);
    }

    public void removeStyle(Object e) {
        this._map.remove(e);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CascadedStyle getCascadedStyle(Object e, boolean restyle) {
        Object object = e;
        synchronized (object) {
            Mapper em = !restyle ? this.getMapper(e) : this.matchElement(e);
            return em.getCascadedStyle(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CascadedStyle getPECascadedStyle(Object e, String pseudoElement) {
        Object object = e;
        synchronized (object) {
            Mapper em = this.getMapper(e);
            return em.getPECascadedStyle(e, pseudoElement);
        }
    }

    public PageInfo getPageCascadedStyle(String pageName, String pseudoPage) {
        ArrayList props = new ArrayList();
        HashMap marginBoxes = new HashMap();
        for (PageRule pageRule : this._pageRules) {
            if (!pageRule.applies(pageName, pseudoPage)) continue;
            props.addAll(pageRule.getRuleset().getPropertyDeclarations());
            marginBoxes.putAll(pageRule.getMarginBoxes());
        }
        CascadedStyle style = null;
        style = props.isEmpty() ? CascadedStyle.emptyCascadedStyle : new CascadedStyle(props.iterator());
        return new PageInfo(props, style, marginBoxes);
    }

    public List getFontFaceRules() {
        return this._fontFaceRules;
    }

    public boolean isVisitedStyled(Object e) {
        return this._visitElements.contains(e);
    }

    public boolean isHoverStyled(Object e) {
        return this._hoverElements.contains(e);
    }

    public boolean isActiveStyled(Object e) {
        return this._activeElements.contains(e);
    }

    public boolean isFocusStyled(Object e) {
        return this._focusElements.contains(e);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Mapper matchElement(Object e) {
        Object object = e;
        synchronized (object) {
            Mapper child;
            Object parent = this._treeRes.getParentElement(e);
            if (parent != null) {
                Mapper m = this.getMapper(parent);
                child = m.mapChild(e);
            } else {
                child = this.docMapper.mapChild(e);
            }
            return child;
        }
    }

    Mapper createDocumentMapper(List stylesheets, String medium) {
        TreeMap sorter = new TreeMap();
        this.addAllStylesheets(stylesheets, sorter, medium);
        XRLog.match("Matcher created with " + sorter.size() + " selectors");
        return new Mapper(sorter.values());
    }

    private void addAllStylesheets(List stylesheets, TreeMap sorter, String medium) {
        int count = 0;
        int pCount = 0;
        for (Stylesheet stylesheet : stylesheets) {
            for (Object obj : stylesheet.getContents()) {
                MediaRule mediaRule;
                if (obj instanceof Ruleset) {
                    for (Selector selector : ((Ruleset)obj).getFSSelectors()) {
                        selector.setPos(++count);
                        sorter.put(selector.getOrder(), selector);
                    }
                    continue;
                }
                if (obj instanceof PageRule) {
                    ((PageRule)obj).setPos(++pCount);
                    this._pageRules.add(obj);
                    continue;
                }
                if (!(obj instanceof MediaRule) || !(mediaRule = (MediaRule)obj).matches(medium)) continue;
                for (Ruleset ruleset : mediaRule.getContents()) {
                    for (Selector selector : ruleset.getFSSelectors()) {
                        selector.setPos(++count);
                        sorter.put(selector.getOrder(), selector);
                    }
                }
            }
            this._fontFaceRules.addAll(stylesheet.getFontFaceRules());
        }
        Collections.sort(this._pageRules, new Comparator(){

            public int compare(Object o1, Object o2) {
                PageRule p1 = (PageRule)o1;
                PageRule p2 = (PageRule)o2;
                if (p1.getOrder() - p2.getOrder() < 0L) {
                    return -1;
                }
                if (p1.getOrder() == p2.getOrder()) {
                    return 0;
                }
                return 1;
            }
        });
    }

    private void link(Object e, Mapper m) {
        this._map.put(e, m);
    }

    private void newMaps() {
        this._map = Collections.synchronizedMap(new HashMap());
        this._hoverElements = Collections.synchronizedSet(new HashSet());
        this._activeElements = Collections.synchronizedSet(new HashSet());
        this._focusElements = Collections.synchronizedSet(new HashSet());
        this._visitElements = Collections.synchronizedSet(new HashSet());
    }

    private Mapper getMapper(Object e) {
        Mapper m = (Mapper)this._map.get(e);
        if (m != null) {
            return m;
        }
        m = this.matchElement(e);
        return m;
    }

    private static Iterator getMatchedRulesets(final List mappedSelectors) {
        return new Iterator(){
            Iterator selectors;
            {
                this.selectors = mappedSelectors.iterator();
            }

            @Override
            public boolean hasNext() {
                return this.selectors.hasNext();
            }

            public Object next() {
                if (this.hasNext()) {
                    return ((Selector)this.selectors.next()).getRuleset();
                }
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    private static Iterator getSelectedRulesets(List selectorList) {
        final List sl = selectorList;
        return new Iterator(){
            Iterator selectors;
            {
                this.selectors = sl.iterator();
            }

            @Override
            public boolean hasNext() {
                return this.selectors.hasNext();
            }

            public Object next() {
                if (this.hasNext()) {
                    return ((Selector)this.selectors.next()).getRuleset();
                }
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Ruleset getElementStyle(Object e) {
        Object object = e;
        synchronized (object) {
            if (this._attRes == null || this._styleFactory == null) {
                return null;
            }
            String style = this._attRes.getElementStyling(e);
            if (Util.isNullOrEmpty(style)) {
                return null;
            }
            return this._styleFactory.parseStyleDeclaration(2, style);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Ruleset getNonCssStyle(Object e) {
        Object object = e;
        synchronized (object) {
            if (this._attRes == null || this._styleFactory == null) {
                return null;
            }
            String style = this._attRes.getNonCssStyling(e);
            if (Util.isNullOrEmpty(style)) {
                return null;
            }
            return this._styleFactory.parseStyleDeclaration(2, style);
        }
    }

    class Mapper {
        List axes;
        private HashMap pseudoSelectors;
        private List mappedSelectors;
        private HashMap children;

        Mapper(Collection selectors) {
            this.axes = new ArrayList(selectors.size());
            this.axes.addAll(selectors);
        }

        private Mapper() {
        }

        Mapper mapChild(Object e) {
            Mapper childMapper;
            ArrayList<Selector> childAxes = new ArrayList<Selector>(this.axes.size() + 10);
            HashMap<String, LinkedList<Selector>> pseudoSelectors = new HashMap<String, LinkedList<Selector>>();
            LinkedList<Selector> mappedSelectors = new LinkedList<Selector>();
            StringBuffer key = new StringBuffer();
            int size = this.axes.size();
            for (int i = 0; i < size; ++i) {
                Selector sel = (Selector)this.axes.get(i);
                if (sel.getAxis() == 0) {
                    childAxes.add(sel);
                } else if (sel.getAxis() == 2) {
                    throw new RuntimeException();
                }
                if (!sel.matches(e, Matcher.this._attRes, Matcher.this._treeRes)) continue;
                String pseudoElement = sel.getPseudoElement();
                if (pseudoElement != null) {
                    LinkedList<Selector> l = (LinkedList<Selector>)pseudoSelectors.get(pseudoElement);
                    if (l == null) {
                        l = new LinkedList<Selector>();
                        pseudoSelectors.put(pseudoElement, l);
                    }
                    l.add(sel);
                    key.append(sel.getSelectorID()).append(":");
                    continue;
                }
                if (sel.isPseudoClass(2)) {
                    Matcher.this._visitElements.add(e);
                }
                if (sel.isPseudoClass(8)) {
                    Matcher.this._activeElements.add(e);
                }
                if (sel.isPseudoClass(4)) {
                    Matcher.this._hoverElements.add(e);
                }
                if (sel.isPseudoClass(16)) {
                    Matcher.this._focusElements.add(e);
                }
                if (!sel.matchesDynamic(e, Matcher.this._attRes, Matcher.this._treeRes)) continue;
                key.append(sel.getSelectorID()).append(":");
                Selector chain = sel.getChainedSelector();
                if (chain == null) {
                    mappedSelectors.add(sel);
                    continue;
                }
                if (chain.getAxis() == 2) {
                    throw new RuntimeException();
                }
                childAxes.add(chain);
            }
            if (this.children == null) {
                this.children = new HashMap();
            }
            if ((childMapper = (Mapper)this.children.get(key.toString())) == null) {
                childMapper = new Mapper();
                childMapper.axes = childAxes;
                childMapper.pseudoSelectors = pseudoSelectors;
                childMapper.mappedSelectors = mappedSelectors;
                this.children.put(key.toString(), childMapper);
            }
            Matcher.this.link(e, childMapper);
            return childMapper;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        CascadedStyle getCascadedStyle(Object e) {
            CascadedStyle result;
            Object object = e;
            synchronized (object) {
                CascadedStyle cs = null;
                Ruleset elementStyling = Matcher.this.getElementStyle(e);
                Ruleset nonCssStyling = Matcher.this.getNonCssStyle(e);
                LinkedList propList = new LinkedList();
                if (nonCssStyling != null) {
                    propList.addAll(nonCssStyling.getPropertyDeclarations());
                }
                Iterator i = Matcher.getMatchedRulesets(this.mappedSelectors);
                while (i.hasNext()) {
                    Ruleset rs = (Ruleset)i.next();
                    propList.addAll(rs.getPropertyDeclarations());
                }
                if (elementStyling != null) {
                    propList.addAll(elementStyling.getPropertyDeclarations());
                }
                cs = propList.size() == 0 ? CascadedStyle.emptyCascadedStyle : new CascadedStyle(propList.iterator());
                result = cs;
            }
            return result;
        }

        public CascadedStyle getPECascadedStyle(Object e, String pseudoElement) {
            Iterator si = this.pseudoSelectors.entrySet().iterator();
            if (!si.hasNext()) {
                return null;
            }
            CascadedStyle cs = null;
            List pe = (List)this.pseudoSelectors.get(pseudoElement);
            if (pe == null) {
                return null;
            }
            LinkedList propList = new LinkedList();
            Iterator i = Matcher.getSelectedRulesets(pe);
            while (i.hasNext()) {
                Ruleset rs = (Ruleset)i.next();
                propList.addAll(rs.getPropertyDeclarations());
            }
            cs = propList.size() == 0 ? CascadedStyle.emptyCascadedStyle : new CascadedStyle(propList.iterator());
            return cs;
        }
    }
}

