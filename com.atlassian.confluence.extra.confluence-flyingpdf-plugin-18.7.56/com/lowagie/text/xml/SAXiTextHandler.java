/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.lowagie.text.xml;

import com.lowagie.text.Anchor;
import com.lowagie.text.Annotation;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocListener;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Meta;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Section;
import com.lowagie.text.Table;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.factories.ElementFactory;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.lowagie.text.xml.XmlPeer;
import com.lowagie.text.xml.simpleparser.EntitiesToSymbol;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import javax.annotation.Nullable;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SAXiTextHandler<T extends XmlPeer>
extends DefaultHandler {
    protected DocListener document;
    protected Stack<Element> stack;
    protected int chapters = 0;
    protected Chunk currentChunk = null;
    protected boolean ignore = false;
    private boolean controlOpenClose = true;
    protected Map<String, T> myTags;
    private float topMargin = 36.0f;
    private float rightMargin = 36.0f;
    private float leftMargin = 36.0f;
    private float bottomMargin = 36.0f;
    private BaseFont bf = null;

    public SAXiTextHandler(DocListener document) {
        this.document = document;
        this.stack = new Stack();
    }

    public SAXiTextHandler(DocListener document, Map<String, T> myTags, BaseFont bf) {
        this(document, myTags);
        this.bf = bf;
    }

    public SAXiTextHandler(DocListener document, Map<String, T> myTags) {
        this(document);
        this.myTags = myTags;
    }

    public void setControlOpenClose(boolean controlOpenClose) {
        this.controlOpenClose = controlOpenClose;
    }

    @Override
    public void startElement(String uri, String localName, String name, @Nullable Attributes attributes) {
        Properties properties = new Properties();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); ++i) {
                String attribute = attributes.getQName(i);
                properties.setProperty(attribute, attributes.getValue(i));
            }
        }
        this.handleStartingTags(name, properties);
    }

    public void handleStartingTags(String name, Properties attributes) {
        TextElementArray current;
        if (this.ignore || "ignore".equals(name)) {
            this.ignore = true;
            return;
        }
        if (this.currentChunk != null && this.isNotBlank(this.currentChunk.getContent())) {
            try {
                current = (TextElementArray)this.stack.pop();
            }
            catch (EmptyStackException ese) {
                current = this.bf == null ? new Paragraph("", new Font()) : new Paragraph("", new Font(this.bf));
            }
            current.add(this.currentChunk);
            this.stack.push(current);
            this.currentChunk = null;
        }
        if ("chunk".equals(name)) {
            this.currentChunk = ElementFactory.getChunk(attributes);
            if (this.bf != null) {
                this.currentChunk.setFont(new Font(this.bf));
            }
            return;
        }
        if ("entity".equals(name)) {
            Font f = new Font();
            if (this.currentChunk != null) {
                this.handleEndingTags("chunk");
                f = this.currentChunk.getFont();
            }
            this.currentChunk = EntitiesToSymbol.get(attributes.getProperty("id"), f);
            return;
        }
        if ("phrase".equals(name)) {
            this.stack.push(ElementFactory.getPhrase(attributes));
            return;
        }
        if ("anchor".equals(name)) {
            this.stack.push(ElementFactory.getAnchor(attributes));
            return;
        }
        if ("paragraph".equals(name) || "title".equals(name)) {
            this.stack.push(ElementFactory.getParagraph(attributes));
            return;
        }
        if ("list".equals(name)) {
            this.stack.push(ElementFactory.getList(attributes));
            return;
        }
        if ("listitem".equals(name)) {
            this.stack.push(ElementFactory.getListItem(attributes));
            return;
        }
        if ("cell".equals(name)) {
            this.stack.push(ElementFactory.getCell(attributes));
            return;
        }
        if ("table".equals(name)) {
            Table table = ElementFactory.getTable(attributes);
            float[] widths = table.getProportionalWidths();
            for (int i = 0; i < widths.length; ++i) {
                if (widths[i] != 0.0f) continue;
                widths[i] = 100.0f / (float)widths.length;
            }
            try {
                table.setWidths(widths);
            }
            catch (BadElementException bee) {
                throw new ExceptionConverter(bee);
            }
            this.stack.push(table);
            return;
        }
        if ("section".equals(name)) {
            Section section;
            Element previous = this.stack.pop();
            try {
                section = ElementFactory.getSection((Section)previous, attributes);
            }
            catch (ClassCastException cce) {
                throw new ExceptionConverter(cce);
            }
            this.stack.push(previous);
            this.stack.push(section);
            return;
        }
        if ("chapter".equals(name)) {
            this.stack.push(ElementFactory.getChapter(attributes));
            return;
        }
        if ("image".equals(name)) {
            try {
                Image img = ElementFactory.getImage(attributes);
                try {
                    this.addImage(img);
                    return;
                }
                catch (EmptyStackException ese) {
                    try {
                        this.document.add(img);
                    }
                    catch (DocumentException de) {
                        throw new ExceptionConverter(de);
                    }
                    return;
                }
            }
            catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }
        if ("annotation".equals(name)) {
            Annotation annotation = ElementFactory.getAnnotation(attributes);
            try {
                try {
                    TextElementArray current2 = (TextElementArray)this.stack.pop();
                    try {
                        current2.add(annotation);
                    }
                    catch (Exception e) {
                        this.document.add(annotation);
                    }
                    this.stack.push(current2);
                }
                catch (EmptyStackException ese) {
                    this.document.add(annotation);
                }
                return;
            }
            catch (DocumentException de) {
                throw new ExceptionConverter(de);
            }
        }
        if (this.isNewline(name)) {
            try {
                current = (TextElementArray)this.stack.pop();
                current.add(Chunk.NEWLINE);
                this.stack.push(current);
            }
            catch (EmptyStackException ese) {
                if (this.currentChunk == null) {
                    try {
                        this.document.add(Chunk.NEWLINE);
                    }
                    catch (DocumentException de) {
                        throw new ExceptionConverter(de);
                    }
                }
                this.currentChunk.append("\n");
            }
            return;
        }
        if (this.isNewpage(name)) {
            try {
                current = (TextElementArray)this.stack.pop();
                Chunk newPage = new Chunk("");
                newPage.setNewPage();
                if (this.bf != null) {
                    newPage.setFont(new Font(this.bf));
                }
                current.add(newPage);
                this.stack.push(current);
            }
            catch (EmptyStackException ese) {
                this.document.newPage();
            }
            return;
        }
        if ("horizontalrule".equals(name)) {
            LineSeparator hr = new LineSeparator(1.0f, 100.0f, null, 1, 0.0f);
            try {
                current = (TextElementArray)this.stack.pop();
                current.add(hr);
                this.stack.push(current);
            }
            catch (EmptyStackException ese) {
                try {
                    this.document.add(hr);
                }
                catch (DocumentException de) {
                    throw new ExceptionConverter(de);
                }
            }
            return;
        }
        if (this.isDocumentRoot(name)) {
            Rectangle pageSize = null;
            String orientation = null;
            for (Object o : attributes.keySet()) {
                String key = (String)o;
                String value = attributes.getProperty(key);
                try {
                    if ("left".equalsIgnoreCase(key)) {
                        this.leftMargin = Float.parseFloat(value + "f");
                    }
                    if ("right".equalsIgnoreCase(key)) {
                        this.rightMargin = Float.parseFloat(value + "f");
                    }
                    if ("top".equalsIgnoreCase(key)) {
                        this.topMargin = Float.parseFloat(value + "f");
                    }
                    if ("bottom".equalsIgnoreCase(key)) {
                        this.bottomMargin = Float.parseFloat(value + "f");
                    }
                }
                catch (Exception ex) {
                    throw new ExceptionConverter(ex);
                }
                if ("pagesize".equals(key)) {
                    try {
                        Field pageSizeField = PageSize.class.getField(value);
                        pageSize = (Rectangle)pageSizeField.get(null);
                        continue;
                    }
                    catch (Exception ex) {
                        throw new ExceptionConverter(ex);
                    }
                }
                if ("orientation".equals(key)) {
                    try {
                        if (!"landscape".equals(value)) continue;
                        orientation = "landscape";
                        continue;
                    }
                    catch (Exception ex) {
                        throw new ExceptionConverter(ex);
                    }
                }
                try {
                    this.document.add(new Meta(key, value));
                }
                catch (DocumentException de) {
                    throw new ExceptionConverter(de);
                }
            }
            if (pageSize != null) {
                if ("landscape".equals(orientation)) {
                    pageSize = pageSize.rotate();
                }
                this.document.setPageSize(pageSize);
            }
            this.document.setMargins(this.leftMargin, this.rightMargin, this.topMargin, this.bottomMargin);
            if (this.controlOpenClose) {
                this.document.open();
            }
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) {
        this.characters(ch, start, length);
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (this.ignore) {
            return;
        }
        String content = new String(ch, start, length);
        if (content.trim().isEmpty() && content.indexOf(32) < 0) {
            return;
        }
        StringBuilder buf = new StringBuilder();
        int len = content.length();
        boolean newline = false;
        block6: for (int i = 0; i < len; ++i) {
            char character = content.charAt(i);
            switch (character) {
                case ' ': {
                    if (newline) continue block6;
                    buf.append(character);
                    continue block6;
                }
                case '\n': {
                    if (i <= 0) continue block6;
                    newline = true;
                    buf.append(' ');
                    continue block6;
                }
                case '\r': {
                    continue block6;
                }
                case '\t': {
                    continue block6;
                }
                default: {
                    newline = false;
                    buf.append(character);
                }
            }
        }
        if (this.currentChunk == null) {
            this.currentChunk = this.bf == null ? new Chunk(buf.toString()) : new Chunk(buf.toString(), new Font(this.bf));
        } else {
            this.currentChunk.append(buf.toString());
        }
    }

    public void setBaseFont(BaseFont bf) {
        this.bf = bf;
    }

    @Override
    public void endElement(String uri, String lname, String name) {
        this.handleEndingTags(name);
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public void handleEndingTags(String name) {
        block47: {
            if ("ignore".equals(name)) {
                this.ignore = false;
                return;
            }
            if (this.ignore) {
                return;
            }
            if (this.isNewpage(name) || "annotation".equals(name) || "image".equals(name) || this.isNewline(name)) {
                return;
            }
            try {
                if ("title".equals(name)) {
                    current = (Paragraph)this.stack.pop();
                    if (this.currentChunk != null) {
                        current.add(this.currentChunk);
                        this.currentChunk = null;
                    }
                    previous = (Section)this.stack.pop();
                    previous.setTitle(current);
                    this.stack.push(previous);
                    return;
                }
                if (this.currentChunk != null) {
                    try {
                        current /* !! */  = (TextElementArray)this.stack.pop();
                    }
                    catch (EmptyStackException ese) {
                        current /* !! */  = new Paragraph();
                    }
                    current /* !! */ .add(this.currentChunk);
                    this.stack.push(current /* !! */ );
                    this.currentChunk = null;
                }
                if ("chunk".equals(name)) {
                    return;
                }
                if ("phrase".equals(name) || "anchor".equals(name) || "list".equals(name) || "paragraph".equals(name)) {
                    current /* !! */  = this.stack.pop();
                    try {
                        previous = (TextElementArray)this.stack.pop();
                        previous.add(current /* !! */ );
                        this.stack.push(previous);
                    }
                    catch (EmptyStackException ese) {
                        this.document.add(current /* !! */ );
                    }
                    return;
                }
                if ("listitem".equals(name)) {
                    listItem = (ListItem)this.stack.pop();
                    list = (List)this.stack.pop();
                    list.add(listItem);
                    this.stack.push(list);
                }
                if ("table".equals(name)) {
                    table = (Table)this.stack.pop();
                    try {
                        previous = (TextElementArray)this.stack.pop();
                        previous.add(table);
                        this.stack.push(previous);
                    }
                    catch (EmptyStackException ese) {
                        this.document.add(table);
                    }
                    return;
                }
                if ("row".equals(name)) {
                    cells = new ArrayList<Cell>();
                    columns = 0;
                    while ((element = this.stack.pop()).type() == 20) {
                        cell = (Cell)element;
                        columns += cell.getColspan();
                        cells.add(cell);
                    }
                    table = (Table)element;
                    if (table.getColumns() < columns) {
                        table.addColumns(columns - table.getColumns());
                    }
                    Collections.reverse(cells);
                    cellWidths = new float[columns];
                    cellNulls = new boolean[columns];
                    for (i = 0; i < columns; ++i) {
                        cellWidths[i] = 0.0f;
                        cellNulls[i] = true;
                    }
                    total = 0.0f;
                    j = 0;
                    var11_23 = cells.iterator();
                    while (var11_23.hasNext()) {
                        cell = value = (Cell)var11_23.next();
                        width = cell.getWidthAsString();
                        if (cell.getWidth() == 0.0f) {
                            if (cell.getColspan() == 1 && cellWidths[j] == 0.0f) {
                                try {
                                    cellWidths[j] = 100.0f / (float)columns;
                                    total += cellWidths[j];
                                }
                                catch (Exception var13_27) {}
                            } else if (cell.getColspan() == 1) {
                                cellNulls[j] = false;
                            }
                        } else if (cell.getColspan() == 1 && width.endsWith("%")) {
                            try {
                                cellWidths[j] = Float.parseFloat(width.substring(0, width.length() - 1) + "f");
                                total += cellWidths[j];
                                cellNulls[j] = false;
                            }
                            catch (Exception var13_28) {
                                // empty catch block
                            }
                        }
                        j += cell.getColspan();
                        table.addCell(cell);
                    }
                    widths = table.getProportionalWidths();
                    if (widths.length == columns) {
                        left = 0.0f;
                        for (i = 0; i < columns; ++i) {
                            if (!cellNulls[i] || widths[i] == 0.0f) continue;
                            left += widths[i];
                            cellWidths[i] = widths[i];
                        }
                        if (100.0 >= (double)total) {
                            for (i = 0; i < widths.length; ++i) {
                                if (cellWidths[i] != 0.0f || widths[i] == 0.0f) continue;
                                cellWidths[i] = widths[i] / left * (100.0f - total);
                            }
                        }
                        table.setWidths(cellWidths);
                    }
                    this.stack.push(table);
                }
                if ("cell".equals(name)) {
                    return;
                }
                if ("section".equals(name)) {
                    this.stack.pop();
                    return;
                }
                if ("chapter".equals(name)) {
                    this.document.add(this.stack.pop());
                    return;
                }
                if (!this.isDocumentRoot(name)) break block47;
                block21: while (true) lbl-1000:
                // 2 sources

                {
                    try {
                        while (true) {
                            element = this.stack.pop();
                            try {
                                previous = (TextElementArray)this.stack.pop();
                                previous.add(element);
                                this.stack.push(previous);
                                continue block21;
                            }
                            catch (EmptyStackException es) {
                                this.document.add(element);
                                continue;
                            }
                            break;
                        }
                    }
                    catch (EmptyStackException element) {
                        if (this.controlOpenClose) {
                            this.document.close();
                        }
                    }
                    break;
                }
            }
            catch (DocumentException de) {
                throw new ExceptionConverter(de);
            }
            {
                ** while (true)
            }
        }
    }

    private boolean isNotBlank(String text) {
        return text != null && !text.trim().isEmpty();
    }

    protected void addImage(Image img) throws EmptyStackException {
        Element current = this.stack.pop();
        if (current instanceof Section || current instanceof Cell) {
            ((TextElementArray)current).add(img);
            this.stack.push(current);
        } else {
            Stack<Element> newStack = new Stack<Element>();
            while (!(current instanceof Section) && !(current instanceof Cell)) {
                newStack.push(current);
                if (current instanceof Anchor) {
                    img.setAnnotation(new Annotation(0.0f, 0.0f, 0.0f, 0.0f, ((Anchor)current).getReference()));
                }
                current = this.stack.pop();
            }
            ((TextElementArray)current).add(img);
            this.stack.push(current);
            while (!newStack.empty()) {
                this.stack.push((Element)newStack.pop());
            }
        }
    }

    private boolean isNewpage(String tag) {
        return "newpage".equals(tag);
    }

    private boolean isNewline(String tag) {
        return "newline".equals(tag);
    }

    protected boolean isDocumentRoot(String tag) {
        return "itext".equals(tag);
    }
}

