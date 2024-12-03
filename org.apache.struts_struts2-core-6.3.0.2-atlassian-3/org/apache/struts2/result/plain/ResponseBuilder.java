/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 */
package org.apache.struts2.result.plain;

import javax.servlet.http.Cookie;
import org.apache.struts2.result.plain.BodyWriter;
import org.apache.struts2.result.plain.HttpCookies;
import org.apache.struts2.result.plain.HttpHeader;
import org.apache.struts2.result.plain.HttpHeaders;

public class ResponseBuilder {
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_HTML = "text/html";
    public static final String APPLICATION_JSON = "application/json";
    private final BodyWriter body = new BodyWriter();
    private final HttpHeaders headers = new HttpHeaders().add("Content-Type", "text/plain; charset=UTF-8");
    private final HttpCookies cookies = new HttpCookies();

    public ResponseBuilder write(String out) {
        this.body.write(out);
        return this;
    }

    public ResponseBuilder writeLine(String out) {
        this.body.writeLine(out);
        return this;
    }

    public ResponseBuilder withHeader(String name, String value) {
        this.headers.add(name, value);
        return this;
    }

    public ResponseBuilder withHeader(String name, Long value) {
        this.headers.add(name, value);
        return this;
    }

    public ResponseBuilder withHeader(String name, Integer value) {
        this.headers.add(name, value);
        return this;
    }

    public ResponseBuilder withContentTypeTextPlain() {
        this.headers.add(CONTENT_TYPE, "text/plain; charset=UTF-8");
        return this;
    }

    public ResponseBuilder withContentTypeTextHtml() {
        this.headers.add(CONTENT_TYPE, "text/html; charset=UTF-8");
        return this;
    }

    public ResponseBuilder withContentTypeJson() {
        this.headers.add(CONTENT_TYPE, APPLICATION_JSON);
        return this;
    }

    public ResponseBuilder withContentType(String contentType) {
        this.headers.add(CONTENT_TYPE, contentType);
        return this;
    }

    public ResponseBuilder withCookie(String name, String value) {
        this.cookies.add(name, value);
        return this;
    }

    public Iterable<HttpHeader<String>> getStringHeaders() {
        return this.headers.getStringHeaders();
    }

    public Iterable<HttpHeader<Long>> getDateHeaders() {
        return this.headers.getDateHeaders();
    }

    public Iterable<HttpHeader<Integer>> getIntHeaders() {
        return this.headers.getIntHeaders();
    }

    public Iterable<Cookie> getCookies() {
        return this.cookies.getCookies();
    }

    public String getBody() {
        return this.body.getBody();
    }
}

