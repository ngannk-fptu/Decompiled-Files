/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.servlet;

public class DownloadException
extends Exception {
    public DownloadException(String message) {
        super(message);
    }

    public DownloadException(String message, Exception cause) {
        super(message, cause);
    }

    public DownloadException(Exception cause) {
        super(cause);
    }
}

