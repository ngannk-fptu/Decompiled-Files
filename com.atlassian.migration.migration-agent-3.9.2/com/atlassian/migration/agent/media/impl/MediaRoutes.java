/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.media.impl;

public enum MediaRoutes {
    CHUNK("%1$s/chunk/%2$s?uploadId=%3$s&partNumber=%4$s"),
    CREATE_FILE_FROM_CHUNKS("%1$s/file/chunked"),
    CREATE_FILE_FROM_UPLOAD("%1$s/file/upload"),
    POST_FILE_BINARY("%1$s/file/binary"),
    CREATE_UPLOAD("%1$s/upload"),
    PUT_UPLOAD_CHUNKS("%1$s/upload/%2$s/chunks"),
    POST_CLIENT_IDENTITY("%1$s/client");

    private final String urlTemplate;

    private MediaRoutes(String urlTemplate) {
        this.urlTemplate = urlTemplate;
    }

    public String getUrl(String ... args) {
        return String.format(this.urlTemplate, args);
    }
}

