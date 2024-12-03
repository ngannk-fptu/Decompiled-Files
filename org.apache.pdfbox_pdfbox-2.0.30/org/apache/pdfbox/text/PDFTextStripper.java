/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.text;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.Bidi;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.pagenavigation.PDThreadBead;
import org.apache.pdfbox.text.LegacyPDFStreamEngine;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.text.TextPositionComparator;
import org.apache.pdfbox.util.IterativeMergeSort;

public class PDFTextStripper
extends LegacyPDFStreamEngine {
    private static float defaultIndentThreshold = 2.0f;
    private static float defaultDropThreshold = 2.5f;
    private static final Log LOG = LogFactory.getLog(PDFTextStripper.class);
    protected final String LINE_SEPARATOR;
    private String lineSeparator;
    private String wordSeparator;
    private String paragraphStart;
    private String paragraphEnd;
    private String pageStart;
    private String pageEnd;
    private String articleStart;
    private String articleEnd;
    private int currentPageNo;
    private int startPage;
    private int endPage;
    private PDOutlineItem startBookmark;
    private int startBookmarkPageNumber;
    private int endBookmarkPageNumber;
    private PDOutlineItem endBookmark;
    private boolean suppressDuplicateOverlappingText;
    private boolean shouldSeparateByBeads;
    private boolean sortByPosition;
    private boolean addMoreFormatting;
    private float indentThreshold;
    private float dropThreshold;
    private float spacingTolerance;
    private float averageCharTolerance;
    private List<PDRectangle> beadRectangles;
    protected ArrayList<List<TextPosition>> charactersByArticle;
    private Map<String, TreeMap<Float, TreeSet<Float>>> characterListMapping;
    protected PDDocument document;
    protected Writer output;
    private boolean inParagraph;
    private static final float END_OF_LAST_TEXT_X_RESET_VALUE = -1.0f;
    private static final float MAX_Y_FOR_LINE_RESET_VALUE = -3.4028235E38f;
    private static final float EXPECTED_START_OF_NEXT_WORD_X_RESET_VALUE = -3.4028235E38f;
    private static final float MAX_HEIGHT_FOR_LINE_RESET_VALUE = -1.0f;
    private static final float MIN_Y_TOP_FOR_LINE_RESET_VALUE = Float.MAX_VALUE;
    private static final float LAST_WORD_SPACING_RESET_VALUE = -1.0f;
    private static final String[] LIST_ITEM_EXPRESSIONS;
    private List<Pattern> listOfPatterns;
    private static Map<Character, Character> MIRRORING_CHAR_MAP;

    public PDFTextStripper() throws IOException {
        this.lineSeparator = this.LINE_SEPARATOR = System.getProperty("line.separator");
        this.wordSeparator = " ";
        this.paragraphStart = "";
        this.paragraphEnd = "";
        this.pageStart = "";
        this.pageEnd = this.LINE_SEPARATOR;
        this.articleStart = "";
        this.articleEnd = "";
        this.currentPageNo = 0;
        this.startPage = 1;
        this.endPage = Integer.MAX_VALUE;
        this.startBookmark = null;
        this.startBookmarkPageNumber = -1;
        this.endBookmarkPageNumber = -1;
        this.endBookmark = null;
        this.suppressDuplicateOverlappingText = true;
        this.shouldSeparateByBeads = true;
        this.sortByPosition = false;
        this.addMoreFormatting = false;
        this.indentThreshold = defaultIndentThreshold;
        this.dropThreshold = defaultDropThreshold;
        this.spacingTolerance = 0.5f;
        this.averageCharTolerance = 0.3f;
        this.beadRectangles = null;
        this.charactersByArticle = new ArrayList();
        this.characterListMapping = new HashMap<String, TreeMap<Float, TreeSet<Float>>>();
        this.listOfPatterns = null;
    }

    public String getText(PDDocument doc) throws IOException {
        StringWriter outputStream = new StringWriter();
        this.writeText(doc, outputStream);
        return outputStream.toString();
    }

    private void resetEngine() {
        this.currentPageNo = 0;
        this.document = null;
        if (this.charactersByArticle != null) {
            this.charactersByArticle.clear();
        }
        this.characterListMapping.clear();
    }

    public void writeText(PDDocument doc, Writer outputStream) throws IOException {
        this.resetEngine();
        this.document = doc;
        this.output = outputStream;
        if (this.getAddMoreFormatting()) {
            this.paragraphEnd = this.lineSeparator;
            this.pageStart = this.lineSeparator;
            this.articleStart = this.lineSeparator;
            this.articleEnd = this.lineSeparator;
        }
        this.startDocument(this.document);
        this.processPages(this.document.getPages());
        this.endDocument(this.document);
    }

    protected void processPages(PDPageTree pages) throws IOException {
        PDPage startBookmarkPage = this.startBookmark == null ? null : this.startBookmark.findDestinationPage(this.document);
        this.startBookmarkPageNumber = startBookmarkPage != null ? pages.indexOf(startBookmarkPage) + 1 : -1;
        PDPage endBookmarkPage = this.endBookmark == null ? null : this.endBookmark.findDestinationPage(this.document);
        this.endBookmarkPageNumber = endBookmarkPage != null ? pages.indexOf(endBookmarkPage) + 1 : -1;
        if (this.startBookmarkPageNumber == -1 && this.startBookmark != null && this.endBookmarkPageNumber == -1 && this.endBookmark != null && this.startBookmark.getCOSObject() == this.endBookmark.getCOSObject()) {
            this.startBookmarkPageNumber = 0;
            this.endBookmarkPageNumber = 0;
        }
        for (PDPage page : pages) {
            ++this.currentPageNo;
            if (!page.hasContents()) continue;
            this.processPage(page);
        }
    }

    protected void startDocument(PDDocument document) throws IOException {
    }

    protected void endDocument(PDDocument document) throws IOException {
    }

    @Override
    public void processPage(PDPage page) throws IOException {
        if (!(this.currentPageNo < this.startPage || this.currentPageNo > this.endPage || this.startBookmarkPageNumber != -1 && this.currentPageNo < this.startBookmarkPageNumber || this.endBookmarkPageNumber != -1 && this.currentPageNo > this.endBookmarkPageNumber)) {
            this.startPage(page);
            int numberOfArticleSections = 1;
            if (this.shouldSeparateByBeads) {
                this.fillBeadRectangles(page);
                numberOfArticleSections += this.beadRectangles.size() * 2;
            }
            int originalSize = this.charactersByArticle.size();
            this.charactersByArticle.ensureCapacity(numberOfArticleSections);
            int lastIndex = Math.max(numberOfArticleSections, originalSize);
            for (int i = 0; i < lastIndex; ++i) {
                if (i < originalSize) {
                    this.charactersByArticle.get(i).clear();
                    continue;
                }
                if (numberOfArticleSections < originalSize) {
                    this.charactersByArticle.remove(i);
                    continue;
                }
                this.charactersByArticle.add(new ArrayList());
            }
            this.characterListMapping.clear();
            super.processPage(page);
            this.writePage();
            this.endPage(page);
        }
    }

    private void fillBeadRectangles(PDPage page) {
        this.beadRectangles = new ArrayList<PDRectangle>();
        for (PDThreadBead bead : page.getThreadBeads()) {
            if (bead == null || bead.getRectangle() == null) {
                this.beadRectangles.add(null);
                continue;
            }
            PDRectangle rect = bead.getRectangle();
            PDRectangle mediaBox = page.getMediaBox();
            float upperRightY = mediaBox.getUpperRightY() - rect.getLowerLeftY();
            float lowerLeftY = mediaBox.getUpperRightY() - rect.getUpperRightY();
            rect.setLowerLeftY(lowerLeftY);
            rect.setUpperRightY(upperRightY);
            PDRectangle cropBox = page.getCropBox();
            if (cropBox.getLowerLeftX() != 0.0f || cropBox.getLowerLeftY() != 0.0f) {
                rect.setLowerLeftX(rect.getLowerLeftX() - cropBox.getLowerLeftX());
                rect.setLowerLeftY(rect.getLowerLeftY() - cropBox.getLowerLeftY());
                rect.setUpperRightX(rect.getUpperRightX() - cropBox.getLowerLeftX());
                rect.setUpperRightY(rect.getUpperRightY() - cropBox.getLowerLeftY());
            }
            this.beadRectangles.add(rect);
        }
    }

    protected void startArticle() throws IOException {
        this.startArticle(true);
    }

    protected void startArticle(boolean isLTR) throws IOException {
        this.output.write(this.getArticleStart());
    }

    protected void endArticle() throws IOException {
        this.output.write(this.getArticleEnd());
    }

    protected void startPage(PDPage page) throws IOException {
    }

    protected void endPage(PDPage page) throws IOException {
    }

    protected void writePage() throws IOException {
        float maxYForLine = -3.4028235E38f;
        float minYTopForLine = Float.MAX_VALUE;
        float endOfLastTextX = -1.0f;
        float lastWordSpacing = -1.0f;
        float maxHeightForLine = -1.0f;
        PositionWrapper lastPosition = null;
        PositionWrapper lastLineStartPosition = null;
        boolean startOfPage = true;
        if (this.charactersByArticle.size() > 0) {
            this.writePageStart();
        }
        for (List<TextPosition> textList : this.charactersByArticle) {
            if (this.getSortByPosition()) {
                TextPositionComparator comparator = new TextPositionComparator();
                try {
                    Collections.sort(textList, comparator);
                }
                catch (IllegalArgumentException e) {
                    IterativeMergeSort.sort(textList, comparator);
                }
            }
            this.startArticle();
            boolean startOfArticle = true;
            ArrayList<LineItem> line = new ArrayList<LineItem>();
            Iterator<TextPosition> textIter = textList.iterator();
            float previousAveCharWidth = -1.0f;
            while (textIter.hasNext()) {
                float positionHeight;
                float positionWidth;
                float positionY;
                float positionX;
                TextPosition position = textIter.next();
                PositionWrapper current = new PositionWrapper(position);
                String characterValue = position.getUnicode();
                if (lastPosition != null && (position.getFont() != lastPosition.getTextPosition().getFont() || position.getFontSize() != lastPosition.getTextPosition().getFontSize())) {
                    previousAveCharWidth = -1.0f;
                }
                if (this.getSortByPosition()) {
                    positionX = position.getXDirAdj();
                    positionY = position.getYDirAdj();
                    positionWidth = position.getWidthDirAdj();
                    positionHeight = position.getHeightDir();
                } else {
                    positionX = position.getX();
                    positionY = position.getY();
                    positionWidth = position.getWidth();
                    positionHeight = position.getHeight();
                }
                int wordCharCount = position.getIndividualWidths().length;
                float wordSpacing = position.getWidthOfSpace();
                float deltaSpace = wordSpacing == 0.0f || Float.isNaN(wordSpacing) ? Float.MAX_VALUE : (lastWordSpacing < 0.0f ? wordSpacing * this.getSpacingTolerance() : (wordSpacing + lastWordSpacing) / 2.0f * this.getSpacingTolerance());
                float averageCharWidth = previousAveCharWidth < 0.0f ? positionWidth / (float)wordCharCount : (previousAveCharWidth + positionWidth / (float)wordCharCount) / 2.0f;
                float deltaCharWidth = averageCharWidth * this.getAverageCharTolerance();
                float expectedStartOfNextWordX = -3.4028235E38f;
                if (endOfLastTextX != -1.0f) {
                    expectedStartOfNextWordX = endOfLastTextX + Math.min(deltaSpace, deltaCharWidth);
                }
                if (lastPosition != null) {
                    if (startOfArticle) {
                        lastPosition.setArticleStart();
                        startOfArticle = false;
                    }
                    if (!this.overlap(positionY, positionHeight, maxYForLine, maxHeightForLine)) {
                        this.writeLine(this.normalize(line));
                        line.clear();
                        lastLineStartPosition = this.handleLineSeparation(current, lastPosition, lastLineStartPosition, maxHeightForLine);
                        expectedStartOfNextWordX = -3.4028235E38f;
                        maxYForLine = -3.4028235E38f;
                        maxHeightForLine = -1.0f;
                        minYTopForLine = Float.MAX_VALUE;
                    }
                    if (expectedStartOfNextWordX != -3.4028235E38f && expectedStartOfNextWordX < positionX && (this.wordSeparator.isEmpty() || lastPosition.getTextPosition().getUnicode() != null && !lastPosition.getTextPosition().getUnicode().endsWith(this.wordSeparator))) {
                        line.add(LineItem.getWordSeparator());
                    }
                    if (Math.abs(position.getX() - lastPosition.getTextPosition().getX()) > wordSpacing + deltaSpace) {
                        maxYForLine = -3.4028235E38f;
                        maxHeightForLine = -1.0f;
                        minYTopForLine = Float.MAX_VALUE;
                    }
                }
                if (positionY >= maxYForLine) {
                    maxYForLine = positionY;
                }
                endOfLastTextX = positionX + positionWidth;
                if (characterValue != null) {
                    if (startOfPage && lastPosition == null) {
                        this.writeParagraphStart();
                    }
                    line.add(new LineItem(position));
                }
                maxHeightForLine = Math.max(maxHeightForLine, positionHeight);
                minYTopForLine = Math.min(minYTopForLine, positionY - positionHeight);
                lastPosition = current;
                if (startOfPage) {
                    lastPosition.setParagraphStart();
                    lastPosition.setLineStart();
                    lastLineStartPosition = lastPosition;
                    startOfPage = false;
                }
                lastWordSpacing = wordSpacing;
                previousAveCharWidth = averageCharWidth;
            }
            if (line.size() > 0) {
                this.writeLine(this.normalize(line));
                this.writeParagraphEnd();
            }
            this.endArticle();
        }
        this.writePageEnd();
    }

    private boolean overlap(float y1, float height1, float y2, float height2) {
        return this.within(y1, y2, 0.1f) || y2 <= y1 && y2 >= y1 - height1 || y1 <= y2 && y1 >= y2 - height2;
    }

    protected void writeLineSeparator() throws IOException {
        this.output.write(this.getLineSeparator());
    }

    protected void writeWordSeparator() throws IOException {
        this.output.write(this.getWordSeparator());
    }

    protected void writeCharacters(TextPosition text) throws IOException {
        this.output.write(text.getUnicode());
    }

    protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
        this.writeString(text);
    }

    protected void writeString(String text) throws IOException {
        this.output.write(text);
    }

    private boolean within(float first, float second, float variance) {
        return second < first + variance && second > first - variance;
    }

    @Override
    protected void processTextPosition(TextPosition text) {
        boolean showCharacter = true;
        if (this.suppressDuplicateOverlappingText) {
            showCharacter = false;
            String textCharacter = text.getUnicode();
            float textX = text.getX();
            float textY = text.getY();
            TreeMap<Float, TreeSet<Float>> sameTextCharacters = this.characterListMapping.get(textCharacter);
            if (sameTextCharacters == null) {
                sameTextCharacters = new TreeMap();
                this.characterListMapping.put(textCharacter, sameTextCharacters);
            }
            boolean suppressCharacter = false;
            float tolerance = text.getWidth() / (float)textCharacter.length() / 3.0f;
            SortedMap<Float, TreeSet<Float>> xMatches = sameTextCharacters.subMap(Float.valueOf(textX - tolerance), Float.valueOf(textX + tolerance));
            for (TreeSet<Float> xMatch : xMatches.values()) {
                SortedSet<Float> yMatches = xMatch.subSet(Float.valueOf(textY - tolerance), Float.valueOf(textY + tolerance));
                if (yMatches.isEmpty()) continue;
                suppressCharacter = true;
                break;
            }
            if (!suppressCharacter) {
                TreeSet<Float> ySet = sameTextCharacters.get(Float.valueOf(textX));
                if (ySet == null) {
                    ySet = new TreeSet();
                    sameTextCharacters.put(Float.valueOf(textX), ySet);
                }
                ySet.add(Float.valueOf(textY));
                showCharacter = true;
            }
        }
        if (showCharacter) {
            int articleDivisionIndex;
            List<TextPosition> textList;
            int foundArticleDivisionIndex = -1;
            int notFoundButFirstLeftAndAboveArticleDivisionIndex = -1;
            int notFoundButFirstLeftArticleDivisionIndex = -1;
            int notFoundButFirstAboveArticleDivisionIndex = -1;
            float x = text.getX();
            float y = text.getY();
            if (this.shouldSeparateByBeads) {
                for (int i = 0; i < this.beadRectangles.size() && foundArticleDivisionIndex == -1; ++i) {
                    PDRectangle rect = this.beadRectangles.get(i);
                    if (rect != null) {
                        if (rect.contains(x, y)) {
                            foundArticleDivisionIndex = i * 2 + 1;
                            continue;
                        }
                        if ((x < rect.getLowerLeftX() || y < rect.getUpperRightY()) && notFoundButFirstLeftAndAboveArticleDivisionIndex == -1) {
                            notFoundButFirstLeftAndAboveArticleDivisionIndex = i * 2;
                            continue;
                        }
                        if (x < rect.getLowerLeftX() && notFoundButFirstLeftArticleDivisionIndex == -1) {
                            notFoundButFirstLeftArticleDivisionIndex = i * 2;
                            continue;
                        }
                        if (!(y < rect.getUpperRightY()) || notFoundButFirstAboveArticleDivisionIndex != -1) continue;
                        notFoundButFirstAboveArticleDivisionIndex = i * 2;
                        continue;
                    }
                    foundArticleDivisionIndex = 0;
                }
            } else {
                foundArticleDivisionIndex = 0;
            }
            if ((textList = this.charactersByArticle.get(articleDivisionIndex = foundArticleDivisionIndex != -1 ? foundArticleDivisionIndex : (notFoundButFirstLeftAndAboveArticleDivisionIndex != -1 ? notFoundButFirstLeftAndAboveArticleDivisionIndex : (notFoundButFirstLeftArticleDivisionIndex != -1 ? notFoundButFirstLeftArticleDivisionIndex : (notFoundButFirstAboveArticleDivisionIndex != -1 ? notFoundButFirstAboveArticleDivisionIndex : this.charactersByArticle.size() - 1))))).isEmpty()) {
                textList.add(text);
            } else {
                TextPosition previousTextPosition = textList.get(textList.size() - 1);
                if (text.isDiacritic() && previousTextPosition.contains(text)) {
                    previousTextPosition.mergeDiacritic(text);
                } else if (previousTextPosition.isDiacritic() && text.contains(previousTextPosition)) {
                    text.mergeDiacritic(previousTextPosition);
                    textList.remove(textList.size() - 1);
                    textList.add(text);
                } else {
                    textList.add(text);
                }
            }
        }
    }

    public int getStartPage() {
        return this.startPage;
    }

    public void setStartPage(int startPageValue) {
        this.startPage = startPageValue;
    }

    public int getEndPage() {
        return this.endPage;
    }

    public void setEndPage(int endPageValue) {
        this.endPage = endPageValue;
    }

    public void setLineSeparator(String separator) {
        this.lineSeparator = separator;
    }

    public String getLineSeparator() {
        return this.lineSeparator;
    }

    public String getWordSeparator() {
        return this.wordSeparator;
    }

    public void setWordSeparator(String separator) {
        this.wordSeparator = separator;
    }

    public boolean getSuppressDuplicateOverlappingText() {
        return this.suppressDuplicateOverlappingText;
    }

    protected int getCurrentPageNo() {
        return this.currentPageNo;
    }

    protected Writer getOutput() {
        return this.output;
    }

    protected List<List<TextPosition>> getCharactersByArticle() {
        return this.charactersByArticle;
    }

    public void setSuppressDuplicateOverlappingText(boolean suppressDuplicateOverlappingTextValue) {
        this.suppressDuplicateOverlappingText = suppressDuplicateOverlappingTextValue;
    }

    public boolean getSeparateByBeads() {
        return this.shouldSeparateByBeads;
    }

    public void setShouldSeparateByBeads(boolean aShouldSeparateByBeads) {
        this.shouldSeparateByBeads = aShouldSeparateByBeads;
    }

    public PDOutlineItem getEndBookmark() {
        return this.endBookmark;
    }

    public void setEndBookmark(PDOutlineItem aEndBookmark) {
        this.endBookmark = aEndBookmark;
    }

    public PDOutlineItem getStartBookmark() {
        return this.startBookmark;
    }

    public void setStartBookmark(PDOutlineItem aStartBookmark) {
        this.startBookmark = aStartBookmark;
    }

    public boolean getAddMoreFormatting() {
        return this.addMoreFormatting;
    }

    public void setAddMoreFormatting(boolean newAddMoreFormatting) {
        this.addMoreFormatting = newAddMoreFormatting;
    }

    public boolean getSortByPosition() {
        return this.sortByPosition;
    }

    public void setSortByPosition(boolean newSortByPosition) {
        this.sortByPosition = newSortByPosition;
    }

    public float getSpacingTolerance() {
        return this.spacingTolerance;
    }

    public void setSpacingTolerance(float spacingToleranceValue) {
        this.spacingTolerance = spacingToleranceValue;
    }

    public float getAverageCharTolerance() {
        return this.averageCharTolerance;
    }

    public void setAverageCharTolerance(float averageCharToleranceValue) {
        this.averageCharTolerance = averageCharToleranceValue;
    }

    public float getIndentThreshold() {
        return this.indentThreshold;
    }

    public void setIndentThreshold(float indentThresholdValue) {
        this.indentThreshold = indentThresholdValue;
    }

    public float getDropThreshold() {
        return this.dropThreshold;
    }

    public void setDropThreshold(float dropThresholdValue) {
        this.dropThreshold = dropThresholdValue;
    }

    public String getParagraphStart() {
        return this.paragraphStart;
    }

    public void setParagraphStart(String s) {
        this.paragraphStart = s;
    }

    public String getParagraphEnd() {
        return this.paragraphEnd;
    }

    public void setParagraphEnd(String s) {
        this.paragraphEnd = s;
    }

    public String getPageStart() {
        return this.pageStart;
    }

    public void setPageStart(String pageStartValue) {
        this.pageStart = pageStartValue;
    }

    public String getPageEnd() {
        return this.pageEnd;
    }

    public void setPageEnd(String pageEndValue) {
        this.pageEnd = pageEndValue;
    }

    public String getArticleStart() {
        return this.articleStart;
    }

    public void setArticleStart(String articleStartValue) {
        this.articleStart = articleStartValue;
    }

    public String getArticleEnd() {
        return this.articleEnd;
    }

    public void setArticleEnd(String articleEndValue) {
        this.articleEnd = articleEndValue;
    }

    private PositionWrapper handleLineSeparation(PositionWrapper current, PositionWrapper lastPosition, PositionWrapper lastLineStartPosition, float maxHeightForLine) throws IOException {
        current.setLineStart();
        this.isParagraphSeparation(current, lastPosition, lastLineStartPosition, maxHeightForLine);
        lastLineStartPosition = current;
        if (current.isParagraphStart()) {
            if (lastPosition.isArticleStart()) {
                if (lastPosition.isLineStart()) {
                    this.writeLineSeparator();
                }
                this.writeParagraphStart();
            } else {
                this.writeLineSeparator();
                this.writeParagraphSeparator();
            }
        } else {
            this.writeLineSeparator();
        }
        return lastLineStartPosition;
    }

    private void isParagraphSeparation(PositionWrapper position, PositionWrapper lastPosition, PositionWrapper lastLineStartPosition, float maxHeightForLine) {
        boolean result = false;
        if (lastLineStartPosition == null) {
            result = true;
        } else {
            float yGap = Math.abs(position.getTextPosition().getYDirAdj() - lastPosition.getTextPosition().getYDirAdj());
            float newYVal = this.multiplyFloat(this.getDropThreshold(), maxHeightForLine);
            float xGap = position.getTextPosition().getXDirAdj() - lastLineStartPosition.getTextPosition().getXDirAdj();
            float newXVal = this.multiplyFloat(this.getIndentThreshold(), position.getTextPosition().getWidthOfSpace());
            float positionWidth = this.multiplyFloat(0.25f, position.getTextPosition().getWidth());
            if (yGap > newYVal) {
                result = true;
            } else if (xGap > newXVal) {
                if (!lastLineStartPosition.isParagraphStart()) {
                    result = true;
                } else {
                    position.setHangingIndent();
                }
            } else if (xGap < -position.getTextPosition().getWidthOfSpace()) {
                if (!lastLineStartPosition.isParagraphStart()) {
                    result = true;
                }
            } else if (Math.abs(xGap) < positionWidth) {
                Pattern currentPattern;
                Pattern liPattern;
                if (lastLineStartPosition.isHangingIndent()) {
                    position.setHangingIndent();
                } else if (lastLineStartPosition.isParagraphStart() && (liPattern = this.matchListItemPattern(lastLineStartPosition)) != null && liPattern == (currentPattern = this.matchListItemPattern(position))) {
                    result = true;
                }
            }
        }
        if (result) {
            position.setParagraphStart();
        }
    }

    private float multiplyFloat(float value1, float value2) {
        return (float)Math.round(value1 * value2 * 1000.0f) / 1000.0f;
    }

    protected void writeParagraphSeparator() throws IOException {
        this.writeParagraphEnd();
        this.writeParagraphStart();
    }

    protected void writeParagraphStart() throws IOException {
        if (this.inParagraph) {
            this.writeParagraphEnd();
            this.inParagraph = false;
        }
        this.output.write(this.getParagraphStart());
        this.inParagraph = true;
    }

    protected void writeParagraphEnd() throws IOException {
        if (!this.inParagraph) {
            this.writeParagraphStart();
        }
        this.output.write(this.getParagraphEnd());
        this.inParagraph = false;
    }

    protected void writePageStart() throws IOException {
        this.output.write(this.getPageStart());
    }

    protected void writePageEnd() throws IOException {
        this.output.write(this.getPageEnd());
    }

    private Pattern matchListItemPattern(PositionWrapper pw) {
        TextPosition tp = pw.getTextPosition();
        String txt = tp.getUnicode();
        return PDFTextStripper.matchPattern(txt, this.getListItemPatterns());
    }

    protected void setListItemPatterns(List<Pattern> patterns) {
        this.listOfPatterns = patterns;
    }

    protected List<Pattern> getListItemPatterns() {
        if (this.listOfPatterns == null) {
            this.listOfPatterns = new ArrayList<Pattern>();
            for (String expression : LIST_ITEM_EXPRESSIONS) {
                Pattern p = Pattern.compile(expression);
                this.listOfPatterns.add(p);
            }
        }
        return this.listOfPatterns;
    }

    protected static Pattern matchPattern(String string, List<Pattern> patterns) {
        for (Pattern p : patterns) {
            if (!p.matcher(string).matches()) continue;
            return p;
        }
        return null;
    }

    private void writeLine(List<WordWithTextPositions> line) throws IOException {
        int numberOfStrings = line.size();
        for (int i = 0; i < numberOfStrings; ++i) {
            WordWithTextPositions word = line.get(i);
            this.writeString(word.getText(), word.getTextPositions());
            if (i >= numberOfStrings - 1) continue;
            this.writeWordSeparator();
        }
    }

    private List<WordWithTextPositions> normalize(List<LineItem> line) {
        LinkedList<WordWithTextPositions> normalized = new LinkedList<WordWithTextPositions>();
        StringBuilder lineBuilder = new StringBuilder();
        ArrayList<TextPosition> wordPositions = new ArrayList<TextPosition>();
        for (LineItem item : line) {
            lineBuilder = this.normalizeAdd(normalized, lineBuilder, wordPositions, item);
        }
        if (lineBuilder.length() > 0) {
            normalized.add(this.createWord(lineBuilder.toString(), wordPositions));
        }
        return normalized;
    }

    private String handleDirection(String word) {
        Bidi bidi = new Bidi(word, -2);
        if (!bidi.isMixed() && bidi.getBaseLevel() == 0) {
            return word;
        }
        int runCount = bidi.getRunCount();
        byte[] levels = new byte[runCount];
        Object[] runs = new Integer[runCount];
        for (int i = 0; i < runCount; ++i) {
            levels[i] = (byte)bidi.getRunLevel(i);
            runs[i] = i;
        }
        Bidi.reorderVisually(levels, 0, runs, 0, runCount);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < runCount; ++i) {
            int index = (Integer)runs[i];
            int start = bidi.getRunStart(index);
            int end = bidi.getRunLimit(index);
            byte level = levels[index];
            if ((level & 1) != 0) {
                while (--end >= start) {
                    char character = word.charAt(end);
                    if (Character.isMirrored(word.codePointAt(end))) {
                        if (MIRRORING_CHAR_MAP.containsKey(Character.valueOf(character))) {
                            result.append(MIRRORING_CHAR_MAP.get(Character.valueOf(character)));
                            continue;
                        }
                        result.append(character);
                        continue;
                    }
                    result.append(character);
                }
                continue;
            }
            result.append(word, start, end);
        }
        return result.toString();
    }

    private static void parseBidiFile(InputStream inputStream) throws IOException {
        String s;
        LineNumberReader rd = new LineNumberReader(new InputStreamReader(inputStream));
        while ((s = rd.readLine()) != null) {
            int comment = s.indexOf(35);
            if (comment != -1) {
                s = s.substring(0, comment);
            }
            if (s.length() < 2) continue;
            StringTokenizer st = new StringTokenizer(s, ";");
            int nFields = st.countTokens();
            Character[] fields = new Character[nFields];
            for (int i = 0; i < nFields; ++i) {
                fields[i] = Character.valueOf((char)Integer.parseInt(st.nextToken().trim(), 16));
            }
            if (fields.length != 2) continue;
            MIRRORING_CHAR_MAP.put(fields[0], fields[1]);
        }
    }

    private WordWithTextPositions createWord(String word, List<TextPosition> wordPositions) {
        return new WordWithTextPositions(this.normalizeWord(word), wordPositions);
    }

    private String normalizeWord(String word) {
        int q;
        StringBuilder builder = null;
        int p = 0;
        int strLength = word.length();
        for (q = 0; q < strLength; ++q) {
            char c = word.charAt(q);
            if (('\ufb00' > c || c > '\ufdff') && ('\ufe70' > c || c > '\ufeff')) continue;
            if (builder == null) {
                builder = new StringBuilder(strLength * 2);
            }
            builder.append(word, p, q);
            if (c == '\ufdf2' && q > 0 && (word.charAt(q - 1) == '\u0627' || word.charAt(q - 1) == '\ufe8d')) {
                builder.append("\u0644\u0644\u0647");
            } else {
                String normalized = Normalizer.normalize(word.substring(q, q + 1), Normalizer.Form.NFKC).trim();
                if ('\ufb1d' <= c && normalized.length() > 1) {
                    normalized = new StringBuilder(normalized).reverse().toString();
                }
                builder.append(normalized);
            }
            p = q + 1;
        }
        if (builder == null) {
            return this.handleDirection(word);
        }
        builder.append(word, p, q);
        return this.handleDirection(builder.toString());
    }

    private StringBuilder normalizeAdd(List<WordWithTextPositions> normalized, StringBuilder lineBuilder, List<TextPosition> wordPositions, LineItem item) {
        if (item.isWordSeparator()) {
            normalized.add(this.createWord(lineBuilder.toString(), new ArrayList<TextPosition>(wordPositions)));
            lineBuilder = new StringBuilder();
            wordPositions.clear();
        } else {
            TextPosition text = item.getTextPosition();
            lineBuilder.append(text.getVisuallyOrderedUnicode());
            wordPositions.add(text);
        }
        return lineBuilder;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        String strDrop = null;
        String strIndent = null;
        try {
            String className = PDFTextStripper.class.getSimpleName().toLowerCase();
            String prop = className + ".indent";
            strIndent = System.getProperty(prop);
            prop = className + ".drop";
            strDrop = System.getProperty(prop);
        }
        catch (SecurityException className) {
            // empty catch block
        }
        if (strIndent != null && strIndent.length() > 0) {
            try {
                defaultIndentThreshold = Float.parseFloat(strIndent);
            }
            catch (NumberFormatException className) {
                // empty catch block
            }
        }
        if (strDrop != null && strDrop.length() > 0) {
            try {
                defaultDropThreshold = Float.parseFloat(strDrop);
            }
            catch (NumberFormatException className) {
                // empty catch block
            }
        }
        LIST_ITEM_EXPRESSIONS = new String[]{"\\.", "\\d+\\.", "\\[\\d+\\]", "\\d+\\)", "[A-Z]\\.", "[a-z]\\.", "[A-Z]\\)", "[a-z]\\)", "[IVXL]+\\.", "[ivxl]+\\."};
        MIRRORING_CHAR_MAP = new HashMap<Character, Character>();
        String path = "/org/apache/pdfbox/resources/text/BidiMirroring.txt";
        BufferedInputStream input = new BufferedInputStream(PDFTextStripper.class.getResourceAsStream(path));
        try {
            PDFTextStripper.parseBidiFile(input);
        }
        catch (IOException e) {
            LOG.warn((Object)("Could not parse BidiMirroring.txt, mirroring char map will be empty: " + e.getMessage()));
        }
        finally {
            try {
                ((InputStream)input).close();
            }
            catch (IOException e) {
                LOG.error((Object)"Could not close BidiMirroring.txt ", (Throwable)e);
            }
        }
    }

    private static final class PositionWrapper {
        private boolean isLineStart = false;
        private boolean isParagraphStart = false;
        private boolean isPageBreak = false;
        private boolean isHangingIndent = false;
        private boolean isArticleStart = false;
        private TextPosition position = null;

        PositionWrapper(TextPosition position) {
            this.position = position;
        }

        public TextPosition getTextPosition() {
            return this.position;
        }

        public boolean isLineStart() {
            return this.isLineStart;
        }

        public void setLineStart() {
            this.isLineStart = true;
        }

        public boolean isParagraphStart() {
            return this.isParagraphStart;
        }

        public void setParagraphStart() {
            this.isParagraphStart = true;
        }

        public boolean isArticleStart() {
            return this.isArticleStart;
        }

        public void setArticleStart() {
            this.isArticleStart = true;
        }

        public boolean isPageBreak() {
            return this.isPageBreak;
        }

        public void setPageBreak() {
            this.isPageBreak = true;
        }

        public boolean isHangingIndent() {
            return this.isHangingIndent;
        }

        public void setHangingIndent() {
            this.isHangingIndent = true;
        }
    }

    private static final class WordWithTextPositions {
        String text;
        List<TextPosition> textPositions;

        WordWithTextPositions(String word, List<TextPosition> positions) {
            this.text = word;
            this.textPositions = positions;
        }

        public String getText() {
            return this.text;
        }

        public List<TextPosition> getTextPositions() {
            return this.textPositions;
        }
    }

    private static final class LineItem {
        public static LineItem WORD_SEPARATOR = new LineItem();
        private final TextPosition textPosition;

        public static LineItem getWordSeparator() {
            return WORD_SEPARATOR;
        }

        private LineItem() {
            this.textPosition = null;
        }

        LineItem(TextPosition textPosition) {
            this.textPosition = textPosition;
        }

        public TextPosition getTextPosition() {
            return this.textPosition;
        }

        public boolean isWordSeparator() {
            return this.textPosition == null;
        }
    }
}

