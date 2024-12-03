/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;
import org.apache.xml.serializer.CharInfo;
import org.apache.xml.serializer.ElemContext;
import org.apache.xml.serializer.ElemDesc;
import org.apache.xml.serializer.Encodings;
import org.apache.xml.serializer.NamespaceMappings;
import org.apache.xml.serializer.OutputPropertyUtils;
import org.apache.xml.serializer.ToStream;
import org.apache.xml.serializer.utils.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ToHTMLStream
extends ToStream {
    protected boolean m_inDTD = false;
    private boolean m_inBlockElem = false;
    private final CharInfo m_htmlcharInfo = CharInfo.getCharInfo(CharInfo.HTML_ENTITIES_RESOURCE, "html");
    static final Trie m_elementFlags = new Trie();
    private static final ElemDesc m_dummy;
    private boolean m_specialEscapeURLs = true;
    private boolean m_omitMetaTag = false;
    private Trie m_htmlInfo = new Trie(m_elementFlags);

    static void initTagReference(Trie m_elementFlags) {
        m_elementFlags.put("BASEFONT", new ElemDesc(2));
        m_elementFlags.put("FRAME", new ElemDesc(10));
        m_elementFlags.put("FRAMESET", new ElemDesc(8));
        m_elementFlags.put("NOFRAMES", new ElemDesc(8));
        m_elementFlags.put("ISINDEX", new ElemDesc(10));
        m_elementFlags.put("APPLET", new ElemDesc(0x200000));
        m_elementFlags.put("CENTER", new ElemDesc(8));
        m_elementFlags.put("DIR", new ElemDesc(8));
        m_elementFlags.put("MENU", new ElemDesc(8));
        m_elementFlags.put("TT", new ElemDesc(4096));
        m_elementFlags.put("I", new ElemDesc(4096));
        m_elementFlags.put("B", new ElemDesc(4096));
        m_elementFlags.put("BIG", new ElemDesc(4096));
        m_elementFlags.put("SMALL", new ElemDesc(4096));
        m_elementFlags.put("EM", new ElemDesc(8192));
        m_elementFlags.put("STRONG", new ElemDesc(8192));
        m_elementFlags.put("DFN", new ElemDesc(8192));
        m_elementFlags.put("CODE", new ElemDesc(8192));
        m_elementFlags.put("SAMP", new ElemDesc(8192));
        m_elementFlags.put("KBD", new ElemDesc(8192));
        m_elementFlags.put("VAR", new ElemDesc(8192));
        m_elementFlags.put("CITE", new ElemDesc(8192));
        m_elementFlags.put("ABBR", new ElemDesc(8192));
        m_elementFlags.put("ACRONYM", new ElemDesc(8192));
        m_elementFlags.put("SUP", new ElemDesc(98304));
        m_elementFlags.put("SUB", new ElemDesc(98304));
        m_elementFlags.put("SPAN", new ElemDesc(98304));
        m_elementFlags.put("BDO", new ElemDesc(98304));
        m_elementFlags.put("BR", new ElemDesc(98314));
        m_elementFlags.put("BODY", new ElemDesc(8));
        m_elementFlags.put("ADDRESS", new ElemDesc(56));
        m_elementFlags.put("DIV", new ElemDesc(56));
        m_elementFlags.put("A", new ElemDesc(32768));
        m_elementFlags.put("MAP", new ElemDesc(98312));
        m_elementFlags.put("AREA", new ElemDesc(10));
        m_elementFlags.put("LINK", new ElemDesc(131082));
        m_elementFlags.put("IMG", new ElemDesc(2195458));
        m_elementFlags.put("OBJECT", new ElemDesc(2326528));
        m_elementFlags.put("PARAM", new ElemDesc(2));
        m_elementFlags.put("HR", new ElemDesc(58));
        m_elementFlags.put("P", new ElemDesc(56));
        m_elementFlags.put("H1", new ElemDesc(262152));
        m_elementFlags.put("H2", new ElemDesc(262152));
        m_elementFlags.put("H3", new ElemDesc(262152));
        m_elementFlags.put("H4", new ElemDesc(262152));
        m_elementFlags.put("H5", new ElemDesc(262152));
        m_elementFlags.put("H6", new ElemDesc(262152));
        m_elementFlags.put("PRE", new ElemDesc(0x100008));
        m_elementFlags.put("Q", new ElemDesc(98304));
        m_elementFlags.put("BLOCKQUOTE", new ElemDesc(56));
        m_elementFlags.put("INS", new ElemDesc(0));
        m_elementFlags.put("DEL", new ElemDesc(0));
        m_elementFlags.put("DL", new ElemDesc(56));
        m_elementFlags.put("DT", new ElemDesc(8));
        m_elementFlags.put("DD", new ElemDesc(8));
        m_elementFlags.put("OL", new ElemDesc(524296));
        m_elementFlags.put("UL", new ElemDesc(524296));
        m_elementFlags.put("LI", new ElemDesc(8));
        m_elementFlags.put("FORM", new ElemDesc(8));
        m_elementFlags.put("LABEL", new ElemDesc(16384));
        m_elementFlags.put("INPUT", new ElemDesc(18434));
        m_elementFlags.put("SELECT", new ElemDesc(18432));
        m_elementFlags.put("OPTGROUP", new ElemDesc(0));
        m_elementFlags.put("OPTION", new ElemDesc(0));
        m_elementFlags.put("TEXTAREA", new ElemDesc(18432));
        m_elementFlags.put("FIELDSET", new ElemDesc(24));
        m_elementFlags.put("LEGEND", new ElemDesc(0));
        m_elementFlags.put("BUTTON", new ElemDesc(18432));
        m_elementFlags.put("TABLE", new ElemDesc(56));
        m_elementFlags.put("CAPTION", new ElemDesc(8));
        m_elementFlags.put("THEAD", new ElemDesc(8));
        m_elementFlags.put("TFOOT", new ElemDesc(8));
        m_elementFlags.put("TBODY", new ElemDesc(8));
        m_elementFlags.put("COLGROUP", new ElemDesc(8));
        m_elementFlags.put("COL", new ElemDesc(10));
        m_elementFlags.put("TR", new ElemDesc(8));
        m_elementFlags.put("TH", new ElemDesc(0));
        m_elementFlags.put("TD", new ElemDesc(0));
        m_elementFlags.put("HEAD", new ElemDesc(0x400008));
        m_elementFlags.put("TITLE", new ElemDesc(8));
        m_elementFlags.put("BASE", new ElemDesc(10));
        m_elementFlags.put("META", new ElemDesc(131082));
        m_elementFlags.put("STYLE", new ElemDesc(131336));
        m_elementFlags.put("SCRIPT", new ElemDesc(229632));
        m_elementFlags.put("NOSCRIPT", new ElemDesc(56));
        m_elementFlags.put("HTML", new ElemDesc(0x800008));
        m_elementFlags.put("FONT", new ElemDesc(4096));
        m_elementFlags.put("S", new ElemDesc(4096));
        m_elementFlags.put("STRIKE", new ElemDesc(4096));
        m_elementFlags.put("U", new ElemDesc(4096));
        m_elementFlags.put("NOBR", new ElemDesc(4096));
        m_elementFlags.put("IFRAME", new ElemDesc(56));
        m_elementFlags.put("LAYER", new ElemDesc(56));
        m_elementFlags.put("ILAYER", new ElemDesc(56));
        ElemDesc elemDesc = (ElemDesc)m_elementFlags.get("a");
        elemDesc.setAttr("HREF", 2);
        elemDesc.setAttr("NAME", 2);
        elemDesc = (ElemDesc)m_elementFlags.get("area");
        elemDesc.setAttr("HREF", 2);
        elemDesc.setAttr("NOHREF", 4);
        elemDesc = (ElemDesc)m_elementFlags.get("base");
        elemDesc.setAttr("HREF", 2);
        elemDesc = (ElemDesc)m_elementFlags.get("button");
        elemDesc.setAttr("DISABLED", 4);
        elemDesc = (ElemDesc)m_elementFlags.get("blockquote");
        elemDesc.setAttr("CITE", 2);
        elemDesc = (ElemDesc)m_elementFlags.get("del");
        elemDesc.setAttr("CITE", 2);
        elemDesc = (ElemDesc)m_elementFlags.get("dir");
        elemDesc.setAttr("COMPACT", 4);
        elemDesc = (ElemDesc)m_elementFlags.get("div");
        elemDesc.setAttr("SRC", 2);
        elemDesc.setAttr("NOWRAP", 4);
        elemDesc = (ElemDesc)m_elementFlags.get("dl");
        elemDesc.setAttr("COMPACT", 4);
        elemDesc = (ElemDesc)m_elementFlags.get("form");
        elemDesc.setAttr("ACTION", 2);
        elemDesc = (ElemDesc)m_elementFlags.get("frame");
        elemDesc.setAttr("SRC", 2);
        elemDesc.setAttr("LONGDESC", 2);
        elemDesc.setAttr("NORESIZE", 4);
        elemDesc = (ElemDesc)m_elementFlags.get("head");
        elemDesc.setAttr("PROFILE", 2);
        elemDesc = (ElemDesc)m_elementFlags.get("hr");
        elemDesc.setAttr("NOSHADE", 4);
        elemDesc = (ElemDesc)m_elementFlags.get("iframe");
        elemDesc.setAttr("SRC", 2);
        elemDesc.setAttr("LONGDESC", 2);
        elemDesc = (ElemDesc)m_elementFlags.get("ilayer");
        elemDesc.setAttr("SRC", 2);
        elemDesc = (ElemDesc)m_elementFlags.get("img");
        elemDesc.setAttr("SRC", 2);
        elemDesc.setAttr("LONGDESC", 2);
        elemDesc.setAttr("USEMAP", 2);
        elemDesc.setAttr("ISMAP", 4);
        elemDesc = (ElemDesc)m_elementFlags.get("input");
        elemDesc.setAttr("SRC", 2);
        elemDesc.setAttr("USEMAP", 2);
        elemDesc.setAttr("CHECKED", 4);
        elemDesc.setAttr("DISABLED", 4);
        elemDesc.setAttr("ISMAP", 4);
        elemDesc.setAttr("READONLY", 4);
        elemDesc = (ElemDesc)m_elementFlags.get("ins");
        elemDesc.setAttr("CITE", 2);
        elemDesc = (ElemDesc)m_elementFlags.get("layer");
        elemDesc.setAttr("SRC", 2);
        elemDesc = (ElemDesc)m_elementFlags.get("link");
        elemDesc.setAttr("HREF", 2);
        elemDesc = (ElemDesc)m_elementFlags.get("menu");
        elemDesc.setAttr("COMPACT", 4);
        elemDesc = (ElemDesc)m_elementFlags.get("object");
        elemDesc.setAttr("CLASSID", 2);
        elemDesc.setAttr("CODEBASE", 2);
        elemDesc.setAttr("DATA", 2);
        elemDesc.setAttr("ARCHIVE", 2);
        elemDesc.setAttr("USEMAP", 2);
        elemDesc.setAttr("DECLARE", 4);
        elemDesc = (ElemDesc)m_elementFlags.get("ol");
        elemDesc.setAttr("COMPACT", 4);
        elemDesc = (ElemDesc)m_elementFlags.get("optgroup");
        elemDesc.setAttr("DISABLED", 4);
        elemDesc = (ElemDesc)m_elementFlags.get("option");
        elemDesc.setAttr("SELECTED", 4);
        elemDesc.setAttr("DISABLED", 4);
        elemDesc = (ElemDesc)m_elementFlags.get("q");
        elemDesc.setAttr("CITE", 2);
        elemDesc = (ElemDesc)m_elementFlags.get("script");
        elemDesc.setAttr("SRC", 2);
        elemDesc.setAttr("FOR", 2);
        elemDesc.setAttr("DEFER", 4);
        elemDesc = (ElemDesc)m_elementFlags.get("select");
        elemDesc.setAttr("DISABLED", 4);
        elemDesc.setAttr("MULTIPLE", 4);
        elemDesc = (ElemDesc)m_elementFlags.get("table");
        elemDesc.setAttr("NOWRAP", 4);
        elemDesc = (ElemDesc)m_elementFlags.get("td");
        elemDesc.setAttr("NOWRAP", 4);
        elemDesc = (ElemDesc)m_elementFlags.get("textarea");
        elemDesc.setAttr("DISABLED", 4);
        elemDesc.setAttr("READONLY", 4);
        elemDesc = (ElemDesc)m_elementFlags.get("th");
        elemDesc.setAttr("NOWRAP", 4);
        elemDesc = (ElemDesc)m_elementFlags.get("tr");
        elemDesc.setAttr("NOWRAP", 4);
        elemDesc = (ElemDesc)m_elementFlags.get("ul");
        elemDesc.setAttr("COMPACT", 4);
    }

    public void setSpecialEscapeURLs(boolean bool) {
        this.m_specialEscapeURLs = bool;
    }

    public void setOmitMetaTag(boolean bool) {
        this.m_omitMetaTag = bool;
    }

    @Override
    public void setOutputFormat(Properties format) {
        String value = format.getProperty("{http://xml.apache.org/xalan}use-url-escaping");
        if (value != null) {
            this.m_specialEscapeURLs = OutputPropertyUtils.getBooleanProperty("{http://xml.apache.org/xalan}use-url-escaping", format);
        }
        if ((value = format.getProperty("{http://xml.apache.org/xalan}omit-meta-tag")) != null) {
            this.m_omitMetaTag = OutputPropertyUtils.getBooleanProperty("{http://xml.apache.org/xalan}omit-meta-tag", format);
        }
        super.setOutputFormat(format);
    }

    private final boolean getSpecialEscapeURLs() {
        return this.m_specialEscapeURLs;
    }

    private final boolean getOmitMetaTag() {
        return this.m_omitMetaTag;
    }

    public static final ElemDesc getElemDesc(String name) {
        Object obj = m_elementFlags.get(name);
        if (null != obj) {
            return (ElemDesc)obj;
        }
        return m_dummy;
    }

    private ElemDesc getElemDesc2(String name) {
        Object obj = this.m_htmlInfo.get2(name);
        if (null != obj) {
            return (ElemDesc)obj;
        }
        return m_dummy;
    }

    public ToHTMLStream() {
        this.m_doIndent = true;
        this.m_charInfo = this.m_htmlcharInfo;
        this.m_prefixMap = new NamespaceMappings();
    }

    @Override
    protected void startDocumentInternal() throws SAXException {
        super.startDocumentInternal();
        this.m_needToCallStartDocument = false;
        this.m_needToOutputDocTypeDecl = true;
        this.m_startNewLine = false;
        this.setOmitXMLDeclaration(true);
    }

    private void outputDocTypeDecl(String name) throws SAXException {
        if (this.m_needToOutputDocTypeDecl) {
            String doctypeSystem = this.getDoctypeSystem();
            String doctypePublic = this.getDoctypePublic();
            if (null != doctypeSystem || null != doctypePublic) {
                Writer writer = this.m_writer;
                try {
                    writer.write("<!DOCTYPE ");
                    writer.write(name);
                    if (null != doctypePublic) {
                        writer.write(" PUBLIC \"");
                        writer.write(doctypePublic);
                        writer.write(34);
                    }
                    if (null != doctypeSystem) {
                        if (null == doctypePublic) {
                            writer.write(" SYSTEM \"");
                        } else {
                            writer.write(" \"");
                        }
                        writer.write(doctypeSystem);
                        writer.write(34);
                    }
                    writer.write(62);
                    this.outputLineSep();
                }
                catch (IOException e) {
                    throw new SAXException(e);
                }
            }
        }
        this.m_needToOutputDocTypeDecl = false;
    }

    @Override
    public final void endDocument() throws SAXException {
        this.flushPending();
        if (this.m_doIndent && !this.m_isprevtext) {
            try {
                this.outputLineSep();
            }
            catch (IOException e) {
                throw new SAXException(e);
            }
        }
        this.flushWriter();
        if (this.m_tracer != null) {
            super.fireEndDoc();
        }
    }

    @Override
    public void startElement(String namespaceURI, String localName, String name, Attributes atts) throws SAXException {
        ElemContext elemContext = this.m_elemContext;
        if (elemContext.m_startTagOpen) {
            this.closeStartTag();
            elemContext.m_startTagOpen = false;
        } else if (this.m_cdataTagOpen) {
            this.closeCDATA();
            this.m_cdataTagOpen = false;
        } else if (this.m_needToCallStartDocument) {
            this.startDocumentInternal();
            this.m_needToCallStartDocument = false;
        }
        if (this.m_needToOutputDocTypeDecl) {
            String n = name;
            if (n == null || n.length() == 0) {
                n = localName;
            }
            this.outputDocTypeDecl(n);
        }
        if (null != namespaceURI && namespaceURI.length() > 0) {
            super.startElement(namespaceURI, localName, name, atts);
            return;
        }
        try {
            ElemDesc elemDesc = this.getElemDesc2(name);
            int elemFlags = elemDesc.getFlags();
            if (this.m_doIndent) {
                boolean isBlockElement;
                boolean bl = isBlockElement = (elemFlags & 8) != 0;
                if (this.m_ispreserve) {
                    this.m_ispreserve = false;
                } else if (null != elemContext.m_elementName && (!this.m_inBlockElem || isBlockElement)) {
                    this.m_startNewLine = true;
                    this.indent();
                }
                boolean bl2 = this.m_inBlockElem = !isBlockElement;
            }
            if (atts != null) {
                this.addAttributes(atts);
            }
            this.m_isprevtext = false;
            Writer writer = this.m_writer;
            writer.write(60);
            writer.write(name);
            if (this.m_tracer != null) {
                this.firePseudoAttributes();
            }
            if ((elemFlags & 2) != 0) {
                this.m_elemContext = elemContext.push();
                this.m_elemContext.m_elementName = name;
                this.m_elemContext.m_elementDesc = elemDesc;
                return;
            }
            this.m_elemContext = elemContext = elemContext.push(namespaceURI, localName, name);
            elemContext.m_elementDesc = elemDesc;
            boolean bl = elemContext.m_isRaw = (elemFlags & 0x100) != 0;
            if ((elemFlags & 0x400000) != 0) {
                this.closeStartTag();
                elemContext.m_startTagOpen = false;
                if (!this.m_omitMetaTag) {
                    if (this.m_doIndent) {
                        this.indent();
                    }
                    writer.write("<META http-equiv=\"Content-Type\" content=\"text/html; charset=");
                    String encoding = this.getEncoding();
                    String encode = Encodings.getMimeEncoding(encoding);
                    writer.write(encode);
                    writer.write("\">");
                }
            }
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public final void endElement(String namespaceURI, String localName, String name) throws SAXException {
        if (this.m_cdataTagOpen) {
            this.closeCDATA();
        }
        if (null != namespaceURI && namespaceURI.length() > 0) {
            super.endElement(namespaceURI, localName, name);
            return;
        }
        try {
            boolean elemEmpty;
            ElemContext elemContext = this.m_elemContext;
            ElemDesc elemDesc = elemContext.m_elementDesc;
            int elemFlags = elemDesc.getFlags();
            boolean bl = elemEmpty = (elemFlags & 2) != 0;
            if (this.m_doIndent) {
                boolean isBlockElement = (elemFlags & 8) != 0;
                boolean shouldIndent = false;
                if (this.m_ispreserve) {
                    this.m_ispreserve = false;
                } else if (this.m_doIndent && (!this.m_inBlockElem || isBlockElement)) {
                    this.m_startNewLine = true;
                    shouldIndent = true;
                }
                if (!elemContext.m_startTagOpen && shouldIndent) {
                    this.indent(elemContext.m_currentElemDepth - 1);
                }
                this.m_inBlockElem = !isBlockElement;
            }
            Writer writer = this.m_writer;
            if (!elemContext.m_startTagOpen) {
                writer.write("</");
                writer.write(name);
                writer.write(62);
            } else {
                int nAttrs;
                if (this.m_tracer != null) {
                    super.fireStartElem(name);
                }
                if ((nAttrs = this.m_attributes.getLength()) > 0) {
                    this.processAttributes(this.m_writer, nAttrs);
                    this.m_attributes.clear();
                }
                if (!elemEmpty) {
                    writer.write("></");
                    writer.write(name);
                    writer.write(62);
                } else {
                    writer.write(62);
                }
            }
            if ((elemFlags & 0x200000) != 0) {
                this.m_ispreserve = true;
            }
            this.m_isprevtext = false;
            if (this.m_tracer != null) {
                super.fireEndElem(name);
            }
            if (elemEmpty) {
                this.m_elemContext = elemContext.m_prev;
                return;
            }
            if (!elemContext.m_startTagOpen && this.m_doIndent && !this.m_preserves.isEmpty()) {
                this.m_preserves.pop();
            }
            this.m_elemContext = elemContext.m_prev;
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    protected void processAttribute(Writer writer, String name, String value, ElemDesc elemDesc) throws IOException {
        writer.write(32);
        if ((value.length() == 0 || value.equalsIgnoreCase(name)) && elemDesc != null && elemDesc.isAttrFlagSet(name, 4)) {
            writer.write(name);
        } else {
            writer.write(name);
            writer.write("=\"");
            if (elemDesc != null && elemDesc.isAttrFlagSet(name, 2)) {
                this.writeAttrURI(writer, value, this.m_specialEscapeURLs);
            } else {
                this.writeAttrString(writer, value, this.getEncoding());
            }
            writer.write(34);
        }
    }

    private boolean isASCIIDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static String makeHHString(int i) {
        String s = Integer.toHexString(i).toUpperCase();
        if (s.length() == 1) {
            s = "0" + s;
        }
        return s;
    }

    private boolean isHHSign(String str) {
        boolean sign = true;
        try {
            char c = (char)Integer.parseInt(str, 16);
        }
        catch (NumberFormatException e) {
            sign = false;
        }
        return sign;
    }

    public void writeAttrURI(Writer writer, String string, boolean doURLEscaping) throws IOException {
        int end = string.length();
        if (end > this.m_attrBuff.length) {
            this.m_attrBuff = new char[end * 2 + 1];
        }
        string.getChars(0, end, this.m_attrBuff, 0);
        char[] chars = this.m_attrBuff;
        int cleanStart = 0;
        int cleanLength = 0;
        char ch = '\u0000';
        for (int i = 0; i < end; ++i) {
            ch = chars[i];
            if (ch < ' ' || ch > '~') {
                if (cleanLength > 0) {
                    writer.write(chars, cleanStart, cleanLength);
                    cleanLength = 0;
                }
                if (doURLEscaping) {
                    int high;
                    if (ch <= '\u007f') {
                        writer.write(37);
                        writer.write(ToHTMLStream.makeHHString(ch));
                    } else if (ch <= '\u07ff') {
                        high = ch >> 6 | 0xC0;
                        int low = ch & 0x3F | 0x80;
                        writer.write(37);
                        writer.write(ToHTMLStream.makeHHString(high));
                        writer.write(37);
                        writer.write(ToHTMLStream.makeHHString(low));
                    } else if (Encodings.isHighUTF16Surrogate(ch)) {
                        int highSurrogate = ch & 0x3FF;
                        int wwww = (highSurrogate & 0x3C0) >> 6;
                        int uuuuu = wwww + 1;
                        int zzzz = (highSurrogate & 0x3C) >> 2;
                        int yyyyyy = (highSurrogate & 3) << 4 & 0x30;
                        ch = chars[++i];
                        int lowSurrogate = ch & 0x3FF;
                        int xxxxxx = lowSurrogate & 0x3F;
                        int byte1 = 0xF0 | uuuuu >> 2;
                        int byte2 = 0x80 | (uuuuu & 3) << 4 & 0x30 | zzzz;
                        int byte3 = 0x80 | (yyyyyy |= (lowSurrogate & 0x3C0) >> 6);
                        int byte4 = 0x80 | xxxxxx;
                        writer.write(37);
                        writer.write(ToHTMLStream.makeHHString(byte1));
                        writer.write(37);
                        writer.write(ToHTMLStream.makeHHString(byte2));
                        writer.write(37);
                        writer.write(ToHTMLStream.makeHHString(byte3));
                        writer.write(37);
                        writer.write(ToHTMLStream.makeHHString(byte4));
                    } else {
                        high = ch >> 12 | 0xE0;
                        int middle = (ch & 0xFC0) >> 6 | 0x80;
                        int low = ch & 0x3F | 0x80;
                        writer.write(37);
                        writer.write(ToHTMLStream.makeHHString(high));
                        writer.write(37);
                        writer.write(ToHTMLStream.makeHHString(middle));
                        writer.write(37);
                        writer.write(ToHTMLStream.makeHHString(low));
                    }
                } else if (this.escapingNotNeeded(ch)) {
                    writer.write(ch);
                } else {
                    writer.write("&#");
                    writer.write(Integer.toString(ch));
                    writer.write(59);
                }
                cleanStart = i + 1;
                continue;
            }
            if (ch == '\"') {
                if (cleanLength > 0) {
                    writer.write(chars, cleanStart, cleanLength);
                    cleanLength = 0;
                }
                if (doURLEscaping) {
                    writer.write("%22");
                } else {
                    writer.write("&quot;");
                }
                cleanStart = i + 1;
                continue;
            }
            if (ch == '&') {
                if (cleanLength > 0) {
                    writer.write(chars, cleanStart, cleanLength);
                    cleanLength = 0;
                }
                writer.write("&amp;");
                cleanStart = i + 1;
                continue;
            }
            ++cleanLength;
        }
        if (cleanLength > 1) {
            if (cleanStart == 0) {
                writer.write(string);
            } else {
                writer.write(chars, cleanStart, cleanLength);
            }
        } else if (cleanLength == 1) {
            writer.write(ch);
        }
    }

    @Override
    public void writeAttrString(Writer writer, String string, String encoding) throws IOException {
        int end = string.length();
        if (end > this.m_attrBuff.length) {
            this.m_attrBuff = new char[end * 2 + 1];
        }
        string.getChars(0, end, this.m_attrBuff, 0);
        char[] chars = this.m_attrBuff;
        int cleanStart = 0;
        int cleanLength = 0;
        char ch = '\u0000';
        for (int i = 0; i < end; ++i) {
            int pos;
            ch = chars[i];
            if (this.escapingNotNeeded(ch) && !this.m_charInfo.shouldMapAttrChar(ch)) {
                ++cleanLength;
                continue;
            }
            if ('<' == ch || '>' == ch) {
                ++cleanLength;
                continue;
            }
            if ('&' == ch && i + 1 < end && '{' == chars[i + 1]) {
                ++cleanLength;
                continue;
            }
            if (cleanLength > 0) {
                writer.write(chars, cleanStart, cleanLength);
                cleanLength = 0;
            }
            if (i != (pos = this.accumDefaultEntity(writer, ch, i, chars, end, false, true))) {
                i = pos - 1;
            } else {
                String outputStringForChar;
                if (Encodings.isHighUTF16Surrogate(ch)) {
                    this.writeUTF16Surrogate(ch, chars, i, end);
                    ++i;
                }
                if (null != (outputStringForChar = this.m_charInfo.getOutputStringForChar(ch))) {
                    writer.write(outputStringForChar);
                } else if (this.escapingNotNeeded(ch)) {
                    writer.write(ch);
                } else {
                    writer.write("&#");
                    writer.write(Integer.toString(ch));
                    writer.write(59);
                }
            }
            cleanStart = i + 1;
        }
        if (cleanLength > 1) {
            if (cleanStart == 0) {
                writer.write(string);
            } else {
                writer.write(chars, cleanStart, cleanLength);
            }
        } else if (cleanLength == 1) {
            writer.write(ch);
        }
    }

    @Override
    public final void characters(char[] chars, int start, int length) throws SAXException {
        if (this.m_elemContext.m_isRaw) {
            try {
                if (this.m_elemContext.m_startTagOpen) {
                    this.closeStartTag();
                    this.m_elemContext.m_startTagOpen = false;
                }
                this.m_ispreserve = true;
                this.writeNormalizedChars(chars, start, length, false, this.m_lineSepUse);
                if (this.m_tracer != null) {
                    super.fireCharEvent(chars, start, length);
                }
                return;
            }
            catch (IOException ioe) {
                throw new SAXException(Utils.messages.createMessage("ER_OIERROR", null), ioe);
            }
        }
        super.characters(chars, start, length);
    }

    @Override
    public final void cdata(char[] ch, int start, int length) throws SAXException {
        if (null != this.m_elemContext.m_elementName && (this.m_elemContext.m_elementName.equalsIgnoreCase("SCRIPT") || this.m_elemContext.m_elementName.equalsIgnoreCase("STYLE"))) {
            try {
                if (this.m_elemContext.m_startTagOpen) {
                    this.closeStartTag();
                    this.m_elemContext.m_startTagOpen = false;
                }
                this.m_ispreserve = true;
                if (this.shouldIndent()) {
                    this.indent();
                }
                this.writeNormalizedChars(ch, start, length, true, this.m_lineSepUse);
            }
            catch (IOException ioe) {
                throw new SAXException(Utils.messages.createMessage("ER_OIERROR", null), ioe);
            }
        } else {
            super.cdata(ch, start, length);
        }
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        this.flushPending();
        if (target.equals("javax.xml.transform.disable-output-escaping")) {
            this.startNonEscaping();
        } else if (target.equals("javax.xml.transform.enable-output-escaping")) {
            this.endNonEscaping();
        } else {
            try {
                if (this.m_elemContext.m_startTagOpen) {
                    this.closeStartTag();
                    this.m_elemContext.m_startTagOpen = false;
                } else if (this.m_cdataTagOpen) {
                    this.closeCDATA();
                } else if (this.m_needToCallStartDocument) {
                    this.startDocumentInternal();
                }
                if (this.m_needToOutputDocTypeDecl) {
                    this.outputDocTypeDecl("html");
                }
                if (this.shouldIndent()) {
                    this.indent();
                }
                Writer writer = this.m_writer;
                writer.write("<?");
                writer.write(target);
                if (data.length() > 0 && !Character.isSpaceChar(data.charAt(0))) {
                    writer.write(32);
                }
                writer.write(data);
                writer.write(62);
                if (this.m_elemContext.m_currentElemDepth <= 0) {
                    this.outputLineSep();
                }
                this.m_startNewLine = true;
            }
            catch (IOException e) {
                throw new SAXException(e);
            }
        }
        if (this.m_tracer != null) {
            super.fireEscapingEvent(target, data);
        }
    }

    @Override
    public final void entityReference(String name) throws SAXException {
        try {
            Writer writer = this.m_writer;
            writer.write(38);
            writer.write(name);
            writer.write(59);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public final void endElement(String elemName) throws SAXException {
        this.endElement(null, null, elemName);
    }

    @Override
    public void processAttributes(Writer writer, int nAttrs) throws IOException, SAXException {
        for (int i = 0; i < nAttrs; ++i) {
            this.processAttribute(writer, this.m_attributes.getQName(i), this.m_attributes.getValue(i), this.m_elemContext.m_elementDesc);
        }
    }

    @Override
    protected void closeStartTag() throws SAXException {
        try {
            int nAttrs;
            if (this.m_tracer != null) {
                super.fireStartElem(this.m_elemContext.m_elementName);
            }
            if ((nAttrs = this.m_attributes.getLength()) > 0) {
                this.processAttributes(this.m_writer, nAttrs);
                this.m_attributes.clear();
            }
            this.m_writer.write(62);
            if (this.m_CdataElems != null) {
                this.m_elemContext.m_isCdataSection = this.isCdataSection();
            }
            if (this.m_doIndent) {
                this.m_isprevtext = false;
                this.m_preserves.push(this.m_ispreserve);
            }
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void namespaceAfterStartElement(String prefix, String uri) throws SAXException {
        String prefix1;
        if (this.m_elemContext.m_elementURI == null && (prefix1 = ToHTMLStream.getPrefixPart(this.m_elemContext.m_elementName)) == null && "".equals(prefix)) {
            this.m_elemContext.m_elementURI = uri;
        }
        this.startPrefixMapping(prefix, uri, false);
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        this.m_inDTD = true;
        super.startDTD(name, publicId, systemId);
    }

    @Override
    public void endDTD() throws SAXException {
        this.m_inDTD = false;
    }

    @Override
    public void attributeDecl(String eName, String aName, String type, String valueDefault, String value) throws SAXException {
    }

    @Override
    public void elementDecl(String name, String model) throws SAXException {
    }

    @Override
    public void internalEntityDecl(String name, String value) throws SAXException {
    }

    @Override
    public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {
    }

    @Override
    public void addUniqueAttribute(String name, String value, int flags) throws SAXException {
        try {
            Writer writer = this.m_writer;
            if ((flags & 1) > 0 && this.m_htmlcharInfo.onlyQuotAmpLtGt) {
                writer.write(32);
                writer.write(name);
                writer.write("=\"");
                writer.write(value);
                writer.write(34);
            } else if ((flags & 2) > 0 && (value.length() == 0 || value.equalsIgnoreCase(name))) {
                writer.write(32);
                writer.write(name);
            } else {
                writer.write(32);
                writer.write(name);
                writer.write("=\"");
                if ((flags & 4) > 0) {
                    this.writeAttrURI(writer, value, this.m_specialEscapeURLs);
                } else {
                    this.writeAttrString(writer, value, this.getEncoding());
                }
                writer.write(34);
            }
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        if (this.m_inDTD) {
            return;
        }
        if (this.m_elemContext.m_startTagOpen) {
            this.closeStartTag();
            this.m_elemContext.m_startTagOpen = false;
        } else if (this.m_cdataTagOpen) {
            this.closeCDATA();
        } else if (this.m_needToCallStartDocument) {
            this.startDocumentInternal();
        }
        if (this.m_needToOutputDocTypeDecl) {
            this.outputDocTypeDecl("html");
        }
        super.comment(ch, start, length);
    }

    @Override
    public boolean reset() {
        boolean ret = super.reset();
        if (!ret) {
            return false;
        }
        this.resetToHTMLStream();
        return true;
    }

    private void resetToHTMLStream() {
        this.m_inBlockElem = false;
        this.m_inDTD = false;
        this.m_omitMetaTag = false;
        this.m_specialEscapeURLs = true;
    }

    static {
        ToHTMLStream.initTagReference(m_elementFlags);
        m_dummy = new ElemDesc(8);
    }

    static class Trie {
        public static final int ALPHA_SIZE = 128;
        final Node m_Root;
        private char[] m_charBuffer = new char[0];
        private final boolean m_lowerCaseOnly;

        public Trie() {
            this.m_Root = new Node();
            this.m_lowerCaseOnly = false;
        }

        public Trie(boolean lowerCaseOnly) {
            this.m_Root = new Node();
            this.m_lowerCaseOnly = lowerCaseOnly;
        }

        public Object put(String key, Object value) {
            int len = key.length();
            if (len > this.m_charBuffer.length) {
                this.m_charBuffer = new char[len];
            }
            Node node = this.m_Root;
            for (int i = 0; i < len; ++i) {
                Node nextNode = node.m_nextChar[Character.toLowerCase(key.charAt(i))];
                if (nextNode != null) {
                    node = nextNode;
                    continue;
                }
                while (i < len) {
                    Node newNode = new Node();
                    if (this.m_lowerCaseOnly) {
                        node.m_nextChar[Character.toLowerCase((char)key.charAt((int)i))] = newNode;
                    } else {
                        node.m_nextChar[Character.toUpperCase((char)key.charAt((int)i))] = newNode;
                        node.m_nextChar[Character.toLowerCase((char)key.charAt((int)i))] = newNode;
                    }
                    node = newNode;
                    ++i;
                }
                break;
            }
            Object ret = node.m_Value;
            node.m_Value = value;
            return ret;
        }

        public Object get(String key) {
            int len = key.length();
            if (this.m_charBuffer.length < len) {
                return null;
            }
            Node node = this.m_Root;
            switch (len) {
                case 0: {
                    return null;
                }
                case 1: {
                    char ch = key.charAt(0);
                    if (ch < '\u0080' && (node = node.m_nextChar[ch]) != null) {
                        return node.m_Value;
                    }
                    return null;
                }
            }
            for (int i = 0; i < len; ++i) {
                char ch = key.charAt(i);
                if ('\u0080' <= ch) {
                    return null;
                }
                node = node.m_nextChar[ch];
                if (node != null) continue;
                return null;
            }
            return node.m_Value;
        }

        public Trie(Trie existingTrie) {
            this.m_Root = existingTrie.m_Root;
            this.m_lowerCaseOnly = existingTrie.m_lowerCaseOnly;
            int max = existingTrie.getLongestKeyLength();
            this.m_charBuffer = new char[max];
        }

        public Object get2(String key) {
            int len = key.length();
            if (this.m_charBuffer.length < len) {
                return null;
            }
            Node node = this.m_Root;
            switch (len) {
                case 0: {
                    return null;
                }
                case 1: {
                    char ch = key.charAt(0);
                    if (ch < '\u0080' && (node = node.m_nextChar[ch]) != null) {
                        return node.m_Value;
                    }
                    return null;
                }
            }
            key.getChars(0, len, this.m_charBuffer, 0);
            for (int i = 0; i < len; ++i) {
                char ch = this.m_charBuffer[i];
                if ('\u0080' <= ch) {
                    return null;
                }
                node = node.m_nextChar[ch];
                if (node != null) continue;
                return null;
            }
            return node.m_Value;
        }

        public int getLongestKeyLength() {
            return this.m_charBuffer.length;
        }

        private static class Node {
            final Node[] m_nextChar = new Node[128];
            Object m_Value = null;

            Node() {
            }
        }
    }
}

