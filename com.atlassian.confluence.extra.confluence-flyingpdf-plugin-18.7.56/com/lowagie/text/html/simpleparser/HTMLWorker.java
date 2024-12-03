/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.lowagie.text.html.simpleparser;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocListener;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.FontProvider;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.html.Markup;
import com.lowagie.text.html.simpleparser.ALink;
import com.lowagie.text.html.simpleparser.ChainedProperties;
import com.lowagie.text.html.simpleparser.FactoryProperties;
import com.lowagie.text.html.simpleparser.ImageProvider;
import com.lowagie.text.html.simpleparser.Img;
import com.lowagie.text.html.simpleparser.IncCell;
import com.lowagie.text.html.simpleparser.IncTable;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.lowagie.text.utils.NumberUtilities;
import com.lowagie.text.xml.simpleparser.SimpleXMLDocHandler;
import com.lowagie.text.xml.simpleparser.SimpleXMLParser;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import javax.annotation.Nullable;

public class HTMLWorker
implements SimpleXMLDocHandler,
DocListener {
    public static final String tagsSupportedString = "ol ul li a pre font span br p div body table td th tr i b u sub sup em strong s strike h1 h2 h3 h4 h5 h6 img hr";
    public static final Map<String, Object> tagsSupported = new HashMap<String, Object>();
    protected ArrayList<Element> objectList;
    protected DocListener document;
    private Paragraph currentParagraph;
    private ChainedProperties cprops = new ChainedProperties();
    private Stack<Object> stack = new Stack();
    private boolean pendingTR = false;
    private boolean pendingTD = false;
    private boolean pendingLI = false;
    private StyleSheet style = new StyleSheet();
    private boolean isPRE = false;
    private Stack<Object> tableState = new Stack();
    private boolean skipText = false;
    private Map<String, Object> interfaceProps;
    private FactoryProperties factoryProperties = new FactoryProperties();

    public HTMLWorker(DocListener document) {
        this.document = document;
    }

    public static ArrayList<Element> parseToList(Reader reader, StyleSheet style) throws IOException {
        Map<String, Object> interfaceProps = null;
        return HTMLWorker.parseToList(reader, style, interfaceProps);
    }

    @Deprecated
    public static ArrayList<Element> parseToList(Reader reader, @Nullable StyleSheet style, HashMap interfaceProps) throws IOException {
        return HTMLWorker.parseToList(reader, style, (Map<String, Object>)interfaceProps);
    }

    public static ArrayList<Element> parseToList(Reader reader, @Nullable StyleSheet style, Map<String, Object> interfaceProps) throws IOException {
        HTMLWorker worker = new HTMLWorker(null);
        if (style != null) {
            worker.style = style;
        }
        worker.document = worker;
        worker.setInterfaceProps(interfaceProps);
        worker.objectList = new ArrayList();
        worker.parse(reader);
        return worker.objectList;
    }

    public StyleSheet getStyleSheet() {
        return this.style;
    }

    public void setStyleSheet(StyleSheet style) {
        this.style = style;
    }

    public Map<String, Object> getInterfaceProps() {
        return this.interfaceProps;
    }

    @Deprecated
    public void setInterfaceProps(HashMap interfaceProps) {
        this.setInterfaceProps((Map<String, Object>)interfaceProps);
    }

    public void setInterfaceProps(Map<String, Object> interfaceProps) {
        this.interfaceProps = interfaceProps;
        FontProvider ff = null;
        if (interfaceProps != null) {
            ff = (FontProvider)interfaceProps.get("font_factory");
        }
        if (ff != null) {
            this.factoryProperties.setFontImp(ff);
        }
    }

    public void parse(Reader reader) throws IOException {
        SimpleXMLParser.parse(this, null, reader, true);
    }

    @Override
    public void endDocument() {
        try {
            this.stack.forEach(o -> this.document.add((Element)o));
            if (this.currentParagraph != null) {
                this.document.add(this.currentParagraph);
            }
            this.currentParagraph = null;
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    @Override
    public void startDocument() {
        HashMap<String, String> h = new HashMap<String, String>();
        this.style.applyStyle("body", (Map<String, String>)h);
        this.cprops.addToChain("body", (Map<String, String>)h);
    }

    @Override
    @Deprecated
    public void startElement(String tag, HashMap h) {
        this.startElement(tag, (Map<String, String>)h);
    }

    @Override
    public void startElement(String tag, Map<String, String> style) {
        if (!tagsSupported.containsKey(tag)) {
            return;
        }
        try {
            this.style.applyStyle(tag, style);
            String follow = FactoryProperties.followTags.get(tag);
            if (follow != null) {
                HashMap<String, String> prop = new HashMap<String, String>();
                prop.put(follow, null);
                this.cprops.addToChain(follow, (Map<String, String>)prop);
                return;
            }
            FactoryProperties.insertStyle(style, this.cprops);
            if (tag.equals("a")) {
                this.cprops.addToChain(tag, style);
                if (this.currentParagraph == null) {
                    this.currentParagraph = new Paragraph();
                }
                this.stack.push(this.currentParagraph);
                this.currentParagraph = new Paragraph();
                return;
            }
            if (tag.equals("br")) {
                if (this.currentParagraph == null) {
                    this.currentParagraph = new Paragraph();
                }
                Chunk chunk = this.factoryProperties.createChunk("\n", this.cprops);
                this.currentParagraph.add(chunk);
                return;
            }
            if (tag.equals("hr")) {
                float tmpSize;
                int numChunks;
                boolean addLeadingBreak = true;
                if (this.currentParagraph == null) {
                    this.currentParagraph = new Paragraph();
                    addLeadingBreak = false;
                }
                if (addLeadingBreak && ((numChunks = this.currentParagraph.getChunks().size()) == 0 || ((Chunk)this.currentParagraph.getChunks().get(numChunks - 1)).getContent().endsWith("\n"))) {
                    addLeadingBreak = false;
                }
                String align = style.get("align");
                int hrAlign = 1;
                if (align != null) {
                    if (align.equalsIgnoreCase("left")) {
                        hrAlign = 0;
                    }
                    if (align.equalsIgnoreCase("right")) {
                        hrAlign = 2;
                    }
                }
                String width = style.get("width");
                float hrWidth = 1.0f;
                if (width != null) {
                    float tmpWidth = Markup.parseLength(width, 12.0f);
                    if (tmpWidth > 0.0f) {
                        hrWidth = tmpWidth;
                    }
                    if (!width.endsWith("%")) {
                        hrWidth = 100.0f;
                    }
                }
                String size = style.get("size");
                float hrSize = 1.0f;
                if (size != null && (tmpSize = Markup.parseLength(size, 12.0f)) > 0.0f) {
                    hrSize = tmpSize;
                }
                if (addLeadingBreak) {
                    this.currentParagraph.add(Chunk.NEWLINE);
                }
                this.currentParagraph.add(new LineSeparator(hrSize, hrWidth, null, hrAlign, this.currentParagraph.getLeading() / 2.0f));
                this.currentParagraph.add(Chunk.NEWLINE);
                return;
            }
            if (tag.equals("font") || tag.equals("span")) {
                this.cprops.addToChain(tag, style);
                return;
            }
            if (tag.equals("img")) {
                String src = style.get("src");
                if (src == null) {
                    return;
                }
                this.cprops.addToChain(tag, style);
                Image img = null;
                if (this.interfaceProps != null) {
                    ImageProvider ip = (ImageProvider)this.interfaceProps.get("img_provider");
                    if (ip != null) {
                        img = ip.getImage(src, (HashMap)style, this.cprops, this.document);
                    }
                    if (img == null) {
                        String baseUrl;
                        HashMap images = (HashMap)this.interfaceProps.get("img_static");
                        if (images != null) {
                            Image tim = (Image)images.get(src);
                            if (tim != null) {
                                img = Image.getInstance(tim);
                            }
                        } else if (!src.startsWith("http") && (baseUrl = (String)this.interfaceProps.get("img_baseurl")) != null) {
                            src = baseUrl + src;
                            img = Image.getInstance(src);
                        }
                    }
                }
                if (img == null) {
                    if (!src.startsWith("http")) {
                        String path = this.cprops.getOrDefault("image_path", "");
                        src = new File(path, src).getPath();
                    }
                    img = Image.getInstance(src);
                }
                String align = style.get("align");
                String width = style.get("width");
                String height = style.get("height");
                this.cprops.findProperty("before").flatMap(NumberUtilities::parseFloat).ifPresent(img::setSpacingBefore);
                this.cprops.findProperty("after").flatMap(NumberUtilities::parseFloat).ifPresent(img::setSpacingAfter);
                float actualFontSize = Markup.parseLength(this.cprops.getProperty("size"), 12.0f);
                if (actualFontSize <= 0.0f) {
                    actualFontSize = 12.0f;
                }
                float widthInPoints = Markup.parseLength(width, actualFontSize);
                float heightInPoints = Markup.parseLength(height, actualFontSize);
                if (widthInPoints > 0.0f && heightInPoints > 0.0f) {
                    img.scaleAbsolute(widthInPoints, heightInPoints);
                } else if (widthInPoints > 0.0f) {
                    heightInPoints = img.getHeight() * widthInPoints / img.getWidth();
                    img.scaleAbsolute(widthInPoints, heightInPoints);
                } else if (heightInPoints > 0.0f) {
                    widthInPoints = img.getWidth() * heightInPoints / img.getHeight();
                    img.scaleAbsolute(widthInPoints, heightInPoints);
                }
                img.setWidthPercentage(0.0f);
                if (align != null) {
                    this.endElement("p");
                    int ralign = 1;
                    if (align.equalsIgnoreCase("left")) {
                        ralign = 0;
                    } else if (align.equalsIgnoreCase("right")) {
                        ralign = 2;
                    }
                    img.setAlignment(ralign);
                    Img i = null;
                    boolean skip = false;
                    if (this.interfaceProps != null && (i = (Img)this.interfaceProps.get("img_interface")) != null) {
                        skip = i.process(img, (HashMap)style, this.cprops, this.document);
                    }
                    if (!skip) {
                        this.document.add(img);
                    }
                    this.cprops.removeChain(tag);
                } else {
                    this.cprops.removeChain(tag);
                    if (this.currentParagraph == null) {
                        this.currentParagraph = FactoryProperties.createParagraph(this.cprops);
                    }
                    this.currentParagraph.add(new Chunk(img, 0.0f, 0.0f));
                }
                return;
            }
            this.endElement("p");
            if (tag.equals("h1") || tag.equals("h2") || tag.equals("h3") || tag.equals("h4") || tag.equals("h5") || tag.equals("h6")) {
                if (!style.containsKey("size")) {
                    int v = 7 - Integer.parseInt(tag.substring(1));
                    style.put("size", Integer.toString(v));
                }
                this.cprops.addToChain(tag, style);
                return;
            }
            if (tag.equals("ul")) {
                if (this.pendingLI) {
                    this.endElement("li");
                }
                this.skipText = true;
                this.cprops.addToChain(tag, style);
                List list = new List(false);
                try {
                    list.setIndentationLeft(Float.parseFloat(this.cprops.getProperty("indent")));
                }
                catch (Exception e) {
                    list.setAutoindent(true);
                }
                list.setListSymbol("\u2022");
                this.stack.push(list);
                return;
            }
            if (tag.equals("ol")) {
                if (this.pendingLI) {
                    this.endElement("li");
                }
                this.skipText = true;
                this.cprops.addToChain(tag, style);
                List list = new List(true);
                try {
                    list.setIndentationLeft(Float.parseFloat(this.cprops.getProperty("indent")));
                }
                catch (Exception e) {
                    list.setAutoindent(true);
                }
                this.stack.push(list);
                return;
            }
            if (tag.equals("li")) {
                if (this.pendingLI) {
                    this.endElement("li");
                }
                this.skipText = false;
                this.pendingLI = true;
                this.cprops.addToChain(tag, style);
                ListItem item = FactoryProperties.createListItem(this.cprops);
                this.stack.push(item);
                return;
            }
            if (tag.equals("div") || tag.equals("body") || tag.equals("p")) {
                this.cprops.addToChain(tag, style);
                return;
            }
            if (tag.equals("pre")) {
                if (!style.containsKey("face")) {
                    style.put("face", "Courier");
                }
                this.cprops.addToChain(tag, style);
                this.isPRE = true;
                return;
            }
            if (tag.equals("tr")) {
                if (this.pendingTR) {
                    this.endElement("tr");
                }
                this.skipText = true;
                this.pendingTR = true;
                this.cprops.addToChain("tr", style);
                return;
            }
            if (tag.equals("td") || tag.equals("th")) {
                if (this.pendingTD) {
                    this.endElement(tag);
                }
                this.skipText = false;
                this.pendingTD = true;
                this.cprops.addToChain("td", style);
                this.stack.push(new IncCell(tag, this.cprops));
                return;
            }
            if (tag.equals("table")) {
                this.cprops.addToChain("table", style);
                IncTable table = new IncTable(style);
                this.stack.push(table);
                this.tableState.push(new boolean[]{this.pendingTR, this.pendingTD});
                this.pendingTD = false;
                this.pendingTR = false;
                this.skipText = true;
                return;
            }
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    @Override
    public void endElement(String tag) {
        if (!tagsSupported.containsKey(tag)) {
            return;
        }
        try {
            Object obj;
            String follow = FactoryProperties.followTags.get(tag);
            if (follow != null) {
                this.cprops.removeChain(follow);
                return;
            }
            if (tag.equals("font") || tag.equals("span")) {
                this.cprops.removeChain(tag);
                return;
            }
            if (tag.equals("a")) {
                ALink i;
                if (this.currentParagraph == null) {
                    this.currentParagraph = new Paragraph();
                }
                boolean skip = false;
                if (this.interfaceProps != null && (i = (ALink)this.interfaceProps.get("alink_interface")) != null) {
                    skip = i.process(this.currentParagraph, this.cprops);
                }
                if (!skip) {
                    this.cprops.findProperty("href").ifPresent(href -> {
                        ArrayList<Element> chunks = this.currentParagraph.getChunks();
                        for (Element chunk : chunks) {
                            Chunk ck = (Chunk)chunk;
                            ck.setAnchor((String)href);
                        }
                    });
                }
                Paragraph tmp = (Paragraph)this.stack.pop();
                Phrase tmp2 = new Phrase();
                tmp2.add(this.currentParagraph);
                tmp.add(tmp2);
                this.currentParagraph = tmp;
                this.cprops.removeChain("a");
                return;
            }
            if (tag.equals("br")) {
                return;
            }
            if (this.currentParagraph != null) {
                if (this.stack.empty()) {
                    this.document.add(this.currentParagraph);
                } else {
                    obj = this.stack.pop();
                    if (obj instanceof TextElementArray) {
                        TextElementArray current = (TextElementArray)obj;
                        current.add(this.currentParagraph);
                    }
                    this.stack.push(obj);
                }
            }
            this.currentParagraph = null;
            if (tag.equals("ul") || tag.equals("ol")) {
                if (this.pendingLI) {
                    this.endElement("li");
                }
                this.skipText = false;
                this.cprops.removeChain(tag);
                if (this.stack.empty()) {
                    return;
                }
                obj = this.stack.pop();
                if (!(obj instanceof List)) {
                    this.stack.push(obj);
                    return;
                }
                if (this.stack.empty()) {
                    this.document.add((Element)obj);
                } else {
                    ((TextElementArray)this.stack.peek()).add((Element)obj);
                }
                return;
            }
            if (tag.equals("li")) {
                this.pendingLI = false;
                this.skipText = true;
                this.cprops.removeChain(tag);
                if (this.stack.empty()) {
                    return;
                }
                obj = this.stack.pop();
                if (!(obj instanceof ListItem)) {
                    this.stack.push(obj);
                    return;
                }
                if (this.stack.empty()) {
                    this.document.add((Element)obj);
                    return;
                }
                Object list = this.stack.pop();
                if (!(list instanceof List)) {
                    this.stack.push(list);
                    return;
                }
                ListItem item = (ListItem)obj;
                ((List)list).add(item);
                ArrayList<Element> cks = item.getChunks();
                if (!cks.isEmpty()) {
                    item.getListSymbol().setFont(((Chunk)cks.get(0)).getFont());
                }
                this.stack.push(list);
                return;
            }
            if (tag.equals("div") || tag.equals("body")) {
                this.cprops.removeChain(tag);
                return;
            }
            if (tag.equals("pre")) {
                this.cprops.removeChain(tag);
                this.isPRE = false;
                return;
            }
            if (tag.equals("p")) {
                this.cprops.removeChain(tag);
                return;
            }
            if (tag.equals("h1") || tag.equals("h2") || tag.equals("h3") || tag.equals("h4") || tag.equals("h5") || tag.equals("h6")) {
                this.cprops.removeChain(tag);
                return;
            }
            if (tag.equals("table")) {
                if (this.pendingTR) {
                    this.endElement("tr");
                }
                this.cprops.removeChain("table");
                IncTable table = (IncTable)this.stack.pop();
                PdfPTable tb = table.buildTable();
                tb.setSplitRows(true);
                if (this.stack.empty()) {
                    this.document.add(tb);
                } else {
                    ((TextElementArray)this.stack.peek()).add(tb);
                }
                boolean[] state = (boolean[])this.tableState.pop();
                this.pendingTR = state[0];
                this.pendingTD = state[1];
                this.skipText = false;
                return;
            }
            if (tag.equals("tr")) {
                Object obj2;
                if (this.pendingTD) {
                    this.endElement("td");
                }
                this.pendingTR = false;
                this.cprops.removeChain("tr");
                ArrayList<PdfPCell> cells = new ArrayList<PdfPCell>();
                do {
                    if (!((obj2 = this.stack.pop()) instanceof IncCell)) continue;
                    cells.add(((IncCell)obj2).getCell());
                } while (!(obj2 instanceof IncTable));
                IncTable table = (IncTable)obj2;
                table.addCols((java.util.List<PdfPCell>)cells);
                table.endRow();
                this.stack.push(table);
                this.skipText = true;
                return;
            }
            if (tag.equals("td") || tag.equals("th")) {
                this.pendingTD = false;
                this.cprops.removeChain("td");
                this.skipText = true;
                return;
            }
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    @Override
    public void text(String str) {
        if (this.skipText) {
            return;
        }
        String content = str;
        if (this.isPRE) {
            if (this.currentParagraph == null) {
                this.currentParagraph = FactoryProperties.createParagraph(this.cprops);
            }
            Chunk chunk = this.factoryProperties.createChunk(content, this.cprops);
            this.currentParagraph.add(chunk);
            return;
        }
        if (content.trim().length() == 0 && content.indexOf(32) < 0) {
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
        if (this.currentParagraph == null) {
            this.currentParagraph = FactoryProperties.createParagraph(this.cprops);
        }
        Chunk chunk = this.factoryProperties.createChunk(buf.toString(), this.cprops);
        this.currentParagraph.add(chunk);
    }

    @Override
    public boolean add(Element element) throws DocumentException {
        this.objectList.add(element);
        return true;
    }

    public void clearTextWrap() throws DocumentException {
    }

    @Override
    public void close() {
    }

    @Override
    public boolean newPage() {
        return true;
    }

    @Override
    public void open() {
    }

    @Override
    public void resetFooter() {
    }

    @Override
    public void resetHeader() {
    }

    @Override
    public void resetPageCount() {
    }

    @Override
    public void setFooter(HeaderFooter footer) {
    }

    @Override
    public void setHeader(HeaderFooter header) {
    }

    @Override
    public boolean setMarginMirroring(boolean marginMirroring) {
        return false;
    }

    @Override
    public boolean setMarginMirroringTopBottom(boolean marginMirroring) {
        return false;
    }

    @Override
    public boolean setMargins(float marginLeft, float marginRight, float marginTop, float marginBottom) {
        return true;
    }

    @Override
    public void setPageCount(int pageN) {
    }

    @Override
    public boolean setPageSize(Rectangle pageSize) {
        return true;
    }

    static {
        StringTokenizer tok = new StringTokenizer(tagsSupportedString);
        while (tok.hasMoreTokens()) {
            tagsSupported.put(tok.nextToken(), null);
        }
    }
}

