/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.validator.routines.UrlValidator
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.widgetconnector.validation;

import com.atlassian.confluence.extra.widgetconnector.validation.ThumbnailPlaceholderUrlValidator;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.ImmutableSet;
import java.net.URI;
import java.util.Set;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultThumbnailPlaceholderUrlValidator
implements ThumbnailPlaceholderUrlValidator {
    private static final Set<String> ALLOWED_OVERLAY_NAMES = ImmutableSet.of((Object)"youtube", (Object)"shade");
    private static final UrlValidator URL_VALIDATOR = new UrlValidator(new String[]{"http", "https"}, 4L);
    private final OutboundWhitelist outboundWhitelist;
    private final UserManager userManager;

    @Autowired
    public DefaultThumbnailPlaceholderUrlValidator(@ComponentImport OutboundWhitelist outboundWhitelist, @ComponentImport UserManager userManager) {
        this.outboundWhitelist = outboundWhitelist;
        this.userManager = userManager;
    }

    @Override
    public boolean isValid(String thumbUrl, String overlayUrl) {
        if (overlayUrl != null) {
            overlayUrl = overlayUrl.toLowerCase();
        }
        UserKey userKey = this.userManager.getRemoteUserKey();
        return thumbUrl != null && overlayUrl != null && ALLOWED_OVERLAY_NAMES.contains(overlayUrl) && URL_VALIDATOR.isValid(thumbUrl) && this.outboundWhitelist.isAllowed(URI.create(thumbUrl), userKey);
    }
}

