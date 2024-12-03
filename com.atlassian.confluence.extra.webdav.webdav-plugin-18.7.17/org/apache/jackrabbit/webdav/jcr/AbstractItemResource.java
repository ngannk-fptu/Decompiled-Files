/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr;

import java.io.IOException;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Workspace;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavCompliance;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.jcr.AbstractResource;
import org.apache.jackrabbit.webdav.jcr.ItemResourceConstants;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.jcr.nodetype.ItemDefinitionImpl;
import org.apache.jackrabbit.webdav.jcr.nodetype.NodeDefinitionImpl;
import org.apache.jackrabbit.webdav.jcr.nodetype.PropertyDefinitionImpl;
import org.apache.jackrabbit.webdav.jcr.property.JcrDavPropertyNameSet;
import org.apache.jackrabbit.webdav.observation.EventDiscovery;
import org.apache.jackrabbit.webdav.observation.ObservationConstants;
import org.apache.jackrabbit.webdav.observation.ObservationResource;
import org.apache.jackrabbit.webdav.observation.Subscription;
import org.apache.jackrabbit.webdav.observation.SubscriptionInfo;
import org.apache.jackrabbit.webdav.observation.SubscriptionManager;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.transaction.TxLockEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractItemResource
extends AbstractResource
implements ObservationResource,
ItemResourceConstants {
    private static Logger log = LoggerFactory.getLogger(AbstractItemResource.class);
    private SubscriptionManager subsMgr;
    protected final Item item;

    AbstractItemResource(DavResourceLocator locator, JcrDavSession session, DavResourceFactory factory, Item item) {
        super(locator, session, factory);
        this.item = item;
        this.initLockSupport();
        this.initSupportedReports();
    }

    @Override
    public String getComplianceClass() {
        return DavCompliance.concatComplianceClasses(new String[]{super.getComplianceClass(), "observation"});
    }

    @Override
    public DavProperty<?> getProperty(DavPropertyName name) {
        AbstractDavProperty prop = super.getProperty(name);
        if (prop == null) {
            if (JCR_DEFINITION.equals(name)) {
                if (this.exists()) {
                    try {
                        ItemDefinitionImpl val = this.item.isNode() ? NodeDefinitionImpl.create(((Node)this.item).getDefinition()) : PropertyDefinitionImpl.create(((Property)this.item).getDefinition());
                        prop = new DefaultDavProperty<NodeDefinitionImpl>(JCR_DEFINITION, (NodeDefinitionImpl)val, true);
                    }
                    catch (RepositoryException e) {
                        log.error("Error while accessing item definition: " + e.getMessage());
                    }
                }
            } else if (JCR_ISNEW.equals(name)) {
                if (this.exists() && this.item.isNew()) {
                    prop = new DefaultDavProperty<Object>(JCR_ISNEW, null, true);
                }
            } else if (JCR_ISMODIFIED.equals(name)) {
                if (this.exists() && this.item.isModified()) {
                    prop = new DefaultDavProperty<Object>(JCR_ISMODIFIED, null, true);
                }
            } else if (ObservationConstants.SUBSCRIPTIONDISCOVERY.equals(name)) {
                prop = this.subsMgr.getSubscriptionDiscovery(this);
            }
        }
        return prop;
    }

    @Override
    public String getSupportedMethods() {
        return "OPTIONS, GET, HEAD, TRACE, PROPFIND, PROPPATCH, MKCOL, COPY, PUT, DELETE, MOVE, LOCK, UNLOCK, SUBSCRIBE, UNSUBSCRIBE, POLL, SEARCH, REPORT";
    }

    @Override
    public boolean exists() {
        return this.item != null;
    }

    @Override
    public String getDisplayName() {
        String resPath = this.getResourcePath();
        return resPath != null ? Text.getName(resPath) : resPath;
    }

    @Override
    public void spool(OutputContext outputContext) throws IOException {
        DavProperty<?> contentLanguage;
        DavProperty<?> contentLength;
        DavProperty<?> contentType;
        if (!this.initedProps) {
            this.initProperties();
        }
        outputContext.setModificationTime(this.getModificationTime());
        DavProperty<?> etag = this.getProperty(DavPropertyName.GETETAG);
        if (etag != null) {
            outputContext.setETag(String.valueOf(etag.getValue()));
        }
        if ((contentType = this.getProperty(DavPropertyName.GETCONTENTTYPE)) != null) {
            outputContext.setContentType(String.valueOf(contentType.getValue()));
        }
        if ((contentLength = this.getProperty(DavPropertyName.GETCONTENTLENGTH)) != null) {
            try {
                long length = Long.parseLong(contentLength.getValue() + "");
                if (length > 0L) {
                    outputContext.setContentLength(length);
                }
            }
            catch (NumberFormatException e) {
                log.error("Could not build content length from property value '" + contentLength.getValue() + "'");
            }
        }
        if ((contentLanguage = this.getProperty(DavPropertyName.GETCONTENTLANGUAGE)) != null) {
            outputContext.setContentLanguage(contentLanguage.getValue().toString());
        }
    }

    @Override
    public DavResource getCollection() {
        DavResource collection = null;
        String parentPath = Text.getRelativeParent(this.getResourcePath(), 1);
        DavResourceLocator parentLoc = this.getLocator().getFactory().createResourceLocator(this.getLocator().getPrefix(), this.getLocator().getWorkspacePath(), parentPath);
        try {
            collection = this.createResourceFromLocator(parentLoc);
        }
        catch (DavException e) {
            log.error("Unexpected error while retrieving collection: " + e.getMessage());
        }
        return collection;
    }

    @Override
    public void move(DavResource destination) throws DavException {
        if (!this.exists()) {
            throw new DavException(404);
        }
        DavResourceLocator destLocator = destination.getLocator();
        if (!this.getLocator().isSameWorkspace(destLocator)) {
            throw new DavException(403);
        }
        try {
            String itemPath = this.getLocator().getRepositoryPath();
            String destItemPath = destination.getLocator().getRepositoryPath();
            if (this.getTransactionId() == null) {
                this.getRepositorySession().getWorkspace().move(itemPath, destItemPath);
            } else {
                this.getRepositorySession().move(itemPath, destItemPath);
            }
        }
        catch (PathNotFoundException e) {
            throw new DavException(409, e.getMessage());
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    public void copy(DavResource destination, boolean shallow) throws DavException {
        if (!this.exists()) {
            throw new DavException(404);
        }
        if (shallow) {
            throw new DavException(403, "Unable to perform shallow copy.");
        }
        try {
            String itemPath = this.getLocator().getRepositoryPath();
            String destItemPath = destination.getLocator().getRepositoryPath();
            Workspace workspace = this.getRepositorySession().getWorkspace();
            if (!this.getLocator().isSameWorkspace(destination.getLocator())) {
                log.error("Copy between workspaces is not yet implemented (src: '" + this.getHref() + "', dest: '" + destination.getHref() + "')");
                throw new DavException(501);
            }
            workspace.copy(itemPath, destItemPath);
        }
        catch (PathNotFoundException e) {
            throw new DavException(404, e.getMessage());
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    public void init(SubscriptionManager subsMgr) {
        this.subsMgr = subsMgr;
    }

    @Override
    public Subscription subscribe(SubscriptionInfo info, String subscriptionId) throws DavException {
        return this.subsMgr.subscribe(info, subscriptionId, this);
    }

    @Override
    public void unsubscribe(String subscriptionId) throws DavException {
        this.subsMgr.unsubscribe(subscriptionId, this);
    }

    @Override
    public EventDiscovery poll(String subscriptionId, long timeout) throws DavException {
        return this.subsMgr.poll(subscriptionId, timeout, this);
    }

    @Override
    protected void initLockSupport() {
        if (this.exists()) {
            this.supportedLock.addEntry(new TxLockEntry(true));
            this.supportedLock.addEntry(new TxLockEntry(false));
        }
    }

    @Override
    protected void initPropertyNames() {
        super.initPropertyNames();
        if (this.exists()) {
            this.names.addAll(JcrDavPropertyNameSet.EXISTING_ITEM_BASE_SET);
            try {
                if (this.item.getDepth() > 0) {
                    this.names.add(JCR_PARENT);
                }
            }
            catch (RepositoryException e) {
                log.warn("Error while accessing node depth: " + e.getMessage());
            }
            if (this.item.isNew()) {
                this.names.add(JCR_ISNEW);
            } else if (this.item.isModified()) {
                this.names.add(JCR_ISMODIFIED);
            }
        } else {
            this.names.addAll(JcrDavPropertyNameSet.ITEM_BASE_SET);
        }
    }

    @Override
    protected void initProperties() {
        super.initProperties();
        if (this.exists()) {
            try {
                this.properties.add(new DefaultDavProperty<String>(JCR_NAME, this.item.getName()));
                this.properties.add(new DefaultDavProperty<String>(JCR_PATH, this.item.getPath()));
                int depth = this.item.getDepth();
                this.properties.add(new DefaultDavProperty<String>(JCR_DEPTH, String.valueOf(depth)));
                if (depth > 0) {
                    String parentHref = this.getLocatorFromItem(this.item.getParent()).getHref(true);
                    this.properties.add(new HrefProperty(JCR_PARENT, parentHref, false));
                }
            }
            catch (RepositoryException e) {
                log.error("Error while accessing jcr properties: " + e.getMessage());
            }
        }
    }

    @Override
    protected String getWorkspaceHref() {
        String workspaceHref = null;
        DavResourceLocator locator = this.getLocator();
        if (locator != null && locator.getWorkspacePath() != null) {
            String wspPath = locator.getWorkspacePath();
            DavResourceLocator wspLocator = locator.getFactory().createResourceLocator(locator.getPrefix(), wspPath, wspPath);
            workspaceHref = wspLocator.getHref(true);
        }
        log.debug(workspaceHref);
        return workspaceHref;
    }

    void complete() throws DavException {
        if (this.exists() && this.getTransactionId() == null) {
            try {
                if (this.item.isModified()) {
                    this.item.save();
                }
            }
            catch (RepositoryException e) {
                log.error("Error while completing request: " + e.getMessage() + " -> reverting changes.");
                try {
                    this.item.refresh(false);
                }
                catch (RepositoryException re) {
                    log.error("Error while reverting changes: " + re.getMessage());
                }
                throw new JcrDavException(e);
            }
        }
    }

    protected static String getItemName(String itemPath) {
        if (itemPath == null) {
            throw new IllegalArgumentException("Cannot retrieve name from a 'null' item path.");
        }
        String name = Text.getName(itemPath);
        if (name.endsWith("]")) {
            name = name.substring(0, name.lastIndexOf(91));
        }
        return name;
    }
}

