/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PushbackReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.tools.ant.util.DateUtils;
import org.apache.tools.ant.util.UnicodeUtil;

public class LayoutPreservingProperties
extends Properties {
    private static final long serialVersionUID = 1L;
    private String eol = System.lineSeparator();
    private List<LogicalLine> logicalLines = new ArrayList<LogicalLine>();
    private Map<String, Integer> keyedPairLines = new HashMap<String, Integer>();
    private boolean removeComments;

    public LayoutPreservingProperties() {
    }

    public LayoutPreservingProperties(Properties defaults) {
        super(defaults);
    }

    public boolean isRemoveComments() {
        return this.removeComments;
    }

    public void setRemoveComments(boolean val) {
        this.removeComments = val;
    }

    @Override
    public void load(InputStream inStream) throws IOException {
        String s = this.readLines(inStream);
        byte[] ba = s.getBytes(StandardCharsets.ISO_8859_1);
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        super.load(bais);
    }

    @Override
    public Object put(Object key, Object value) throws NullPointerException {
        Object obj = super.put(key, value);
        this.innerSetProperty(key.toString(), value.toString());
        return obj;
    }

    @Override
    public Object setProperty(String key, String value) throws NullPointerException {
        Object obj = super.setProperty(key, value);
        this.innerSetProperty(key, value);
        return obj;
    }

    private void innerSetProperty(String key, String value) {
        value = this.escapeValue(value);
        if (this.keyedPairLines.containsKey(key)) {
            Integer i = this.keyedPairLines.get(key);
            Pair p = (Pair)this.logicalLines.get(i);
            p.setValue(value);
        } else {
            key = this.escapeName(key);
            Pair p = new Pair(key, value);
            p.setNew(true);
            this.keyedPairLines.put(key, this.logicalLines.size());
            this.logicalLines.add(p);
        }
    }

    @Override
    public void clear() {
        super.clear();
        this.keyedPairLines.clear();
        this.logicalLines.clear();
    }

    @Override
    public Object remove(Object key) {
        Object obj = super.remove(key);
        Integer i = this.keyedPairLines.remove(key);
        if (null != i) {
            if (this.removeComments) {
                this.removeCommentsEndingAt(i);
            }
            this.logicalLines.set(i, null);
        }
        return obj;
    }

    @Override
    public Object clone() {
        LayoutPreservingProperties dolly = (LayoutPreservingProperties)super.clone();
        dolly.keyedPairLines = new HashMap<String, Integer>(this.keyedPairLines);
        dolly.logicalLines = new ArrayList<LogicalLine>(this.logicalLines);
        int size = dolly.logicalLines.size();
        for (int j = 0; j < size; ++j) {
            LogicalLine line = dolly.logicalLines.get(j);
            if (!(line instanceof Pair)) continue;
            Pair p = (Pair)line;
            dolly.logicalLines.set(j, (Pair)p.clone());
        }
        return dolly;
    }

    public void listLines(PrintStream out) {
        out.println("-- logical lines --");
        for (LogicalLine line : this.logicalLines) {
            if (line instanceof Blank) {
                out.println("blank:   \"" + line + "\"");
                continue;
            }
            if (line instanceof Comment) {
                out.println("comment: \"" + line + "\"");
                continue;
            }
            if (!(line instanceof Pair)) continue;
            out.println("pair:    \"" + line + "\"");
        }
    }

    public void saveAs(File dest) throws IOException {
        OutputStream fos = Files.newOutputStream(dest.toPath(), new OpenOption[0]);
        this.store(fos, null);
        fos.close();
    }

    @Override
    public void store(OutputStream out, String header) throws IOException {
        OutputStreamWriter osw = new OutputStreamWriter(out, StandardCharsets.ISO_8859_1);
        int skipLines = 0;
        int totalLines = this.logicalLines.size();
        if (header != null) {
            osw.write("#" + header + this.eol);
            if (totalLines > 0 && this.logicalLines.get(0) instanceof Comment && header.equals(this.logicalLines.get(0).toString().substring(1))) {
                skipLines = 1;
            }
        }
        if (totalLines > skipLines && this.logicalLines.get(skipLines) instanceof Comment) {
            try {
                DateUtils.parseDateFromHeader(this.logicalLines.get(skipLines).toString().substring(1));
                ++skipLines;
            }
            catch (ParseException parseException) {
                // empty catch block
            }
        }
        osw.write("#" + DateUtils.getDateForHeader() + this.eol);
        boolean writtenSep = false;
        for (LogicalLine line : this.logicalLines.subList(skipLines, totalLines)) {
            if (line instanceof Pair) {
                if (((Pair)line).isNew() && !writtenSep) {
                    osw.write(this.eol);
                    writtenSep = true;
                }
                osw.write(line.toString() + this.eol);
                continue;
            }
            if (line == null) continue;
            osw.write(line.toString() + this.eol);
        }
        osw.close();
    }

    private String readLines(InputStream is) throws IOException {
        InputStreamReader isr = new InputStreamReader(is, StandardCharsets.ISO_8859_1);
        PushbackReader pbr = new PushbackReader(isr, 1);
        if (!this.logicalLines.isEmpty()) {
            this.logicalLines.add(new Blank());
        }
        String s = this.readFirstLine(pbr);
        BufferedReader br = new BufferedReader(pbr);
        boolean continuation = false;
        boolean comment = false;
        StringBuilder fileBuffer = new StringBuilder();
        StringBuilder logicalLineBuffer = new StringBuilder();
        while (s != null) {
            fileBuffer.append(s).append(this.eol);
            if (continuation) {
                s = "\n" + s;
            } else {
                comment = s.matches("^[ \t\f]*[#!].*");
            }
            if (!comment) {
                continuation = this.requiresContinuation(s);
            }
            logicalLineBuffer.append(s);
            if (!continuation) {
                LogicalLine line;
                if (comment) {
                    line = new Comment(logicalLineBuffer.toString());
                } else if (logicalLineBuffer.toString().trim().isEmpty()) {
                    line = new Blank();
                } else {
                    line = new Pair(logicalLineBuffer.toString());
                    String key = this.unescape(((Pair)line).getName());
                    if (this.keyedPairLines.containsKey(key)) {
                        this.remove(key);
                    }
                    this.keyedPairLines.put(key, this.logicalLines.size());
                }
                this.logicalLines.add(line);
                logicalLineBuffer.setLength(0);
            }
            s = br.readLine();
        }
        return fileBuffer.toString();
    }

    private String readFirstLine(PushbackReader r) throws IOException {
        StringBuilder sb = new StringBuilder(80);
        int ch = r.read();
        boolean hasCR = false;
        this.eol = System.lineSeparator();
        while (ch >= 0) {
            if (hasCR && ch != 10) {
                r.unread(ch);
                break;
            }
            if (ch == 13) {
                this.eol = "\r";
                hasCR = true;
            } else {
                if (ch == 10) {
                    this.eol = hasCR ? "\r\n" : "\n";
                    break;
                }
                sb.append((char)ch);
            }
            ch = r.read();
        }
        return sb.toString();
    }

    private boolean requiresContinuation(String s) {
        int i;
        char[] ca = s.toCharArray();
        for (i = ca.length - 1; i > 0 && ca[i] == '\\'; --i) {
        }
        int tb = ca.length - i - 1;
        return tb % 2 == 1;
    }

    private String unescape(String s) {
        char c;
        char[] ch = new char[s.length() + 1];
        s.getChars(0, s.length(), ch, 0);
        ch[s.length()] = 10;
        StringBuilder buffy = new StringBuilder(s.length());
        for (int i = 0; i < ch.length && (c = ch[i]) != '\n'; ++i) {
            if (c == '\\') {
                if ((c = ch[++i]) == 'n') {
                    buffy.append('\n');
                    continue;
                }
                if (c == 'r') {
                    buffy.append('\r');
                    continue;
                }
                if (c == 'f') {
                    buffy.append('\f');
                    continue;
                }
                if (c == 't') {
                    buffy.append('\t');
                    continue;
                }
                if (c == 'u') {
                    c = this.unescapeUnicode(ch, i + 1);
                    i += 4;
                    buffy.append(c);
                    continue;
                }
                buffy.append(c);
                continue;
            }
            buffy.append(c);
        }
        return buffy.toString();
    }

    private char unescapeUnicode(char[] ch, int i) {
        String s = new String(ch, i, 4);
        return (char)Integer.parseInt(s, 16);
    }

    private String escapeValue(String s) {
        return this.escape(s, false);
    }

    private String escapeName(String s) {
        return this.escape(s, true);
    }

    private String escape(String s, boolean escapeAllSpaces) {
        if (s == null) {
            return null;
        }
        char[] ch = new char[s.length()];
        s.getChars(0, s.length(), ch, 0);
        String forEscaping = "\t\f\r\n\\:=#!";
        String escaped = "tfrn\\:=#!";
        StringBuilder buffy = new StringBuilder(s.length());
        boolean leadingSpace = true;
        for (char c : ch) {
            int p;
            if (c == ' ') {
                if (escapeAllSpaces || leadingSpace) {
                    buffy.append("\\");
                }
            } else {
                leadingSpace = false;
            }
            if ((p = "\t\f\r\n\\:=#!".indexOf(c)) != -1) {
                buffy.append("\\").append("tfrn\\:=#!", p, p + 1);
                continue;
            }
            if (c < ' ' || c > '~') {
                buffy.append(this.escapeUnicode(c));
                continue;
            }
            buffy.append(c);
        }
        return buffy.toString();
    }

    private String escapeUnicode(char ch) {
        return "\\" + UnicodeUtil.EscapeUnicode(ch);
    }

    private void removeCommentsEndingAt(int pos) {
        int end;
        for (pos = end = pos - 1; pos > 0 && this.logicalLines.get(pos) instanceof Blank; --pos) {
        }
        if (!(this.logicalLines.get(pos) instanceof Comment)) {
            return;
        }
        while (pos >= 0 && this.logicalLines.get(pos) instanceof Comment) {
            --pos;
        }
        ++pos;
        while (pos <= end) {
            this.logicalLines.set(pos, null);
            ++pos;
        }
    }

    private static class Pair
    extends LogicalLine
    implements Cloneable {
        private static final long serialVersionUID = 1L;
        private String name;
        private String value;
        private boolean added;

        public Pair(String text) {
            super(text);
            this.parsePair(text);
        }

        public Pair(String name, String value) {
            this(name + "=" + value);
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
            this.setText(this.name + "=" + value);
        }

        public boolean isNew() {
            return this.added;
        }

        public void setNew(boolean val) {
            this.added = val;
        }

        public Object clone() {
            Object dolly = null;
            try {
                dolly = super.clone();
            }
            catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return dolly;
        }

        private void parsePair(String text) {
            int pos = this.findFirstSeparator(text);
            if (pos == -1) {
                this.name = text;
                this.setValue(null);
            } else {
                this.name = text.substring(0, pos);
                this.setValue(text.substring(pos + 1));
            }
            this.name = this.stripStart(this.name, " \t\f");
        }

        private String stripStart(String s, String chars) {
            int i;
            if (s == null) {
                return null;
            }
            for (i = 0; i < s.length() && chars.indexOf(s.charAt(i)) != -1; ++i) {
            }
            if (i == s.length()) {
                return "";
            }
            return s.substring(i);
        }

        private int findFirstSeparator(String s) {
            s = s.replaceAll("\\\\\\\\", "__");
            s = s.replaceAll("\\\\=", "__");
            s = s.replaceAll("\\\\:", "__");
            s = s.replaceAll("\\\\ ", "__");
            s = s.replaceAll("\\\\t", "__");
            return this.indexOfAny(s, " :=\t");
        }

        private int indexOfAny(String s, String chars) {
            if (s == null || chars == null) {
                return -1;
            }
            int p = s.length() + 1;
            for (int i = 0; i < chars.length(); ++i) {
                int x = s.indexOf(chars.charAt(i));
                if (x == -1 || x >= p) continue;
                p = x;
            }
            if (p == s.length() + 1) {
                return -1;
            }
            return p;
        }
    }

    private static abstract class LogicalLine
    implements Serializable {
        private static final long serialVersionUID = 1L;
        private String text;

        public LogicalLine(String text) {
            this.text = text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String toString() {
            return this.text;
        }
    }

    private static class Blank
    extends LogicalLine {
        private static final long serialVersionUID = 1L;

        public Blank() {
            super("");
        }
    }

    private class Comment
    extends LogicalLine {
        private static final long serialVersionUID = 1L;

        public Comment(String text) {
            super(text);
        }
    }
}

