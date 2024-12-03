/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.shared;

import javax.xml.namespace.QName;
import org.bedework.access.AccessPrincipal;
import org.bedework.util.misc.ToString;
import org.bedework.util.misc.Util;
import org.bedework.webdav.servlet.shared.WebdavException;

public abstract class WdEntity<T>
implements Comparable<WdEntity> {
    private String name;
    private String displayName;
    private String path;
    private String parentPath;
    private AccessPrincipal owner;
    private String created;
    private String lastmod;
    private String description;

    public abstract boolean getCanShare() throws WebdavException;

    public abstract boolean getCanPublish() throws WebdavException;

    public abstract boolean isAlias() throws WebdavException;

    public abstract String getAliasUri() throws WebdavException;

    public abstract T resolveAlias(boolean var1) throws WebdavException;

    public abstract void setProperty(QName var1, String var2) throws WebdavException;

    public abstract String getProperty(QName var1) throws WebdavException;

    public void setName(String val) throws WebdavException {
        this.name = val;
    }

    public String getName() throws WebdavException {
        return this.name;
    }

    public void setDisplayName(String val) throws WebdavException {
        this.displayName = val;
    }

    public String getDisplayName() throws WebdavException {
        return this.displayName;
    }

    public void setPath(String val) throws WebdavException {
        this.path = val;
    }

    public String getPath() throws WebdavException {
        return this.path;
    }

    public void setParentPath(String val) throws WebdavException {
        this.parentPath = val;
    }

    public String getParentPath() throws WebdavException {
        return this.parentPath;
    }

    public void setOwner(AccessPrincipal val) throws WebdavException {
        this.owner = val;
    }

    public AccessPrincipal getOwner() throws WebdavException {
        return this.owner;
    }

    public void setCreated(String val) throws WebdavException {
        this.created = val;
    }

    public String getCreated() throws WebdavException {
        return this.created;
    }

    public void setLastmod(String val) throws WebdavException {
        this.lastmod = val;
    }

    public String getLastmod() throws WebdavException {
        return this.lastmod;
    }

    public abstract String getEtag() throws WebdavException;

    public abstract String getPreviousEtag() throws WebdavException;

    public void setDescription(String val) throws WebdavException {
        this.description = val;
    }

    public String getDescription() throws WebdavException {
        return this.description;
    }

    public void toStringSegment(ToString ts) {
        try {
            ts.append("name", this.getName());
            ts.append("displayName", this.getDisplayName());
            ts.append("path", this.getPath());
            ts.append("parentPath", this.getParentPath());
            ts.append("owner", this.getOwner());
            ts.append("created", this.getCreated());
            ts.append("lastmod", this.getLastmod());
            ts.append("etag", this.getEtag());
            ts.append("previousEtag", this.getPreviousEtag());
            ts.append("description", this.getDescription());
        }
        catch (Throwable t) {
            ts.append(t);
        }
    }

    public int hashCode() {
        try {
            return this.getPath().hashCode() * this.getName().hashCode();
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public int compareTo(WdEntity that) {
        try {
            if (this == that) {
                return 0;
            }
            return Util.cmpObjval((Comparable)((Object)this.getPath()), (Comparable)((Object)that.getPath()));
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

