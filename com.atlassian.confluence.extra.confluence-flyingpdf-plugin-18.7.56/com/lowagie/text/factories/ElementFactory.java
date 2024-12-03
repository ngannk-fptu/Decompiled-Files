/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.factories;

import com.lowagie.text.Anchor;
import com.lowagie.text.Annotation;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.ChapterAutoNumber;
import com.lowagie.text.Chunk;
import com.lowagie.text.ElementTags;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Section;
import com.lowagie.text.Table;
import com.lowagie.text.Utilities;
import com.lowagie.text.alignment.HorizontalAlignment;
import com.lowagie.text.alignment.VerticalAlignment;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.html.Markup;
import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

public class ElementFactory {
    public static Chunk getChunk(Properties attributes) {
        Chunk chunk = new Chunk();
        chunk.setFont(FontFactory.getFont(attributes));
        String value = attributes.getProperty("itext");
        if (value != null) {
            chunk.append(value);
        }
        if ((value = attributes.getProperty(ElementTags.LOCALGOTO)) != null) {
            chunk.setLocalGoto(value);
        }
        if ((value = attributes.getProperty(ElementTags.REMOTEGOTO)) != null) {
            String page = attributes.getProperty("page");
            if (page != null) {
                chunk.setRemoteGoto(value, Integer.parseInt(page));
            } else {
                String destination = attributes.getProperty("destination");
                if (destination != null) {
                    chunk.setRemoteGoto(value, destination);
                }
            }
        }
        if ((value = attributes.getProperty(ElementTags.LOCALDESTINATION)) != null) {
            chunk.setLocalDestination(value);
        }
        if ((value = attributes.getProperty(ElementTags.SUBSUPSCRIPT)) != null) {
            chunk.setTextRise(Float.parseFloat(value + "f"));
        }
        if ((value = attributes.getProperty("vertical-align")) != null && value.endsWith("%")) {
            float p = Float.parseFloat(value.substring(0, value.length() - 1) + "f") / 100.0f;
            chunk.setTextRise(p * chunk.getFont().getSize());
        }
        if ((value = attributes.getProperty(ElementTags.GENERICTAG)) != null) {
            chunk.setGenericTag(value);
        }
        if ((value = attributes.getProperty("backgroundcolor")) != null) {
            chunk.setBackground(Markup.decodeColor(value));
        }
        return chunk;
    }

    public static Phrase getPhrase(Properties attributes) {
        Phrase phrase = new Phrase();
        phrase.setFont(FontFactory.getFont(attributes));
        String value = attributes.getProperty("leading");
        if (value != null) {
            phrase.setLeading(Float.parseFloat(value + "f"));
        }
        if ((value = attributes.getProperty("line-height")) != null) {
            phrase.setLeading(Markup.parseLength(value, 12.0f));
        }
        if ((value = attributes.getProperty("itext")) != null) {
            Chunk chunk = new Chunk(value);
            value = attributes.getProperty(ElementTags.GENERICTAG);
            if (value != null) {
                chunk.setGenericTag(value);
            }
            phrase.add(chunk);
        }
        return phrase;
    }

    public static Anchor getAnchor(Properties attributes) {
        Anchor anchor = new Anchor(ElementFactory.getPhrase(attributes));
        String value = attributes.getProperty("name");
        if (value != null) {
            anchor.setName(value);
        }
        if ((value = (String)attributes.remove("reference")) != null) {
            anchor.setReference(value);
        }
        return anchor;
    }

    public static Paragraph getParagraph(Properties attributes) {
        Paragraph paragraph = new Paragraph(ElementFactory.getPhrase(attributes));
        String value = attributes.getProperty("align");
        if (value != null) {
            paragraph.setAlignment(value);
        }
        if ((value = attributes.getProperty("indentationleft")) != null) {
            paragraph.setIndentationLeft(Float.parseFloat(value + "f"));
        }
        if ((value = attributes.getProperty("indentationright")) != null) {
            paragraph.setIndentationRight(Float.parseFloat(value + "f"));
        }
        return paragraph;
    }

    public static ListItem getListItem(Properties attributes) {
        ListItem item = new ListItem(ElementFactory.getParagraph(attributes));
        return item;
    }

    public static List getList(Properties attributes) {
        List list = new List();
        list.setNumbered(Utilities.checkTrueOrFalse(attributes, "numbered"));
        list.setLettered(Utilities.checkTrueOrFalse(attributes, "lettered"));
        list.setLowercase(Utilities.checkTrueOrFalse(attributes, "lowercase"));
        list.setAutoindent(Utilities.checkTrueOrFalse(attributes, "autoindent"));
        list.setAlignindent(Utilities.checkTrueOrFalse(attributes, "alignindent"));
        String value = attributes.getProperty("first");
        if (value != null) {
            char character = value.charAt(0);
            if (Character.isLetter(character)) {
                list.setFirst(character);
            } else {
                list.setFirst(Integer.parseInt(value));
            }
        }
        if ((value = attributes.getProperty("listsymbol")) != null) {
            list.setListSymbol(new Chunk(value, FontFactory.getFont(attributes)));
        }
        if ((value = attributes.getProperty("indentationleft")) != null) {
            list.setIndentationLeft(Float.parseFloat(value + "f"));
        }
        if ((value = attributes.getProperty("indentationright")) != null) {
            list.setIndentationRight(Float.parseFloat(value + "f"));
        }
        if ((value = attributes.getProperty("symbolindent")) != null) {
            list.setSymbolIndent(Float.parseFloat(value));
        }
        return list;
    }

    public static Cell getCell(Properties attributes) {
        Cell cell = new Cell();
        try {
            if (attributes.getProperty("horizontalalign") != null) {
                HorizontalAlignment horizontalAlignment = HorizontalAlignment.valueOf(attributes.getProperty("horizontalalign"));
                cell.setHorizontalAlignment(horizontalAlignment);
            } else {
                cell.setHorizontalAlignment(HorizontalAlignment.UNDEFINED);
            }
        }
        catch (IllegalArgumentException exc) {
            cell.setHorizontalAlignment(HorizontalAlignment.UNDEFINED);
        }
        try {
            if (attributes.getProperty("verticalalign") != null) {
                VerticalAlignment verticalAlignment = VerticalAlignment.valueOf(attributes.getProperty("verticalalign"));
                cell.setVerticalAlignment(verticalAlignment);
            } else {
                cell.setVerticalAlignment(VerticalAlignment.UNDEFINED);
            }
        }
        catch (IllegalArgumentException exc) {
            cell.setVerticalAlignment(VerticalAlignment.UNDEFINED);
        }
        String value = attributes.getProperty("width");
        if (value != null) {
            cell.setWidth(value);
        }
        if ((value = attributes.getProperty("colspan")) != null) {
            cell.setColspan(Integer.parseInt(value));
        }
        if ((value = attributes.getProperty("rowspan")) != null) {
            cell.setRowspan(Integer.parseInt(value));
        }
        if ((value = attributes.getProperty("leading")) != null) {
            cell.setLeading(Float.parseFloat(value + "f"));
        }
        cell.setHeader(Utilities.checkTrueOrFalse(attributes, "header"));
        if (Utilities.checkTrueOrFalse(attributes, "nowrap")) {
            cell.setMaxLines(1);
        }
        ElementFactory.setRectangleProperties(cell, attributes);
        return cell;
    }

    public static Table getTable(Properties attributes) {
        try {
            Table table;
            String value = attributes.getProperty("widths");
            if (value != null) {
                StringTokenizer widthTokens = new StringTokenizer(value, ";");
                ArrayList<String> values = new ArrayList<String>();
                while (widthTokens.hasMoreTokens()) {
                    values.add(widthTokens.nextToken());
                }
                table = new Table(values.size());
                float[] widths = new float[table.getColumns()];
                for (int i = 0; i < values.size(); ++i) {
                    value = (String)values.get(i);
                    widths[i] = Float.parseFloat(value + "f");
                }
                table.setWidths(widths);
            } else {
                value = attributes.getProperty("columns");
                try {
                    table = new Table(Integer.parseInt(value));
                }
                catch (Exception e) {
                    table = new Table(1);
                }
            }
            table.setBorder(15);
            table.setBorderWidth(1.0f);
            table.getDefaultCell().setBorder(15);
            value = attributes.getProperty("lastHeaderRow");
            if (value != null) {
                table.setLastHeaderRow(Integer.parseInt(value));
            }
            if ((value = attributes.getProperty("align")) != null) {
                try {
                    HorizontalAlignment horizontalAlignment = HorizontalAlignment.valueOf(value);
                    table.setHorizontalAlignment(horizontalAlignment);
                }
                catch (IllegalArgumentException exc) {
                    table.setHorizontalAlignment(HorizontalAlignment.UNDEFINED);
                }
            }
            if ((value = attributes.getProperty("cellspacing")) != null) {
                table.setSpacing(Float.parseFloat(value + "f"));
            }
            if ((value = attributes.getProperty("cellpadding")) != null) {
                table.setPadding(Float.parseFloat(value + "f"));
            }
            if ((value = attributes.getProperty("offset")) != null) {
                table.setOffset(Float.parseFloat(value + "f"));
            }
            if ((value = attributes.getProperty("width")) != null) {
                if (value.endsWith("%")) {
                    table.setWidth(Float.parseFloat(value.substring(0, value.length() - 1) + "f"));
                } else {
                    table.setWidth(Float.parseFloat(value + "f"));
                    table.setLocked(true);
                }
            }
            table.setTableFitsPage(Utilities.checkTrueOrFalse(attributes, "tablefitspage"));
            table.setCellsFitPage(Utilities.checkTrueOrFalse(attributes, "cellsfitpage"));
            table.setConvert2pdfptable(Utilities.checkTrueOrFalse(attributes, "convert2pdfp"));
            ElementFactory.setRectangleProperties(table, attributes);
            return table;
        }
        catch (BadElementException e) {
            throw new ExceptionConverter(e);
        }
    }

    private static void setRectangleProperties(Rectangle rect, Properties attributes) {
        int blue;
        int green;
        int red;
        String value = attributes.getProperty("borderwidth");
        if (value != null) {
            rect.setBorderWidth(Float.parseFloat(value + "f"));
        }
        int border = 0;
        if (Utilities.checkTrueOrFalse(attributes, "left")) {
            border |= 4;
        }
        if (Utilities.checkTrueOrFalse(attributes, "right")) {
            border |= 8;
        }
        if (Utilities.checkTrueOrFalse(attributes, "top")) {
            border |= 1;
        }
        if (Utilities.checkTrueOrFalse(attributes, "bottom")) {
            border |= 2;
        }
        rect.setBorder(border);
        String r = attributes.getProperty("red");
        String g = attributes.getProperty("green");
        String b = attributes.getProperty("blue");
        if (r != null || g != null || b != null) {
            red = 0;
            green = 0;
            blue = 0;
            if (r != null) {
                red = Integer.parseInt(r);
            }
            if (g != null) {
                green = Integer.parseInt(g);
            }
            if (b != null) {
                blue = Integer.parseInt(b);
            }
            rect.setBorderColor(new Color(red, green, blue));
        } else {
            rect.setBorderColor(Markup.decodeColor(attributes.getProperty("bordercolor")));
        }
        r = (String)attributes.remove("bgred");
        g = (String)attributes.remove("bggreen");
        b = (String)attributes.remove("bgblue");
        value = attributes.getProperty("backgroundcolor");
        if (r != null || g != null || b != null) {
            red = 0;
            green = 0;
            blue = 0;
            if (r != null) {
                red = Integer.parseInt(r);
            }
            if (g != null) {
                green = Integer.parseInt(g);
            }
            if (b != null) {
                blue = Integer.parseInt(b);
            }
            rect.setBackgroundColor(new Color(red, green, blue));
        } else if (value != null) {
            rect.setBackgroundColor(Markup.decodeColor(value));
        } else {
            value = attributes.getProperty("grayfill");
            if (value != null) {
                rect.setGrayFill(Float.parseFloat(value + "f"));
            }
        }
    }

    public static ChapterAutoNumber getChapter(Properties attributes) {
        ChapterAutoNumber chapter = new ChapterAutoNumber("");
        ElementFactory.setSectionParameters(chapter, attributes);
        return chapter;
    }

    public static Section getSection(Section parent, Properties attributes) {
        Section section = parent.addSection("");
        ElementFactory.setSectionParameters(section, attributes);
        return section;
    }

    private static void setSectionParameters(Section section, Properties attributes) {
        String value = attributes.getProperty("numberdepth");
        if (value != null) {
            section.setNumberDepth(Integer.parseInt(value));
        }
        if ((value = attributes.getProperty("indent")) != null) {
            section.setIndentation(Float.parseFloat(value + "f"));
        }
        if ((value = attributes.getProperty("indentationleft")) != null) {
            section.setIndentationLeft(Float.parseFloat(value + "f"));
        }
        if ((value = attributes.getProperty("indentationright")) != null) {
            section.setIndentationRight(Float.parseFloat(value + "f"));
        }
    }

    public static Image getImage(Properties attributes) throws BadElementException, IOException {
        String value = attributes.getProperty("url");
        if (value == null) {
            throw new MalformedURLException(MessageLocalization.getComposedMessage("the.url.of.the.image.is.missing"));
        }
        Image image = Image.getInstance(value);
        value = attributes.getProperty("align");
        int align = 0;
        if (value != null) {
            if ("Left".equalsIgnoreCase(value)) {
                align |= 0;
            } else if ("Right".equalsIgnoreCase(value)) {
                align |= 2;
            } else if ("Middle".equalsIgnoreCase(value)) {
                align |= 1;
            }
        }
        if ("true".equalsIgnoreCase(attributes.getProperty("underlying"))) {
            align |= 8;
        }
        if ("true".equalsIgnoreCase(attributes.getProperty("textwrap"))) {
            align |= 4;
        }
        image.setAlignment(align);
        value = attributes.getProperty("alt");
        if (value != null) {
            image.setAlt(value);
        }
        String x = attributes.getProperty("absolutex");
        String y = attributes.getProperty("absolutey");
        if (x != null && y != null) {
            image.setAbsolutePosition(Float.parseFloat(x + "f"), Float.parseFloat(y + "f"));
        }
        if ((value = attributes.getProperty("plainwidth")) != null) {
            image.scaleAbsoluteWidth(Float.parseFloat(value + "f"));
        }
        if ((value = attributes.getProperty("plainheight")) != null) {
            image.scaleAbsoluteHeight(Float.parseFloat(value + "f"));
        }
        if ((value = attributes.getProperty("rotation")) != null) {
            image.setRotation(Float.parseFloat(value + "f"));
        }
        return image;
    }

    public static Annotation getAnnotation(Properties attributes) {
        float llx = 0.0f;
        float lly = 0.0f;
        float urx = 0.0f;
        float ury = 0.0f;
        String value = attributes.getProperty("llx");
        if (value != null) {
            llx = Float.parseFloat(value + "f");
        }
        if ((value = attributes.getProperty("lly")) != null) {
            lly = Float.parseFloat(value + "f");
        }
        if ((value = attributes.getProperty("urx")) != null) {
            urx = Float.parseFloat(value + "f");
        }
        if ((value = attributes.getProperty("ury")) != null) {
            ury = Float.parseFloat(value + "f");
        }
        String title = attributes.getProperty("title");
        String text = attributes.getProperty("content");
        if (title != null || text != null) {
            return new Annotation(title, text, llx, lly, urx, ury);
        }
        value = attributes.getProperty("url");
        if (value != null) {
            return new Annotation(llx, lly, urx, ury, value);
        }
        value = attributes.getProperty("named");
        if (value != null) {
            return new Annotation(llx, lly, urx, ury, Integer.parseInt(value));
        }
        String file = attributes.getProperty("file");
        String destination = attributes.getProperty("destination");
        String page = (String)attributes.remove("page");
        if (file != null) {
            if (destination != null) {
                return new Annotation(llx, lly, urx, ury, file, destination);
            }
            if (page != null) {
                return new Annotation(llx, lly, urx, ury, file, Integer.parseInt(page));
            }
        }
        return new Annotation("", "", llx, lly, urx, ury);
    }
}

