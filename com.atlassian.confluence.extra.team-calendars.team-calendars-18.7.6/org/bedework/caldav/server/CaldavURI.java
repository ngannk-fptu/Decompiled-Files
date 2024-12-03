/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server;

import org.bedework.access.AccessPrincipal;
import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.caldav.server.CalDAVEvent;
import org.bedework.caldav.server.CalDAVResource;
import org.bedework.util.misc.Util;
import org.bedework.webdav.servlet.shared.WebdavException;

public class CaldavURI {
    boolean exists;
    CalDAVCollection col;
    CalDAVResource resource;
    CalDAVEvent entity;
    AccessPrincipal principal;
    String entityName;
    String path;
    boolean nameless;
    boolean resourceUri;

    CaldavURI(CalDAVCollection col, boolean exists) {
        this.init(col, null, null, null, exists, false);
    }

    CaldavURI(CalDAVCollection col, CalDAVEvent entity, String entityName, boolean exists, boolean nameless) {
        this.init(col, null, entity, entityName, exists, nameless);
    }

    CaldavURI(CalDAVCollection col, CalDAVResource res, boolean exists) throws WebdavException {
        this.init(col, res, null, res.getName(), exists, false);
        this.resourceUri = true;
    }

    CaldavURI(AccessPrincipal pi) {
        this.principal = pi;
        this.exists = true;
        this.col = null;
        this.entityName = pi.getAccount();
        this.path = pi.getPrincipalRef();
    }

    private void init(CalDAVCollection col, CalDAVResource res, CalDAVEvent entity, String entityName, boolean exists, boolean nameless) {
        this.col = col;
        this.resource = res;
        this.entity = entity;
        this.entityName = entityName;
        this.exists = exists;
        this.nameless = nameless;
    }

    public boolean getExists() {
        return this.exists;
    }

    public CalDAVCollection getCol() {
        return this.col;
    }

    public CalDAVResource getResource() {
        return this.resource;
    }

    public CalDAVEvent getEntity() {
        return this.entity;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public String getPath() {
        if (this.principal != null) {
            return this.path;
        }
        try {
            return this.col.getPath();
        }
        catch (WebdavException wde) {
            throw new RuntimeException(wde);
        }
    }

    public String getUri() throws WebdavException {
        if (this.entityName == null || this.principal != null) {
            return this.getPath();
        }
        return Util.buildPath(false, this.getPath(), "/", this.entityName);
    }

    public boolean isResource() {
        return this.resourceUri;
    }

    public boolean isCollection() {
        return !this.nameless && this.entityName == null;
    }

    public AccessPrincipal getPrincipal() {
        if (this.principal == null) {
            return null;
        }
        return this.principal;
    }

    public boolean sameName(String entityName) {
        if (entityName == null && this.getEntityName() == null) {
            return true;
        }
        if (entityName == null || this.getEntityName() == null) {
            return false;
        }
        return entityName.equals(this.getEntityName());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("CaldavURI{path=");
        try {
            sb.append(this.getPath());
        }
        catch (Throwable t) {
            sb.append("Exception: ");
            sb.append(t.getMessage());
        }
        sb.append(", entityName=");
        sb.append(String.valueOf(this.entityName));
        sb.append("}");
        return sb.toString();
    }

    public int hashCode() {
        try {
            if (this.principal != null) {
                return this.principal.hashCode();
            }
            int hc = this.entityName.hashCode();
            return hc * 3 + this.getPath().hashCode();
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CaldavURI)) {
            return false;
        }
        CaldavURI that = (CaldavURI)o;
        if (this.principal != null) {
            return this.principal.equals(that.principal);
        }
        if (!this.getPath().equals(that.getPath())) {
            return false;
        }
        return this.sameName(that.entityName);
    }
}

