/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package cz.vutbr.web.domassign;

import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.CombinedSelector;
import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.ElementMatcher;
import cz.vutbr.web.css.MatchCondition;
import cz.vutbr.web.css.MediaSpec;
import cz.vutbr.web.css.NodeData;
import cz.vutbr.web.css.RuleSet;
import cz.vutbr.web.css.Selector;
import cz.vutbr.web.css.StyleSheet;
import cz.vutbr.web.domassign.AnalyzerUtil;
import cz.vutbr.web.domassign.AssignedDeclaration;
import cz.vutbr.web.domassign.DeclarationMap;
import cz.vutbr.web.domassign.StyleMap;
import cz.vutbr.web.domassign.Traversal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.TreeWalker;

public class Analyzer {
    private static final Logger log = LoggerFactory.getLogger(Analyzer.class);
    protected List<StyleSheet> sheets;
    protected int currentOrder;
    protected Holder rules;
    private MatchCondition matchCond;
    private ElementMatcher matcher;

    public Analyzer(StyleSheet sheet) {
        this.sheets = new ArrayList<StyleSheet>(1);
        this.sheets.add(sheet);
        this.matchCond = CSSFactory.getDefaultMatchCondition();
        this.matcher = CSSFactory.getElementMatcher();
    }

    public Analyzer(List<StyleSheet> sheets) {
        this.sheets = sheets;
        this.matchCond = CSSFactory.getDefaultMatchCondition();
        this.matcher = CSSFactory.getElementMatcher();
    }

    public final void registerMatchCondition(MatchCondition matchCond) {
        this.matchCond = matchCond;
    }

    public final MatchCondition getMatchCondition() {
        return this.matchCond;
    }

    public final void registerElementMatcher(ElementMatcher matcher) {
        this.matcher = matcher;
    }

    public final ElementMatcher getElementMatcher() {
        return this.matcher;
    }

    public StyleMap evaluateDOM(Document doc, MediaSpec media, final boolean inherit) {
        DeclarationMap declarations = this.assingDeclarationsToDOM(doc, media, inherit);
        StyleMap nodes = new StyleMap(declarations.size());
        Traversal<StyleMap> traversal = new Traversal<StyleMap>(doc, (Object)declarations, 1){

            @Override
            protected void processNode(StyleMap result, Node current, Object source) {
                NodeData main = CSSFactory.createNodeData();
                List declarations = (List)((DeclarationMap)source).get((Element)current, null);
                if (declarations != null) {
                    for (Declaration d : declarations) {
                        main.push(d);
                    }
                    if (inherit) {
                        main.inheritFrom((NodeData)result.get((Element)this.walker.parentNode(), null));
                    }
                }
                result.put((Element)current, null, main.concretize());
                for (Selector.PseudoElementType pseudo : ((DeclarationMap)source).pseudoSet((Element)current)) {
                    NodeData pdata = CSSFactory.createNodeData();
                    declarations = (List)((DeclarationMap)source).get((Element)current, pseudo);
                    if (declarations != null) {
                        for (Declaration d : declarations) {
                            pdata.push(d);
                        }
                        pdata.inheritFrom(main);
                    }
                    result.put((Element)current, pseudo, pdata.concretize());
                }
            }
        };
        traversal.levelTraversal(nodes);
        return nodes;
    }

    public StyleMap evaluateDOM(Document doc, String media, boolean inherit) {
        return this.evaluateDOM(doc, new MediaSpec(media), inherit);
    }

    protected DeclarationMap assingDeclarationsToDOM(Document doc, MediaSpec media, boolean inherit) {
        this.classifyAllSheets(media);
        DeclarationMap declarations = new DeclarationMap();
        if (this.rules != null && !this.rules.isEmpty()) {
            Traversal<DeclarationMap> traversal = new Traversal<DeclarationMap>(doc, (Object)this.rules, 1){

                @Override
                protected void processNode(DeclarationMap result, Node current, Object source) {
                    Analyzer.this.assignDeclarationsToElement(result, this.walker, (Element)current, (Holder)source);
                }
            };
            if (!inherit) {
                traversal.listTraversal(declarations);
            } else {
                traversal.levelTraversal(declarations);
            }
        }
        return declarations;
    }

    protected void assignDeclarationsToElement(DeclarationMap declarations, TreeWalker walker, Element e, Holder holder) {
        List<OrderedRule> rules;
        List<OrderedRule> rules2;
        if (log.isDebugEnabled()) {
            log.debug("Traversal of {} {}.", (Object)e.getNodeName(), (Object)e.getNodeValue());
        }
        HashSet<OrderedRule> candidates = new HashSet<OrderedRule>();
        for (String cname : this.matcher.elementClasses(e)) {
            rules2 = holder.get(HolderItem.CLASS, cname.toLowerCase());
            if (rules2 == null) continue;
            candidates.addAll(rules2);
        }
        log.trace("After CLASSes {} total candidates.", (Object)candidates.size());
        String id = this.matcher.elementID(e);
        if (id != null && id.length() != 0 && (rules = holder.get(HolderItem.ID, id.toLowerCase())) != null) {
            candidates.addAll(rules);
        }
        log.trace("After IDs {} total candidates.", (Object)candidates.size());
        String name = this.matcher.elementName(e);
        if (name != null && (rules2 = holder.get(HolderItem.ELEMENT, name.toLowerCase())) != null) {
            candidates.addAll(rules2);
        }
        log.trace("After ELEMENTs {} total candidates.", (Object)candidates.size());
        candidates.addAll(holder.get(HolderItem.OTHER, null));
        ArrayList clist = new ArrayList(candidates);
        Collections.sort(clist);
        log.debug("Totally {} candidates.", (Object)candidates.size());
        log.trace("With values: {}", clist);
        ArrayList<AssignedDeclaration> eldecl = new ArrayList<AssignedDeclaration>();
        HashSet<Selector.PseudoElementType> pseudos = new HashSet<Selector.PseudoElementType>();
        for (OrderedRule orule : clist) {
            RuleSet rule = orule.getRule();
            StyleSheet sheet = rule.getStyleSheet();
            if (sheet == null) {
                log.warn("No source style sheet set for rule: {}", (Object)rule.toString());
            }
            StyleSheet.Origin origin = sheet == null ? StyleSheet.Origin.AGENT : sheet.getOrigin();
            for (CombinedSelector s : rule.getSelectors()) {
                if (!this.matchSelector(s, e, walker)) {
                    log.trace("CombinedSelector \"{}\" NOT matched!", (Object)s);
                    continue;
                }
                log.trace("CombinedSelector \"{}\" matched", (Object)s);
                Selector.PseudoElementType pseudo = s.getPseudoElementType();
                CombinedSelector.Specificity spec = s.computeSpecificity();
                if (pseudo == null) {
                    for (Declaration d : rule) {
                        eldecl.add(new AssignedDeclaration(d, spec, origin));
                    }
                    continue;
                }
                pseudos.add(pseudo);
                for (Declaration d : rule) {
                    declarations.addDeclaration(e, pseudo, new AssignedDeclaration(d, spec, origin));
                }
            }
        }
        Collections.sort(eldecl);
        log.debug("Sorted {} declarations.", (Object)eldecl.size());
        log.trace("With values: {}", eldecl);
        for (Selector.PseudoElementType p : pseudos) {
            declarations.sortDeclarations(e, p);
        }
        declarations.put(e, null, eldecl);
    }

    protected boolean elementSelectorMatches(Selector s, Element e) {
        return s.matches(e, this.matcher, this.matchCond);
    }

    protected boolean matchSelector(CombinedSelector sel, Element e, TreeWalker w) {
        Node current = w.getCurrentNode();
        boolean retval = false;
        Selector.Combinator combinator = null;
        for (int i = sel.size() - 1; i >= 0; --i) {
            Selector s = (Selector)sel.get(i);
            if (combinator == null) {
                retval = this.elementSelectorMatches(s, e);
            } else if (combinator == Selector.Combinator.ADJACENT) {
                Element adjacent = (Element)w.previousSibling();
                retval = false;
                if (adjacent != null) {
                    retval = this.elementSelectorMatches(s, adjacent);
                }
            } else if (combinator == Selector.Combinator.PRECEDING) {
                Element preceding;
                retval = false;
                while (!retval && (preceding = (Element)w.previousSibling()) != null) {
                    retval = this.elementSelectorMatches(s, preceding);
                }
            } else if (combinator == Selector.Combinator.DESCENDANT) {
                Element ancestor;
                retval = false;
                while (!retval && (ancestor = (Element)w.parentNode()) != null) {
                    retval = this.elementSelectorMatches(s, ancestor);
                }
            } else if (combinator == Selector.Combinator.CHILD) {
                Element parent = (Element)w.parentNode();
                retval = false;
                if (parent != null) {
                    retval = this.elementSelectorMatches(s, parent);
                }
            }
            combinator = s.getCombinator();
            if (!retval) break;
        }
        w.setCurrentNode(current);
        return retval;
    }

    protected void classifyAllSheets(MediaSpec mediaspec) {
        this.rules = new Holder();
        AnalyzerUtil.classifyAllSheets(this.sheets, this.rules, mediaspec);
    }

    public static class Holder {
        private List<Map<String, List<OrderedRule>>> items = new ArrayList<Map<String, List<OrderedRule>>>(HolderItem.values().length - 1);
        private List<OrderedRule> others;

        public Holder() {
            for (HolderItem hi : HolderItem.values()) {
                if (hi == HolderItem.OTHER) {
                    this.others = new ArrayList<OrderedRule>();
                    continue;
                }
                this.items.add(new HashMap());
            }
        }

        public boolean isEmpty() {
            for (HolderItem hi : HolderItem.values()) {
                if (!(hi == HolderItem.OTHER ? !this.others.isEmpty() : !this.items.get(hi.type).isEmpty())) continue;
                return false;
            }
            return true;
        }

        public static Holder union(Holder one, Holder two) {
            Holder union = new Holder();
            if (one == null) {
                one = new Holder();
            }
            if (two == null) {
                two = new Holder();
            }
            for (HolderItem hi : HolderItem.values()) {
                if (hi == HolderItem.OTHER) {
                    union.others.addAll(one.others);
                    union.others.addAll(two.others);
                    continue;
                }
                Map<String, List<OrderedRule>> oneMap = one.items.get(hi.type);
                Map<String, List<OrderedRule>> twoMap = two.items.get(hi.type);
                Map<String, List<OrderedRule>> unionMap = union.items.get(hi.type);
                unionMap.putAll(oneMap);
                for (String key : twoMap.keySet()) {
                    if (unionMap.containsKey(key)) {
                        unionMap.get(key).addAll((Collection<OrderedRule>)twoMap.get(key));
                        continue;
                    }
                    unionMap.put(key, twoMap.get(key));
                }
            }
            return union;
        }

        public void insert(HolderItem item, String key, OrderedRule value) {
            if (item == HolderItem.OTHER) {
                this.others.add(value);
                return;
            }
            Map<String, List<OrderedRule>> map = this.items.get(item.type);
            List<OrderedRule> list = map.get(key);
            if (list == null) {
                list = new ArrayList<OrderedRule>();
                map.put(key, list);
            }
            list.add(value);
        }

        public List<OrderedRule> get(HolderItem item, String key) {
            if (item == HolderItem.OTHER) {
                return this.others;
            }
            return this.items.get(item.type()).get(key);
        }

        public String contentCount() {
            StringBuilder sb = new StringBuilder();
            for (HolderItem hi : HolderItem.values()) {
                if (hi == HolderItem.OTHER) {
                    sb.append(hi.name()).append(": ").append(this.others.size()).append(" ");
                    continue;
                }
                sb.append(hi.name()).append(":").append(this.items.get(hi.type).size()).append(" ");
            }
            return sb.toString();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (HolderItem hi : HolderItem.values()) {
                if (hi == HolderItem.OTHER) {
                    sb.append(hi.name()).append(" (").append(this.others.size()).append("): ").append(this.others).append("\n");
                    continue;
                }
                sb.append(hi.name()).append(" (").append(this.items.get(hi.type).size()).append("): ").append(this.items.get(hi.type)).append("\n");
            }
            return sb.toString();
        }
    }

    public static final class OrderedRule
    implements Comparable<OrderedRule> {
        private final RuleSet rule;
        private final int order;

        public OrderedRule(RuleSet rule, int order) {
            this.rule = rule;
            this.order = order;
        }

        public RuleSet getRule() {
            return this.rule;
        }

        public int getOrder() {
            return this.order;
        }

        @Override
        public int compareTo(OrderedRule o) {
            return this.getOrder() - o.getOrder();
        }

        public String toString() {
            return "OR" + this.order + ", " + this.rule;
        }
    }

    protected static class HolderSelector {
        public HolderItem item;
        public String key;

        public HolderSelector(HolderItem item, String key) {
            this.item = item;
            this.key = key;
        }
    }

    protected static enum HolderItem {
        ELEMENT(0),
        ID(1),
        CLASS(2),
        OTHER(3);

        private int type;

        private HolderItem(int type) {
            this.type = type;
        }

        public int type() {
            return this.type;
        }
    }
}

