/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.lock;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractActiveLock
implements ActiveLock,
DavConstants {
    private String lockroot;

    @Override
    public String getLockroot() {
        return this.lockroot;
    }

    @Override
    public void setLockroot(String lockroot) {
        this.lockroot = lockroot;
    }

    @Override
    public Element toXml(Document document) {
        Element activeLock = DomUtil.createElement(document, "activelock", NAMESPACE);
        activeLock.appendChild(this.getScope().toXml(document));
        activeLock.appendChild(this.getType().toXml(document));
        activeLock.appendChild(DomUtil.depthToXml(this.isDeep(), document));
        long timeout = this.getTimeout();
        if (!this.isExpired() && timeout != Integer.MIN_VALUE) {
            activeLock.appendChild(DomUtil.timeoutToXml(timeout, document));
        }
        if (this.getOwner() != null) {
            DomUtil.addChildElement(activeLock, "owner", NAMESPACE, this.getOwner());
        }
        if (this.getToken() != null) {
            Element lToken = DomUtil.addChildElement(activeLock, "locktoken", NAMESPACE);
            lToken.appendChild(DomUtil.hrefToXml(this.getToken(), document));
        }
        if (this.getLockroot() != null) {
            Element lroot = DomUtil.addChildElement(activeLock, "lockroot", NAMESPACE);
            lroot.appendChild(DomUtil.hrefToXml(this.getLockroot(), document));
        }
        return activeLock;
    }
}

