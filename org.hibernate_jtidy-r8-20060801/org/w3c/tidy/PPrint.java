/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import org.w3c.tidy.AttVal;
import org.w3c.tidy.Attribute;
import org.w3c.tidy.AttributeTable;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.EncodingUtils;
import org.w3c.tidy.EntityTable;
import org.w3c.tidy.Lexer;
import org.w3c.tidy.Node;
import org.w3c.tidy.Out;
import org.w3c.tidy.OutFactory;
import org.w3c.tidy.ParserImpl;
import org.w3c.tidy.TagTable;
import org.w3c.tidy.TidyUtils;

public class PPrint {
    private static final short NORMAL = 0;
    private static final short PREFORMATTED = 1;
    private static final short COMMENT = 2;
    private static final short ATTRIBVALUE = 4;
    private static final short NOWRAP = 8;
    private static final short CDATA = 16;
    private static final String CDATA_START = "<![CDATA[";
    private static final String CDATA_END = "]]>";
    private static final String JS_COMMENT_START = "//";
    private static final String JS_COMMENT_END = "";
    private static final String VB_COMMENT_START = "'";
    private static final String VB_COMMENT_END = "";
    private static final String CSS_COMMENT_START = "/*";
    private static final String CSS_COMMENT_END = "*/";
    private static final String DEFAULT_COMMENT_START = "";
    private static final String DEFAULT_COMMENT_END = "";
    private int[] linebuf;
    private int lbufsize;
    private int linelen;
    private int wraphere;
    private boolean inAttVal;
    private boolean inString;
    private int slide;
    private int count;
    private Node slidecontent;
    private Configuration configuration;

    public PPrint(Configuration configuration) {
        this.configuration = configuration;
    }

    int cWrapLen(int ind) {
        if ("zh".equals(this.configuration.language)) {
            return ind + (this.configuration.wraplen - ind) / 2;
        }
        if ("ja".equals(this.configuration.language)) {
            return ind + (this.configuration.wraplen - ind) * 7 / 10;
        }
        return this.configuration.wraplen;
    }

    public static int getUTF8(byte[] str, int start, int[] ch) {
        int[] n = new int[1];
        int[] bytes = new int[]{0};
        byte[] successorBytes = str;
        boolean err = EncodingUtils.decodeUTF8BytesToChar(n, TidyUtils.toUnsigned(str[start]), successorBytes, null, bytes, start + 1);
        if (err) {
            n[0] = 65533;
        }
        ch[0] = n[0];
        return bytes[0] - 1;
    }

    public static int putUTF8(byte[] buf, int start, int c) {
        int[] count = new int[]{0};
        boolean err = EncodingUtils.encodeCharToUTF8Bytes(c, buf, null, count);
        if (err) {
            buf[0] = -17;
            buf[1] = -65;
            buf[2] = -67;
            count[0] = 3;
        }
        return start += count[0];
    }

    private void addC(int c, int index) {
        if (index + 1 >= this.lbufsize) {
            while (index + 1 >= this.lbufsize) {
                if (this.lbufsize == 0) {
                    this.lbufsize = 256;
                    continue;
                }
                this.lbufsize *= 2;
            }
            int[] temp = new int[this.lbufsize];
            if (this.linebuf != null) {
                System.arraycopy(this.linebuf, 0, temp, 0, index);
            }
            this.linebuf = temp;
        }
        this.linebuf[index] = c;
    }

    private int addAsciiString(String str, int index) {
        int len = str.length();
        if (index + len >= this.lbufsize) {
            while (index + len >= this.lbufsize) {
                if (this.lbufsize == 0) {
                    this.lbufsize = 256;
                    continue;
                }
                this.lbufsize *= 2;
            }
            int[] temp = new int[this.lbufsize];
            if (this.linebuf != null) {
                System.arraycopy(this.linebuf, 0, temp, 0, index);
            }
            this.linebuf = temp;
        }
        for (int ix = 0; ix < len; ++ix) {
            this.linebuf[index + ix] = str.charAt(ix);
        }
        return index + len;
    }

    private void wrapLine(Out fout, int indent) {
        int i;
        if (this.wraphere == 0) {
            return;
        }
        for (i = 0; i < indent; ++i) {
            fout.outc(32);
        }
        for (i = 0; i < this.wraphere; ++i) {
            fout.outc(this.linebuf[i]);
        }
        if (this.inString) {
            fout.outc(32);
            fout.outc(92);
        }
        fout.newline();
        if (this.linelen > this.wraphere) {
            int p = 0;
            if (this.linebuf[this.wraphere] == 32) {
                ++this.wraphere;
            }
            int q = this.wraphere;
            this.addC(0, this.linelen);
            while (true) {
                this.linebuf[p] = this.linebuf[q];
                if (this.linebuf[q] == 0) break;
                ++p;
                ++q;
            }
            this.linelen -= this.wraphere;
        } else {
            this.linelen = 0;
        }
        this.wraphere = 0;
    }

    private void wrapAttrVal(Out fout, int indent, boolean inString) {
        int i;
        for (i = 0; i < indent; ++i) {
            fout.outc(32);
        }
        for (i = 0; i < this.wraphere; ++i) {
            fout.outc(this.linebuf[i]);
        }
        fout.outc(32);
        if (inString) {
            fout.outc(92);
        }
        fout.newline();
        if (this.linelen > this.wraphere) {
            int p = 0;
            if (this.linebuf[this.wraphere] == 32) {
                ++this.wraphere;
            }
            int q = this.wraphere;
            this.addC(0, this.linelen);
            while (true) {
                this.linebuf[p] = this.linebuf[q];
                if (this.linebuf[q] == 0) break;
                ++p;
                ++q;
            }
            this.linelen -= this.wraphere;
        } else {
            this.linelen = 0;
        }
        this.wraphere = 0;
    }

    public void flushLine(Out fout, int indent) {
        if (this.linelen > 0) {
            int i;
            if (indent + this.linelen >= this.configuration.wraplen) {
                this.wrapLine(fout, indent);
            }
            if (!this.inAttVal || this.configuration.indentAttributes) {
                for (i = 0; i < indent; ++i) {
                    fout.outc(32);
                }
            }
            for (i = 0; i < this.linelen; ++i) {
                fout.outc(this.linebuf[i]);
            }
        }
        fout.newline();
        this.linelen = 0;
        this.wraphere = 0;
        this.inAttVal = false;
    }

    public void condFlushLine(Out fout, int indent) {
        if (this.linelen > 0) {
            int i;
            if (indent + this.linelen >= this.configuration.wraplen) {
                this.wrapLine(fout, indent);
            }
            if (!this.inAttVal || this.configuration.indentAttributes) {
                for (i = 0; i < indent; ++i) {
                    fout.outc(32);
                }
            }
            for (i = 0; i < this.linelen; ++i) {
                fout.outc(this.linebuf[i]);
            }
            fout.newline();
            this.linelen = 0;
            this.wraphere = 0;
            this.inAttVal = false;
        }
    }

    private void printChar(int c, short mode) {
        boolean breakable = false;
        if (c == 32 && !TidyUtils.toBoolean(mode & 0x17)) {
            if (TidyUtils.toBoolean(mode & 8)) {
                if (this.configuration.numEntities || this.configuration.xmlTags) {
                    this.addC(38, this.linelen++);
                    this.addC(35, this.linelen++);
                    this.addC(49, this.linelen++);
                    this.addC(54, this.linelen++);
                    this.addC(48, this.linelen++);
                    this.addC(59, this.linelen++);
                } else {
                    this.addC(38, this.linelen++);
                    this.addC(110, this.linelen++);
                    this.addC(98, this.linelen++);
                    this.addC(115, this.linelen++);
                    this.addC(112, this.linelen++);
                    this.addC(59, this.linelen++);
                }
                return;
            }
            this.wraphere = this.linelen;
        }
        if (TidyUtils.toBoolean(mode & 0x12)) {
            this.addC(c, this.linelen++);
            return;
        }
        if (!TidyUtils.toBoolean(mode & 0x10)) {
            if (c == 60) {
                this.addC(38, this.linelen++);
                this.addC(108, this.linelen++);
                this.addC(116, this.linelen++);
                this.addC(59, this.linelen++);
                return;
            }
            if (c == 62) {
                this.addC(38, this.linelen++);
                this.addC(103, this.linelen++);
                this.addC(116, this.linelen++);
                this.addC(59, this.linelen++);
                return;
            }
            if (c == 38 && this.configuration.quoteAmpersand) {
                this.addC(38, this.linelen++);
                this.addC(97, this.linelen++);
                this.addC(109, this.linelen++);
                this.addC(112, this.linelen++);
                this.addC(59, this.linelen++);
                return;
            }
            if (c == 34 && this.configuration.quoteMarks) {
                this.addC(38, this.linelen++);
                this.addC(113, this.linelen++);
                this.addC(117, this.linelen++);
                this.addC(111, this.linelen++);
                this.addC(116, this.linelen++);
                this.addC(59, this.linelen++);
                return;
            }
            if (c == 39 && this.configuration.quoteMarks) {
                this.addC(38, this.linelen++);
                this.addC(35, this.linelen++);
                this.addC(51, this.linelen++);
                this.addC(57, this.linelen++);
                this.addC(59, this.linelen++);
                return;
            }
            if (c == 160 && !this.configuration.rawOut) {
                if (this.configuration.makeBare) {
                    this.addC(32, this.linelen++);
                } else if (this.configuration.quoteNbsp) {
                    this.addC(38, this.linelen++);
                    if (this.configuration.numEntities || this.configuration.xmlTags) {
                        this.addC(35, this.linelen++);
                        this.addC(49, this.linelen++);
                        this.addC(54, this.linelen++);
                        this.addC(48, this.linelen++);
                    } else {
                        this.addC(110, this.linelen++);
                        this.addC(98, this.linelen++);
                        this.addC(115, this.linelen++);
                        this.addC(112, this.linelen++);
                    }
                    this.addC(59, this.linelen++);
                } else {
                    this.addC(c, this.linelen++);
                }
                return;
            }
        }
        if ("UTF8".equals(this.configuration.getOutCharEncodingName())) {
            if (c >= 8192 && !TidyUtils.toBoolean(mode & 1)) {
                if (c >= 8192 && c <= 8198 || c >= 8200 && c <= 8208 || c >= 8209 && c <= 8262 || c >= 8317 && c <= 8318 || c >= 8333 && c <= 8334 || c >= 9001 && c <= 9002 || c >= 12289 && c <= 12291 || c >= 12296 && c <= 12305 || c >= 12308 && c <= 12319 || c >= 64830 && c <= 64831 || c >= 65072 && c <= 65092 || c >= 65097 && c <= 65106 || c >= 65108 && c <= 65121 || c >= 65130 && c <= 65131 || c >= 65281 && c <= 65283 || c >= 65285 && c <= 65290 || c >= 65292 && c <= 65295 || c >= 65306 && c <= 65307 || c >= 65311 && c <= 65312 || c >= 65339 && c <= 65341 || c >= 65377 && c <= 65381) {
                    this.wraphere = this.linelen + 2;
                    breakable = true;
                } else {
                    switch (c) {
                        case 12336: 
                        case 12539: 
                        case 65123: 
                        case 65128: 
                        case 65343: 
                        case 65371: 
                        case 65373: {
                            this.wraphere = this.linelen + 2;
                            breakable = true;
                        }
                    }
                }
                if (breakable) {
                    if (c >= 8218 && c <= 8220 || c >= 8222 && c <= 8223) {
                        --this.wraphere;
                    } else {
                        switch (c) {
                            case 8216: 
                            case 8249: 
                            case 8261: 
                            case 8317: 
                            case 8333: 
                            case 9001: 
                            case 12296: 
                            case 12298: 
                            case 12300: 
                            case 12302: 
                            case 12304: 
                            case 12308: 
                            case 12310: 
                            case 12312: 
                            case 12314: 
                            case 12317: 
                            case 64830: 
                            case 65077: 
                            case 65079: 
                            case 65081: 
                            case 65083: 
                            case 65085: 
                            case 65087: 
                            case 65089: 
                            case 65091: 
                            case 65113: 
                            case 65115: 
                            case 65117: 
                            case 65288: 
                            case 65339: 
                            case 65371: 
                            case 65378: {
                                --this.wraphere;
                            }
                        }
                    }
                }
            } else {
                if ("BIG5".equals(this.configuration.getOutCharEncodingName())) {
                    this.addC(c, this.linelen++);
                    if ((c & 0xFF00) == 41216 && !TidyUtils.toBoolean(mode & 1)) {
                        this.wraphere = this.linelen;
                        if (c > 92 && c < 173 && (c & 1) == 1) {
                            --this.wraphere;
                        }
                    }
                    return;
                }
                if ("SHIFTJIS".equals(this.configuration.getOutCharEncodingName()) || "ISO2022".equals(this.configuration.getOutCharEncodingName())) {
                    this.addC(c, this.linelen++);
                    return;
                }
                if (this.configuration.rawOut) {
                    this.addC(c, this.linelen++);
                    return;
                }
            }
        }
        if (c == 160 && TidyUtils.toBoolean(mode & 1)) {
            this.addC(32, this.linelen++);
            return;
        }
        if ((this.configuration.makeClean && this.configuration.asciiChars || this.configuration.makeBare) && c >= 8211 && c <= 8222) {
            switch (c) {
                case 8211: 
                case 8212: {
                    c = 45;
                    break;
                }
                case 8216: 
                case 8217: 
                case 8218: {
                    c = 39;
                    break;
                }
                case 8220: 
                case 8221: 
                case 8222: {
                    c = 34;
                }
            }
        }
        if ("ISO8859_1".equals(this.configuration.getOutCharEncodingName())) {
            if (c > 255) {
                String entity;
                entity = !this.configuration.numEntities ? ((entity = EntityTable.getDefaultEntityTable().entityName((short)c)) != null ? "&" + entity + ";" : "&#" + c + ";") : "&#" + c + ";";
                for (int i = 0; i < entity.length(); ++i) {
                    this.addC(entity.charAt(i), this.linelen++);
                }
                return;
            }
            if (c > 126 && c < 160) {
                String entity = "&#" + c + ";";
                for (int i = 0; i < entity.length(); ++i) {
                    this.addC(entity.charAt(i), this.linelen++);
                }
                return;
            }
            this.addC(c, this.linelen++);
            return;
        }
        if (this.configuration.getOutCharEncodingName().startsWith("UTF")) {
            this.addC(c, this.linelen++);
            return;
        }
        if (this.configuration.xmlTags) {
            if (c > 127 && "ASCII".equals(this.configuration.getOutCharEncodingName())) {
                String entity = "&#" + c + ";";
                for (int i = 0; i < entity.length(); ++i) {
                    this.addC(entity.charAt(i), this.linelen++);
                }
                return;
            }
            this.addC(c, this.linelen++);
            return;
        }
        if ("ASCII".equals(this.configuration.getOutCharEncodingName()) && (c > 126 || c < 32 && c != 9)) {
            String entity;
            entity = !this.configuration.numEntities ? ((entity = EntityTable.getDefaultEntityTable().entityName((short)c)) != null ? "&" + entity + ";" : "&#" + c + ";") : "&#" + c + ";";
            for (int i = 0; i < entity.length(); ++i) {
                this.addC(entity.charAt(i), this.linelen++);
            }
            return;
        }
        this.addC(c, this.linelen++);
    }

    private void printText(Out fout, short mode, int indent, byte[] textarray, int start, int end) {
        int[] ci = new int[1];
        for (int i = start; i < end; ++i) {
            int c;
            if (indent + this.linelen >= this.configuration.wraplen) {
                this.wrapLine(fout, indent);
            }
            if ((c = textarray[i] & 0xFF) > 127) {
                i += PPrint.getUTF8(textarray, i, ci);
                c = ci[0];
            }
            if (c == 10) {
                this.flushLine(fout, indent);
                continue;
            }
            this.printChar(c, mode);
        }
    }

    private void printString(String str) {
        for (int i = 0; i < str.length(); ++i) {
            this.addC(str.charAt(i), this.linelen++);
        }
    }

    private void printAttrValue(Out fout, int indent, String value, int delim, boolean wrappable) {
        short mode;
        int[] ci = new int[1];
        boolean wasinstring = false;
        byte[] valueChars = null;
        short s = mode = wrappable ? (short)4 : 5;
        if (value != null) {
            valueChars = TidyUtils.getBytes(value);
        }
        if (valueChars != null && valueChars.length >= 5 && valueChars[0] == 60 && (valueChars[1] == 37 || valueChars[1] == 64 || new String(valueChars, 0, 5).equals("<?php"))) {
            mode = (short)(mode | 0x10);
        }
        if (delim == 0) {
            delim = 34;
        }
        this.addC(61, this.linelen++);
        if (!this.configuration.xmlOut) {
            if (indent + this.linelen < this.configuration.wraplen) {
                this.wraphere = this.linelen;
            }
            if (indent + this.linelen >= this.configuration.wraplen) {
                this.wrapLine(fout, indent);
            }
            if (indent + this.linelen < this.configuration.wraplen) {
                this.wraphere = this.linelen;
            } else {
                this.condFlushLine(fout, indent);
            }
        }
        this.addC(delim, this.linelen++);
        if (value != null) {
            this.inString = false;
            int i = 0;
            while (i < valueChars.length) {
                int c = valueChars[i] & 0xFF;
                if (wrappable && c == 32 && indent + this.linelen < this.configuration.wraplen) {
                    this.wraphere = this.linelen;
                    wasinstring = this.inString;
                }
                if (wrappable && this.wraphere > 0 && indent + this.linelen >= this.configuration.wraplen) {
                    this.wrapAttrVal(fout, indent, wasinstring);
                }
                if (c == delim) {
                    String entity = c == 34 ? "&quot;" : "&#39;";
                    for (int j = 0; j < entity.length(); ++j) {
                        this.addC(entity.charAt(j), this.linelen++);
                    }
                    ++i;
                    continue;
                }
                if (c == 34) {
                    if (this.configuration.quoteMarks) {
                        this.addC(38, this.linelen++);
                        this.addC(113, this.linelen++);
                        this.addC(117, this.linelen++);
                        this.addC(111, this.linelen++);
                        this.addC(116, this.linelen++);
                        this.addC(59, this.linelen++);
                    } else {
                        this.addC(34, this.linelen++);
                    }
                    if (delim == 39) {
                        this.inString = !this.inString;
                    }
                    ++i;
                    continue;
                }
                if (c == 39) {
                    if (this.configuration.quoteMarks) {
                        this.addC(38, this.linelen++);
                        this.addC(35, this.linelen++);
                        this.addC(51, this.linelen++);
                        this.addC(57, this.linelen++);
                        this.addC(59, this.linelen++);
                    } else {
                        this.addC(39, this.linelen++);
                    }
                    if (delim == 34) {
                        this.inString = !this.inString;
                    }
                    ++i;
                    continue;
                }
                if (c > 127) {
                    i += PPrint.getUTF8(valueChars, i, ci);
                    c = ci[0];
                }
                ++i;
                if (c == 10) {
                    this.flushLine(fout, indent);
                    continue;
                }
                this.printChar(c, mode);
            }
        }
        this.inString = false;
        this.addC(delim, this.linelen++);
    }

    private void printAttribute(Out fout, int indent, Node node, AttVal attr) {
        boolean wrappable = false;
        if (this.configuration.indentAttributes) {
            this.flushLine(fout, indent);
            indent += this.configuration.spaces;
        }
        String name = attr.attribute;
        if (indent + this.linelen >= this.configuration.wraplen) {
            this.wrapLine(fout, indent);
        }
        if (!this.configuration.xmlTags && !this.configuration.xmlOut && attr.dict != null) {
            if (AttributeTable.getDefaultAttributeTable().isScript(name)) {
                wrappable = this.configuration.wrapScriptlets;
            } else if (!attr.dict.isNowrap() && this.configuration.wrapAttVals) {
                wrappable = true;
            }
        }
        if (indent + this.linelen < this.configuration.wraplen) {
            this.wraphere = this.linelen;
            this.addC(32, this.linelen++);
        } else {
            this.condFlushLine(fout, indent);
            this.addC(32, this.linelen++);
        }
        for (int i = 0; i < name.length(); ++i) {
            this.addC(TidyUtils.foldCase(name.charAt(i), this.configuration.upperCaseAttrs, this.configuration.xmlTags), this.linelen++);
        }
        if (indent + this.linelen >= this.configuration.wraplen) {
            this.wrapLine(fout, indent);
        }
        if (attr.value == null) {
            if (this.configuration.xmlTags || this.configuration.xmlOut) {
                this.printAttrValue(fout, indent, attr.isBoolAttribute() ? attr.attribute : "", attr.delim, true);
            } else if (!attr.isBoolAttribute() && node != null && !node.isNewNode()) {
                this.printAttrValue(fout, indent, "", attr.delim, true);
            } else if (indent + this.linelen < this.configuration.wraplen) {
                this.wraphere = this.linelen;
            }
        } else {
            this.printAttrValue(fout, indent, attr.value, attr.delim, wrappable);
        }
    }

    private void printAttrs(Out fout, int indent, Node node, AttVal attr) {
        if (this.configuration.xmlOut && this.configuration.xmlSpace && ParserImpl.XMLPreserveWhiteSpace(node, this.configuration.tt) && node.getAttrByName("xml:space") == null) {
            node.addAttribute("xml:space", "preserve");
            if (attr != null) {
                attr = node.attributes;
            }
        }
        if (attr != null) {
            if (attr.next != null) {
                this.printAttrs(fout, indent, node, attr.next);
            }
            if (attr.attribute != null) {
                Attribute attribute = attr.dict;
                if (!this.configuration.dropProprietaryAttributes || attribute != null && !TidyUtils.toBoolean(attribute.getVersions() & 0x1C0)) {
                    this.printAttribute(fout, indent, node, attr);
                }
            } else if (attr.asp != null) {
                this.addC(32, this.linelen++);
                this.printAsp(fout, indent, attr.asp);
            } else if (attr.php != null) {
                this.addC(32, this.linelen++);
                this.printPhp(fout, indent, attr.php);
            }
        }
    }

    private static boolean afterSpace(Node node) {
        if (node == null || node.tag == null || !TidyUtils.toBoolean(node.tag.model & 0x10)) {
            return true;
        }
        Node prev = node.prev;
        if (prev != null) {
            int c;
            return prev.type == 4 && prev.end > prev.start && ((c = prev.textarray[prev.end - 1] & 0xFF) == 160 || c == 32 || c == 10);
        }
        return PPrint.afterSpace(node.parent);
    }

    private void printTag(Lexer lexer, Out fout, short mode, int indent, Node node) {
        TagTable tt = this.configuration.tt;
        this.addC(60, this.linelen++);
        if (node.type == 6) {
            this.addC(47, this.linelen++);
        }
        String p = node.element;
        for (int i = 0; i < p.length(); ++i) {
            this.addC(TidyUtils.foldCase(p.charAt(i), this.configuration.upperCaseTags, this.configuration.xmlTags), this.linelen++);
        }
        this.printAttrs(fout, indent, node, node.attributes);
        if ((this.configuration.xmlOut || this.configuration.xHTML) && (node.type == 7 || TidyUtils.toBoolean(node.tag.model & 1))) {
            this.addC(32, this.linelen++);
            this.addC(47, this.linelen++);
        }
        this.addC(62, this.linelen++);
        if ((node.type != 7 || this.configuration.xHTML) && !TidyUtils.toBoolean(mode & 1)) {
            if (indent + this.linelen >= this.configuration.wraplen) {
                this.wrapLine(fout, indent);
            }
            if (!(indent + this.linelen >= this.configuration.wraplen || TidyUtils.toBoolean(mode & 8) || TidyUtils.toBoolean(node.tag.model & 0x10) && node.tag != tt.tagBr || !PPrint.afterSpace(node))) {
                this.wraphere = this.linelen;
            }
        } else {
            this.condFlushLine(fout, indent);
        }
    }

    private void printEndTag(short mode, int indent, Node node) {
        this.addC(60, this.linelen++);
        this.addC(47, this.linelen++);
        String p = node.element;
        for (int i = 0; i < p.length(); ++i) {
            this.addC(TidyUtils.foldCase(p.charAt(i), this.configuration.upperCaseTags, this.configuration.xmlTags), this.linelen++);
        }
        this.addC(62, this.linelen++);
    }

    private void printComment(Out fout, int indent, Node node) {
        if (this.configuration.hideComments) {
            return;
        }
        if (indent + this.linelen < this.configuration.wraplen) {
            this.wraphere = this.linelen;
        }
        this.addC(60, this.linelen++);
        this.addC(33, this.linelen++);
        this.addC(45, this.linelen++);
        this.addC(45, this.linelen++);
        this.printText(fout, (short)2, indent, node.textarray, node.start, node.end);
        this.addC(45, this.linelen++);
        this.addC(45, this.linelen++);
        this.addC(62, this.linelen++);
        if (node.linebreak) {
            this.flushLine(fout, indent);
        }
    }

    private void printDocType(Out fout, int indent, Lexer lexer, Node node) {
        int c = 0;
        short mode = 0;
        boolean q = this.configuration.quoteMarks;
        this.configuration.quoteMarks = false;
        if (indent + this.linelen < this.configuration.wraplen) {
            this.wraphere = this.linelen;
        }
        this.condFlushLine(fout, indent);
        this.addC(60, this.linelen++);
        this.addC(33, this.linelen++);
        this.addC(68, this.linelen++);
        this.addC(79, this.linelen++);
        this.addC(67, this.linelen++);
        this.addC(84, this.linelen++);
        this.addC(89, this.linelen++);
        this.addC(80, this.linelen++);
        this.addC(69, this.linelen++);
        this.addC(32, this.linelen++);
        if (indent + this.linelen < this.configuration.wraplen) {
            this.wraphere = this.linelen;
        }
        for (int i = node.start; i < node.end; ++i) {
            if (indent + this.linelen >= this.configuration.wraplen) {
                this.wrapLine(fout, indent);
            }
            c = node.textarray[i] & 0xFF;
            if (TidyUtils.toBoolean(mode & 0x10)) {
                if (c == 93) {
                    mode = (short)(mode & 0xFFFFFFEF);
                }
            } else if (c == 91) {
                mode = (short)(mode | 0x10);
            }
            int[] ci = new int[1];
            if (c > 127) {
                i += PPrint.getUTF8(node.textarray, i, ci);
                c = ci[0];
            }
            if (c == 10) {
                this.flushLine(fout, indent);
                continue;
            }
            this.printChar(c, mode);
        }
        if (this.linelen < this.configuration.wraplen) {
            this.wraphere = this.linelen;
        }
        this.addC(62, this.linelen++);
        this.configuration.quoteMarks = q;
        this.condFlushLine(fout, indent);
    }

    private void printPI(Out fout, int indent, Node node) {
        if (indent + this.linelen < this.configuration.wraplen) {
            this.wraphere = this.linelen;
        }
        this.addC(60, this.linelen++);
        this.addC(63, this.linelen++);
        this.printText(fout, (short)16, indent, node.textarray, node.start, node.end);
        if (node.end <= 0 || node.textarray[node.end - 1] != 63) {
            this.addC(63, this.linelen++);
        }
        this.addC(62, this.linelen++);
        this.condFlushLine(fout, indent);
    }

    private void printXmlDecl(Out fout, int indent, Node node) {
        if (indent + this.linelen < this.configuration.wraplen) {
            this.wraphere = this.linelen;
        }
        this.addC(60, this.linelen++);
        this.addC(63, this.linelen++);
        this.addC(120, this.linelen++);
        this.addC(109, this.linelen++);
        this.addC(108, this.linelen++);
        this.printAttrs(fout, indent, node, node.attributes);
        if (node.end <= 0 || node.textarray[node.end - 1] != 63) {
            this.addC(63, this.linelen++);
        }
        this.addC(62, this.linelen++);
        this.condFlushLine(fout, indent);
    }

    private void printAsp(Out fout, int indent, Node node) {
        int savewraplen = this.configuration.wraplen;
        if (!this.configuration.wrapAsp || !this.configuration.wrapJste) {
            this.configuration.wraplen = 0xFFFFFF;
        }
        this.addC(60, this.linelen++);
        this.addC(37, this.linelen++);
        this.printText(fout, this.configuration.wrapAsp ? (short)16 : 2, indent, node.textarray, node.start, node.end);
        this.addC(37, this.linelen++);
        this.addC(62, this.linelen++);
        this.configuration.wraplen = savewraplen;
    }

    private void printJste(Out fout, int indent, Node node) {
        int savewraplen = this.configuration.wraplen;
        if (!this.configuration.wrapJste) {
            this.configuration.wraplen = 0xFFFFFF;
        }
        this.addC(60, this.linelen++);
        this.addC(35, this.linelen++);
        this.printText(fout, this.configuration.wrapJste ? (short)16 : 2, indent, node.textarray, node.start, node.end);
        this.addC(35, this.linelen++);
        this.addC(62, this.linelen++);
        this.configuration.wraplen = savewraplen;
    }

    private void printPhp(Out fout, int indent, Node node) {
        int savewraplen = this.configuration.wraplen;
        if (!this.configuration.wrapPhp) {
            this.configuration.wraplen = 0xFFFFFF;
        }
        this.addC(60, this.linelen++);
        this.addC(63, this.linelen++);
        this.printText(fout, this.configuration.wrapPhp ? (short)16 : 2, indent, node.textarray, node.start, node.end);
        this.addC(63, this.linelen++);
        this.addC(62, this.linelen++);
        this.configuration.wraplen = savewraplen;
    }

    private void printCDATA(Out fout, int indent, Node node) {
        int savewraplen = this.configuration.wraplen;
        if (!this.configuration.indentCdata) {
            indent = 0;
        }
        this.condFlushLine(fout, indent);
        this.configuration.wraplen = 0xFFFFFF;
        this.addC(60, this.linelen++);
        this.addC(33, this.linelen++);
        this.addC(91, this.linelen++);
        this.addC(67, this.linelen++);
        this.addC(68, this.linelen++);
        this.addC(65, this.linelen++);
        this.addC(84, this.linelen++);
        this.addC(65, this.linelen++);
        this.addC(91, this.linelen++);
        this.printText(fout, (short)2, indent, node.textarray, node.start, node.end);
        this.addC(93, this.linelen++);
        this.addC(93, this.linelen++);
        this.addC(62, this.linelen++);
        this.condFlushLine(fout, indent);
        this.configuration.wraplen = savewraplen;
    }

    private void printSection(Out fout, int indent, Node node) {
        int savewraplen = this.configuration.wraplen;
        if (!this.configuration.wrapSection) {
            this.configuration.wraplen = 0xFFFFFF;
        }
        this.addC(60, this.linelen++);
        this.addC(33, this.linelen++);
        this.addC(91, this.linelen++);
        this.printText(fout, this.configuration.wrapSection ? (short)16 : 2, indent, node.textarray, node.start, node.end);
        this.addC(93, this.linelen++);
        this.addC(62, this.linelen++);
        this.configuration.wraplen = savewraplen;
    }

    private boolean insideHead(Node node) {
        if (node.tag == this.configuration.tt.tagHead) {
            return true;
        }
        if (node.parent != null) {
            return this.insideHead(node.parent);
        }
        return false;
    }

    private int textEndsWithNewline(Lexer lexer, Node node) {
        if (node.type == 4 && node.end > node.start) {
            int ch;
            int ix;
            for (ix = node.end - 1; ix >= node.start && TidyUtils.toBoolean(ch = node.textarray[ix] & 0xFF) && (ch == 32 || ch == 9 || ch == 13); --ix) {
            }
            if (node.textarray[ix] == 10) {
                return node.end - ix - 1;
            }
        }
        return -1;
    }

    static boolean hasCDATA(Lexer lexer, Node node) {
        if (node.type != 4) {
            return false;
        }
        int len = node.end - node.start + 1;
        String start = TidyUtils.getString(node.textarray, node.start, len);
        int indexOfCData = start.indexOf(CDATA_START);
        return indexOfCData > -1 && indexOfCData <= len;
    }

    private void printScriptStyle(Out fout, short mode, int indent, Lexer lexer, Node node) {
        int savewraplen;
        String commentStart = "";
        String commentEnd = "";
        boolean hasCData = false;
        int contentIndent = -1;
        if (this.insideHead(node)) {
            // empty if block
        }
        indent = 0;
        this.printTag(lexer, fout, mode, indent, node);
        if (lexer.configuration.xHTML && node.content != null) {
            AttVal type = node.getAttrByName("type");
            if (type != null) {
                if ("text/javascript".equalsIgnoreCase(type.value)) {
                    commentStart = JS_COMMENT_START;
                    commentEnd = "";
                } else if ("text/css".equalsIgnoreCase(type.value)) {
                    commentStart = CSS_COMMENT_START;
                    commentEnd = CSS_COMMENT_END;
                } else if ("text/vbscript".equalsIgnoreCase(type.value)) {
                    commentStart = VB_COMMENT_START;
                    commentEnd = "";
                }
            }
            if (!(hasCData = PPrint.hasCDATA(lexer, node.content))) {
                savewraplen = lexer.configuration.wraplen;
                lexer.configuration.wraplen = 0xFFFFFF;
                this.linelen = this.addAsciiString(commentStart, this.linelen);
                this.linelen = this.addAsciiString(CDATA_START, this.linelen);
                this.linelen = this.addAsciiString(commentEnd, this.linelen);
                this.condFlushLine(fout, indent);
                lexer.configuration.wraplen = savewraplen;
            }
        }
        Node content = node.content;
        while (content != null) {
            this.printTree(fout, (short)(mode | 1 | 8 | 0x10), 0, lexer, content);
            if (content.next == null) {
                contentIndent = this.textEndsWithNewline(lexer, content);
            }
            content = content.next;
        }
        if (contentIndent < 0) {
            this.condFlushLine(fout, indent);
            contentIndent = 0;
        }
        if (lexer.configuration.xHTML && node.content != null && !hasCData) {
            savewraplen = lexer.configuration.wraplen;
            lexer.configuration.wraplen = 0xFFFFFF;
            if (contentIndent > 0 && this.linelen < contentIndent) {
                this.linelen = contentIndent;
            }
            for (int ix = 0; contentIndent < indent && ix < indent - contentIndent; ++ix) {
                this.addC(32, this.linelen++);
            }
            this.linelen = this.addAsciiString(commentStart, this.linelen);
            this.linelen = this.addAsciiString(CDATA_END, this.linelen);
            this.linelen = this.addAsciiString(commentEnd, this.linelen);
            lexer.configuration.wraplen = savewraplen;
            this.condFlushLine(fout, 0);
        }
        this.printEndTag(mode, indent, node);
        if (!(lexer.configuration.indentContent || node.next == null || node.tag != null && TidyUtils.toBoolean(node.tag.model & 0x10) || node.type != 4)) {
            this.flushLine(fout, indent);
        }
        this.flushLine(fout, indent);
    }

    private boolean shouldIndent(Node node) {
        TagTable tt = this.configuration.tt;
        if (!this.configuration.indentContent) {
            return false;
        }
        if (this.configuration.smartIndent) {
            if (node.content != null && TidyUtils.toBoolean(node.tag.model & 0x40000)) {
                node = node.content;
                while (node != null) {
                    if (node.tag != null && TidyUtils.toBoolean(node.tag.model & 8)) {
                        return true;
                    }
                    node = node.next;
                }
                return false;
            }
            if (TidyUtils.toBoolean(node.tag.model & 0x4000)) {
                return false;
            }
            if (node.tag == tt.tagP) {
                return false;
            }
            if (node.tag == tt.tagTitle) {
                return false;
            }
        }
        if (TidyUtils.toBoolean(node.tag.model & 0xC00)) {
            return true;
        }
        if (node.tag == tt.tagMap) {
            return true;
        }
        return !TidyUtils.toBoolean(node.tag.model & 0x10);
    }

    void printBody(Out fout, Lexer lexer, Node root, boolean xml) {
        if (root == null) {
            return;
        }
        Node body = root.findBody(lexer.configuration.tt);
        if (body != null) {
            Node content = body.content;
            while (content != null) {
                if (xml) {
                    this.printXMLTree(fout, (short)0, 0, lexer, content);
                } else {
                    this.printTree(fout, (short)0, 0, lexer, content);
                }
                content = content.next;
            }
        }
    }

    public void printTree(Out fout, short mode, int indent, Lexer lexer, Node node) {
        TagTable tt = this.configuration.tt;
        if (node == null) {
            return;
        }
        if (node.type == 4 || node.type == 8 && lexer.configuration.escapeCdata) {
            this.printText(fout, mode, indent, node.textarray, node.start, node.end);
        } else if (node.type == 2) {
            this.printComment(fout, indent, node);
        } else if (node.type == 0) {
            Node content = node.content;
            while (content != null) {
                this.printTree(fout, mode, indent, lexer, content);
                content = content.next;
            }
        } else if (node.type == 1) {
            this.printDocType(fout, indent, lexer, node);
        } else if (node.type == 3) {
            this.printPI(fout, indent, node);
        } else if (node.type == 13) {
            this.printXmlDecl(fout, indent, node);
        } else if (node.type == 8) {
            this.printCDATA(fout, indent, node);
        } else if (node.type == 9) {
            this.printSection(fout, indent, node);
        } else if (node.type == 10) {
            this.printAsp(fout, indent, node);
        } else if (node.type == 11) {
            this.printJste(fout, indent, node);
        } else if (node.type == 12) {
            this.printPhp(fout, indent, node);
        } else if (TidyUtils.toBoolean(node.tag.model & 1) || node.type == 7 && !this.configuration.xHTML) {
            if (!TidyUtils.toBoolean(node.tag.model & 0x10)) {
                this.condFlushLine(fout, indent);
            }
            if (node.tag == tt.tagBr && node.prev != null && node.prev.tag != tt.tagBr && this.configuration.breakBeforeBR) {
                this.flushLine(fout, indent);
            }
            if (this.configuration.makeClean && node.tag == tt.tagWbr) {
                this.printString(" ");
            } else {
                this.printTag(lexer, fout, mode, indent, node);
            }
            if (node.tag == tt.tagParam || node.tag == tt.tagArea) {
                this.condFlushLine(fout, indent);
            } else if (node.tag == tt.tagBr || node.tag == tt.tagHr) {
                this.flushLine(fout, indent);
            }
        } else {
            if (node.type == 7) {
                node.type = (short)5;
            }
            if (node.tag != null && node.tag.getParser() == ParserImpl.PRE) {
                this.condFlushLine(fout, indent);
                indent = 0;
                this.condFlushLine(fout, indent);
                this.printTag(lexer, fout, mode, indent, node);
                this.flushLine(fout, indent);
                Node content = node.content;
                while (content != null) {
                    this.printTree(fout, (short)(mode | 1 | 8), indent, lexer, content);
                    content = content.next;
                }
                this.condFlushLine(fout, indent);
                this.printEndTag(mode, indent, node);
                this.flushLine(fout, indent);
                if (!this.configuration.indentContent && node.next != null) {
                    this.flushLine(fout, indent);
                }
            } else if (node.tag == tt.tagStyle || node.tag == tt.tagScript) {
                this.printScriptStyle(fout, (short)(mode | 1 | 8 | 0x10), indent, lexer, node);
            } else if (TidyUtils.toBoolean(node.tag.model & 0x10)) {
                if (this.configuration.makeClean) {
                    if (node.tag == tt.tagFont) {
                        Node content = node.content;
                        while (content != null) {
                            this.printTree(fout, mode, indent, lexer, content);
                            content = content.next;
                        }
                        return;
                    }
                    if (node.tag == tt.tagNobr) {
                        Node content = node.content;
                        while (content != null) {
                            this.printTree(fout, (short)(mode | 8), indent, lexer, content);
                            content = content.next;
                        }
                        return;
                    }
                }
                this.printTag(lexer, fout, mode, indent, node);
                if (this.shouldIndent(node)) {
                    this.condFlushLine(fout, indent);
                    indent += this.configuration.spaces;
                    Node content = node.content;
                    while (content != null) {
                        this.printTree(fout, mode, indent, lexer, content);
                        content = content.next;
                    }
                    this.condFlushLine(fout, indent);
                    this.condFlushLine(fout, indent -= this.configuration.spaces);
                } else {
                    Node content = node.content;
                    while (content != null) {
                        this.printTree(fout, mode, indent, lexer, content);
                        content = content.next;
                    }
                }
                this.printEndTag(mode, indent, node);
            } else {
                this.condFlushLine(fout, indent);
                if (this.configuration.smartIndent && node.prev != null) {
                    this.flushLine(fout, indent);
                }
                if (!this.configuration.hideEndTags || node.tag == null || !TidyUtils.toBoolean(node.tag.model & 0x200000) || node.attributes != null) {
                    this.printTag(lexer, fout, mode, indent, node);
                    if (this.shouldIndent(node)) {
                        this.condFlushLine(fout, indent);
                    } else if (TidyUtils.toBoolean(node.tag.model & 2) || node.tag == tt.tagNoframes || TidyUtils.toBoolean(node.tag.model & 4) && node.tag != tt.tagTitle) {
                        this.flushLine(fout, indent);
                    }
                }
                if (node.tag == tt.tagBody && this.configuration.burstSlides) {
                    this.printSlide(fout, mode, this.configuration.indentContent ? indent + this.configuration.spaces : indent, lexer);
                } else {
                    Node last = null;
                    Node content = node.content;
                    while (content != null) {
                        if (last != null && !this.configuration.indentContent && last.type == 4 && content.tag != null && !TidyUtils.toBoolean(content.tag.model & 0x10)) {
                            this.flushLine(fout, indent);
                        }
                        this.printTree(fout, mode, this.shouldIndent(node) ? indent + this.configuration.spaces : indent, lexer, content);
                        last = content;
                        content = content.next;
                    }
                }
                if (this.shouldIndent(node) || (TidyUtils.toBoolean(node.tag.model & 2) || node.tag == tt.tagNoframes || TidyUtils.toBoolean(node.tag.model & 4) && node.tag != tt.tagTitle) && !this.configuration.hideEndTags) {
                    this.condFlushLine(fout, this.configuration.indentContent ? indent + this.configuration.spaces : indent);
                    if (!this.configuration.hideEndTags || !TidyUtils.toBoolean(node.tag.model & 0x8000)) {
                        this.printEndTag(mode, indent, node);
                        if (!lexer.seenEndHtml) {
                            this.flushLine(fout, indent);
                        }
                    }
                } else {
                    if (!this.configuration.hideEndTags || !TidyUtils.toBoolean(node.tag.model & 0x8000)) {
                        this.printEndTag(mode, indent, node);
                    }
                    this.flushLine(fout, indent);
                }
            }
        }
    }

    public void printXMLTree(Out fout, short mode, int indent, Lexer lexer, Node node) {
        TagTable tt = this.configuration.tt;
        if (node == null) {
            return;
        }
        if (node.type == 4 || node.type == 8 && lexer.configuration.escapeCdata) {
            this.printText(fout, mode, indent, node.textarray, node.start, node.end);
        } else if (node.type == 2) {
            this.condFlushLine(fout, indent);
            this.printComment(fout, 0, node);
            this.condFlushLine(fout, 0);
        } else if (node.type == 0) {
            Node content = node.content;
            while (content != null) {
                this.printXMLTree(fout, mode, indent, lexer, content);
                content = content.next;
            }
        } else if (node.type == 1) {
            this.printDocType(fout, indent, lexer, node);
        } else if (node.type == 3) {
            this.printPI(fout, indent, node);
        } else if (node.type == 13) {
            this.printXmlDecl(fout, indent, node);
        } else if (node.type == 8) {
            this.printCDATA(fout, indent, node);
        } else if (node.type == 9) {
            this.printSection(fout, indent, node);
        } else if (node.type == 10) {
            this.printAsp(fout, indent, node);
        } else if (node.type == 11) {
            this.printJste(fout, indent, node);
        } else if (node.type == 12) {
            this.printPhp(fout, indent, node);
        } else if (TidyUtils.toBoolean(node.tag.model & 1) || node.type == 7 && !this.configuration.xHTML) {
            this.condFlushLine(fout, indent);
            this.printTag(lexer, fout, mode, indent, node);
        } else {
            int cindent;
            boolean mixed = false;
            Node content = node.content;
            while (content != null) {
                if (content.type == 4) {
                    mixed = true;
                    break;
                }
                content = content.next;
            }
            this.condFlushLine(fout, indent);
            if (ParserImpl.XMLPreserveWhiteSpace(node, tt)) {
                indent = 0;
                cindent = 0;
                mixed = false;
            } else {
                cindent = mixed ? indent : indent + this.configuration.spaces;
            }
            this.printTag(lexer, fout, mode, indent, node);
            if (!mixed && node.content != null) {
                this.flushLine(fout, indent);
            }
            content = node.content;
            while (content != null) {
                this.printXMLTree(fout, mode, cindent, lexer, content);
                content = content.next;
            }
            if (!mixed && node.content != null) {
                this.condFlushLine(fout, cindent);
            }
            this.printEndTag(mode, indent, node);
        }
    }

    public int countSlides(Node node) {
        int n = 1;
        TagTable tt = this.configuration.tt;
        if (node != null && node.content != null && node.content.tag == tt.tagH2) {
            --n;
        }
        if (node != null) {
            node = node.content;
            while (node != null) {
                if (node.tag == tt.tagH2) {
                    ++n;
                }
                node = node.next;
            }
        }
        return n;
    }

    private void printNavBar(Out fout, int indent) {
        String buf;
        this.condFlushLine(fout, indent);
        this.printString("<center><small>");
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumIntegerDigits(3);
        if (this.slide > 1) {
            buf = "<a href=\"slide" + numberFormat.format(this.slide - 1) + ".html\">previous</a> | ";
            this.printString(buf);
            this.condFlushLine(fout, indent);
            if (this.slide < this.count) {
                this.printString("<a href=\"slide001.html\">start</a> | ");
            } else {
                this.printString("<a href=\"slide001.html\">start</a>");
            }
            this.condFlushLine(fout, indent);
        }
        if (this.slide < this.count) {
            buf = "<a href=\"slide" + numberFormat.format(this.slide + 1) + ".html\">next</a>";
            this.printString(buf);
        }
        this.printString("</small></center>");
        this.condFlushLine(fout, indent);
    }

    public void printSlide(Out fout, short mode, int indent, Lexer lexer) {
        TagTable tt = this.configuration.tt;
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumIntegerDigits(3);
        String s = "<div onclick=\"document.location='slide" + numberFormat.format(this.slide < this.count ? (long)(this.slide + 1) : 1L) + ".html'\">";
        this.printString(s);
        this.condFlushLine(fout, indent);
        if (this.slidecontent != null && this.slidecontent.tag == tt.tagH2) {
            this.printNavBar(fout, indent);
            this.addC(60, this.linelen++);
            this.addC(TidyUtils.foldCase('h', this.configuration.upperCaseTags, this.configuration.xmlTags), this.linelen++);
            this.addC(TidyUtils.foldCase('r', this.configuration.upperCaseTags, this.configuration.xmlTags), this.linelen++);
            if (this.configuration.xmlOut) {
                this.printString(" />");
            } else {
                this.addC(62, this.linelen++);
            }
            if (this.configuration.indentContent) {
                this.condFlushLine(fout, indent);
            }
            this.printTree(fout, mode, this.configuration.indentContent ? indent + this.configuration.spaces : indent, lexer, this.slidecontent);
            this.slidecontent = this.slidecontent.next;
        }
        Node last = null;
        Node content = this.slidecontent;
        while (content != null && content.tag != tt.tagH2) {
            if (last != null && !this.configuration.indentContent && last.type == 4 && content.tag != null && TidyUtils.toBoolean(content.tag.model & 8)) {
                this.flushLine(fout, indent);
                this.flushLine(fout, indent);
            }
            this.printTree(fout, mode, this.configuration.indentContent ? indent + this.configuration.spaces : indent, lexer, content);
            last = content;
            content = content.next;
        }
        this.slidecontent = content;
        this.condFlushLine(fout, indent);
        this.printString("<br clear=\"all\">");
        this.condFlushLine(fout, indent);
        this.addC(60, this.linelen++);
        this.addC(TidyUtils.foldCase('h', this.configuration.upperCaseTags, this.configuration.xmlTags), this.linelen++);
        this.addC(TidyUtils.foldCase('r', this.configuration.upperCaseTags, this.configuration.xmlTags), this.linelen++);
        if (this.configuration.xmlOut) {
            this.printString(" />");
        } else {
            this.addC(62, this.linelen++);
        }
        if (this.configuration.indentContent) {
            this.condFlushLine(fout, indent);
        }
        this.printNavBar(fout, indent);
        this.printString("</div>");
        this.condFlushLine(fout, indent);
    }

    public void addTransitionEffect(Lexer lexer, Node root, double duration) {
        Node head = root.findHEAD(lexer.configuration.tt);
        String transition = "blendTrans(Duration=" + new Double(duration).toString() + ")";
        if (head != null) {
            Node meta = lexer.inferredTag("meta");
            meta.addAttribute("http-equiv", "Page-Enter");
            meta.addAttribute("content", transition);
            head.insertNodeAtStart(meta);
        }
    }

    public void createSlides(Lexer lexer, Node root) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumIntegerDigits(3);
        Node body = root.findBody(lexer.configuration.tt);
        this.count = this.countSlides(body);
        this.slidecontent = body.content;
        this.addTransitionEffect(lexer, root, 3.0);
        this.slide = 1;
        while (this.slide <= this.count) {
            String buf = "slide" + numberFormat.format(this.slide) + ".html";
            try {
                FileOutputStream fis = new FileOutputStream(buf);
                Out out = OutFactory.getOut(this.configuration, fis);
                this.printTree(out, (short)0, 0, lexer, root);
                this.flushLine(out, 0);
                fis.close();
            }
            catch (IOException e) {
                System.err.println(buf + e.toString());
            }
            ++this.slide;
        }
        while (new File("slide" + numberFormat.format(this.slide) + ".html").delete()) {
            ++this.slide;
        }
    }
}

