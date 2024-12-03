/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.simple;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.simple.DeltaVResourceImpl;
import org.apache.jackrabbit.webdav.simple.ResourceConfig;
import org.apache.jackrabbit.webdav.version.LabelInfo;
import org.apache.jackrabbit.webdav.version.MergeInfo;
import org.apache.jackrabbit.webdav.version.UpdateInfo;
import org.apache.jackrabbit.webdav.version.VersionControlledResource;
import org.apache.jackrabbit.webdav.version.VersionHistoryResource;
import org.apache.jackrabbit.webdav.version.VersionResource;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionControlledResourceImpl
extends DeltaVResourceImpl
implements VersionControlledResource {
    private static final Logger log = LoggerFactory.getLogger(VersionControlledResourceImpl.class);

    public VersionControlledResourceImpl(DavResourceLocator locator, DavResourceFactory factory, DavSession session, ResourceConfig config, Item item) throws DavException {
        super(locator, factory, session, config, item);
        this.initSupportedReports();
    }

    public VersionControlledResourceImpl(DavResourceLocator locator, DavResourceFactory factory, DavSession session, ResourceConfig config, boolean isCollection) throws DavException {
        super(locator, factory, session, config, isCollection);
        this.initSupportedReports();
    }

    @Override
    public String getSupportedMethods() {
        StringBuffer sb = new StringBuffer(super.getSupportedMethods());
        sb.append(", ").append("VERSION-CONTROL");
        if (this.isVersionControlled()) {
            try {
                if (this.getNode().isCheckedOut()) {
                    sb.append(", ").append("CHECKIN");
                } else {
                    sb.append(", ").append("CHECKOUT");
                    sb.append(", ").append("LABEL");
                }
            }
            catch (RepositoryException e) {
                log.error(e.getMessage());
            }
        }
        return sb.toString();
    }

    @Override
    public void addVersionControl() throws DavException {
        if (!this.exists()) {
            throw new DavException(404);
        }
        if (this.isCollection()) {
            throw new DavException(405);
        }
        if (!this.isVersionControlled()) {
            Node item = this.getNode();
            try {
                item.addMixin("mix:versionable");
                item.save();
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
            Version v = this.getNode().checkin();
            String versionHref = this.getLocatorFromNode(v).getHref(false);
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
            this.getNode().checkout();
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
        throw new DavException(501);
    }

    @Override
    public MultiStatus merge(MergeInfo mergeInfo) throws DavException {
        throw new DavException(501);
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
            if (!this.isVersionControlled() || this.getNode().isCheckedOut()) {
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
        if (!this.isVersionControlled()) {
            throw new DavException(403);
        }
        try {
            VersionHistory vh = this.getNode().getVersionHistory();
            DavResourceLocator loc = this.getLocatorFromNode(vh);
            DavResource vhr = this.createResourceFromLocator(loc);
            if (vhr instanceof VersionHistoryResource) {
                return (VersionHistoryResource)vhr;
            }
            throw new DavException(500);
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
    protected void initProperties() {
        if (!this.propsInitialized) {
            super.initProperties();
            if (this.isVersionControlled()) {
                Node n = this.getNode();
                try {
                    String vhHref = this.getLocatorFromNode(n.getVersionHistory()).getHref(true);
                    this.properties.add(new HrefProperty(VERSION_HISTORY, vhHref, true));
                    this.properties.add(new DefaultDavProperty<Object>(AUTO_VERSION, null, true));
                    String baseVHref = this.getLocatorFromNode(n.getBaseVersion()).getHref(false);
                    if (n.isCheckedOut()) {
                        if (n.hasProperty("jcr:predecessors")) {
                            Value[] pv = n.getProperty("jcr:predecessors").getValues();
                            Node[] predecessors = new Node[pv.length];
                            for (int i = 0; i < pv.length; ++i) {
                                predecessors[i] = n.getSession().getNodeByIdentifier(pv[i].getString());
                            }
                            this.properties.add(this.getHrefProperty(VersionResource.PREDECESSOR_SET, predecessors, true, false));
                        }
                        this.properties.add(new HrefProperty(CHECKED_OUT, baseVHref, true));
                    } else {
                        this.properties.add(new HrefProperty(CHECKED_IN, baseVHref, true));
                    }
                }
                catch (RepositoryException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    private boolean isVersionControlled() {
        boolean vc = false;
        if (this.exists() && !this.isCollection()) {
            Node item = this.getNode();
            try {
                vc = item.isNodeType("mix:versionable");
            }
            catch (RepositoryException e) {
                log.warn(e.getMessage());
            }
        }
        return vc;
    }
}

