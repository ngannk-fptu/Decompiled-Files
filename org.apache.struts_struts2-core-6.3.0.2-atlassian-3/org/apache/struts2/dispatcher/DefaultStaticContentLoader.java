/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.HostConfig;
import org.apache.struts2.dispatcher.StaticContentLoader;

public class DefaultStaticContentLoader
implements StaticContentLoader {
    private final Logger LOG = LogManager.getLogger(DefaultStaticContentLoader.class);
    protected List<String> pathPrefixes;
    protected boolean serveStatic;
    protected String uiStaticContentPath;
    protected boolean serveStaticBrowserCache;
    protected final Calendar lastModifiedCal = Calendar.getInstance();
    protected String encoding;
    protected boolean devMode;

    @Inject(value="struts.serve.static")
    public void setServeStaticContent(String serveStaticContent) {
        this.serveStatic = BooleanUtils.toBoolean((String)serveStaticContent);
    }

    @Inject(value="struts.ui.staticContentPath")
    public void setStaticContentPath(String uiStaticContentPath) {
        this.uiStaticContentPath = StaticContentLoader.Validator.validateStaticContentPath(uiStaticContentPath);
    }

    @Inject(value="struts.serve.static.browserCache")
    public void setServeStaticBrowserCache(String serveStaticBrowserCache) {
        this.serveStaticBrowserCache = BooleanUtils.toBoolean((String)serveStaticBrowserCache);
    }

    @Inject(value="struts.i18n.encoding")
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Inject(value="struts.devMode")
    public void setDevMode(String devMode) {
        this.devMode = Boolean.parseBoolean(devMode);
    }

    @Override
    public void setHostConfig(HostConfig filterConfig) {
        String param = filterConfig.getInitParameter("packages");
        String packages = this.getAdditionalPackages();
        if (param != null) {
            packages = param + " " + packages;
        }
        this.pathPrefixes = this.parse(packages);
    }

    protected String getAdditionalPackages() {
        LinkedList<String> packages = new LinkedList<String>();
        packages.add("org.apache.struts2.static");
        packages.add("template");
        packages.add("static");
        if (this.devMode) {
            packages.add("org.apache.struts2.interceptor.debugging");
        }
        return StringUtils.join(packages.iterator(), (char)' ');
    }

    protected List<String> parse(String packages) {
        if (packages == null) {
            return Collections.emptyList();
        }
        ArrayList<String> pathPrefixes = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(packages, ", \n\t");
        while (st.hasMoreTokens()) {
            String pathPrefix = st.nextToken().replace('.', '/');
            if (!pathPrefix.endsWith("/")) {
                pathPrefix = pathPrefix + "/";
            }
            pathPrefixes.add(pathPrefix);
        }
        return pathPrefixes;
    }

    @Override
    public void findStaticResource(String path, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = this.cleanupPath(path);
        for (String pathPrefix : this.pathPrefixes) {
            InputStream is;
            block6: {
                URL resourceUrl = this.findResource(this.buildPath(name, pathPrefix));
                if (resourceUrl == null) continue;
                is = null;
                try {
                    String pathEnding = this.buildPath(name, pathPrefix);
                    if (!resourceUrl.getFile().endsWith(pathEnding)) break block6;
                    is = resourceUrl.openStream();
                }
                catch (IOException ex) {
                    continue;
                }
            }
            if (is == null) continue;
            this.process(is, path, request, response);
            return;
        }
        try {
            response.sendError(404);
        }
        catch (IOException e1) {
            this.LOG.warn("Unable to send error response, code: {};", (Object)404, (Object)e1);
        }
        catch (IllegalStateException ise) {
            this.LOG.warn("Unable to send error response, code: {}; isCommitted: {};", (Object)404, (Object)response.isCommitted(), (Object)ise);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void process(InputStream is, String path, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (is != null) {
            Calendar cal = Calendar.getInstance();
            long ifModifiedSince = 0L;
            try {
                ifModifiedSince = request.getDateHeader("If-Modified-Since");
            }
            catch (Exception e) {
                this.LOG.warn("Invalid If-Modified-Since header value: '{}', ignoring", (Object)request.getHeader("If-Modified-Since"));
            }
            long lastModifiedMillis = this.lastModifiedCal.getTimeInMillis();
            long now = cal.getTimeInMillis();
            cal.add(5, 1);
            long expires = cal.getTimeInMillis();
            if (ifModifiedSince > 0L && ifModifiedSince <= lastModifiedMillis) {
                response.setDateHeader("Expires", expires);
                response.setStatus(304);
                is.close();
                return;
            }
            String contentType = this.getContentType(path);
            if (contentType != null) {
                response.setContentType(contentType);
            }
            if (this.serveStaticBrowserCache) {
                response.setDateHeader("Date", now);
                response.setDateHeader("Expires", expires);
                response.setDateHeader("Retry-After", expires);
                response.setHeader("Cache-Control", "public");
                response.setDateHeader("Last-Modified", lastModifiedMillis);
            } else {
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Expires", "-1");
            }
            try {
                this.copy(is, (OutputStream)response.getOutputStream());
            }
            finally {
                is.close();
            }
        }
    }

    protected URL findResource(String path) throws IOException {
        return ClassLoaderUtil.getResource(path, this.getClass());
    }

    protected String buildPath(String name, String packagePrefix) throws UnsupportedEncodingException {
        String resourcePath = packagePrefix.endsWith("/") && name.startsWith("/") ? packagePrefix + name.substring(1) : packagePrefix + name;
        return URLDecoder.decode(resourcePath, this.encoding);
    }

    protected String getContentType(String name) {
        if (name.endsWith(".js")) {
            return "text/javascript";
        }
        if (name.endsWith(".css")) {
            return "text/css";
        }
        if (name.endsWith(".html")) {
            return "text/html";
        }
        if (name.endsWith(".txt")) {
            return "text/plain";
        }
        if (name.endsWith(".gif")) {
            return "image/gif";
        }
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (name.endsWith(".png")) {
            return "image/png";
        }
        return null;
    }

    protected void copy(InputStream input, OutputStream output) throws IOException {
        int n;
        byte[] buffer = new byte[4096];
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        output.flush();
    }

    @Override
    public boolean canHandle(String resourcePath) {
        return this.serveStatic && resourcePath.startsWith(this.uiStaticContentPath + "/");
    }

    protected String cleanupPath(String path) {
        if (path.startsWith(this.uiStaticContentPath)) {
            return path.substring(this.uiStaticContentPath.length());
        }
        return path;
    }
}

