/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.cache.CachingStrategy
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.web.filter;

import com.atlassian.confluence.web.filter.CachingHeaders;
import com.atlassian.core.filters.cache.CachingStrategy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

abstract class AttachmentCachingStrategies {
    private static final String ATTACHMENTS_URL_PATTERN = "download/attachments";
    private static final String THUMBNAILS_URL_PATTERN = "download/thumbnails";

    AttachmentCachingStrategies() {
    }

    private static boolean isAttachmentUrl(String url) {
        return StringUtils.contains((CharSequence)url, (CharSequence)ATTACHMENTS_URL_PATTERN) || StringUtils.contains((CharSequence)url, (CharSequence)THUMBNAILS_URL_PATTERN);
    }

    static class DefaultCachingStrategy
    implements CachingStrategy {
        DefaultCachingStrategy() {
        }

        public boolean matches(HttpServletRequest request) {
            return AttachmentCachingStrategies.isAttachmentUrl(request.getRequestURI());
        }

        public void setCachingHeaders(HttpServletResponse response) {
            CachingHeaders.PREVENT_CACHING.apply(response);
        }
    }

    static class InternetExplorerSslCachingStrategy
    implements CachingStrategy {
        InternetExplorerSslCachingStrategy() {
        }

        public boolean matches(HttpServletRequest request) {
            return AttachmentCachingStrategies.isAttachmentUrl(request.getRequestURI()) && StringUtils.contains((CharSequence)request.getHeader("User-Agent"), (CharSequence)"MSIE");
        }

        public void setCachingHeaders(HttpServletResponse response) {
            CachingHeaders.PREVENT_CACHING_IE_SSL.apply(response);
        }
    }

    static class SpecificVersionCachingStrategy
    implements CachingStrategy {
        SpecificVersionCachingStrategy() {
        }

        public boolean matches(HttpServletRequest request) {
            return AttachmentCachingStrategies.isAttachmentUrl(request.getRequestURI()) && this.isForSpecificVersion(request);
        }

        private boolean isForSpecificVersion(HttpServletRequest request) {
            return request.getParameter("version") != null && request.getParameter("modificationDate") != null;
        }

        public void setCachingHeaders(HttpServletResponse response) {
            CachingHeaders.PRIVATE_LONG_TERM.apply(response);
        }
    }
}

