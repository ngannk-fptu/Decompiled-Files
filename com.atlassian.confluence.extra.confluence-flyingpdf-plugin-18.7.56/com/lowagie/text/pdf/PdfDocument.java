/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Anchor;
import com.lowagie.text.Annotation;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.MarkedObject;
import com.lowagie.text.MarkedSection;
import com.lowagie.text.Meta;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Section;
import com.lowagie.text.SimpleTable;
import com.lowagie.text.Table;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.MultiColumnText;
import com.lowagie.text.pdf.PageResources;
import com.lowagie.text.pdf.PdfAcroForm;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfChunk;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfContents;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfEncryption;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfFont;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLine;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNameTree;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPage;
import com.lowagie.text.pdf.PdfPageEvent;
import com.lowagie.text.pdf.PdfPageLabels;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfTable;
import com.lowagie.text.pdf.PdfTextArray;
import com.lowagie.text.pdf.PdfTransition;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfXConformanceException;
import com.lowagie.text.pdf.collection.PdfCollection;
import com.lowagie.text.pdf.draw.DrawInterface;
import com.lowagie.text.pdf.internal.PdfAnnotationsImp;
import com.lowagie.text.pdf.internal.PdfViewerPreferencesImp;
import java.awt.Color;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PdfDocument
extends Document {
    protected PdfWriter writer;
    protected PdfContentByte text;
    protected PdfContentByte graphics;
    protected float leading = 0.0f;
    protected int alignment = 0;
    protected float currentHeight = 0.0f;
    protected boolean isSectionTitle = false;
    protected int leadingCount = 0;
    protected PdfAction anchorAction = null;
    protected int textEmptySize;
    protected byte[] xmpMetadata = null;
    protected float nextMarginLeft;
    protected float nextMarginRight;
    protected float nextMarginTop;
    protected float nextMarginBottom;
    protected boolean firstPageEvent = true;
    protected PdfLine line = null;
    protected java.util.List<PdfLine> lines = new ArrayList<PdfLine>();
    protected int lastElementType = -1;
    static final String hangingPunctuation = ".,;:'";
    protected Indentation indentation = new Indentation();
    protected PdfInfo info = new PdfInfo();
    protected PdfOutline rootOutline;
    protected PdfOutline currentOutline;
    protected PdfViewerPreferencesImp viewerPreferences = new PdfViewerPreferencesImp();
    protected PdfPageLabels pageLabels;
    protected TreeMap<String, Object[]> localDestinations = new TreeMap();
    int jsCounter;
    protected HashMap<String, PdfIndirectReference> documentLevelJS = new HashMap();
    protected static final DecimalFormat SIXTEEN_DIGITS = new DecimalFormat("0000000000000000");
    protected HashMap<String, PdfIndirectReference> documentFileAttachment = new HashMap();
    protected String openActionName;
    protected PdfAction openActionAction;
    protected PdfDictionary additionalActions;
    protected PdfCollection collection;
    PdfAnnotationsImp annotationsImp;
    protected int markPoint;
    protected Rectangle nextPageSize = null;
    protected HashMap<String, PdfRectangle> thisBoxSize = new HashMap();
    protected HashMap<String, PdfRectangle> boxSize = new HashMap();
    private boolean pageEmpty = true;
    protected int duration = -1;
    protected PdfTransition transition = null;
    protected PdfDictionary pageAA = null;
    protected PdfIndirectReference thumb;
    protected PageResources pageResources;
    protected boolean strictImageSequence = false;
    protected float imageEnd = -1.0f;
    protected Image imageWait = null;

    public void addWriter(PdfWriter writer) throws DocumentException {
        if (this.writer == null) {
            this.writer = writer;
            this.annotationsImp = new PdfAnnotationsImp(writer);
            return;
        }
        throw new DocumentException(MessageLocalization.getComposedMessage("you.can.only.add.a.writer.to.a.pdfdocument.once"));
    }

    public float getLeading() {
        return this.leading;
    }

    void setLeading(float leading) {
        this.leading = leading;
    }

    @Override
    public boolean add(Element element) throws DocumentException {
        if (this.writer != null && this.writer.isPaused()) {
            return false;
        }
        try {
            switch (element.type()) {
                case 0: {
                    this.info.addkey(((Meta)element).getName(), ((Meta)element).getContent());
                    break;
                }
                case 1: {
                    this.info.addTitle(((Meta)element).getContent());
                    break;
                }
                case 2: {
                    this.info.addSubject(((Meta)element).getContent());
                    break;
                }
                case 3: {
                    this.info.addKeywords(((Meta)element).getContent());
                    break;
                }
                case 4: {
                    this.info.addAuthor(((Meta)element).getContent());
                    break;
                }
                case 7: {
                    this.info.addCreator(((Meta)element).getContent());
                    break;
                }
                case 5: {
                    this.info.addProducer(((Meta)element).getContent());
                    break;
                }
                case 6: {
                    this.info.addCreationDate();
                    break;
                }
                case 10: {
                    PdfChunk overflow;
                    if (this.line == null) {
                        this.carriageReturn();
                    }
                    PdfChunk chunk = new PdfChunk((Chunk)element, this.anchorAction);
                    while ((overflow = this.line.add(chunk)) != null) {
                        this.carriageReturn();
                        chunk = overflow;
                        chunk.trimFirstSpace();
                    }
                    this.pageEmpty = false;
                    if (!chunk.isAttribute("NEWPAGE")) break;
                    this.newPage();
                    break;
                }
                case 17: {
                    ++this.leadingCount;
                    Anchor anchor = (Anchor)element;
                    String url = anchor.getReference();
                    this.leading = anchor.getLeading();
                    if (url != null) {
                        this.anchorAction = new PdfAction(url);
                    }
                    element.process(this);
                    this.anchorAction = null;
                    --this.leadingCount;
                    break;
                }
                case 29: {
                    if (this.line == null) {
                        this.carriageReturn();
                    }
                    Annotation annot = (Annotation)element;
                    Rectangle rect = new Rectangle(0.0f, 0.0f);
                    if (this.line != null) {
                        rect = new Rectangle(annot.llx(this.indentRight() - this.line.widthLeft()), annot.ury(this.indentTop() - this.currentHeight - 20.0f), annot.urx(this.indentRight() - this.line.widthLeft() + 20.0f), annot.lly(this.indentTop() - this.currentHeight));
                    }
                    PdfAnnotation an = PdfAnnotationsImp.convertAnnotation(this.writer, annot, rect);
                    this.annotationsImp.addPlainAnnotation(an);
                    this.pageEmpty = false;
                    break;
                }
                case 11: {
                    ++this.leadingCount;
                    this.leading = ((Phrase)element).getLeading();
                    element.process(this);
                    --this.leadingCount;
                    break;
                }
                case 12: {
                    ++this.leadingCount;
                    Paragraph paragraph = (Paragraph)element;
                    this.addSpacing(paragraph.getSpacingBefore(), this.leading, paragraph.getFont());
                    this.alignment = paragraph.getAlignment();
                    this.leading = paragraph.getTotalLeading();
                    this.carriageReturn();
                    if (this.currentHeight + this.line.height() + this.leading > this.indentTop() - this.indentBottom()) {
                        this.newPage();
                    }
                    this.indentation.indentLeft += paragraph.getIndentationLeft();
                    this.indentation.indentRight += paragraph.getIndentationRight();
                    this.carriageReturn();
                    PdfPageEvent pageEvent = this.writer.getPageEvent();
                    if (pageEvent != null && !this.isSectionTitle) {
                        pageEvent.onParagraph(this.writer, this, this.indentTop() - this.currentHeight);
                    }
                    if (paragraph.getKeepTogether()) {
                        this.carriageReturn();
                        PdfPTable table = new PdfPTable(1);
                        table.setWidthPercentage(100.0f);
                        PdfPCell cell = new PdfPCell();
                        cell.addElement(paragraph);
                        cell.setBorder(0);
                        cell.setPadding(0.0f);
                        table.addCell(cell);
                        this.indentation.indentLeft -= paragraph.getIndentationLeft();
                        this.indentation.indentRight -= paragraph.getIndentationRight();
                        this.add(table);
                        this.indentation.indentLeft += paragraph.getIndentationLeft();
                        this.indentation.indentRight += paragraph.getIndentationRight();
                    } else {
                        this.line.setExtraIndent(paragraph.getFirstLineIndent());
                        element.process(this);
                        this.carriageReturn();
                        this.addSpacing(paragraph.getSpacingAfter(), paragraph.getTotalLeading(), paragraph.getFont());
                    }
                    if (pageEvent != null && !this.isSectionTitle) {
                        pageEvent.onParagraphEnd(this.writer, this, this.indentTop() - this.currentHeight);
                    }
                    this.alignment = 0;
                    this.indentation.indentLeft -= paragraph.getIndentationLeft();
                    this.indentation.indentRight -= paragraph.getIndentationRight();
                    this.carriageReturn();
                    --this.leadingCount;
                    break;
                }
                case 13: 
                case 16: {
                    boolean hasTitle;
                    Section section = (Section)element;
                    PdfPageEvent pageEvent = this.writer.getPageEvent();
                    boolean bl = hasTitle = section.isNotAddedYet() && section.getTitle() != null;
                    if (section.isTriggerNewPage()) {
                        this.newPage();
                    }
                    if (hasTitle) {
                        PdfOutline outline;
                        float fith = this.indentTop() - this.currentHeight;
                        int rotation = this.pageSize.getRotation();
                        if (rotation == 90 || rotation == 180) {
                            fith = this.pageSize.getHeight() - fith;
                        }
                        PdfDestination destination = new PdfDestination(2, fith);
                        while (this.currentOutline.level() >= section.getDepth()) {
                            this.currentOutline = this.currentOutline.parent();
                        }
                        this.currentOutline = outline = new PdfOutline(this.currentOutline, destination, section.getBookmarkTitle(), section.isBookmarkOpen());
                    }
                    this.carriageReturn();
                    this.indentation.sectionIndentLeft += section.getIndentationLeft();
                    this.indentation.sectionIndentRight += section.getIndentationRight();
                    if (section.isNotAddedYet() && pageEvent != null) {
                        if (element.type() == 16) {
                            pageEvent.onChapter(this.writer, this, this.indentTop() - this.currentHeight, section.getTitle());
                        } else {
                            pageEvent.onSection(this.writer, this, this.indentTop() - this.currentHeight, section.getDepth(), section.getTitle());
                        }
                    }
                    if (hasTitle) {
                        this.isSectionTitle = true;
                        this.add(section.getTitle());
                        this.isSectionTitle = false;
                    }
                    this.indentation.sectionIndentLeft += section.getIndentation();
                    element.process(this);
                    this.flushLines();
                    this.indentation.sectionIndentLeft -= section.getIndentationLeft() + section.getIndentation();
                    this.indentation.sectionIndentRight -= section.getIndentationRight();
                    if (!section.isComplete() || pageEvent == null) break;
                    if (element.type() == 16) {
                        pageEvent.onChapterEnd(this.writer, this, this.indentTop() - this.currentHeight);
                        break;
                    }
                    pageEvent.onSectionEnd(this.writer, this, this.indentTop() - this.currentHeight);
                    break;
                }
                case 14: {
                    List list = (List)element;
                    if (list.isAlignindent()) {
                        list.normalizeIndentation();
                    }
                    this.indentation.listIndentLeft += list.getIndentationLeft();
                    this.indentation.indentRight += list.getIndentationRight();
                    element.process(this);
                    this.indentation.listIndentLeft -= list.getIndentationLeft();
                    this.indentation.indentRight -= list.getIndentationRight();
                    this.carriageReturn();
                    break;
                }
                case 15: {
                    ++this.leadingCount;
                    ListItem listItem = (ListItem)element;
                    this.addSpacing(listItem.getSpacingBefore(), this.leading, listItem.getFont());
                    this.alignment = listItem.getAlignment();
                    this.indentation.listIndentLeft += listItem.getIndentationLeft();
                    this.indentation.indentRight += listItem.getIndentationRight();
                    this.leading = listItem.getTotalLeading();
                    this.carriageReturn();
                    this.line.setListItem(listItem);
                    element.process(this);
                    this.addSpacing(listItem.getSpacingAfter(), listItem.getTotalLeading(), listItem.getFont());
                    if (this.line.hasToBeJustified()) {
                        this.line.resetAlignment();
                    }
                    this.carriageReturn();
                    this.indentation.listIndentLeft -= listItem.getIndentationLeft();
                    this.indentation.indentRight -= listItem.getIndentationRight();
                    --this.leadingCount;
                    break;
                }
                case 30: {
                    Rectangle rectangle = (Rectangle)element;
                    this.graphics.rectangle(rectangle);
                    this.pageEmpty = false;
                    break;
                }
                case 23: {
                    PdfPTable ptable = (PdfPTable)element;
                    if (ptable.size() <= ptable.getHeaderRows()) break;
                    this.ensureNewLine();
                    this.flushLines();
                    this.addPTable(ptable);
                    this.pageEmpty = false;
                    this.newLine();
                    break;
                }
                case 40: {
                    this.ensureNewLine();
                    this.flushLines();
                    MultiColumnText multiText = (MultiColumnText)element;
                    float height = multiText.write(this.writer.getDirectContent(), this, this.indentTop() - this.currentHeight);
                    this.currentHeight += height;
                    this.text.moveText(0.0f, -1.0f * height);
                    this.pageEmpty = false;
                    break;
                }
                case 22: {
                    if (element instanceof SimpleTable) {
                        PdfPTable ptable = ((SimpleTable)element).createPdfPTable();
                        if (ptable.size() <= ptable.getHeaderRows()) break;
                        this.ensureNewLine();
                        this.flushLines();
                        this.addPTable(ptable);
                        this.pageEmpty = false;
                        break;
                    }
                    if (element instanceof Table) {
                        try {
                            PdfPTable ptable = ((Table)element).createPdfPTable();
                            if (ptable.size() <= ptable.getHeaderRows()) break;
                            this.ensureNewLine();
                            this.flushLines();
                            this.addPTable(ptable);
                            this.pageEmpty = false;
                        }
                        catch (BadElementException bee) {
                            float offset = ((Table)element).getOffset();
                            if (Float.isNaN(offset)) {
                                offset = this.leading;
                            }
                            this.carriageReturn();
                            this.lines.add(new PdfLine(this.indentLeft(), this.indentRight(), this.alignment, offset));
                            this.currentHeight += offset;
                            this.addPdfTable((Table)element);
                        }
                        break;
                    }
                    return false;
                }
                case 32: 
                case 33: 
                case 34: 
                case 35: 
                case 36: {
                    this.add((Image)element);
                    break;
                }
                case 55: {
                    DrawInterface zh = (DrawInterface)((Object)element);
                    zh.draw(this.graphics, this.indentLeft(), this.indentBottom(), this.indentRight(), this.indentTop(), this.indentTop() - this.currentHeight - (this.leadingCount > 0 ? this.leading : 0.0f));
                    this.pageEmpty = false;
                    break;
                }
                case 50: {
                    MarkedObject mo;
                    if (element instanceof MarkedSection && (mo = ((MarkedSection)element).getTitle()) != null) {
                        mo.process(this);
                    }
                    mo = (MarkedObject)element;
                    mo.process(this);
                    break;
                }
                default: {
                    return false;
                }
            }
            this.lastElementType = element.type();
            return true;
        }
        catch (Exception e) {
            throw new DocumentException(e);
        }
    }

    @Override
    public void open() {
        if (!this.open) {
            super.open();
            this.writer.open();
            this.currentOutline = this.rootOutline = new PdfOutline(this.writer);
        }
        try {
            this.initPage();
        }
        catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    @Override
    public void close() {
        if (this.close) {
            return;
        }
        try {
            boolean wasImage = this.imageWait != null;
            this.newPage();
            if (this.imageWait != null || wasImage) {
                this.newPage();
            }
            if (this.annotationsImp.hasUnusedAnnotations()) {
                throw new RuntimeException(MessageLocalization.getComposedMessage("not.all.annotations.could.be.added.to.the.document.the.document.doesn.t.have.enough.pages"));
            }
            PdfPageEvent pageEvent = this.writer.getPageEvent();
            if (pageEvent != null) {
                pageEvent.onCloseDocument(this.writer, this);
            }
            super.close();
            this.writer.addLocalDestinations(this.localDestinations);
            this.calculateOutlineCount();
            this.writeOutlines();
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
        this.writer.close();
    }

    public void setXmpMetadata(byte[] xmpMetadata) {
        this.xmpMetadata = xmpMetadata;
    }

    @Override
    public boolean newPage() {
        this.lastElementType = -1;
        if (this.isPageEmpty()) {
            this.setNewPageSizeAndMargins();
            return false;
        }
        if (!this.open || this.close) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("the.document.is.not.open"));
        }
        PdfPageEvent pageEvent = this.writer.getPageEvent();
        if (pageEvent != null) {
            pageEvent.onEndPage(this.writer, this);
        }
        super.newPage();
        this.indentation.imageIndentLeft = 0.0f;
        this.indentation.imageIndentRight = 0.0f;
        try {
            PdfArray array;
            this.flushLines();
            int rotation = this.pageSize.getRotation();
            if (this.writer.isPdfX()) {
                if (this.thisBoxSize.containsKey("art") && this.thisBoxSize.containsKey("trim")) {
                    throw new PdfXConformanceException(MessageLocalization.getComposedMessage("only.one.of.artbox.or.trimbox.can.exist.in.the.page"));
                }
                if (!this.thisBoxSize.containsKey("art") && !this.thisBoxSize.containsKey("trim")) {
                    if (this.thisBoxSize.containsKey("crop")) {
                        this.thisBoxSize.put("trim", this.thisBoxSize.get("crop"));
                    } else {
                        this.thisBoxSize.put("trim", new PdfRectangle(this.pageSize, this.pageSize.getRotation()));
                    }
                }
            }
            this.pageResources.addDefaultColorDiff(this.writer.getDefaultColorspace());
            if (this.writer.isRgbTransparencyBlending()) {
                PdfDictionary dcs = new PdfDictionary();
                dcs.put(PdfName.CS, PdfName.DEVICERGB);
                this.pageResources.addDefaultColorDiff(dcs);
            }
            PdfDictionary resources = this.pageResources.getResources();
            PdfPage page = new PdfPage(new PdfRectangle(this.pageSize, rotation), this.thisBoxSize, resources, rotation);
            page.put(PdfName.TABS, this.writer.getTabs());
            if (this.xmpMetadata != null) {
                PdfStream xmp = new PdfStream(this.xmpMetadata);
                xmp.put(PdfName.TYPE, PdfName.METADATA);
                xmp.put(PdfName.SUBTYPE, PdfName.XML);
                PdfEncryption crypto = this.writer.getEncryption();
                if (crypto != null && !crypto.isMetadataEncrypted()) {
                    PdfArray ar = new PdfArray();
                    ar.add(PdfName.CRYPT);
                    xmp.put(PdfName.FILTER, ar);
                }
                page.put(PdfName.METADATA, this.writer.addToBody(xmp).getIndirectReference());
            }
            if (this.transition != null) {
                page.put(PdfName.TRANS, this.transition.getTransitionDictionary());
                this.transition = null;
            }
            if (this.duration > 0) {
                page.put(PdfName.DUR, new PdfNumber(this.duration));
                this.duration = 0;
            }
            if (this.pageAA != null) {
                page.put(PdfName.AA, this.writer.addToBody(this.pageAA).getIndirectReference());
                this.pageAA = null;
            }
            if (this.thumb != null) {
                page.put(PdfName.THUMB, this.thumb);
                this.thumb = null;
            }
            if (this.writer.getUserunit() > 0.0f) {
                page.put(PdfName.USERUNIT, new PdfNumber(this.writer.getUserunit()));
            }
            if (this.annotationsImp.hasUnusedAnnotations() && (array = this.annotationsImp.rotateAnnotations(this.writer, this.pageSize)).size() != 0) {
                page.put(PdfName.ANNOTS, array);
            }
            if (this.writer.isTagged()) {
                page.put(PdfName.STRUCTPARENTS, new PdfNumber(this.writer.getCurrentPageNumber() - 1));
            }
            if (this.text.size() > this.textEmptySize) {
                this.text.endText();
            } else {
                this.text = null;
            }
            this.writer.add(page, new PdfContents(this.writer.getDirectContentUnder(), this.graphics, this.text, this.writer.getDirectContent(), this.pageSize));
            this.initPage();
        }
        catch (DocumentException | IOException de) {
            throw new ExceptionConverter(de);
        }
        return true;
    }

    @Override
    public boolean setPageSize(Rectangle pageSize) {
        if (this.writer != null && this.writer.isPaused()) {
            return false;
        }
        this.nextPageSize = new Rectangle(pageSize);
        return true;
    }

    @Override
    public boolean setMargins(float marginLeft, float marginRight, float marginTop, float marginBottom) {
        if (this.writer != null && this.writer.isPaused()) {
            return false;
        }
        this.nextMarginLeft = marginLeft;
        this.nextMarginRight = marginRight;
        this.nextMarginTop = marginTop;
        this.nextMarginBottom = marginBottom;
        return true;
    }

    @Override
    public boolean setMarginMirroring(boolean MarginMirroring) {
        if (this.writer != null && this.writer.isPaused()) {
            return false;
        }
        return super.setMarginMirroring(MarginMirroring);
    }

    @Override
    public boolean setMarginMirroringTopBottom(boolean MarginMirroringTopBottom) {
        if (this.writer != null && this.writer.isPaused()) {
            return false;
        }
        return super.setMarginMirroringTopBottom(MarginMirroringTopBottom);
    }

    @Override
    public void setPageCount(int pageN) {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.setPageCount(pageN);
    }

    @Override
    public void resetPageCount() {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.resetPageCount();
    }

    @Override
    public void setHeader(HeaderFooter header) {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.setHeader(header);
    }

    @Override
    public void resetHeader() {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.resetHeader();
    }

    @Override
    public void setFooter(HeaderFooter footer) {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.setFooter(footer);
    }

    @Override
    public void resetFooter() {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.resetFooter();
    }

    protected void initPage() throws DocumentException {
        ++this.pageN;
        this.annotationsImp.resetAnnotations();
        this.pageResources = new PageResources();
        this.writer.resetContent();
        this.graphics = new PdfContentByte(this.writer);
        this.text = new PdfContentByte(this.writer);
        this.text.reset();
        this.text.beginText();
        this.textEmptySize = this.text.size();
        this.markPoint = 0;
        this.setNewPageSizeAndMargins();
        this.imageEnd = -1.0f;
        this.indentation.imageIndentRight = 0.0f;
        this.indentation.imageIndentLeft = 0.0f;
        this.indentation.indentBottom = 0.0f;
        this.indentation.indentTop = 0.0f;
        this.currentHeight = 0.0f;
        this.thisBoxSize = new HashMap<String, PdfRectangle>(this.boxSize);
        if (this.pageSize.getBackgroundColor() != null || this.pageSize.hasBorders() || this.pageSize.getBorderColor() != null) {
            this.add(this.pageSize);
        }
        float oldleading = this.leading;
        int oldAlignment = this.alignment;
        this.doFooter();
        this.text.moveText(this.left(), this.top());
        this.doHeader();
        this.pageEmpty = true;
        try {
            if (this.imageWait != null) {
                this.add(this.imageWait);
                this.imageWait = null;
            }
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
        this.leading = oldleading;
        this.alignment = oldAlignment;
        this.carriageReturn();
        PdfPageEvent pageEvent = this.writer.getPageEvent();
        if (pageEvent != null) {
            if (this.firstPageEvent) {
                pageEvent.onOpenDocument(this.writer, this);
            }
            pageEvent.onStartPage(this.writer, this);
        }
        this.firstPageEvent = false;
    }

    protected void newLine() throws DocumentException {
        this.lastElementType = -1;
        this.carriageReturn();
        if (this.lines != null && !this.lines.isEmpty()) {
            this.lines.add(this.line);
            this.currentHeight += this.line.height();
        }
        this.line = new PdfLine(this.indentLeft(), this.indentRight(), this.alignment, this.leading);
    }

    protected void carriageReturn() {
        if (this.lines == null) {
            this.lines = new ArrayList<PdfLine>();
        }
        if (this.line != null) {
            if (this.currentHeight + this.line.height() + this.leading < this.indentTop() - this.indentBottom()) {
                if (this.line.size() > 0) {
                    this.currentHeight += this.line.height();
                    this.lines.add(this.line);
                    this.pageEmpty = false;
                }
            } else {
                this.newPage();
            }
        }
        if (this.imageEnd > -1.0f && this.currentHeight > this.imageEnd) {
            this.imageEnd = -1.0f;
            this.indentation.imageIndentRight = 0.0f;
            this.indentation.imageIndentLeft = 0.0f;
        }
        this.line = new PdfLine(this.indentLeft(), this.indentRight(), this.alignment, this.leading);
    }

    public float getVerticalPosition(boolean ensureNewLine) {
        if (ensureNewLine) {
            this.ensureNewLine();
        }
        return this.top() - this.currentHeight - this.indentation.indentTop;
    }

    protected void ensureNewLine() {
        try {
            if (this.lastElementType == 11 || this.lastElementType == 10) {
                this.newLine();
                this.flushLines();
            }
        }
        catch (DocumentException ex) {
            throw new ExceptionConverter(ex);
        }
    }

    protected float flushLines() throws DocumentException {
        if (this.lines == null) {
            return 0.0f;
        }
        if (this.line != null && this.line.size() > 0) {
            this.lines.add(this.line);
            this.line = new PdfLine(this.indentLeft(), this.indentRight(), this.alignment, this.leading);
        }
        if (this.lines.isEmpty()) {
            return 0.0f;
        }
        Object[] currentValues = new Object[2];
        PdfFont currentFont = null;
        float displacement = 0.0f;
        currentValues[1] = Float.valueOf(0.0f);
        Iterator<PdfLine> iterator = this.lines.iterator();
        while (iterator.hasNext()) {
            PdfLine line1;
            PdfLine l = line1 = iterator.next();
            float moveTextX = l.indentLeft() - this.indentLeft() + this.indentation.indentLeft + this.indentation.listIndentLeft + this.indentation.sectionIndentLeft;
            this.text.moveText(moveTextX, -l.height());
            if (l.listSymbol() != null) {
                ColumnText.showTextAligned(this.graphics, 0, new Phrase(l.listSymbol()), this.text.getXTLM() - l.listIndent(), this.text.getYTLM(), 0.0f);
            }
            currentValues[0] = currentFont;
            this.writeLineToContent(l, this.text, this.graphics, currentValues, this.writer.getSpaceCharRatio());
            currentFont = (PdfFont)currentValues[0];
            displacement += l.height();
            this.text.moveText(-moveTextX, 0.0f);
        }
        this.lines = new ArrayList<PdfLine>();
        return displacement;
    }

    void writeLineToContent(PdfLine line, PdfContentByte text, PdfContentByte graphics, Object[] currentValues, float ratio) throws DocumentException {
        float xMarker;
        PdfFont currentFont = (PdfFont)currentValues[0];
        float lastBaseFactor = ((Float)currentValues[1]).floatValue();
        float hangingCorrection = 0.0f;
        float hScale = 1.0f;
        float lastHScale = Float.NaN;
        float baseWordSpacing = 0.0f;
        float baseCharacterSpacing = 0.0f;
        float glueWidth = 0.0f;
        int numberOfSpaces = line.numberOfSpaces();
        int lineLen = line.GetLineLengthUtf32();
        boolean isJustified = line.hasToBeJustified() && (numberOfSpaces != 0 || lineLen > 1);
        int separatorCount = line.getSeparatorCount();
        if (separatorCount > 0) {
            glueWidth = line.widthLeft() / (float)separatorCount;
        } else if (isJustified) {
            if (line.isNewlineSplit() && line.widthLeft() >= lastBaseFactor * (ratio * (float)numberOfSpaces + (float)lineLen - 1.0f)) {
                if (line.isRTL()) {
                    text.moveText(line.widthLeft() - lastBaseFactor * (ratio * (float)numberOfSpaces + (float)lineLen - 1.0f), 0.0f);
                }
                baseWordSpacing = ratio * lastBaseFactor;
                baseCharacterSpacing = lastBaseFactor;
            } else {
                char c;
                String s;
                float width = line.widthLeft();
                PdfChunk last = line.getChunk(line.size() - 1);
                if (last != null && (s = last.toString()).length() > 0 && hangingPunctuation.indexOf(c = s.charAt(s.length() - 1)) >= 0) {
                    float oldWidth = width;
                    hangingCorrection = (width += last.font().width(c) * 0.4f) - oldWidth;
                }
                float baseFactor = numberOfSpaces == 0 && ratio == 1.0E7f ? 0.0f : width / (ratio * (float)numberOfSpaces + (float)lineLen - 1.0f);
                baseWordSpacing = ratio * baseFactor;
                baseCharacterSpacing = baseFactor;
                lastBaseFactor = baseFactor;
            }
        }
        int lastChunkStroke = line.getLastStrokeChunk();
        int chunkStrokeIdx = 0;
        float baseXMarker = xMarker = text.getXTLM();
        float yMarker = text.getYTLM();
        boolean adjustMatrix = false;
        float tabPosition = 0.0f;
        Iterator j = line.iterator();
        while (j.hasNext()) {
            PdfChunk chunk = (PdfChunk)j.next();
            Color color = chunk.color();
            hScale = 1.0f;
            if (chunkStrokeIdx <= lastChunkStroke) {
                float width = isJustified ? chunk.getWidthCorrected(baseCharacterSpacing, baseWordSpacing) : chunk.width();
                if (chunk.isStroked()) {
                    DrawInterface di;
                    PdfChunk nextChunk = line.getChunk(chunkStrokeIdx + 1);
                    if (chunk.isSeparator()) {
                        width = glueWidth;
                        Object[] sep = (Object[])chunk.getAttribute("SEPARATOR");
                        di = (DrawInterface)sep[0];
                        Boolean vertical = (Boolean)sep[1];
                        float fontSize = chunk.font().size();
                        float ascender = chunk.font().getFont().getFontDescriptor(1, fontSize);
                        float descender = chunk.font().getFont().getFontDescriptor(3, fontSize);
                        if (vertical.booleanValue()) {
                            di.draw(graphics, baseXMarker, yMarker + descender, baseXMarker + line.getOriginalWidth(), ascender - descender, yMarker);
                        } else {
                            di.draw(graphics, xMarker, yMarker + descender, xMarker + width, ascender - descender, yMarker);
                        }
                    }
                    if (chunk.isTab()) {
                        Object[] tab = (Object[])chunk.getAttribute("TAB");
                        di = (DrawInterface)tab[0];
                        tabPosition = ((Float)tab[1]).floatValue() + ((Float)tab[3]).floatValue();
                        float fontSize = chunk.font().size();
                        float ascender = chunk.font().getFont().getFontDescriptor(1, fontSize);
                        float descender = chunk.font().getFont().getFontDescriptor(3, fontSize);
                        if (tabPosition > xMarker) {
                            di.draw(graphics, xMarker, yMarker + descender, tabPosition, ascender - descender, yMarker);
                        }
                        float tmp = xMarker;
                        xMarker = tabPosition;
                        tabPosition = tmp;
                    }
                    if (chunk.isAttribute("BACKGROUND")) {
                        float subtract = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute("BACKGROUND")) {
                            subtract = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract += hangingCorrection;
                        }
                        float fontSize = chunk.font().size();
                        float ascender = chunk.font().getFont().getFontDescriptor(1, fontSize);
                        float descender = chunk.font().getFont().getFontDescriptor(3, fontSize);
                        Object[] bgr = (Object[])chunk.getAttribute("BACKGROUND");
                        graphics.setColorFill((Color)bgr[0]);
                        float[] extra = (float[])bgr[1];
                        graphics.rectangle(xMarker - extra[0], yMarker + descender - extra[1] + chunk.getTextRise(), width - subtract + extra[0] + extra[2], ascender - descender + extra[1] + extra[3]);
                        graphics.fill();
                        graphics.setGrayFill(0.0f);
                    }
                    if (chunk.isAttribute("UNDERLINE")) {
                        float subtract = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute("UNDERLINE")) {
                            subtract = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract += hangingCorrection;
                        }
                        Object[][] unders = (Object[][])chunk.getAttribute("UNDERLINE");
                        Color scolor = null;
                        for (Object[] obj : unders) {
                            scolor = (Color)obj[0];
                            float[] ps = (float[])obj[1];
                            if (scolor == null) {
                                scolor = color;
                            }
                            if (scolor != null) {
                                graphics.setColorStroke(scolor);
                            }
                            float fsize = chunk.font().size();
                            graphics.setLineWidth(ps[0] + fsize * ps[1]);
                            float shift = ps[2] + fsize * ps[3];
                            int cap2 = (int)ps[4];
                            if (cap2 != 0) {
                                graphics.setLineCap(cap2);
                            }
                            graphics.moveTo(xMarker, yMarker + shift);
                            graphics.lineTo(xMarker + width - subtract, yMarker + shift);
                            graphics.stroke();
                            if (scolor != null) {
                                graphics.resetGrayStroke();
                            }
                            if (cap2 == 0) continue;
                            graphics.setLineCap(0);
                        }
                        graphics.setLineWidth(1.0f);
                    }
                    if (chunk.isAttribute("ACTION")) {
                        float subtract = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute("ACTION")) {
                            subtract = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract += hangingCorrection;
                        }
                        text.addAnnotation(new PdfAnnotation(this.writer, xMarker, yMarker, xMarker + width - subtract, yMarker + chunk.font().size(), (PdfAction)chunk.getAttribute("ACTION")));
                    }
                    if (chunk.isAttribute("REMOTEGOTO")) {
                        float subtract = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute("REMOTEGOTO")) {
                            subtract = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract += hangingCorrection;
                        }
                        Object[] obj = (Object[])chunk.getAttribute("REMOTEGOTO");
                        String filename = (String)obj[0];
                        if (obj[1] instanceof String) {
                            this.remoteGoto(filename, (String)obj[1], xMarker, yMarker, xMarker + width - subtract, yMarker + chunk.font().size());
                        } else {
                            this.remoteGoto(filename, (Integer)obj[1], xMarker, yMarker, xMarker + width - subtract, yMarker + chunk.font().size());
                        }
                    }
                    if (chunk.isAttribute("LOCALGOTO")) {
                        float subtract = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute("LOCALGOTO")) {
                            subtract = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract += hangingCorrection;
                        }
                        this.localGoto((String)chunk.getAttribute("LOCALGOTO"), xMarker, yMarker, xMarker + width - subtract, yMarker + chunk.font().size());
                    }
                    if (chunk.isAttribute("LOCALDESTINATION")) {
                        float subtract = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute("LOCALDESTINATION")) {
                            subtract = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract += hangingCorrection;
                        }
                        this.localDestination((String)chunk.getAttribute("LOCALDESTINATION"), new PdfDestination(0, xMarker, yMarker + chunk.font().size(), 0.0f));
                    }
                    if (chunk.isAttribute("GENERICTAG")) {
                        float subtract = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute("GENERICTAG")) {
                            subtract = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract += hangingCorrection;
                        }
                        Rectangle rect = new Rectangle(xMarker, yMarker, xMarker + width - subtract, yMarker + chunk.font().size());
                        PdfPageEvent pev = this.writer.getPageEvent();
                        if (pev != null) {
                            pev.onGenericTag(this.writer, this, rect, (String)chunk.getAttribute("GENERICTAG"));
                        }
                    }
                    if (chunk.isAttribute("PDFANNOTATION")) {
                        float subtract = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute("PDFANNOTATION")) {
                            subtract = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract += hangingCorrection;
                        }
                        float fontSize = chunk.font().size();
                        float ascender = chunk.font().getFont().getFontDescriptor(1, fontSize);
                        float descender = chunk.font().getFont().getFontDescriptor(3, fontSize);
                        PdfAnnotation annot = PdfFormField.shallowDuplicate((PdfAnnotation)chunk.getAttribute("PDFANNOTATION"));
                        annot.put(PdfName.RECT, new PdfRectangle(xMarker, yMarker + descender, xMarker + width - subtract, yMarker + ascender));
                        text.addAnnotation(annot);
                    }
                    float[] params = (float[])chunk.getAttribute("SKEW");
                    Float hs = (Float)chunk.getAttribute("HSCALE");
                    if (params != null || hs != null) {
                        float b = 0.0f;
                        float c = 0.0f;
                        if (params != null) {
                            b = params[0];
                            c = params[1];
                        }
                        if (hs != null) {
                            hScale = hs.floatValue();
                        }
                        text.setTextMatrix(hScale, b, c, 1.0f, xMarker, yMarker);
                    }
                    if (chunk.isAttribute("CHAR_SPACING")) {
                        Float cs = (Float)chunk.getAttribute("CHAR_SPACING");
                        text.setCharacterSpacing(cs.floatValue());
                    }
                    if (chunk.isImage()) {
                        Image image = chunk.getImage();
                        float[] matrix = image.matrix();
                        matrix[4] = xMarker + chunk.getImageOffsetX() - matrix[4];
                        matrix[5] = yMarker + chunk.getImageOffsetY() - matrix[5];
                        graphics.addImage(image, matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);
                        text.moveText(xMarker + lastBaseFactor + image.getScaledWidth() - text.getXTLM(), 0.0f);
                    }
                }
                xMarker += width;
                ++chunkStrokeIdx;
            }
            if (chunk.font().compareTo(currentFont) != 0) {
                currentFont = chunk.font();
                text.setFontAndSize(currentFont.getFont(), currentFont.size());
            }
            float rise = 0.0f;
            Object[] textRender = (Object[])chunk.getAttribute("TEXTRENDERMODE");
            int tr = 0;
            float strokeWidth = 1.0f;
            Color strokeColor = null;
            Float fr = (Float)chunk.getAttribute("SUBSUPSCRIPT");
            if (textRender != null) {
                tr = (Integer)textRender[0] & 3;
                if (tr != 0) {
                    text.setTextRenderingMode(tr);
                }
                if (tr == 1 || tr == 2) {
                    strokeWidth = ((Float)textRender[1]).floatValue();
                    if (strokeWidth != 1.0f) {
                        text.setLineWidth(strokeWidth);
                    }
                    if ((strokeColor = (Color)textRender[2]) == null) {
                        strokeColor = color;
                    }
                    if (strokeColor != null) {
                        text.setColorStroke(strokeColor);
                    }
                }
            }
            if (fr != null) {
                rise = fr.floatValue();
            }
            if (color != null) {
                text.setColorFill(color);
            }
            if (rise != 0.0f) {
                text.setTextRise(rise);
            }
            if (chunk.isImage()) {
                adjustMatrix = true;
            } else if (chunk.isHorizontalSeparator()) {
                PdfTextArray array = new PdfTextArray();
                array.add(-glueWidth * 1000.0f / chunk.font.size() / hScale);
                text.showText(array);
            } else if (chunk.isTab()) {
                PdfTextArray array = new PdfTextArray();
                array.add((tabPosition - xMarker) * 1000.0f / chunk.font.size() / hScale);
                text.showText(array);
            } else if (isJustified && numberOfSpaces > 0 && chunk.isSpecialEncoding()) {
                String s;
                int idx;
                if (hScale != lastHScale) {
                    lastHScale = hScale;
                    text.setWordSpacing(baseWordSpacing / hScale);
                    text.setCharacterSpacing(baseCharacterSpacing / hScale + text.getCharacterSpacing());
                }
                if ((idx = (s = chunk.toString()).indexOf(32)) < 0) {
                    text.showText(s);
                } else {
                    float spaceCorrection = -baseWordSpacing * 1000.0f / chunk.font.size() / hScale;
                    PdfTextArray textArray = new PdfTextArray(s.substring(0, idx));
                    int lastIdx = idx;
                    while ((idx = s.indexOf(32, lastIdx + 1)) >= 0) {
                        textArray.add(spaceCorrection);
                        textArray.add(s.substring(lastIdx, idx));
                        lastIdx = idx;
                    }
                    textArray.add(spaceCorrection);
                    textArray.add(s.substring(lastIdx));
                    text.showText(textArray);
                }
            } else {
                if (isJustified && hScale != lastHScale) {
                    lastHScale = hScale;
                    text.setWordSpacing(baseWordSpacing / hScale);
                    text.setCharacterSpacing(baseCharacterSpacing / hScale + text.getCharacterSpacing());
                }
                text.showText(chunk.toString());
            }
            if (rise != 0.0f) {
                text.setTextRise(0.0f);
            }
            if (color != null) {
                text.resetRGBColorFill();
            }
            if (tr != 0) {
                text.setTextRenderingMode(0);
            }
            if (strokeColor != null) {
                text.resetRGBColorStroke();
            }
            if (strokeWidth != 1.0f) {
                text.setLineWidth(1.0f);
            }
            if (chunk.isAttribute("SKEW") || chunk.isAttribute("HSCALE")) {
                adjustMatrix = true;
                text.setTextMatrix(xMarker, yMarker);
            }
            if (!chunk.isAttribute("CHAR_SPACING")) continue;
            text.setCharacterSpacing(baseCharacterSpacing);
        }
        if (isJustified) {
            text.setWordSpacing(0.0f);
            text.setCharacterSpacing(0.0f);
            if (line.isNewlineSplit()) {
                lastBaseFactor = 0.0f;
            }
        }
        if (adjustMatrix) {
            text.moveText(baseXMarker - text.getXTLM(), 0.0f);
        }
        currentValues[0] = currentFont;
        currentValues[1] = Float.valueOf(lastBaseFactor);
    }

    protected float indentLeft() {
        return this.left(this.indentation.indentLeft + this.indentation.listIndentLeft + this.indentation.imageIndentLeft + this.indentation.sectionIndentLeft);
    }

    protected float indentRight() {
        return this.right(this.indentation.indentRight + this.indentation.sectionIndentRight + this.indentation.imageIndentRight);
    }

    protected float indentTop() {
        return this.top(this.indentation.indentTop);
    }

    float indentBottom() {
        return this.bottom(this.indentation.indentBottom);
    }

    protected void addSpacing(float extraspace, float oldleading, Font f) {
        if (extraspace == 0.0f) {
            return;
        }
        if (this.pageEmpty) {
            return;
        }
        if (this.currentHeight + this.line.height() + this.leading > this.indentTop() - this.indentBottom()) {
            return;
        }
        this.leading = extraspace;
        this.carriageReturn();
        if (f.isUnderlined() || f.isStrikethru()) {
            f = new Font(f);
            int style = f.getStyle();
            style &= 0xFFFFFFFB;
            f.setStyle(style &= 0xFFFFFFF7);
        }
        Chunk space = new Chunk(" ", f);
        space.process(this);
        this.carriageReturn();
        this.leading = oldleading;
    }

    protected PdfInfo getInfo() {
        return this.info;
    }

    PdfCatalog getCatalog(PdfIndirectReference pages) {
        PdfCatalog catalog = new PdfCatalog(pages, this.writer);
        if (this.rootOutline.getKids().size() > 0) {
            catalog.put(PdfName.PAGEMODE, PdfName.USEOUTLINES);
            catalog.put(PdfName.OUTLINES, this.rootOutline.indirectReference());
        }
        this.writer.getPdfVersion().addToCatalog(catalog);
        this.viewerPreferences.addToCatalog(catalog);
        if (this.pageLabels != null) {
            catalog.put(PdfName.PAGELABELS, this.pageLabels.getDictionary(this.writer));
        }
        catalog.addNames(this.localDestinations, this.getDocumentLevelJS(), this.documentFileAttachment, this.writer);
        if (this.openActionName != null) {
            PdfAction action = this.getLocalGotoAction(this.openActionName);
            catalog.setOpenAction(action);
        } else if (this.openActionAction != null) {
            catalog.setOpenAction(this.openActionAction);
        }
        if (this.additionalActions != null) {
            catalog.setAdditionalActions(this.additionalActions);
        }
        if (this.collection != null) {
            catalog.put(PdfName.COLLECTION, this.collection);
        }
        if (this.annotationsImp.hasValidAcroForm()) {
            try {
                catalog.put(PdfName.ACROFORM, this.writer.addToBody(this.annotationsImp.getAcroForm()).getIndirectReference());
            }
            catch (IOException e) {
                throw new ExceptionConverter(e);
            }
        }
        return catalog;
    }

    void addOutline(PdfOutline outline, String name) {
        this.localDestination(name, outline.getPdfDestination());
    }

    public PdfOutline getRootOutline() {
        return this.rootOutline;
    }

    void calculateOutlineCount() {
        if (this.rootOutline.getKids().size() == 0) {
            return;
        }
        this.traverseOutlineCount(this.rootOutline);
    }

    void traverseOutlineCount(PdfOutline outline) {
        java.util.List<PdfOutline> kids = outline.getKids();
        PdfOutline parent = outline.parent();
        if (kids.isEmpty()) {
            if (parent != null) {
                parent.setCount(parent.getCount() + 1);
            }
        } else {
            for (PdfOutline kid : kids) {
                this.traverseOutlineCount(kid);
            }
            if (parent != null) {
                if (outline.isOpen()) {
                    parent.setCount(outline.getCount() + parent.getCount() + 1);
                } else {
                    parent.setCount(parent.getCount() + 1);
                    outline.setCount(-outline.getCount());
                }
            }
        }
    }

    void writeOutlines() throws IOException {
        if (this.rootOutline.getKids().size() == 0) {
            return;
        }
        this.outlineTree(this.rootOutline);
        this.writer.addToBody((PdfObject)this.rootOutline, this.rootOutline.indirectReference());
    }

    void outlineTree(PdfOutline outline) throws IOException {
        outline.setIndirectReference(this.writer.getPdfIndirectReference());
        if (outline.parent() != null) {
            outline.put(PdfName.PARENT, outline.parent().indirectReference());
        }
        java.util.List<PdfOutline> kids = outline.getKids();
        int size = kids.size();
        for (PdfOutline kid1 : kids) {
            this.outlineTree(kid1);
        }
        for (int k = 0; k < size; ++k) {
            if (k > 0) {
                kids.get(k).put(PdfName.PREV, kids.get(k - 1).indirectReference());
            }
            if (k >= size - 1) continue;
            kids.get(k).put(PdfName.NEXT, kids.get(k + 1).indirectReference());
        }
        if (size > 0) {
            outline.put(PdfName.FIRST, kids.get(0).indirectReference());
            outline.put(PdfName.LAST, kids.get(size - 1).indirectReference());
        }
        for (PdfOutline kid : kids) {
            this.writer.addToBody((PdfObject)kid, kid.indirectReference());
        }
    }

    void setViewerPreferences(int preferences) {
        this.viewerPreferences.setViewerPreferences(preferences);
    }

    void addViewerPreference(PdfName key, PdfObject value) {
        this.viewerPreferences.addViewerPreference(key, value);
    }

    void setPageLabels(PdfPageLabels pageLabels) {
        this.pageLabels = pageLabels;
    }

    void localGoto(String name, float llx, float lly, float urx, float ury) {
        PdfAction action = this.getLocalGotoAction(name);
        this.annotationsImp.addPlainAnnotation(new PdfAnnotation(this.writer, llx, lly, urx, ury, action));
    }

    void remoteGoto(String filename, String name, float llx, float lly, float urx, float ury) {
        this.annotationsImp.addPlainAnnotation(new PdfAnnotation(this.writer, llx, lly, urx, ury, new PdfAction(filename, name)));
    }

    void remoteGoto(String filename, int page, float llx, float lly, float urx, float ury) {
        this.addAnnotation(new PdfAnnotation(this.writer, llx, lly, urx, ury, new PdfAction(filename, page)));
    }

    void setAction(PdfAction action, float llx, float lly, float urx, float ury) {
        this.addAnnotation(new PdfAnnotation(this.writer, llx, lly, urx, ury, action));
    }

    PdfAction getLocalGotoAction(String name) {
        PdfAction action;
        Object[] obj = this.localDestinations.get(name);
        if (obj == null) {
            obj = new Object[3];
        }
        if (obj[0] == null) {
            if (obj[1] == null) {
                obj[1] = this.writer.getPdfIndirectReference();
            }
            action = new PdfAction((PdfIndirectReference)obj[1]);
            obj[0] = action;
            this.localDestinations.put(name, obj);
        } else {
            action = (PdfAction)obj[0];
        }
        return action;
    }

    boolean localDestination(String name, PdfDestination destination) {
        Object[] obj = this.localDestinations.get(name);
        if (obj == null) {
            obj = new Object[3];
        }
        if (obj[2] != null) {
            return false;
        }
        obj[2] = destination;
        this.localDestinations.put(name, obj);
        if (!destination.hasPage()) {
            destination.addPage(this.writer.getCurrentPage());
        }
        return true;
    }

    void addJavaScript(PdfAction js) {
        if (js.get(PdfName.JS) == null) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("only.javascript.actions.are.allowed"));
        }
        try {
            this.documentLevelJS.put(SIXTEEN_DIGITS.format(this.jsCounter++), this.writer.addToBody(js).getIndirectReference());
        }
        catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

    void addJavaScript(String name, PdfAction js) {
        if (js.get(PdfName.JS) == null) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("only.javascript.actions.are.allowed"));
        }
        try {
            this.documentLevelJS.put(name, this.writer.addToBody(js).getIndirectReference());
        }
        catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

    HashMap<String, PdfIndirectReference> getDocumentLevelJS() {
        return this.documentLevelJS;
    }

    void addFileAttachment(String description, PdfFileSpecification fs) throws IOException {
        if (description == null) {
            PdfString desc = (PdfString)fs.get(PdfName.DESC);
            description = desc == null ? "" : PdfEncodings.convertToString(desc.getBytes(), null);
        }
        fs.addDescription(description, true);
        if (description.length() == 0) {
            description = "Unnamed";
        }
        String fn = PdfEncodings.convertToString(new PdfString(description, "UnicodeBig").getBytes(), null);
        int k = 0;
        while (this.documentFileAttachment.containsKey(fn)) {
            fn = PdfEncodings.convertToString(new PdfString(description + " " + ++k, "UnicodeBig").getBytes(), null);
        }
        this.documentFileAttachment.put(fn, fs.getReference());
    }

    HashMap<String, PdfIndirectReference> getDocumentFileAttachment() {
        return this.documentFileAttachment;
    }

    void setOpenAction(String name) {
        this.openActionName = name;
        this.openActionAction = null;
    }

    void setOpenAction(PdfAction action) {
        this.openActionAction = action;
        this.openActionName = null;
    }

    void addAdditionalAction(PdfName actionType, PdfAction action) {
        if (this.additionalActions == null) {
            this.additionalActions = new PdfDictionary();
        }
        if (action == null) {
            this.additionalActions.remove(actionType);
        } else {
            this.additionalActions.put(actionType, action);
        }
        if (this.additionalActions.size() == 0) {
            this.additionalActions = null;
        }
    }

    public void setCollection(PdfCollection collection) {
        this.collection = collection;
    }

    PdfAcroForm getAcroForm() {
        return this.annotationsImp.getAcroForm();
    }

    void setSigFlags(int f) {
        this.annotationsImp.setSigFlags(f);
    }

    void addCalculationOrder(PdfFormField formField) {
        this.annotationsImp.addCalculationOrder(formField);
    }

    void addAnnotation(PdfAnnotation annot) {
        this.pageEmpty = false;
        this.annotationsImp.addAnnotation(annot);
    }

    int getMarkPoint() {
        return this.markPoint;
    }

    void incMarkPoint() {
        ++this.markPoint;
    }

    void setCropBoxSize(Rectangle crop) {
        this.setBoxSize("crop", crop);
    }

    void setBoxSize(String boxName, Rectangle size) {
        if (size == null) {
            this.boxSize.remove(boxName);
        } else {
            this.boxSize.put(boxName, new PdfRectangle(size));
        }
    }

    protected void setNewPageSizeAndMargins() {
        this.pageSize = this.nextPageSize;
        if (this.marginMirroring && (this.getPageNumber() & 1) == 0) {
            this.marginRight = this.nextMarginLeft;
            this.marginLeft = this.nextMarginRight;
        } else {
            this.marginLeft = this.nextMarginLeft;
            this.marginRight = this.nextMarginRight;
        }
        if (this.marginMirroringTopBottom && (this.getPageNumber() & 1) == 0) {
            this.marginTop = this.nextMarginBottom;
            this.marginBottom = this.nextMarginTop;
        } else {
            this.marginTop = this.nextMarginTop;
            this.marginBottom = this.nextMarginBottom;
        }
    }

    Rectangle getBoxSize(String boxName) {
        PdfRectangle r = this.thisBoxSize.get(boxName);
        if (r != null) {
            return r.getRectangle();
        }
        return null;
    }

    void setPageEmpty(boolean pageEmpty) {
        this.pageEmpty = pageEmpty;
    }

    boolean isPageEmpty() {
        return this.writer == null || this.writer.getDirectContent().size() == 0 && this.writer.getDirectContentUnder().size() == 0 && (this.pageEmpty || this.writer.isPaused());
    }

    void setDuration(int seconds) {
        this.duration = seconds > 0 ? seconds : -1;
    }

    void setTransition(PdfTransition transition) {
        this.transition = transition;
    }

    void setPageAction(PdfName actionType, PdfAction action) {
        if (this.pageAA == null) {
            this.pageAA = new PdfDictionary();
        }
        this.pageAA.put(actionType, action);
    }

    void setThumbnail(Image image) throws DocumentException {
        this.thumb = this.writer.getImageReference(this.writer.addDirectImageSimple(image));
    }

    PageResources getPageResources() {
        return this.pageResources;
    }

    boolean isStrictImageSequence() {
        return this.strictImageSequence;
    }

    void setStrictImageSequence(boolean strictImageSequence) {
        this.strictImageSequence = strictImageSequence;
    }

    public void clearTextWrap() {
        float tmpHeight = this.imageEnd - this.currentHeight;
        if (this.line != null) {
            tmpHeight += this.line.height();
        }
        if (this.imageEnd > -1.0f && tmpHeight > 0.0f) {
            this.carriageReturn();
            this.currentHeight += tmpHeight;
        }
    }

    protected void add(Image image) throws DocumentException {
        if (image.hasAbsoluteY()) {
            this.graphics.addImage(image);
            this.pageEmpty = false;
            return;
        }
        if (this.currentHeight != 0.0f && this.indentTop() - this.currentHeight - image.getScaledHeight() < this.indentBottom()) {
            if (!this.strictImageSequence && this.imageWait == null) {
                this.imageWait = image;
                return;
            }
            this.newPage();
            if (this.currentHeight != 0.0f && this.indentTop() - this.currentHeight - image.getScaledHeight() < this.indentBottom()) {
                this.imageWait = image;
                return;
            }
        }
        this.pageEmpty = false;
        if (image == this.imageWait) {
            this.imageWait = null;
        }
        boolean textwrap = (image.getAlignment() & 4) == 4 && (image.getAlignment() & 1) != 1;
        boolean underlying = (image.getAlignment() & 8) == 8;
        float diff = this.leading / 2.0f;
        if (textwrap) {
            diff += this.leading;
        }
        float lowerleft = this.indentTop() - this.currentHeight - image.getScaledHeight() - diff;
        float[] mt = image.matrix();
        float startPosition = this.indentLeft() - mt[4];
        if ((image.getAlignment() & 2) == 2) {
            startPosition = this.indentRight() - image.getScaledWidth() - mt[4];
        }
        if ((image.getAlignment() & 1) == 1) {
            startPosition = this.indentLeft() + (this.indentRight() - this.indentLeft() - image.getScaledWidth()) / 2.0f - mt[4];
        }
        if (image.hasAbsoluteX()) {
            startPosition = image.getAbsoluteX();
        }
        if (textwrap) {
            if (this.imageEnd < 0.0f || this.imageEnd < this.currentHeight + image.getScaledHeight() + diff) {
                this.imageEnd = this.currentHeight + image.getScaledHeight() + diff;
            }
            if ((image.getAlignment() & 2) == 2) {
                this.indentation.imageIndentRight += image.getScaledWidth() + image.getIndentationLeft();
            } else {
                this.indentation.imageIndentLeft += image.getScaledWidth() + image.getIndentationRight();
            }
        } else {
            startPosition = (image.getAlignment() & 2) == 2 ? (startPosition -= image.getIndentationRight()) : ((image.getAlignment() & 1) == 1 ? (startPosition += image.getIndentationLeft() - image.getIndentationRight()) : (startPosition += image.getIndentationLeft()));
        }
        this.graphics.addImage(image, mt[0], mt[1], mt[2], mt[3], startPosition, lowerleft - mt[5]);
        if (!textwrap && !underlying) {
            this.currentHeight += image.getScaledHeight() + diff;
            this.flushLines();
            this.text.moveText(0.0f, -(image.getScaledHeight() + diff));
            this.newLine();
        }
    }

    void addPTable(PdfPTable ptable) throws DocumentException {
        ColumnText ct = new ColumnText(this.writer.getDirectContent());
        if (ptable.getKeepTogether() && !this.fitsPage(ptable, 0.0f) && this.currentHeight > 0.0f) {
            this.newPage();
        }
        if (this.currentHeight > 0.0f) {
            Paragraph p = new Paragraph();
            p.setLeading(0.0f);
            ct.addElement(p);
        }
        ct.addElement(ptable);
        boolean he = ptable.isHeadersInEvent();
        ptable.setHeadersInEvent(true);
        int loop = 0;
        while (true) {
            ct.setSimpleColumn(this.indentLeft(), this.indentBottom(), this.indentRight(), this.indentTop() - this.currentHeight);
            int status = ct.go();
            if ((status & 1) != 0) {
                this.text.moveText(0.0f, ct.getYLine() - this.indentTop() + this.currentHeight);
                this.currentHeight = this.indentTop() - ct.getYLine();
                break;
            }
            loop = this.indentTop() - this.currentHeight == ct.getYLine() ? ++loop : 0;
            if (loop == 3) {
                this.add(new Paragraph("ERROR: Infinite table loop"));
                break;
            }
            this.newPage();
        }
        ptable.setHeadersInEvent(he);
    }

    boolean fitsPage(PdfPTable table, float margin) {
        if (!table.isLockedWidth()) {
            float totalWidth = (this.indentRight() - this.indentLeft()) * table.getWidthPercentage() / 100.0f;
            table.setTotalWidth(totalWidth);
        }
        this.ensureNewLine();
        return table.getTotalHeight() + (this.currentHeight > 0.0f ? table.spacingBefore() : 0.0f) <= this.indentTop() - this.currentHeight - this.indentBottom() - margin;
    }

    private void addPdfTable(Table t) throws DocumentException {
        this.flushLines();
        PdfTable table = new PdfTable(t, this.indentLeft(), this.indentRight(), this.indentTop() - this.currentHeight);
        RenderingContext ctx = new RenderingContext();
        ctx.pagetop = this.indentTop();
        ctx.oldHeight = this.currentHeight;
        ctx.cellGraphics = new PdfContentByte(this.writer);
        ctx.rowspanMap = new HashMap<PdfCell, Integer>();
        ctx.table = table;
        ArrayList<PdfCell> headerCells = table.getHeaderCells();
        ArrayList<PdfCell> cells = table.getCells();
        java.util.List<java.util.List<PdfCell>> rows = this.extractRows(cells, ctx);
        boolean isContinue = false;
        while (!cells.isEmpty()) {
            int i;
            PdfCell cell;
            ctx.lostTableBottom = 0.0f;
            boolean cellsShown = false;
            Iterator<java.util.List<PdfCell>> iterator = rows.iterator();
            boolean atLeastOneFits = false;
            while (iterator.hasNext()) {
                java.util.List<PdfCell> row = iterator.next();
                this.analyzeRow(rows, ctx);
                this.renderCells(ctx, row, table.hasToFitPageCells() & atLeastOneFits);
                if (!this.mayBeRemoved(row)) break;
                this.consumeRowspan(row, ctx);
                iterator.remove();
                atLeastOneFits = true;
            }
            cells.clear();
            HashSet<PdfCell> opt = new HashSet<PdfCell>();
            for (ArrayList arrayList : rows) {
                for (Object o : arrayList) {
                    cell = (PdfCell)o;
                    if (opt.contains(cell)) continue;
                    cells.add(cell);
                    opt.add(cell);
                }
            }
            Rectangle rectangle = new Rectangle(table);
            rectangle.setBorder(table.getBorder());
            rectangle.setBorderWidth(table.getBorderWidth());
            rectangle.setBorderColor(table.getBorderColor());
            rectangle.setBackgroundColor(table.getBackgroundColor());
            PdfContentByte under = this.writer.getDirectContentUnder();
            under.rectangle(rectangle.rectangle(this.top(), this.indentBottom()));
            under.add(ctx.cellGraphics);
            rectangle.setBackgroundColor(null);
            Rectangle rectangle2 = rectangle.rectangle(this.top(), this.indentBottom());
            rectangle2.setBorder(table.getBorder());
            under.rectangle(rectangle2);
            ctx.cellGraphics = new PdfContentByte(null);
            if (rows.isEmpty()) continue;
            isContinue = true;
            this.graphics.setLineWidth(table.getBorderWidth());
            if (cellsShown && (table.getBorder() & 2) == 2) {
                Color tColor = table.getBorderColor();
                if (tColor != null) {
                    this.graphics.setColorStroke(tColor);
                }
                this.graphics.moveTo(table.getLeft(), Math.max(table.getBottom(), this.indentBottom()));
                this.graphics.lineTo(table.getRight(), Math.max(table.getBottom(), this.indentBottom()));
                this.graphics.stroke();
                if (tColor != null) {
                    this.graphics.resetRGBColorStroke();
                }
            }
            this.pageEmpty = false;
            float difference = ctx.lostTableBottom;
            this.newPage();
            float heightCorrection = 0.0f;
            boolean somethingAdded = false;
            if (this.currentHeight > 0.0f) {
                heightCorrection = 6.0f;
                this.currentHeight += heightCorrection;
                somethingAdded = true;
                this.newLine();
                this.flushLines();
                this.indentation.indentTop = this.currentHeight - this.leading;
                this.currentHeight = 0.0f;
            } else {
                this.flushLines();
            }
            int size = headerCells.size();
            if (size > 0) {
                cell = (PdfCell)headerCells.get(0);
                float oldTop = cell.getTop(0.0f);
                for (int i2 = 0; i2 < size; ++i2) {
                    cell = (PdfCell)headerCells.get(i2);
                    cell.setTop(this.indentTop() - oldTop + cell.getTop(0.0f));
                    cell.setBottom(this.indentTop() - oldTop + cell.getBottom(0.0f));
                    ctx.pagetop = cell.getBottom();
                    ctx.cellGraphics.rectangle(cell.rectangle(this.indentTop(), this.indentBottom()));
                    ArrayList<Image> images = cell.getImages(this.indentTop(), this.indentBottom());
                    for (Object e : images) {
                        cellsShown = true;
                        Image image = (Image)e;
                        this.graphics.addImage(image);
                    }
                    this.lines = cell.getLines(this.indentTop(), this.indentBottom());
                    float cellTop = cell.getTop(this.indentTop());
                    this.text.moveText(0.0f, cellTop - heightCorrection);
                    float f = this.flushLines() - cellTop + heightCorrection;
                    this.text.moveText(0.0f, f);
                }
                this.currentHeight = this.indentTop() - ctx.pagetop + table.cellspacing();
                this.text.moveText(0.0f, ctx.pagetop - this.indentTop() - this.currentHeight);
            } else if (somethingAdded) {
                ctx.pagetop = this.indentTop();
                this.text.moveText(0.0f, -table.cellspacing());
            }
            ctx.oldHeight = this.currentHeight - heightCorrection;
            size = Math.min(cells.size(), table.columns());
            for (i = 0; i < size; ++i) {
                float neededHeight;
                float newBottom;
                cell = (PdfCell)cells.get(i);
                if (!(cell.getTop(-table.cellspacing()) > ctx.lostTableBottom) || !((newBottom = ctx.pagetop - difference + cell.getBottom()) > ctx.pagetop - (neededHeight = cell.remainingHeight()))) continue;
                difference += newBottom - (ctx.pagetop - neededHeight);
            }
            size = cells.size();
            table.setTop(this.indentTop());
            table.setBottom(ctx.pagetop - difference + table.getBottom(table.cellspacing()));
            for (i = 0; i < size; ++i) {
                cell = (PdfCell)cells.get(i);
                float newBottom = ctx.pagetop - difference + cell.getBottom();
                float newTop = ctx.pagetop - difference + cell.getTop(-table.cellspacing());
                if (newTop > this.indentTop() - this.currentHeight) {
                    newTop = this.indentTop() - this.currentHeight;
                }
                cell.setTop(newTop);
                cell.setBottom(newBottom);
            }
        }
        float tableHeight = table.getTop() - table.getBottom();
        if (isContinue) {
            this.currentHeight = tableHeight;
            this.text.moveText(0.0f, -(tableHeight - ctx.oldHeight * 2.0f));
        } else {
            this.currentHeight = ctx.oldHeight + tableHeight;
            this.text.moveText(0.0f, -tableHeight);
        }
        this.pageEmpty = false;
    }

    protected void analyzeRow(java.util.List<java.util.List<PdfCell>> rows, RenderingContext ctx) {
        ctx.maxCellBottom = this.indentBottom();
        int rowIndex = 0;
        java.util.List<PdfCell> row = rows.get(rowIndex);
        int maxRowspan = 1;
        for (PdfCell cell : row) {
            maxRowspan = Math.max(ctx.currentRowspan(cell), maxRowspan);
        }
        boolean useTop = true;
        if ((rowIndex += maxRowspan) == rows.size()) {
            rowIndex = rows.size() - 1;
            useTop = false;
        }
        if (rowIndex < 0 || rowIndex >= rows.size()) {
            return;
        }
        row = rows.get(rowIndex);
        for (PdfCell cell : row) {
            Rectangle cellRect = cell.rectangle(ctx.pagetop, this.indentBottom());
            if (useTop) {
                ctx.maxCellBottom = Math.max(ctx.maxCellBottom, cellRect.getTop());
                continue;
            }
            if (ctx.currentRowspan(cell) != 1) continue;
            ctx.maxCellBottom = Math.max(ctx.maxCellBottom, cellRect.getBottom());
        }
    }

    protected boolean mayBeRemoved(java.util.List<PdfCell> row) {
        Iterator<PdfCell> iterator = row.iterator();
        boolean mayBeRemoved = true;
        while (iterator.hasNext()) {
            PdfCell cell = iterator.next();
            mayBeRemoved &= cell.mayBeRemoved();
        }
        return mayBeRemoved;
    }

    protected void consumeRowspan(java.util.List<PdfCell> row, RenderingContext ctx) {
        for (PdfCell c : row) {
            ctx.consumeRowspan(c);
        }
    }

    protected java.util.List<java.util.List<PdfCell>> extractRows(java.util.List<PdfCell> cells, RenderingContext ctx) {
        PdfCell previousCell = null;
        ArrayList<java.util.List<PdfCell>> rows = new ArrayList<java.util.List<PdfCell>>();
        ArrayList<PdfCell> rowCells = new ArrayList<PdfCell>();
        Iterator<PdfCell> iterator = cells.iterator();
        while (iterator.hasNext()) {
            boolean isCurrentCellPartOfRow;
            PdfCell cell = iterator.next();
            boolean isAdded = false;
            boolean isEndOfRow = !iterator.hasNext();
            boolean bl = isCurrentCellPartOfRow = !iterator.hasNext();
            if (previousCell != null && cell.getLeft() <= previousCell.getLeft()) {
                isEndOfRow = true;
                isCurrentCellPartOfRow = false;
            }
            if (isCurrentCellPartOfRow) {
                rowCells.add(cell);
                isAdded = true;
            }
            if (isEndOfRow) {
                if (!rowCells.isEmpty()) {
                    rows.add(rowCells);
                }
                rowCells = new ArrayList();
            }
            if (!isAdded) {
                rowCells.add(cell);
            }
            previousCell = cell;
        }
        if (!rowCells.isEmpty()) {
            rows.add(rowCells);
        }
        for (int i = rows.size() - 1; i >= 0; --i) {
            java.util.List row = (java.util.List)rows.get(i);
            for (int j = 0; j < row.size(); ++j) {
                PdfCell c = (PdfCell)row.get(j);
                int rowspan = c.rowspan();
                for (int k = 1; k < rowspan && rows.size() < i + k; ++k) {
                    java.util.List spannedRow = (java.util.List)rows.get(i + k);
                    if (spannedRow.size() <= j) continue;
                    spannedRow.add(j, c);
                }
            }
        }
        return rows;
    }

    protected void renderCells(RenderingContext ctx, java.util.List cells, boolean hasToFit) throws DocumentException {
        if (hasToFit) {
            for (PdfCell cell : cells) {
                if (cell.isHeader() || !(cell.getBottom() < this.indentBottom())) continue;
                return;
            }
        }
        for (PdfCell cell : cells) {
            if (ctx.isCellRenderedOnPage(cell, this.getPageNumber())) continue;
            float correction = 0.0f;
            if (ctx.numCellRendered(cell) >= 1) {
                correction = 1.0f;
            }
            this.lines = cell.getLines(ctx.pagetop, this.indentBottom() - correction);
            if (this.lines != null && !this.lines.isEmpty()) {
                float cellTop = cell.getTop(ctx.pagetop - ctx.oldHeight);
                this.text.moveText(0.0f, cellTop);
                float cellDisplacement = this.flushLines() - cellTop;
                this.text.moveText(0.0f, cellDisplacement);
                if (ctx.oldHeight + cellDisplacement > this.currentHeight) {
                    this.currentHeight = ctx.oldHeight + cellDisplacement;
                }
                ctx.cellRendered(cell, this.getPageNumber());
            }
            float indentBottom = Math.max(cell.getBottom(), this.indentBottom());
            Rectangle tableRect = ctx.table.rectangle(ctx.pagetop, this.indentBottom());
            indentBottom = Math.max(tableRect.getBottom(), indentBottom);
            Rectangle cellRect = cell.rectangle(tableRect.getTop(), indentBottom);
            if (cellRect.getHeight() > 0.0f) {
                ctx.lostTableBottom = indentBottom;
                ctx.cellGraphics.rectangle(cellRect);
            }
            ArrayList<Image> images = cell.getImages(ctx.pagetop, this.indentBottom());
            for (Object e : images) {
                Image image = (Image)e;
                this.graphics.addImage(image);
            }
        }
    }

    float bottom(Table table) {
        PdfTable tmp = new PdfTable(table, this.indentLeft(), this.indentRight(), this.indentTop() - this.currentHeight);
        return tmp.getBottom();
    }

    protected void doFooter() throws DocumentException {
        if (this.footer == null) {
            return;
        }
        float tmpIndentLeft = this.indentation.indentLeft;
        float tmpIndentRight = this.indentation.indentRight;
        float tmpListIndentLeft = this.indentation.listIndentLeft;
        float tmpImageIndentLeft = this.indentation.imageIndentLeft;
        float tmpImageIndentRight = this.indentation.imageIndentRight;
        this.indentation.indentRight = 0.0f;
        this.indentation.indentLeft = 0.0f;
        this.indentation.listIndentLeft = 0.0f;
        this.indentation.imageIndentLeft = 0.0f;
        this.indentation.imageIndentRight = 0.0f;
        this.footer.setPageNumber(this.pageN);
        this.leading = this.footer.paragraph().getTotalLeading();
        this.add(this.footer.paragraph());
        this.indentation.indentBottom = this.currentHeight;
        this.text.moveText(this.left(), this.indentBottom());
        this.flushLines();
        this.text.moveText(-this.left(), -this.bottom());
        this.footer.setTop(this.bottom(this.currentHeight));
        this.footer.setBottom(this.bottom() - 0.75f * this.leading);
        this.footer.setLeft(this.left());
        this.footer.setRight(this.right());
        this.graphics.rectangle(this.footer);
        this.indentation.indentBottom = this.currentHeight + this.leading * 2.0f;
        this.currentHeight = 0.0f;
        this.indentation.indentLeft = tmpIndentLeft;
        this.indentation.indentRight = tmpIndentRight;
        this.indentation.listIndentLeft = tmpListIndentLeft;
        this.indentation.imageIndentLeft = tmpImageIndentLeft;
        this.indentation.imageIndentRight = tmpImageIndentRight;
    }

    protected void doHeader() throws DocumentException {
        if (this.header == null) {
            return;
        }
        float tmpIndentLeft = this.indentation.indentLeft;
        float tmpIndentRight = this.indentation.indentRight;
        float tmpListIndentLeft = this.indentation.listIndentLeft;
        float tmpImageIndentLeft = this.indentation.imageIndentLeft;
        float tmpImageIndentRight = this.indentation.imageIndentRight;
        this.indentation.indentRight = 0.0f;
        this.indentation.indentLeft = 0.0f;
        this.indentation.listIndentLeft = 0.0f;
        this.indentation.imageIndentLeft = 0.0f;
        this.indentation.imageIndentRight = 0.0f;
        this.header.setPageNumber(this.pageN);
        this.leading = this.header.paragraph().getTotalLeading();
        this.text.moveText(0.0f, this.leading);
        this.add(this.header.paragraph());
        this.newLine();
        this.indentation.indentTop = this.currentHeight - this.leading;
        this.header.setTop(this.top() + this.leading);
        this.header.setBottom(this.indentTop() + this.leading * 2.0f / 3.0f);
        this.header.setLeft(this.left());
        this.header.setRight(this.right());
        this.graphics.rectangle(this.header);
        this.flushLines();
        this.currentHeight = 0.0f;
        this.indentation.indentLeft = tmpIndentLeft;
        this.indentation.indentRight = tmpIndentRight;
        this.indentation.listIndentLeft = tmpListIndentLeft;
        this.indentation.imageIndentLeft = tmpImageIndentLeft;
        this.indentation.imageIndentRight = tmpImageIndentRight;
    }

    protected static class RenderingContext {
        float pagetop = -1.0f;
        float oldHeight = -1.0f;
        PdfContentByte cellGraphics = null;
        float lostTableBottom;
        float maxCellBottom;
        float maxCellHeight;
        Map<PdfCell, Integer> rowspanMap = new HashMap<PdfCell, Integer>();
        Map<PdfCell, Integer> pageMap = new HashMap<PdfCell, Integer>();
        Map<Integer, Set<PdfCell>> pageCellSetMap = new HashMap<Integer, Set<PdfCell>>();
        public PdfTable table;

        protected RenderingContext() {
        }

        public int consumeRowspan(PdfCell c) {
            if (c.rowspan() == 1) {
                return 1;
            }
            Integer i = this.rowspanMap.get(c);
            if (i == null) {
                i = c.rowspan();
            }
            i = i - 1;
            this.rowspanMap.put(c, i);
            if (i < 1) {
                return 1;
            }
            return i;
        }

        public int currentRowspan(PdfCell c) {
            Integer i = this.rowspanMap.get(c);
            if (i == null) {
                return c.rowspan();
            }
            return i;
        }

        public int cellRendered(PdfCell cell, int pageNumber) {
            Integer i = this.pageMap.get(cell);
            i = i == null ? Integer.valueOf(1) : Integer.valueOf(i + 1);
            this.pageMap.put(cell, i);
            Integer pageInteger = pageNumber;
            Set set = this.pageCellSetMap.computeIfAbsent(pageInteger, k -> new HashSet());
            set.add(cell);
            return i;
        }

        public int numCellRendered(PdfCell cell) {
            Integer i = this.pageMap.get(cell);
            if (i == null) {
                i = 0;
            }
            return i;
        }

        public boolean isCellRenderedOnPage(PdfCell cell, int pageNumber) {
            Integer pageInteger = pageNumber;
            Set<PdfCell> set = this.pageCellSetMap.get(pageInteger);
            if (set != null) {
                return set.contains(cell);
            }
            return false;
        }
    }

    public static class Indentation {
        float indentLeft = 0.0f;
        float sectionIndentLeft = 0.0f;
        float listIndentLeft = 0.0f;
        float imageIndentLeft = 0.0f;
        float indentRight = 0.0f;
        float sectionIndentRight = 0.0f;
        float imageIndentRight = 0.0f;
        float indentTop = 0.0f;
        float indentBottom = 0.0f;
    }

    static class PdfCatalog
    extends PdfDictionary {
        PdfWriter writer;

        PdfCatalog(PdfIndirectReference pages, PdfWriter writer) {
            super(CATALOG);
            this.writer = writer;
            this.put(PdfName.PAGES, pages);
        }

        void addNames(TreeMap<String, Object[]> localDestinations, Map<String, PdfIndirectReference> documentLevelJS, Map<String, PdfIndirectReference> documentFileAttachment, PdfWriter writer) {
            if (localDestinations.isEmpty() && documentLevelJS.isEmpty() && documentFileAttachment.isEmpty()) {
                return;
            }
            try {
                PdfDictionary names = new PdfDictionary();
                if (!localDestinations.isEmpty()) {
                    PdfArray ar = new PdfArray();
                    for (Map.Entry<String, Object[]> entry : localDestinations.entrySet()) {
                        String name = entry.getKey();
                        Object[] obj = entry.getValue();
                        if (obj[2] == null) continue;
                        PdfIndirectReference ref = (PdfIndirectReference)obj[1];
                        ar.add(new PdfString(name, null));
                        ar.add(ref);
                    }
                    if (ar.size() > 0) {
                        PdfDictionary dests = new PdfDictionary();
                        dests.put(PdfName.NAMES, ar);
                        names.put(PdfName.DESTS, writer.addToBody(dests).getIndirectReference());
                    }
                }
                if (!documentLevelJS.isEmpty()) {
                    PdfDictionary tree = PdfNameTree.writeTree(documentLevelJS, writer);
                    names.put(PdfName.JAVASCRIPT, writer.addToBody(tree).getIndirectReference());
                }
                if (!documentFileAttachment.isEmpty()) {
                    names.put(PdfName.EMBEDDEDFILES, writer.addToBody(PdfNameTree.writeTree(documentFileAttachment, writer)).getIndirectReference());
                }
                if (names.size() > 0) {
                    this.put(PdfName.NAMES, writer.addToBody(names).getIndirectReference());
                }
            }
            catch (IOException e) {
                throw new ExceptionConverter(e);
            }
        }

        void setOpenAction(PdfAction action) {
            this.put(PdfName.OPENACTION, action);
        }

        void setAdditionalActions(PdfDictionary actions) {
            try {
                this.put(PdfName.AA, this.writer.addToBody(actions).getIndirectReference());
            }
            catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }
    }

    public static class PdfInfo
    extends PdfDictionary {
        PdfInfo() {
        }

        PdfInfo(String author, String title, String subject) {
            this();
            this.addProducer();
            this.addCreationDate();
            this.addTitle(title);
            this.addSubject(subject);
            this.addAuthor(author);
        }

        void addTitle(String title) {
            this.put(PdfName.TITLE, new PdfString(title, "UnicodeBig"));
        }

        void addSubject(String subject) {
            this.put(PdfName.SUBJECT, new PdfString(subject, "UnicodeBig"));
        }

        void addKeywords(String keywords) {
            this.put(PdfName.KEYWORDS, new PdfString(keywords, "UnicodeBig"));
        }

        void addAuthor(String author) {
            this.put(PdfName.AUTHOR, new PdfString(author, "UnicodeBig"));
        }

        void addCreator(String creator) {
            this.put(PdfName.CREATOR, new PdfString(creator, "UnicodeBig"));
        }

        void addProducer() {
            this.addProducer(Document.getVersion());
        }

        void addProducer(String producer) {
            this.put(PdfName.PRODUCER, new PdfString(producer));
        }

        void addCreationDate() {
            PdfDate date = new PdfDate();
            this.put(PdfName.CREATIONDATE, date);
            this.put(PdfName.MODDATE, date);
        }

        void addkey(String key, String value) {
            if (key.equals("Producer") || key.equals("CreationDate")) {
                return;
            }
            this.put(new PdfName(key), new PdfString(value, "UnicodeBig"));
        }
    }
}

