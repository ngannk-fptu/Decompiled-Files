/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.lowagie.text.html;

import com.lowagie.text.Anchor;
import com.lowagie.text.Annotation;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Header;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.MarkedObject;
import com.lowagie.text.MarkedSection;
import com.lowagie.text.Meta;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Row;
import com.lowagie.text.Section;
import com.lowagie.text.SimpleTable;
import com.lowagie.text.Table;
import com.lowagie.text.TableRectangle;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.html.HtmlEncoder;
import com.lowagie.text.html.HtmlTags;
import com.lowagie.text.pdf.BaseFont;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import javax.annotation.Nullable;

public class HtmlWriter
extends DocWriter {
    public static final byte[] BEGINCOMMENT = HtmlWriter.getISOBytes("<!-- ");
    public static final byte[] ENDCOMMENT = HtmlWriter.getISOBytes(" -->");
    public static final String NBSP = "&nbsp;";
    protected Stack<Font> currentfont = new Stack();
    protected Font standardfont = new Font();
    protected String imagepath = null;
    protected int pageN = 0;
    protected HeaderFooter header = null;
    protected HeaderFooter footer = null;
    protected Properties markup = new Properties();

    protected HtmlWriter(Document doc, OutputStream os) {
        super(doc, os);
        this.document.addDocListener(this);
        this.pageN = this.document.getPageNumber();
        try {
            os.write(60);
            os.write(HtmlWriter.getISOBytes("html"));
            os.write(62);
            os.write(10);
            os.write(9);
            os.write(60);
            os.write(HtmlWriter.getISOBytes("head"));
            os.write(62);
        }
        catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }

    public static HtmlWriter getInstance(Document document, OutputStream os) {
        return new HtmlWriter(document, os);
    }

    @Override
    public boolean newPage() {
        try {
            this.writeStart("div");
            this.write(" ");
            this.write("style");
            this.write("=\"");
            this.writeCssProperty("page-break-before", "always");
            this.write("\" /");
            this.os.write(62);
        }
        catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
        return true;
    }

    @Override
    public boolean add(Element element) throws DocumentException {
        if (this.pause) {
            return false;
        }
        if (this.open && !element.isContent()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("the.document.is.open.you.can.only.add.elements.with.content"));
        }
        try {
            switch (element.type()) {
                case 0: {
                    try {
                        Header header = (Header)element;
                        if ("stylesheet".equals(header.getName())) {
                            this.writeLink(header);
                        } else if ("JavaScript".equals(header.getName())) {
                            this.writeJavaScript(header);
                        } else {
                            this.writeHeader(header);
                        }
                    }
                    catch (ClassCastException header) {
                        // empty catch block
                    }
                    return true;
                }
                case 2: 
                case 3: 
                case 4: {
                    Meta meta = (Meta)element;
                    this.writeHeader(meta);
                    return true;
                }
                case 1: {
                    this.addTabs(2);
                    this.writeStart("title");
                    this.os.write(62);
                    this.addTabs(3);
                    this.write(HtmlEncoder.encode(((Meta)element).getContent()));
                    this.addTabs(2);
                    this.writeEnd("title");
                    return true;
                }
                case 7: {
                    this.writeComment("Creator: " + HtmlEncoder.encode(((Meta)element).getContent()));
                    return true;
                }
                case 5: {
                    this.writeComment("Producer: " + HtmlEncoder.encode(((Meta)element).getContent()));
                    return true;
                }
                case 6: {
                    this.writeComment("Creationdate: " + HtmlEncoder.encode(((Meta)element).getContent()));
                    return true;
                }
                case 50: {
                    if (element instanceof MarkedSection) {
                        MarkedSection ms = (MarkedSection)element;
                        this.addTabs(1);
                        this.writeStart("div");
                        this.writeMarkupAttributes(ms.getMarkupAttributes());
                        this.os.write(62);
                        MarkedObject mo = ((MarkedSection)element).getTitle();
                        if (mo != null) {
                            this.markup = mo.getMarkupAttributes();
                            mo.process(this);
                        }
                        ms.process(this);
                        this.writeEnd("div");
                        return true;
                    }
                    MarkedObject mo = (MarkedObject)element;
                    this.markup = mo.getMarkupAttributes();
                    return mo.process(this);
                }
            }
            this.write(element, 2);
            return true;
        }
        catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }

    @Override
    public void open() {
        super.open();
        try {
            this.writeComment(Document.getVersion());
            this.writeComment("CreationDate: " + new Date().toString());
            this.addTabs(1);
            this.writeEnd("head");
            this.addTabs(1);
            this.writeStart("body");
            if (this.document.leftMargin() > 0.0f) {
                this.write("leftmargin", String.valueOf(this.document.leftMargin()));
            }
            if (this.document.rightMargin() > 0.0f) {
                this.write("rightmargin", String.valueOf(this.document.rightMargin()));
            }
            if (this.document.topMargin() > 0.0f) {
                this.write("topmargin", String.valueOf(this.document.topMargin()));
            }
            if (this.document.bottomMargin() > 0.0f) {
                this.write("bottommargin", String.valueOf(this.document.bottomMargin()));
            }
            if (this.pageSize.getBackgroundColor() != null) {
                this.write("bgcolor", HtmlEncoder.encode(this.pageSize.getBackgroundColor()));
            }
            if (this.document.getJavaScript_onLoad() != null) {
                this.write("onLoad", HtmlEncoder.encode(this.document.getJavaScript_onLoad()));
            }
            if (this.document.getJavaScript_onUnLoad() != null) {
                this.write("onUnLoad", HtmlEncoder.encode(this.document.getJavaScript_onUnLoad()));
            }
            if (this.document.getHtmlStyleClass() != null) {
                this.write("class", this.document.getHtmlStyleClass());
            }
            this.os.write(62);
            this.initHeader();
        }
        catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }

    @Override
    public void close() {
        try {
            this.initFooter();
            this.addTabs(1);
            this.writeEnd("body");
            this.os.write(10);
            this.writeEnd("html");
            super.close();
        }
        catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }

    protected void initHeader() {
        if (this.header != null) {
            try {
                this.add(this.header.paragraph());
            }
            catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }
    }

    protected void initFooter() {
        if (this.footer != null) {
            try {
                this.footer.setPageNumber(this.pageN + 1);
                this.add(this.footer.paragraph());
            }
            catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }
    }

    protected void writeHeader(Meta meta) throws IOException {
        this.addTabs(2);
        this.writeStart("meta");
        switch (meta.type()) {
            case 0: {
                this.write("name", meta.getName());
                break;
            }
            case 2: {
                this.write("name", "subject");
                break;
            }
            case 3: {
                this.write("name", "keywords");
                break;
            }
            case 4: {
                this.write("name", "author");
            }
        }
        this.write("content", HtmlEncoder.encode(meta.getContent()));
        this.writeEnd();
    }

    protected void writeLink(Header header) throws IOException {
        this.addTabs(2);
        this.writeStart("link");
        this.write("rel", header.getName());
        this.write("type", "text/css");
        this.write("href", header.getContent());
        this.writeEnd();
    }

    protected void writeJavaScript(Header header) throws IOException {
        this.addTabs(2);
        this.writeStart("script");
        this.write("language", "JavaScript");
        if (this.markup.size() > 0) {
            this.writeMarkupAttributes(this.markup);
            this.os.write(62);
            this.writeEnd("script");
        } else {
            this.write("type", "text/javascript");
            this.os.write(62);
            this.addTabs(2);
            this.write(new String(BEGINCOMMENT) + "\n");
            this.write(header.getContent());
            this.addTabs(2);
            this.write("//" + new String(ENDCOMMENT));
            this.addTabs(2);
            this.writeEnd("script");
        }
    }

    protected void writeComment(String comment) throws IOException {
        this.addTabs(2);
        this.os.write(BEGINCOMMENT);
        this.write(comment);
        this.os.write(ENDCOMMENT);
    }

    public void setStandardFont(Font standardfont) {
        this.standardfont = standardfont;
    }

    public boolean isOtherFont(@Nullable Font font) {
        try {
            Font cFont = this.currentfont.peek();
            return cFont.compareTo(font) != 0;
        }
        catch (EmptyStackException ese) {
            return this.standardfont.compareTo(font) != 0;
        }
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public void resetImagepath() {
        this.imagepath = null;
    }

    @Override
    public void setHeader(HeaderFooter header) {
        this.header = header;
    }

    @Override
    public void setFooter(HeaderFooter footer) {
        this.footer = footer;
    }

    public boolean add(String string) {
        if (this.pause) {
            return false;
        }
        try {
            this.write(string);
            return true;
        }
        catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }

    protected void write(Element element, int indent) throws IOException {
        switch (element.type()) {
            case 50: {
                try {
                    this.add(element);
                }
                catch (DocumentException e) {
                    e.printStackTrace();
                }
                return;
            }
            case 10: {
                boolean tag;
                Chunk chunk = (Chunk)element;
                Image image = chunk.getImage();
                if (image != null) {
                    this.write(image, indent);
                    return;
                }
                if (chunk.isEmpty()) {
                    return;
                }
                Map<String, Object> attributes = chunk.getChunkAttributes();
                if (attributes != null && attributes.get("NEWPAGE") != null) {
                    return;
                }
                boolean bl = tag = this.isOtherFont(chunk.getFont()) || this.markup.size() > 0;
                if (tag) {
                    this.addTabs(indent);
                    this.writeStart("span");
                    if (this.isOtherFont(chunk.getFont())) {
                        this.write(chunk.getFont(), null);
                    }
                    this.writeMarkupAttributes(this.markup);
                    this.os.write(62);
                }
                if (attributes != null && attributes.get("SUBSUPSCRIPT") != null) {
                    if (((Float)attributes.get("SUBSUPSCRIPT")).floatValue() > 0.0f) {
                        this.writeStart("sup");
                    } else {
                        this.writeStart("sub");
                    }
                    this.os.write(62);
                }
                this.write(HtmlEncoder.encode(chunk.getContent()));
                if (attributes != null && attributes.get("SUBSUPSCRIPT") != null) {
                    this.os.write(60);
                    this.os.write(47);
                    if (((Float)attributes.get("SUBSUPSCRIPT")).floatValue() > 0.0f) {
                        this.write("sup");
                    } else {
                        this.write("sub");
                    }
                    this.os.write(62);
                }
                if (tag) {
                    this.writeEnd("span");
                }
                return;
            }
            case 11: {
                Phrase phrase = (Phrase)element;
                Properties styleAttributes = new Properties();
                if (phrase.hasLeading()) {
                    styleAttributes.setProperty("line-height", phrase.getLeading() + "pt");
                }
                this.addTabs(indent);
                this.writeStart("span");
                this.writeMarkupAttributes(this.markup);
                this.write(phrase.getFont(), styleAttributes);
                this.os.write(62);
                this.currentfont.push(phrase.getFont());
                for (Object o : phrase) {
                    this.write((Element)o, indent + 1);
                }
                this.addTabs(indent);
                this.writeEnd("span");
                this.currentfont.pop();
                return;
            }
            case 17: {
                Anchor anchor = (Anchor)element;
                Properties styleAttributes = new Properties();
                if (anchor.hasLeading()) {
                    styleAttributes.setProperty("line-height", anchor.getLeading() + "pt");
                }
                this.addTabs(indent);
                this.writeStart("a");
                if (anchor.getName() != null) {
                    this.write("name", anchor.getName());
                }
                if (anchor.getReference() != null) {
                    this.write("href", anchor.getReference());
                }
                this.writeMarkupAttributes(this.markup);
                this.write(anchor.getFont(), styleAttributes);
                this.os.write(62);
                this.currentfont.push(anchor.getFont());
                for (Object o : anchor) {
                    this.write((Element)o, indent + 1);
                }
                this.addTabs(indent);
                this.writeEnd("a");
                this.currentfont.pop();
                return;
            }
            case 12: {
                Paragraph paragraph = (Paragraph)element;
                Properties styleAttributes = new Properties();
                if (paragraph.hasLeading()) {
                    styleAttributes.setProperty("line-height", paragraph.getTotalLeading() + "pt");
                }
                this.addTabs(indent);
                this.writeStart("div");
                this.writeMarkupAttributes(this.markup);
                String alignment = HtmlEncoder.getAlignment(paragraph.getAlignment());
                if (!"".equals(alignment)) {
                    this.write("align", alignment);
                }
                this.write(paragraph.getFont(), styleAttributes);
                this.os.write(62);
                this.currentfont.push(paragraph.getFont());
                for (Object o : paragraph) {
                    this.write((Element)o, indent + 1);
                }
                this.addTabs(indent);
                this.writeEnd("div");
                this.currentfont.pop();
                return;
            }
            case 13: 
            case 16: {
                this.writeSection((Section)element, indent);
                return;
            }
            case 14: {
                List list = (List)element;
                this.addTabs(indent);
                if (list.isNumbered()) {
                    this.writeStart("ol");
                } else {
                    this.writeStart("ul");
                }
                this.writeMarkupAttributes(this.markup);
                this.os.write(62);
                for (Element o : list.getItems()) {
                    this.write(o, indent + 1);
                }
                this.addTabs(indent);
                if (list.isNumbered()) {
                    this.writeEnd("ol");
                } else {
                    this.writeEnd("ul");
                }
                return;
            }
            case 15: {
                ListItem listItem = (ListItem)element;
                Properties styleAttributes = new Properties();
                if (listItem.hasLeading()) {
                    styleAttributes.setProperty("line-height", listItem.getTotalLeading() + "pt");
                }
                this.addTabs(indent);
                this.writeStart("li");
                this.writeMarkupAttributes(this.markup);
                this.write(listItem.getFont(), styleAttributes);
                this.os.write(62);
                this.currentfont.push(listItem.getFont());
                for (Object o : listItem) {
                    this.write((Element)o, indent + 1);
                }
                this.addTabs(indent);
                this.writeEnd("li");
                this.currentfont.pop();
                return;
            }
            case 20: {
                String alignment;
                Cell cell = (Cell)element;
                this.addTabs(indent);
                if (cell.isHeader()) {
                    this.writeStart("th");
                } else {
                    this.writeStart("td");
                }
                this.writeMarkupAttributes(this.markup);
                if (cell.getBorderWidth() != -1.0f) {
                    this.write("border", String.valueOf(cell.getBorderWidth()));
                }
                if (cell.getBorderColor() != null) {
                    this.write("bordercolor", HtmlEncoder.encode(cell.getBorderColor()));
                }
                if (cell.getBackgroundColor() != null) {
                    this.write("bgcolor", HtmlEncoder.encode(cell.getBackgroundColor()));
                }
                if (!"".equals(alignment = HtmlEncoder.getAlignment(cell.getHorizontalAlignment()))) {
                    this.write("align", alignment);
                }
                if (!"".equals(alignment = HtmlEncoder.getAlignment(cell.getVerticalAlignment()))) {
                    this.write("valign", alignment);
                }
                if (cell.getWidthAsString() != null) {
                    this.write("width", cell.getWidthAsString());
                }
                if (cell.getColspan() != 1) {
                    this.write("colspan", String.valueOf(cell.getColspan()));
                }
                if (cell.getRowspan() != 1) {
                    this.write("rowspan", String.valueOf(cell.getRowspan()));
                }
                if (cell.getMaxLines() == 1) {
                    this.write("style", "white-space: nowrap;");
                }
                this.os.write(62);
                if (cell.isEmpty()) {
                    this.write(NBSP);
                } else {
                    Iterator i = cell.getElements();
                    while (i.hasNext()) {
                        this.write((Element)i.next(), indent + 1);
                    }
                }
                this.addTabs(indent);
                if (cell.isHeader()) {
                    this.writeEnd("th");
                } else {
                    this.writeEnd("td");
                }
                return;
            }
            case 21: {
                Row row = (Row)element;
                this.addTabs(indent);
                this.writeStart("tr");
                this.writeMarkupAttributes(this.markup);
                this.os.write(62);
                for (int i = 0; i < row.getColumns(); ++i) {
                    TableRectangle cell = row.getCell(i);
                    if (cell == null) continue;
                    this.write(cell, indent + 1);
                }
                this.addTabs(indent);
                this.writeEnd("tr");
                return;
            }
            case 22: {
                Table table;
                try {
                    table = (Table)element;
                }
                catch (ClassCastException cce) {
                    try {
                        table = ((SimpleTable)element).createTable();
                    }
                    catch (BadElementException e) {
                        throw new ExceptionConverter(e);
                    }
                }
                table.complete();
                this.addTabs(indent);
                this.writeStart("table");
                this.writeMarkupAttributes(this.markup);
                this.os.write(32);
                this.write("width");
                this.os.write(61);
                this.os.write(34);
                this.write(String.valueOf(table.getWidth()));
                if (!table.isLocked()) {
                    this.write("%");
                }
                this.os.write(34);
                String alignment = HtmlEncoder.getAlignment(table.getAlignment());
                if (!"".equals(alignment)) {
                    this.write("align", alignment);
                }
                this.write("cellpadding", String.valueOf(table.getPadding()));
                this.write("cellspacing", String.valueOf(table.getSpacing()));
                if (table.getBorderWidth() != -1.0f) {
                    this.write("border", String.valueOf(table.getBorderWidth()));
                }
                if (table.getBorderColor() != null) {
                    this.write("bordercolor", HtmlEncoder.encode(table.getBorderColor()));
                }
                if (table.getBackgroundColor() != null) {
                    this.write("bgcolor", HtmlEncoder.encode(table.getBackgroundColor()));
                }
                this.os.write(62);
                Iterator iterator = table.iterator();
                while (iterator.hasNext()) {
                    Row row = (Row)iterator.next();
                    this.write(row, indent + 1);
                }
                this.addTabs(indent);
                this.writeEnd("table");
                return;
            }
            case 29: {
                Annotation annotation = (Annotation)element;
                this.writeComment(annotation.title() + ": " + annotation.content());
                return;
            }
            case 32: 
            case 33: 
            case 34: 
            case 35: {
                Image image = (Image)element;
                if (image.getUrl() == null) {
                    return;
                }
                this.addTabs(indent);
                this.writeStart("img");
                String path = image.getUrl().toString();
                if (this.imagepath != null) {
                    path = path.indexOf(47) > 0 ? this.imagepath + path.substring(path.lastIndexOf(47) + 1) : this.imagepath + path;
                }
                this.write("src", path);
                if ((image.getAlignment() & 2) > 0) {
                    this.write("align", "Right");
                } else if ((image.getAlignment() & 1) > 0) {
                    this.write("align", "Middle");
                } else {
                    this.write("align", "Left");
                }
                if (image.getAlt() != null) {
                    this.write("alt", image.getAlt());
                }
                this.write("width", String.valueOf(image.getScaledWidth()));
                this.write("height", String.valueOf(image.getScaledHeight()));
                this.writeMarkupAttributes(this.markup);
                this.writeEnd();
                return;
            }
        }
    }

    protected void writeSection(Section section, int indent) throws IOException {
        if (section.getTitle() != null) {
            int depth = section.getDepth() - 1;
            if (depth > 5) {
                depth = 5;
            }
            Properties styleAttributes = new Properties();
            if (section.getTitle().hasLeading()) {
                styleAttributes.setProperty("line-height", section.getTitle().getTotalLeading() + "pt");
            }
            this.addTabs(indent);
            this.writeStart(HtmlTags.H[depth]);
            this.write(section.getTitle().getFont(), styleAttributes);
            String alignment = HtmlEncoder.getAlignment(section.getTitle().getAlignment());
            if (!"".equals(alignment)) {
                this.write("align", alignment);
            }
            this.writeMarkupAttributes(this.markup);
            this.os.write(62);
            this.currentfont.push(section.getTitle().getFont());
            for (Object o : section.getTitle()) {
                this.write((Element)o, indent + 1);
            }
            this.addTabs(indent);
            this.writeEnd(HtmlTags.H[depth]);
            this.currentfont.pop();
        }
        for (Object o : section) {
            this.write((Element)o, indent);
        }
    }

    protected void write(@Nullable Font font, @Nullable Properties styleAttributes) throws IOException {
        if (font == null || !this.isOtherFont(font)) {
            return;
        }
        this.write(" ");
        this.write("style");
        this.write("=\"");
        if (styleAttributes != null) {
            Enumeration<?> e = styleAttributes.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String)e.nextElement();
                this.writeCssProperty(key, styleAttributes.getProperty(key));
            }
        }
        if (this.isOtherFont(font)) {
            this.writeCssProperty("font-family", font.getFamilyname());
            if (font.getSize() != -1.0f) {
                this.writeCssProperty("font-size", font.getSize() + "pt");
            }
            if (font.getColor() != null) {
                this.writeCssProperty("color", HtmlEncoder.encode(font.getColor()));
            }
            int fontstyle = font.getStyle();
            BaseFont bf = font.getBaseFont();
            if (bf != null) {
                String ps = bf.getPostscriptFontName().toLowerCase();
                if (ps.contains("bold")) {
                    if (fontstyle == -1) {
                        fontstyle = 0;
                    }
                    fontstyle |= 1;
                }
                if (ps.contains("italic") || ps.contains("oblique")) {
                    if (fontstyle == -1) {
                        fontstyle = 0;
                    }
                    fontstyle |= 2;
                }
            }
            if (fontstyle != -1 && fontstyle != 0) {
                switch (fontstyle & 3) {
                    case 1: {
                        this.writeCssProperty("font-weight", "bold");
                        break;
                    }
                    case 2: {
                        this.writeCssProperty("font-style", "italic");
                        break;
                    }
                    case 3: {
                        this.writeCssProperty("font-weight", "bold");
                        this.writeCssProperty("font-style", "italic");
                    }
                }
                if ((fontstyle & 4) > 0) {
                    this.writeCssProperty("text-decoration", "underline");
                }
                if ((fontstyle & 8) > 0) {
                    this.writeCssProperty("text-decoration", "line-through");
                }
            }
        }
        this.write("\"");
    }

    protected void writeCssProperty(String prop, String value) throws IOException {
        this.write(prop + ": " + value + "; ");
    }
}

