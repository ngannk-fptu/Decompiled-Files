/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import java.io.PrintWriter;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import org.w3c.tidy.AttVal;
import org.w3c.tidy.AttributeTable;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.EncodingUtils;
import org.w3c.tidy.EntityTable;
import org.w3c.tidy.IStack;
import org.w3c.tidy.Node;
import org.w3c.tidy.Report;
import org.w3c.tidy.StreamIn;
import org.w3c.tidy.Style;
import org.w3c.tidy.TidyUtils;

public class Lexer {
    public static final short IGNORE_WHITESPACE = 0;
    public static final short MIXED_CONTENT = 1;
    public static final short PREFORMATTED = 2;
    public static final short IGNORE_MARKUP = 3;
    private static final String VOYAGER_LOOSE = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd";
    private static final String VOYAGER_STRICT = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
    private static final String VOYAGER_FRAMESET = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd";
    private static final String VOYAGER_11 = "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd";
    private static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";
    private static final W3CVersionInfo[] W3CVERSION = new W3CVersionInfo[]{new W3CVersionInfo("HTML 4.01", "XHTML 1.0 Strict", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd", 4), new W3CVersionInfo("HTML 4.01 Transitional", "XHTML 1.0 Transitional", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd", 8), new W3CVersionInfo("HTML 4.01 Frameset", "XHTML 1.0 Frameset", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd", 16), new W3CVersionInfo("HTML 4.0", "XHTML 1.0 Strict", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd", 4), new W3CVersionInfo("HTML 4.0 Transitional", "XHTML 1.0 Transitional", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd", 8), new W3CVersionInfo("HTML 4.0 Frameset", "XHTML 1.0 Frameset", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd", 16), new W3CVersionInfo("HTML 3.2", "XHTML 1.0 Transitional", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd", 2), new W3CVersionInfo("HTML 3.2 Final", "XHTML 1.0 Transitional", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd", 2), new W3CVersionInfo("HTML 3.2 Draft", "XHTML 1.0 Transitional", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd", 2), new W3CVersionInfo("HTML 2.0", "XHTML 1.0 Strict", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd", 1), new W3CVersionInfo("HTML 4.01", "XHTML 1.1", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd", 1024)};
    private static final short LEX_CONTENT = 0;
    private static final short LEX_GT = 1;
    private static final short LEX_ENDTAG = 2;
    private static final short LEX_STARTTAG = 3;
    private static final short LEX_COMMENT = 4;
    private static final short LEX_DOCTYPE = 5;
    private static final short LEX_PROCINSTR = 6;
    private static final short LEX_CDATA = 8;
    private static final short LEX_SECTION = 9;
    private static final short LEX_ASP = 10;
    private static final short LEX_JSTE = 11;
    private static final short LEX_PHP = 12;
    private static final short LEX_XMLDECL = 13;
    protected StreamIn in;
    protected PrintWriter errout;
    protected short badAccess;
    protected short badLayout;
    protected short badChars;
    protected short badForm;
    protected short warnings;
    protected short errors;
    protected int lines;
    protected int columns;
    protected boolean waswhite;
    protected boolean pushed;
    protected boolean insertspace;
    protected boolean excludeBlocks;
    protected boolean exiled;
    protected boolean isvoyager;
    protected short versions;
    protected int doctype;
    protected boolean badDoctype;
    protected int txtstart;
    protected int txtend;
    protected short state;
    protected Node token;
    protected byte[] lexbuf;
    protected int lexlength;
    protected int lexsize;
    protected Node inode;
    protected int insert;
    protected Stack istack;
    protected int istackbase;
    protected Style styles;
    protected Configuration configuration;
    protected boolean seenEndBody;
    protected boolean seenEndHtml;
    protected Report report;
    protected Node root;
    private List nodeList;

    public Lexer(StreamIn in, Configuration configuration, Report report) {
        this.report = report;
        this.in = in;
        this.lines = 1;
        this.columns = 1;
        this.state = 0;
        this.versions = (short)3551;
        this.doctype = 0;
        this.insert = -1;
        this.istack = new Stack();
        this.configuration = configuration;
        this.nodeList = new Vector();
    }

    public Node newNode() {
        Node node = new Node();
        this.nodeList.add(node);
        return node;
    }

    public Node newNode(short type, byte[] textarray, int start, int end) {
        Node node = new Node(type, textarray, start, end);
        this.nodeList.add(node);
        return node;
    }

    public Node newNode(short type, byte[] textarray, int start, int end, String element) {
        Node node = new Node(type, textarray, start, end, element, this.configuration.tt);
        this.nodeList.add(node);
        return node;
    }

    public Node cloneNode(Node node) {
        Node cnode = (Node)node.clone();
        this.nodeList.add(cnode);
        AttVal att = cnode.attributes;
        while (att != null) {
            if (att.asp != null) {
                this.nodeList.add(att.asp);
            }
            if (att.php != null) {
                this.nodeList.add(att.php);
            }
            att = att.next;
        }
        return cnode;
    }

    public AttVal cloneAttributes(AttVal attrs) {
        AttVal cattrs;
        AttVal att = cattrs = (AttVal)attrs.clone();
        while (att != null) {
            if (att.asp != null) {
                this.nodeList.add(att.asp);
            }
            if (att.php != null) {
                this.nodeList.add(att.php);
            }
            att = att.next;
        }
        return cattrs;
    }

    protected void updateNodeTextArrays(byte[] oldtextarray, byte[] newtextarray) {
        for (int i = 0; i < this.nodeList.size(); ++i) {
            Node node = (Node)this.nodeList.get(i);
            if (node.textarray != oldtextarray) continue;
            node.textarray = newtextarray;
        }
    }

    public Node newLineNode() {
        Node node = this.newNode();
        node.textarray = this.lexbuf;
        node.start = this.lexsize;
        this.addCharToLexer(10);
        node.end = this.lexsize;
        return node;
    }

    public boolean endOfInput() {
        return this.in.isEndOfStream();
    }

    public void addByte(int c) {
        if (this.lexsize + 1 >= this.lexlength) {
            while (this.lexsize + 1 >= this.lexlength) {
                if (this.lexlength == 0) {
                    this.lexlength = 8192;
                    continue;
                }
                this.lexlength *= 2;
            }
            byte[] temp = this.lexbuf;
            this.lexbuf = new byte[this.lexlength];
            if (temp != null) {
                System.arraycopy(temp, 0, this.lexbuf, 0, temp.length);
                this.updateNodeTextArrays(temp, this.lexbuf);
            }
        }
        this.lexbuf[this.lexsize++] = (byte)c;
        this.lexbuf[this.lexsize] = 0;
    }

    public void changeChar(byte c) {
        if (this.lexsize > 0) {
            this.lexbuf[this.lexsize - 1] = c;
        }
    }

    public void addCharToLexer(int c) {
        if (!(!this.configuration.xmlOut && !this.configuration.xHTML || c >= 32 && c <= 55295 || c == 9 || c == 10 || c == 13 || c >= 57344 && c <= 65533 || c >= 65536 && c <= 0x10FFFF)) {
            return;
        }
        int i = 0;
        byte[] buf = new byte[10];
        int[] count = new int[]{0};
        boolean err = EncodingUtils.encodeCharToUTF8Bytes(c, buf, null, count);
        if (err) {
            buf[0] = -17;
            buf[1] = -65;
            buf[2] = -67;
            count[0] = 3;
        }
        for (i = 0; i < count[0]; ++i) {
            this.addByte(buf[i]);
        }
    }

    public void addStringToLexer(String str) {
        for (int i = 0; i < str.length(); ++i) {
            this.addCharToLexer(str.charAt(i));
        }
    }

    public void parseEntity(short mode) {
        int ch;
        String str;
        int c;
        boolean first = true;
        boolean semicolon = false;
        int start = this.lexsize - 1;
        int startcol = this.in.getCurcol() - 1;
        while ((c = this.in.readChar()) != -1) {
            if (c == 59) {
                semicolon = true;
                break;
            }
            if (first && c == 35) {
                if (!this.configuration.ncr || "BIG5".equals(this.configuration.getInCharEncodingName()) || "SHIFTJIS".equals(this.configuration.getInCharEncodingName())) {
                    this.in.ungetChar(c);
                    return;
                }
                this.addCharToLexer(c);
                first = false;
                continue;
            }
            first = false;
            if (TidyUtils.isNamechar((char)c)) {
                this.addCharToLexer(c);
                continue;
            }
            this.in.ungetChar(c);
            break;
        }
        if ("&apos".equals(str = TidyUtils.getString(this.lexbuf, start, this.lexsize - start)) && !this.configuration.xmlOut && !this.isvoyager && !this.configuration.xHTML) {
            this.report.entityError(this, (short)5, str, 39);
        }
        if ((ch = EntityTable.getDefaultEntityTable().entityCode(str)) <= 0 || ch >= 256 && c != 59) {
            this.lines = this.in.getCurline();
            this.columns = startcol;
            if (this.lexsize > start + 1) {
                if (ch >= 128 && ch <= 159) {
                    int replaceMode;
                    int c1 = 0;
                    if ("WIN1252".equals(this.configuration.replacementCharEncoding)) {
                        c1 = EncodingUtils.decodeWin1252(ch);
                    } else if ("MACROMAN".equals(this.configuration.replacementCharEncoding)) {
                        c1 = EncodingUtils.decodeMacRoman(ch);
                    }
                    int n = replaceMode = c1 != 0 ? 0 : 1;
                    if (c != 59) {
                        this.report.entityError(this, (short)2, str, c);
                    }
                    this.report.encodingError(this, (short)(0x52 | replaceMode), ch);
                    if (c1 != 0) {
                        this.lexsize = start;
                        this.addCharToLexer(c1);
                        semicolon = false;
                    } else {
                        this.lexsize = start;
                        semicolon = false;
                    }
                } else {
                    this.report.entityError(this, (short)3, str, ch);
                }
                if (semicolon) {
                    this.addCharToLexer(59);
                }
            } else {
                this.report.entityError(this, (short)4, str, ch);
            }
        } else {
            if (c != 59) {
                this.lines = this.in.getCurline();
                this.columns = startcol;
                this.report.entityError(this, (short)1, str, c);
            }
            this.lexsize = start;
            if (ch == 160 && TidyUtils.toBoolean(mode & 2)) {
                ch = 32;
            }
            this.addCharToLexer(ch);
            if (ch == 38 && !this.configuration.quoteAmpersand) {
                this.addCharToLexer(97);
                this.addCharToLexer(109);
                this.addCharToLexer(112);
                this.addCharToLexer(59);
            }
        }
    }

    public char parseTagName() {
        int c = this.lexbuf[this.txtstart];
        if (!this.configuration.xmlTags && TidyUtils.isUpper((char)c)) {
            c = TidyUtils.toLower((char)c);
            this.lexbuf[this.txtstart] = (byte)c;
        }
        while ((c = this.in.readChar()) != -1 && TidyUtils.isNamechar((char)c)) {
            if (!this.configuration.xmlTags && TidyUtils.isUpper((char)c)) {
                c = TidyUtils.toLower((char)c);
            }
            this.addCharToLexer(c);
        }
        this.txtend = this.lexsize;
        return (char)c;
    }

    public void addStringLiteral(String str) {
        int len = str.length();
        for (int i = 0; i < len; ++i) {
            this.addCharToLexer(str.charAt(i));
        }
    }

    void addStringLiteralLen(String str, int len) {
        int strlen = str.length();
        if (strlen < len) {
            len = strlen;
        }
        for (int i = 0; i < len; ++i) {
            this.addCharToLexer(str.charAt(i));
        }
    }

    public short htmlVersion() {
        if (TidyUtils.toBoolean(this.versions & 1)) {
            return 1;
        }
        if (!(this.configuration.xmlOut | this.configuration.xmlTags | this.isvoyager) && TidyUtils.toBoolean(this.versions & 2)) {
            return 2;
        }
        if (TidyUtils.toBoolean(this.versions & 0x400)) {
            return 1024;
        }
        if (TidyUtils.toBoolean(this.versions & 4)) {
            return 4;
        }
        if (TidyUtils.toBoolean(this.versions & 8)) {
            return 8;
        }
        if (TidyUtils.toBoolean(this.versions & 0x10)) {
            return 16;
        }
        return 0;
    }

    public String htmlVersionName() {
        short guessed = this.apparentVersion();
        for (int j = 0; j < W3CVERSION.length; ++j) {
            if (guessed != Lexer.W3CVERSION[j].code) continue;
            if (this.isvoyager) {
                return Lexer.W3CVERSION[j].voyagerName;
            }
            return Lexer.W3CVERSION[j].name;
        }
        return null;
    }

    public boolean addGenerator(Node root) {
        Node head = root.findHEAD(this.configuration.tt);
        if (head != null) {
            String meta = "HTML Tidy for Java (vers. " + Report.RELEASE_DATE_STRING + "), see www.w3.org";
            Node node = head.content;
            while (node != null) {
                AttVal attval;
                if (node.tag == this.configuration.tt.tagMeta && (attval = node.getAttrByName("name")) != null && attval.value != null && "generator".equalsIgnoreCase(attval.value) && (attval = node.getAttrByName("content")) != null && attval.value != null && attval.value.length() >= 9 && "HTML Tidy".equalsIgnoreCase(attval.value.substring(0, 9))) {
                    attval.value = meta;
                    return false;
                }
                node = node.next;
            }
            node = this.inferredTag("meta");
            node.addAttribute("content", meta);
            node.addAttribute("name", "generator");
            head.insertNodeAtStart(node);
            return true;
        }
        return false;
    }

    public boolean checkDocTypeKeyWords(Node doctype) {
        int len = doctype.end - doctype.start;
        String s = TidyUtils.getString(this.lexbuf, doctype.start, len);
        return !TidyUtils.findBadSubString("SYSTEM", s, len) && !TidyUtils.findBadSubString("PUBLIC", s, len) && !TidyUtils.findBadSubString("//DTD", s, len) && !TidyUtils.findBadSubString("//W3C", s, len) && !TidyUtils.findBadSubString("//EN", s, len);
    }

    public short findGivenVersion(Node doctype) {
        String str1 = TidyUtils.getString(this.lexbuf, doctype.start, 5);
        if (!"html ".equalsIgnoreCase(str1)) {
            return 0;
        }
        if (!this.checkDocTypeKeyWords(doctype)) {
            this.report.warning(this, doctype, null, (short)37);
        }
        if ("SYSTEM ".equalsIgnoreCase(str1 = TidyUtils.getString(this.lexbuf, doctype.start + 5, 7))) {
            if (!str1.substring(0, 6).equals("SYSTEM")) {
                System.arraycopy(TidyUtils.getBytes("SYSTEM"), 0, this.lexbuf, doctype.start + 5, 6);
            }
            return 0;
        }
        if ("PUBLIC ".equalsIgnoreCase(str1)) {
            if (!str1.substring(0, 6).equals("PUBLIC")) {
                System.arraycopy(TidyUtils.getBytes("PUBLIC "), 0, this.lexbuf, doctype.start + 5, 6);
            }
        } else {
            this.badDoctype = true;
        }
        for (int i = doctype.start; i < doctype.end; ++i) {
            int j;
            if (this.lexbuf[i] != 34) continue;
            str1 = TidyUtils.getString(this.lexbuf, i + 1, 12);
            String str2 = TidyUtils.getString(this.lexbuf, i + 1, 13);
            if (str1.equals("-//W3C//DTD ")) {
                int j2;
                for (j2 = i + 13; j2 < doctype.end && this.lexbuf[j2] != 47; ++j2) {
                }
                int len = j2 - i - 13;
                String p = TidyUtils.getString(this.lexbuf, i + 13, len);
                for (j2 = 1; j2 < W3CVERSION.length; ++j2) {
                    String s = Lexer.W3CVERSION[j2].name;
                    if (len != s.length() || !s.equals(p)) continue;
                    return Lexer.W3CVERSION[j2].code;
                }
                break;
            }
            if (!str2.equals("-//IETF//DTD ")) break;
            for (j = i + 14; j < doctype.end && this.lexbuf[j] != 47; ++j) {
            }
            int len = j - i - 14;
            String p = TidyUtils.getString(this.lexbuf, i + 14, len);
            String s = Lexer.W3CVERSION[0].name;
            if (len != s.length() || !s.equals(p)) break;
            return Lexer.W3CVERSION[0].code;
        }
        return 0;
    }

    public void fixHTMLNameSpace(Node root, String profile) {
        Node node = root.content;
        while (node != null && node.tag != this.configuration.tt.tagHtml) {
            node = node.next;
        }
        if (node != null) {
            AttVal attr = node.attributes;
            while (attr != null && !attr.attribute.equals("xmlns")) {
                attr = attr.next;
            }
            if (attr != null) {
                if (!attr.value.equals(profile)) {
                    this.report.warning(this, node, null, (short)33);
                    attr.value = profile;
                }
            } else {
                attr = new AttVal(node.attributes, null, 34, "xmlns", profile);
                attr.dict = AttributeTable.getDefaultAttributeTable().findAttribute(attr);
                node.attributes = attr;
            }
        }
    }

    Node newXhtmlDocTypeNode(Node root) {
        Node html = root.findHTML(this.configuration.tt);
        if (html == null) {
            return null;
        }
        Node newdoctype = this.newNode();
        newdoctype.setType((short)1);
        newdoctype.next = html;
        newdoctype.parent = root;
        newdoctype.prev = null;
        if (html == root.content) {
            root.content.prev = newdoctype;
            root.content = newdoctype;
            newdoctype.prev = null;
        } else {
            newdoctype.prev = html.prev;
            newdoctype.prev.next = newdoctype;
        }
        html.prev = newdoctype;
        return newdoctype;
    }

    public boolean setXHTMLDocType(Node root) {
        String fpi = " ";
        String sysid = "";
        String namespace = XHTML_NAMESPACE;
        String dtdsub = null;
        int dtdlen = 0;
        Node doctype = root.findDocType();
        this.fixHTMLNameSpace(root, namespace);
        if (this.configuration.docTypeMode == 0) {
            if (doctype != null) {
                Node.discardElement(doctype);
            }
            return true;
        }
        if (this.configuration.docTypeMode == 1) {
            if (TidyUtils.toBoolean(this.versions & 4)) {
                fpi = "-//W3C//DTD XHTML 1.0 Strict//EN";
                sysid = VOYAGER_STRICT;
            } else if (TidyUtils.toBoolean(this.versions & 0x10)) {
                fpi = "-//W3C//DTD XHTML 1.0 Frameset//EN";
                sysid = VOYAGER_FRAMESET;
            } else if (TidyUtils.toBoolean(this.versions & 0x1A)) {
                fpi = "-//W3C//DTD XHTML 1.0 Transitional//EN";
                sysid = VOYAGER_LOOSE;
            } else if (TidyUtils.toBoolean(this.versions & 0x400)) {
                fpi = "-//W3C//DTD XHTML 1.1//EN";
                sysid = VOYAGER_11;
            } else {
                fpi = null;
                sysid = "";
                if (doctype != null) {
                    Node.discardElement(doctype);
                }
            }
        } else if (this.configuration.docTypeMode == 2) {
            fpi = "-//W3C//DTD XHTML 1.0 Strict//EN";
            sysid = VOYAGER_STRICT;
        } else if (this.configuration.docTypeMode == 3) {
            fpi = "-//W3C//DTD XHTML 1.0 Transitional//EN";
            sysid = VOYAGER_LOOSE;
        }
        if (this.configuration.docTypeMode == 4 && this.configuration.docTypeStr != null) {
            fpi = this.configuration.docTypeStr;
            sysid = "";
        }
        if (fpi == null) {
            return false;
        }
        if (doctype != null) {
            int dtdend;
            int len;
            String start;
            int dtdbeg;
            if ((this.configuration.xHTML || this.configuration.xmlOut) && (dtdbeg = (start = TidyUtils.getString(this.lexbuf, doctype.start, len = doctype.end - doctype.start + 1)).indexOf(91)) >= 0 && (dtdend = start.substring(dtdbeg).indexOf(93)) >= 0) {
                dtdlen = dtdend + 1;
                dtdsub = start.substring(dtdbeg);
            }
        } else {
            doctype = this.newXhtmlDocTypeNode(root);
            if (doctype == null) {
                return false;
            }
        }
        this.txtstart = this.lexsize;
        this.txtend = this.lexsize;
        this.addStringLiteral("html PUBLIC ");
        if (fpi.charAt(0) == '\"') {
            this.addStringLiteral(fpi);
        } else {
            this.addStringLiteral("\"");
            this.addStringLiteral(fpi);
            this.addStringLiteral("\"");
        }
        if (this.configuration.wraplen != 0 && sysid.length() + 6 >= this.configuration.wraplen) {
            this.addStringLiteral("\n\"");
        } else {
            this.addStringLiteral(" \"");
        }
        this.addStringLiteral(sysid);
        this.addStringLiteral("\"");
        if (dtdlen > 0 && dtdsub != null) {
            this.addCharToLexer(32);
            this.addStringLiteralLen(dtdsub, dtdlen);
        }
        this.txtend = this.lexsize;
        int length = this.txtend - this.txtstart;
        doctype.textarray = new byte[length];
        System.arraycopy(this.lexbuf, this.txtstart, doctype.textarray, 0, length);
        doctype.start = 0;
        doctype.end = length;
        return false;
    }

    public short apparentVersion() {
        switch (this.doctype) {
            case 0: {
                return this.htmlVersion();
            }
            case 1: {
                if (!TidyUtils.toBoolean(this.versions & 1)) break;
                return 1;
            }
            case 2: {
                if (!TidyUtils.toBoolean(this.versions & 2)) break;
                return 2;
            }
            case 4: {
                if (!TidyUtils.toBoolean(this.versions & 4)) break;
                return 4;
            }
            case 8: {
                if (!TidyUtils.toBoolean(this.versions & 8)) break;
                return 8;
            }
            case 16: {
                if (!TidyUtils.toBoolean(this.versions & 0x10)) break;
                return 16;
            }
            case 1024: {
                if (!TidyUtils.toBoolean(this.versions & 0x400)) break;
                return 1024;
            }
        }
        this.lines = 1;
        this.columns = 1;
        this.report.warning(this, null, null, (short)28);
        return this.htmlVersion();
    }

    public boolean fixDocType(Node root) {
        short guessed = 4;
        if (this.badDoctype) {
            this.report.warning(this, null, null, (short)35);
        }
        Node doctype = root.findDocType();
        if (this.configuration.docTypeMode == 0) {
            if (doctype != null) {
                Node.discardElement(doctype);
            }
            return true;
        }
        if (this.configuration.xmlOut) {
            return true;
        }
        if (this.configuration.docTypeMode == 2) {
            Node.discardElement(doctype);
            doctype = null;
            guessed = 4;
        } else if (this.configuration.docTypeMode == 3) {
            Node.discardElement(doctype);
            doctype = null;
            guessed = 8;
        } else if (this.configuration.docTypeMode == 1) {
            if (doctype != null) {
                if (this.doctype == 0) {
                    return false;
                }
                switch (this.doctype) {
                    case 0: {
                        return false;
                    }
                    case 1: {
                        if (!TidyUtils.toBoolean(this.versions & 1)) break;
                        return true;
                    }
                    case 2: {
                        if (!TidyUtils.toBoolean(this.versions & 2)) break;
                        return true;
                    }
                    case 4: {
                        if (!TidyUtils.toBoolean(this.versions & 4)) break;
                        return true;
                    }
                    case 8: {
                        if (!TidyUtils.toBoolean(this.versions & 8)) break;
                        return true;
                    }
                    case 16: {
                        if (!TidyUtils.toBoolean(this.versions & 0x10)) break;
                        return true;
                    }
                    case 1024: {
                        if (!TidyUtils.toBoolean(this.versions & 0x400)) break;
                        return true;
                    }
                }
            }
            guessed = this.htmlVersion();
        }
        if (guessed == 0) {
            return false;
        }
        if (this.configuration.xmlOut || this.configuration.xmlTags || this.isvoyager) {
            if (doctype != null) {
                Node.discardElement(doctype);
            }
            this.fixHTMLNameSpace(root, XHTML_NAMESPACE);
        }
        if (doctype == null && (doctype = this.newXhtmlDocTypeNode(root)) == null) {
            return false;
        }
        this.txtstart = this.lexsize;
        this.txtend = this.lexsize;
        this.addStringLiteral("html PUBLIC ");
        if (this.configuration.docTypeMode == 4 && this.configuration.docTypeStr != null && this.configuration.docTypeStr.length() > 0) {
            if (this.configuration.docTypeStr.charAt(0) == '\"') {
                this.addStringLiteral(this.configuration.docTypeStr);
            } else {
                this.addStringLiteral("\"");
                this.addStringLiteral(this.configuration.docTypeStr);
                this.addStringLiteral("\"");
            }
        } else if (guessed == 1) {
            this.addStringLiteral("\"-//IETF//DTD HTML 2.0//EN\"");
        } else {
            this.addStringLiteral("\"-//W3C//DTD ");
            for (int i = 0; i < W3CVERSION.length; ++i) {
                if (guessed != Lexer.W3CVERSION[i].code) continue;
                this.addStringLiteral(Lexer.W3CVERSION[i].name);
                break;
            }
            this.addStringLiteral("//EN\"");
        }
        this.txtend = this.lexsize;
        int length = this.txtend - this.txtstart;
        doctype.textarray = new byte[length];
        System.arraycopy(this.lexbuf, this.txtstart, doctype.textarray, 0, length);
        doctype.start = 0;
        doctype.end = length;
        return true;
    }

    public boolean fixXmlDecl(Node root) {
        Node xml;
        if (root.content != null && root.content.type == 13) {
            xml = root.content;
        } else {
            xml = this.newNode((short)13, this.lexbuf, 0, 0);
            xml.next = root.content;
            if (root.content != null) {
                root.content.prev = xml;
                xml.next = root.content;
            }
            root.content = xml;
        }
        AttVal version = xml.getAttrByName("version");
        AttVal encoding = xml.getAttrByName("encoding");
        if (encoding == null && !"UTF8".equals(this.configuration.getOutCharEncodingName())) {
            if ("ISO8859_1".equals(this.configuration.getOutCharEncodingName())) {
                xml.addAttribute("encoding", "iso-8859-1");
            }
            if ("ISO2022".equals(this.configuration.getOutCharEncodingName())) {
                xml.addAttribute("encoding", "iso-2022");
            }
        }
        if (version == null) {
            xml.addAttribute("version", "1.0");
        }
        return true;
    }

    public Node inferredTag(String name) {
        Node node = this.newNode((short)5, this.lexbuf, this.txtstart, this.txtend, name);
        node.implicit = true;
        return node;
    }

    public Node getCDATA(Node container) {
        int c;
        int qt = 0;
        int esc = 0;
        boolean endtag = false;
        boolean begtag = false;
        if (container.isJavaScript()) {
            esc = 92;
        }
        this.lines = this.in.getCurline();
        this.columns = this.in.getCurcol();
        this.waswhite = false;
        this.txtstart = this.lexsize;
        this.txtend = this.lexsize;
        int lastc = 0;
        int start = -1;
        while ((c = this.in.readChar()) != -1) {
            int len;
            if (qt > 0) {
                if (!(c != 13 && c != 10 && c != qt || TidyUtils.toBoolean(esc) && lastc == esc)) {
                    qt = 0;
                } else if (c == 47 && lastc == 60) {
                    start = this.lexsize + 1;
                } else if (c == 62 && start >= 0) {
                    len = this.lexsize - start;
                    this.lines = this.in.getCurline();
                    this.columns = this.in.getCurcol() - 3;
                    this.report.warning(this, null, null, (short)32);
                    if (TidyUtils.toBoolean(esc)) {
                        for (int i = this.lexsize; i > start - 1; --i) {
                            this.lexbuf[i] = this.lexbuf[i - 1];
                        }
                        this.lexbuf[start - 1] = (byte)esc;
                    }
                    start = -1;
                }
            } else if (TidyUtils.isQuote(c) && (!TidyUtils.toBoolean(esc) || lastc != esc)) {
                qt = c;
            } else if (c == 60) {
                start = this.lexsize + 1;
                endtag = false;
                begtag = true;
            } else if (c == 33 && lastc == 60) {
                start = -1;
                endtag = false;
                begtag = false;
            } else if (c == 47 && lastc == 60) {
                start = this.lexsize + 1;
                endtag = true;
                begtag = false;
            } else {
                if (c == 62 && start >= 0) {
                    String str;
                    int decr = 2;
                    if (endtag && (len = this.lexsize - start) == container.element.length() && container.element.equalsIgnoreCase(str = TidyUtils.getString(this.lexbuf, start, len))) {
                        this.txtend = start - decr;
                        this.lexsize = start - decr;
                        break;
                    }
                    this.lines = this.in.getCurline();
                    this.columns = this.in.getCurcol() - 3;
                    this.report.warning(this, null, null, (short)32);
                    if (begtag) {
                        decr = 1;
                    }
                    this.txtend = start - decr;
                    this.lexsize = start - decr;
                    break;
                }
                if (c == 13) {
                    if (begtag || endtag) continue;
                    c = this.in.readChar();
                    if (c != 10) {
                        this.in.ungetChar(c);
                    }
                    c = 10;
                } else if (!(c != 10 && c != 9 && c != 32 || !begtag && !endtag)) continue;
            }
            this.addCharToLexer(c);
            this.txtend = ++this.lexsize;
            lastc = c;
        }
        if (c == -1) {
            this.report.warning(this, container, null, (short)6);
        }
        if (this.txtend > this.txtstart) {
            this.token = this.newNode((short)4, this.lexbuf, this.txtstart, this.txtend);
            return this.token;
        }
        return null;
    }

    public void ungetToken() {
        this.pushed = true;
    }

    /*
     * Unable to fully structure code
     */
    public Node getToken(short mode) {
        c = 0;
        badcomment = 0;
        isempty = new boolean[1];
        inDTDSubset = false;
        attributes = null;
        if (this.pushed && (this.token.type != 4 || this.insert == -1 && this.inode == null)) {
            this.pushed = false;
            return this.token;
        }
        if (this.insert != -1 || this.inode != null) {
            return this.insertedToken();
        }
        this.lines = this.in.getCurline();
        this.columns = this.in.getCurcol();
        this.waswhite = false;
        this.txtstart = this.lexsize;
        this.txtend = this.lexsize;
        block15: while ((c = this.in.readChar()) != -1) {
            if (this.insertspace && mode != 0) {
                this.addCharToLexer(32);
            }
            if (this.insertspace && !TidyUtils.toBoolean(mode & 0)) {
                this.waswhite = true;
                this.insertspace = false;
            }
            if (c == 13) {
                c = this.in.readChar();
                if (c != 10) {
                    this.in.ungetChar(c);
                }
                c = 10;
            }
            this.addCharToLexer(c);
            switch (this.state) {
                case 0: {
                    if (TidyUtils.isWhite((char)c) && mode == 0 && this.lexsize == this.txtstart + 1) {
                        --this.lexsize;
                        this.waswhite = false;
                        this.lines = this.in.getCurline();
                        this.columns = this.in.getCurcol();
                        continue block15;
                    }
                    if (c == 60) {
                        this.state = 1;
                        continue block15;
                    }
                    if (TidyUtils.isWhite((char)c)) {
                        if (this.waswhite) {
                            if (mode == 2 || mode == 3) continue block15;
                            --this.lexsize;
                            this.lines = this.in.getCurline();
                            this.columns = this.in.getCurcol();
                            continue block15;
                        }
                        this.waswhite = true;
                        if (mode == 2 || mode == 3 || c == 32) continue block15;
                        this.changeChar((byte)32);
                        continue block15;
                    }
                    if (c == 38 && mode != 3) {
                        this.parseEntity(mode);
                    }
                    if (mode == 0) {
                        mode = 1;
                    }
                    this.waswhite = false;
                    continue block15;
                }
                case 1: {
                    if (c == 47) {
                        c = this.in.readChar();
                        if (c == -1) {
                            this.in.ungetChar(c);
                            continue block15;
                        }
                        this.addCharToLexer(c);
                        if (TidyUtils.isLetter((char)c)) {
                            this.lexsize -= 3;
                            this.txtend = this.lexsize;
                            this.in.ungetChar(c);
                            this.state = (short)2;
                            this.lexbuf[this.lexsize] = 0;
                            this.columns -= 2;
                            if (this.txtend <= this.txtstart) continue block15;
                            if (mode == 0 && this.lexbuf[this.lexsize - 1] == 32) {
                                --this.lexsize;
                                this.txtend = this.lexsize;
                            }
                            this.token = this.newNode((short)4, this.lexbuf, this.txtstart, this.txtend);
                            return this.token;
                        }
                        this.waswhite = false;
                        this.state = 0;
                        continue block15;
                    }
                    if (mode == 3) {
                        this.waswhite = false;
                        this.state = 0;
                        continue block15;
                    }
                    if (c != 33) ** GOTO lbl136
                    c = this.in.readChar();
                    if (c != 45) ** GOTO lbl98
                    c = this.in.readChar();
                    if (c == 45) {
                        this.state = (short)4;
                        this.lexsize -= 2;
                        this.txtend = this.lexsize;
                        if (this.txtend > this.txtstart) {
                            this.token = this.newNode((short)4, this.lexbuf, this.txtstart, this.txtend);
                            return this.token;
                        }
                        this.txtstart = this.lexsize;
                        continue block15;
                    }
                    this.report.warning(this, null, null, (short)29);
                    ** GOTO lbl128
lbl98:
                    // 1 sources

                    if (c != 100 && c != 68) ** GOTO lbl119
                    this.state = (short)5;
                    this.lexsize -= 2;
                    this.txtend = this.lexsize;
                    mode = 0;
                    do {
                        if ((c = this.in.readChar()) != -1 && c != 62) continue;
                        this.in.ungetChar(c);
                        ** GOTO lbl114
                    } while (!TidyUtils.isWhite((char)c));
                    do {
                        if ((c = this.in.readChar()) != -1 && c != 62) continue;
                        this.in.ungetChar(c);
                        ** GOTO lbl114
                    } while (TidyUtils.isWhite((char)c));
                    this.in.ungetChar(c);
lbl114:
                    // 3 sources

                    if (this.txtend > this.txtstart) {
                        this.token = this.newNode((short)4, this.lexbuf, this.txtstart, this.txtend);
                        return this.token;
                    }
                    this.txtstart = this.lexsize;
                    continue block15;
lbl119:
                    // 1 sources

                    if (c == 91) {
                        this.lexsize -= 2;
                        this.state = (short)9;
                        this.txtend = this.lexsize;
                        if (this.txtend > this.txtstart) {
                            this.token = this.newNode((short)4, this.lexbuf, this.txtstart, this.txtend);
                            return this.token;
                        }
                        this.txtstart = this.lexsize;
                        continue block15;
                    }
lbl128:
                    // 4 sources

                    while ((c = this.in.readChar()) != 62) {
                        if (c != -1) continue;
                        this.in.ungetChar(c);
                        break;
                    }
                    this.lexsize -= 2;
                    this.lexbuf[this.lexsize] = 0;
                    this.state = 0;
                    continue block15;
lbl136:
                    // 1 sources

                    if (c == 63) {
                        this.lexsize -= 2;
                        this.state = (short)6;
                        this.txtend = this.lexsize;
                        if (this.txtend > this.txtstart) {
                            this.token = this.newNode((short)4, this.lexbuf, this.txtstart, this.txtend);
                            return this.token;
                        }
                        this.txtstart = this.lexsize;
                        continue block15;
                    }
                    if (c == 37) {
                        this.lexsize -= 2;
                        this.state = (short)10;
                        this.txtend = this.lexsize;
                        if (this.txtend > this.txtstart) {
                            this.token = this.newNode((short)4, this.lexbuf, this.txtstart, this.txtend);
                            return this.token;
                        }
                        this.txtstart = this.lexsize;
                        continue block15;
                    }
                    if (c == 35) {
                        this.lexsize -= 2;
                        this.state = (short)11;
                        this.txtend = this.lexsize;
                        if (this.txtend > this.txtstart) {
                            this.token = this.newNode((short)4, this.lexbuf, this.txtstart, this.txtend);
                            return this.token;
                        }
                        this.txtstart = this.lexsize;
                        continue block15;
                    }
                    if (TidyUtils.isLetter((char)c)) {
                        this.in.ungetChar(c);
                        this.lexsize -= 2;
                        this.txtend = this.lexsize;
                        this.state = (short)3;
                        if (this.txtend <= this.txtstart) continue block15;
                        this.token = this.newNode((short)4, this.lexbuf, this.txtstart, this.txtend);
                        return this.token;
                    }
                    this.state = 0;
                    this.waswhite = false;
                    continue block15;
                }
                case 2: {
                    this.txtstart = this.lexsize - 1;
                    this.columns -= 2;
                    c = this.parseTagName();
                    this.token = this.newNode((short)6, this.lexbuf, this.txtstart, this.txtend, TidyUtils.getString(this.lexbuf, this.txtstart, this.txtend - this.txtstart));
                    this.lexsize = this.txtstart;
                    this.txtend = this.txtstart;
                    while (c != 62 && (c = this.in.readChar()) != -1) {
                    }
                    if (c == -1) {
                        this.in.ungetChar(c);
                        continue block15;
                    }
                    this.state = 0;
                    this.waswhite = false;
                    return this.token;
                }
                case 3: {
                    this.txtstart = this.lexsize - 1;
                    c = this.parseTagName();
                    isempty[0] = false;
                    attributes = null;
                    this.token = this.newNode(isempty[0] != false ? 7 : 5, this.lexbuf, this.txtstart, this.txtend, TidyUtils.getString(this.lexbuf, this.txtstart, this.txtend - this.txtstart));
                    if (c != 62) {
                        if (c == 47) {
                            this.in.ungetChar(c);
                        }
                        attributes = this.parseAttrs(isempty);
                    }
                    if (isempty[0]) {
                        this.token.type = (short)7;
                    }
                    this.token.attributes = attributes;
                    this.lexsize = this.txtstart;
                    this.txtend = this.txtstart;
                    if ((mode != 2 || this.preContent(this.token)) && (this.token.expectsContent() || this.token.tag == this.configuration.tt.tagBr)) {
                        c = this.in.readChar();
                        if (c == 13) {
                            c = this.in.readChar();
                            if (c != 10) {
                                this.in.ungetChar(c);
                            }
                        } else if (c != 10 && c != 12) {
                            this.in.ungetChar(c);
                        }
                        this.waswhite = true;
                    } else {
                        this.waswhite = false;
                    }
                    this.state = 0;
                    if (this.token.tag == null) {
                        this.report.error(this, null, this.token, (short)22);
                    } else if (!this.configuration.xmlTags) {
                        this.constrainVersion(this.token.tag.versions);
                        if (TidyUtils.toBoolean(this.token.tag.versions & 448)) {
                            if (this.configuration.makeClean && this.token.tag != this.configuration.tt.tagNobr && this.token.tag != this.configuration.tt.tagWbr) {
                                this.report.warning(this, null, this.token, (short)21);
                            } else if (!this.configuration.makeClean) {
                                this.report.warning(this, null, this.token, (short)21);
                            }
                        }
                        if (this.token.tag.getChkattrs() != null) {
                            this.token.tag.getChkattrs().check(this, this.token);
                        } else {
                            this.token.checkAttributes(this);
                        }
                        this.token.repairDuplicateAttributes(this);
                    }
                    return this.token;
                }
                case 4: {
                    if (c != 45) continue block15;
                    c = this.in.readChar();
                    this.addCharToLexer(c);
                    if (c != 45) continue block15;
                    do {
                        if ((c = this.in.readChar()) == 62) {
                            if (badcomment != 0) {
                                this.report.warning(this, null, null, (short)29);
                            }
                            this.txtend = this.lexsize - 2;
                            this.lexbuf[this.lexsize] = 0;
                            this.state = 0;
                            this.waswhite = false;
                            this.token = this.newNode((short)2, this.lexbuf, this.txtstart, this.txtend);
                            c = this.in.readChar();
                            if (c == 13 && (c = this.in.readChar()) != 10) {
                                this.token.linebreak = true;
                            }
                            if (c == 10) {
                                this.token.linebreak = true;
                            } else {
                                this.in.ungetChar(c);
                            }
                            return this.token;
                        }
                        if (badcomment == 0) {
                            this.lines = this.in.getCurline();
                            this.columns = this.in.getCurcol() - 3;
                        }
                        ++badcomment;
                        if (this.configuration.fixComments) {
                            this.lexbuf[this.lexsize - 2] = 61;
                        }
                        this.addCharToLexer(c);
                    } while (c == 45);
                    this.lexbuf[this.lexsize - 2] = 61;
                    continue block15;
                }
                case 5: {
                    if (TidyUtils.isWhite((char)c)) {
                        if (this.waswhite) {
                            --this.lexsize;
                        }
                        this.waswhite = true;
                    } else {
                        this.waswhite = false;
                    }
                    if (inDTDSubset) {
                        if (c == 93) {
                            inDTDSubset = false;
                        }
                    } else if (c == 91) {
                        inDTDSubset = true;
                    }
                    if (inDTDSubset || c != 62) continue block15;
                    --this.lexsize;
                    this.txtend = this.lexsize;
                    this.lexbuf[this.lexsize] = 0;
                    this.state = 0;
                    this.waswhite = false;
                    this.token = this.newNode((short)1, this.lexbuf, this.txtstart, this.txtend);
                    this.doctype = this.findGivenVersion(this.token);
                    return this.token;
                }
                case 6: {
                    if (this.lexsize - this.txtstart == 3 && TidyUtils.getString(this.lexbuf, this.txtstart, 3).equals("php")) {
                        this.state = (short)12;
                        continue block15;
                    }
                    if (this.lexsize - this.txtstart == 4 && TidyUtils.getString(this.lexbuf, this.txtstart, 3).equals("xml") && TidyUtils.isWhite((char)this.lexbuf[this.txtstart + 3])) {
                        this.state = (short)13;
                        attributes = null;
                        continue block15;
                    }
                    if (this.configuration.xmlPIs) {
                        if (c != 63) continue block15;
                        c = this.in.readChar();
                        if (c == -1) {
                            this.report.warning(this, null, null, (short)36);
                            this.in.ungetChar(c);
                            continue block15;
                        }
                        this.addCharToLexer(c);
                    }
                    if (c != 62) continue block15;
                    --this.lexsize;
                    this.txtend = this.lexsize;
                    this.lexbuf[this.lexsize] = 0;
                    this.state = 0;
                    this.waswhite = false;
                    this.token = this.newNode((short)3, this.lexbuf, this.txtstart, this.txtend);
                    return this.token;
                }
                case 10: {
                    if (c != 37) continue block15;
                    c = this.in.readChar();
                    if (c != 62) {
                        this.in.ungetChar(c);
                        continue block15;
                    }
                    --this.lexsize;
                    this.txtend = this.lexsize;
                    this.lexbuf[this.lexsize] = 0;
                    this.state = 0;
                    this.waswhite = false;
                    this.token = this.newNode((short)10, this.lexbuf, this.txtstart, this.txtend);
                    return this.token;
                }
                case 11: {
                    if (c != 35) continue block15;
                    c = this.in.readChar();
                    if (c != 62) {
                        this.in.ungetChar(c);
                        continue block15;
                    }
                    --this.lexsize;
                    this.txtend = this.lexsize;
                    this.lexbuf[this.lexsize] = 0;
                    this.state = 0;
                    this.waswhite = false;
                    this.token = this.newNode((short)11, this.lexbuf, this.txtstart, this.txtend);
                    return this.token;
                }
                case 12: {
                    if (c != 63) continue block15;
                    c = this.in.readChar();
                    if (c != 62) {
                        this.in.ungetChar(c);
                        continue block15;
                    }
                    --this.lexsize;
                    this.txtend = this.lexsize;
                    this.lexbuf[this.lexsize] = 0;
                    this.state = 0;
                    this.waswhite = false;
                    this.token = this.newNode((short)12, this.lexbuf, this.txtstart, this.txtend);
                    return this.token;
                }
                case 13: {
                    if (TidyUtils.isWhite((char)c) && c != 63) continue block15;
                    if (c != 63) {
                        asp = new Node[1];
                        php = new Node[1];
                        av = new AttVal();
                        pdelim = new int[1];
                        isempty[0] = false;
                        this.in.ungetChar(c);
                        av.attribute = name = this.parseAttribute(isempty, asp, php);
                        av.value = this.parseValue(name, true, isempty, pdelim);
                        av.delim = pdelim[0];
                        av.next = attributes;
                        attributes = av;
                    }
                    if ((c = this.in.readChar()) != 62) {
                        this.in.ungetChar(c);
                        continue block15;
                    }
                    --this.lexsize;
                    this.txtend = this.txtstart;
                    this.lexbuf[this.txtend] = 0;
                    this.state = 0;
                    this.waswhite = false;
                    this.token = this.newNode((short)13, this.lexbuf, this.txtstart, this.txtend);
                    this.token.attributes = attributes;
                    return this.token;
                }
                case 9: {
                    if (c == 91 && this.lexsize == this.txtstart + 6 && TidyUtils.getString(this.lexbuf, this.txtstart, 6).equals("CDATA[")) {
                        this.state = (short)8;
                        this.lexsize -= 6;
                        continue block15;
                    }
                    if (c != 93) continue block15;
                    c = this.in.readChar();
                    if (c != 62) {
                        this.in.ungetChar(c);
                        continue block15;
                    }
                    --this.lexsize;
                    this.txtend = this.lexsize;
                    this.lexbuf[this.lexsize] = 0;
                    this.state = 0;
                    this.waswhite = false;
                    this.token = this.newNode((short)9, this.lexbuf, this.txtstart, this.txtend);
                    return this.token;
                }
                case 8: {
                    if (c != 93) continue block15;
                    c = this.in.readChar();
                    if (c != 93) {
                        this.in.ungetChar(c);
                        continue block15;
                    }
                    c = this.in.readChar();
                    if (c != 62) {
                        this.in.ungetChar(c);
                        continue block15;
                    }
                    --this.lexsize;
                    this.txtend = this.lexsize;
                    this.lexbuf[this.lexsize] = 0;
                    this.state = 0;
                    this.waswhite = false;
                    this.token = this.newNode((short)8, this.lexbuf, this.txtstart, this.txtend);
                    return this.token;
                }
            }
        }
        if (this.state == 0) {
            this.txtend = this.lexsize;
            if (this.txtend > this.txtstart) {
                this.in.ungetChar(c);
                if (this.lexbuf[this.lexsize - 1] == 32) {
                    --this.lexsize;
                    this.txtend = this.lexsize;
                }
                this.token = this.newNode((short)4, this.lexbuf, this.txtstart, this.txtend);
                return this.token;
            }
        } else if (this.state == 4) {
            if (c == -1) {
                this.report.warning(this, null, null, (short)29);
            }
            this.txtend = this.lexsize;
            this.lexbuf[this.lexsize] = 0;
            this.state = 0;
            this.waswhite = false;
            this.token = this.newNode((short)2, this.lexbuf, this.txtstart, this.txtend);
            return this.token;
        }
        return null;
    }

    public Node parseAsp() {
        int c;
        Node asp = null;
        this.txtstart = this.lexsize;
        while ((c = this.in.readChar()) != -1) {
            this.addCharToLexer(c);
            if (c != 37) continue;
            c = this.in.readChar();
            if (c == -1) break;
            this.addCharToLexer(c);
            if (c != 62) continue;
        }
        this.lexsize -= 2;
        this.txtend = this.lexsize;
        if (this.txtend > this.txtstart) {
            asp = this.newNode((short)10, this.lexbuf, this.txtstart, this.txtend);
        }
        this.txtstart = this.txtend;
        return asp;
    }

    public Node parsePhp() {
        int c;
        Node php = null;
        this.txtstart = this.lexsize;
        while ((c = this.in.readChar()) != -1) {
            this.addCharToLexer(c);
            if (c != 63) continue;
            c = this.in.readChar();
            if (c == -1) break;
            this.addCharToLexer(c);
            if (c != 62) continue;
        }
        this.lexsize -= 2;
        this.txtend = this.lexsize;
        if (this.txtend > this.txtstart) {
            php = this.newNode((short)12, this.lexbuf, this.txtstart, this.txtend);
        }
        this.txtstart = this.txtend;
        return php;
    }

    public String parseAttribute(boolean[] isempty, Node[] asp, Node[] php) {
        int start = 0;
        int c = 0;
        int lastc = 0;
        asp[0] = null;
        php[0] = null;
        while (true) {
            if ((c = this.in.readChar()) == 47) {
                c = this.in.readChar();
                if (c == 62) {
                    isempty[0] = true;
                    return null;
                }
                this.in.ungetChar(c);
                c = 47;
                break;
            }
            if (c == 62) {
                return null;
            }
            if (c == 60) {
                c = this.in.readChar();
                if (c == 37) {
                    asp[0] = this.parseAsp();
                    return null;
                }
                if (c == 63) {
                    php[0] = this.parsePhp();
                    return null;
                }
                this.in.ungetChar(c);
                if (this.state != 13) {
                    this.in.ungetChar(60);
                }
                this.report.attrError(this, this.token, null, (short)52);
                return null;
            }
            if (c == 61) {
                this.report.attrError(this, this.token, null, (short)69);
                continue;
            }
            if (c == 34 || c == 39) {
                this.report.attrError(this, this.token, null, (short)59);
                continue;
            }
            if (c == -1) {
                this.report.attrError(this, this.token, null, (short)36);
                this.in.ungetChar(c);
                return null;
            }
            if (!TidyUtils.isWhite((char)c)) break;
        }
        start = this.lexsize;
        lastc = c;
        while (true) {
            if (c == 61 || c == 62) {
                this.in.ungetChar(c);
                break;
            }
            if (c == 60 || c == -1) {
                this.in.ungetChar(c);
                break;
            }
            if (lastc == 45 && (c == 34 || c == 39)) {
                --this.lexsize;
                this.in.ungetChar(c);
                break;
            }
            if (TidyUtils.isWhite((char)c)) break;
            if (!this.configuration.xmlTags && TidyUtils.isUpper((char)c)) {
                c = TidyUtils.toLower((char)c);
            }
            this.addCharToLexer(c);
            lastc = c;
            c = this.in.readChar();
        }
        int len = this.lexsize - start;
        String attr = len > 0 ? TidyUtils.getString(this.lexbuf, start, len) : null;
        this.lexsize = start;
        return attr;
    }

    public int parseServerInstruction() {
        int delim = 34;
        boolean isrule = false;
        int c = this.in.readChar();
        this.addCharToLexer(c);
        if (c == 37 || c == 63 || c == 64) {
            isrule = true;
        }
        while ((c = this.in.readChar()) != -1) {
            if (c == 62) {
                if (isrule) {
                    this.addCharToLexer(c);
                    break;
                }
                this.in.ungetChar(c);
                break;
            }
            if (!isrule && TidyUtils.isWhite((char)c)) break;
            this.addCharToLexer(c);
            if (c == 34) {
                do {
                    c = this.in.readChar();
                    if (this.endOfInput()) {
                        this.report.attrError(this, this.token, null, (short)36);
                        this.in.ungetChar(c);
                        return 0;
                    }
                    if (c == 62) {
                        this.in.ungetChar(c);
                        this.report.attrError(this, this.token, null, (short)52);
                        return 0;
                    }
                    this.addCharToLexer(c);
                } while (c != 34);
                delim = 39;
                continue;
            }
            if (c != 39) continue;
            do {
                c = this.in.readChar();
                if (this.endOfInput()) {
                    this.report.attrError(this, this.token, null, (short)36);
                    this.in.ungetChar(c);
                    return 0;
                }
                if (c == 62) {
                    this.in.ungetChar(c);
                    this.report.attrError(this, this.token, null, (short)52);
                    return 0;
                }
                this.addCharToLexer(c);
            } while (c != 39);
        }
        return delim;
    }

    public String parseValue(String name, boolean foldCase, boolean[] isempty, int[] pdelim) {
        String value;
        int len = 0;
        boolean seenGt = false;
        boolean munge = true;
        int c = 0;
        int delim = 0;
        pdelim[0] = 34;
        if (this.configuration.literalAttribs) {
            munge = false;
        }
        do {
            if ((c = this.in.readChar()) != -1) continue;
            this.in.ungetChar(c);
            break;
        } while (TidyUtils.isWhite((char)c));
        if (c != 61 && c != 34 && c != 39) {
            this.in.ungetChar(c);
            return null;
        }
        do {
            if ((c = this.in.readChar()) != -1) continue;
            this.in.ungetChar(c);
            break;
        } while (TidyUtils.isWhite((char)c));
        if (c == 34 || c == 39) {
            delim = c;
        } else {
            if (c == 60) {
                int start = this.lexsize;
                this.addCharToLexer(c);
                pdelim[0] = this.parseServerInstruction();
                len = this.lexsize - start;
                this.lexsize = start;
                return len > 0 ? TidyUtils.getString(this.lexbuf, start, len) : null;
            }
            this.in.ungetChar(c);
        }
        int quotewarning = 0;
        int start = this.lexsize;
        c = 0;
        while (true) {
            int lastc = c;
            c = this.in.readChar();
            if (c == -1) {
                this.report.attrError(this, this.token, null, (short)36);
                this.in.ungetChar(c);
                break;
            }
            if (delim == 0) {
                if (c == 62) {
                    this.in.ungetChar(c);
                    break;
                }
                if (c == 34 || c == 39) {
                    this.report.attrError(this, this.token, null, (short)59);
                    break;
                }
                if (c == 60) {
                    this.in.ungetChar(c);
                    c = 62;
                    this.in.ungetChar(c);
                    this.report.attrError(this, this.token, null, (short)52);
                    break;
                }
                if (c == 47) {
                    c = this.in.readChar();
                    if (c == 62 && !AttributeTable.getDefaultAttributeTable().isUrl(name)) {
                        isempty[0] = true;
                        this.in.ungetChar(c);
                        break;
                    }
                    this.in.ungetChar(c);
                    c = 47;
                }
            } else {
                if (c == delim) break;
                if (c == 13) {
                    c = this.in.readChar();
                    if (c != 10) {
                        this.in.ungetChar(c);
                    }
                    c = 10;
                }
                if (c == 10 || c == 60 || c == 62) {
                    ++quotewarning;
                }
                if (c == 62) {
                    seenGt = true;
                }
            }
            if (c == 38) {
                if ("id".equalsIgnoreCase(name)) {
                    this.report.attrError(this, null, null, (short)67);
                    continue;
                }
                this.addCharToLexer(c);
                this.parseEntity((short)0);
                continue;
            }
            if (c == 92 && (c = this.in.readChar()) != 10) {
                this.in.ungetChar(c);
                c = 92;
            }
            if (TidyUtils.isWhite((char)c)) {
                if (delim == 0) break;
                if (munge) {
                    if (c == 10 && AttributeTable.getDefaultAttributeTable().isUrl(name)) {
                        this.report.attrError(this, this.token, null, (short)65);
                        continue;
                    }
                    c = 32;
                    if (lastc == 32) {
                        continue;
                    }
                }
            } else if (foldCase && TidyUtils.isUpper((char)c)) {
                c = TidyUtils.toLower((char)c);
            }
            this.addCharToLexer(c);
        }
        if (!(quotewarning <= 10 || !seenGt || !munge || AttributeTable.getDefaultAttributeTable().isScript(name) || AttributeTable.getDefaultAttributeTable().isUrl(name) && "javascript:".equals(TidyUtils.getString(this.lexbuf, start, 11)) || "<xml ".equals(TidyUtils.getString(this.lexbuf, start, 5)))) {
            this.report.error(this, null, null, (short)16);
        }
        len = this.lexsize - start;
        this.lexsize = start;
        if (len > 0 || delim != 0) {
            if (munge && !TidyUtils.isInValuesIgnoreCase(new String[]{"alt", "title", "value", "prompt"}, name)) {
                while (TidyUtils.isWhite((char)this.lexbuf[start + len - 1])) {
                    --len;
                }
                while (TidyUtils.isWhite((char)this.lexbuf[start]) && start < len) {
                    ++start;
                    --len;
                }
            }
            value = TidyUtils.getString(this.lexbuf, start, len);
        } else {
            value = null;
        }
        pdelim[0] = delim != 0 ? delim : 34;
        return value;
    }

    public static boolean isValidAttrName(String attr) {
        char c = attr.charAt(0);
        if (!TidyUtils.isLetter(c)) {
            return false;
        }
        for (int i = 1; i < attr.length(); ++i) {
            c = attr.charAt(i);
            if (TidyUtils.isNamechar(c)) continue;
            return false;
        }
        return true;
    }

    public static boolean isCSS1Selector(String buf) {
        if (buf == null) {
            return false;
        }
        boolean valid = true;
        int esclen = 0;
        for (int pos = 0; valid && pos < buf.length(); ++pos) {
            char c = buf.charAt(pos);
            if (c == '\\') {
                esclen = 1;
                continue;
            }
            if (Character.isDigit(c)) {
                if (esclen > 0) {
                    boolean bl = valid = ++esclen < 6;
                }
                if (!valid) continue;
                valid = pos > 0 || esclen > 0;
                continue;
            }
            valid = esclen > 0 || pos > 0 && c == '-' || Character.isLetter(c) || c >= '\u00a1' && c <= '\u00ff';
            esclen = 0;
        }
        return valid;
    }

    public AttVal parseAttrs(boolean[] isempty) {
        int[] delim = new int[1];
        Node[] asp = new Node[1];
        Node[] php = new Node[1];
        AttVal list = null;
        while (!this.endOfInput()) {
            AttVal av;
            String attribute = this.parseAttribute(isempty, asp, php);
            if (attribute == null) {
                if (asp[0] != null) {
                    list = av = new AttVal(list, null, asp[0], null, 0, null, null);
                    continue;
                }
                if (php[0] == null) break;
                list = av = new AttVal(list, null, null, php[0], 0, null, null);
                continue;
            }
            String value = this.parseValue(attribute, false, isempty, delim);
            if (attribute != null && Lexer.isValidAttrName(attribute)) {
                av = new AttVal(list, null, null, null, delim[0], attribute, value);
                av.dict = AttributeTable.getDefaultAttributeTable().findAttribute(av);
                list = av;
                continue;
            }
            av = new AttVal(null, null, null, null, 0, attribute, value);
            if (value != null) {
                this.report.attrError(this, this.token, av, (short)51);
                continue;
            }
            if (TidyUtils.lastChar(attribute) == 34) {
                this.report.attrError(this, this.token, av, (short)58);
                continue;
            }
            this.report.attrError(this, this.token, av, (short)48);
        }
        return list;
    }

    public void pushInline(Node node) {
        if (node.implicit) {
            return;
        }
        if (node.tag == null) {
            return;
        }
        if (!TidyUtils.toBoolean(node.tag.model & 0x10)) {
            return;
        }
        if (TidyUtils.toBoolean(node.tag.model & 0x800)) {
            return;
        }
        if (node.tag != this.configuration.tt.tagFont && this.isPushed(node)) {
            return;
        }
        IStack is = new IStack();
        is.tag = node.tag;
        is.element = node.element;
        if (node.attributes != null) {
            is.attributes = this.cloneAttributes(node.attributes);
        }
        this.istack.push(is);
    }

    public void popInline(Node node) {
        if (node != null) {
            if (node.tag == null) {
                return;
            }
            if (!TidyUtils.toBoolean(node.tag.model & 0x10)) {
                return;
            }
            if (TidyUtils.toBoolean(node.tag.model & 0x800)) {
                return;
            }
            if (node.tag == this.configuration.tt.tagA) {
                while (this.istack.size() > 0) {
                    IStack is = (IStack)this.istack.pop();
                    if (is.tag != this.configuration.tt.tagA) continue;
                }
                if (this.insert >= this.istack.size()) {
                    this.insert = -1;
                }
                return;
            }
        }
        if (this.istack.size() > 0) {
            IStack is = (IStack)this.istack.pop();
            if (this.insert >= this.istack.size()) {
                this.insert = -1;
            }
        }
    }

    public boolean isPushed(Node node) {
        for (int i = this.istack.size() - 1; i >= 0; --i) {
            IStack is = (IStack)this.istack.elementAt(i);
            if (is.tag != node.tag) continue;
            return true;
        }
        return false;
    }

    public int inlineDup(Node node) {
        int n = this.istack.size() - this.istackbase;
        if (n > 0) {
            this.insert = this.istackbase;
            this.inode = node;
        }
        return n;
    }

    public Node insertedToken() {
        if (this.insert == -1) {
            Node node = this.inode;
            this.inode = null;
            return node;
        }
        if (this.inode == null) {
            this.lines = this.in.getCurline();
            this.columns = this.in.getCurcol();
        }
        Node node = this.newNode((short)5, this.lexbuf, this.txtstart, this.txtend);
        node.implicit = true;
        IStack is = (IStack)this.istack.elementAt(this.insert);
        node.element = is.element;
        node.tag = is.tag;
        if (is.attributes != null) {
            node.attributes = this.cloneAttributes(is.attributes);
        }
        int n = this.insert;
        this.insert = ++n < this.istack.size() ? n : -1;
        return node;
    }

    public boolean canPrune(Node element) {
        if (element.type == 4) {
            return true;
        }
        if (element.content != null) {
            return false;
        }
        if (element.tag == this.configuration.tt.tagA && element.attributes != null) {
            return false;
        }
        if (element.tag == this.configuration.tt.tagP && !this.configuration.dropEmptyParas) {
            return false;
        }
        if (element.tag == null) {
            return false;
        }
        if (TidyUtils.toBoolean(element.tag.model & 0x200)) {
            return false;
        }
        if (TidyUtils.toBoolean(element.tag.model & 1)) {
            return false;
        }
        if (element.tag == this.configuration.tt.tagApplet) {
            return false;
        }
        if (element.tag == this.configuration.tt.tagObject) {
            return false;
        }
        if (element.tag == this.configuration.tt.tagScript && element.getAttrByName("src") != null) {
            return false;
        }
        if (element.tag == this.configuration.tt.tagTitle) {
            return false;
        }
        if (element.tag == this.configuration.tt.tagIframe) {
            return false;
        }
        return element.getAttrByName("id") == null && element.getAttrByName("name") == null;
    }

    public void fixId(Node node) {
        AttVal name = node.getAttrByName("name");
        AttVal id = node.getAttrByName("id");
        if (name != null) {
            if (id != null) {
                if (id.value != null && !id.value.equals(name.value)) {
                    this.report.attrError(this, node, name, (short)60);
                }
            } else if (this.configuration.xmlOut) {
                node.addAttribute("id", name.value);
            }
        }
    }

    public void deferDup() {
        this.insert = -1;
        this.inode = null;
    }

    void constrainVersion(int vers) {
        this.versions = (short)(this.versions & (vers | 0x1C0));
    }

    protected boolean preContent(Node node) {
        if (node.tag == this.configuration.tt.tagP) {
            return true;
        }
        return node.tag != null && node.tag != this.configuration.tt.tagP && TidyUtils.toBoolean(node.tag.model & 0x100010);
    }

    private static class W3CVersionInfo {
        String name;
        String voyagerName;
        String profile;
        short code;

        public W3CVersionInfo(String name, String voyagerName, String profile, short code) {
            this.name = name;
            this.voyagerName = voyagerName;
            this.profile = profile;
            this.code = code;
        }
    }
}

