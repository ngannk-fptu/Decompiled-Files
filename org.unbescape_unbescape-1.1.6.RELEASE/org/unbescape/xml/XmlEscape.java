/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.xml;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.unbescape.xml.XmlEscapeLevel;
import org.unbescape.xml.XmlEscapeSymbols;
import org.unbescape.xml.XmlEscapeType;
import org.unbescape.xml.XmlEscapeUtil;

public final class XmlEscape {
    public static String escapeXml10Minimal(String text) {
        return XmlEscape.escapeXml(text, XmlEscapeSymbols.XML10_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static String escapeXml11Minimal(String text) {
        return XmlEscape.escapeXml(text, XmlEscapeSymbols.XML11_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static String escapeXml10AttributeMinimal(String text) {
        return XmlEscape.escapeXml(text, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static String escapeXml11AttributeMinimal(String text) {
        return XmlEscape.escapeXml(text, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static String escapeXml10(String text) {
        return XmlEscape.escapeXml(text, XmlEscapeSymbols.XML10_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static String escapeXml11(String text) {
        return XmlEscape.escapeXml(text, XmlEscapeSymbols.XML11_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static String escapeXml10Attribute(String text) {
        return XmlEscape.escapeXml(text, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static String escapeXml11Attribute(String text) {
        return XmlEscape.escapeXml(text, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static String escapeXml10(String text, XmlEscapeType type, XmlEscapeLevel level) {
        return XmlEscape.escapeXml(text, XmlEscapeSymbols.XML10_SYMBOLS, type, level);
    }

    public static String escapeXml11(String text, XmlEscapeType type, XmlEscapeLevel level) {
        return XmlEscape.escapeXml(text, XmlEscapeSymbols.XML11_SYMBOLS, type, level);
    }

    public static String escapeXml10Attribute(String text, XmlEscapeType type, XmlEscapeLevel level) {
        return XmlEscape.escapeXml(text, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS, type, level);
    }

    public static String escapeXml11Attribute(String text, XmlEscapeType type, XmlEscapeLevel level) {
        return XmlEscape.escapeXml(text, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS, type, level);
    }

    private static String escapeXml(String text, XmlEscapeSymbols symbols, XmlEscapeType type, XmlEscapeLevel level) {
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        return XmlEscapeUtil.escape(text, symbols, type, level);
    }

    public static void escapeXml10Minimal(String text, Writer writer) throws IOException {
        XmlEscape.escapeXml(text, writer, XmlEscapeSymbols.XML10_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml11Minimal(String text, Writer writer) throws IOException {
        XmlEscape.escapeXml(text, writer, XmlEscapeSymbols.XML11_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml10AttributeMinimal(String text, Writer writer) throws IOException {
        XmlEscape.escapeXml(text, writer, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml11AttributeMinimal(String text, Writer writer) throws IOException {
        XmlEscape.escapeXml(text, writer, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml10(String text, Writer writer) throws IOException {
        XmlEscape.escapeXml(text, writer, XmlEscapeSymbols.XML10_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml11(String text, Writer writer) throws IOException {
        XmlEscape.escapeXml(text, writer, XmlEscapeSymbols.XML11_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml10Attribute(String text, Writer writer) throws IOException {
        XmlEscape.escapeXml(text, writer, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml11Attribute(String text, Writer writer) throws IOException {
        XmlEscape.escapeXml(text, writer, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml10(String text, Writer writer, XmlEscapeType type, XmlEscapeLevel level) throws IOException {
        XmlEscape.escapeXml(text, writer, XmlEscapeSymbols.XML10_SYMBOLS, type, level);
    }

    public static void escapeXml11(String text, Writer writer, XmlEscapeType type, XmlEscapeLevel level) throws IOException {
        XmlEscape.escapeXml(text, writer, XmlEscapeSymbols.XML11_SYMBOLS, type, level);
    }

    public static void escapeXml10Attribute(String text, Writer writer, XmlEscapeType type, XmlEscapeLevel level) throws IOException {
        XmlEscape.escapeXml(text, writer, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS, type, level);
    }

    public static void escapeXml11Attribute(String text, Writer writer, XmlEscapeType type, XmlEscapeLevel level) throws IOException {
        XmlEscape.escapeXml(text, writer, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS, type, level);
    }

    private static void escapeXml(String text, Writer writer, XmlEscapeSymbols symbols, XmlEscapeType type, XmlEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        XmlEscapeUtil.escape(new InternalStringReader(text), writer, symbols, type, level);
    }

    public static void escapeXml10Minimal(Reader reader, Writer writer) throws IOException {
        XmlEscape.escapeXml(reader, writer, XmlEscapeSymbols.XML10_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml11Minimal(Reader reader, Writer writer) throws IOException {
        XmlEscape.escapeXml(reader, writer, XmlEscapeSymbols.XML11_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml10AttributeMinimal(Reader reader, Writer writer) throws IOException {
        XmlEscape.escapeXml(reader, writer, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml11AttributeMinimal(Reader reader, Writer writer) throws IOException {
        XmlEscape.escapeXml(reader, writer, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml10(Reader reader, Writer writer) throws IOException {
        XmlEscape.escapeXml(reader, writer, XmlEscapeSymbols.XML10_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml11(Reader reader, Writer writer) throws IOException {
        XmlEscape.escapeXml(reader, writer, XmlEscapeSymbols.XML11_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml10Attribute(Reader reader, Writer writer) throws IOException {
        XmlEscape.escapeXml(reader, writer, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml11Attribute(Reader reader, Writer writer) throws IOException {
        XmlEscape.escapeXml(reader, writer, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml10(Reader reader, Writer writer, XmlEscapeType type, XmlEscapeLevel level) throws IOException {
        XmlEscape.escapeXml(reader, writer, XmlEscapeSymbols.XML10_SYMBOLS, type, level);
    }

    public static void escapeXml11(Reader reader, Writer writer, XmlEscapeType type, XmlEscapeLevel level) throws IOException {
        XmlEscape.escapeXml(reader, writer, XmlEscapeSymbols.XML11_SYMBOLS, type, level);
    }

    public static void escapeXml10Attribute(Reader reader, Writer writer, XmlEscapeType type, XmlEscapeLevel level) throws IOException {
        XmlEscape.escapeXml(reader, writer, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS, type, level);
    }

    public static void escapeXml11Attribute(Reader reader, Writer writer, XmlEscapeType type, XmlEscapeLevel level) throws IOException {
        XmlEscape.escapeXml(reader, writer, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS, type, level);
    }

    private static void escapeXml(Reader reader, Writer writer, XmlEscapeSymbols symbols, XmlEscapeType type, XmlEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        XmlEscapeUtil.escape(reader, writer, symbols, type, level);
    }

    public static void escapeXml10Minimal(char[] text, int offset, int len, Writer writer) throws IOException {
        XmlEscape.escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML10_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml11Minimal(char[] text, int offset, int len, Writer writer) throws IOException {
        XmlEscape.escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML11_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml10AttributeMinimal(char[] text, int offset, int len, Writer writer) throws IOException {
        XmlEscape.escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml11AttributeMinimal(char[] text, int offset, int len, Writer writer) throws IOException {
        XmlEscape.escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml10(char[] text, int offset, int len, Writer writer) throws IOException {
        XmlEscape.escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML10_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml11(char[] text, int offset, int len, Writer writer) throws IOException {
        XmlEscape.escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML11_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml10Attribute(char[] text, int offset, int len, Writer writer) throws IOException {
        XmlEscape.escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml11Attribute(char[] text, int offset, int len, Writer writer) throws IOException {
        XmlEscape.escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS, XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA, XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeXml10(char[] text, int offset, int len, Writer writer, XmlEscapeType type, XmlEscapeLevel level) throws IOException {
        XmlEscape.escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML10_SYMBOLS, type, level);
    }

    public static void escapeXml11(char[] text, int offset, int len, Writer writer, XmlEscapeType type, XmlEscapeLevel level) throws IOException {
        XmlEscape.escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML11_SYMBOLS, type, level);
    }

    public static void escapeXml10Attribute(char[] text, int offset, int len, Writer writer, XmlEscapeType type, XmlEscapeLevel level) throws IOException {
        XmlEscape.escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS, type, level);
    }

    public static void escapeXml11Attribute(char[] text, int offset, int len, Writer writer, XmlEscapeType type, XmlEscapeLevel level) throws IOException {
        XmlEscape.escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS, type, level);
    }

    private static void escapeXml(char[] text, int offset, int len, Writer writer, XmlEscapeSymbols symbols, XmlEscapeType type, XmlEscapeLevel level) throws IOException {
        int textLen;
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        int n = textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        XmlEscapeUtil.escape(text, offset, len, writer, symbols, type, level);
    }

    public static String unescapeXml(String text) {
        if (text == null) {
            return null;
        }
        if (text.indexOf(38) < 0) {
            return text;
        }
        return XmlEscapeUtil.unescape(text, XmlEscapeSymbols.XML11_SYMBOLS);
    }

    public static void unescapeXml(String text, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (text == null) {
            return;
        }
        if (text.indexOf(38) < 0) {
            writer.write(text);
            return;
        }
        XmlEscapeUtil.unescape(new InternalStringReader(text), writer, XmlEscapeSymbols.XML11_SYMBOLS);
    }

    public static void unescapeXml(Reader reader, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        XmlEscapeUtil.unescape(reader, writer, XmlEscapeSymbols.XML11_SYMBOLS);
    }

    public static void unescapeXml(char[] text, int offset, int len, Writer writer) throws IOException {
        int textLen;
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        int n = textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        XmlEscapeUtil.unescape(text, offset, len, writer, XmlEscapeSymbols.XML11_SYMBOLS);
    }

    private XmlEscape() {
    }

    private static final class InternalStringReader
    extends Reader {
        private String str;
        private int length;
        private int next = 0;

        public InternalStringReader(String s) {
            this.str = s;
            this.length = s.length();
        }

        @Override
        public int read() throws IOException {
            if (this.next >= this.length) {
                return -1;
            }
            return this.str.charAt(this.next++);
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return 0;
            }
            if (this.next >= this.length) {
                return -1;
            }
            int n = Math.min(this.length - this.next, len);
            this.str.getChars(this.next, this.next + n, cbuf, off);
            this.next += n;
            return n;
        }

        @Override
        public void close() throws IOException {
            this.str = null;
        }
    }
}

