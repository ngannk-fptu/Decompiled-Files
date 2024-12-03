/*
 * Decompiled with CFR 0.152.
 */
package org.kxml2.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.xmlpull.v1.XmlSerializer;

public class KXmlSerializer
implements XmlSerializer {
    private Writer writer;
    private boolean pending;
    private int auto;
    private int depth;
    private String[] elementStack = new String[12];
    private int[] nspCounts = new int[4];
    private String[] nspStack = new String[8];
    private boolean[] indent = new boolean[4];
    private boolean unicode;
    private String encoding;

    private final void check(boolean bl) throws IOException {
        if (!this.pending) {
            return;
        }
        ++this.depth;
        this.pending = false;
        if (this.indent.length <= this.depth) {
            boolean[] blArray = new boolean[this.depth + 4];
            System.arraycopy(this.indent, 0, blArray, 0, this.depth);
            this.indent = blArray;
        }
        this.indent[this.depth] = this.indent[this.depth - 1];
        for (int i = this.nspCounts[this.depth - 1]; i < this.nspCounts[this.depth]; ++i) {
            this.writer.write(32);
            this.writer.write("xmlns");
            if (!"".equals(this.nspStack[i * 2])) {
                this.writer.write(58);
                this.writer.write(this.nspStack[i * 2]);
            } else if ("".equals(this.getNamespace()) && !"".equals(this.nspStack[i * 2 + 1])) {
                throw new IllegalStateException("Cannot set default namespace for elements in no namespace");
            }
            this.writer.write("=\"");
            this.writeEscaped(this.nspStack[i * 2 + 1], 34);
            this.writer.write(34);
        }
        if (this.nspCounts.length <= this.depth + 1) {
            int[] nArray = new int[this.depth + 8];
            System.arraycopy(this.nspCounts, 0, nArray, 0, this.depth + 1);
            this.nspCounts = nArray;
        }
        this.nspCounts[this.depth + 1] = this.nspCounts[this.depth];
        this.writer.write(bl ? " />" : ">");
    }

    private final void writeEscaped(String string, int n) throws IOException {
        block7: for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            switch (c) {
                case '\t': 
                case '\n': 
                case '\r': {
                    if (n == -1) {
                        this.writer.write(c);
                        continue block7;
                    }
                    this.writer.write("&#" + c + ';');
                    continue block7;
                }
                case '&': {
                    this.writer.write("&amp;");
                    continue block7;
                }
                case '>': {
                    this.writer.write("&gt;");
                    continue block7;
                }
                case '<': {
                    this.writer.write("&lt;");
                    continue block7;
                }
                case '\"': 
                case '\'': {
                    if (c == n) {
                        this.writer.write(c == '\"' ? "&quot;" : "&apos;");
                        continue block7;
                    }
                }
                default: {
                    if (c >= ' ' && c != '@' && (c < '\u007f' || this.unicode)) {
                        this.writer.write(c);
                        continue block7;
                    }
                    this.writer.write("&#" + c + ";");
                }
            }
        }
    }

    public void docdecl(String string) throws IOException {
        this.writer.write("<!DOCTYPE");
        this.writer.write(string);
        this.writer.write(">");
    }

    public void endDocument() throws IOException {
        while (this.depth > 0) {
            this.endTag(this.elementStack[this.depth * 3 - 3], this.elementStack[this.depth * 3 - 1]);
        }
        this.flush();
    }

    public void entityRef(String string) throws IOException {
        this.check(false);
        this.writer.write(38);
        this.writer.write(string);
        this.writer.write(59);
    }

    public boolean getFeature(String string) {
        return "http://xmlpull.org/v1/doc/features.html#indent-output".equals(string) ? this.indent[this.depth] : false;
    }

    public String getPrefix(String string, boolean bl) {
        try {
            return this.getPrefix(string, false, bl);
        }
        catch (IOException iOException) {
            throw new RuntimeException(iOException.toString());
        }
    }

    private final String getPrefix(String string, boolean bl, boolean bl2) throws IOException {
        String string2;
        for (int i = this.nspCounts[this.depth + 1] * 2 - 2; i >= 0; i -= 2) {
            if (!this.nspStack[i + 1].equals(string) || !bl && this.nspStack[i].equals("")) continue;
            String string3 = this.nspStack[i];
            for (int j = i + 2; j < this.nspCounts[this.depth + 1] * 2; ++j) {
                if (!this.nspStack[j].equals(string3)) continue;
                string3 = null;
                break;
            }
            if (string3 == null) continue;
            return string3;
        }
        if (!bl2) {
            return null;
        }
        if ("".equals(string)) {
            string2 = "";
        } else {
            block2: do {
                string2 = "n" + this.auto++;
                for (int i = this.nspCounts[this.depth + 1] * 2 - 2; i >= 0; i -= 2) {
                    if (!string2.equals(this.nspStack[i])) continue;
                    string2 = null;
                    continue block2;
                }
            } while (string2 == null);
        }
        boolean bl3 = this.pending;
        this.pending = false;
        this.setPrefix(string2, string);
        this.pending = bl3;
        return string2;
    }

    public Object getProperty(String string) {
        throw new RuntimeException("Unsupported property");
    }

    public void ignorableWhitespace(String string) throws IOException {
        this.text(string);
    }

    public void setFeature(String string, boolean bl) {
        if (!"http://xmlpull.org/v1/doc/features.html#indent-output".equals(string)) {
            throw new RuntimeException("Unsupported Feature");
        }
        this.indent[this.depth] = bl;
    }

    public void setProperty(String string, Object object) {
        throw new RuntimeException("Unsupported Property:" + object);
    }

    public void setPrefix(String string, String string2) throws IOException {
        String string3;
        this.check(false);
        if (string == null) {
            string = "";
        }
        if (string2 == null) {
            string2 = "";
        }
        if (string.equals(string3 = this.getPrefix(string2, true, false))) {
            return;
        }
        int n = this.depth + 1;
        int n2 = this.nspCounts[n];
        this.nspCounts[n] = n2 + 1;
        int n3 = n2 << 1;
        if (this.nspStack.length < n3 + 1) {
            String[] stringArray = new String[this.nspStack.length + 16];
            System.arraycopy(this.nspStack, 0, stringArray, 0, n3);
            this.nspStack = stringArray;
        }
        this.nspStack[n3++] = string;
        this.nspStack[n3] = string2;
    }

    public void setOutput(Writer writer) {
        this.writer = writer;
        this.nspCounts[0] = 2;
        this.nspCounts[1] = 2;
        this.nspStack[0] = "";
        this.nspStack[1] = "";
        this.nspStack[2] = "xml";
        this.nspStack[3] = "http://www.w3.org/XML/1998/namespace";
        this.pending = false;
        this.auto = 0;
        this.depth = 0;
        this.unicode = false;
    }

    public void setOutput(OutputStream outputStream, String string) throws IOException {
        if (outputStream == null) {
            throw new IllegalArgumentException();
        }
        this.setOutput(string == null ? new OutputStreamWriter(outputStream) : new OutputStreamWriter(outputStream, string));
        this.encoding = string;
        if (string != null && string.toLowerCase().startsWith("utf")) {
            this.unicode = true;
        }
    }

    public void startDocument(String string, Boolean bl) throws IOException {
        this.writer.write("<?xml version='1.0' ");
        if (string != null) {
            this.encoding = string;
            if (string.toLowerCase().startsWith("utf")) {
                this.unicode = true;
            }
        }
        if (this.encoding != null) {
            this.writer.write("encoding='");
            this.writer.write(this.encoding);
            this.writer.write("' ");
        }
        if (bl != null) {
            this.writer.write("standalone='");
            this.writer.write(bl != false ? "yes" : "no");
            this.writer.write("' ");
        }
        this.writer.write("?>");
    }

    public XmlSerializer startTag(String string, String string2) throws IOException {
        String[] stringArray;
        int n;
        this.check(false);
        if (this.indent[this.depth]) {
            this.writer.write("\r\n");
            for (n = 0; n < this.depth; ++n) {
                this.writer.write("  ");
            }
        }
        if (this.elementStack.length < (n = this.depth * 3) + 3) {
            stringArray = new String[this.elementStack.length + 12];
            System.arraycopy(this.elementStack, 0, stringArray, 0, n);
            this.elementStack = stringArray;
        }
        Object object = stringArray = string == null ? "" : this.getPrefix(string, true, true);
        if ("".equals(string)) {
            for (int i = this.nspCounts[this.depth]; i < this.nspCounts[this.depth + 1]; ++i) {
                if (!"".equals(this.nspStack[i * 2]) || "".equals(this.nspStack[i * 2 + 1])) continue;
                throw new IllegalStateException("Cannot set default namespace for elements in no namespace");
            }
        }
        this.elementStack[n++] = string;
        this.elementStack[n++] = stringArray;
        this.elementStack[n] = string2;
        this.writer.write(60);
        if (!"".equals(stringArray)) {
            this.writer.write((String)stringArray);
            this.writer.write(58);
        }
        this.writer.write(string2);
        this.pending = true;
        return this;
    }

    public XmlSerializer attribute(String string, String string2, String string3) throws IOException {
        if (!this.pending) {
            throw new IllegalStateException("illegal position for attribute");
        }
        if (string == null) {
            string = "";
        }
        String string4 = "".equals(string) ? "" : this.getPrefix(string, false, true);
        this.writer.write(32);
        if (!"".equals(string4)) {
            this.writer.write(string4);
            this.writer.write(58);
        }
        this.writer.write(string2);
        this.writer.write(61);
        int n = string3.indexOf(34) == -1 ? 34 : 39;
        this.writer.write(n);
        this.writeEscaped(string3, n);
        this.writer.write(n);
        return this;
    }

    public void flush() throws IOException {
        this.check(false);
        this.writer.flush();
    }

    public XmlSerializer endTag(String string, String string2) throws IOException {
        if (!this.pending) {
            --this.depth;
        }
        if (string == null && this.elementStack[this.depth * 3] != null || string != null && !string.equals(this.elementStack[this.depth * 3]) || !this.elementStack[this.depth * 3 + 2].equals(string2)) {
            throw new IllegalArgumentException("</{" + string + "}" + string2 + "> does not match start");
        }
        if (this.pending) {
            this.check(true);
            --this.depth;
        } else {
            if (this.indent[this.depth + 1]) {
                this.writer.write("\r\n");
                for (int i = 0; i < this.depth; ++i) {
                    this.writer.write("  ");
                }
            }
            this.writer.write("</");
            String string3 = this.elementStack[this.depth * 3 + 1];
            if (!"".equals(string3)) {
                this.writer.write(string3);
                this.writer.write(58);
            }
            this.writer.write(string2);
            this.writer.write(62);
        }
        this.nspCounts[this.depth + 1] = this.nspCounts[this.depth];
        return this;
    }

    public String getNamespace() {
        return this.getDepth() == 0 ? null : this.elementStack[this.getDepth() * 3 - 3];
    }

    public String getName() {
        return this.getDepth() == 0 ? null : this.elementStack[this.getDepth() * 3 - 1];
    }

    public int getDepth() {
        return this.pending ? this.depth + 1 : this.depth;
    }

    public XmlSerializer text(String string) throws IOException {
        this.check(false);
        this.indent[this.depth] = false;
        this.writeEscaped(string, -1);
        return this;
    }

    public XmlSerializer text(char[] cArray, int n, int n2) throws IOException {
        this.text(new String(cArray, n, n2));
        return this;
    }

    public void cdsect(String string) throws IOException {
        this.check(false);
        this.writer.write("<![CDATA[");
        this.writer.write(string);
        this.writer.write("]]>");
    }

    public void comment(String string) throws IOException {
        this.check(false);
        this.writer.write("<!--");
        this.writer.write(string);
        this.writer.write("-->");
    }

    public void processingInstruction(String string) throws IOException {
        this.check(false);
        this.writer.write("<?");
        this.writer.write(string);
        this.writer.write("?>");
    }
}

