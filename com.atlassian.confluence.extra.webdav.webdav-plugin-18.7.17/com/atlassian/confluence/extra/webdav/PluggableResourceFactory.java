/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.webdav;

import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.DavResourceFactoryPluginManager;
import com.atlassian.confluence.extra.webdav.resource.NonExistentResource;
import com.atlassian.confluence.extra.webdav.resource.WorkspaceResourceImpl;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletRequest;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="resourceFactory")
public final class PluggableResourceFactory
implements DavResourceFactory {
    private static final Logger log = LoggerFactory.getLogger(PluggableResourceFactory.class);
    private final String defaultWorkspaceName;
    private final DavResourceFactoryPluginManager davResourceFactoryPluginManager;

    @Autowired
    public PluggableResourceFactory(DavResourceFactoryPluginManager davResourceFactoryPluginManager) {
        this.defaultWorkspaceName = "default";
        this.davResourceFactoryPluginManager = davResourceFactoryPluginManager;
    }

    private DavSession getDavSession(HttpServletRequest httpServletRequest) {
        return (DavSession)httpServletRequest.getSession().getAttribute(ConfluenceDavSession.class.getName());
    }

    @Override
    public DavResource createResource(DavResourceLocator davResourceLocator, DavServletRequest davServletRequest, DavServletResponse davServletResponse) throws DavException {
        return this.createResource(davResourceLocator, this.getDavSession(davServletRequest));
    }

    @Override
    public DavResource createResource(DavResourceLocator davResourceLocator, DavSession davSession) throws DavException {
        String resourcePath = davResourceLocator.getResourcePath();
        log.debug("Trying to locate DavResource: " + resourcePath);
        ConfluenceDavSession confluenceDavSession = (ConfluenceDavSession)davSession;
        LockManager lockManager = confluenceDavSession.getLockManager();
        if (StringUtils.isBlank((String)davResourceLocator.getWorkspacePath())) {
            return new WorkspaceResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.defaultWorkspaceName);
        }
        String[] resourcePathTokens = StringUtils.split((String)resourcePath, (char)'/');
        DavResourceFactory factoryForWorkspace = this.davResourceFactoryPluginManager.getFactoryForWorkspace(resourcePathTokens[0]);
        if (factoryForWorkspace != null) {
            return factoryForWorkspace.createResource(davResourceLocator, confluenceDavSession);
        }
        return new NonExistentResource(davResourceLocator, this, lockManager, confluenceDavSession);
    }
}

