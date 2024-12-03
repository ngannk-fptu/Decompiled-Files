/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 */
package groovy.servlet;

import groovy.servlet.ServletBinding;
import groovy.util.ResourceConnector;
import groovy.util.ResourceException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

public abstract class AbstractHttpServlet
extends HttpServlet
implements ResourceConnector {
    public static final String INIT_PARAM_RESOURCE_NAME_REGEX = "resource.name.regex";
    public static final String INIT_PARAM_RESOURCE_NAME_REGEX_FLAGS = "resource.name.regex.flags";
    public static final String CONTENT_TYPE_TEXT_HTML = "text/html";
    public static final String INC_PATH_INFO = "javax.servlet.include.path_info";
    public static final String INC_REQUEST_URI = "javax.servlet.include.request_uri";
    public static final String INC_SERVLET_PATH = "javax.servlet.include.servlet_path";
    protected ServletContext servletContext = null;
    protected Pattern resourceNamePattern;
    protected String resourceNameReplacement = null;
    protected boolean resourceNameReplaceAll = true;
    protected boolean verbose = false;
    protected String encoding = "UTF-8";
    protected boolean reflection = false;
    private boolean logGROOVY861 = false;
    protected String namePrefix;

    protected void generateNamePrefixOnce() {
        URI uri = null;
        String realPath = this.servletContext.getRealPath("/");
        if (realPath != null) {
            uri = new File(realPath).toURI();
        }
        try {
            URL res = this.servletContext.getResource("/");
            if (res != null) {
                uri = res.toURI();
            }
        }
        catch (MalformedURLException res) {
        }
        catch (URISyntaxException res) {
            // empty catch block
        }
        if (uri != null) {
            try {
                this.namePrefix = uri.toURL().toExternalForm();
                return;
            }
            catch (MalformedURLException e) {
                this.log("generateNamePrefixOnce [ERROR] Malformed URL for base path / == '" + uri + '\'', e);
            }
        }
        this.namePrefix = "";
    }

    protected String removeNamePrefix(String name) throws ResourceException {
        if (this.namePrefix == null) {
            this.generateNamePrefixOnce();
        }
        if (name.startsWith(this.namePrefix)) {
            return name.substring(this.namePrefix.length());
        }
        return name;
    }

    @Override
    public URLConnection getResourceConnection(String name) throws ResourceException {
        if ((name = this.removeNamePrefix(name).replace('\\', '/')).startsWith("WEB-INF/groovy/")) {
            name = name.substring(15);
        } else if (name.startsWith("/")) {
            name = name.substring(1);
        }
        try {
            URL url = this.servletContext.getResource('/' + name);
            if (url == null) {
                url = this.servletContext.getResource("/WEB-INF/groovy/" + name);
            }
            if (url == null) {
                throw new ResourceException("Resource \"" + name + "\" not found!");
            }
            return url.openConnection();
        }
        catch (IOException e) {
            throw new ResourceException("Problems getting resource named \"" + name + "\"!", e);
        }
    }

    protected String getScriptUri(HttpServletRequest request) {
        if (this.logGROOVY861) {
            this.log("Logging request class and its class loader:");
            this.log(" c = request.getClass() :\"" + request.getClass() + "\"");
            this.log(" l = c.getClassLoader() :\"" + request.getClass().getClassLoader() + "\"");
            this.log(" l.getClass()           :\"" + request.getClass().getClassLoader().getClass() + "\"");
            this.logGROOVY861 = this.verbose;
        }
        String uri = null;
        String info = null;
        uri = (String)request.getAttribute(INC_SERVLET_PATH);
        if (uri != null) {
            info = (String)request.getAttribute(INC_PATH_INFO);
            if (info != null) {
                uri = uri + info;
            }
            return this.applyResourceNameMatcher(uri);
        }
        uri = request.getServletPath();
        info = request.getPathInfo();
        if (info != null) {
            uri = uri + info;
        }
        return this.applyResourceNameMatcher(uri);
    }

    protected String applyResourceNameMatcher(String uri) {
        if (this.resourceNamePattern != null) {
            Matcher matcher = this.resourceNamePattern.matcher(uri);
            String replaced = this.resourceNameReplaceAll ? matcher.replaceAll(this.resourceNameReplacement) : matcher.replaceFirst(this.resourceNameReplacement);
            if (!uri.equals(replaced)) {
                if (this.verbose) {
                    this.log("Replaced resource name \"" + uri + "\" with \"" + replaced + "\".");
                }
                return replaced;
            }
        }
        return uri;
    }

    protected File getScriptUriAsFile(HttpServletRequest request) {
        String uri = this.getScriptUri(request);
        String real = this.servletContext.getRealPath(uri);
        if (real == null) {
            return null;
        }
        return new File(real).getAbsoluteFile();
    }

    public void init(ServletConfig config) throws ServletException {
        String regex;
        super.init(config);
        this.servletContext = config.getServletContext();
        String value = config.getInitParameter("verbose");
        if (value != null) {
            this.verbose = Boolean.valueOf(value);
        }
        if ((value = config.getInitParameter("encoding")) != null) {
            this.encoding = value;
        }
        if (this.verbose) {
            this.log("Parsing init parameters...");
        }
        if ((regex = config.getInitParameter(INIT_PARAM_RESOURCE_NAME_REGEX)) != null) {
            String replacement = config.getInitParameter("resource.name.replacement");
            if (replacement == null) {
                NullPointerException npex = new NullPointerException("resource.name.replacement");
                String message = "Init-param 'resource.name.replacement' not specified!";
                this.log(message, npex);
                throw new ServletException(message, (Throwable)npex);
            }
            if ("EMPTY_STRING".equals(replacement)) {
                replacement = "";
            }
            int flags = 0;
            String flagsStr = config.getInitParameter(INIT_PARAM_RESOURCE_NAME_REGEX_FLAGS);
            if (flagsStr != null && flagsStr.length() > 0) {
                flags = Integer.decode(flagsStr.trim());
            }
            this.resourceNamePattern = Pattern.compile(regex, flags);
            this.resourceNameReplacement = replacement;
            String all = config.getInitParameter("resource.name.replace.all");
            if (all != null) {
                this.resourceNameReplaceAll = Boolean.valueOf(all.trim());
            }
        }
        if ((value = config.getInitParameter("logGROOVY861")) != null) {
            this.logGROOVY861 = Boolean.valueOf(value);
        }
        if (this.verbose) {
            this.log("(Abstract) init done. Listing some parameter name/value pairs:");
            this.log("verbose = " + this.verbose);
            this.log("reflection = " + this.reflection);
            this.log("logGROOVY861 = " + this.logGROOVY861);
            this.log("resource.name.regex = " + this.resourceNamePattern);
            this.log("resource.name.replacement = " + this.resourceNameReplacement);
        }
    }

    protected void setVariables(ServletBinding binding) {
    }
}

