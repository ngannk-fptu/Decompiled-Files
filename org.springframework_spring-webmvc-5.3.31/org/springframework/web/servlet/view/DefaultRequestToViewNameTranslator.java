/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 *  org.springframework.web.util.ServletRequestPathUtils
 *  org.springframework.web.util.UrlPathHelper
 */
package org.springframework.web.servlet.view;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UrlPathHelper;

public class DefaultRequestToViewNameTranslator
implements RequestToViewNameTranslator {
    private static final String SLASH = "/";
    private String prefix = "";
    private String suffix = "";
    private String separator = "/";
    private boolean stripLeadingSlash = true;
    private boolean stripTrailingSlash = true;
    private boolean stripExtension = true;

    public void setPrefix(@Nullable String prefix) {
        this.prefix = prefix != null ? prefix : "";
    }

    public void setSuffix(@Nullable String suffix) {
        this.suffix = suffix != null ? suffix : "";
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public void setStripLeadingSlash(boolean stripLeadingSlash) {
        this.stripLeadingSlash = stripLeadingSlash;
    }

    public void setStripTrailingSlash(boolean stripTrailingSlash) {
        this.stripTrailingSlash = stripTrailingSlash;
    }

    public void setStripExtension(boolean stripExtension) {
        this.stripExtension = stripExtension;
    }

    @Deprecated
    public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
    }

    @Deprecated
    public void setUrlDecode(boolean urlDecode) {
    }

    @Deprecated
    public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
    }

    @Deprecated
    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
    }

    @Override
    public String getViewName(HttpServletRequest request) {
        String path = ServletRequestPathUtils.getCachedPathValue((ServletRequest)request);
        return this.prefix + this.transformPath(path) + this.suffix;
    }

    @Nullable
    protected String transformPath(String lookupPath) {
        String path = lookupPath;
        if (this.stripLeadingSlash && path.startsWith(SLASH)) {
            path = path.substring(1);
        }
        if (this.stripTrailingSlash && path.endsWith(SLASH)) {
            path = path.substring(0, path.length() - 1);
        }
        if (this.stripExtension) {
            path = StringUtils.stripFilenameExtension((String)path);
        }
        if (!SLASH.equals(this.separator)) {
            path = StringUtils.replace((String)path, (String)SLASH, (String)this.separator);
        }
        return path;
    }
}

