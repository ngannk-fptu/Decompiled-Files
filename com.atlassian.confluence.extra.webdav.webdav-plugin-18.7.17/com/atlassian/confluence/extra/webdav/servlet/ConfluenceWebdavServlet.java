/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.xml.SecureXmlParserFactory
 *  com.google.common.base.Preconditions
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.webdav.servlet;

import com.atlassian.security.xml.SecureXmlParserFactory;
import com.google.common.base.Preconditions;
import java.io.IOException;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavSessionProvider;
import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.WebdavRequestImpl;
import org.apache.jackrabbit.webdav.WebdavResponse;
import org.apache.jackrabbit.webdav.WebdavResponseImpl;
import org.apache.jackrabbit.webdav.server.AbstractWebdavServlet;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Component(value="webDavServlet")
@ParametersAreNonnullByDefault
public class ConfluenceWebdavServlet
extends AbstractWebdavServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfluenceWebdavServlet.class);
    private static final String DEFAULT_AUTH_HEADER = "Basic realm=\"Confluence WebDAV Server\"";
    private DavSessionProvider davSessionProvider;
    private DavResourceFactory resourceFactory;
    private DavLocatorFactory locatorFactory;

    @Autowired
    public ConfluenceWebdavServlet(DavSessionProvider davSessionProvider, @Qualifier(value="resourceFactory") DavResourceFactory resourceFactory, DavLocatorFactory locatorFactory) {
        this.davSessionProvider = (DavSessionProvider)Preconditions.checkNotNull((Object)davSessionProvider);
        this.resourceFactory = (DavResourceFactory)Preconditions.checkNotNull((Object)resourceFactory);
        this.locatorFactory = (DavLocatorFactory)Preconditions.checkNotNull((Object)locatorFactory);
        DocumentBuilderFactory documentBuilderFactory = SecureXmlParserFactory.newDocumentBuilderFactory();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setIgnoringComments(true);
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        documentBuilderFactory.setCoalescing(true);
        DomUtil.setBuilderFactory(documentBuilderFactory);
    }

    @Override
    public DavSessionProvider getDavSessionProvider() {
        return this.davSessionProvider;
    }

    @Override
    public void setDavSessionProvider(DavSessionProvider davSessionProvider) {
        this.davSessionProvider = (DavSessionProvider)Preconditions.checkNotNull((Object)davSessionProvider);
    }

    @Override
    public DavResourceFactory getResourceFactory() {
        return this.resourceFactory;
    }

    @Override
    public void setResourceFactory(DavResourceFactory resourceFactory) {
        this.resourceFactory = (DavResourceFactory)Preconditions.checkNotNull((Object)resourceFactory);
    }

    @Override
    public DavLocatorFactory getLocatorFactory() {
        return this.locatorFactory;
    }

    @Override
    public void setLocatorFactory(DavLocatorFactory locatorFactory) {
        this.locatorFactory = (DavLocatorFactory)Preconditions.checkNotNull((Object)locatorFactory);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        LOGGER.debug("{}, {} {}", new Object[]{request.getHeader("User-Agent"), method, uri});
        WebdavRequestImpl webdavRequest = new WebdavRequestImpl(request, this.getLocatorFactory());
        int methodCode = DavMethods.getMethodCode(request.getMethod());
        boolean noCache = DavMethods.isDeltaVMethod(webdavRequest) && 20 != methodCode && 19 != methodCode;
        WebdavResponseImpl webdavResponse = new WebdavResponseImpl(response, noCache);
        try {
            ServletActionContext.setRequest((HttpServletRequest)request);
            ServletActionContext.setResponse((HttpServletResponse)response);
            if (!this.getDavSessionProvider().attachSession(webdavRequest)) {
                throw new DavException(401, "Unable to authenticate.");
            }
            DavResource resource = this.getResourceFactory().createResource(webdavRequest.getRequestLocator(), webdavRequest, webdavResponse);
            if (!this.isPreconditionValid(webdavRequest, resource)) {
                webdavResponse.sendError(412);
                return;
            }
            if (!this.execute(webdavRequest, webdavResponse, methodCode, resource)) {
                super.service(request, response);
            }
        }
        catch (DavException e) {
            if (e.getErrorCode() == 401) {
                LOGGER.debug("{} {} unauthorized", (Object)method, (Object)uri);
                webdavResponse.setHeader("WWW-Authenticate", this.getAuthenticateHeaderValue());
                webdavResponse.sendError(e.getErrorCode(), e.getStatusPhrase());
            } else if (403 == e.getErrorCode()) {
                LOGGER.debug("{} {} denied: {}", new Object[]{method, uri, e.getMessage()});
                webdavResponse.sendError(e);
            } else {
                LOGGER.error("Unexpected error", (Throwable)e);
                webdavResponse.sendError(e);
            }
        }
        finally {
            this.getDavSessionProvider().releaseSession(webdavRequest);
            ServletActionContext.setRequest(null);
            ServletActionContext.setResponse(null);
        }
    }

    @Override
    protected void doPropFind(WebdavRequest request, WebdavResponse response, DavResource resource) throws IOException, DavException {
        int depth = request.getDepth(Integer.MAX_VALUE);
        if (depth == Integer.MAX_VALUE) {
            Document document;
            try {
                document = DomUtil.createDocument();
            }
            catch (ParserConfigurationException e) {
                throw new DavException(500, (Throwable)e);
            }
            Element propfindFiniteDepthCondition = DomUtil.createElement(document, "propfind-finite-depth", DavConstants.NAMESPACE);
            throw new DavException(403, "propfind-finite-depth", null, propfindFiniteDepthCondition);
        }
        super.doPropFind(request, response, resource);
    }

    @Override
    public String getAuthenticateHeaderValue() {
        return DEFAULT_AUTH_HEADER;
    }

    @Override
    protected boolean isPreconditionValid(WebdavRequest request, DavResource resource) {
        return !resource.exists() || request.matchesIfHeader(resource);
    }
}

