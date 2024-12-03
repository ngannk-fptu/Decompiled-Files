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
import cz.vutbr.web.css.MediaQuery;
import cz.vutbr.web.css.MediaSpec;
import cz.vutbr.web.css.NodeData;
import cz.vutbr.web.css.Rule;
import cz.vutbr.web.css.RuleMedia;
import cz.vutbr.web.css.RuleSet;
import cz.vutbr.web.css.Selector;
import cz.vutbr.web.css.StyleSheet;
import cz.vutbr.web.domassign.Analyzer;
import cz.vutbr.web.domassign.AssignedDeclaration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class AnalyzerUtil {
    private static final Logger log = LoggerFactory.getLogger(AnalyzerUtil.class);

    public static Analyzer.OrderedRule[] getApplicableRules(List<StyleSheet> sheets, Element element, MediaSpec mediaspec) {
        Analyzer.Holder rules = AnalyzerUtil.getClassifiedRules(sheets, mediaspec);
        return AnalyzerUtil.getApplicableRules(element, rules, null);
    }

    public static Analyzer.Holder getClassifiedRules(List<StyleSheet> sheets, MediaSpec mediaspec) {
        Analyzer.Holder rules = new Analyzer.Holder();
        AnalyzerUtil.classifyAllSheets(sheets, rules, mediaspec);
        return rules;
    }

    public static NodeData getElementStyle(Element el, Selector.PseudoElementType pseudo, ElementMatcher matcher, MatchCondition matchCond, Analyzer.OrderedRule[] applicableRules) {
        return AnalyzerUtil.makeNodeData(AnalyzerUtil.computeDeclarations(el, pseudo, applicableRules, matcher, matchCond));
    }

    public static Analyzer.OrderedRule[] getApplicableRules(Element e, Analyzer.Holder holder, RuleSet[] elementRuleSets) {
        List<Analyzer.OrderedRule> nameRules;
        String name;
        List<Analyzer.OrderedRule> idRules;
        HashSet<Analyzer.OrderedRule> candidates = new HashSet<Analyzer.OrderedRule>();
        for (String cname : CSSFactory.getElementMatcher().elementClasses(e)) {
            List<Analyzer.OrderedRule> classRules = holder.get(Analyzer.HolderItem.CLASS, cname.toLowerCase());
            if (classRules == null) continue;
            candidates.addAll(classRules);
        }
        String id = CSSFactory.getElementMatcher().elementID(e);
        if (id != null && id.length() != 0 && (idRules = holder.get(Analyzer.HolderItem.ID, id.toLowerCase())) != null) {
            candidates.addAll(idRules);
        }
        if ((name = CSSFactory.getElementMatcher().elementName(e)) != null && (nameRules = holder.get(Analyzer.HolderItem.ELEMENT, name.toLowerCase())) != null) {
            candidates.addAll(nameRules);
        }
        candidates.addAll(holder.get(Analyzer.HolderItem.OTHER, null));
        int totalCandidates = candidates.size();
        int netCandidates = elementRuleSets == null ? totalCandidates : totalCandidates + elementRuleSets.length;
        Object[] clist = candidates.toArray(new Analyzer.OrderedRule[netCandidates]);
        Arrays.sort(clist, 0, totalCandidates);
        if (elementRuleSets != null) {
            int lastOrder = totalCandidates > 0 ? ((Analyzer.OrderedRule)clist[totalCandidates - 1]).getOrder() : 0;
            for (int i = 0; i < elementRuleSets.length; ++i) {
                clist[totalCandidates + i] = new Analyzer.OrderedRule(elementRuleSets[i], lastOrder + i);
            }
        }
        return clist;
    }

    static NodeData makeNodeData(List<Declaration> decls) {
        NodeData main = CSSFactory.createNodeData();
        for (Declaration d : decls) {
            main.push(d);
        }
        return main;
    }

    static void classifyAllSheets(List<StyleSheet> sheets, Analyzer.Holder rules, MediaSpec mediaspec) {
        Counter orderCounter = new Counter();
        for (StyleSheet sheet : sheets) {
            AnalyzerUtil.classifyRules(sheet, mediaspec, rules, orderCounter);
        }
    }

    static boolean elementSelectorMatches(Selector s, Element e, ElementMatcher matcher, MatchCondition matchCond) {
        return s.matches(e, matcher, matchCond);
    }

    private static boolean nodeSelectorMatches(Selector s, Node n, ElementMatcher matcher, MatchCondition matchCond) {
        if (n.getNodeType() == 1) {
            return s.matches((Element)n, matcher, matchCond);
        }
        return false;
    }

    static List<Declaration> computeDeclarations(Element e, Selector.PseudoElementType pseudo, Analyzer.OrderedRule[] clist, ElementMatcher matcher, MatchCondition matchCond) {
        ArrayList<Declaration> eldecl = new ArrayList<Declaration>();
        for (Analyzer.OrderedRule orule : clist) {
            RuleSet rule = orule.getRule();
            StyleSheet sheet = rule.getStyleSheet();
            StyleSheet.Origin origin = sheet == null ? StyleSheet.Origin.AGENT : sheet.getOrigin();
            for (CombinedSelector s : rule.getSelectors()) {
                if (!AnalyzerUtil.matchSelector(s, e, matcher, matchCond)) {
                    log.trace("CombinedSelector \"{}\" NOT matched!", (Object)s);
                    continue;
                }
                log.trace("CombinedSelector \"{}\" matched", (Object)s);
                Selector.PseudoElementType ptype = s.getPseudoElementType();
                if (ptype != pseudo) continue;
                CombinedSelector.Specificity spec = s.computeSpecificity();
                for (Declaration d : rule) {
                    eldecl.add(new AssignedDeclaration(d, spec, origin));
                }
            }
        }
        Collections.sort(eldecl);
        log.debug("Sorted {} declarations.", (Object)eldecl.size());
        log.trace("With values: {}", eldecl);
        return eldecl;
    }

    public static boolean hasPseudoSelector(Analyzer.OrderedRule[] rules, Element e, MatchCondition matchCond, Selector.PseudoClassType pd) {
        for (Analyzer.OrderedRule rule : rules) {
            for (CombinedSelector cs : rule.getRule().getSelectors()) {
                Selector lastSelector = (Selector)cs.get(cs.size() - 1);
                if (!lastSelector.hasPseudoClass(pd)) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean hasPseudoSelectorForAncestor(Analyzer.OrderedRule[] rules, Element e, Element targetAncestor, ElementMatcher matcher, MatchCondition matchCond, Selector.PseudoClassType pd) {
        for (Analyzer.OrderedRule rule : rules) {
            for (CombinedSelector cs : rule.getRule().getSelectors()) {
                if (!AnalyzerUtil.hasPseudoSelectorForAncestor(cs, e, targetAncestor, matcher, matchCond, pd)) continue;
                return true;
            }
        }
        return false;
    }

    private static boolean hasPseudoSelectorForAncestor(CombinedSelector sel, Element e, Element targetAncestor, ElementMatcher matcher, MatchCondition matchCond, Selector.PseudoClassType pd) {
        boolean retval = false;
        Selector.Combinator combinator = null;
        Element current = e;
        for (int i = sel.size() - 1; i >= 0; --i) {
            Selector s = (Selector)sel.get(i);
            if (combinator == null) {
                retval = AnalyzerUtil.elementSelectorMatches(s, current, matcher, matchCond);
            } else if (combinator == Selector.Combinator.ADJACENT) {
                Node adjacent = current;
                while ((adjacent = adjacent.getPreviousSibling()) != null && adjacent.getNodeType() != 1) {
                }
                retval = false;
                if (adjacent != null && adjacent.getNodeType() == 1) {
                    current = (Element)adjacent;
                    retval = AnalyzerUtil.elementSelectorMatches(s, current, matcher, matchCond);
                }
            } else if (combinator == Selector.Combinator.PRECEDING) {
                Node preceding = current.getPreviousSibling();
                retval = false;
                do {
                    if (preceding == null) continue;
                    if (AnalyzerUtil.nodeSelectorMatches(s, preceding, matcher, matchCond)) {
                        current = (Element)preceding;
                        retval = true;
                        continue;
                    }
                    preceding = preceding.getPreviousSibling();
                } while (!retval && preceding != null);
            } else if (combinator == Selector.Combinator.DESCENDANT) {
                Node ancestor = current.getParentNode();
                retval = false;
                do {
                    if (ancestor == null) continue;
                    if (AnalyzerUtil.nodeSelectorMatches(s, ancestor, matcher, matchCond)) {
                        current = (Element)ancestor;
                        retval = true;
                        continue;
                    }
                    ancestor = ancestor.getParentNode();
                } while (!retval && ancestor != null);
            } else if (combinator == Selector.Combinator.CHILD) {
                Node parent = current.getParentNode();
                retval = false;
                if (parent != null && parent.getNodeType() == 1) {
                    current = (Element)parent;
                    retval = AnalyzerUtil.elementSelectorMatches(s, current, matcher, matchCond);
                }
            }
            combinator = s.getCombinator();
            if (!retval) break;
            if (current != targetAncestor) continue;
            return s.hasPseudoClass(pd);
        }
        return false;
    }

    protected static boolean matchSelector(CombinedSelector sel, Element e, ElementMatcher matcher, MatchCondition matchCond) {
        boolean retval = false;
        Selector.Combinator combinator = null;
        Element current = e;
        for (int i = sel.size() - 1; i >= 0; --i) {
            Selector s = (Selector)sel.get(i);
            log.trace("Iterating loop with selector {}, combinator {}", (Object)s, combinator);
            if (combinator == null) {
                retval = AnalyzerUtil.elementSelectorMatches(s, current, matcher, matchCond);
            } else if (combinator == Selector.Combinator.ADJACENT) {
                Node adjacent = current;
                while ((adjacent = adjacent.getPreviousSibling()) != null && adjacent.getNodeType() != 1) {
                }
                retval = false;
                if (adjacent != null && adjacent.getNodeType() == 1) {
                    current = (Element)adjacent;
                    retval = AnalyzerUtil.elementSelectorMatches(s, current, matcher, matchCond);
                }
            } else if (combinator == Selector.Combinator.PRECEDING) {
                Node preceding = current.getPreviousSibling();
                retval = false;
                do {
                    if (preceding == null) continue;
                    if (AnalyzerUtil.nodeSelectorMatches(s, preceding, matcher, matchCond)) {
                        current = (Element)preceding;
                        retval = true;
                        continue;
                    }
                    preceding = preceding.getPreviousSibling();
                } while (!retval && preceding != null);
            } else if (combinator == Selector.Combinator.DESCENDANT) {
                Node ancestor = current.getParentNode();
                retval = false;
                do {
                    if (ancestor == null) continue;
                    if (AnalyzerUtil.nodeSelectorMatches(s, ancestor, matcher, matchCond)) {
                        current = (Element)ancestor;
                        retval = true;
                        continue;
                    }
                    ancestor = ancestor.getParentNode();
                } while (!retval && ancestor != null);
            } else if (combinator == Selector.Combinator.CHILD) {
                Node parent = current.getParentNode();
                retval = false;
                if (parent != null && parent.getNodeType() == 1) {
                    current = (Element)parent;
                    retval = AnalyzerUtil.elementSelectorMatches(s, current, matcher, matchCond);
                }
            }
            combinator = s.getCombinator();
            if (!retval) break;
        }
        return retval;
    }

    private static List<Analyzer.HolderSelector> classifySelector(CombinedSelector selector) {
        ArrayList<Analyzer.HolderSelector> hs = new ArrayList<Analyzer.HolderSelector>();
        try {
            String id;
            String className;
            Selector last = selector.getLastSelector();
            String element = last.getElementName();
            if (element != null) {
                if ("*".equals(element)) {
                    hs.add(new Analyzer.HolderSelector(Analyzer.HolderItem.OTHER, null));
                } else {
                    hs.add(new Analyzer.HolderSelector(Analyzer.HolderItem.ELEMENT, element.toLowerCase()));
                }
            }
            if ((className = last.getClassName()) != null) {
                hs.add(new Analyzer.HolderSelector(Analyzer.HolderItem.CLASS, className.toLowerCase()));
            }
            if ((id = last.getIDName()) != null) {
                hs.add(new Analyzer.HolderSelector(Analyzer.HolderItem.ID, id.toLowerCase()));
            }
            if (hs.size() == 0) {
                hs.add(new Analyzer.HolderSelector(Analyzer.HolderItem.OTHER, null));
            }
            return hs;
        }
        catch (UnsupportedOperationException e) {
            log.error("CombinedSelector does not include any selector, this should not happen!");
            return Collections.emptyList();
        }
    }

    private static void insertClassified(Analyzer.Holder holder, List<Analyzer.HolderSelector> hs, RuleSet value, Counter orderCounter) {
        for (Analyzer.HolderSelector h : hs) {
            holder.insert(h.item, h.key, new Analyzer.OrderedRule(value, orderCounter.getAndIncrement()));
        }
    }

    private static void classifyRules(StyleSheet sheet, MediaSpec mediaspec, Analyzer.Holder rules, Counter orderCounter) {
        for (Rule rule : sheet) {
            if (rule instanceof RuleSet) {
                RuleSet ruleset = (RuleSet)rule;
                for (CombinedSelector s : ruleset.getSelectors()) {
                    AnalyzerUtil.insertClassified(rules, AnalyzerUtil.classifySelector(s), ruleset, orderCounter);
                }
                continue;
            }
            if (!(rule instanceof RuleMedia)) continue;
            RuleMedia rulemedia = (RuleMedia)rule;
            boolean mediaValid = false;
            if (rulemedia.getMediaQueries() == null || rulemedia.getMediaQueries().isEmpty()) {
                mediaValid = mediaspec.matchesEmpty();
            } else {
                for (MediaQuery media : rulemedia.getMediaQueries()) {
                    if (!mediaspec.matches(media)) continue;
                    mediaValid = true;
                    break;
                }
            }
            if (!mediaValid) continue;
            for (RuleSet ruleset : rulemedia) {
                for (CombinedSelector s : ruleset.getSelectors()) {
                    AnalyzerUtil.insertClassified(rules, AnalyzerUtil.classifySelector(s), ruleset, orderCounter);
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("For media \"{}\" we have {} rules", (Object)mediaspec, (Object)rules.contentCount());
            if (log.isTraceEnabled()) {
                log.trace("Detailed view: \n{}", (Object)rules);
            }
        }
    }

    private static class Counter {
        private int count = 0;

        private Counter() {
        }

        public int getAndIncrement() {
            return this.count++;
        }
    }
}

