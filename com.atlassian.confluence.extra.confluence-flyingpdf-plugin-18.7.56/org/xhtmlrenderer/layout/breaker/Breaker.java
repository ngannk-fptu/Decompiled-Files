/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout.breaker;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.LineBreakContext;
import org.xhtmlrenderer.layout.TextUtil;
import org.xhtmlrenderer.layout.breaker.BreakAnywhereLineBreakStrategy;
import org.xhtmlrenderer.layout.breaker.BreakPoint;
import org.xhtmlrenderer.layout.breaker.BreakPointsProvider;
import org.xhtmlrenderer.render.FSFont;

public class Breaker {
    private static final String DEFAULT_LANGUAGE = System.getProperty("org.xhtmlrenderer.layout.breaker.default-language", "en");

    public static void breakFirstLetter(LayoutContext c, LineBreakContext context, int avail, CalculatedStyle style) {
        FSFont font = style.getFSFont(c);
        context.setEnd(Breaker.getFirstLetterEnd(context.getMaster(), context.getStart()));
        context.setWidth(c.getTextRenderer().getWidth(c.getFontContext(), font, context.getCalculatedSubstring()));
        if (context.getWidth() > avail) {
            context.setNeedsNewLine(true);
            context.setUnbreakable(true);
        }
    }

    private static int getFirstLetterEnd(String text, int start) {
        boolean letterFound = false;
        int end = text.length();
        for (int i = start; i < end; ++i) {
            char currentChar = text.charAt(i);
            if (TextUtil.isFirstLetterSeparatorChar(currentChar)) continue;
            if (letterFound) {
                return i;
            }
            letterFound = true;
        }
        return end;
    }

    public static void breakText(LayoutContext c, LineBreakContext context, int avail, CalculatedStyle style) {
        FSFont font = style.getFSFont(c);
        IdentValue whitespace = style.getWhitespace();
        if (whitespace == IdentValue.NOWRAP) {
            context.setEnd(context.getLast());
            context.setWidth(c.getTextRenderer().getWidth(c.getFontContext(), font, context.getCalculatedSubstring()));
            return;
        }
        if (whitespace == IdentValue.PRE || whitespace == IdentValue.PRE_WRAP || whitespace == IdentValue.PRE_LINE) {
            int n = context.getStartSubstring().indexOf("\n");
            if (n > -1) {
                context.setEnd(context.getStart() + n + 1);
                context.setWidth(c.getTextRenderer().getWidth(c.getFontContext(), font, context.getCalculatedSubstring()));
                context.setNeedsNewLine(true);
                context.setEndsOnNL(true);
            } else if (whitespace == IdentValue.PRE) {
                context.setEnd(context.getLast());
                context.setWidth(c.getTextRenderer().getWidth(c.getFontContext(), font, context.getCalculatedSubstring()));
            }
        }
        if (whitespace == IdentValue.PRE || context.isNeedsNewLine() && context.getWidth() <= avail) {
            return;
        }
        context.setEndsOnNL(false);
        Breaker.doBreakText(c, context, avail, style, false);
    }

    private static int getWidth(LayoutContext c, FSFont f, String text) {
        return c.getTextRenderer().getWidth(c.getFontContext(), f, text);
    }

    public static BreakPointsProvider getBreakPointsProvider(String text, LayoutContext c, Element element, CalculatedStyle style) {
        return c.getSharedContext().getLineBreakingStrategy().getBreakPointsProvider(text, Breaker.getLanguage(c, element), style);
    }

    public static BreakPointsProvider getBreakPointsProvider(String text, LayoutContext c, Text textNode, CalculatedStyle style) {
        return c.getSharedContext().getLineBreakingStrategy().getBreakPointsProvider(text, Breaker.getLanguage(c, textNode), style);
    }

    private static String getLanguage(LayoutContext c, Element element) {
        String language = c.getNamespaceHandler().getLang(element);
        if (language == null || language.isEmpty()) {
            language = DEFAULT_LANGUAGE;
        }
        return language;
    }

    private static String getLanguage(LayoutContext c, Text textNode) {
        Node parentNode;
        if (textNode != null && (parentNode = textNode.getParentNode()) instanceof Element) {
            return Breaker.getLanguage(c, (Element)parentNode);
        }
        return DEFAULT_LANGUAGE;
    }

    private static void doBreakText(LayoutContext c, LineBreakContext context, int avail, CalculatedStyle style, boolean tryToBreakAnywhere) {
        FSFont f = style.getFSFont(c);
        String currentString = context.getStartSubstring();
        BreakPointsProvider iterator = Breaker.getBreakPointsProvider(currentString, c, context.getTextNode(), style);
        if (tryToBreakAnywhere) {
            iterator = new BreakAnywhereLineBreakStrategy(currentString);
        }
        BreakPoint bp = iterator.next();
        BreakPoint lastBreakPoint = null;
        int right = -1;
        int previousWidth = 0;
        int previousPosition = 0;
        while (bp != null && bp.getPosition() != -1) {
            int widthWithHyphen;
            int currentWidth = Breaker.getWidth(c, f, currentString.substring(previousPosition, bp.getPosition()) + bp.getHyphen());
            previousWidth = widthWithHyphen = previousWidth + currentWidth;
            previousPosition = bp.getPosition();
            if (widthWithHyphen > avail) break;
            right = previousPosition;
            lastBreakPoint = bp;
            bp = iterator.next();
        }
        if (bp != null && bp.getPosition() != -1 && right >= 0 && !lastBreakPoint.getHyphen().isEmpty()) {
            context.setMaster(new StringBuilder(context.getMaster()).insert(context.getStart() + right, lastBreakPoint.getHyphen()).toString());
            right += lastBreakPoint.getHyphen().length();
        }
        if (bp != null && bp.getPosition() == -1) {
            context.setWidth(Breaker.getWidth(c, f, currentString));
            context.setEnd(context.getMaster().length());
            return;
        }
        context.setNeedsNewLine(true);
        if (right <= 0 && style.getWordWrap() == IdentValue.BREAK_WORD && !tryToBreakAnywhere) {
            Breaker.doBreakText(c, context, avail, style, true);
            return;
        }
        if (right > 0) {
            context.setEnd(context.getStart() + right);
            context.setWidth(Breaker.getWidth(c, f, context.getMaster().substring(context.getStart(), context.getStart() + right)));
            return;
        }
        context.setEnd(context.getStart() + currentString.length());
        context.setUnbreakable(true);
        context.setWidth(Breaker.getWidth(c, f, context.getCalculatedSubstring()));
    }
}

