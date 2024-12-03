/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.resource;

public enum DownloadResourcePrefixEnum {
    TOKEN_AUTH_ATTACHMENT_DOWNLOAD_RESOURCE_PREFIX("/download/token-auth/attachments"),
    ATTACHMENT_DOWNLOAD_RESOURCE_PREFIX("/download/attachments"),
    THUMBNAIL_DOWNLOAD_RESOURCE_PREFIX("/download/thumbnails"),
    ICON_DOWNLOAD_RESOURCE_PREFIX("/images/icons"),
    PACKAGE_DOWNLOAD_RESOURCE_PREFIX("/packages");

    private String prefix;

    private DownloadResourcePrefixEnum(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return this.prefix;
    }
}

