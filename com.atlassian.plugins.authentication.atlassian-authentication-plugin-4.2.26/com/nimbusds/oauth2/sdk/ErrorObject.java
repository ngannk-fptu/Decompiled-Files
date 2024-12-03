/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class ErrorObject {
    private final String code;
    private final String description;
    private final int httpStatusCode;
    private final URI uri;

    public ErrorObject(String code) {
        this(code, null, 0, null);
    }

    public ErrorObject(String code, String description) {
        this(code, description, 0, null);
    }

    public ErrorObject(String code, String description, int httpStatusCode) {
        this(code, description, httpStatusCode, null);
    }

    public ErrorObject(String code, String description, int httpStatusCode, URI uri) {
        if (!ErrorObject.isLegal(code)) {
            throw new IllegalArgumentException("Illegal char(s) in code, see RFC 6749, section 5.2");
        }
        this.code = code;
        if (!ErrorObject.isLegal(description)) {
            throw new IllegalArgumentException("Illegal char(s) in description, see RFC 6749, section 5.2");
        }
        this.description = description;
        this.httpStatusCode = httpStatusCode;
        this.uri = uri;
    }

    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    public ErrorObject setDescription(String description) {
        return new ErrorObject(this.getCode(), description, this.getHTTPStatusCode(), this.getURI());
    }

    public ErrorObject appendDescription(String text) {
        String newDescription = this.getDescription() != null ? this.getDescription() + text : text;
        return new ErrorObject(this.getCode(), newDescription, this.getHTTPStatusCode(), this.getURI());
    }

    public int getHTTPStatusCode() {
        return this.httpStatusCode;
    }

    public ErrorObject setHTTPStatusCode(int httpStatusCode) {
        return new ErrorObject(this.getCode(), this.getDescription(), httpStatusCode, this.getURI());
    }

    public URI getURI() {
        return this.uri;
    }

    public ErrorObject setURI(URI uri) {
        return new ErrorObject(this.getCode(), this.getDescription(), this.getHTTPStatusCode(), uri);
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        if (this.code != null) {
            o.put("error", this.code);
        }
        if (this.description != null) {
            o.put("error_description", this.description);
        }
        if (this.uri != null) {
            o.put("error_uri", this.uri.toString());
        }
        return o;
    }

    public Map<String, List<String>> toParameters() {
        HashMap<String, List<String>> params = new HashMap<String, List<String>>();
        if (this.getCode() != null) {
            params.put("error", Collections.singletonList(this.getCode()));
        }
        if (this.getDescription() != null) {
            params.put("error_description", Collections.singletonList(this.getDescription()));
        }
        if (this.getURI() != null) {
            params.put("error_uri", Collections.singletonList(this.getURI().toString()));
        }
        return params;
    }

    public HTTPResponse toHTTPResponse() {
        int statusCode = this.getHTTPStatusCode() > 0 ? this.getHTTPStatusCode() : 400;
        HTTPResponse httpResponse = new HTTPResponse(statusCode);
        httpResponse.setCacheControl("no-store");
        httpResponse.setPragma("no-cache");
        if (this.getCode() != null) {
            httpResponse.setEntityContentType(ContentType.APPLICATION_JSON);
            httpResponse.setContent(this.toJSONObject().toJSONString());
        }
        return httpResponse;
    }

    public String toString() {
        return this.code != null ? this.code : "null";
    }

    public int hashCode() {
        return this.code != null ? this.code.hashCode() : "null".hashCode();
    }

    public boolean equals(Object object) {
        return object instanceof ErrorObject && this.toString().equals(object.toString());
    }

    public static ErrorObject parse(JSONObject jsonObject) {
        String code = null;
        try {
            code = JSONObjectUtils.getString(jsonObject, "error", null);
        }
        catch (ParseException parseException) {
            // empty catch block
        }
        if (!ErrorObject.isLegal(code)) {
            code = null;
        }
        String description = null;
        try {
            description = JSONObjectUtils.getString(jsonObject, "error_description", null);
        }
        catch (ParseException parseException) {
            // empty catch block
        }
        if (!ErrorObject.isLegal(description)) {
            description = null;
        }
        URI uri = null;
        try {
            uri = JSONObjectUtils.getURI(jsonObject, "error_uri", null);
        }
        catch (ParseException parseException) {
            // empty catch block
        }
        return new ErrorObject(code, description, 0, uri);
    }

    public static ErrorObject parse(Map<String, List<String>> params) {
        String code = MultivaluedMapUtils.getFirstValue(params, "error");
        String description = MultivaluedMapUtils.getFirstValue(params, "error_description");
        String uriString = MultivaluedMapUtils.getFirstValue(params, "error_uri");
        URI uri = null;
        if (uriString != null) {
            try {
                uri = new URI(uriString);
            }
            catch (URISyntaxException uRISyntaxException) {
                // empty catch block
            }
        }
        if (!ErrorObject.isLegal(code)) {
            code = null;
        }
        if (!ErrorObject.isLegal(description)) {
            description = null;
        }
        return new ErrorObject(code, description, 0, uri);
    }

    public static ErrorObject parse(HTTPResponse httpResponse) {
        JSONObject jsonObject;
        try {
            jsonObject = httpResponse.getContentAsJSONObject();
        }
        catch (ParseException e) {
            return new ErrorObject(null, null, httpResponse.getStatusCode());
        }
        ErrorObject intermediary = ErrorObject.parse(jsonObject);
        return new ErrorObject(intermediary.getCode(), intermediary.description, httpResponse.getStatusCode(), intermediary.getURI());
    }

    public static boolean isLegal(String s) {
        if (s == null) {
            return true;
        }
        for (char c : s.toCharArray()) {
            if (ErrorObject.isLegal(c)) continue;
            return false;
        }
        return true;
    }

    public static boolean isLegal(char c) {
        if (c > '\u007f') {
            return false;
        }
        return c >= ' ' && c <= '!' || c >= '#' && c <= '[' || c >= ']' && c <= '~';
    }
}

