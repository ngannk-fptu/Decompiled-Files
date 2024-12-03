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
import javax.jcr.RepositoryException;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.jcr.DefaultItemCollection;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.jcr.property.JcrDavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.property.ResourceType;
import org.apache.jackrabbit.webdav.version.VersionHistoryResource;
import org.apache.jackrabbit.webdav.version.VersionResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionHistoryItemCollection
extends DefaultItemCollection
implements VersionHistoryResource {
    private static Logger log = LoggerFactory.getLogger(VersionHistoryItemCollection.class);

    public VersionHistoryItemCollection(DavResourceLocator resourcePath, JcrDavSession session, DavResourceFactory factory, Item item) {
        super(resourcePath, session, factory, item);
        if (item == null || !(item instanceof VersionHistory)) {
            throw new IllegalArgumentException("VersionHistory item expected.");
        }
    }

    @Override
    public String getSupportedMethods() {
        StringBuffer sb = new StringBuffer("OPTIONS, GET, HEAD, TRACE, PROPFIND, PROPPATCH, MKCOL, COPY, PUT, DELETE, MOVE, LOCK, UNLOCK, SUBSCRIBE, UNSUBSCRIBE, POLL, SEARCH, REPORT");
        sb.append(", ").append("");
        return sb.toString();
    }

    @Override
    public DavProperty<?> getProperty(DavPropertyName name) {
        HrefProperty prop = super.getProperty(name);
        if (prop == null) {
            try {
                if (ROOT_VERSION.equals(name)) {
                    String rootVersionHref = this.getLocatorFromItem(((VersionHistory)this.item).getRootVersion()).getHref(true);
                    prop = new HrefProperty(ROOT_VERSION, rootVersionHref, true);
                } else if (VERSION_SET.equals(name)) {
                    VersionIterator vIter = ((VersionHistory)this.item).getAllVersions();
                    prop = this.getHrefProperty(VERSION_SET, vIter, true);
                }
            }
            catch (RepositoryException e) {
                log.error(e.getMessage());
            }
        }
        return prop;
    }

    @Override
    public void removeMember(DavResource member) throws DavException {
        if (this.exists()) {
            VersionHistory versionHistory = (VersionHistory)this.item;
            try {
                versionHistory.removeVersion(VersionHistoryItemCollection.getItemName(member.getLocator().getRepositoryPath()));
            }
            catch (RepositoryException e) {
                throw new JcrDavException(e);
            }
        } else {
            throw new DavException(404);
        }
    }

    @Override
    public VersionResource[] getVersions() throws DavException {
        try {
            VersionIterator vIter = ((VersionHistory)this.item).getAllVersions();
            ArrayList<VersionResource> l = new ArrayList<VersionResource>();
            while (vIter.hasNext()) {
                DavResourceLocator versionLoc = this.getLocatorFromItem(vIter.nextVersion());
                VersionResource vr = (VersionResource)this.createResourceFromLocator(versionLoc);
                l.add(vr);
            }
            return l.toArray(new VersionResource[l.size()]);
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    protected void initPropertyNames() {
        super.initPropertyNames();
        if (this.exists()) {
            this.names.addAll(JcrDavPropertyNameSet.VERSIONHISTORY_SET);
        }
    }

    @Override
    protected void initProperties() {
        super.initProperties();
        this.properties.add(new ResourceType(2));
        try {
            this.properties.add(new DefaultDavProperty<String>(JCR_VERSIONABLEUUID, ((VersionHistory)this.item).getVersionableIdentifier()));
        }
        catch (RepositoryException e) {
            log.error(e.getMessage());
        }
    }
}

