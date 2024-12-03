/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aspose.words.BookmarkStart
 *  com.aspose.words.Cell
 *  com.aspose.words.CellCollection
 *  com.aspose.words.ConvertUtil
 *  com.aspose.words.Document
 *  com.aspose.words.DocumentVisitor
 *  com.aspose.words.FieldEnd
 *  com.aspose.words.FieldSeparator
 *  com.aspose.words.FieldStart
 *  com.aspose.words.Footnote
 *  com.aspose.words.HeaderFooter
 *  com.aspose.words.ImageSaveOptions
 *  com.aspose.words.ListFormat
 *  com.aspose.words.ListLevel
 *  com.aspose.words.Node
 *  com.aspose.words.Paragraph
 *  com.aspose.words.ParagraphFormat
 *  com.aspose.words.Row
 *  com.aspose.words.RowCollection
 *  com.aspose.words.Run
 *  com.aspose.words.Shape
 *  com.aspose.words.Table
 */
package com.atlassian.plugins.conversion.confluence.dom.doc2wiki;

import com.aspose.words.BookmarkStart;
import com.aspose.words.Cell;
import com.aspose.words.CellCollection;
import com.aspose.words.ConvertUtil;
import com.aspose.words.Document;
import com.aspose.words.DocumentVisitor;
import com.aspose.words.FieldEnd;
import com.aspose.words.FieldSeparator;
import com.aspose.words.FieldStart;
import com.aspose.words.Footnote;
import com.aspose.words.HeaderFooter;
import com.aspose.words.ImageSaveOptions;
import com.aspose.words.ListFormat;
import com.aspose.words.ListLevel;
import com.aspose.words.Node;
import com.aspose.words.Paragraph;
import com.aspose.words.ParagraphFormat;
import com.aspose.words.Row;
import com.aspose.words.RowCollection;
import com.aspose.words.Run;
import com.aspose.words.Shape;
import com.aspose.words.Table;
import com.atlassian.plugins.conversion.confluence.dom.FormattingState;
import com.atlassian.plugins.conversion.confluence.dom.ImageSizeException;
import com.atlassian.plugins.conversion.confluence.dom.ImportContext;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.TableState;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.WordImageData;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.WordImageDataFactory;
import com.atlassian.plugins.conversion.confluence.parser.ConfluenceImage;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;
import javax.imageio.ImageIO;

public class Doc2Wiki<T extends ImportContext>
extends DocumentVisitor {
    protected static final String BLOCKQUOTE = "blockquote";
    protected static final String IMAGE_PREFIX = "worddav";
    private int _nestingLevel = 0;
    private int _fieldNestingLevel = 0;
    private boolean _inheritIgnore;
    private boolean _ignore;
    private String _currentHyperlink;
    private TableState _currentState;
    private Stack<TableState> _stateStack;
    private HashSet<String> _imgHashes;
    private boolean _hasFootnotes;
    private boolean _hasShape;
    protected StringBuilder _out;
    private boolean _tableHeading;
    private FormattingState _formatState;
    protected T _importContext;
    private boolean _firstRun;
    private MessageDigest _digest;
    private boolean _doFootnotes;
    private final int[] tempResult = new int[]{0, 0};

    public Doc2Wiki(T importContext, boolean doFootnotes) {
        this._out = new StringBuilder();
        this._formatState = new FormattingState();
        this._importContext = importContext;
        this._stateStack = new Stack();
        this._imgHashes = new HashSet();
        this._doFootnotes = doFootnotes;
        try {
            this._digest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            // empty catch block
        }
    }

    public int visitBookmarkStart(BookmarkStart bkmkStart) throws Exception {
        this._out.append("{anchor:").append(bkmkStart.getName()).append("}");
        this._firstRun = false;
        return 0;
    }

    public int visitCellEnd(Cell cell) throws Exception {
        this._formatState.finishBlock(this._out);
        if (this._firstRun) {
            this._out.append(' ');
        }
        String barStr = "|";
        if (this._tableHeading) {
            barStr = "||";
        }
        this._out.append(barStr);
        double cellEnd = cell.getCellFormat().getWidth() + this._currentState.getRowLeft();
        int[] range = this.findCellRange(cellEnd);
        int colStartIdx = range[0];
        int colEndIdx = range[1];
        if (colEndIdx - colStartIdx > 1) {
            for (int y = colStartIdx; y < colEndIdx - 1; ++y) {
                this._out.append(' ').append(barStr);
            }
        }
        this._currentState.setRowLeft(cellEnd);
        return 0;
    }

    private int[] findCellRange(double cellEnd) {
        int x;
        int colStartIdx = 0;
        int colEndIdx = 0;
        ArrayList<Double> currentColumns = this._currentState.getCurrentColumns();
        for (x = 0; x < currentColumns.size(); ++x) {
            if (currentColumns.get(x).doubleValue() != this._currentState.getRowLeft()) continue;
            colStartIdx = x;
            break;
        }
        while (x < currentColumns.size()) {
            if (currentColumns.get(x) == cellEnd) {
                colEndIdx = x;
                break;
            }
            ++x;
        }
        this.tempResult[0] = colStartIdx;
        this.tempResult[1] = colEndIdx;
        return this.tempResult;
    }

    public int visitCellStart(Cell cell) throws Exception {
        this._firstRun = true;
        return super.visitCellStart(cell);
    }

    public int visitDocumentEnd(Document arg0) throws Exception {
        this.pageEnd();
        this._importContext.finish(this._out);
        return super.visitDocumentEnd(arg0);
    }

    public int visitDocumentStart(Document arg0) throws Exception {
        return super.visitDocumentStart(arg0);
    }

    public int visitFieldEnd(FieldEnd arg0) throws Exception {
        if (this._fieldNestingLevel == 1) {
            this._inheritIgnore = false;
        }
        this._ignore = false;
        if (arg0.getFieldType() == 88) {
            this._formatState.finishBlock(this._out);
            if (this._currentHyperlink != null) {
                this._out.append("|");
                this._out.append(this._currentHyperlink);
                this._out.append("]");
            }
            this._currentHyperlink = null;
        }
        --this._fieldNestingLevel;
        return 0;
    }

    public int visitFieldSeparator(FieldSeparator arg0) throws Exception {
        if (this._fieldNestingLevel == 1) {
            this._inheritIgnore = false;
        }
        if (arg0.getFieldType() != 37) {
            this._ignore = false;
        }
        return 0;
    }

    public int visitFieldStart(FieldStart start) throws Exception {
        ++this._fieldNestingLevel;
        if (this._fieldNestingLevel == 1) {
            this._inheritIgnore = true;
        }
        this._ignore = true;
        int fieldType = start.getFieldType();
        if (fieldType == 88) {
            this._formatState.finishBlock(this._out, false);
            StringBuffer codeBuf = new StringBuffer();
            boolean skip = false;
            int currentLevel = this._fieldNestingLevel;
            for (Node n = start.getNextSibling(); !(n == null || n instanceof FieldSeparator && currentLevel <= this._fieldNestingLevel); n = n.getNextSibling()) {
                if (n instanceof FieldStart) {
                    ++currentLevel;
                    skip = true;
                    continue;
                }
                if (n instanceof FieldSeparator) {
                    skip = false;
                    continue;
                }
                if (n instanceof FieldEnd) {
                    --currentLevel;
                    skip = false;
                    continue;
                }
                if (skip) continue;
                codeBuf.append(n.getText());
            }
            this._out.append("[");
            this._currentHyperlink = this._importContext.createHyperlinkReference(codeBuf);
        }
        return 0;
    }

    public int visitHeaderFooterStart(HeaderFooter arg0) throws Exception {
        return 1;
    }

    public int visitParagraphEnd(Paragraph paragraph) throws Exception {
        this._formatState.finishBlock(this._out);
        Node next = paragraph.getNextSibling();
        if (this._nestingLevel > 0 && next != null) {
            this._out.append(" \\\\");
            if (!(next instanceof Paragraph) || !((Paragraph)next).isEndOfCell() || next.getText().trim().length() > 0) {
                this._out.append("\r\n");
            } else {
                this._out.append(' ');
            }
        } else if (next != null) {
            if (!this.isVisible(paragraph.getText()) && !this._hasShape) {
                this._out.append("\\\\");
            }
            this._out.append("\r\n");
        }
        this._hasShape = false;
        return 0;
    }

    private boolean isVisible(String text) {
        int len = text.length();
        block3: for (int x = 0; x < len; ++x) {
            switch (text.charAt(x)) {
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    continue block3;
                }
                default: {
                    return true;
                }
            }
        }
        return false;
    }

    public int visitParagraphStart(Paragraph paragraph) throws Exception {
        ParagraphFormat format = paragraph.getParagraphFormat();
        ListFormat listFormat = paragraph.getListFormat();
        this._firstRun = true;
        if (listFormat != null && listFormat.isListItem() && this.getHeadingLevel(format) == 0) {
            ListLevel lvl = listFormat.getListLevel();
            int levelNum = listFormat.getListLevelNumber() + 1;
            char lvlChar = '#';
            if (lvl.getNumberStyle() == 23) {
                lvlChar = '*';
            }
            for (int x = 0; x < levelNum; ++x) {
                this._out.append(lvlChar);
            }
            this._out.append(' ');
        } else {
            this.handleListEndFunk(paragraph);
        }
        return this.handleParagraphStyle(paragraph, format);
    }

    protected int handleParagraphStyle(Paragraph paragraph, ParagraphFormat format) throws Exception {
        String text;
        int headingLvl = this.getHeadingLevel(format);
        if (headingLvl > 0 && (text = paragraph.getText().trim()).length() > 0) {
            this._out.append("h").append(headingLvl).append(".");
            paragraph.getParagraphFormat().setStyleIdentifier(0);
        }
        return 0;
    }

    private int getHeadingLevel(ParagraphFormat format) throws Exception {
        int headingLvl = 0;
        switch (format.getStyleIdentifier()) {
            case 1: {
                headingLvl = 1;
                break;
            }
            case 2: {
                headingLvl = 2;
                break;
            }
            case 3: {
                headingLvl = 3;
                break;
            }
            case 4: {
                headingLvl = 4;
                break;
            }
            case 5: {
                headingLvl = 5;
                break;
            }
            case 6: {
                headingLvl = 6;
                break;
            }
            case 7: {
                headingLvl = 7;
                break;
            }
            case 8: {
                headingLvl = 8;
                break;
            }
            case 9: {
                headingLvl = 9;
            }
        }
        if (headingLvl == 0) {
            headingLvl = format.getOutlineLevel() == 9 ? 0 : format.getOutlineLevel() + 1;
        }
        return headingLvl;
    }

    protected void handleListEndFunk(Paragraph paragraph) throws Exception {
        ListFormat lf;
        Node n = paragraph.getPreviousSibling();
        if (n instanceof Paragraph && !paragraph.isEndOfCell() && (lf = ((Paragraph)n).getListFormat()) != null && lf.isListItem()) {
            if (paragraph.isInCell()) {
                this._out.append(" \\\\");
            }
            this._out.append("\r\n");
        }
    }

    public int visitRowEnd(Row arg0) throws Exception {
        this._currentState.nextRow();
        if (this._tableHeading) {
            this._tableHeading = false;
        }
        this._out.append("\r\n");
        return 0;
    }

    public int visitRowStart(Row row) throws Exception {
        this._tableHeading = row.getRowFormat().getHeadingFormat();
        this._out.append(this._tableHeading ? "||" : "|");
        return 0;
    }

    public int visitRun(Run run) throws Exception {
        if (!this._inheritIgnore && !this._ignore) {
            if (this._currentHyperlink != null && run.getFont().getStyleIdentifier() == 85) {
                run.getFont().setStyleIdentifier(65);
            }
            this._formatState.processNextRun(this._out, run, this._firstRun);
        }
        this._firstRun = false;
        return 0;
    }

    public int visitShapeStart(Shape shape) throws Exception {
        if (shape.isHorizontalRule()) {
            this._out.append("----");
            this._hasShape = true;
        } else if (shape.hasImage() && shape.getImageData() != null && !shape.getImageData().isLinkOnly()) {
            if (shape.getAlternativeText().startsWith("wiki://") && this.handleExistingImage(shape)) {
                return 0;
            }
            this._hasShape = true;
            Dimension size = shape.getShapeRenderer().getSizeInPixels(1.0f, 96.0f);
            WordImageData imageData = WordImageDataFactory.create(shape.getImageData(), (Node)shape, size.width, size.height, shape.getImageData().getImageSize().getWidthPixels(), shape.getImageData().getImageSize().getHeightPixels());
            this.handleImage(imageData, (Node)shape);
        }
        return 0;
    }

    private void outputWikiImageDefinition(String txt, Node node) throws IOException {
        String text;
        if (!this._firstRun && this._formatState.getTrailingSpaces() == 0) {
            this._formatState.finishBlock(this._out);
            this._out.append(' ');
        } else {
            this._formatState.finishBlock(this._out);
        }
        this._out.append(txt);
        Node next = node.getNextSibling();
        if (next != null && (text = next.getText()).length() > 0 && !Character.isWhitespace(text.charAt(0))) {
            this._formatState.setTrailingSpaces(1);
        }
    }

    private boolean handleExistingImage(Shape shape) {
        String heightStr = String.valueOf((int)ConvertUtil.pointToPixel((double)shape.getHeight()));
        String widthStr = String.valueOf((int)ConvertUtil.pointToPixel((double)shape.getWidth()));
        String wikiTxt = shape.getAlternativeText().substring(7);
        try {
            StringReader reader = new StringReader(wikiTxt);
            ConfluenceImage parser = new ConfluenceImage(reader);
            parser.Image();
            if (this._importContext.imageExists(parser)) {
                wikiTxt = parser.changeWidthHeight(widthStr, heightStr);
                this.outputWikiImageDefinition(wikiTxt, (Node)shape);
                return true;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return false;
    }

    private void handleImage(WordImageData imageData, Node node) throws Exception {
        Dimension maxSize = this._importContext.getMaxImportedImageSize();
        if ((long)imageData.getHeight() * (long)imageData.getWidth() > (long)maxSize.height * (long)maxSize.width) {
            throw new ImageSizeException(new Dimension(imageData.getWidth(), imageData.getHeight()), maxSize);
        }
        Dimension limitedSize = this.calcLimitedSize(imageData.getOriginalWidth(), imageData.getOriginalHeight(), maxSize);
        String extension = null;
        String contentType = null;
        boolean isWmf = false;
        boolean isEmf = false;
        switch (imageData.getImageType()) {
            case 7: {
                extension = "bmp";
                contentType = "image/bmp";
                break;
            }
            case 2: {
                extension = "png";
                contentType = "image/png";
                isEmf = true;
                break;
            }
            case 5: {
                extension = "jpg";
                contentType = "image/jpeg";
                break;
            }
            case 4: {
                extension = "pct";
                contentType = "image/pict";
                break;
            }
            case 6: {
                extension = "png";
                contentType = "image/png";
                break;
            }
            case 3: {
                extension = "png";
                contentType = "image/png";
                isWmf = true;
            }
        }
        if (extension != null) {
            byte[] digest;
            byte[] buf;
            block19: {
                buf = null;
                digest = null;
                try {
                    if (isEmf || isWmf) {
                        ByteArrayOutputStream bout = new ByteArrayOutputStream();
                        digest = this._digest.digest(imageData.getImageBytes());
                        if (!this._imgHashes.contains(this.createHashString(digest))) {
                            bout = this.saveImageToOutputStream(imageData, node, limitedSize, bout);
                        }
                        buf = bout.toByteArray();
                    } else {
                        BufferedImage img = imageData.toImage();
                        if (img != null) {
                            img = this.resizeImage(img, imageData.getOriginalWidth(), imageData.getOriginalHeight());
                            ByteArrayOutputStream bout = new ByteArrayOutputStream();
                            ImageIO.write((RenderedImage)img, "png", bout);
                            buf = bout.toByteArray();
                            digest = this._digest.digest(buf);
                            extension = "png";
                            contentType = "image/png";
                            img.flush();
                        }
                    }
                }
                catch (Exception e) {
                    if (isWmf) {
                        extension = "wmf";
                        contentType = "image/x-wmf";
                    }
                    if (!isEmf) break block19;
                    extension = "emf";
                    contentType = "image/x-emf";
                }
            }
            if (buf == null) {
                buf = imageData.getImageBytes();
                digest = this._digest.digest(buf);
            }
            String hashBuf = this.createHashString(digest);
            String title = IMAGE_PREFIX + hashBuf + '.' + extension;
            if (!this._imgHashes.contains(hashBuf)) {
                this._importContext.importImage(title, contentType, buf);
                this._imgHashes.add(hashBuf);
            }
            int displayHeight = (double)imageData.getHeight() < limitedSize.getHeight() ? imageData.getHeight() : (int)limitedSize.getHeight();
            int displayWidth = (double)imageData.getWidth() < limitedSize.getWidth() ? imageData.getWidth() : (int)limitedSize.getWidth();
            this.outputWikiImageDefinition("!" + title + "|height=" + displayHeight + ",width=" + displayWidth + "!", imageData.getNode());
        }
    }

    private ByteArrayOutputStream saveImageToOutputStream(WordImageData imageData, Node node, Dimension limitedSize, ByteArrayOutputStream bout) throws Exception {
        ImageSaveOptions imageSaveOptions = new ImageSaveOptions(101);
        imageSaveOptions.setScale((float)(limitedSize.getWidth() / (double)imageData.getWidth()));
        if (node instanceof Shape) {
            ((Shape)node).getShapeRenderer().save((OutputStream)bout, imageSaveOptions);
        }
        return bout;
    }

    private BufferedImage resizeImage(BufferedImage img, int pixelWidth, int pixelHeight) {
        Dimension maxSize = this._importContext.getMaxImportedImageSize();
        Dimension limitedSize = this.calcLimitedSize(pixelWidth, pixelHeight, maxSize);
        if (new Dimension(img.getWidth(), img.getHeight()).equals(limitedSize)) {
            return img;
        }
        BufferedImage newImg = new BufferedImage(limitedSize.width, limitedSize.height, 2);
        Graphics2D g = (Graphics2D)newImg.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        Image scaled = img.getScaledInstance(limitedSize.width, limitedSize.height, 4);
        g.drawImage(scaled, 0, 0, null);
        return newImg;
    }

    private Dimension calcLimitedSize(int pixelWidth, int pixelHeight, Dimension maxSize) {
        if (pixelWidth > maxSize.width) {
            pixelHeight = pixelHeight * maxSize.width / pixelWidth;
            pixelWidth = maxSize.width;
        }
        if (pixelHeight > maxSize.height) {
            pixelWidth = pixelWidth * maxSize.height / pixelHeight;
            pixelHeight = maxSize.height;
        }
        return new Dimension(pixelWidth > 0 ? pixelWidth : 1, pixelHeight > 0 ? pixelHeight : 1);
    }

    private String createHashString(byte[] digest) {
        StringBuilder hashBuf = new StringBuilder(64);
        for (int x = 0; x < digest.length; ++x) {
            String str = Integer.toHexString(digest[x] & 0xFF);
            if (str.length() < 2) {
                str = "0" + str;
            }
            hashBuf.append(str);
        }
        return hashBuf.toString();
    }

    public int visitTableEnd(Table arg0) throws Exception {
        --this._nestingLevel;
        this._currentState = null;
        if (this._stateStack.size() > 0) {
            this._currentState = this._stateStack.pop();
        }
        return 0;
    }

    public int visitTableStart(Table table) throws Exception {
        if (this._nestingLevel >= 1) {
            this._stateStack.push(this._currentState);
        }
        this._currentState = new TableState();
        RowCollection rows = table.getRows();
        for (int x = 0; x < rows.getCount(); ++x) {
            Row r = rows.get(x);
            double current = 0.0;
            CellCollection cells = r.getCells();
            for (int y = 0; y < cells.getCount(); ++y) {
                Cell c = cells.get(y);
                this.placeColumn(current += c.getCellFormat().getWidth());
            }
        }
        ++this._nestingLevel;
        return 0;
    }

    public void pageEnd() {
        if (this._hasFootnotes) {
            this._out.append("\r\n----\r\n{display-footnotes}");
        }
    }

    public int visitFootnoteEnd(Footnote footnote) throws Exception {
        if (this._doFootnotes) {
            this._out.append("{footnote}");
        }
        return 0;
    }

    public int visitFootnoteStart(Footnote footnote) throws Exception {
        if (this._doFootnotes) {
            this._out.append("{footnote}");
            this._hasFootnotes = true;
        }
        return 0;
    }

    private void placeColumn(double right) {
        ArrayList<Double> currentColumns = this._currentState.getCurrentColumns();
        for (int x = 0; x < currentColumns.size(); ++x) {
            Double d = currentColumns.get(x);
            if (d > right) {
                currentColumns.add(x, right);
                return;
            }
            if (d != right) continue;
            return;
        }
        currentColumns.add(right);
    }

    public String getOutput() {
        return this._out.toString();
    }
}

