/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.header;

import javax.servlet.http.HttpServletRequest;
import org.apache.jackrabbit.webdav.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodedUrlHeader
implements Header {
    private static Logger log = LoggerFactory.getLogger(CodedUrlHeader.class);
    private final String headerName;
    private final String headerValue;

    public CodedUrlHeader(String headerName, String headerValue) {
        this.headerName = headerName;
        if (!(headerValue == null || headerValue.startsWith("<") && headerValue.endsWith(">"))) {
            headerValue = "<" + headerValue + ">";
        }
        this.headerValue = headerValue;
    }

    @Override
    public String getHeaderName() {
        return this.headerName;
    }

    @Override
    public String getHeaderValue() {
        return this.headerValue;
    }

    public String getCodedUrl() {
        String[] codedUrls = this.getCodedUrls();
        return codedUrls != null ? codedUrls[0] : null;
    }

    public String[] getCodedUrls() {
        String[] codedUrls = null;
        if (this.headerValue != null) {
            String[] values = this.headerValue.split(",");
            codedUrls = new String[values.length];
            for (int i = 0; i < values.length; ++i) {
                int p1 = values[i].indexOf(60);
                if (p1 < 0) {
                    throw new IllegalArgumentException("Invalid CodedURL header value:" + values[i]);
                }
                int p2 = values[i].indexOf(62, p1);
                if (p2 < 0) {
                    throw new IllegalArgumentException("Invalid CodedURL header value:" + values[i]);
                }
                codedUrls[i] = values[i].substring(p1 + 1, p2);
            }
        }
        return codedUrls;
    }

    public static CodedUrlHeader parse(HttpServletRequest request, String headerName) {
        String headerValue = request.getHeader(headerName);
        return new CodedUrlHeader(headerName, headerValue);
    }
}

