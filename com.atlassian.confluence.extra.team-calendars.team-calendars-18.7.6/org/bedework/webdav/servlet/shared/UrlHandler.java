/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.bedework.webdav.servlet.shared;

import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import org.bedework.util.misc.Util;
import org.bedework.webdav.servlet.shared.UrlPrefixer;
import org.bedework.webdav.servlet.shared.UrlUnprefixer;
import org.bedework.webdav.servlet.shared.WebdavException;

public class UrlHandler
implements UrlPrefixer,
UrlUnprefixer {
    private String urlPrefix;
    private final boolean relative;
    private String context;

    public UrlHandler(HttpServletRequest req, boolean relative) {
        String sp;
        this.relative = relative;
        String contextPath = req.getContextPath();
        if (contextPath == null || contextPath.equals(".")) {
            contextPath = "/";
        }
        if ((sp = req.getServletPath()) == null || sp.equals(".")) {
            sp = "/";
        }
        this.context = Util.buildPath(false, contextPath, "/", sp);
        if (this.context.equals("/")) {
            this.context = "";
        }
        this.urlPrefix = req.getRequestURL().toString();
        int pos = this.context.length() > 0 ? this.urlPrefix.indexOf(this.context) : this.urlPrefix.indexOf(req.getRequestURI());
        if (pos > 0) {
            this.urlPrefix = this.urlPrefix.substring(0, pos);
        }
    }

    public UrlHandler(String urlPrefix, String context, boolean relative) {
        this.relative = relative;
        this.context = context == null || context.equals("/") ? "" : (context.endsWith("/") ? context.substring(0, context.length() - 1) : context);
        this.urlPrefix = urlPrefix;
    }

    @Override
    public String prefix(String val) throws WebdavException {
        try {
            if (val == null) {
                return null;
            }
            if (val.toLowerCase().startsWith("mailto:")) {
                return val;
            }
            String enc = new URI(null, null, val, null).toString();
            enc = new URI(enc).toASCIIString();
            StringBuilder sb = new StringBuilder();
            if (!this.relative) {
                sb.append(this.getUrlPrefix());
            }
            if (!val.startsWith(this.context + "/")) {
                this.append(sb, this.context);
            }
            this.append(sb, enc);
            return sb.toString();
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public String unprefix(String val) throws WebdavException {
        if (val == null) {
            return null;
        }
        if (val.startsWith(this.getUrlPrefix())) {
            val = val.substring(this.getUrlPrefix().length());
        }
        if (val.startsWith(this.context)) {
            val = val.substring(this.context.length());
        }
        return val;
    }

    public String getUrlPrefix() {
        return this.urlPrefix;
    }

    private boolean endsWithSlash(StringBuilder sb) {
        if (sb.length() == 0) {
            return false;
        }
        return sb.charAt(sb.length() - 1) == '/';
    }

    private void append(StringBuilder sb, String val) {
        if (val.startsWith("/")) {
            if (!this.endsWithSlash(sb)) {
                sb.append(val);
            } else {
                sb.append(val.substring(1));
            }
        } else {
            if (!this.endsWithSlash(sb)) {
                sb.append("/");
            }
            sb.append(val);
        }
    }
}

