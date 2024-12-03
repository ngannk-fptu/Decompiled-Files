/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.lock;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class LockInfo
implements DavConstants,
XmlSerializable {
    private static Logger log = LoggerFactory.getLogger(LockInfo.class);
    private Type type;
    private Scope scope;
    private String owner;
    private boolean isDeep;
    private long timeout;
    private boolean isRefreshLock;

    public LockInfo(long timeout) {
        this.timeout = timeout > 0L ? timeout : Integer.MAX_VALUE;
        this.isRefreshLock = true;
    }

    public LockInfo(Scope scope, Type type, String owner, long timeout, boolean isDeep) {
        this.timeout = timeout > 0L ? timeout : Integer.MAX_VALUE;
        this.isDeep = isDeep;
        if (scope == null || type == null) {
            this.isRefreshLock = true;
        } else {
            this.scope = scope;
            this.type = type;
            this.owner = owner;
        }
    }

    public LockInfo(Element liElement, long timeout, boolean isDeep) throws DavException {
        this.timeout = timeout > 0L ? timeout : Integer.MAX_VALUE;
        this.isDeep = isDeep;
        if (liElement != null) {
            if (!DomUtil.matches(liElement, "lockinfo", NAMESPACE)) {
                log.warn("'DAV:lockinfo' element expected.");
                throw new DavException(400);
            }
            ElementIterator it = DomUtil.getChildren(liElement);
            while (it.hasNext()) {
                Element child = it.nextElement();
                String childName = child.getLocalName();
                if ("locktype".equals(childName)) {
                    this.type = Type.createFromXml(child);
                    continue;
                }
                if ("lockscope".equals(childName)) {
                    this.scope = Scope.createFromXml(child);
                    continue;
                }
                if (!"owner".equals(childName)) continue;
                this.owner = DomUtil.getChildTextTrim(child, "href", NAMESPACE);
                if (this.owner != null) continue;
                this.owner = DomUtil.getTextTrim(child);
            }
            this.isRefreshLock = false;
        } else {
            this.isRefreshLock = true;
        }
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Scope getScope() {
        return this.scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public String getOwner() {
        return this.owner;
    }

    public boolean isDeep() {
        return this.isDeep;
    }

    public long getTimeout() {
        return this.timeout;
    }

    public boolean isRefreshLock() {
        return this.isRefreshLock;
    }

    @Override
    public Element toXml(Document document) {
        if (this.isRefreshLock) {
            return null;
        }
        Element lockInfo = DomUtil.createElement(document, "lockinfo", NAMESPACE);
        lockInfo.appendChild(this.scope.toXml(document));
        lockInfo.appendChild(this.type.toXml(document));
        if (this.owner != null) {
            DomUtil.addChildElement(lockInfo, "owner", NAMESPACE, this.owner);
        }
        return lockInfo;
    }
}

