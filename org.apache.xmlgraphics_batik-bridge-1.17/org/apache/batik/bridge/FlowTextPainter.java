/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.flow.BlockInfo
 *  org.apache.batik.gvt.flow.FlowRegions
 *  org.apache.batik.gvt.flow.GlyphGroupInfo
 *  org.apache.batik.gvt.flow.LineInfo
 *  org.apache.batik.gvt.flow.RegionInfo
 *  org.apache.batik.gvt.flow.TextLineBreaks
 *  org.apache.batik.gvt.flow.WordInfo
 *  org.apache.batik.gvt.font.GVTFont
 *  org.apache.batik.gvt.font.GVTGlyphVector
 *  org.apache.batik.gvt.font.GVTLineMetrics
 *  org.apache.batik.gvt.font.MultiGlyphVector
 *  org.apache.batik.gvt.text.GVTAttributedCharacterIterator$TextAttribute
 */
package org.apache.batik.bridge;

import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.batik.bridge.GlyphLayout;
import org.apache.batik.bridge.StrokingTextPainter;
import org.apache.batik.bridge.TextNode;
import org.apache.batik.bridge.TextPainter;
import org.apache.batik.bridge.TextSpanLayout;
import org.apache.batik.gvt.flow.BlockInfo;
import org.apache.batik.gvt.flow.FlowRegions;
import org.apache.batik.gvt.flow.GlyphGroupInfo;
import org.apache.batik.gvt.flow.LineInfo;
import org.apache.batik.gvt.flow.RegionInfo;
import org.apache.batik.gvt.flow.TextLineBreaks;
import org.apache.batik.gvt.flow.WordInfo;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTGlyphVector;
import org.apache.batik.gvt.font.GVTLineMetrics;
import org.apache.batik.gvt.font.MultiGlyphVector;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;

public class FlowTextPainter
extends StrokingTextPainter {
    protected static TextPainter singleton = new FlowTextPainter();
    public static final char SOFT_HYPHEN = '\u00ad';
    public static final char ZERO_WIDTH_SPACE = '\u200b';
    public static final char ZERO_WIDTH_JOINER = '\u200d';
    public static final char SPACE = ' ';
    public static final AttributedCharacterIterator.Attribute WORD_LIMIT = TextLineBreaks.WORD_LIMIT;
    public static final AttributedCharacterIterator.Attribute FLOW_REGIONS = GVTAttributedCharacterIterator.TextAttribute.FLOW_REGIONS;
    public static final AttributedCharacterIterator.Attribute FLOW_LINE_BREAK = GVTAttributedCharacterIterator.TextAttribute.FLOW_LINE_BREAK;
    public static final AttributedCharacterIterator.Attribute LINE_HEIGHT = GVTAttributedCharacterIterator.TextAttribute.LINE_HEIGHT;
    public static final AttributedCharacterIterator.Attribute GVT_FONT = GVTAttributedCharacterIterator.TextAttribute.GVT_FONT;
    protected static Set szAtts = new HashSet();

    public static TextPainter getInstance() {
        return singleton;
    }

    @Override
    public List getTextRuns(TextNode node, AttributedCharacterIterator aci) {
        List textRuns = node.getTextRuns();
        if (textRuns != null) {
            return textRuns;
        }
        AttributedCharacterIterator[] chunkACIs = this.getTextChunkACIs(aci);
        textRuns = this.computeTextRuns(node, aci, chunkACIs);
        aci.first();
        List rgns = (List)aci.getAttribute(FLOW_REGIONS);
        if (rgns != null) {
            Iterator i = textRuns.iterator();
            ArrayList chunkLayouts = new ArrayList();
            StrokingTextPainter.TextRun tr = (StrokingTextPainter.TextRun)i.next();
            ArrayList<TextSpanLayout> layouts = new ArrayList<TextSpanLayout>();
            chunkLayouts.add(layouts);
            layouts.add(tr.getLayout());
            while (i.hasNext()) {
                tr = (StrokingTextPainter.TextRun)i.next();
                if (tr.isFirstRunInChunk()) {
                    layouts = new ArrayList();
                    chunkLayouts.add(layouts);
                }
                layouts.add(tr.getLayout());
            }
            FlowTextPainter.textWrap(chunkACIs, chunkLayouts, rgns, this.fontRenderContext);
        }
        node.setTextRuns(textRuns);
        return node.getTextRuns();
    }

    public static boolean textWrap(AttributedCharacterIterator[] acis, List chunkLayouts, List flowRects, FontRenderContext frc) {
        boolean overflow;
        WordInfo[] chunkInfo;
        WordInfo[][] wordInfos = new WordInfo[acis.length][];
        Iterator clIter = chunkLayouts.iterator();
        float prevBotMargin = 0.0f;
        int numWords = 0;
        BlockInfo[] blockInfos = new BlockInfo[acis.length];
        float[] topSkip = new float[acis.length];
        int chunk = 0;
        while (clIter.hasNext()) {
            AttributedCharacterIterator aci = acis[chunk];
            LinkedList<GVTGlyphVector> gvl = new LinkedList<GVTGlyphVector>();
            List layouts = (List)clIter.next();
            for (Object layout : layouts) {
                GlyphLayout gl = (GlyphLayout)layout;
                gvl.add(gl.getGlyphVector());
            }
            MultiGlyphVector gv = new MultiGlyphVector(gvl);
            wordInfos[chunk] = FlowTextPainter.doWordAnalysis((GVTGlyphVector)gv, aci, numWords, frc);
            aci.first();
            BlockInfo bi = (BlockInfo)aci.getAttribute(FLOW_PARAGRAPH);
            bi.initLineInfo(frc);
            blockInfos[chunk] = bi;
            topSkip[chunk] = prevBotMargin > bi.getTopMargin() ? prevBotMargin : bi.getTopMargin();
            prevBotMargin = bi.getBottomMargin();
            numWords += wordInfos[chunk].length;
            ++chunk;
        }
        Iterator frIter = flowRects.iterator();
        RegionInfo currentRegion = null;
        int currWord = 0;
        int chunk2 = 0;
        LinkedList<LineInfo> lineInfos = new LinkedList<LineInfo>();
        while (frIter.hasNext()) {
            currentRegion = (RegionInfo)frIter.next();
            FlowRegions fr = new FlowRegions(currentRegion.getShape());
            while (chunk2 < wordInfos.length) {
                chunkInfo = wordInfos[chunk2];
                BlockInfo bi = blockInfos[chunk2];
                WordInfo wi = chunkInfo[currWord];
                Object flowLine = wi.getFlowLine();
                double lh = Math.max(wi.getLineHeight(), bi.getLineHeight());
                LineInfo li = new LineInfo(fr, bi, true);
                double newY = li.getCurrentY() + (double)topSkip[chunk2];
                topSkip[chunk2] = 0.0f;
                if (li.gotoY(newY)) break;
                while (!li.addWord(wi) && !li.gotoY(newY = li.getCurrentY() + lh * 0.1)) {
                }
                if (fr.done()) break;
                ++currWord;
                while (currWord < chunkInfo.length) {
                    wi = chunkInfo[currWord];
                    if (wi.getFlowLine() != flowLine || !li.addWord(wi)) {
                        li.layout();
                        lineInfos.add(li);
                        li = null;
                        flowLine = wi.getFlowLine();
                        lh = Math.max(wi.getLineHeight(), bi.getLineHeight());
                        if (!fr.newLine(lh)) break;
                        li = new LineInfo(fr, bi, false);
                        while (!li.addWord(wi) && !li.gotoY(newY = li.getCurrentY() + lh * 0.1)) {
                        }
                        if (fr.done()) break;
                    }
                    ++currWord;
                }
                if (li != null) {
                    li.setParaEnd(true);
                    li.layout();
                }
                if (fr.done()) break;
                ++chunk2;
                currWord = 0;
                if (!bi.isFlowRegionBreak() && fr.newLine(lh)) continue;
                break;
            }
            if (chunk2 != wordInfos.length) continue;
            break;
        }
        boolean bl = overflow = chunk2 < wordInfos.length;
        while (chunk2 < wordInfos.length) {
            chunkInfo = wordInfos[chunk2];
            while (currWord < chunkInfo.length) {
                WordInfo wi = chunkInfo[currWord];
                int numGG = wi.getNumGlyphGroups();
                for (int gg = 0; gg < numGG; ++gg) {
                    GlyphGroupInfo ggi = wi.getGlyphGroup(gg);
                    GVTGlyphVector gv = ggi.getGlyphVector();
                    int end = ggi.getEnd();
                    for (int g = ggi.getStart(); g <= end; ++g) {
                        gv.setGlyphVisible(g, false);
                    }
                }
                ++currWord;
            }
            ++chunk2;
            currWord = 0;
        }
        return overflow;
    }

    static int[] allocWordMap(int[] wordMap, int sz) {
        int ext;
        if (wordMap != null) {
            if (sz <= wordMap.length) {
                return wordMap;
            }
            if (sz < wordMap.length * 2) {
                sz = wordMap.length * 2;
            }
        }
        int[] ret = new int[sz];
        int n = ext = wordMap != null ? wordMap.length : 0;
        if (sz < ext) {
            ext = sz;
        }
        if (ext != 0) {
            System.arraycopy(wordMap, 0, ret, 0, ext);
        }
        Arrays.fill(ret, ext, sz, -1);
        return ret;
    }

    static WordInfo[] doWordAnalysis(GVTGlyphVector gv, AttributedCharacterIterator aci, int numWords, FontRenderContext frc) {
        int i;
        int numGlyphs = gv.getNumGlyphs();
        int[] glyphWords = new int[numGlyphs];
        int[] wordMap = FlowTextPainter.allocWordMap(null, 10);
        int maxWord = 0;
        int aciIdx = aci.getBeginIndex();
        for (int i2 = 0; i2 < numGlyphs; ++i2) {
            int cnt = gv.getCharacterCount(i2, i2);
            aci.setIndex(aciIdx);
            Integer integer = (Integer)aci.getAttribute(WORD_LIMIT);
            int minWord = integer - numWords;
            if (minWord > maxWord) {
                maxWord = minWord;
                wordMap = FlowTextPainter.allocWordMap(wordMap, maxWord + 1);
            }
            ++aciIdx;
            for (int c = 1; c < cnt; ++c) {
                aci.setIndex(aciIdx);
                integer = (Integer)aci.getAttribute(WORD_LIMIT);
                int cWord = integer - numWords;
                if (cWord > maxWord) {
                    maxWord = cWord;
                    wordMap = FlowTextPainter.allocWordMap(wordMap, maxWord + 1);
                }
                if (cWord < minWord) {
                    wordMap[minWord] = cWord;
                    minWord = cWord;
                } else if (cWord > minWord) {
                    wordMap[cWord] = minWord;
                }
                ++aciIdx;
            }
            glyphWords[i2] = minWord;
        }
        int words = 0;
        WordInfo[] cWordMap = new WordInfo[maxWord + 1];
        for (int i3 = 0; i3 <= maxWord; ++i3) {
            int nw = wordMap[i3];
            if (nw == -1) {
                cWordMap[i3] = new WordInfo(words++);
                continue;
            }
            int word = nw;
            nw = wordMap[i3];
            while (nw != -1) {
                word = nw;
                nw = wordMap[word];
            }
            wordMap[i3] = word;
            cWordMap[i3] = cWordMap[word];
        }
        wordMap = null;
        WordInfo[] wordInfos = new WordInfo[words];
        for (int i4 = 0; i4 <= maxWord; ++i4) {
            WordInfo wi = cWordMap[i4];
            wordInfos[wi.getIndex()] = cWordMap[i4];
        }
        aciIdx = aci.getBeginIndex();
        int aciEnd = aci.getEndIndex();
        char ch = aci.setIndex(aciIdx);
        int aciWordStart = aciIdx;
        GVTFont gvtFont = (GVTFont)aci.getAttribute(GVT_FONT);
        float lineHeight = 1.0f;
        Float lineHeightFloat = (Float)aci.getAttribute(LINE_HEIGHT);
        if (lineHeightFloat != null) {
            lineHeight = lineHeightFloat.floatValue();
        }
        int runLimit = aci.getRunLimit(szAtts);
        WordInfo prevWI = null;
        float[] lastAdvAdj = new float[numGlyphs];
        float[] advAdj = new float[numGlyphs];
        boolean[] hideLast = new boolean[numGlyphs];
        boolean[] hide = new boolean[numGlyphs];
        boolean[] space = new boolean[numGlyphs];
        float[] glyphPos = gv.getGlyphPositions(0, numGlyphs + 1, null);
        for (int i5 = 0; i5 < numGlyphs; ++i5) {
            char pch = ch;
            ch = aci.setIndex(aciIdx);
            Integer integer = (Integer)aci.getAttribute(WORD_LIMIT);
            WordInfo theWI = cWordMap[integer - numWords];
            if (theWI.getFlowLine() == null) {
                theWI.setFlowLine(aci.getAttribute(FLOW_LINE_BREAK));
            }
            if (prevWI == null) {
                prevWI = theWI;
            } else if (prevWI != theWI) {
                GVTLineMetrics lm = gvtFont.getLineMetrics((CharacterIterator)aci, aciWordStart, aciIdx, frc);
                prevWI.addLineMetrics(gvtFont, lm);
                prevWI.addLineHeight(lineHeight);
                aciWordStart = aciIdx;
                prevWI = theWI;
            }
            int chCnt = gv.getCharacterCount(i5, i5);
            if (chCnt == 1) {
                switch (ch) {
                    case '\u00ad': {
                        hideLast[i5] = true;
                        char nch = aci.next();
                        aci.previous();
                        float kern = gvtFont.getHKern((int)pch, (int)nch);
                        advAdj[i5] = -(glyphPos[2 * i5 + 2] - glyphPos[2 * i5] + kern);
                        break;
                    }
                    case '\u200d': {
                        hide[i5] = true;
                        break;
                    }
                    case '\u200b': {
                        hide[i5] = true;
                        break;
                    }
                    case ' ': {
                        space[i5] = true;
                        char nch = aci.next();
                        aci.previous();
                        float kern = gvtFont.getHKern((int)pch, (int)nch);
                        lastAdvAdj[i5] = -(glyphPos[2 * i5 + 2] - glyphPos[2 * i5] + kern);
                    }
                }
            }
            if ((aciIdx += chCnt) <= runLimit || aciIdx >= aciEnd) continue;
            GVTLineMetrics lm = gvtFont.getLineMetrics((CharacterIterator)aci, aciWordStart, runLimit, frc);
            prevWI.addLineMetrics(gvtFont, lm);
            prevWI.addLineHeight(lineHeight);
            prevWI = null;
            aciWordStart = aciIdx;
            aci.setIndex(aciIdx);
            gvtFont = (GVTFont)aci.getAttribute(GVT_FONT);
            Float f = (Float)aci.getAttribute(LINE_HEIGHT);
            lineHeight = f.floatValue();
            runLimit = aci.getRunLimit(szAtts);
        }
        GVTLineMetrics lm = gvtFont.getLineMetrics((CharacterIterator)aci, aciWordStart, runLimit, frc);
        prevWI.addLineMetrics(gvtFont, lm);
        prevWI.addLineHeight(lineHeight);
        int[] wordGlyphCounts = new int[words];
        for (int i6 = 0; i6 < numGlyphs; ++i6) {
            int cWord;
            int word = glyphWords[i6];
            glyphWords[i6] = cWord = cWordMap[word].getIndex();
            int n = cWord;
            wordGlyphCounts[n] = wordGlyphCounts[n] + 1;
        }
        cWordMap = null;
        int[][] wordGlyphs = new int[words][];
        int[] wordGlyphGroupsCounts = new int[words];
        for (i = 0; i < numGlyphs; ++i) {
            int cWord = glyphWords[i];
            int[] wgs = wordGlyphs[cWord];
            if (wgs == null) {
                wgs = wordGlyphs[cWord] = new int[wordGlyphCounts[cWord]];
                wordGlyphCounts[cWord] = 0;
            }
            int cnt = wordGlyphCounts[cWord];
            wgs[cnt] = i;
            if (cnt == 0) {
                int n = cWord;
                wordGlyphGroupsCounts[n] = wordGlyphGroupsCounts[n] + 1;
            } else if (wgs[cnt - 1] != i - 1) {
                int n = cWord;
                wordGlyphGroupsCounts[n] = wordGlyphGroupsCounts[n] + 1;
            }
            int n = cWord;
            wordGlyphCounts[n] = wordGlyphCounts[n] + 1;
        }
        for (i = 0; i < words; ++i) {
            int cnt = wordGlyphGroupsCounts[i];
            GlyphGroupInfo[] wordGlyphGroups = new GlyphGroupInfo[cnt];
            if (cnt == 1) {
                int[] glyphs = wordGlyphs[i];
                int start = glyphs[0];
                int end = glyphs[glyphs.length - 1];
                wordGlyphGroups[0] = new GlyphGroupInfo(gv, start, end, hide, hideLast[end], glyphPos, advAdj, lastAdvAdj, space);
            } else {
                int prev;
                int glyphGroup = 0;
                int[] glyphs = wordGlyphs[i];
                int start = prev = glyphs[0];
                for (int j = 1; j < glyphs.length; ++j) {
                    if (prev + 1 != glyphs[j]) {
                        int end = glyphs[j - 1];
                        wordGlyphGroups[glyphGroup] = new GlyphGroupInfo(gv, start, end, hide, hideLast[end], glyphPos, advAdj, lastAdvAdj, space);
                        start = glyphs[j];
                        ++glyphGroup;
                    }
                    prev = glyphs[j];
                }
                int end = glyphs[glyphs.length - 1];
                wordGlyphGroups[glyphGroup] = new GlyphGroupInfo(gv, start, end, hide, hideLast[end], glyphPos, advAdj, lastAdvAdj, space);
            }
            wordInfos[i].setGlyphGroups(wordGlyphGroups);
        }
        return wordInfos;
    }

    static {
        szAtts.add(TextAttribute.SIZE);
        szAtts.add(GVT_FONT);
        szAtts.add(LINE_HEIGHT);
    }
}

