/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.simple;

import java.util.ArrayList;
import java.util.List;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavResourceIteratorImpl;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.simple.DeltaVResourceImpl;
import org.apache.jackrabbit.webdav.simple.ResourceConfig;
import org.apache.jackrabbit.webdav.util.HttpDateFormat;
import org.apache.jackrabbit.webdav.version.LabelInfo;
import org.apache.jackrabbit.webdav.version.LabelSetProperty;
import org.apache.jackrabbit.webdav.version.VersionHistoryResource;
import org.apache.jackrabbit.webdav.version.VersionResource;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionResourceImpl
extends DeltaVResourceImpl
implements VersionResource {
    private static final Logger log = LoggerFactory.getLogger(VersionResourceImpl.class);

    public VersionResourceImpl(DavResourceLocator locator, DavResourceFactory factory, DavSession session, ResourceConfig config, Item item) throws DavException {
        super(locator, factory, session, config, item);
        if (this.getNode() == null || !(this.getNode() instanceof Version)) {
            throw new IllegalArgumentException("Version item expected.");
        }
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public DavResourceIterator getMembers() {
        return DavResourceIteratorImpl.EMPTY;
    }

    @Override
    public void addMember(DavResource member, InputContext inputContext) throws DavException {
        throw new DavException(403);
    }

    @Override
    public void removeMember(DavResource member) throws DavException {
        throw new DavException(403);
    }

    @Override
    public void setProperty(DavProperty<?> property) throws DavException {
        throw new DavException(403);
    }

    @Override
    public void removeProperty(DavPropertyName propertyName) throws DavException {
        throw new DavException(403);
    }

    @Override
    public MultiStatusResponse alterProperties(List<? extends PropEntry> changeList) throws DavException {
        throw new DavException(403);
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
            VersionHistory vh = this.getVersionHistoryItem();
            if (labelInfo.getType() == 1) {
                vh.removeVersionLabel(labelInfo.getLabelName());
            } else if (labelInfo.getType() == 2) {
                vh.addVersionLabel(this.getNode().getName(), labelInfo.getLabelName(), false);
            } else {
                vh.addVersionLabel(this.getNode().getName(), labelInfo.getLabelName(), true);
            }
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
            VersionHistory vh = this.getVersionHistoryItem();
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

    private VersionHistory getVersionHistoryItem() throws RepositoryException {
        return ((Version)this.getNode()).getContainingHistory();
    }

    @Override
    protected void initSupportedReports() {
        super.initSupportedReports();
        if (this.exists()) {
            this.supportedReports.addReportType(ReportType.VERSION_TREE);
        }
    }

    @Override
    protected void initProperties() {
        if (!this.propsInitialized) {
            super.initProperties();
            Version v = (Version)this.getNode();
            try {
                String creationDate = HttpDateFormat.creationDateFormat().format(v.getCreated().getTime());
                this.properties.add(new DefaultDavProperty<String>(DavPropertyName.CREATIONDATE, creationDate));
                this.properties.add(new DefaultDavProperty<String>(VERSION_NAME, v.getName(), true));
                String[] labels = this.getVersionHistoryItem().getVersionLabels(v);
                this.properties.add(new LabelSetProperty(labels));
                this.properties.add(this.getHrefProperty(VersionResource.PREDECESSOR_SET, v.getPredecessors(), true, false));
                this.properties.add(this.getHrefProperty(SUCCESSOR_SET, v.getSuccessors(), true, false));
                String vhHref = this.getLocatorFromNode(this.getVersionHistoryItem()).getHref(true);
                this.properties.add(new HrefProperty(VersionResource.VERSION_HISTORY, vhHref, true));
                PropertyIterator it = v.getReferences();
                ArrayList<Node> nodeList = new ArrayList<Node>();
                while (it.hasNext()) {
                    Node n;
                    Property p = it.nextProperty();
                    if (!"jcr:baseVersion".equals(p.getName()) || !(n = p.getParent()).isCheckedOut()) continue;
                    nodeList.add(n);
                }
                this.properties.add(this.getHrefProperty(CHECKOUT_SET, nodeList.toArray(new Node[nodeList.size()]), true, false));
            }
            catch (RepositoryException e) {
                log.error(e.getMessage());
            }
        }
    }
}

