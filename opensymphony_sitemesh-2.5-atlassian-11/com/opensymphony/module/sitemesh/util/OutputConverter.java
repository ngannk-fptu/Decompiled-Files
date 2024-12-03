/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.util;

import com.opensymphony.module.sitemesh.util.CharArrayWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;

public class OutputConverter {
    public static final String WORK_AROUND_RESIN_I18N_BUG = "sitemesh.resin.i18n.workaround";

    public static Writer getWriter(Writer out) {
        if ("true".equalsIgnoreCase(System.getProperty(WORK_AROUND_RESIN_I18N_BUG))) {
            return new ResinWriter(out);
        }
        return out;
    }

    public static String convert(String inputString) throws IOException {
        if ("true".equalsIgnoreCase(System.getProperty(WORK_AROUND_RESIN_I18N_BUG))) {
            StringWriter sr = new StringWriter();
            OutputConverter.resinConvert(inputString, sr);
            return sr.getBuffer().toString();
        }
        return inputString;
    }

    private static void resinConvert(String inputString, Writer writer) throws IOException {
        int i;
        InputStreamReader reader = new InputStreamReader((InputStream)new ByteArrayInputStream(inputString.getBytes("UTF-8")), "ISO-8859-1");
        while ((i = reader.read()) != -1) {
            writer.write(i);
        }
        writer.flush();
    }

    static class ResinWriter
    extends Writer {
        private final Writer target;
        private final CharArrayWriter buffer = new CharArrayWriter();

        public ResinWriter(Writer target) {
            this.target = target;
        }

        public void close() throws IOException {
            this.flush();
        }

        public void flush() throws IOException {
            OutputConverter.resinConvert(this.buffer.toString(), this.target);
            this.buffer.reset();
        }

        public void write(char[] cbuf, int off, int len) throws IOException {
            this.buffer.write(cbuf, off, len);
            this.flush();
        }
    }
}

