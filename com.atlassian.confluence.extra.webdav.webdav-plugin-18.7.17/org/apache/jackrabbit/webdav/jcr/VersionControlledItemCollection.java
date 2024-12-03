/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionManager;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.jcr.AbstractResource;
import org.apache.jackrabbit.webdav.jcr.DefaultItemCollection;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.jcr.property.JcrDavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.version.LabelInfo;
import org.apache.jackrabbit.webdav.version.MergeInfo;
import org.apache.jackrabbit.webdav.version.UpdateInfo;
import org.apache.jackrabbit.webdav.version.VersionControlledResource;
import org.apache.jackrabbit.webdav.version.VersionHistoryResource;
import org.apache.jackrabbit.webdav.version.VersionResource;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class VersionControlledItemCollection
extends DefaultItemCollection
implements VersionControlledResource {
    private static Logger log = LoggerFactory.getLogger(VersionControlledItemCollection.class);

    public VersionControlledItemCollection(DavResourceLocator locator, JcrDavSession session, DavResourceFactory factory, Item item) {
        super(locator, session, factory, item);
        if (this.exists() && !(item instanceof Node)) {
            throw new IllegalArgumentException("A collection resource can not be constructed from a Property item.");
        }
    }

    @Override
    public String getSupportedMethods() {
        StringBuffer sb = new StringBuffer(super.getSupportedMethods());
        sb.append(", ").append("VERSION-CONTROL");
        if (this.isVersionControlled()) {
            try {
                if (((Node)this.item).isCheckedOut()) {
                    sb.append(", ").append("CHECKIN, MERGE");
                } else {
                    sb.append(", ").append("CHECKOUT, UPDATE, MERGE, LABEL");
                }
            }
            catch (RepositoryException e) {
                log.error(e.getMessage());
            }
        }
        return sb.toString();
    }

    @Override
    public DavProperty<?> getProperty(DavPropertyName name) {
        HrefProperty prop = super.getProperty(name);
        if (prop == null && this.isVersionControlled()) {
            Node n = (Node)this.item;
            try {
                if (VERSION_HISTORY.equals(name)) {
                    String vhHref = this.getLocatorFromItem(n.getVersionHistory()).getHref(true);
                    prop = new HrefProperty(VERSION_HISTORY, vhHref, true);
                } else if (CHECKED_OUT.equals(name) && n.isCheckedOut()) {
                    String baseVHref = this.getLocatorFromItem(n.getBaseVersion()).getHref(true);
                    prop = new HrefProperty(CHECKED_OUT, baseVHref, true);
                } else if (CHECKED_IN.equals(name) && !n.isCheckedOut()) {
                    String baseVHref = this.getLocatorFromItem(n.getBaseVersion()).getHref(true);
                    prop = new HrefProperty(CHECKED_IN, baseVHref, true);
                }
            }
            catch (RepositoryException e) {
                log.error(e.getMessage());
            }
        }
        return prop;
    }

    @Override
    public MultiStatusResponse alterProperties(List<? extends PropEntry> changeList) throws DavException {
        this.resolveMergeConflict(changeList);
        return super.alterProperties(changeList);
    }

    private void resolveMergeConflict(List<? extends PropEntry> changeList) throws DavException {
        if (!this.exists()) {
            throw new DavException(404);
        }
        try {
            Node n = (Node)this.item;
            VersionManager vMgr = this.getVersionManager();
            String path = this.item.getPath();
            DavProperty autoMergeSet = null;
            DavProperty predecessorSet = null;
            for (int i = 0; i < changeList.size(); ++i) {
                PropEntry propEntry = changeList.get(i);
                if (propEntry instanceof DavPropertyName && AUTO_MERGE_SET.equals(propEntry)) {
                    Value[] mergeFailed;
                    if (!n.hasProperty("jcr:mergeFailed")) {
                        throw new DavException(409, "Attempt to resolve non-existing merge conflicts.");
                    }
                    for (Value value : mergeFailed = n.getProperty("jcr:mergeFailed").getValues()) {
                        vMgr.cancelMerge(path, (Version)this.getRepositorySession().getNodeByIdentifier(value.getString()));
                    }
                    changeList.remove(propEntry);
                    continue;
                }
                if (!(propEntry instanceof DavProperty)) continue;
                if (AUTO_MERGE_SET.equals(((DavProperty)propEntry).getName())) {
                    autoMergeSet = (DavProperty)propEntry;
                    continue;
                }
                if (!PREDECESSOR_SET.equals(((DavProperty)propEntry).getName())) continue;
                predecessorSet = (DavProperty)propEntry;
            }
            if (autoMergeSet != null) {
                Value[] mergeFailed;
                if (!n.hasProperty("jcr:mergeFailed")) {
                    throw new DavException(409, "Attempt to resolve non-existing merge conflicts.");
                }
                List<String> mergeset = new HrefProperty(autoMergeSet).getHrefs();
                List<Object> predecL = predecessorSet == null ? Collections.emptyList() : new HrefProperty(predecessorSet).getHrefs();
                Session session = this.getRepositorySession();
                for (Value value : mergeFailed = n.getProperty("jcr:mergeFailed").getValues()) {
                    Version version = (Version)session.getNodeByIdentifier(value.getString());
                    String href = this.getLocatorFromItem(version).getHref(true);
                    if (mergeset.contains(href)) continue;
                    if (predecL.contains(href)) {
                        vMgr.doneMerge(path, version);
                        continue;
                    }
                    vMgr.cancelMerge(path, version);
                }
                changeList.remove(autoMergeSet);
                if (predecessorSet != null) {
                    changeList.remove(predecessorSet);
                }
            }
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    public void addVersionControl() throws DavException {
        if (!this.exists()) {
            throw new DavException(404);
        }
        if (!this.isVersionControlled()) {
            try {
                ((Node)this.item).addMixin("mix:versionable");
                this.item.save();
            }
            catch (RepositoryException e) {
                throw new JcrDavException(e);
            }
        }
    }

    @Override
    public String checkin() throws DavException {
        if (!this.exists()) {
            throw new DavException(404);
        }
        if (!this.isVersionControlled()) {
            throw new DavException(405);
        }
        try {
            Version v = this.getVersionManager().checkin(this.item.getPath());
            String versionHref = this.getLocatorFromItem(v).getHref(true);
            return versionHref;
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    public void checkout() throws DavException {
        if (!this.exists()) {
            throw new DavException(404);
        }
        if (!this.isVersionControlled()) {
            throw new DavException(405);
        }
        try {
            this.getVersionManager().checkout(this.item.getPath());
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    public void uncheckout() throws DavException {
        throw new DavException(501);
    }

    @Override
    public MultiStatus update(UpdateInfo updateInfo) throws DavException {
        if (updateInfo == null) {
            throw new DavException(400, "Valid update request body required.");
        }
        if (!this.exists()) {
            throw new DavException(404);
        }
        MultiStatus ms = new MultiStatus();
        try {
            Node node = (Node)this.item;
            Element udElem = updateInfo.getUpdateElement();
            boolean removeExisting = DomUtil.hasChildElement(udElem, "removeexisting", NAMESPACE);
            AbstractResource.EListener el = new AbstractResource.EListener(updateInfo.getPropertyNameSet(), ms);
            this.registerEventListener(el, node.getPath());
            if (updateInfo.getVersionHref() != null) {
                String[] hrefs = updateInfo.getVersionHref();
                if (hrefs.length != 1) {
                    throw new DavException(400, "Invalid update request body missing version href or containing multiple version hrefs.");
                }
                String href = this.normalizeResourceHref(hrefs[0]);
                String versionPath = this.getLocatorFromHref(href).getRepositoryPath();
                String versionName = VersionControlledItemCollection.getItemName(versionPath);
                String relPath = DomUtil.getChildText(udElem, "relpath", NAMESPACE);
                if (relPath == null) {
                    node.restore(versionName, removeExisting);
                } else if (node.hasNode(relPath)) {
                    Version v = node.getNode(relPath).getVersionHistory().getVersion(versionName);
                    node.restore(v, relPath, removeExisting);
                } else {
                    Version v = (Version)this.getRepositorySession().getNode(versionPath);
                    node.restore(v, relPath, removeExisting);
                }
            } else if (updateInfo.getLabelName() != null) {
                String[] labels = updateInfo.getLabelName();
                if (labels.length != 1) {
                    throw new DavException(400, "Invalid update request body: Multiple labels specified.");
                }
                node.restoreByLabel(labels[0], removeExisting);
            } else if (updateInfo.getWorkspaceHref() != null) {
                String href = this.normalizeResourceHref(VersionControlledItemCollection.obtainAbsolutePathFromUri(updateInfo.getWorkspaceHref()));
                String workspaceName = this.getLocatorFromHref(href).getWorkspaceName();
                node.update(workspaceName);
            } else {
                throw new DavException(400, "Invalid update request body.");
            }
            this.unregisterEventListener(el);
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
        return ms;
    }

    @Override
    public MultiStatus merge(MergeInfo mergeInfo) throws DavException {
        if (mergeInfo == null) {
            throw new DavException(400);
        }
        if (!this.exists()) {
            throw new DavException(404);
        }
        MultiStatus ms = new MultiStatus();
        try {
            String href = this.normalizeResourceHref(mergeInfo.getSourceHrefs()[0]);
            String workspaceName = this.getLocatorFromHref(href).getWorkspaceName();
            String depth = DomUtil.getChildTextTrim(mergeInfo.getMergeElement(), "depth", DavConstants.NAMESPACE);
            boolean isShallow = "0".equals(depth);
            NodeIterator failed = this.getVersionManager().merge(this.item.getPath(), workspaceName, !mergeInfo.isNoAutoMerge(), isShallow);
            while (failed.hasNext()) {
                Node failedNode = failed.nextNode();
                DavResourceLocator loc = this.getLocatorFromItem(failedNode);
                DavResource res = this.createResourceFromLocator(loc);
                ms.addResponse(new MultiStatusResponse(res, mergeInfo.getPropertyNameSet()));
            }
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
        return ms;
    }

    @Override
    public void label(LabelInfo labelInfo) throws DavException {
        if (labelInfo == null) {
            throw new DavException(400, "Valid label request body required.");
        }
        if (!this.exists()) {
            throw new DavException(404);
        }
        try {
            if (!this.isVersionControlled() || ((Node)this.item).isCheckedOut()) {
                throw new DavException(412, "A LABEL request may only be applied to a version-controlled, checked-in resource.");
            }
            DavResource[] resArr = this.getReferenceResources(CHECKED_IN);
            if (resArr.length != 1 || !(resArr[0] instanceof VersionResource)) {
                throw new DavException(500, "DAV:checked-in property on '" + this.getHref() + "' did not point to a single VersionResource.");
            }
            ((VersionResource)resArr[0]).label(labelInfo);
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    public VersionHistoryResource getVersionHistory() throws DavException {
        if (!this.exists()) {
            throw new DavException(404);
        }
        try {
            VersionHistory vh = ((Node)this.item).getVersionHistory();
            DavResourceLocator loc = this.getLocatorFromItem(vh);
            return (VersionHistoryResource)this.createResourceFromLocator(loc);
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    protected void initSupportedReports() {
        super.initSupportedReports();
        if (this.exists()) {
            this.supportedReports.addReportType(ReportType.LOCATE_BY_HISTORY);
            if (this.isVersionControlled()) {
                this.supportedReports.addReportType(ReportType.VERSION_TREE);
            }
        }
    }

    @Override
    protected void initPropertyNames() {
        super.initPropertyNames();
        if (this.isVersionControlled()) {
            this.names.addAll(JcrDavPropertyNameSet.VERSIONABLE_SET);
            Node n = (Node)this.item;
            try {
                if (n.isCheckedOut()) {
                    this.names.add(CHECKED_OUT);
                    if (n.hasProperty("jcr:predecessors")) {
                        this.names.add(PREDECESSOR_SET);
                    }
                    if (n.hasProperty("jcr:mergeFailed")) {
                        this.names.add(AUTO_MERGE_SET);
                    }
                } else {
                    this.names.add(CHECKED_IN);
                }
            }
            catch (RepositoryException e) {
                log.warn(e.getMessage());
            }
        }
    }

    @Override
    protected void initProperties() {
        super.initProperties();
        if (this.isVersionControlled()) {
            Node n = (Node)this.item;
            try {
                String vhHref = this.getLocatorFromItem(n.getVersionHistory()).getHref(true);
                this.properties.add(new HrefProperty(VERSION_HISTORY, vhHref, true));
                this.properties.add(new DefaultDavProperty<Object>(AUTO_VERSION, null, false));
                String baseVHref = this.getLocatorFromItem(n.getBaseVersion()).getHref(true);
                if (n.isCheckedOut()) {
                    if (n.hasProperty("jcr:predecessors")) {
                        Value[] predec = n.getProperty("jcr:predecessors").getValues();
                        this.addHrefProperty(PREDECESSOR_SET, predec, false);
                    }
                    if (n.hasProperty("jcr:mergeFailed")) {
                        Value[] mergeFailed = n.getProperty("jcr:mergeFailed").getValues();
                        this.addHrefProperty(AUTO_MERGE_SET, mergeFailed, false);
                    }
                }
            }
            catch (RepositoryException e) {
                log.error(e.getMessage());
            }
        }
    }

    private void addHrefProperty(DavPropertyName name, Value[] values, boolean isProtected) throws ValueFormatException, IllegalStateException, RepositoryException {
        Item[] nodes = new Node[values.length];
        for (int i = 0; i < values.length; ++i) {
            nodes[i] = this.getRepositorySession().getNodeByIdentifier(values[i].getString());
        }
        this.addHrefProperty(name, nodes, isProtected);
    }

    private boolean isVersionControlled() {
        boolean vc = false;
        if (this.exists()) {
            try {
                vc = ((Node)this.item).isNodeType("mix:versionable");
            }
            catch (RepositoryException e) {
                log.warn(e.getMessage());
            }
        }
        return vc;
    }

    private DavResourceLocator getLocatorFromHref(String href) {
        DavLocatorFactory f = this.getLocator().getFactory();
        String prefix = this.getLocator().getPrefix();
        return f.createResourceLocator(prefix, href);
    }

    private VersionManager getVersionManager() throws RepositoryException {
        return this.getRepositorySession().getWorkspace().getVersionManager();
    }

    private static String obtainAbsolutePathFromUri(String uri) {
        try {
            URI u = new URI(uri);
            StringBuilder sb = new StringBuilder();
            sb.append(u.getRawPath());
            if (u.getRawQuery() != null) {
                sb.append("?").append(u.getRawQuery());
            }
            return sb.toString();
        }
        catch (URISyntaxException ex) {
            log.warn("parsing " + uri, (Throwable)ex);
            return uri;
        }
    }
}

