/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui.text;

import groovy.ui.text.StructuredSyntaxDocumentFilter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class StructuredSyntaxHandler
extends DefaultHandler {
    public static final String REGEXP = "regexp";
    public static final String STYLE = "style";
    public static final String ALIGN_CENTER = "ALIGN_CENTER";
    public static final String ALIGN_JUSTIFIED = "ALIGN_JUSTIFIED";
    public static final String ALIGN_LEFT = "ALIGN_LEFT";
    public static final String ALIGN_RIGHT = "ALIGN_RIGHT";
    public static final String ALIGNMENT = "alignment";
    public static final String BACKGROUND = "background";
    public static final String BIDI_LEVEL = "bidiLevel";
    public static final String BOLD = "bold";
    public static final String COMPONENT_ATTRIBUTE = "componentAttribute";
    public static final String COMPONENT_ELEMENT_NAME = "componentElementName";
    public static final String COMPOSED_TEXT_ATTRIBUTE = "composedTextAttribute";
    public static final String FIRST_LINE_INDENT = "firstLineIndent";
    public static final String FONT_FAMILY = "fontFamily";
    public static final String FONT_SIZE = "fontSize";
    public static final String FOREGROUND = "foreground";
    public static final String ICON_ATTRIBUTE = "iconAttribute";
    public static final String ICON_ELEMENT_NAME = "iconElementName";
    public static final String ITALIC = "italic";
    public static final String LEFT_INDENT = "leftIndent";
    public static final String LINE_SPACING = "lineSpacing";
    public static final String MODEL_ATTRIBUTE = "modelAttribute";
    public static final String NAME_ATTRIBUTE = "nameAttribute";
    public static final String ORIENTATION = "orientation";
    public static final String RESOLVE_ATTRIBUTE = "resolveAttribute";
    public static final String RIGHT_INDENT = "rightIndent";
    public static final String SPACE_ABOVE = "spaceAbove";
    public static final String SPACE_BELOW = "spaceBelow";
    public static final String STRIKE_THROUGH = "strikeThrough";
    public static final String SUBSCRIPT = "subscript";
    public static final String SUPERSCRIPT = "superscript";
    public static final String TAB_SET = "tabSet";
    public static final String UNDERLINE = "underline";
    private StructuredSyntaxDocumentFilter.LexerNode currentNode;
    private final StructuredSyntaxDocumentFilter filter;

    public StructuredSyntaxHandler(StructuredSyntaxDocumentFilter filter) {
        this.filter = filter;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        throw new SAXException("Line: " + e.getLineNumber() + " message: " + e.getMessage());
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        this.currentNode = this.filter.getRootNode();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
    }
}

