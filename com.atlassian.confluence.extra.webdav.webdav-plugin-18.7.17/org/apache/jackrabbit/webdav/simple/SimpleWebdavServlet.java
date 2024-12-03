/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.simple;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.format.DateTimeParseException;
import javax.jcr.Repository;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.jackrabbit.server.BasicCredentialsProvider;
import org.apache.jackrabbit.server.CredentialsProvider;
import org.apache.jackrabbit.server.SessionProvider;
import org.apache.jackrabbit.server.SessionProviderImpl;
import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavSessionProvider;
import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.SimpleLockManager;
import org.apache.jackrabbit.webdav.server.AbstractWebdavServlet;
import org.apache.jackrabbit.webdav.simple.DavSessionProviderImpl;
import org.apache.jackrabbit.webdav.simple.LocatorFactoryImplEx;
import org.apache.jackrabbit.webdav.simple.ResourceConfig;
import org.apache.jackrabbit.webdav.simple.ResourceFactoryImpl;
import org.apache.jackrabbit.webdav.util.HttpDateTimeFormatter;
import org.apache.tika.detect.Detector;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SimpleWebdavServlet
extends AbstractWebdavServlet {
    private static final Logger log = LoggerFactory.getLogger(SimpleWebdavServlet.class);
    public static final String INIT_PARAM_RESOURCE_PATH_PREFIX = "resource-path-prefix";
    public static final String INIT_PARAM_RESOURCE_CONFIG = "resource-config";
    public static final String INIT_PARAM_MIME_INFO = "mime-info";
    public static final String CTX_ATTR_RESOURCE_PATH_PREFIX = "jackrabbit.webdav.simple.resourcepath";
    private String resourcePathPrefix;
    private LockManager lockManager;
    private DavResourceFactory resourceFactory;
    private DavLocatorFactory locatorFactory;
    private DavSessionProvider davSessionProvider;
    private SessionProvider sessionProvider;
    private ResourceConfig config;

    @Override
    public void init() throws ServletException {
        super.init();
        this.resourcePathPrefix = this.getInitParameter(INIT_PARAM_RESOURCE_PATH_PREFIX);
        if (this.resourcePathPrefix == null) {
            log.debug("Missing path prefix > setting to empty string.");
            this.resourcePathPrefix = "";
        } else if (this.resourcePathPrefix.endsWith("/")) {
            log.debug("Path prefix ends with '/' > removing trailing slash.");
            this.resourcePathPrefix = this.resourcePathPrefix.substring(0, this.resourcePathPrefix.length() - 1);
        }
        this.getServletContext().setAttribute(CTX_ATTR_RESOURCE_PATH_PREFIX, (Object)this.resourcePathPrefix);
        log.info("resource-path-prefix = '" + this.resourcePathPrefix + "'");
        this.config = new ResourceConfig(this.getDetector());
        String configParam = this.getInitParameter(INIT_PARAM_RESOURCE_CONFIG);
        if (configParam != null) {
            try {
                this.config.parse(this.getServletContext().getResource(configParam));
            }
            catch (MalformedURLException e) {
                log.debug("Unable to build resource filter provider", (Throwable)e);
            }
        }
    }

    private Detector getDetector() throws ServletException {
        URL url;
        String mimeInfo = this.getInitParameter(INIT_PARAM_MIME_INFO);
        if (mimeInfo != null) {
            try {
                url = this.getServletContext().getResource(mimeInfo);
            }
            catch (MalformedURLException e) {
                throw new ServletException("Invalid mime-info configuration setting: " + mimeInfo, (Throwable)e);
            }
        } else {
            url = MimeTypesFactory.class.getResource("tika-mimetypes.xml");
        }
        try {
            return MimeTypesFactory.create(url);
        }
        catch (MimeTypeException e) {
            throw new ServletException("Invalid MIME media type database: " + url, (Throwable)e);
        }
        catch (IOException e) {
            throw new ServletException("Unable to read MIME media type database: " + url, (Throwable)e);
        }
    }

    @Override
    protected boolean isPreconditionValid(WebdavRequest request, DavResource resource) {
        long ifUnmodifiedSince = -1L;
        try {
            String value = AbstractWebdavServlet.getSingletonField(request, "If-Unmodified-Since");
            if (value != null) {
                ifUnmodifiedSince = HttpDateTimeFormatter.parse(value);
            }
        }
        catch (IllegalArgumentException | DateTimeParseException ex) {
            log.debug("illegal value for if-unmodified-since ignored: " + ex.getMessage());
        }
        if (ifUnmodifiedSince > -1L && resource.exists() && resource.getModificationTime() / 1000L > ifUnmodifiedSince / 1000L) {
            return false;
        }
        return !resource.exists() || request.matchesIfHeader(resource);
    }

    public String getPathPrefix() {
        return this.resourcePathPrefix;
    }

    public static String getPathPrefix(ServletContext ctx) {
        return (String)ctx.getAttribute(CTX_ATTR_RESOURCE_PATH_PREFIX);
    }

    @Override
    public DavLocatorFactory getLocatorFactory() {
        if (this.locatorFactory == null) {
            this.locatorFactory = new LocatorFactoryImplEx(this.resourcePathPrefix);
        }
        return this.locatorFactory;
    }

    @Override
    public void setLocatorFactory(DavLocatorFactory locatorFactory) {
        this.locatorFactory = locatorFactory;
    }

    public LockManager getLockManager() {
        if (this.lockManager == null) {
            this.lockManager = new SimpleLockManager();
        }
        return this.lockManager;
    }

    public void setLockManager(LockManager lockManager) {
        this.lockManager = lockManager;
    }

    @Override
    public DavResourceFactory getResourceFactory() {
        if (this.resourceFactory == null) {
            this.resourceFactory = new ResourceFactoryImpl(this.getLockManager(), this.getResourceConfig());
        }
        return this.resourceFactory;
    }

    @Override
    public void setResourceFactory(DavResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    public synchronized SessionProvider getSessionProvider() {
        if (this.sessionProvider == null) {
            this.sessionProvider = new SessionProviderImpl(this.getCredentialsProvider());
        }
        return this.sessionProvider;
    }

    protected CredentialsProvider getCredentialsProvider() {
        return new BasicCredentialsProvider(this.getInitParameter("missing-auth-mapping"));
    }

    public synchronized void setSessionProvider(SessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    @Override
    public synchronized DavSessionProvider getDavSessionProvider() {
        if (this.davSessionProvider == null) {
            this.davSessionProvider = new DavSessionProviderImpl(this.getRepository(), this.getSessionProvider());
        }
        return this.davSessionProvider;
    }

    @Override
    public synchronized void setDavSessionProvider(DavSessionProvider sessionProvider) {
        this.davSessionProvider = sessionProvider;
    }

    public ResourceConfig getResourceConfig() {
        return this.config;
    }

    public void setResourceConfig(ResourceConfig config) {
        this.config = config;
    }

    public abstract Repository getRepository();
}

