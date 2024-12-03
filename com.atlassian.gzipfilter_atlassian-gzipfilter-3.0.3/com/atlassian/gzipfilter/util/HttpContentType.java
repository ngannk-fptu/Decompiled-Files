/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gzipfilter.util;

public class HttpContentType {
    private final String type;
    private final String encoding;

    public HttpContentType(String fullValue) {
        int offset = fullValue.lastIndexOf("charset=");
        this.encoding = offset != -1 ? this.extractContentTypeValue(fullValue, offset + 8) : null;
        this.type = this.extractContentTypeValue(fullValue, 0);
    }

    private String extractContentTypeValue(String type, int startIndex) {
        int endIndex;
        if (startIndex < 0) {
            return null;
        }
        while (startIndex < type.length() && type.charAt(startIndex) == ' ') {
            ++startIndex;
        }
        if (startIndex >= type.length()) {
            return null;
        }
        if (type.charAt(startIndex) == '\"') {
            if ((endIndex = type.indexOf(34, ++startIndex)) == -1) {
                endIndex = type.length();
            }
        } else {
            char ch;
            for (endIndex = startIndex; endIndex < type.length() && (ch = type.charAt(endIndex)) != ' ' && ch != ';' && ch != '(' && ch != ')' && ch != '[' && ch != ']' && ch != '<' && ch != '>' && ch != ':' && ch != ',' && ch != '=' && ch != '?' && ch != '@' && ch != '\"' && ch != '\\'; ++endIndex) {
            }
        }
        return type.substring(startIndex, endIndex);
    }

    public String getType() {
        return this.type;
    }

    public String getEncoding() {
        return this.encoding;
    }
}

