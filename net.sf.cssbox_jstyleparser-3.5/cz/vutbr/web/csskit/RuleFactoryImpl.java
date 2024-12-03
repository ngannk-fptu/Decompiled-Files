/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.CombinedSelector;
import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.KeyframeBlock;
import cz.vutbr.web.css.MediaExpression;
import cz.vutbr.web.css.MediaQuery;
import cz.vutbr.web.css.RuleFactory;
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
import cz.vutbr.web.csskit.CombinedSelectorImpl;
import cz.vutbr.web.csskit.DeclarationImpl;
import cz.vutbr.web.csskit.KeyframeBlockImpl;
import cz.vutbr.web.csskit.MediaExpressionImpl;
import cz.vutbr.web.csskit.MediaQueryImpl;
import cz.vutbr.web.csskit.RuleFontFaceImpl;
import cz.vutbr.web.csskit.RuleImportImpl;
import cz.vutbr.web.csskit.RuleKeyframesImpl;
import cz.vutbr.web.csskit.RuleMarginImpl;
import cz.vutbr.web.csskit.RuleMediaImpl;
import cz.vutbr.web.csskit.RulePageImpl;
import cz.vutbr.web.csskit.RuleSetImpl;
import cz.vutbr.web.csskit.RuleViewportImpl;
import cz.vutbr.web.csskit.SelectorImpl;
import cz.vutbr.web.csskit.StyleSheetImpl;
import org.w3c.dom.Element;

public class RuleFactoryImpl
implements RuleFactory {
    private static RuleFactory instance = new RuleFactoryImpl();

    private RuleFactoryImpl() {
    }

    public static final RuleFactory getInstance() {
        return instance;
    }

    @Override
    public Declaration createDeclaration() {
        return new DeclarationImpl();
    }

    @Override
    public Declaration createDeclaration(Declaration clone) {
        return new DeclarationImpl(clone);
    }

    @Override
    @Deprecated
    public RuleImport createImport() {
        return new RuleImportImpl();
    }

    @Override
    public RuleMedia createMedia() {
        return new RuleMediaImpl();
    }

    @Override
    public MediaQuery createMediaQuery() {
        return new MediaQueryImpl();
    }

    @Override
    public MediaExpression createMediaExpression() {
        return new MediaExpressionImpl();
    }

    @Override
    public RuleKeyframes createKeyframes() {
        return new RuleKeyframesImpl();
    }

    @Override
    public KeyframeBlock createKeyframeBlock() {
        return new KeyframeBlockImpl();
    }

    @Override
    public RulePage createPage() {
        return new RulePageImpl();
    }

    @Override
    public RuleMargin createMargin(String area) {
        return new RuleMarginImpl(area);
    }

    @Override
    public RuleViewport createViewport() {
        return new RuleViewportImpl();
    }

    @Override
    public RuleFontFace createFontFace() {
        return new RuleFontFaceImpl();
    }

    @Override
    public CombinedSelector createCombinedSelector() {
        return new CombinedSelectorImpl();
    }

    @Override
    public RuleSet createSet() {
        return new RuleSetImpl();
    }

    @Override
    public Selector createSelector() {
        return new SelectorImpl();
    }

    @Override
    public Selector.ElementAttribute createAttribute(String value, boolean isStringValue, Selector.Operator operator, String attribute) {
        return new SelectorImpl.ElementAttributeImpl(value, isStringValue, operator, attribute);
    }

    @Override
    public Selector.ElementClass createClass(String className) {
        return new SelectorImpl.ElementClassImpl(className);
    }

    @Override
    public Selector.ElementName createElement(String elementName) {
        return new SelectorImpl.ElementNameImpl(elementName);
    }

    @Override
    public Selector.ElementDOM createElementDOM(Element e, boolean inlinePriority) {
        return new SelectorImpl.ElementDOMImpl(e, inlinePriority);
    }

    @Override
    public Selector.ElementID createID(String id) {
        return new SelectorImpl.ElementIDImpl(id);
    }

    @Override
    public Selector.PseudoPage createPseudoPage(String name) {
        return new SelectorImpl.PseudoPageImpl(name);
    }

    @Override
    public Selector.PseudoElement createPseudoElement(String name) {
        return new SelectorImpl.PseudoElementImpl(name);
    }

    @Override
    public Selector.PseudoElement createPseudoElement(String name, String functionValue) {
        return new SelectorImpl.PseudoElementImpl(name, functionValue);
    }

    @Override
    public Selector.PseudoElement createPseudoElement(String name, Selector nestedSelector) {
        return new SelectorImpl.PseudoElementImpl(name, nestedSelector);
    }

    @Override
    public Selector.PseudoClass createPseudoClass(String name) {
        return new SelectorImpl.PseudoClassImpl(name);
    }

    @Override
    public Selector.PseudoClass createPseudoClass(String name, String functionValue) {
        return new SelectorImpl.PseudoClassImpl(name, functionValue);
    }

    @Override
    public Selector.PseudoClass createPseudoClass(String name, Selector nestedSelector) {
        return new SelectorImpl.PseudoClassImpl(name, nestedSelector);
    }

    @Override
    public StyleSheet createStyleSheet() {
        return new StyleSheetImpl();
    }

    @Override
    public StyleSheet createStyleSheet(StyleSheet.Origin origin) {
        StyleSheetImpl ret = new StyleSheetImpl();
        ret.setOrigin(origin);
        return ret;
    }
}

