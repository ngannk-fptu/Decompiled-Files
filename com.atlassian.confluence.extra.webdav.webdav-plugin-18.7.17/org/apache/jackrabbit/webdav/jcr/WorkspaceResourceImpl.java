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
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.jcr.Item;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.NodeTypeTemplate;
import javax.jcr.version.Version;
import org.apache.jackrabbit.commons.cnd.CompactNodeTypeDefReader;
import org.apache.jackrabbit.commons.cnd.CompactNodeTypeDefWriter;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.jackrabbit.commons.cnd.TemplateBuilderFactory;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavResourceIteratorImpl;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.jcr.AbstractResource;
import org.apache.jackrabbit.webdav.jcr.ItemResourceConstants;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.jcr.property.JcrDavPropertyNameSet;
import org.apache.jackrabbit.webdav.jcr.property.NamespacesProperty;
import org.apache.jackrabbit.webdav.jcr.security.JcrSupportedPrivilegesProperty;
import org.apache.jackrabbit.webdav.jcr.security.JcrUserPrivilegesProperty;
import org.apache.jackrabbit.webdav.jcr.security.SecurityUtils;
import org.apache.jackrabbit.webdav.jcr.version.report.JcrPrivilegeReport;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.security.SecurityConstants;
import org.apache.jackrabbit.webdav.version.LabelInfo;
import org.apache.jackrabbit.webdav.version.MergeInfo;
import org.apache.jackrabbit.webdav.version.UpdateInfo;
import org.apache.jackrabbit.webdav.version.VersionControlledResource;
import org.apache.jackrabbit.webdav.version.VersionHistoryResource;
import org.apache.jackrabbit.webdav.version.WorkspaceResource;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class WorkspaceResourceImpl
extends AbstractResource
implements WorkspaceResource,
VersionControlledResource {
    private static Logger log = LoggerFactory.getLogger(WorkspaceResourceImpl.class);

    WorkspaceResourceImpl(DavResourceLocator locator, JcrDavSession session, DavResourceFactory factory) {
        super(locator, session, factory);
        this.initLockSupport();
        this.initSupportedReports();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DavProperty<?> getProperty(DavPropertyName name) {
        AbstractDavProperty prop = super.getProperty(name);
        if (prop == null) {
            StringWriter writer = null;
            try {
                if (ItemResourceConstants.JCR_NODETYPES_CND.equals(name)) {
                    writer = new StringWriter();
                    Session s = this.getRepositorySession();
                    CompactNodeTypeDefWriter cndWriter = new CompactNodeTypeDefWriter((Writer)writer, s, true);
                    NodeTypeIterator ntIterator = s.getWorkspace().getNodeTypeManager().getAllNodeTypes();
                    while (ntIterator.hasNext()) {
                        cndWriter.write(ntIterator.nextNodeType());
                    }
                    cndWriter.close();
                    prop = new DefaultDavProperty<String>(ItemResourceConstants.JCR_NODETYPES_CND, writer.toString(), true);
                } else if (SecurityConstants.SUPPORTED_PRIVILEGE_SET.equals(name)) {
                    prop = new JcrSupportedPrivilegesProperty(this.getRepositorySession(), null).asDavProperty();
                } else if (SecurityConstants.CURRENT_USER_PRIVILEGE_SET.equals(name)) {
                    prop = new JcrUserPrivilegesProperty(this.getRepositorySession(), null).asDavProperty();
                }
            }
            catch (RepositoryException e) {
                log.error("Failed to access NodeTypeManager: " + e.getMessage());
            }
            catch (IOException e) {
                log.error("Failed to write compact node definition: " + e.getMessage());
            }
            finally {
                if (writer != null) {
                    try {
                        writer.close();
                    }
                    catch (IOException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        }
        return prop;
    }

    @Override
    public String getSupportedMethods() {
        StringBuilder sb = new StringBuilder("OPTIONS, GET, HEAD, TRACE, PROPFIND, PROPPATCH, MKCOL, COPY, PUT, DELETE, MOVE, LOCK, UNLOCK");
        sb.append(", ");
        sb.append("REPORT, MKWORKSPACE");
        sb.append(", ");
        sb.append("SEARCH");
        sb.append(", ");
        sb.append("UPDATE");
        return sb.toString();
    }

    @Override
    public boolean exists() {
        try {
            List<String> available = Arrays.asList(this.getRepositorySession().getWorkspace().getAccessibleWorkspaceNames());
            return available.contains(this.getDisplayName());
        }
        catch (RepositoryException e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public String getDisplayName() {
        return this.getLocator().getWorkspaceName();
    }

    @Override
    public long getModificationTime() {
        return new Date().getTime();
    }

    @Override
    public void spool(OutputContext outputContext) throws IOException {
        outputContext.setProperty("Link", "<??type=journal>; title=\"Event Journal\"; rel=alternate; type=\"application/atom+xml\"");
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
            sb.append("</title>");
            sb.append("<link rel=alternate type=\"application/atom+xml\" title=\"Event Journal\" href=\"??type=journal\">");
            sb.append("</head>");
            sb.append("<body><h2>").append(repostr).append("</h2><ul>");
            sb.append("<li><a href=\"..\">..</a></li>");
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
        DavResource collection = null;
        DavResourceLocator parentLoc = this.getLocator().getFactory().createResourceLocator(this.getLocator().getPrefix(), null, null, false);
        try {
            collection = this.createResourceFromLocator(parentLoc);
        }
        catch (DavException e) {
            log.error("Unexpected error while retrieving collection: " + e.getMessage());
        }
        return collection;
    }

    @Override
    public void addMember(DavResource resource, InputContext inputContext) throws DavException {
        log.error("Cannot add a new member to the workspace resource.");
        throw new DavException(403);
    }

    @Override
    public DavResourceIterator getMembers() {
        try {
            DavResourceLocator loc = this.getLocatorFromItem(this.getRepositorySession().getRootNode());
            List<DavResource> list = Collections.singletonList(this.createResourceFromLocator(loc));
            return new DavResourceIteratorImpl(list);
        }
        catch (DavException e) {
            log.error("Internal error while building resource for the root node.", (Throwable)e);
            return DavResourceIteratorImpl.EMPTY;
        }
        catch (RepositoryException e) {
            log.error("Internal error while building resource for the root node.", (Throwable)e);
            return DavResourceIteratorImpl.EMPTY;
        }
    }

    @Override
    public void removeMember(DavResource member) throws DavException {
        log.error("Cannot add a remove the root node.");
        throw new DavException(403);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void setProperty(DavProperty<?> property) throws DavException {
        if (ItemResourceConstants.JCR_NAMESPACES.equals(property.getName())) {
            NamespacesProperty nsp = new NamespacesProperty(property);
            try {
                HashMap<String, String> changes = new HashMap<String, String>(nsp.getNamespaces());
                NamespaceRegistry nsReg = this.getRepositorySession().getWorkspace().getNamespaceRegistry();
                for (String prefix : nsReg.getPrefixes()) {
                    if (!changes.containsKey(prefix)) {
                        nsReg.unregisterNamespace(prefix);
                        continue;
                    }
                    if (!((String)changes.get(prefix)).equals(nsReg.getURI(prefix))) continue;
                    changes.remove(prefix);
                }
                for (String prefix : changes.keySet()) {
                    String uri = (String)changes.get(prefix);
                    nsReg.registerNamespace(prefix, uri);
                }
                return;
            }
            catch (RepositoryException e) {
                throw new JcrDavException(e);
            }
        }
        if (!ItemResourceConstants.JCR_NODETYPES_CND.equals(property.getName())) throw new DavException(409);
        try {
            List<?> cmds;
            Object value = property.getValue();
            if (value instanceof List) {
                cmds = (List<?>)value;
            } else if (value instanceof Element) {
                cmds = Collections.singletonList(value);
            } else {
                log.warn("Unexpected structure of dcr:nodetypes-cnd property.");
                throw new DavException(500);
            }
            String registerCnd = null;
            boolean allowUpdate = false;
            ArrayList<String> unregisterNames = new ArrayList<String>();
            for (Object listEntry : cmds) {
                if (!(listEntry instanceof Element)) continue;
                Element e = (Element)listEntry;
                String localName = e.getLocalName();
                if ("cnd".equals(localName)) {
                    registerCnd = DomUtil.getText(e);
                    continue;
                }
                if ("allowupdate".equals(localName)) {
                    String allow = DomUtil.getTextTrim(e);
                    allowUpdate = Boolean.parseBoolean(allow);
                    continue;
                }
                if (!"nodetypename".equals(localName)) continue;
                unregisterNames.add(DomUtil.getTextTrim(e));
            }
            Session s = this.getRepositorySession();
            NodeTypeManager ntMgr = s.getWorkspace().getNodeTypeManager();
            if (registerCnd != null) {
                StringReader reader = new StringReader(registerCnd);
                TemplateBuilderFactory factory = new TemplateBuilderFactory(ntMgr, s.getValueFactory(), s.getWorkspace().getNamespaceRegistry());
                CompactNodeTypeDefReader<NodeTypeTemplate, NamespaceRegistry> cndReader = new CompactNodeTypeDefReader<NodeTypeTemplate, NamespaceRegistry>(reader, "davex", factory);
                List<NodeTypeTemplate> ntts = cndReader.getNodeTypeDefinitions();
                ntMgr.registerNodeTypes(ntts.toArray(new NodeTypeTemplate[ntts.size()]), allowUpdate);
                return;
            } else {
                if (unregisterNames.isEmpty()) return;
                ntMgr.unregisterNodeTypes(unregisterNames.toArray(new String[unregisterNames.size()]));
            }
            return;
        }
        catch (ParseException e) {
            throw new DavException(400, (Throwable)e);
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    public MultiStatusResponse alterProperties(List<? extends PropEntry> changeList) throws DavException {
        PropEntry propEntry;
        if (changeList.size() == 1) {
            propEntry = changeList.get(0);
            if (!(propEntry instanceof DavProperty) || !ItemResourceConstants.JCR_NAMESPACES.equals(((DavProperty)propEntry).getName()) && !ItemResourceConstants.JCR_NODETYPES_CND.equals(((DavProperty)propEntry).getName())) {
                throw new DavException(409);
            }
        } else {
            throw new DavException(409);
        }
        this.setProperty((DavProperty)propEntry);
        return new MultiStatusResponse(this.getHref(), 200);
    }

    @Override
    public void addVersionControl() throws DavException {
        throw new DavException(403);
    }

    @Override
    public String checkin() throws DavException {
        throw new DavException(403);
    }

    @Override
    public void checkout() throws DavException {
        throw new DavException(403);
    }

    @Override
    public void uncheckout() throws DavException {
        throw new DavException(403);
    }

    @Override
    public MultiStatus update(UpdateInfo updateInfo) throws DavException {
        if (updateInfo == null) {
            throw new DavException(400, "Valid update request body required.");
        }
        if (!this.exists()) {
            throw new DavException(404);
        }
        Session session = this.getRepositorySession();
        MultiStatus ms = new MultiStatus();
        try {
            Element udElem = updateInfo.getUpdateElement();
            boolean removeExisting = DomUtil.hasChildElement(udElem, "removeexisting", ItemResourceConstants.NAMESPACE);
            AbstractResource.EListener el = new AbstractResource.EListener(updateInfo.getPropertyNameSet(), ms);
            this.registerEventListener(el, session.getRootNode().getPath());
            String[] hrefs = updateInfo.getVersionHref();
            if (hrefs == null || hrefs.length < 1) {
                throw new DavException(400, "Invalid update request body: at least a single version href must be specified.");
            }
            Version[] versions = new Version[hrefs.length];
            for (int i = 0; i < hrefs.length; ++i) {
                String href = this.normalizeResourceHref(hrefs[i]);
                DavResourceLocator vLoc = this.getLocator().getFactory().createResourceLocator(this.getLocator().getPrefix(), href);
                String versionPath = vLoc.getRepositoryPath();
                Item item = this.getRepositorySession().getItem(versionPath);
                if (!(item instanceof Version)) {
                    throw new DavException(400, "Invalid update request body: href does not identify a version " + hrefs[i]);
                }
                versions[i] = (Version)item;
            }
            session.getWorkspace().restore(versions, removeExisting);
            this.unregisterEventListener(el);
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
        return ms;
    }

    @Override
    public MultiStatus merge(MergeInfo mergeInfo) throws DavException {
        throw new DavException(403);
    }

    @Override
    public void label(LabelInfo labelInfo) throws DavException {
        throw new DavException(403);
    }

    @Override
    public VersionHistoryResource getVersionHistory() throws DavException {
        throw new DavException(403);
    }

    @Override
    protected void initLockSupport() {
    }

    @Override
    protected void initSupportedReports() {
        super.initSupportedReports();
        this.supportedReports.addReportType(JcrPrivilegeReport.PRIVILEGES_REPORT);
    }

    @Override
    protected String getWorkspaceHref() {
        return this.getHref();
    }

    @Override
    protected void initPropertyNames() {
        super.initPropertyNames();
        this.names.addAll(JcrDavPropertyNameSet.WORKSPACE_SET);
        if (SecurityUtils.supportsAccessControl(this.getRepositorySession())) {
            this.names.add(SecurityConstants.SUPPORTED_PRIVILEGE_SET);
            this.names.add(SecurityConstants.CURRENT_USER_PRIVILEGE_SET);
        }
    }

    @Override
    protected void initProperties() {
        super.initProperties();
        try {
            NamespaceRegistry nsReg = this.getRepositorySession().getWorkspace().getNamespaceRegistry();
            NamespacesProperty namespacesProp = new NamespacesProperty(nsReg);
            this.properties.add(namespacesProp);
        }
        catch (RepositoryException e) {
            log.error("Failed to access NamespaceRegistry: " + e.getMessage());
        }
    }
}

