/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io.json;

import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.AbstractJsonWriter;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import java.io.Writer;

public class JsonWriter
extends AbstractJsonWriter {
    protected final QuickWriter writer;
    protected final Format format;
    private int depth;
    private boolean newLineProposed;

    public JsonWriter(Writer writer, char[] lineIndenter, String newLine) {
        this(writer, 0, new Format(lineIndenter, newLine.toCharArray(), Format.SPACE_AFTER_LABEL | Format.COMPACT_EMPTY_ELEMENT));
    }

    public JsonWriter(Writer writer, char[] lineIndenter) {
        this(writer, 0, new Format(lineIndenter, new char[]{'\n'}, Format.SPACE_AFTER_LABEL | Format.COMPACT_EMPTY_ELEMENT));
    }

    public JsonWriter(Writer writer, String lineIndenter, String newLine) {
        this(writer, 0, new Format(lineIndenter.toCharArray(), newLine.toCharArray(), Format.SPACE_AFTER_LABEL | Format.COMPACT_EMPTY_ELEMENT));
    }

    public JsonWriter(Writer writer, String lineIndenter) {
        this(writer, 0, new Format(lineIndenter.toCharArray(), new char[]{'\n'}, Format.SPACE_AFTER_LABEL | Format.COMPACT_EMPTY_ELEMENT));
    }

    public JsonWriter(Writer writer) {
        this(writer, 0, new Format(new char[]{' ', ' '}, new char[]{'\n'}, Format.SPACE_AFTER_LABEL | Format.COMPACT_EMPTY_ELEMENT));
    }

    public JsonWriter(Writer writer, char[] lineIndenter, String newLine, int mode) {
        this(writer, mode, new Format(lineIndenter, newLine.toCharArray(), Format.SPACE_AFTER_LABEL | Format.COMPACT_EMPTY_ELEMENT));
    }

    public JsonWriter(Writer writer, int mode) {
        this(writer, mode, new Format());
    }

    public JsonWriter(Writer writer, Format format) {
        this(writer, 0, format);
    }

    public JsonWriter(Writer writer, int mode, Format format) {
        this(writer, mode, format, 1024);
    }

    public JsonWriter(Writer writer, int mode, Format format, int bufferSize) {
        super(mode, format.getNameCoder());
        this.writer = new QuickWriter(writer, bufferSize);
        this.format = format;
        this.depth = (mode & 1) == 0 ? -1 : 0;
    }

    public void flush() {
        this.writer.flush();
    }

    public void close() {
        this.writer.close();
    }

    public HierarchicalStreamWriter underlyingWriter() {
        return this;
    }

    protected void startObject() {
        if (this.newLineProposed) {
            this.writeNewLine();
        }
        this.writer.write('{');
        this.startNewLine();
    }

    protected void addLabel(String name) {
        if (this.newLineProposed) {
            this.writeNewLine();
        }
        this.writer.write('\"');
        this.writeText(name);
        this.writer.write("\":");
        if ((this.format.mode() & Format.SPACE_AFTER_LABEL) != 0) {
            this.writer.write(' ');
        }
    }

    protected void addValue(String value, AbstractJsonWriter.Type type) {
        if (this.newLineProposed) {
            this.writeNewLine();
        }
        if (type == AbstractJsonWriter.Type.STRING) {
            this.writer.write('\"');
        }
        this.writeText(value);
        if (type == AbstractJsonWriter.Type.STRING) {
            this.writer.write('\"');
        }
    }

    protected void startArray() {
        if (this.newLineProposed) {
            this.writeNewLine();
        }
        this.writer.write("[");
        this.startNewLine();
    }

    protected void nextElement() {
        this.writer.write(",");
        this.writeNewLine();
    }

    protected void endArray() {
        this.endNewLine();
        this.writer.write("]");
    }

    protected void endObject() {
        this.endNewLine();
        this.writer.write("}");
    }

    private void startNewLine() {
        if (++this.depth > 0) {
            this.newLineProposed = true;
        }
    }

    private void endNewLine() {
        if (this.depth-- > 0) {
            if ((this.format.mode() & Format.COMPACT_EMPTY_ELEMENT) != 0 && this.newLineProposed) {
                this.newLineProposed = false;
            } else {
                this.writeNewLine();
            }
        }
    }

    private void writeNewLine() {
        int depth = this.depth;
        this.writer.write(this.format.getNewLine());
        while (depth-- > 0) {
            this.writer.write(this.format.getLineIndenter());
        }
        this.newLineProposed = false;
    }

    private void writeText(String text) {
        int length = text.length();
        block9: for (int i = 0; i < length; ++i) {
            char c = text.charAt(i);
            switch (c) {
                case '\"': {
                    this.writer.write("\\\"");
                    continue block9;
                }
                case '\\': {
                    this.writer.write("\\\\");
                    continue block9;
                }
                case '\b': {
                    this.writer.write("\\b");
                    continue block9;
                }
                case '\f': {
                    this.writer.write("\\f");
                    continue block9;
                }
                case '\n': {
                    this.writer.write("\\n");
                    continue block9;
                }
                case '\r': {
                    this.writer.write("\\r");
                    continue block9;
                }
                case '\t': {
                    this.writer.write("\\t");
                    continue block9;
                }
                default: {
                    if (c > '\u001f') {
                        this.writer.write(c);
                        continue block9;
                    }
                    this.writer.write("\\u");
                    String hex = "000" + Integer.toHexString(c);
                    this.writer.write(hex.substring(hex.length() - 4));
                }
            }
        }
    }

    public static class Format {
        public static int SPACE_AFTER_LABEL = 1;
        public static int COMPACT_EMPTY_ELEMENT = 2;
        private char[] lineIndenter;
        private char[] newLine;
        private final int mode;
        private final NameCoder nameCoder;

        public Format() {
            this(new char[]{' ', ' '}, new char[]{'\n'}, SPACE_AFTER_LABEL | COMPACT_EMPTY_ELEMENT);
        }

        public Format(char[] lineIndenter, char[] newLine, int mode) {
            this(lineIndenter, newLine, mode, new NoNameCoder());
        }

        public Format(char[] lineIndenter, char[] newLine, int mode, NameCoder nameCoder) {
            this.lineIndenter = lineIndenter;
            this.newLine = newLine;
            this.mode = mode;
            this.nameCoder = nameCoder;
        }

        public char[] getLineIndenter() {
            return this.lineIndenter;
        }

        public char[] getNewLine() {
            return this.newLine;
        }

        public int mode() {
            return this.mode;
        }

        public NameCoder getNameCoder() {
            return this.nameCoder;
        }
    }
}

