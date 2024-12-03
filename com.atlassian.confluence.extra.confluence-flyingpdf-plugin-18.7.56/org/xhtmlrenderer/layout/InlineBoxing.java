/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.css.style.derived.BorderPropertySet;
import org.xhtmlrenderer.css.style.derived.RectPropertySet;
import org.xhtmlrenderer.layout.FloatLayoutResult;
import org.xhtmlrenderer.layout.FunctionData;
import org.xhtmlrenderer.layout.InlineBoxMeasurements;
import org.xhtmlrenderer.layout.Layer;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.LayoutUtil;
import org.xhtmlrenderer.layout.LineBreakContext;
import org.xhtmlrenderer.layout.Styleable;
import org.xhtmlrenderer.layout.TextUtil;
import org.xhtmlrenderer.layout.VerticalAlignContext;
import org.xhtmlrenderer.layout.breaker.Breaker;
import org.xhtmlrenderer.render.AnonymousBlockBox;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.FSFontMetrics;
import org.xhtmlrenderer.render.FloatDistances;
import org.xhtmlrenderer.render.InlineBox;
import org.xhtmlrenderer.render.InlineLayoutBox;
import org.xhtmlrenderer.render.InlineText;
import org.xhtmlrenderer.render.LineBox;
import org.xhtmlrenderer.render.MarkerData;
import org.xhtmlrenderer.render.StrutMetrics;
import org.xhtmlrenderer.render.TextDecoration;
import org.xhtmlrenderer.util.XRRuntimeException;

public class InlineBoxing {
    private static final int MAX_ITERATION_COUNT = 100000;

    private InlineBoxing() {
    }

    public static void layoutContent(LayoutContext c, BlockBox box, int initialY, int breakAtLine) {
        int maxAvailableWidth;
        int remainingWidth = maxAvailableWidth = box.getContentWidth();
        LineBox currentLine = InlineBoxing.newLine(c, initialY, (Box)box);
        LineBox previousLine = null;
        InlineLayoutBox currentIB = null;
        InlineLayoutBox previousIB = null;
        int contentStart = 0;
        ArrayList<InlineBox> openInlineBoxes = null;
        HashMap<InlineBox, InlineLayoutBox> iBMap = new HashMap<InlineBox, InlineLayoutBox>();
        if (box instanceof AnonymousBlockBox && (openInlineBoxes = ((AnonymousBlockBox)box).getOpenInlineBoxes()) != null) {
            openInlineBoxes = new ArrayList(openInlineBoxes);
            currentIB = InlineBoxing.addOpenInlineBoxes(c, currentLine, openInlineBoxes, maxAvailableWidth, iBMap);
        }
        if (openInlineBoxes == null) {
            openInlineBoxes = new ArrayList<InlineBox>();
        }
        remainingWidth -= c.getBlockFormattingContext().getFloatDistance(c, currentLine, remainingWidth);
        CalculatedStyle parentStyle = box.getStyle();
        int minimumLineHeight = (int)parentStyle.getLineHeight(c);
        int indent = (int)parentStyle.getFloatPropertyProportionalWidth(CSSName.TEXT_INDENT, maxAvailableWidth, c);
        remainingWidth -= indent;
        contentStart += indent;
        MarkerData markerData = c.getCurrentMarkerData();
        if (markerData != null && box.getStyle().isListMarkerInside()) {
            remainingWidth -= markerData.getLayoutWidth();
            contentStart += markerData.getLayoutWidth();
        }
        c.setCurrentMarkerData(null);
        ArrayList pendingFloats = new ArrayList();
        int pendingLeftMBP = 0;
        int pendingRightMBP = 0;
        boolean hasFirstLinePEs = false;
        ArrayList<Layer> pendingInlineLayers = new ArrayList<Layer>();
        if (c.getFirstLinesTracker().hasStyles()) {
            box.styleText(c, c.getFirstLinesTracker().deriveAll(box.getStyle()));
            hasFirstLinePEs = true;
        }
        boolean needFirstLetter = c.getFirstLettersTracker().hasStyles();
        boolean zeroWidthInlineBlock = false;
        int lineOffset = 0;
        for (Styleable node : box.getInlineContent()) {
            if (node.getStyle().isInline()) {
                InlineBox iB = (InlineBox)node;
                CalculatedStyle style = iB.getStyle();
                if (iB.isStartsHere()) {
                    previousIB = currentIB;
                    currentIB = new InlineLayoutBox(c, iB.getElement(), style, maxAvailableWidth);
                    openInlineBoxes.add(iB);
                    iBMap.put(iB, currentIB);
                    if (previousIB == null) {
                        currentLine.addChildForLayout(c, currentIB);
                    } else {
                        previousIB.addInlineChild(c, currentIB);
                    }
                    if (currentIB.getElement() != null) {
                        String id;
                        String name = c.getNamespaceHandler().getAnchorName(currentIB.getElement());
                        if (name != null) {
                            c.addBoxId(name, currentIB);
                        }
                        if ((id = c.getNamespaceHandler().getID(currentIB.getElement())) != null) {
                            c.addBoxId(id, currentIB);
                        }
                    }
                    pendingLeftMBP += style.getMarginBorderPadding(c, maxAvailableWidth, 1);
                    pendingRightMBP += style.getMarginBorderPadding(c, maxAvailableWidth, 2);
                }
                LineBreakContext lbContext = new LineBreakContext();
                lbContext.setMaster(iB.getText());
                lbContext.setTextNode(iB.getTextNode());
                if (iB.isDynamicFunction()) {
                    lbContext.setMaster(iB.getContentFunction().getLayoutReplacementText());
                }
                int q = 0;
                do {
                    if (q++ > 100000) {
                        throw new XRRuntimeException("Too many iterations (" + q + ") in InlineBoxing, giving up.");
                    }
                    lbContext.reset();
                    int fit = 0;
                    if (lbContext.getStart() == 0) {
                        fit += pendingLeftMBP + pendingRightMBP;
                    }
                    boolean trimmedLeadingSpace = false;
                    if (InlineBoxing.hasTrimmableLeadingSpace(currentLine, style, lbContext, zeroWidthInlineBlock)) {
                        trimmedLeadingSpace = true;
                        InlineBoxing.trimLeadingSpace(lbContext);
                    }
                    lbContext.setEndsOnNL(false);
                    zeroWidthInlineBlock = false;
                    if (lbContext.getStartSubstring().length() == 0) break;
                    if (needFirstLetter && !lbContext.isFinished()) {
                        InlineLayoutBox firstLetter = InlineBoxing.addFirstLetterBox(c, currentLine, currentIB, lbContext, maxAvailableWidth, remainingWidth);
                        remainingWidth -= firstLetter.getInlineWidth();
                        if (currentIB.isStartsHere()) {
                            pendingLeftMBP -= currentIB.getStyle().getMarginBorderPadding(c, maxAvailableWidth, 1);
                        }
                        needFirstLetter = false;
                    } else {
                        int delta;
                        lbContext.saveEnd();
                        InlineText inlineText = InlineBoxing.layoutText(c, iB.getStyle(), remainingWidth - fit, lbContext, false);
                        if (lbContext.isUnbreakable() && !currentLine.isContainsContent() && (delta = c.getBlockFormattingContext().getNextLineBoxDelta(c, currentLine, maxAvailableWidth)) > 0) {
                            currentLine.setY(currentLine.getY() + delta);
                            currentLine.calcCanvasLocation();
                            remainingWidth = maxAvailableWidth;
                            remainingWidth -= c.getBlockFormattingContext().getFloatDistance(c, currentLine, maxAvailableWidth);
                            lbContext.resetEnd();
                            continue;
                        }
                        if (!lbContext.isUnbreakable() || lbContext.isUnbreakable() && !currentLine.isContainsContent()) {
                            if (iB.isDynamicFunction()) {
                                inlineText.setFunctionData(new FunctionData(iB.getContentFunction(), iB.getFunction()));
                            }
                            inlineText.setTrimmedLeadingSpace(trimmedLeadingSpace);
                            currentLine.setContainsDynamicFunction(inlineText.isDynamicFunction());
                            currentIB.addInlineChild(c, inlineText);
                            currentLine.setContainsContent(true);
                            lbContext.setStart(lbContext.getEnd());
                            remainingWidth -= inlineText.getWidth();
                            if (currentIB.isStartsHere()) {
                                int marginBorderPadding = currentIB.getStyle().getMarginBorderPadding(c, maxAvailableWidth, 1);
                                pendingLeftMBP -= marginBorderPadding;
                                remainingWidth -= marginBorderPadding;
                            }
                        } else {
                            lbContext.resetEnd();
                        }
                    }
                    if (!lbContext.isNeedsNewLine()) continue;
                    if (iB.getStyle().isTextJustify()) {
                        currentLine.trimTrailingSpace(c);
                    }
                    InlineBoxing.saveLine(currentLine, c, box, minimumLineHeight, maxAvailableWidth, pendingFloats, hasFirstLinePEs, pendingInlineLayers, markerData, contentStart, InlineBoxing.isAlwaysBreak(c, box, breakAtLine, lineOffset));
                    ++lineOffset;
                    markerData = null;
                    contentStart = 0;
                    if (currentLine.isFirstLine() && hasFirstLinePEs) {
                        lbContext.setMaster(TextUtil.transformText(iB.getText(), iB.getStyle()));
                    }
                    if (lbContext.isEndsOnNL()) {
                        currentLine.setEndsOnNL(true);
                    }
                    previousIB = (currentIB = InlineBoxing.addOpenInlineBoxes(c, currentLine = InlineBoxing.newLine(c, previousLine = currentLine, (Box)box), openInlineBoxes, maxAvailableWidth, iBMap)).getParent() instanceof LineBox ? null : (InlineLayoutBox)currentIB.getParent();
                    remainingWidth = maxAvailableWidth;
                    remainingWidth -= c.getBlockFormattingContext().getFloatDistance(c, currentLine, remainingWidth);
                } while (!lbContext.isFinished());
                if (!iB.isEndsHere()) continue;
                int rightMBP = style.getMarginBorderPadding(c, maxAvailableWidth, 2);
                pendingRightMBP -= rightMBP;
                remainingWidth -= rightMBP;
                openInlineBoxes.remove(openInlineBoxes.size() - 1);
                if (currentIB.isPending()) {
                    currentIB.unmarkPending(c);
                    currentIB.setStartsHere(iB.isStartsHere());
                }
                currentIB.setEndsHere(true);
                if (currentIB.getStyle().requiresLayer()) {
                    if (!(currentIB.isPending() || currentIB.getElement() != null && currentIB.getElement() == c.getLayer().getMaster().getElement())) {
                        throw new RuntimeException("internal error");
                    }
                    if (!currentIB.isPending()) {
                        c.getLayer().setEnd(currentIB);
                        c.popLayer();
                        pendingInlineLayers.add(currentIB.getContainingLayer());
                    }
                }
                previousIB = currentIB;
                currentIB = currentIB.getParent() instanceof LineBox ? null : (InlineLayoutBox)currentIB.getParent();
                continue;
            }
            BlockBox child = (BlockBox)node;
            if (child.getStyle().isNonFlowContent()) {
                remainingWidth -= InlineBoxing.processOutOfFlowContent(c, currentLine, child, remainingWidth, pendingFloats);
                continue;
            }
            if (!child.getStyle().isInlineBlock() && !child.getStyle().isInlineTable()) continue;
            InlineBoxing.layoutInlineBlockContent(c, box, child, initialY);
            if (child.getWidth() > remainingWidth && currentLine.isContainsContent()) {
                InlineBoxing.saveLine(currentLine, c, box, minimumLineHeight, maxAvailableWidth, pendingFloats, hasFirstLinePEs, pendingInlineLayers, markerData, contentStart, InlineBoxing.isAlwaysBreak(c, box, breakAtLine, lineOffset));
                ++lineOffset;
                markerData = null;
                contentStart = 0;
                previousLine = currentLine;
                currentLine = InlineBoxing.newLine(c, previousLine, (Box)box);
                currentIB = InlineBoxing.addOpenInlineBoxes(c, currentLine, openInlineBoxes, maxAvailableWidth, iBMap);
                previousIB = currentIB == null || currentIB.getParent() instanceof LineBox ? null : (InlineLayoutBox)currentIB.getParent();
                remainingWidth = maxAvailableWidth;
                remainingWidth -= c.getBlockFormattingContext().getFloatDistance(c, currentLine, remainingWidth);
                child.reset(c);
                InlineBoxing.layoutInlineBlockContent(c, box, child, initialY);
            }
            if (currentIB == null) {
                currentLine.addChildForLayout(c, child);
            } else {
                currentIB.addInlineChild(c, child);
            }
            currentLine.setContainsContent(true);
            currentLine.setContainsBlockLevelContent(true);
            remainingWidth -= child.getWidth();
            if (currentIB != null && currentIB.isStartsHere()) {
                pendingLeftMBP -= currentIB.getStyle().getMarginBorderPadding(c, maxAvailableWidth, 1);
            }
            needFirstLetter = false;
            if (child.getWidth() != 0) continue;
            zeroWidthInlineBlock = true;
        }
        currentLine.trimTrailingSpace(c);
        InlineBoxing.saveLine(currentLine, c, box, minimumLineHeight, maxAvailableWidth, pendingFloats, hasFirstLinePEs, pendingInlineLayers, markerData, contentStart, InlineBoxing.isAlwaysBreak(c, box, breakAtLine, lineOffset));
        if (currentLine.isFirstLine() && currentLine.getHeight() == 0 && markerData != null) {
            c.setCurrentMarkerData(markerData);
        }
        markerData = null;
        box.setContentWidth(maxAvailableWidth);
        box.setHeight(currentLine.getY() + currentLine.getHeight());
    }

    private static boolean isAlwaysBreak(LayoutContext c, BlockBox parent, int breakAtLine, int lineOffset) {
        if (parent.isCurrentBreakAtLineContext(c)) {
            return lineOffset == breakAtLine;
        }
        return breakAtLine > 0 && lineOffset == breakAtLine;
    }

    private static InlineLayoutBox addFirstLetterBox(LayoutContext c, LineBox current, InlineLayoutBox currentIB, LineBreakContext lbContext, int maxAvailableWidth, int remainingWidth) {
        CalculatedStyle previous = currentIB.getStyle();
        currentIB.setStyle(c.getFirstLettersTracker().deriveAll(currentIB.getStyle()));
        InlineLayoutBox iB = new InlineLayoutBox(c, null, currentIB.getStyle(), maxAvailableWidth);
        iB.setStartsHere(true);
        iB.setEndsHere(true);
        currentIB.addInlineChild(c, iB);
        current.setContainsContent(true);
        InlineText text = InlineBoxing.layoutText(c, iB.getStyle(), remainingWidth, lbContext, true);
        iB.addInlineChild(c, text);
        iB.setInlineWidth(text.getWidth());
        lbContext.setStart(lbContext.getEnd());
        c.getFirstLettersTracker().clearStyles();
        currentIB.setStyle(previous);
        return iB;
    }

    private static void layoutInlineBlockContent(LayoutContext c, BlockBox containingBlock, BlockBox inlineBlock, int initialY) {
        inlineBlock.setContainingBlock(containingBlock);
        inlineBlock.setContainingLayer(c.getLayer());
        inlineBlock.initStaticPos(c, containingBlock, initialY);
        inlineBlock.calcCanvasLocation();
        inlineBlock.layout(c);
    }

    public static int positionHorizontally(CssContext c, Box current, int start) {
        int x = start;
        InlineLayoutBox currentIB = null;
        if (current instanceof InlineLayoutBox) {
            currentIB = (InlineLayoutBox)current;
            x += currentIB.getLeftMarginBorderPadding(c);
        }
        for (int i = 0; i < current.getChildCount(); ++i) {
            Box b = current.getChild(i);
            if (b instanceof InlineLayoutBox) {
                InlineLayoutBox iB = (InlineLayoutBox)current.getChild(i);
                iB.setX(x);
                x += InlineBoxing.positionHorizontally(c, iB, x);
                continue;
            }
            b.setX(x);
            x += b.getWidth();
        }
        if (currentIB != null) {
            currentIB.setInlineWidth((x += currentIB.getRightMarginPaddingBorder(c)) - start);
        }
        return x - start;
    }

    private static int positionHorizontally(CssContext c, InlineLayoutBox current, int start) {
        int x = start;
        x += current.getLeftMarginBorderPadding(c);
        for (int i = 0; i < current.getInlineChildCount(); ++i) {
            Object child = current.getInlineChild(i);
            if (child instanceof InlineLayoutBox) {
                InlineLayoutBox iB = (InlineLayoutBox)child;
                iB.setX(x);
                x += InlineBoxing.positionHorizontally(c, iB, x);
                continue;
            }
            if (child instanceof InlineText) {
                InlineText iT = (InlineText)child;
                iT.setX(x - start);
                x += iT.getWidth();
                continue;
            }
            if (!(child instanceof Box)) continue;
            Box b = (Box)child;
            b.setX(x);
            x += b.getWidth();
        }
        current.setInlineWidth((x += current.getRightMarginPaddingBorder(c)) - start);
        return x - start;
    }

    public static StrutMetrics createDefaultStrutMetrics(LayoutContext c, Box container) {
        FSFontMetrics strutM = container.getStyle().getFSFontMetrics(c);
        InlineBoxMeasurements measurements = InlineBoxing.getInitialMeasurements(c, container, strutM);
        return new StrutMetrics(strutM.getAscent(), measurements.getBaseline(), strutM.getDescent());
    }

    private static void positionVertically(LayoutContext c, Box container, LineBox current, MarkerData markerData) {
        if (current.getChildCount() == 0 || !current.isContainsVisibleContent()) {
            current.setHeight(0);
        } else {
            FSFontMetrics strutM = container.getStyle().getFSFontMetrics(c);
            VerticalAlignContext vaContext = new VerticalAlignContext();
            InlineBoxMeasurements measurements = InlineBoxing.getInitialMeasurements(c, container, strutM);
            vaContext.setInitialMeasurements(measurements);
            List lBDecorations = InlineBoxing.calculateTextDecorations(container, measurements.getBaseline(), strutM);
            if (lBDecorations != null) {
                current.setTextDecorations(lBDecorations);
            }
            for (int i = 0; i < current.getChildCount(); ++i) {
                Box child = current.getChild(i);
                InlineBoxing.positionInlineContentVertically(c, vaContext, child);
            }
            vaContext.alignChildren();
            current.setHeight(vaContext.getLineBoxHeight());
            int paintingTop = vaContext.getPaintingTop();
            int paintingBottom = vaContext.getPaintingBottom();
            if (vaContext.getInlineTop() < 0) {
                InlineBoxing.moveLineContents(current, -vaContext.getInlineTop());
                if (lBDecorations != null) {
                    for (TextDecoration lBDecoration : lBDecorations) {
                        lBDecoration.setOffset(lBDecoration.getOffset() - vaContext.getInlineTop());
                    }
                }
                paintingTop -= vaContext.getInlineTop();
                paintingBottom -= vaContext.getInlineTop();
            }
            if (markerData != null) {
                StrutMetrics strutMetrics = markerData.getStructMetrics();
                strutMetrics.setBaseline(measurements.getBaseline() - vaContext.getInlineTop());
                markerData.setReferenceLine(current);
                current.setMarkerData(markerData);
            }
            current.setBaseline(measurements.getBaseline() - vaContext.getInlineTop());
            current.setPaintingTop(paintingTop);
            current.setPaintingHeight(paintingBottom - paintingTop);
        }
    }

    private static void positionInlineVertically(LayoutContext c, VerticalAlignContext vaContext, InlineLayoutBox iB) {
        InlineBoxMeasurements iBMeasurements = InlineBoxing.calculateInlineMeasurements(c, iB, vaContext);
        vaContext.pushMeasurements(iBMeasurements);
        InlineBoxing.positionInlineChildrenVertically(c, iB, vaContext);
        vaContext.popMeasurements();
    }

    private static void positionInlineBlockVertically(LayoutContext c, VerticalAlignContext vaContext, BlockBox inlineBlock) {
        int baseline;
        int ascent = baseline = inlineBlock.calcInlineBaseline(c);
        int descent = inlineBlock.getHeight() - baseline;
        InlineBoxing.alignInlineContent(c, inlineBlock, ascent, descent, vaContext);
        vaContext.updateInlineTop(inlineBlock.getY());
        vaContext.updatePaintingTop(inlineBlock.getY());
        vaContext.updateInlineBottom(inlineBlock.getY() + inlineBlock.getHeight());
        vaContext.updatePaintingBottom(inlineBlock.getY() + inlineBlock.getHeight());
    }

    private static void moveLineContents(LineBox current, int ty) {
        for (int i = 0; i < current.getChildCount(); ++i) {
            Box child = current.getChild(i);
            child.setY(child.getY() + ty);
            if (!(child instanceof InlineLayoutBox)) continue;
            InlineBoxing.moveInlineContents((InlineLayoutBox)child, ty);
        }
    }

    private static void moveInlineContents(InlineLayoutBox box, int ty) {
        for (int i = 0; i < box.getInlineChildCount(); ++i) {
            Object obj = box.getInlineChild(i);
            if (!(obj instanceof Box)) continue;
            ((Box)obj).setY(((Box)obj).getY() + ty);
            if (!(obj instanceof InlineLayoutBox)) continue;
            InlineBoxing.moveInlineContents((InlineLayoutBox)obj, ty);
        }
    }

    private static InlineBoxMeasurements calculateInlineMeasurements(LayoutContext c, InlineLayoutBox iB, VerticalAlignContext vaContext) {
        FSFontMetrics fm = iB.getStyle().getFSFontMetrics(c);
        CalculatedStyle style = iB.getStyle();
        float lineHeight = style.getLineHeight(c);
        int halfLeading = Math.round((lineHeight - iB.getStyle().getFont((CssContext)c).size) / 2.0f);
        if (halfLeading > 0) {
            halfLeading = Math.round((lineHeight - (fm.getDescent() + fm.getAscent())) / 2.0f);
        }
        iB.setBaseline(Math.round(fm.getAscent()));
        InlineBoxing.alignInlineContent(c, iB, fm.getAscent(), fm.getDescent(), vaContext);
        List decorations = InlineBoxing.calculateTextDecorations(iB, iB.getBaseline(), fm);
        if (decorations != null) {
            iB.setTextDecorations(decorations);
        }
        InlineBoxMeasurements result = new InlineBoxMeasurements();
        result.setBaseline(iB.getY() + iB.getBaseline());
        result.setInlineTop(iB.getY() - halfLeading);
        result.setInlineBottom(Math.round((float)result.getInlineTop() + lineHeight));
        result.setTextTop(iB.getY());
        result.setTextBottom((int)((float)result.getBaseline() + fm.getDescent()));
        RectPropertySet padding = iB.getPadding(c);
        BorderPropertySet border = iB.getBorder(c);
        result.setPaintingTop((int)Math.floor((float)iB.getY() - border.top() - padding.top()));
        result.setPaintingBottom((int)Math.ceil((float)iB.getY() + fm.getAscent() + fm.getDescent() + border.bottom() + padding.bottom()));
        return result;
    }

    public static List calculateTextDecorations(Box box, int baseline, FSFontMetrics fm) {
        ArrayList<TextDecoration> result = null;
        CalculatedStyle style = box.getStyle();
        List idents = style.getTextDecorations();
        if (idents != null) {
            TextDecoration decoration;
            result = new ArrayList<TextDecoration>(idents.size());
            if (idents.contains(IdentValue.UNDERLINE)) {
                decoration = new TextDecoration(IdentValue.UNDERLINE);
                if (fm.getUnderlineOffset() == 0.0f) {
                    decoration.setOffset(Math.round((float)baseline + fm.getUnderlineThickness()));
                } else {
                    decoration.setOffset(Math.round((float)baseline + fm.getUnderlineOffset()));
                }
                decoration.setThickness(Math.round(fm.getUnderlineThickness()));
                if (fm.getUnderlineOffset() == 0.0f) {
                    int maxOffset = baseline + (int)fm.getDescent() - decoration.getThickness();
                    if (decoration.getOffset() > maxOffset) {
                        decoration.setOffset(maxOffset);
                    }
                }
                result.add(decoration);
            }
            if (idents.contains(IdentValue.LINE_THROUGH)) {
                decoration = new TextDecoration(IdentValue.LINE_THROUGH);
                decoration.setOffset(Math.round((float)baseline + fm.getStrikethroughOffset()));
                decoration.setThickness(Math.round(fm.getStrikethroughThickness()));
                result.add(decoration);
            }
            if (idents.contains(IdentValue.OVERLINE)) {
                decoration = new TextDecoration(IdentValue.OVERLINE);
                decoration.setOffset(0);
                decoration.setThickness(Math.round(fm.getUnderlineThickness()));
                result.add(decoration);
            }
        }
        return result;
    }

    private static void alignInlineContent(LayoutContext c, Box box, float ascent, float descent, VerticalAlignContext vaContext) {
        InlineBoxMeasurements measurements = vaContext.getParentMeasurements();
        CalculatedStyle style = box.getStyle();
        if (style.isLength(CSSName.VERTICAL_ALIGN)) {
            box.setY((int)((float)measurements.getBaseline() - ascent - style.getFloatPropertyProportionalTo(CSSName.VERTICAL_ALIGN, style.getLineHeight(c), c)));
        } else {
            IdentValue vAlign = style.getIdent(CSSName.VERTICAL_ALIGN);
            if (vAlign == IdentValue.BASELINE) {
                box.setY(Math.round((float)measurements.getBaseline() - ascent));
            } else if (vAlign == IdentValue.TEXT_TOP) {
                box.setY(measurements.getTextTop());
            } else if (vAlign == IdentValue.TEXT_BOTTOM) {
                box.setY(Math.round((float)measurements.getTextBottom() - descent - ascent));
            } else if (vAlign == IdentValue.MIDDLE) {
                box.setY(Math.round((float)((measurements.getBaseline() - measurements.getTextTop()) / 2) - (ascent + descent) / 2.0f));
            } else if (vAlign == IdentValue.SUPER) {
                box.setY(Math.round((float)measurements.getBaseline() - 3.0f * ascent / 2.0f));
            } else if (vAlign == IdentValue.SUB) {
                box.setY(Math.round((float)measurements.getBaseline() - ascent / 2.0f));
            } else {
                box.setY(Math.round((float)measurements.getBaseline() - ascent));
            }
        }
    }

    private static InlineBoxMeasurements getInitialMeasurements(LayoutContext c, Box container, FSFontMetrics strutM) {
        float lineHeight = container.getStyle().getLineHeight(c);
        int halfLeading = Math.round((lineHeight - container.getStyle().getFont((CssContext)c).size) / 2.0f);
        if (halfLeading > 0) {
            halfLeading = Math.round((lineHeight - (strutM.getDescent() + strutM.getAscent())) / 2.0f);
        }
        InlineBoxMeasurements measurements = new InlineBoxMeasurements();
        measurements.setBaseline((int)((float)halfLeading + strutM.getAscent()));
        measurements.setTextTop(halfLeading);
        measurements.setTextBottom((int)((float)measurements.getBaseline() + strutM.getDescent()));
        measurements.setInlineTop(halfLeading);
        measurements.setInlineBottom((int)((float)halfLeading + lineHeight));
        return measurements;
    }

    private static void positionInlineChildrenVertically(LayoutContext c, InlineLayoutBox current, VerticalAlignContext vaContext) {
        for (int i = 0; i < current.getInlineChildCount(); ++i) {
            Object child = current.getInlineChild(i);
            if (!(child instanceof Box)) continue;
            InlineBoxing.positionInlineContentVertically(c, vaContext, (Box)child);
        }
    }

    private static void positionInlineContentVertically(LayoutContext c, VerticalAlignContext vaContext, Box child) {
        IdentValue vAlign;
        VerticalAlignContext vaTarget = vaContext;
        if (!(child.getStyle().isLength(CSSName.VERTICAL_ALIGN) || (vAlign = child.getStyle().getIdent(CSSName.VERTICAL_ALIGN)) != IdentValue.TOP && vAlign != IdentValue.BOTTOM)) {
            vaTarget = vaContext.createChild(child);
        }
        if (child instanceof InlineLayoutBox) {
            InlineLayoutBox iB = (InlineLayoutBox)child;
            InlineBoxing.positionInlineVertically(c, vaTarget, iB);
        } else {
            InlineBoxing.positionInlineBlockVertically(c, vaTarget, (BlockBox)child);
        }
    }

    private static void saveLine(LineBox current, LayoutContext c, BlockBox block, int minHeight, int maxAvailableWidth, List pendingFloats, boolean hasFirstLinePCs, List pendingInlineLayers, MarkerData markerData, int contentStart, boolean alwaysBreak) {
        current.setContentStart(contentStart);
        current.prunePendingInlineBoxes();
        int totalLineWidth = InlineBoxing.positionHorizontally((CssContext)c, current, 0);
        current.setContentWidth(totalLineWidth);
        InlineBoxing.positionVertically(c, block, current, markerData);
        if (current.getHeight() != 0 && current.getHeight() < minHeight && !current.isContainsOnlyBlockLevelContent()) {
            current.setHeight(minHeight);
        }
        if (c.isPrint()) {
            current.checkPagePosition(c, alwaysBreak);
        }
        InlineBoxing.alignLine(c, current, maxAvailableWidth);
        current.calcChildLocations();
        block.addChildForLayout(c, current);
        if (pendingInlineLayers.size() > 0) {
            InlineBoxing.finishPendingInlineLayers(c, pendingInlineLayers);
            pendingInlineLayers.clear();
        }
        if (hasFirstLinePCs && current.isFirstLine()) {
            c.getFirstLinesTracker().clearStyles();
            block.styleText(c);
        }
        if (pendingFloats.size() > 0) {
            for (FloatLayoutResult layoutResult : pendingFloats) {
                LayoutUtil.layoutFloated(c, current, layoutResult.getBlock(), maxAvailableWidth, null);
                current.addNonFlowContent(layoutResult.getBlock());
            }
            pendingFloats.clear();
        }
    }

    private static void alignLine(final LayoutContext c, final LineBox current, final int maxAvailableWidth) {
        if (!current.isContainsDynamicFunction() && !current.getParent().getStyle().isTextJustify()) {
            current.setFloatDistances(new FloatDistances(){

                @Override
                public int getLeftFloatDistance() {
                    return c.getBlockFormattingContext().getLeftFloatDistance(c, current, maxAvailableWidth);
                }

                @Override
                public int getRightFloatDistance() {
                    return c.getBlockFormattingContext().getRightFloatDistance(c, current, maxAvailableWidth);
                }
            });
        } else {
            FloatDistances distances = new FloatDistances();
            distances.setLeftFloatDistance(c.getBlockFormattingContext().getLeftFloatDistance(c, current, maxAvailableWidth));
            distances.setRightFloatDistance(c.getBlockFormattingContext().getRightFloatDistance(c, current, maxAvailableWidth));
            current.setFloatDistances(distances);
        }
        current.align(false);
        if (!current.isContainsDynamicFunction() && !current.getParent().getStyle().isTextJustify()) {
            current.setFloatDistances(null);
        }
    }

    private static void finishPendingInlineLayers(LayoutContext c, List layers) {
        for (int i = 0; i < layers.size(); ++i) {
            Layer l = (Layer)layers.get(i);
            l.positionChildren(c);
        }
    }

    private static InlineText layoutText(LayoutContext c, CalculatedStyle style, int remainingWidth, LineBreakContext lbContext, boolean needFirstLetter) {
        InlineText result = new InlineText();
        String masterText = lbContext.getMaster();
        if (needFirstLetter) {
            masterText = TextUtil.transformFirstLetterText(masterText, style);
            lbContext.setMaster(masterText);
            Breaker.breakFirstLetter(c, lbContext, remainingWidth, style);
        } else {
            Breaker.breakText(c, lbContext, remainingWidth, style);
        }
        result.setMasterText(lbContext.getMaster());
        result.setTextNode(lbContext.getTextNode());
        result.setSubstring(lbContext.getStart(), lbContext.getEnd());
        result.setWidth(lbContext.getWidth());
        return result;
    }

    private static int processOutOfFlowContent(LayoutContext c, LineBox current, BlockBox block, int available, List pendingFloats) {
        int result = 0;
        CalculatedStyle style = block.getStyle();
        if (style.isAbsolute() || style.isFixed()) {
            LayoutUtil.layoutAbsolute(c, current, block);
            current.addNonFlowContent(block);
        } else if (style.isFloated()) {
            FloatLayoutResult layoutResult = LayoutUtil.layoutFloated(c, current, block, available, pendingFloats);
            if (layoutResult.isPending()) {
                pendingFloats.add(layoutResult);
            } else {
                result = layoutResult.getBlock().getWidth();
                current.addNonFlowContent(layoutResult.getBlock());
            }
        } else if (style.isRunning()) {
            block.setStaticEquivalent(current);
            c.getRootLayer().addRunningBlock(block);
        }
        return result;
    }

    private static boolean hasTrimmableLeadingSpace(LineBox line, CalculatedStyle style, LineBreakContext lbContext, boolean zeroWidthInlineBlock) {
        IdentValue whitespace;
        return (!line.isContainsContent() || zeroWidthInlineBlock) && lbContext.getStartSubstring().startsWith(" ") && ((whitespace = style.getWhitespace()) == IdentValue.NORMAL || whitespace == IdentValue.NOWRAP || whitespace == IdentValue.PRE_LINE || whitespace == IdentValue.PRE_WRAP && lbContext.getStart() > 0 && lbContext.getMaster().length() > lbContext.getStart() - 1 && lbContext.getMaster().charAt(lbContext.getStart() - 1) != '\n');
    }

    private static void trimLeadingSpace(LineBreakContext lbContext) {
        int i;
        String s = lbContext.getStartSubstring();
        for (i = 0; i < s.length() && s.charAt(i) == ' '; ++i) {
        }
        lbContext.setStart(lbContext.getStart() + i);
    }

    private static LineBox newLine(LayoutContext c, LineBox previousLine, Box box) {
        int y = 0;
        if (previousLine != null) {
            y = previousLine.getY() + previousLine.getHeight();
        }
        return InlineBoxing.newLine(c, y, box);
    }

    private static LineBox newLine(LayoutContext c, int y, Box box) {
        LineBox result = new LineBox();
        result.setStyle(box.getStyle().createAnonymousStyle(IdentValue.BLOCK));
        result.setParent(box);
        result.initContainingLayer(c);
        result.setY(y);
        result.calcCanvasLocation();
        return result;
    }

    private static InlineLayoutBox addOpenInlineBoxes(LayoutContext c, LineBox line, List openParents, int cbWidth, Map iBMap) {
        ArrayList<InlineBox> result = new ArrayList<InlineBox>();
        InlineLayoutBox currentIB = null;
        InlineLayoutBox previousIB = null;
        boolean first = true;
        for (InlineBox iB : openParents) {
            currentIB = new InlineLayoutBox(c, iB.getElement(), iB.getStyle(), cbWidth);
            InlineLayoutBox prev = (InlineLayoutBox)iBMap.get(iB);
            if (prev != null) {
                currentIB.setPending(prev.isPending());
            }
            iBMap.put(iB, currentIB);
            result.add(iB);
            if (first) {
                line.addChildForLayout(c, currentIB);
                first = false;
            } else {
                previousIB.addInlineChild(c, currentIB, false);
            }
            previousIB = currentIB;
        }
        return currentIB;
    }
}

