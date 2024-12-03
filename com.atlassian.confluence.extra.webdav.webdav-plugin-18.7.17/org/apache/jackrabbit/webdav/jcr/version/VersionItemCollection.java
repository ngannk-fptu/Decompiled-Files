/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr.version;

import java.util.ArrayList;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.jcr.DefaultItemCollection;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.jcr.property.JcrDavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.util.HttpDateFormat;
import org.apache.jackrabbit.webdav.version.LabelInfo;
import org.apache.jackrabbit.webdav.version.LabelSetProperty;
import org.apache.jackrabbit.webdav.version.VersionHistoryResource;
import org.apache.jackrabbit.webdav.version.VersionResource;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionItemCollection
extends DefaultItemCollection
implements VersionResource {
    private static Logger log = LoggerFactory.getLogger(VersionItemCollection.class);

    public VersionItemCollection(DavResourceLocator locator, JcrDavSession session, DavResourceFactory factory, Item item) {
        super(locator, session, factory, item);
        if (item == null || !(item instanceof Version)) {
            throw new IllegalArgumentException("Version item expected.");
        }
    }

    @Override
    public DavProperty<?> getProperty(DavPropertyName name) {
        AbstractDavProperty prop = super.getProperty(name);
        if (prop == null && this.exists()) {
            Version v = (Version)this.item;
            try {
                if (VERSION_NAME.equals(name)) {
                    prop = new DefaultDavProperty<String>(VERSION_NAME, v.getName(), true);
                } else if (VERSION_HISTORY.equals(name)) {
                    String vhHref = this.getLocatorFromItem(this.getVersionHistoryItem()).getHref(true);
                    prop = new HrefProperty(VERSION_HISTORY, vhHref, true);
                } else if (PREDECESSOR_SET.equals(name)) {
                    prop = this.getHrefProperty(VersionResource.PREDECESSOR_SET, v.getPredecessors(), true);
                } else if (SUCCESSOR_SET.equals(name)) {
                    prop = this.getHrefProperty(SUCCESSOR_SET, v.getSuccessors(), true);
                } else if (LABEL_NAME_SET.equals(name)) {
                    String[] labels = this.getVersionHistoryItem().getVersionLabels(v);
                    prop = new LabelSetProperty(labels);
                } else if (CHECKOUT_SET.equals(name)) {
                    PropertyIterator it = v.getReferences();
                    ArrayList<Node> nodeList = new ArrayList<Node>();
                    while (it.hasNext()) {
                        Node n;
                        Property p = it.nextProperty();
                        if (!"jcr:baseVersion".equals(p.getName()) || !(n = p.getParent()).isCheckedOut()) continue;
                        nodeList.add(n);
                    }
                    prop = this.getHrefProperty(CHECKOUT_SET, nodeList.toArray(new Node[nodeList.size()]), true);
                }
            }
            catch (RepositoryException e) {
                log.error(e.getMessage());
            }
        }
        return prop;
    }

    @Override
    public String getSupportedMethods() {
        StringBuffer sb = new StringBuffer("OPTIONS, GET, HEAD, TRACE, PROPFIND, PROPPATCH, MKCOL, COPY, PUT, DELETE, MOVE, LOCK, UNLOCK, SUBSCRIBE, UNSUBSCRIBE, POLL, SEARCH, REPORT");
        sb.append(", ").append("LABEL");
        return sb.toString();
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
                vh.addVersionLabel(this.item.getName(), labelInfo.getLabelName(), false);
            } else {
                vh.addVersionLabel(this.item.getName(), labelInfo.getLabelName(), true);
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
            DavResourceLocator loc = this.getLocatorFromItem(vh);
            return (VersionHistoryResource)this.createResourceFromLocator(loc);
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    private VersionHistory getVersionHistoryItem() throws RepositoryException {
        return ((Version)this.item).getContainingHistory();
    }

    @Override
    protected void initSupportedReports() {
        super.initSupportedReports();
        if (this.exists()) {
            this.supportedReports.addReportType(ReportType.VERSION_TREE);
        }
    }

    @Override
    protected void initPropertyNames() {
        super.initPropertyNames();
        if (this.exists()) {
            this.names.addAll(JcrDavPropertyNameSet.VERSION_SET);
        }
    }

    @Override
    protected String getCreationDate() {
        if (this.exists()) {
            Version v = (Version)this.item;
            try {
                return HttpDateFormat.creationDateFormat().format(v.getCreated().getTime());
            }
            catch (RepositoryException e) {
                log.error(e.getMessage());
            }
        }
        return super.getCreationDate();
    }
}

