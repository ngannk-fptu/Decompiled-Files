/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 */
package com.atlassian.core.filters;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class HeaderSanitisingResponseWrapper
extends HttpServletResponseWrapper {
    private static final char[] DISALLOWED_CHARS = new char[]{'\r', '\n'};
    private static final char REPLACEMENT_CHAR = ' ';
    private final char[] disallowedChars;
    private final char replacementChar;

    public HeaderSanitisingResponseWrapper(HttpServletResponse httpServletResponse) {
        this(httpServletResponse, DISALLOWED_CHARS, ' ');
    }

    HeaderSanitisingResponseWrapper(HttpServletResponse httpServletResponse, char[] disallowedChars, char replacementChar) {
        super(httpServletResponse);
        Arrays.sort(disallowedChars);
        this.disallowedChars = disallowedChars;
        this.replacementChar = replacementChar;
    }

    public void addCookie(Cookie cookie) {
        if (cookie != null) {
            cookie.setValue(this.cleanString(cookie.getValue()));
        }
        super.addCookie(cookie);
    }

    public void setContentType(String contentType) {
        super.setContentType(this.cleanString(contentType));
    }

    public void setDateHeader(String name, long value) {
        super.setDateHeader(this.cleanString(name), value);
    }

    public void addDateHeader(String name, long value) {
        super.addDateHeader(this.cleanString(name), value);
    }

    public void setHeader(String name, String value) {
        super.setHeader(this.cleanString(name), this.cleanString(value));
    }

    public void addHeader(String name, String value) {
        super.addHeader(this.cleanString(name), this.cleanString(value));
    }

    public void setIntHeader(String name, int value) {
        super.setIntHeader(this.cleanString(name), value);
    }

    public void addIntHeader(String name, int value) {
        super.addIntHeader(this.cleanString(name), value);
    }

    public void sendRedirect(String location) throws IOException {
        super.sendRedirect(this.cleanString(location));
    }

    public void sendError(int code, String message) throws IOException {
        super.sendError(code, this.cleanString(message));
    }

    public void setStatus(int code, String status) {
        super.setStatus(code, this.cleanString(status));
    }

    String cleanString(String value) {
        if (value != null && !"".equals(value)) {
            char[] chars = value.toCharArray();
            for (int i = 0; i < chars.length; ++i) {
                if (!this.isDisallowedChar(chars[i])) continue;
                chars[i] = this.replacementChar;
            }
            value = new String(chars);
        }
        return value;
    }

    private boolean isDisallowedChar(char c) {
        return Arrays.binarySearch(this.disallowedChars, c) >= 0;
    }
}

