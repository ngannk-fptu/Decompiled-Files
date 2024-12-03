/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.catalina.connector.Connector
 *  org.apache.catalina.connector.Request
 *  org.apache.tomcat.util.buf.B2CConverter
 *  org.apache.tomcat.util.buf.UDecoder
 *  org.apache.tomcat.util.http.RequestUtil
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ssi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.catalina.ssi.ByteArrayServletOutputStream;
import org.apache.catalina.ssi.ResponseIncludeWrapper;
import org.apache.catalina.ssi.SSIExternalResolver;
import org.apache.catalina.ssi.SSIServletRequestUtil;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.http.RequestUtil;
import org.apache.tomcat.util.res.StringManager;

public class SSIServletExternalResolver
implements SSIExternalResolver {
    private static final StringManager sm = StringManager.getManager(SSIServletExternalResolver.class);
    protected final String[] VARIABLE_NAMES = new String[]{"AUTH_TYPE", "CONTENT_LENGTH", "CONTENT_TYPE", "DOCUMENT_NAME", "DOCUMENT_URI", "GATEWAY_INTERFACE", "HTTP_ACCEPT", "HTTP_ACCEPT_ENCODING", "HTTP_ACCEPT_LANGUAGE", "HTTP_CONNECTION", "HTTP_HOST", "HTTP_REFERER", "HTTP_USER_AGENT", "PATH_INFO", "PATH_TRANSLATED", "QUERY_STRING", "QUERY_STRING_UNESCAPED", "REMOTE_ADDR", "REMOTE_HOST", "REMOTE_PORT", "REMOTE_USER", "REQUEST_METHOD", "REQUEST_URI", "SCRIPT_FILENAME", "SCRIPT_NAME", "SERVER_ADDR", "SERVER_NAME", "SERVER_PORT", "SERVER_PROTOCOL", "SERVER_SOFTWARE", "UNIQUE_ID"};
    protected final ServletContext context;
    protected final HttpServletRequest req;
    protected final HttpServletResponse res;
    protected final boolean isVirtualWebappRelative;
    protected final int debug;
    protected final String inputEncoding;

    public SSIServletExternalResolver(ServletContext context, HttpServletRequest req, HttpServletResponse res, boolean isVirtualWebappRelative, int debug, String inputEncoding) {
        this.context = context;
        this.req = req;
        this.res = res;
        this.isVirtualWebappRelative = isVirtualWebappRelative;
        this.debug = debug;
        this.inputEncoding = inputEncoding;
    }

    @Override
    public void log(String message, Throwable throwable) {
        if (throwable != null) {
            this.context.log(message, throwable);
        } else {
            this.context.log(message);
        }
    }

    @Override
    public void addVariableNames(Collection<String> variableNames) {
        for (String variableName : this.VARIABLE_NAMES) {
            String variableValue = this.getVariableValue(variableName);
            if (variableValue == null) continue;
            variableNames.add(variableName);
        }
        Enumeration e = this.req.getAttributeNames();
        while (e.hasMoreElements()) {
            String name = (String)e.nextElement();
            if (this.isNameReserved(name)) continue;
            variableNames.add(name);
        }
    }

    protected Object getReqAttributeIgnoreCase(String targetName) {
        Object object;
        block1: {
            String name;
            object = null;
            if (this.isNameReserved(targetName) || (object = this.req.getAttribute(targetName)) != null) break block1;
            Enumeration e = this.req.getAttributeNames();
            while (e.hasMoreElements() && (!targetName.equalsIgnoreCase(name = (String)e.nextElement()) || this.isNameReserved(name) || (object = this.req.getAttribute(name)) == null)) {
            }
        }
        return object;
    }

    protected boolean isNameReserved(String name) {
        return name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("sun.");
    }

    @Override
    public void setVariableValue(String name, String value) {
        if (!this.isNameReserved(name)) {
            this.req.setAttribute(name, (Object)value);
        }
    }

    @Override
    public String getVariableValue(String name) {
        String retVal = null;
        Object object = this.getReqAttributeIgnoreCase(name);
        retVal = object != null ? object.toString() : this.getCGIVariable(name);
        return retVal;
    }

    protected String getCGIVariable(String name) {
        String retVal = null;
        String[] nameParts = name.toUpperCase(Locale.ENGLISH).split("_");
        int requiredParts = 2;
        if (nameParts.length == 1) {
            if (nameParts[0].equals("PATH")) {
                requiredParts = 1;
            }
        } else if (nameParts[0].equals("AUTH")) {
            if (nameParts[1].equals("TYPE")) {
                retVal = this.req.getAuthType();
            }
        } else if (nameParts[0].equals("CONTENT")) {
            if (nameParts[1].equals("LENGTH")) {
                long contentLength = this.req.getContentLengthLong();
                if (contentLength >= 0L) {
                    retVal = Long.toString(contentLength);
                }
            } else if (nameParts[1].equals("TYPE")) {
                retVal = this.req.getContentType();
            }
        } else if (nameParts[0].equals("DOCUMENT")) {
            if (nameParts[1].equals("NAME")) {
                String requestURI = this.req.getRequestURI();
                retVal = requestURI.substring(requestURI.lastIndexOf(47) + 1);
            } else if (nameParts[1].equals("URI")) {
                retVal = this.req.getRequestURI();
            }
        } else if (name.equalsIgnoreCase("GATEWAY_INTERFACE")) {
            retVal = "CGI/1.1";
        } else if (nameParts[0].equals("HTTP")) {
            if (nameParts[1].equals("ACCEPT")) {
                Enumeration acceptHeaders;
                String accept = null;
                if (nameParts.length == 2) {
                    accept = "Accept";
                } else if (nameParts[2].equals("ENCODING")) {
                    requiredParts = 3;
                    accept = "Accept-Encoding";
                } else if (nameParts[2].equals("LANGUAGE")) {
                    requiredParts = 3;
                    accept = "Accept-Language";
                }
                if (accept != null && (acceptHeaders = this.req.getHeaders(accept)) != null && acceptHeaders.hasMoreElements()) {
                    StringBuilder rv = new StringBuilder((String)acceptHeaders.nextElement());
                    while (acceptHeaders.hasMoreElements()) {
                        rv.append(", ");
                        rv.append((String)acceptHeaders.nextElement());
                    }
                    retVal = rv.toString();
                }
            } else if (nameParts[1].equals("CONNECTION")) {
                retVal = this.req.getHeader("Connection");
            } else if (nameParts[1].equals("HOST")) {
                retVal = this.req.getHeader("Host");
            } else if (nameParts[1].equals("REFERER")) {
                retVal = this.req.getHeader("Referer");
            } else if (nameParts[1].equals("USER") && nameParts.length == 3 && nameParts[2].equals("AGENT")) {
                requiredParts = 3;
                retVal = this.req.getHeader("User-Agent");
            }
        } else if (nameParts[0].equals("PATH")) {
            if (nameParts[1].equals("INFO")) {
                retVal = this.req.getPathInfo();
            } else if (nameParts[1].equals("TRANSLATED")) {
                retVal = this.req.getPathTranslated();
            }
        } else if (nameParts[0].equals("QUERY")) {
            if (nameParts[1].equals("STRING")) {
                String queryString = this.req.getQueryString();
                if (nameParts.length == 2) {
                    retVal = this.nullToEmptyString(queryString);
                } else if (nameParts[2].equals("UNESCAPED")) {
                    requiredParts = 3;
                    if (queryString != null) {
                        Charset uriCharset = null;
                        Charset requestCharset = null;
                        boolean useBodyEncodingForURI = false;
                        if (this.req instanceof Request) {
                            try {
                                requestCharset = ((Request)this.req).getCoyoteRequest().getCharset();
                            }
                            catch (UnsupportedEncodingException unsupportedEncodingException) {
                                // empty catch block
                            }
                            Connector connector = ((Request)this.req).getConnector();
                            uriCharset = connector.getURICharset();
                            useBodyEncodingForURI = connector.getUseBodyEncodingForURI();
                        }
                        Charset queryStringCharset = useBodyEncodingForURI && requestCharset != null ? requestCharset : (uriCharset != null ? uriCharset : StandardCharsets.UTF_8);
                        retVal = UDecoder.URLDecode((String)queryString, (Charset)queryStringCharset);
                    }
                }
            }
        } else if (nameParts[0].equals("REMOTE")) {
            if (nameParts[1].equals("ADDR")) {
                retVal = this.req.getRemoteAddr();
            } else if (nameParts[1].equals("HOST")) {
                retVal = this.req.getRemoteHost();
            } else if (!nameParts[1].equals("IDENT")) {
                if (nameParts[1].equals("PORT")) {
                    retVal = Integer.toString(this.req.getRemotePort());
                } else if (nameParts[1].equals("USER")) {
                    retVal = this.req.getRemoteUser();
                }
            }
        } else if (nameParts[0].equals("REQUEST")) {
            if (nameParts[1].equals("METHOD")) {
                retVal = this.req.getMethod();
            } else if (nameParts[1].equals("URI") && (retVal = (String)this.req.getAttribute("javax.servlet.forward.request_uri")) == null) {
                retVal = this.req.getRequestURI();
            }
        } else if (nameParts[0].equals("SCRIPT")) {
            String scriptName = this.req.getServletPath();
            if (nameParts[1].equals("FILENAME")) {
                retVal = this.context.getRealPath(scriptName);
            } else if (nameParts[1].equals("NAME")) {
                retVal = scriptName;
            }
        } else if (nameParts[0].equals("SERVER")) {
            if (nameParts[1].equals("ADDR")) {
                retVal = this.req.getLocalAddr();
            }
            if (nameParts[1].equals("NAME")) {
                retVal = this.req.getServerName();
            } else if (nameParts[1].equals("PORT")) {
                retVal = Integer.toString(this.req.getServerPort());
            } else if (nameParts[1].equals("PROTOCOL")) {
                retVal = this.req.getProtocol();
            } else if (nameParts[1].equals("SOFTWARE")) {
                StringBuilder rv = new StringBuilder(this.context.getServerInfo());
                rv.append(' ');
                rv.append(System.getProperty("java.vm.name"));
                rv.append('/');
                rv.append(System.getProperty("java.vm.version"));
                rv.append(' ');
                rv.append(System.getProperty("os.name"));
                retVal = rv.toString();
            }
        } else if (name.equalsIgnoreCase("UNIQUE_ID")) {
            retVal = this.req.getRequestedSessionId();
        }
        if (requiredParts != nameParts.length) {
            return null;
        }
        return retVal;
    }

    @Override
    public Date getCurrentDate() {
        return new Date();
    }

    protected String nullToEmptyString(String string) {
        String retVal = string;
        if (retVal == null) {
            retVal = "";
        }
        return retVal;
    }

    protected String getPathWithoutFileName(String servletPath) {
        String retVal = null;
        int lastSlash = servletPath.lastIndexOf(47);
        if (lastSlash >= 0) {
            retVal = servletPath.substring(0, lastSlash + 1);
        }
        return retVal;
    }

    protected String getPathWithoutContext(String contextPath, String servletPath) {
        if (servletPath.startsWith(contextPath)) {
            return servletPath.substring(contextPath.length());
        }
        return servletPath;
    }

    protected String getAbsolutePath(String path) throws IOException {
        String pathWithoutContext = SSIServletRequestUtil.getRelativePath(this.req);
        String prefix = this.getPathWithoutFileName(pathWithoutContext);
        if (prefix == null) {
            throw new IOException(sm.getString("ssiServletExternalResolver.removeFilenameError", new Object[]{pathWithoutContext}));
        }
        String fullPath = prefix + path;
        String retVal = RequestUtil.normalize((String)fullPath);
        if (retVal == null) {
            throw new IOException(sm.getString("ssiServletExternalResolver.normalizationError", new Object[]{fullPath}));
        }
        return retVal;
    }

    protected ServletContextAndPath getServletContextAndPathFromNonVirtualPath(String nonVirtualPath) throws IOException {
        if (nonVirtualPath.startsWith("/") || nonVirtualPath.startsWith("\\")) {
            throw new IOException(sm.getString("ssiServletExternalResolver.absoluteNonVirtualPath", new Object[]{nonVirtualPath}));
        }
        if (nonVirtualPath.contains("../")) {
            throw new IOException(sm.getString("ssiServletExternalResolver.pathTraversalNonVirtualPath", new Object[]{nonVirtualPath}));
        }
        String path = this.getAbsolutePath(nonVirtualPath);
        ServletContextAndPath csAndP = new ServletContextAndPath(this.context, path);
        return csAndP;
    }

    protected ServletContextAndPath getServletContextAndPathFromVirtualPath(String virtualPath) throws IOException {
        if (!virtualPath.startsWith("/") && !virtualPath.startsWith("\\")) {
            return new ServletContextAndPath(this.context, this.getAbsolutePath(virtualPath));
        }
        String normalized = RequestUtil.normalize((String)virtualPath);
        if (this.isVirtualWebappRelative) {
            return new ServletContextAndPath(this.context, normalized);
        }
        ServletContext normContext = this.context.getContext(normalized);
        if (normContext == null) {
            throw new IOException(sm.getString("ssiServletExternalResolver.noContext", new Object[]{normalized}));
        }
        if (!this.isRootContext(normContext)) {
            String noContext = this.getPathWithoutContext(normContext.getContextPath(), normalized);
            return new ServletContextAndPath(normContext, noContext);
        }
        return new ServletContextAndPath(normContext, normalized);
    }

    protected boolean isRootContext(ServletContext servletContext) {
        return servletContext == servletContext.getContext("/");
    }

    protected ServletContextAndPath getServletContextAndPath(String originalPath, boolean virtual) throws IOException {
        ServletContextAndPath csAndP = null;
        if (this.debug > 0) {
            this.log("SSIServletExternalResolver.getServletContextAndPath( " + originalPath + ", " + virtual + ")", null);
        }
        csAndP = virtual ? this.getServletContextAndPathFromVirtualPath(originalPath) : this.getServletContextAndPathFromNonVirtualPath(originalPath);
        return csAndP;
    }

    protected URLConnection getURLConnection(String originalPath, boolean virtual) throws IOException {
        String path;
        ServletContextAndPath csAndP = this.getServletContextAndPath(originalPath, virtual);
        ServletContext context = csAndP.getServletContext();
        URL url = context.getResource(path = csAndP.getPath());
        if (url == null) {
            throw new IOException(sm.getString("ssiServletExternalResolver.noResource", new Object[]{path}));
        }
        URLConnection urlConnection = url.openConnection();
        return urlConnection;
    }

    @Override
    public long getFileLastModified(String path, boolean virtual) throws IOException {
        long lastModified = 0L;
        try {
            URLConnection urlConnection = this.getURLConnection(path, virtual);
            lastModified = urlConnection.getLastModified();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return lastModified;
    }

    @Override
    public long getFileSize(String path, boolean virtual) throws IOException {
        long fileSize = -1L;
        try {
            URLConnection urlConnection = this.getURLConnection(path, virtual);
            fileSize = urlConnection.getContentLengthLong();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return fileSize;
    }

    @Override
    public String getFileText(String originalPath, boolean virtual) throws IOException {
        try {
            ServletContextAndPath csAndP = this.getServletContextAndPath(originalPath, virtual);
            ServletContext context = csAndP.getServletContext();
            String path = csAndP.getPath();
            RequestDispatcher rd = context.getRequestDispatcher(path);
            if (rd == null) {
                throw new IOException(sm.getString("ssiServletExternalResolver.requestDispatcherError", new Object[]{path}));
            }
            ByteArrayServletOutputStream basos = new ByteArrayServletOutputStream();
            ResponseIncludeWrapper responseIncludeWrapper = new ResponseIncludeWrapper(this.res, basos);
            rd.include((ServletRequest)this.req, (ServletResponse)responseIncludeWrapper);
            responseIncludeWrapper.flushOutputStreamOrWriter();
            byte[] bytes = basos.toByteArray();
            String retVal = this.inputEncoding == null ? new String(bytes) : new String(bytes, B2CConverter.getCharset((String)this.inputEncoding));
            if (retVal.equals("") && !this.req.getMethod().equalsIgnoreCase("HEAD")) {
                throw new IOException(sm.getString("ssiServletExternalResolver.noFile", new Object[]{path}));
            }
            return retVal;
        }
        catch (ServletException e) {
            throw new IOException(sm.getString("ssiServletExternalResolver.noIncludeFile", new Object[]{originalPath}), e);
        }
    }

    protected static class ServletContextAndPath {
        protected final ServletContext servletContext;
        protected final String path;

        public ServletContextAndPath(ServletContext servletContext, String path) {
            this.servletContext = servletContext;
            this.path = path;
        }

        public ServletContext getServletContext() {
            return this.servletContext;
        }

        public String getPath() {
            return this.path;
        }
    }
}

