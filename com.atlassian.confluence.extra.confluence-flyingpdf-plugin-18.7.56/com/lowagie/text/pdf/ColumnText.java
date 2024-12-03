/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.SimpleTable;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.BidiLine;
import com.lowagie.text.pdf.PdfChunk;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfFont;
import com.lowagie.text.pdf.PdfLine;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPRow;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.draw.DrawInterface;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class ColumnText {
    public static final int AR_NOVOWEL = 1;
    public static final int AR_COMPOSEDTASHKEEL = 4;
    public static final int AR_LIG = 8;
    public static final int DIGITS_EN2AN = 32;
    public static final int DIGITS_AN2EN = 64;
    public static final int DIGITS_EN2AN_INIT_LR = 96;
    public static final int DIGITS_EN2AN_INIT_AL = 128;
    public static final int DIGIT_TYPE_AN = 0;
    public static final int DIGIT_TYPE_AN_EXTENDED = 256;
    protected int runDirection = 0;
    public static final float GLOBAL_SPACE_CHAR_RATIO = 0.0f;
    public static final int START_COLUMN = 0;
    public static final int NO_MORE_TEXT = 1;
    public static final int NO_MORE_COLUMN = 2;
    protected static final int LINE_STATUS_OK = 0;
    protected static final int LINE_STATUS_OFFLIMITS = 1;
    protected static final int LINE_STATUS_NOLINE = 2;
    protected float maxY;
    protected float minY;
    protected float leftX;
    protected float rightX;
    protected int alignment = 0;
    protected java.util.List<float[]> leftWall;
    protected java.util.List<float[]> rightWall;
    protected BidiLine bidiLine;
    protected float yLine;
    protected float currentLeading = 16.0f;
    protected float fixedLeading = 16.0f;
    protected float multipliedLeading = 0.0f;
    protected PdfContentByte canvas;
    protected PdfContentByte[] canvases;
    protected int lineStatus;
    protected float indent = 0.0f;
    protected float followingIndent = 0.0f;
    protected float rightIndent = 0.0f;
    protected float extraParagraphSpace = 0.0f;
    protected float rectangularWidth = -1.0f;
    protected boolean rectangularMode = false;
    private float spaceCharRatio = 0.0f;
    private boolean lastWasNewline = true;
    private int linesWritten;
    private float firstLineY;
    private boolean firstLineYDone = false;
    private int arabicOptions = 0;
    protected float descender;
    protected boolean composite = false;
    protected ColumnText compositeColumn;
    protected LinkedList<Element> compositeElements;
    protected int listIdx = 0;
    private boolean splittedRow;
    protected Phrase waitPhrase;
    private boolean useAscender = false;
    private float filledWidth;
    private boolean adjustFirstLine = true;

    public ColumnText(PdfContentByte canvas) {
        this.canvas = canvas;
    }

    public static ColumnText duplicate(ColumnText org) {
        ColumnText ct = new ColumnText(null);
        ct.setACopy(org);
        return ct;
    }

    public ColumnText setACopy(ColumnText org) {
        this.setSimpleVars(org);
        if (org.bidiLine != null) {
            this.bidiLine = new BidiLine(org.bidiLine);
        }
        return this;
    }

    protected void setSimpleVars(ColumnText org) {
        this.maxY = org.maxY;
        this.minY = org.minY;
        this.alignment = org.alignment;
        this.leftWall = null;
        if (org.leftWall != null) {
            this.leftWall = new ArrayList<float[]>(org.leftWall);
        }
        this.rightWall = null;
        if (org.rightWall != null) {
            this.rightWall = new ArrayList<float[]>(org.rightWall);
        }
        this.yLine = org.yLine;
        this.currentLeading = org.currentLeading;
        this.fixedLeading = org.fixedLeading;
        this.multipliedLeading = org.multipliedLeading;
        this.canvas = org.canvas;
        this.canvases = org.canvases;
        this.lineStatus = org.lineStatus;
        this.indent = org.indent;
        this.followingIndent = org.followingIndent;
        this.rightIndent = org.rightIndent;
        this.extraParagraphSpace = org.extraParagraphSpace;
        this.rectangularWidth = org.rectangularWidth;
        this.rectangularMode = org.rectangularMode;
        this.spaceCharRatio = org.spaceCharRatio;
        this.lastWasNewline = org.lastWasNewline;
        this.linesWritten = org.linesWritten;
        this.arabicOptions = org.arabicOptions;
        this.runDirection = org.runDirection;
        this.descender = org.descender;
        this.composite = org.composite;
        this.splittedRow = org.splittedRow;
        if (org.composite) {
            this.compositeElements = new LinkedList<Element>(org.compositeElements);
            if (this.splittedRow) {
                PdfPTable table = (PdfPTable)this.compositeElements.getFirst();
                this.compositeElements.set(0, new PdfPTable(table));
            }
            if (org.compositeColumn != null) {
                this.compositeColumn = ColumnText.duplicate(org.compositeColumn);
            }
        }
        this.listIdx = org.listIdx;
        this.firstLineY = org.firstLineY;
        this.leftX = org.leftX;
        this.rightX = org.rightX;
        this.firstLineYDone = org.firstLineYDone;
        this.waitPhrase = org.waitPhrase;
        this.useAscender = org.useAscender;
        this.filledWidth = org.filledWidth;
        this.adjustFirstLine = org.adjustFirstLine;
    }

    private void addWaitingPhrase() {
        if (this.bidiLine == null && this.waitPhrase != null) {
            this.bidiLine = new BidiLine();
            for (Element o : this.waitPhrase.getChunks()) {
                this.bidiLine.addChunk(new PdfChunk((Chunk)o, null));
            }
            this.waitPhrase = null;
        }
    }

    public void addText(Phrase phrase) {
        if (phrase == null || this.composite) {
            return;
        }
        this.addWaitingPhrase();
        if (this.bidiLine == null) {
            this.waitPhrase = phrase;
            return;
        }
        for (Element o : phrase.getChunks()) {
            this.bidiLine.addChunk(new PdfChunk((Chunk)o, null));
        }
    }

    public void setText(Phrase phrase) {
        this.bidiLine = null;
        this.composite = false;
        this.compositeColumn = null;
        this.compositeElements = null;
        this.listIdx = 0;
        this.splittedRow = false;
        this.waitPhrase = phrase;
    }

    public void addText(Chunk chunk) {
        if (chunk == null || this.composite) {
            return;
        }
        this.addText(new Phrase(chunk));
    }

    public void addElement(Element element) {
        if (element == null) {
            return;
        }
        if (element instanceof Image) {
            Image img = (Image)element;
            PdfPTable t = new PdfPTable(1);
            float w = img.getWidthPercentage();
            if (w == 0.0f) {
                t.setTotalWidth(img.getScaledWidth());
                t.setLockedWidth(true);
            } else {
                t.setWidthPercentage(w);
            }
            t.setSpacingAfter(img.getSpacingAfter());
            t.setSpacingBefore(img.getSpacingBefore());
            switch (img.getAlignment()) {
                case 0: {
                    t.setHorizontalAlignment(0);
                    break;
                }
                case 2: {
                    t.setHorizontalAlignment(2);
                    break;
                }
                default: {
                    t.setHorizontalAlignment(1);
                }
            }
            PdfPCell c = new PdfPCell(img, true);
            c.setPadding(0.0f);
            c.setBorder(img.getBorder());
            c.setBorderColor(img.getBorderColor());
            c.setBorderWidth(img.getBorderWidth());
            c.setBackgroundColor(img.getBackgroundColor());
            t.addCell(c);
            element = t;
        }
        if (element.type() == 10) {
            element = new Paragraph((Chunk)element);
        } else if (element.type() == 11) {
            element = new Paragraph((Phrase)element);
        }
        if (element instanceof SimpleTable) {
            try {
                element = ((SimpleTable)element).createPdfPTable();
            }
            catch (DocumentException e) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("element.not.allowed"));
            }
        } else if (element.type() != 12 && element.type() != 14 && element.type() != 23 && element.type() != 55) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("element.not.allowed"));
        }
        if (!this.composite) {
            this.composite = true;
            this.compositeElements = new LinkedList();
            this.bidiLine = null;
            this.waitPhrase = null;
        }
        this.compositeElements.add(element);
    }

    protected java.util.List<float[]> convertColumn(float[] cLine) {
        if (cLine.length < 4) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("no.valid.column.line.found"));
        }
        ArrayList<float[]> cc = new ArrayList<float[]>();
        for (int k = 0; k < cLine.length - 2 && cLine.length != k + 3; k += 2) {
            float x1 = cLine[k];
            float y1 = cLine[k + 1];
            float x2 = cLine[k + 2];
            float y2 = cLine[k + 3];
            if (y1 == y2) continue;
            float a = (x1 - x2) / (y1 - y2);
            float b = x1 - a * y1;
            float[] r = new float[]{Math.min(y1, y2), Math.max(y1, y2), a, b};
            cc.add(r);
            this.maxY = Math.max(this.maxY, r[1]);
            this.minY = Math.min(this.minY, r[0]);
        }
        if (cc.isEmpty()) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("no.valid.column.line.found"));
        }
        return cc;
    }

    protected float findLimitsPoint(java.util.List<float[]> wall) {
        this.lineStatus = 0;
        if (this.yLine < this.minY || this.yLine > this.maxY) {
            this.lineStatus = 1;
            return 0.0f;
        }
        for (float[] o : wall) {
            float[] r = o;
            if (this.yLine < r[0] || this.yLine > r[1]) continue;
            return r[2] * this.yLine + r[3];
        }
        this.lineStatus = 2;
        return 0.0f;
    }

    protected float[] findLimitsOneLine() {
        float x1 = this.findLimitsPoint(this.leftWall);
        if (this.lineStatus == 1 || this.lineStatus == 2) {
            return null;
        }
        float x2 = this.findLimitsPoint(this.rightWall);
        if (this.lineStatus == 2) {
            return null;
        }
        return new float[]{x1, x2};
    }

    protected float[] findLimitsTwoLines() {
        float[] x2;
        float[] x1;
        boolean repeat = false;
        while (true) {
            if (repeat && this.currentLeading == 0.0f) {
                return null;
            }
            repeat = true;
            x1 = this.findLimitsOneLine();
            if (this.lineStatus == 1) {
                return null;
            }
            this.yLine -= this.currentLeading;
            if (this.lineStatus == 2) continue;
            x2 = this.findLimitsOneLine();
            if (this.lineStatus == 1) {
                return null;
            }
            if (this.lineStatus == 2) {
                this.yLine -= this.currentLeading;
                continue;
            }
            if (!(x1[0] >= x2[1]) && !(x2[0] >= x1[1])) break;
        }
        return new float[]{x1[0], x1[1], x2[0], x2[1]};
    }

    public void setColumns(float[] leftLine, float[] rightLine) {
        this.maxY = -1.0E21f;
        this.minY = 1.0E21f;
        this.setYLine(Math.max(leftLine[1], leftLine[leftLine.length - 1]));
        this.rightWall = this.convertColumn(rightLine);
        this.leftWall = this.convertColumn(leftLine);
        this.rectangularWidth = -1.0f;
        this.rectangularMode = false;
    }

    public void setSimpleColumn(Phrase phrase, float llx, float lly, float urx, float ury, float leading, int alignment) {
        this.addText(phrase);
        this.setSimpleColumn(llx, lly, urx, ury, leading, alignment);
    }

    public void setSimpleColumn(float llx, float lly, float urx, float ury, float leading, int alignment) {
        this.setLeading(leading);
        this.alignment = alignment;
        this.setSimpleColumn(llx, lly, urx, ury);
    }

    public void setSimpleColumn(float llx, float lly, float urx, float ury) {
        this.leftX = Math.min(llx, urx);
        this.maxY = Math.max(lly, ury);
        this.minY = Math.min(lly, ury);
        this.rightX = Math.max(llx, urx);
        this.yLine = this.maxY;
        this.rectangularWidth = this.rightX - this.leftX;
        if (this.rectangularWidth < 0.0f) {
            this.rectangularWidth = 0.0f;
        }
        this.rectangularMode = true;
    }

    public void setLeading(float leading) {
        this.fixedLeading = leading;
        this.multipliedLeading = 0.0f;
    }

    public void setLeading(float fixedLeading, float multipliedLeading) {
        this.fixedLeading = fixedLeading;
        this.multipliedLeading = multipliedLeading;
    }

    public float getLeading() {
        return this.fixedLeading;
    }

    public float getMultipliedLeading() {
        return this.multipliedLeading;
    }

    public void setYLine(float yLine) {
        this.yLine = yLine;
    }

    public float getYLine() {
        return this.yLine;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public void setIndent(float indent) {
        this.indent = indent;
        this.lastWasNewline = true;
    }

    public float getIndent() {
        return this.indent;
    }

    public void setFollowingIndent(float indent) {
        this.followingIndent = indent;
        this.lastWasNewline = true;
    }

    public float getFollowingIndent() {
        return this.followingIndent;
    }

    public void setRightIndent(float indent) {
        this.rightIndent = indent;
        this.lastWasNewline = true;
    }

    public float getRightIndent() {
        return this.rightIndent;
    }

    public int go() throws DocumentException {
        return this.go(false);
    }

    public int go(boolean simulate) throws DocumentException {
        if (this.composite) {
            return this.goComposite(simulate);
        }
        this.addWaitingPhrase();
        if (this.bidiLine == null) {
            return 1;
        }
        this.descender = 0.0f;
        this.linesWritten = 0;
        boolean dirty = false;
        float ratio = this.spaceCharRatio;
        Object[] currentValues = new Object[2];
        PdfFont currentFont = null;
        currentValues[1] = Float.valueOf(0.0f);
        PdfDocument pdf = null;
        PdfContentByte graphics = null;
        PdfContentByte text = null;
        this.firstLineY = Float.NaN;
        int localRunDirection = 1;
        if (this.runDirection != 0) {
            localRunDirection = this.runDirection;
        }
        if (this.canvas != null) {
            graphics = this.canvas;
            pdf = this.canvas.getPdfDocument();
            text = this.canvas.getDuplicate();
        } else if (!simulate) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("columntext.go.with.simulate.eq.eq.false.and.text.eq.eq.null"));
        }
        if (!simulate) {
            if (ratio == 0.0f) {
                ratio = text.getPdfWriter().getSpaceCharRatio();
            } else if (ratio < 0.001f) {
                ratio = 0.001f;
            }
        }
        float firstIndent = 0.0f;
        int status = 0;
        while (true) {
            float x1;
            PdfLine line;
            float f = firstIndent = this.lastWasNewline ? this.indent : this.followingIndent;
            if (this.rectangularMode) {
                if (this.rectangularWidth <= firstIndent + this.rightIndent) {
                    status = 2;
                    if (!this.bidiLine.isEmpty()) break;
                    status |= 1;
                    break;
                }
                if (this.bidiLine.isEmpty()) {
                    status = 1;
                    break;
                }
                line = this.bidiLine.processLine(this.leftX, this.rectangularWidth - firstIndent - this.rightIndent, this.alignment, localRunDirection, this.arabicOptions);
                if (line == null) {
                    status = 1;
                    break;
                }
                float[] maxSize = line.getMaxSize();
                this.currentLeading = this.isUseAscender() && Float.isNaN(this.firstLineY) ? line.getAscender() : Math.max(this.fixedLeading + maxSize[0] * this.multipliedLeading, maxSize[1]);
                if (this.yLine > this.maxY || this.yLine - this.currentLeading < this.minY) {
                    status = 2;
                    this.bidiLine.restore();
                    break;
                }
                this.yLine -= this.currentLeading;
                if (!simulate && !dirty) {
                    text.beginText();
                    dirty = true;
                }
                if (Float.isNaN(this.firstLineY)) {
                    this.firstLineY = this.yLine;
                }
                this.updateFilledWidth(this.rectangularWidth - line.widthLeft());
                x1 = this.leftX;
            } else {
                float yTemp = this.yLine;
                float[] xx = this.findLimitsTwoLines();
                if (xx == null) {
                    status = 2;
                    if (this.bidiLine.isEmpty()) {
                        status |= 1;
                    }
                    this.yLine = yTemp;
                    break;
                }
                if (this.bidiLine.isEmpty()) {
                    status = 1;
                    this.yLine = yTemp;
                    break;
                }
                x1 = Math.max(xx[0], xx[2]);
                float x2 = Math.min(xx[1], xx[3]);
                if (x2 - x1 <= firstIndent + this.rightIndent) continue;
                if (!simulate && !dirty) {
                    text.beginText();
                    dirty = true;
                }
                if ((line = this.bidiLine.processLine(x1, x2 - x1 - firstIndent - this.rightIndent, this.alignment, localRunDirection, this.arabicOptions)) == null) {
                    status = 1;
                    this.yLine = yTemp;
                    break;
                }
            }
            if (!simulate) {
                currentValues[0] = currentFont;
                text.setTextMatrix(x1 + (line.isRTL() ? this.rightIndent : firstIndent) + line.indentLeft(), this.yLine);
                pdf.writeLineToContent(line, text, graphics, currentValues, ratio);
                currentFont = (PdfFont)currentValues[0];
            }
            this.lastWasNewline = line.isNewlineSplit();
            this.yLine -= line.isNewlineSplit() ? this.extraParagraphSpace : 0.0f;
            ++this.linesWritten;
            this.descender = line.getDescender();
        }
        if (dirty) {
            text.endText();
            this.canvas.add(text);
        }
        return status;
    }

    public float getExtraParagraphSpace() {
        return this.extraParagraphSpace;
    }

    public void setExtraParagraphSpace(float extraParagraphSpace) {
        this.extraParagraphSpace = extraParagraphSpace;
    }

    public void clearChunks() {
        if (this.bidiLine != null) {
            this.bidiLine.clearChunks();
        }
    }

    public float getSpaceCharRatio() {
        return this.spaceCharRatio;
    }

    public void setSpaceCharRatio(float spaceCharRatio) {
        this.spaceCharRatio = spaceCharRatio;
    }

    public void setRunDirection(int runDirection) {
        if (runDirection < 0 || runDirection > 3) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.run.direction.1", runDirection));
        }
        this.runDirection = runDirection;
    }

    public int getRunDirection() {
        return this.runDirection;
    }

    public int getLinesWritten() {
        return this.linesWritten;
    }

    public int getArabicOptions() {
        return this.arabicOptions;
    }

    public void setArabicOptions(int arabicOptions) {
        this.arabicOptions = arabicOptions;
    }

    public float getDescender() {
        return this.descender;
    }

    public static float getWidth(Phrase phrase, int runDirection, int arabicOptions) {
        ColumnText ct = new ColumnText(null);
        ct.addText(phrase);
        ct.addWaitingPhrase();
        PdfLine line = ct.bidiLine.processLine(0.0f, 20000.0f, 0, runDirection, arabicOptions);
        if (line == null) {
            return 0.0f;
        }
        return 20000.0f - line.widthLeft();
    }

    public static float getWidth(Phrase phrase) {
        return ColumnText.getWidth(phrase, 1, 0);
    }

    public static void showTextAligned(PdfContentByte canvas, int alignment, Phrase phrase, float x, float y, float rotation, int runDirection, int arabicOptions) {
        float urx;
        float llx;
        if (alignment != 0 && alignment != 1 && alignment != 2) {
            alignment = 0;
        }
        canvas.saveState();
        ColumnText ct = new ColumnText(canvas);
        float lly = -1.0f;
        float ury = 2.0f;
        switch (alignment) {
            case 0: {
                llx = 0.0f;
                urx = 20000.0f;
                break;
            }
            case 2: {
                llx = -20000.0f;
                urx = 0.0f;
                break;
            }
            default: {
                llx = -20000.0f;
                urx = 20000.0f;
            }
        }
        if (rotation == 0.0f) {
            llx += x;
            lly += y;
            urx += x;
            ury += y;
        } else {
            double alpha = (double)rotation * Math.PI / 180.0;
            float cos = (float)Math.cos(alpha);
            float sin = (float)Math.sin(alpha);
            canvas.concatCTM(cos, sin, -sin, cos, x, y);
        }
        ct.setSimpleColumn(phrase, llx, lly, urx, ury, 2.0f, alignment);
        if (runDirection == 3) {
            if (alignment == 0) {
                alignment = 2;
            } else if (alignment == 2) {
                alignment = 0;
            }
        }
        ct.setAlignment(alignment);
        ct.setArabicOptions(arabicOptions);
        ct.setRunDirection(runDirection);
        try {
            ct.go();
        }
        catch (DocumentException e) {
            throw new ExceptionConverter(e);
        }
        canvas.restoreState();
    }

    public static void showTextAligned(PdfContentByte canvas, int alignment, Phrase phrase, float x, float y, float rotation) {
        ColumnText.showTextAligned(canvas, alignment, phrase, x, y, rotation, 1, 0);
    }

    protected int goComposite(boolean simulate) throws DocumentException {
        if (!this.rectangularMode) {
            throw new DocumentException(MessageLocalization.getComposedMessage("irregular.columns.are.not.supported.in.composite.mode"));
        }
        this.linesWritten = 0;
        this.descender = 0.0f;
        boolean firstPass = this.adjustFirstLine;
        block4: while (!this.compositeElements.isEmpty()) {
            Element element = this.compositeElements.getFirst();
            if (element.type() == 12) {
                Paragraph para = (Paragraph)element;
                int status = 0;
                for (int keep = 0; keep < 2; ++keep) {
                    float lastY = this.yLine;
                    boolean createHere = false;
                    if (this.compositeColumn == null) {
                        this.compositeColumn = new ColumnText(this.canvas);
                        this.compositeColumn.setUseAscender(firstPass && this.useAscender);
                        this.compositeColumn.setAlignment(para.getAlignment());
                        this.compositeColumn.setIndent(para.getIndentationLeft() + para.getFirstLineIndent());
                        this.compositeColumn.setExtraParagraphSpace(para.getExtraParagraphSpace());
                        this.compositeColumn.setFollowingIndent(para.getIndentationLeft());
                        this.compositeColumn.setRightIndent(para.getIndentationRight());
                        this.compositeColumn.setLeading(para.getLeading(), para.getMultipliedLeading());
                        this.compositeColumn.setRunDirection(this.runDirection);
                        this.compositeColumn.setArabicOptions(this.arabicOptions);
                        this.compositeColumn.setSpaceCharRatio(this.spaceCharRatio);
                        this.compositeColumn.addText(para);
                        if (!firstPass) {
                            this.yLine -= para.getSpacingBefore();
                        }
                        createHere = true;
                    }
                    this.compositeColumn.leftX = this.leftX;
                    this.compositeColumn.rightX = this.rightX;
                    this.compositeColumn.yLine = this.yLine;
                    this.compositeColumn.rectangularWidth = this.rectangularWidth;
                    this.compositeColumn.rectangularMode = this.rectangularMode;
                    this.compositeColumn.minY = this.minY;
                    this.compositeColumn.maxY = this.maxY;
                    boolean keepCandidate = para.getKeepTogether() && createHere && !firstPass;
                    status = this.compositeColumn.go(simulate || keepCandidate && keep == 0);
                    this.updateFilledWidth(this.compositeColumn.filledWidth);
                    if ((status & 1) == 0 && keepCandidate) {
                        this.compositeColumn = null;
                        this.yLine = lastY;
                        return 2;
                    }
                    if (simulate || !keepCandidate) break;
                    if (keep != 0) continue;
                    this.compositeColumn = null;
                    this.yLine = lastY;
                }
                firstPass = false;
                this.yLine = this.compositeColumn.yLine;
                this.linesWritten += this.compositeColumn.linesWritten;
                this.descender = this.compositeColumn.descender;
                if (status & true) {
                    this.compositeColumn = null;
                    this.compositeElements.removeFirst();
                    this.yLine -= para.getSpacingAfter();
                }
                if ((status & 2) == 0) continue;
                return 2;
            }
            if (element.type() == 14) {
                List list = (List)element;
                java.util.List<Element> items = list.getItems();
                ListItem item = null;
                float listIndentation = list.getIndentationLeft();
                int count = 0;
                Stack<Object[]> stack = new Stack<Object[]>();
                for (int k = 0; k < items.size(); ++k) {
                    Element obj = items.get(k);
                    if (obj instanceof ListItem) {
                        if (count == this.listIdx) {
                            item = (ListItem)obj;
                            break;
                        }
                        ++count;
                    } else if (obj instanceof List) {
                        stack.push(new Object[]{list, k, Float.valueOf(listIndentation)});
                        list = (List)obj;
                        items = list.getItems();
                        listIndentation += list.getIndentationLeft();
                        k = -1;
                        continue;
                    }
                    if (k != items.size() - 1 || stack.isEmpty()) continue;
                    Object[] objs = (Object[])stack.pop();
                    list = (List)objs[0];
                    items = list.getItems();
                    k = (Integer)objs[1];
                    listIndentation = ((Float)objs[2]).floatValue();
                }
                int status = 0;
                for (int keep = 0; keep < 2; ++keep) {
                    float lastY = this.yLine;
                    boolean createHere = false;
                    if (this.compositeColumn == null) {
                        if (item == null) {
                            this.listIdx = 0;
                            this.compositeElements.removeFirst();
                            continue block4;
                        }
                        this.compositeColumn = new ColumnText(this.canvas);
                        this.compositeColumn.setUseAscender(firstPass && this.useAscender);
                        this.compositeColumn.setAlignment(item.getAlignment());
                        this.compositeColumn.setIndent(item.getIndentationLeft() + listIndentation + item.getFirstLineIndent());
                        this.compositeColumn.setExtraParagraphSpace(item.getExtraParagraphSpace());
                        this.compositeColumn.setFollowingIndent(this.compositeColumn.getIndent());
                        this.compositeColumn.setRightIndent(item.getIndentationRight() + list.getIndentationRight());
                        this.compositeColumn.setLeading(item.getLeading(), item.getMultipliedLeading());
                        this.compositeColumn.setRunDirection(this.runDirection);
                        this.compositeColumn.setArabicOptions(this.arabicOptions);
                        this.compositeColumn.setSpaceCharRatio(this.spaceCharRatio);
                        this.compositeColumn.addText(item);
                        if (!firstPass) {
                            this.yLine -= item.getSpacingBefore();
                        }
                        createHere = true;
                    }
                    this.compositeColumn.leftX = this.leftX;
                    this.compositeColumn.rightX = this.rightX;
                    this.compositeColumn.yLine = this.yLine;
                    this.compositeColumn.rectangularWidth = this.rectangularWidth;
                    this.compositeColumn.rectangularMode = this.rectangularMode;
                    this.compositeColumn.minY = this.minY;
                    this.compositeColumn.maxY = this.maxY;
                    boolean keepCandidate = item.getKeepTogether() && createHere && !firstPass;
                    status = this.compositeColumn.go(simulate || keepCandidate && keep == 0);
                    this.updateFilledWidth(this.compositeColumn.filledWidth);
                    if ((status & 1) == 0 && keepCandidate) {
                        this.compositeColumn = null;
                        this.yLine = lastY;
                        return 2;
                    }
                    if (simulate || !keepCandidate) break;
                    if (keep != 0) continue;
                    this.compositeColumn = null;
                    this.yLine = lastY;
                }
                firstPass = false;
                this.yLine = this.compositeColumn.yLine;
                this.linesWritten += this.compositeColumn.linesWritten;
                this.descender = this.compositeColumn.descender;
                if (!Float.isNaN(this.compositeColumn.firstLineY) && !this.compositeColumn.firstLineYDone) {
                    if (!simulate) {
                        ColumnText.showTextAligned(this.canvas, 0, new Phrase(item.getListSymbol()), this.compositeColumn.leftX + listIndentation, this.compositeColumn.firstLineY, 0.0f);
                    }
                    this.compositeColumn.firstLineYDone = true;
                }
                if (status & true) {
                    this.compositeColumn = null;
                    ++this.listIdx;
                    this.yLine -= item.getSpacingAfter();
                }
                if ((status & 2) == 0) continue;
                return 2;
            }
            if (element.type() == 23) {
                float rowHeight;
                int k;
                boolean skipHeader;
                float tableWidth;
                if (this.yLine < this.minY || this.yLine > this.maxY) {
                    return 2;
                }
                PdfPTable table = (PdfPTable)element;
                if (table.size() <= table.getHeaderRows()) {
                    this.compositeElements.removeFirst();
                    continue;
                }
                float yTemp = this.yLine;
                if (!firstPass && this.listIdx == 0) {
                    yTemp -= table.spacingBefore();
                }
                float yLineWrite = yTemp;
                if (yTemp < this.minY || yTemp > this.maxY) {
                    return 2;
                }
                this.currentLeading = 0.0f;
                float x1 = this.leftX;
                if (table.isLockedWidth()) {
                    tableWidth = table.getTotalWidth();
                    this.updateFilledWidth(tableWidth);
                } else {
                    tableWidth = this.rectangularWidth * table.getWidthPercentage() / 100.0f;
                    table.setTotalWidth(tableWidth);
                }
                int headerRows = table.getHeaderRows();
                int footerRows = table.getFooterRows();
                if (footerRows > headerRows) {
                    footerRows = headerRows;
                }
                int realHeaderRows = headerRows - footerRows;
                float headerHeight = table.getHeaderHeight();
                float footerHeight = table.getFooterHeight();
                boolean bl = skipHeader = !firstPass && table.isSkipFirstHeader() && this.listIdx <= headerRows;
                if (!skipHeader && ((yTemp -= headerHeight) < this.minY || yTemp > this.maxY)) {
                    if (firstPass) {
                        this.compositeElements.removeFirst();
                        continue;
                    }
                    return 2;
                }
                if (this.listIdx < headerRows) {
                    this.listIdx = headerRows;
                }
                if (!table.isComplete()) {
                    yTemp -= footerHeight;
                }
                for (k = this.listIdx; k < table.size() && !(yTemp - (rowHeight = table.getRowHeight(k)) < this.minY); ++k) {
                    yTemp -= rowHeight;
                }
                if (!table.isComplete()) {
                    yTemp += footerHeight;
                }
                if (k < table.size()) {
                    if (table.isSplitRows() && (!table.isSplitLate() || k == this.listIdx && firstPass)) {
                        if (!this.splittedRow) {
                            this.splittedRow = true;
                            table = new PdfPTable(table);
                            this.compositeElements.set(0, table);
                            ArrayList<PdfPRow> rows = table.getRows();
                            for (int i = headerRows; i < this.listIdx; ++i) {
                                rows.set(i, null);
                            }
                        }
                        float h = yTemp - this.minY;
                        PdfPRow newRow = table.getRow(k).splitRow(table, k, h);
                        if (newRow == null) {
                            if (k == this.listIdx) {
                                return 2;
                            }
                        } else {
                            yTemp = this.minY;
                            table.getRows().add(++k, newRow);
                        }
                    } else {
                        if (!table.isSplitRows() && k == this.listIdx && firstPass) {
                            this.compositeElements.removeFirst();
                            this.splittedRow = false;
                            continue;
                        }
                        if (!(k != this.listIdx || firstPass || table.isSplitRows() && !table.isSplitLate() || table.getFooterRows() != 0 && !table.isComplete())) {
                            return 2;
                        }
                    }
                }
                firstPass = false;
                if (!simulate) {
                    switch (table.getHorizontalAlignment()) {
                        case 0: {
                            break;
                        }
                        case 2: {
                            x1 += this.rectangularWidth - tableWidth;
                            break;
                        }
                        default: {
                            x1 += (this.rectangularWidth - tableWidth) / 2.0f;
                        }
                    }
                    PdfPTable nt = PdfPTable.shallowCopy(table);
                    ArrayList<PdfPRow> sub = nt.getRows();
                    if (!skipHeader && realHeaderRows > 0) {
                        sub.addAll(table.getRows(0, realHeaderRows));
                    } else {
                        nt.setHeaderRows(footerRows);
                    }
                    sub.addAll(table.getRows(this.listIdx, k));
                    boolean showFooter = !table.isSkipLastFooter();
                    boolean newPageFollows = false;
                    if (k < table.size()) {
                        nt.setComplete(true);
                        showFooter = true;
                        newPageFollows = true;
                    }
                    for (int j = 0; j < footerRows && nt.isComplete() && showFooter; ++j) {
                        sub.add(table.getRow(j + realHeaderRows));
                    }
                    float rowHeight2 = 0.0f;
                    int index = sub.size() - 1;
                    if (showFooter) {
                        index -= footerRows;
                    }
                    PdfPRow last = (PdfPRow)sub.get(index);
                    if (table.isExtendLastRow(newPageFollows)) {
                        rowHeight2 = last.getMaxHeights();
                        last.setMaxHeights(yTemp - this.minY + rowHeight2);
                        yTemp = this.minY;
                    }
                    if (this.canvases != null) {
                        nt.writeSelectedRows(0, -1, x1, yLineWrite, this.canvases);
                    } else {
                        nt.writeSelectedRows(0, -1, x1, yLineWrite, this.canvas);
                    }
                    if (table.isExtendLastRow(newPageFollows)) {
                        last.setMaxHeights(rowHeight2);
                    }
                } else if (table.isExtendLastRow() && this.minY > -1.07374182E9f) {
                    yTemp = this.minY;
                }
                this.yLine = yTemp;
                if (!skipHeader && !table.isComplete()) {
                    this.yLine += footerHeight;
                }
                if (k >= table.size()) {
                    this.yLine -= table.spacingAfter();
                    this.compositeElements.removeFirst();
                    this.splittedRow = false;
                    this.listIdx = 0;
                    continue;
                }
                if (this.splittedRow) {
                    ArrayList<PdfPRow> rows = table.getRows();
                    for (int i = this.listIdx; i < k; ++i) {
                        rows.set(i, null);
                    }
                }
                this.listIdx = k;
                return 2;
            }
            if (element.type() == 55) {
                if (!simulate) {
                    DrawInterface zh = (DrawInterface)((Object)element);
                    zh.draw(this.canvas, this.leftX, this.minY, this.rightX, this.maxY, this.yLine);
                }
                this.compositeElements.removeFirst();
                continue;
            }
            this.compositeElements.removeFirst();
        }
        return 1;
    }

    public PdfContentByte getCanvas() {
        return this.canvas;
    }

    public void setCanvas(PdfContentByte canvas) {
        this.canvas = canvas;
        this.canvases = null;
        if (this.compositeColumn != null) {
            this.compositeColumn.setCanvas(canvas);
        }
    }

    public void setCanvases(PdfContentByte[] canvases) {
        this.canvases = canvases;
        this.canvas = canvases[3];
        if (this.compositeColumn != null) {
            this.compositeColumn.setCanvases(canvases);
        }
    }

    public PdfContentByte[] getCanvases() {
        return this.canvases;
    }

    public boolean zeroHeightElement() {
        return this.composite && !this.compositeElements.isEmpty() && this.compositeElements.getFirst().type() == 55;
    }

    public boolean isUseAscender() {
        return this.useAscender;
    }

    public void setUseAscender(boolean useAscender) {
        this.useAscender = useAscender;
    }

    public static boolean hasMoreText(int status) {
        return (status & 1) == 0;
    }

    public float getFilledWidth() {
        return this.filledWidth;
    }

    public void setFilledWidth(float filledWidth) {
        this.filledWidth = filledWidth;
    }

    public void updateFilledWidth(float w) {
        if (w > this.filledWidth) {
            this.filledWidth = w;
        }
    }

    public boolean isAdjustFirstLine() {
        return this.adjustFirstLine;
    }

    public void setAdjustFirstLine(boolean adjustFirstLine) {
        this.adjustFirstLine = adjustFirstLine;
    }
}

