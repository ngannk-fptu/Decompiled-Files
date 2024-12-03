/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.multipart.MultipartConfig
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.rest.multipart;

import com.atlassian.confluence.plugins.hipchat.emoticons.service.AdminConfigurationService;
import com.atlassian.plugins.rest.common.multipart.MultipartConfig;

public class ConfluenceEmojiMultipartConfig
implements MultipartConfig {
    public long getMaxFileSize() {
        return AdminConfigurationService.getMaxFileSize();
    }

    public long getMaxSize() {
        return this.getMaxFileSize() + 3072L;
    }
}

