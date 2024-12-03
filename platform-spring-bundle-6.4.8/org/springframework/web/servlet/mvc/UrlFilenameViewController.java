/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.servlet.mvc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.AbstractUrlViewController;
import org.springframework.web.util.ServletRequestPathUtils;

public class UrlFilenameViewController
extends AbstractUrlViewController {
    private String prefix = "";
    private String suffix = "";
    private final Map<String, String> viewNameCache = new ConcurrentHashMap<String, String>(256);

    public void setPrefix(@Nullable String prefix) {
        this.prefix = prefix != null ? prefix : "";
    }

    protected String getPrefix() {
        return this.prefix;
    }

    public void setSuffix(@Nullable String suffix) {
        this.suffix = suffix != null ? suffix : "";
    }

    protected String getSuffix() {
        return this.suffix;
    }

    @Override
    protected String getViewNameForRequest(HttpServletRequest request) {
        String uri = this.extractOperableUrl(request);
        return this.getViewNameForUrlPath(uri);
    }

    protected String extractOperableUrl(HttpServletRequest request) {
        String urlPath = (String)request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        if (!StringUtils.hasText(urlPath)) {
            urlPath = ServletRequestPathUtils.getCachedPathValue((ServletRequest)request);
        }
        return urlPath;
    }

    protected String getViewNameForUrlPath(String uri) {
        return this.viewNameCache.computeIfAbsent(uri, u -> this.postProcessViewName(this.extractViewNameFromUrlPath((String)u)));
    }

    protected String extractViewNameFromUrlPath(String uri) {
        int start = uri.charAt(0) == '/' ? 1 : 0;
        int lastIndex = uri.lastIndexOf(46);
        int end = lastIndex < 0 ? uri.length() : lastIndex;
        return uri.substring(start, end);
    }

    protected String postProcessViewName(String viewName) {
        return this.getPrefix() + viewName + this.getSuffix();
    }
}

