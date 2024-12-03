/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.jfree.util.Configuration;
import org.jfree.util.LineBreakIterator;
import org.jfree.util.StringUtils;

public class SortedConfigurationWriter {
    private static final int ESCAPE_KEY = 0;
    private static final int ESCAPE_VALUE = 1;
    private static final int ESCAPE_COMMENT = 2;
    private static final String END_OF_LINE = StringUtils.getLineSeparator();
    private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    protected String getDescription(String key) {
        return null;
    }

    public void save(String filename, Configuration config) throws IOException {
        this.save(new File(filename), config);
    }

    public void save(File file, Configuration config) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        this.save(out, config);
        out.close();
    }

    public void save(OutputStream outStream, Configuration config) throws IOException {
        ArrayList<String> names = new ArrayList<String>();
        Iterator defaults = config.findPropertyKeys("");
        while (defaults.hasNext()) {
            String key = (String)defaults.next();
            names.add(key);
        }
        Collections.sort(names);
        OutputStreamWriter out = new OutputStreamWriter(outStream, "iso-8859-1");
        for (int i = 0; i < names.size(); ++i) {
            String key = (String)names.get(i);
            String value = config.getConfigProperty(key);
            String description = this.getDescription(key);
            if (description != null) {
                this.writeDescription(description, out);
            }
            this.saveConvert(key, 0, out);
            out.write("=");
            this.saveConvert(value, 1, out);
            out.write(END_OF_LINE);
        }
        out.flush();
    }

    private void writeDescription(String text, Writer writer) throws IOException {
        if (text.length() == 0) {
            return;
        }
        writer.write("# ");
        writer.write(END_OF_LINE);
        LineBreakIterator iterator = new LineBreakIterator(text);
        while (iterator.hasNext()) {
            writer.write("# ");
            this.saveConvert((String)iterator.next(), 2, writer);
            writer.write(END_OF_LINE);
        }
    }

    private void saveConvert(String text, int escapeMode, Writer writer) throws IOException {
        char[] string = text.toCharArray();
        block9: for (int x = 0; x < string.length; ++x) {
            char aChar = string[x];
            switch (aChar) {
                case ' ': {
                    if (escapeMode != 2 && (x == 0 || escapeMode == 0)) {
                        writer.write(92);
                    }
                    writer.write(32);
                    continue block9;
                }
                case '\\': {
                    writer.write(92);
                    writer.write(92);
                    continue block9;
                }
                case '\t': {
                    if (escapeMode == 2) {
                        writer.write(aChar);
                        continue block9;
                    }
                    writer.write(92);
                    writer.write(116);
                    continue block9;
                }
                case '\n': {
                    writer.write(92);
                    writer.write(110);
                    continue block9;
                }
                case '\r': {
                    writer.write(92);
                    writer.write(114);
                    continue block9;
                }
                case '\f': {
                    if (escapeMode == 2) {
                        writer.write(aChar);
                        continue block9;
                    }
                    writer.write(92);
                    writer.write(102);
                    continue block9;
                }
                case '!': 
                case '\"': 
                case '#': 
                case ':': 
                case '=': {
                    if (escapeMode == 2) {
                        writer.write(aChar);
                        continue block9;
                    }
                    writer.write(92);
                    writer.write(aChar);
                    continue block9;
                }
                default: {
                    if (aChar < ' ' || aChar > '~') {
                        writer.write(92);
                        writer.write(117);
                        writer.write(HEX_CHARS[aChar >> 12 & 0xF]);
                        writer.write(HEX_CHARS[aChar >> 8 & 0xF]);
                        writer.write(HEX_CHARS[aChar >> 4 & 0xF]);
                        writer.write(HEX_CHARS[aChar & 0xF]);
                        continue block9;
                    }
                    writer.write(aChar);
                }
            }
        }
    }
}

