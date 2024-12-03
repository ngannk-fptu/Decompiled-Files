/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.config.util.BootstrapUtils
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.tuple.Pair
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.servlet.simpledisplay;

import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.servlet.ServletManager;
import com.atlassian.confluence.servlet.SpringManagedServlet;
import com.atlassian.confluence.servlet.simpledisplay.ConvertedPath;
import com.atlassian.confluence.servlet.simpledisplay.PathConversionAction;
import com.atlassian.confluence.servlet.simpledisplay.PathConverter;
import com.atlassian.confluence.servlet.simpledisplay.PathConverterManager;
import com.atlassian.confluence.util.HtmlUtil;
import java.io.IOException;
import java.net.SocketException;
import java.text.ParseException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleDisplayServlet
implements ServletManager {
    private static final Logger log = LoggerFactory.getLogger(SimpleDisplayServlet.class);
    protected static final String HOMEPAGE_PATH = "/homepage.action";
    private static final String PAGE_NOT_FOUND_MESSAGE = "Page not found: ";
    private PathConverterManager pathConverterManager;

    @Override
    public void servletDestroyed(SpringManagedServlet springManagedServlet) {
    }

    @Override
    public void servletInitialised(SpringManagedServlet springManagedServlet, ServletConfig servletConfig) throws ServletException {
    }

    @Override
    public void service(SpringManagedServlet springManagedServlet, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        if (!this.getBootstrapManager().isSetupComplete()) {
            httpServletResponse.sendError(503);
            return;
        }
        String pathInfo = HtmlUtil.urlDecode(httpServletRequest.getPathInfo());
        try {
            Pair<PathConversionAction, String> pathToForward = this.parsePath(pathInfo, httpServletRequest.getQueryString());
            log.debug("Forwarding the request to '{}'", pathToForward);
            if (pathToForward.getLeft() == PathConversionAction.FORWARD) {
                httpServletRequest.getRequestDispatcher((String)pathToForward.getRight()).forward((ServletRequest)httpServletRequest, (ServletResponse)httpServletResponse);
            } else {
                httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + (String)pathToForward.getRight());
            }
        }
        catch (SocketException e) {
            log.debug("Client closed socket", (Throwable)e);
        }
        catch (ParseException e) {
            log.debug("Failed to parse path '{}'", (Object)pathInfo, (Object)e);
            httpServletResponse.sendError(404, PAGE_NOT_FOUND_MESSAGE + HtmlUtil.htmlEncode(pathInfo));
        }
        catch (Exception e) {
            log.warn("Error while parsing path '{}'", (Object)pathInfo, (Object)e);
            httpServletResponse.sendError(404, PAGE_NOT_FOUND_MESSAGE + HtmlUtil.htmlEncode(pathInfo));
        }
    }

    private Pair<PathConversionAction, String> parsePath(String pathInfo, String queryString) throws ParseException {
        if (StringUtils.isBlank((CharSequence)(pathInfo = this.sanitizePathString(pathInfo)))) {
            return Pair.of((Object)((Object)PathConversionAction.FORWARD), (Object)HOMEPAGE_PATH);
        }
        for (PathConverter pathConverter : this.getPathConverterManager().getPathConverters()) {
            ConvertedPath convertedPath;
            String result;
            if (!pathConverter.handles(pathInfo, queryString) || !StringUtils.isNotBlank((CharSequence)(result = this.sanitizePathString((convertedPath = pathConverter.getPath(pathInfo, queryString)).getPath())))) continue;
            return Pair.of((Object)((Object)convertedPath.getAction()), (Object)("/" + result));
        }
        throw new ParseException("Simple path '" + pathInfo + "' could not be translated", 0);
    }

    private String sanitizePathString(String path) throws ParseException {
        if (path.contains("../") || path.contains("..\\") || path.endsWith("/..") || path.endsWith("\\..")) {
            throw new ParseException("Invalid path: " + path, 0);
        }
        return SimpleDisplayServlet.stripSlashes(path);
    }

    public static String stripSlashes(String path) {
        if (StringUtils.isBlank((CharSequence)path)) {
            return "";
        }
        String result = path;
        while (result.startsWith("/") || result.startsWith("\\")) {
            result = result.substring(1);
        }
        while (result.endsWith("/") || result.endsWith("\\")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public PathConverterManager getPathConverterManager() {
        return this.pathConverterManager;
    }

    public void setPathConverterManager(PathConverterManager pathConverterManager) {
        this.pathConverterManager = pathConverterManager;
    }

    protected AtlassianBootstrapManager getBootstrapManager() {
        return BootstrapUtils.getBootstrapManager();
    }
}

