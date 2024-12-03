/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.xml.simpleparser;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.xml.simpleparser.EntitiesToUnicode;
import com.lowagie.text.xml.simpleparser.IanaEncodings;
import com.lowagie.text.xml.simpleparser.SimpleXMLDocHandler;
import com.lowagie.text.xml.simpleparser.SimpleXMLDocHandlerComment;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import org.mozilla.universalchardet.UniversalDetector;

public final class SimpleXMLParser {
    private static final int UNKNOWN = 0;
    private static final int TEXT = 1;
    private static final int TAG_ENCOUNTERED = 2;
    private static final int EXAMIN_TAG = 3;
    private static final int TAG_EXAMINED = 4;
    private static final int IN_CLOSETAG = 5;
    private static final int SINGLE_TAG = 6;
    private static final int CDATA = 7;
    private static final int COMMENT = 8;
    private static final int PI = 9;
    private static final int ENTITY = 10;
    private static final int QUOTE = 11;
    private static final int ATTRIBUTE_KEY = 12;
    private static final int ATTRIBUTE_EQUAL = 13;
    private static final int ATTRIBUTE_VALUE = 14;
    Stack<Integer> stack;
    int character = 0;
    int previousCharacter = -1;
    int lines = 1;
    int columns = 0;
    boolean eol = false;
    boolean nowhite = false;
    int state;
    boolean html;
    StringBuffer text = new StringBuffer();
    StringBuffer entity = new StringBuffer();
    String tag = null;
    Map<String, String> attributes = null;
    SimpleXMLDocHandler doc;
    SimpleXMLDocHandlerComment comment;
    int nested = 0;
    int quoteCharacter = 34;
    String attributekey = null;
    String attributevalue = null;

    private SimpleXMLParser(SimpleXMLDocHandler doc, SimpleXMLDocHandlerComment comment, boolean html) {
        this.doc = doc;
        this.comment = comment;
        this.html = html;
        this.stack = new Stack();
        this.state = html ? 1 : 0;
    }

    private void go(Reader r) throws IOException {
        BufferedReader reader = r instanceof BufferedReader ? (BufferedReader)r : new BufferedReader(r);
        this.doc.startDocument();
        block17: while (true) {
            if (this.previousCharacter == -1) {
                this.character = reader.read();
            } else {
                this.character = this.previousCharacter;
                this.previousCharacter = -1;
            }
            if (this.character == -1) {
                if (this.html) {
                    if (this.html && this.state == 1) {
                        this.flush();
                    }
                    this.doc.endDocument();
                } else {
                    this.throwException(MessageLocalization.getComposedMessage("missing.end.tag"));
                }
                return;
            }
            if (this.character == 10 && this.eol) {
                this.eol = false;
                continue;
            }
            if (this.eol) {
                this.eol = false;
            } else if (this.character == 10) {
                ++this.lines;
                this.columns = 0;
            } else if (this.character == 13) {
                this.eol = true;
                this.character = 10;
                ++this.lines;
                this.columns = 0;
            } else {
                ++this.columns;
            }
            switch (this.state) {
                case 0: {
                    if (this.character != 60) break;
                    this.saveState(1);
                    this.state = 2;
                    break;
                }
                case 1: {
                    if (this.character == 60) {
                        this.flush();
                        this.saveState(this.state);
                        this.state = 2;
                        break;
                    }
                    if (this.character == 38) {
                        this.saveState(this.state);
                        this.entity.setLength(0);
                        this.state = 10;
                        this.nowhite = true;
                        break;
                    }
                    if (Character.isWhitespace((char)this.character)) {
                        if (this.nowhite) {
                            this.text.append((char)this.character);
                        }
                        this.nowhite = false;
                        break;
                    }
                    this.text.append((char)this.character);
                    this.nowhite = true;
                    break;
                }
                case 2: {
                    this.initTag();
                    if (this.character == 47) {
                        this.state = 5;
                        break;
                    }
                    if (this.character == 63) {
                        this.restoreState();
                        this.state = 9;
                        break;
                    }
                    this.text.append((char)this.character);
                    this.state = 3;
                    break;
                }
                case 3: {
                    if (this.character == 62) {
                        this.doTag();
                        this.processTag(true);
                        this.initTag();
                        this.state = this.restoreState();
                        break;
                    }
                    if (this.character == 47) {
                        this.state = 6;
                        break;
                    }
                    if (this.character == 45 && this.text.toString().equals("!-")) {
                        this.flush();
                        this.state = 8;
                        break;
                    }
                    if (this.character == 91 && this.text.toString().equals("![CDATA")) {
                        this.flush();
                        this.state = 7;
                        break;
                    }
                    if (this.character == 69 && this.text.toString().equals("!DOCTYP")) {
                        this.flush();
                        this.state = 9;
                        break;
                    }
                    if (Character.isWhitespace((char)this.character)) {
                        this.doTag();
                        this.state = 4;
                        break;
                    }
                    this.text.append((char)this.character);
                    break;
                }
                case 4: {
                    if (this.character == 62) {
                        this.processTag(true);
                        this.initTag();
                        this.state = this.restoreState();
                        break;
                    }
                    if (this.character == 47) {
                        this.state = 6;
                        break;
                    }
                    if (Character.isWhitespace((char)this.character)) continue block17;
                    this.text.append((char)this.character);
                    this.state = 12;
                    break;
                }
                case 5: {
                    if (this.character == 62) {
                        this.doTag();
                        this.processTag(false);
                        if (!this.html && this.nested == 0) {
                            return;
                        }
                        this.state = this.restoreState();
                        break;
                    }
                    if (Character.isWhitespace((char)this.character)) break;
                    this.text.append((char)this.character);
                    break;
                }
                case 6: {
                    if (this.character != 62) {
                        this.throwException(MessageLocalization.getComposedMessage("expected.gt.for.tag.lt.1.gt", this.tag));
                    }
                    this.doTag();
                    this.processTag(true);
                    this.processTag(false);
                    this.initTag();
                    if (!this.html && this.nested == 0) {
                        this.doc.endDocument();
                        return;
                    }
                    this.state = this.restoreState();
                    break;
                }
                case 7: {
                    if (this.character == 62 && this.text.toString().endsWith("]]")) {
                        this.text.setLength(this.text.length() - 2);
                        this.flush();
                        this.state = this.restoreState();
                        break;
                    }
                    this.text.append((char)this.character);
                    break;
                }
                case 8: {
                    if (this.character == 62 && this.text.toString().endsWith("--")) {
                        this.text.setLength(this.text.length() - 2);
                        this.flush();
                        this.state = this.restoreState();
                        break;
                    }
                    this.text.append((char)this.character);
                    break;
                }
                case 9: {
                    if (this.character != 62) break;
                    this.state = this.restoreState();
                    if (this.state != 1) break;
                    this.state = 0;
                    break;
                }
                case 10: {
                    if (this.character == 59) {
                        this.state = this.restoreState();
                        String cent = this.entity.toString();
                        this.entity.setLength(0);
                        char ce = EntitiesToUnicode.decodeEntity(cent);
                        if (ce == '\u0000') {
                            this.text.append('&').append(cent).append(';');
                            break;
                        }
                        this.text.append(ce);
                        break;
                    }
                    if (!((this.character == 35 || this.character >= 48 && this.character <= 57 || this.character >= 97 && this.character <= 122 || this.character >= 65 && this.character <= 90) && this.entity.length() < 7)) {
                        this.state = this.restoreState();
                        this.previousCharacter = this.character;
                        this.text.append('&').append(this.entity.toString());
                        this.entity.setLength(0);
                        break;
                    }
                    this.entity.append((char)this.character);
                    break;
                }
                case 11: {
                    if (this.html && this.quoteCharacter == 32 && this.character == 62) {
                        this.flush();
                        this.processTag(true);
                        this.initTag();
                        this.state = this.restoreState();
                        break;
                    }
                    if (this.html && this.quoteCharacter == 32 && Character.isWhitespace((char)this.character)) {
                        this.flush();
                        this.state = 4;
                        break;
                    }
                    if (this.html && this.quoteCharacter == 32) {
                        this.text.append((char)this.character);
                        break;
                    }
                    if (this.character == this.quoteCharacter) {
                        this.flush();
                        this.state = 4;
                        break;
                    }
                    if (" \r\n\t".indexOf(this.character) >= 0) {
                        this.text.append(' ');
                        break;
                    }
                    if (this.character == 38) {
                        this.saveState(this.state);
                        this.state = 10;
                        this.entity.setLength(0);
                        break;
                    }
                    this.text.append((char)this.character);
                    break;
                }
                case 12: {
                    if (Character.isWhitespace((char)this.character)) {
                        this.flush();
                        this.state = 13;
                        break;
                    }
                    if (this.character == 61) {
                        this.flush();
                        this.state = 14;
                        break;
                    }
                    if (this.html && this.character == 62) {
                        this.text.setLength(0);
                        this.processTag(true);
                        this.initTag();
                        this.state = this.restoreState();
                        break;
                    }
                    this.text.append((char)this.character);
                    break;
                }
                case 13: {
                    if (this.character == 61) {
                        this.state = 14;
                        break;
                    }
                    if (Character.isWhitespace((char)this.character)) continue block17;
                    if (this.html && this.character == 62) {
                        this.text.setLength(0);
                        this.processTag(true);
                        this.initTag();
                        this.state = this.restoreState();
                        break;
                    }
                    if (this.html && this.character == 47) {
                        this.flush();
                        this.state = 6;
                        break;
                    }
                    if (this.html) {
                        this.flush();
                        this.text.append((char)this.character);
                        this.state = 12;
                        break;
                    }
                    this.throwException(MessageLocalization.getComposedMessage("error.in.attribute.processing"));
                    break;
                }
                case 14: {
                    if (this.character == 34 || this.character == 39) {
                        this.quoteCharacter = this.character;
                        this.state = 11;
                        break;
                    }
                    if (Character.isWhitespace((char)this.character)) continue block17;
                    if (this.html && this.character == 62) {
                        this.flush();
                        this.processTag(true);
                        this.initTag();
                        this.state = this.restoreState();
                        break;
                    }
                    if (this.html) {
                        this.text.append((char)this.character);
                        this.quoteCharacter = 32;
                        this.state = 11;
                        break;
                    }
                    this.throwException(MessageLocalization.getComposedMessage("error.in.attribute.processing"));
                }
            }
        }
    }

    private int restoreState() {
        if (!this.stack.empty()) {
            return this.stack.pop();
        }
        return 0;
    }

    private void saveState(int s) {
        this.stack.push(s);
    }

    private void flush() {
        switch (this.state) {
            case 1: 
            case 7: {
                if (this.text.length() <= 0) break;
                this.doc.text(this.text.toString());
                break;
            }
            case 8: {
                if (this.comment == null) break;
                this.comment.comment(this.text.toString());
                break;
            }
            case 12: {
                this.attributekey = this.text.toString();
                if (!this.html) break;
                this.attributekey = this.attributekey.toLowerCase();
                break;
            }
            case 11: 
            case 14: {
                this.attributevalue = this.text.toString();
                this.attributes.put(this.attributekey, this.attributevalue);
                break;
            }
        }
        this.text.setLength(0);
    }

    private void initTag() {
        this.tag = null;
        this.attributes = new HashMap<String, String>();
    }

    private void doTag() {
        if (this.tag == null) {
            this.tag = this.text.toString();
        }
        if (this.html) {
            this.tag = this.tag.toLowerCase();
        }
        this.text.setLength(0);
    }

    private void processTag(boolean start) {
        if (start) {
            ++this.nested;
            this.doc.startElement(this.tag, this.attributes);
        } else {
            --this.nested;
            this.doc.endElement(this.tag);
        }
    }

    private void throwException(String s) throws IOException {
        throw new IOException(MessageLocalization.getComposedMessage("1.near.line.2.column.3", s, String.valueOf(this.lines), String.valueOf(this.columns)));
    }

    public static void parse(SimpleXMLDocHandler doc, SimpleXMLDocHandlerComment comment, Reader r, boolean html) throws IOException {
        SimpleXMLParser parser = new SimpleXMLParser(doc, comment, html);
        parser.go(r);
    }

    public static void parse(SimpleXMLDocHandler doc, InputStream in) throws IOException {
        byte[] b4 = new byte[4];
        int count = in.read(b4);
        if (count != 4) {
            throw new IOException(MessageLocalization.getComposedMessage("insufficient.length"));
        }
        String encoding = UniversalDetector.detectCharsetFromBOM(b4);
        if (encoding == null) {
            encoding = "UTF-8";
        }
        String decl = null;
        if (encoding.equals("UTF-8")) {
            int c;
            StringBuilder sb = new StringBuilder();
            while ((c = in.read()) != -1 && c != 62) {
                sb.append((char)c);
            }
            decl = sb.toString();
        } else if (encoding.equals("CP037")) {
            int c;
            ByteArrayOutputStream bi = new ByteArrayOutputStream();
            while ((c = in.read()) != -1 && c != 110) {
                bi.write(c);
            }
            decl = new String(bi.toByteArray(), "CP037");
        }
        if (decl != null && (decl = SimpleXMLParser.getDeclaredEncoding(decl)) != null) {
            encoding = decl;
        }
        SimpleXMLParser.parse(doc, new InputStreamReader(in, IanaEncodings.getJavaEncoding(encoding)));
    }

    private static String getDeclaredEncoding(String decl) {
        int idx2;
        if (decl == null) {
            return null;
        }
        int idx = decl.indexOf("encoding");
        if (idx < 0) {
            return null;
        }
        int idx1 = decl.indexOf(34, idx);
        if (idx1 == (idx2 = decl.indexOf(39, idx))) {
            return null;
        }
        if (idx1 < 0 && idx2 > 0 || idx2 > 0 && idx2 < idx1) {
            int idx3 = decl.indexOf(39, idx2 + 1);
            if (idx3 < 0) {
                return null;
            }
            return decl.substring(idx2 + 1, idx3);
        }
        if (idx2 < 0 && idx1 > 0 || idx1 > 0 && idx1 < idx2) {
            int idx3 = decl.indexOf(34, idx1 + 1);
            if (idx3 < 0) {
                return null;
            }
            return decl.substring(idx1 + 1, idx3);
        }
        return null;
    }

    public static void parse(SimpleXMLDocHandler doc, Reader r) throws IOException {
        SimpleXMLParser.parse(doc, null, r, false);
    }
}

