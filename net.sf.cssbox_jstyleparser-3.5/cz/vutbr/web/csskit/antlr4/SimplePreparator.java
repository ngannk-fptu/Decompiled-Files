/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package cz.vutbr.web.csskit.antlr4;

import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.CombinedSelector;
import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.KeyframeBlock;
import cz.vutbr.web.css.MediaQuery;
import cz.vutbr.web.css.RuleBlock;
import cz.vutbr.web.css.RuleFactory;
import cz.vutbr.web.css.RuleFontFace;
import cz.vutbr.web.css.RuleKeyframes;
import cz.vutbr.web.css.RuleMargin;
import cz.vutbr.web.css.RuleMedia;
import cz.vutbr.web.css.RulePage;
import cz.vutbr.web.css.RuleSet;
import cz.vutbr.web.css.RuleViewport;
import cz.vutbr.web.css.Selector;
import cz.vutbr.web.csskit.antlr4.Preparator;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class SimplePreparator
implements Preparator {
    protected static final Logger log = LoggerFactory.getLogger(SimplePreparator.class);
    private static RuleFactory rf = CSSFactory.getRuleFactory();
    private Element elem;
    private boolean inlinePriority;

    public SimplePreparator(Element e, boolean inlinePriority) {
        this.elem = e;
        this.inlinePriority = inlinePriority;
    }

    @Override
    public RuleBlock<?> prepareRuleSet(List<CombinedSelector> cslist, List<Declaration> dlist, boolean wrap, List<MediaQuery> media) {
        if (cslist == null || cslist.isEmpty() || dlist == null || dlist.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("Empty RuleSet was ommited");
            }
            return null;
        }
        RuleSet rs = rf.createSet();
        rs.setSelectors(cslist);
        rs.replaceAll(dlist);
        log.info("Created RuleSet as with:\n{}", (Object)rs);
        if (wrap) {
            RuleMedia rm = rf.createMedia();
            if (log.isDebugEnabled()) {
                log.debug("Wrapping RuleSet {} into RuleMedia: {}", (Object)rs, media);
            }
            rm.unlock();
            rm.add(rs);
            rm.setMediaQueries(media);
            return rm;
        }
        return rs;
    }

    @Override
    public RuleBlock<?> prepareRuleMedia(List<RuleSet> rules, List<MediaQuery> media) {
        if (rules == null || rules.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("Empty RuleMedia was ommited");
            }
            return null;
        }
        RuleMedia rm = rf.createMedia();
        rm.replaceAll(rules);
        if (media != null && !media.isEmpty()) {
            rm.setMediaQueries(media);
        }
        log.info("Create @media as with:\n{}", (Object)rm);
        return rm;
    }

    @Override
    public RuleBlock<?> prepareRulePage(List<Declaration> declarations, List<RuleMargin> marginRules, String name, Selector.PseudoPage pseudo) {
        if ((declarations == null || declarations.isEmpty()) && (marginRules == null || marginRules.isEmpty())) {
            if (log.isDebugEnabled()) {
                log.debug("Empty RulePage was ommited");
            }
            return null;
        }
        RulePage rp = rf.createPage();
        if (declarations != null) {
            for (Declaration d : declarations) {
                rp.add(d);
            }
        }
        if (marginRules != null) {
            for (RuleMargin m : marginRules) {
                rp.add(m);
            }
        }
        rp.setName(name);
        rp.setPseudo(pseudo);
        log.info("Create @page as with:\n{}", (Object)rp);
        return rp;
    }

    @Override
    public RuleMargin prepareRuleMargin(String area, List<Declaration> decl) {
        if (decl == null || decl.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("Empty RuleMargin was ommited");
            }
            return null;
        }
        RuleMargin rm = rf.createMargin(area);
        rm.replaceAll(decl);
        log.info("Create @" + area + " with:\n" + rm);
        return rm;
    }

    @Override
    public RuleBlock<?> prepareRuleViewport(List<Declaration> decl) {
        if (decl == null || decl.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("Empty Viewport was ommited");
            }
            return null;
        }
        RuleViewport rp = rf.createViewport();
        rp.replaceAll(decl);
        log.info("Create @viewport as {}th with:\n{}", (Object)rp);
        return rp;
    }

    @Override
    public RuleBlock<?> prepareRuleFontFace(List<Declaration> decl) {
        if (decl == null || decl.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("Empty RuleFontFace was ommited");
            }
            return null;
        }
        RuleFontFace rp = rf.createFontFace();
        rp.replaceAll(decl);
        log.info("Create @font-face as with:\n{}", (Object)rp);
        return rp;
    }

    @Override
    public RuleBlock<?> prepareInlineRuleSet(List<Declaration> dlist, List<Selector.SelectorPart> pseudos) {
        if (dlist == null || dlist.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("Empty RuleSet (inline) was ommited");
            }
            return null;
        }
        CombinedSelector cs = (CombinedSelector)rf.createCombinedSelector().unlock();
        Selector sel = (Selector)rf.createSelector().unlock();
        sel.add(rf.createElementDOM(this.elem, this.inlinePriority));
        if (pseudos != null) {
            sel.addAll(pseudos);
        }
        cs.add(sel);
        RuleSet rs = rf.createSet();
        rs.replaceAll(dlist);
        rs.setSelectors(Arrays.asList(cs));
        log.info("Create inline ruleset as with:\n{}", (Object)rs);
        return rs;
    }

    @Override
    public RuleBlock<?> prepareRuleKeyframes(List<KeyframeBlock> rules, String name) {
        if (rules == null || rules.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("Empty RuleKeyframes was ommited");
            }
            return null;
        }
        if (name == null || name.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("RuleKeyframes with no name was ommited");
            }
            return null;
        }
        RuleKeyframes rk = rf.createKeyframes();
        rk.replaceAll(rules);
        rk.setName(name);
        log.info("Create @keyframes as with:\n{}", (Object)rk);
        return rk;
    }
}

