/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.compat.JrePlatform
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.servlets;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.catalina.util.IOTools;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.compat.JrePlatform;
import org.apache.tomcat.util.res.StringManager;

public final class CGIServlet
extends HttpServlet {
    private static final Log log = LogFactory.getLog(CGIServlet.class);
    private static final StringManager sm = StringManager.getManager(CGIServlet.class);
    private static final long serialVersionUID = 1L;
    private static final Set<String> DEFAULT_SUPER_METHODS = new HashSet<String>();
    private static final Pattern DEFAULT_CMD_LINE_ARGUMENTS_DECODED_PATTERN;
    private static final String ALLOW_ANY_PATTERN = ".*";
    private String cgiPathPrefix = null;
    private String cgiExecutable = "perl";
    private List<String> cgiExecutableArgs = null;
    private String parameterEncoding = System.getProperty("file.encoding", "UTF-8");
    private Set<String> cgiMethods = new HashSet<String>();
    private boolean cgiMethodsAll = false;
    private long stderrTimeout = 2000L;
    private Pattern envHttpHeadersPattern = Pattern.compile("ACCEPT[-0-9A-Z]*|CACHE-CONTROL|COOKIE|HOST|IF-[-0-9A-Z]*|REFERER|USER-AGENT");
    private static final Object expandFileLock;
    private final Hashtable<String, String> shellEnv = new Hashtable();
    private boolean enableCmdLineArguments = false;
    private Pattern cmdLineArgumentsEncodedPattern = Pattern.compile("[\\w\\Q%;/?:@&,$-.!~*'()\\E]+");
    private Pattern cmdLineArgumentsDecodedPattern = DEFAULT_CMD_LINE_ARGUMENTS_DECODED_PATTERN;

    public void init(ServletConfig config) throws ServletException {
        String value;
        super.init(config);
        this.cgiPathPrefix = this.getServletConfig().getInitParameter("cgiPathPrefix");
        boolean passShellEnvironment = Boolean.parseBoolean(this.getServletConfig().getInitParameter("passShellEnvironment"));
        if (passShellEnvironment) {
            this.shellEnv.putAll(System.getenv());
        }
        Enumeration e = config.getInitParameterNames();
        while (e.hasMoreElements()) {
            String initParamName = (String)e.nextElement();
            if (!initParamName.startsWith("environment-variable-")) continue;
            if (initParamName.length() == 21) {
                throw new ServletException(sm.getString("cgiServlet.emptyEnvVarName"));
            }
            this.shellEnv.put(initParamName.substring(21), config.getInitParameter(initParamName));
        }
        if (this.getServletConfig().getInitParameter("executable") != null) {
            this.cgiExecutable = this.getServletConfig().getInitParameter("executable");
        }
        if (this.getServletConfig().getInitParameter("executable-arg-1") != null) {
            String arg;
            ArrayList<String> args = new ArrayList<String>();
            int i = 1;
            while ((arg = this.getServletConfig().getInitParameter("executable-arg-" + i)) != null) {
                args.add(arg);
                ++i;
            }
            this.cgiExecutableArgs = args;
        }
        if (this.getServletConfig().getInitParameter("parameterEncoding") != null) {
            this.parameterEncoding = this.getServletConfig().getInitParameter("parameterEncoding");
        }
        if (this.getServletConfig().getInitParameter("stderrTimeout") != null) {
            this.stderrTimeout = Long.parseLong(this.getServletConfig().getInitParameter("stderrTimeout"));
        }
        if (this.getServletConfig().getInitParameter("envHttpHeaders") != null) {
            this.envHttpHeadersPattern = Pattern.compile(this.getServletConfig().getInitParameter("envHttpHeaders"));
        }
        if (this.getServletConfig().getInitParameter("enableCmdLineArguments") != null) {
            this.enableCmdLineArguments = Boolean.parseBoolean(config.getInitParameter("enableCmdLineArguments"));
        }
        if (this.getServletConfig().getInitParameter("cgiMethods") != null) {
            String paramValue = this.getServletConfig().getInitParameter("cgiMethods");
            if ("*".equals(paramValue = paramValue.trim())) {
                this.cgiMethodsAll = true;
            } else {
                String[] methods;
                for (String method : methods = paramValue.split(",")) {
                    String trimmedMethod = method.trim();
                    this.cgiMethods.add(trimmedMethod);
                }
            }
        } else {
            this.cgiMethods.add("GET");
            this.cgiMethods.add("POST");
        }
        if (this.getServletConfig().getInitParameter("cmdLineArgumentsEncoded") != null) {
            this.cmdLineArgumentsEncodedPattern = Pattern.compile(this.getServletConfig().getInitParameter("cmdLineArgumentsEncoded"));
        }
        if (ALLOW_ANY_PATTERN.equals(value = this.getServletConfig().getInitParameter("cmdLineArgumentsDecoded"))) {
            this.cmdLineArgumentsDecodedPattern = null;
        } else if (value != null) {
            this.cmdLineArgumentsDecodedPattern = Pattern.compile(value);
        }
    }

    private void printServletEnvironment(HttpServletRequest req) throws IOException {
        log.trace((Object)"ServletRequest Properties");
        Enumeration attrs = req.getAttributeNames();
        while (attrs.hasMoreElements()) {
            String attr = (String)attrs.nextElement();
            log.trace((Object)("Request Attribute: " + attr + ": [ " + req.getAttribute(attr) + "]"));
        }
        log.trace((Object)("Character Encoding: [" + req.getCharacterEncoding() + "]"));
        log.trace((Object)("Content Length: [" + req.getContentLengthLong() + "]"));
        log.trace((Object)("Content Type: [" + req.getContentType() + "]"));
        Enumeration locales = req.getLocales();
        while (locales.hasMoreElements()) {
            Locale locale = (Locale)locales.nextElement();
            log.trace((Object)("Locale: [" + locale + "]"));
        }
        Enumeration params = req.getParameterNames();
        while (params.hasMoreElements()) {
            String param = (String)params.nextElement();
            for (String string : req.getParameterValues(param)) {
                log.trace((Object)("Request Parameter: " + param + ":  [" + string + "]"));
            }
        }
        log.trace((Object)("Protocol: [" + req.getProtocol() + "]"));
        log.trace((Object)("Remote Address: [" + req.getRemoteAddr() + "]"));
        log.trace((Object)("Remote Host: [" + req.getRemoteHost() + "]"));
        log.trace((Object)("Scheme: [" + req.getScheme() + "]"));
        log.trace((Object)("Secure: [" + req.isSecure() + "]"));
        log.trace((Object)("Server Name: [" + req.getServerName() + "]"));
        log.trace((Object)("Server Port: [" + req.getServerPort() + "]"));
        log.trace((Object)"HttpServletRequest Properties");
        log.trace((Object)("Auth Type: [" + req.getAuthType() + "]"));
        log.trace((Object)("Context Path: [" + req.getContextPath() + "]"));
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (String string : cookies) {
                log.trace((Object)("Cookie: " + string.getName() + ": [" + string.getValue() + "]"));
            }
        }
        Enumeration headers = req.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header = (String)headers.nextElement();
            log.trace((Object)("HTTP Header: " + header + ": [" + req.getHeader(header) + "]"));
        }
        log.trace((Object)("Method: [" + req.getMethod() + "]"));
        log.trace((Object)("Path Info: [" + req.getPathInfo() + "]"));
        log.trace((Object)("Path Translated: [" + req.getPathTranslated() + "]"));
        log.trace((Object)("Query String: [" + req.getQueryString() + "]"));
        log.trace((Object)("Remote User: [" + req.getRemoteUser() + "]"));
        log.trace((Object)("Requested Session ID: [" + req.getRequestedSessionId() + "]"));
        log.trace((Object)("Requested Session ID From Cookie: [" + req.isRequestedSessionIdFromCookie() + "]"));
        log.trace((Object)("Requested Session ID From URL: [" + req.isRequestedSessionIdFromURL() + "]"));
        log.trace((Object)("Requested Session ID Valid: [" + req.isRequestedSessionIdValid() + "]"));
        log.trace((Object)("Request URI: [" + req.getRequestURI() + "]"));
        log.trace((Object)("Servlet Path: [" + req.getServletPath() + "]"));
        log.trace((Object)("User Principal: [" + req.getUserPrincipal() + "]"));
        HttpSession session = req.getSession(false);
        if (session != null) {
            log.trace((Object)"HttpSession Properties");
            log.trace((Object)("ID: [" + session.getId() + "]"));
            log.trace((Object)("Creation Time: [" + new Date(session.getCreationTime()) + "]"));
            log.trace((Object)("Last Accessed Time: [" + new Date(session.getLastAccessedTime()) + "]"));
            log.trace((Object)("Max Inactive Interval: [" + session.getMaxInactiveInterval() + "]"));
            attrs = session.getAttributeNames();
            while (attrs.hasMoreElements()) {
                String attr = (String)attrs.nextElement();
                log.trace((Object)("Session Attribute: " + attr + ": [" + session.getAttribute(attr) + "]"));
            }
        }
        log.trace((Object)"ServletConfig Properties");
        log.trace((Object)("Servlet Name: [" + this.getServletConfig().getServletName() + "]"));
        params = this.getServletConfig().getInitParameterNames();
        while (params.hasMoreElements()) {
            String param = (String)params.nextElement();
            String string = this.getServletConfig().getInitParameter(param);
            log.trace((Object)("Servlet Init Param: " + param + ": [" + string + "]"));
        }
        log.trace((Object)"ServletContext Properties");
        log.trace((Object)("Major Version: [" + this.getServletContext().getMajorVersion() + "]"));
        log.trace((Object)("Minor Version: [" + this.getServletContext().getMinorVersion() + "]"));
        log.trace((Object)("Real Path for '/': [" + this.getServletContext().getRealPath("/") + "]"));
        log.trace((Object)("Server Info: [" + this.getServletContext().getServerInfo() + "]"));
        log.trace((Object)"ServletContext Initialization Parameters");
        params = this.getServletContext().getInitParameterNames();
        while (params.hasMoreElements()) {
            String param = (String)params.nextElement();
            String string = this.getServletContext().getInitParameter(param);
            log.trace((Object)("Servlet Context Init Param: " + param + ": [" + string + "]"));
        }
        log.trace((Object)"ServletContext Attributes");
        attrs = this.getServletContext().getAttributeNames();
        while (attrs.hasMoreElements()) {
            String attr = (String)attrs.nextElement();
            log.trace((Object)("Servlet Context Attribute: " + attr + ": [" + this.getServletContext().getAttribute(attr) + "]"));
        }
    }

    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String method = req.getMethod();
        if (this.cgiMethodsAll || this.cgiMethods.contains(method)) {
            this.doGet(req, res);
        } else if (DEFAULT_SUPER_METHODS.contains(method)) {
            super.service(req, res);
        } else {
            res.sendError(405);
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        CGIEnvironment cgiEnv = new CGIEnvironment(req, this.getServletContext());
        if (cgiEnv.isValid()) {
            CGIRunner cgi = new CGIRunner(cgiEnv.getCommand(), cgiEnv.getEnvironment(), cgiEnv.getWorkingDirectory(), cgiEnv.getParameters());
            if ("POST".equals(req.getMethod())) {
                cgi.setInput((InputStream)req.getInputStream());
            }
            cgi.setResponse(res);
            cgi.run();
        } else {
            res.sendError(404);
        }
        if (log.isTraceEnabled()) {
            String[] cgiEnvLines;
            for (String cgiEnvLine : cgiEnvLines = cgiEnv.toString().split(System.lineSeparator())) {
                log.trace((Object)cgiEnvLine);
            }
            this.printServletEnvironment(req);
        }
    }

    protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HashSet<String> allowedMethods = new HashSet<String>();
        allowedMethods.addAll(this.cgiMethods);
        allowedMethods.addAll(DEFAULT_SUPER_METHODS);
        StringBuilder headerValue = new StringBuilder();
        for (String method : allowedMethods) {
            headerValue.append(method);
            headerValue.append(',');
        }
        headerValue.deleteCharAt(headerValue.length() - 1);
        res.setHeader("allow", headerValue.toString());
    }

    private boolean setStatus(HttpServletResponse response, int status) throws IOException {
        if (status >= 400) {
            response.sendError(status);
            return true;
        }
        response.setStatus(status);
        return false;
    }

    static {
        DEFAULT_SUPER_METHODS.add("HEAD");
        DEFAULT_SUPER_METHODS.add("OPTIONS");
        DEFAULT_SUPER_METHODS.add("TRACE");
        DEFAULT_CMD_LINE_ARGUMENTS_DECODED_PATTERN = JrePlatform.IS_WINDOWS ? Pattern.compile("[\\w\\Q-.\\/:\\E]+") : null;
        expandFileLock = new Object();
    }

    protected class CGIEnvironment {
        private ServletContext context = null;
        private String contextPath = null;
        private String servletPath = null;
        private String pathInfo = null;
        private String webAppRootDir = null;
        private File tmpDir = null;
        private Hashtable<String, String> env = null;
        private String command = null;
        private final File workingDirectory;
        private final ArrayList<String> cmdLineParameters = new ArrayList();
        private final boolean valid;

        protected CGIEnvironment(HttpServletRequest req, ServletContext context) throws IOException {
            this.setupFromContext(context);
            boolean valid = this.setupFromRequest(req);
            if (valid) {
                valid = this.setCGIEnvironment(req);
            }
            this.workingDirectory = valid ? new File(this.command.substring(0, this.command.lastIndexOf(File.separator))) : null;
            this.valid = valid;
        }

        protected void setupFromContext(ServletContext context) {
            this.context = context;
            this.webAppRootDir = context.getRealPath("/");
            this.tmpDir = (File)context.getAttribute("javax.servlet.context.tempdir");
        }

        protected boolean setupFromRequest(HttpServletRequest req) throws UnsupportedEncodingException {
            String qs;
            boolean isIncluded = false;
            if (req.getAttribute("javax.servlet.include.request_uri") != null) {
                isIncluded = true;
            }
            if (isIncluded) {
                this.contextPath = (String)req.getAttribute("javax.servlet.include.context_path");
                this.servletPath = (String)req.getAttribute("javax.servlet.include.servlet_path");
                this.pathInfo = (String)req.getAttribute("javax.servlet.include.path_info");
            } else {
                this.contextPath = req.getContextPath();
                this.servletPath = req.getServletPath();
                this.pathInfo = req.getPathInfo();
            }
            if (this.pathInfo == null) {
                this.pathInfo = this.servletPath;
            }
            if (CGIServlet.this.enableCmdLineArguments && (req.getMethod().equals("GET") || req.getMethod().equals("POST") || req.getMethod().equals("HEAD")) && (qs = isIncluded ? (String)req.getAttribute("javax.servlet.include.query_string") : req.getQueryString()) != null && qs.indexOf(61) == -1) {
                StringTokenizer qsTokens = new StringTokenizer(qs, "+");
                while (qsTokens.hasMoreTokens()) {
                    String encodedArgument = qsTokens.nextToken();
                    if (!CGIServlet.this.cmdLineArgumentsEncodedPattern.matcher(encodedArgument).matches()) {
                        if (log.isDebugEnabled()) {
                            log.debug((Object)sm.getString("cgiServlet.invalidArgumentEncoded", new Object[]{encodedArgument, CGIServlet.this.cmdLineArgumentsEncodedPattern.toString()}));
                        }
                        return false;
                    }
                    String decodedArgument = URLDecoder.decode(encodedArgument, CGIServlet.this.parameterEncoding);
                    if (CGIServlet.this.cmdLineArgumentsDecodedPattern != null && !CGIServlet.this.cmdLineArgumentsDecodedPattern.matcher(decodedArgument).matches()) {
                        if (log.isDebugEnabled()) {
                            log.debug((Object)sm.getString("cgiServlet.invalidArgumentDecoded", new Object[]{decodedArgument, CGIServlet.this.cmdLineArgumentsDecodedPattern.toString()}));
                        }
                        return false;
                    }
                    this.cmdLineParameters.add(decodedArgument);
                }
            }
            return true;
        }

        protected String[] findCGI(String pathInfo, String webAppRootDir, String contextPath, String servletPath, String cgiPathPrefix) {
            String path = null;
            String name = null;
            String scriptname = null;
            if (webAppRootDir.lastIndexOf(File.separator) == webAppRootDir.length() - 1) {
                webAppRootDir = webAppRootDir.substring(0, webAppRootDir.length() - 1);
            }
            if (cgiPathPrefix != null) {
                webAppRootDir = webAppRootDir + File.separator + cgiPathPrefix;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("cgiServlet.find.path", new Object[]{pathInfo, webAppRootDir}));
            }
            File currentLocation = new File(webAppRootDir);
            StringTokenizer dirWalker = new StringTokenizer(pathInfo, "/");
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("cgiServlet.find.location", new Object[]{currentLocation.getAbsolutePath()}));
            }
            StringBuilder cginameBuilder = new StringBuilder();
            while (!currentLocation.isFile() && dirWalker.hasMoreElements()) {
                String nextElement = (String)dirWalker.nextElement();
                currentLocation = new File(currentLocation, nextElement);
                cginameBuilder.append('/').append(nextElement);
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)sm.getString("cgiServlet.find.location", new Object[]{currentLocation.getAbsolutePath()}));
            }
            String cginame = cginameBuilder.toString();
            if (!currentLocation.isFile()) {
                return new String[]{null, null, null, null};
            }
            path = currentLocation.getAbsolutePath();
            name = currentLocation.getName();
            scriptname = servletPath.startsWith(cginame) ? contextPath + cginame : contextPath + servletPath + cginame;
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("cgiServlet.find.found", new Object[]{name, path, scriptname, cginame}));
            }
            return new String[]{path, scriptname, cginame, name};
        }

        protected boolean setCGIEnvironment(HttpServletRequest req) throws IOException {
            Hashtable<String, String> envp = new Hashtable<String, String>(CGIServlet.this.shellEnv);
            String sPathInfoOrig = null;
            String sPathInfoCGI = null;
            String sPathTranslatedCGI = null;
            String sCGIFullPath = null;
            String sCGIScriptName = null;
            String sCGIFullName = null;
            String sCGIName = null;
            sPathInfoOrig = this.pathInfo;
            String string = sPathInfoOrig = sPathInfoOrig == null ? "" : sPathInfoOrig;
            if (this.webAppRootDir == null) {
                this.webAppRootDir = this.tmpDir.toString();
                this.expandCGIScript();
            }
            String[] sCGINames = this.findCGI(sPathInfoOrig, this.webAppRootDir, this.contextPath, this.servletPath, CGIServlet.this.cgiPathPrefix);
            sCGIFullPath = sCGINames[0];
            sCGIScriptName = sCGINames[1];
            sCGIFullName = sCGINames[2];
            sCGIName = sCGINames[3];
            if (sCGIFullPath == null || sCGIScriptName == null || sCGIFullName == null || sCGIName == null) {
                return false;
            }
            envp.put("SERVER_SOFTWARE", "TOMCAT");
            envp.put("SERVER_NAME", this.nullsToBlanks(req.getServerName()));
            envp.put("GATEWAY_INTERFACE", "CGI/1.1");
            envp.put("SERVER_PROTOCOL", this.nullsToBlanks(req.getProtocol()));
            int port = req.getServerPort();
            Integer iPort = port == 0 ? Integer.valueOf(-1) : Integer.valueOf(port);
            envp.put("SERVER_PORT", iPort.toString());
            envp.put("REQUEST_METHOD", this.nullsToBlanks(req.getMethod()));
            envp.put("REQUEST_URI", this.nullsToBlanks(req.getRequestURI()));
            sPathInfoCGI = this.pathInfo == null || this.pathInfo.substring(sCGIFullName.length()).length() <= 0 ? "" : this.pathInfo.substring(sCGIFullName.length());
            envp.put("PATH_INFO", sPathInfoCGI);
            if (!sPathInfoCGI.isEmpty()) {
                sPathTranslatedCGI = this.context.getRealPath(sPathInfoCGI);
            }
            if (sPathTranslatedCGI != null && !"".equals(sPathTranslatedCGI)) {
                envp.put("PATH_TRANSLATED", this.nullsToBlanks(sPathTranslatedCGI));
            }
            envp.put("SCRIPT_NAME", this.nullsToBlanks(sCGIScriptName));
            envp.put("QUERY_STRING", this.nullsToBlanks(req.getQueryString()));
            envp.put("REMOTE_HOST", this.nullsToBlanks(req.getRemoteHost()));
            envp.put("REMOTE_ADDR", this.nullsToBlanks(req.getRemoteAddr()));
            envp.put("AUTH_TYPE", this.nullsToBlanks(req.getAuthType()));
            envp.put("REMOTE_USER", this.nullsToBlanks(req.getRemoteUser()));
            envp.put("REMOTE_IDENT", "");
            envp.put("CONTENT_TYPE", this.nullsToBlanks(req.getContentType()));
            long contentLength = req.getContentLengthLong();
            String sContentLength = contentLength <= 0L ? "" : Long.toString(contentLength);
            envp.put("CONTENT_LENGTH", sContentLength);
            Enumeration headers = req.getHeaderNames();
            String header = null;
            while (headers.hasMoreElements()) {
                header = null;
                header = ((String)headers.nextElement()).toUpperCase(Locale.ENGLISH);
                if (!CGIServlet.this.envHttpHeadersPattern.matcher(header).matches()) continue;
                envp.put("HTTP_" + header.replace('-', '_'), req.getHeader(header));
            }
            File fCGIFullPath = new File(sCGIFullPath);
            this.command = fCGIFullPath.getCanonicalPath();
            envp.put("X_TOMCAT_SCRIPT_PATH", this.command);
            envp.put("SCRIPT_FILENAME", this.command);
            this.env = envp;
            return true;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Loose catch block
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         * Converted monitor instructions to comments
         * Lifted jumps to return sites
         */
        protected void expandCGIScript() {
            InputStream is;
            StringBuilder srcPath;
            block32: {
                File f;
                StringBuilder destPath;
                block31: {
                    block30: {
                        block29: {
                            block28: {
                                srcPath = new StringBuilder();
                                destPath = new StringBuilder();
                                is = null;
                                if (CGIServlet.this.cgiPathPrefix == null) {
                                    srcPath.append(this.pathInfo);
                                    is = this.context.getResourceAsStream(srcPath.toString());
                                    destPath.append(this.tmpDir);
                                    destPath.append(this.pathInfo);
                                } else {
                                    srcPath.append(CGIServlet.this.cgiPathPrefix);
                                    StringTokenizer pathWalker = new StringTokenizer(this.pathInfo, "/");
                                    while (pathWalker.hasMoreElements() && is == null) {
                                        srcPath.append('/');
                                        srcPath.append(pathWalker.nextElement());
                                        is = this.context.getResourceAsStream(srcPath.toString());
                                    }
                                    destPath.append(this.tmpDir);
                                    destPath.append('/');
                                    destPath.append((CharSequence)srcPath);
                                }
                                if (is == null) {
                                    log.warn((Object)sm.getString("cgiServlet.expandNotFound", new Object[]{srcPath}));
                                    return;
                                }
                                f = new File(destPath.toString());
                                if (!f.exists()) break block28;
                                try {
                                    is.close();
                                    return;
                                }
                                catch (IOException e) {
                                    log.warn((Object)sm.getString("cgiServlet.expandCloseFail", new Object[]{srcPath}), (Throwable)e);
                                }
                                return;
                            }
                            File dir = f.getParentFile();
                            if (dir.mkdirs() || dir.isDirectory()) break block29;
                            log.warn((Object)sm.getString("cgiServlet.expandCreateDirFail", new Object[]{dir.getAbsolutePath()}));
                            try {
                                is.close();
                                return;
                            }
                            catch (IOException e) {
                                log.warn((Object)sm.getString("cgiServlet.expandCloseFail", new Object[]{srcPath}), (Throwable)e);
                            }
                            return;
                        }
                        Object e = expandFileLock;
                        // MONITORENTER : e
                        if (!f.exists()) break block30;
                        // MONITOREXIT : e
                        try {
                            is.close();
                            return;
                        }
                        catch (IOException e2) {
                            log.warn((Object)sm.getString("cgiServlet.expandCloseFail", new Object[]{srcPath}), (Throwable)e2);
                        }
                        return;
                    }
                    if (f.createNewFile()) break block31;
                    // MONITOREXIT : e
                    {
                        catch (IOException ioe) {
                            log.warn((Object)sm.getString("cgiServlet.expandFail", new Object[]{srcPath, destPath}), (Throwable)ioe);
                            if (!f.exists() || f.delete()) break block32;
                            log.warn((Object)sm.getString("cgiServlet.expandDeleteFail", new Object[]{f.getAbsolutePath()}));
                        }
                    }
                    try {
                        is.close();
                        return;
                    }
                    catch (IOException e) {
                        log.warn((Object)sm.getString("cgiServlet.expandCloseFail", new Object[]{srcPath}), (Throwable)e);
                    }
                    return;
                }
                try {
                    Files.copy(is, f.toPath(), new CopyOption[0]);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)sm.getString("cgiServlet.expandOk", new Object[]{srcPath, destPath}));
                    }
                    // MONITOREXIT : e
                }
                catch (Throwable throwable) {
                    try {
                        is.close();
                        throw throwable;
                    }
                    catch (IOException e) {
                        log.warn((Object)sm.getString("cgiServlet.expandCloseFail", new Object[]{srcPath}), (Throwable)e);
                    }
                    throw throwable;
                }
            }
            try {
                is.close();
                return;
            }
            catch (IOException e) {
                log.warn((Object)sm.getString("cgiServlet.expandCloseFail", new Object[]{srcPath}), (Throwable)e);
                return;
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("CGIEnvironment Info:");
            sb.append(System.lineSeparator());
            if (this.isValid()) {
                sb.append("Validity: [true]");
                sb.append(System.lineSeparator());
                sb.append("Environment values:");
                sb.append(System.lineSeparator());
                for (Map.Entry<String, String> entry : this.env.entrySet()) {
                    sb.append("  ");
                    sb.append(entry.getKey());
                    sb.append(": [");
                    sb.append(this.blanksToString(entry.getValue(), "will be set to blank"));
                    sb.append(']');
                    sb.append(System.lineSeparator());
                }
                sb.append("Derived Command :[");
                sb.append(this.nullsToBlanks(this.command));
                sb.append(']');
                sb.append(System.lineSeparator());
                sb.append("Working Directory: [");
                if (this.workingDirectory != null) {
                    sb.append(this.workingDirectory.toString());
                }
                sb.append(']');
                sb.append(System.lineSeparator());
                sb.append("Command Line Params:");
                sb.append(System.lineSeparator());
                for (String param : this.cmdLineParameters) {
                    sb.append("  [");
                    sb.append(param);
                    sb.append(']');
                    sb.append(System.lineSeparator());
                }
            } else {
                sb.append("Validity: [false]");
                sb.append(System.lineSeparator());
                sb.append("CGI script not found or not specified.");
                sb.append(System.lineSeparator());
                sb.append("Check the HttpServletRequest pathInfo property to see if it is what ");
                sb.append(System.lineSeparator());
                sb.append("you meant it to be. You must specify an existent and executable file ");
                sb.append(System.lineSeparator());
                sb.append("as part of the path-info.");
                sb.append(System.lineSeparator());
            }
            return sb.toString();
        }

        protected String getCommand() {
            return this.command;
        }

        protected File getWorkingDirectory() {
            return this.workingDirectory;
        }

        protected Hashtable<String, String> getEnvironment() {
            return this.env;
        }

        protected ArrayList<String> getParameters() {
            return this.cmdLineParameters;
        }

        protected boolean isValid() {
            return this.valid;
        }

        protected String nullsToBlanks(String s) {
            return this.nullsToString(s, "");
        }

        protected String nullsToString(String couldBeNull, String subForNulls) {
            return couldBeNull == null ? subForNulls : couldBeNull;
        }

        protected String blanksToString(String couldBeBlank, String subForBlanks) {
            return couldBeBlank == null || couldBeBlank.isEmpty() ? subForBlanks : couldBeBlank;
        }
    }

    protected class CGIRunner {
        private final String command;
        private final Hashtable<String, String> env;
        private final File wd;
        private final ArrayList<String> params;
        private InputStream stdin = null;
        private HttpServletResponse response = null;
        private boolean readyToRun = false;

        protected CGIRunner(String command, Hashtable<String, String> env, File wd, ArrayList<String> params) {
            this.command = command;
            this.env = env;
            this.wd = wd;
            this.params = params;
            this.updateReadyStatus();
        }

        protected void updateReadyStatus() {
            this.readyToRun = this.command != null && this.env != null && this.wd != null && this.params != null && this.response != null;
        }

        protected boolean isReady() {
            return this.readyToRun;
        }

        protected void setResponse(HttpServletResponse response) {
            this.response = response;
            this.updateReadyStatus();
        }

        protected void setInput(InputStream stdin) {
            this.stdin = stdin;
            this.updateReadyStatus();
        }

        protected String[] hashToStringArray(Hashtable<String, ?> h) throws NullPointerException {
            ArrayList<String> list = new ArrayList<String>(h.size());
            Enumeration<String> e = h.keys();
            while (e.hasMoreElements()) {
                String k = e.nextElement();
                list.add(k + "=" + h.get(k).toString());
            }
            return list.toArray(new String[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void run() throws IOException {
            if (!this.isReady()) {
                throw new IOException(this.getClass().getName() + ": not ready to run.");
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("envp: [" + this.env + "], command: [" + this.command + "]"));
            }
            if (this.command.contains(File.separator + "." + File.separator) || this.command.contains(File.separator + "..") || this.command.contains(".." + File.separator)) {
                throw new IOException(this.getClass().getName() + "Illegal Character in CGI command path ('.' or '..') detected.  Not running CGI [" + this.command + "].");
            }
            Runtime rt = null;
            BufferedReader cgiHeaderReader = null;
            InputStream cgiOutput = null;
            BufferedReader commandsStdErr = null;
            Thread errReaderThread = null;
            BufferedOutputStream commandsStdIn = null;
            Process proc = null;
            int bufRead = -1;
            ArrayList<String> cmdAndArgs = new ArrayList<String>();
            if (CGIServlet.this.cgiExecutable.length() != 0) {
                cmdAndArgs.add(CGIServlet.this.cgiExecutable);
            }
            if (CGIServlet.this.cgiExecutableArgs != null) {
                cmdAndArgs.addAll(CGIServlet.this.cgiExecutableArgs);
            }
            cmdAndArgs.add(this.command);
            cmdAndArgs.addAll(this.params);
            try {
                rt = Runtime.getRuntime();
                proc = rt.exec(cmdAndArgs.toArray(new String[0]), this.hashToStringArray(this.env), this.wd);
                String sContentLength = this.env.get("CONTENT_LENGTH");
                if (!"".equals(sContentLength)) {
                    commandsStdIn = new BufferedOutputStream(proc.getOutputStream());
                    IOTools.flow(this.stdin, commandsStdIn);
                    commandsStdIn.flush();
                    commandsStdIn.close();
                }
                boolean isRunning = true;
                BufferedReader stdErrRdr = commandsStdErr = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                errReaderThread = new Thread(() -> this.sendToLog(stdErrRdr));
                errReaderThread.start();
                HTTPHeaderInputStream cgiHeaderStream = new HTTPHeaderInputStream(proc.getInputStream());
                cgiHeaderReader = new BufferedReader(new InputStreamReader(cgiHeaderStream));
                boolean skipBody = false;
                while (isRunning) {
                    try {
                        String line = null;
                        while ((line = cgiHeaderReader.readLine()) != null && !line.isEmpty()) {
                            if (log.isTraceEnabled()) {
                                log.trace((Object)("addHeader(\"" + line + "\")"));
                            }
                            if (line.startsWith("HTTP")) {
                                skipBody = CGIServlet.this.setStatus(this.response, this.getSCFromHttpStatusLine(line));
                                continue;
                            }
                            if (line.indexOf(58) >= 0) {
                                String header = line.substring(0, line.indexOf(58)).trim();
                                String value = line.substring(line.indexOf(58) + 1).trim();
                                if (header.equalsIgnoreCase("status")) {
                                    skipBody = CGIServlet.this.setStatus(this.response, this.getSCFromCGIStatusHeader(value));
                                    continue;
                                }
                                this.response.addHeader(header, value);
                                continue;
                            }
                            log.info((Object)sm.getString("cgiServlet.runBadHeader", new Object[]{line}));
                        }
                        byte[] bBuf = new byte[2048];
                        ServletOutputStream out = this.response.getOutputStream();
                        cgiOutput = proc.getInputStream();
                        try {
                            while (!skipBody && (bufRead = cgiOutput.read(bBuf)) != -1) {
                                if (log.isTraceEnabled()) {
                                    log.trace((Object)("output " + bufRead + " bytes of data"));
                                }
                                out.write(bBuf, 0, bufRead);
                            }
                        }
                        finally {
                            if (bufRead != -1) {
                                while ((bufRead = cgiOutput.read(bBuf)) != -1) {
                                }
                            }
                        }
                        proc.exitValue();
                        isRunning = false;
                    }
                    catch (IllegalThreadStateException e) {
                        try {
                            Thread.sleep(500L);
                        }
                        catch (InterruptedException interruptedException) {}
                    }
                }
            }
            catch (IOException e) {
                log.warn((Object)sm.getString("cgiServlet.runFail"), (Throwable)e);
                throw e;
            }
            finally {
                if (cgiHeaderReader != null) {
                    try {
                        cgiHeaderReader.close();
                    }
                    catch (IOException ioe) {
                        log.warn((Object)sm.getString("cgiServlet.runHeaderReaderFail"), (Throwable)ioe);
                    }
                }
                if (cgiOutput != null) {
                    try {
                        cgiOutput.close();
                    }
                    catch (IOException ioe) {
                        log.warn((Object)sm.getString("cgiServlet.runOutputStreamFail"), (Throwable)ioe);
                    }
                }
                if (errReaderThread != null) {
                    try {
                        errReaderThread.join(CGIServlet.this.stderrTimeout);
                    }
                    catch (InterruptedException e) {
                        log.warn((Object)sm.getString("cgiServlet.runReaderInterrupt"));
                    }
                }
                if (proc != null) {
                    proc.destroy();
                    proc = null;
                }
            }
        }

        private int getSCFromHttpStatusLine(String line) {
            int statusCode;
            int statusStart = line.indexOf(32) + 1;
            if (statusStart < 1 || line.length() < statusStart + 3) {
                log.warn((Object)sm.getString("cgiServlet.runInvalidStatus", new Object[]{line}));
                return 500;
            }
            String status = line.substring(statusStart, statusStart + 3);
            try {
                statusCode = Integer.parseInt(status);
            }
            catch (NumberFormatException nfe) {
                log.warn((Object)sm.getString("cgiServlet.runInvalidStatus", new Object[]{status}));
                return 500;
            }
            return statusCode;
        }

        private int getSCFromCGIStatusHeader(String value) {
            int statusCode;
            if (value.length() < 3) {
                log.warn((Object)sm.getString("cgiServlet.runInvalidStatus", new Object[]{value}));
                return 500;
            }
            String status = value.substring(0, 3);
            try {
                statusCode = Integer.parseInt(status);
            }
            catch (NumberFormatException nfe) {
                log.warn((Object)sm.getString("cgiServlet.runInvalidStatus", new Object[]{status}));
                return 500;
            }
            return statusCode;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void sendToLog(BufferedReader rdr) {
            String line = null;
            int lineCount = 0;
            try {
                while ((line = rdr.readLine()) != null) {
                    log.warn((Object)sm.getString("cgiServlet.runStdErr", new Object[]{line}));
                    ++lineCount;
                }
            }
            catch (IOException e) {
                log.warn((Object)sm.getString("cgiServlet.runStdErrFail"), (Throwable)e);
            }
            finally {
                try {
                    rdr.close();
                }
                catch (IOException e) {
                    log.warn((Object)sm.getString("cgiServlet.runStdErrFail"), (Throwable)e);
                }
            }
            if (lineCount > 0) {
                log.warn((Object)sm.getString("cgiServlet.runStdErrCount", new Object[]{lineCount}));
            }
        }
    }

    protected static class HTTPHeaderInputStream
    extends InputStream {
        private static final int STATE_CHARACTER = 0;
        private static final int STATE_FIRST_CR = 1;
        private static final int STATE_FIRST_LF = 2;
        private static final int STATE_SECOND_CR = 3;
        private static final int STATE_HEADER_END = 4;
        private final InputStream input;
        private int state;

        HTTPHeaderInputStream(InputStream theInput) {
            this.input = theInput;
            this.state = 0;
        }

        @Override
        public int read() throws IOException {
            if (this.state == 4) {
                return -1;
            }
            int i = this.input.read();
            if (i == 10) {
                switch (this.state) {
                    case 0: {
                        this.state = 2;
                        break;
                    }
                    case 1: {
                        this.state = 2;
                        break;
                    }
                    case 2: 
                    case 3: {
                        this.state = 4;
                    }
                }
            } else if (i == 13) {
                switch (this.state) {
                    case 0: {
                        this.state = 1;
                        break;
                    }
                    case 1: {
                        this.state = 4;
                        break;
                    }
                    case 2: {
                        this.state = 3;
                    }
                }
            } else {
                this.state = 0;
            }
            return i;
        }
    }
}

