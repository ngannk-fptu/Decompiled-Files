/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.util;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.util.TargetUrlNormalizer;
import com.atlassian.sal.api.ApplicationProperties;
import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public final class JsmUrlChecker {
    private static final String JSM_URL_PREFIX = "/servicedesk";
    private static final Logger log = LoggerFactory.getLogger(JsmUrlChecker.class);
    private final ApplicationProperties applicationProperties;
    private final TargetUrlNormalizer targetUrlNormalizer;

    @Inject
    public JsmUrlChecker(@ComponentImport ApplicationProperties applicationProperties, TargetUrlNormalizer targetUrlNormalizer) {
        this.applicationProperties = applicationProperties;
        this.targetUrlNormalizer = targetUrlNormalizer;
    }

    public boolean isJsmRequest(String path) {
        if (this.applicationProperties.getPlatformId().equals("jira")) {
            try {
                URI uri = this.targetUrlNormalizer.removeContextPathFromUriIfNeeded(new URI(path));
                return uri.getPath().startsWith(JSM_URL_PREFIX);
            }
            catch (URISyntaxException e) {
                log.info("Request not considered to be JSM-related", (Throwable)e);
            }
        }
        return false;
    }
}

