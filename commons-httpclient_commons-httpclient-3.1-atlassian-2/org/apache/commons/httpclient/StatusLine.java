/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import java.util.Locale;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.ProtocolException;

public class StatusLine {
    private final String statusLine;
    private final String httpVersion;
    private final int statusCode;
    private final String reasonPhrase;

    public StatusLine(String statusLine) throws HttpException {
        int length = statusLine.length();
        int at = 0;
        int start = 0;
        try {
            while (Character.isWhitespace(statusLine.charAt(at))) {
                ++at;
                ++start;
            }
            if (!"HTTP".equals(statusLine.substring(at, at += 4))) {
                throw new HttpException("Status-Line '" + statusLine + "' does not start with HTTP");
            }
            if ((at = statusLine.indexOf(" ", at)) <= 0) {
                throw new ProtocolException("Unable to parse HTTP-Version from the status line: '" + statusLine + "'");
            }
            this.httpVersion = statusLine.substring(start, at).toUpperCase(Locale.ENGLISH);
            while (statusLine.charAt(at) == ' ') {
                ++at;
            }
            int to = statusLine.indexOf(" ", at);
            if (to < 0) {
                to = length;
            }
            try {
                this.statusCode = Integer.parseInt(statusLine.substring(at, to));
            }
            catch (NumberFormatException e) {
                throw new ProtocolException("Unable to parse status code from status line: '" + statusLine + "'");
            }
            at = to + 1;
            this.reasonPhrase = at < length ? statusLine.substring(at).trim() : "";
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new HttpException("Status-Line '" + statusLine + "' is not valid");
        }
        this.statusLine = statusLine;
    }

    public final int getStatusCode() {
        return this.statusCode;
    }

    public final String getHttpVersion() {
        return this.httpVersion;
    }

    public final String getReasonPhrase() {
        return this.reasonPhrase;
    }

    public final String toString() {
        return this.statusLine;
    }

    public static boolean startsWithHTTP(String s) {
        try {
            int at = 0;
            while (Character.isWhitespace(s.charAt(at))) {
                ++at;
            }
            return "HTTP".equals(s.substring(at, at + 4));
        }
        catch (StringIndexOutOfBoundsException e) {
            return false;
        }
    }
}

