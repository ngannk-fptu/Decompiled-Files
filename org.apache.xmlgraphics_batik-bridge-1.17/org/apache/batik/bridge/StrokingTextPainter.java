/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.font.GVTFont
 *  org.apache.batik.gvt.font.GVTFontFamily
 *  org.apache.batik.gvt.font.GVTGlyphMetrics
 *  org.apache.batik.gvt.font.GVTLineMetrics
 *  org.apache.batik.gvt.text.AttributedCharacterSpanIterator
 *  org.apache.batik.gvt.text.BidiAttributedCharacterIterator
 *  org.apache.batik.gvt.text.GVTAttributedCharacterIterator$TextAttribute
 *  org.apache.batik.gvt.text.TextPaintInfo
 *  org.apache.batik.gvt.text.TextPath
 */
package org.apache.batik.bridge;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.batik.bridge.BasicTextPainter;
import org.apache.batik.bridge.DefaultFontFamilyResolver;
import org.apache.batik.bridge.FontFamilyResolver;
import org.apache.batik.bridge.Mark;
import org.apache.batik.bridge.TextHit;
import org.apache.batik.bridge.TextNode;
import org.apache.batik.bridge.TextPainter;
import org.apache.batik.bridge.TextSpanLayout;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTFontFamily;
import org.apache.batik.gvt.font.GVTGlyphMetrics;
import org.apache.batik.gvt.font.GVTLineMetrics;
import org.apache.batik.gvt.text.AttributedCharacterSpanIterator;
import org.apache.batik.gvt.text.BidiAttributedCharacterIterator;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.TextPaintInfo;
import org.apache.batik.gvt.text.TextPath;

public class StrokingTextPainter
extends BasicTextPainter {
    public static final AttributedCharacterIterator.Attribute PAINT_INFO = GVTAttributedCharacterIterator.TextAttribute.PAINT_INFO;
    public static final AttributedCharacterIterator.Attribute FLOW_REGIONS = GVTAttributedCharacterIterator.TextAttribute.FLOW_REGIONS;
    public static final AttributedCharacterIterator.Attribute FLOW_PARAGRAPH = GVTAttributedCharacterIterator.TextAttribute.FLOW_PARAGRAPH;
    public static final AttributedCharacterIterator.Attribute TEXT_COMPOUND_ID = GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_ID;
    public static final AttributedCharacterIterator.Attribute GVT_FONT = GVTAttributedCharacterIterator.TextAttribute.GVT_FONT;
    public static final AttributedCharacterIterator.Attribute GVT_FONTS = GVTAttributedCharacterIterator.TextAttribute.GVT_FONTS;
    public static final AttributedCharacterIterator.Attribute BIDI_LEVEL = GVTAttributedCharacterIterator.TextAttribute.BIDI_LEVEL;
    public static final AttributedCharacterIterator.Attribute XPOS = GVTAttributedCharacterIterator.TextAttribute.X;
    public static final AttributedCharacterIterator.Attribute YPOS = GVTAttributedCharacterIterator.TextAttribute.Y;
    public static final AttributedCharacterIterator.Attribute TEXTPATH = GVTAttributedCharacterIterator.TextAttribute.TEXTPATH;
    public static final AttributedCharacterIterator.Attribute WRITING_MODE = GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE;
    public static final Integer WRITING_MODE_TTB = GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE_TTB;
    public static final Integer WRITING_MODE_RTL = GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE_RTL;
    public static final AttributedCharacterIterator.Attribute ANCHOR_TYPE = GVTAttributedCharacterIterator.TextAttribute.ANCHOR_TYPE;
    public static final Integer ADJUST_SPACING = GVTAttributedCharacterIterator.TextAttribute.ADJUST_SPACING;
    public static final Integer ADJUST_ALL = GVTAttributedCharacterIterator.TextAttribute.ADJUST_ALL;
    public static final GVTAttributedCharacterIterator.TextAttribute ALT_GLYPH_HANDLER = GVTAttributedCharacterIterator.TextAttribute.ALT_GLYPH_HANDLER;
    static Set extendedAtts = new HashSet();
    protected static TextPainter singleton;

    public static TextPainter getInstance() {
        return singleton;
    }

    @Override
    public void paint(TextNode node, Graphics2D g2d) {
        AttributedCharacterIterator aci = node.getAttributedCharacterIterator();
        if (aci == null) {
            return;
        }
        List textRuns = this.getTextRuns(node, aci);
        this.paintDecorations(textRuns, g2d, 1);
        this.paintDecorations(textRuns, g2d, 4);
        this.paintTextRuns(textRuns, g2d);
        this.paintDecorations(textRuns, g2d, 2);
    }

    protected void printAttrs(AttributedCharacterIterator aci) {
        aci.first();
        int start = aci.getBeginIndex();
        System.out.print("AttrRuns: ");
        while (aci.current() != '\uffff') {
            int end = aci.getRunLimit();
            System.out.print("" + (end - start) + ", ");
            aci.setIndex(end);
            start = end;
        }
        System.out.println("");
    }

    public List getTextRuns(TextNode node, AttributedCharacterIterator aci) {
        List textRuns = node.getTextRuns();
        if (textRuns != null) {
            return textRuns;
        }
        AttributedCharacterIterator[] chunkACIs = this.getTextChunkACIs(aci);
        textRuns = this.computeTextRuns(node, aci, chunkACIs);
        node.setTextRuns(textRuns);
        return node.getTextRuns();
    }

    public List computeTextRuns(TextNode node, AttributedCharacterIterator aci, AttributedCharacterIterator[] chunkACIs) {
        int[][] chunkCharMaps = new int[chunkACIs.length][];
        int chunkStart = aci.getBeginIndex();
        for (int i = 0; i < chunkACIs.length; ++i) {
            BidiAttributedCharacterIterator iter = new BidiAttributedCharacterIterator(chunkACIs[i], this.fontRenderContext, chunkStart);
            chunkACIs[i] = iter;
            chunkCharMaps[i] = iter.getCharMap();
            chunkStart += chunkACIs[i].getEndIndex() - chunkACIs[i].getBeginIndex();
        }
        return this.computeTextRuns(node, aci, chunkACIs, chunkCharMaps);
    }

    protected List computeTextRuns(TextNode node, AttributedCharacterIterator aci, AttributedCharacterIterator[] chunkACIs, int[][] chunkCharMaps) {
        TextChunk chunk;
        int chunkStart = aci.getBeginIndex();
        for (int i = 0; i < chunkACIs.length; ++i) {
            chunkACIs[i] = this.createModifiedACIForFontMatching(chunkACIs[i]);
            chunkStart += chunkACIs[i].getEndIndex() - chunkACIs[i].getBeginIndex();
        }
        ArrayList perNodeRuns = new ArrayList();
        TextChunk prevChunk = null;
        int currentChunk = 0;
        Point2D location = node.getLocation();
        do {
            chunkACIs[currentChunk].first();
            List perChunkRuns = new ArrayList();
            chunk = this.getTextChunk(node, chunkACIs[currentChunk], chunkCharMaps != null ? chunkCharMaps[currentChunk] : null, perChunkRuns, prevChunk);
            perChunkRuns = this.reorderTextRuns(chunk, perChunkRuns);
            chunkACIs[currentChunk].first();
            if (chunk != null) {
                location = this.adjustChunkOffsets(location, perChunkRuns, chunk);
            }
            perNodeRuns.addAll(perChunkRuns);
            prevChunk = chunk;
        } while (chunk != null && ++currentChunk < chunkACIs.length);
        return perNodeRuns;
    }

    protected List reorderTextRuns(TextChunk chunk, List runs) {
        return runs;
    }

    protected AttributedCharacterIterator[] getTextChunkACIs(AttributedCharacterIterator aci) {
        boolean vertical;
        ArrayList<AttributedCharacterSpanIterator> aciList = new ArrayList<AttributedCharacterSpanIterator>();
        int chunkStartIndex = aci.getBeginIndex();
        aci.first();
        Object writingMode = aci.getAttribute(WRITING_MODE);
        boolean bl = vertical = writingMode == WRITING_MODE_TTB;
        while (aci.setIndex(chunkStartIndex) != '\uffff') {
            TextPath prevTextPath = null;
            int start = chunkStartIndex;
            int end = 0;
            while (aci.setIndex(start) != '\uffff') {
                Float runX;
                Float runY;
                TextNode.Anchor anchor;
                TextPath textPath = (TextPath)aci.getAttribute(TEXTPATH);
                if (start != chunkStartIndex) {
                    Float runY2;
                    Float runX2;
                    if (!vertical ? (runX2 = (Float)aci.getAttribute(XPOS)) != null && !runX2.isNaN() : (runY2 = (Float)aci.getAttribute(YPOS)) != null && !runY2.isNaN()) break;
                    if (prevTextPath == null && textPath != null || prevTextPath != null && textPath == null) break;
                }
                prevTextPath = textPath;
                if (aci.getAttribute(FLOW_PARAGRAPH) != null) {
                    end = aci.getRunLimit(FLOW_PARAGRAPH);
                    aci.setIndex(end);
                    break;
                }
                end = aci.getRunLimit(TEXT_COMPOUND_ID);
                if (start == chunkStartIndex && (anchor = (TextNode.Anchor)aci.getAttribute(ANCHOR_TYPE)) != TextNode.Anchor.START && !(vertical ? (runY = (Float)aci.getAttribute(YPOS)) == null || runY.isNaN() : (runX = (Float)aci.getAttribute(XPOS)) == null || runX.isNaN())) {
                    int i = start + 1;
                    while (i < end) {
                        Float runX3;
                        Float runY3;
                        aci.setIndex(i);
                        if (vertical ? (runY3 = (Float)aci.getAttribute(YPOS)) == null || runY3.isNaN() : (runX3 = (Float)aci.getAttribute(XPOS)) == null || runX3.isNaN()) break;
                        aciList.add(new AttributedCharacterSpanIterator(aci, i - 1, i));
                        chunkStartIndex = i++;
                    }
                }
                start = end;
            }
            int chunkEndIndex = aci.getIndex();
            aciList.add(new AttributedCharacterSpanIterator(aci, chunkStartIndex, chunkEndIndex));
            chunkStartIndex = chunkEndIndex;
        }
        AttributedCharacterIterator[] aciArray = new AttributedCharacterIterator[aciList.size()];
        Iterator iter = aciList.iterator();
        int i = 0;
        while (iter.hasNext()) {
            aciArray[i] = (AttributedCharacterIterator)iter.next();
            ++i;
        }
        return aciArray;
    }

    protected AttributedCharacterIterator createModifiedACIForFontMatching(AttributedCharacterIterator aci) {
        aci.first();
        AttributedString as = null;
        int asOff = 0;
        int begin = aci.getBeginIndex();
        boolean moreChunks = true;
        int end = aci.getRunStart(TEXT_COMPOUND_ID);
        while (moreChunks) {
            int start = end;
            end = aci.getRunLimit(TEXT_COMPOUND_ID);
            int aciLength = end - start;
            List fonts = (List)aci.getAttribute(GVT_FONTS);
            float fontSize = 12.0f;
            Float fsFloat = (Float)aci.getAttribute(TextAttribute.SIZE);
            if (fsFloat != null) {
                fontSize = fsFloat.floatValue();
            }
            if (fonts.size() == 0) {
                fonts.add(this.getFontFamilyResolver().getDefault().deriveFont(fontSize, aci));
            }
            boolean[] fontAssigned = new boolean[aciLength];
            if (as == null) {
                as = new AttributedString(aci);
            }
            GVTFont defaultFont = null;
            int numSet = 0;
            int firstUnset = start;
            for (Object font1 : fonts) {
                int currentIndex = firstUnset;
                boolean firstUnsetSet = false;
                aci.setIndex(currentIndex);
                GVTFont font = (GVTFont)font1;
                if (defaultFont == null) {
                    defaultFont = font;
                }
                while (currentIndex < end) {
                    int displayUpToIndex = font.canDisplayUpTo((CharacterIterator)aci, currentIndex, end);
                    Object altGlyphElement = aci.getAttribute((AttributedCharacterIterator.Attribute)ALT_GLYPH_HANDLER);
                    if (altGlyphElement != null) {
                        displayUpToIndex = -1;
                    }
                    if (displayUpToIndex == -1) {
                        displayUpToIndex = end;
                    }
                    if (displayUpToIndex <= currentIndex) {
                        if (!firstUnsetSet) {
                            firstUnset = currentIndex;
                            firstUnsetSet = true;
                        }
                        ++currentIndex;
                        continue;
                    }
                    int runStart = -1;
                    for (int j = currentIndex; j < displayUpToIndex; ++j) {
                        if (fontAssigned[j - start]) {
                            if (runStart != -1) {
                                as.addAttribute(GVT_FONT, font, runStart - begin, j - begin);
                                runStart = -1;
                            }
                        } else if (runStart == -1) {
                            runStart = j;
                        }
                        fontAssigned[j - start] = true;
                        ++numSet;
                    }
                    if (runStart != -1) {
                        as.addAttribute(GVT_FONT, font, runStart - begin, displayUpToIndex - begin);
                    }
                    currentIndex = displayUpToIndex + 1;
                }
                if (numSet != aciLength) continue;
                break;
            }
            int runStart = -1;
            GVTFontFamily prevFF = null;
            GVTFont prevF = defaultFont;
            for (int i = 0; i < aciLength; ++i) {
                if (fontAssigned[i]) {
                    if (runStart == -1) continue;
                    as.addAttribute(GVT_FONT, prevF, runStart + asOff, i + asOff);
                    runStart = -1;
                    prevF = null;
                    prevFF = null;
                    continue;
                }
                char c = aci.setIndex(start + i);
                GVTFontFamily fontFamily = this.getFontFamilyResolver().getFamilyThatCanDisplay(c);
                if (runStart == -1) {
                    runStart = i;
                    prevFF = fontFamily;
                    if (prevFF == null) {
                        prevF = defaultFont;
                        continue;
                    }
                    prevF = fontFamily.deriveFont(fontSize, aci);
                    continue;
                }
                if (prevFF == fontFamily) continue;
                as.addAttribute(GVT_FONT, prevF, runStart + asOff, i + asOff);
                runStart = i;
                prevFF = fontFamily;
                prevF = prevFF == null ? defaultFont : fontFamily.deriveFont(fontSize, aci);
            }
            if (runStart != -1) {
                as.addAttribute(GVT_FONT, prevF, runStart + asOff, aciLength + asOff);
            }
            asOff += aciLength;
            if (aci.setIndex(end) == '\uffff') {
                moreChunks = false;
            }
            start = end;
        }
        if (as != null) {
            return as.getIterator();
        }
        return aci;
    }

    protected FontFamilyResolver getFontFamilyResolver() {
        return DefaultFontFamilyResolver.SINGLETON;
    }

    protected Set getTextRunBoundaryAttributes() {
        return extendedAtts;
    }

    protected TextChunk getTextChunk(TextNode node, AttributedCharacterIterator aci, int[] charMap, List textRuns, TextChunk prevChunk) {
        int beginChunk = 0;
        if (prevChunk != null) {
            beginChunk = prevChunk.end;
        }
        int endChunk = beginChunk;
        int begin = aci.getIndex();
        if (aci.current() == '\uffff') {
            return null;
        }
        Point2D.Float offset = new Point2D.Float(0.0f, 0.0f);
        Point2D.Float advance = new Point2D.Float(0.0f, 0.0f);
        boolean isChunkStart = true;
        TextSpanLayout layout = null;
        Set textRunBoundaryAttributes = this.getTextRunBoundaryAttributes();
        while (true) {
            int start = aci.getRunStart(textRunBoundaryAttributes);
            int end = aci.getRunLimit(textRunBoundaryAttributes);
            AttributedCharacterSpanIterator runaci = new AttributedCharacterSpanIterator(aci, start, end);
            int[] subCharMap = new int[end - start];
            if (charMap != null) {
                System.arraycopy(charMap, start - begin, subCharMap, 0, subCharMap.length);
            } else {
                int n = subCharMap.length;
                for (int i = 0; i < n; ++i) {
                    subCharMap[i] = i;
                }
            }
            FontRenderContext frc = this.fontRenderContext;
            RenderingHints rh = node.getRenderingHints();
            if (rh != null && rh.get(RenderingHints.KEY_TEXT_ANTIALIASING) == RenderingHints.VALUE_TEXT_ANTIALIAS_OFF) {
                frc = this.aaOffFontRenderContext;
            }
            layout = this.getTextLayoutFactory().createTextLayout((AttributedCharacterIterator)runaci, subCharMap, offset, frc);
            textRuns.add(new TextRun(layout, (AttributedCharacterIterator)runaci, isChunkStart));
            Point2D layoutAdvance = layout.getAdvance2D();
            advance.x += (float)layoutAdvance.getX();
            advance.y += (float)layoutAdvance.getY();
            ++endChunk;
            if (aci.setIndex(end) == '\uffff') break;
            isChunkStart = false;
        }
        return new TextChunk(beginChunk, endChunk, advance);
    }

    protected Point2D adjustChunkOffsets(Point2D location, List textRuns, TextChunk chunk) {
        Point2D.Float visualAdvance;
        int numRuns = chunk.end - chunk.begin;
        TextRun r = (TextRun)textRuns.get(0);
        int anchorType = r.getAnchorType();
        Float length = r.getLength();
        Integer lengthAdj = r.getLengthAdjust();
        boolean doAdjust = true;
        if (length == null || length.isNaN()) {
            doAdjust = false;
        }
        int numChars = 0;
        for (int i = 0; i < numRuns; ++i) {
            r = (TextRun)textRuns.get(i);
            AttributedCharacterIterator aci = r.getACI();
            numChars += aci.getEndIndex() - aci.getBeginIndex();
        }
        if (lengthAdj == GVTAttributedCharacterIterator.TextAttribute.ADJUST_SPACING && numChars == 1) {
            doAdjust = false;
        }
        float xScale = 1.0f;
        float yScale = 1.0f;
        r = (TextRun)textRuns.get(numRuns - 1);
        TextSpanLayout layout = r.getLayout();
        GVTGlyphMetrics lastMetrics = layout.getGlyphMetrics(layout.getGlyphCount() - 1);
        GVTLineMetrics lastLineMetrics = layout.getLineMetrics();
        Rectangle2D lastBounds = lastMetrics.getBounds2D();
        float halfLeading = (lastMetrics.getVerticalAdvance() - (lastLineMetrics.getAscent() + lastLineMetrics.getDescent())) / 2.0f;
        float lastW = (float)(lastBounds.getWidth() + lastBounds.getX());
        float lastH = (float)((double)(halfLeading + lastLineMetrics.getAscent()) + (lastBounds.getHeight() + lastBounds.getY()));
        if (!doAdjust) {
            visualAdvance = new Point2D.Float((float)chunk.advance.getX(), (float)(chunk.advance.getY() + (double)lastH - (double)lastMetrics.getVerticalAdvance()));
        } else {
            double adv;
            Point2D advance = chunk.advance;
            if (layout.isVertical()) {
                if (lengthAdj == ADJUST_SPACING) {
                    yScale = (float)((double)(length.floatValue() - lastH) / (advance.getY() - (double)lastMetrics.getVerticalAdvance()));
                } else {
                    adv = advance.getY() + (double)lastH - (double)lastMetrics.getVerticalAdvance();
                    yScale = (float)((double)length.floatValue() / adv);
                }
                visualAdvance = new Point2D.Float(0.0f, length.floatValue());
            } else {
                if (lengthAdj == ADJUST_SPACING) {
                    xScale = (float)((double)(length.floatValue() - lastW) / (advance.getX() - (double)lastMetrics.getHorizontalAdvance()));
                } else {
                    adv = advance.getX() + (double)lastW - (double)lastMetrics.getHorizontalAdvance();
                    xScale = (float)((double)length.floatValue() / adv);
                }
                visualAdvance = new Point2D.Float(length.floatValue(), 0.0f);
            }
            Point2D.Float adv2 = new Point2D.Float(0.0f, 0.0f);
            for (int i = 0; i < numRuns; ++i) {
                r = (TextRun)textRuns.get(i);
                layout = r.getLayout();
                layout.setScale(xScale, yScale, lengthAdj == ADJUST_SPACING);
                Point2D lAdv = layout.getAdvance2D();
                adv2.x += (float)lAdv.getX();
                adv2.y += (float)lAdv.getY();
            }
            chunk.advance = adv2;
        }
        float dx = 0.0f;
        float dy = 0.0f;
        switch (anchorType) {
            case 1: {
                dx = (float)(-((Point2D)visualAdvance).getX() / 2.0);
                dy = (float)(-((Point2D)visualAdvance).getY() / 2.0);
                break;
            }
            case 2: {
                dx = (float)(-((Point2D)visualAdvance).getX());
                dy = (float)(-((Point2D)visualAdvance).getY());
                break;
            }
        }
        r = (TextRun)textRuns.get(0);
        layout = r.getLayout();
        AttributedCharacterIterator runaci = r.getACI();
        runaci.first();
        boolean vertical = layout.isVertical();
        Float runX = (Float)runaci.getAttribute(XPOS);
        Float runY = (Float)runaci.getAttribute(YPOS);
        TextPath textPath = (TextPath)runaci.getAttribute(TEXTPATH);
        float absX = (float)location.getX();
        float absY = (float)location.getY();
        float tpShiftX = 0.0f;
        float tpShiftY = 0.0f;
        if (runX != null && !runX.isNaN()) {
            tpShiftX = absX = runX.floatValue();
        }
        if (runY != null && !runY.isNaN()) {
            tpShiftY = absY = runY.floatValue();
        }
        if (vertical) {
            absY += dy;
            tpShiftY += dy;
            tpShiftX = 0.0f;
        } else {
            absX += dx;
            tpShiftX += dx;
            tpShiftY = 0.0f;
        }
        for (int i = 0; i < numRuns; ++i) {
            Point2D ladv;
            r = (TextRun)textRuns.get(i);
            layout = r.getLayout();
            runaci = r.getACI();
            runaci.first();
            textPath = (TextPath)runaci.getAttribute(TEXTPATH);
            if (vertical) {
                runX = (Float)runaci.getAttribute(XPOS);
                if (runX != null && !runX.isNaN()) {
                    absX = runX.floatValue();
                }
            } else {
                runY = (Float)runaci.getAttribute(YPOS);
                if (runY != null && !runY.isNaN()) {
                    absY = runY.floatValue();
                }
            }
            if (textPath == null) {
                layout.setOffset(new Point2D.Float(absX, absY));
                ladv = layout.getAdvance2D();
                absX = (float)((double)absX + ladv.getX());
                absY = (float)((double)absY + ladv.getY());
                continue;
            }
            layout.setOffset(new Point2D.Float(tpShiftX, tpShiftY));
            ladv = layout.getAdvance2D();
            tpShiftX += (float)ladv.getX();
            tpShiftY += (float)ladv.getY();
            ladv = layout.getTextPathAdvance();
            absX = (float)ladv.getX();
            absY = (float)ladv.getY();
        }
        return new Point2D.Float(absX, absY);
    }

    protected void paintDecorations(List textRuns, Graphics2D g2d, int decorationType) {
        Paint prevPaint = null;
        Paint prevStrokePaint = null;
        Stroke prevStroke = null;
        boolean prevVisible = true;
        RectangularShape decorationRect = null;
        double yLoc = 0.0;
        double height = 0.0;
        for (Object textRun1 : textRuns) {
            Rectangle2D r2d;
            TextRun textRun = (TextRun)textRun1;
            AttributedCharacterIterator runaci = textRun.getACI();
            runaci.first();
            Paint paint = null;
            Stroke stroke = null;
            Paint strokePaint = null;
            boolean visible = true;
            TextPaintInfo tpi = (TextPaintInfo)runaci.getAttribute(PAINT_INFO);
            if (tpi != null) {
                visible = tpi.visible;
                if (tpi.composite != null) {
                    g2d.setComposite(tpi.composite);
                }
                switch (decorationType) {
                    case 1: {
                        paint = tpi.underlinePaint;
                        stroke = tpi.underlineStroke;
                        strokePaint = tpi.underlineStrokePaint;
                        break;
                    }
                    case 4: {
                        paint = tpi.overlinePaint;
                        stroke = tpi.overlineStroke;
                        strokePaint = tpi.overlineStrokePaint;
                        break;
                    }
                    case 2: {
                        paint = tpi.strikethroughPaint;
                        stroke = tpi.strikethroughStroke;
                        strokePaint = tpi.strikethroughStrokePaint;
                        break;
                    }
                    default: {
                        return;
                    }
                }
            }
            if (textRun.isFirstRunInChunk()) {
                Shape s = textRun.getLayout().getDecorationOutline(decorationType);
                r2d = s.getBounds2D();
                yLoc = r2d.getY();
                height = r2d.getHeight();
            }
            if (textRun.isFirstRunInChunk() || paint != prevPaint || stroke != prevStroke || strokePaint != prevStrokePaint || visible != prevVisible) {
                if (prevVisible && decorationRect != null) {
                    if (prevPaint != null) {
                        g2d.setPaint(prevPaint);
                        g2d.fill(decorationRect);
                    }
                    if (prevStroke != null && prevStrokePaint != null) {
                        g2d.setPaint(prevStrokePaint);
                        g2d.setStroke(prevStroke);
                        g2d.draw(decorationRect);
                    }
                }
                decorationRect = null;
            }
            if (!(paint == null && strokePaint == null || textRun.getLayout().isVertical() || textRun.getLayout().isOnATextPath())) {
                Shape decorationShape = textRun.getLayout().getDecorationOutline(decorationType);
                if (decorationRect == null) {
                    r2d = decorationShape.getBounds2D();
                    decorationRect = new Rectangle2D.Double(r2d.getX(), yLoc, r2d.getWidth(), height);
                } else {
                    Rectangle2D bounds = decorationShape.getBounds2D();
                    double minX = Math.min(decorationRect.getX(), bounds.getX());
                    double maxX = Math.max(decorationRect.getMaxX(), bounds.getMaxX());
                    ((Rectangle2D)decorationRect).setRect(minX, yLoc, maxX - minX, height);
                }
            }
            prevPaint = paint;
            prevStroke = stroke;
            prevStrokePaint = strokePaint;
            prevVisible = visible;
        }
        if (prevVisible && decorationRect != null) {
            if (prevPaint != null) {
                g2d.setPaint(prevPaint);
                g2d.fill(decorationRect);
            }
            if (prevStroke != null && prevStrokePaint != null) {
                g2d.setPaint(prevStrokePaint);
                g2d.setStroke(prevStroke);
                g2d.draw(decorationRect);
            }
        }
    }

    protected void paintTextRuns(List textRuns, Graphics2D g2d) {
        for (Object textRun1 : textRuns) {
            TextRun textRun = (TextRun)textRun1;
            AttributedCharacterIterator runaci = textRun.getACI();
            runaci.first();
            TextPaintInfo tpi = (TextPaintInfo)runaci.getAttribute(PAINT_INFO);
            if (tpi != null && tpi.composite != null) {
                g2d.setComposite(tpi.composite);
            }
            textRun.getLayout().draw(g2d);
        }
    }

    @Override
    public Shape getOutline(TextNode node) {
        GeneralPath outline = null;
        AttributedCharacterIterator aci = node.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        List textRuns = this.getTextRuns(node, aci);
        for (Object textRun1 : textRuns) {
            TextRun textRun = (TextRun)textRun1;
            TextSpanLayout textRunLayout = textRun.getLayout();
            GeneralPath textRunOutline = new GeneralPath(textRunLayout.getOutline());
            if (outline == null) {
                outline = textRunOutline;
                continue;
            }
            outline.setWindingRule(1);
            outline.append(textRunOutline, false);
        }
        Shape underline = this.getDecorationOutline(textRuns, 1);
        Shape strikeThrough = this.getDecorationOutline(textRuns, 2);
        Shape overline = this.getDecorationOutline(textRuns, 4);
        if (underline != null) {
            if (outline == null) {
                outline = new GeneralPath(underline);
            } else {
                outline.setWindingRule(1);
                outline.append(underline, false);
            }
        }
        if (strikeThrough != null) {
            if (outline == null) {
                outline = new GeneralPath(strikeThrough);
            } else {
                outline.setWindingRule(1);
                outline.append(strikeThrough, false);
            }
        }
        if (overline != null) {
            if (outline == null) {
                outline = new GeneralPath(overline);
            } else {
                outline.setWindingRule(1);
                outline.append(overline, false);
            }
        }
        return outline;
    }

    @Override
    public Rectangle2D getBounds2D(TextNode node) {
        Shape overline;
        Shape strikeThrough;
        AttributedCharacterIterator aci = node.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        List textRuns = this.getTextRuns(node, aci);
        Rectangle2D bounds = null;
        for (Object textRun1 : textRuns) {
            TextRun textRun = (TextRun)textRun1;
            TextSpanLayout textRunLayout = textRun.getLayout();
            Rectangle2D runBounds = textRunLayout.getBounds2D();
            if (runBounds == null) continue;
            if (bounds == null) {
                bounds = runBounds;
                continue;
            }
            bounds.add(runBounds);
        }
        Shape underline = this.getDecorationStrokeOutline(textRuns, 1);
        if (underline != null) {
            if (bounds == null) {
                bounds = underline.getBounds2D();
            } else {
                bounds.add(underline.getBounds2D());
            }
        }
        if ((strikeThrough = this.getDecorationStrokeOutline(textRuns, 2)) != null) {
            if (bounds == null) {
                bounds = strikeThrough.getBounds2D();
            } else {
                bounds.add(strikeThrough.getBounds2D());
            }
        }
        if ((overline = this.getDecorationStrokeOutline(textRuns, 4)) != null) {
            if (bounds == null) {
                bounds = overline.getBounds2D();
            } else {
                bounds.add(overline.getBounds2D());
            }
        }
        return bounds;
    }

    protected Shape getDecorationOutline(List textRuns, int decorationType) {
        Path2D outline = null;
        Paint prevPaint = null;
        Paint prevStrokePaint = null;
        Stroke prevStroke = null;
        RectangularShape decorationRect = null;
        double yLoc = 0.0;
        double height = 0.0;
        for (Object textRun1 : textRuns) {
            Rectangle2D r2d;
            TextRun textRun = (TextRun)textRun1;
            AttributedCharacterIterator runaci = textRun.getACI();
            runaci.first();
            Paint paint = null;
            Stroke stroke = null;
            Paint strokePaint = null;
            TextPaintInfo tpi = (TextPaintInfo)runaci.getAttribute(PAINT_INFO);
            if (tpi != null) {
                switch (decorationType) {
                    case 1: {
                        paint = tpi.underlinePaint;
                        stroke = tpi.underlineStroke;
                        strokePaint = tpi.underlineStrokePaint;
                        break;
                    }
                    case 4: {
                        paint = tpi.overlinePaint;
                        stroke = tpi.overlineStroke;
                        strokePaint = tpi.overlineStrokePaint;
                        break;
                    }
                    case 2: {
                        paint = tpi.strikethroughPaint;
                        stroke = tpi.strikethroughStroke;
                        strokePaint = tpi.strikethroughStrokePaint;
                        break;
                    }
                    default: {
                        return null;
                    }
                }
            }
            if (textRun.isFirstRunInChunk()) {
                Shape s = textRun.getLayout().getDecorationOutline(decorationType);
                r2d = s.getBounds2D();
                yLoc = r2d.getY();
                height = r2d.getHeight();
            }
            if ((textRun.isFirstRunInChunk() || paint != prevPaint || stroke != prevStroke || strokePaint != prevStrokePaint) && decorationRect != null) {
                if (outline == null) {
                    outline = new GeneralPath(decorationRect);
                } else {
                    outline.append(decorationRect, false);
                }
                decorationRect = null;
            }
            if (!(paint == null && strokePaint == null || textRun.getLayout().isVertical() || textRun.getLayout().isOnATextPath())) {
                Shape decorationShape = textRun.getLayout().getDecorationOutline(decorationType);
                if (decorationRect == null) {
                    r2d = decorationShape.getBounds2D();
                    decorationRect = new Rectangle2D.Double(r2d.getX(), yLoc, r2d.getWidth(), height);
                } else {
                    Rectangle2D bounds = decorationShape.getBounds2D();
                    double minX = Math.min(decorationRect.getX(), bounds.getX());
                    double maxX = Math.max(decorationRect.getMaxX(), bounds.getMaxX());
                    ((Rectangle2D)decorationRect).setRect(minX, yLoc, maxX - minX, height);
                }
            }
            prevPaint = paint;
            prevStroke = stroke;
            prevStrokePaint = strokePaint;
        }
        if (decorationRect != null) {
            if (outline == null) {
                outline = new GeneralPath(decorationRect);
            } else {
                outline.append(decorationRect, false);
            }
        }
        return outline;
    }

    protected Shape getDecorationStrokeOutline(List textRuns, int decorationType) {
        Path2D outline = null;
        Paint prevPaint = null;
        Paint prevStrokePaint = null;
        Stroke prevStroke = null;
        Shape decorationRect = null;
        double yLoc = 0.0;
        double height = 0.0;
        for (Object textRun1 : textRuns) {
            Rectangle2D r2d;
            Shape s;
            TextRun textRun = (TextRun)textRun1;
            AttributedCharacterIterator runaci = textRun.getACI();
            runaci.first();
            Paint paint = null;
            Stroke stroke = null;
            Paint strokePaint = null;
            TextPaintInfo tpi = (TextPaintInfo)runaci.getAttribute(PAINT_INFO);
            if (tpi != null) {
                switch (decorationType) {
                    case 1: {
                        paint = tpi.underlinePaint;
                        stroke = tpi.underlineStroke;
                        strokePaint = tpi.underlineStrokePaint;
                        break;
                    }
                    case 4: {
                        paint = tpi.overlinePaint;
                        stroke = tpi.overlineStroke;
                        strokePaint = tpi.overlineStrokePaint;
                        break;
                    }
                    case 2: {
                        paint = tpi.strikethroughPaint;
                        stroke = tpi.strikethroughStroke;
                        strokePaint = tpi.strikethroughStrokePaint;
                        break;
                    }
                    default: {
                        return null;
                    }
                }
            }
            if (textRun.isFirstRunInChunk()) {
                s = textRun.getLayout().getDecorationOutline(decorationType);
                r2d = s.getBounds2D();
                yLoc = r2d.getY();
                height = r2d.getHeight();
            }
            if ((textRun.isFirstRunInChunk() || paint != prevPaint || stroke != prevStroke || strokePaint != prevStrokePaint) && decorationRect != null) {
                s = null;
                if (prevStroke != null && prevStrokePaint != null) {
                    s = prevStroke.createStrokedShape(decorationRect);
                } else if (prevPaint != null) {
                    s = decorationRect;
                }
                if (s != null) {
                    if (outline == null) {
                        outline = new GeneralPath(s);
                    } else {
                        outline.append(s, false);
                    }
                }
                decorationRect = null;
            }
            if (!(paint == null && strokePaint == null || textRun.getLayout().isVertical() || textRun.getLayout().isOnATextPath())) {
                Shape decorationShape = textRun.getLayout().getDecorationOutline(decorationType);
                if (decorationRect == null) {
                    r2d = decorationShape.getBounds2D();
                    decorationRect = new Rectangle2D.Double(r2d.getX(), yLoc, r2d.getWidth(), height);
                } else {
                    Rectangle2D bounds = decorationShape.getBounds2D();
                    double minX = Math.min(((RectangularShape)decorationRect).getX(), bounds.getX());
                    double maxX = Math.max(((RectangularShape)decorationRect).getMaxX(), bounds.getMaxX());
                    ((Rectangle2D)decorationRect).setRect(minX, yLoc, maxX - minX, height);
                }
            }
            prevPaint = paint;
            prevStroke = stroke;
            prevStrokePaint = strokePaint;
        }
        if (decorationRect != null) {
            Shape s = null;
            if (prevStroke != null && prevStrokePaint != null) {
                s = prevStroke.createStrokedShape(decorationRect);
            } else if (prevPaint != null) {
                s = decorationRect;
            }
            if (s != null) {
                if (outline == null) {
                    outline = new GeneralPath(s);
                } else {
                    outline.append(s, false);
                }
            }
        }
        return outline;
    }

    @Override
    public Mark getMark(TextNode node, int index, boolean leadingEdge) {
        AttributedCharacterIterator aci = node.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        if (index < aci.getBeginIndex() || index > aci.getEndIndex()) {
            return null;
        }
        TextHit textHit = new TextHit(index, leadingEdge);
        return new BasicTextPainter.BasicMark(node, textHit);
    }

    @Override
    protected Mark hitTest(double x, double y, TextNode node) {
        AttributedCharacterIterator aci = node.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        List textRuns = this.getTextRuns(node, aci);
        if (textRuns != null) {
            for (Object textRun1 : textRuns) {
                TextRun textRun = (TextRun)textRun1;
                TextSpanLayout layout = textRun.getLayout();
                TextHit textHit = layout.hitTestChar((float)x, (float)y);
                Rectangle2D bounds = layout.getBounds2D();
                if (textHit == null || bounds == null || !bounds.contains(x, y)) continue;
                return new BasicTextPainter.BasicMark(node, textHit);
            }
        }
        return null;
    }

    @Override
    public Mark selectFirst(TextNode node) {
        AttributedCharacterIterator aci = node.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        TextHit textHit = new TextHit(aci.getBeginIndex(), false);
        return new BasicTextPainter.BasicMark(node, textHit);
    }

    @Override
    public Mark selectLast(TextNode node) {
        AttributedCharacterIterator aci = node.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        TextHit textHit = new TextHit(aci.getEndIndex() - 1, false);
        return new BasicTextPainter.BasicMark(node, textHit);
    }

    @Override
    public int[] getSelected(Mark startMark, Mark finishMark) {
        BasicTextPainter.BasicMark finish;
        BasicTextPainter.BasicMark start;
        if (startMark == null || finishMark == null) {
            return null;
        }
        try {
            start = (BasicTextPainter.BasicMark)startMark;
            finish = (BasicTextPainter.BasicMark)finishMark;
        }
        catch (ClassCastException cce) {
            throw new RuntimeException("This Mark was not instantiated by this TextPainter class!");
        }
        TextNode textNode = start.getTextNode();
        if (textNode == null) {
            return null;
        }
        if (textNode != finish.getTextNode()) {
            throw new RuntimeException("Markers are from different TextNodes!");
        }
        AttributedCharacterIterator aci = textNode.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        int[] result = new int[]{start.getHit().getCharIndex(), finish.getHit().getCharIndex()};
        List textRuns = this.getTextRuns(textNode, aci);
        Iterator trI = textRuns.iterator();
        int startGlyphIndex = -1;
        int endGlyphIndex = -1;
        TextSpanLayout startLayout = null;
        TextSpanLayout endLayout = null;
        while (trI.hasNext()) {
            TextRun tr = (TextRun)trI.next();
            TextSpanLayout tsl = tr.getLayout();
            if (startGlyphIndex == -1 && (startGlyphIndex = tsl.getGlyphIndex(result[0])) != -1) {
                startLayout = tsl;
            }
            if (endGlyphIndex == -1 && (endGlyphIndex = tsl.getGlyphIndex(result[1])) != -1) {
                endLayout = tsl;
            }
            if (startGlyphIndex == -1 || endGlyphIndex == -1) continue;
            break;
        }
        if (startLayout == null || endLayout == null) {
            return null;
        }
        int startCharCount = startLayout.getCharacterCount(startGlyphIndex, startGlyphIndex);
        int endCharCount = endLayout.getCharacterCount(endGlyphIndex, endGlyphIndex);
        if (startCharCount > 1) {
            if (result[0] > result[1] && startLayout.isLeftToRight()) {
                result[0] = result[0] + (startCharCount - 1);
            } else if (result[1] > result[0] && !startLayout.isLeftToRight()) {
                result[0] = result[0] - (startCharCount - 1);
            }
        }
        if (endCharCount > 1) {
            if (result[1] > result[0] && endLayout.isLeftToRight()) {
                result[1] = result[1] + (endCharCount - 1);
            } else if (result[0] > result[1] && !endLayout.isLeftToRight()) {
                result[1] = result[1] - (endCharCount - 1);
            }
        }
        return result;
    }

    @Override
    public Shape getHighlightShape(Mark beginMark, Mark endMark) {
        int endIndex;
        BasicTextPainter.BasicMark end;
        BasicTextPainter.BasicMark begin;
        if (beginMark == null || endMark == null) {
            return null;
        }
        try {
            begin = (BasicTextPainter.BasicMark)beginMark;
            end = (BasicTextPainter.BasicMark)endMark;
        }
        catch (ClassCastException cce) {
            throw new RuntimeException("This Mark was not instantiated by this TextPainter class!");
        }
        TextNode textNode = begin.getTextNode();
        if (textNode == null) {
            return null;
        }
        if (textNode != end.getTextNode()) {
            throw new RuntimeException("Markers are from different TextNodes!");
        }
        AttributedCharacterIterator aci = textNode.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        int beginIndex = begin.getHit().getCharIndex();
        if (beginIndex > (endIndex = end.getHit().getCharIndex())) {
            BasicTextPainter.BasicMark tmpMark = begin;
            begin = end;
            end = tmpMark;
            int tmpIndex = beginIndex;
            beginIndex = endIndex;
            endIndex = tmpIndex;
        }
        List textRuns = this.getTextRuns(textNode, aci);
        GeneralPath highlightedShape = new GeneralPath();
        for (Object textRun1 : textRuns) {
            TextRun textRun = (TextRun)textRun1;
            TextSpanLayout layout = textRun.getLayout();
            Shape layoutHighlightedShape = layout.getHighlightShape(beginIndex, endIndex);
            if (layoutHighlightedShape == null || layoutHighlightedShape.getBounds().isEmpty()) continue;
            highlightedShape.append(layoutHighlightedShape, false);
        }
        return highlightedShape;
    }

    static {
        extendedAtts.add(FLOW_PARAGRAPH);
        extendedAtts.add(TEXT_COMPOUND_ID);
        extendedAtts.add(GVT_FONT);
        singleton = new StrokingTextPainter();
    }

    public static class TextRun {
        protected AttributedCharacterIterator aci;
        protected TextSpanLayout layout;
        protected int anchorType;
        protected boolean firstRunInChunk;
        protected Float length;
        protected Integer lengthAdjust;
        private int level;
        private int reversals;

        public TextRun(TextSpanLayout layout, AttributedCharacterIterator aci, boolean firstRunInChunk) {
            this.layout = layout;
            this.aci = aci;
            this.aci.first();
            this.firstRunInChunk = firstRunInChunk;
            this.anchorType = 0;
            TextNode.Anchor anchor = (TextNode.Anchor)aci.getAttribute((AttributedCharacterIterator.Attribute)GVTAttributedCharacterIterator.TextAttribute.ANCHOR_TYPE);
            if (anchor != null) {
                this.anchorType = anchor.getType();
            }
            if (aci.getAttribute(WRITING_MODE) == WRITING_MODE_RTL) {
                if (this.anchorType == 0) {
                    this.anchorType = 2;
                } else if (this.anchorType == 2) {
                    this.anchorType = 0;
                }
            }
            this.length = (Float)aci.getAttribute((AttributedCharacterIterator.Attribute)GVTAttributedCharacterIterator.TextAttribute.BBOX_WIDTH);
            this.lengthAdjust = (Integer)aci.getAttribute((AttributedCharacterIterator.Attribute)GVTAttributedCharacterIterator.TextAttribute.LENGTH_ADJUST);
            Integer level = (Integer)aci.getAttribute(BIDI_LEVEL);
            this.level = level != null ? level : -1;
        }

        public AttributedCharacterIterator getACI() {
            return this.aci;
        }

        public TextSpanLayout getLayout() {
            return this.layout;
        }

        public int getAnchorType() {
            return this.anchorType;
        }

        public Float getLength() {
            return this.length;
        }

        public Integer getLengthAdjust() {
            return this.lengthAdjust;
        }

        public boolean isFirstRunInChunk() {
            return this.firstRunInChunk;
        }

        public int getBidiLevel() {
            return this.level;
        }

        public void reverse() {
            ++this.reversals;
        }

        public void maybeReverseGlyphs(boolean mirror) {
            if ((this.reversals & 1) == 1) {
                this.layout.maybeReverse(mirror);
            }
        }
    }

    public static class TextChunk {
        public int begin;
        public int end;
        public Point2D advance;

        public TextChunk(int begin, int end, Point2D advance) {
            this.begin = begin;
            this.end = end;
            this.advance = new Point2D.Float((float)advance.getX(), (float)advance.getY());
        }
    }
}

