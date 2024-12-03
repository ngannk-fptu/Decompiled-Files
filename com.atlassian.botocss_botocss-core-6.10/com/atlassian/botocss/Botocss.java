/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cz.vutbr.web.css.CombinedSelector
 *  cz.vutbr.web.css.Declaration
 *  cz.vutbr.web.css.RuleBlock
 *  cz.vutbr.web.css.RuleSet
 *  cz.vutbr.web.css.StyleSheet
 *  cz.vutbr.web.css.Term
 *  cz.vutbr.web.css.TermColor
 *  cz.vutbr.web.css.TermLength
 *  cz.vutbr.web.css.TermURI
 *  cz.vutbr.web.csskit.Color
 *  org.jsoup.Jsoup
 *  org.jsoup.nodes.Document
 *  org.jsoup.nodes.Element
 *  org.jsoup.select.Selector$SelectorParseException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.unbescape.css.CssEscape
 */
package com.atlassian.botocss;

import com.atlassian.botocss.BotocssExpansion;
import com.atlassian.botocss.BotocssStyles;
import com.atlassian.botocss.DocumentFunctions;
import cz.vutbr.web.css.CombinedSelector;
import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.RuleBlock;
import cz.vutbr.web.css.RuleSet;
import cz.vutbr.web.css.StyleSheet;
import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermColor;
import cz.vutbr.web.css.TermLength;
import cz.vutbr.web.css.TermURI;
import cz.vutbr.web.csskit.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unbescape.css.CssEscape;

public final class Botocss {
    private static final int INCH_MULTIPLIER = 96;
    private static final float PC_MULTIPLIER = 16.0f;
    private static final float MM_MULTIPLIER = 3.7795277f;
    private static final float CM_MULTIPLIER = 37.795277f;
    private static final float PT_MULTIPLIER = 1.3333334f;
    private static final Logger log = LoggerFactory.getLogger(Botocss.class);
    private static final Map<String, BotocssExpansion> EXPANDABLE_ATTRS = new HashMap<String, BotocssExpansion>();

    public static String inject(String html, String ... stylesheets) {
        log.debug("Parsing external stylesheets");
        BotocssStyles styles = BotocssStyles.parse(stylesheets);
        return Botocss.inject(html, styles);
    }

    public static String inject(String html, BotocssStyles styles) {
        return Botocss.inject(html, styles, DocumentFunctions.PRETTY_PRINT);
    }

    public static String inject(String html, BotocssStyles styles, Function<Document, Document> documentFunction) {
        return Botocss.inject(html, styles, documentFunction, true);
    }

    public static String inject(String html, BotocssStyles styles, Function<Document, Document> documentFunction, boolean processInlineStyles) {
        Document processedDocumentWithCSSInlined;
        HashMap stringCache = new HashMap();
        Function<String, String> cache = k -> stringCache.computeIfAbsent(k, fKey -> k);
        long start = System.currentTimeMillis();
        Document documentWithCSSInlined = Jsoup.parse((String)html);
        log.debug("Parsed HTML document in {} ms", (Object)(System.currentTimeMillis() - start));
        int selectorCount = 0;
        log.debug("Applying external stylesheets");
        selectorCount += Botocss.applyStyles(cache, documentWithCSSInlined, styles);
        if (processInlineStyles) {
            log.debug("Finding inline stylesheets");
            LinkedHashSet<String> inlineCssStyles = new LinkedHashSet<String>();
            for (Element inlineStyle : documentWithCSSInlined.getElementsByTag("style")) {
                inlineCssStyles.add(inlineStyle.html());
            }
            if (!inlineCssStyles.isEmpty()) {
                log.debug("Parsing inline stylesheets");
                BotocssStyles inlineStyles = BotocssStyles.parse(inlineCssStyles.toArray(new String[0]));
                log.debug("Applying inline stylesheets");
                selectorCount += Botocss.applyStyles(cache, documentWithCSSInlined, inlineStyles);
            }
        }
        Document resultDocumentWithCSSInlined = (processedDocumentWithCSSInlined = documentFunction.apply(documentWithCSSInlined)) != null ? processedDocumentWithCSSInlined : documentWithCSSInlined;
        String result = resultDocumentWithCSSInlined.outerHtml();
        log.info("Applying {} CSS selectors to HTML (length {}) took {} ms", new Object[]{selectorCount, html.length(), System.currentTimeMillis() - start});
        return result;
    }

    public static BotocssStyles parse(String ... stylesheets) {
        return BotocssStyles.parse(stylesheets);
    }

    private static int applyStyles(Function<String, String> cache, Document document, BotocssStyles styles) {
        int selectorCount = 0;
        for (StyleSheet styleSheet : styles.getStyleSheets()) {
            selectorCount += Botocss.applyStylesheet(cache, document, styleSheet);
        }
        return selectorCount;
    }

    private static int applyStylesheet(Function<String, String> cache, Document document, StyleSheet stylesheet) {
        int selectorCount = 0;
        for (RuleBlock block : stylesheet) {
            if (!(block instanceof RuleSet)) continue;
            RuleSet set = (RuleSet)block;
            for (CombinedSelector selector : set.getSelectors()) {
                log.debug("Applying selector #{}: {}", (Object)(++selectorCount), (Object)selector.toString());
                List<Element> elements = Botocss.findElements(document, selector);
                for (Element element : elements) {
                    for (Declaration declaration : set) {
                        log.debug("Applying style [ {} ] to element: {}", (Object)declaration.toString().trim(), (Object)element.nodeName());
                        try {
                            String ruleKey = declaration.getProperty() + ": " + Botocss.getStringValue(declaration);
                            String ruleVal = cache.apply(ruleKey);
                            String existingStyleKey = element.attr("style");
                            String existingStyleVal = cache.apply(existingStyleKey);
                            String updateStyleKey = existingStyleVal.equals("") ? ruleVal : existingStyleVal + "; " + ruleVal;
                            String updateStyleVal = cache.apply(updateStyleKey);
                            element.attr("style", updateStyleVal);
                            Botocss.expandProperties(cache, element, declaration);
                        }
                        catch (IllegalArgumentException e) {
                            log.warn("Failed to process CSS property value: " + e.getMessage());
                        }
                    }
                }
            }
        }
        return selectorCount;
    }

    private static List<Element> findElements(Document document, CombinedSelector selector) {
        try {
            return document.select(CssEscape.unescapeCss((String)selector.toString()));
        }
        catch (Selector.SelectorParseException e) {
            log.info("Skipping unsupported selector: " + selector.toString());
            return Collections.emptyList();
        }
    }

    private static String getStringValue(Declaration declaration) {
        StringBuilder result = new StringBuilder();
        for (Term term : declaration) {
            result.append(Botocss.getStringValue(term));
        }
        return result.toString();
    }

    private static String getStringValue(Term<?> term) {
        if (term instanceof TermURI) {
            String operator = term.getOperator() == null ? "" : term.getOperator().value();
            return operator + "url(" + term.getValue() + ")";
        }
        return term.toString();
    }

    private static String formatColor(TermColor term) {
        Color color = (Color)term.getValue();
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    private static String formatNumber(float value) {
        if ((double)value == Math.ceil(value)) {
            return String.valueOf(Math.round(value));
        }
        return String.format(Locale.ENGLISH, "%.3f", Float.valueOf(value));
    }

    private static void expandProperties(Function<String, String> cache, Element element, Declaration declaration) {
        String property = declaration.getProperty();
        if (declaration.isEmpty()) {
            log.debug("Value for {} not provided", (Object)property);
            return;
        }
        BotocssExpansion expansion = EXPANDABLE_ATTRS.get(property);
        if (expansion == null) {
            return;
        }
        String value = expansion.getProcessor().parse(declaration);
        if (value == null) {
            return;
        }
        element.attr(cache.apply(expansion.getAttributeName()), cache.apply(value));
    }

    private static String parseLengthForExpansion(Declaration declaration) {
        float multiplier;
        Term term = (Term)declaration.get(0);
        if (!(term instanceof TermLength)) {
            log.debug("Not converting length for {}", (Object)declaration);
            return null;
        }
        TermLength termLength = (TermLength)term;
        switch (termLength.getUnit()) {
            case px: {
                multiplier = 1.0f;
                break;
            }
            case in: {
                multiplier = 96.0f;
                break;
            }
            case pt: {
                multiplier = 1.3333334f;
                break;
            }
            case cm: {
                multiplier = 37.795277f;
                break;
            }
            case mm: {
                multiplier = 3.7795277f;
                break;
            }
            case pc: {
                multiplier = 16.0f;
                break;
            }
            default: {
                multiplier = 0.0f;
                log.debug("Not converting length for {}", (Object)declaration);
            }
        }
        return multiplier > 0.0f ? Botocss.formatNumber(((Float)termLength.getValue()).floatValue() * multiplier) : null;
    }

    private static String parseColorForExpansion(Declaration declaration) {
        Term term = (Term)declaration.get(0);
        if (!(term instanceof TermColor)) {
            log.debug("Not converting color for {}", (Object)declaration);
            return null;
        }
        return Botocss.formatColor((TermColor)term);
    }

    static {
        EXPANDABLE_ATTRS.put("width", new BotocssExpansion("width", Botocss::parseLengthForExpansion));
        EXPANDABLE_ATTRS.put("height", new BotocssExpansion("height", Botocss::parseLengthForExpansion));
        EXPANDABLE_ATTRS.put("background-color", new BotocssExpansion("bgcolor", Botocss::parseColorForExpansion));
    }
}

