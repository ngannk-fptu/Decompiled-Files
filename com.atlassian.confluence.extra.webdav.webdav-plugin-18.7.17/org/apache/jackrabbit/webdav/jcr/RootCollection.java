/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavResourceIteratorImpl;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.jcr.AbstractResource;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.jcr.security.JcrSupportedPrivilegesProperty;
import org.apache.jackrabbit.webdav.jcr.security.SecurityUtils;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.security.SecurityConstants;
import org.apache.jackrabbit.webdav.security.SupportedPrivilegeSetProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RootCollection
extends AbstractResource {
    private static Logger log = LoggerFactory.getLogger(RootCollection.class);

    protected RootCollection(DavResourceLocator locator, JcrDavSession session, DavResourceFactory factory) {
        super(locator, session, factory);
        this.initLockSupport();
        this.initSupportedReports();
    }

    @Override
    public String getSupportedMethods() {
        StringBuilder sb = new StringBuilder("OPTIONS, GET, HEAD, TRACE, PROPFIND, PROPPATCH, MKCOL, COPY, PUT, DELETE, MOVE, LOCK, UNLOCK");
        sb.append(", ");
        sb.append("REPORT, MKWORKSPACE");
        sb.append(", ");
        sb.append("SEARCH");
        return sb.toString();
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public DavProperty<?> getProperty(DavPropertyName name) {
        SupportedPrivilegeSetProperty prop = super.getProperty(name);
        if (prop == null) {
            try {
                if (SecurityConstants.SUPPORTED_PRIVILEGE_SET.equals(name)) {
                    prop = new JcrSupportedPrivilegesProperty(this.getRepositorySession()).asDavProperty();
                }
            }
            catch (RepositoryException e) {
                log.error("Failed to build SupportedPrivilegeSet property: " + e.getMessage());
            }
        }
        return prop;
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public String getDisplayName() {
        return "";
    }

    @Override
    public long getModificationTime() {
        return new Date().getTime();
    }

    @Override
    public void spool(OutputContext outputContext) throws IOException {
        if (outputContext.hasStream()) {
            Session session = this.getRepositorySession();
            Repository rep = session.getRepository();
            String repName = rep.getDescriptor("jcr.repository.name");
            String repURL = rep.getDescriptor("jcr.repository.vendor.url");
            String repVersion = rep.getDescriptor("jcr.repository.version");
            String repostr = repName + " " + repVersion;
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><title>");
            sb.append(repostr);
            sb.append("</title></head>");
            sb.append("<body><h2>").append(repostr).append("</h2>");
            sb.append("<h3>Available Workspace Resources:</h3><ul>");
            DavResourceIterator it = this.getMembers();
            while (it.hasNext()) {
                DavResource res = it.nextResource();
                sb.append("<li><a href=\"");
                sb.append(res.getHref());
                sb.append("\">");
                sb.append(res.getDisplayName());
                sb.append("</a></li>");
            }
            sb.append("</ul><hr size=\"1\"><em>Powered by <a href=\"");
            sb.append(repURL).append("\">").append(repName);
            sb.append("</a> ").append(repVersion);
            sb.append("</em></body></html>");
            outputContext.setContentLength(sb.length());
            outputContext.setModificationTime(this.getModificationTime());
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputContext.getOutputStream(), "utf8"));
            writer.print(sb.toString());
            writer.close();
        } else {
            outputContext.setContentLength(0L);
            outputContext.setModificationTime(this.getModificationTime());
        }
    }

    @Override
    public DavResource getCollection() {
        return null;
    }

    @Override
    public void addMember(DavResource resource, InputContext inputContext) throws DavException {
        throw new DavException(403);
    }

    @Override
    public DavResourceIterator getMembers() {
        ArrayList<DavResource> memberList = new ArrayList<DavResource>();
        try {
            String[] wsNames;
            for (String wsName : wsNames = this.getRepositorySession().getWorkspace().getAccessibleWorkspaceNames()) {
                String wspPath = "/" + wsName;
                DavResourceLocator childLoc = this.getLocator().getFactory().createResourceLocator(this.getLocator().getPrefix(), wspPath, wspPath);
                memberList.add(this.createResourceFromLocator(childLoc));
            }
        }
        catch (RepositoryException e) {
            log.error(e.getMessage());
        }
        catch (DavException e) {
            log.error(e.getMessage());
        }
        return new DavResourceIteratorImpl(memberList);
    }

    @Override
    public void removeMember(DavResource member) throws DavException {
        Workspace wsp = this.getRepositorySession().getWorkspace();
        String name = Text.getName(member.getResourcePath());
        try {
            wsp.deleteWorkspace(name);
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    public void addWorkspace(DavResource workspace) throws DavException {
        Workspace wsp = this.getRepositorySession().getWorkspace();
        String name = workspace.getDisplayName();
        try {
            wsp.createWorkspace(name);
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    protected void initLockSupport() {
    }

    @Override
    protected String getWorkspaceHref() {
        return null;
    }

    @Override
    protected void initPropertyNames() {
        super.initPropertyNames();
        if (SecurityUtils.supportsAccessControl(this.getRepositorySession())) {
            this.names.add(SecurityConstants.SUPPORTED_PRIVILEGE_SET);
        }
    }
}

