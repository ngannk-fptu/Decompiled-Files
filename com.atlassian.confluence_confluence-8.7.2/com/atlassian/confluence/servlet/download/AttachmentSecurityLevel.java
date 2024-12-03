/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.http.mime.DownloadPolicy
 */
package com.atlassian.confluence.servlet.download;

import com.atlassian.http.mime.DownloadPolicy;
import java.util.Locale;

public enum AttachmentSecurityLevel {
    SMART(DownloadPolicy.Smart),
    INSECURE(DownloadPolicy.Insecure),
    SECURE(DownloadPolicy.Secure);

    private final DownloadPolicy policy;

    private AttachmentSecurityLevel(DownloadPolicy policy) {
        this.policy = policy;
    }

    public String getLevel() {
        return this.name().toLowerCase(Locale.ENGLISH);
    }

    public static AttachmentSecurityLevel fromLevel(String level) {
        return AttachmentSecurityLevel.valueOf(level.toUpperCase(Locale.ENGLISH));
    }

    public DownloadPolicy getDownloadPolicyLevel() {
        return this.policy;
    }
}

