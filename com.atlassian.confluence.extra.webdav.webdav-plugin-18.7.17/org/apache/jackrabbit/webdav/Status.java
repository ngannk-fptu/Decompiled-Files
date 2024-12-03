/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Status
implements DavConstants,
XmlSerializable {
    private static Logger log = LoggerFactory.getLogger(Status.class);
    private final String version;
    private final int code;
    private final String phrase;

    public Status(int code) {
        this.version = "HTTP/1.1";
        this.code = code;
        this.phrase = DavException.getStatusPhrase(code);
    }

    public Status(String version, int code, String phrase) {
        this.version = version;
        this.code = code;
        this.phrase = phrase;
    }

    public int getStatusCode() {
        return this.code;
    }

    @Override
    public Element toXml(Document document) {
        String statusLine = this.version + " " + this.code + " " + this.phrase;
        Element e = DomUtil.createElement(document, "status", NAMESPACE);
        DomUtil.setText(e, statusLine);
        return e;
    }

    public static Status parse(String statusLine) {
        Status status;
        if (statusLine == null) {
            throw new IllegalArgumentException("Unable to parse status line from null xml element.");
        }
        int length = statusLine.length();
        int at = 0;
        int start = 0;
        try {
            int code;
            while (Character.isWhitespace(statusLine.charAt(at))) {
                ++at;
                ++start;
            }
            if (!"HTTP".equals(statusLine.substring(at, at += 4))) {
                log.warn("Status-Line '" + statusLine + "' does not start with HTTP");
            }
            if ((at = statusLine.indexOf(32, at)) <= 0) {
                log.warn("Unable to parse HTTP-Version from the status line: '" + statusLine + "'");
            }
            String version = statusLine.substring(start, at).toUpperCase();
            while (statusLine.charAt(at) == ' ') {
                ++at;
            }
            int to = statusLine.indexOf(32, at);
            if (to < 0) {
                to = length;
            }
            try {
                code = Integer.parseInt(statusLine.substring(at, to));
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Unable to parse status code from status line: '" + statusLine + "'");
            }
            String phrase = "";
            at = to + 1;
            if (at < length) {
                phrase = statusLine.substring(at).trim();
            }
            status = new Status(version, code, phrase);
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Status-Line '" + statusLine + "' is not valid");
        }
        return status;
    }
}

