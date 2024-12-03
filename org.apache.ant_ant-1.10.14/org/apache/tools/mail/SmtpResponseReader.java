/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.mail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SmtpResponseReader {
    protected BufferedReader reader = null;

    public SmtpResponseReader(InputStream in) {
        this.reader = new BufferedReader(new InputStreamReader(in));
    }

    public String getResponse() throws IOException {
        StringBuilder result = new StringBuilder();
        String line = this.reader.readLine();
        if (line != null && line.length() >= 3) {
            result.append(line, 0, 3);
            result.append(" ");
        }
        while (line != null) {
            SmtpResponseReader.appendTo(result, line);
            if (!this.hasMoreLines(line)) break;
            line = this.reader.readLine();
        }
        return result.toString().trim();
    }

    public void close() throws IOException {
        this.reader.close();
    }

    protected boolean hasMoreLines(String line) {
        return line.length() > 3 && line.charAt(3) == '-';
    }

    private static void appendTo(StringBuilder target, String line) {
        if (line.length() > 4) {
            target.append(line.substring(4)).append(' ');
        }
    }
}

