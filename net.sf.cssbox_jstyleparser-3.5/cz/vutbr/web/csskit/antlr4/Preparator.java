/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.antlr4;

import cz.vutbr.web.css.CombinedSelector;
import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.KeyframeBlock;
import cz.vutbr.web.css.MediaQuery;
import cz.vutbr.web.css.RuleBlock;
import cz.vutbr.web.css.RuleMargin;
import cz.vutbr.web.css.RuleSet;
import cz.vutbr.web.css.Selector;
import java.util.List;

public interface Preparator {
    public RuleBlock<?> prepareRuleSet(List<CombinedSelector> var1, List<Declaration> var2, boolean var3, List<MediaQuery> var4);

    public RuleBlock<?> prepareInlineRuleSet(List<Declaration> var1, List<Selector.SelectorPart> var2);

    public RuleBlock<?> prepareRuleMedia(List<RuleSet> var1, List<MediaQuery> var2);

    public RuleBlock<?> prepareRulePage(List<Declaration> var1, List<RuleMargin> var2, String var3, Selector.PseudoPage var4);

    public RuleMargin prepareRuleMargin(String var1, List<Declaration> var2);

    public RuleBlock<?> prepareRuleViewport(List<Declaration> var1);

    public RuleBlock<?> prepareRuleFontFace(List<Declaration> var1);

    public RuleBlock<?> prepareRuleKeyframes(List<KeyframeBlock> var1, String var2);
}

