/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 *  com.nimbusds.jwt.JWT
 *  com.nimbusds.jwt.JWTParser
 *  net.jcip.annotations.ThreadSafe
 *  net.minidev.json.JSONArray
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk.http;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPMessage;
import com.nimbusds.oauth2.sdk.util.JSONArrayUtils;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import net.jcip.annotations.ThreadSafe;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

@ThreadSafe
public class HTTPResponse
extends HTTPMessage {
    public static final int SC_OK = 200;
    public static final int SC_CREATED = 201;
    public static final int SC_FOUND = 302;
    public static final int SC_BAD_REQUEST = 400;
    public static final int SC_UNAUTHORIZED = 401;
    public static final int SC_FORBIDDEN = 403;
    public static final int SC_NOT_FOUND = 404;
    public static final int SC_SERVER_ERROR = 500;
    public static final int SC_SERVICE_UNAVAILABLE = 503;
    private final int statusCode;
    private String statusMessage;
    private String content = null;

    public HTTPResponse(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public boolean indicatesSuccess() {
        return this.statusCode >= 200 && this.statusCode < 300;
    }

    public void ensureStatusCode(int ... expectedStatusCode) throws ParseException {
        for (int c : expectedStatusCode) {
            if (this.statusCode != c) continue;
            return;
        }
        throw new ParseException("Unexpected HTTP status code " + this.statusCode + ", must be " + Arrays.toString(expectedStatusCode));
    }

    public void ensureStatusCodeNotOK() throws ParseException {
        if (this.statusCode == 200) {
            throw new ParseException("Unexpected HTTP status code, must not be 200 (OK)");
        }
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    public void setStatusMessage(String message) {
        this.statusMessage = message;
    }

    public URI getLocation() {
        String value = this.getHeaderValue("Location");
        if (value == null) {
            return null;
        }
        try {
            return new URI(value);
        }
        catch (URISyntaxException e) {
            return null;
        }
    }

    public void setLocation(URI location) {
        this.setHeader("Location", new String[]{location != null ? location.toString() : null});
    }

    public String getCacheControl() {
        return this.getHeaderValue("Cache-Control");
    }

    public void setCacheControl(String cacheControl) {
        this.setHeader("Cache-Control", new String[]{cacheControl});
    }

    public String getPragma() {
        return this.getHeaderValue("Pragma");
    }

    public void setPragma(String pragma) {
        this.setHeader("Pragma", new String[]{pragma});
    }

    public String getWWWAuthenticate() {
        return this.getHeaderValue("WWW-Authenticate");
    }

    public void setWWWAuthenticate(String wwwAuthenticate) {
        this.setHeader("WWW-Authenticate", new String[]{wwwAuthenticate});
    }

    private void ensureContent() throws ParseException {
        if (this.content == null || this.content.isEmpty()) {
            throw new ParseException("Missing or empty HTTP response body");
        }
    }

    public String getContent() {
        return this.content;
    }

    public JSONObject getContentAsJSONObject() throws ParseException {
        this.ensureEntityContentType(ContentType.APPLICATION_JSON);
        this.ensureContent();
        return JSONObjectUtils.parse(this.content);
    }

    public JSONArray getContentAsJSONArray() throws ParseException {
        this.ensureEntityContentType(ContentType.APPLICATION_JSON);
        this.ensureContent();
        return JSONArrayUtils.parse(this.content);
    }

    public JWT getContentAsJWT() throws ParseException {
        this.ensureEntityContentType(ContentType.APPLICATION_JWT);
        this.ensureContent();
        try {
            return JWTParser.parse((String)this.content);
        }
        catch (java.text.ParseException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }

    public void setContent(String content) {
        this.content = content;
    }
}

