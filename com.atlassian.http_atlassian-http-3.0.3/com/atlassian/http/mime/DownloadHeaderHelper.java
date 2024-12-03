/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.atlassian.http.mime;

import com.atlassian.http.mime.ContentDispositionHeaderGuesser;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;

public class DownloadHeaderHelper {
    private String contentDisposition;
    private String contentType;
    private static final ImmutableMap<String, String> CONSTANT_HEADER = new ImmutableMap.Builder().put((Object)"X-Content-Type-Options", (Object)"nosniff").put((Object)"X-Download-Options", (Object)"noopen").build();

    public DownloadHeaderHelper(ContentDispositionHeaderGuesser contentDispositionHeaderGuesser, String fileName, String contentType, String userAgent) {
        this.contentType = contentType;
        this.contentDisposition = contentDispositionHeaderGuesser.guessContentDispositionHeader(fileName, this.contentType, userAgent);
    }

    public DownloadHeaderHelper(String contentDisposition, String fileName, String contentType) {
        this.contentDisposition = contentDisposition;
        this.contentType = contentType;
    }

    public DownloadHeaderHelper(String contentDisposition, String contentType) {
        this(contentDisposition, null, contentType);
    }

    public HashMap<String, String> getDownloadHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.putAll((Map<String, String>)CONSTANT_HEADER);
        headers.put("Content-Type", this.contentType);
        headers.put("Content-Disposition", this.contentDisposition);
        return headers;
    }

    public String getContentDisposition() {
        return this.contentDisposition;
    }

    public String getContentType() {
        return this.contentType;
    }
}

