/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.util.Base64
 *  net.jcip.annotations.Immutable
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences.attachment;

import com.nimbusds.jose.util.Base64;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.Attachment;
import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.AttachmentType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.Content;
import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.Digest;
import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.DigestMismatchException;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class ExternalAttachment
extends Attachment {
    private final URI url;
    private final BearerAccessToken accessToken;
    private final long expiresIn;
    private final Digest digest;

    public ExternalAttachment(URI url, BearerAccessToken accessToken, long expiresIn, Digest digest, String description) {
        super(AttachmentType.EXTERNAL, description);
        Objects.requireNonNull(url);
        this.url = url;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        Objects.requireNonNull(digest);
        this.digest = digest;
    }

    public URI getURL() {
        return this.url;
    }

    public BearerAccessToken getBearerAccessToken() {
        return this.accessToken;
    }

    public long getExpiresIn() {
        return this.expiresIn;
    }

    public Digest getDigest() {
        return this.digest;
    }

    public Content retrieveContent(int httpConnectTimeout, int httpReadTimeout) throws IOException, NoSuchAlgorithmException, DigestMismatchException {
        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.GET, this.getURL());
        if (this.getBearerAccessToken() != null) {
            httpRequest.setAuthorization(this.getBearerAccessToken().toAuthorizationHeader());
        }
        httpRequest.setConnectTimeout(httpConnectTimeout);
        httpRequest.setReadTimeout(httpReadTimeout);
        HTTPResponse httpResponse = httpRequest.send();
        try {
            httpResponse.ensureStatusCode(200);
        }
        catch (ParseException e) {
            throw new IOException(e.getMessage(), e);
        }
        if (httpResponse.getEntityContentType() == null) {
            throw new IOException("Missing Content-Type header in HTTP response: " + this.url);
        }
        if (StringUtils.isBlank(httpResponse.getContent())) {
            throw new IOException("The HTTP response has no content: " + this.url);
        }
        Base64 contentBase64 = new Base64(httpResponse.getContent().trim());
        if (!this.getDigest().matches(contentBase64)) {
            throw new DigestMismatchException("The computed " + this.digest.getHashAlgorithm() + " digest for the retrieved content doesn't match the expected: " + this.getURL());
        }
        return new Content(httpResponse.getEntityContentType(), contentBase64, this.getDescriptionString());
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = super.toJSONObject();
        jsonObject.put((Object)"url", (Object)this.getURL().toString());
        if (this.getBearerAccessToken() != null) {
            jsonObject.put((Object)"access_token", (Object)this.getBearerAccessToken().getValue());
        }
        if (this.expiresIn > 0L) {
            jsonObject.put((Object)"expires_in", (Object)this.getExpiresIn());
        }
        jsonObject.put((Object)"digest", (Object)this.getDigest().toJSONObject());
        return jsonObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExternalAttachment)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ExternalAttachment that = (ExternalAttachment)o;
        return this.getExpiresIn() == that.getExpiresIn() && this.url.equals(that.url) && Objects.equals(this.accessToken, that.accessToken) && this.getDigest().equals(that.getDigest());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.url, this.accessToken, this.getExpiresIn(), this.getDigest());
    }

    public static ExternalAttachment parse(JSONObject jsonObject) throws ParseException {
        URI url = JSONObjectUtils.getURI(jsonObject, "url");
        long expiresIn = 0L;
        if (jsonObject.get((Object)"expires_in") != null && (expiresIn = JSONObjectUtils.getLong(jsonObject, "expires_in")) < 1L) {
            throw new ParseException("The expires_in parameter must be a positive integer");
        }
        BearerAccessToken accessToken = null;
        if (jsonObject.get((Object)"access_token") != null) {
            String tokenValue = JSONObjectUtils.getString(jsonObject, "access_token");
            accessToken = expiresIn > 0L ? new BearerAccessToken(tokenValue, expiresIn, null) : new BearerAccessToken(tokenValue);
        }
        String description = JSONObjectUtils.getString(jsonObject, "desc", null);
        Digest digest = Digest.parse(JSONObjectUtils.getJSONObject(jsonObject, "digest"));
        return new ExternalAttachment(url, accessToken, expiresIn, digest, description);
    }
}

