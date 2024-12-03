/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.CombinedSelector;
import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.KeyframeBlock;
import cz.vutbr.web.css.MediaExpression;
import cz.vutbr.web.css.MediaQuery;
import cz.vutbr.web.css.RuleFontFace;
import cz.vutbr.web.css.RuleImport;
import cz.vutbr.web.css.RuleKeyframes;
import cz.vutbr.web.css.RuleMargin;
import cz.vutbr.web.css.RuleMedia;
import cz.vutbr.web.css.RulePage;
import cz.vutbr.web.css.RuleSet;
import cz.vutbr.web.css.RuleViewport;
import cz.vutbr.web.css.Selector;
import cz.vutbr.web.css.StyleSheet;
import org.w3c.dom.Element;

public interface RuleFactory {
    public Declaration createDeclaration();

    public Declaration createDeclaration(Declaration var1);

    @Deprecated
    public RuleImport createImport();

    public RuleSet createSet();

    public RuleMedia createMedia();

    public MediaQuery createMediaQuery();

    public MediaExpression createMediaExpression();

    public RuleKeyframes createKeyframes();

    public KeyframeBlock createKeyframeBlock();

    public RulePage createPage();

    public RuleMargin createMargin(String var1);

    public RuleViewport createViewport();

    public RuleFontFace createFontFace();

    public CombinedSelector createCombinedSelector();

    public Selector createSelector();

    public Selector.ElementDOM createElementDOM(Element var1, boolean var2);

    public Selector.ElementName createElement(String var1);

    public Selector.ElementAttribute createAttribute(String var1, boolean var2, Selector.Operator var3, String var4);

    public Selector.ElementClass createClass(String var1);

    public Selector.ElementID createID(String var1);

    public Selector.PseudoPage createPseudoPage(String var1);

    public Selector.PseudoElement createPseudoElement(String var1);

    public Selector.PseudoElement createPseudoElement(String var1, String var2);

    public Selector.PseudoElement createPseudoElement(String var1, Selector var2);

    public Selector.PseudoClass createPseudoClass(String var1);

    public Selector.PseudoClass createPseudoClass(String var1, String var2);

    public Selector.PseudoClass createPseudoClass(String var1, Selector var2);

    public StyleSheet createStyleSheet();

    public StyleSheet createStyleSheet(StyleSheet.Origin var1);
}

