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
import javax.jcr.RepositoryException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import org.apache.jackrabbit.util.Text;
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
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.property.ResourceType;
import org.apache.jackrabbit.webdav.simple.DeltaVResourceImpl;
import org.apache.jackrabbit.webdav.simple.ResourceConfig;
import org.apache.jackrabbit.webdav.version.VersionHistoryResource;
import org.apache.jackrabbit.webdav.version.VersionResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionHistoryResourceImpl
extends DeltaVResourceImpl
implements VersionHistoryResource {
    private static final Logger log = LoggerFactory.getLogger(VersionHistoryResourceImpl.class);

    public VersionHistoryResourceImpl(DavResourceLocator locator, DavResourceFactory factory, DavSession session, ResourceConfig config, Item item) throws DavException {
        super(locator, factory, session, config, item);
        if (this.getNode() == null || !(this.getNode() instanceof VersionHistory)) {
            throw new IllegalArgumentException("VersionHistory item expected.");
        }
    }

    @Override
    public DavResourceIterator getMembers() {
        ArrayList<DavResource> list = new ArrayList<DavResource>();
        if (this.exists() && this.isCollection()) {
            try {
                VersionIterator it = ((VersionHistory)this.getNode()).getAllVersions();
                while (it.hasNext()) {
                    Version v = it.nextVersion();
                    DavResourceLocator vhLocator = this.getLocator();
                    DavResourceLocator resourceLocator = vhLocator.getFactory().createResourceLocator(vhLocator.getPrefix(), vhLocator.getWorkspacePath(), v.getPath(), false);
                    DavResource childRes = this.getFactory().createResource(resourceLocator, this.getSession());
                    list.add(childRes);
                }
            }
            catch (RepositoryException e) {
                log.error("Unexpected error", (Throwable)e);
            }
            catch (DavException e) {
                log.error("Unexpected error", (Throwable)e);
            }
        }
        return new DavResourceIteratorImpl(list);
    }

    @Override
    public void addMember(DavResource member, InputContext inputContext) throws DavException {
        throw new DavException(403);
    }

    @Override
    public void removeMember(DavResource member) throws DavException {
        if (this.exists()) {
            VersionHistory versionHistory = (VersionHistory)this.getNode();
            try {
                String itemPath = member.getLocator().getRepositoryPath();
                if (itemPath == null) {
                    throw new IllegalArgumentException("Cannot retrieve name from a 'null' item path.");
                }
                String name = Text.getName(itemPath);
                if (name.endsWith("]")) {
                    name = name.substring(0, name.lastIndexOf(91));
                }
                versionHistory.removeVersion(name);
            }
            catch (RepositoryException e) {
                throw new JcrDavException(e);
            }
        } else {
            throw new DavException(404);
        }
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
    public VersionResource[] getVersions() throws DavException {
        try {
            VersionIterator vIter = ((VersionHistory)this.getNode()).getAllVersions();
            ArrayList<VersionResource> l = new ArrayList<VersionResource>();
            while (vIter.hasNext()) {
                DavResourceLocator versionLoc = this.getLocatorFromNode(vIter.nextVersion());
                DavResource vr = this.createResourceFromLocator(versionLoc);
                if (vr instanceof VersionResource) {
                    l.add((VersionResource)vr);
                    continue;
                }
                throw new DavException(500);
            }
            return l.toArray(new VersionResource[l.size()]);
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    protected void initProperties() {
        if (!this.propsInitialized) {
            super.initProperties();
            this.properties.add(new ResourceType(new int[]{1, 2}));
            try {
                String rootVersionHref = this.getLocatorFromNode(((VersionHistory)this.getNode()).getRootVersion()).getHref(false);
                this.properties.add(new HrefProperty(VersionHistoryResource.ROOT_VERSION, rootVersionHref, false));
            }
            catch (RepositoryException e) {
                log.error(e.getMessage());
            }
            try {
                VersionIterator vIter = ((VersionHistory)this.getNode()).getAllVersions();
                ArrayList<Version> l = new ArrayList<Version>();
                while (vIter.hasNext()) {
                    l.add(vIter.nextVersion());
                }
                this.properties.add(this.getHrefProperty(VersionHistoryResource.VERSION_SET, l.toArray(new Version[l.size()]), true, false));
            }
            catch (RepositoryException e) {
                log.error(e.getMessage());
            }
        }
    }
}

