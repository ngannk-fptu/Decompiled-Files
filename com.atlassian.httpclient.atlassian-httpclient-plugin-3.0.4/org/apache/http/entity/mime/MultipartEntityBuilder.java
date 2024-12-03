/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.entity.mime;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.AbstractMultipartForm;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.HttpBrowserCompatibleMultipart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.HttpRFC6532Multipart;
import org.apache.http.entity.mime.HttpStrictMultipart;
import org.apache.http.entity.mime.MultipartFormEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.Args;

public class MultipartEntityBuilder {
    private static final char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final String DEFAULT_SUBTYPE = "form-data";
    private ContentType contentType;
    private HttpMultipartMode mode = HttpMultipartMode.STRICT;
    private String boundary = null;
    private Charset charset = null;
    private List<FormBodyPart> bodyParts = null;

    public static MultipartEntityBuilder create() {
        return new MultipartEntityBuilder();
    }

    MultipartEntityBuilder() {
    }

    public MultipartEntityBuilder setMode(HttpMultipartMode mode) {
        this.mode = mode;
        return this;
    }

    public MultipartEntityBuilder setLaxMode() {
        this.mode = HttpMultipartMode.BROWSER_COMPATIBLE;
        return this;
    }

    public MultipartEntityBuilder setStrictMode() {
        this.mode = HttpMultipartMode.STRICT;
        return this;
    }

    public MultipartEntityBuilder setBoundary(String boundary) {
        this.boundary = boundary;
        return this;
    }

    public MultipartEntityBuilder setMimeSubtype(String subType) {
        Args.notBlank(subType, "MIME subtype");
        this.contentType = ContentType.create("multipart/" + subType);
        return this;
    }

    @Deprecated
    public MultipartEntityBuilder seContentType(ContentType contentType) {
        return this.setContentType(contentType);
    }

    public MultipartEntityBuilder setContentType(ContentType contentType) {
        Args.notNull(contentType, "Content type");
        this.contentType = contentType;
        return this;
    }

    public MultipartEntityBuilder setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public MultipartEntityBuilder addPart(FormBodyPart bodyPart) {
        if (bodyPart == null) {
            return this;
        }
        if (this.bodyParts == null) {
            this.bodyParts = new ArrayList<FormBodyPart>();
        }
        this.bodyParts.add(bodyPart);
        return this;
    }

    public MultipartEntityBuilder addPart(String name, ContentBody contentBody) {
        Args.notNull(name, "Name");
        Args.notNull(contentBody, "Content body");
        return this.addPart(FormBodyPartBuilder.create(name, contentBody).build());
    }

    public MultipartEntityBuilder addTextBody(String name, String text, ContentType contentType) {
        return this.addPart(name, new StringBody(text, contentType));
    }

    public MultipartEntityBuilder addTextBody(String name, String text) {
        return this.addTextBody(name, text, ContentType.DEFAULT_TEXT);
    }

    public MultipartEntityBuilder addBinaryBody(String name, byte[] b, ContentType contentType, String filename) {
        return this.addPart(name, new ByteArrayBody(b, contentType, filename));
    }

    public MultipartEntityBuilder addBinaryBody(String name, byte[] b) {
        return this.addBinaryBody(name, b, ContentType.DEFAULT_BINARY, null);
    }

    public MultipartEntityBuilder addBinaryBody(String name, File file, ContentType contentType, String filename) {
        return this.addPart(name, new FileBody(file, contentType, filename));
    }

    public MultipartEntityBuilder addBinaryBody(String name, File file) {
        return this.addBinaryBody(name, file, ContentType.DEFAULT_BINARY, file != null ? file.getName() : null);
    }

    public MultipartEntityBuilder addBinaryBody(String name, InputStream stream, ContentType contentType, String filename) {
        return this.addPart(name, new InputStreamBody(stream, contentType, filename));
    }

    public MultipartEntityBuilder addBinaryBody(String name, InputStream stream) {
        return this.addBinaryBody(name, stream, ContentType.DEFAULT_BINARY, null);
    }

    private String generateBoundary() {
        StringBuilder buffer = new StringBuilder();
        Random rand = new Random();
        int count = rand.nextInt(11) + 30;
        for (int i = 0; i < count; ++i) {
            buffer.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        return buffer.toString();
    }

    MultipartFormEntity buildEntity() {
        AbstractMultipartForm form;
        Charset charsetCopy;
        String boundaryCopy = this.boundary;
        if (boundaryCopy == null && this.contentType != null) {
            boundaryCopy = this.contentType.getParameter("boundary");
        }
        if (boundaryCopy == null) {
            boundaryCopy = this.generateBoundary();
        }
        if ((charsetCopy = this.charset) == null && this.contentType != null) {
            charsetCopy = this.contentType.getCharset();
        }
        ArrayList<BasicNameValuePair> paramsList = new ArrayList<BasicNameValuePair>(2);
        paramsList.add(new BasicNameValuePair("boundary", boundaryCopy));
        if (charsetCopy != null) {
            paramsList.add(new BasicNameValuePair("charset", charsetCopy.name()));
        }
        NameValuePair[] params = paramsList.toArray(new NameValuePair[paramsList.size()]);
        ContentType contentTypeCopy = this.contentType != null ? this.contentType.withParameters(params) : ContentType.create("multipart/form-data", params);
        ArrayList<FormBodyPart> bodyPartsCopy = this.bodyParts != null ? new ArrayList<FormBodyPart>(this.bodyParts) : Collections.emptyList();
        HttpMultipartMode modeCopy = this.mode != null ? this.mode : HttpMultipartMode.STRICT;
        switch (modeCopy) {
            case BROWSER_COMPATIBLE: {
                form = new HttpBrowserCompatibleMultipart(charsetCopy, boundaryCopy, bodyPartsCopy);
                break;
            }
            case RFC6532: {
                form = new HttpRFC6532Multipart(charsetCopy, boundaryCopy, bodyPartsCopy);
                break;
            }
            default: {
                form = new HttpStrictMultipart(charsetCopy, boundaryCopy, bodyPartsCopy);
            }
        }
        return new MultipartFormEntity(form, contentTypeCopy, form.getTotalLength());
    }

    public HttpEntity build() {
        return this.buildEntity();
    }
}

