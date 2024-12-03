/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.extras.common.org.springframework.util;

import com.atlassian.extras.common.org.springframework.util.StringUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

public class DefaultPropertiesPersister {
    public void load(Properties props, InputStream is) throws IOException {
        props.load(is);
    }

    public void load(Properties props, Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while ((line = in.readLine()) != null) {
            char firstChar;
            if ((line = StringUtils.trimLeadingWhitespace(line)).length() <= 0 || (firstChar = line.charAt(0)) == '#' || firstChar == '!') continue;
            while (this.endsWithContinuationMarker(line)) {
                String nextLine = in.readLine();
                line = line.substring(0, line.length() - 1);
                if (nextLine == null) continue;
                line = line + StringUtils.trimLeadingWhitespace(nextLine);
            }
            int separatorIndex = line.indexOf("=");
            if (separatorIndex == -1) {
                separatorIndex = line.indexOf(":");
            }
            String key = separatorIndex != -1 ? line.substring(0, separatorIndex) : line;
            String value = separatorIndex != -1 ? line.substring(separatorIndex + 1) : "";
            key = StringUtils.trimTrailingWhitespace(key);
            value = StringUtils.trimLeadingWhitespace(value);
            props.put(this.unescape(key), this.unescape(value));
        }
        return;
    }

    protected boolean endsWithContinuationMarker(String line) {
        boolean evenSlashCount = true;
        for (int index = line.length() - 1; index >= 0 && line.charAt(index) == '\\'; --index) {
            evenSlashCount = !evenSlashCount;
        }
        return !evenSlashCount;
    }

    protected String unescape(String str) {
        StringBuffer outBuffer = new StringBuffer(str.length());
        int index = 0;
        while (index < str.length()) {
            int c;
            if ((c = str.charAt(index++)) == 92) {
                if ((c = str.charAt(index++)) == 116) {
                    c = 9;
                } else if (c == 114) {
                    c = 13;
                } else if (c == 110) {
                    c = 10;
                } else if (c == 102) {
                    c = 12;
                }
            }
            outBuffer.append((char)c);
        }
        return outBuffer.toString();
    }

    public void store(Properties props, OutputStream os, String header) throws IOException {
        props.store(os, header);
    }

    public void store(Properties props, Writer writer, String header) throws IOException {
        this.store(props, writer, header, false);
    }

    public void store(Properties props, Writer writer, String header, boolean omitDate) throws IOException {
        BufferedWriter out = new BufferedWriter(writer);
        if (header != null) {
            out.write("#" + header);
            out.write(10);
        }
        if (!omitDate) {
            out.write("#" + new Date());
            out.write(10);
        }
        Enumeration<Object> keys = props.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String val = props.getProperty(key);
            out.write(this.escape(key, true) + "=" + this.escape(val, false));
            out.write(10);
        }
        out.flush();
    }

    protected String escape(String str, boolean isKey) {
        int len = str.length();
        StringBuffer outBuffer = new StringBuffer(len * 2);
        block8: for (int index = 0; index < len; ++index) {
            char c = str.charAt(index);
            switch (c) {
                case ' ': {
                    if (index == 0 || isKey) {
                        outBuffer.append('\\');
                    }
                    outBuffer.append(' ');
                    continue block8;
                }
                case '\\': {
                    outBuffer.append("\\\\");
                    continue block8;
                }
                case '\t': {
                    outBuffer.append("\\t");
                    continue block8;
                }
                case '\n': {
                    outBuffer.append("\\n");
                    continue block8;
                }
                case '\r': {
                    outBuffer.append("\\r");
                    continue block8;
                }
                case '\f': {
                    outBuffer.append("\\f");
                    continue block8;
                }
                default: {
                    if ("=: \t\r\n\f#!".indexOf(c) != -1) {
                        outBuffer.append('\\');
                    }
                    outBuffer.append(c);
                }
            }
        }
        return outBuffer.toString();
    }
}

